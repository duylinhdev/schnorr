package com.schnorr.model;

import java.math.BigInteger;
import java.security.SecureRandom;

public class KeyGenerator {
    private BigInteger x; // private key
    private BigInteger y; // public key

    public void setPrivateKey(BigInteger x) {
        this.x = x;
    }

    public void generateRandomPrivateKey(SystemParameters params) {
        BigInteger q = params.getQ();
        SecureRandom rng = new SecureRandom();
        do {
            x = new BigInteger(q.bitLength(), rng)
                    .mod(q.subtract(BigInteger.ONE))
                    .add(BigInteger.ONE);
        } while (x.compareTo(BigInteger.ONE) < 0 || x.compareTo(q.subtract(BigInteger.ONE)) > 0);
    }

    public BigInteger computePublicKey(SystemParameters params) {
        this.y = params.getG().modPow(this.x, params.getP());
        return this.y;
    }

    public BigInteger getX() { return x; }
    public BigInteger getY() { return y; }
}
