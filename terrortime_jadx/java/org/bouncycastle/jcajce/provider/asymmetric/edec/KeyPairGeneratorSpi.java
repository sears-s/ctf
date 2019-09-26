package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.Ed448KeyPairGenerator;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.X448KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed448KeyGenerationParameters;
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.X448KeyGenerationParameters;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;
import org.bouncycastle.jcajce.spec.XDHParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;

public class KeyPairGeneratorSpi extends java.security.KeyPairGeneratorSpi {
    private static final int Ed25519 = 1;
    private static final int Ed448 = 0;
    private static final int EdDSA = -1;
    private static final int X25519 = 3;
    private static final int X448 = 2;
    private static final int XDH = -2;
    private int algorithm;
    private AsymmetricCipherKeyPairGenerator generator;
    private boolean initialised;
    private SecureRandom secureRandom;

    public static final class Ed25519 extends KeyPairGeneratorSpi {
        public Ed25519() {
            super(1, new Ed25519KeyPairGenerator());
        }
    }

    public static final class Ed448 extends KeyPairGeneratorSpi {
        public Ed448() {
            super(0, new Ed448KeyPairGenerator());
        }
    }

    public static final class EdDSA extends KeyPairGeneratorSpi {
        public EdDSA() {
            super(-1, null);
        }
    }

    public static final class X25519 extends KeyPairGeneratorSpi {
        public X25519() {
            super(3, new X25519KeyPairGenerator());
        }
    }

    public static final class X448 extends KeyPairGeneratorSpi {
        public X448() {
            super(2, new X448KeyPairGenerator());
        }
    }

    public static final class XDH extends KeyPairGeneratorSpi {
        public XDH() {
            super(-2, null);
        }
    }

    KeyPairGeneratorSpi(int i, AsymmetricCipherKeyPairGenerator asymmetricCipherKeyPairGenerator) {
        this.algorithm = i;
        this.generator = asymmetricCipherKeyPairGenerator;
    }

    private void algorithmCheck(int i) throws InvalidAlgorithmParameterException {
        int i2 = this.algorithm;
        if (i2 != i) {
            String str = "parameterSpec for wrong curve type";
            if (i2 == 1 || i2 == 0) {
                throw new InvalidAlgorithmParameterException(str);
            } else if (i2 != -1 || i == 1 || i == 0) {
                int i3 = this.algorithm;
                if (i3 == 3 || i3 == 2) {
                    throw new InvalidAlgorithmParameterException(str);
                } else if (i3 != -2 || i == 3 || i == 2) {
                    this.algorithm = i;
                } else {
                    throw new InvalidAlgorithmParameterException(str);
                }
            } else {
                throw new InvalidAlgorithmParameterException(str);
            }
        }
    }

    private void initializeGenerator(String str) throws InvalidAlgorithmParameterException {
        int i;
        AsymmetricCipherKeyPairGenerator asymmetricCipherKeyPairGenerator;
        if (str.equalsIgnoreCase(EdDSAParameterSpec.Ed448) || str.equals(EdECObjectIdentifiers.id_Ed448.getId())) {
            i = 0;
            algorithmCheck(0);
            asymmetricCipherKeyPairGenerator = new Ed448KeyPairGenerator();
        } else if (str.equalsIgnoreCase(EdDSAParameterSpec.Ed25519) || str.equals(EdECObjectIdentifiers.id_Ed25519.getId())) {
            i = 1;
            algorithmCheck(1);
            asymmetricCipherKeyPairGenerator = new Ed25519KeyPairGenerator();
        } else if (str.equalsIgnoreCase(XDHParameterSpec.X448) || str.equals(EdECObjectIdentifiers.id_X448.getId())) {
            i = 2;
            algorithmCheck(2);
            asymmetricCipherKeyPairGenerator = new X448KeyPairGenerator();
        } else if (str.equalsIgnoreCase(XDHParameterSpec.X25519) || str.equals(EdECObjectIdentifiers.id_X25519.getId())) {
            i = 3;
            algorithmCheck(3);
            asymmetricCipherKeyPairGenerator = new X25519KeyPairGenerator();
        } else {
            return;
        }
        this.generator = asymmetricCipherKeyPairGenerator;
        setupGenerator(i);
    }

