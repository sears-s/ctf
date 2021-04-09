package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.util.OpenSSHPrivateKeyUtil;
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;
import org.bouncycastle.jcajce.spec.XDHParameterSpec;
import org.bouncycastle.jce.spec.OpenSSHPrivateKeySpec;
import org.bouncycastle.jce.spec.OpenSSHPublicKeySpec;
import org.bouncycastle.util.encoders.Hex;

public class KeyFactorySpi extends BaseKeyFactorySpi implements AsymmetricKeyInfoConverter {
    static final byte[] Ed25519Prefix = Hex.decode("302a300506032b6570032100");
    private static final byte Ed25519_type = 112;
    static final byte[] Ed448Prefix = Hex.decode("3043300506032b6571033a00");
    private static final byte Ed448_type = 113;
    static final byte[] x25519Prefix = Hex.decode("302a300506032b656e032100");
    private static final byte x25519_type = 110;
    static final byte[] x448Prefix = Hex.decode("3042300506032b656f033900");
    private static final byte x448_type = 111;
    String algorithm;
    private final boolean isXdh;
    private final int specificBase;

    public static class ED25519 extends KeyFactorySpi {
        public ED25519() {
            super(EdDSAParameterSpec.Ed25519, false, 112);
        }
    }

    public static class ED448 extends KeyFactorySpi {
        public ED448() {
            super(EdDSAParameterSpec.Ed448, false, 113);
        }
    }

    public static class EDDSA extends KeyFactorySpi {
        public EDDSA() {
            super("EdDSA", false, 0);
        }
    }

    public static class X25519 extends KeyFactorySpi {
        public X25519() {
            super(XDHParameterSpec.X25519, true, 110);
        }
    }

    public static class X448 extends KeyFactorySpi {
        public X448() {
            super(XDHParameterSpec.X448, true, 111);
        }
    }

    public static class XDH extends KeyFactorySpi {
        public XDH() {
            super("XDH", true, 0);
        }
    }

    public KeyFactorySpi(String str, boolean z, int i) {
        this.algorithm = str;
        this.isXdh = z;
        this.specificBase = i;
    }

    /* access modifiers changed from: protected */
    public PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException {
        if (!(keySpec instanceof OpenSSHPrivateKeySpec)) {
            return super.engineGeneratePrivate(keySpec);
        }
        AsymmetricKeyParameter parsePrivateKeyBlob = OpenSSHPrivateKeyUtil.parsePrivateKeyBlob(((OpenSSHPrivateKeySpec) keySpec).getEncoded());
        if (parsePrivateKeyBlob instanceof Ed25519PrivateKeyParameters) {
            return new BCEdDSAPrivateKey((AsymmetricKeyParameter) (Ed25519PrivateKeyParameters) parsePrivateKeyBlob);
        }
        throw new IllegalStateException("openssh private key not Ed25519 private key");
    }

