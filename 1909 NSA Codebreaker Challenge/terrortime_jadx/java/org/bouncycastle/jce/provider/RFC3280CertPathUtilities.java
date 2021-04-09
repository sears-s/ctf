package org.bouncycastle.jce.provider;

import android.support.v4.os.EnvironmentCompat;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.NameConstraints;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector.Builder;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.util.Arrays;

class RFC3280CertPathUtilities {
    public static final String ANY_POLICY = "2.5.29.32.0";
    public static final String AUTHORITY_KEY_IDENTIFIER = Extension.authorityKeyIdentifier.getId();
    public static final String BASIC_CONSTRAINTS = Extension.basicConstraints.getId();
    public static final String CERTIFICATE_POLICIES = Extension.certificatePolicies.getId();
    public static final String CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
    public static final String CRL_NUMBER = Extension.cRLNumber.getId();
    protected static final int CRL_SIGN = 6;
    private static final PKIXCRLUtil CRL_UTIL = new PKIXCRLUtil();
    public static final String DELTA_CRL_INDICATOR = Extension.deltaCRLIndicator.getId();
    public static final String FRESHEST_CRL = Extension.freshestCRL.getId();
    public static final String INHIBIT_ANY_POLICY = Extension.inhibitAnyPolicy.getId();
    public static final String ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();
    protected static final int KEY_CERT_SIGN = 5;
    public static final String KEY_USAGE = Extension.keyUsage.getId();
    public static final String NAME_CONSTRAINTS = Extension.nameConstraints.getId();
    public static final String POLICY_CONSTRAINTS = Extension.policyConstraints.getId();
    public static final String POLICY_MAPPINGS = Extension.policyMappings.getId();
    public static final String SUBJECT_ALTERNATIVE_NAME = Extension.subjectAlternativeName.getId();
    protected static final String[] crlReasons = {"unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", EnvironmentCompat.MEDIA_UNKNOWN, "removeFromCRL", "privilegeWithdrawn", "aACompromise"};

    RFC3280CertPathUtilities() {
    }

    private static void checkCRL(DistributionPoint distributionPoint, PKIXExtendedParameters pKIXExtendedParameters, X509Certificate x509Certificate, Date date, X509Certificate x509Certificate2, PublicKey publicKey, CertStatus certStatus, ReasonsMask reasonsMask, List list, JcaJceHelper jcaJceHelper) throws AnnotatedException {
        Iterator it;
        Date date2;
        int i;
        ReasonsMask reasonsMask2;
        X509CRL processCRLH;
        DistributionPoint distributionPoint2 = distributionPoint;
        PKIXExtendedParameters pKIXExtendedParameters2 = pKIXExtendedParameters;
        X509Certificate x509Certificate3 = x509Certificate;
        Date date3 = date;
        CertStatus certStatus2 = certStatus;
        ReasonsMask reasonsMask3 = reasonsMask;
        Date date4 = new Date(System.currentTimeMillis());
        if (date.getTime() <= date4.getTime()) {
            Iterator it2 = CertPathValidatorUtilities.getCompleteCRLs(distributionPoint2, x509Certificate3, date4, pKIXExtendedParameters2).iterator();
            int i2 = 1;
            int i3 = 0;
            e = null;
            while (it2.hasNext() && certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons()) {
                try {
                    X509CRL x509crl = (X509CRL) it2.next();
                    ReasonsMask processCRLD = processCRLD(x509crl, distributionPoint2);
                    if (!processCRLD.hasNewReasons(reasonsMask3)) {
                        continue;
                    } else {
                        date2 = date4;
                        ReasonsMask reasonsMask4 = processCRLD;
                        it = it2;
                        X509CRL x509crl2 = x509crl;
                        Throwable th = e;
                        int i4 = i2;
                        try {
                            processCRLH = pKIXExtendedParameters.isUseDeltasEnabled() ? processCRLH(CertPathValidatorUtilities.getDeltaCRLs(pKIXExtendedParameters.getDate() != null ? pKIXExtendedParameters.getDate() : date2, x509crl2, pKIXExtendedParameters.getCertStores(), pKIXExtendedParameters.getCRLStores()), processCRLG(x509crl2, processCRLF(x509crl, x509Certificate, x509Certificate2, publicKey, pKIXExtendedParameters, list, jcaJceHelper))) : null;
                            if (pKIXExtendedParameters.getValidityModel() != i4) {
                                if (x509Certificate.getNotAfter().getTime() < x509crl2.getThisUpdate().getTime()) {
                                    throw new AnnotatedException("No valid CRL for current time found.");
                                }
                            }
                            processCRLB1(distributionPoint2, x509Certificate3, x509crl2);
                            processCRLB2(distributionPoint2, x509Certificate3, x509crl2);
                            processCRLC(processCRLH, x509crl2, pKIXExtendedParameters2);
                            processCRLI(date3, processCRLH, x509Certificate3, certStatus2, pKIXExtendedParameters2);
                            processCRLJ(date3, x509crl2, x509Certificate3, certStatus2);
                            if (certStatus.getCertStatus() == 8) {
                                certStatus2.setCertStatus(11);
                            }
                            reasonsMask2 = reasonsMask;
                            i = i4;
                        } catch (AnnotatedException e) {
                            e = e;
                            reasonsMask2 = reasonsMask;
                            i = i4;
                            reasonsMask3 = reasonsMask2;
                            i2 = i;
                            date4 = date2;
                            it2 = it;
                        }
                        try {
                            reasonsMask2.addReasons(reasonsMask4);
                            Set criticalExtensionOIDs = x509crl2.getCriticalExtensionOIDs();
                            if (criticalExtensionOIDs != null) {
                                HashSet hashSet = new HashSet(criticalExtensionOIDs);
                                hashSet.remove(Extension.issuingDistributionPoint.getId());
                                hashSet.remove(Extension.deltaCRLIndicator.getId());
                                if (!hashSet.isEmpty()) {
                                    throw new AnnotatedException("CRL contains unsupported critical extensions.");
                                }
                            }
                            if (processCRLH != null) {
                                Set criticalExtensionOIDs2 = processCRLH.getCriticalExtensionOIDs();
                                if (criticalExtensionOIDs2 != null) {
                                    HashSet hashSet2 = new HashSet(criticalExtensionOIDs2);
                                    hashSet2.remove(Extension.issuingDistributionPoint.getId());
                                    hashSet2.remove(Extension.deltaCRLIndicator.getId());
                                    if (!hashSet2.isEmpty()) {
                                        throw new AnnotatedException("Delta CRL contains unsupported critical extension.");
                                    }
                                }
                            }
                            reasonsMask3 = reasonsMask2;
                            i2 = i;
                            i3 = i2;
                            date4 = date2;
                            it2 = it;
                            e = th;
                        } catch (AnnotatedException e2) {
                            e = e2;
                            reasonsMask3 = reasonsMask2;
                            i2 = i;
                            date4 = date2;
                            it2 = it;
                        }
                    }
                } catch (AnnotatedException e3) {
                    e = e3;
                    i = i2;
                    reasonsMask2 = reasonsMask3;
                    date2 = date4;
                    it = it2;
                    reasonsMask3 = reasonsMask2;
                    i2 = i;
                    date4 = date2;
                    it2 = it;
                }
            }
            Throwable th2 = e;
            if (i3 == 0) {
                throw th2;
            }
            return;
        }
        throw new AnnotatedException("Validation time is in future.");
    }

