package com.schnorr.controller;

import com.schnorr.model.SchnorrFacade;
import com.schnorr.utils.DocumentReader;
import com.schnorr.utils.SignatureFileManager;
import com.schnorr.view.SchnorrView;

import java.io.File;
import java.math.BigInteger;

public class VerifyController extends BaseController {

    public VerifyController(SchnorrFacade model, SchnorrView view) {
        super(model, view);
    }

    public void handleVerify() {
        try {
            validateAndSyncSystemParams();

            if (model.getKeyGen().getY() == null) {
                throw new IllegalArgumentException("Chưa tính toán Khóa công khai (y)! Hãy hoàn thành Bước 2 trước.");
            }

            if (view.getVerifyMessage().trim().isEmpty()) {
                throw new IllegalArgumentException("Thông điệp cần xác minh (M) không được để trống!");
            }

            String sigEStr = view.getVerifyE().trim();
            String sigSStr = view.getVerifyS().trim();

            if (sigEStr.isEmpty() || isNumeric(sigEStr) || sigSStr.isEmpty() || isNumeric(sigSStr)) {
                throw new IllegalArgumentException("Thành phần chữ ký số (e) và (s) không hợp lệ!");
            }

            boolean ok = model.getVerifier().verify(
                    view.getVerifyMessage(),
                    new BigInteger(sigEStr),
                    new BigInteger(sigSStr),
                    model.getParams(),
                    model.getKeyGen());
            view.setVerifyResult(model.getVerifier().getRv().toString(), model.getVerifier().getEv().toString(), ok);
        } catch (IllegalArgumentException ex) {
            view.showError("Dữ liệu xác minh lỗi: " + ex.getMessage());
        } catch (Exception ex) {
            view.showError("Lỗi xác minh: " + ex.getMessage());
        }
    }

    public void handleLoadFileVerify() {
        File file = view.pickDocumentFile();
        if (file == null) return;
        try {
            String content = DocumentReader.readContent(file);
            view.setVerifyMessage(content);
        } catch (Exception ex) {
            view.showError("Không đọc được file: " + ex.getMessage());
        }
    }

    public void handleLoadSign() {
        File file = view.pickSignatureFileToLoad();
        if (file == null) return;
        try {
            String[] sigs = SignatureFileManager.loadSignature(file);
            view.prefillVerify(view.getVerifyMessage(), sigs[0], sigs[1]);
        } catch (Exception ex) {
            view.showError("Lỗi khi tải chữ ký: " + ex.getMessage());
        }
    }
}
