package org.jivesoftware.smackx.hoxt.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp;
import org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp.Base64;
import org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp.ChunkedBase64;
import org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp.Ibb;
import org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp.Text;
import org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp.Xml;
import org.jivesoftware.smackx.shim.packet.HeadersExtension;
import org.jivesoftware.smackx.shim.provider.HeadersProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class AbstractHttpOverXmppProvider<H extends AbstractHttpOverXmpp> extends IQProvider<H> {
    private static final String ATTRIBUTE_SID = "sid";
    private static final String ATTRIBUTE_STREAM_ID = "streamId";
    static final String ATTRIBUTE_VERSION = "version";
    private static final String ELEMENT_BASE_64 = "base64";
    private static final String ELEMENT_CHUNKED_BASE_64 = "chunkedBase64";
    private static final String ELEMENT_DATA = "data";
    static final String ELEMENT_IBB = "ibb";
    static final String ELEMENT_JINGLE = "jingle";
    static final String ELEMENT_SIPUB = "sipub";
    private static final String ELEMENT_TEXT = "text";
    private static final String ELEMENT_XML = "xml";

    /* access modifiers changed from: protected */
    public HeadersExtension parseHeaders(XmlPullParser parser) throws Exception {
        if (parser.next() != 2 || !parser.getName().equals(HeadersExtension.ELEMENT)) {
            return null;
        }
        HeadersExtension headersExtension = (HeadersExtension) HeadersProvider.INSTANCE.parse(parser);
        parser.next();
        return headersExtension;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0044, code lost:
        if (r6.equals("xml") != false) goto L_0x0066;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp.Data parseData(org.xmlpull.v1.XmlPullParser r10) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r9 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            int r3 = r10.getEventType()
            r4 = 2
            if (r3 != r4) goto L_0x00c5
        L_0x000a:
            if (r1 != 0) goto L_0x00bf
            int r3 = r10.next()
            r5 = 3
            if (r3 != r4) goto L_0x00ae
            java.lang.String r6 = r10.getName()
            r7 = -1
            int r8 = r6.hashCode()
            switch(r8) {
                case -1396204209: goto L_0x005b;
                case -1159928143: goto L_0x0051;
                case 104041: goto L_0x0047;
                case 118807: goto L_0x003e;
                case 3556653: goto L_0x0034;
                case 109444327: goto L_0x002a;
                case 1970784315: goto L_0x0020;
                default: goto L_0x001f;
            }
        L_0x001f:
            goto L_0x0065
        L_0x0020:
            java.lang.String r5 = "chunkedBase64"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x001f
            r5 = r4
            goto L_0x0066
        L_0x002a:
            java.lang.String r5 = "sipub"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x001f
            r5 = 5
            goto L_0x0066
        L_0x0034:
            java.lang.String r5 = "text"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x001f
            r5 = 0
            goto L_0x0066
        L_0x003e:
            java.lang.String r8 = "xml"
            boolean r6 = r6.equals(r8)
            if (r6 == 0) goto L_0x001f
            goto L_0x0066
        L_0x0047:
            java.lang.String r5 = "ibb"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x001f
            r5 = 4
            goto L_0x0066
        L_0x0051:
            java.lang.String r5 = "jingle"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x001f
            r5 = 6
            goto L_0x0066
        L_0x005b:
            java.lang.String r5 = "base64"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x001f
            r5 = 1
            goto L_0x0066
        L_0x0065:
            r5 = r7
        L_0x0066:
            switch(r5) {
                case 0: goto L_0x00a8;
                case 1: goto L_0x00a3;
                case 2: goto L_0x009e;
                case 3: goto L_0x0099;
                case 4: goto L_0x0094;
                case 5: goto L_0x008c;
                case 6: goto L_0x0084;
                default: goto L_0x0069;
            }
        L_0x0069:
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "unsupported child tag: "
            r5.append(r6)
            java.lang.String r6 = r10.getName()
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r4.<init>(r5)
            throw r4
        L_0x0084:
            java.lang.UnsupportedOperationException r4 = new java.lang.UnsupportedOperationException
            java.lang.String r5 = "jingle is not supported yet"
            r4.<init>(r5)
            throw r4
        L_0x008c:
            java.lang.UnsupportedOperationException r4 = new java.lang.UnsupportedOperationException
            java.lang.String r5 = "sipub is not supported yet"
            r4.<init>(r5)
            throw r4
        L_0x0094:
            org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp$Ibb r0 = parseIbb(r10)
            goto L_0x00ad
        L_0x0099:
            org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp$Xml r0 = parseXml(r10)
            goto L_0x00ad
        L_0x009e:
            org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp$ChunkedBase64 r0 = parseChunkedBase64(r10)
            goto L_0x00ad
        L_0x00a3:
            org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp$Base64 r0 = parseBase64(r10)
            goto L_0x00ad
        L_0x00a8:
            org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp$Text r0 = parseText(r10)
        L_0x00ad:
            goto L_0x00bd
        L_0x00ae:
            if (r3 != r5) goto L_0x00bd
            java.lang.String r5 = r10.getName()
            java.lang.String r6 = "data"
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto L_0x00bd
            r1 = 1
        L_0x00bd:
            goto L_0x000a
        L_0x00bf:
            org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp$Data r3 = new org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp$Data
            r3.<init>(r0)
            r2 = r3
        L_0x00c5:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.hoxt.provider.AbstractHttpOverXmppProvider.parseData(org.xmlpull.v1.XmlPullParser):org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp$Data");
    }

    private static Text parseText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String text = null;
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == 3) {
                if (parser.getName().equals("text")) {
                    done = true;
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("unexpected end tag of: ");
                    sb.append(parser.getName());
                    throw new IllegalArgumentException(sb.toString());
                }
            } else if (eventType == 4) {
                text = parser.getText();
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("unexpected eventType: ");
                sb2.append(eventType);
                throw new IllegalArgumentException(sb2.toString());
            }
        }
        return new Text(text);
    }

    private static Xml parseXml(XmlPullParser parser) throws XmlPullParserException, IOException {
        StringBuilder builder = new StringBuilder();
        boolean done = false;
        boolean startClosed = true;
        while (!done) {
            int eventType = parser.next();
            if (eventType == 3 && parser.getName().equals("xml")) {
                done = true;
            } else if (eventType == 2) {
                if (!startClosed) {
                    builder.append('>');
                }
                builder.append('<');
                builder.append(parser.getName());
                appendXmlAttributes(parser, builder);
                startClosed = false;
            } else if (eventType == 3) {
                if (startClosed) {
                    builder.append("</");
                    builder.append(parser.getName());
                    builder.append('>');
                } else {
                    builder.append("/>");
                    startClosed = true;
                }
            } else if (eventType == 4) {
                if (!startClosed) {
                    builder.append('>');
                    startClosed = true;
                }
                builder.append(StringUtils.escapeForXmlText(parser.getText()));
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("unexpected eventType: ");
                sb.append(eventType);
                throw new IllegalArgumentException(sb.toString());
            }
        }
        return new Xml(builder.toString());
    }

    private static void appendXmlAttributes(XmlPullParser parser, StringBuilder builder) {
        int count = parser.getAttributeCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                builder.append(' ');
                builder.append(parser.getAttributeName(i));
                builder.append("=\"");
                builder.append(StringUtils.escapeForXml(parser.getAttributeValue(i)));
                builder.append('\"');
            }
        }
    }

    private static Base64 parseBase64(XmlPullParser parser) throws XmlPullParserException, IOException {
        String text = null;
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == 3) {
                if (parser.getName().equals("base64")) {
                    done = true;
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("unexpected end tag of: ");
                    sb.append(parser.getName());
                    throw new IllegalArgumentException(sb.toString());
                }
            } else if (eventType == 4) {
                text = parser.getText();
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("unexpected eventType: ");
                sb2.append(eventType);
                throw new IllegalArgumentException(sb2.toString());
            }
        }
        return new Base64(text);
    }

    private static ChunkedBase64 parseChunkedBase64(XmlPullParser parser) throws XmlPullParserException, IOException {
        ChunkedBase64 child = new ChunkedBase64(parser.getAttributeValue(BuildConfig.FLAVOR, "streamId"));
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType != 3) {
                StringBuilder sb = new StringBuilder();
                sb.append("unexpected event type: ");
                sb.append(eventType);
                throw new IllegalArgumentException(sb.toString());
            } else if (parser.getName().equals("chunkedBase64")) {
                done = true;
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("unexpected end tag: ");
                sb2.append(parser.getName());
                throw new IllegalArgumentException(sb2.toString());
            }
        }
        return child;
    }

    private static Ibb parseIbb(XmlPullParser parser) throws XmlPullParserException, IOException {
        Ibb child = new Ibb(parser.getAttributeValue(BuildConfig.FLAVOR, "sid"));
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType != 3) {
                StringBuilder sb = new StringBuilder();
                sb.append("unexpected event type: ");
                sb.append(eventType);
                throw new IllegalArgumentException(sb.toString());
            } else if (parser.getName().equals("ibb")) {
                done = true;
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("unexpected end tag: ");
                sb2.append(parser.getName());
                throw new IllegalArgumentException(sb2.toString());
            }
        }
        return child;
    }
}
