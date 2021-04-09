package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.DirectoryString;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.IssuerSerial;

public class ProcurationSyntax extends ASN1Object {
    private IssuerSerial certRef;
    private String country;
    private GeneralName thirdPerson;
    private DirectoryString typeOfSubstitution;

    public ProcurationSyntax(String str, DirectoryString directoryString, GeneralName generalName) {
        this.country = str;
        this.typeOfSubstitution = directoryString;
        this.thirdPerson = generalName;
        this.certRef = null;
    }

    public ProcurationSyntax(String str, DirectoryString directoryString, IssuerSerial issuerSerial) {
        this.country = str;
        this.typeOfSubstitution = directoryString;
        this.thirdPerson = null;
        this.certRef = issuerSerial;
    }

    private ProcurationSyntax(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() < 1 || aSN1Sequence.size() > 3) {
            StringBuilder sb = new StringBuilder();
            sb.append("Bad sequence size: ");
            sb.append(aSN1Sequence.size());
            throw new IllegalArgumentException(sb.toString());
        }
        Enumeration objects = aSN1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            ASN1TaggedObject instance = ASN1TaggedObject.getInstance(objects.nextElement());
            int tagNo = instance.getTagNo();
            if (tagNo == 1) {
                this.country = DERPrintableString.getInstance(instance, true).getString();
            } else if (tagNo == 2) {
                this.typeOfSubstitution = DirectoryString.getInstance(instance, true);
            } else if (tagNo == 3) {
                ASN1Primitive object = instance.getObject();
                if (object instanceof ASN1TaggedObject) {
                    this.thirdPerson = GeneralName.getInstance(object);
                } else {
                    this.certRef = IssuerSerial.getInstance(object);
                }
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Bad tag number: ");
                sb2.append(instance.getTagNo());
                throw new IllegalArgumentException(sb2.toString());
            }
        }
    }

    public static ProcurationSyntax getInstance(Object obj) {
        if (obj == null || (obj instanceof ProcurationSyntax)) {
            return (ProcurationSyntax) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new ProcurationSyntax((ASN1Sequence) obj);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("illegal object in getInstance: ");
        sb.append(obj.getClass().getName());
        throw new IllegalArgumentException(sb.toString());
    }

    public IssuerSerial getCertRef() {
        return this.certRef;
    }

    public String getCountry() {
        return this.country;
    }

    public GeneralName getThirdPerson() {
        return this.thirdPerson;
    }

    public DirectoryString getTypeOfSubstitution() {
        return this.typeOfSubstitution;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        String str = this.country;
        if (str != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 1, new DERPrintableString(str, true)));
        }
        DirectoryString directoryString = this.typeOfSubstitution;
        if (directoryString != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 2, directoryString));
        }
        GeneralName generalName = this.thirdPerson;
        if (generalName != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 3, generalName));
        } else {
            aSN1EncodableVector.add(new DERTaggedObject(true, 3, this.certRef));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
