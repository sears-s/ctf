package org.jivesoftware.smackx.jingle.provider;

import com.badguy.terrortime.BuildConfig;
import java.util.logging.Logger;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.parsing.StandardExtensionElementProvider;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.jivesoftware.smackx.jingle.element.Jingle.Builder;
import org.jivesoftware.smackx.jingle.element.JingleAction;
import org.jivesoftware.smackx.jingle.element.JingleContent;
import org.jivesoftware.smackx.jingle.element.JingleContent.Creator;
import org.jivesoftware.smackx.jingle.element.JingleContent.Senders;
import org.jivesoftware.smackx.jingle.element.JingleContentDescription;
import org.jivesoftware.smackx.jingle.element.JingleContentTransport;
import org.jivesoftware.smackx.jingle.element.JingleReason;
import org.jivesoftware.smackx.jingle.element.JingleReason.AlternativeSession;
import org.jivesoftware.smackx.jingle.element.JingleReason.Reason;
import org.jivesoftware.smackx.jingle.element.UnknownJingleContentDescription;
import org.jivesoftware.smackx.jingle.element.UnknownJingleContentTransport;
import org.xmlpull.v1.XmlPullParser;

public class JingleProvider extends IQProvider<Jingle> {
    private static final Logger LOGGER = Logger.getLogger(JingleProvider.class.getName());

    public Jingle parse(XmlPullParser parser, int initialDepth) throws Exception {
        JingleReason reason;
        Builder builder = Jingle.getBuilder();
        String str = BuildConfig.FLAVOR;
        String actionString = parser.getAttributeValue(str, "action");
        if (actionString != null) {
            builder.setAction(JingleAction.fromString(actionString));
        }
        builder.setInitiator(ParserUtils.getFullJidAttribute(parser, Jingle.INITIATOR_ATTRIBUTE_NAME));
        builder.setResponder(ParserUtils.getFullJidAttribute(parser, Jingle.RESPONDER_ATTRIBUTE_NAME));
        builder.setSessionId(parser.getAttributeValue(str, "sid"));
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String tagName = parser.getName();
                char c = 65535;
                int hashCode = tagName.hashCode();
                if (hashCode != -934964668) {
                    if (hashCode == 951530617 && tagName.equals(JingleContent.ELEMENT)) {
                        c = 0;
                    }
                } else if (tagName.equals(JingleReason.ELEMENT)) {
                    c = 1;
                }
                if (c == 0) {
                    builder.addJingleContent(parseJingleContent(parser, parser.getDepth()));
                } else if (c != 1) {
                    Logger logger = LOGGER;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Unknown Jingle element: ");
                    sb.append(tagName);
                    logger.severe(sb.toString());
                } else {
                    parser.next();
                    String reasonString = parser.getName();
                    if (reasonString.equals("alternative-session")) {
                        parser.next();
                        reason = new AlternativeSession(parser.nextText());
                    } else {
                        reason = new JingleReason(Reason.fromString(reasonString));
                    }
                    builder.setReason(reason);
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return builder.build();
            }
        }
    }

    public static JingleContent parseJingleContent(XmlPullParser parser, int initialDepth) throws Exception {
        JingleContentDescription description;
        JingleContentTransport transport;
        JingleContent.Builder builder = JingleContent.getBuilder();
        String str = BuildConfig.FLAVOR;
        builder.setCreator(Creator.valueOf(parser.getAttributeValue(str, "creator")));
        builder.setDisposition(parser.getAttributeValue(str, JingleContent.DISPOSITION_ATTRIBUTE_NAME));
        builder.setName(parser.getAttributeValue(str, "name"));
        String sendersString = parser.getAttributeValue(str, JingleContent.SENDERS_ATTRIBUTE_NAME);
        if (sendersString != null) {
            builder.setSenders(Senders.valueOf(sendersString));
        }
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String tagName = parser.getName();
                String namespace = parser.getNamespace();
                char c = 65535;
                int hashCode = tagName.hashCode();
                if (hashCode != -1724546052) {
                    if (hashCode == 1052964649 && tagName.equals("transport")) {
                        c = 1;
                    }
                } else if (tagName.equals(JingleContentDescription.ELEMENT)) {
                    c = 0;
                }
                if (c == 0) {
                    JingleContentDescriptionProvider<?> provider = JingleContentProviderManager.getJingleContentDescriptionProvider(namespace);
                    if (provider == null) {
                        description = new UnknownJingleContentDescription((StandardExtensionElement) StandardExtensionElementProvider.INSTANCE.parse(parser));
                    } else {
                        description = (JingleContentDescription) provider.parse(parser);
                    }
                    builder.setDescription(description);
                } else if (c != 1) {
                    Logger logger = LOGGER;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Unknown Jingle content element: ");
                    sb.append(tagName);
                    logger.severe(sb.toString());
                } else {
                    JingleContentTransportProvider<?> provider2 = JingleContentProviderManager.getJingleContentTransportProvider(namespace);
                    if (provider2 == null) {
                        transport = new UnknownJingleContentTransport((StandardExtensionElement) StandardExtensionElementProvider.INSTANCE.parse(parser));
                    } else {
                        transport = (JingleContentTransport) provider2.parse(parser);
                    }
                    builder.setTransport(transport);
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return builder.build();
            }
        }
    }
}
