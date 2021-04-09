package org.jivesoftware.smackx.jingle.element;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public final class JingleError implements ExtensionElement {
    public static String NAMESPACE = "urn:xmpp:jingle:errors:1";
    public static final JingleError OUT_OF_ORDER = new JingleError("out-of-order");
    public static final JingleError TIE_BREAK = new JingleError("tie-break");
    public static final JingleError UNKNOWN_SESSION = new JingleError("unknown-session");
    public static final JingleError UNSUPPORTED_INFO = new JingleError("unsupported-info");
    private final String errorName;

    private JingleError(String errorName2) {
        this.errorName = errorName2;
    }

    public String getMessage() {
        return this.errorName;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.closeEmptyElement();
        return xml;
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.jivesoftware.smackx.jingle.element.JingleError fromString(java.lang.String r4) {
        /*
            java.util.Locale r0 = java.util.Locale.US
            java.lang.String r4 = r4.toLowerCase(r0)
            int r0 = r4.hashCode()
            r1 = 3
            r2 = 2
            r3 = 1
            switch(r0) {
                case -1789601929: goto L_0x002f;
                case -1454954477: goto L_0x0025;
                case -73956990: goto L_0x001b;
                case 1052474694: goto L_0x0011;
                default: goto L_0x0010;
            }
        L_0x0010:
            goto L_0x0039
        L_0x0011:
            java.lang.String r0 = "unsupported-info"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0010
            r0 = r1
            goto L_0x003a
        L_0x001b:
            java.lang.String r0 = "tie-break"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0010
            r0 = r2
            goto L_0x003a
        L_0x0025:
            java.lang.String r0 = "unknown-session"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0010
            r0 = r3
            goto L_0x003a
        L_0x002f:
            java.lang.String r0 = "out-of-order"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0010
            r0 = 0
            goto L_0x003a
        L_0x0039:
            r0 = -1
        L_0x003a:
            if (r0 == 0) goto L_0x0051
            if (r0 == r3) goto L_0x004e
            if (r0 == r2) goto L_0x004b
            if (r0 != r1) goto L_0x0045
            org.jivesoftware.smackx.jingle.element.JingleError r0 = UNSUPPORTED_INFO
            return r0
        L_0x0045:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>()
            throw r0
        L_0x004b:
            org.jivesoftware.smackx.jingle.element.JingleError r0 = TIE_BREAK
            return r0
        L_0x004e:
            org.jivesoftware.smackx.jingle.element.JingleError r0 = UNKNOWN_SESSION
            return r0
        L_0x0051:
            org.jivesoftware.smackx.jingle.element.JingleError r0 = OUT_OF_ORDER
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.jingle.element.JingleError.fromString(java.lang.String):org.jivesoftware.smackx.jingle.element.JingleError");
    }

    public String toString() {
        return getMessage();
    }

    public String getElementName() {
        return this.errorName;
    }

    public String getNamespace() {
        return NAMESPACE;
    }
}
