package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.DirectoryString;

public class ProfessionInfo extends ASN1Object {
    public static final ASN1ObjectIdentifier Notar;
    public static final ASN1ObjectIdentifier Notariatsverwalter;
    public static final ASN1ObjectIdentifier Notariatsverwalterin;
    public static final ASN1ObjectIdentifier Notarin;
    public static final ASN1ObjectIdentifier Notarvertreter;
    public static final ASN1ObjectIdentifier Notarvertreterin;
    public static final ASN1ObjectIdentifier Patentanwalt;
    public static final ASN1ObjectIdentifier Patentanwltin;
    public static final ASN1ObjectIdentifier Rechtsanwalt;
    public static final ASN1ObjectIdentifier Rechtsanwltin;
    public static final ASN1ObjectIdentifier Rechtsbeistand;
    public static final ASN1ObjectIdentifier Steuerberater;
    public static final ASN1ObjectIdentifier Steuerberaterin;
    public static final ASN1ObjectIdentifier Steuerbevollmchtigte;
    public static final ASN1ObjectIdentifier Steuerbevollmchtigter;
    public static final ASN1ObjectIdentifier VereidigteBuchprferin;
    public static final ASN1ObjectIdentifier VereidigterBuchprfer;
    public static final ASN1ObjectIdentifier Wirtschaftsprfer;
    public static final ASN1ObjectIdentifier Wirtschaftsprferin;
    private ASN1OctetString addProfessionInfo;
    private NamingAuthority namingAuthority;
    private ASN1Sequence professionItems;
    private ASN1Sequence professionOIDs;
    private String registrationNumber;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb.append(".1");
        Rechtsanwltin = new ASN1ObjectIdentifier(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb2.append(".2");
        Rechtsanwalt = new ASN1ObjectIdentifier(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb3.append(".3");
        Rechtsbeistand = new ASN1ObjectIdentifier(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb4.append(".4");
        Steuerberaterin = new ASN1ObjectIdentifier(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb5.append(".5");
        Steuerberater = new ASN1ObjectIdentifier(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb6.append(".6");
        Steuerbevollmchtigte = new ASN1ObjectIdentifier(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb7.append(".7");
        Steuerbevollmchtigter = new ASN1ObjectIdentifier(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb8.append(".8");
        Notarin = new ASN1ObjectIdentifier(sb8.toString());
        StringBuilder sb9 = new StringBuilder();
        sb9.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb9.append(".9");
        Notar = new ASN1ObjectIdentifier(sb9.toString());
        StringBuilder sb10 = new StringBuilder();
        sb10.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb10.append(".10");
        Notarvertreterin = new ASN1ObjectIdentifier(sb10.toString());
        StringBuilder sb11 = new StringBuilder();
        sb11.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb11.append(".11");
        Notarvertreter = new ASN1ObjectIdentifier(sb11.toString());
        StringBuilder sb12 = new StringBuilder();
        sb12.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb12.append(".12");
        Notariatsverwalterin = new ASN1ObjectIdentifier(sb12.toString());
        StringBuilder sb13 = new StringBuilder();
        sb13.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb13.append(".13");
        Notariatsverwalter = new ASN1ObjectIdentifier(sb13.toString());
        StringBuilder sb14 = new StringBuilder();
        sb14.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb14.append(".14");
        Wirtschaftsprferin = new ASN1ObjectIdentifier(sb14.toString());
        StringBuilder sb15 = new StringBuilder();
        sb15.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb15.append(".15");
        Wirtschaftsprfer = new ASN1ObjectIdentifier(sb15.toString());
        StringBuilder sb16 = new StringBuilder();
        sb16.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb16.append(".16");
        VereidigteBuchprferin = new ASN1ObjectIdentifier(sb16.toString());
        StringBuilder sb17 = new StringBuilder();
        sb17.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb17.append(".17");
        VereidigterBuchprfer = new ASN1ObjectIdentifier(sb17.toString());
        StringBuilder sb18 = new StringBuilder();
        sb18.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb18.append(".18");
        Patentanwltin = new ASN1ObjectIdentifier(sb18.toString());
        StringBuilder sb19 = new StringBuilder();
        sb19.append(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern);
        sb19.append(".19");
        Patentanwalt = new ASN1ObjectIdentifier(sb19.toString());
    }

    private ProfessionInfo(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() <= 5) {
            Enumeration objects = aSN1Sequence.getObjects();
            ASN1Encodable aSN1Encodable = (ASN1Encodable) objects.nextElement();
            if (aSN1Encodable instanceof ASN1TaggedObject) {
                ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) aSN1Encodable;
                if (aSN1TaggedObject.getTagNo() == 0) {
                    this.namingAuthority = NamingAuthority.getInstance(aSN1TaggedObject, true);
                    aSN1Encodable = (ASN1Encodable) objects.nextElement();
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Bad tag number: ");
                    sb.append(aSN1TaggedObject.getTagNo());
                    throw new IllegalArgumentException(sb.toString());
                }
            }
            this.professionItems = ASN1Sequence.getInstance(aSN1Encodable);
            String str = "Bad object encountered: ";
            if (objects.hasMoreElements()) {
                ASN1Encodable aSN1Encodable2 = (ASN1Encodable) objects.nextElement();
                if (aSN1Encodable2 instanceof ASN1Sequence) {
                    this.professionOIDs = ASN1Sequence.getInstance(aSN1Encodable2);
                } else if (aSN1Encodable2 instanceof DERPrintableString) {
                    this.registrationNumber = DERPrintableString.getInstance(aSN1Encodable2).getString();
                } else if (aSN1Encodable2 instanceof ASN1OctetString) {
                    this.addProfessionInfo = ASN1OctetString.getInstance(aSN1Encodable2);
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str);
                    sb2.append(aSN1Encodable2.getClass());
                    throw new IllegalArgumentException(sb2.toString());
                }
            }
            if (objects.hasMoreElements()) {
                ASN1Encodable aSN1Encodable3 = (ASN1Encodable) objects.nextElement();
                if (aSN1Encodable3 instanceof DERPrintableString) {
                    this.registrationNumber = DERPrintableString.getInstance(aSN1Encodable3).getString();
                } else if (aSN1Encodable3 instanceof DEROctetString) {
                    this.addProfessionInfo = (DEROctetString) aSN1Encodable3;
                } else {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(str);
                    sb3.append(aSN1Encodable3.getClass());
                    throw new IllegalArgumentException(sb3.toString());
                }
            }
            if (objects.hasMoreElements()) {
                ASN1Encodable aSN1Encodable4 = (ASN1Encodable) objects.nextElement();
                if (aSN1Encodable4 instanceof DEROctetString) {
                    this.addProfessionInfo = (DEROctetString) aSN1Encodable4;
                    return;
                }
                StringBuilder sb4 = new StringBuilder();
                sb4.append(str);
                sb4.append(aSN1Encodable4.getClass());
                throw new IllegalArgumentException(sb4.toString());
            }
            return;
        }
        StringBuilder sb5 = new StringBuilder();
        sb5.append("Bad sequence size: ");
        sb5.append(aSN1Sequence.size());
        throw new IllegalArgumentException(sb5.toString());
    }

    public ProfessionInfo(NamingAuthority namingAuthority2, DirectoryString[] directoryStringArr, ASN1ObjectIdentifier[] aSN1ObjectIdentifierArr, String str, ASN1OctetString aSN1OctetString) {
        this.namingAuthority = namingAuthority2;
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != directoryStringArr.length; i++) {
            aSN1EncodableVector.add(directoryStringArr[i]);
        }
        this.professionItems = new DERSequence(aSN1EncodableVector);
        if (aSN1ObjectIdentifierArr != null) {
            ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
            for (int i2 = 0; i2 != aSN1ObjectIdentifierArr.length; i2++) {
                aSN1EncodableVector2.add(aSN1ObjectIdentifierArr[i2]);
            }
            this.professionOIDs = new DERSequence(aSN1EncodableVector2);
        }
        this.registrationNumber = str;
        this.addProfessionInfo = aSN1OctetString;
    }

    public static ProfessionInfo getInstance(Object obj) {
        if (obj == null || (obj instanceof ProfessionInfo)) {
            return (ProfessionInfo) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new ProfessionInfo((ASN1Sequence) obj);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("illegal object in getInstance: ");
        sb.append(obj.getClass().getName());
        throw new IllegalArgumentException(sb.toString());
    }

    public ASN1OctetString getAddProfessionInfo() {
        return this.addProfessionInfo;
    }

    public NamingAuthority getNamingAuthority() {
        return this.namingAuthority;
    }

    public DirectoryString[] getProfessionItems() {
        DirectoryString[] directoryStringArr = new DirectoryString[this.professionItems.size()];
        Enumeration objects = this.professionItems.getObjects();
        int i = 0;
        while (objects.hasMoreElements()) {
            int i2 = i + 1;
            directoryStringArr[i] = DirectoryString.getInstance(objects.nextElement());
            i = i2;
        }
        return directoryStringArr;
    }

    public ASN1ObjectIdentifier[] getProfessionOIDs() {
        ASN1Sequence aSN1Sequence = this.professionOIDs;
        int i = 0;
        if (aSN1Sequence == null) {
            return new ASN1ObjectIdentifier[0];
        }
        ASN1ObjectIdentifier[] aSN1ObjectIdentifierArr = new ASN1ObjectIdentifier[aSN1Sequence.size()];
        Enumeration objects = this.professionOIDs.getObjects();
        while (objects.hasMoreElements()) {
            int i2 = i + 1;
            aSN1ObjectIdentifierArr[i] = ASN1ObjectIdentifier.getInstance(objects.nextElement());
            i = i2;
        }
        return aSN1ObjectIdentifierArr;
    }

    public String getRegistrationNumber() {
        return this.registrationNumber;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        NamingAuthority namingAuthority2 = this.namingAuthority;
        if (namingAuthority2 != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 0, namingAuthority2));
        }
        aSN1EncodableVector.add(this.professionItems);
        ASN1Sequence aSN1Sequence = this.professionOIDs;
        if (aSN1Sequence != null) {
            aSN1EncodableVector.add(aSN1Sequence);
        }
        String str = this.registrationNumber;
        if (str != null) {
            aSN1EncodableVector.add(new DERPrintableString(str, true));
        }
        ASN1OctetString aSN1OctetString = this.addProfessionInfo;
        if (aSN1OctetString != null) {
            aSN1EncodableVector.add(aSN1OctetString);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
