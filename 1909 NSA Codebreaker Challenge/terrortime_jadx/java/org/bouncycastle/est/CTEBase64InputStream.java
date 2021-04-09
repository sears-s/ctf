package org.bouncycastle.est;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class CTEBase64InputStream extends InputStream {
    protected final byte[] data = new byte[768];
    protected final OutputStream dataOutputStream;
    protected boolean end;
    protected final Long max;
    protected final byte[] rawBuf = new byte[1024];
    protected long read;
    protected int rp;
    protected final InputStream src;
    protected int wp;

    public CTEBase64InputStream(InputStream inputStream, Long l) {
        this.src = inputStream;
        this.dataOutputStream = new OutputStream() {
            public void write(int i) throws IOException {
                byte[] bArr = CTEBase64InputStream.this.data;
                CTEBase64InputStream cTEBase64InputStream = CTEBase64InputStream.this;
                int i2 = cTEBase64InputStream.wp;
                cTEBase64InputStream.wp = i2 + 1;
                bArr[i2] = (byte) i;
            }
        };
        this.max = l;
    }

    public void close() throws IOException {
        this.src.close();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0054 A[SYNTHETIC, Splitter:B:21:0x0054] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0074  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int pullFromSrc() throws java.io.IOException {
        /*
            r11 = this;
            long r0 = r11.read
            java.lang.Long r2 = r11.max
            long r2 = r2.longValue()
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            r1 = -1
            if (r0 < 0) goto L_0x000e
            return r1
        L_0x000e:
            r0 = 0
            r2 = r0
        L_0x0010:
            java.io.InputStream r3 = r11.src
            int r3 = r3.read()
            r4 = 33
            r5 = 10
            r6 = 1
            if (r3 >= r4) goto L_0x002d
            r4 = 13
            if (r3 == r4) goto L_0x002d
            if (r3 != r5) goto L_0x0025
            goto L_0x002d
        L_0x0025:
            if (r3 < 0) goto L_0x003d
            long r8 = r11.read
            long r8 = r8 + r6
            r11.read = r8
            goto L_0x003d
        L_0x002d:
            byte[] r4 = r11.rawBuf
            int r8 = r4.length
            if (r2 >= r8) goto L_0x007a
            int r8 = r2 + 1
            byte r9 = (byte) r3
            r4[r2] = r9
            long r9 = r11.read
            long r9 = r9 + r6
            r11.read = r9
            r2 = r8
        L_0x003d:
            if (r3 <= r1) goto L_0x0052
            byte[] r4 = r11.rawBuf
            int r4 = r4.length
            if (r2 >= r4) goto L_0x0052
            if (r3 == r5) goto L_0x0052
            long r4 = r11.read
            java.lang.Long r6 = r11.max
            long r6 = r6.longValue()
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 < 0) goto L_0x0010
        L_0x0052:
            if (r2 <= 0) goto L_0x0074
            byte[] r1 = r11.rawBuf     // Catch:{ Exception -> 0x005c }
            java.io.OutputStream r3 = r11.dataOutputStream     // Catch:{ Exception -> 0x005c }
            org.bouncycastle.util.encoders.Base64.decode(r1, r0, r2, r3)     // Catch:{ Exception -> 0x005c }
            goto L_0x0077
        L_0x005c:
            r0 = move-exception
            java.io.IOException r1 = new java.io.IOException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Decode Base64 Content-Transfer-Encoding: "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1.<init>(r0)
            throw r1
        L_0x0074:
            if (r3 != r1) goto L_0x0077
            return r1
        L_0x0077:
            int r0 = r11.wp
            return r0
        L_0x007a:
            java.io.IOException r0 = new java.io.IOException
            java.lang.String r1 = "Content Transfer Encoding, base64 line length > 1024"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.est.CTEBase64InputStream.pullFromSrc():int");
    }

    public int read() throws IOException {
        if (this.rp == this.wp) {
            this.rp = 0;
            this.wp = 0;
            int pullFromSrc = pullFromSrc();
            if (pullFromSrc == -1) {
                return pullFromSrc;
            }
        }
        byte[] bArr = this.data;
        int i = this.rp;
        this.rp = i + 1;
        return bArr[i] & 255;
    }
}
