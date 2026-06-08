package com.schnorr.controller;

import com.schnorr.model.SchnorrFacade;
import com.schnorr.view.SchnorrView;

import java.math.BigInteger;

public class KeyGenController extends BaseController {

    public KeyGenController(SchnorrFacade model, SchnorrView view) {
        super(model, view);
    }

    public void handleGenPrivKey() {
        try {
            validateAndSyncSystemParams();
            model.getKeyGen().generateRandomPrivateKey(model.getParams());
            view.setPrivateKey(model.getKeyGen().getX().toString());
        } catch (IllegalArgumentException ex) {
            view.showError("Lỗi dữ liệu: " + ex.getMessage());
        } catch (Exception ex) {
            view.showError("Lỗi sinh khóa bí mật: " + ex.getMessage());
        }
    }

    public void handleCalcPubKey() {
        try {
            validateAndSyncSystemParams();

            String privKeyStr = view.getPrivateKey().trim();
            if (privKeyStr.isEmpty()) throw new IllegalArgumentException("Khóa bí mật (x) không được để trống!");
            if (isNumeric(privKeyStr)) throw new IllegalArgumentException("Khóa bí mật (x) phải là số nguyên dương!");

            model.getKeyGen().setPrivateKey(new BigInteger(privKeyStr));
            BigInteger y = model.getKeyGen().computePublicKey(model.getParams());
            String formula = model.getParams().getG() + "^" + model.getKeyGen().getX()
                    + " mod " + model.getParams().getP() + " = " + y;
            view.setPublicKey(y.toString(), formula);
        } catch (IllegalArgumentException ex) {
            view.showError("Lỗi dữ liệu: " + ex.getMessage());
        } catch (Exception ex) {
            view.showError("Lỗi tính khóa công khai: " + ex.getMessage());
        }
    }
}