    /* JADX WARNING: Removed duplicated region for block: B:49:0x00fa  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0107  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static void checkCRLs(org.bouncycastle.jcajce.PKIXExtendedParameters r20, java.security.cert.X509Certificate r21, java.util.Date r22, java.security.cert.X509Certificate r23, java.security.PublicKey r24, java.util.List r25, org.bouncycastle.jcajce.util.JcaJceHelper r26) throws org.bouncycastle.jce.provider.AnnotatedException {
        /*
            java.lang.String r0 = CRL_DISTRIBUTION_POINTS     // Catch:{ Exception -> 0x0181 }
            r11 = r21
            org.bouncycastle.asn1.ASN1Primitive r0 = org.bouncycastle.jce.provider.CertPathValidatorUtilities.getExtensionValue(r11, r0)     // Catch:{ Exception -> 0x0181 }
            org.bouncycastle.asn1.x509.CRLDistPoint r0 = org.bouncycastle.asn1.x509.CRLDistPoint.getInstance(r0)     // Catch:{ Exception -> 0x0181 }
            org.bouncycastle.jcajce.PKIXExtendedParameters$Builder r1 = new org.bouncycastle.jcajce.PKIXExtendedParameters$Builder
            r12 = r20
            r1.<init>(r12)
            java.util.Map r2 = r20.getNamedCRLStoreMap()     // Catch:{ AnnotatedException -> 0x0178 }
            java.util.List r2 = org.bouncycastle.jce.provider.CertPathValidatorUtilities.getAdditionalStoresFromCRLDistributionPoint(r0, r2)     // Catch:{ AnnotatedException -> 0x0178 }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ AnnotatedException -> 0x0178 }
        L_0x001f:
            boolean r3 = r2.hasNext()     // Catch:{ AnnotatedException -> 0x0178 }
            if (r3 == 0) goto L_0x002f
            java.lang.Object r3 = r2.next()     // Catch:{ AnnotatedException -> 0x0178 }
            org.bouncycastle.jcajce.PKIXCRLStore r3 = (org.bouncycastle.jcajce.PKIXCRLStore) r3     // Catch:{ AnnotatedException -> 0x0178 }
            r1.addCRLStore(r3)     // Catch:{ AnnotatedException -> 0x0178 }
            goto L_0x001f
        L_0x002f:
            org.bouncycastle.jce.provider.CertStatus r13 = new org.bouncycastle.jce.provider.CertStatus
            r13.<init>()
            org.bouncycastle.jce.provider.ReasonsMask r14 = new org.bouncycastle.jce.provider.ReasonsMask
            r14.<init>()
            org.bouncycastle.jcajce.PKIXExtendedParameters r15 = r1.build()
            r16 = 1
            r10 = 0
            r9 = 0
            r8 = 11
            if (r0 == 0) goto L_0x0098
            org.bouncycastle.asn1.x509.DistributionPoint[] r7 = r0.getDistributionPoints()     // Catch:{ Exception -> 0x008e }
            if (r7 == 0) goto L_0x0098
            r0 = r9
            r6 = r10
            r17 = r6
        L_0x004f:
            int r1 = r7.length
            if (r6 >= r1) goto L_0x008c
            int r1 = r13.getCertStatus()
            if (r1 != r8) goto L_0x008c
            boolean r1 = r14.isAllReasons()
            if (r1 != 0) goto L_0x008c
            r1 = r7[r6]     // Catch:{ AnnotatedException -> 0x007c }
            r2 = r15
            r3 = r21
            r4 = r22
            r5 = r23
            r18 = r6
            r6 = r24
            r19 = r7
            r7 = r13
            r11 = r8
            r8 = r14
            r9 = r25
            r10 = r26
            checkCRL(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ AnnotatedException -> 0x007a }
            r17 = r16
            goto L_0x0082
        L_0x007a:
            r0 = move-exception
            goto L_0x0082
        L_0x007c:
            r0 = move-exception
            r18 = r6
            r19 = r7
            r11 = r8
        L_0x0082:
            int r6 = r18 + 1
            r8 = r11
            r7 = r19
            r9 = 0
            r10 = 0
            r11 = r21
            goto L_0x004f
        L_0x008c:
            r11 = r8
            goto L_0x009c
        L_0x008e:
            r0 = move-exception
            r1 = r0
            org.bouncycastle.jce.provider.AnnotatedException r0 = new org.bouncycastle.jce.provider.AnnotatedException
            java.lang.String r2 = "Distribution points could not be read."
            r0.<init>(r2, r1)
            throw r0
        L_0x0098:
            r11 = r8
            r0 = 0
            r17 = 0
        L_0x009c:
            int r1 = r13.getCertStatus()
            if (r1 != r11) goto L_0x00f8
            boolean r1 = r14.isAllReasons()
            if (r1 != 0) goto L_0x00f8
            org.bouncycastle.asn1.ASN1InputStream r1 = new org.bouncycastle.asn1.ASN1InputStream     // Catch:{ Exception -> 0x00ef }
            org.bouncycastle.asn1.x500.X500Name r2 = org.bouncycastle.jce.provider.PrincipalUtils.getEncodedIssuerPrincipal(r21)     // Catch:{ Exception -> 0x00ef }
            byte[] r2 = r2.getEncoded()     // Catch:{ Exception -> 0x00ef }
            r1.<init>(r2)     // Catch:{ Exception -> 0x00ef }
            org.bouncycastle.asn1.ASN1Primitive r1 = r1.readObject()     // Catch:{ Exception -> 0x00ef }
            org.bouncycastle.asn1.x509.DistributionPoint r2 = new org.bouncycastle.asn1.x509.DistributionPoint     // Catch:{ AnnotatedException -> 0x00ed }
            org.bouncycastle.asn1.x509.DistributionPointName r3 = new org.bouncycastle.asn1.x509.DistributionPointName     // Catch:{ AnnotatedException -> 0x00ed }
            org.bouncycastle.asn1.x509.GeneralNames r4 = new org.bouncycastle.asn1.x509.GeneralNames     // Catch:{ AnnotatedException -> 0x00ed }
            org.bouncycastle.asn1.x509.GeneralName r5 = new org.bouncycastle.asn1.x509.GeneralName     // Catch:{ AnnotatedException -> 0x00ed }
            r6 = 4
            r5.<init>(r6, r1)     // Catch:{ AnnotatedException -> 0x00ed }
            r4.<init>(r5)     // Catch:{ AnnotatedException -> 0x00ed }
            r1 = 0
            r3.<init>(r1, r4)     // Catch:{ AnnotatedException -> 0x00ed }
            r1 = 0
            r2.<init>(r3, r1, r1)     // Catch:{ AnnotatedException -> 0x00ed }
            java.lang.Object r1 = r20.clone()     // Catch:{ AnnotatedException -> 0x00ed }
            r3 = r1
            org.bouncycastle.jcajce.PKIXExtendedParameters r3 = (org.bouncycastle.jcajce.PKIXExtendedParameters) r3     // Catch:{ AnnotatedException -> 0x00ed }
            r1 = r2
            r2 = r3
            r3 = r21
            r4 = r22
            r5 = r23
            r6 = r24
            r7 = r13
            r8 = r14
            r9 = r25
            r10 = r26
            checkCRL(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ AnnotatedException -> 0x00ed }
            r17 = r16
            goto L_0x00f8
        L_0x00ed:
            r0 = move-exception
            goto L_0x00f8
        L_0x00ef:
            r0 = move-exception
            org.bouncycastle.jce.provider.AnnotatedException r1 = new org.bouncycastle.jce.provider.AnnotatedException     // Catch:{ AnnotatedException -> 0x00ed }
            java.lang.String r2 = "Issuer from certificate for CRL could not be reencoded."
            r1.<init>(r2, r0)     // Catch:{ AnnotatedException -> 0x00ed }
            throw r1     // Catch:{ AnnotatedException -> 0x00ed }
        L_0x00f8:
            if (r17 != 0) goto L_0x0107
            boolean r1 = r0 instanceof org.bouncycastle.jce.provider.AnnotatedException
            if (r1 == 0) goto L_0x00ff
            throw r0
        L_0x00ff:
            org.bouncycastle.jce.provider.AnnotatedException r1 = new org.bouncycastle.jce.provider.AnnotatedException
            java.lang.String r2 = "No valid CRL found."
            r1.<init>(r2, r0)
            throw r1
        L_0x0107:
            int r0 = r13.getCertStatus()
            if (r0 != r11) goto L_0x012d
            boolean r0 = r14.isAllReasons()
            r1 = 12
            if (r0 != 0) goto L_0x011e
            int r0 = r13.getCertStatus()
            if (r0 != r11) goto L_0x011e
            r13.setCertStatus(r1)
        L_0x011e:
            int r0 = r13.getCertStatus()
            if (r0 == r1) goto L_0x0125
            return
        L_0x0125:
            org.bouncycastle.jce.provider.AnnotatedException r0 = new org.bouncycastle.jce.provider.AnnotatedException
            java.lang.String r1 = "Certificate status could not be determined."
            r0.<init>(r1)
            throw r0
        L_0x012d:
            java.text.SimpleDateFormat r0 = new java.text.SimpleDateFormat
            java.lang.String r1 = "yyyy-MM-dd HH:mm:ss Z"
            r0.<init>(r1)
            java.lang.String r1 = "UTC"
            java.util.TimeZone r1 = java.util.TimeZone.getTimeZone(r1)
            r0.setTimeZone(r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Certificate revocation after "
            r1.append(r2)
            java.util.Date r2 = r13.getRevocationDate()
            java.lang.String r0 = r0.format(r2)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            java.lang.String r0 = ", reason: "
            r1.append(r0)
            java.lang.String[] r0 = crlReasons
            int r2 = r13.getCertStatus()
            r0 = r0[r2]
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            org.bouncycastle.jce.provider.AnnotatedException r1 = new org.bouncycastle.jce.provider.AnnotatedException
            r1.<init>(r0)
            throw r1
        L_0x0178:
            r0 = move-exception
            org.bouncycastle.jce.provider.AnnotatedException r1 = new org.bouncycastle.jce.provider.AnnotatedException
            java.lang.String r2 = "No additional CRL locations could be decoded from CRL distribution point extension."
            r1.<init>(r2, r0)
            throw r1
        L_0x0181:
            r0 = move-exception
            org.bouncycastle.jce.provider.AnnotatedException r1 = new org.bouncycastle.jce.provider.AnnotatedException
            java.lang.String r2 = "CRL distribution point extension could not be read."
            r1.<init>(r2, r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jce.provider.RFC3280CertPathUtilities.checkCRLs(org.bouncycastle.jcajce.PKIXExtendedParameters, java.security.cert.X509Certificate, java.util.Date, java.security.cert.X509Certificate, java.security.PublicKey, java.util.List, org.bouncycastle.jcajce.util.JcaJceHelper):void");
    }

    protected static PKIXPolicyNode prepareCertB(CertPath certPath, int i, List[] listArr, PKIXPolicyNode pKIXPolicyNode, int i2) throws CertPathValidatorException {
        boolean z;
        CertPath certPath2 = certPath;
        int i3 = i;
        List[] listArr2 = listArr;
        List certificates = certPath.getCertificates();
        X509Certificate x509Certificate = (X509Certificate) certificates.get(i3);
        int size = certificates.size() - i3;
        try {
            ASN1Sequence instance = DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, POLICY_MAPPINGS));
            if (instance == null) {
                return pKIXPolicyNode;
            }
            HashMap hashMap = new HashMap();
            HashSet<String> hashSet = new HashSet<>();
            boolean z2 = false;
            for (int i4 = 0; i4 < instance.size(); i4++) {
                ASN1Sequence aSN1Sequence = (ASN1Sequence) instance.getObjectAt(i4);
                String id = ((ASN1ObjectIdentifier) aSN1Sequence.getObjectAt(0)).getId();
                String id2 = ((ASN1ObjectIdentifier) aSN1Sequence.getObjectAt(1)).getId();
                if (!hashMap.containsKey(id)) {
                    HashSet hashSet2 = new HashSet();
                    hashSet2.add(id2);
                    hashMap.put(id, hashSet2);
                    hashSet.add(id);
                } else {
                    ((Set) hashMap.get(id)).add(id2);
                }
            }
            PKIXPolicyNode pKIXPolicyNode2 = pKIXPolicyNode;
            for (String str : hashSet) {
                if (i2 > 0) {
                    Iterator it = listArr2[size].iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            z = z2;
                            break;
                        }
                        PKIXPolicyNode pKIXPolicyNode3 = (PKIXPolicyNode) it.next();
                        if (pKIXPolicyNode3.getValidPolicy().equals(str)) {
                            pKIXPolicyNode3.expectedPolicies = (Set) hashMap.get(str);
                            z = true;
                            break;
                        }
                    }
                    if (!z) {
                        Iterator it2 = listArr2[size].iterator();
                        while (true) {
                            if (!it2.hasNext()) {
                                break;
                            }
                            PKIXPolicyNode pKIXPolicyNode4 = (PKIXPolicyNode) it2.next();
                            String validPolicy = pKIXPolicyNode4.getValidPolicy();
                            String str2 = ANY_POLICY;
                            if (str2.equals(validPolicy)) {
                                Set set = null;
                                try {
                                    Enumeration objects = ((ASN1Sequence) CertPathValidatorUtilities.getExtensionValue(x509Certificate, CERTIFICATE_POLICIES)).getObjects();
                                    while (true) {
                                        if (!objects.hasMoreElements()) {
                                            break;
                                        }
                                        try {
                                            PolicyInformation instance2 = PolicyInformation.getInstance(objects.nextElement());
                                            if (str2.equals(instance2.getPolicyIdentifier().getId())) {
                                                try {
                                                    set = CertPathValidatorUtilities.getQualifierSet(instance2.getPolicyQualifiers());
                                                    break;
                                                } catch (CertPathValidatorException e) {
                                                    throw new ExtCertPathValidatorException("Policy qualifier info set could not be decoded.", e, certPath2, i3);
                                                }
                                            }
                                        } catch (Exception e2) {
                                            throw new CertPathValidatorException("Policy information could not be decoded.", e2, certPath2, i3);
                                        }
                                    }
                                    Set set2 = set;
                                    boolean contains = x509Certificate.getCriticalExtensionOIDs() != null ? x509Certificate.getCriticalExtensionOIDs().contains(CERTIFICATE_POLICIES) : z2;
                                    PKIXPolicyNode pKIXPolicyNode5 = (PKIXPolicyNode) pKIXPolicyNode4.getParent();
                                    if (str2.equals(pKIXPolicyNode5.getValidPolicy())) {
                                        PKIXPolicyNode pKIXPolicyNode6 = r5;
                                        PKIXPolicyNode pKIXPolicyNode7 = pKIXPolicyNode5;
                                        PKIXPolicyNode pKIXPolicyNode8 = new PKIXPolicyNode(new ArrayList(), size, (Set) hashMap.get(str), pKIXPolicyNode5, set2, str, contains);
                                        pKIXPolicyNode7.addChild(pKIXPolicyNode6);
                                        listArr2[size].add(pKIXPolicyNode6);
                                    }
                                } catch (AnnotatedException e3) {
                                    throw new ExtCertPathValidatorException("Certificate policies extension could not be decoded.", e3, certPath2, i3);
                                }
                            }
                        }
                    }
                } else if (i2 <= 0) {
                    Iterator it3 = listArr2[size].iterator();
                    while (it3.hasNext()) {
                        PKIXPolicyNode pKIXPolicyNode9 = (PKIXPolicyNode) it3.next();
                        if (pKIXPolicyNode9.getValidPolicy().equals(str)) {
                            ((PKIXPolicyNode) pKIXPolicyNode9.getParent()).removeChild(pKIXPolicyNode9);
                            it3.remove();
                            for (int i5 = size - 1; i5 >= 0; i5--) {
                                List list = listArr2[i5];
                                PKIXPolicyNode pKIXPolicyNode10 = pKIXPolicyNode2;
                                for (int i6 = 0; i6 < list.size(); i6++) {
                                    PKIXPolicyNode pKIXPolicyNode11 = (PKIXPolicyNode) list.get(i6);
                                    if (!pKIXPolicyNode11.hasChildren()) {
                                        pKIXPolicyNode10 = CertPathValidatorUtilities.removePolicyNode(pKIXPolicyNode10, listArr2, pKIXPolicyNode11);
                                        if (pKIXPolicyNode10 == null) {
                                            break;
                                        }
                                    }
                                }
                                pKIXPolicyNode2 = pKIXPolicyNode10;
                            }
                        }
                    }
                }
                z2 = false;
            }
            return pKIXPolicyNode2;
        } catch (AnnotatedException e4) {
            throw new ExtCertPathValidatorException("Policy mappings extension could not be decoded.", e4, certPath2, i3);
        }
    }

    protected static void prepareNextCertA(CertPath certPath, int i) throws CertPathValidatorException {
        try {
            ASN1Sequence instance = DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), POLICY_MAPPINGS));
            if (instance != null) {
                int i2 = 0;
                while (i2 < instance.size()) {
                    try {
                        ASN1Sequence instance2 = DERSequence.getInstance(instance.getObjectAt(i2));
                        ASN1ObjectIdentifier instance3 = ASN1ObjectIdentifier.getInstance(instance2.getObjectAt(0));
                        ASN1ObjectIdentifier instance4 = ASN1ObjectIdentifier.getInstance(instance2.getObjectAt(1));
                        String id = instance3.getId();
                        String str = ANY_POLICY;
                        if (str.equals(id)) {
                            throw new CertPathValidatorException("IssuerDomainPolicy is anyPolicy", null, certPath, i);
                        } else if (!str.equals(instance4.getId())) {
                            i2++;
                        } else {
                            throw new CertPathValidatorException("SubjectDomainPolicy is anyPolicy,", null, certPath, i);
                        }
                    } catch (Exception e) {
                        throw new ExtCertPathValidatorException("Policy mappings extension contents could not be decoded.", e, certPath, i);
                    }
                }
            }
        } catch (AnnotatedException e2) {
            throw new ExtCertPathValidatorException("Policy mappings extension could not be decoded.", e2, certPath, i);
        }
    }

    protected static void prepareNextCertG(CertPath certPath, int i, PKIXNameConstraintValidator pKIXNameConstraintValidator) throws CertPathValidatorException {
        try {
            ASN1Sequence instance = DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), NAME_CONSTRAINTS));
            NameConstraints instance2 = instance != null ? NameConstraints.getInstance(instance) : null;
            if (instance2 != null) {
                GeneralSubtree[] permittedSubtrees = instance2.getPermittedSubtrees();
                if (permittedSubtrees != null) {
                    try {
                        pKIXNameConstraintValidator.intersectPermittedSubtree(permittedSubtrees);
                    } catch (Exception e) {
                        throw new ExtCertPathValidatorException("Permitted subtrees cannot be build from name constraints extension.", e, certPath, i);
                    }
                }
                GeneralSubtree[] excludedSubtrees = instance2.getExcludedSubtrees();
                if (excludedSubtrees != null) {
                    int i2 = 0;
                    while (i2 != excludedSubtrees.length) {
                        try {
                            pKIXNameConstraintValidator.addExcludedSubtree(excludedSubtrees[i2]);
                            i2++;
                        } catch (Exception e2) {
                            throw new ExtCertPathValidatorException("Excluded subtrees cannot be build from name constraints extension.", e2, certPath, i);
                        }
                    }
                }
            }
        } catch (Exception e3) {
            throw new ExtCertPathValidatorException("Name constraints extension could not be decoded.", e3, certPath, i);
        }
    }

    protected static int prepareNextCertH1(CertPath certPath, int i, int i2) {
        return (CertPathValidatorUtilities.isSelfIssued((X509Certificate) certPath.getCertificates().get(i)) || i2 == 0) ? i2 : i2 - 1;
    }

    protected static int prepareNextCertH2(CertPath certPath, int i, int i2) {
        return (CertPathValidatorUtilities.isSelfIssued((X509Certificate) certPath.getCertificates().get(i)) || i2 == 0) ? i2 : i2 - 1;
    }

    protected static int prepareNextCertH3(CertPath certPath, int i, int i2) {
        return (CertPathValidatorUtilities.isSelfIssued((X509Certificate) certPath.getCertificates().get(i)) || i2 == 0) ? i2 : i2 - 1;
    }

    protected static int prepareNextCertI1(CertPath certPath, int i, int i2) throws CertPathValidatorException {
        try {
            ASN1Sequence instance = DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), POLICY_CONSTRAINTS));
            if (instance != null) {
                Enumeration objects = instance.getObjects();
                while (true) {
                    if (!objects.hasMoreElements()) {
                        break;
                    }
                    try {
                        ASN1TaggedObject instance2 = ASN1TaggedObject.getInstance(objects.nextElement());
                        if (instance2.getTagNo() == 0) {
                            int intValue = ASN1Integer.getInstance(instance2, false).getValue().intValue();
                            if (intValue < i2) {
                                return intValue;
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        throw new ExtCertPathValidatorException("Policy constraints extension contents cannot be decoded.", e, certPath, i);
                    }
                }
            }
            return i2;
        } catch (Exception e2) {
            throw new ExtCertPathValidatorException("Policy constraints extension cannot be decoded.", e2, certPath, i);
        }
    }

    protected static int prepareNextCertI2(CertPath certPath, int i, int i2) throws CertPathValidatorException {
        try {
            ASN1Sequence instance = DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), POLICY_CONSTRAINTS));
            if (instance != null) {
                Enumeration objects = instance.getObjects();
                while (true) {
                    if (!objects.hasMoreElements()) {
                        break;
                    }
                    try {
                        ASN1TaggedObject instance2 = ASN1TaggedObject.getInstance(objects.nextElement());
                        if (instance2.getTagNo() == 1) {
                            int intValue = ASN1Integer.getInstance(instance2, false).getValue().intValue();
                            if (intValue < i2) {
                                return intValue;
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        throw new ExtCertPathValidatorException("Policy constraints extension contents cannot be decoded.", e, certPath, i);
                    }
                }
            }
            return i2;
        } catch (Exception e2) {
            throw new ExtCertPathValidatorException("Policy constraints extension cannot be decoded.", e2, certPath, i);
        }
    }

    protected static int prepareNextCertJ(CertPath certPath, int i, int i2) throws CertPathValidatorException {
        try {
            ASN1Integer instance = ASN1Integer.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), INHIBIT_ANY_POLICY));
            if (instance != null) {
                int intValue = instance.getValue().intValue();
                if (intValue < i2) {
                    return intValue;
                }
            }
            return i2;
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Inhibit any-policy extension cannot be decoded.", e, certPath, i);
        }
    }

    protected static void prepareNextCertK(CertPath certPath, int i) throws CertPathValidatorException {
        try {
            BasicConstraints instance = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), BASIC_CONSTRAINTS));
            if (instance == null) {
                throw new CertPathValidatorException("Intermediate certificate lacks BasicConstraints");
            } else if (!instance.isCA()) {
                throw new CertPathValidatorException("Not a CA certificate");
            }
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Basic constraints extension cannot be decoded.", e, certPath, i);
        }
    }

    protected static int prepareNextCertL(CertPath certPath, int i, int i2) throws CertPathValidatorException {
        if (CertPathValidatorUtilities.isSelfIssued((X509Certificate) certPath.getCertificates().get(i))) {
            return i2;
        }
        if (i2 > 0) {
            return i2 - 1;
        }
        throw new ExtCertPathValidatorException("Max path length not greater than zero", null, certPath, i);
    }

    protected static int prepareNextCertM(CertPath certPath, int i, int i2) throws CertPathValidatorException {
        try {
            BasicConstraints instance = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), BASIC_CONSTRAINTS));
            if (instance != null) {
                BigInteger pathLenConstraint = instance.getPathLenConstraint();
                if (pathLenConstraint != null) {
                    int intValue = pathLenConstraint.intValue();
                    if (intValue < i2) {
                        return intValue;
                    }
                }
            }
            return i2;
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Basic constraints extension cannot be decoded.", e, certPath, i);
        }
    }

    protected static void prepareNextCertN(CertPath certPath, int i) throws CertPathValidatorException {
        boolean[] keyUsage = ((X509Certificate) certPath.getCertificates().get(i)).getKeyUsage();
        if (keyUsage != null && !keyUsage[5]) {
            throw new ExtCertPathValidatorException("Issuer certificate keyusage extension is critical and does not permit key signing.", null, certPath, i);
        }
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.List, code=java.util.List<java.security.cert.PKIXCertPathChecker>, for r5v0, types: [java.util.List, java.util.List<java.security.cert.PKIXCertPathChecker>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static void prepareNextCertO(java.security.cert.CertPath r2, int r3, java.util.Set r4, java.util.List<java.security.cert.PKIXCertPathChecker> r5) throws java.security.cert.CertPathValidatorException {
        /*
            java.util.List r0 = r2.getCertificates()
            java.lang.Object r0 = r0.get(r3)
            java.security.cert.X509Certificate r0 = (java.security.cert.X509Certificate) r0
            java.util.Iterator r5 = r5.iterator()
        L_0x000e:
            boolean r1 = r5.hasNext()
            if (r1 == 0) goto L_0x002d
            java.lang.Object r1 = r5.next()     // Catch:{ CertPathValidatorException -> 0x001e }
            java.security.cert.PKIXCertPathChecker r1 = (java.security.cert.PKIXCertPathChecker) r1     // Catch:{ CertPathValidatorException -> 0x001e }
            r1.check(r0, r4)     // Catch:{ CertPathValidatorException -> 0x001e }
            goto L_0x000e
        L_0x001e:
            r4 = move-exception
            java.security.cert.CertPathValidatorException r5 = new java.security.cert.CertPathValidatorException
            java.lang.String r0 = r4.getMessage()
            java.lang.Throwable r4 = r4.getCause()
            r5.<init>(r0, r4, r2, r3)
            throw r5
        L_0x002d:
            boolean r5 = r4.isEmpty()
            if (r5 == 0) goto L_0x0034
            return
        L_0x0034:
            org.bouncycastle.jce.exception.ExtCertPathValidatorException r5 = new org.bouncycastle.jce.exception.ExtCertPathValidatorException
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Certificate has unsupported critical extension: "
            r0.append(r1)
            r0.append(r4)
            java.lang.String r4 = r0.toString()
            r0 = 0
            r5.<init>(r4, r0, r2, r3)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jce.provider.RFC3280CertPathUtilities.prepareNextCertO(java.security.cert.CertPath, int, java.util.Set, java.util.List):void");
    }

    protected static Set processCRLA1i(Date date, PKIXExtendedParameters pKIXExtendedParameters, X509Certificate x509Certificate, X509CRL x509crl) throws AnnotatedException {
        HashSet hashSet = new HashSet();
        if (pKIXExtendedParameters.isUseDeltasEnabled()) {
            try {
                CRLDistPoint instance = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, FRESHEST_CRL));
                if (instance == null) {
                    try {
                        instance = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509crl, FRESHEST_CRL));
                    } catch (AnnotatedException e) {
                        throw new AnnotatedException("Freshest CRL extension could not be decoded from CRL.", e);
                    }
                }
                if (instance != null) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.addAll(pKIXExtendedParameters.getCRLStores());
                    try {
                        arrayList.addAll(CertPathValidatorUtilities.getAdditionalStoresFromCRLDistributionPoint(instance, pKIXExtendedParameters.getNamedCRLStoreMap()));
                        try {
                            hashSet.addAll(CertPathValidatorUtilities.getDeltaCRLs(date, x509crl, pKIXExtendedParameters.getCertStores(), arrayList));
                        } catch (AnnotatedException e2) {
                            throw new AnnotatedException("Exception obtaining delta CRLs.", e2);
                        }
                    } catch (AnnotatedException e3) {
                        throw new AnnotatedException("No new delta CRL locations could be added from Freshest CRL extension.", e3);
                    }
                }
            } catch (AnnotatedException e4) {
                throw new AnnotatedException("Freshest CRL extension could not be decoded from certificate.", e4);
            }
        }
        return hashSet;
    }

    protected static Set[] processCRLA1ii(Date date, PKIXExtendedParameters pKIXExtendedParameters, X509Certificate x509Certificate, X509CRL x509crl) throws AnnotatedException {
        HashSet hashSet = new HashSet();
        X509CRLSelector x509CRLSelector = new X509CRLSelector();
        x509CRLSelector.setCertificateChecking(x509Certificate);
        try {
            x509CRLSelector.addIssuerName(PrincipalUtils.getIssuerPrincipal(x509crl).getEncoded());
            PKIXCRLStoreSelector build = new Builder(x509CRLSelector).setCompleteCRLEnabled(true).build();
            if (pKIXExtendedParameters.getDate() != null) {
                date = pKIXExtendedParameters.getDate();
            }
            Set findCRLs = CRL_UTIL.findCRLs(build, date, pKIXExtendedParameters.getCertStores(), pKIXExtendedParameters.getCRLStores());
            if (pKIXExtendedParameters.isUseDeltasEnabled()) {
                try {
                    hashSet.addAll(CertPathValidatorUtilities.getDeltaCRLs(date, x509crl, pKIXExtendedParameters.getCertStores(), pKIXExtendedParameters.getCRLStores()));
                } catch (AnnotatedException e) {
                    throw new AnnotatedException("Exception obtaining delta CRLs.", e);
                }
            }
            return new Set[]{findCRLs, hashSet};
        } catch (IOException e2) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot extract issuer from CRL.");
            sb.append(e2);
            throw new AnnotatedException(sb.toString(), e2);
        }
    }

    protected static void processCRLB1(DistributionPoint distributionPoint, Object obj, X509CRL x509crl) throws AnnotatedException {
        boolean z;
        ASN1Primitive extensionValue = CertPathValidatorUtilities.getExtensionValue(x509crl, ISSUING_DISTRIBUTION_POINT);
        boolean z2 = extensionValue != null && IssuingDistributionPoint.getInstance(extensionValue).isIndirectCRL();
        try {
            byte[] encoded = PrincipalUtils.getIssuerPrincipal(x509crl).getEncoded();
            if (distributionPoint.getCRLIssuer() != null) {
                GeneralName[] names = distributionPoint.getCRLIssuer().getNames();
                z = false;
                for (int i = 0; i < names.length; i++) {
                    if (names[i].getTagNo() == 4) {
                        try {
                            if (Arrays.areEqual(names[i].getName().toASN1Primitive().getEncoded(), encoded)) {
                                z = true;
                            }
                        } catch (IOException e) {
                            throw new AnnotatedException("CRL issuer information from distribution point cannot be decoded.", e);
                        }
                    }
                }
                if (z && !z2) {
                    throw new AnnotatedException("Distribution point contains cRLIssuer field but CRL is not indirect.");
                } else if (!z) {
                    throw new AnnotatedException("CRL issuer of CRL does not match CRL issuer of distribution point.");
                }
            } else {
                z = PrincipalUtils.getIssuerPrincipal(x509crl).equals(PrincipalUtils.getEncodedIssuerPrincipal(obj));
            }
            if (!z) {
                throw new AnnotatedException("Cannot find matching CRL issuer for certificate.");
            }
        } catch (IOException e2) {
            StringBuilder sb = new StringBuilder();
            sb.append("Exception encoding CRL issuer: ");
            sb.append(e2.getMessage());
            throw new AnnotatedException(sb.toString(), e2);
        }
    }

    protected static void processCRLB2(DistributionPoint distributionPoint, Object obj, X509CRL x509crl) throws AnnotatedException {
        GeneralName[] generalNameArr;
        try {
            IssuingDistributionPoint instance = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509crl, ISSUING_DISTRIBUTION_POINT));
            if (instance != null) {
                if (instance.getDistributionPoint() != null) {
                    DistributionPointName distributionPoint2 = IssuingDistributionPoint.getInstance(instance).getDistributionPoint();
                    ArrayList arrayList = new ArrayList();
                    boolean z = false;
                    if (distributionPoint2.getType() == 0) {
                        GeneralName[] names = GeneralNames.getInstance(distributionPoint2.getName()).getNames();
                        for (GeneralName add : names) {
                            arrayList.add(add);
                        }
                    }
                    if (distributionPoint2.getType() == 1) {
                        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                        try {
                            Enumeration objects = ASN1Sequence.getInstance(PrincipalUtils.getIssuerPrincipal(x509crl)).getObjects();
                            while (objects.hasMoreElements()) {
                                aSN1EncodableVector.add((ASN1Encodable) objects.nextElement());
                            }
                            aSN1EncodableVector.add(distributionPoint2.getName());
                            arrayList.add(new GeneralName(X500Name.getInstance(new DERSequence(aSN1EncodableVector))));
                        } catch (Exception e) {
                            throw new AnnotatedException("Could not read CRL issuer.", e);
                        }
                    }
                    String str = "No match for certificate CRL issuing distribution point name to cRLIssuer CRL distribution point.";
                    if (distributionPoint.getDistributionPoint() != null) {
                        DistributionPointName distributionPoint3 = distributionPoint.getDistributionPoint();
                        GeneralName[] generalNameArr2 = null;
                        if (distributionPoint3.getType() == 0) {
                            generalNameArr2 = GeneralNames.getInstance(distributionPoint3.getName()).getNames();
                        }
                        if (distributionPoint3.getType() == 1) {
                            if (distributionPoint.getCRLIssuer() != null) {
                                generalNameArr = distributionPoint.getCRLIssuer().getNames();
                            } else {
                                generalNameArr = new GeneralName[1];
                                try {
                                    generalNameArr[0] = new GeneralName(X500Name.getInstance(PrincipalUtils.getEncodedIssuerPrincipal(obj).getEncoded()));
                                } catch (Exception e2) {
                                    throw new AnnotatedException("Could not read certificate issuer.", e2);
                                }
                            }
                            generalNameArr2 = generalNameArr;
                            for (int i = 0; i < generalNameArr2.length; i++) {
                                Enumeration objects2 = ASN1Sequence.getInstance(generalNameArr2[i].getName().toASN1Primitive()).getObjects();
                                ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
                                while (objects2.hasMoreElements()) {
                                    aSN1EncodableVector2.add((ASN1Encodable) objects2.nextElement());
                                }
                                aSN1EncodableVector2.add(distributionPoint3.getName());
                                generalNameArr2[i] = new GeneralName(X500Name.getInstance(new DERSequence(aSN1EncodableVector2)));
                            }
                        }
                        if (generalNameArr2 != null) {
                            int i2 = 0;
                            while (true) {
                                if (i2 >= generalNameArr2.length) {
                                    break;
                                } else if (arrayList.contains(generalNameArr2[i2])) {
                                    z = true;
                                    break;
                                } else {
                                    i2++;
                                }
                            }
                        }
                        if (!z) {
                            throw new AnnotatedException(str);
                        }
                    } else if (distributionPoint.getCRLIssuer() != null) {
                        GeneralName[] names2 = distributionPoint.getCRLIssuer().getNames();
                        int i3 = 0;
                        while (true) {
                            if (i3 >= names2.length) {
                                break;
                            } else if (arrayList.contains(names2[i3])) {
                                z = true;
                                break;
                            } else {
                                i3++;
                            }
                        }
                        if (!z) {
                            throw new AnnotatedException(str);
                        }
                    } else {
                        throw new AnnotatedException("Either the cRLIssuer or the distributionPoint field must be contained in DistributionPoint.");
                    }
                }
                try {
                    BasicConstraints instance2 = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Extension) obj, BASIC_CONSTRAINTS));
                    if (obj instanceof X509Certificate) {
                        if (instance.onlyContainsUserCerts() && instance2 != null && instance2.isCA()) {
                            throw new AnnotatedException("CA Cert CRL only contains user certificates.");
                        } else if (instance.onlyContainsCACerts() && (instance2 == null || !instance2.isCA())) {
                            throw new AnnotatedException("End CRL only contains CA certificates.");
                        }
                    }
                    if (instance.onlyContainsAttributeCerts()) {
                        throw new AnnotatedException("onlyContainsAttributeCerts boolean is asserted.");
                    }
                } catch (Exception e3) {
                    throw new AnnotatedException("Basic constraints extension could not be decoded.", e3);
                }
            }
        } catch (Exception e4) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", e4);
        }
    }

    protected static void processCRLC(X509CRL x509crl, X509CRL x509crl2, PKIXExtendedParameters pKIXExtendedParameters) throws AnnotatedException {
        if (x509crl != null) {
            try {
                IssuingDistributionPoint instance = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509crl2, ISSUING_DISTRIBUTION_POINT));
                if (pKIXExtendedParameters.isUseDeltasEnabled()) {
                    if (PrincipalUtils.getIssuerPrincipal(x509crl).equals(PrincipalUtils.getIssuerPrincipal(x509crl2))) {
                        try {
                            IssuingDistributionPoint instance2 = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509crl, ISSUING_DISTRIBUTION_POINT));
                            boolean z = true;
                            if (instance != null ? !instance.equals(instance2) : instance2 != null) {
                                z = false;
                            }
                            if (z) {
                                try {
                                    ASN1Primitive extensionValue = CertPathValidatorUtilities.getExtensionValue(x509crl2, AUTHORITY_KEY_IDENTIFIER);
                                    try {
                                        ASN1Primitive extensionValue2 = CertPathValidatorUtilities.getExtensionValue(x509crl, AUTHORITY_KEY_IDENTIFIER);
                                        if (extensionValue == null) {
                                            throw new AnnotatedException("CRL authority key identifier is null.");
                                        } else if (extensionValue2 == null) {
                                            throw new AnnotatedException("Delta CRL authority key identifier is null.");
                                        } else if (!extensionValue.equals(extensionValue2)) {
                                            throw new AnnotatedException("Delta CRL authority key identifier does not match complete CRL authority key identifier.");
                                        }
                                    } catch (AnnotatedException e) {
                                        throw new AnnotatedException("Authority key identifier extension could not be extracted from delta CRL.", e);
                                    }
                                } catch (AnnotatedException e2) {
                                    throw new AnnotatedException("Authority key identifier extension could not be extracted from complete CRL.", e2);
                                }
                            } else {
                                throw new AnnotatedException("Issuing distribution point extension from delta CRL and complete CRL does not match.");
                            }
                        } catch (Exception e3) {
                            throw new AnnotatedException("Issuing distribution point extension from delta CRL could not be decoded.", e3);
                        }
                    } else {
                        throw new AnnotatedException("Complete CRL issuer does not match delta CRL issuer.");
                    }
                }
            } catch (Exception e4) {
                throw new AnnotatedException("Issuing distribution point extension could not be decoded.", e4);
            }
        }
    }

    protected static ReasonsMask processCRLD(X509CRL x509crl, DistributionPoint distributionPoint) throws AnnotatedException {
        try {
            IssuingDistributionPoint instance = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509crl, ISSUING_DISTRIBUTION_POINT));
            if (instance != null && instance.getOnlySomeReasons() != null && distributionPoint.getReasons() != null) {
                return new ReasonsMask(distributionPoint.getReasons()).intersect(new ReasonsMask(instance.getOnlySomeReasons()));
            }
            if ((instance == null || instance.getOnlySomeReasons() == null) && distributionPoint.getReasons() == null) {
                return ReasonsMask.allReasons;
            }
            return (distributionPoint.getReasons() == null ? ReasonsMask.allReasons : new ReasonsMask(distributionPoint.getReasons())).intersect(instance == null ? ReasonsMask.allReasons : new ReasonsMask(instance.getOnlySomeReasons()));
        } catch (Exception e) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", e);
        }
    }

    protected static Set processCRLF(X509CRL x509crl, Object obj, X509Certificate x509Certificate, PublicKey publicKey, PKIXExtendedParameters pKIXExtendedParameters, List list, JcaJceHelper jcaJceHelper) throws AnnotatedException {
        int i;
        X509CertSelector x509CertSelector = new X509CertSelector();
        try {
            x509CertSelector.setSubject(PrincipalUtils.getIssuerPrincipal(x509crl).getEncoded());
            PKIXCertStoreSelector build = new PKIXCertStoreSelector.Builder(x509CertSelector).build();
            try {
                Collection findCertificates = CertPathValidatorUtilities.findCertificates(build, pKIXExtendedParameters.getCertificateStores());
                findCertificates.addAll(CertPathValidatorUtilities.findCertificates(build, pKIXExtendedParameters.getCertStores()));
                findCertificates.add(x509Certificate);
                Iterator it = findCertificates.iterator();
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    X509Certificate x509Certificate2 = (X509Certificate) it.next();
                    if (x509Certificate2.equals(x509Certificate)) {
                        arrayList.add(x509Certificate2);
                        arrayList2.add(publicKey);
                    } else {
                        try {
                            PKIXCertPathBuilderSpi pKIXCertPathBuilderSpi = new PKIXCertPathBuilderSpi();
                            X509CertSelector x509CertSelector2 = new X509CertSelector();
                            x509CertSelector2.setCertificate(x509Certificate2);
                            PKIXExtendedParameters.Builder targetConstraints = new PKIXExtendedParameters.Builder(pKIXExtendedParameters).setTargetConstraints(new PKIXCertStoreSelector.Builder(x509CertSelector2).build());
                            if (list.contains(x509Certificate2)) {
                                targetConstraints.setRevocationEnabled(false);
                            } else {
                                targetConstraints.setRevocationEnabled(true);
                            }
                            List certificates = pKIXCertPathBuilderSpi.engineBuild(new PKIXExtendedBuilderParameters.Builder(targetConstraints.build()).build()).getCertPath().getCertificates();
                            arrayList.add(x509Certificate2);
                            arrayList2.add(CertPathValidatorUtilities.getNextWorkingKey(certificates, 0, jcaJceHelper));
                        } catch (CertPathBuilderException e) {
                            throw new AnnotatedException("CertPath for CRL signer failed to validate.", e);
                        } catch (CertPathValidatorException e2) {
                            throw new AnnotatedException("Public key of issuer certificate of CRL could not be retrieved.", e2);
                        } catch (Exception e3) {
                            throw new AnnotatedException(e3.getMessage());
                        }
                    }
                }
                HashSet hashSet = new HashSet();
                AnnotatedException annotatedException = null;
                for (i = 0; i < arrayList.size(); i++) {
                    boolean[] keyUsage = ((X509Certificate) arrayList.get(i)).getKeyUsage();
                    if (keyUsage == null || (keyUsage.length >= 7 && keyUsage[6])) {
                        hashSet.add(arrayList2.get(i));
                    } else {
                        annotatedException = new AnnotatedException("Issuer certificate key usage extension does not permit CRL signing.");
                    }
                }
                if (hashSet.isEmpty() && annotatedException == null) {
                    throw new AnnotatedException("Cannot find a valid issuer certificate.");
                } else if (!hashSet.isEmpty() || annotatedException == null) {
                    return hashSet;
                } else {
                    throw annotatedException;
                }
            } catch (AnnotatedException e4) {
                throw new AnnotatedException("Issuer certificate for CRL cannot be searched.", e4);
            }
        } catch (IOException e5) {
            throw new AnnotatedException("Subject criteria for certificate selector to find issuer certificate for CRL could not be set.", e5);
        }
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.security.PublicKey>, for r3v0, types: [java.util.Set<java.security.PublicKey>, java.util.Set] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static java.security.PublicKey processCRLG(java.security.cert.X509CRL r2, java.util.Set<java.security.PublicKey> r3) throws org.bouncycastle.jce.provider.AnnotatedException {
        /*
            java.util.Iterator r3 = r3.iterator()
            r0 = 0
        L_0x0005:
            boolean r1 = r3.hasNext()
            if (r1 == 0) goto L_0x0017
            java.lang.Object r0 = r3.next()
            java.security.PublicKey r0 = (java.security.PublicKey) r0
            r2.verify(r0)     // Catch:{ Exception -> 0x0015 }
            return r0
        L_0x0015:
            r0 = move-exception
            goto L_0x0005
        L_0x0017:
            org.bouncycastle.jce.provider.AnnotatedException r2 = new org.bouncycastle.jce.provider.AnnotatedException
            java.lang.String r3 = "Cannot verify CRL."
            r2.<init>(r3, r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jce.provider.RFC3280CertPathUtilities.processCRLG(java.security.cert.X509CRL, java.util.Set):java.security.PublicKey");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.security.cert.X509CRL>, for r3v0, types: [java.util.Set, java.util.Set<java.security.cert.X509CRL>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static java.security.cert.X509CRL processCRLH(java.util.Set<java.security.cert.X509CRL> r3, java.security.PublicKey r4) throws org.bouncycastle.jce.provider.AnnotatedException {
        /*
            java.util.Iterator r3 = r3.iterator()
            r0 = 0
            r1 = r0
        L_0x0006:
            boolean r2 = r3.hasNext()
            if (r2 == 0) goto L_0x0018
            java.lang.Object r1 = r3.next()
            java.security.cert.X509CRL r1 = (java.security.cert.X509CRL) r1
            r1.verify(r4)     // Catch:{ Exception -> 0x0016 }
            return r1
        L_0x0016:
            r1 = move-exception
            goto L_0x0006
        L_0x0018:
            if (r1 != 0) goto L_0x001b
            return r0
        L_0x001b:
            org.bouncycastle.jce.provider.AnnotatedException r3 = new org.bouncycastle.jce.provider.AnnotatedException
            java.lang.String r4 = "Cannot verify delta CRL."
            r3.<init>(r4, r1)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jce.provider.RFC3280CertPathUtilities.processCRLH(java.util.Set, java.security.PublicKey):java.security.cert.X509CRL");
    }

    protected static void processCRLI(Date date, X509CRL x509crl, Object obj, CertStatus certStatus, PKIXExtendedParameters pKIXExtendedParameters) throws AnnotatedException {
        if (pKIXExtendedParameters.isUseDeltasEnabled() && x509crl != null) {
            CertPathValidatorUtilities.getCertStatus(date, x509crl, obj, certStatus);
        }
    }

    protected static void processCRLJ(Date date, X509CRL x509crl, Object obj, CertStatus certStatus) throws AnnotatedException {
        if (certStatus.getCertStatus() == 11) {
            CertPathValidatorUtilities.getCertStatus(date, x509crl, obj, certStatus);
        }
    }

    protected static void processCertA(CertPath certPath, PKIXExtendedParameters pKIXExtendedParameters, int i, PublicKey publicKey, boolean z, X500Name x500Name, X509Certificate x509Certificate, JcaJceHelper jcaJceHelper) throws ExtCertPathValidatorException {
        CertPath certPath2 = certPath;
        PKIXExtendedParameters pKIXExtendedParameters2 = pKIXExtendedParameters;
        int i2 = i;
        X500Name x500Name2 = x500Name;
        String str = "Could not validate certificate: ";
        List certificates = certPath.getCertificates();
        X509Certificate x509Certificate2 = (X509Certificate) certificates.get(i);
        if (!z) {
            try {
                PublicKey publicKey2 = publicKey;
                CertPathValidatorUtilities.verifyX509Certificate(x509Certificate2, publicKey, pKIXExtendedParameters.getSigProvider());
            } catch (GeneralSecurityException e) {
                throw new ExtCertPathValidatorException("Could not validate certificate signature.", e, certPath, i);
            }
        } else {
            PublicKey publicKey3 = publicKey;
        }
        try {
            x509Certificate2.checkValidity(CertPathValidatorUtilities.getValidCertDateFromValidityModel(pKIXExtendedParameters, certPath, i));
            if (pKIXExtendedParameters.isRevocationEnabled()) {
                try {
                    checkCRLs(pKIXExtendedParameters, x509Certificate2, CertPathValidatorUtilities.getValidCertDateFromValidityModel(pKIXExtendedParameters, certPath, i), x509Certificate, publicKey, certificates, jcaJceHelper);
                } catch (AnnotatedException e2) {
                    throw new ExtCertPathValidatorException(e2.getMessage(), e2.getCause() != null ? e2.getCause() : e2, certPath, i);
                }
            }
            if (!PrincipalUtils.getEncodedIssuerPrincipal(x509Certificate2).equals(x500Name2)) {
                StringBuilder sb = new StringBuilder();
                sb.append("IssuerName(");
                sb.append(PrincipalUtils.getEncodedIssuerPrincipal(x509Certificate2));
                sb.append(") does not match SubjectName(");
                sb.append(x500Name2);
                sb.append(") of signing certificate.");
                throw new ExtCertPathValidatorException(sb.toString(), null, certPath, i);
            }
        } catch (CertificateExpiredException e3) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(e3.getMessage());
            throw new ExtCertPathValidatorException(sb2.toString(), e3, certPath, i);
        } catch (CertificateNotYetValidException e4) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            sb3.append(e4.getMessage());
            throw new ExtCertPathValidatorException(sb3.toString(), e4, certPath, i);
        } catch (AnnotatedException e5) {
            throw new ExtCertPathValidatorException("Could not validate time of certificate.", e5, certPath, i);
        }
    }

    protected static void processCertBC(CertPath certPath, int i, PKIXNameConstraintValidator pKIXNameConstraintValidator) throws CertPathValidatorException {
        List certificates = certPath.getCertificates();
        X509Certificate x509Certificate = (X509Certificate) certificates.get(i);
        int size = certificates.size();
        int i2 = size - i;
        if (!CertPathValidatorUtilities.isSelfIssued(x509Certificate) || i2 >= size) {
            try {
                ASN1Sequence instance = DERSequence.getInstance(PrincipalUtils.getSubjectPrincipal(x509Certificate).getEncoded());
                try {
                    pKIXNameConstraintValidator.checkPermittedDN(instance);
                    pKIXNameConstraintValidator.checkExcludedDN(instance);
                    try {
                        GeneralNames instance2 = GeneralNames.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, SUBJECT_ALTERNATIVE_NAME));
                        RDN[] rDNs = X500Name.getInstance(instance).getRDNs(BCStyle.EmailAddress);
                        int i3 = 0;
                        int i4 = 0;
                        while (i4 != rDNs.length) {
                            GeneralName generalName = new GeneralName(1, ((ASN1String) rDNs[i4].getFirst().getValue()).getString());
                            try {
                                pKIXNameConstraintValidator.checkPermitted(generalName);
                                pKIXNameConstraintValidator.checkExcluded(generalName);
                                i4++;
                            } catch (PKIXNameConstraintValidatorException e) {
                                throw new CertPathValidatorException("Subtree check for certificate subject alternative email failed.", e, certPath, i);
                            }
                        }
                        if (instance2 != null) {
                            try {
                                GeneralName[] names = instance2.getNames();
                                while (i3 < names.length) {
                                    try {
                                        pKIXNameConstraintValidator.checkPermitted(names[i3]);
                                        pKIXNameConstraintValidator.checkExcluded(names[i3]);
                                        i3++;
                                    } catch (PKIXNameConstraintValidatorException e2) {
                                        throw new CertPathValidatorException("Subtree check for certificate subject alternative name failed.", e2, certPath, i);
                                    }
                                }
                            } catch (Exception e3) {
                                throw new CertPathValidatorException("Subject alternative name contents could not be decoded.", e3, certPath, i);
                            }
                        }
                    } catch (Exception e4) {
                        throw new CertPathValidatorException("Subject alternative name extension could not be decoded.", e4, certPath, i);
                    }
                } catch (PKIXNameConstraintValidatorException e5) {
                    throw new CertPathValidatorException("Subtree check for certificate subject failed.", e5, certPath, i);
                }
            } catch (Exception e6) {
                throw new CertPathValidatorException("Exception extracting subject name when checking subtrees.", e6, certPath, i);
            }
        }
    }

    protected static PKIXPolicyNode processCertD(CertPath certPath, int i, Set set, PKIXPolicyNode pKIXPolicyNode, List[] listArr, int i2) throws CertPathValidatorException {
        String str;
        String id;
        CertPath certPath2 = certPath;
        int i3 = i;
        Set set2 = set;
        List[] listArr2 = listArr;
        List certificates = certPath.getCertificates();
        X509Certificate x509Certificate = (X509Certificate) certificates.get(i3);
        int size = certificates.size();
        int i4 = size - i3;
        try {
            ASN1Sequence instance = DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, CERTIFICATE_POLICIES));
            if (instance == null || pKIXPolicyNode == null) {
                return null;
            }
            Enumeration objects = instance.getObjects();
            HashSet hashSet = new HashSet();
            while (true) {
                boolean hasMoreElements = objects.hasMoreElements();
                str = ANY_POLICY;
                if (!hasMoreElements) {
                    break;
                }
                PolicyInformation instance2 = PolicyInformation.getInstance(objects.nextElement());
                ASN1ObjectIdentifier policyIdentifier = instance2.getPolicyIdentifier();
                hashSet.add(policyIdentifier.getId());
                if (!str.equals(policyIdentifier.getId())) {
                    try {
                        Set qualifierSet = CertPathValidatorUtilities.getQualifierSet(instance2.getPolicyQualifiers());
                        if (!CertPathValidatorUtilities.processCertD1i(i4, listArr2, policyIdentifier, qualifierSet)) {
                            CertPathValidatorUtilities.processCertD1ii(i4, listArr2, policyIdentifier, qualifierSet);
                        }
                    } catch (CertPathValidatorException e) {
                        throw new ExtCertPathValidatorException("Policy qualifier info set could not be build.", e, certPath2, i3);
                    }
                }
            }
            if (set.isEmpty() || set2.contains(str)) {
                set.clear();
                set2.addAll(hashSet);
            } else {
                HashSet hashSet2 = new HashSet();
                for (Object next : set) {
                    if (hashSet.contains(next)) {
                        hashSet2.add(next);
                    }
                }
                set.clear();
                set2.addAll(hashSet2);
            }
            if (i2 > 0 || (i4 < size && CertPathValidatorUtilities.isSelfIssued(x509Certificate))) {
                Enumeration objects2 = instance.getObjects();
                while (true) {
                    if (!objects2.hasMoreElements()) {
                        break;
                    }
                    PolicyInformation instance3 = PolicyInformation.getInstance(objects2.nextElement());
                    if (str.equals(instance3.getPolicyIdentifier().getId())) {
                        Set qualifierSet2 = CertPathValidatorUtilities.getQualifierSet(instance3.getPolicyQualifiers());
                        List list = listArr2[i4 - 1];
                        for (int i5 = 0; i5 < list.size(); i5++) {
                            PKIXPolicyNode pKIXPolicyNode2 = (PKIXPolicyNode) list.get(i5);
                            for (Object next2 : pKIXPolicyNode2.getExpectedPolicies()) {
                                if (next2 instanceof String) {
                                    id = (String) next2;
                                } else if (next2 instanceof ASN1ObjectIdentifier) {
                                    id = ((ASN1ObjectIdentifier) next2).getId();
                                }
                                String str2 = id;
                                Iterator children = pKIXPolicyNode2.getChildren();
                                boolean z = false;
                                while (children.hasNext()) {
                                    if (str2.equals(((PKIXPolicyNode) children.next()).getValidPolicy())) {
                                        z = true;
                                    }
                                }
                                if (!z) {
                                    HashSet hashSet3 = new HashSet();
                                    hashSet3.add(str2);
                                    PKIXPolicyNode pKIXPolicyNode3 = r6;
                                    PKIXPolicyNode pKIXPolicyNode4 = new PKIXPolicyNode(new ArrayList(), i4, hashSet3, pKIXPolicyNode2, qualifierSet2, str2, false);
                                    pKIXPolicyNode2.addChild(pKIXPolicyNode3);
                                    listArr2[i4].add(pKIXPolicyNode3);
                                }
                            }
                        }
                    }
                }
            }
            PKIXPolicyNode pKIXPolicyNode5 = pKIXPolicyNode;
            for (int i6 = i4 - 1; i6 >= 0; i6--) {
                List list2 = listArr2[i6];
                PKIXPolicyNode pKIXPolicyNode6 = pKIXPolicyNode5;
                for (int i7 = 0; i7 < list2.size(); i7++) {
                    PKIXPolicyNode pKIXPolicyNode7 = (PKIXPolicyNode) list2.get(i7);
                    if (!pKIXPolicyNode7.hasChildren()) {
                        pKIXPolicyNode6 = CertPathValidatorUtilities.removePolicyNode(pKIXPolicyNode6, listArr2, pKIXPolicyNode7);
                        if (pKIXPolicyNode6 == null) {
                            break;
                        }
                    }
                }
                pKIXPolicyNode5 = pKIXPolicyNode6;
            }
            Set criticalExtensionOIDs = x509Certificate.getCriticalExtensionOIDs();
            if (criticalExtensionOIDs != null) {
                boolean contains = criticalExtensionOIDs.contains(CERTIFICATE_POLICIES);
                List list3 = listArr2[i4];
                for (int i8 = 0; i8 < list3.size(); i8++) {
                    ((PKIXPolicyNode) list3.get(i8)).setCritical(contains);
                }
            }
            return pKIXPolicyNode5;
        } catch (AnnotatedException e2) {
            throw new ExtCertPathValidatorException("Could not read certificate policies extension from certificate.", e2, certPath2, i3);
        }
    }

    protected static PKIXPolicyNode processCertE(CertPath certPath, int i, PKIXPolicyNode pKIXPolicyNode) throws CertPathValidatorException {
        try {
            if (DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), CERTIFICATE_POLICIES)) == null) {
                return null;
            }
            return pKIXPolicyNode;
        } catch (AnnotatedException e) {
            throw new ExtCertPathValidatorException("Could not read certificate policies extension from certificate.", e, certPath, i);
        }
    }

    protected static void processCertF(CertPath certPath, int i, PKIXPolicyNode pKIXPolicyNode, int i2) throws CertPathValidatorException {
        if (i2 <= 0 && pKIXPolicyNode == null) {
            throw new ExtCertPathValidatorException("No valid policy tree found when one expected.", null, certPath, i);
        }
    }

    protected static int wrapupCertA(int i, X509Certificate x509Certificate) {
        return (CertPathValidatorUtilities.isSelfIssued(x509Certificate) || i == 0) ? i : i - 1;
    }

    protected static int wrapupCertB(CertPath certPath, int i, int i2) throws CertPathValidatorException {
        try {
            ASN1Sequence instance = DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), POLICY_CONSTRAINTS));
            if (instance != null) {
                Enumeration objects = instance.getObjects();
                while (objects.hasMoreElements()) {
                    ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) objects.nextElement();
                    if (aSN1TaggedObject.getTagNo() == 0) {
                        try {
                            if (ASN1Integer.getInstance(aSN1TaggedObject, false).getValue().intValue() == 0) {
                                return 0;
                            }
                        } catch (Exception e) {
                            throw new ExtCertPathValidatorException("Policy constraints requireExplicitPolicy field could not be decoded.", e, certPath, i);
                        }
                    }
                }
            }
            return i2;
        } catch (AnnotatedException e2) {
            throw new ExtCertPathValidatorException("Policy constraints could not be decoded.", e2, certPath, i);
        }
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.List, code=java.util.List<java.security.cert.PKIXCertPathChecker>, for r4v0, types: [java.util.List, java.util.List<java.security.cert.PKIXCertPathChecker>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static void wrapupCertF(java.security.cert.CertPath r2, int r3, java.util.List<java.security.cert.PKIXCertPathChecker> r4, java.util.Set r5) throws java.security.cert.CertPathValidatorException {
        /*
            java.util.List r0 = r2.getCertificates()
            java.lang.Object r0 = r0.get(r3)
            java.security.cert.X509Certificate r0 = (java.security.cert.X509Certificate) r0
            java.util.Iterator r4 = r4.iterator()
        L_0x000e:
            boolean r1 = r4.hasNext()
            if (r1 == 0) goto L_0x0032
            java.lang.Object r1 = r4.next()     // Catch:{ CertPathValidatorException -> 0x0027, Exception -> 0x001e }
            java.security.cert.PKIXCertPathChecker r1 = (java.security.cert.PKIXCertPathChecker) r1     // Catch:{ CertPathValidatorException -> 0x0027, Exception -> 0x001e }
            r1.check(r0, r5)     // Catch:{ CertPathValidatorException -> 0x0027, Exception -> 0x001e }
            goto L_0x000e
        L_0x001e:
            r4 = move-exception
            java.security.cert.CertPathValidatorException r5 = new java.security.cert.CertPathValidatorException
            java.lang.String r0 = "Additional certificate path checker failed."
            r5.<init>(r0, r4, r2, r3)
            throw r5
        L_0x0027:
            r4 = move-exception
            org.bouncycastle.jce.exception.ExtCertPathValidatorException r5 = new org.bouncycastle.jce.exception.ExtCertPathValidatorException
            java.lang.String r0 = r4.getMessage()
            r5.<init>(r0, r4, r2, r3)
            throw r5
        L_0x0032:
            boolean r4 = r5.isEmpty()
            if (r4 == 0) goto L_0x0039
            return
        L_0x0039:
            org.bouncycastle.jce.exception.ExtCertPathValidatorException r4 = new org.bouncycastle.jce.exception.ExtCertPathValidatorException
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Certificate has unsupported critical extension: "
            r0.append(r1)
            r0.append(r5)
            java.lang.String r5 = r0.toString()
            r0 = 0
            r4.<init>(r5, r0, r2, r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jce.provider.RFC3280CertPathUtilities.wrapupCertF(java.security.cert.CertPath, int, java.util.List, java.util.Set):void");
    }

    protected static PKIXPolicyNode wrapupCertG(CertPath certPath, PKIXExtendedParameters pKIXExtendedParameters, Set set, int i, List[] listArr, PKIXPolicyNode pKIXPolicyNode, Set set2) throws CertPathValidatorException {
        int size = certPath.getCertificates().size();
        String str = "Explicit policy requested but none available.";
        if (pKIXPolicyNode != null) {
            boolean isAnyPolicy = CertPathValidatorUtilities.isAnyPolicy(set);
            String str2 = ANY_POLICY;
            if (!isAnyPolicy) {
                HashSet<PKIXPolicyNode> hashSet = new HashSet<>();
                for (List list : listArr) {
                    for (int i2 = 0; i2 < list.size(); i2++) {
                        PKIXPolicyNode pKIXPolicyNode2 = (PKIXPolicyNode) list.get(i2);
                        if (str2.equals(pKIXPolicyNode2.getValidPolicy())) {
                            Iterator children = pKIXPolicyNode2.getChildren();
                            while (children.hasNext()) {
                                PKIXPolicyNode pKIXPolicyNode3 = (PKIXPolicyNode) children.next();
                                if (!str2.equals(pKIXPolicyNode3.getValidPolicy())) {
                                    hashSet.add(pKIXPolicyNode3);
                                }
                            }
                        }
                    }
                }
                for (PKIXPolicyNode pKIXPolicyNode4 : hashSet) {
                    if (!set.contains(pKIXPolicyNode4.getValidPolicy())) {
                        pKIXPolicyNode = CertPathValidatorUtilities.removePolicyNode(pKIXPolicyNode, listArr, pKIXPolicyNode4);
                    }
                }
                if (pKIXPolicyNode != null) {
                    for (int i3 = size - 1; i3 >= 0; i3--) {
                        List list2 = listArr[i3];
                        for (int i4 = 0; i4 < list2.size(); i4++) {
                            PKIXPolicyNode pKIXPolicyNode5 = (PKIXPolicyNode) list2.get(i4);
                            if (!pKIXPolicyNode5.hasChildren()) {
                                pKIXPolicyNode = CertPathValidatorUtilities.removePolicyNode(pKIXPolicyNode, listArr, pKIXPolicyNode5);
                            }
                        }
                    }
                }
            } else if (pKIXExtendedParameters.isExplicitPolicyRequired()) {
                if (!set2.isEmpty()) {
                    HashSet<PKIXPolicyNode> hashSet2 = new HashSet<>();
                    for (List list3 : listArr) {
                        for (int i5 = 0; i5 < list3.size(); i5++) {
                            PKIXPolicyNode pKIXPolicyNode6 = (PKIXPolicyNode) list3.get(i5);
                            if (str2.equals(pKIXPolicyNode6.getValidPolicy())) {
                                Iterator children2 = pKIXPolicyNode6.getChildren();
                                while (children2.hasNext()) {
                                    hashSet2.add(children2.next());
                                }
                            }
                        }
                    }
                    for (PKIXPolicyNode validPolicy : hashSet2) {
                        set2.contains(validPolicy.getValidPolicy());
                    }
                    if (pKIXPolicyNode != null) {
                        for (int i6 = size - 1; i6 >= 0; i6--) {
                            List list4 = listArr[i6];
                            for (int i7 = 0; i7 < list4.size(); i7++) {
                                PKIXPolicyNode pKIXPolicyNode7 = (PKIXPolicyNode) list4.get(i7);
                                if (!pKIXPolicyNode7.hasChildren()) {
                                    pKIXPolicyNode = CertPathValidatorUtilities.removePolicyNode(pKIXPolicyNode, listArr, pKIXPolicyNode7);
                                }
                            }
                        }
                    }
                } else {
                    throw new ExtCertPathValidatorException(str, null, certPath, i);
                }
            }
            return pKIXPolicyNode;
        } else if (!pKIXExtendedParameters.isExplicitPolicyRequired()) {
            return null;
        } else {
            throw new ExtCertPathValidatorException(str, null, certPath, i);
        }
    }
}