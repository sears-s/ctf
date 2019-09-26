package org.jivesoftware.smack.util;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import javax.xml.namespace.QName;
import org.jivesoftware.smack.SmackException;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppDateTime;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ParserUtils {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final String JID = "jid";

    public static void assertAtStartTag(XmlPullParser parser) throws XmlPullParserException {
    }

    public static void assertAtStartTag(XmlPullParser parser, String name) throws XmlPullParserException {
        assertAtStartTag(parser);
    }

    public static void assertAtEndTag(XmlPullParser parser) throws XmlPullParserException {
    }

    public static void forwardToEndTagOfDepth(XmlPullParser parser, int depth) throws XmlPullParserException, IOException {
        int event = parser.getEventType();
        while (true) {
            if (event != 3 || parser.getDepth() != depth) {
                event = parser.next();
            } else {
                return;
            }
        }
    }

    public static Jid getJidAttribute(XmlPullParser parser) throws XmppStringprepException {
        return getJidAttribute(parser, "jid");
    }

    public static Jid getJidAttribute(XmlPullParser parser, String name) throws XmppStringprepException {
        String jidString = parser.getAttributeValue(BuildConfig.FLAVOR, name);
        if (jidString == null) {
            return null;
        }
        return JidCreate.from(jidString);
    }

    public static EntityBareJid getBareJidAttribute(XmlPullParser parser) throws XmppStringprepException {
        return getBareJidAttribute(parser, "jid");
    }

    public static EntityBareJid getBareJidAttribute(XmlPullParser parser, String name) throws XmppStringprepException {
        String jidString = parser.getAttributeValue(BuildConfig.FLAVOR, name);
        if (jidString == null) {
            return null;
        }
        return JidCreate.entityBareFrom(jidString);
    }

    public static EntityFullJid getFullJidAttribute(XmlPullParser parser) throws XmppStringprepException {
        return getFullJidAttribute(parser, "jid");
    }

    public static EntityFullJid getFullJidAttribute(XmlPullParser parser, String name) throws XmppStringprepException {
        String jidString = parser.getAttributeValue(BuildConfig.FLAVOR, name);
        if (jidString == null) {
            return null;
        }
        return JidCreate.entityFullFrom(jidString);
    }

    public static EntityJid getEntityJidAttribute(XmlPullParser parser, String name) throws XmppStringprepException {
        String jidString = parser.getAttributeValue(BuildConfig.FLAVOR, name);
        if (jidString == null) {
            return null;
        }
        Jid jid = JidCreate.from(jidString);
        if (!jid.hasLocalpart()) {
            return null;
        }
        EntityFullJid fullJid = jid.asEntityFullJidIfPossible();
        if (fullJid != null) {
            return fullJid;
        }
        return jid.asEntityBareJidIfPossible();
    }

    public static Resourcepart getResourcepartAttribute(XmlPullParser parser, String name) throws XmppStringprepException {
        String resourcepartString = parser.getAttributeValue(BuildConfig.FLAVOR, name);
        if (resourcepartString == null) {
            return null;
        }
        return Resourcepart.from(resourcepartString);
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0046 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean parseXmlBoolean(java.lang.String r6) {
        /*
            int r0 = r6.hashCode()
            r1 = 48
            r2 = 0
            r3 = 3
            r4 = 2
            r5 = 1
            if (r0 == r1) goto L_0x0039
            r1 = 49
            if (r0 == r1) goto L_0x002f
            r1 = 3569038(0x36758e, float:5.001287E-39)
            if (r0 == r1) goto L_0x0025
            r1 = 97196323(0x5cb1923, float:1.9099262E-35)
            if (r0 == r1) goto L_0x001b
        L_0x001a:
            goto L_0x0043
        L_0x001b:
            java.lang.String r0 = "false"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x001a
            r0 = r4
            goto L_0x0044
        L_0x0025:
            java.lang.String r0 = "true"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x001a
            r0 = r2
            goto L_0x0044
        L_0x002f:
            java.lang.String r0 = "1"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x001a
            r0 = r5
            goto L_0x0044
        L_0x0039:
            java.lang.String r0 = "0"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x001a
            r0 = r3
            goto L_0x0044
        L_0x0043:
            r0 = -1
        L_0x0044:
            if (r0 == 0) goto L_0x0065
            if (r0 == r5) goto L_0x0065
            if (r0 == r4) goto L_0x0064
            if (r0 != r3) goto L_0x004d
            goto L_0x0064
        L_0x004d:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r6)
            java.lang.String r2 = " is not a valid boolean string"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x0064:
            return r2
        L_0x0065:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.util.ParserUtils.parseXmlBoolean(java.lang.String):boolean");
    }

    public static Boolean getBooleanAttribute(XmlPullParser parser, String name) {
        String valueString = parser.getAttributeValue(BuildConfig.FLAVOR, name);
        if (valueString == null) {
            return null;
        }
        return Boolean.valueOf(parseXmlBoolean(valueString.toLowerCase(Locale.US)));
    }

    public static boolean getBooleanAttribute(XmlPullParser parser, String name, boolean defaultValue) {
        Boolean bool = getBooleanAttribute(parser, name);
        if (bool == null) {
            return defaultValue;
        }
        return bool.booleanValue();
    }

    public static int getIntegerAttributeOrThrow(XmlPullParser parser, String name, String throwMessage) throws SmackException {
        Integer res = getIntegerAttribute(parser, name);
        if (res != null) {
            return res.intValue();
        }
        throw new SmackException(throwMessage);
    }

    public static Integer getIntegerAttribute(XmlPullParser parser, String name) {
        String valueString = parser.getAttributeValue(BuildConfig.FLAVOR, name);
        if (valueString == null) {
            return null;
        }
        return Integer.valueOf(valueString);
    }

    public static int getIntegerAttribute(XmlPullParser parser, String name, int defaultValue) {
        Integer integer = getIntegerAttribute(parser, name);
        if (integer == null) {
            return defaultValue;
        }
        return integer.intValue();
    }

    public static int getIntegerFromNextText(XmlPullParser parser) throws XmlPullParserException, IOException {
        return Integer.valueOf(parser.nextText()).intValue();
    }

    public static Long getLongAttribute(XmlPullParser parser, String name) {
        String valueString = parser.getAttributeValue(BuildConfig.FLAVOR, name);
        if (valueString == null) {
            return null;
        }
        return Long.valueOf(valueString);
    }

    public static long getLongAttribute(XmlPullParser parser, String name, long defaultValue) {
        Long l = getLongAttribute(parser, name);
        if (l == null) {
            return defaultValue;
        }
        return l.longValue();
    }

    public static double getDoubleFromNextText(XmlPullParser parser) throws XmlPullParserException, IOException {
        return Double.valueOf(parser.nextText()).doubleValue();
    }

    public static Double getDoubleAttribute(XmlPullParser parser, String name) {
        String valueString = parser.getAttributeValue(BuildConfig.FLAVOR, name);
        if (valueString == null) {
            return null;
        }
        return Double.valueOf(valueString);
    }

    public static double getDoubleAttribute(XmlPullParser parser, String name, long defaultValue) {
        Double d = getDoubleAttribute(parser, name);
        if (d == null) {
            return (double) defaultValue;
        }
        return d.doubleValue();
    }

    public static Date getDateFromNextText(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        return XmppDateTime.parseDate(parser.nextText());
    }

    public static URI getUriFromNextText(XmlPullParser parser) throws XmlPullParserException, IOException, URISyntaxException {
        return new URI(parser.nextText());
    }

    public static String getRequiredAttribute(XmlPullParser parser, String name) throws IOException {
        String value = parser.getAttributeValue(BuildConfig.FLAVOR, name);
        if (!StringUtils.isNullOrEmpty((CharSequence) value)) {
            return value;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Attribute ");
        sb.append(name);
        sb.append(" is null or empty (");
        sb.append(value);
        sb.append(')');
        throw new IOException(sb.toString());
    }

    public static String getRequiredNextText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String text = parser.nextText();
        if (!StringUtils.isNullOrEmpty((CharSequence) text)) {
            return text;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Next text is null or empty (");
        sb.append(text);
        sb.append(')');
        throw new IOException(sb.toString());
    }

    public static String getXmlLang(XmlPullParser parser) {
        return parser.getAttributeValue("http://www.w3.org/XML/1998/namespace", "lang");
    }

    public static QName getQName(XmlPullParser parser) {
        String elementName = parser.getName();
        String prefix = parser.getPrefix();
        if (prefix == null) {
            prefix = BuildConfig.FLAVOR;
        }
        return new QName(parser.getNamespace(), elementName, prefix);
    }
}
