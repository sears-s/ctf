package org.bouncycastle.mime;

import java.io.InputStream;
import org.bouncycastle.util.Strings;

public class BoundaryLimitedInputStream extends InputStream {
    private final byte[] boundary;
    private final byte[] buf;
    private int bufOff = 0;
    private boolean ended = false;
    private int index = 0;
    private int lastI;
    private final InputStream src;

    public BoundaryLimitedInputStream(InputStream inputStream, String str) {
        this.src = inputStream;
        this.boundary = Strings.toByteArray(str);
        this.buf = new byte[(str.length() + 3)];
        this.bufOff = 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0054  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0066  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00a8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int read() throws java.io.IOException {
        /*
            r8 = this;
            boolean r0 = r8.ended
            r1 = -1
            if (r0 == 0) goto L_0x0006
            return r1
        L_0x0006:
            int r0 = r8.index
            int r2 = r8.bufOff
            r3 = 0
            if (r0 >= r2) goto L_0x0021
            byte[] r4 = r8.buf
            int r5 = r0 + 1
            r8.index = r5
            byte r0 = r4[r0]
            r0 = r0 & 255(0xff, float:3.57E-43)
            int r4 = r8.index
            if (r4 >= r2) goto L_0x001c
            return r0
        L_0x001c:
            r8.bufOff = r3
            r8.index = r3
            goto L_0x0027
        L_0x0021:
            java.io.InputStream r0 = r8.src
            int r0 = r0.read()
        L_0x0027:
            r8.lastI = r0
            if (r0 >= 0) goto L_0x002c
            return r1
        L_0x002c:
            r2 = 13
            r4 = 10
            if (r0 == r2) goto L_0x0034
            if (r0 != r4) goto L_0x00b5
        L_0x0034:
            r8.index = r3
            if (r0 != r2) goto L_0x004a
            java.io.InputStream r2 = r8.src
            int r2 = r2.read()
            if (r2 != r4) goto L_0x0050
            byte[] r2 = r8.buf
            int r3 = r8.bufOff
            int r5 = r3 + 1
            r8.bufOff = r5
            r2[r3] = r4
        L_0x004a:
            java.io.InputStream r2 = r8.src
            int r2 = r2.read()
        L_0x0050:
            r3 = 45
            if (r2 != r3) goto L_0x0064
            byte[] r2 = r8.buf
            int r4 = r8.bufOff
            int r5 = r4 + 1
            r8.bufOff = r5
            r2[r4] = r3
            java.io.InputStream r2 = r8.src
            int r2 = r2.read()
        L_0x0064:
            if (r2 != r3) goto L_0x00a8
            byte[] r2 = r8.buf
            int r4 = r8.bufOff
            int r5 = r4 + 1
            r8.bufOff = r5
            r2[r4] = r3
            int r2 = r8.bufOff
        L_0x0072:
            int r3 = r8.bufOff
            int r3 = r3 - r2
            byte[] r4 = r8.boundary
            int r4 = r4.length
            r5 = 1
            if (r3 == r4) goto L_0x009d
            java.io.InputStream r3 = r8.src
            int r3 = r3.read()
            if (r3 < 0) goto L_0x009d
            byte[] r4 = r8.buf
            int r6 = r8.bufOff
            byte r3 = (byte) r3
            r4[r6] = r3
            byte r3 = r4[r6]
            byte[] r4 = r8.boundary
            int r7 = r6 - r2
            byte r4 = r4[r7]
            if (r3 == r4) goto L_0x0098
            int r6 = r6 + r5
            r8.bufOff = r6
            goto L_0x009d
        L_0x0098:
            int r6 = r6 + 1
            r8.bufOff = r6
            goto L_0x0072
        L_0x009d:
            int r3 = r8.bufOff
            int r3 = r3 - r2
            byte[] r2 = r8.boundary
            int r2 = r2.length
            if (r3 != r2) goto L_0x00b5
            r8.ended = r5
            return r1
        L_0x00a8:
            if (r2 < 0) goto L_0x00b5
            byte[] r1 = r8.buf
            int r3 = r8.bufOff
            int r4 = r3 + 1
            r8.bufOff = r4
            byte r2 = (byte) r2
            r1[r3] = r2
        L_0x00b5:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.mime.BoundaryLimitedInputStream.read():int");
    }
}
