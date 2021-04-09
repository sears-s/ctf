package org.jivesoftware.smack.sasl.core;

import java.security.InvalidKeyException;

public interface ScramHmac {
    String getHmacName();

    byte[] hmac(byte[] bArr, byte[] bArr2) throws InvalidKeyException;
}
