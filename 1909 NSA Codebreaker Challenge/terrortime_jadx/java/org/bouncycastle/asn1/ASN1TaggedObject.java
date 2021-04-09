package org.bouncycastle.asn1;

import java.io.IOException;

public abstract class ASN1TaggedObject extends ASN1Primitive implements ASN1TaggedObjectParser {
    boolean empty = false;
    boolean explicit = true;
    ASN1Encodable obj = null;
    int tagNo;

    public ASN1TaggedObject(boolean z, int i, ASN1Encodable aSN1Encodable) {
        if (aSN1Encodable instanceof ASN1Choice) {
            this.explicit = true;
        } else {
            this.explicit = z;
        }
        this.tagNo = i;
        if (!this.explicit) {
            boolean z2 = aSN1Encodable.toASN1Primitive() instanceof ASN1Set;
        }
        this.obj = aSN1Encodable;
    }

    public static ASN1TaggedObject getInstance(Object obj2) {
        if (obj2 == null || (obj2 instanceof ASN1TaggedObject)) {
            return (ASN1TaggedObject) obj2;
        }
        if (obj2 instanceof byte[]) {
            try {
                return getInstance(fromByteArray((byte[]) obj2));
            } catch (IOException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("failed to construct tagged object from byte[]: ");
                sb.append(e.getMessage());
                throw new IllegalArgumentException(sb.toString());
            }
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("unknown object in getInstance: ");
            sb2.append(obj2.getClass().getName());
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    public static ASN1TaggedObject getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        if (z) {
            return (ASN1TaggedObject) aSN1TaggedObject.getObject();
        }
        throw new IllegalArgumentException("implicitly tagged tagged object");
    }

    /* access modifiers changed from: 0000 */
    public boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1TaggedObject)) {
            return false;
        }
        ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) aSN1Primitive;
        if (this.tagNo != aSN1TaggedObject.tagNo || this.empty != aSN1TaggedObject.empty || this.explicit != aSN1TaggedObject.explicit) {
            return false;
        }
        ASN1Encodable aSN1Encodable = this.obj;
        if (aSN1Encodable == null) {
            if (aSN1TaggedObject.obj != null) {
                return false;
            }
        } else if (!aSN1Encodable.toASN1Primitive().equals(aSN1TaggedObject.obj.toASN1Primitive())) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public abstract void encode(ASN1OutputStream aSN1OutputStream) throws IOException;

    public ASN1Primitive getLoadedObject() {
        return toASN1Primitive();
    }

    public ASN1Primitive getObject() {
        ASN1Encodable aSN1Encodable = this.obj;
        if (aSN1Encodable != null) {
            return aSN1Encodable.toASN1Primitive();
        }
        return null;
    }

    public ASN1Encodable getObjectParser(int i, boolean z) throws IOException {
        if (i == 4) {
            return ASN1OctetString.getInstance(this, z).parser();
        }
        if (i == 16) {
            return ASN1Sequence.getInstance(this, z).parser();
        }
        if (i == 17) {
            return ASN1Set.getInstance(this, z).parser();
        }
        if (z) {
            return getObject();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("implicit tagging not implemented for tag: ");
        sb.append(i);
        throw new ASN1Exception(sb.toString());
    }

    public int getTagNo() {
        return this.tagNo;
    }

    public int hashCode() {
        int i = this.tagNo;
        ASN1Encodable aSN1Encodable = this.obj;
        return aSN1Encodable != null ? i ^ aSN1Encodable.hashCode() : i;
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public boolean isExplicit() {
        return this.explicit;
    }

    /* access modifiers changed from: 0000 */
    public ASN1Primitive toDERObject() {
        return new DERTaggedObject(this.explicit, this.tagNo, this.obj);
    }

    /* access modifiers changed from: 0000 */
    public ASN1Primitive toDLObject() {
        return new DLTaggedObject(this.explicit, this.tagNo, this.obj);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(this.tagNo);
        sb.append("]");
        sb.append(this.obj);
        return sb.toString();
    }
}
