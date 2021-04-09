package org.bouncycastle.asn1.dvcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class DVCSRequestInformation extends ASN1Object {
    private static final int DEFAULT_VERSION = 1;
    private static final int TAG_DATA_LOCATIONS = 3;
    private static final int TAG_DVCS = 2;
    private static final int TAG_EXTENSIONS = 4;
    private static final int TAG_REQUESTER = 0;
    private static final int TAG_REQUEST_POLICY = 1;
    private GeneralNames dataLocations;
    private GeneralNames dvcs;
    private Extensions extensions;
    private BigInteger nonce;
    private PolicyInformation requestPolicy;
    private DVCSTime requestTime;
    private GeneralNames requester;
    private ServiceType service;
    private int version = 1;

    private DVCSRequestInformation(ASN1Sequence aSN1Sequence) {
        int i;
        if (aSN1Sequence.getObjectAt(0) instanceof ASN1Integer) {
            this.version = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).getValue().intValue();
            i = 1;
        } else {
            this.version = 1;
            i = 0;
        }
        this.service = ServiceType.getInstance(aSN1Sequence.getObjectAt(i));
        for (int i2 = i + 1; i2 < aSN1Sequence.size(); i2++) {
            ASN1Encodable objectAt = aSN1Sequence.getObjectAt(i2);
            if (objectAt instanceof ASN1Integer) {
                this.nonce = ASN1Integer.getInstance(objectAt).getValue();
            } else if (!(objectAt instanceof ASN1GeneralizedTime) && (objectAt instanceof ASN1TaggedObject)) {
                ASN1TaggedObject instance = ASN1TaggedObject.getInstance(objectAt);
                int tagNo = instance.getTagNo();
                if (tagNo == 0) {
                    this.requester = GeneralNames.getInstance(instance, false);
                } else if (tagNo == 1) {
                    this.requestPolicy = PolicyInformation.getInstance(ASN1Sequence.getInstance(instance, false));
                } else if (tagNo == 2) {
                    this.dvcs = GeneralNames.getInstance(instance, false);
                } else if (tagNo == 3) {
                    this.dataLocations = GeneralNames.getInstance(instance, false);
                } else if (tagNo == 4) {
                    this.extensions = Extensions.getInstance(instance, false);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("unknown tag number encountered: ");
                    sb.append(tagNo);
                    throw new IllegalArgumentException(sb.toString());
                }
            } else {
                this.requestTime = DVCSTime.getInstance(objectAt);
            }
        }
    }

    public static DVCSRequestInformation getInstance(Object obj) {
        if (obj instanceof DVCSRequestInformation) {
            return (DVCSRequestInformation) obj;
        }
        if (obj != null) {
            return new DVCSRequestInformation(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static DVCSRequestInformation getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public GeneralNames getDVCS() {
        return this.dvcs;
    }

    public GeneralNames getDataLocations() {
        return this.dataLocations;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    public BigInteger getNonce() {
        return this.nonce;
    }

    public PolicyInformation getRequestPolicy() {
        return this.requestPolicy;
    }

    public DVCSTime getRequestTime() {
        return this.requestTime;
    }

    public GeneralNames getRequester() {
        return this.requester;
    }

    public ServiceType getService() {
        return this.service;
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
        aSN1EncodableVector.add(this.service);
        BigInteger bigInteger = this.nonce;
        if (bigInteger != null) {
            aSN1EncodableVector.add(new ASN1Integer(bigInteger));
        }
        DVCSTime dVCSTime = this.requestTime;
        if (dVCSTime != null) {
            aSN1EncodableVector.add(dVCSTime);
        }
        int[] iArr = {0, 1, 2, 3, 4};
        ASN1Encodable[] aSN1EncodableArr = {this.requester, this.requestPolicy, this.dvcs, this.dataLocations, this.extensions};
        for (int i2 = 0; i2 < iArr.length; i2++) {
            int i3 = iArr[i2];
            ASN1Encodable aSN1Encodable = aSN1EncodableArr[i2];
            if (aSN1Encodable != null) {
                aSN1EncodableVector.add(new DERTaggedObject(false, i3, aSN1Encodable));
            }
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("DVCSRequestInformation {\n");
        String str = "\n";
        if (this.version != 1) {
            StringBuilder sb = new StringBuilder();
            sb.append("version: ");
            sb.append(this.version);
            sb.append(str);
            stringBuffer.append(sb.toString());
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("service: ");
        sb2.append(this.service);
        sb2.append(str);
        stringBuffer.append(sb2.toString());
        if (this.nonce != null) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("nonce: ");
            sb3.append(this.nonce);
            sb3.append(str);
            stringBuffer.append(sb3.toString());
        }
        if (this.requestTime != null) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append("requestTime: ");
            sb4.append(this.requestTime);
            sb4.append(str);
            stringBuffer.append(sb4.toString());
        }
        if (this.requester != null) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append("requester: ");
            sb5.append(this.requester);
            sb5.append(str);
            stringBuffer.append(sb5.toString());
        }
        if (this.requestPolicy != null) {
            StringBuilder sb6 = new StringBuilder();
            sb6.append("requestPolicy: ");
            sb6.append(this.requestPolicy);
            sb6.append(str);
            stringBuffer.append(sb6.toString());
        }
        if (this.dvcs != null) {
            StringBuilder sb7 = new StringBuilder();
            sb7.append("dvcs: ");
            sb7.append(this.dvcs);
            sb7.append(str);
            stringBuffer.append(sb7.toString());
        }
        if (this.dataLocations != null) {
            StringBuilder sb8 = new StringBuilder();
            sb8.append("dataLocations: ");
            sb8.append(this.dataLocations);
            sb8.append(str);
            stringBuffer.append(sb8.toString());
        }
        if (this.extensions != null) {
            StringBuilder sb9 = new StringBuilder();
            sb9.append("extensions: ");
            sb9.append(this.extensions);
            sb9.append(str);
            stringBuffer.append(sb9.toString());
        }
        stringBuffer.append("}\n");
        return stringBuffer.toString();
    }
}
