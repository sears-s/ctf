package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;

public class ProofOfPossession extends ASN1Object implements ASN1Choice {
    public static final int TYPE_KEY_AGREEMENT = 3;
    public static final int TYPE_KEY_ENCIPHERMENT = 2;
    public static final int TYPE_RA_VERIFIED = 0;
    public static final int TYPE_SIGNING_KEY = 1;
    private ASN1Encodable obj;
    private int tagNo;

    public ProofOfPossession() {
        this.tagNo = 0;
        this.obj = DERNull.INSTANCE;
    }

    public ProofOfPossession(int i, POPOPrivKey pOPOPrivKey) {
        this.tagNo = i;
        this.obj = pOPOPrivKey;
    }

    private ProofOfPossession(ASN1TaggedObject aSN1TaggedObject) {
        ASN1Encodable aSN1Encodable;
        this.tagNo = aSN1TaggedObject.getTagNo();
        int i = this.tagNo;
        if (i == 0) {
            aSN1Encodable = DERNull.INSTANCE;
        } else if (i == 1) {
            aSN1Encodable = POPOSigningKey.getInstance(aSN1TaggedObject, false);
        } else if (i == 2 || i == 3) {
            aSN1Encodable = POPOPrivKey.getInstance(aSN1TaggedObject, true);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("unknown tag: ");
            sb.append(this.tagNo);
            throw new IllegalArgumentException(sb.toString());
        }
        this.obj = aSN1Encodable;
    }

    public ProofOfPossession(POPOSigningKey pOPOSigningKey) {
        this.tagNo = 1;
        this.obj = pOPOSigningKey;
    }

    public static ProofOfPossession getInstance(Object obj2) {
        if (obj2 == null || (obj2 instanceof ProofOfPossession)) {
            return (ProofOfPossession) obj2;
        }
        if (obj2 instanceof ASN1TaggedObject) {
            return new ProofOfPossession((ASN1TaggedObject) obj2);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid object: ");
        sb.append(obj2.getClass().getName());
        throw new IllegalArgumentException(sb.toString());
    }

    public ASN1Encodable getObject() {
        return this.obj;
    }

    public int getType() {
        return this.tagNo;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(false, this.tagNo, this.obj);
    }
}
