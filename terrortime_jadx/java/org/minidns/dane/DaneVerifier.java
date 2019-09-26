package org.minidns.dane;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateEncodingException;
import org.jivesoftware.smack.util.TLSUtils;
import org.minidns.dane.DaneCertificateException.CertificateMismatch;
import org.minidns.dane.DaneCertificateException.MultipleCertificateMismatchExceptions;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsname.DnsName;
import org.minidns.dnssec.DnssecClient;
import org.minidns.dnssec.DnssecQueryResult;
import org.minidns.dnssec.DnssecUnverifiedReason;
import org.minidns.record.Data;
import org.minidns.record.Record;
import org.minidns.record.Record.TYPE;
import org.minidns.record.TLSA;
import org.minidns.record.TLSA.CertUsage;
import org.minidns.record.TLSA.MatchingType;
import org.minidns.record.TLSA.Selector;

public class DaneVerifier {
    private static final Logger LOGGER = Logger.getLogger(DaneVerifier.class.getName());
    private final DnssecClient client;

    /* renamed from: org.minidns.dane.DaneVerifier$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$minidns$record$TLSA$CertUsage = new int[CertUsage.values().length];
        static final /* synthetic */ int[] $SwitchMap$org$minidns$record$TLSA$MatchingType = new int[MatchingType.values().length];
        static final /* synthetic */ int[] $SwitchMap$org$minidns$record$TLSA$Selector = new int[Selector.values().length];

