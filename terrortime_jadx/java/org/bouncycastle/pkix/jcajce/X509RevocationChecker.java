package org.bouncycastle.pkix.jcajce;

import android.support.v4.os.EnvironmentCompat;
import java.lang.ref.WeakReference;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CRL;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Iterable;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

public class X509RevocationChecker extends PKIXCertPathChecker {
    public static final int CHAIN_VALIDITY_MODEL = 1;
    private static Logger LOG = Logger.getLogger(X509RevocationChecker.class.getName());
    public static final int PKIX_VALIDITY_MODEL = 0;
    private static final Map<GeneralName, WeakReference<X509CRL>> crlCache = Collections.synchronizedMap(new WeakHashMap());
    protected static final String[] crlReasons = {"unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", EnvironmentCompat.MEDIA_UNKNOWN, "removeFromCRL", "privilegeWithdrawn", "aACompromise"};
    private final boolean canSoftFail;
    private final List<CertStore> crlCertStores;
    private final List<Store<CRL>> crls;
    private final long failHardMaxTime;
    private final long failLogMaxTime;
    private final Map<X500Principal, Long> failures;
    private final PKIXJcaJceHelper helper;
    private final boolean isCheckEEOnly;
    private X509Certificate signingCert;
    private final Set<TrustAnchor> trustAnchors;
    private X500Principal workingIssuerName;
    private PublicKey workingPublicKey;

    public static class Builder {
        /* access modifiers changed from: private */
        public boolean canSoftFail;
        /* access modifiers changed from: private */
        public List<CertStore> crlCertStores;
        /* access modifiers changed from: private */
        public List<Store<CRL>> crls;
        /* access modifiers changed from: private */
        public long failHardMaxTime;
        /* access modifiers changed from: private */
        public long failLogMaxTime;
        /* access modifiers changed from: private */
        public boolean isCheckEEOnly;
        /* access modifiers changed from: private */
        public Provider provider;
        /* access modifiers changed from: private */
        public String providerName;
        /* access modifiers changed from: private */
        public Set<TrustAnchor> trustAnchors;
        private int validityModel;

        public Builder(KeyStore keyStore) throws KeyStoreException {
            this.crlCertStores = new ArrayList();
            this.crls = new ArrayList();
            this.validityModel = 0;
            this.trustAnchors = new HashSet();
            Enumeration aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String str = (String) aliases.nextElement();
                if (keyStore.isCertificateEntry(str)) {
                    this.trustAnchors.add(new TrustAnchor((X509Certificate) keyStore.getCertificate(str), null));
                }
            }
        }

        public Builder(TrustAnchor trustAnchor) {
            this.crlCertStores = new ArrayList();
            this.crls = new ArrayList();
            this.validityModel = 0;
            this.trustAnchors = Collections.singleton(trustAnchor);
        }

        public Builder(Set<TrustAnchor> set) {
            this.crlCertStores = new ArrayList();
            this.crls = new ArrayList();
            this.validityModel = 0;
            this.trustAnchors = new HashSet(set);
        }

        public Builder addCrls(CertStore certStore) {
            this.crlCertStores.add(certStore);
            return this;
        }

        public Builder addCrls(Store<CRL> store) {
            this.crls.add(store);
            return this;
        }

        public X509RevocationChecker build() {
            return new X509RevocationChecker(this);
        }

        public Builder setCheckEndEntityOnly(boolean z) {
            this.isCheckEEOnly = z;
            return this;
        }

        public Builder setSoftFail(boolean z, long j) {
            this.canSoftFail = z;
            this.failLogMaxTime = j;
            this.failHardMaxTime = -1;
            return this;
        }

        public Builder setSoftFailHardLimit(boolean z, long j) {
            this.canSoftFail = z;
            this.failLogMaxTime = (3 * j) / 4;
            this.failHardMaxTime = j;
            return this;
        }

        public Builder usingProvider(String str) {
            this.providerName = str;
            return this;
        }

