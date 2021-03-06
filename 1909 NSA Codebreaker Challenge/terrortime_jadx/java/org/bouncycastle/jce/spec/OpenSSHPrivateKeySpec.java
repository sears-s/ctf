package org.bouncycastle.jce.spec;

import java.security.spec.EncodedKeySpec;

public class OpenSSHPrivateKeySpec extends EncodedKeySpec {
    private final String format;

    public OpenSSHPrivateKeySpec(byte[] bArr) {
        String str;
        super(bArr);
        if (bArr[0] == 48) {
            str = "ASN.1";
        } else if (bArr[0] == 111) {
            str = "OpenSSH";
        } else {
            throw new IllegalArgumentException("unknown byte encoding");
        }
        this.format = str;
    }

    public String getFormat() {
        return this.format;
    }
}
