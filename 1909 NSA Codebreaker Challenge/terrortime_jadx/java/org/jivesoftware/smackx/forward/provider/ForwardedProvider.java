package org.jivesoftware.smackx.forward.provider;

import java.util.logging.Logger;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.delay.provider.DelayInformationProvider;
import org.jivesoftware.smackx.forward.packet.Forwarded;
import org.xmlpull.v1.XmlPullParser;

public class ForwardedProvider extends ExtensionElementProvider<Forwarded> {
    public static final ForwardedProvider INSTANCE = new ForwardedProvider();
    private static final Logger LOGGER = Logger.getLogger(ForwardedProvider.class.getName());

    public Forwarded parse(XmlPullParser parser, int initialDepth) throws Exception {
        DelayInformation di = null;
        Stanza packet = null;
        while (true) {
            int eventType = parser.next();
            if (eventType != 2) {
                if (eventType == 3 && parser.getDepth() == initialDepth) {
                    break;
                }
            } else {
                String name = parser.getName();
                String namespace = parser.getNamespace();
                char c = 65535;
                int hashCode = name.hashCode();
                if (hashCode != 95467907) {
                    if (hashCode == 954925063 && name.equals(Message.ELEMENT)) {
                        c = 1;
                    }
                } else if (name.equals(DelayInformation.ELEMENT)) {
                    c = 0;
                }
                if (c == 0) {
                    String str = DelayInformation.NAMESPACE;
                    if (str.equals(namespace)) {
                        di = DelayInformationProvider.INSTANCE.parse(parser, parser.getDepth());
                    } else {
                        Logger logger = LOGGER;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Namespace '");
                        sb.append(namespace);
                        sb.append("' does not match expected namespace '");
                        sb.append(str);
                        sb.append("'");
                        logger.warning(sb.toString());
                    }
                } else if (c != 1) {
                    Logger logger2 = LOGGER;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Unsupported forwarded packet type: ");
                    sb2.append(name);
                    logger2.warning(sb2.toString());
                } else {
                    packet = PacketParserUtils.parseMessage(parser);
                }
            }
        }
        if (packet != null) {
            return new Forwarded(di, packet);
        }
        throw new SmackException("forwarded extension must contain a packet");
    }
}
