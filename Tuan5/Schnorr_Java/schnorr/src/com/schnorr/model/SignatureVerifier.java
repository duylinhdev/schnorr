package com.schnorr.model;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SignatureVerifier {
    private BigInteger rv; // recovered r'
    private BigInteger ev; // recomputed e'

    public boolean verify(String message, BigInteger eIn, BigInteger sIn, SystemParameters params, KeyGenerator keyGen) throws Exception {
        BigInteger p = params.getP();
        BigInteger g = params.getG();
        BigInteger y = keyGen.getY();

        // r' = g^s * y^e mod p
        this.rv = g.modPow(sIn, p).multiply(y.modPow(eIn, p)).mod(p);
        
        // e' = H(r' || M) mod q
        this.ev = hash(this.rv.toString() + message, params.getHashAlgorithm()).mod(params.getQ());
        
        return this.ev.equals(eIn);
    }

    private BigInteger hash(String input, String algorithm) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] dig = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return new BigInteger(1, dig);
    }

    public BigInteger getRv() { return rv; }
    public BigInteger getEv() { return ev; }
}
