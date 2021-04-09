package org.bouncycastle.jce.provider;

import android.support.v4.os.EnvironmentCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.PolicyQualifierInfo;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.isismtt.ISISMTTObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.jcajce.PKIXCertStore;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXCertStoreSelector.Builder;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;
import org.bouncycastle.x509.X509AttributeCertificate;

class CertPathValidatorUtilities {
    protected static final String ANY_POLICY = "2.5.29.32.0";
    protected static final String AUTHORITY_KEY_IDENTIFIER = Extension.authorityKeyIdentifier.getId();
    protected static final String BASIC_CONSTRAINTS = Extension.basicConstraints.getId();
    protected static final String CERTIFICATE_POLICIES = Extension.certificatePolicies.getId();
    protected static final String CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
    protected static final String CRL_NUMBER = Extension.cRLNumber.getId();
    protected static final int CRL_SIGN = 6;
    protected static final PKIXCRLUtil CRL_UTIL = new PKIXCRLUtil();
    protected static final String DELTA_CRL_INDICATOR = Extension.deltaCRLIndicator.getId();
    protected static final String FRESHEST_CRL = Extension.freshestCRL.getId();
    protected static final String INHIBIT_ANY_POLICY = Extension.inhibitAnyPolicy.getId();
    protected static final String ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();
    protected static final int KEY_CERT_SIGN = 5;
    protected static final String KEY_USAGE = Extension.keyUsage.getId();
    protected static final String NAME_CONSTRAINTS = Extension.nameConstraints.getId();
    protected static final String POLICY_CONSTRAINTS = Extension.policyConstraints.getId();
    protected static final String POLICY_MAPPINGS = Extension.policyMappings.getId();
    protected static final String SUBJECT_ALTERNATIVE_NAME = Extension.subjectAlternativeName.getId();
    protected static final String[] crlReasons = {"unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", EnvironmentCompat.MEDIA_UNKNOWN, "removeFromCRL", "privilegeWithdrawn", "aACompromise"};

    CertPathValidatorUtilities() {
    }

