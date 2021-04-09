package org.bouncycastle.jcajce.provider.symmetric;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.util.Strings;

public final class OpenSSLPBKDF {

    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = OpenSSLPBKDF.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$PBKDF");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF-OPENSSL", sb.toString());
        }
    }

    public static class PBKDF extends BaseSecretKeyFactory {
        public PBKDF() {
            super("PBKDF-OpenSSL", null);
        }

        /* access modifiers changed from: protected */
        public SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException {
            if (keySpec instanceof PBEKeySpec) {
                PBEKeySpec pBEKeySpec = (PBEKeySpec) keySpec;
                if (pBEKeySpec.getSalt() == null) {
                    throw new InvalidKeySpecException("missing required salt");
                } else if (pBEKeySpec.getIterationCount() <= 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("positive iteration count required: ");
                    sb.append(pBEKeySpec.getIterationCount());
                    throw new InvalidKeySpecException(sb.toString());
                } else if (pBEKeySpec.getKeyLength() <= 0) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("positive key length required: ");
                    sb2.append(pBEKeySpec.getKeyLength());
                    throw new InvalidKeySpecException(sb2.toString());
                } else if (pBEKeySpec.getPassword().length != 0) {
                    OpenSSLPBEParametersGenerator openSSLPBEParametersGenerator = new OpenSSLPBEParametersGenerator();
                    openSSLPBEParametersGenerator.init(Strings.toByteArray(pBEKeySpec.getPassword()), pBEKeySpec.getSalt());
                    return new SecretKeySpec(((KeyParameter) openSSLPBEParametersGenerator.generateDerivedParameters(pBEKeySpec.getKeyLength())).getKey(), "OpenSSLPBKDF");
                } else {
                    throw new IllegalArgumentException("password empty");
                }
            } else {
                throw new InvalidKeySpecException("Invalid KeySpec");
            }
        }
    }

    private OpenSSLPBKDF() {
    }
}
