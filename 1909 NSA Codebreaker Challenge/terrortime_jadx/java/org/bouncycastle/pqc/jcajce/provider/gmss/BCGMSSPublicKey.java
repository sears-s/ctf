package org.bouncycastle.pqc.jcajce.provider.gmss;

import java.security.PublicKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.asn1.GMSSPublicKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.ParSet;
import org.bouncycastle.pqc.crypto.gmss.GMSSParameters;
import org.bouncycastle.pqc.crypto.gmss.GMSSPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.bouncycastle.util.encoders.Hex;

public class BCGMSSPublicKey implements CipherParameters, PublicKey {
    private static final long serialVersionUID = 1;
    private GMSSParameters gmssParameterSet;
    private GMSSParameters gmssParams;
    private byte[] publicKeyBytes;

    public BCGMSSPublicKey(GMSSPublicKeyParameters gMSSPublicKeyParameters) {
        this(gMSSPublicKeyParameters.getPublicKey(), gMSSPublicKeyParameters.getParameters());
    }

    public BCGMSSPublicKey(byte[] bArr, GMSSParameters gMSSParameters) {
        this.gmssParameterSet = gMSSParameters;
        this.publicKeyBytes = bArr;
    }

    public String getAlgorithm() {
        return "GMSS";
    }

    public byte[] getEncoded() {
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.gmss, new ParSet(this.gmssParameterSet.getNumOfLayers(), this.gmssParameterSet.getHeightOfTrees(), this.gmssParameterSet.getWinternitzParameter(), this.gmssParameterSet.getK()).toASN1Primitive()), (ASN1Encodable) new GMSSPublicKey(this.publicKeyBytes));
    }

    public String getFormat() {
        return "X.509";
    }

    public GMSSParameters getParameterSet() {
        return this.gmssParameterSet;
    }

    public byte[] getPublicKeyBytes() {
        return this.publicKeyBytes;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GMSS public key : ");
        sb.append(new String(Hex.encode(this.publicKeyBytes)));
        sb.append("\nHeight of Trees: \n");
        String sb2 = sb.toString();
        for (int i = 0; i < this.gmssParameterSet.getHeightOfTrees().length; i++) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(sb2);
            sb3.append("Layer ");
            sb3.append(i);
            sb3.append(" : ");
            sb3.append(this.gmssParameterSet.getHeightOfTrees()[i]);
            sb3.append(" WinternitzParameter: ");
            sb3.append(this.gmssParameterSet.getWinternitzParameter()[i]);
            sb3.append(" K: ");
            sb3.append(this.gmssParameterSet.getK()[i]);
            sb3.append("\n");
            sb2 = sb3.toString();
        }
        return sb2;
    }
}
