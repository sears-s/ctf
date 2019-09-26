package org.minidns.dnssec.algorithms;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.minidns.dnssec.DigestCalculator;

public class JavaSecDigestCalculator implements DigestCalculator {
    private MessageDigest md;

    public JavaSecDigestCalculator(String algorithm) throws NoSuchAlgorithmException {
        this.md = MessageDigest.getInstance(algorithm);
    }

    public byte[] digest(byte[] bytes) {
        return this.md.digest(bytes);
    }
}
