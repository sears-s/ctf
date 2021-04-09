package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.jcajce.PBKDF2Key;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import org.bouncycastle.util.Integers;

public class PBEPBKDF2 {
    /* access modifiers changed from: private */
    public static final Map prfCodes = new HashMap();

    public static class AlgParams extends BaseAlgorithmParameters {
        PBKDF2Params params;

        /* access modifiers changed from: protected */
        public byte[] engineGetEncoded() {
            try {
                return this.params.getEncoded(ASN1Encoding.DER);
            } catch (IOException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Oooops! ");
                sb.append(e.toString());
                throw new RuntimeException(sb.toString());
            }
        }

        /* access modifiers changed from: protected */
        public byte[] engineGetEncoded(String str) {
            if (isASN1FormatString(str)) {
                return engineGetEncoded();
            }
            return null;
        }

        /* access modifiers changed from: protected */
        public void engineInit(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (algorithmParameterSpec instanceof PBEParameterSpec) {
                PBEParameterSpec pBEParameterSpec = (PBEParameterSpec) algorithmParameterSpec;
                this.params = new PBKDF2Params(pBEParameterSpec.getSalt(), pBEParameterSpec.getIterationCount());
                return;
            }
            throw new InvalidParameterSpecException("PBEParameterSpec required to initialise a PBKDF2 PBE parameters algorithm parameters object");
        }

        /* access modifiers changed from: protected */
        public void engineInit(byte[] bArr) throws IOException {
            this.params = PBKDF2Params.getInstance(ASN1Primitive.fromByteArray(bArr));
        }

        /* access modifiers changed from: protected */
        public void engineInit(byte[] bArr, String str) throws IOException {
            if (isASN1FormatString(str)) {
                engineInit(bArr);
                return;
            }
            throw new IOException("Unknown parameters format in PBKDF2 parameters object");
        }

        /* access modifiers changed from: protected */
        public String engineToString() {
            return "PBKDF2 Parameters";
        }

        /* access modifiers changed from: protected */
        public AlgorithmParameterSpec localEngineGetParameterSpec(Class cls) throws InvalidParameterSpecException {
            if (cls == PBEParameterSpec.class) {
                return new PBEParameterSpec(this.params.getSalt(), this.params.getIterationCount().intValue());
            }
            throw new InvalidParameterSpecException("unknown parameter spec passed to PBKDF2 PBE parameters object.");
        }
    }

    public static class BasePBKDF2 extends BaseSecretKeyFactory {
        private int defaultDigest;
        private int scheme;

        public BasePBKDF2(String str, int i) {
            this(str, i, 1);
        }

        public BasePBKDF2(String str, int i, int i2) {
            super(str, PKCSObjectIdentifiers.id_PBKDF2);
            this.scheme = i;
            this.defaultDigest = i2;
        }

