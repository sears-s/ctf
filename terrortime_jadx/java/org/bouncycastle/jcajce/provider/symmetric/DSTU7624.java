package org.bouncycastle.jcajce.provider.symmetric;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.crypto.engines.DSTU7624WrapEngine;
import org.bouncycastle.crypto.macs.KGMac;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.KCCMBlockCipher;
import org.bouncycastle.crypto.modes.KCTRBlockCipher;
import org.bouncycastle.crypto.modes.KGCMBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;

public class DSTU7624 {

    public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
        private final int ivLength;

        public AlgParamGen(int i) {
            this.ivLength = i / 8;
        }

        /* access modifiers changed from: protected */
        public AlgorithmParameters engineGenerateParameters() {
            byte[] bArr = new byte[this.ivLength];
            if (this.random == null) {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
            this.random.nextBytes(bArr);
            try {
                AlgorithmParameters createParametersInstance = createParametersInstance("DSTU7624");
                createParametersInstance.init(new IvParameterSpec(bArr));
                return createParametersInstance;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        /* access modifiers changed from: protected */
        public void engineInit(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for DSTU7624 parameter generation.");
        }
    }

    public static class AlgParamGen128 extends AlgParamGen {
        AlgParamGen128() {
            super(128);
        }
    }

    public static class AlgParamGen256 extends AlgParamGen {
        AlgParamGen256() {
            super(256);
        }
    }

    public static class AlgParamGen512 extends AlgParamGen {
        AlgParamGen512() {
            super(512);
        }
    }

    public static class AlgParams extends IvAlgorithmParameters {
        /* access modifiers changed from: protected */
        public String engineToString() {
            return "DSTU7624 IV";
        }
    }

    public static class CBC128 extends BaseBlockCipher {
        public CBC128() {
            super((BlockCipher) new CBCBlockCipher(new DSTU7624Engine(128)), 128);
        }
    }

    public static class CBC256 extends BaseBlockCipher {
        public CBC256() {
            super((BlockCipher) new CBCBlockCipher(new DSTU7624Engine(256)), 256);
        }
    }

    public static class CBC512 extends BaseBlockCipher {
        public CBC512() {
            super((BlockCipher) new CBCBlockCipher(new DSTU7624Engine(512)), 512);
        }
    }

    public static class CCM128 extends BaseBlockCipher {
        public CCM128() {
            super((AEADBlockCipher) new KCCMBlockCipher(new DSTU7624Engine(128)));
        }
    }

    public static class CCM256 extends BaseBlockCipher {
        public CCM256() {
            super((AEADBlockCipher) new KCCMBlockCipher(new DSTU7624Engine(256)));
        }
    }

    public static class CCM512 extends BaseBlockCipher {
        public CCM512() {
            super((AEADBlockCipher) new KCCMBlockCipher(new DSTU7624Engine(512)));
        }
    }

    public static class CFB128 extends BaseBlockCipher {
        public CFB128() {
            super(new BufferedBlockCipher(new CFBBlockCipher(new DSTU7624Engine(128), 128)), 128);
        }
    }

    public static class CFB256 extends BaseBlockCipher {
        public CFB256() {
            super(new BufferedBlockCipher(new CFBBlockCipher(new DSTU7624Engine(256), 256)), 256);
        }
    }

    public static class CFB512 extends BaseBlockCipher {
        public CFB512() {
            super(new BufferedBlockCipher(new CFBBlockCipher(new DSTU7624Engine(512), 512)), 512);
        }
    }

    public static class CTR128 extends BaseBlockCipher {
        public CTR128() {
            super(new BufferedBlockCipher(new KCTRBlockCipher(new DSTU7624Engine(128))), 128);
        }
    }

    public static class CTR256 extends BaseBlockCipher {
        public CTR256() {
            super(new BufferedBlockCipher(new KCTRBlockCipher(new DSTU7624Engine(256))), 256);
        }
    }

    public static class CTR512 extends BaseBlockCipher {
        public CTR512() {
            super(new BufferedBlockCipher(new KCTRBlockCipher(new DSTU7624Engine(512))), 512);
        }
    }

    public static class ECB extends BaseBlockCipher {
        public ECB() {
            super((BlockCipherProvider) new BlockCipherProvider() {
                public BlockCipher get() {
                    return new DSTU7624Engine(128);
                }
            });
        }
    }

    public static class ECB128 extends BaseBlockCipher {
        public ECB128() {
            super((BlockCipher) new DSTU7624Engine(128));
        }
    }

    public static class ECB256 extends BaseBlockCipher {
        public ECB256() {
            super((BlockCipher) new DSTU7624Engine(256));
        }
    }

    public static class ECB512 extends BaseBlockCipher {
        public ECB512() {
            super((BlockCipher) new DSTU7624Engine(512));
        }
    }

    public static class ECB_128 extends BaseBlockCipher {
        public ECB_128() {
            super((BlockCipher) new DSTU7624Engine(128));
        }
    }

    public static class ECB_256 extends BaseBlockCipher {
        public ECB_256() {
            super((BlockCipher) new DSTU7624Engine(256));
        }
    }

    public static class ECB_512 extends BaseBlockCipher {
        public ECB_512() {
            super((BlockCipher) new DSTU7624Engine(512));
        }
    }

    public static class GCM128 extends BaseBlockCipher {
        public GCM128() {
            super((AEADBlockCipher) new KGCMBlockCipher(new DSTU7624Engine(128)));
        }
    }

    public static class GCM256 extends BaseBlockCipher {
        public GCM256() {
            super((AEADBlockCipher) new KGCMBlockCipher(new DSTU7624Engine(256)));
        }
    }

    public static class GCM512 extends BaseBlockCipher {
        public GCM512() {
            super((AEADBlockCipher) new KGCMBlockCipher(new DSTU7624Engine(512)));
        }
    }

    public static class GMAC extends BaseMac {
        public GMAC() {
            super(new KGMac(new KGCMBlockCipher(new DSTU7624Engine(128)), 128));
        }
    }

    public static class GMAC128 extends BaseMac {
        public GMAC128() {
            super(new KGMac(new KGCMBlockCipher(new DSTU7624Engine(128)), 128));
        }
    }

    public static class GMAC256 extends BaseMac {
        public GMAC256() {
            super(new KGMac(new KGCMBlockCipher(new DSTU7624Engine(256)), 256));
        }
    }

    public static class GMAC512 extends BaseMac {
        public GMAC512() {
            super(new KGMac(new KGCMBlockCipher(new DSTU7624Engine(512)), 512));
        }
    }

    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            this(256);
        }

        public KeyGen(int i) {
            super("DSTU7624", i, new CipherKeyGenerator());
        }
    }

