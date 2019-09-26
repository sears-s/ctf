package org.bouncycastle.est.jcajce;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import okhttp3.internal.http.StatusLine;
import org.bouncycastle.est.ESTClient;
import org.bouncycastle.est.ESTClientSourceProvider;
import org.bouncycastle.est.ESTException;
import org.bouncycastle.est.ESTRequest;
import org.bouncycastle.est.ESTRequestBuilder;
import org.bouncycastle.est.ESTResponse;
import org.jivesoftware.smack.util.StringUtils;

class DefaultESTClient implements ESTClient {
    private static byte[] CRLF = {13, 10};
    private static final Charset utf8 = Charset.forName(StringUtils.UTF8);
    private final ESTClientSourceProvider sslSocketProvider;

    private class PrintingOutputStream extends OutputStream {
        private final OutputStream tgt;

        public PrintingOutputStream(OutputStream outputStream) {
            this.tgt = outputStream;
        }

        public void write(int i) throws IOException {
            System.out.print(String.valueOf((char) i));
            this.tgt.write(i);
        }
    }

    public DefaultESTClient(ESTClientSourceProvider eSTClientSourceProvider) {
        this.sslSocketProvider = eSTClientSourceProvider;
    }

    private static void writeLine(OutputStream outputStream, String str) throws IOException {
        outputStream.write(str.getBytes());
        outputStream.write(CRLF);
    }

