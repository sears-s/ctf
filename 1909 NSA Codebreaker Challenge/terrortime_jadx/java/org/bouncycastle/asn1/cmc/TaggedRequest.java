package org.bouncycastle.asn1.cmc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.CertReqMsg;

public class TaggedRequest extends ASN1Object implements ASN1Choice {
    public static final int CRM = 1;
    public static final int ORM = 2;
    public static final int TCR = 0;
    private final int tagNo = 2;
    private final ASN1Encodable value;

    private TaggedRequest(ASN1Sequence aSN1Sequence) {
        this.value = aSN1Sequence;
    }

    public TaggedRequest(TaggedCertificationRequest taggedCertificationRequest) {
        this.value = taggedCertificationRequest;
    }

    public TaggedRequest(CertReqMsg certReqMsg) {
        this.value = certReqMsg;
    }

    public static TaggedRequest getInstance(Object obj) {
        if (obj instanceof TaggedRequest) {
            return (TaggedRequest) obj;
        }
        if (obj == null) {
            return null;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1TaggedObject instance = ASN1TaggedObject.getInstance(((ASN1Encodable) obj).toASN1Primitive());
            int tagNo2 = instance.getTagNo();
            if (tagNo2 == 0) {
                return new TaggedRequest(TaggedCertificationRequest.getInstance(instance, false));
            }
            if (tagNo2 == 1) {
                return new TaggedRequest(CertReqMsg.getInstance(instance, false));
            }
            if (tagNo2 == 2) {
                return new TaggedRequest(ASN1Sequence.getInstance(instance, false));
            }
            StringBuilder sb = new StringBuilder();
            sb.append("unknown tag in getInstance(): ");
            sb.append(instance.getTagNo());
            throw new IllegalArgumentException(sb.toString());
        } else if (obj instanceof byte[]) {
            try {
                return getInstance(ASN1Primitive.fromByteArray((byte[]) obj));
            } catch (IOException e) {
                throw new IllegalArgumentException("unknown encoding in getInstance()");
            }
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("unknown object in getInstance(): ");
            sb2.append(obj.getClass().getName());
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    public int getTagNo() {
        return this.tagNo;
    }

    public ASN1Encodable getValue() {
        return this.value;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(false, this.tagNo, this.value);
    }
}
