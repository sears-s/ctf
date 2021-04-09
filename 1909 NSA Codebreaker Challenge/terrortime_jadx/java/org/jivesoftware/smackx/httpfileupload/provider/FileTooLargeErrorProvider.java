package org.jivesoftware.smackx.httpfileupload.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.httpfileupload.element.FileTooLargeError;

public class FileTooLargeErrorProvider extends ExtensionElementProvider<FileTooLargeError> {
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003a, code lost:
        if (r0.equals("urn:xmpp:http:upload:0") != false) goto L_0x003e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0040  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0052  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smackx.httpfileupload.element.FileTooLargeError parse(org.xmlpull.v1.XmlPullParser r9, int r10) throws java.lang.Exception {
        /*
            r8 = this;
            java.lang.String r0 = r9.getNamespace()
            r1 = 0
        L_0x0005:
            int r2 = r9.next()
            r3 = 2
            r4 = 0
            r5 = -1
            if (r2 == r3) goto L_0x005c
            r3 = 3
            if (r2 == r3) goto L_0x0013
            goto L_0x0080
        L_0x0013:
            int r3 = r9.getDepth()
            if (r3 != r10) goto L_0x0080
            int r2 = r0.hashCode()
            r3 = -1906675379(0xffffffff8e5a714d, float:-2.6925127E-30)
            r6 = 1
            if (r2 == r3) goto L_0x0034
            r3 = -1320418345(0xffffffffb14c03d7, float:-2.968809E-9)
            if (r2 == r3) goto L_0x002a
        L_0x0029:
            goto L_0x003d
        L_0x002a:
            java.lang.String r2 = "urn:xmpp:http:upload"
            boolean r2 = r0.equals(r2)
            if (r2 == 0) goto L_0x0029
            r4 = r6
            goto L_0x003e
        L_0x0034:
            java.lang.String r2 = "urn:xmpp:http:upload:0"
            boolean r2 = r0.equals(r2)
            if (r2 == 0) goto L_0x0029
            goto L_0x003e
        L_0x003d:
            r4 = r5
        L_0x003e:
            if (r4 == 0) goto L_0x0052
            if (r4 != r6) goto L_0x004c
            org.jivesoftware.smackx.httpfileupload.element.FileTooLargeError_V0_2 r2 = new org.jivesoftware.smackx.httpfileupload.element.FileTooLargeError_V0_2
            long r3 = r1.longValue()
            r2.<init>(r3)
            return r2
        L_0x004c:
            java.lang.AssertionError r2 = new java.lang.AssertionError
            r2.<init>()
            throw r2
        L_0x0052:
            org.jivesoftware.smackx.httpfileupload.element.FileTooLargeError r2 = new org.jivesoftware.smackx.httpfileupload.element.FileTooLargeError
            long r3 = r1.longValue()
            r2.<init>(r3)
            return r2
        L_0x005c:
            java.lang.String r3 = r9.getName()
            int r6 = r3.hashCode()
            r7 = -486525815(0xffffffffe3003489, float:-2.3649688E21)
            if (r6 == r7) goto L_0x006a
        L_0x0069:
            goto L_0x0073
        L_0x006a:
            java.lang.String r6 = "max-file-size"
            boolean r6 = r3.equals(r6)
            if (r6 == 0) goto L_0x0069
            goto L_0x0074
        L_0x0073:
            r4 = r5
        L_0x0074:
            if (r4 == 0) goto L_0x0077
            goto L_0x007f
        L_0x0077:
            java.lang.String r4 = r9.nextText()
            java.lang.Long r1 = java.lang.Long.valueOf(r4)
        L_0x007f:
        L_0x0080:
            goto L_0x0005
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.httpfileupload.provider.FileTooLargeErrorProvider.parse(org.xmlpull.v1.XmlPullParser, int):org.jivesoftware.smackx.httpfileupload.element.FileTooLargeError");
    }
}
