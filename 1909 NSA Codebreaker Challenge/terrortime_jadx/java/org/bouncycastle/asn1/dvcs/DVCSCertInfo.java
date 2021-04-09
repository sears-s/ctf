package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class DVCSCertInfo extends ASN1Object {
    private static final int DEFAULT_VERSION = 1;
    private static final int TAG_CERTS = 3;
    private static final int TAG_DV_STATUS = 0;
    private static final int TAG_POLICY = 1;
    private static final int TAG_REQ_SIGNATURE = 2;
    private ASN1Sequence certs;
    private DVCSRequestInformation dvReqInfo;
    private PKIStatusInfo dvStatus;
    private Extensions extensions;
    private DigestInfo messageImprint;
    private PolicyInformation policy;
    private ASN1Set reqSignature;
    private DVCSTime responseTime;
    private ASN1Integer serialNumber;
    private int version = 1;

    private DVCSCertInfo(ASN1Sequence aSN1Sequence) {
        int i;
        ASN1Encodable objectAt = aSN1Sequence.getObjectAt(0);
        try {
            this.version = ASN1Integer.getInstance(objectAt).getValue().intValue();
            try {
                objectAt = aSN1Sequence.getObjectAt(1);
            } catch (IllegalArgumentException e) {
            }
            i = 2;
        } catch (IllegalArgumentException e2) {
            i = 1;
        }
        this.dvReqInfo = DVCSRequestInformation.getInstance(objectAt);
        int i2 = i + 1;
        this.messageImprint = DigestInfo.getInstance(aSN1Sequence.getObjectAt(i));
        int i3 = i2 + 1;
        this.serialNumber = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(i2));
        int i4 = i3 + 1;
        this.responseTime = DVCSTime.getInstance(aSN1Sequence.getObjectAt(i3));
        while (i4 < aSN1Sequence.size()) {
            int i5 = i4 + 1;
            ASN1Encodable objectAt2 = aSN1Sequence.getObjectAt(i4);
            if (objectAt2 instanceof ASN1TaggedObject) {
                ASN1TaggedObject instance = ASN1TaggedObject.getInstance(objectAt2);
                int tagNo = instance.getTagNo();
                if (tagNo == 0) {
                    this.dvStatus = PKIStatusInfo.getInstance(instance, false);
                } else if (tagNo == 1) {
                    this.policy = PolicyInformation.getInstance(ASN1Sequence.getInstance(instance, false));
                } else if (tagNo == 2) {
                    this.reqSignature = ASN1Set.getInstance(instance, false);
                } else if (tagNo == 3) {
                    this.certs = ASN1Sequence.getInstance(instance, false);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Unknown tag encountered: ");
                    sb.append(tagNo);
                    throw new IllegalArgumentException(sb.toString());
                }
            } else {
                try {
                    this.extensions = Extensions.getInstance(objectAt2);
                } catch (IllegalArgumentException e3) {
                }
            }
            i4 = i5;
        }
    }

    public DVCSCertInfo(DVCSRequestInformation dVCSRequestInformation, DigestInfo digestInfo, ASN1Integer aSN1Integer, DVCSTime dVCSTime) {
        this.dvReqInfo = dVCSRequestInformation;
        this.messageImprint = digestInfo;
        this.serialNumber = aSN1Integer;
        this.responseTime = dVCSTime;
    }

    public static DVCSCertInfo getInstance(Object obj) {
        if (obj instanceof DVCSCertInfo) {
            return (DVCSCertInfo) obj;
        }
        if (obj != null) {
            return new DVCSCertInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static DVCSCertInfo getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    private void setDvReqInfo(DVCSRequestInformation dVCSRequestInformation) {
        this.dvReqInfo = dVCSRequestInformation;
    }

    private void setMessageImprint(DigestInfo digestInfo) {
        this.messageImprint = digestInfo;
    }

    private void setVersion(int i) {
        this.version = i;
    }

    public TargetEtcChain[] getCerts() {
        ASN1Sequence aSN1Sequence = this.certs;
        if (aSN1Sequence != null) {
            return TargetEtcChain.arrayFromSequence(aSN1Sequence);
        }
        return null;
    }

    public DVCSRequestInformation getDvReqInfo() {
        return this.dvReqInfo;
    }

    public PKIStatusInfo getDvStatus() {
        return this.dvStatus;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    public DigestInfo getMessageImprint() {
        return this.messageImprint;
    }

    public PolicyInformation getPolicy() {
        return this.policy;
    }

    public ASN1Set getReqSignature() {
        return this.reqSignature;
    }

    public DVCSTime getResponseTime() {
        return this.responseTime;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    public int getVersion() {
        return this.version;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        int i = this.version;
        if (i != 1) {
            aSN1EncodableVector.add(new ASN1Integer((long) i));
        }
        aSN1EncodableVector.add(this.dvReqInfo);
        aSN1EncodableVector.add(this.messageImprint);
        aSN1EncodableVector.add(this.serialNumber);
        aSN1EncodableVector.add(this.responseTime);
        PKIStatusInfo pKIStatusInfo = this.dvStatus;
        if (pKIStatusInfo != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 0, pKIStatusInfo));
        }
        PolicyInformation policyInformation = this.policy;
        if (policyInformation != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 1, policyInformation));
        }
        ASN1Set aSN1Set = this.reqSignature;
        if (aSN1Set != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 2, aSN1Set));
        }
        ASN1Sequence aSN1Sequence = this.certs;
        if (aSN1Sequence != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 3, aSN1Sequence));
        }
        Extensions extensions2 = this.extensions;
        if (extensions2 != null) {
            aSN1EncodableVector.add(extensions2);
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("DVCSCertInfo {\n");
        String str = "\n";
        if (this.version != 1) {
            StringBuilder sb = new StringBuilder();
            sb.append("version: ");
            sb.append(this.version);
            sb.append(str);
            stringBuffer.append(sb.toString());
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("dvReqInfo: ");
        sb2.append(this.dvReqInfo);
        sb2.append(str);
        stringBuffer.append(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("messageImprint: ");
        sb3.append(this.messageImprint);
        sb3.append(str);
        stringBuffer.append(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("serialNumber: ");
        sb4.append(this.serialNumber);
        sb4.append(str);
        stringBuffer.append(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("responseTime: ");
        sb5.append(this.responseTime);
        sb5.append(str);
        stringBuffer.append(sb5.toString());
        if (this.dvStatus != null) {
            StringBuilder sb6 = new StringBuilder();
            sb6.append("dvStatus: ");
            sb6.append(this.dvStatus);
            sb6.append(str);
            stringBuffer.append(sb6.toString());
        }
        if (this.policy != null) {
            StringBuilder sb7 = new StringBuilder();
            sb7.append("policy: ");
            sb7.append(this.policy);
            sb7.append(str);
            stringBuffer.append(sb7.toString());
        }
        if (this.reqSignature != null) {
            StringBuilder sb8 = new StringBuilder();
            sb8.append("reqSignature: ");
            sb8.append(this.reqSignature);
            sb8.append(str);
            stringBuffer.append(sb8.toString());
        }
        if (this.certs != null) {
            StringBuilder sb9 = new StringBuilder();
            sb9.append("certs: ");
            sb9.append(this.certs);
            sb9.append(str);
            stringBuffer.append(sb9.toString());
        }
        if (this.extensions != null) {
            StringBuilder sb10 = new StringBuilder();
            sb10.append("extensions: ");
            sb10.append(this.extensions);
            sb10.append(str);
            stringBuffer.append(sb10.toString());
        }
        stringBuffer.append("}\n");
        return stringBuffer.toString();
    }
}
