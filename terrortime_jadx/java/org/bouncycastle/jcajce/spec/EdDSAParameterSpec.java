package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;

public class EdDSAParameterSpec implements AlgorithmParameterSpec {
    public static final String Ed25519 = "Ed25519";
    public static final String Ed448 = "Ed448";
    private final String curveName;

    public EdDSAParameterSpec(String str) {
        String str2 = Ed25519;
        if (!str.equalsIgnoreCase(str2)) {
            String str3 = Ed448;
            if (!str.equalsIgnoreCase(str3)) {
                if (!str.equals(EdECObjectIdentifiers.id_Ed25519.getId())) {
                    if (!str.equals(EdECObjectIdentifiers.id_Ed448.getId())) {
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
