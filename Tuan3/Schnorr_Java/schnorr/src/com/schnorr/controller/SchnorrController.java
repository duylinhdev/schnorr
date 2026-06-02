package com.schnorr.controller;

import com.schnorr.model.SchnorrAlgorithm;
import com.schnorr.view.SchnorrView;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.SecureRandom;

/**
 * CONTROLLER — Mediates between Model ({@link SchnorrAlgorithm}) and
 * View ({@link SchnorrView}).
 *
 * Responsibilities:
 * • Read raw string data from the View.
 * • Validate and convert inputs.
 * • Invoke the Model.
 * • Push results back to the View.
 * • Never touches layout or rendering directly.
 */
public class SchnorrController {

    private final SchnorrAlgorithm model;
    private SchnorrView view;

    public SchnorrController(SchnorrAlgorithm model) {
        this.model = model;
    }

    /**
     * Called once the View is fully constructed so the Controller
     * can wire all action listeners.
     */
    public void setView(SchnorrView view) {
        this.view = view;
        wireListeners();
    }

    // ── Listener wiring ────────────────────────────────────────────────────────

    private void wireListeners() {
        view.onCheckParams(e   -> handleCheckParams());
        view.onGenPrivKey(e    -> handleGenPrivKey());
        view.onCalcPubKey(e    -> handleCalcPubKey());
        view.onGenNonce(e      -> handleGenNonce());
        view.onSign(e          -> handleSign());
        view.onVerify(e        -> handleVerify());
        view.onLoadMsgSign(e   -> handleLoadFileSign());
        view.onLoadMsgVerify(e -> handleLoadFileVerify());
        view.onReset(e         -> handleReset());
    }

    // ── Handlers ───────────────────────────────────────────────────────────────

    private void handleCheckParams() {
        try {
            // Thực hiện đồng bộ và kiểm tra định dạng số của p, q, g ngay tại đây
            validateAndSyncSystemParams();

            boolean ok = model.validateParameters();
            view.showParamStatus(ok);
        } catch (IllegalArgumentException ex) {
            view.showError("Lỗi dữ liệu: " + ex.getMessage());
        } catch (Exception ex) {
            view.showError("Lỗi kiểm tra tham số: " + ex.getMessage());
        }
    }

    private void handleGenPrivKey() {
        try {
            validateAndSyncSystemParams();
            model.generateRandomPrivateKey();
            view.setPrivateKey(model.getX().toString());
        } catch (IllegalArgumentException ex) {
            view.showError("Lỗi dữ liệu: " + ex.getMessage());
        } catch (Exception ex) {
            view.showError("Lỗi sinh khóa bí mật: " + ex.getMessage());
        }
    }

    private void handleCalcPubKey() {
        try {
            validateAndSyncSystemParams();

            String privKeyStr = view.getPrivateKey().trim();
            if (privKeyStr.isEmpty()) {
                throw new IllegalArgumentException("Khóa bí mật (x) không được để trống!");
            }
            if (!isNumeric(privKeyStr)) {
                throw new IllegalArgumentException("Khóa bí mật (x) phải là số nguyên dương, không được chứa ký tự chữ!");
            }

            model.setPrivateKey(new BigInteger(privKeyStr));
            BigInteger y       = model.computePublicKey();
            String     formula = model.getG() + "^" + model.getX()
                    + " mod " + model.getP() + " = " + y;
            view.setPublicKey(y.toString(), formula);
        } catch (IllegalArgumentException ex) {
            view.showError("Lỗi dữ liệu: " + ex.getMessage());
        } catch (Exception ex) {
            view.showError("Lỗi tính khóa công khai: " + ex.getMessage());
        }
    }

    private void handleGenNonce() {
        try {
            validateAndSyncSystemParams();
            BigInteger   q   = model.getQ();
            SecureRandom rng = new SecureRandom();
            BigInteger   k;
            do {
                k = new BigInteger(q.bitLength(), rng).mod(q);
            } while (k.compareTo(BigInteger.ONE) < 0
                    || k.compareTo(q.subtract(BigInteger.ONE)) > 0);
            view.setNonce(k.toString());
        } catch (IllegalArgumentException ex) {
            view.showError("Lỗi dữ liệu: " + ex.getMessage());
        } catch (Exception ex) {
            view.showError("Lỗi sinh nonce: " + ex.getMessage());
        }
    }

