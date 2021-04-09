package org.jivesoftware.smack.util.dns.minidns;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.jivesoftware.smack.util.dns.SmackDaneVerifier;
import org.minidns.dane.DaneVerifier;
import org.minidns.dane.ExpectingTrustManager;

public class MiniDnsDaneVerifier implements SmackDaneVerifier {
    private static final Logger LOGGER = Logger.getLogger(MiniDnsDaneVerifier.class.getName());
    private static final DaneVerifier VERIFIER = new DaneVerifier();
    private ExpectingTrustManager expectingTrustManager;

    MiniDnsDaneVerifier() {
    }

    public void init(SSLContext context, KeyManager[] km, X509TrustManager tm, SecureRandom random) throws KeyManagementException {
        if (this.expectingTrustManager == null) {
            this.expectingTrustManager = new ExpectingTrustManager(tm);
            context.init(km, new TrustManager[]{this.expectingTrustManager}, random);
            return;
        }
        throw new IllegalStateException("DaneProvider was initialized before. Use newInstance() instead.");
    }

    public void finish(SSLSocket sslSocket) throws CertificateException {
        if (!VERIFIER.verify(sslSocket) && this.expectingTrustManager.hasException()) {
            try {
                sslSocket.close();
            } catch (IOException e) {
                LOGGER.log(Level.FINER, "Closing TLS socket failed", e);
            }
            throw this.expectingTrustManager.getException();
        }
    }
}