        static {
            try {
                $SwitchMap$org$minidns$record$TLSA$MatchingType[MatchingType.noHash.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$minidns$record$TLSA$MatchingType[MatchingType.sha256.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$minidns$record$TLSA$MatchingType[MatchingType.sha512.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$minidns$record$TLSA$Selector[Selector.fullCertificate.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$minidns$record$TLSA$Selector[Selector.subjectPublicKeyInfo.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$minidns$record$TLSA$CertUsage[CertUsage.serviceCertificateConstraint.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$minidns$record$TLSA$CertUsage[CertUsage.domainIssuedCertificate.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$minidns$record$TLSA$CertUsage[CertUsage.caConstraint.ordinal()] = 3;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$org$minidns$record$TLSA$CertUsage[CertUsage.trustAnchorAssertion.ordinal()] = 4;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    public DaneVerifier() {
        this(new DnssecClient());
    }

    public DaneVerifier(DnssecClient client2) {
        this.client = client2;
    }

    public boolean verify(SSLSocket socket) throws CertificateException {
        if (socket.isConnected()) {
            return verify(socket.getSession());
        }
        throw new IllegalStateException("Socket not yet connected.");
    }

    public boolean verify(SSLSession session) throws CertificateException {
        try {
            return verifyCertificateChain(convert(session.getPeerCertificateChain()), session.getPeerHost(), session.getPeerPort());
        } catch (SSLPeerUnverifiedException e) {
            throw new CertificateException("Peer not verified", e);
        }
    }

    public boolean verifyCertificateChain(X509Certificate[] chain, String hostName, int port) throws CertificateException {
        StringBuilder sb = new StringBuilder();
        sb.append("_");
        sb.append(port);
        sb.append("._tcp.");
        sb.append(hostName);
        DnsName req = DnsName.from(sb.toString());
        try {
            DnssecQueryResult result = this.client.queryDnssec(req, TYPE.TLSA);
            DnsMessage res = result.dnsQueryResult.response;
            if (!result.isAuthenticData()) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Got TLSA response from DNS server, but was not signed properly.");
                sb2.append(" Reasons:");
                String msg = sb2.toString();
                for (DnssecUnverifiedReason reason : result.getUnverifiedReasons()) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(msg);
                    sb3.append(" ");
                    sb3.append(reason);
                    msg = sb3.toString();
                }
                LOGGER.info(msg);
                return false;
            }
            List<CertificateMismatch> certificateMismatchExceptions = new LinkedList<>();
            boolean verified = false;
            for (Record<? extends Data> record : res.answerSection) {
                if (record.type == TYPE.TLSA && record.name.equals(req)) {
                    try {
                        verified |= checkCertificateMatches(chain[0], (TLSA) record.payloadData, hostName);
                    } catch (CertificateMismatch certificateMismatchException) {
                        certificateMismatchExceptions.add(certificateMismatchException);
                    }
                    if (verified) {
                        break;
                    }
                }
            }
            if (verified || certificateMismatchExceptions.isEmpty()) {
                return verified;
            }
            throw new MultipleCertificateMismatchExceptions(certificateMismatchExceptions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean checkCertificateMatches(X509Certificate cert, TLSA tlsa, String hostName) throws CertificateException {
        byte[] comp;
        String str = " is not supported while verifying ";
        boolean z = false;
        if (tlsa.certUsage == null) {
            Logger logger = LOGGER;
            StringBuilder sb = new StringBuilder();
            sb.append("TLSA certificate usage byte ");
            sb.append(tlsa.certUsageByte);
            sb.append(str);
            sb.append(hostName);
            logger.warning(sb.toString());
            return false;
        }
        int i = AnonymousClass1.$SwitchMap$org$minidns$record$TLSA$CertUsage[tlsa.certUsage.ordinal()];
        String str2 = ") not supported while verifying ";
        String str3 = " (";
        if (i != 1 && i != 2) {
            Logger logger2 = LOGGER;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("TLSA certificate usage ");
            sb2.append(tlsa.certUsage);
            sb2.append(str3);
            sb2.append(tlsa.certUsageByte);
            sb2.append(str2);
            sb2.append(hostName);
            logger2.warning(sb2.toString());
            return false;
        } else if (tlsa.selector == null) {
            Logger logger3 = LOGGER;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("TLSA selector byte ");
            sb3.append(tlsa.selectorByte);
            sb3.append(str);
            sb3.append(hostName);
            logger3.warning(sb3.toString());
            return false;
        } else {
            int i2 = AnonymousClass1.$SwitchMap$org$minidns$record$TLSA$Selector[tlsa.selector.ordinal()];
            if (i2 == 1) {
                comp = cert.getEncoded();
            } else if (i2 != 2) {
                Logger logger4 = LOGGER;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("TLSA selector ");
                sb4.append(tlsa.selector);
                sb4.append(str3);
                sb4.append(tlsa.selectorByte);
                sb4.append(str2);
                sb4.append(hostName);
                logger4.warning(sb4.toString());
                return false;
            } else {
                comp = cert.getPublicKey().getEncoded();
            }
            if (tlsa.matchingType == null) {
                Logger logger5 = LOGGER;
                StringBuilder sb5 = new StringBuilder();
                sb5.append("TLSA matching type byte ");
                sb5.append(tlsa.matchingTypeByte);
                sb5.append(str);
                sb5.append(hostName);
                logger5.warning(sb5.toString());
                return false;
            }
            int i3 = AnonymousClass1.$SwitchMap$org$minidns$record$TLSA$MatchingType[tlsa.matchingType.ordinal()];
            if (i3 != 1) {
                if (i3 == 2) {
                    try {
                        comp = MessageDigest.getInstance("SHA-256").digest(comp);
                    } catch (NoSuchAlgorithmException e) {
                        throw new CertificateException("Verification using TLSA failed: could not SHA-256 for matching", e);
                    }
                } else if (i3 != 3) {
                    Logger logger6 = LOGGER;
                    StringBuilder sb6 = new StringBuilder();
                    sb6.append("TLSA matching type ");
                    sb6.append(tlsa.matchingType);
                    sb6.append(" not supported while verifying ");
                    sb6.append(hostName);
                    logger6.warning(sb6.toString());
                    return false;
                } else {
                    try {
                        comp = MessageDigest.getInstance("SHA-512").digest(comp);
                    } catch (NoSuchAlgorithmException e2) {
                        throw new CertificateException("Verification using TLSA failed: could not SHA-512 for matching", e2);
                    }
                }
            }
            if (tlsa.certificateAssociationEquals(comp)) {
                if (tlsa.certUsage == CertUsage.domainIssuedCertificate) {
                    z = true;
                }
                return z;
            }
            throw new CertificateMismatch(tlsa, comp);
        }
    }

    public HttpsURLConnection verifiedConnect(HttpsURLConnection conn) throws IOException, CertificateException {
        return verifiedConnect(conn, null);
    }

    public HttpsURLConnection verifiedConnect(HttpsURLConnection conn, X509TrustManager trustManager) throws IOException, CertificateException {
        try {
            SSLContext context = SSLContext.getInstance(TLSUtils.TLS);
            ExpectingTrustManager expectingTrustManager = new ExpectingTrustManager(trustManager);
            context.init(null, new TrustManager[]{expectingTrustManager}, null);
            conn.setSSLSocketFactory(context.getSocketFactory());
            conn.connect();
            if (!verifyCertificateChain(convert(conn.getServerCertificates()), conn.getURL().getHost(), conn.getURL().getPort() < 0 ? conn.getURL().getDefaultPort() : conn.getURL().getPort())) {
                if (expectingTrustManager.hasException()) {
                    throw new IOException("Peer verification failed using PKIX", expectingTrustManager.getException());
                }
            }
            return conn;
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static X509Certificate[] convert(Certificate[] certificates) {
        List<X509Certificate> certs = new ArrayList<>();
        for (Certificate certificate : certificates) {
            if (certificate instanceof X509Certificate) {
                certs.add((X509Certificate) certificate);
            }
        }
        return (X509Certificate[]) certs.toArray(new X509Certificate[certs.size()]);
    }

    private static X509Certificate[] convert(javax.security.cert.X509Certificate[] certificates) {
        X509Certificate[] certs = new X509Certificate[certificates.length];
        for (int i = 0; i < certificates.length; i++) {
            try {
                certs[i] = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certificates[i].getEncoded()));
            } catch (CertificateException | CertificateEncodingException e) {
                LOGGER.log(Level.WARNING, "Could not convert", e);
            }
        }
        return certs;
    }
}