        public Builder usingProvider(Provider provider2) {
            this.provider = provider2;
            return this;
        }
    }

    private class LocalCRLStore<T extends CRL> implements PKIXCRLStore, Iterable<CRL> {
        private Collection<CRL> _local;

        public LocalCRLStore(Store<CRL> store) {
            this._local = new ArrayList(store.getMatches(null));
        }

        public Collection getMatches(Selector selector) {
            if (selector == null) {
                return new ArrayList(this._local);
            }
            ArrayList arrayList = new ArrayList();
            for (CRL crl : this._local) {
                if (selector.match(crl)) {
                    arrayList.add(crl);
                }
            }
            return arrayList;
        }

        public Iterator<CRL> iterator() {
            return getMatches(null).iterator();
        }
    }

    private X509RevocationChecker(Builder builder) {
        PKIXJcaJceHelper pKIXNamedJcaJceHelper;
        this.failures = new HashMap();
        this.crls = new ArrayList(builder.crls);
        this.crlCertStores = new ArrayList(builder.crlCertStores);
        this.isCheckEEOnly = builder.isCheckEEOnly;
        this.trustAnchors = builder.trustAnchors;
        this.canSoftFail = builder.canSoftFail;
        this.failLogMaxTime = builder.failLogMaxTime;
        this.failHardMaxTime = builder.failHardMaxTime;
        if (builder.provider != null) {
            pKIXNamedJcaJceHelper = new PKIXProviderJcaJceHelper(builder.provider);
        } else if (builder.providerName != null) {
            pKIXNamedJcaJceHelper = new PKIXNamedJcaJceHelper(builder.providerName);
        } else {
            this.helper = new PKIXDefaultJcaJceHelper();
            return;
        }
        this.helper = pKIXNamedJcaJceHelper;
    }

    private void addIssuers(final List<X500Principal> list, CertStore certStore) throws CertStoreException {
        certStore.getCRLs(new X509CRLSelector() {
            public boolean match(CRL crl) {
                if (!(crl instanceof X509CRL)) {
                    return false;
                }
                list.add(((X509CRL) crl).getIssuerX500Principal());
                return false;
            }
        });
    }

    private void addIssuers(final List<X500Principal> list, Store<CRL> store) {
        store.getMatches(new Selector<CRL>() {
            public Object clone() {
                return this;
            }

            public boolean match(CRL crl) {
                if (!(crl instanceof X509CRL)) {
                    return false;
                }
                list.add(((X509CRL) crl).getIssuerX500Principal());
                return false;
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x00da  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00fb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.security.cert.CRL downloadCRLs(javax.security.auth.x500.X500Principal r17, java.util.Date r18, org.bouncycastle.asn1.ASN1Primitive r19, org.bouncycastle.jcajce.util.JcaJceHelper r20) {
        /*
            r16 = this;
            r1 = r18
            org.bouncycastle.asn1.x509.CRLDistPoint r0 = org.bouncycastle.asn1.x509.CRLDistPoint.getInstance(r19)
            org.bouncycastle.asn1.x509.DistributionPoint[] r2 = r0.getDistributionPoints()
            r3 = 0
            r4 = r3
        L_0x000c:
            int r0 = r2.length
            r5 = 0
            if (r4 == r0) goto L_0x012c
            r0 = r2[r4]
            org.bouncycastle.asn1.x509.DistributionPointName r0 = r0.getDistributionPoint()
            int r6 = r0.getType()
            if (r6 != 0) goto L_0x0124
            org.bouncycastle.asn1.ASN1Encodable r0 = r0.getName()
            org.bouncycastle.asn1.x509.GeneralNames r0 = org.bouncycastle.asn1.x509.GeneralNames.getInstance(r0)
            org.bouncycastle.asn1.x509.GeneralName[] r6 = r0.getNames()
            r7 = r3
        L_0x0029:
            int r0 = r6.length
            if (r7 == r0) goto L_0x0124
            r0 = r6[r7]
            int r8 = r0.getTagNo()
            r9 = 6
            if (r8 != r9) goto L_0x011c
            java.util.Map<org.bouncycastle.asn1.x509.GeneralName, java.lang.ref.WeakReference<java.security.cert.X509CRL>> r8 = crlCache
            java.lang.Object r8 = r8.get(r0)
            java.lang.ref.WeakReference r8 = (java.lang.ref.WeakReference) r8
            if (r8 == 0) goto L_0x0061
            java.lang.Object r8 = r8.get()
            java.security.cert.X509CRL r8 = (java.security.cert.X509CRL) r8
            if (r8 == 0) goto L_0x005c
            java.util.Date r9 = r8.getThisUpdate()
            boolean r9 = r1.before(r9)
            if (r9 != 0) goto L_0x005c
            java.util.Date r9 = r8.getNextUpdate()
            boolean r9 = r1.after(r9)
            if (r9 != 0) goto L_0x005c
            return r8
        L_0x005c:
            java.util.Map<org.bouncycastle.asn1.x509.GeneralName, java.lang.ref.WeakReference<java.security.cert.X509CRL>> r8 = crlCache
            r8.remove(r0)
        L_0x0061:
            java.net.URL r8 = new java.net.URL     // Catch:{ Exception -> 0x00c6 }
            org.bouncycastle.asn1.ASN1Encodable r9 = r0.getName()     // Catch:{ Exception -> 0x00c6 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x00c6 }
            r8.<init>(r9)     // Catch:{ Exception -> 0x00c6 }
            java.lang.String r9 = "X.509"
            r10 = r20
            java.security.cert.CertificateFactory r9 = r10.createCertificateFactory(r9)     // Catch:{ Exception -> 0x00bc }
            java.io.InputStream r11 = r8.openStream()     // Catch:{ Exception -> 0x00bc }
            java.io.BufferedInputStream r12 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x00bc }
            r12.<init>(r11)     // Catch:{ Exception -> 0x00bc }
            java.security.cert.CRL r9 = r9.generateCRL(r12)     // Catch:{ Exception -> 0x00bc }
            java.security.cert.X509CRL r9 = (java.security.cert.X509CRL) r9     // Catch:{ Exception -> 0x00bc }
            r11.close()     // Catch:{ Exception -> 0x00bc }
            java.util.logging.Logger r11 = LOG     // Catch:{ Exception -> 0x00bc }
            java.util.logging.Level r12 = java.util.logging.Level.INFO     // Catch:{ Exception -> 0x00bc }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00bc }
            r13.<init>()     // Catch:{ Exception -> 0x00bc }
            java.lang.String r14 = "downloaded CRL from CrlDP "
            r13.append(r14)     // Catch:{ Exception -> 0x00bc }
            r13.append(r8)     // Catch:{ Exception -> 0x00bc }
            java.lang.String r14 = " for issuer \""
            r13.append(r14)     // Catch:{ Exception -> 0x00bc }
            r14 = r17
            r13.append(r14)     // Catch:{ Exception -> 0x00ba }
            java.lang.String r15 = "\""
            r13.append(r15)     // Catch:{ Exception -> 0x00ba }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x00ba }
            r11.log(r12, r13)     // Catch:{ Exception -> 0x00ba }
            java.util.Map<org.bouncycastle.asn1.x509.GeneralName, java.lang.ref.WeakReference<java.security.cert.X509CRL>> r11 = crlCache     // Catch:{ Exception -> 0x00ba }
            java.lang.ref.WeakReference r12 = new java.lang.ref.WeakReference     // Catch:{ Exception -> 0x00ba }
            r12.<init>(r9)     // Catch:{ Exception -> 0x00ba }
            r11.put(r0, r12)     // Catch:{ Exception -> 0x00ba }
            return r9
        L_0x00ba:
            r0 = move-exception
            goto L_0x00cc
        L_0x00bc:
            r0 = move-exception
            r14 = r17
            goto L_0x00cc
        L_0x00c0:
            r0 = move-exception
            r14 = r17
            r10 = r20
            goto L_0x00cc
        L_0x00c6:
            r0 = move-exception
            r14 = r17
            r10 = r20
            r8 = r5
        L_0x00cc:
            java.util.logging.Logger r9 = LOG
            java.util.logging.Level r11 = java.util.logging.Level.FINE
            boolean r9 = r9.isLoggable(r11)
            java.lang.String r11 = " ignored: "
            java.lang.String r12 = "CrlDP "
            if (r9 == 0) goto L_0x00fb
            java.util.logging.Logger r9 = LOG
            java.util.logging.Level r13 = java.util.logging.Level.FINE
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r12)
            r15.append(r8)
            r15.append(r11)
            java.lang.String r8 = r0.getMessage()
            r15.append(r8)
            java.lang.String r8 = r15.toString()
            r9.log(r13, r8, r0)
            goto L_0x0120
        L_0x00fb:
            java.util.logging.Logger r9 = LOG
            java.util.logging.Level r13 = java.util.logging.Level.INFO
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r12)
            r15.append(r8)
            r15.append(r11)
            java.lang.String r0 = r0.getMessage()
            r15.append(r0)
            java.lang.String r0 = r15.toString()
            r9.log(r13, r0)
            goto L_0x0120
        L_0x011c:
            r14 = r17
            r10 = r20
        L_0x0120:
            int r7 = r7 + 1
            goto L_0x0029
        L_0x0124:
            r14 = r17
            r10 = r20
            int r4 = r4 + 1
            goto L_0x000c
        L_0x012c:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.pkix.jcajce.X509RevocationChecker.downloadCRLs(javax.security.auth.x500.X500Principal, java.util.Date, org.bouncycastle.asn1.ASN1Primitive, org.bouncycastle.jcajce.util.JcaJceHelper):java.security.cert.CRL");
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
            throw new AnnotatedException("could not read distribution points could not be read", e);
        }
    }

    public void check(Certificate certificate, Collection<String> collection) throws CertPathValidatorException {
        Logger logger;
        StringBuilder sb;
        Level level;
        X509Certificate x509Certificate = (X509Certificate) certificate;
        if (!this.isCheckEEOnly || x509Certificate.getBasicConstraints() == -1) {
            TrustAnchor trustAnchor = null;
            if (this.workingIssuerName == null) {
                this.workingIssuerName = x509Certificate.getIssuerX500Principal();
                for (TrustAnchor trustAnchor2 : this.trustAnchors) {
                    if (this.workingIssuerName.equals(trustAnchor2.getCA()) || this.workingIssuerName.equals(trustAnchor2.getTrustedCert().getSubjectX500Principal())) {
                        trustAnchor = trustAnchor2;
                    }
                }
                if (trustAnchor != null) {
                    this.signingCert = trustAnchor.getTrustedCert();
                    this.workingPublicKey = this.signingCert.getPublicKey();
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("no trust anchor found for ");
                    sb2.append(this.workingIssuerName);
                    throw new CertPathValidatorException(sb2.toString());
                }
            }
            ArrayList arrayList = new ArrayList();
            try {
                PKIXParameters pKIXParameters = new PKIXParameters(this.trustAnchors);
                pKIXParameters.setRevocationEnabled(false);
                pKIXParameters.setDate(new Date());
                for (int i = 0; i != this.crlCertStores.size(); i++) {
                    if (LOG.isLoggable(Level.INFO)) {
                        addIssuers((List<X500Principal>) arrayList, (CertStore) this.crlCertStores.get(i));
                    }
                    pKIXParameters.addCertStore((CertStore) this.crlCertStores.get(i));
                }
                org.bouncycastle.jcajce.PKIXExtendedParameters.Builder builder = new org.bouncycastle.jcajce.PKIXExtendedParameters.Builder(pKIXParameters);
                for (int i2 = 0; i2 != this.crls.size(); i2++) {
                    if (LOG.isLoggable(Level.INFO)) {
                        addIssuers((List<X500Principal>) arrayList, (Store) this.crls.get(i2));
                    }
                    builder.addCRLStore(new LocalCRLStore((Store) this.crls.get(i2)));
                }
                String str = "\"";
                if (arrayList.isEmpty()) {
                    LOG.log(Level.INFO, "configured with 0 pre-loaded CRLs");
                } else if (LOG.isLoggable(Level.FINE)) {
                    for (int i3 = 0; i3 != arrayList.size(); i3++) {
                        Logger logger2 = LOG;
                        Level level2 = Level.FINE;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("configuring with CRL for issuer \"");
                        sb3.append(arrayList.get(i3));
                        sb3.append(str);
                        logger2.log(level2, sb3.toString());
                    }
                } else {
                    Logger logger3 = LOG;
                    Level level3 = Level.INFO;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("configured with ");
                    sb4.append(arrayList.size());
                    sb4.append(" pre-loaded CRLs");
                    logger3.log(level3, sb4.toString());
                }
                try {
                    checkCRLs(builder.build(), x509Certificate, pKIXParameters.getDate(), this.signingCert, this.workingPublicKey, new ArrayList(), this.helper);
                } catch (AnnotatedException e) {
                    throw new CertPathValidatorException(e.getMessage(), e.getCause());
                } catch (CRLNotFoundException e2) {
                    if (x509Certificate.getExtensionValue(Extension.cRLDistributionPoints.getId()) != null) {
                        try {
                            CRL downloadCRLs = downloadCRLs(x509Certificate.getIssuerX500Principal(), pKIXParameters.getDate(), RevocationUtilities.getExtensionValue(x509Certificate, Extension.cRLDistributionPoints), this.helper);
                            if (downloadCRLs != null) {
                                try {
                                    builder.addCRLStore(new LocalCRLStore(new CollectionStore(Collections.singleton(downloadCRLs))));
                                    checkCRLs(builder.build(), x509Certificate, new Date(), this.signingCert, this.workingPublicKey, new ArrayList(), this.helper);
                                } catch (AnnotatedException e3) {
                                    throw new CertPathValidatorException(e2.getMessage(), e2.getCause());
                                }
                            } else if (this.canSoftFail) {
                                X500Principal issuerX500Principal = x509Certificate.getIssuerX500Principal();
                                Long l = (Long) this.failures.get(issuerX500Principal);
                                if (l != null) {
                                    long currentTimeMillis = System.currentTimeMillis() - l.longValue();
                                    long j = this.failHardMaxTime;
                                    if (j == -1 || j >= currentTimeMillis) {
                                        String str2 = "soft failing for issuer: \"";
                                        if (currentTimeMillis < this.failLogMaxTime) {
                                            logger = LOG;
                                            level = Level.WARNING;
                                            sb = new StringBuilder();
                                        } else {
                                            logger = LOG;
                                            level = Level.SEVERE;
                                            sb = new StringBuilder();
                                        }
                                        sb.append(str2);
                                        sb.append(issuerX500Principal);
                                        sb.append(str);
                                        logger.log(level, sb.toString());
                                    } else {
                                        throw e2;
                                    }
                                } else {
                                    this.failures.put(issuerX500Principal, Long.valueOf(System.currentTimeMillis()));
                                }
                            } else {
                                throw e2;
                            }
                        } catch (AnnotatedException e4) {
                            throw new CertPathValidatorException(e2.getMessage(), e2.getCause());
                        }
                    } else {
                        throw e2;
                    }
                }
                this.signingCert = x509Certificate;
                this.workingPublicKey = x509Certificate.getPublicKey();
                this.workingIssuerName = x509Certificate.getSubjectX500Principal();
            } catch (GeneralSecurityException e5) {
                StringBuilder sb5 = new StringBuilder();
                sb5.append("error setting up baseParams: ");
                sb5.append(e5.getMessage());
                throw new RuntimeException(sb5.toString());
            }
        } else {
            this.workingIssuerName = x509Certificate.getSubjectX500Principal();
            this.workingPublicKey = x509Certificate.getPublicKey();
            this.signingCert = x509Certificate;
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00eb  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00fd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkCRLs(org.bouncycastle.jcajce.PKIXExtendedParameters r21, java.security.cert.X509Certificate r22, java.util.Date r23, java.security.cert.X509Certificate r24, java.security.PublicKey r25, java.util.List r26, org.bouncycastle.pkix.jcajce.PKIXJcaJceHelper r27) throws org.bouncycastle.pkix.jcajce.AnnotatedException, java.security.cert.CertPathValidatorException {
        /*
            r20 = this;
            org.bouncycastle.asn1.ASN1ObjectIdentifier r0 = org.bouncycastle.asn1.x509.Extension.cRLDistributionPoints     // Catch:{ Exception -> 0x019b }
            r11 = r22
            org.bouncycastle.asn1.ASN1Primitive r0 = org.bouncycastle.pkix.jcajce.RevocationUtilities.getExtensionValue(r11, r0)     // Catch:{ Exception -> 0x019b }
            org.bouncycastle.asn1.x509.CRLDistPoint r0 = org.bouncycastle.asn1.x509.CRLDistPoint.getInstance(r0)     // Catch:{ Exception -> 0x019b }
            org.bouncycastle.jcajce.PKIXExtendedParameters$Builder r1 = new org.bouncycastle.jcajce.PKIXExtendedParameters$Builder
            r12 = r21
            r1.<init>(r12)
            java.util.Map r2 = r21.getNamedCRLStoreMap()     // Catch:{ AnnotatedException -> 0x0192 }
            java.util.List r2 = getAdditionalStoresFromCRLDistributionPoint(r0, r2)     // Catch:{ AnnotatedException -> 0x0192 }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ AnnotatedException -> 0x0192 }
        L_0x001f:
            boolean r3 = r2.hasNext()     // Catch:{ AnnotatedException -> 0x0192 }
            if (r3 == 0) goto L_0x002f
            java.lang.Object r3 = r2.next()     // Catch:{ AnnotatedException -> 0x0192 }
            org.bouncycastle.jcajce.PKIXCRLStore r3 = (org.bouncycastle.jcajce.PKIXCRLStore) r3     // Catch:{ AnnotatedException -> 0x0192 }
            r1.addCRLStore(r3)     // Catch:{ AnnotatedException -> 0x0192 }
            goto L_0x001f
        L_0x002f:
            org.bouncycastle.pkix.jcajce.CertStatus r13 = new org.bouncycastle.pkix.jcajce.CertStatus
            r13.<init>()
            org.bouncycastle.pkix.jcajce.ReasonsMask r14 = new org.bouncycastle.pkix.jcajce.ReasonsMask
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
            r3 = r22
            r4 = r23
            r5 = r24
            r18 = r6
            r6 = r25
            r19 = r7
            r7 = r13
            r11 = r8
            r8 = r14
            r9 = r26
            r10 = r27
            org.bouncycastle.pkix.jcajce.RFC3280CertPathUtilities.checkCRL(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ AnnotatedException -> 0x007a }
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
            r11 = r22
            goto L_0x004f
        L_0x008c:
            r11 = r8
            goto L_0x009c
        L_0x008e:
            r0 = move-exception
            r1 = r0
            org.bouncycastle.pkix.jcajce.AnnotatedException r0 = new org.bouncycastle.pkix.jcajce.AnnotatedException
            java.lang.String r2 = "cannot read distribution points"
            r0.<init>(r2, r1)
            throw r0
        L_0x0098:
            r11 = r8
            r0 = 0
            r17 = 0
        L_0x009c:
            int r1 = r13.getCertStatus()
            if (r1 != r11) goto L_0x00e9
            boolean r1 = r14.isAllReasons()
            if (r1 != 0) goto L_0x00e9
            javax.security.auth.x500.X500Principal r1 = r22.getIssuerX500Principal()     // Catch:{ AnnotatedException -> 0x00e8 }
            org.bouncycastle.asn1.x509.DistributionPoint r2 = new org.bouncycastle.asn1.x509.DistributionPoint     // Catch:{ AnnotatedException -> 0x00e8 }
            org.bouncycastle.asn1.x509.DistributionPointName r3 = new org.bouncycastle.asn1.x509.DistributionPointName     // Catch:{ AnnotatedException -> 0x00e8 }
            org.bouncycastle.asn1.x509.GeneralNames r4 = new org.bouncycastle.asn1.x509.GeneralNames     // Catch:{ AnnotatedException -> 0x00e8 }
            org.bouncycastle.asn1.x509.GeneralName r5 = new org.bouncycastle.asn1.x509.GeneralName     // Catch:{ AnnotatedException -> 0x00e8 }
            r6 = 4
            byte[] r1 = r1.getEncoded()     // Catch:{ AnnotatedException -> 0x00e8 }
            org.bouncycastle.asn1.x500.X500Name r1 = org.bouncycastle.asn1.x500.X500Name.getInstance(r1)     // Catch:{ AnnotatedException -> 0x00e8 }
            r5.<init>(r6, r1)     // Catch:{ AnnotatedException -> 0x00e8 }
            r4.<init>(r5)     // Catch:{ AnnotatedException -> 0x00e8 }
            r1 = 0
            r3.<init>(r1, r4)     // Catch:{ AnnotatedException -> 0x00e8 }
            r1 = 0
            r2.<init>(r3, r1, r1)     // Catch:{ AnnotatedException -> 0x00e8 }
            java.lang.Object r1 = r21.clone()     // Catch:{ AnnotatedException -> 0x00e8 }
            r3 = r1
            org.bouncycastle.jcajce.PKIXExtendedParameters r3 = (org.bouncycastle.jcajce.PKIXExtendedParameters) r3     // Catch:{ AnnotatedException -> 0x00e8 }
            r1 = r2
            r2 = r3
            r3 = r22
            r4 = r23
            r5 = r24
            r6 = r25
            r7 = r13
            r8 = r14
            r9 = r26
            r10 = r27
            org.bouncycastle.pkix.jcajce.RFC3280CertPathUtilities.checkCRL(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ AnnotatedException -> 0x00e8 }
            r17 = r16
            goto L_0x00e9
        L_0x00e8:
            r0 = move-exception
        L_0x00e9:
            if (r17 != 0) goto L_0x00fd
            boolean r1 = r0 instanceof org.bouncycastle.pkix.jcajce.AnnotatedException
            java.lang.String r2 = "no valid CRL found"
            if (r1 == 0) goto L_0x00f7
            org.bouncycastle.pkix.jcajce.CRLNotFoundException r1 = new org.bouncycastle.pkix.jcajce.CRLNotFoundException
            r1.<init>(r2, r0)
            throw r1
        L_0x00f7:
            org.bouncycastle.pkix.jcajce.CRLNotFoundException r0 = new org.bouncycastle.pkix.jcajce.CRLNotFoundException
            r0.<init>(r2)
            throw r0
        L_0x00fd:
            int r0 = r13.getCertStatus()
            if (r0 != r11) goto L_0x0123
            boolean r0 = r14.isAllReasons()
            r1 = 12
            if (r0 != 0) goto L_0x0114
            int r0 = r13.getCertStatus()
            if (r0 != r11) goto L_0x0114
            r13.setCertStatus(r1)
        L_0x0114:
            int r0 = r13.getCertStatus()
            if (r0 == r1) goto L_0x011b
            return
        L_0x011b:
            org.bouncycastle.pkix.jcajce.AnnotatedException r0 = new org.bouncycastle.pkix.jcajce.AnnotatedException
            java.lang.String r1 = "certificate status could not be determined"
            r0.<init>(r1)
            throw r0
        L_0x0123:
            java.text.SimpleDateFormat r0 = new java.text.SimpleDateFormat
            java.lang.String r1 = "yyyy-MM-dd HH:mm:ss Z"
            r0.<init>(r1)
            java.lang.String r1 = "UTC"
            java.util.TimeZone r1 = java.util.TimeZone.getTimeZone(r1)
            r0.setTimeZone(r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "certificate [issuer=\""
            r1.append(r2)
            javax.security.auth.x500.X500Principal r2 = r22.getIssuerX500Principal()
            r1.append(r2)
            java.lang.String r2 = "\",serialNumber="
            r1.append(r2)
            java.math.BigInteger r2 = r22.getSerialNumber()
            r1.append(r2)
            java.lang.String r2 = ",subject=\""
            r1.append(r2)
            javax.security.auth.x500.X500Principal r2 = r22.getSubjectX500Principal()
            r1.append(r2)
            java.lang.String r2 = "\"] revoked after "
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
            org.bouncycastle.pkix.jcajce.AnnotatedException r1 = new org.bouncycastle.pkix.jcajce.AnnotatedException
            r1.<init>(r0)
            throw r1
        L_0x0192:
            r0 = move-exception
            org.bouncycastle.pkix.jcajce.AnnotatedException r1 = new org.bouncycastle.pkix.jcajce.AnnotatedException
            java.lang.String r2 = "no additional CRL locations could be decoded from CRL distribution point extension"
            r1.<init>(r2, r0)
            throw r1
        L_0x019b:
            r0 = move-exception
            org.bouncycastle.pkix.jcajce.AnnotatedException r1 = new org.bouncycastle.pkix.jcajce.AnnotatedException
            java.lang.String r2 = "cannot read CRL distribution point extension"
            r1.<init>(r2, r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.pkix.jcajce.X509RevocationChecker.checkCRLs(org.bouncycastle.jcajce.PKIXExtendedParameters, java.security.cert.X509Certificate, java.util.Date, java.security.cert.X509Certificate, java.security.PublicKey, java.util.List, org.bouncycastle.pkix.jcajce.PKIXJcaJceHelper):void");
    }

    public Object clone() {
        return this;
    }

    public Set<String> getSupportedExtensions() {
        return null;
    }

    public void init(boolean z) throws CertPathValidatorException {
        if (!z) {
            this.workingIssuerName = null;
            return;
        }
        throw new IllegalArgumentException("forward processing not supported");
    }

    public boolean isForwardCheckingSupported() {
        return false;
    }
}
