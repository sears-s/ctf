package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jcajce.PKIXCertStoreSelector.Builder;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.x509.X509AttributeCertificate;
import org.bouncycastle.x509.X509CertStoreSelector;

class RFC3281CertPathUtilities {
    private static final String AUTHORITY_INFO_ACCESS = Extension.authorityInfoAccess.getId();
    private static final String CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
    private static final String NO_REV_AVAIL = Extension.noRevAvail.getId();
    private static final String TARGET_INFORMATION = Extension.targetInformation.getId();

    RFC3281CertPathUtilities() {
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.lang.String>, for r5v0, types: [java.util.Set, java.util.Set<java.lang.String>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static void additionalChecks(org.bouncycastle.x509.X509AttributeCertificate r3, java.util.Set r4, java.util.Set<java.lang.String> r5) throws java.security.cert.CertPathValidatorException {
        /*
            java.util.Iterator r4 = r4.iterator()
        L_0x0004:
            boolean r0 = r4.hasNext()
            java.lang.String r1 = "."
            if (r0 == 0) goto L_0x0033
            java.lang.Object r0 = r4.next()
            java.lang.String r0 = (java.lang.String) r0
            org.bouncycastle.x509.X509Attribute[] r2 = r3.getAttributes(r0)
            if (r2 != 0) goto L_0x0019
            goto L_0x0004
        L_0x0019:
            java.security.cert.CertPathValidatorException r3 = new java.security.cert.CertPathValidatorException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Attribute certificate contains prohibited attribute: "
            r4.append(r5)
            r4.append(r0)
            r4.append(r1)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            throw r3
        L_0x0033:
            java.util.Iterator r4 = r5.iterator()
        L_0x0037:
            boolean r5 = r4.hasNext()
            if (r5 == 0) goto L_0x0064
            java.lang.Object r5 = r4.next()
            java.lang.String r5 = (java.lang.String) r5
            org.bouncycastle.x509.X509Attribute[] r0 = r3.getAttributes(r5)
            if (r0 == 0) goto L_0x004a
            goto L_0x0037
        L_0x004a:
            java.security.cert.CertPathValidatorException r3 = new java.security.cert.CertPathValidatorException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r0 = "Attribute certificate does not contain necessary attribute: "
            r4.append(r0)
            r4.append(r5)
            r4.append(r1)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            throw r3
        L_0x0064:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jce.provider.RFC3281CertPathUtilities.additionalChecks(org.bouncycastle.x509.X509AttributeCertificate, java.util.Set, java.util.Set):void");
    }

    private static void checkCRL(DistributionPoint distributionPoint, X509AttributeCertificate x509AttributeCertificate, PKIXExtendedParameters pKIXExtendedParameters, Date date, X509Certificate x509Certificate, CertStatus certStatus, ReasonsMask reasonsMask, List list, JcaJceHelper jcaJceHelper) throws AnnotatedException {
        Iterator it;
        int i;
        DistributionPoint distributionPoint2 = distributionPoint;
        X509AttributeCertificate x509AttributeCertificate2 = x509AttributeCertificate;
        PKIXExtendedParameters pKIXExtendedParameters2 = pKIXExtendedParameters;
        Date date2 = date;
        CertStatus certStatus2 = certStatus;
        ReasonsMask reasonsMask2 = reasonsMask;
        if (x509AttributeCertificate2.getExtensionValue(X509Extensions.NoRevAvail.getId()) == null) {
            Date date3 = new Date(System.currentTimeMillis());
            if (date.getTime() <= date3.getTime()) {
                Iterator it2 = CertPathValidatorUtilities.getCompleteCRLs(distributionPoint2, x509AttributeCertificate2, date3, pKIXExtendedParameters2).iterator();
                int i2 = 1;
                int i3 = 0;
                e = null;
                while (it2.hasNext() && certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons()) {
                    try {
                        X509CRL x509crl = (X509CRL) it2.next();
                        ReasonsMask processCRLD = RFC3280CertPathUtilities.processCRLD(x509crl, distributionPoint2);
                        if (!processCRLD.hasNewReasons(reasonsMask2)) {
                            continue;
                        } else {
                            ReasonsMask reasonsMask3 = processCRLD;
                            it = it2;
                            i = i2;
                            try {
                                X509CRL x509crl2 = x509crl;
                                X509CRL processCRLH = pKIXExtendedParameters.isUseDeltasEnabled() ? RFC3280CertPathUtilities.processCRLH(CertPathValidatorUtilities.getDeltaCRLs(date3, x509crl2, pKIXExtendedParameters.getCertStores(), pKIXExtendedParameters.getCRLStores()), RFC3280CertPathUtilities.processCRLG(x509crl2, RFC3280CertPathUtilities.processCRLF(x509crl, x509AttributeCertificate, null, null, pKIXExtendedParameters, list, jcaJceHelper))) : null;
                                if (pKIXExtendedParameters.getValidityModel() != i) {
                                    if (x509AttributeCertificate.getNotAfter().getTime() < x509crl2.getThisUpdate().getTime()) {
                                        throw new AnnotatedException("No valid CRL for current time found.");
                                    }
                                }
                                RFC3280CertPathUtilities.processCRLB1(distributionPoint2, x509AttributeCertificate2, x509crl2);
                                RFC3280CertPathUtilities.processCRLB2(distributionPoint2, x509AttributeCertificate2, x509crl2);
                                RFC3280CertPathUtilities.processCRLC(processCRLH, x509crl2, pKIXExtendedParameters2);
                                RFC3280CertPathUtilities.processCRLI(date2, processCRLH, x509AttributeCertificate2, certStatus2, pKIXExtendedParameters2);
                                RFC3280CertPathUtilities.processCRLJ(date2, x509crl2, x509AttributeCertificate2, certStatus2);
                                if (certStatus.getCertStatus() == 8) {
                                    certStatus2.setCertStatus(11);
                                }
                                reasonsMask2.addReasons(reasonsMask3);
                                i2 = i;
                                i3 = i2;
                            } catch (AnnotatedException e) {
                                e = e;
                                i2 = i;
                                it2 = it;
                            }
                            it2 = it;
                        }
                    } catch (AnnotatedException e2) {
                        e = e2;
                        it = it2;
                        i = i2;
                        i2 = i;
                        it2 = it;
                    }
                }
                if (i3 == 0) {
                    throw e;
                }
                return;
            }
            throw new AnnotatedException("Validation time is in future.");
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:54:0x0118  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0175  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static void checkCRLs(org.bouncycastle.x509.X509AttributeCertificate r18, org.bouncycastle.jcajce.PKIXExtendedParameters r19, java.security.cert.X509Certificate r20, java.util.Date r21, java.util.List r22, org.bouncycastle.jcajce.util.JcaJceHelper r23) throws java.security.cert.CertPathValidatorException {
        /*
            r10 = r18
            boolean r0 = r19.isRevocationEnabled()
            if (r0 == 0) goto L_0x01a8
            java.lang.String r0 = NO_REV_AVAIL
            byte[] r0 = r10.getExtensionValue(r0)
            if (r0 != 0) goto L_0x018f
            java.lang.String r0 = CRL_DISTRIBUTION_POINTS     // Catch:{ AnnotatedException -> 0x0186 }
            org.bouncycastle.asn1.ASN1Primitive r0 = org.bouncycastle.jce.provider.CertPathValidatorUtilities.getExtensionValue(r10, r0)     // Catch:{ AnnotatedException -> 0x0186 }
            org.bouncycastle.asn1.x509.CRLDistPoint r0 = org.bouncycastle.asn1.x509.CRLDistPoint.getInstance(r0)     // Catch:{ AnnotatedException -> 0x0186 }
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.Map r2 = r19.getNamedCRLStoreMap()     // Catch:{ AnnotatedException -> 0x017d }
            java.util.List r2 = org.bouncycastle.jce.provider.CertPathValidatorUtilities.getAdditionalStoresFromCRLDistributionPoint(r0, r2)     // Catch:{ AnnotatedException -> 0x017d }
            r1.addAll(r2)     // Catch:{ AnnotatedException -> 0x017d }
            org.bouncycastle.jcajce.PKIXExtendedParameters$Builder r2 = new org.bouncycastle.jcajce.PKIXExtendedParameters$Builder
            r3 = r19
            r2.<init>(r3)
            java.util.Iterator r3 = r1.iterator()
        L_0x0035:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x0042
            r4 = r1
            org.bouncycastle.jcajce.PKIXCRLStore r4 = (org.bouncycastle.jcajce.PKIXCRLStore) r4
            r2.addCRLStore(r4)
            goto L_0x0035
        L_0x0042:
            org.bouncycastle.jcajce.PKIXExtendedParameters r11 = r2.build()
            org.bouncycastle.jce.provider.CertStatus r12 = new org.bouncycastle.jce.provider.CertStatus
            r12.<init>()
            org.bouncycastle.jce.provider.ReasonsMask r13 = new org.bouncycastle.jce.provider.ReasonsMask
            r13.<init>()
            java.lang.String r14 = "No valid CRL for distribution point found."
            r9 = 0
            r8 = 11
            r7 = 0
            if (r0 == 0) goto L_0x00aa
            org.bouncycastle.asn1.x509.DistributionPoint[] r0 = r0.getDistributionPoints()     // Catch:{ Exception -> 0x00a0 }
            r6 = r7
            r16 = r6
        L_0x005f:
            int r1 = r0.length     // Catch:{ AnnotatedException -> 0x0097 }
            if (r6 >= r1) goto L_0x0094
            int r1 = r12.getCertStatus()     // Catch:{ AnnotatedException -> 0x0097 }
            if (r1 != r8) goto L_0x0094
            boolean r1 = r13.isAllReasons()     // Catch:{ AnnotatedException -> 0x0097 }
            if (r1 != 0) goto L_0x0094
            java.lang.Object r1 = r11.clone()     // Catch:{ AnnotatedException -> 0x0097 }
            r3 = r1
            org.bouncycastle.jcajce.PKIXExtendedParameters r3 = (org.bouncycastle.jcajce.PKIXExtendedParameters) r3     // Catch:{ AnnotatedException -> 0x0097 }
            r1 = r0[r6]     // Catch:{ AnnotatedException -> 0x0097 }
            r2 = r18
            r4 = r21
            r5 = r20
            r17 = r6
            r6 = r12
            r15 = r7
            r7 = r13
            r15 = r8
            r8 = r22
            r9 = r23
            checkCRL(r1, r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ AnnotatedException -> 0x0092 }
            int r6 = r17 + 1
            r8 = r15
            r7 = 0
            r9 = 0
            r16 = 1
            goto L_0x005f
        L_0x0092:
            r0 = move-exception
            goto L_0x0099
        L_0x0094:
            r15 = r8
            r0 = 0
            goto L_0x00ae
        L_0x0097:
            r0 = move-exception
            r15 = r8
        L_0x0099:
            org.bouncycastle.jce.provider.AnnotatedException r9 = new org.bouncycastle.jce.provider.AnnotatedException
            r9.<init>(r14, r0)
            r0 = r9
            goto L_0x00ae
        L_0x00a0:
            r0 = move-exception
            r1 = r0
            org.bouncycastle.jce.exception.ExtCertPathValidatorException r0 = new org.bouncycastle.jce.exception.ExtCertPathValidatorException
            java.lang.String r2 = "Distribution points could not be read."
            r0.<init>(r2, r1)
            throw r0
        L_0x00aa:
            r15 = r8
            r0 = 0
            r16 = 0
        L_0x00ae:
            int r1 = r12.getCertStatus()
            if (r1 != r15) goto L_0x0116
            boolean r1 = r13.isAllReasons()
            if (r1 != 0) goto L_0x0116
            org.bouncycastle.asn1.ASN1InputStream r1 = new org.bouncycastle.asn1.ASN1InputStream     // Catch:{ Exception -> 0x0107 }
            org.bouncycastle.x509.AttributeCertificateIssuer r2 = r18.getIssuer()     // Catch:{ Exception -> 0x0107 }
            java.security.Principal[] r2 = r2.getPrincipals()     // Catch:{ Exception -> 0x0107 }
            r3 = 0
            r2 = r2[r3]     // Catch:{ Exception -> 0x0107 }
            javax.security.auth.x500.X500Principal r2 = (javax.security.auth.x500.X500Principal) r2     // Catch:{ Exception -> 0x0107 }
            byte[] r2 = r2.getEncoded()     // Catch:{ Exception -> 0x0107 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x0107 }
            org.bouncycastle.asn1.ASN1Primitive r1 = r1.readObject()     // Catch:{ Exception -> 0x0107 }
            org.bouncycastle.asn1.x509.DistributionPoint r2 = new org.bouncycastle.asn1.x509.DistributionPoint     // Catch:{ AnnotatedException -> 0x0105 }
            org.bouncycastle.asn1.x509.DistributionPointName r3 = new org.bouncycastle.asn1.x509.DistributionPointName     // Catch:{ AnnotatedException -> 0x0105 }
            org.bouncycastle.asn1.x509.GeneralNames r4 = new org.bouncycastle.asn1.x509.GeneralNames     // Catch:{ AnnotatedException -> 0x0105 }
            org.bouncycastle.asn1.x509.GeneralName r5 = new org.bouncycastle.asn1.x509.GeneralName     // Catch:{ AnnotatedException -> 0x0105 }
            r6 = 4
            r5.<init>(r6, r1)     // Catch:{ AnnotatedException -> 0x0105 }
            r4.<init>(r5)     // Catch:{ AnnotatedException -> 0x0105 }
            r1 = 0
            r3.<init>(r1, r4)     // Catch:{ AnnotatedException -> 0x0105 }
            r1 = 0
            r2.<init>(r3, r1, r1)     // Catch:{ AnnotatedException -> 0x0105 }
            java.lang.Object r1 = r11.clone()     // Catch:{ AnnotatedException -> 0x0105 }
            r3 = r1
            org.bouncycastle.jcajce.PKIXExtendedParameters r3 = (org.bouncycastle.jcajce.PKIXExtendedParameters) r3     // Catch:{ AnnotatedException -> 0x0105 }
            r1 = r2
            r2 = r18
            r4 = r21
            r5 = r20
            r6 = r12
            r7 = r13
            r8 = r22
            r9 = r23
            checkCRL(r1, r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ AnnotatedException -> 0x0105 }
            r16 = 1
            goto L_0x0116
        L_0x0105:
            r0 = move-exception
            goto L_0x0110
        L_0x0107:
            r0 = move-exception
            org.bouncycastle.jce.provider.AnnotatedException r1 = new org.bouncycastle.jce.provider.AnnotatedException     // Catch:{ AnnotatedException -> 0x0105 }
            java.lang.String r2 = "Issuer from certificate for CRL could not be reencoded."
            r1.<init>(r2, r0)     // Catch:{ AnnotatedException -> 0x0105 }
            throw r1     // Catch:{ AnnotatedException -> 0x0105 }
        L_0x0110:
            org.bouncycastle.jce.provider.AnnotatedException r1 = new org.bouncycastle.jce.provider.AnnotatedException
            r1.<init>(r14, r0)
            r0 = r1
        L_0x0116:
            if (r16 == 0) goto L_0x0175
            int r0 = r12.getCertStatus()
            if (r0 != r15) goto L_0x013e
            boolean r0 = r13.isAllReasons()
            r1 = 12
            if (r0 != 0) goto L_0x012f
            int r0 = r12.getCertStatus()
            if (r0 != r15) goto L_0x012f
            r12.setCertStatus(r1)
        L_0x012f:
            int r0 = r12.getCertStatus()
            if (r0 == r1) goto L_0x0136
            goto L_0x01a8
        L_0x0136:
            java.security.cert.CertPathValidatorException r0 = new java.security.cert.CertPathValidatorException
            java.lang.String r1 = "Attribute certificate status could not be determined."
            r0.<init>(r1)
            throw r0
        L_0x013e:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Attribute certificate revocation after "
            r0.append(r1)
            java.util.Date r1 = r12.getRevocationDate()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            java.lang.String r0 = ", reason: "
            r1.append(r0)
            java.lang.String[] r0 = org.bouncycastle.jce.provider.RFC3280CertPathUtilities.crlReasons
            int r2 = r12.getCertStatus()
            r0 = r0[r2]
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.security.cert.CertPathValidatorException r1 = new java.security.cert.CertPathValidatorException
            r1.<init>(r0)
            throw r1
        L_0x0175:
            org.bouncycastle.jce.exception.ExtCertPathValidatorException r1 = new org.bouncycastle.jce.exception.ExtCertPathValidatorException
            java.lang.String r2 = "No valid CRL found."
            r1.<init>(r2, r0)
            throw r1
        L_0x017d:
            r0 = move-exception
            java.security.cert.CertPathValidatorException r1 = new java.security.cert.CertPathValidatorException
            java.lang.String r2 = "No additional CRL locations could be decoded from CRL distribution point extension."
            r1.<init>(r2, r0)
            throw r1
        L_0x0186:
            r0 = move-exception
            java.security.cert.CertPathValidatorException r1 = new java.security.cert.CertPathValidatorException
            java.lang.String r2 = "CRL distribution point extension could not be read."
            r1.<init>(r2, r0)
            throw r1
        L_0x018f:
            java.lang.String r0 = CRL_DISTRIBUTION_POINTS
            byte[] r0 = r10.getExtensionValue(r0)
            if (r0 != 0) goto L_0x01a0
            java.lang.String r0 = AUTHORITY_INFO_ACCESS
            byte[] r0 = r10.getExtensionValue(r0)
            if (r0 != 0) goto L_0x01a0
            goto L_0x01a8
        L_0x01a0:
            java.security.cert.CertPathValidatorException r0 = new java.security.cert.CertPathValidatorException
            java.lang.String r1 = "No rev avail extension is set, but also an AC revocation pointer."
            r0.<init>(r1)
            throw r0
        L_0x01a8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jce.provider.RFC3281CertPathUtilities.checkCRLs(org.bouncycastle.x509.X509AttributeCertificate, org.bouncycastle.jcajce.PKIXExtendedParameters, java.security.cert.X509Certificate, java.util.Date, java.util.List, org.bouncycastle.jcajce.util.JcaJceHelper):void");
    }

    protected static CertPath processAttrCert1(X509AttributeCertificate x509AttributeCertificate, PKIXExtendedParameters pKIXExtendedParameters) throws CertPathValidatorException {
        String str = "Support class could not be created.";
        HashSet<X509Certificate> hashSet = new HashSet<>();
        String str2 = "Unable to encode X500 principal.";
        String str3 = "Public key certificate for attribute certificate cannot be searched.";
        int i = 0;
        if (x509AttributeCertificate.getHolder().getIssuer() != null) {
            X509CertSelector x509CertSelector = new X509CertSelector();
            x509CertSelector.setSerialNumber(x509AttributeCertificate.getHolder().getSerialNumber());
            Principal[] issuer = x509AttributeCertificate.getHolder().getIssuer();
            int i2 = 0;
            while (i2 < issuer.length) {
                try {
                    if (issuer[i2] instanceof X500Principal) {
                        x509CertSelector.setIssuer(((X500Principal) issuer[i2]).getEncoded());
                    }
                    hashSet.addAll(CertPathValidatorUtilities.findCertificates(new Builder(x509CertSelector).build(), pKIXExtendedParameters.getCertStores()));
                    i2++;
                } catch (AnnotatedException e) {
                    throw new ExtCertPathValidatorException(str3, e);
                } catch (IOException e2) {
                    throw new ExtCertPathValidatorException(str2, e2);
                }
            }
            if (hashSet.isEmpty()) {
                throw new CertPathValidatorException("Public key certificate specified in base certificate ID for attribute certificate cannot be found.");
            }
        }
        if (x509AttributeCertificate.getHolder().getEntityNames() != null) {
            X509CertStoreSelector x509CertStoreSelector = new X509CertStoreSelector();
            Principal[] entityNames = x509AttributeCertificate.getHolder().getEntityNames();
            while (i < entityNames.length) {
                try {
                    if (entityNames[i] instanceof X500Principal) {
                        x509CertStoreSelector.setIssuer(((X500Principal) entityNames[i]).getEncoded());
                    }
                    hashSet.addAll(CertPathValidatorUtilities.findCertificates(new Builder(x509CertStoreSelector).build(), pKIXExtendedParameters.getCertStores()));
                    i++;
                } catch (AnnotatedException e3) {
                    throw new ExtCertPathValidatorException(str3, e3);
                } catch (IOException e4) {
                    throw new ExtCertPathValidatorException(str2, e4);
                }
            }
            if (hashSet.isEmpty()) {
                throw new CertPathValidatorException("Public key certificate specified in entity name for attribute certificate cannot be found.");
            }
        }
        PKIXExtendedParameters.Builder builder = new PKIXExtendedParameters.Builder(pKIXExtendedParameters);
        Throwable th = null;
        CertPathBuilderResult certPathBuilderResult = null;
        for (X509Certificate certificate : hashSet) {
            X509CertStoreSelector x509CertStoreSelector2 = new X509CertStoreSelector();
            x509CertStoreSelector2.setCertificate(certificate);
            builder.setTargetConstraints(new Builder(x509CertStoreSelector2).build());
            try {
                try {
                    certPathBuilderResult = CertPathBuilder.getInstance("PKIX", "BC").build(new PKIXExtendedBuilderParameters.Builder(builder.build()).build());
                } catch (CertPathBuilderException e5) {
                    th = new ExtCertPathValidatorException("Certification path for public key certificate of attribute certificate could not be build.", e5);
                } catch (InvalidAlgorithmParameterException e6) {
                    throw new RuntimeException(e6.getMessage());
                }
            } catch (NoSuchProviderException e7) {
                throw new ExtCertPathValidatorException(str, e7);
            } catch (NoSuchAlgorithmException e8) {
                throw new ExtCertPathValidatorException(str, e8);
            }
        }
        if (th == null) {
            return certPathBuilderResult.getCertPath();
        }
        throw th;
    }

    protected static CertPathValidatorResult processAttrCert2(CertPath certPath, PKIXExtendedParameters pKIXExtendedParameters) throws CertPathValidatorException {
        String str = "Support class could not be created.";
        try {
            try {
                return CertPathValidator.getInstance("PKIX", "BC").validate(certPath, pKIXExtendedParameters);
            } catch (CertPathValidatorException e) {
                throw new ExtCertPathValidatorException("Certification path for issuer certificate of attribute certificate could not be validated.", e);
            } catch (InvalidAlgorithmParameterException e2) {
                throw new RuntimeException(e2.getMessage());
            }
        } catch (NoSuchProviderException e3) {
            throw new ExtCertPathValidatorException(str, e3);
        } catch (NoSuchAlgorithmException e4) {
            throw new ExtCertPathValidatorException(str, e4);
        }
    }

    protected static void processAttrCert3(X509Certificate x509Certificate, PKIXExtendedParameters pKIXExtendedParameters) throws CertPathValidatorException {
        if (x509Certificate.getKeyUsage() != null && !x509Certificate.getKeyUsage()[0] && !x509Certificate.getKeyUsage()[1]) {
            throw new CertPathValidatorException("Attribute certificate issuer public key cannot be used to validate digital signatures.");
        } else if (x509Certificate.getBasicConstraints() != -1) {
            throw new CertPathValidatorException("Attribute certificate issuer is also a public key certificate issuer.");
        }
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.security.cert.TrustAnchor>, for r5v0, types: [java.util.Set<java.security.cert.TrustAnchor>, java.util.Set] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static void processAttrCert4(java.security.cert.X509Certificate r4, java.util.Set<java.security.cert.TrustAnchor> r5) throws java.security.cert.CertPathValidatorException {
        /*
            java.util.Iterator r5 = r5.iterator()
            r0 = 0
        L_0x0005:
            boolean r1 = r5.hasNext()
            if (r1 == 0) goto L_0x0031
            java.lang.Object r1 = r5.next()
            java.security.cert.TrustAnchor r1 = (java.security.cert.TrustAnchor) r1
            javax.security.auth.x500.X500Principal r2 = r4.getSubjectX500Principal()
            java.lang.String r3 = "RFC2253"
            java.lang.String r2 = r2.getName(r3)
            java.lang.String r3 = r1.getCAName()
            boolean r2 = r2.equals(r3)
            if (r2 != 0) goto L_0x002f
            java.security.cert.X509Certificate r1 = r1.getTrustedCert()
            boolean r1 = r4.equals(r1)
            if (r1 == 0) goto L_0x0005
        L_0x002f:
            r0 = 1
            goto L_0x0005
        L_0x0031:
            if (r0 == 0) goto L_0x0034
            return
        L_0x0034:
            java.security.cert.CertPathValidatorException r4 = new java.security.cert.CertPathValidatorException
            java.lang.String r5 = "Attribute certificate issuer is not directly trusted."
            r4.<init>(r5)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jce.provider.RFC3281CertPathUtilities.processAttrCert4(java.security.cert.X509Certificate, java.util.Set):void");
    }

    protected static void processAttrCert5(X509AttributeCertificate x509AttributeCertificate, PKIXExtendedParameters pKIXExtendedParameters) throws CertPathValidatorException {
        String str = "Attribute certificate is not valid.";
        try {
            x509AttributeCertificate.checkValidity(CertPathValidatorUtilities.getValidDate(pKIXExtendedParameters));
        } catch (CertificateExpiredException e) {
            throw new ExtCertPathValidatorException(str, e);
        } catch (CertificateNotYetValidException e2) {
            throw new ExtCertPathValidatorException(str, e2);
        }
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<org.bouncycastle.x509.PKIXAttrCertChecker>, for r6v0, types: [java.util.Set<org.bouncycastle.x509.PKIXAttrCertChecker>, java.util.Set] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static void processAttrCert7(org.bouncycastle.x509.X509AttributeCertificate r2, java.security.cert.CertPath r3, java.security.cert.CertPath r4, org.bouncycastle.jcajce.PKIXExtendedParameters r5, java.util.Set<org.bouncycastle.x509.PKIXAttrCertChecker> r6) throws java.security.cert.CertPathValidatorException {
        /*
            java.lang.String r5 = "Target information extension could not be read."
            java.util.Set r0 = r2.getCriticalExtensionOIDs()
            java.lang.String r1 = TARGET_INFORMATION
            boolean r1 = r0.contains(r1)
            if (r1 == 0) goto L_0x0026
            java.lang.String r1 = TARGET_INFORMATION     // Catch:{ AnnotatedException -> 0x001f, IllegalArgumentException -> 0x0018 }
            org.bouncycastle.asn1.ASN1Primitive r1 = org.bouncycastle.jce.provider.CertPathValidatorUtilities.getExtensionValue(r2, r1)     // Catch:{ AnnotatedException -> 0x001f, IllegalArgumentException -> 0x0018 }
            org.bouncycastle.asn1.x509.TargetInformation.getInstance(r1)     // Catch:{ AnnotatedException -> 0x001f, IllegalArgumentException -> 0x0018 }
            goto L_0x0026
        L_0x0018:
            r2 = move-exception
            org.bouncycastle.jce.exception.ExtCertPathValidatorException r3 = new org.bouncycastle.jce.exception.ExtCertPathValidatorException
            r3.<init>(r5, r2)
            throw r3
        L_0x001f:
            r2 = move-exception
            org.bouncycastle.jce.exception.ExtCertPathValidatorException r3 = new org.bouncycastle.jce.exception.ExtCertPathValidatorException
            r3.<init>(r5, r2)
            throw r3
        L_0x0026:
            java.lang.String r5 = TARGET_INFORMATION
            r0.remove(r5)
            java.util.Iterator r5 = r6.iterator()
        L_0x002f:
            boolean r6 = r5.hasNext()
            if (r6 == 0) goto L_0x003f
            java.lang.Object r6 = r5.next()
            org.bouncycastle.x509.PKIXAttrCertChecker r6 = (org.bouncycastle.x509.PKIXAttrCertChecker) r6
            r6.check(r2, r3, r4, r0)
            goto L_0x002f
        L_0x003f:
            boolean r2 = r0.isEmpty()
            if (r2 == 0) goto L_0x0046
            return
        L_0x0046:
            java.security.cert.CertPathValidatorException r2 = new java.security.cert.CertPathValidatorException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Attribute certificate contains unsupported critical extensions: "
            r3.append(r4)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jce.provider.RFC3281CertPathUtilities.processAttrCert7(org.bouncycastle.x509.X509AttributeCertificate, java.security.cert.CertPath, java.security.cert.CertPath, org.bouncycastle.jcajce.PKIXExtendedParameters, java.util.Set):void");
    }
}
