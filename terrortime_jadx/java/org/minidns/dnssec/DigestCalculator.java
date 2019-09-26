package org.minidns.dnssec;

public interface DigestCalculator {
    byte[] digest(byte[] bArr);
}
