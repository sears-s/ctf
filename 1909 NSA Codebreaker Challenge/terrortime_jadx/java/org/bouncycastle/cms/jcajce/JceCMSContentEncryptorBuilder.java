package org.bouncycastle.cms.jcajce;

import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.io.CipherOutputStream;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceCMSContentEncryptorBuilder {
    private static final SecretKeySizeProvider KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
    private AlgorithmIdentifier algorithmIdentifier;
    private AlgorithmParameters algorithmParameters;
    private final ASN1ObjectIdentifier encryptionOID;
    /* access modifiers changed from: private */
    public EnvelopedDataHelper helper;
    private final int keySize;
    private SecureRandom random;

    private class CMSOutputEncryptor implements OutputEncryptor {
        private AlgorithmIdentifier algorithmIdentifier;
        private Cipher cipher;
        private SecretKey encKey;

        CMSOutputEncryptor(ASN1ObjectIdentifier aSN1ObjectIdentifier, int i, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws CMSException {
            KeyGenerator createKeyGenerator = JceCMSContentEncryptorBuilder.this.helper.createKeyGenerator(aSN1ObjectIdentifier);
            if (secureRandom == null) {
                secureRandom = CryptoServicesRegistrar.getSecureRandom();
            }
            if (i < 0) {
                createKeyGenerator.init(secureRandom);
            } else {
                createKeyGenerator.init(i, secureRandom);
            }
            this.cipher = JceCMSContentEncryptorBuilder.this.helper.createCipher(aSN1ObjectIdentifier);
            this.encKey = createKeyGenerator.generateKey();
            if (algorithmParameters == null) {
                algorithmParameters = JceCMSContentEncryptorBuilder.this.helper.generateParameters(aSN1ObjectIdentifier, this.encKey, secureRandom);
            }
            try {
                this.cipher.init(1, this.encKey, algorithmParameters, secureRandom);
                if (algorithmParameters == null) {
                    algorithmParameters = this.cipher.getParameters();
                }
                this.algorithmIdentifier = JceCMSContentEncryptorBuilder.this.helper.getAlgorithmIdentifier(aSN1ObjectIdentifier, algorithmParameters);
            } catch (GeneralSecurityException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("unable to initialize cipher: ");
                sb.append(e.getMessage());
                throw new CMSException(sb.toString(), e);
            }
        }

        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }

        public GenericKey getKey() {
            return new JceGenericKey(this.algorithmIdentifier, this.encKey);
        }

        public OutputStream getOutputStream(OutputStream outputStream) {
            return new CipherOutputStream(outputStream, this.cipher);
        }
    }

    public JceCMSContentEncryptorBuilder(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this(aSN1ObjectIdentifier, KEY_SIZE_PROVIDER.getKeySize(aSN1ObjectIdentifier));
    }

    public JceCMSContentEncryptorBuilder(ASN1ObjectIdentifier aSN1ObjectIdentifier, int i) {
        int i2;
        this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.encryptionOID = aSN1ObjectIdentifier;
        int keySize2 = KEY_SIZE_PROVIDER.getKeySize(aSN1ObjectIdentifier);
        String str = "incorrect keySize for encryptionOID passed to builder.";
        if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.des_EDE3_CBC)) {
            i2 = 168;
            if (!(i == 168 || i == keySize2)) {
                throw new IllegalArgumentException(str);
            }
        } else if (aSN1ObjectIdentifier.equals(OIWObjectIdentifiers.desCBC)) {
            i2 = 56;
            if (!(i == 56 || i == keySize2)) {
                throw new IllegalArgumentException(str);
            }
        } else if (keySize2 <= 0 || keySize2 == i) {
            this.keySize = i;
            return;
        } else {
            throw new IllegalArgumentException(str);
        }
        this.keySize = i2;
    }

    public JceCMSContentEncryptorBuilder(AlgorithmIdentifier algorithmIdentifier2) {
        this(algorithmIdentifier2.getAlgorithm(), KEY_SIZE_PROVIDER.getKeySize(algorithmIdentifier2.getAlgorithm()));
        this.algorithmIdentifier = algorithmIdentifier2;
    }

    public OutputEncryptor build() throws CMSException {
        AlgorithmParameters algorithmParameters2 = this.algorithmParameters;
        if (algorithmParameters2 != null) {
            CMSOutputEncryptor cMSOutputEncryptor = new CMSOutputEncryptor(this.encryptionOID, this.keySize, algorithmParameters2, this.random);
            return cMSOutputEncryptor;
        }
        AlgorithmIdentifier algorithmIdentifier2 = this.algorithmIdentifier;
        if (algorithmIdentifier2 != null) {
            ASN1Encodable parameters = algorithmIdentifier2.getParameters();
            if (parameters != null && !parameters.equals(DERNull.INSTANCE)) {
                try {
                    this.algorithmParameters = this.helper.createAlgorithmParameters(this.algorithmIdentifier.getAlgorithm());
                    this.algorithmParameters.init(parameters.toASN1Primitive().getEncoded());
                } catch (Exception e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("unable to process provided algorithmIdentifier: ");
                    sb.append(e.toString());
                    throw new CMSException(sb.toString(), e);
                }
            }
        }
        CMSOutputEncryptor cMSOutputEncryptor2 = new CMSOutputEncryptor(this.encryptionOID, this.keySize, this.algorithmParameters, this.random);
        return cMSOutputEncryptor2;
    }

    public JceCMSContentEncryptorBuilder setAlgorithmParameters(AlgorithmParameters algorithmParameters2) {
        this.algorithmParameters = algorithmParameters2;
        return this;
    }

    public JceCMSContentEncryptorBuilder setProvider(String str) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(str));
        return this;
    }

    public JceCMSContentEncryptorBuilder setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceCMSContentEncryptorBuilder setSecureRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }
}
