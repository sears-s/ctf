package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;

public class XDHParameterSpec implements AlgorithmParameterSpec {
    public static final String X25519 = "X25519";
    public static final String X448 = "X448";
    private final String curveName;

    public XDHParameterSpec(String str) {
        String str2 = X25519;
        if (!str.equalsIgnoreCase(str2)) {
            String str3 = X448;
            if (!str.equalsIgnoreCase(str3)) {
                if (!str.equals(EdECObjectIdentifiers.id_X25519.getId())) {
                    if (!str.equals(EdECObjectIdentifiers.id_X448.getId())) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("unrecognized curve name: ");
                        sb.append(str);
                        throw new IllegalArgumentException(sb.toString());
                    }
                }
            }
            this.curveName = str3;
            return;
        }
        this.curveName = str2;
    }

    public String getCurveName() {
        return this.curveName;
    }
}
