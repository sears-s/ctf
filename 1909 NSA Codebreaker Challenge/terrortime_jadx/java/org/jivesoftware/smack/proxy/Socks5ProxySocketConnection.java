package org.jivesoftware.smack.proxy;

import java.io.IOException;
import java.io.InputStream;
import org.jivesoftware.smack.proxy.ProxyInfo.ProxyType;

public class Socks5ProxySocketConnection implements ProxySocketConnection {
    private final ProxyInfo proxy;

    Socks5ProxySocketConnection(ProxyInfo proxy2) {
        this.proxy = proxy2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:44:0x015a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x015c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x015c A[ExcHandler: RuntimeException (e java.lang.RuntimeException), Splitter:B:4:0x0027] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:41:0x0150=Splitter:B:41:0x0150, B:35:0x012f=Splitter:B:35:0x012f} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void connect(java.net.Socket r19, java.lang.String r20, int r21, int r22) throws java.io.IOException {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            r3 = r21
            r4 = 0
            r5 = 0
            org.jivesoftware.smack.proxy.ProxyInfo r0 = r1.proxy
            java.lang.String r6 = r0.getProxyAddress()
            org.jivesoftware.smack.proxy.ProxyInfo r0 = r1.proxy
            int r7 = r0.getProxyPort()
            org.jivesoftware.smack.proxy.ProxyInfo r0 = r1.proxy
            java.lang.String r8 = r0.getProxyUsername()
            org.jivesoftware.smack.proxy.ProxyInfo r0 = r1.proxy
            java.lang.String r9 = r0.getProxyPassword()
            java.net.InetSocketAddress r0 = new java.net.InetSocketAddress     // Catch:{ RuntimeException -> 0x0171, Exception -> 0x015e }
            r0.<init>(r6, r7)     // Catch:{ RuntimeException -> 0x0171, Exception -> 0x015e }
            r10 = r22
            r2.connect(r0, r10)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            java.io.InputStream r0 = r19.getInputStream()     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r4 = r0
            java.io.OutputStream r0 = r19.getOutputStream()     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r5 = r0
            r11 = 1
            r2.setTcpNoDelay(r11)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r0 = 1024(0x400, float:1.435E-42)
            byte[] r0 = new byte[r0]     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r12 = r0
            r0 = 0
            int r13 = r0 + 1
            r14 = 5
            r12[r0] = r14     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            int r0 = r13 + 1
            r15 = 2
            r12[r13] = r15     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            int r13 = r0 + 1
            r14 = 0
            r12[r0] = r14     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            int r0 = r13 + 1
            r12[r13] = r15     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r5.write(r12, r14, r0)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            fill(r4, r12, r15)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r13 = 0
            byte r14 = r12[r11]     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r14 = r14 & 255(0xff, float:3.57E-43)
            java.lang.String r11 = "UTF-8"
            if (r14 == 0) goto L_0x00b5
            if (r14 == r15) goto L_0x0063
            goto L_0x00b3
        L_0x0063:
            if (r8 == 0) goto L_0x00b3
            if (r9 != 0) goto L_0x0068
            goto L_0x00b3
        L_0x0068:
            r0 = 0
            int r14 = r0 + 1
            r17 = 1
            r12[r0] = r17     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            int r0 = r14 + 1
            int r15 = r8.length()     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            byte r15 = (byte) r15     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r12[r14] = r15     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            byte[] r14 = r8.getBytes(r11)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            int r15 = r8.length()     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r1 = 0
            java.lang.System.arraycopy(r14, r1, r12, r0, r15)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            int r1 = r8.length()     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            int r0 = r0 + r1
            byte[] r1 = r9.getBytes(r11)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            int r15 = r0 + 1
            int r2 = r1.length     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            byte r2 = (byte) r2     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r12[r0] = r2     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            int r0 = r9.length()     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r2 = 0
            java.lang.System.arraycopy(r1, r2, r12, r15, r0)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            int r0 = r9.length()     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            int r0 = r0 + r15
            r5.write(r12, r2, r0)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r2 = 2
            fill(r4, r12, r2)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r2 = 1
            byte r15 = r12[r2]     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            if (r15 != 0) goto L_0x00b1
            r13 = 1
            r1 = r0
            goto L_0x00b7
        L_0x00b1:
            r1 = r0
            goto L_0x00b7
        L_0x00b3:
            r1 = r0
            goto L_0x00b7
        L_0x00b5:
            r13 = 1
            r1 = r0
        L_0x00b7:
            if (r13 == 0) goto L_0x014b
            r0 = 0
            int r1 = r0 + 1
            r2 = 5
            r12[r0] = r2     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            int r0 = r1 + 1
            r2 = 1
            r12[r1] = r2     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            int r1 = r0 + 1
            r2 = 0
            r12[r0] = r2     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r2 = r20
            byte[] r0 = r2.getBytes(r11)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r11 = r0
            int r0 = r11.length     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r14 = r0
            int r0 = r1 + 1
            r15 = 3
            r12[r1] = r15     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            int r1 = r0 + 1
            byte r15 = (byte) r14     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r12[r0] = r15     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r0 = 0
            java.lang.System.arraycopy(r11, r0, r12, r1, r14)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            int r1 = r1 + r14
            int r0 = r1 + 1
            int r15 = r3 >>> 8
            byte r15 = (byte) r15     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r12[r1] = r15     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            int r1 = r0 + 1
            r15 = r3 & 255(0xff, float:3.57E-43)
            byte r15 = (byte) r15     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r12[r0] = r15     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r0 = 0
            r5.write(r12, r0, r1)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r0 = 4
            fill(r4, r12, r0)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r15 = 1
            byte r17 = r12[r15]     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            if (r17 != 0) goto L_0x0128
            r0 = 3
            byte r15 = r12[r0]     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r15 = r15 & 255(0xff, float:3.57E-43)
            r16 = r1
            r1 = 1
            if (r15 == r1) goto L_0x0121
            if (r15 == r0) goto L_0x0112
            r0 = 4
            if (r15 == r0) goto L_0x010c
            goto L_0x0126
        L_0x010c:
            r0 = 18
            fill(r4, r12, r0)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            goto L_0x0126
        L_0x0112:
            r1 = 1
            fill(r4, r12, r1)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r0 = 0
            byte r0 = r12[r0]     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r0 = r0 & 255(0xff, float:3.57E-43)
            r1 = 2
            int r0 = r0 + r1
            fill(r4, r12, r0)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            goto L_0x0126
        L_0x0121:
            r0 = 6
            fill(r4, r12, r0)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
        L_0x0126:
            return
        L_0x0128:
            r16 = r1
            r19.close()     // Catch:{ Exception -> 0x012e, RuntimeException -> 0x015c }
            goto L_0x012f
        L_0x012e:
            r0 = move-exception
        L_0x012f:
            org.jivesoftware.smack.proxy.ProxyException r0 = new org.jivesoftware.smack.proxy.ProxyException     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            org.jivesoftware.smack.proxy.ProxyInfo$ProxyType r1 = org.jivesoftware.smack.proxy.ProxyInfo.ProxyType.SOCKS5     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r15.<init>()     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            java.lang.String r2 = "server returns "
            r15.append(r2)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r2 = 1
            byte r2 = r12[r2]     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r15.append(r2)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            java.lang.String r2 = r15.toString()     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            r0.<init>(r1, r2)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            throw r0     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
        L_0x014b:
            r19.close()     // Catch:{ Exception -> 0x014f, RuntimeException -> 0x015c }
            goto L_0x0150
        L_0x014f:
            r0 = move-exception
        L_0x0150:
            org.jivesoftware.smack.proxy.ProxyException r0 = new org.jivesoftware.smack.proxy.ProxyException     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            org.jivesoftware.smack.proxy.ProxyInfo$ProxyType r2 = org.jivesoftware.smack.proxy.ProxyInfo.ProxyType.SOCKS5     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            java.lang.String r11 = "fail in SOCKS5 proxy"
            r0.<init>(r2, r11)     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
            throw r0     // Catch:{ RuntimeException -> 0x015c, Exception -> 0x015a }
        L_0x015a:
            r0 = move-exception
            goto L_0x0161
        L_0x015c:
            r0 = move-exception
            goto L_0x0174
        L_0x015e:
            r0 = move-exception
            r10 = r22
        L_0x0161:
            r1 = r0
            r19.close()     // Catch:{ Exception -> 0x0166 }
            goto L_0x0167
        L_0x0166:
            r0 = move-exception
        L_0x0167:
            java.io.IOException r0 = new java.io.IOException
            java.lang.String r2 = r1.getLocalizedMessage()
            r0.<init>(r2)
            throw r0
        L_0x0171:
            r0 = move-exception
            r10 = r22
        L_0x0174:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.proxy.Socks5ProxySocketConnection.connect(java.net.Socket, java.lang.String, int, int):void");
    }

    private static void fill(InputStream in, byte[] buf, int len) throws IOException {
        int s = 0;
        while (s < len) {
            int i = in.read(buf, s, len - s);
            if (i > 0) {
                s += i;
            } else {
                throw new ProxyException(ProxyType.SOCKS5, "stream is closed");
            }
        }
    }
}
