package org.jivesoftware.smack.proxy;

public class Socks4ProxySocketConnection implements ProxySocketConnection {
    private final ProxyInfo proxy;

    Socks4ProxySocketConnection(ProxyInfo proxy2) {
        this.proxy = proxy2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00f0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00f2, code lost:
        r0 = e;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00f2 A[ExcHandler: RuntimeException (e java.lang.RuntimeException), Splitter:B:4:0x0021] */
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
            java.net.InetSocketAddress r0 = new java.net.InetSocketAddress     // Catch:{ RuntimeException -> 0x0109, Exception -> 0x00f4 }
            r0.<init>(r6, r7)     // Catch:{ RuntimeException -> 0x0109, Exception -> 0x00f4 }
            r9 = r22
            r2.connect(r0, r9)     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            java.io.InputStream r0 = r19.getInputStream()     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r4 = r0
            java.io.OutputStream r0 = r19.getOutputStream()     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r5 = r0
            r10 = 1
            r2.setTcpNoDelay(r10)     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r0 = 1024(0x400, float:1.435E-42)
            byte[] r0 = new byte[r0]     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r11 = r0
            r0 = 0
            r0 = 0
            int r12 = r0 + 1
            r13 = 4
            r11[r0] = r13     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            int r0 = r12 + 1
            r11[r12] = r10     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            int r12 = r0 + 1
            int r13 = r3 >>> 8
            byte r13 = (byte) r13     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r11[r0] = r13     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            int r0 = r12 + 1
            r13 = r3 & 255(0xff, float:3.57E-43)
            byte r13 = (byte) r13     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r11[r12] = r13     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            java.net.InetAddress r12 = java.net.InetAddress.getByName(r6)     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            byte[] r13 = r12.getAddress()     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r14 = 0
            r15 = r14
        L_0x005a:
            int r10 = r13.length     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            if (r15 >= r10) goto L_0x0068
            int r10 = r0 + 1
            byte r16 = r13[r15]     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r11[r0] = r16     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            int r15 = r15 + 1
            r0 = r10
            r10 = 1
            goto L_0x005a
        L_0x0068:
            if (r8 == 0) goto L_0x007c
            java.lang.String r10 = "UTF-8"
            byte[] r10 = r8.getBytes(r10)     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            int r15 = r8.length()     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            java.lang.System.arraycopy(r10, r14, r11, r0, r15)     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            int r15 = r8.length()     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            int r0 = r0 + r15
        L_0x007c:
            int r10 = r0 + 1
            r11[r0] = r14     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r5.write(r11, r14, r10)     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r15 = 6
            r0 = 0
            r14 = r0
        L_0x0086:
            if (r14 >= r15) goto L_0x009e
            int r0 = r15 - r14
            int r0 = r4.read(r11, r14, r0)     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            if (r0 <= 0) goto L_0x0092
            int r14 = r14 + r0
            goto L_0x0086
        L_0x0092:
            r17 = r0
            org.jivesoftware.smack.proxy.ProxyException r0 = new org.jivesoftware.smack.proxy.ProxyException     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            org.jivesoftware.smack.proxy.ProxyInfo$ProxyType r1 = org.jivesoftware.smack.proxy.ProxyInfo.ProxyType.SOCKS4     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            java.lang.String r2 = "stream is closed"
            r0.<init>(r1, r2)     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            throw r0     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
        L_0x009e:
            r0 = 0
            byte r1 = r11[r0]     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            if (r1 != 0) goto L_0x00d4
            r1 = 1
            byte r0 = r11[r1]     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r1 = 90
            if (r0 != r1) goto L_0x00b3
            r0 = 2
            byte[] r1 = new byte[r0]     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r2 = 0
            r4.read(r1, r2, r0)     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            return
        L_0x00b3:
            r19.close()     // Catch:{ Exception -> 0x00b7, RuntimeException -> 0x00f2 }
            goto L_0x00b8
        L_0x00b7:
            r0 = move-exception
        L_0x00b8:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r0.<init>()     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            java.lang.String r1 = "ProxySOCKS4: server returns CD "
            r0.append(r1)     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r1 = 1
            byte r1 = r11[r1]     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r0.append(r1)     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            java.lang.String r0 = r0.toString()     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            org.jivesoftware.smack.proxy.ProxyException r1 = new org.jivesoftware.smack.proxy.ProxyException     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            org.jivesoftware.smack.proxy.ProxyInfo$ProxyType r2 = org.jivesoftware.smack.proxy.ProxyInfo.ProxyType.SOCKS4     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r1.<init>(r2, r0)     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            throw r1     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
        L_0x00d4:
            org.jivesoftware.smack.proxy.ProxyException r0 = new org.jivesoftware.smack.proxy.ProxyException     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            org.jivesoftware.smack.proxy.ProxyInfo$ProxyType r1 = org.jivesoftware.smack.proxy.ProxyInfo.ProxyType.SOCKS4     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r2.<init>()     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            java.lang.String r3 = "server returns VN "
            r2.append(r3)     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r3 = 0
            byte r3 = r11[r3]     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r2.append(r3)     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            java.lang.String r2 = r2.toString()     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            r0.<init>(r1, r2)     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
            throw r0     // Catch:{ RuntimeException -> 0x00f2, Exception -> 0x00f0 }
        L_0x00f0:
            r0 = move-exception
            goto L_0x00f7
        L_0x00f2:
            r0 = move-exception
            goto L_0x010c
        L_0x00f4:
            r0 = move-exception
            r9 = r22
        L_0x00f7:
            r1 = r0
            r19.close()     // Catch:{ Exception -> 0x00fc }
            goto L_0x00fd
        L_0x00fc:
            r0 = move-exception
        L_0x00fd:
            org.jivesoftware.smack.proxy.ProxyException r0 = new org.jivesoftware.smack.proxy.ProxyException
            org.jivesoftware.smack.proxy.ProxyInfo$ProxyType r2 = org.jivesoftware.smack.proxy.ProxyInfo.ProxyType.SOCKS4
            java.lang.String r3 = r1.toString()
            r0.<init>(r2, r3)
            throw r0
        L_0x0109:
            r0 = move-exception
            r9 = r22
        L_0x010c:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.proxy.Socks4ProxySocketConnection.connect(java.net.Socket, java.lang.String, int, int):void");
    }
}
