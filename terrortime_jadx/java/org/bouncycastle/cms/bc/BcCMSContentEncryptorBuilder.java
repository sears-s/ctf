package org.bouncycastle.cms.bc;

import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.CipherFactory;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.Integers;

public class BcCMSContentEncryptorBuilder {
    private static Map keySizes = new HashMap();
    private final ASN1ObjectIdentifier encryptionOID;
    /* access modifiers changed from: private */
    public EnvelopedDataHelper helper;
    private final int keySize;
    private SecureRandom random;

    private class CMSOutputEncryptor implements OutputEncryptor {
        private AlgorithmIdentifier algorithmIdentifier;
        private Object cipher;
        private KeyParameter encKey;

        CMSOutputEncryptor(ASN1ObjectIdentifier aSN1ObjectIdentifier, int i, SecureRandom secureRandom) throws CMSException {
            if (secureRandom == null) {
                secureRandom = new SecureRandom();
            }
            this.encKey = new KeyParameter(BcCMSContentEncryptorBuilder.this.helper.createKeyGenerator(aSN1ObjectIdentifier, secureRandom).generateKey());
            this.algorithmIdentifier = BcCMSContentEncryptorBuilder.this.helper.generateEncryptionAlgID(aSN1ObjectIdentifier, this.encKey, secureRandom);
            this.cipher = EnvelopedDataHelper.createContentCipher(true, this.encKey, this.algorithmIdentifier);
        }

        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }

        public GenericKey getKey() {
            return new GenericKey(this.algorithmIdentifier, this.encKey.getKey());
        }

        public OutputStream getOutputStream(OutputStream outputStream) {
            return CipherFactory.createOutputStream(outputStream, this.cipher);
        }
    }

    static {
        keySizes.put(CMSAlgorithm.AES128_CBC, Integers.valueOf(128));
        keySizes.put(CMSAlgorithm.AES192_CBC, Integers.valueOf(192));
        keySizes.put(CMSAlgorithm.AES256_CBC, Integers.valueOf(256));
        keySizes.put(CMSAlgorithm.CAMELLIA128_CBC, Integers.valueOf(128));
        keySizes.put(CMSAlgorithm.CAMELLIA192_CBC, Integers.valueOf(192));
        keySizes.put(CMSAlgorithm.CAMELLIA256_CBC, Integers.valueOf(256));
    }

    public BcCMSContentEncryptorBuilder(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this(aSN1ObjectIdentifier, getKeySize(aSN1ObjectIdentifier));
    }

    public BcCMSContentEncryptorBuilder(ASN1ObjectIdentifier aSN1ObjectIdentifier, int i) {
        this.helper = new EnvelopedDataHelper();
        this.encryptionOID = aSN1ObjectIdentifier;
        this.keySize = i;
    }

    private static int getKeySize(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        Integer num = (Integer) keySizes.get(aSN1ObjectIdentifier);
        if (num != null) {
            return num.intValue();
        }
        return -1;
    }

    public OutputEncryptor build() throws CMSException {
        return new CMSOutputEncryptor(this.encryptionOID, this.keySize, this.random);
    }

    public BcCMSContentEncryptorBuilder setSecureRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }
}