    private void handleSign() {
        try {
            validateAndSyncSystemParams();

            // Kiểm tra khóa bí mật x
            String privKeyStr = view.getPrivateKey().trim();
            if (privKeyStr.isEmpty()) throw new IllegalArgumentException("Chưa nhập Khóa bí mật (x)!");
            if (!isNumeric(privKeyStr)) throw new IllegalArgumentException("Khóa bí mật (x) phải là định dạng số!");

            // Kiểm tra giá trị nonce k
            String nonceStr = view.getNonce().trim();
            if (nonceStr.isEmpty()) throw new IllegalArgumentException("Chưa nhập số ngẫu nhiên (k)! Hãy tự nhập hoặc bấm 'NGẪU NHIÊN'.");
            if (!isNumeric(nonceStr)) throw new IllegalArgumentException("Số ngẫu nhiên (k) phải là định dạng số!");

            // Kiểm tra thông điệp M
            if (view.getSignMessage().trim().isEmpty()) {
                throw new IllegalArgumentException("Thông điệp cần ký (M) không được để trống!");
            }

            model.setPrivateKey(new BigInteger(privKeyStr));
            model.computePublicKey();

            BigInteger   k   = new BigInteger(nonceStr);
            BigInteger[] sig = model.signWithNonce(view.getSignMessage(), k);

            view.setSignResult(
                    model.getR().toString(),
                    model.getE().toString(),
                    sig[0].toString(),
                    sig[1].toString());

            // Tự động điền sang tab xác minh
            view.prefillVerify(view.getSignMessage(), sig[0].toString(), sig[1].toString());
        } catch (IllegalArgumentException ex) {
            view.showError("Dữ liệu không hợp lệ: " + ex.getMessage());
        } catch (Exception ex) {
            view.showError("Lỗi tạo chữ ký: " + ex.getMessage());
        }
    }

    private void handleVerify() {
        try {
            validateAndSyncSystemParams();

            if (model.getY() == null) {
                throw new IllegalArgumentException("Chưa tính toán Khóa công khai (y)! Hãy hoàn thành Bước 2 trước.");
            }

            // Kiểm tra thông điệp cần xác minh
            if (view.getVerifyMessage().trim().isEmpty()) {
                throw new IllegalArgumentException("Thông điệp cần xác minh (M) không được để trống!");
            }

            // Kiểm tra cặp chữ ký nhập vào (e, s)
            String sigEStr = view.getVerifyE().trim();
            String sigSStr = view.getVerifyS().trim();

            if (sigEStr.isEmpty() || sigSStr.isEmpty()) {
                throw new IllegalArgumentException("Thành phần chữ ký số (e) và (s) không được để trống!");
            }
            if (!isNumeric(sigEStr)) throw new IllegalArgumentException("Thành phần chữ ký (e) phải là định dạng số!");
            if (!isNumeric(sigSStr)) throw new IllegalArgumentException("Thành phần chữ ký (s) phải là định dạng số!");

            boolean ok = model.verify(
                    view.getVerifyMessage(),
                    new BigInteger(sigEStr),
                    new BigInteger(sigSStr));
            view.setVerifyResult(model.getRv().toString(), model.getEv().toString(), ok);
        } catch (IllegalArgumentException ex) {
            view.showError("Dữ liệu xác minh lỗi: " + ex.getMessage());
        } catch (Exception ex) {
            view.showError("Lỗi xác minh: " + ex.getMessage());
        }
    }

    private void handleLoadFileSign() {
        java.io.File file = view.pickTextFile();
        if (file == null) return;
        try {
            String content = new String(Files.readAllBytes(file.toPath()), "UTF-8").trim();
            view.setSignMessage(content);
        } catch (IOException ex) {
            view.showError("Không đọc được file: " + ex.getMessage());
        }
    }

    private void handleLoadFileVerify() {
        java.io.File file = view.pickTextFile();
        if (file == null) return;
        try {
            String content = new String(Files.readAllBytes(file.toPath()), "UTF-8").trim();
            view.setVerifyMessage(content);
        } catch (IOException ex) {
            view.showError("Không đọc được file: " + ex.getMessage());
        }
    }

    private void handleReset() {
        model.setParameters(
                new BigInteger("48731"),
                new BigInteger("443"),
                new BigInteger("11444"));
        model.setHashAlgorithm("SHA-256");
        view.resetAll();
    }

    // ── Internal helpers ───────────────────────────────────────────────────────

    /**
     * Đồng bộ và kiểm tra nghiêm ngặt định dạng số của 3 tham số hệ thống p, q, g.
     * Ném ra ngoại lệ IllegalArgumentException có kèm thông báo chi tiết nếu sai sót.
     */
    private void validateAndSyncSystemParams() {
        String pStr = view.getP().trim();
        String qStr = view.getQ().trim();
        String gStr = view.getG().trim();

        if (pStr.isEmpty()) throw new IllegalArgumentException("Tham số số nguyên tố p không được để trống!");
        if (qStr.isEmpty()) throw new IllegalArgumentException("Tham số ước nguyên tố q không được để trống!");
        if (gStr.isEmpty()) throw new IllegalArgumentException("Tham số phần tử sinh g không được để trống!");

        if (!isNumeric(pStr)) throw new IllegalArgumentException("Tham số p phải là một số nguyên dương (không chứa chữ hoặc ký tự đặc biệt)!");
        if (!isNumeric(qStr)) throw new IllegalArgumentException("Tham số q phải là một số nguyên dương (không chứa chữ hoặc ký tự đặc biệt)!");
        if (!isNumeric(gStr)) throw new IllegalArgumentException("Tham số g phải là một số nguyên dương (không chứa chữ hoặc ký tự đặc biệt)!");

        model.setParameters(new BigInteger(pStr), new BigInteger(qStr), new BigInteger(gStr));
        model.setHashAlgorithm(view.getHashAlgorithm());
    }

    /**
     * Hàm tiện ích kiểm tra xem chuỗi có phải cấu thành hoàn toàn từ các chữ số hay không.
     */
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        return str.matches("\\d+");
    }
}