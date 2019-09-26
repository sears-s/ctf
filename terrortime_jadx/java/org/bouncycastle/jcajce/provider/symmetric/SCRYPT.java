package org.bouncycastle.jcajce.provider.symmetric;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.spec.ScryptKeySpec;

public class SCRYPT {

    public static class BasePBKDF2 extends BaseSecretKeyFactory {
        private int scheme;

        public BasePBKDF2(String str, int i) {
            super(str, MiscObjectIdentifiers.id_scrypt);
            this.scheme = i;
        }

        /* access modifiers changed from: protected */
        public SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException {
            if (keySpec instanceof ScryptKeySpec) {
                ScryptKeySpec scryptKeySpec = (ScryptKeySpec) keySpec;
                if (scryptKeySpec.getSalt() == null) {
                    throw new IllegalArgumentException("Salt S must be provided.");
                } else if (scryptKeySpec.getCostParameter() <= 1) {
                    throw new IllegalArgumentException("Cost parameter N must be > 1.");
                } else if (scryptKeySpec.getKeyLength() <= 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("positive key length required: ");
                    sb.append(scryptKeySpec.getKeyLength());
                    throw new InvalidKeySpecException(sb.toString());
                } else if (scryptKeySpec.getPassword().length != 0) {
                    return new BCPBEKey(this.algName, scryptKeySpec, new KeyParameter(SCrypt.generate(PasswordConverter.UTF8.convert(scryptKeySpec.getPassword()), scryptKeySpec.getSalt(), scryptKeySpec.getCostParameter(), scryptKeySpec.getBlockSize(), scryptKeySpec.getParallelizationParameter(), scryptKeySpec.getKeyLength() / 8)));
                } else {
                    throw new IllegalArgumentException("password empty");
                }
            } else {
                throw new InvalidKeySpecException("Invalid KeySpec");
            }
        }
    }

    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = SCRYPT.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            String str = "$ScryptWithUTF8";
            sb.append(str);
            configurableProvider.addAlgorithm("SecretKeyFactory.SCRYPT", sb.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier = MiscObjectIdentifiers.id_scrypt;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(PREFIX);
            sb2.append(str);
            configurableProvider.addAlgorithm("SecretKeyFactory", aSN1ObjectIdentifier, sb2.toString());
        }
    }

    public static class ScryptWithUTF8 extends BasePBKDF2 {
        public ScryptWithUTF8() {
            super("SCRYPT", 5);
        }
    }

    private SCRYPT() {
    }
}