    static void checkCRLsNotEmpty(Set set, Object obj) throws AnnotatedException {
        if (set.isEmpty()) {
            String str = "\"";
            String str2 = "No CRLs found for issuer \"";
            if (obj instanceof X509AttributeCertificate) {
                X509AttributeCertificate x509AttributeCertificate = (X509AttributeCertificate) obj;
                StringBuilder sb = new StringBuilder();
                sb.append(str2);
                sb.append(x509AttributeCertificate.getIssuer().getPrincipals()[0]);
                sb.append(str);
                throw new AnnotatedException(sb.toString());
            }
            X509Certificate x509Certificate = (X509Certificate) obj;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str2);
            sb2.append(RFC4519Style.INSTANCE.toString(PrincipalUtils.getIssuerPrincipal(x509Certificate)));
            sb2.append(str);
            throw new AnnotatedException(sb2.toString());
        }
    }

    protected static Collection findCertificates(PKIXCertStoreSelector pKIXCertStoreSelector, List list) throws AnnotatedException {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (Object next : list) {
            if (next instanceof Store) {
                try {
                    linkedHashSet.addAll(((Store) next).getMatches(pKIXCertStoreSelector));
                } catch (StoreException e) {
                    throw new AnnotatedException("Problem while picking certificates from X.509 store.", e);
                }
            } else {
                try {
                    linkedHashSet.addAll(PKIXCertStoreSelector.getCertificates(pKIXCertStoreSelector, (CertStore) next));
                } catch (CertStoreException e2) {
                    throw new AnnotatedException("Problem while picking certificates from certificate store.", e2);
                }
            }
        }
        return linkedHashSet;
    }

    static Collection findIssuerCerts(X509Certificate x509Certificate, List<CertStore> list, List<PKIXCertStore> list2) throws AnnotatedException {
        X509CertSelector x509CertSelector = new X509CertSelector();
        try {
            x509CertSelector.setSubject(PrincipalUtils.getIssuerPrincipal(x509Certificate).getEncoded());
            try {
                byte[] extensionValue = x509Certificate.getExtensionValue(AUTHORITY_KEY_IDENTIFIER);
                if (extensionValue != null) {
                    byte[] keyIdentifier = AuthorityKeyIdentifier.getInstance(ASN1OctetString.getInstance(extensionValue).getOctets()).getKeyIdentifier();
                    if (keyIdentifier != null) {
                        x509CertSelector.setSubjectKeyIdentifier(new DEROctetString(keyIdentifier).getEncoded());
                    }
                }
            } catch (Exception e) {
            }
            PKIXCertStoreSelector build = new Builder(x509CertSelector).build();
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            try {
                ArrayList<X509Certificate> arrayList = new ArrayList<>();
                arrayList.addAll(findCertificates(build, list));
                arrayList.addAll(findCertificates(build, list2));
                for (X509Certificate add : arrayList) {
                    linkedHashSet.add(add);
                }
                return linkedHashSet;
            } catch (AnnotatedException e2) {
                throw new AnnotatedException("Issuer certificate cannot be searched.", e2);
            }
        } catch (IOException e3) {
            throw new AnnotatedException("Subject criteria for certificate selector to find issuer certificate could not be set.", e3);
        }
    }

    protected static TrustAnchor findTrustAnchor(X509Certificate x509Certificate, Set set) throws AnnotatedException {
        return findTrustAnchor(x509Certificate, set, null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x005e A[SYNTHETIC, Splitter:B:23:0x005e] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0018 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static java.security.cert.TrustAnchor findTrustAnchor(java.security.cert.X509Certificate r7, java.util.Set r8, java.lang.String r9) throws org.bouncycastle.jce.provider.AnnotatedException {
        /*
            java.security.cert.X509CertSelector r0 = new java.security.cert.X509CertSelector
            r0.<init>()
            org.bouncycastle.asn1.x500.X500Name r1 = org.bouncycastle.jce.provider.PrincipalUtils.getEncodedIssuerPrincipal(r7)
            byte[] r2 = r1.getEncoded()     // Catch:{ IOException -> 0x0074 }
            r0.setSubject(r2)     // Catch:{ IOException -> 0x0074 }
            java.util.Iterator r8 = r8.iterator()
            r2 = 0
            r3 = r2
            r4 = r3
            r5 = r4
        L_0x0018:
            boolean r6 = r8.hasNext()
            if (r6 == 0) goto L_0x0066
            if (r3 != 0) goto L_0x0066
            java.lang.Object r3 = r8.next()
            java.security.cert.TrustAnchor r3 = (java.security.cert.TrustAnchor) r3
            java.security.cert.X509Certificate r6 = r3.getTrustedCert()
            if (r6 == 0) goto L_0x003f
            java.security.cert.X509Certificate r6 = r3.getTrustedCert()
            boolean r6 = r0.match(r6)
            if (r6 == 0) goto L_0x005b
            java.security.cert.X509Certificate r5 = r3.getTrustedCert()
            java.security.PublicKey r5 = r5.getPublicKey()
            goto L_0x005c
        L_0x003f:
            java.lang.String r6 = r3.getCAName()
            if (r6 == 0) goto L_0x005b
            java.security.PublicKey r6 = r3.getCAPublicKey()
            if (r6 == 0) goto L_0x005b
            org.bouncycastle.asn1.x500.X500Name r6 = org.bouncycastle.jce.provider.PrincipalUtils.getCA(r3)     // Catch:{ IllegalArgumentException -> 0x005a }
            boolean r6 = r1.equals(r6)     // Catch:{ IllegalArgumentException -> 0x005a }
            if (r6 == 0) goto L_0x005b
            java.security.PublicKey r5 = r3.getCAPublicKey()     // Catch:{ IllegalArgumentException -> 0x005a }
            goto L_0x005c
        L_0x005a:
            r3 = move-exception
        L_0x005b:
            r3 = r2
        L_0x005c:
            if (r5 == 0) goto L_0x0018
            verifyX509Certificate(r7, r5, r9)     // Catch:{ Exception -> 0x0062 }
            goto L_0x0018
        L_0x0062:
            r4 = move-exception
            r3 = r2
            r5 = r3
            goto L_0x0018
        L_0x0066:
            if (r3 != 0) goto L_0x0073
            if (r4 != 0) goto L_0x006b
            goto L_0x0073
        L_0x006b:
            org.bouncycastle.jce.provider.AnnotatedException r7 = new org.bouncycastle.jce.provider.AnnotatedException
            java.lang.String r8 = "TrustAnchor found but certificate validation failed."
            r7.<init>(r8, r4)
            throw r7
        L_0x0073:
            return r3
        L_0x0074:
            r7 = move-exception
            org.bouncycastle.jce.provider.AnnotatedException r8 = new org.bouncycastle.jce.provider.AnnotatedException
            java.lang.String r9 = "Cannot set subject search criteria for trust anchor."
            r8.<init>(r9, r7)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jce.provider.CertPathValidatorUtilities.findTrustAnchor(java.security.cert.X509Certificate, java.util.Set, java.lang.String):java.security.cert.TrustAnchor");
    }

    static List<PKIXCertStore> getAdditionalStoresFromAltNames(byte[] bArr, Map<GeneralName, PKIXCertStore> map) throws CertificateParsingException {
        if (bArr == null) {
            return Collections.EMPTY_LIST;
        }
        GeneralName[] names = GeneralNames.getInstance(ASN1OctetString.getInstance(bArr).getOctets()).getNames();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i != names.length; i++) {
            PKIXCertStore pKIXCertStore = (PKIXCertStore) map.get(names[i]);
            if (pKIXCertStore != null) {
                arrayList.add(pKIXCertStore);
            }
        }
        return arrayList;
    }

    static List<PKIXCRLStore> getAdditionalStoresFromCRLDistributionPoint(CRLDistPoint cRLDistPoint, Map<GeneralName, PKIXCRLStore> map) throws AnnotatedException {
        if (cRLDistPoint == null) {
            return Collections.EMPTY_LIST;
        }
        try {
            DistributionPoint[] distributionPoints = cRLDistPoint.getDistributionPoints();
            ArrayList arrayList = new ArrayList();
            for (DistributionPoint distributionPoint : distributionPoints) {
                DistributionPointName distributionPoint2 = distributionPoint.getDistributionPoint();
                if (distributionPoint2 != null && distributionPoint2.getType() == 0) {
                    GeneralName[] names = GeneralNames.getInstance(distributionPoint2.getName()).getNames();
                    for (GeneralName generalName : names) {
                        PKIXCRLStore pKIXCRLStore = (PKIXCRLStore) map.get(generalName);
                        if (pKIXCRLStore != null) {
                            arrayList.add(pKIXCRLStore);
                        }
                    }
                }
            }
            return arrayList;
        } catch (Exception e) {
            throw new AnnotatedException("Distribution points could not be read.", e);
        }
    }

    protected static AlgorithmIdentifier getAlgorithmIdentifier(PublicKey publicKey) throws CertPathValidatorException {
        try {
            return SubjectPublicKeyInfo.getInstance(new ASN1InputStream(publicKey.getEncoded()).readObject()).getAlgorithm();
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Subject public key cannot be decoded.", e);
        }
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Collection, code=java.util.Collection<java.lang.Object>, for r4v0, types: [java.util.Collection<java.lang.Object>, java.util.Collection] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static void getCRLIssuersFromDistributionPoint(org.bouncycastle.asn1.x509.DistributionPoint r3, java.util.Collection<java.lang.Object> r4, java.security.cert.X509CRLSelector r5) throws org.bouncycastle.jce.provider.AnnotatedException {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            org.bouncycastle.asn1.x509.GeneralNames r1 = r3.getCRLIssuer()
            if (r1 == 0) goto L_0x0042
            org.bouncycastle.asn1.x509.GeneralNames r3 = r3.getCRLIssuer()
            org.bouncycastle.asn1.x509.GeneralName[] r3 = r3.getNames()
            r4 = 0
        L_0x0014:
            int r1 = r3.length
            if (r4 >= r1) goto L_0x005a
            r1 = r3[r4]
            int r1 = r1.getTagNo()
            r2 = 4
            if (r1 != r2) goto L_0x003f
            r1 = r3[r4]     // Catch:{ IOException -> 0x0036 }
            org.bouncycastle.asn1.ASN1Encodable r1 = r1.getName()     // Catch:{ IOException -> 0x0036 }
            org.bouncycastle.asn1.ASN1Primitive r1 = r1.toASN1Primitive()     // Catch:{ IOException -> 0x0036 }
            byte[] r1 = r1.getEncoded()     // Catch:{ IOException -> 0x0036 }
            org.bouncycastle.asn1.x500.X500Name r1 = org.bouncycastle.asn1.x500.X500Name.getInstance(r1)     // Catch:{ IOException -> 0x0036 }
            r0.add(r1)     // Catch:{ IOException -> 0x0036 }
            goto L_0x003f
        L_0x0036:
            r3 = move-exception
            org.bouncycastle.jce.provider.AnnotatedException r4 = new org.bouncycastle.jce.provider.AnnotatedException
            java.lang.String r5 = "CRL issuer information from distribution point cannot be decoded."
            r4.<init>(r5, r3)
            throw r4
        L_0x003f:
            int r4 = r4 + 1
            goto L_0x0014
        L_0x0042:
            org.bouncycastle.asn1.x509.DistributionPointName r3 = r3.getDistributionPoint()
            if (r3 == 0) goto L_0x007c
            java.util.Iterator r3 = r4.iterator()
        L_0x004c:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x005a
            java.lang.Object r4 = r3.next()
            r0.add(r4)
            goto L_0x004c
        L_0x005a:
            java.util.Iterator r3 = r0.iterator()
        L_0x005e:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x007b
            java.lang.Object r4 = r3.next()     // Catch:{ IOException -> 0x0072 }
            org.bouncycastle.asn1.x500.X500Name r4 = (org.bouncycastle.asn1.x500.X500Name) r4     // Catch:{ IOException -> 0x0072 }
            byte[] r4 = r4.getEncoded()     // Catch:{ IOException -> 0x0072 }
            r5.addIssuerName(r4)     // Catch:{ IOException -> 0x0072 }
            goto L_0x005e
        L_0x0072:
            r3 = move-exception
            org.bouncycastle.jce.provider.AnnotatedException r4 = new org.bouncycastle.jce.provider.AnnotatedException
            java.lang.String r5 = "Cannot decode CRL issuer information."
            r4.<init>(r5, r3)
            throw r4
        L_0x007b:
            return
        L_0x007c:
            org.bouncycastle.jce.provider.AnnotatedException r3 = new org.bouncycastle.jce.provider.AnnotatedException
            java.lang.String r4 = "CRL issuer is omitted from distribution point but no distributionPoint field present."
            r3.<init>(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jce.provider.CertPathValidatorUtilities.getCRLIssuersFromDistributionPoint(org.bouncycastle.asn1.x509.DistributionPoint, java.util.Collection, java.security.cert.X509CRLSelector):void");
    }

    protected static void getCertStatus(Date date, X509CRL x509crl, Object obj, CertStatus certStatus) throws AnnotatedException {
        X509CRLEntry x509CRLEntry;
        try {
            if (X509CRLObject.isIndirectCRL(x509crl)) {
                x509CRLEntry = x509crl.getRevokedCertificate(getSerialNumber(obj));
                if (x509CRLEntry != null) {
                    X500Principal certificateIssuer = x509CRLEntry.getCertificateIssuer();
                    if (!PrincipalUtils.getEncodedIssuerPrincipal(obj).equals(certificateIssuer == null ? PrincipalUtils.getIssuerPrincipal(x509crl) : X500Name.getInstance(certificateIssuer.getEncoded()))) {
                        return;
                    }
                } else {
                    return;
                }
            } else if (PrincipalUtils.getEncodedIssuerPrincipal(obj).equals(PrincipalUtils.getIssuerPrincipal(x509crl))) {
                x509CRLEntry = x509crl.getRevokedCertificate(getSerialNumber(obj));
                if (x509CRLEntry == null) {
                    return;
                }
            } else {
                return;
            }
            ASN1Enumerated aSN1Enumerated = null;
            if (x509CRLEntry.hasExtensions()) {
                try {
                    aSN1Enumerated = ASN1Enumerated.getInstance(getExtensionValue(x509CRLEntry, Extension.reasonCode.getId()));
                } catch (Exception e) {
                    throw new AnnotatedException("Reason code CRL entry extension could not be decoded.", e);
                }
            }
            if (date.getTime() >= x509CRLEntry.getRevocationDate().getTime() || aSN1Enumerated == null || aSN1Enumerated.getValue().intValue() == 0 || aSN1Enumerated.getValue().intValue() == 1 || aSN1Enumerated.getValue().intValue() == 2 || aSN1Enumerated.getValue().intValue() == 8) {
                certStatus.setCertStatus(aSN1Enumerated != null ? aSN1Enumerated.getValue().intValue() : 0);
                certStatus.setRevocationDate(x509CRLEntry.getRevocationDate());
            }
        } catch (CRLException e2) {
            throw new AnnotatedException("Failed check for indirect CRL.", e2);
        }
    }

    protected static Set getCompleteCRLs(DistributionPoint distributionPoint, Object obj, Date date, PKIXExtendedParameters pKIXExtendedParameters) throws AnnotatedException {
        X509CRLSelector x509CRLSelector = new X509CRLSelector();
        try {
            HashSet hashSet = new HashSet();
            hashSet.add(PrincipalUtils.getEncodedIssuerPrincipal(obj));
            getCRLIssuersFromDistributionPoint(distributionPoint, hashSet, x509CRLSelector);
            if (obj instanceof X509Certificate) {
                x509CRLSelector.setCertificateChecking((X509Certificate) obj);
            }
            PKIXCRLStoreSelector build = new PKIXCRLStoreSelector.Builder(x509CRLSelector).setCompleteCRLEnabled(true).build();
            if (pKIXExtendedParameters.getDate() != null) {
                date = pKIXExtendedParameters.getDate();
            }
            Set findCRLs = CRL_UTIL.findCRLs(build, date, pKIXExtendedParameters.getCertStores(), pKIXExtendedParameters.getCRLStores());
            checkCRLsNotEmpty(findCRLs, obj);
            return findCRLs;
        } catch (AnnotatedException e) {
            throw new AnnotatedException("Could not get issuer information from distribution point.", e);
        }
    }

    protected static Set getDeltaCRLs(Date date, X509CRL x509crl, List<CertStore> list, List<PKIXCRLStore> list2) throws AnnotatedException {
        X509CRLSelector x509CRLSelector = new X509CRLSelector();
        try {
            x509CRLSelector.addIssuerName(PrincipalUtils.getIssuerPrincipal(x509crl).getEncoded());
            try {
                ASN1Primitive extensionValue = getExtensionValue(x509crl, CRL_NUMBER);
                BigInteger bigInteger = null;
                BigInteger positiveValue = extensionValue != null ? ASN1Integer.getInstance(extensionValue).getPositiveValue() : null;
                try {
                    byte[] extensionValue2 = x509crl.getExtensionValue(ISSUING_DISTRIBUTION_POINT);
                    if (positiveValue != null) {
                        bigInteger = positiveValue.add(BigInteger.valueOf(1));
                    }
                    x509CRLSelector.setMinCRLNumber(bigInteger);
                    PKIXCRLStoreSelector.Builder builder = new PKIXCRLStoreSelector.Builder(x509CRLSelector);
                    builder.setIssuingDistributionPoint(extensionValue2);
                    builder.setIssuingDistributionPointEnabled(true);
                    builder.setMaxBaseCRLNumber(positiveValue);
                    Set<X509CRL> findCRLs = CRL_UTIL.findCRLs(builder.build(), date, list, list2);
                    HashSet hashSet = new HashSet();
                    for (X509CRL x509crl2 : findCRLs) {
                        if (isDeltaCRL(x509crl2)) {
                            hashSet.add(x509crl2);
                        }
                    }
                    return hashSet;
                } catch (Exception e) {
                    throw new AnnotatedException("Issuing distribution point extension value could not be read.", e);
                }
            } catch (Exception e2) {
                throw new AnnotatedException("CRL number extension could not be extracted from CRL.", e2);
            }
        } catch (IOException e3) {
            throw new AnnotatedException("Cannot extract issuer from CRL.", e3);
        }
    }

    protected static ASN1Primitive getExtensionValue(X509Extension x509Extension, String str) throws AnnotatedException {
        byte[] extensionValue = x509Extension.getExtensionValue(str);
        if (extensionValue == null) {
            return null;
        }
        return getObject(str, extensionValue);
    }

    protected static PublicKey getNextWorkingKey(List list, int i, JcaJceHelper jcaJceHelper) throws CertPathValidatorException {
        DSAPublicKey dSAPublicKey;
        PublicKey publicKey = ((Certificate) list.get(i)).getPublicKey();
        if (!(publicKey instanceof DSAPublicKey)) {
            return publicKey;
        }
        DSAPublicKey dSAPublicKey2 = (DSAPublicKey) publicKey;
        if (dSAPublicKey2.getParams() != null) {
            return dSAPublicKey2;
        }
        do {
            i++;
            String str = "DSA parameters cannot be inherited from previous certificate.";
            if (i < list.size()) {
                PublicKey publicKey2 = ((X509Certificate) list.get(i)).getPublicKey();
                if (publicKey2 instanceof DSAPublicKey) {
                    dSAPublicKey = (DSAPublicKey) publicKey2;
                } else {
                    throw new CertPathValidatorException(str);
                }
            } else {
                throw new CertPathValidatorException(str);
            }
        } while (dSAPublicKey.getParams() == null);
        DSAParams params = dSAPublicKey.getParams();
        try {
            return jcaJceHelper.createKeyFactory("DSA").generatePublic(new DSAPublicKeySpec(dSAPublicKey2.getY(), params.getP(), params.getQ(), params.getG()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static ASN1Primitive getObject(String str, byte[] bArr) throws AnnotatedException {
        try {
            return new ASN1InputStream(((ASN1OctetString) new ASN1InputStream(bArr).readObject()).getOctets()).readObject();
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("exception processing extension ");
            sb.append(str);
            throw new AnnotatedException(sb.toString(), e);
        }
    }

    protected static final Set getQualifierSet(ASN1Sequence aSN1Sequence) throws CertPathValidatorException {
        HashSet hashSet = new HashSet();
        if (aSN1Sequence == null) {
            return hashSet;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ASN1OutputStream aSN1OutputStream = new ASN1OutputStream(byteArrayOutputStream);
        Enumeration objects = aSN1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            try {
                aSN1OutputStream.writeObject((ASN1Encodable) objects.nextElement());
                hashSet.add(new PolicyQualifierInfo(byteArrayOutputStream.toByteArray()));
                byteArrayOutputStream.reset();
            } catch (IOException e) {
                throw new ExtCertPathValidatorException("Policy qualifier info cannot be decoded.", e);
            }
        }
        return hashSet;
    }

    private static BigInteger getSerialNumber(Object obj) {
        return ((X509Certificate) obj).getSerialNumber();
    }

    protected static Date getValidCertDateFromValidityModel(PKIXExtendedParameters pKIXExtendedParameters, CertPath certPath, int i) throws AnnotatedException {
        String str = "Date of cert gen extension could not be read.";
        if (pKIXExtendedParameters.getValidityModel() != 1) {
            return getValidDate(pKIXExtendedParameters);
        }
        if (i <= 0) {
            return getValidDate(pKIXExtendedParameters);
        }
        int i2 = i - 1;
        if (i2 == 0) {
            ASN1GeneralizedTime aSN1GeneralizedTime = null;
            try {
                byte[] extensionValue = ((X509Certificate) certPath.getCertificates().get(i2)).getExtensionValue(ISISMTTObjectIdentifiers.id_isismtt_at_dateOfCertGen.getId());
                if (extensionValue != null) {
                    aSN1GeneralizedTime = ASN1GeneralizedTime.getInstance(ASN1Primitive.fromByteArray(extensionValue));
                }
                if (aSN1GeneralizedTime != null) {
                    try {
                        return aSN1GeneralizedTime.getDate();
                    } catch (ParseException e) {
                        throw new AnnotatedException("Date from date of cert gen extension could not be parsed.", e);
                    }
                }
            } catch (IOException e2) {
                throw new AnnotatedException(str);
            } catch (IllegalArgumentException e3) {
                throw new AnnotatedException(str);
            }
        }
        return ((X509Certificate) certPath.getCertificates().get(i2)).getNotBefore();
    }

    protected static Date getValidDate(PKIXExtendedParameters pKIXExtendedParameters) {
        Date date = pKIXExtendedParameters.getDate();
        return date == null ? new Date() : date;
    }

    protected static boolean isAnyPolicy(Set set) {
        return set == null || set.contains("2.5.29.32.0") || set.isEmpty();
    }

    private static boolean isDeltaCRL(X509CRL x509crl) {
        Set criticalExtensionOIDs = x509crl.getCriticalExtensionOIDs();
        if (criticalExtensionOIDs == null) {
            return false;
        }
        return criticalExtensionOIDs.contains(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
    }

    static boolean isIssuerTrustAnchor(X509Certificate x509Certificate, Set set, String str) throws AnnotatedException {
        boolean z = false;
        try {
            if (findTrustAnchor(x509Certificate, set, str) != null) {
                z = true;
            }
            return z;
        } catch (Exception e) {
            return false;
        }
    }

    protected static boolean isSelfIssued(X509Certificate x509Certificate) {
        return x509Certificate.getSubjectDN().equals(x509Certificate.getIssuerDN());
    }

    protected static void prepareNextCertB1(int i, List[] listArr, String str, Map map, X509Certificate x509Certificate) throws AnnotatedException, CertPathValidatorException {
        boolean z;
        boolean z2;
        Iterator it = listArr[i].iterator();
        while (true) {
            z = false;
            if (!it.hasNext()) {
                z2 = false;
                break;
            }
            PKIXPolicyNode pKIXPolicyNode = (PKIXPolicyNode) it.next();
            if (pKIXPolicyNode.getValidPolicy().equals(str)) {
                z2 = true;
                pKIXPolicyNode.expectedPolicies = (Set) map.get(str);
                break;
            }
        }
        if (!z2) {
            for (PKIXPolicyNode pKIXPolicyNode2 : listArr[i]) {
                String str2 = "2.5.29.32.0";
                if (str2.equals(pKIXPolicyNode2.getValidPolicy())) {
                    Set set = null;
                    try {
                        Enumeration objects = DERSequence.getInstance(getExtensionValue(x509Certificate, CERTIFICATE_POLICIES)).getObjects();
                        while (true) {
                            if (!objects.hasMoreElements()) {
                                break;
                            }
                            try {
                                PolicyInformation instance = PolicyInformation.getInstance(objects.nextElement());
                                if (str2.equals(instance.getPolicyIdentifier().getId())) {
                                    try {
                                        set = getQualifierSet(instance.getPolicyQualifiers());
                                        break;
                                    } catch (CertPathValidatorException e) {
                                        throw new ExtCertPathValidatorException("Policy qualifier info set could not be built.", e);
                                    }
                                }
                            } catch (Exception e2) {
                                throw new AnnotatedException("Policy information cannot be decoded.", e2);
                            }
                        }
                        Set set2 = set;
                        if (x509Certificate.getCriticalExtensionOIDs() != null) {
                            z = x509Certificate.getCriticalExtensionOIDs().contains(CERTIFICATE_POLICIES);
                        }
                        boolean z3 = z;
                        PKIXPolicyNode pKIXPolicyNode3 = (PKIXPolicyNode) pKIXPolicyNode2.getParent();
                        if (str2.equals(pKIXPolicyNode3.getValidPolicy())) {
                            PKIXPolicyNode pKIXPolicyNode4 = new PKIXPolicyNode(new ArrayList(), i, (Set) map.get(str), pKIXPolicyNode3, set2, str, z3);
                            pKIXPolicyNode3.addChild(pKIXPolicyNode4);
                            listArr[i].add(pKIXPolicyNode4);
                            return;
                        }
                        return;
                    } catch (Exception e3) {
                        throw new AnnotatedException("Certificate policies cannot be decoded.", e3);
                    }
                }
            }
        }
    }

    protected static PKIXPolicyNode prepareNextCertB2(int i, List[] listArr, String str, PKIXPolicyNode pKIXPolicyNode) {
        Iterator it = listArr[i].iterator();
        while (it.hasNext()) {
            PKIXPolicyNode pKIXPolicyNode2 = (PKIXPolicyNode) it.next();
            if (pKIXPolicyNode2.getValidPolicy().equals(str)) {
                ((PKIXPolicyNode) pKIXPolicyNode2.getParent()).removeChild(pKIXPolicyNode2);
                it.remove();
                for (int i2 = i - 1; i2 >= 0; i2--) {
                    List list = listArr[i2];
                    for (int i3 = 0; i3 < list.size(); i3++) {
                        PKIXPolicyNode pKIXPolicyNode3 = (PKIXPolicyNode) list.get(i3);
                        if (!pKIXPolicyNode3.hasChildren()) {
                            pKIXPolicyNode = removePolicyNode(pKIXPolicyNode, listArr, pKIXPolicyNode3);
                            if (pKIXPolicyNode == null) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return pKIXPolicyNode;
    }

    protected static boolean processCertD1i(int i, List[] listArr, ASN1ObjectIdentifier aSN1ObjectIdentifier, Set set) {
        List list = listArr[i - 1];
        for (int i2 = 0; i2 < list.size(); i2++) {
            PKIXPolicyNode pKIXPolicyNode = (PKIXPolicyNode) list.get(i2);
            if (pKIXPolicyNode.getExpectedPolicies().contains(aSN1ObjectIdentifier.getId())) {
                HashSet hashSet = new HashSet();
                hashSet.add(aSN1ObjectIdentifier.getId());
                PKIXPolicyNode pKIXPolicyNode2 = new PKIXPolicyNode(new ArrayList(), i, hashSet, pKIXPolicyNode, set, aSN1ObjectIdentifier.getId(), false);
                pKIXPolicyNode.addChild(pKIXPolicyNode2);
                listArr[i].add(pKIXPolicyNode2);
                return true;
            }
        }
        return false;
    }

    protected static void processCertD1ii(int i, List[] listArr, ASN1ObjectIdentifier aSN1ObjectIdentifier, Set set) {
        List list = listArr[i - 1];
        for (int i2 = 0; i2 < list.size(); i2++) {
            PKIXPolicyNode pKIXPolicyNode = (PKIXPolicyNode) list.get(i2);
            if ("2.5.29.32.0".equals(pKIXPolicyNode.getValidPolicy())) {
                HashSet hashSet = new HashSet();
                hashSet.add(aSN1ObjectIdentifier.getId());
                PKIXPolicyNode pKIXPolicyNode2 = new PKIXPolicyNode(new ArrayList(), i, hashSet, pKIXPolicyNode, set, aSN1ObjectIdentifier.getId(), false);
                pKIXPolicyNode.addChild(pKIXPolicyNode2);
                listArr[i].add(pKIXPolicyNode2);
                return;
            }
        }
    }

    protected static PKIXPolicyNode removePolicyNode(PKIXPolicyNode pKIXPolicyNode, List[] listArr, PKIXPolicyNode pKIXPolicyNode2) {
        PKIXPolicyNode pKIXPolicyNode3 = (PKIXPolicyNode) pKIXPolicyNode2.getParent();
        if (pKIXPolicyNode == null) {
            return null;
        }
        if (pKIXPolicyNode3 == null) {
            for (int i = 0; i < listArr.length; i++) {
                listArr[i] = new ArrayList();
            }
            return null;
        }
        pKIXPolicyNode3.removeChild(pKIXPolicyNode2);
        removePolicyNodeRecurse(listArr, pKIXPolicyNode2);
        return pKIXPolicyNode;
    }

    private static void removePolicyNodeRecurse(List[] listArr, PKIXPolicyNode pKIXPolicyNode) {
        listArr[pKIXPolicyNode.getDepth()].remove(pKIXPolicyNode);
        if (pKIXPolicyNode.hasChildren()) {
            Iterator children = pKIXPolicyNode.getChildren();
            while (children.hasNext()) {
                removePolicyNodeRecurse(listArr, (PKIXPolicyNode) children.next());
            }
        }
    }

    protected static void verifyX509Certificate(X509Certificate x509Certificate, PublicKey publicKey, String str) throws GeneralSecurityException {
        if (str == null) {
            x509Certificate.verify(publicKey);
        } else {
            x509Certificate.verify(publicKey, str);
        }
    }
}
