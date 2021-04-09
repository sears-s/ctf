package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.encoders.Base64;

public class PEMUtil {
    private final String _footer1;
    private final String _footer2;
    private final String _header1;
    private final String _header2;

    PEMUtil(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN ");
        sb.append(str);
        String str2 = "-----";
        sb.append(str2);
        this._header1 = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("-----BEGIN X509 ");
        sb2.append(str);
        sb2.append(str2);
        this._header2 = sb2.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append("-----END ");
        sb3.append(str);
        sb3.append(str2);
        this._footer1 = sb3.toString();
        StringBuilder sb4 = new StringBuilder();
        sb4.append("-----END X509 ");
        sb4.append(str);
        sb4.append(str2);
        this._footer2 = sb4.toString();
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0025  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0027  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String readLine(java.io.InputStream r5) throws java.io.IOException {
        /*
            r4 = this;
            java.lang.StringBuffer r0 = new java.lang.StringBuffer
            r0.<init>()
        L_0x0005:
            int r1 = r5.read()
            r2 = 13
            if (r1 == r2) goto L_0x001b
            r3 = 10
            if (r1 == r3) goto L_0x001b
            if (r1 < 0) goto L_0x001b
            if (r1 != r2) goto L_0x0016
            goto L_0x0005
        L_0x0016:
            char r1 = (char) r1
            r0.append(r1)
            goto L_0x0005
        L_0x001b:
            if (r1 < 0) goto L_0x0023
            int r2 = r0.length()
            if (r2 == 0) goto L_0x0005
        L_0x0023:
            if (r1 >= 0) goto L_0x0027
            r5 = 0
            return r5
        L_0x0027:
            java.lang.String r5 = r0.toString()
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jce.provider.PEMUtil.readLine(java.io.InputStream):java.lang.String");
    }

    /* access modifiers changed from: 0000 */
    public ASN1Sequence readPEMObject(InputStream inputStream) throws IOException {
        String readLine;
        StringBuffer stringBuffer = new StringBuffer();
        do {
            readLine = readLine(inputStream);
            if (readLine == null || readLine.startsWith(this._header1)) {
            }
        } while (!readLine.startsWith(this._header2));
        while (true) {
            String readLine2 = readLine(inputStream);
            if (readLine2 != null && !readLine2.startsWith(this._footer1) && !readLine2.startsWith(this._footer2)) {
                stringBuffer.append(readLine2);
            }
        }
        if (stringBuffer.length() == 0) {
            return null;
        }
        ASN1Primitive readObject = new ASN1InputStream(Base64.decode(stringBuffer.toString())).readObject();
        if (readObject instanceof ASN1Sequence) {
            return (ASN1Sequence) readObject;
        }
        throw new IOException("malformed PEM data encountered");
    }
}
