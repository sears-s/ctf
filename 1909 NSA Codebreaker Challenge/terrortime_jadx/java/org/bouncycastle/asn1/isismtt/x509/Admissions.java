package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.GeneralName;

public class Admissions extends ASN1Object {
    private GeneralName admissionAuthority;
    private NamingAuthority namingAuthority;
    private ASN1Sequence professionInfos;

    private Admissions(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() <= 3) {
            Enumeration objects = aSN1Sequence.getObjects();
            ASN1Encodable aSN1Encodable = (ASN1Encodable) objects.nextElement();
            String str = "Bad tag number: ";
            if (aSN1Encodable instanceof ASN1TaggedObject) {
                ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) aSN1Encodable;
                int tagNo = aSN1TaggedObject.getTagNo();
                if (tagNo == 0) {
                    this.admissionAuthority = GeneralName.getInstance(aSN1TaggedObject, true);
                } else if (tagNo == 1) {
                    this.namingAuthority = NamingAuthority.getInstance(aSN1TaggedObject, true);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    sb.append(aSN1TaggedObject.getTagNo());
                    throw new IllegalArgumentException(sb.toString());
                }
                aSN1Encodable = (ASN1Encodable) objects.nextElement();
            }
            if (aSN1Encodable instanceof ASN1TaggedObject) {
                ASN1TaggedObject aSN1TaggedObject2 = (ASN1TaggedObject) aSN1Encodable;
                if (aSN1TaggedObject2.getTagNo() == 1) {
                    this.namingAuthority = NamingAuthority.getInstance(aSN1TaggedObject2, true);
                    aSN1Encodable = (ASN1Encodable) objects.nextElement();
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str);
                    sb2.append(aSN1TaggedObject2.getTagNo());
                    throw new IllegalArgumentException(sb2.toString());
                }
            }
            this.professionInfos = ASN1Sequence.getInstance(aSN1Encodable);
            if (objects.hasMoreElements()) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Bad object encountered: ");
                sb3.append(objects.nextElement().getClass());
                throw new IllegalArgumentException(sb3.toString());
            }
            return;
        }
        StringBuilder sb4 = new StringBuilder();
        sb4.append("Bad sequence size: ");
        sb4.append(aSN1Sequence.size());
        throw new IllegalArgumentException(sb4.toString());
    }

    public Admissions(GeneralName generalName, NamingAuthority namingAuthority2, ProfessionInfo[] professionInfoArr) {
        this.admissionAuthority = generalName;
        this.namingAuthority = namingAuthority2;
        this.professionInfos = new DERSequence((ASN1Encodable[]) professionInfoArr);
    }

    public static Admissions getInstance(Object obj) {
        if (obj == null || (obj instanceof Admissions)) {
            return (Admissions) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new Admissions((ASN1Sequence) obj);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("illegal object in getInstance: ");
        sb.append(obj.getClass().getName());
        throw new IllegalArgumentException(sb.toString());
    }

    public GeneralName getAdmissionAuthority() {
        return this.admissionAuthority;
    }

    public NamingAuthority getNamingAuthority() {
        return this.namingAuthority;
    }

    public ProfessionInfo[] getProfessionInfos() {
        ProfessionInfo[] professionInfoArr = new ProfessionInfo[this.professionInfos.size()];
        Enumeration objects = this.professionInfos.getObjects();
        int i = 0;
        while (objects.hasMoreElements()) {
            int i2 = i + 1;
            professionInfoArr[i] = ProfessionInfo.getInstance(objects.nextElement());
            i = i2;
        }
        return professionInfoArr;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        GeneralName generalName = this.admissionAuthority;
        if (generalName != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 0, generalName));
        }
        NamingAuthority namingAuthority2 = this.namingAuthority;
        if (namingAuthority2 != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 1, namingAuthority2));
        }
        aSN1EncodableVector.add(this.professionInfos);
        return new DERSequence(aSN1EncodableVector);
    }
}
