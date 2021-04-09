package org.jivesoftware.smack.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.jivesoftware.smack.ConnectionConfiguration.Builder;
import org.jivesoftware.smack.SmackException.SecurityNotPossibleException;

public class TLSUtils {
    private static final HostnameVerifier DOES_NOT_VERIFY_VERIFIER = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    public static final String PROTO_SSL3 = "SSLv3";
    public static final String PROTO_TLSV1 = "TLSv1";
    public static final String PROTO_TLSV1_1 = "TLSv1.1";
    public static final String PROTO_TLSV1_2 = "TLSv1.2";
    public static final String SSL = "SSL";
    public static final String TLS = "TLS";

    public static class AcceptAllTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static <B extends Builder<B, ?>> B setTLSOnly(B builder) {
        builder.setEnabledSSLProtocols(new String[]{PROTO_TLSV1_2, PROTO_TLSV1_1, PROTO_TLSV1});
        return builder;
    }

    public static <B extends Builder<B, ?>> B setSSLv3AndTLSOnly(B builder) {
        builder.setEnabledSSLProtocols(new String[]{PROTO_TLSV1_2, PROTO_TLSV1_1, PROTO_TLSV1, PROTO_SSL3});
        return builder;
    }

    public static <B extends Builder<B, ?>> B acceptAllCertificates(B builder) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getInstance(TLS);
        context.init(null, new TrustManager[]{new AcceptAllTrustManager()}, new SecureRandom());
        builder.setCustomSSLContext(context);
        return builder;
    }

    public static <B extends Builder<B, ?>> B disableHostnameVerificationForTlsCertificates(B builder) {
        builder.setHostnameVerifier(DOES_NOT_VERIFY_VERIFIER);
        return builder;
    }

    public static void setEnabledProtocolsAndCiphers(SSLSocket sslSocket, String[] enabledProtocols, String[] enabledCiphers) throws SecurityNotPossibleException {
        String str = "' are supported.";
        String str2 = "', but only '";
        if (enabledProtocols != null) {
            Set<String> enabledProtocolsSet = new HashSet<>(Arrays.asList(enabledProtocols));
            Set<String> supportedProtocolsSet = new HashSet<>(Arrays.asList(sslSocket.getSupportedProtocols()));
            Set<String> protocolsIntersection = new HashSet<>(supportedProtocolsSet);
            protocolsIntersection.retainAll(enabledProtocolsSet);
            if (!protocolsIntersection.isEmpty()) {
                sslSocket.setEnabledProtocols((String[]) protocolsIntersection.toArray(new String[protocolsIntersection.size()]));
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Request to enable SSL/TLS protocols '");
                sb.append(StringUtils.collectionToString(enabledProtocolsSet));
                sb.append(str2);
                sb.append(StringUtils.collectionToString(supportedProtocolsSet));
                sb.append(str);
                throw new SecurityNotPossibleException(sb.toString());
            }
        }
        if (enabledCiphers != null) {
            Set<String> enabledCiphersSet = new HashSet<>(Arrays.asList(enabledCiphers));
            Set<String> supportedCiphersSet = new HashSet<>(Arrays.asList(sslSocket.getEnabledCipherSuites()));
            Set<String> ciphersIntersection = new HashSet<>(supportedCiphersSet);
            ciphersIntersection.retainAll(enabledCiphersSet);
            if (!ciphersIntersection.isEmpty()) {
                sslSocket.setEnabledCipherSuites((String[]) ciphersIntersection.toArray(new String[ciphersIntersection.size()]));
                return;
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Request to enable SSL/TLS ciphers '");
            sb2.append(StringUtils.collectionToString(enabledCiphersSet));
            sb2.append(str2);
            sb2.append(StringUtils.collectionToString(supportedCiphersSet));
            sb2.append(str);
            throw new SecurityNotPossibleException(sb2.toString());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x002f, code lost:
        if (r3.equals(org.jivesoftware.smack.util.StringUtils.MD5) != false) goto L_0x0033;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] getChannelBindingTlsServerEndPoint(javax.net.ssl.SSLSession r7) throws javax.net.ssl.SSLPeerUnverifiedException, java.security.cert.CertificateEncodingException, java.security.NoSuchAlgorithmException {
        /*
            java.security.cert.Certificate[] r0 = r7.getPeerCertificates()
            r1 = 0
            r2 = r0[r1]
            java.security.PublicKey r3 = r2.getPublicKey()
            java.lang.String r3 = r3.getAlgorithm()
            int r4 = r3.hashCode()
            r5 = 76158(0x1297e, float:1.0672E-40)
            r6 = 1
            if (r4 == r5) goto L_0x0029
            r1 = 78861104(0x4b35330, float:4.2159093E-36)
            if (r4 == r1) goto L_0x001f
        L_0x001e:
            goto L_0x0032
        L_0x001f:
            java.lang.String r1 = "SHA-1"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x001e
            r1 = r6
            goto L_0x0033
        L_0x0029:
            java.lang.String r4 = "MD5"
            boolean r4 = r3.equals(r4)
            if (r4 == 0) goto L_0x001e
            goto L_0x0033
        L_0x0032:
            r1 = -1
        L_0x0033:
            if (r1 == 0) goto L_0x0039
            if (r1 == r6) goto L_0x0039
            r1 = r3
            goto L_0x003c
        L_0x0039:
            java.lang.String r1 = "SHA-256"
        L_0x003c:
            java.security.MessageDigest r4 = java.security.MessageDigest.getInstance(r1)
            byte[] r5 = r2.getEncoded()
            r4.update(r5)
            byte[] r6 = r4.digest()
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.util.TLSUtils.getChannelBindingTlsServerEndPoint(javax.net.ssl.SSLSession):byte[]");
    }
}
