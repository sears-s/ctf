package org.jivesoftware.smackx.reference.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.reference.element.ReferenceElement;

public class ReferenceProvider extends ExtensionElementProvider<ReferenceElement> {
    public static final ReferenceProvider TEST_PROVIDER = new ReferenceProvider();

    /* JADX WARNING: Removed duplicated region for block: B:14:0x005d A[LOOP:0: B:4:0x002e->B:14:0x005d, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0050 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smackx.reference.element.ReferenceElement parse(org.xmlpull.v1.XmlPullParser r17, int r18) throws java.lang.Exception {
        /*
            r16 = this;
            r0 = r17
            java.lang.String r1 = "begin"
            java.lang.Integer r1 = org.jivesoftware.smack.util.ParserUtils.getIntegerAttribute(r0, r1)
            java.lang.String r2 = "end"
            java.lang.Integer r9 = org.jivesoftware.smack.util.ParserUtils.getIntegerAttribute(r0, r2)
            r2 = 0
            java.lang.String r3 = "type"
            java.lang.String r10 = r0.getAttributeValue(r2, r3)
            org.jivesoftware.smackx.reference.element.ReferenceElement$Type r11 = org.jivesoftware.smackx.reference.element.ReferenceElement.Type.valueOf(r10)
            java.lang.String r3 = "anchor"
            java.lang.String r12 = r0.getAttributeValue(r2, r3)
            java.lang.String r3 = "uri"
            java.lang.String r13 = r0.getAttributeValue(r2, r3)
            if (r13 == 0) goto L_0x002c
            java.net.URI r2 = new java.net.URI
            r2.<init>(r13)
        L_0x002c:
            r7 = r2
            r2 = 0
        L_0x002e:
            int r3 = r17.next()
            r4 = 2
            if (r3 != r4) goto L_0x004c
            java.lang.String r4 = r17.getName()
            java.lang.String r5 = r17.getNamespace()
            org.jivesoftware.smack.provider.ExtensionElementProvider r6 = org.jivesoftware.smack.provider.ProviderManager.getExtensionProvider(r4, r5)
            if (r6 == 0) goto L_0x004c
            org.jivesoftware.smack.packet.Element r8 = r6.parse(r0)
            r2 = r8
            org.jivesoftware.smack.packet.ExtensionElement r2 = (org.jivesoftware.smack.packet.ExtensionElement) r2
            r14 = r2
            goto L_0x004d
        L_0x004c:
            r14 = r2
        L_0x004d:
            r2 = 3
            if (r3 != r2) goto L_0x005d
            org.jivesoftware.smackx.reference.element.ReferenceElement r15 = new org.jivesoftware.smackx.reference.element.ReferenceElement
            r2 = r15
            r3 = r1
            r4 = r9
            r5 = r11
            r6 = r12
            r8 = r14
            r2.<init>(r3, r4, r5, r6, r7, r8)
            return r15
        L_0x005d:
            r2 = r14
            goto L_0x002e
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.reference.provider.ReferenceProvider.parse(org.xmlpull.v1.XmlPullParser, int):org.jivesoftware.smackx.reference.element.ReferenceElement");
    }
}
