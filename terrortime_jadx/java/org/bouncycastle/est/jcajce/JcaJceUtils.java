package org.bouncycastle.est.jcajce;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRL;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Set;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;

public class JcaJceUtils {
    public static KeyManagerFactory createKeyManagerFactory(String str, String str2, KeyStore keyStore, char[] cArr) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        KeyManagerFactory keyManagerFactory;
        if (str == null && str2 == null) {
            str = KeyManagerFactory.getDefaultAlgorithm();
        } else if (str2 != null) {
            keyManagerFactory = KeyManagerFactory.getInstance(str, str2);
            keyManagerFactory.init(keyStore, cArr);
            return keyManagerFactory;
        }
        keyManagerFactory = KeyManagerFactory.getInstance(str);
        keyManagerFactory.init(keyStore, cArr);
        return keyManagerFactory;
    }

    public static X509TrustManager[] getCertPathTrustManager(final Set<TrustAnchor> set, final CRL[] crlArr) {
        final X509Certificate[] x509CertificateArr = new X509Certificate[set.size()];
        int i = 0;
        for (TrustAnchor trustedCert : set) {
            int i2 = i + 1;
            x509CertificateArr[i] = trustedCert.getTrustedCert();
            i = i2;
        }
        return new X509TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
                String str2 = "BC";
                String str3 = "Collection";
                try {
                    CertStore instance = CertStore.getInstance(str3, new CollectionCertStoreParameters(Arrays.asList(x509CertificateArr)), str2);
                    CertPathBuilder instance2 = CertPathBuilder.getInstance("PKIX", str2);
                    X509CertSelector x509CertSelector = new X509CertSelector();
                    x509CertSelector.setCertificate(x509CertificateArr[0]);
                    PKIXBuilderParameters pKIXBuilderParameters = new PKIXBuilderParameters(set, x509CertSelector);
                    pKIXBuilderParameters.addCertStore(instance);
                    if (crlArr != null) {
                        pKIXBuilderParameters.setRevocationEnabled(true);
                        pKIXBuilderParameters.addCertStore(CertStore.getInstance(str3, new CollectionCertStoreParameters(Arrays.asList(crlArr))));
                    } else {
                        pKIXBuilderParameters.setRevocationEnabled(false);
                    }
                    PKIXCertPathValidatorResult pKIXCertPathValidatorResult = (PKIXCertPathValidatorResult) instance2.build(pKIXBuilderParameters);
                    JcaJceUtils.validateServerCertUsage(x509CertificateArr[0]);
                } catch (CertificateException e) {
                    throw e;
                } catch (GeneralSecurityException e2) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("unable to process certificates: ");
                    sb.append(e2.getMessage());
                    throw new CertificateException(sb.toString(), e2);
                }
            }

            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509CertificateArr = x509CertificateArr;
                X509Certificate[] x509CertificateArr2 = new X509Certificate[x509CertificateArr.length];
                System.arraycopy(x509CertificateArr, 0, x509CertificateArr2, 0, x509CertificateArr2.length);
                return x509CertificateArr2;
            }
        }};
    }

    public static X509TrustManager getTrustAllTrustManager() {
        return new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    public static void validateServerCertUsage(X509Certificate x509Certificate) throws CertificateException {
        try {
            X509CertificateHolder x509CertificateHolder = new X509CertificateHolder(x509Certificate.getEncoded());
            KeyUsage fromExtensions = KeyUsage.fromExtensions(x509CertificateHolder.getExtensions());
            if (fromExtensions != null) {
                if (fromExtensions.hasUsages(4)) {
                    throw new CertificateException("Key usage must not contain keyCertSign");
                } else if (!fromExtensions.hasUsages(128)) {
                    if (!fromExtensions.hasUsages(32)) {
                        throw new CertificateException("Key usage must be none, digitalSignature or keyEncipherment");
                    }
                }
            }
            ExtendedKeyUsage fromExtensions2 = ExtendedKeyUsage.fromExtensions(x509CertificateHolder.getExtensions());
            if (fromExtensions2 != null && !fromExtensions2.hasKeyPurposeId(KeyPurposeId.id_kp_serverAuth) && !fromExtensions2.hasKeyPurposeId(KeyPurposeId.id_kp_msSGC)) {
                if (!fromExtensions2.hasKeyPurposeId(KeyPurposeId.id_kp_nsSGC)) {
                    throw new CertificateException("Certificate extended key usage must include serverAuth, msSGC or nsSGC");
                }
            }
        } catch (CertificateException e) {
            throw e;
        } catch (Exception e2) {
            throw new CertificateException(e2.getMessage(), e2);
        }
    }
}