    public ESTResponse doRequest(ESTRequest eSTRequest) throws IOException {
        ESTResponse performRequest;
        int i = 15;
        while (true) {
            performRequest = performRequest(eSTRequest);
            ESTRequest redirectURL = redirectURL(performRequest);
            if (redirectURL == null) {
                break;
            }
            i--;
            if (i <= 0) {
                break;
            }
            eSTRequest = redirectURL;
        }
        if (i != 0) {
            return performRequest;
        }
        throw new ESTException("Too many redirects..");
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0066 A[Catch:{ all -> 0x0151 }] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x006f A[Catch:{ all -> 0x0151 }] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0087 A[Catch:{ all -> 0x0151 }] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00b8 A[Catch:{ all -> 0x0151 }] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00f3 A[Catch:{ all -> 0x0151 }] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x013b A[Catch:{ all -> 0x0151 }] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x014b A[SYNTHETIC, Splitter:B:41:0x014b] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.bouncycastle.est.ESTResponse performRequest(org.bouncycastle.est.ESTRequest r10) throws java.io.IOException {
        /*
            r9 = this;
            java.lang.String r0 = "Connection"
            r1 = 0
            org.bouncycastle.est.ESTClientSourceProvider r2 = r9.sslSocketProvider     // Catch:{ all -> 0x0151 }
            java.net.URL r3 = r10.getURL()     // Catch:{ all -> 0x0151 }
            java.lang.String r3 = r3.getHost()     // Catch:{ all -> 0x0151 }
            java.net.URL r4 = r10.getURL()     // Catch:{ all -> 0x0151 }
            int r4 = r4.getPort()     // Catch:{ all -> 0x0151 }
            org.bouncycastle.est.Source r1 = r2.makeSource(r3, r4)     // Catch:{ all -> 0x0151 }
            org.bouncycastle.est.ESTSourceConnectionListener r2 = r10.getListener()     // Catch:{ all -> 0x0151 }
            if (r2 == 0) goto L_0x0027
            org.bouncycastle.est.ESTSourceConnectionListener r2 = r10.getListener()     // Catch:{ all -> 0x0151 }
            org.bouncycastle.est.ESTRequest r10 = r2.onConnection(r1, r10)     // Catch:{ all -> 0x0151 }
        L_0x0027:
            java.lang.String r2 = "org.bouncycastle.debug.est"
            java.util.Set r2 = org.bouncycastle.util.Properties.asKeySet(r2)     // Catch:{ all -> 0x0151 }
            java.lang.String r3 = "output"
            boolean r3 = r2.contains(r3)     // Catch:{ all -> 0x0151 }
            if (r3 != 0) goto L_0x0043
            java.lang.String r3 = "all"
            boolean r2 = r2.contains(r3)     // Catch:{ all -> 0x0151 }
            if (r2 == 0) goto L_0x003e
            goto L_0x0043
        L_0x003e:
            java.io.OutputStream r2 = r1.getOutputStream()     // Catch:{ all -> 0x0151 }
            goto L_0x004c
        L_0x0043:
            org.bouncycastle.est.jcajce.DefaultESTClient$PrintingOutputStream r2 = new org.bouncycastle.est.jcajce.DefaultESTClient$PrintingOutputStream     // Catch:{ all -> 0x0151 }
            java.io.OutputStream r3 = r1.getOutputStream()     // Catch:{ all -> 0x0151 }
            r2.<init>(r3)     // Catch:{ all -> 0x0151 }
        L_0x004c:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0151 }
            r3.<init>()     // Catch:{ all -> 0x0151 }
            java.net.URL r4 = r10.getURL()     // Catch:{ all -> 0x0151 }
            java.lang.String r4 = r4.getPath()     // Catch:{ all -> 0x0151 }
            r3.append(r4)     // Catch:{ all -> 0x0151 }
            java.net.URL r4 = r10.getURL()     // Catch:{ all -> 0x0151 }
            java.lang.String r4 = r4.getQuery()     // Catch:{ all -> 0x0151 }
            if (r4 == 0) goto L_0x006f
            java.net.URL r4 = r10.getURL()     // Catch:{ all -> 0x0151 }
            java.lang.String r4 = r4.getQuery()     // Catch:{ all -> 0x0151 }
            goto L_0x0071
        L_0x006f:
            java.lang.String r4 = ""
        L_0x0071:
            r3.append(r4)     // Catch:{ all -> 0x0151 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0151 }
            org.bouncycastle.est.ESTRequestBuilder r4 = new org.bouncycastle.est.ESTRequestBuilder     // Catch:{ all -> 0x0151 }
            r4.<init>(r10)     // Catch:{ all -> 0x0151 }
            java.util.Map r5 = r10.getHeaders()     // Catch:{ all -> 0x0151 }
            boolean r5 = r5.containsKey(r0)     // Catch:{ all -> 0x0151 }
            if (r5 != 0) goto L_0x008c
            java.lang.String r5 = "close"
            r4.addHeader(r0, r5)     // Catch:{ all -> 0x0151 }
        L_0x008c:
            java.net.URL r10 = r10.getURL()     // Catch:{ all -> 0x0151 }
            int r0 = r10.getPort()     // Catch:{ all -> 0x0151 }
            r5 = -1
            r6 = 0
            java.lang.String r7 = "Host"
            if (r0 <= r5) goto L_0x00b8
            java.lang.String r0 = "%s:%d"
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x0151 }
            java.lang.String r8 = r10.getHost()     // Catch:{ all -> 0x0151 }
            r5[r6] = r8     // Catch:{ all -> 0x0151 }
            int r10 = r10.getPort()     // Catch:{ all -> 0x0151 }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ all -> 0x0151 }
            r8 = 1
            r5[r8] = r10     // Catch:{ all -> 0x0151 }
            java.lang.String r10 = java.lang.String.format(r0, r5)     // Catch:{ all -> 0x0151 }
        L_0x00b4:
            r4.setHeader(r7, r10)     // Catch:{ all -> 0x0151 }
            goto L_0x00bd
        L_0x00b8:
            java.lang.String r10 = r10.getHost()     // Catch:{ all -> 0x0151 }
            goto L_0x00b4
        L_0x00bd:
            org.bouncycastle.est.ESTRequest r10 = r4.build()     // Catch:{ all -> 0x0151 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0151 }
            r0.<init>()     // Catch:{ all -> 0x0151 }
            java.lang.String r4 = r10.getMethod()     // Catch:{ all -> 0x0151 }
            r0.append(r4)     // Catch:{ all -> 0x0151 }
            java.lang.String r4 = " "
            r0.append(r4)     // Catch:{ all -> 0x0151 }
            r0.append(r3)     // Catch:{ all -> 0x0151 }
            java.lang.String r3 = " HTTP/1.1"
            r0.append(r3)     // Catch:{ all -> 0x0151 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0151 }
            writeLine(r2, r0)     // Catch:{ all -> 0x0151 }
            java.util.Map r0 = r10.getHeaders()     // Catch:{ all -> 0x0151 }
            java.util.Set r0 = r0.entrySet()     // Catch:{ all -> 0x0151 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x0151 }
        L_0x00ed:
            boolean r3 = r0.hasNext()     // Catch:{ all -> 0x0151 }
            if (r3 == 0) goto L_0x0127
            java.lang.Object r3 = r0.next()     // Catch:{ all -> 0x0151 }
            java.util.Map$Entry r3 = (java.util.Map.Entry) r3     // Catch:{ all -> 0x0151 }
            java.lang.Object r4 = r3.getValue()     // Catch:{ all -> 0x0151 }
            java.lang.String[] r4 = (java.lang.String[]) r4     // Catch:{ all -> 0x0151 }
            java.lang.String[] r4 = (java.lang.String[]) r4     // Catch:{ all -> 0x0151 }
            r5 = r6
        L_0x0102:
            int r7 = r4.length     // Catch:{ all -> 0x0151 }
            if (r5 == r7) goto L_0x00ed
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0151 }
            r7.<init>()     // Catch:{ all -> 0x0151 }
            java.lang.Object r8 = r3.getKey()     // Catch:{ all -> 0x0151 }
            java.lang.String r8 = (java.lang.String) r8     // Catch:{ all -> 0x0151 }
            r7.append(r8)     // Catch:{ all -> 0x0151 }
            java.lang.String r8 = ": "
            r7.append(r8)     // Catch:{ all -> 0x0151 }
            r8 = r4[r5]     // Catch:{ all -> 0x0151 }
            r7.append(r8)     // Catch:{ all -> 0x0151 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0151 }
            writeLine(r2, r7)     // Catch:{ all -> 0x0151 }
            int r5 = r5 + 1
            goto L_0x0102
        L_0x0127:
            byte[] r0 = CRLF     // Catch:{ all -> 0x0151 }
            r2.write(r0)     // Catch:{ all -> 0x0151 }
            r2.flush()     // Catch:{ all -> 0x0151 }
            r10.writeData(r2)     // Catch:{ all -> 0x0151 }
            r2.flush()     // Catch:{ all -> 0x0151 }
            org.bouncycastle.est.ESTHijacker r0 = r10.getHijacker()     // Catch:{ all -> 0x0151 }
            if (r0 == 0) goto L_0x014b
            org.bouncycastle.est.ESTHijacker r0 = r10.getHijacker()     // Catch:{ all -> 0x0151 }
            org.bouncycastle.est.ESTResponse r10 = r0.hijack(r10, r1)     // Catch:{ all -> 0x0151 }
            if (r1 == 0) goto L_0x014a
            if (r10 != 0) goto L_0x014a
            r1.close()
        L_0x014a:
            return r10
        L_0x014b:
            org.bouncycastle.est.ESTResponse r0 = new org.bouncycastle.est.ESTResponse     // Catch:{ all -> 0x0151 }
            r0.<init>(r10, r1)     // Catch:{ all -> 0x0151 }
            return r0
        L_0x0151:
            r10 = move-exception
            if (r1 == 0) goto L_0x0157
            r1.close()
        L_0x0157:
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.est.jcajce.DefaultESTClient.performRequest(org.bouncycastle.est.ESTRequest):org.bouncycastle.est.ESTResponse");
    }

    /* access modifiers changed from: protected */
    public ESTRequest redirectURL(ESTResponse eSTResponse) throws IOException {
        ESTRequest eSTRequest;
        ESTRequestBuilder eSTRequestBuilder;
        if (eSTResponse.getStatusCode() < 300 || eSTResponse.getStatusCode() > 399) {
            eSTRequest = null;
        } else {
            switch (eSTResponse.getStatusCode()) {
                case 301:
                case 302:
                case 303:
                case 306:
                case StatusLine.HTTP_TEMP_REDIRECT /*307*/:
                    String header = eSTResponse.getHeader("Location");
                    if (!BuildConfig.FLAVOR.equals(header)) {
                        ESTRequestBuilder eSTRequestBuilder2 = new ESTRequestBuilder(eSTResponse.getOriginalRequest());
                        if (header.startsWith("http")) {
                            eSTRequestBuilder = eSTRequestBuilder2.withURL(new URL(header));
                        } else {
                            URL url = eSTResponse.getOriginalRequest().getURL();
                            eSTRequestBuilder = eSTRequestBuilder2.withURL(new URL(url.getProtocol(), url.getHost(), url.getPort(), header));
                        }
                        eSTRequest = eSTRequestBuilder.build();
                        break;
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Redirect status type: ");
                        sb.append(eSTResponse.getStatusCode());
                        sb.append(" but no location header");
                        throw new ESTException(sb.toString());
                    }
                default:
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Client does not handle http status code: ");
                    sb2.append(eSTResponse.getStatusCode());
                    throw new ESTException(sb2.toString());
            }
        }
        if (eSTRequest != null) {
            eSTResponse.close();
        }
        return eSTRequest;
    }
}
