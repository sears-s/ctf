package org.bouncycastle.asn1;

import java.io.IOException;

public abstract class ASN1Null extends ASN1Primitive {
    ASN1Null() {
    }

    public static ASN1Null getInstance(Object obj) {
        if (obj instanceof ASN1Null) {
            return (ASN1Null) obj;
        }
        if (obj == null) {
            return null;
        }
        try {
            return getInstance(ASN1Primitive.fromByteArray((byte[]) obj));
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("failed to construct NULL from byte[]: ");
            sb.append(e.getMessage());
            throw new IllegalArgumentException(sb.toString());
        } catch (ClassCastException e2) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("unknown object in getInstance(): ");
            sb2.append(obj.getClass().getName());
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        return aSN1Primitive instanceof ASN1Null;
    }

    /* access modifiers changed from: 0000 */
    public abstract void encode(ASN1OutputStream aSN1OutputStream) throws IOException;

    public int hashCode() {
        return -1;
    }

    public String toString() {
        return "NULL";
    }
}