        private int getDigestCode(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws InvalidKeySpecException {
            Integer num = (Integer) PBEPBKDF2.prfCodes.get(aSN1ObjectIdentifier);
            if (num != null) {
                return num.intValue();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid KeySpec: unknown PRF algorithm ");
            sb.append(aSN1ObjectIdentifier);
            throw new InvalidKeySpecException(sb.toString());
        }

        /* access modifiers changed from: protected */
        public SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException {
            if (keySpec instanceof PBEKeySpec) {
                PBEKeySpec pBEKeySpec = (PBEKeySpec) keySpec;
                if (pBEKeySpec.getSalt() == null) {
                    return new PBKDF2Key(pBEKeySpec.getPassword(), this.scheme == 1 ? PasswordConverter.ASCII : PasswordConverter.UTF8);
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
                } else if (pBEKeySpec.getPassword().length == 0) {
                    throw new IllegalArgumentException("password empty");
                } else if (pBEKeySpec instanceof PBKDF2KeySpec) {
                    int digestCode = getDigestCode(((PBKDF2KeySpec) pBEKeySpec).getPrf().getAlgorithm());
                    int keyLength = pBEKeySpec.getKeyLength();
                    BCPBEKey bCPBEKey = new BCPBEKey(this.algName, this.algOid, this.scheme, digestCode, keyLength, -1, pBEKeySpec, Util.makePBEMacParameters(pBEKeySpec, this.scheme, digestCode, keyLength));
                    return bCPBEKey;
                } else {
                    int i = this.defaultDigest;
                    int keyLength2 = pBEKeySpec.getKeyLength();
                    BCPBEKey bCPBEKey2 = new BCPBEKey(this.algName, this.algOid, this.scheme, i, keyLength2, -1, pBEKeySpec, Util.makePBEMacParameters(pBEKeySpec, this.scheme, i, keyLength2));
                    return bCPBEKey2;
                }
            } else {
                throw new InvalidKeySpecException("Invalid KeySpec");
            }
        }
    }

    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = PBEPBKDF2.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$AlgParams");
            configurableProvider.addAlgorithm("AlgorithmParameters.PBKDF2", sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Alg.Alias.AlgorithmParameters.");
            sb2.append(PKCSObjectIdentifiers.id_PBKDF2);
            String str = "PBKDF2";
            configurableProvider.addAlgorithm(sb2.toString(), str);
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            sb3.append("$PBKDF2withUTF8");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2", sb3.toString());
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WITHHMACSHA1", str);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WITHHMACSHA1ANDUTF8", str);
            StringBuilder sb4 = new StringBuilder();
            sb4.append("Alg.Alias.SecretKeyFactory.");
            sb4.append(PKCSObjectIdentifiers.id_PBKDF2);
            configurableProvider.addAlgorithm(sb4.toString(), str);
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append("$PBKDF2with8BIT");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHASCII", sb5.toString());
            String str2 = "PBKDF2WITHASCII";
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WITH8BIT", str2);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WITHHMACSHA1AND8BIT", str2);
            StringBuilder sb6 = new StringBuilder();
            sb6.append(PREFIX);
            sb6.append("$PBKDF2withSHA224");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA224", sb6.toString());
            StringBuilder sb7 = new StringBuilder();
            sb7.append(PREFIX);
            sb7.append("$PBKDF2withSHA256");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA256", sb7.toString());
            StringBuilder sb8 = new StringBuilder();
            sb8.append(PREFIX);
            sb8.append("$PBKDF2withSHA384");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA384", sb8.toString());
            StringBuilder sb9 = new StringBuilder();
            sb9.append(PREFIX);
            sb9.append("$PBKDF2withSHA512");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA512", sb9.toString());
            StringBuilder sb10 = new StringBuilder();
            sb10.append(PREFIX);
            sb10.append("$PBKDF2withSHA3_224");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA3-224", sb10.toString());
            StringBuilder sb11 = new StringBuilder();
            sb11.append(PREFIX);
            sb11.append("$PBKDF2withSHA3_256");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA3-256", sb11.toString());
            StringBuilder sb12 = new StringBuilder();
            sb12.append(PREFIX);
            sb12.append("$PBKDF2withSHA3_384");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA3-384", sb12.toString());
            StringBuilder sb13 = new StringBuilder();
            sb13.append(PREFIX);
            sb13.append("$PBKDF2withSHA3_512");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA3-512", sb13.toString());
            StringBuilder sb14 = new StringBuilder();
            sb14.append(PREFIX);
            sb14.append("$PBKDF2withGOST3411");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACGOST3411", sb14.toString());
        }
    }

    public static class PBKDF2with8BIT extends BasePBKDF2 {
        public PBKDF2with8BIT() {
            super("PBKDF2", 1);
        }
    }

    public static class PBKDF2withGOST3411 extends BasePBKDF2 {
        public PBKDF2withGOST3411() {
            super("PBKDF2", 5, 6);
        }
    }

    public static class PBKDF2withSHA224 extends BasePBKDF2 {
        public PBKDF2withSHA224() {
            super("PBKDF2", 5, 7);
        }
    }

    public static class PBKDF2withSHA256 extends BasePBKDF2 {
        public PBKDF2withSHA256() {
            super("PBKDF2", 5, 4);
        }
    }

    public static class PBKDF2withSHA384 extends BasePBKDF2 {
        public PBKDF2withSHA384() {
            super("PBKDF2", 5, 8);
        }
    }

    public static class PBKDF2withSHA3_224 extends BasePBKDF2 {
        public PBKDF2withSHA3_224() {
            super("PBKDF2", 5, 10);
        }
    }

    public static class PBKDF2withSHA3_256 extends BasePBKDF2 {
        public PBKDF2withSHA3_256() {
            super("PBKDF2", 5, 11);
        }
    }

    public static class PBKDF2withSHA3_384 extends BasePBKDF2 {
        public PBKDF2withSHA3_384() {
            super("PBKDF2", 5, 12);
        }
    }

    public static class PBKDF2withSHA3_512 extends BasePBKDF2 {
        public PBKDF2withSHA3_512() {
            super("PBKDF2", 5, 13);
        }
    }

    public static class PBKDF2withSHA512 extends BasePBKDF2 {
        public PBKDF2withSHA512() {
            super("PBKDF2", 5, 9);
        }
    }

    public static class PBKDF2withUTF8 extends BasePBKDF2 {
        public PBKDF2withUTF8() {
            super("PBKDF2", 5);
        }
    }

    static {
        prfCodes.put(CryptoProObjectIdentifiers.gostR3411Hmac, Integers.valueOf(6));
        prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA1, Integers.valueOf(1));
        prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA256, Integers.valueOf(4));
        prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA224, Integers.valueOf(7));
        prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA384, Integers.valueOf(8));
        prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA512, Integers.valueOf(9));
        prfCodes.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, Integers.valueOf(11));
        prfCodes.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, Integers.valueOf(10));
        prfCodes.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, Integers.valueOf(12));
        prfCodes.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, Integers.valueOf(13));
    }

    private PBEPBKDF2() {
    }
}
