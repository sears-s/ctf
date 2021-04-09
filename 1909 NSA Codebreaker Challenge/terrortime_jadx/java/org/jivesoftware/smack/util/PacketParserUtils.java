package org.jivesoftware.smack.util;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.compress.packet.Compress.Feature;
import org.jivesoftware.smack.packet.EmptyResultIQ;
import org.jivesoftware.smack.packet.ErrorIQ;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Mechanisms;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Subject;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Session;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.packet.StanzaError.Builder;
import org.jivesoftware.smack.packet.StartTls;
import org.jivesoftware.smack.packet.StreamError;
import org.jivesoftware.smack.packet.StreamError.Condition;
import org.jivesoftware.smack.packet.UnparsedIQ;
import org.jivesoftware.smack.parsing.StandardExtensionElementProvider;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.sasl.packet.SaslStreamElements.SASLFailure;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportCandidate;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jivesoftware.smackx.sid.element.StanzaIdElement;
import org.jxmpp.jid.Jid;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class PacketParserUtils {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final String FEATURE_XML_ROUNDTRIP = "http://xmlpull.org/v1/doc/features.html#xml-roundtrip";
    private static final Logger LOGGER = Logger.getLogger(PacketParserUtils.class.getName());
    private static final XmlPullParserFactory XML_PULL_PARSER_FACTORY;
    public static final boolean XML_PULL_PARSER_SUPPORTS_ROUNDTRIP;

    /* renamed from: org.jivesoftware.smack.util.PacketParserUtils$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$packet$IQ$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$packet$IQ$Type[Type.error.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$IQ$Type[Type.result.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    static {
        SmackConfiguration.getVersion();
        boolean roundtrip = false;
        try {
            XML_PULL_PARSER_FACTORY = XmlPullParserFactory.newInstance();
            try {
                XML_PULL_PARSER_FACTORY.newPullParser().setFeature(FEATURE_XML_ROUNDTRIP, true);
                roundtrip = true;
            } catch (XmlPullParserException e) {
                LOGGER.log(Level.FINEST, "XmlPullParser does not support XML_ROUNDTRIP", e);
            }
            XML_PULL_PARSER_SUPPORTS_ROUNDTRIP = roundtrip;
        } catch (XmlPullParserException e2) {
            throw new AssertionError(e2);
        }
    }

    public static XmlPullParser getParserFor(String stanza) throws XmlPullParserException, IOException {
        return getParserFor((Reader) new StringReader(stanza));
    }

    public static XmlPullParser getParserFor(Reader reader) throws XmlPullParserException, IOException {
        XmlPullParser parser = newXmppParser(reader);
        int event = parser.getEventType();
        while (event != 2) {
            if (event != 1) {
                event = parser.next();
            } else {
                throw new IllegalArgumentException("Document contains no start tag");
            }
        }
        return parser;
    }

    public static XmlPullParser getParserFor(String stanza, String startTag) throws XmlPullParserException, IOException {
        XmlPullParser parser = getParserFor(stanza);
        while (true) {
            int event = parser.getEventType();
            String name = parser.getName();
            if (event == 2 && name.equals(startTag)) {
                return parser;
            }
            if (event != 1) {
                parser.next();
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Could not find start tag '");
                sb.append(startTag);
                sb.append("' in stanza: ");
                sb.append(stanza);
                throw new IllegalArgumentException(sb.toString());
            }
        }
    }

    public static <S extends Stanza> S parseStanza(String stanza) throws Exception {
        return parseStanza(getParserFor(stanza));
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x003d  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0062  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.jivesoftware.smack.packet.Stanza parseStanza(org.xmlpull.v1.XmlPullParser r5) throws java.lang.Exception {
        /*
            org.jivesoftware.smack.util.ParserUtils.assertAtStartTag(r5)
            java.lang.String r0 = r5.getName()
            int r1 = r0.hashCode()
            r2 = -1276666629(0xffffffffb3e79cfb, float:-1.078533E-7)
            r3 = 2
            r4 = 1
            if (r1 == r2) goto L_0x0030
            r2 = 3368(0xd28, float:4.72E-42)
            if (r1 == r2) goto L_0x0026
            r2 = 954925063(0x38eb0007, float:1.1205678E-4)
            if (r1 == r2) goto L_0x001c
        L_0x001b:
            goto L_0x003a
        L_0x001c:
            java.lang.String r1 = "message"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x001b
            r1 = 0
            goto L_0x003b
        L_0x0026:
            java.lang.String r1 = "iq"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x001b
            r1 = r4
            goto L_0x003b
        L_0x0030:
            java.lang.String r1 = "presence"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x001b
            r1 = r3
            goto L_0x003b
        L_0x003a:
            r1 = -1
        L_0x003b:
            if (r1 == 0) goto L_0x0062
            if (r1 == r4) goto L_0x005d
            if (r1 != r3) goto L_0x0046
            org.jivesoftware.smack.packet.Presence r1 = parsePresence(r5)
            return r1
        L_0x0046:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Can only parse message, iq or presence, not "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        L_0x005d:
            org.jivesoftware.smack.packet.IQ r1 = parseIQ(r5)
            return r1
        L_0x0062:
            org.jivesoftware.smack.packet.Message r1 = parseMessage(r5)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.util.PacketParserUtils.parseStanza(org.xmlpull.v1.XmlPullParser):org.jivesoftware.smack.packet.Stanza");
    }

    public static XmlPullParser newXmppParser() throws XmlPullParserException {
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
        if (XML_PULL_PARSER_SUPPORTS_ROUNDTRIP) {
            try {
                parser.setFeature(FEATURE_XML_ROUNDTRIP, true);
            } catch (XmlPullParserException e) {
                LOGGER.log(Level.SEVERE, "XmlPullParser does not support XML_ROUNDTRIP, although it was first determined to be supported", e);
            }
        }
        return parser;
    }

    public static XmlPullParser newXmppParser(Reader reader) throws XmlPullParserException {
        XmlPullParser parser = newXmppParser();
        parser.setInput(reader);
        return parser;
    }

    public static Message parseMessage(XmlPullParser parser) throws Exception {
        ParserUtils.assertAtStartTag(parser);
        int initialDepth = parser.getDepth();
        Message message = new Message();
        String str = BuildConfig.FLAVOR;
        message.setStanzaId(parser.getAttributeValue(str, "id"));
        message.setTo(ParserUtils.getJidAttribute(parser, PrivacyItem.SUBSCRIPTION_TO));
        message.setFrom(ParserUtils.getJidAttribute(parser, PrivacyItem.SUBSCRIPTION_FROM));
        String typeString = parser.getAttributeValue(str, "type");
        if (typeString != null) {
            message.setType(Message.Type.fromString(typeString));
        }
        message.setLanguage(ParserUtils.getXmlLang(parser));
        String thread = null;
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String elementName = parser.getName();
                String namespace = parser.getNamespace();
                char c = 65535;
                int hashCode = elementName.hashCode();
                if (hashCode != -1867885268) {
                    if (hashCode != -874443254) {
                        if (hashCode == 96784904 && elementName.equals("error")) {
                            c = 2;
                        }
                    } else if (elementName.equals("thread")) {
                        c = 1;
                    }
                } else if (elementName.equals(Subject.ELEMENT)) {
                    c = 0;
                }
                if (c == 0) {
                    String xmlLangSubject = ParserUtils.getXmlLang(parser);
                    String subject = parseElementText(parser);
                    if (message.getSubject(xmlLangSubject) == null) {
                        message.addSubject(xmlLangSubject, subject);
                    }
                } else if (c != 1) {
                    if (c != 2) {
                        addExtensionElement((Stanza) message, parser, elementName, namespace);
                    } else {
                        message.setError(parseError(parser));
                    }
                } else if (thread == null) {
                    thread = parser.nextText();
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                message.setThread(thread);
                return message;
            }
        }
    }

    public static String parseElementText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String res;
        if (parser.isEmptyElementTag()) {
            res = BuildConfig.FLAVOR;
        } else {
            int event = parser.next();
            if (event == 4) {
                String res2 = parser.getText();
                if (parser.next() == 3) {
                    res = res2;
                } else {
                    throw new XmlPullParserException("Non-empty element tag contains child-elements, while Mixed Content (XML 3.2.2) is disallowed");
                }
            } else if (event == 3) {
                return BuildConfig.FLAVOR;
            } else {
                throw new XmlPullParserException("Non-empty element tag not followed by text, while Mixed Content (XML 3.2.2) is disallowed");
            }
        }
        return res;
    }

    public static CharSequence parseElement(XmlPullParser parser) throws XmlPullParserException, IOException {
        return parseElement(parser, false);
    }

    public static CharSequence parseElement(XmlPullParser parser, boolean fullNamespaces) throws XmlPullParserException, IOException {
        return parseContentDepth(parser, parser.getDepth(), fullNamespaces);
    }

    public static CharSequence parseContent(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.isEmptyElementTag()) {
            return BuildConfig.FLAVOR;
        }
        parser.next();
        return parseContentDepth(parser, parser.getDepth(), false);
    }

    public static CharSequence parseContentDepth(XmlPullParser parser, int depth) throws XmlPullParserException, IOException {
        return parseContentDepth(parser, depth, false);
    }

    public static CharSequence parseContentDepth(XmlPullParser parser, int depth, boolean fullNamespaces) throws XmlPullParserException, IOException {
        if (parser.getFeature(FEATURE_XML_ROUNDTRIP)) {
            return parseContentDepthWithRoundtrip(parser, depth, fullNamespaces);
        }
        return parseContentDepthWithoutRoundtrip(parser, depth, fullNamespaces);
    }

    private static CharSequence parseContentDepthWithoutRoundtrip(XmlPullParser parser, int depth, boolean fullNamespaces) throws XmlPullParserException, IOException {
        XmlStringBuilder xml = new XmlStringBuilder();
        int event = parser.getEventType();
        boolean isEmptyElement = false;
        String namespaceElement = null;
        while (true) {
            if (event == 2) {
                xml.halfOpenElement(parser.getName());
                if (namespaceElement == null || fullNamespaces) {
                    String namespace = parser.getNamespace();
                    if (StringUtils.isNotEmpty((CharSequence) namespace)) {
                        xml.attribute("xmlns", namespace);
                        namespaceElement = parser.getName();
                    }
                }
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    xml.attribute(parser.getAttributeName(i), parser.getAttributeValue(i));
                }
                if (parser.isEmptyElementTag() != 0) {
                    xml.closeEmptyElement();
                    isEmptyElement = true;
                } else {
                    xml.rightAngleBracket();
                }
            } else if (event == 3) {
                if (isEmptyElement) {
                    isEmptyElement = false;
                } else {
                    xml.closeElement(parser.getName());
                }
                if (namespaceElement != null && namespaceElement.equals(parser.getName())) {
                    namespaceElement = null;
                }
                if (parser.getDepth() <= depth) {
                    return xml;
                }
            } else if (event == 4) {
                xml.escape(parser.getText());
            }
            event = parser.next();
        }
    }

    private static CharSequence parseContentDepthWithRoundtrip(XmlPullParser parser, int depth, boolean fullNamespaces) throws XmlPullParserException, IOException {
        StringBuilder sb = new StringBuilder();
        int event = parser.getEventType();
        while (true) {
            if (event != 2 || !parser.isEmptyElementTag()) {
                CharSequence text = parser.getText();
                if (event == 4) {
                    text = StringUtils.escapeForXmlText(text);
                }
                sb.append(text);
            }
            if (event == 3 && parser.getDepth() <= depth) {
                return sb;
            }
            event = parser.next();
        }
    }

    public static Presence parsePresence(XmlPullParser parser) throws Exception {
        ParserUtils.assertAtStartTag(parser);
        int initialDepth = parser.getDepth();
        Presence.Type type = Presence.Type.available;
        String str = BuildConfig.FLAVOR;
        String typeString = parser.getAttributeValue(str, "type");
        if (typeString != null && !typeString.equals(str)) {
            type = Presence.Type.fromString(typeString);
        }
        Presence presence = new Presence(type);
        presence.setTo(ParserUtils.getJidAttribute(parser, PrivacyItem.SUBSCRIPTION_TO));
        presence.setFrom(ParserUtils.getJidAttribute(parser, PrivacyItem.SUBSCRIPTION_FROM));
        presence.setStanzaId(parser.getAttributeValue(str, "id"));
        String language = ParserUtils.getXmlLang(parser);
        if (language != null && !str.equals(language.trim())) {
            presence.setLanguage(language);
        }
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String elementName = parser.getName();
                String namespace = parser.getNamespace();
                char c = 65535;
                switch (elementName.hashCode()) {
                    case -1165461084:
                        if (elementName.equals(JingleS5BTransportCandidate.ATTR_PRIORITY)) {
                            c = 1;
                            break;
                        }
                        break;
                    case -892481550:
                        if (elementName.equals("status")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 3529469:
                        if (elementName.equals("show")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 96784904:
                        if (elementName.equals("error")) {
                            c = 3;
                            break;
                        }
                        break;
                }
                if (c == 0) {
                    presence.setStatus(parser.nextText());
                } else if (c == 1) {
                    presence.setPriority(Integer.parseInt(parser.nextText()));
                } else if (c == 2) {
                    String modeText = parser.nextText();
                    if (StringUtils.isNotEmpty((CharSequence) modeText)) {
                        presence.setMode(Mode.fromString(modeText));
                    } else {
                        Logger logger = LOGGER;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Empty or null mode text in presence show element form ");
                        sb.append(presence.getFrom());
                        sb.append(" with id '");
                        sb.append(presence.getStanzaId());
                        sb.append("' which is invalid according to RFC6121 4.7.2.1");
                        logger.warning(sb.toString());
                    }
                } else if (c != 3) {
                    try {
                        addExtensionElement((Stanza) presence, parser, elementName, namespace);
                    } catch (Exception e) {
                        Logger logger2 = LOGGER;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Failed to parse extension element in Presence stanza: \"");
                        sb2.append(e);
                        sb2.append("\" from: '");
                        sb2.append(presence.getFrom());
                        sb2.append(" id: '");
                        sb2.append(presence.getStanzaId());
                        sb2.append("'");
                        logger2.warning(sb2.toString());
                    }
                } else {
                    presence.setError(parseError(parser));
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return presence;
            }
        }
    }

    public static IQ parseIQ(XmlPullParser parser) throws Exception {
        ParserUtils.assertAtStartTag(parser);
        int initialDepth = parser.getDepth();
        IQ iqPacket = null;
        Builder error = null;
        String str = BuildConfig.FLAVOR;
        String id = parser.getAttributeValue(str, "id");
        Jid to = ParserUtils.getJidAttribute(parser, PrivacyItem.SUBSCRIPTION_TO);
        Jid from = ParserUtils.getJidAttribute(parser, PrivacyItem.SUBSCRIPTION_FROM);
        Type type = Type.fromString(parser.getAttributeValue(str, "type"));
        while (true) {
            int eventType = parser.next();
            if (eventType != 2) {
                if (eventType == 3 && parser.getDepth() == initialDepth) {
                    break;
                }
            } else {
                String elementName = parser.getName();
                String namespace = parser.getNamespace();
                char c = 65535;
                if (elementName.hashCode() == 96784904 && elementName.equals("error")) {
                    c = 0;
                }
                if (c != 0) {
                    IQProvider<IQ> provider = ProviderManager.getIQProvider(elementName, namespace);
                    if (provider != null) {
                        iqPacket = (IQ) provider.parse(parser);
                    } else {
                        iqPacket = new UnparsedIQ(elementName, namespace, parseElement(parser));
                    }
                } else {
                    error = parseError(parser);
                }
            }
        }
        if (iqPacket == null) {
            int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$packet$IQ$Type[type.ordinal()];
            if (i == 1) {
                iqPacket = new ErrorIQ(error);
            } else if (i == 2) {
                iqPacket = new EmptyResultIQ();
            }
        }
        iqPacket.setStanzaId(id);
        iqPacket.setTo(to);
        iqPacket.setFrom(from);
        iqPacket.setType(type);
        iqPacket.setError(error);
        return iqPacket;
    }

    public static Collection<String> parseMechanisms(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<String> mechanisms = new ArrayList<>();
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == 2) {
                if (parser.getName().equals("mechanism")) {
                    mechanisms.add(parser.nextText());
                }
            } else if (eventType == 3 && parser.getName().equals(Mechanisms.ELEMENT)) {
                done = true;
            }
        }
        return mechanisms;
    }

    public static Feature parseCompressionFeature(XmlPullParser parser) throws IOException, XmlPullParserException {
        int initialDepth = parser.getDepth();
        List<String> methods = new LinkedList<>();
        while (true) {
            int eventType = parser.next();
            boolean z = false;
            if (eventType == 2) {
                String name = parser.getName();
                if (name.hashCode() != -1077554975 || !name.equals("method")) {
                    z = true;
                }
                if (!z) {
                    methods.add(parser.nextText());
                }
            } else if (eventType != 3) {
                continue;
            } else {
                String name2 = parser.getName();
                if (name2.hashCode() != 1431984486 || !name2.equals(Feature.ELEMENT)) {
                    z = true;
                }
                if (!z && parser.getDepth() == initialDepth) {
                    return new Feature(methods);
                }
            }
        }
    }

    public static Map<String, String> parseDescriptiveTexts(XmlPullParser parser, Map<String, String> descriptiveTexts) throws XmlPullParserException, IOException {
        if (descriptiveTexts == null) {
            descriptiveTexts = new HashMap<>();
        }
        String xmllang = ParserUtils.getXmlLang(parser);
        if (xmllang == null) {
            xmllang = BuildConfig.FLAVOR;
        }
        String str = (String) descriptiveTexts.put(xmllang, parser.nextText());
        return descriptiveTexts;
    }

    public static SASLFailure parseSASLFailure(XmlPullParser parser) throws XmlPullParserException, IOException {
        int initialDepth = parser.getDepth();
        String condition = null;
        Map<String, String> descriptiveTexts = null;
        while (true) {
            int eventType = parser.next();
            if (eventType != 2) {
                if (eventType == 3 && parser.getDepth() == initialDepth) {
                    return new SASLFailure(condition, descriptiveTexts);
                }
            } else if (parser.getName().equals("text")) {
                descriptiveTexts = parseDescriptiveTexts(parser, descriptiveTexts);
            } else {
                condition = parser.getName();
            }
        }
    }

    public static StreamError parseStreamError(XmlPullParser parser) throws Exception {
        int initialDepth = parser.getDepth();
        List<ExtensionElement> extensions = new ArrayList<>();
        Map<String, String> descriptiveTexts = null;
        Condition condition = null;
        String conditionText = null;
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                String namespace = parser.getNamespace();
                boolean z = false;
                if ((namespace.hashCode() == 904188284 && namespace.equals(StreamError.NAMESPACE)) ? false : true) {
                    addExtensionElement((Collection<ExtensionElement>) extensions, parser, name, namespace);
                } else {
                    if (name.hashCode() != 3556653 || !name.equals("text")) {
                        z = true;
                    }
                    if (z) {
                        condition = Condition.fromString(name);
                        if (!parser.isEmptyElementTag()) {
                            conditionText = parser.nextText();
                        }
                    } else {
                        descriptiveTexts = parseDescriptiveTexts(parser, descriptiveTexts);
                    }
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return new StreamError(condition, conditionText, descriptiveTexts, extensions);
            }
        }
    }

    public static Builder parseError(XmlPullParser parser) throws Exception {
        int initialDepth = parser.getDepth();
        Map<String, String> descriptiveTexts = null;
        List<ExtensionElement> extensions = new ArrayList<>();
        Builder builder = StanzaError.getBuilder();
        String str = BuildConfig.FLAVOR;
        builder.setType(StanzaError.Type.fromString(parser.getAttributeValue(str, "type")));
        builder.setErrorGenerator(parser.getAttributeValue(str, StanzaIdElement.ATTR_BY));
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                String namespace = parser.getNamespace();
                boolean z = false;
                if ((namespace.hashCode() == 888780199 && namespace.equals("urn:ietf:params:xml:ns:xmpp-stanzas")) ? false : true) {
                    addExtensionElement((Collection<ExtensionElement>) extensions, parser, name, namespace);
                } else {
                    if (name.hashCode() != 3556653 || !name.equals("text")) {
                        z = true;
                    }
                    if (z) {
                        builder.setCondition(StanzaError.Condition.fromString(name));
                        if (!parser.isEmptyElementTag()) {
                            builder.setConditionText(parser.nextText());
                        }
                    } else {
                        descriptiveTexts = parseDescriptiveTexts(parser, descriptiveTexts);
                    }
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                ((Builder) builder.setExtensions(extensions)).setDescriptiveTexts(descriptiveTexts);
                return builder;
            }
        }
    }

    @Deprecated
    public static ExtensionElement parsePacketExtension(String elementName, String namespace, XmlPullParser parser) throws Exception {
        return parseExtensionElement(elementName, namespace, parser);
    }

    public static ExtensionElement parseExtensionElement(String elementName, String namespace, XmlPullParser parser) throws Exception {
        ParserUtils.assertAtStartTag(parser);
        ExtensionElementProvider<ExtensionElement> provider = ProviderManager.getExtensionProvider(elementName, namespace);
        if (provider != null) {
            return (ExtensionElement) provider.parse(parser);
        }
        return (ExtensionElement) StandardExtensionElementProvider.INSTANCE.parse(parser);
    }

    public static StartTls parseStartTlsFeature(XmlPullParser parser) throws XmlPullParserException, IOException {
        int initalDepth = parser.getDepth();
        boolean required = false;
        while (true) {
            int event = parser.next();
            if (event == 2) {
                String name = parser.getName();
                char c = 65535;
                if (name.hashCode() == -393139297 && name.equals("required")) {
                    c = 0;
                }
                if (c == 0) {
                    required = true;
                }
            } else if (event == 3 && parser.getDepth() == initalDepth) {
                return new StartTls(required);
            }
        }
    }

    public static Session.Feature parseSessionFeature(XmlPullParser parser) throws XmlPullParserException, IOException {
        ParserUtils.assertAtStartTag(parser);
        int initialDepth = parser.getDepth();
        boolean optional = false;
        if (parser.isEmptyElementTag()) {
            return new Session.Feature(false);
        }
        while (true) {
            int event = parser.next();
            if (event == 2) {
                String name = parser.getName();
                char c = 65535;
                if (name.hashCode() == -79017120 && name.equals(Session.Feature.OPTIONAL_ELEMENT)) {
                    c = 0;
                }
                if (c == 0) {
                    optional = true;
                }
            } else if (event == 3 && parser.getDepth() == initialDepth) {
                return new Session.Feature(optional);
            }
        }
    }

    public static void addExtensionElement(Stanza packet, XmlPullParser parser) throws Exception {
        ParserUtils.assertAtStartTag(parser);
        addExtensionElement(packet, parser, parser.getName(), parser.getNamespace());
    }

    public static void addExtensionElement(Stanza packet, XmlPullParser parser, String elementName, String namespace) throws Exception {
        packet.addExtension(parseExtensionElement(elementName, namespace, parser));
    }

    public static void addExtensionElement(Collection<ExtensionElement> collection, XmlPullParser parser) throws Exception {
        addExtensionElement(collection, parser, parser.getName(), parser.getNamespace());
    }

    public static void addExtensionElement(Collection<ExtensionElement> collection, XmlPullParser parser, String elementName, String namespace) throws Exception {
        collection.add(parseExtensionElement(elementName, namespace, parser));
    }
}
