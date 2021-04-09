package org.jivesoftware.smackx.disco.provider;

import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;

public class DiscoverInfoProvider extends IQProvider<DiscoverInfo> {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    /* JADX WARNING: Removed duplicated region for block: B:18:0x005f  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0069  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smackx.disco.packet.DiscoverInfo parse(org.xmlpull.v1.XmlPullParser r18, int r19) throws java.lang.Exception {
        /*
            r17 = this;
            r0 = r18
            org.jivesoftware.smackx.disco.packet.DiscoverInfo r1 = new org.jivesoftware.smackx.disco.packet.DiscoverInfo
            r1.<init>()
            r2 = 0
            java.lang.String r3 = ""
            java.lang.String r4 = ""
            java.lang.String r5 = ""
            java.lang.String r6 = ""
            java.lang.String r7 = ""
            java.lang.String r8 = ""
            java.lang.String r9 = "node"
            java.lang.String r9 = r0.getAttributeValue(r8, r9)
            r1.setNode(r9)
        L_0x001d:
            if (r2 != 0) goto L_0x00c8
            int r9 = r18.next()
            r10 = 2
            java.lang.String r11 = "feature"
            java.lang.String r12 = "identity"
            if (r9 != r10) goto L_0x0091
            java.lang.String r10 = r18.getName()
            java.lang.String r13 = r18.getNamespace()
            java.lang.String r14 = "http://jabber.org/protocol/disco#info"
            boolean r14 = r13.equals(r14)
            if (r14 == 0) goto L_0x0089
            int r15 = r10.hashCode()
            r14 = -979207434(0xffffffffc5a27af6, float:-5199.37)
            r16 = r2
            r2 = 1
            if (r15 == r14) goto L_0x0054
            r11 = -135761730(0xfffffffff7e870be, float:-9.428903E33)
            if (r15 == r11) goto L_0x004c
        L_0x004b:
            goto L_0x005c
        L_0x004c:
            boolean r11 = r10.equals(r12)
            if (r11 == 0) goto L_0x004b
            r14 = 0
            goto L_0x005d
        L_0x0054:
            boolean r11 = r10.equals(r11)
            if (r11 == 0) goto L_0x004b
            r14 = r2
            goto L_0x005d
        L_0x005c:
            r14 = -1
        L_0x005d:
            if (r14 == 0) goto L_0x0069
            if (r14 == r2) goto L_0x0062
            goto L_0x0088
        L_0x0062:
            java.lang.String r2 = "var"
            java.lang.String r6 = r0.getAttributeValue(r8, r2)
            goto L_0x0088
        L_0x0069:
            java.lang.String r2 = "category"
            java.lang.String r3 = r0.getAttributeValue(r8, r2)
            java.lang.String r2 = "name"
            java.lang.String r4 = r0.getAttributeValue(r8, r2)
            java.lang.String r2 = "type"
            java.lang.String r5 = r0.getAttributeValue(r8, r2)
            java.lang.String r2 = "xml"
            java.lang.String r2 = r0.getNamespace(r2)
            java.lang.String r11 = "lang"
            java.lang.String r7 = r0.getAttributeValue(r2, r11)
        L_0x0088:
            goto L_0x008e
        L_0x0089:
            r16 = r2
            org.jivesoftware.smack.util.PacketParserUtils.addExtensionElement(r1, r0)
        L_0x008e:
            r2 = r16
            goto L_0x00c6
        L_0x0091:
            r16 = r2
            r2 = 3
            if (r9 != r2) goto L_0x00c4
            java.lang.String r2 = r18.getName()
            boolean r2 = r2.equals(r12)
            if (r2 == 0) goto L_0x00a8
            org.jivesoftware.smackx.disco.packet.DiscoverInfo$Identity r2 = new org.jivesoftware.smackx.disco.packet.DiscoverInfo$Identity
            r2.<init>(r3, r5, r4, r7)
            r1.addIdentity(r2)
        L_0x00a8:
            java.lang.String r2 = r18.getName()
            boolean r2 = r2.equals(r11)
            if (r2 == 0) goto L_0x00b6
            boolean r2 = r1.addFeature(r6)
        L_0x00b6:
            java.lang.String r2 = r18.getName()
            java.lang.String r10 = "query"
            boolean r2 = r2.equals(r10)
            if (r2 == 0) goto L_0x00c4
            r2 = 1
            goto L_0x00c6
        L_0x00c4:
            r2 = r16
        L_0x00c6:
            goto L_0x001d
        L_0x00c8:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.disco.provider.DiscoverInfoProvider.parse(org.xmlpull.v1.XmlPullParser, int):org.jivesoftware.smackx.disco.packet.DiscoverInfo");
    }
}
