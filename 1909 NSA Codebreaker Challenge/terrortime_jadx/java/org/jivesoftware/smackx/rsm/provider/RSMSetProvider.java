package org.jivesoftware.smackx.rsm.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.rsm.packet.RSMSet;

public class RSMSetProvider extends ExtensionElementProvider<RSMSet> {
    public static final RSMSetProvider INSTANCE = new RSMSetProvider();

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x004e, code lost:
        if (r11.equals("first") != false) goto L_0x0084;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smackx.rsm.packet.RSMSet parse(org.xmlpull.v1.XmlPullParser r19, int r20) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r18 = this;
            r0 = 0
            r1 = 0
            r2 = -1
            r3 = -1
            r4 = 0
            r5 = -1
            r6 = 0
            r7 = -1
        L_0x0008:
            int r8 = r19.next()
            r9 = 3
            r10 = 2
            if (r8 == r10) goto L_0x0031
            if (r8 == r9) goto L_0x0013
            goto L_0x002d
        L_0x0013:
            int r9 = r19.getDepth()
            r15 = r20
            if (r9 != r15) goto L_0x002d
            org.jivesoftware.smackx.rsm.packet.RSMSet r17 = new org.jivesoftware.smackx.rsm.packet.RSMSet
            r8 = r17
            r9 = r0
            r10 = r1
            r11 = r2
            r12 = r3
            r13 = r4
            r14 = r5
            r15 = r6
            r16 = r7
            r8.<init>(r9, r10, r11, r12, r13, r14, r15, r16)
            return r17
        L_0x002d:
            r9 = r19
            goto L_0x00c0
        L_0x0031:
            java.lang.String r11 = r19.getName()
            int r12 = r11.hashCode()
            java.lang.String r13 = "index"
            r14 = -1
            switch(r12) {
                case -1392885889: goto L_0x0079;
                case 107876: goto L_0x006f;
                case 3314326: goto L_0x0065;
                case 92734940: goto L_0x005b;
                case 94851343: goto L_0x0051;
                case 97440432: goto L_0x0048;
                case 100346066: goto L_0x0040;
                default: goto L_0x003f;
            }
        L_0x003f:
            goto L_0x0083
        L_0x0040:
            boolean r9 = r11.equals(r13)
            if (r9 == 0) goto L_0x003f
            r9 = 4
            goto L_0x0084
        L_0x0048:
            java.lang.String r10 = "first"
            boolean r10 = r11.equals(r10)
            if (r10 == 0) goto L_0x003f
            goto L_0x0084
        L_0x0051:
            java.lang.String r9 = "count"
            boolean r9 = r11.equals(r9)
            if (r9 == 0) goto L_0x003f
            r9 = r10
            goto L_0x0084
        L_0x005b:
            java.lang.String r9 = "after"
            boolean r9 = r11.equals(r9)
            if (r9 == 0) goto L_0x003f
            r9 = 0
            goto L_0x0084
        L_0x0065:
            java.lang.String r9 = "last"
            boolean r9 = r11.equals(r9)
            if (r9 == 0) goto L_0x003f
            r9 = 5
            goto L_0x0084
        L_0x006f:
            java.lang.String r9 = "max"
            boolean r9 = r11.equals(r9)
            if (r9 == 0) goto L_0x003f
            r9 = 6
            goto L_0x0084
        L_0x0079:
            java.lang.String r9 = "before"
            boolean r9 = r11.equals(r9)
            if (r9 == 0) goto L_0x003f
            r9 = 1
            goto L_0x0084
        L_0x0083:
            r9 = r14
        L_0x0084:
            switch(r9) {
                case 0: goto L_0x00b8;
                case 1: goto L_0x00b1;
                case 2: goto L_0x00aa;
                case 3: goto L_0x009f;
                case 4: goto L_0x0098;
                case 5: goto L_0x0091;
                case 6: goto L_0x008a;
                default: goto L_0x0087;
            }
        L_0x0087:
            r9 = r19
            goto L_0x00bf
        L_0x008a:
            int r5 = org.jivesoftware.smack.util.ParserUtils.getIntegerFromNextText(r19)
            r9 = r19
            goto L_0x00bf
        L_0x0091:
            java.lang.String r4 = r19.nextText()
            r9 = r19
            goto L_0x00bf
        L_0x0098:
            int r3 = org.jivesoftware.smack.util.ParserUtils.getIntegerFromNextText(r19)
            r9 = r19
            goto L_0x00bf
        L_0x009f:
            r9 = r19
            int r7 = org.jivesoftware.smack.util.ParserUtils.getIntegerAttribute(r9, r13, r14)
            java.lang.String r6 = r19.nextText()
            goto L_0x00bf
        L_0x00aa:
            r9 = r19
            int r2 = org.jivesoftware.smack.util.ParserUtils.getIntegerFromNextText(r19)
            goto L_0x00bf
        L_0x00b1:
            r9 = r19
            java.lang.String r1 = r19.nextText()
            goto L_0x00bf
        L_0x00b8:
            r9 = r19
            java.lang.String r0 = r19.nextText()
        L_0x00bf:
        L_0x00c0:
            goto L_0x0008
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.rsm.provider.RSMSetProvider.parse(org.xmlpull.v1.XmlPullParser, int):org.jivesoftware.smackx.rsm.packet.RSMSet");
    }
}
