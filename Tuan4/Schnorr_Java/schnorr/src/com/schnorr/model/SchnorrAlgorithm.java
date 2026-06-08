package com.schnorr.model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * MODEL — Pure cryptographic logic for the Schnorr Digital Signature scheme.
 *
 * Implements the three phases of the protocol:
 *   1. Key Generation  : x  →  y = g^x mod p
 *   2. Signing         : (M, x, k)  →  (e, s)
 *   3. Verification    : (M, e, s, y)  →  valid / invalid
 *
 * No UI or framework dependencies. Fully self-contained.
 */
public class SchnorrAlgorithm {

    // ── System parameters ──────────────────────────────────────────────────────
    private BigInteger p;                              // large prime
    private BigInteger q;                              // prime divisor of (p − 1)
    private BigInteger g;                              // generator of order-q subgroup
    private String     hashAlgorithm = "SHA-256";

    // ── Key pair ───────────────────────────────────────────────────────────────
    private BigInteger x;                              // private key
    private BigInteger y;                              // public key: y = g^x mod p

    // ── Signing intermediates ──────────────────────────────────────────────────
    private BigInteger k;                              // nonce (secret random)
    private BigInteger r;                              // commitment:  r = g^k mod p
    private BigInteger e;                              // challenge:   e = H(r ‖ M) mod q
    private BigInteger s;                              // response:    s = (k − x·e) mod q

    // ── Verification intermediates ─────────────────────────────────────────────
    private BigInteger rv;                             // recovered:   r' = g^s · y^e mod p
    private BigInteger ev;                             // recomputed:  e' = H(r' ‖ M) mod q

    // ── Constructor ────────────────────────────────────────────────────────────
    public SchnorrAlgorithm() {
        // Small demonstration parameters
        p = new BigInteger("48731");
        q = new BigInteger("443");
        g = new BigInteger("11444");
    }

    // ── Parameter management ───────────────────────────────────────────────────

    public void setParameters(BigInteger p, BigInteger q, BigInteger g) {
        this.p = p;
        this.q = q;
        this.g = g;
    }

    public void setHashAlgorithm(String algo) {
        this.hashAlgorithm = algo;
    }

    /**
     * Validates the current system parameters.
     * Checks: p prime, q prime, q | (p−1), g^q ≡ 1 (mod p).
     */
    public boolean validateParameters() {
        if (!p.isProbablePrime(20)) return false;
        if (!q.isProbablePrime(20)) return false;
        if (!p.subtract(BigInteger.ONE).mod(q).equals(BigInteger.ZERO)) return false;
        if (!g.modPow(q, p).equals(BigInteger.ONE)) return false;
        return true;
    }

    // ── Key generation ─────────────────────────────────────────────────────────

    public void setPrivateKey(BigInteger x) {
        this.x = x;
    }

    public void generateRandomPrivateKey() {
        SecureRandom rng = new SecureRandom();
        do {
            x = new BigInteger(q.bitLength(), rng)
                    .mod(q.subtract(BigInteger.ONE))
                    .add(BigInteger.ONE);
        } while (x.compareTo(BigInteger.ONE) < 0
                || x.compareTo(q.subtract(BigInteger.ONE)) > 0);
    }

    public BigInteger computePublicKey() {
        y = g.modPow(x, p);
        return y;
    }

    // ── Signing ────────────────────────────────────────────────────────────────

    /**
     * Signs a message with a randomly generated nonce.
     * @return signature pair (e, s)
     */
    public BigInteger[] sign(String message) throws Exception {
        SecureRandom rng = new SecureRandom();
        do {
            k = new BigInteger(q.bitLength(), rng)
                    .mod(q.subtract(BigInteger.ONE))
                    .add(BigInteger.ONE);
        } while (k.compareTo(BigInteger.ONE) < 0
                || k.compareTo(q.subtract(BigInteger.ONE)) > 0);
        return signWithNonce(message, k);
    }

    /**
     * Signs a message with a caller-supplied nonce (for demos/testing).
     * @return signature pair (e, s)
     */
    public BigInteger[] signWithNonce(String message, BigInteger nonce) throws Exception {
        k = nonce;
        r = g.modPow(k, p);
        e = hash(r.toString() + message).mod(q);
        s = k.subtract(x.multiply(e)).mod(q);
        return new BigInteger[]{ e, s };
    }

    // ── Verification ───────────────────────────────────────────────────────────

    /**
     * Verifies a Schnorr signature (eIn, sIn) against a message.
     * @return true iff the signature is valid
     */
    public boolean verify(String message, BigInteger eIn, BigInteger sIn) throws Exception {
        rv = g.modPow(sIn, p).multiply(y.modPow(eIn, p)).mod(p);
        ev = hash(rv.toString() + message).mod(q);
        return ev.equals(eIn);
    }

    // ── Internal helpers ───────────────────────────────────────────────────────

    private BigInteger hash(String input) throws Exception {
        MessageDigest md  = MessageDigest.getInstance(hashAlgorithm);
        byte[]        dig = md.digest(input.getBytes("UTF-8"));
        return new BigInteger(1, dig);
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public BigInteger getP()             { return p;             }
    public BigInteger getQ()             { return q;             }
    public BigInteger getG()             { return g;             }
    public BigInteger getX()             { return x;             }
    public BigInteger getY()             { return y;             }
    public BigInteger getK()             { return k;             }
    public BigInteger getR()             { return r;             }
    public BigInteger getE()             { return e;             }
    public BigInteger getS()             { return s;             }
    public BigInteger getRv()            { return rv;            }
    public BigInteger getEv()            { return ev;            }
    public String     getHashAlgorithm() { return hashAlgorithm; }
}
