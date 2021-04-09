package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class PartialHashtree extends ASN1Object {
    private ASN1Sequence values;

    private PartialHashtree(ASN1Sequence aSN1Sequence) {
        int i = 0;
        while (i != aSN1Sequence.size()) {
            if (aSN1Sequence.getObjectAt(i) instanceof DEROctetString) {
                i++;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("unknown object in constructor: ");
                sb.append(aSN1Sequence.getObjectAt(i).getClass().getName());
                throw new IllegalArgumentException(sb.toString());
            }
        }
        this.values = aSN1Sequence;
    }

    public PartialHashtree(byte[] bArr) {
        this(new byte[][]{bArr});
    }

    public PartialHashtree(byte[][] bArr) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != bArr.length; i++) {
            aSN1EncodableVector.add(new DEROctetString(Arrays.clone(bArr[i])));
        }
        this.values = new DERSequence(aSN1EncodableVector);
    }

    public static PartialHashtree getInstance(Object obj) {
        if (obj instanceof PartialHashtree) {
            return (PartialHashtree) obj;
        }
        if (obj != null) {
            return new PartialHashtree(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public byte[][] getValues() {
        byte[][] bArr = new byte[this.values.size()][];
        for (int i = 0; i != bArr.length; i++) {
            bArr[i] = Arrays.clone(ASN1OctetString.getInstance(this.values.getObjectAt(i)).getOctets());
        }
        return bArr;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.values;
    }
}
