package org.bouncycastle.asn1.misc;

import org.bouncycastle.asn1.DERIA5String;

public class NetscapeRevocationURL extends DERIA5String {
    public NetscapeRevocationURL(DERIA5String dERIA5String) {
        super(dERIA5String.getString());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("NetscapeRevocationURL: ");
        sb.append(getString());
        return sb.toString();
    }
}