    /* access modifiers changed from: protected */
    public PublicKey engineGeneratePublic(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof X509EncodedKeySpec) {
            byte[] encoded = ((X509EncodedKeySpec) keySpec).getEncoded();
            int i = this.specificBase;
            if (i == 0 || i == encoded[8]) {
                switch (encoded[8]) {
                    case 110:
                        return new BCXDHPublicKey(x25519Prefix, encoded);
                    case 111:
                        return new BCXDHPublicKey(x448Prefix, encoded);
                    case 112:
                        return new BCEdDSAPublicKey(Ed25519Prefix, encoded);
                    case 113:
                        return new BCEdDSAPublicKey(Ed448Prefix, encoded);
                    default:
                        return super.engineGeneratePublic(keySpec);
                }
            }
        } else if (keySpec instanceof OpenSSHPublicKeySpec) {
            AsymmetricKeyParameter parsePublicKey = OpenSSHPublicKeyUtil.parsePublicKey(((OpenSSHPublicKeySpec) keySpec).getEncoded());
            if (parsePublicKey instanceof Ed25519PublicKeyParameters) {
                return new BCEdDSAPublicKey(new byte[0], ((Ed25519PublicKeyParameters) parsePublicKey).getEncoded());
            }
            throw new IllegalStateException("openssh public key not Ed25519 public key");
        }
        return super.engineGeneratePublic(keySpec);
    }

    /* access modifiers changed from: protected */
    public KeySpec engineGetKeySpec(Key key, Class cls) throws InvalidKeySpecException {
        if (cls.isAssignableFrom(OpenSSHPrivateKeySpec.class) && (key instanceof BCEdDSAPrivateKey)) {
            try {
                return new OpenSSHPrivateKeySpec(OpenSSHPrivateKeyUtil.encodePrivateKey(new Ed25519PrivateKeyParameters(((DEROctetString) new ASN1InputStream(((DEROctetString) ASN1Sequence.getInstance(key.getEncoded()).getObjectAt(2)).getOctets()).readObject()).getOctets(), 0)));
            } catch (IOException e) {
                throw new InvalidKeySpecException(e.getMessage(), e.getCause());
            }
        } else if (!cls.isAssignableFrom(OpenSSHPublicKeySpec.class) || !(key instanceof BCEdDSAPublicKey)) {
            return super.engineGetKeySpec(key, cls);
        } else {
            try {
                return new OpenSSHPublicKeySpec(OpenSSHPublicKeyUtil.encodePublicKey(new Ed25519PublicKeyParameters(key.getEncoded(), Ed25519Prefix.length)));
            } catch (IOException e2) {
                throw new InvalidKeySpecException(e2.getMessage(), e2.getCause());
            }
        }
    }

    /* access modifiers changed from: protected */
    public Key engineTranslateKey(Key key) throws InvalidKeyException {
        throw new InvalidKeyException("key type unknown");
    }

    public PrivateKey generatePrivate(PrivateKeyInfo privateKeyInfo) throws IOException {
        ASN1ObjectIdentifier algorithm2 = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        if (this.isXdh) {
            int i = this.specificBase;
            if ((i == 0 || i == 111) && algorithm2.equals(EdECObjectIdentifiers.id_X448)) {
                return new BCXDHPrivateKey(privateKeyInfo);
            }
            int i2 = this.specificBase;
            if ((i2 == 0 || i2 == 110) && algorithm2.equals(EdECObjectIdentifiers.id_X25519)) {
                return new BCXDHPrivateKey(privateKeyInfo);
            }
        } else if (algorithm2.equals(EdECObjectIdentifiers.id_Ed448) || algorithm2.equals(EdECObjectIdentifiers.id_Ed25519)) {
            int i3 = this.specificBase;
            if ((i3 == 0 || i3 == 113) && algorithm2.equals(EdECObjectIdentifiers.id_Ed448)) {
                return new BCEdDSAPrivateKey(privateKeyInfo);
            }
            int i4 = this.specificBase;
            if ((i4 == 0 || i4 == 112) && algorithm2.equals(EdECObjectIdentifiers.id_Ed25519)) {
                return new BCEdDSAPrivateKey(privateKeyInfo);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("algorithm identifier ");
        sb.append(algorithm2);
        sb.append(" in key not recognized");
        throw new IOException(sb.toString());
    }

    public PublicKey generatePublic(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        ASN1ObjectIdentifier algorithm2 = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
        if (this.isXdh) {
            int i = this.specificBase;
            if ((i == 0 || i == 111) && algorithm2.equals(EdECObjectIdentifiers.id_X448)) {
                return new BCXDHPublicKey(subjectPublicKeyInfo);
            }
            int i2 = this.specificBase;
            if ((i2 == 0 || i2 == 110) && algorithm2.equals(EdECObjectIdentifiers.id_X25519)) {
                return new BCXDHPublicKey(subjectPublicKeyInfo);
            }
        } else if (algorithm2.equals(EdECObjectIdentifiers.id_Ed448) || algorithm2.equals(EdECObjectIdentifiers.id_Ed25519)) {
            int i3 = this.specificBase;
            if ((i3 == 0 || i3 == 113) && algorithm2.equals(EdECObjectIdentifiers.id_Ed448)) {
                return new BCEdDSAPublicKey(subjectPublicKeyInfo);
            }
            int i4 = this.specificBase;
            if ((i4 == 0 || i4 == 112) && algorithm2.equals(EdECObjectIdentifiers.id_Ed25519)) {
                return new BCEdDSAPublicKey(subjectPublicKeyInfo);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("algorithm identifier ");
        sb.append(algorithm2);
        sb.append(" in key not recognized");
        throw new IOException(sb.toString());
    }
}
