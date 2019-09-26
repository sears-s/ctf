package org.bouncycastle.tsp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Integers;
import org.jivesoftware.smack.util.StringUtils;

public class TSPUtil {
    private static List EMPTY_LIST = Collections.unmodifiableList(new ArrayList());
    private static final Map digestLengths = new HashMap();
    private static final Map digestNames = new HashMap();

    static {
        digestLengths.put(PKCSObjectIdentifiers.md5.getId(), Integers.valueOf(16));
        digestLengths.put(OIWObjectIdentifiers.idSHA1.getId(), Integers.valueOf(20));
        digestLengths.put(NISTObjectIdentifiers.id_sha224.getId(), Integers.valueOf(28));
        digestLengths.put(NISTObjectIdentifiers.id_sha256.getId(), Integers.valueOf(32));
        digestLengths.put(NISTObjectIdentifiers.id_sha384.getId(), Integers.valueOf(48));
        digestLengths.put(NISTObjectIdentifiers.id_sha512.getId(), Integers.valueOf(64));
        digestLengths.put(TeleTrusTObjectIdentifiers.ripemd128.getId(), Integers.valueOf(16));
        digestLengths.put(TeleTrusTObjectIdentifiers.ripemd160.getId(), Integers.valueOf(20));
        digestLengths.put(TeleTrusTObjectIdentifiers.ripemd256.getId(), Integers.valueOf(32));
        digestLengths.put(CryptoProObjectIdentifiers.gostR3411.getId(), Integers.valueOf(32));
        digestLengths.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256.getId(), Integers.valueOf(32));
        digestLengths.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512.getId(), Integers.valueOf(64));
        digestLengths.put(GMObjectIdentifiers.sm3.getId(), Integers.valueOf(32));
        digestNames.put(PKCSObjectIdentifiers.md5.getId(), StringUtils.MD5);
        String str = "SHA1";
        digestNames.put(OIWObjectIdentifiers.idSHA1.getId(), str);
        String str2 = "SHA224";
        digestNames.put(NISTObjectIdentifiers.id_sha224.getId(), str2);
        String str3 = "SHA256";
        digestNames.put(NISTObjectIdentifiers.id_sha256.getId(), str3);
        String str4 = "SHA384";
        digestNames.put(NISTObjectIdentifiers.id_sha384.getId(), str4);
        String str5 = "SHA512";
        digestNames.put(NISTObjectIdentifiers.id_sha512.getId(), str5);
        digestNames.put(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId(), str);
        digestNames.put(PKCSObjectIdentifiers.sha224WithRSAEncryption.getId(), str2);
        digestNames.put(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId(), str3);
        digestNames.put(PKCSObjectIdentifiers.sha384WithRSAEncryption.getId(), str4);
        digestNames.put(PKCSObjectIdentifiers.sha512WithRSAEncryption.getId(), str5);
        digestNames.put(TeleTrusTObjectIdentifiers.ripemd128.getId(), "RIPEMD128");
        digestNames.put(TeleTrusTObjectIdentifiers.ripemd160.getId(), "RIPEMD160");
        digestNames.put(TeleTrusTObjectIdentifiers.ripemd256.getId(), "RIPEMD256");
        digestNames.put(CryptoProObjectIdentifiers.gostR3411.getId(), "GOST3411");
        digestNames.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256.getId(), "GOST3411-2012-256");
        digestNames.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512.getId(), "GOST3411-2012-512");
        digestNames.put(GMObjectIdentifiers.sm3.getId(), "SM3");
    }

    static void addExtension(ExtensionsGenerator extensionsGenerator, ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean z, ASN1Encodable aSN1Encodable) throws TSPIOException {
        try {
            extensionsGenerator.addExtension(aSN1ObjectIdentifier, z, aSN1Encodable);
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("cannot encode extension: ");
            sb.append(e.getMessage());
            throw new TSPIOException(sb.toString(), e);
        }
    }

    static int getDigestLength(String str) throws TSPException {
        Integer num = (Integer) digestLengths.get(str);
        if (num != null) {
            return num.intValue();
        }
        throw new TSPException("digest algorithm cannot be found.");
    }

    static List getExtensionOIDs(Extensions extensions) {
        return extensions == null ? EMPTY_LIST : Collections.unmodifiableList(Arrays.asList(extensions.getExtensionOIDs()));
    }

    public static Collection getSignatureTimestamps(SignerInformation signerInformation, DigestCalculatorProvider digestCalculatorProvider) throws TSPValidationException {
        ArrayList arrayList = new ArrayList();
        AttributeTable unsignedAttributes = signerInformation.getUnsignedAttributes();
        if (unsignedAttributes != null) {
            ASN1EncodableVector all = unsignedAttributes.getAll(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);
            for (int i = 0; i < all.size(); i++) {
                ASN1Set attrValues = ((Attribute) all.get(i)).getAttrValues();
                int i2 = 0;
                while (i2 < attrValues.size()) {
                    try {
                        TimeStampToken timeStampToken = new TimeStampToken(ContentInfo.getInstance(attrValues.getObjectAt(i2)));
                        TimeStampTokenInfo timeStampInfo = timeStampToken.getTimeStampInfo();
                        DigestCalculator digestCalculator = digestCalculatorProvider.get(timeStampInfo.getHashAlgorithm());
                        OutputStream outputStream = digestCalculator.getOutputStream();
                        outputStream.write(signerInformation.getSignature());
                        outputStream.close();
                        if (org.bouncycastle.util.Arrays.constantTimeAreEqual(digestCalculator.getDigest(), timeStampInfo.getMessageImprintDigest())) {
                            arrayList.add(timeStampToken);
                            i2++;
                        } else {
                            throw new TSPValidationException("Incorrect digest in message imprint");
                        }
                    } catch (OperatorCreationException e) {
                        throw new TSPValidationException("Unknown hash algorithm specified in timestamp");
                    } catch (Exception e2) {
                        throw new TSPValidationException("Timestamp could not be parsed");
                    }
                }
            }
        }
        return arrayList;
    }

    public static void validateCertificate(X509CertificateHolder x509CertificateHolder) throws TSPValidationException {
        String str = "Certificate must have an ExtendedKeyUsage extension.";
        if (x509CertificateHolder.toASN1Structure().getVersionNumber() == 3) {
            Extension extension = x509CertificateHolder.getExtension(Extension.extendedKeyUsage);
            if (extension == null) {
                throw new TSPValidationException(str);
            } else if (extension.isCritical()) {
                ExtendedKeyUsage instance = ExtendedKeyUsage.getInstance(extension.getParsedValue());
                if (!instance.hasKeyPurposeId(KeyPurposeId.id_kp_timeStamping) || instance.size() != 1) {
                    throw new TSPValidationException("ExtendedKeyUsage not solely time stamping.");
                }
            } else {
                throw new TSPValidationException("Certificate must have an ExtendedKeyUsage extension marked as critical.");
            }
        } else {
            throw new IllegalArgumentException(str);
        }
    }
}
