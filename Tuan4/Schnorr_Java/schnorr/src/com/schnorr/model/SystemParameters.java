package com.schnorr.model;

import java.math.BigInteger;

public class SystemParameters {
    private BigInteger p; // large prime
    private BigInteger q; // prime divisor of (p - 1)
    private BigInteger g; // generator
    private String hashAlgorithm = "SHA-256";

    public SystemParameters() {
        // Small demo parameters by default
        this.p = new BigInteger("48731");
        this.q = new BigInteger("443");
        this.g = new BigInteger("11444");
    }

    public void setParameters(BigInteger p, BigInteger q, BigInteger g) {
        this.p = p;
        this.q = q;
        this.g = g;
    }

    public void setHashAlgorithm(String algo) {
        this.hashAlgorithm = algo;
    }

    public void validateParameters() {
        if (!p.isProbablePrime(20)) {
            throw new IllegalArgumentException("Tham số p phải là số nguyên tố.");
        }
        if (!q.isProbablePrime(20)) {
            throw new IllegalArgumentException("Tham số q phải là số nguyên tố.");
        }
        if (!p.subtract(BigInteger.ONE).mod(q).equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("Tham số q phải là ước số của (p - 1).");
        }
        if (!g.modPow(q, p).equals(BigInteger.ONE)) {
            throw new IllegalArgumentException("Phần tử sinh g không thỏa mãn điều kiện g^q ≡ 1 (mod p).");
        }
    }

    public BigInteger getP() { return p; }
    public BigInteger getQ() { return q; }
    public BigInteger getG() { return g; }
    public String getHashAlgorithm() { return hashAlgorithm; }
}
