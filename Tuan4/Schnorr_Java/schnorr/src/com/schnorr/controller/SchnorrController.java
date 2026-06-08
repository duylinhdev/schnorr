package com.schnorr.controller;

import com.schnorr.model.SchnorrFacade;
import com.schnorr.view.SchnorrView;

import java.math.BigInteger;

/**
 * MASTER CONTROLLER — Đóng vai trò điều phối chính.
 * Thay vì xử lý toàn bộ logic, nó khởi tạo và giao việc cho 4 Controller con.
 */
public class SchnorrController {

    private final SchnorrFacade model;
    private SchnorrView view;

    private SystemParamController paramController;
    private KeyGenController keyGenController;
    private SignController signController;
    private VerifyController verifyController;

    public SchnorrController(SchnorrFacade model) {
        this.model = model;
    }

    public void setView(SchnorrView view) {
        this.view = view;
        
        // Khởi tạo các sub-controllers
        this.paramController = new SystemParamController(model, view);
        this.keyGenController = new KeyGenController(model, view);
        this.signController = new SignController(model, view);
        this.verifyController = new VerifyController(model, view);
        
        wireListeners();
    }

    private void wireListeners() {
        // Giao việc cho SystemParamController
        view.onCheckParams(e -> paramController.handleCheckParams());

        // Giao việc cho KeyGenController
        view.onGenPrivKey(e -> keyGenController.handleGenPrivKey());
        view.onCalcPubKey(e -> keyGenController.handleCalcPubKey());

        // Giao việc cho SignController
        view.onGenNonce(e -> signController.handleGenNonce());
        view.onSign(e -> signController.handleSign());
        view.onLoadMsgSign(e -> signController.handleLoadFileSign());
        view.onSaveSign(e -> signController.handleSaveSign());

        // Giao việc cho VerifyController
        view.onVerify(e -> verifyController.handleVerify());
        view.onLoadMsgVerify(e -> verifyController.handleLoadFileVerify());
        view.onLoadSign(e -> verifyController.handleLoadSign());

        // Master Controller tự xử lý nút Reset
        view.onReset(e -> handleReset());
    }

    private void handleReset() {
        model.resetParameters(
                new BigInteger("48731"),
                new BigInteger("443"),
                new BigInteger("11444"));
        model.getParams().setHashAlgorithm("SHA-256");
        view.resetAll();
    }
}