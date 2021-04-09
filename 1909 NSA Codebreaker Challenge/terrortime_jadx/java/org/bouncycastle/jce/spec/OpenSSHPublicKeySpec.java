package org.bouncycastle.jce.spec;

import java.security.spec.EncodedKeySpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class OpenSSHPublicKeySpec extends EncodedKeySpec {
    private static final String[] allowedTypes = {"ssh-rsa", "ssh-ed25519", "ssh-dss"};
    private final String type;

    public OpenSSHPublicKeySpec(byte[] bArr) {
        super(bArr);
        int i = 0;
        int i2 = (((bArr[0] & 255) << 24) | ((bArr[1] & 255) << Tnaf.POW_2_WIDTH) | ((bArr[2] & 255) << 8) | (bArr[3] & 255)) + 4;
        if (i2 < bArr.length) {
            this.type = Strings.fromByteArray(Arrays.copyOfRange(bArr, 4, i2));
            if (!this.type.startsWith("ecdsa")) {
                while (true) {
                    String[] strArr = allowedTypes;
                    if (i >= strArr.length) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("unrecognised public key type ");
                        sb.append(this.type);
                        throw new IllegalArgumentException(sb.toString());
                    } else if (!strArr[i].equals(this.type)) {
                        i++;
                    } else {
                        return;
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("invalid public key blob: type field longer than blob");
        }
    }

    public String getFormat() {
        return "OpenSSH";
    }

    public String getType() {
        return this.type;
    }
}
