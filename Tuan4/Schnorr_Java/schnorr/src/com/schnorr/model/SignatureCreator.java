package com.schnorr.model;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class SignatureCreator {
    private BigInteger k; // nonce
    private BigInteger r; // commitment
    private BigInteger e; // challenge
    private BigInteger s; // response

    public BigInteger[] sign(String message, SystemParameters params, KeyGenerator keyGen) throws Exception {
        BigInteger q = params.getQ();
        SecureRandom rng = new SecureRandom();
        do {
            k = new BigInteger(q.bitLength(), rng)
                    .mod(q.subtract(BigInteger.ONE))
                    .add(BigInteger.ONE);
        } while (k.compareTo(BigInteger.ONE) < 0 || k.compareTo(q.subtract(BigInteger.ONE)) > 0);
        return signWithNonce(message, k, params, keyGen);
    }

    public BigInteger[] signWithNonce(String message, BigInteger nonce, SystemParameters params, KeyGenerator keyGen) throws Exception {
        this.k = nonce;
        this.r = params.getG().modPow(this.k, params.getP());
        this.e = hash(this.r.toString() + message, params.getHashAlgorithm()).mod(params.getQ());
        this.s = this.k.subtract(keyGen.getX().multiply(this.e)).mod(params.getQ());
        return new BigInteger[]{ e, s };
    }

    private BigInteger hash(String input, String algorithm) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] dig = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return new BigInteger(1, dig);
    }

    public BigInteger getK() { return k; }
    public BigInteger getR() { return r; }
    public BigInteger getE() { return e; }
    public BigInteger getS() { return s; }
}
