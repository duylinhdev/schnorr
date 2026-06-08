package com.schnorr.controller;

import com.schnorr.model.SchnorrFacade;
import com.schnorr.view.SchnorrView;

public class SystemParamController extends BaseController {

    public SystemParamController(SchnorrFacade model, SchnorrView view) {
        super(model, view);
    }

    public void handleCheckParams() {
        try {
            validateAndSyncSystemParams();
            model.getParams().validateParameters();
            view.showParamStatus(true);
        } catch (IllegalArgumentException ex) {
            view.showParamStatus(false);
            view.showError("Lỗi dữ liệu: " + ex.getMessage());
        } catch (Exception ex) {
            view.showParamStatus(false);
            view.showError("Lỗi kiểm tra tham số: " + ex.getMessage());
        }
    }
}
