package org.bouncycastle.est;

import com.badguy.terrortime.BuildConfig;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cmc.CMCException;
import org.bouncycastle.cmc.SimplePKIResponse;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;
import org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp;

public class ESTService {
    protected static final String CACERTS = "/cacerts";
    protected static final String CSRATTRS = "/csrattrs";
    protected static final String FULLCMC = "/fullcmc";
    protected static final String SERVERGEN = "/serverkeygen";
    protected static final String SIMPLE_ENROLL = "/simpleenroll";
    protected static final String SIMPLE_REENROLL = "/simplereenroll";
    protected static final Set<String> illegalParts = new HashSet();
    private static final Pattern pathInvalid = Pattern.compile("^[0-9a-zA-Z_\\-.~!$&'()*+,;=]+");
    private final ESTClientProvider clientProvider;
    private final String server;

    static {
        illegalParts.add(CACERTS.substring(1));
        illegalParts.add(SIMPLE_ENROLL.substring(1));
        illegalParts.add(SIMPLE_REENROLL.substring(1));
        illegalParts.add(FULLCMC.substring(1));
        illegalParts.add(SERVERGEN.substring(1));
        illegalParts.add(CSRATTRS.substring(1));
    }

    ESTService(String str, String str2, ESTClientProvider eSTClientProvider) {
        String str3;
        String verifyServer = verifyServer(str);
        String str4 = "https://";
        if (str2 != null) {
            String verifyLabel = verifyLabel(str2);
            StringBuilder sb = new StringBuilder();
            sb.append(str4);
            sb.append(verifyServer);
            sb.append("/.well-known/est/");
            sb.append(verifyLabel);
            str3 = sb.toString();
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str4);
            sb2.append(verifyServer);
            sb2.append("/.well-known/est");
            str3 = sb2.toString();
        }
        this.server = str3;
        this.clientProvider = eSTClientProvider;
    }

    /* access modifiers changed from: private */
    public String annotateRequest(byte[] bArr) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        int i = 0;
        do {
            int i2 = i + 48;
            if (i2 < bArr.length) {
                printWriter.print(Base64.toBase64String(bArr, i, 48));
                i = i2;
            } else {
                printWriter.print(Base64.toBase64String(bArr, i, bArr.length - i));
                i = bArr.length;
            }
            printWriter.print(10);
        } while (i < bArr.length);
        printWriter.flush();
        return stringWriter.toString();
    }

    public static X509CertificateHolder[] storeToArray(Store<X509CertificateHolder> store) {
        return storeToArray(store, null);
    }

    public static X509CertificateHolder[] storeToArray(Store<X509CertificateHolder> store, Selector<X509CertificateHolder> selector) {
        Collection matches = store.getMatches(selector);
        return (X509CertificateHolder[]) matches.toArray(new X509CertificateHolder[matches.size()]);
    }

    private String verifyLabel(String str) {
        String str2;
        while (true) {
            str2 = "/";
            if (str.endsWith(str2) && str.length() > 0) {
                str = str.substring(0, str.length() - 1);
            }
        }
        while (str.startsWith(str2) && str.length() > 0) {
            str = str.substring(1);
        }
        if (str.length() == 0) {
            throw new IllegalArgumentException("Label set but after trimming '/' is not zero length string.");
        } else if (!pathInvalid.matcher(str).matches()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Server path ");
            sb.append(str);
            sb.append(" contains invalid characters");
            throw new IllegalArgumentException(sb.toString());
        } else if (!illegalParts.contains(str)) {
            return str;
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Label ");
            sb2.append(str);
            sb2.append(" is a reserved path segment.");
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    private String verifyServer(String str) {
        String str2 = "/";
        while (str.endsWith(str2) && str.length() > 0) {
            try {
                str = str.substring(0, str.length() - 1);
            } catch (Exception e) {
                if (e instanceof IllegalArgumentException) {
                    throw ((IllegalArgumentException) e);
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Scheme and host is invalid: ");
                sb.append(e.getMessage());
                throw new IllegalArgumentException(sb.toString(), e);
            }
        }
        if (!str.contains("://")) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("https://");
            sb2.append(str);
            URL url = new URL(sb2.toString());
            if (url.getPath().length() != 0) {
                if (!url.getPath().equals(str2)) {
                    throw new IllegalArgumentException("Server contains path, must only be <dnsname/ipaddress>:port, a path of '/.well-known/est/<label>' will be added arbitrarily.");
                }
            }
            return str;
        }
        throw new IllegalArgumentException("Server contains scheme, must only be <dnsname/ipaddress>:port, https:// will be added arbitrarily.");
    }

    /* JADX WARNING: Removed duplicated region for block: B:54:0x017a A[Catch:{ all -> 0x0187 }] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x017d A[Catch:{ all -> 0x0187 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.bouncycastle.est.CACertsResponse getCACerts() throws java.lang.Exception {
        /*
            r12 = this;
            java.lang.String r0 = "Content-Type"
            r1 = 0
            java.net.URL r2 = new java.net.URL     // Catch:{ all -> 0x0174 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0174 }
            r3.<init>()     // Catch:{ all -> 0x0174 }
            java.lang.String r4 = r12.server     // Catch:{ all -> 0x0174 }
            r3.append(r4)     // Catch:{ all -> 0x0174 }
            java.lang.String r4 = "/cacerts"
            r3.append(r4)     // Catch:{ all -> 0x0174 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0174 }
            r2.<init>(r3)     // Catch:{ all -> 0x0174 }
            org.bouncycastle.est.ESTClientProvider r3 = r12.clientProvider     // Catch:{ all -> 0x0174 }
            org.bouncycastle.est.ESTClient r3 = r3.makeClient()     // Catch:{ all -> 0x0174 }
            org.bouncycastle.est.ESTRequestBuilder r4 = new org.bouncycastle.est.ESTRequestBuilder     // Catch:{ all -> 0x0174 }
            java.lang.String r5 = "GET"
            r4.<init>(r5, r2)     // Catch:{ all -> 0x0174 }
            org.bouncycastle.est.ESTRequestBuilder r4 = r4.withClient(r3)     // Catch:{ all -> 0x0174 }
            org.bouncycastle.est.ESTRequest r8 = r4.build()     // Catch:{ all -> 0x0174 }
            org.bouncycastle.est.ESTResponse r3 = r3.doRequest(r8)     // Catch:{ all -> 0x0174 }
            int r4 = r3.getStatusCode()     // Catch:{ all -> 0x0172 }
            r5 = 200(0xc8, float:2.8E-43)
            java.lang.String r11 = "Get CACerts: "
            if (r4 != r5) goto L_0x0109
            java.lang.String r4 = "application/pkcs7-mime"
            org.bouncycastle.est.HttpUtil$Headers r5 = r3.getHeaders()     // Catch:{ all -> 0x0172 }
            java.lang.String r5 = r5.getFirstValue(r0)     // Catch:{ all -> 0x0172 }
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x0172 }
            if (r4 != 0) goto L_0x009f
            org.bouncycastle.est.HttpUtil$Headers r4 = r3.getHeaders()     // Catch:{ all -> 0x0172 }
            java.lang.String r4 = r4.getFirstValue(r0)     // Catch:{ all -> 0x0172 }
            if (r4 == 0) goto L_0x0072
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0172 }
            r4.<init>()     // Catch:{ all -> 0x0172 }
            java.lang.String r5 = " got "
            r4.append(r5)     // Catch:{ all -> 0x0172 }
            org.bouncycastle.est.HttpUtil$Headers r5 = r3.getHeaders()     // Catch:{ all -> 0x0172 }
            java.lang.String r0 = r5.getFirstValue(r0)     // Catch:{ all -> 0x0172 }
            r4.append(r0)     // Catch:{ all -> 0x0172 }
            java.lang.String r0 = r4.toString()     // Catch:{ all -> 0x0172 }
            goto L_0x0074
        L_0x0072:
            java.lang.String r0 = " but was not present."
        L_0x0074:
            org.bouncycastle.est.ESTException r4 = new org.bouncycastle.est.ESTException     // Catch:{ all -> 0x0172 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0172 }
            r5.<init>()     // Catch:{ all -> 0x0172 }
            java.lang.String r6 = "Response : "
            r5.append(r6)     // Catch:{ all -> 0x0172 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0172 }
            r5.append(r2)     // Catch:{ all -> 0x0172 }
            java.lang.String r2 = "Expecting application/pkcs7-mime "
            r5.append(r2)     // Catch:{ all -> 0x0172 }
            r5.append(r0)     // Catch:{ all -> 0x0172 }
            java.lang.String r0 = r5.toString()     // Catch:{ all -> 0x0172 }
            int r2 = r3.getStatusCode()     // Catch:{ all -> 0x0172 }
            java.io.InputStream r5 = r3.getInputStream()     // Catch:{ all -> 0x0172 }
            r4.<init>(r0, r1, r2, r5)     // Catch:{ all -> 0x0172 }
            throw r4     // Catch:{ all -> 0x0172 }
        L_0x009f:
            java.lang.Long r0 = r3.getContentLength()     // Catch:{ all -> 0x00d9 }
            if (r0 == 0) goto L_0x00d4
            java.lang.Long r0 = r3.getContentLength()     // Catch:{ all -> 0x00d9 }
            long r4 = r0.longValue()     // Catch:{ all -> 0x00d9 }
            r6 = 0
            int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r0 <= 0) goto L_0x00d4
            org.bouncycastle.asn1.ASN1InputStream r0 = new org.bouncycastle.asn1.ASN1InputStream     // Catch:{ all -> 0x00d9 }
            java.io.InputStream r4 = r3.getInputStream()     // Catch:{ all -> 0x00d9 }
            r0.<init>(r4)     // Catch:{ all -> 0x00d9 }
            org.bouncycastle.cmc.SimplePKIResponse r4 = new org.bouncycastle.cmc.SimplePKIResponse     // Catch:{ all -> 0x00d9 }
            org.bouncycastle.asn1.ASN1Primitive r0 = r0.readObject()     // Catch:{ all -> 0x00d9 }
            org.bouncycastle.asn1.ASN1Sequence r0 = (org.bouncycastle.asn1.ASN1Sequence) r0     // Catch:{ all -> 0x00d9 }
            org.bouncycastle.asn1.cms.ContentInfo r0 = org.bouncycastle.asn1.cms.ContentInfo.getInstance(r0)     // Catch:{ all -> 0x00d9 }
            r4.<init>(r0)     // Catch:{ all -> 0x00d9 }
            org.bouncycastle.util.Store r0 = r4.getCertificates()     // Catch:{ all -> 0x00d9 }
            org.bouncycastle.util.Store r4 = r4.getCRLs()     // Catch:{ all -> 0x00d9 }
            goto L_0x00d6
        L_0x00d4:
            r0 = r1
            r4 = r0
        L_0x00d6:
            r6 = r0
            r7 = r4
            goto L_0x0113
        L_0x00d9:
            r0 = move-exception
            org.bouncycastle.est.ESTException r1 = new org.bouncycastle.est.ESTException     // Catch:{ all -> 0x0172 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0172 }
            r4.<init>()     // Catch:{ all -> 0x0172 }
            java.lang.String r5 = "Decoding CACerts: "
            r4.append(r5)     // Catch:{ all -> 0x0172 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0172 }
            r4.append(r2)     // Catch:{ all -> 0x0172 }
            java.lang.String r2 = " "
            r4.append(r2)     // Catch:{ all -> 0x0172 }
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x0172 }
            r4.append(r2)     // Catch:{ all -> 0x0172 }
            java.lang.String r2 = r4.toString()     // Catch:{ all -> 0x0172 }
            int r4 = r3.getStatusCode()     // Catch:{ all -> 0x0172 }
            java.io.InputStream r5 = r3.getInputStream()     // Catch:{ all -> 0x0172 }
            r1.<init>(r2, r0, r4, r5)     // Catch:{ all -> 0x0172 }
            throw r1     // Catch:{ all -> 0x0172 }
        L_0x0109:
            int r0 = r3.getStatusCode()     // Catch:{ all -> 0x0172 }
            r4 = 204(0xcc, float:2.86E-43)
            if (r0 != r4) goto L_0x0151
            r6 = r1
            r7 = r6
        L_0x0113:
            org.bouncycastle.est.CACertsResponse r0 = new org.bouncycastle.est.CACertsResponse     // Catch:{ all -> 0x0172 }
            org.bouncycastle.est.Source r9 = r3.getSource()     // Catch:{ all -> 0x0172 }
            org.bouncycastle.est.ESTClientProvider r4 = r12.clientProvider     // Catch:{ all -> 0x0172 }
            boolean r10 = r4.isTrusted()     // Catch:{ all -> 0x0172 }
            r5 = r0
            r5.<init>(r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0172 }
            if (r3 == 0) goto L_0x012b
            r3.close()     // Catch:{ Exception -> 0x0129 }
            goto L_0x012b
        L_0x0129:
            r4 = move-exception
            goto L_0x012c
        L_0x012b:
            r4 = r1
        L_0x012c:
            if (r4 == 0) goto L_0x0150
            boolean r0 = r4 instanceof org.bouncycastle.est.ESTException
            if (r0 == 0) goto L_0x0133
            throw r4
        L_0x0133:
            org.bouncycastle.est.ESTException r0 = new org.bouncycastle.est.ESTException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r11)
            java.lang.String r2 = r2.toString()
            r5.append(r2)
            java.lang.String r2 = r5.toString()
            int r3 = r3.getStatusCode()
            r0.<init>(r2, r4, r3, r1)
            throw r0
        L_0x0150:
            return r0
        L_0x0151:
            org.bouncycastle.est.ESTException r0 = new org.bouncycastle.est.ESTException     // Catch:{ all -> 0x0172 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0172 }
            r4.<init>()     // Catch:{ all -> 0x0172 }
            r4.append(r11)     // Catch:{ all -> 0x0172 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0172 }
            r4.append(r2)     // Catch:{ all -> 0x0172 }
            java.lang.String r2 = r4.toString()     // Catch:{ all -> 0x0172 }
            int r4 = r3.getStatusCode()     // Catch:{ all -> 0x0172 }
            java.io.InputStream r5 = r3.getInputStream()     // Catch:{ all -> 0x0172 }
            r0.<init>(r2, r1, r4, r5)     // Catch:{ all -> 0x0172 }
            throw r0     // Catch:{ all -> 0x0172 }
        L_0x0172:
            r0 = move-exception
            goto L_0x0176
        L_0x0174:
            r0 = move-exception
            r3 = r1
        L_0x0176:
            boolean r1 = r0 instanceof org.bouncycastle.est.ESTException     // Catch:{ all -> 0x0187 }
            if (r1 == 0) goto L_0x017d
            org.bouncycastle.est.ESTException r0 = (org.bouncycastle.est.ESTException) r0     // Catch:{ all -> 0x0187 }
            throw r0     // Catch:{ all -> 0x0187 }
        L_0x017d:
            org.bouncycastle.est.ESTException r1 = new org.bouncycastle.est.ESTException     // Catch:{ all -> 0x0187 }
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x0187 }
            r1.<init>(r2, r0)     // Catch:{ all -> 0x0187 }
            throw r1     // Catch:{ all -> 0x0187 }
        L_0x0187:
            r0 = move-exception
            if (r3 == 0) goto L_0x018f
            r3.close()     // Catch:{ Exception -> 0x018e }
            goto L_0x018f
        L_0x018e:
            r1 = move-exception
        L_0x018f:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.est.ESTService.getCACerts():org.bouncycastle.est.CACertsResponse");
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x00a2 A[SYNTHETIC, Splitter:B:23:0x00a2] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00c0  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0104 A[Catch:{ all -> 0x0111 }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0107 A[Catch:{ all -> 0x0111 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.bouncycastle.est.CSRRequestResponse getCSRAttributes() throws org.bouncycastle.est.ESTException {
        /*
            r7 = this;
            org.bouncycastle.est.ESTClientProvider r0 = r7.clientProvider
            boolean r0 = r0.isTrusted()
            if (r0 == 0) goto L_0x011a
            r0 = 0
            java.net.URL r1 = new java.net.URL     // Catch:{ all -> 0x00fc }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00fc }
            r2.<init>()     // Catch:{ all -> 0x00fc }
            java.lang.String r3 = r7.server     // Catch:{ all -> 0x00fc }
            r2.append(r3)     // Catch:{ all -> 0x00fc }
            java.lang.String r3 = "/csrattrs"
            r2.append(r3)     // Catch:{ all -> 0x00fc }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00fc }
            r1.<init>(r2)     // Catch:{ all -> 0x00fc }
            org.bouncycastle.est.ESTClientProvider r2 = r7.clientProvider     // Catch:{ all -> 0x00fc }
            org.bouncycastle.est.ESTClient r2 = r2.makeClient()     // Catch:{ all -> 0x00fc }
            org.bouncycastle.est.ESTRequestBuilder r3 = new org.bouncycastle.est.ESTRequestBuilder     // Catch:{ all -> 0x00fc }
            java.lang.String r4 = "GET"
            r3.<init>(r4, r1)     // Catch:{ all -> 0x00fc }
            org.bouncycastle.est.ESTRequestBuilder r3 = r3.withClient(r2)     // Catch:{ all -> 0x00fc }
            org.bouncycastle.est.ESTRequest r3 = r3.build()     // Catch:{ all -> 0x00fc }
            org.bouncycastle.est.ESTResponse r2 = r2.doRequest(r3)     // Catch:{ all -> 0x00fc }
            int r4 = r2.getStatusCode()     // Catch:{ all -> 0x00fa }
            r5 = 200(0xc8, float:2.8E-43)
            if (r4 == r5) goto L_0x0074
            r1 = 204(0xcc, float:2.86E-43)
            if (r4 == r1) goto L_0x0072
            r1 = 404(0x194, float:5.66E-43)
            if (r4 != r1) goto L_0x004b
            goto L_0x0072
        L_0x004b:
            org.bouncycastle.est.ESTException r1 = new org.bouncycastle.est.ESTException     // Catch:{ all -> 0x00fa }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00fa }
            r4.<init>()     // Catch:{ all -> 0x00fa }
            java.lang.String r5 = "CSR Attribute request: "
            r4.append(r5)     // Catch:{ all -> 0x00fa }
            java.net.URL r3 = r3.getURL()     // Catch:{ all -> 0x00fa }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00fa }
            r4.append(r3)     // Catch:{ all -> 0x00fa }
            java.lang.String r3 = r4.toString()     // Catch:{ all -> 0x00fa }
            int r4 = r2.getStatusCode()     // Catch:{ all -> 0x00fa }
            java.io.InputStream r5 = r2.getInputStream()     // Catch:{ all -> 0x00fa }
            r1.<init>(r3, r0, r4, r5)     // Catch:{ all -> 0x00fa }
            throw r1     // Catch:{ all -> 0x00fa }
        L_0x0072:
            r4 = r0
            goto L_0x00a0
        L_0x0074:
            java.lang.Long r3 = r2.getContentLength()     // Catch:{ all -> 0x00ca }
            if (r3 == 0) goto L_0x0072
            java.lang.Long r3 = r2.getContentLength()     // Catch:{ all -> 0x00ca }
            long r3 = r3.longValue()     // Catch:{ all -> 0x00ca }
            r5 = 0
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 <= 0) goto L_0x0072
            org.bouncycastle.asn1.ASN1InputStream r3 = new org.bouncycastle.asn1.ASN1InputStream     // Catch:{ all -> 0x00ca }
            java.io.InputStream r4 = r2.getInputStream()     // Catch:{ all -> 0x00ca }
            r3.<init>(r4)     // Catch:{ all -> 0x00ca }
            org.bouncycastle.asn1.ASN1Primitive r3 = r3.readObject()     // Catch:{ all -> 0x00ca }
            org.bouncycastle.asn1.ASN1Sequence r3 = (org.bouncycastle.asn1.ASN1Sequence) r3     // Catch:{ all -> 0x00ca }
            org.bouncycastle.est.CSRAttributesResponse r4 = new org.bouncycastle.est.CSRAttributesResponse     // Catch:{ all -> 0x00ca }
            org.bouncycastle.asn1.est.CsrAttrs r3 = org.bouncycastle.asn1.est.CsrAttrs.getInstance(r3)     // Catch:{ all -> 0x00ca }
            r4.<init>(r3)     // Catch:{ all -> 0x00ca }
        L_0x00a0:
            if (r2 == 0) goto L_0x00a8
            r2.close()     // Catch:{ Exception -> 0x00a6 }
            goto L_0x00a8
        L_0x00a6:
            r1 = move-exception
            goto L_0x00a9
        L_0x00a8:
            r1 = r0
        L_0x00a9:
            if (r1 == 0) goto L_0x00c0
            boolean r3 = r1 instanceof org.bouncycastle.est.ESTException
            if (r3 == 0) goto L_0x00b2
            org.bouncycastle.est.ESTException r1 = (org.bouncycastle.est.ESTException) r1
            throw r1
        L_0x00b2:
            org.bouncycastle.est.ESTException r3 = new org.bouncycastle.est.ESTException
            java.lang.String r4 = r1.getMessage()
            int r2 = r2.getStatusCode()
            r3.<init>(r4, r1, r2, r0)
            throw r3
        L_0x00c0:
            org.bouncycastle.est.CSRRequestResponse r0 = new org.bouncycastle.est.CSRRequestResponse
            org.bouncycastle.est.Source r1 = r2.getSource()
            r0.<init>(r4, r1)
            return r0
        L_0x00ca:
            r0 = move-exception
            org.bouncycastle.est.ESTException r3 = new org.bouncycastle.est.ESTException     // Catch:{ all -> 0x00fa }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00fa }
            r4.<init>()     // Catch:{ all -> 0x00fa }
            java.lang.String r5 = "Decoding CACerts: "
            r4.append(r5)     // Catch:{ all -> 0x00fa }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x00fa }
            r4.append(r1)     // Catch:{ all -> 0x00fa }
            java.lang.String r1 = " "
            r4.append(r1)     // Catch:{ all -> 0x00fa }
            java.lang.String r1 = r0.getMessage()     // Catch:{ all -> 0x00fa }
            r4.append(r1)     // Catch:{ all -> 0x00fa }
            java.lang.String r1 = r4.toString()     // Catch:{ all -> 0x00fa }
            int r4 = r2.getStatusCode()     // Catch:{ all -> 0x00fa }
            java.io.InputStream r5 = r2.getInputStream()     // Catch:{ all -> 0x00fa }
            r3.<init>(r1, r0, r4, r5)     // Catch:{ all -> 0x00fa }
            throw r3     // Catch:{ all -> 0x00fa }
        L_0x00fa:
            r0 = move-exception
            goto L_0x00ff
        L_0x00fc:
            r1 = move-exception
            r2 = r0
            r0 = r1
        L_0x00ff:
            boolean r1 = r0 instanceof org.bouncycastle.est.ESTException     // Catch:{ all -> 0x0111 }
            if (r1 == 0) goto L_0x0107
            org.bouncycastle.est.ESTException r0 = (org.bouncycastle.est.ESTException) r0     // Catch:{ all -> 0x0111 }
            throw r0     // Catch:{ all -> 0x0111 }
        L_0x0107:
            org.bouncycastle.est.ESTException r1 = new org.bouncycastle.est.ESTException     // Catch:{ all -> 0x0111 }
            java.lang.String r3 = r0.getMessage()     // Catch:{ all -> 0x0111 }
            r1.<init>(r3, r0)     // Catch:{ all -> 0x0111 }
            throw r1     // Catch:{ all -> 0x0111 }
        L_0x0111:
            r0 = move-exception
            if (r2 == 0) goto L_0x0119
            r2.close()     // Catch:{ Exception -> 0x0118 }
            goto L_0x0119
        L_0x0118:
            r1 = move-exception
        L_0x0119:
            throw r0
        L_0x011a:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "No trust anchors."
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.est.ESTService.getCSRAttributes():org.bouncycastle.est.CSRRequestResponse");
    }

    /* access modifiers changed from: protected */
    public EnrollmentResponse handleEnrollResponse(ESTResponse eSTResponse) throws IOException {
        long j;
        ESTRequest originalRequest = eSTResponse.getOriginalRequest();
        if (eSTResponse.getStatusCode() == 202) {
            String header = eSTResponse.getHeader("Retry-After");
            if (header != null) {
                try {
                    j = System.currentTimeMillis() + (Long.parseLong(header) * 1000);
                } catch (NumberFormatException e) {
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                        j = simpleDateFormat.parse(header).getTime();
                    } catch (Exception e2) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Unable to parse Retry-After header:");
                        sb.append(originalRequest.getURL().toString());
                        sb.append(" ");
                        sb.append(e2.getMessage());
                        throw new ESTException(sb.toString(), null, eSTResponse.getStatusCode(), eSTResponse.getInputStream());
                    }
                }
                EnrollmentResponse enrollmentResponse = new EnrollmentResponse(null, j, originalRequest, eSTResponse.getSource());
                return enrollmentResponse;
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Got Status 202 but not Retry-After header from: ");
            sb2.append(originalRequest.getURL().toString());
            throw new ESTException(sb2.toString());
        } else if (eSTResponse.getStatusCode() == 200) {
            try {
                EnrollmentResponse enrollmentResponse2 = new EnrollmentResponse(new SimplePKIResponse(ContentInfo.getInstance(new ASN1InputStream(eSTResponse.getInputStream()).readObject())).getCertificates(), -1, null, eSTResponse.getSource());
                return enrollmentResponse2;
            } catch (CMCException e3) {
                throw new ESTException(e3.getMessage(), e3.getCause());
            }
        } else {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Simple Enroll: ");
            sb3.append(originalRequest.getURL().toString());
            throw new ESTException(sb3.toString(), null, eSTResponse.getStatusCode(), eSTResponse.getInputStream());
        }
    }

    public EnrollmentResponse simpleEnroll(EnrollmentResponse enrollmentResponse) throws Exception {
        if (this.clientProvider.isTrusted()) {
            ESTResponse eSTResponse = null;
            try {
                ESTClient makeClient = this.clientProvider.makeClient();
                ESTResponse doRequest = makeClient.doRequest(new ESTRequestBuilder(enrollmentResponse.getRequestToRetry()).withClient(makeClient).build());
                EnrollmentResponse handleEnrollResponse = handleEnrollResponse(doRequest);
                if (doRequest != null) {
                    doRequest.close();
                }
                return handleEnrollResponse;
            } catch (Throwable th) {
                if (eSTResponse != null) {
                    eSTResponse.close();
                }
                throw th;
            }
        } else {
            throw new IllegalStateException("No trust anchors.");
        }
    }

    public EnrollmentResponse simpleEnroll(boolean z, PKCS10CertificationRequest pKCS10CertificationRequest, ESTAuth eSTAuth) throws IOException {
        if (this.clientProvider.isTrusted()) {
            ESTResponse eSTResponse = null;
            try {
                byte[] bytes = annotateRequest(pKCS10CertificationRequest.getEncoded()).getBytes();
                StringBuilder sb = new StringBuilder();
                sb.append(this.server);
                sb.append(z ? SIMPLE_REENROLL : SIMPLE_ENROLL);
                URL url = new URL(sb.toString());
                ESTClient makeClient = this.clientProvider.makeClient();
                ESTRequestBuilder withClient = new ESTRequestBuilder("POST", url).withData(bytes).withClient(makeClient);
                withClient.addHeader("Content-Type", "application/pkcs10");
                StringBuilder sb2 = new StringBuilder();
                sb2.append(BuildConfig.FLAVOR);
                sb2.append(bytes.length);
                withClient.addHeader("Content-Length", sb2.toString());
                withClient.addHeader("Content-Transfer-Encoding", AbstractHttpOverXmpp.Base64.ELEMENT);
                if (eSTAuth != null) {
                    eSTAuth.applyAuth(withClient);
                }
                ESTResponse doRequest = makeClient.doRequest(withClient.build());
                EnrollmentResponse handleEnrollResponse = handleEnrollResponse(doRequest);
                if (doRequest != null) {
                    doRequest.close();
                }
                return handleEnrollResponse;
            } catch (Throwable th) {
                if (eSTResponse != null) {
                    eSTResponse.close();
                }
                throw th;
            }
        } else {
            throw new IllegalStateException("No trust anchors.");
        }
    }

    public EnrollmentResponse simpleEnrollPoP(boolean z, final PKCS10CertificationRequestBuilder pKCS10CertificationRequestBuilder, final ContentSigner contentSigner, ESTAuth eSTAuth) throws IOException {
        if (this.clientProvider.isTrusted()) {
            ESTResponse eSTResponse = null;
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(this.server);
                sb.append(z ? SIMPLE_REENROLL : SIMPLE_ENROLL);
                URL url = new URL(sb.toString());
                ESTClient makeClient = this.clientProvider.makeClient();
                ESTRequestBuilder withConnectionListener = new ESTRequestBuilder("POST", url).withClient(makeClient).withConnectionListener(new ESTSourceConnectionListener() {
                    public ESTRequest onConnection(Source source, ESTRequest eSTRequest) throws IOException {
                        if (source instanceof TLSUniqueProvider) {
                            TLSUniqueProvider tLSUniqueProvider = (TLSUniqueProvider) source;
                            if (tLSUniqueProvider.isTLSUniqueAvailable()) {
                                PKCS10CertificationRequestBuilder pKCS10CertificationRequestBuilder = new PKCS10CertificationRequestBuilder(pKCS10CertificationRequestBuilder);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                pKCS10CertificationRequestBuilder.setAttribute(PKCSObjectIdentifiers.pkcs_9_at_challengePassword, (ASN1Encodable) new DERPrintableString(Base64.toBase64String(tLSUniqueProvider.getTLSUnique())));
                                byteArrayOutputStream.write(ESTService.this.annotateRequest(pKCS10CertificationRequestBuilder.build(contentSigner).getEncoded()).getBytes());
                                byteArrayOutputStream.flush();
                                ESTRequestBuilder withData = new ESTRequestBuilder(eSTRequest).withData(byteArrayOutputStream.toByteArray());
                                withData.setHeader("Content-Type", "application/pkcs10");
                                withData.setHeader("Content-Transfer-Encoding", AbstractHttpOverXmpp.Base64.ELEMENT);
                                withData.setHeader("Content-Length", Long.toString((long) byteArrayOutputStream.size()));
                                return withData.build();
                            }
                        }
                        throw new IOException("Source does not supply TLS unique.");
                    }
                });
                if (eSTAuth != null) {
                    eSTAuth.applyAuth(withConnectionListener);
                }
                ESTResponse doRequest = makeClient.doRequest(withConnectionListener.build());
                EnrollmentResponse handleEnrollResponse = handleEnrollResponse(doRequest);
                if (doRequest != null) {
                    doRequest.close();
                }
                return handleEnrollResponse;
            } catch (Throwable th) {
                if (eSTResponse != null) {
                    eSTResponse.close();
                }
                throw th;
            }
        } else {
            throw new IllegalStateException("No trust anchors.");
        }
    }
}
