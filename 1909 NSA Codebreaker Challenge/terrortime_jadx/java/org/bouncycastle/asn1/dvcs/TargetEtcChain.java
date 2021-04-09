package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class TargetEtcChain extends ASN1Object {
    private ASN1Sequence chain;
    private PathProcInput pathProcInput;
    private CertEtcToken target;

    private TargetEtcChain(ASN1Sequence aSN1Sequence) {
        this.target = CertEtcToken.getInstance(aSN1Sequence.getObjectAt(0));
        if (aSN1Sequence.size() > 1) {
            ASN1Encodable objectAt = aSN1Sequence.getObjectAt(1);
            if (objectAt instanceof ASN1TaggedObject) {
                extractPathProcInput(objectAt);
                return;
            }
            this.chain = ASN1Sequence.getInstance(objectAt);
            if (aSN1Sequence.size() > 2) {
                extractPathProcInput(aSN1Sequence.getObjectAt(2));
            }
        }
    }

    public TargetEtcChain(CertEtcToken certEtcToken) {
        this(certEtcToken, null, null);
    }

    public TargetEtcChain(CertEtcToken certEtcToken, PathProcInput pathProcInput2) {
        this(certEtcToken, null, pathProcInput2);
    }

    public TargetEtcChain(CertEtcToken certEtcToken, CertEtcToken[] certEtcTokenArr) {
        this(certEtcToken, certEtcTokenArr, null);
    }

    public TargetEtcChain(CertEtcToken certEtcToken, CertEtcToken[] certEtcTokenArr, PathProcInput pathProcInput2) {
        this.target = certEtcToken;
        if (certEtcTokenArr != null) {
            this.chain = new DERSequence((ASN1Encodable[]) certEtcTokenArr);
        }
        this.pathProcInput = pathProcInput2;
    }

    public static TargetEtcChain[] arrayFromSequence(ASN1Sequence aSN1Sequence) {
        TargetEtcChain[] targetEtcChainArr = new TargetEtcChain[aSN1Sequence.size()];
        for (int i = 0; i != targetEtcChainArr.length; i++) {
            targetEtcChainArr[i] = getInstance(aSN1Sequence.getObjectAt(i));
        }
        return targetEtcChainArr;
    }

    private void extractPathProcInput(ASN1Encodable aSN1Encodable) {
        ASN1TaggedObject instance = ASN1TaggedObject.getInstance(aSN1Encodable);
        if (instance.getTagNo() == 0) {
            this.pathProcInput = PathProcInput.getInstance(instance, false);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown tag encountered: ");
        sb.append(instance.getTagNo());
        throw new IllegalArgumentException(sb.toString());
    }

    public static TargetEtcChain getInstance(Object obj) {
        if (obj instanceof TargetEtcChain) {
            return (TargetEtcChain) obj;
        }
        if (obj != null) {
            return new TargetEtcChain(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static TargetEtcChain getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public CertEtcToken[] getChain() {
        ASN1Sequence aSN1Sequence = this.chain;
        if (aSN1Sequence != null) {
            return CertEtcToken.arrayFromSequence(aSN1Sequence);
        }
        return null;
    }

    public PathProcInput getPathProcInput() {
        return this.pathProcInput;
    }

    public CertEtcToken getTarget() {
        return this.target;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(this.target);
        ASN1Sequence aSN1Sequence = this.chain;
        if (aSN1Sequence != null) {
            aSN1EncodableVector.add(aSN1Sequence);
        }
        PathProcInput pathProcInput2 = this.pathProcInput;
        if (pathProcInput2 != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 0, pathProcInput2));
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("TargetEtcChain {\n");
        StringBuilder sb = new StringBuilder();
        sb.append("target: ");
        sb.append(this.target);
        String str = "\n";
        sb.append(str);
        stringBuffer.append(sb.toString());
        if (this.chain != null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("chain: ");
            sb2.append(this.chain);
            sb2.append(str);
            stringBuffer.append(sb2.toString());
        }
        if (this.pathProcInput != null) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("pathProcInput: ");
            sb3.append(this.pathProcInput);
            sb3.append(str);
            stringBuffer.append(sb3.toString());
        }
        stringBuffer.append("}\n");
        return stringBuffer.toString();
    }
}