    public static class KeyGen128 extends KeyGen {
        public KeyGen128() {
            super(128);
        }
    }

    public static class KeyGen256 extends KeyGen {
        public KeyGen256() {
            super(256);
        }
    }

    public static class KeyGen512 extends KeyGen {
        public KeyGen512() {
            super(512);
        }
    }

    public static class Mappings extends SymmetricAlgorithmProvider {
        private static final String PREFIX = DSTU7624.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$AlgParams128");
            configurableProvider.addAlgorithm("AlgorithmParameters.DSTU7624", sb.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier = UAObjectIdentifiers.dstu7624cbc_128;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(PREFIX);
            String str = "$AlgParams";
            sb2.append(str);
            String str2 = "AlgorithmParameters";
            configurableProvider.addAlgorithm(str2, aSN1ObjectIdentifier, sb2.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier2 = UAObjectIdentifiers.dstu7624cbc_256;
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            sb3.append(str);
            configurableProvider.addAlgorithm(str2, aSN1ObjectIdentifier2, sb3.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier3 = UAObjectIdentifiers.dstu7624cbc_512;
            StringBuilder sb4 = new StringBuilder();
            sb4.append(PREFIX);
            sb4.append(str);
            configurableProvider.addAlgorithm(str2, aSN1ObjectIdentifier3, sb4.toString());
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            String str3 = "$AlgParamGen128";
            sb5.append(str3);
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.DSTU7624", sb5.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier4 = UAObjectIdentifiers.dstu7624cbc_128;
            StringBuilder sb6 = new StringBuilder();
            sb6.append(PREFIX);
            sb6.append(str3);
            String sb7 = sb6.toString();
            String str4 = "AlgorithmParameterGenerator";
            configurableProvider.addAlgorithm(str4, aSN1ObjectIdentifier4, sb7);
            ASN1ObjectIdentifier aSN1ObjectIdentifier5 = UAObjectIdentifiers.dstu7624cbc_256;
            StringBuilder sb8 = new StringBuilder();
            sb8.append(PREFIX);
            sb8.append("$AlgParamGen256");
            configurableProvider.addAlgorithm(str4, aSN1ObjectIdentifier5, sb8.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier6 = UAObjectIdentifiers.dstu7624cbc_512;
            StringBuilder sb9 = new StringBuilder();
            sb9.append(PREFIX);
            sb9.append("$AlgParamGen512");
            configurableProvider.addAlgorithm(str4, aSN1ObjectIdentifier6, sb9.toString());
            StringBuilder sb10 = new StringBuilder();
            sb10.append(PREFIX);
            String str5 = "$ECB_128";
            sb10.append(str5);
            configurableProvider.addAlgorithm("Cipher.DSTU7624", sb10.toString());
            StringBuilder sb11 = new StringBuilder();
            sb11.append(PREFIX);
            sb11.append(str5);
            configurableProvider.addAlgorithm("Cipher.DSTU7624-128", sb11.toString());
            StringBuilder sb12 = new StringBuilder();
            sb12.append(PREFIX);
            sb12.append("$ECB_256");
            configurableProvider.addAlgorithm("Cipher.DSTU7624-256", sb12.toString());
            StringBuilder sb13 = new StringBuilder();
            sb13.append(PREFIX);
            sb13.append("$ECB_512");
            configurableProvider.addAlgorithm("Cipher.DSTU7624-512", sb13.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier7 = UAObjectIdentifiers.dstu7624ecb_128;
            StringBuilder sb14 = new StringBuilder();
            sb14.append(PREFIX);
            sb14.append("$ECB128");
            String str6 = "Cipher";
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier7, sb14.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier8 = UAObjectIdentifiers.dstu7624ecb_256;
            StringBuilder sb15 = new StringBuilder();
            sb15.append(PREFIX);
            sb15.append("$ECB256");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier8, sb15.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier9 = UAObjectIdentifiers.dstu7624ecb_512;
            StringBuilder sb16 = new StringBuilder();
            sb16.append(PREFIX);
            sb16.append("$ECB512");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier9, sb16.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier10 = UAObjectIdentifiers.dstu7624cbc_128;
            StringBuilder sb17 = new StringBuilder();
            sb17.append(PREFIX);
            sb17.append("$CBC128");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier10, sb17.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier11 = UAObjectIdentifiers.dstu7624cbc_256;
            StringBuilder sb18 = new StringBuilder();
            sb18.append(PREFIX);
            sb18.append("$CBC256");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier11, sb18.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier12 = UAObjectIdentifiers.dstu7624cbc_512;
            StringBuilder sb19 = new StringBuilder();
            sb19.append(PREFIX);
            sb19.append("$CBC512");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier12, sb19.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier13 = UAObjectIdentifiers.dstu7624ofb_128;
            StringBuilder sb20 = new StringBuilder();
            sb20.append(PREFIX);
            sb20.append("$OFB128");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier13, sb20.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier14 = UAObjectIdentifiers.dstu7624ofb_256;
            StringBuilder sb21 = new StringBuilder();
            sb21.append(PREFIX);
            sb21.append("$OFB256");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier14, sb21.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier15 = UAObjectIdentifiers.dstu7624ofb_512;
            StringBuilder sb22 = new StringBuilder();
            sb22.append(PREFIX);
            sb22.append("$OFB512");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier15, sb22.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier16 = UAObjectIdentifiers.dstu7624cfb_128;
            StringBuilder sb23 = new StringBuilder();
            sb23.append(PREFIX);
            sb23.append("$CFB128");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier16, sb23.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier17 = UAObjectIdentifiers.dstu7624cfb_256;
            StringBuilder sb24 = new StringBuilder();
            sb24.append(PREFIX);
            sb24.append("$CFB256");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier17, sb24.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier18 = UAObjectIdentifiers.dstu7624cfb_512;
            StringBuilder sb25 = new StringBuilder();
            sb25.append(PREFIX);
            sb25.append("$CFB512");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier18, sb25.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier19 = UAObjectIdentifiers.dstu7624ctr_128;
            StringBuilder sb26 = new StringBuilder();
            sb26.append(PREFIX);
            sb26.append("$CTR128");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier19, sb26.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier20 = UAObjectIdentifiers.dstu7624ctr_256;
            StringBuilder sb27 = new StringBuilder();
            sb27.append(PREFIX);
            sb27.append("$CTR256");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier20, sb27.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier21 = UAObjectIdentifiers.dstu7624ctr_512;
            StringBuilder sb28 = new StringBuilder();
            sb28.append(PREFIX);
            sb28.append("$CTR512");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier21, sb28.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier22 = UAObjectIdentifiers.dstu7624ccm_128;
            StringBuilder sb29 = new StringBuilder();
            sb29.append(PREFIX);
            sb29.append("$CCM128");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier22, sb29.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier23 = UAObjectIdentifiers.dstu7624ccm_256;
            StringBuilder sb30 = new StringBuilder();
            sb30.append(PREFIX);
            sb30.append("$CCM256");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier23, sb30.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier24 = UAObjectIdentifiers.dstu7624ccm_512;
            StringBuilder sb31 = new StringBuilder();
            sb31.append(PREFIX);
            sb31.append("$CCM512");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier24, sb31.toString());
            StringBuilder sb32 = new StringBuilder();
            sb32.append(PREFIX);
            sb32.append("$Wrap");
            configurableProvider.addAlgorithm("Cipher.DSTU7624KW", sb32.toString());
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.DSTU7624WRAP", "DSTU7624KW");
            StringBuilder sb33 = new StringBuilder();
            sb33.append(PREFIX);
            sb33.append("$Wrap128");
            configurableProvider.addAlgorithm("Cipher.DSTU7624-128KW", sb33.toString());
            StringBuilder sb34 = new StringBuilder();
            String str7 = "Alg.Alias.Cipher.";
            sb34.append(str7);
            sb34.append(UAObjectIdentifiers.dstu7624kw_128.getId());
            String str8 = "DSTU7624-128KW";
            configurableProvider.addAlgorithm(sb34.toString(), str8);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.DSTU7624-128WRAP", str8);
            StringBuilder sb35 = new StringBuilder();
            sb35.append(PREFIX);
            sb35.append("$Wrap256");
            configurableProvider.addAlgorithm("Cipher.DSTU7624-256KW", sb35.toString());
            StringBuilder sb36 = new StringBuilder();
            sb36.append(str7);
            sb36.append(UAObjectIdentifiers.dstu7624kw_256.getId());
            String str9 = "DSTU7624-256KW";
            configurableProvider.addAlgorithm(sb36.toString(), str9);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.DSTU7624-256WRAP", str9);
            StringBuilder sb37 = new StringBuilder();
            sb37.append(PREFIX);
            sb37.append("$Wrap512");
            configurableProvider.addAlgorithm("Cipher.DSTU7624-512KW", sb37.toString());
            StringBuilder sb38 = new StringBuilder();
            sb38.append(str7);
            sb38.append(UAObjectIdentifiers.dstu7624kw_512.getId());
            String str10 = "DSTU7624-512KW";
            configurableProvider.addAlgorithm(sb38.toString(), str10);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.DSTU7624-512WRAP", str10);
            StringBuilder sb39 = new StringBuilder();
            sb39.append(PREFIX);
            sb39.append("$GMAC");
            configurableProvider.addAlgorithm("Mac.DSTU7624GMAC", sb39.toString());
            StringBuilder sb40 = new StringBuilder();
            sb40.append(PREFIX);
            sb40.append("$GMAC128");
            configurableProvider.addAlgorithm("Mac.DSTU7624-128GMAC", sb40.toString());
            StringBuilder sb41 = new StringBuilder();
            String str11 = "Alg.Alias.Mac.";
            sb41.append(str11);
            sb41.append(UAObjectIdentifiers.dstu7624gmac_128.getId());
            configurableProvider.addAlgorithm(sb41.toString(), "DSTU7624-128GMAC");
            StringBuilder sb42 = new StringBuilder();
            sb42.append(PREFIX);
            sb42.append("$GMAC256");
            configurableProvider.addAlgorithm("Mac.DSTU7624-256GMAC", sb42.toString());
            StringBuilder sb43 = new StringBuilder();
            sb43.append(str11);
            sb43.append(UAObjectIdentifiers.dstu7624gmac_256.getId());
            configurableProvider.addAlgorithm(sb43.toString(), "DSTU7624-256GMAC");
            StringBuilder sb44 = new StringBuilder();
            sb44.append(PREFIX);
            sb44.append("$GMAC512");
            configurableProvider.addAlgorithm("Mac.DSTU7624-512GMAC", sb44.toString());
            StringBuilder sb45 = new StringBuilder();
            sb45.append(str11);
            sb45.append(UAObjectIdentifiers.dstu7624gmac_512.getId());
            configurableProvider.addAlgorithm(sb45.toString(), "DSTU7624-512GMAC");
            StringBuilder sb46 = new StringBuilder();
            sb46.append(PREFIX);
            sb46.append("$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator.DSTU7624", sb46.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier25 = UAObjectIdentifiers.dstu7624kw_128;
            StringBuilder sb47 = new StringBuilder();
            sb47.append(PREFIX);
            String str12 = "$KeyGen128";
            sb47.append(str12);
            String str13 = "KeyGenerator";
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier25, sb47.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier26 = UAObjectIdentifiers.dstu7624kw_256;
            StringBuilder sb48 = new StringBuilder();
            sb48.append(PREFIX);
            String str14 = "$KeyGen256";
            sb48.append(str14);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier26, sb48.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier27 = UAObjectIdentifiers.dstu7624kw_512;
            StringBuilder sb49 = new StringBuilder();
            sb49.append(PREFIX);
            String str15 = "$KeyGen512";
            sb49.append(str15);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier27, sb49.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier28 = UAObjectIdentifiers.dstu7624ecb_128;
            StringBuilder sb50 = new StringBuilder();
            sb50.append(PREFIX);
            sb50.append(str12);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier28, sb50.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier29 = UAObjectIdentifiers.dstu7624ecb_256;
            StringBuilder sb51 = new StringBuilder();
            sb51.append(PREFIX);
            sb51.append(str14);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier29, sb51.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier30 = UAObjectIdentifiers.dstu7624ecb_512;
            StringBuilder sb52 = new StringBuilder();
            sb52.append(PREFIX);
            sb52.append(str15);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier30, sb52.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier31 = UAObjectIdentifiers.dstu7624cbc_128;
            StringBuilder sb53 = new StringBuilder();
            sb53.append(PREFIX);
            sb53.append(str12);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier31, sb53.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier32 = UAObjectIdentifiers.dstu7624cbc_256;
            StringBuilder sb54 = new StringBuilder();
            sb54.append(PREFIX);
            sb54.append(str14);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier32, sb54.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier33 = UAObjectIdentifiers.dstu7624cbc_512;
            StringBuilder sb55 = new StringBuilder();
            sb55.append(PREFIX);
            sb55.append(str15);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier33, sb55.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier34 = UAObjectIdentifiers.dstu7624ofb_128;
            StringBuilder sb56 = new StringBuilder();
            sb56.append(PREFIX);
            sb56.append(str12);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier34, sb56.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier35 = UAObjectIdentifiers.dstu7624ofb_256;
            StringBuilder sb57 = new StringBuilder();
            sb57.append(PREFIX);
            sb57.append(str14);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier35, sb57.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier36 = UAObjectIdentifiers.dstu7624ofb_512;
            StringBuilder sb58 = new StringBuilder();
            sb58.append(PREFIX);
            sb58.append(str15);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier36, sb58.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier37 = UAObjectIdentifiers.dstu7624cfb_128;
            StringBuilder sb59 = new StringBuilder();
            sb59.append(PREFIX);
            sb59.append(str12);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier37, sb59.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier38 = UAObjectIdentifiers.dstu7624cfb_256;
            StringBuilder sb60 = new StringBuilder();
            sb60.append(PREFIX);
            sb60.append(str14);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier38, sb60.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier39 = UAObjectIdentifiers.dstu7624cfb_512;
            StringBuilder sb61 = new StringBuilder();
            sb61.append(PREFIX);
            sb61.append(str15);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier39, sb61.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier40 = UAObjectIdentifiers.dstu7624ctr_128;
            StringBuilder sb62 = new StringBuilder();
            sb62.append(PREFIX);
            sb62.append(str12);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier40, sb62.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier41 = UAObjectIdentifiers.dstu7624ctr_256;
            StringBuilder sb63 = new StringBuilder();
            sb63.append(PREFIX);
            sb63.append(str14);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier41, sb63.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier42 = UAObjectIdentifiers.dstu7624ctr_512;
            StringBuilder sb64 = new StringBuilder();
            sb64.append(PREFIX);
            sb64.append(str15);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier42, sb64.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier43 = UAObjectIdentifiers.dstu7624ccm_128;
            StringBuilder sb65 = new StringBuilder();
            sb65.append(PREFIX);
            sb65.append(str12);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier43, sb65.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier44 = UAObjectIdentifiers.dstu7624ccm_256;
            StringBuilder sb66 = new StringBuilder();
            sb66.append(PREFIX);
            sb66.append(str14);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier44, sb66.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier45 = UAObjectIdentifiers.dstu7624ccm_512;
            StringBuilder sb67 = new StringBuilder();
            sb67.append(PREFIX);
            sb67.append(str15);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier45, sb67.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier46 = UAObjectIdentifiers.dstu7624gmac_128;
            StringBuilder sb68 = new StringBuilder();
            sb68.append(PREFIX);
            sb68.append(str12);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier46, sb68.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier47 = UAObjectIdentifiers.dstu7624gmac_256;
            StringBuilder sb69 = new StringBuilder();
            sb69.append(PREFIX);
            sb69.append(str14);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier47, sb69.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier48 = UAObjectIdentifiers.dstu7624gmac_512;
            StringBuilder sb70 = new StringBuilder();
            sb70.append(PREFIX);
            sb70.append(str15);
            configurableProvider.addAlgorithm(str13, aSN1ObjectIdentifier48, sb70.toString());
        }
    }

    public static class OFB128 extends BaseBlockCipher {
        public OFB128() {
            super(new BufferedBlockCipher(new OFBBlockCipher(new DSTU7624Engine(128), 128)), 128);
        }
    }

    public static class OFB256 extends BaseBlockCipher {
        public OFB256() {
            super(new BufferedBlockCipher(new OFBBlockCipher(new DSTU7624Engine(256), 256)), 256);
        }
    }

    public static class OFB512 extends BaseBlockCipher {
        public OFB512() {
            super(new BufferedBlockCipher(new OFBBlockCipher(new DSTU7624Engine(512), 512)), 512);
        }
    }

    public static class Wrap extends BaseWrapCipher {
        public Wrap() {
            super(new DSTU7624WrapEngine(128));
        }
    }

    public static class Wrap128 extends BaseWrapCipher {
        public Wrap128() {
            super(new DSTU7624WrapEngine(128));
        }
    }

    public static class Wrap256 extends BaseWrapCipher {
        public Wrap256() {
            super(new DSTU7624WrapEngine(256));
        }
    }

    public static class Wrap512 extends BaseWrapCipher {
        public Wrap512() {
            super(new DSTU7624WrapEngine(512));
        }
    }

    private DSTU7624() {
    }
}
