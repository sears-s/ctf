package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.pkcs.PBEParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public class PBEPBKDF1 {

    public static class AlgParams extends BaseAlgorithmParameters {
        PBEParameter params;

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
                this.params = new PBEParameter(pBEParameterSpec.getSalt(), pBEParameterSpec.getIterationCount());
                return;
            }
            throw new InvalidParameterSpecException("PBEParameterSpec required to initialise a PBKDF1 PBE parameters algorithm parameters object");
        }

        /* access modifiers changed from: protected */
        public void engineInit(byte[] bArr) throws IOException {
            this.params = PBEParameter.getInstance(bArr);
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
            return "PBKDF1 Parameters";
        }

        /* access modifiers changed from: protected */
        public AlgorithmParameterSpec localEngineGetParameterSpec(Class cls) throws InvalidParameterSpecException {
            if (cls == PBEParameterSpec.class) {
                return new PBEParameterSpec(this.params.getSalt(), this.params.getIterationCount().intValue());
            }
            throw new InvalidParameterSpecException("unknown parameter spec passed to PBKDF1 PBE parameters object.");
        }
    }

    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = PBEPBKDF1.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$AlgParams");
            configurableProvider.addAlgorithm("AlgorithmParameters.PBKDF1", sb.toString());
            StringBuilder sb2 = new StringBuilder();
            String str = "Alg.Alias.AlgorithmParameters.";
            sb2.append(str);
            sb2.append(PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC);
            String str2 = "PBKDF1";
            configurableProvider.addAlgorithm(sb2.toString(), str2);
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            sb3.append(PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC);
            configurableProvider.addAlgorithm(sb3.toString(), str2);
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str);
            sb4.append(PKCSObjectIdentifiers.pbeWithMD5AndRC2_CBC);
            configurableProvider.addAlgorithm(sb4.toString(), str2);
            StringBuilder sb5 = new StringBuilder();
            sb5.append(str);
            sb5.append(PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC);
            configurableProvider.addAlgorithm(sb5.toString(), str2);
            StringBuilder sb6 = new StringBuilder();
            sb6.append(str);
            sb6.append(PKCSObjectIdentifiers.pbeWithSHA1AndRC2_CBC);
            configurableProvider.addAlgorithm(sb6.toString(), str2);
        }
    }

    private PBEPBKDF1() {
    }
}
