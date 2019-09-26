package org.jivesoftware.smackx.xdatalayout.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;
import org.jivesoftware.smackx.xdatalayout.packet.DataLayout;
import org.jivesoftware.smackx.xdatalayout.packet.DataLayout.Fieldref;
import org.jivesoftware.smackx.xdatalayout.packet.DataLayout.Section;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class DataLayoutProvider {
    public static DataLayout parse(XmlPullParser parser) throws XmlPullParserException, IOException {
        DataLayout dataLayout = new DataLayout(parser.getAttributeValue(BuildConfig.FLAVOR, AnnotatedPrivateKey.LABEL));
        parseLayout(dataLayout.getPageLayout(), parser);
        return dataLayout;
    }

    private static Section parseSection(XmlPullParser parser) throws XmlPullParserException, IOException {
        Section layout = new Section(parser.getAttributeValue(BuildConfig.FLAVOR, AnnotatedPrivateKey.LABEL));
        parseLayout(layout.getSectionLayout(), parser);
        return layout;
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void parseLayout(java.util.List<org.jivesoftware.smackx.xdatalayout.packet.DataLayout.DataFormLayoutElement> r8, org.xmlpull.v1.XmlPullParser r9) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            int r0 = r9.getDepth()
        L_0x0004:
            int r1 = r9.next()
            r2 = 3
            r3 = 2
            if (r1 == r3) goto L_0x0018
            if (r1 == r2) goto L_0x0010
            goto L_0x007f
        L_0x0010:
            int r2 = r9.getDepth()
            if (r2 != r0) goto L_0x007f
            return
        L_0x0018:
            java.lang.String r4 = r9.getName()
            r5 = -1
            int r6 = r4.hashCode()
            r7 = 1
            switch(r6) {
                case -928989863: goto L_0x0044;
                case -241484064: goto L_0x003a;
                case 3556653: goto L_0x0030;
                case 1970241253: goto L_0x0026;
                default: goto L_0x0025;
            }
        L_0x0025:
            goto L_0x004e
        L_0x0026:
            java.lang.String r6 = "section"
            boolean r4 = r4.equals(r6)
            if (r4 == 0) goto L_0x0025
            r4 = r7
            goto L_0x004f
        L_0x0030:
            java.lang.String r6 = "text"
            boolean r4 = r4.equals(r6)
            if (r4 == 0) goto L_0x0025
            r4 = 0
            goto L_0x004f
        L_0x003a:
            java.lang.String r6 = "reportedref"
            boolean r4 = r4.equals(r6)
            if (r4 == 0) goto L_0x0025
            r4 = r2
            goto L_0x004f
        L_0x0044:
            java.lang.String r6 = "fieldref"
            boolean r4 = r4.equals(r6)
            if (r4 == 0) goto L_0x0025
            r4 = r3
            goto L_0x004f
        L_0x004e:
            r4 = r5
        L_0x004f:
            if (r4 == 0) goto L_0x0071
            if (r4 == r7) goto L_0x0069
            if (r4 == r3) goto L_0x0061
            if (r4 == r2) goto L_0x0058
            goto L_0x007e
        L_0x0058:
            org.jivesoftware.smackx.xdatalayout.packet.DataLayout$Reportedref r2 = new org.jivesoftware.smackx.xdatalayout.packet.DataLayout$Reportedref
            r2.<init>()
            r8.add(r2)
            goto L_0x007e
        L_0x0061:
            org.jivesoftware.smackx.xdatalayout.packet.DataLayout$Fieldref r2 = parseFieldref(r9)
            r8.add(r2)
            goto L_0x007e
        L_0x0069:
            org.jivesoftware.smackx.xdatalayout.packet.DataLayout$Section r2 = parseSection(r9)
            r8.add(r2)
            goto L_0x007e
        L_0x0071:
            org.jivesoftware.smackx.xdatalayout.packet.DataLayout$Text r2 = new org.jivesoftware.smackx.xdatalayout.packet.DataLayout$Text
            java.lang.String r3 = r9.nextText()
            r2.<init>(r3)
            r8.add(r2)
        L_0x007e:
        L_0x007f:
            goto L_0x0004
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.xdatalayout.provider.DataLayoutProvider.parseLayout(java.util.List, org.xmlpull.v1.XmlPullParser):void");
    }

    private static Fieldref parseFieldref(XmlPullParser parser) throws XmlPullParserException, IOException {
        int initialDepth = parser.getDepth();
        Fieldref fieldref = new Fieldref(parser.getAttributeValue(BuildConfig.FLAVOR, "var"));
        while (true) {
            if (parser.next() == 3 && parser.getDepth() == initialDepth) {
                return fieldref;
            }
        }
    }
}
