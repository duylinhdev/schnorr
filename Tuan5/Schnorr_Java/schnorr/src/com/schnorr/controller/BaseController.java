package com.schnorr.controller;

import com.schnorr.model.SchnorrFacade;
import com.schnorr.view.SchnorrView;

import java.math.BigInteger;

public abstract class BaseController {
    protected final SchnorrFacade model;
    protected final SchnorrView view;

    public BaseController(SchnorrFacade model, SchnorrView view) {
        this.model = model;
        this.view = view;
    }

    /**
     * Đồng bộ và kiểm tra định dạng số của 3 tham số hệ thống p, q, g.
     */
    protected void validateAndSyncSystemParams() {
        String pStr = view.getP().trim();
        String qStr = view.getQ().trim();
        String gStr = view.getG().trim();

        if (pStr.isEmpty() || qStr.isEmpty() || gStr.isEmpty()) 
            throw new IllegalArgumentException("Các tham số p, q, g không được để trống!");

        if (isNumeric(pStr) || isNumeric(qStr) || isNumeric(gStr))
            throw new IllegalArgumentException("Các tham số p, q, g phải là số nguyên dương!");

        model.resetParameters(new BigInteger(pStr), new BigInteger(qStr), new BigInteger(gStr));
        model.getParams().setHashAlgorithm(view.getHashAlgorithm());
    }

    protected boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return true;
        return !str.matches("\\d+");
    }
}
