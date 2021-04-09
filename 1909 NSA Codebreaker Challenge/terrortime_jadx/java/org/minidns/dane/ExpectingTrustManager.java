package org.minidns.dane;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class ExpectingTrustManager implements X509TrustManager {
    private CertificateException exception;
    private final X509TrustManager trustManager;

    public ExpectingTrustManager(X509TrustManager trustManager2) {
        this.trustManager = trustManager2 == null ? getDefaultTrustManager() : trustManager2;
    }

    public boolean hasException() {
        return this.exception != null;
    }

    public CertificateException getException() {
        CertificateException e = this.exception;
        this.exception = null;
        return e;
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            this.trustManager.checkClientTrusted(chain, authType);
        } catch (CertificateException e) {
            this.exception = e;
        }
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            this.trustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException e) {
            this.exception = e;
        }
    }

    public X509Certificate[] getAcceptedIssuers() {
        return this.trustManager.getAcceptedIssuers();
    }

    private static X509TrustManager getDefaultTrustManager() {
        TrustManager[] trustManagers;
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(null);
            for (TrustManager trustManager2 : tmf.getTrustManagers()) {
                if (trustManager2 instanceof X509TrustManager) {
                    return (X509TrustManager) trustManager2;
                }
            }
            return null;
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            throw new RuntimeException("X.509 not supported.", e);
        }
    }
}
