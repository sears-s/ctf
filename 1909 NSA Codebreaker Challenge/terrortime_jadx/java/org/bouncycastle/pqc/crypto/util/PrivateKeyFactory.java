package org.bouncycastle.pqc.crypto.util;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSPrivateKey;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.BDS;
import org.bouncycastle.pqc.crypto.xmss.BDSStateMap;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters.Builder;
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;
import org.bouncycastle.util.Pack;

public class PrivateKeyFactory {
    private static short[] convert(byte[] bArr) {
        short[] sArr = new short[(bArr.length / 2)];
        for (int i = 0; i != sArr.length; i++) {
            sArr[i] = Pack.littleEndianToShort(bArr, i * 2);
        }
        return sArr;
    }

    public static AsymmetricKeyParameter createKey(InputStream inputStream) throws IOException {
        return createKey(PrivateKeyInfo.getInstance(new ASN1InputStream(inputStream).readObject()));
    }

    public static AsymmetricKeyParameter createKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        ASN1ObjectIdentifier algorithm = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        if (algorithm.on(BCObjectIdentifiers.qTESLA)) {
            return new QTESLAPrivateKeyParameters(Utils.qTeslaLookupSecurityCategory(privateKeyInfo.getPrivateKeyAlgorithm()), ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets());
        } else if (algorithm.equals(BCObjectIdentifiers.sphincs256)) {
            return new SPHINCSPrivateKeyParameters(ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets(), Utils.sphincs256LookupTreeAlgName(SPHINCS256KeyParams.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters())));
        } else {
            if (algorithm.equals(BCObjectIdentifiers.newHope)) {
                return new NHPrivateKeyParameters(convert(ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets()));
            }
            String str = "ClassNotFoundException processing BDS state: ";
            if (algorithm.equals(BCObjectIdentifiers.xmss)) {
                XMSSKeyParams instance = XMSSKeyParams.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
                ASN1ObjectIdentifier algorithm2 = instance.getTreeDigest().getAlgorithm();
                XMSSPrivateKey instance2 = XMSSPrivateKey.getInstance(privateKeyInfo.parsePrivateKey());
                try {
                    Builder withRoot = new Builder(new XMSSParameters(instance.getHeight(), Utils.getDigest(algorithm2))).withIndex(instance2.getIndex()).withSecretKeySeed(instance2.getSecretKeySeed()).withSecretKeyPRF(instance2.getSecretKeyPRF()).withPublicSeed(instance2.getPublicSeed()).withRoot(instance2.getRoot());
                    if (instance2.getBdsState() != null) {
                        withRoot.withBDSState(((BDS) XMSSUtil.deserialize(instance2.getBdsState(), BDS.class)).withWOTSDigest(algorithm2));
                    }
                    return withRoot.build();
                } catch (ClassNotFoundException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    sb.append(e.getMessage());
                    throw new IOException(sb.toString());
                }
            } else if (algorithm.equals(PQCObjectIdentifiers.xmss_mt)) {
                XMSSMTKeyParams instance3 = XMSSMTKeyParams.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
                ASN1ObjectIdentifier algorithm3 = instance3.getTreeDigest().getAlgorithm();
                try {
                    XMSSPrivateKey instance4 = XMSSPrivateKey.getInstance(privateKeyInfo.parsePrivateKey());
                    XMSSMTPrivateKeyParameters.Builder withRoot2 = new XMSSMTPrivateKeyParameters.Builder(new XMSSMTParameters(instance3.getHeight(), instance3.getLayers(), Utils.getDigest(algorithm3))).withIndex((long) instance4.getIndex()).withSecretKeySeed(instance4.getSecretKeySeed()).withSecretKeyPRF(instance4.getSecretKeyPRF()).withPublicSeed(instance4.getPublicSeed()).withRoot(instance4.getRoot());
                    if (instance4.getBdsState() != null) {
                        withRoot2.withBDSState(((BDSStateMap) XMSSUtil.deserialize(instance4.getBdsState(), BDSStateMap.class)).withWOTSDigest(algorithm3));
                    }
                    return withRoot2.build();
                } catch (ClassNotFoundException e2) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str);
                    sb2.append(e2.getMessage());
                    throw new IOException(sb2.toString());
                }
            } else {
                throw new RuntimeException("algorithm identifier in private key not recognised");
            }
        }
    }

    public static AsymmetricKeyParameter createKey(byte[] bArr) throws IOException {
        return createKey(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(bArr)));
    }
}
