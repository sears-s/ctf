package org.bouncycastle.tsp;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ess.SigningCertificate;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.Accuracy;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSAttributeTableGenerationException;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;

public class TimeStampTokenGenerator {
    public static final int R_MICROSECONDS = 2;
    public static final int R_MILLISECONDS = 3;
    public static final int R_SECONDS = 0;
    public static final int R_TENTHS_OF_SECONDS = 1;
    private int accuracyMicros;
    private int accuracyMillis;
    private int accuracySeconds;
    private List attrCerts;
    private List certs;
    private List crls;
    private Locale locale;
    boolean ordering;
    private Map otherRevoc;
    private int resolution;
    private SignerInfoGenerator signerInfoGen;
    GeneralName tsa;
    private ASN1ObjectIdentifier tsaPolicyOID;

    public TimeStampTokenGenerator(SignerInfoGenerator signerInfoGenerator, DigestCalculator digestCalculator, ASN1ObjectIdentifier aSN1ObjectIdentifier) throws IllegalArgumentException, TSPException {
        this(signerInfoGenerator, digestCalculator, aSN1ObjectIdentifier, false);
    }

    public TimeStampTokenGenerator(final SignerInfoGenerator signerInfoGenerator, DigestCalculator digestCalculator, ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean z) throws IllegalArgumentException, TSPException {
        SignerInfoGenerator signerInfoGenerator2;
        this.resolution = 0;
        IssuerSerial issuerSerial = null;
        this.locale = null;
        this.accuracySeconds = -1;
        this.accuracyMillis = -1;
        this.accuracyMicros = -1;
        this.ordering = false;
        this.tsa = null;
        this.certs = new ArrayList();
        this.crls = new ArrayList();
        this.attrCerts = new ArrayList();
        this.otherRevoc = new HashMap();
        this.signerInfoGen = signerInfoGenerator;
        this.tsaPolicyOID = aSN1ObjectIdentifier;
        if (signerInfoGenerator.hasAssociatedCertificate()) {
            X509CertificateHolder associatedCertificate = signerInfoGenerator.getAssociatedCertificate();
            TSPUtil.validateCertificate(associatedCertificate);
            try {
                OutputStream outputStream = digestCalculator.getOutputStream();
                outputStream.write(associatedCertificate.getEncoded());
                outputStream.close();
                if (digestCalculator.getAlgorithmIdentifier().getAlgorithm().equals(OIWObjectIdentifiers.idSHA1)) {
                    byte[] digest = digestCalculator.getDigest();
                    if (z) {
                        issuerSerial = new IssuerSerial(new GeneralNames(new GeneralName(associatedCertificate.getIssuer())), associatedCertificate.getSerialNumber());
                    }
                    final ESSCertID eSSCertID = new ESSCertID(digest, issuerSerial);
                    signerInfoGenerator2 = new SignerInfoGenerator(signerInfoGenerator, new CMSAttributeTableGenerator() {
                        public AttributeTable getAttributes(Map map) throws CMSAttributeTableGenerationException {
                            AttributeTable attributes = signerInfoGenerator.getSignedAttributeTableGenerator().getAttributes(map);
                            return attributes.get(PKCSObjectIdentifiers.id_aa_signingCertificate) == null ? attributes.add(PKCSObjectIdentifiers.id_aa_signingCertificate, new SigningCertificate(eSSCertID)) : attributes;
                        }
                    }, signerInfoGenerator.getUnsignedAttributeTableGenerator());
                } else {
                    AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(digestCalculator.getAlgorithmIdentifier().getAlgorithm());
                    byte[] digest2 = digestCalculator.getDigest();
                    if (z) {
                        issuerSerial = new IssuerSerial(new GeneralNames(new GeneralName(associatedCertificate.getIssuer())), new ASN1Integer(associatedCertificate.getSerialNumber()));
                    }
                    final ESSCertIDv2 eSSCertIDv2 = new ESSCertIDv2(algorithmIdentifier, digest2, issuerSerial);
                    signerInfoGenerator2 = new SignerInfoGenerator(signerInfoGenerator, new CMSAttributeTableGenerator() {
                        public AttributeTable getAttributes(Map map) throws CMSAttributeTableGenerationException {
                            AttributeTable attributes = signerInfoGenerator.getSignedAttributeTableGenerator().getAttributes(map);
                            return attributes.get(PKCSObjectIdentifiers.id_aa_signingCertificateV2) == null ? attributes.add(PKCSObjectIdentifiers.id_aa_signingCertificateV2, new SigningCertificateV2(eSSCertIDv2)) : attributes;
                        }
                    }, signerInfoGenerator.getUnsignedAttributeTableGenerator());
                }
                this.signerInfoGen = signerInfoGenerator2;
            } catch (IOException e) {
                throw new TSPException("Exception processing certificate.", e);
            }
        } else {
            throw new IllegalArgumentException("SignerInfoGenerator must have an associated certificate");
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0066, code lost:
        if (r1.length() > r4) goto L_0x0071;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x006f, code lost:
        if (r1.length() > r4) goto L_0x0071;
     */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0085 A[LOOP:0: B:21:0x0078->B:23:0x0085, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0095  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.bouncycastle.asn1.ASN1GeneralizedTime createGeneralizedTime(java.util.Date r6) throws org.bouncycastle.tsp.TSPException {
        /*
            r5 = this;
            java.util.Locale r0 = r5.locale
            java.lang.String r1 = "yyyyMMddHHmmss.SSS"
            if (r0 != 0) goto L_0x000c
            java.text.SimpleDateFormat r0 = new java.text.SimpleDateFormat
            r0.<init>(r1)
            goto L_0x0012
        L_0x000c:
            java.text.SimpleDateFormat r2 = new java.text.SimpleDateFormat
            r2.<init>(r1, r0)
            r0 = r2
        L_0x0012:
            java.util.SimpleTimeZone r1 = new java.util.SimpleTimeZone
            r2 = 0
            java.lang.String r3 = "Z"
            r1.<init>(r2, r3)
            r0.setTimeZone(r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            java.lang.String r6 = r0.format(r6)
            r1.<init>(r6)
            java.lang.String r6 = "."
            int r6 = r1.indexOf(r6)
            if (r6 >= 0) goto L_0x003b
            r1.append(r3)
            org.bouncycastle.asn1.ASN1GeneralizedTime r6 = new org.bouncycastle.asn1.ASN1GeneralizedTime
            java.lang.String r0 = r1.toString()
            r6.<init>(r0)
            return r6
        L_0x003b:
            int r0 = r5.resolution
            r2 = 1
            if (r0 == r2) goto L_0x0069
            r4 = 2
            if (r0 == r4) goto L_0x0060
            r4 = 3
            if (r0 != r4) goto L_0x0047
            goto L_0x0078
        L_0x0047:
            org.bouncycastle.tsp.TSPException r6 = new org.bouncycastle.tsp.TSPException
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "unknown time-stamp resolution: "
            r0.append(r1)
            int r1 = r5.resolution
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r6.<init>(r0)
            throw r6
        L_0x0060:
            int r0 = r1.length()
            int r4 = r6 + 3
            if (r0 <= r4) goto L_0x0078
            goto L_0x0071
        L_0x0069:
            int r0 = r1.length()
            int r4 = r6 + 2
            if (r0 <= r4) goto L_0x0078
        L_0x0071:
            int r0 = r1.length()
            r1.delete(r4, r0)
        L_0x0078:
            int r0 = r1.length()
            int r0 = r0 - r2
            char r0 = r1.charAt(r0)
            r4 = 48
            if (r0 != r4) goto L_0x008e
            int r0 = r1.length()
            int r0 = r0 - r2
            r1.deleteCharAt(r0)
            goto L_0x0078
        L_0x008e:
            int r0 = r1.length()
            int r0 = r0 - r2
            if (r0 != r6) goto L_0x009d
            int r6 = r1.length()
            int r6 = r6 - r2
            r1.deleteCharAt(r6)
        L_0x009d:
            r1.append(r3)
            org.bouncycastle.asn1.ASN1GeneralizedTime r6 = new org.bouncycastle.asn1.ASN1GeneralizedTime
            java.lang.String r0 = r1.toString()
            r6.<init>(r0)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.tsp.TimeStampTokenGenerator.createGeneralizedTime(java.util.Date):org.bouncycastle.asn1.ASN1GeneralizedTime");
    }

    public void addAttributeCertificates(Store store) {
        this.attrCerts.addAll(store.getMatches(null));
    }

    public void addCRLs(Store store) {
        this.crls.addAll(store.getMatches(null));
    }

    public void addCertificates(Store store) {
        this.certs.addAll(store.getMatches(null));
    }

    public void addOtherRevocationInfo(ASN1ObjectIdentifier aSN1ObjectIdentifier, Store store) {
        this.otherRevoc.put(aSN1ObjectIdentifier, store.getMatches(null));
    }

    public TimeStampToken generate(TimeStampRequest timeStampRequest, BigInteger bigInteger, Date date) throws TSPException {
        return generate(timeStampRequest, bigInteger, date, null);
    }

    public TimeStampToken generate(TimeStampRequest timeStampRequest, BigInteger bigInteger, Date date, Extensions extensions) throws TSPException {
        Accuracy accuracy;
        Extensions extensions2;
        ASN1GeneralizedTime aSN1GeneralizedTime;
        Date date2 = date;
        Extensions extensions3 = extensions;
        MessageImprint messageImprint = new MessageImprint(new AlgorithmIdentifier(timeStampRequest.getMessageImprintAlgOID(), DERNull.INSTANCE), timeStampRequest.getMessageImprintDigest());
        if (this.accuracySeconds > 0 || this.accuracyMillis > 0 || this.accuracyMicros > 0) {
            int i = this.accuracySeconds;
            ASN1Integer aSN1Integer = i > 0 ? new ASN1Integer((long) i) : null;
            int i2 = this.accuracyMillis;
            ASN1Integer aSN1Integer2 = i2 > 0 ? new ASN1Integer((long) i2) : null;
            int i3 = this.accuracyMicros;
            accuracy = new Accuracy(aSN1Integer, aSN1Integer2, i3 > 0 ? new ASN1Integer((long) i3) : null);
        } else {
            accuracy = null;
        }
        boolean z = this.ordering;
        ASN1Boolean instance = z ? ASN1Boolean.getInstance(z) : null;
        ASN1Integer aSN1Integer3 = timeStampRequest.getNonce() != null ? new ASN1Integer(timeStampRequest.getNonce()) : null;
        ASN1ObjectIdentifier aSN1ObjectIdentifier = this.tsaPolicyOID;
        if (timeStampRequest.getReqPolicy() != null) {
            aSN1ObjectIdentifier = timeStampRequest.getReqPolicy();
        }
        ASN1ObjectIdentifier aSN1ObjectIdentifier2 = aSN1ObjectIdentifier;
        Extensions extensions4 = timeStampRequest.getExtensions();
        if (extensions3 != null) {
            ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
            if (extensions4 != null) {
                Enumeration oids = extensions4.oids();
                while (oids.hasMoreElements()) {
                    extensionsGenerator.addExtension(extensions4.getExtension(ASN1ObjectIdentifier.getInstance(oids.nextElement())));
                }
            }
            Enumeration oids2 = extensions.oids();
            while (oids2.hasMoreElements()) {
                extensionsGenerator.addExtension(extensions3.getExtension(ASN1ObjectIdentifier.getInstance(oids2.nextElement())));
            }
            extensions2 = extensionsGenerator.generate();
        } else {
            extensions2 = extensions4;
        }
        if (this.resolution == 0) {
            Locale locale2 = this.locale;
            aSN1GeneralizedTime = locale2 == null ? new ASN1GeneralizedTime(date2) : new ASN1GeneralizedTime(date2, locale2);
        } else {
            aSN1GeneralizedTime = createGeneralizedTime(date2);
        }
        TSTInfo tSTInfo = new TSTInfo(aSN1ObjectIdentifier2, messageImprint, new ASN1Integer(bigInteger), aSN1GeneralizedTime, accuracy, instance, aSN1Integer3, this.tsa, extensions2);
        try {
            CMSSignedDataGenerator cMSSignedDataGenerator = new CMSSignedDataGenerator();
            if (timeStampRequest.getCertReq()) {
                cMSSignedDataGenerator.addCertificates(new CollectionStore(this.certs));
                cMSSignedDataGenerator.addAttributeCertificates(new CollectionStore(this.attrCerts));
            }
            cMSSignedDataGenerator.addCRLs(new CollectionStore(this.crls));
            if (!this.otherRevoc.isEmpty()) {
                for (ASN1ObjectIdentifier aSN1ObjectIdentifier3 : this.otherRevoc.keySet()) {
                    cMSSignedDataGenerator.addOtherRevocationInfo(aSN1ObjectIdentifier3, (Store) new CollectionStore((Collection) this.otherRevoc.get(aSN1ObjectIdentifier3)));
                }
            }
            cMSSignedDataGenerator.addSignerInfoGenerator(this.signerInfoGen);
            return new TimeStampToken(cMSSignedDataGenerator.generate(new CMSProcessableByteArray(PKCSObjectIdentifiers.id_ct_TSTInfo, tSTInfo.getEncoded(ASN1Encoding.DER)), true));
        } catch (CMSException e) {
            throw new TSPException("Error generating time-stamp token", e);
        } catch (IOException e2) {
            throw new TSPException("Exception encoding info", e2);
        }
    }

    public void setAccuracyMicros(int i) {
        this.accuracyMicros = i;
    }

    public void setAccuracyMillis(int i) {
        this.accuracyMillis = i;
    }

    public void setAccuracySeconds(int i) {
        this.accuracySeconds = i;
    }

    public void setLocale(Locale locale2) {
        this.locale = locale2;
    }

    public void setOrdering(boolean z) {
        this.ordering = z;
    }

    public void setResolution(int i) {
        this.resolution = i;
    }

    public void setTSA(GeneralName generalName) {
        this.tsa = generalName;
    }
}
