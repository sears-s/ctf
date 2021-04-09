package org.bouncycastle.asn1;

import java.io.IOException;

public abstract class ASN1External extends ASN1Primitive {
    protected ASN1Primitive dataValueDescriptor;
    protected ASN1ObjectIdentifier directReference;
    protected int encoding;
    protected ASN1Primitive externalContent;
    protected ASN1Integer indirectReference;

    public ASN1External(ASN1EncodableVector aSN1EncodableVector) {
        int i = 0;
        ASN1Primitive objFromVector = getObjFromVector(aSN1EncodableVector, 0);
        if (objFromVector instanceof ASN1ObjectIdentifier) {
            this.directReference = (ASN1ObjectIdentifier) objFromVector;
            objFromVector = getObjFromVector(aSN1EncodableVector, 1);
            i = 1;
        }
        if (objFromVector instanceof ASN1Integer) {
            this.indirectReference = (ASN1Integer) objFromVector;
            i++;
            objFromVector = getObjFromVector(aSN1EncodableVector, i);
        }
        if (!(objFromVector instanceof ASN1TaggedObject)) {
            this.dataValueDescriptor = objFromVector;
            i++;
            objFromVector = getObjFromVector(aSN1EncodableVector, i);
        }
        if (aSN1EncodableVector.size() != i + 1) {
            throw new IllegalArgumentException("input vector too large");
        } else if (objFromVector instanceof ASN1TaggedObject) {
            ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) objFromVector;
            setEncoding(aSN1TaggedObject.getTagNo());
            this.externalContent = aSN1TaggedObject.getObject();
        } else {
            throw new IllegalArgumentException("No tagged object found in vector. Structure doesn't seem to be of type External");
        }
    }

    public ASN1External(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Integer aSN1Integer, ASN1Primitive aSN1Primitive, int i, ASN1Primitive aSN1Primitive2) {
        setDirectReference(aSN1ObjectIdentifier);
        setIndirectReference(aSN1Integer);
        setDataValueDescriptor(aSN1Primitive);
        setEncoding(i);
        setExternalContent(aSN1Primitive2.toASN1Primitive());
    }

    public ASN1External(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Integer aSN1Integer, ASN1Primitive aSN1Primitive, DERTaggedObject dERTaggedObject) {
        this(aSN1ObjectIdentifier, aSN1Integer, aSN1Primitive, dERTaggedObject.getTagNo(), dERTaggedObject.toASN1Primitive());
    }

    private ASN1Primitive getObjFromVector(ASN1EncodableVector aSN1EncodableVector, int i) {
        if (aSN1EncodableVector.size() > i) {
            return aSN1EncodableVector.get(i).toASN1Primitive();
        }
        throw new IllegalArgumentException("too few objects in input vector");
    }

    private void setDataValueDescriptor(ASN1Primitive aSN1Primitive) {
        this.dataValueDescriptor = aSN1Primitive;
    }

    private void setDirectReference(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.directReference = aSN1ObjectIdentifier;
    }

    private void setEncoding(int i) {
        if (i < 0 || i > 2) {
            StringBuilder sb = new StringBuilder();
            sb.append("invalid encoding value: ");
            sb.append(i);
            throw new IllegalArgumentException(sb.toString());
        }
        this.encoding = i;
    }

    private void setExternalContent(ASN1Primitive aSN1Primitive) {
        this.externalContent = aSN1Primitive;
    }

    private void setIndirectReference(ASN1Integer aSN1Integer) {
        this.indirectReference = aSN1Integer;
    }

    /* access modifiers changed from: 0000 */
    public boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1External)) {
            return false;
        }
        if (this == aSN1Primitive) {
            return true;
        }
        ASN1External aSN1External = (ASN1External) aSN1Primitive;
        ASN1ObjectIdentifier aSN1ObjectIdentifier = this.directReference;
        if (aSN1ObjectIdentifier != null) {
            ASN1ObjectIdentifier aSN1ObjectIdentifier2 = aSN1External.directReference;
            if (aSN1ObjectIdentifier2 == null || !aSN1ObjectIdentifier2.equals(aSN1ObjectIdentifier)) {
                return false;
            }
        }
        ASN1Integer aSN1Integer = this.indirectReference;
        if (aSN1Integer != null) {
            ASN1Integer aSN1Integer2 = aSN1External.indirectReference;
            if (aSN1Integer2 == null || !aSN1Integer2.equals(aSN1Integer)) {
                return false;
            }
        }
        ASN1Primitive aSN1Primitive2 = this.dataValueDescriptor;
        if (aSN1Primitive2 != null) {
            ASN1Primitive aSN1Primitive3 = aSN1External.dataValueDescriptor;
            if (aSN1Primitive3 == null || !aSN1Primitive3.equals(aSN1Primitive2)) {
                return false;
            }
        }
        return this.externalContent.equals(aSN1External.externalContent);
    }

    /* access modifiers changed from: 0000 */
    public int encodedLength() throws IOException {
        return getEncoded().length;
    }

    public ASN1Primitive getDataValueDescriptor() {
        return this.dataValueDescriptor;
    }

    public ASN1ObjectIdentifier getDirectReference() {
        return this.directReference;
    }

    public int getEncoding() {
        return this.encoding;
    }

    public ASN1Primitive getExternalContent() {
        return this.externalContent;
    }

    public ASN1Integer getIndirectReference() {
        return this.indirectReference;
    }

    public int hashCode() {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = this.directReference;
        int hashCode = aSN1ObjectIdentifier != null ? aSN1ObjectIdentifier.hashCode() : 0;
        ASN1Integer aSN1Integer = this.indirectReference;
        if (aSN1Integer != null) {
            hashCode ^= aSN1Integer.hashCode();
        }
        ASN1Primitive aSN1Primitive = this.dataValueDescriptor;
        if (aSN1Primitive != null) {
            hashCode ^= aSN1Primitive.hashCode();
        }
        return hashCode ^ this.externalContent.hashCode();
    }

    /* access modifiers changed from: 0000 */
    public boolean isConstructed() {
        return true;
    }

    /* access modifiers changed from: 0000 */
    public ASN1Primitive toDERObject() {
        if (this instanceof DERExternal) {
            return this;
        }
        DERExternal dERExternal = new DERExternal(this.directReference, this.indirectReference, this.dataValueDescriptor, this.encoding, this.externalContent);
        return dERExternal;
    }
}
