package com.schnorr.controller;

import com.schnorr.model.SchnorrFacade;
import com.schnorr.utils.DocumentReader;
import com.schnorr.utils.SignatureFileManager;
import com.schnorr.view.SchnorrView;

import javax.swing.*;
import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;

public class SignController extends BaseController {

    public SignController(SchnorrFacade model, SchnorrView view) {
        super(model, view);
    }

    public void handleGenNonce() {
        try {
            validateAndSyncSystemParams();
            BigInteger q = model.getParams().getQ();
            SecureRandom rng = new SecureRandom();
            BigInteger k;
            do {
                k = new BigInteger(q.bitLength(), rng).mod(q);
            } while (k.compareTo(BigInteger.ONE) < 0 || k.compareTo(q.subtract(BigInteger.ONE)) > 0);
            view.setNonce(k.toString());
        } catch (IllegalArgumentException ex) {
            view.showError("Lỗi dữ liệu: " + ex.getMessage());
        } catch (Exception ex) {
            view.showError("Lỗi sinh nonce: " + ex.getMessage());
        }
    }

    public void handleSign() {
        try {
            validateAndSyncSystemParams();

            String privKeyStr = view.getPrivateKey().trim();
            if (privKeyStr.isEmpty() || isNumeric(privKeyStr)) throw new IllegalArgumentException("Khóa bí mật (x) không hợp lệ!");

            String nonceStr = view.getNonce().trim();
            if (nonceStr.isEmpty() || isNumeric(nonceStr)) throw new IllegalArgumentException("Số ngẫu nhiên (k) không hợp lệ!");

            String msg = view.getSignMessage().trim();
            if (msg.isEmpty()) throw new IllegalArgumentException("Thông điệp cần ký (M) không được để trống!");

            model.getKeyGen().setPrivateKey(new BigInteger(privKeyStr));
            model.getKeyGen().computePublicKey(model.getParams());

            BigInteger k = new BigInteger(nonceStr);
            BigInteger[] sig = model.getCreator().signWithNonce(msg, k, model.getParams(), model.getKeyGen());

            view.setSignResult(
                    model.getCreator().getR().toString(),
                    model.getCreator().getE().toString(),
                    sig[0].toString(),
                    sig[1].toString());

            view.prefillVerify(msg, sig[0].toString(), sig[1].toString());
        } catch (IllegalArgumentException ex) {
            view.showError("Dữ liệu không hợp lệ: " + ex.getMessage());
        } catch (Exception ex) {
            view.showError("Lỗi tạo chữ ký: " + ex.getMessage());
        }
    }

    public void handleLoadFileSign() {
        File file = view.pickDocumentFile();
        if (file == null) return;
        try {
            String content = DocumentReader.readContent(file);
            view.setSignMessage(content);
        } catch (Exception ex) {
            view.showError("Không đọc được file: " + ex.getMessage());
        }
    }

    public void handleSaveSign() {
        try {
            String e = model.getCreator().getE() != null ? model.getCreator().getE().toString() : "";
            String s = model.getCreator().getS() != null ? model.getCreator().getS().toString() : "";
            
            if (e.isEmpty() || s.isEmpty()) {
                view.showError("Chưa có chữ ký nào được tạo ra để lưu!");
                return;
            }
            
            File file = view.pickSignatureFileToSave();
            if (file == null) return;
            
            if (!file.getName().toLowerCase().endsWith(".sig")) {
                file = new File(file.getParentFile(), file.getName() + ".sig");
            }

            SignatureFileManager.saveSignature(file, e, s);
            JOptionPane.showMessageDialog(view, "Đã lưu chữ ký thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            view.showError("Lỗi khi lưu chữ ký: " + ex.getMessage());
        }
    }
}