    private void setupGenerator(int i) {
        AsymmetricCipherKeyPairGenerator asymmetricCipherKeyPairGenerator;
        KeyGenerationParameters keyGenerationParameters;
        this.initialised = true;
        if (this.secureRandom == null) {
            this.secureRandom = new SecureRandom();
        }
        if (i != -2) {
            if (i != -1) {
                if (i == 0) {
                    asymmetricCipherKeyPairGenerator = this.generator;
                    keyGenerationParameters = new Ed448KeyGenerationParameters(this.secureRandom);
                } else if (i != 1) {
                    if (i == 2) {
                        asymmetricCipherKeyPairGenerator = this.generator;
                        keyGenerationParameters = new X448KeyGenerationParameters(this.secureRandom);
                    } else if (i != 3) {
                        return;
                    }
                }
                asymmetricCipherKeyPairGenerator.init(keyGenerationParameters);
            }
            asymmetricCipherKeyPairGenerator = this.generator;
            keyGenerationParameters = new Ed25519KeyGenerationParameters(this.secureRandom);
            asymmetricCipherKeyPairGenerator.init(keyGenerationParameters);
        }
        asymmetricCipherKeyPairGenerator = this.generator;
        keyGenerationParameters = new X25519KeyGenerationParameters(this.secureRandom);
        asymmetricCipherKeyPairGenerator.init(keyGenerationParameters);
    }

    public KeyPair generateKeyPair() {
        String str = "generator not correctly initialized";
        if (this.generator != null) {
            if (!this.initialised) {
                setupGenerator(this.algorithm);
            }
            AsymmetricCipherKeyPair generateKeyPair = this.generator.generateKeyPair();
            int i = this.algorithm;
            if (i == 0) {
                return new KeyPair(new BCEdDSAPublicKey(generateKeyPair.getPublic()), new BCEdDSAPrivateKey(generateKeyPair.getPrivate()));
            }
            if (i == 1) {
                return new KeyPair(new BCEdDSAPublicKey(generateKeyPair.getPublic()), new BCEdDSAPrivateKey(generateKeyPair.getPrivate()));
            }
            if (i == 2) {
                return new KeyPair(new BCXDHPublicKey(generateKeyPair.getPublic()), new BCXDHPrivateKey(generateKeyPair.getPrivate()));
            }
            if (i == 3) {
                return new KeyPair(new BCXDHPublicKey(generateKeyPair.getPublic()), new BCXDHPrivateKey(generateKeyPair.getPrivate()));
            }
            throw new IllegalStateException(str);
        }
        throw new IllegalStateException(str);
    }

    public void initialize(int i, SecureRandom secureRandom2) {
        int i2;
        this.secureRandom = secureRandom2;
        String str = "key size not configurable";
        if (i == 255 || i == 256) {
            int i3 = this.algorithm;
            i2 = 3;
            if (i3 != -2) {
                if (i3 == -1 || i3 == 1) {
                    setupGenerator(1);
                    return;
                } else if (i3 != 3) {
                    throw new InvalidParameterException(str);
                }
            }
        } else if (i == 448) {
            int i4 = this.algorithm;
            i2 = 2;
            if (i4 != -2) {
                if (i4 == -1 || i4 == 0) {
                    setupGenerator(0);
                    return;
                } else if (i4 != 2) {
                    throw new InvalidParameterException(str);
                }
            }
        } else {
            throw new InvalidParameterException("unknown key size");
        }
        setupGenerator(i2);
    }

    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom2) throws InvalidAlgorithmParameterException {
        String curveName;
        this.secureRandom = secureRandom2;
        if (algorithmParameterSpec instanceof ECGenParameterSpec) {
            curveName = ((ECGenParameterSpec) algorithmParameterSpec).getName();
        } else if (algorithmParameterSpec instanceof ECNamedCurveGenParameterSpec) {
            curveName = ((ECNamedCurveGenParameterSpec) algorithmParameterSpec).getName();
        } else if (algorithmParameterSpec instanceof EdDSAParameterSpec) {
            curveName = ((EdDSAParameterSpec) algorithmParameterSpec).getCurveName();
        } else if (algorithmParameterSpec instanceof XDHParameterSpec) {
            curveName = ((XDHParameterSpec) algorithmParameterSpec).getCurveName();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("invalid parameterSpec: ");
            sb.append(algorithmParameterSpec);
            throw new InvalidAlgorithmParameterException(sb.toString());
        }
        initializeGenerator(curveName);
    }
}
