package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PSSParameterSpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.util.MessageDigestUtils;

class X509SignatureUtil {
    private static final ASN1Null derNull = DERNull.INSTANCE;

    X509SignatureUtil() {
    }

    private static String getDigestAlgName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String digestName = MessageDigestUtils.getDigestName(aSN1ObjectIdentifier);
        int indexOf = digestName.indexOf(45);
        if (indexOf <= 0 || digestName.startsWith("SHA3")) {
            return MessageDigestUtils.getDigestName(aSN1ObjectIdentifier);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(digestName.substring(0, indexOf));
        sb.append(digestName.substring(indexOf + 1));
        return sb.toString();
    }

    static String getSignatureName(AlgorithmIdentifier algorithmIdentifier) {
        ASN1Encodable parameters = algorithmIdentifier.getParameters();
        if (parameters != null && !derNull.equals(parameters)) {
            if (algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
                RSASSAPSSparams instance = RSASSAPSSparams.getInstance(parameters);
                StringBuilder sb = new StringBuilder();
                sb.append(getDigestAlgName(instance.getHashAlgorithm().getAlgorithm()));
                sb.append("withRSAandMGF1");
                return sb.toString();
            } else if (algorithmIdentifier.getAlgorithm().equals(X9ObjectIdentifiers.ecdsa_with_SHA2)) {
                ASN1Sequence instance2 = ASN1Sequence.getInstance(parameters);
                StringBuilder sb2 = new StringBuilder();
                sb2.append(getDigestAlgName((ASN1ObjectIdentifier) instance2.getObjectAt(0)));
                sb2.append("withECDSA");
                return sb2.toString();
            }
        }
        Provider provider = Security.getProvider("BC");
        String str = "Alg.Alias.Signature.";
        if (provider != null) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            sb3.append(algorithmIdentifier.getAlgorithm().getId());
            String property = provider.getProperty(sb3.toString());
            if (property != null) {
                return property;
            }
        }
        Provider[] providers = Security.getProviders();
        for (int i = 0; i != providers.length; i++) {
            Provider provider2 = providers[i];
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str);
            sb4.append(algorithmIdentifier.getAlgorithm().getId());
            String property2 = provider2.getProperty(sb4.toString());
            if (property2 != null) {
                return property2;
            }
        }
        return algorithmIdentifier.getAlgorithm().getId();
    }

    static void setSignatureParameters(Signature signature, ASN1Encodable aSN1Encodable) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (aSN1Encodable != null && !derNull.equals(aSN1Encodable)) {
            AlgorithmParameters instance = AlgorithmParameters.getInstance(signature.getAlgorithm(), signature.getProvider());
            try {
                instance.init(aSN1Encodable.toASN1Primitive().getEncoded());
                if (signature.getAlgorithm().endsWith("MGF1")) {
                    try {
                        signature.setParameter(instance.getParameterSpec(PSSParameterSpec.class));
                    } catch (GeneralSecurityException e) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Exception extracting parameters: ");
                        sb.append(e.getMessage());
                        throw new SignatureException(sb.toString());
                    }
                }
            } catch (IOException e2) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("IOException decoding parameters: ");
                sb2.append(e2.getMessage());
                throw new SignatureException(sb2.toString());
            }
        }
    }
}
