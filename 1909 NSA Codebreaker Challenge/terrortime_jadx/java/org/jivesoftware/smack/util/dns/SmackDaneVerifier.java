package org.jivesoftware.smack.util.dns;

import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509TrustManager;

public interface SmackDaneVerifier {
    void finish(SSLSocket sSLSocket) throws CertificateException;

    void init(SSLContext sSLContext, KeyManager[] keyManagerArr, X509TrustManager x509TrustManager, SecureRandom secureRandom) throws KeyManagementException;
}
