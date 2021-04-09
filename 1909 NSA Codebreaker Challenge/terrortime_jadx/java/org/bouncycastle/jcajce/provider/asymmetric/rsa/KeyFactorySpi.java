package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.util.OpenSSHPrivateKeyUtil;
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.ExtendedInvalidKeySpecException;
import org.bouncycastle.jce.spec.OpenSSHPrivateKeySpec;
import org.bouncycastle.jce.spec.OpenSSHPublicKeySpec;

public class KeyFactorySpi extends BaseKeyFactorySpi {
    /* access modifiers changed from: protected */
    public PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            try {
                return generatePrivate(PrivateKeyInfo.getInstance(((PKCS8EncodedKeySpec) keySpec).getEncoded()));
            } catch (Exception e) {
                try {
                    return new BCRSAPrivateCrtKey(RSAPrivateKey.getInstance(((PKCS8EncodedKeySpec) keySpec).getEncoded()));
                } catch (Exception e2) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("unable to process key spec: ");
                    sb.append(e.toString());
                    throw new ExtendedInvalidKeySpecException(sb.toString(), e);
                }
            }
        } else if (keySpec instanceof RSAPrivateCrtKeySpec) {
            return new BCRSAPrivateCrtKey((RSAPrivateCrtKeySpec) keySpec);
        } else {
            if (keySpec instanceof RSAPrivateKeySpec) {
                return new BCRSAPrivateKey((RSAPrivateKeySpec) keySpec);
            }
            if (keySpec instanceof OpenSSHPrivateKeySpec) {
                AsymmetricKeyParameter parsePrivateKeyBlob = OpenSSHPrivateKeyUtil.parsePrivateKeyBlob(((OpenSSHPrivateKeySpec) keySpec).getEncoded());
                if (parsePrivateKeyBlob instanceof RSAPrivateCrtKeyParameters) {
                    return new BCRSAPrivateCrtKey((RSAPrivateCrtKeyParameters) parsePrivateKeyBlob);
                }
                throw new InvalidKeySpecException("open SSH public key is not RSA private key");
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("unknown KeySpec type: ");
            sb2.append(keySpec.getClass().getName());
            throw new InvalidKeySpecException(sb2.toString());
        }
    }

    /* access modifiers changed from: protected */
    public PublicKey engineGeneratePublic(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof RSAPublicKeySpec) {
            return new BCRSAPublicKey((RSAPublicKeySpec) keySpec);
        }
        if (!(keySpec instanceof OpenSSHPublicKeySpec)) {
            return super.engineGeneratePublic(keySpec);
        }
        AsymmetricKeyParameter parsePublicKey = OpenSSHPublicKeyUtil.parsePublicKey(((OpenSSHPublicKeySpec) keySpec).getEncoded());
        if (parsePublicKey instanceof RSAKeyParameters) {
            return new BCRSAPublicKey((RSAKeyParameters) parsePublicKey);
        }
        throw new InvalidKeySpecException("Open SSH public key is not RSA public key");
    }

    /* access modifiers changed from: protected */
    public KeySpec engineGetKeySpec(Key key, Class cls) throws InvalidKeySpecException {
        if (cls.isAssignableFrom(RSAPublicKeySpec.class) && (key instanceof RSAPublicKey)) {
            RSAPublicKey rSAPublicKey = (RSAPublicKey) key;
            return new RSAPublicKeySpec(rSAPublicKey.getModulus(), rSAPublicKey.getPublicExponent());
        } else if (cls.isAssignableFrom(RSAPrivateKeySpec.class) && (key instanceof java.security.interfaces.RSAPrivateKey)) {
            java.security.interfaces.RSAPrivateKey rSAPrivateKey = (java.security.interfaces.RSAPrivateKey) key;
            return new RSAPrivateKeySpec(rSAPrivateKey.getModulus(), rSAPrivateKey.getPrivateExponent());
        } else if (!cls.isAssignableFrom(RSAPrivateCrtKeySpec.class) || !(key instanceof RSAPrivateCrtKey)) {
            String str = "unable to produce encoding: ";
            if (cls.isAssignableFrom(OpenSSHPublicKeySpec.class) && (key instanceof RSAPublicKey)) {
                try {
                    return new OpenSSHPublicKeySpec(OpenSSHPublicKeyUtil.encodePublicKey(new RSAKeyParameters(false, ((RSAPublicKey) key).getModulus(), ((RSAPublicKey) key).getPublicExponent())));
                } catch (IOException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    sb.append(e.getMessage());
                    throw new IllegalArgumentException(sb.toString());
                }
            } else if (!cls.isAssignableFrom(OpenSSHPrivateKeySpec.class) || !(key instanceof RSAPrivateCrtKey)) {
                return super.engineGetKeySpec(key, cls);
            } else {
                try {
                    RSAPrivateCrtKeyParameters rSAPrivateCrtKeyParameters = new RSAPrivateCrtKeyParameters(((RSAPrivateCrtKey) key).getModulus(), ((RSAPrivateCrtKey) key).getPublicExponent(), ((RSAPrivateCrtKey) key).getPrivateExponent(), ((RSAPrivateCrtKey) key).getPrimeP(), ((RSAPrivateCrtKey) key).getPrimeQ(), ((RSAPrivateCrtKey) key).getPrimeExponentP(), ((RSAPrivateCrtKey) key).getPrimeExponentQ(), ((RSAPrivateCrtKey) key).getCrtCoefficient());
                    return new OpenSSHPrivateKeySpec(OpenSSHPrivateKeyUtil.encodePrivateKey(rSAPrivateCrtKeyParameters));
                } catch (IOException e2) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str);
                    sb2.append(e2.getMessage());
                    throw new IllegalArgumentException(sb2.toString());
                }
            }
        } else {
            RSAPrivateCrtKey rSAPrivateCrtKey = (RSAPrivateCrtKey) key;
            RSAPrivateCrtKeySpec rSAPrivateCrtKeySpec = new RSAPrivateCrtKeySpec(rSAPrivateCrtKey.getModulus(), rSAPrivateCrtKey.getPublicExponent(), rSAPrivateCrtKey.getPrivateExponent(), rSAPrivateCrtKey.getPrimeP(), rSAPrivateCrtKey.getPrimeQ(), rSAPrivateCrtKey.getPrimeExponentP(), rSAPrivateCrtKey.getPrimeExponentQ(), rSAPrivateCrtKey.getCrtCoefficient());
            return rSAPrivateCrtKeySpec;
        }
    }

    /* access modifiers changed from: protected */
    public Key engineTranslateKey(Key key) throws InvalidKeyException {
        if (key instanceof RSAPublicKey) {
            return new BCRSAPublicKey((RSAPublicKey) key);
        }
        if (key instanceof RSAPrivateCrtKey) {
            return new BCRSAPrivateCrtKey((RSAPrivateCrtKey) key);
        }
        if (key instanceof java.security.interfaces.RSAPrivateKey) {
            return new BCRSAPrivateKey((java.security.interfaces.RSAPrivateKey) key);
        }
        throw new InvalidKeyException("key type unknown");
    }

    public PrivateKey generatePrivate(PrivateKeyInfo privateKeyInfo) throws IOException {
        ASN1ObjectIdentifier algorithm = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        if (RSAUtil.isRsaOid(algorithm)) {
            RSAPrivateKey instance = RSAPrivateKey.getInstance(privateKeyInfo.parsePrivateKey());
            return instance.getCoefficient().intValue() == 0 ? new BCRSAPrivateKey(instance) : new BCRSAPrivateCrtKey(privateKeyInfo);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("algorithm identifier ");
        sb.append(algorithm);
        sb.append(" in key not recognised");
        throw new IOException(sb.toString());
    }

    public PublicKey generatePublic(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        ASN1ObjectIdentifier algorithm = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
        if (RSAUtil.isRsaOid(algorithm)) {
            return new BCRSAPublicKey(subjectPublicKeyInfo);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("algorithm identifier ");
        sb.append(algorithm);
        sb.append(" in key not recognised");
        throw new IOException(sb.toString());
    }
}
