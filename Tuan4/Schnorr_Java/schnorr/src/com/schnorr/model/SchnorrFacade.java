package com.schnorr.model;

import java.math.BigInteger;

/**
 * FACADE — Cung cấp giao diện thống nhất cho các module đã chia nhỏ:
 * SystemParameters, KeyGenerator, SignatureCreator, SignatureVerifier.
 */
public class SchnorrFacade {
    private final SystemParameters params;
    private final KeyGenerator keyGen;
    private final SignatureCreator creator;
    private final SignatureVerifier verifier;

    public SchnorrFacade() {
        this.params = new SystemParameters();
        this.keyGen = new KeyGenerator();
        this.creator = new SignatureCreator();
        this.verifier = new SignatureVerifier();
    }

    public SystemParameters getParams() { return params; }
    public KeyGenerator getKeyGen() { return keyGen; }
    public SignatureCreator getCreator() { return creator; }
    public SignatureVerifier getVerifier() { return verifier; }

    // Tiện ích Reset
    public void resetParameters(BigInteger p, BigInteger q, BigInteger g) {
        params.setParameters(p, q, g);
    }
}
