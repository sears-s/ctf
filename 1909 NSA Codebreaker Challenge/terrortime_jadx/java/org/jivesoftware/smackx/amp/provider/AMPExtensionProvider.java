package org.jivesoftware.smackx.amp.provider;

import java.io.IOException;
import java.util.logging.Logger;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.amp.AMPDeliverCondition;
import org.jivesoftware.smackx.amp.AMPDeliverCondition.Value;
import org.jivesoftware.smackx.amp.AMPExpireAtCondition;
import org.jivesoftware.smackx.amp.AMPMatchResourceCondition;
import org.jivesoftware.smackx.amp.packet.AMPExtension;
import org.jivesoftware.smackx.amp.packet.AMPExtension.Action;
import org.jivesoftware.smackx.amp.packet.AMPExtension.Condition;
import org.jivesoftware.smackx.amp.packet.AMPExtension.Rule;
import org.jivesoftware.smackx.amp.packet.AMPExtension.Status;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AMPExtensionProvider extends ExtensionElementProvider<AMPExtension> {
    private static final Logger LOGGER = Logger.getLogger(AMPExtensionProvider.class.getName());

    public AMPExtension parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser = parser;
        String str = null;
        String from = xmlPullParser.getAttributeValue(null, PrivacyItem.SUBSCRIPTION_FROM);
        String to = xmlPullParser.getAttributeValue(null, PrivacyItem.SUBSCRIPTION_TO);
        String statusString = xmlPullParser.getAttributeValue(null, "status");
        Status status = null;
        if (statusString != null) {
            try {
                status = Status.valueOf(statusString);
            } catch (IllegalArgumentException e) {
                IllegalArgumentException illegalArgumentException = e;
                Logger logger = LOGGER;
                StringBuilder sb = new StringBuilder();
                sb.append("Found invalid amp status ");
                sb.append(statusString);
                logger.severe(sb.toString());
            }
        }
        AMPExtension ampExtension = new AMPExtension(from, to, status);
        String perHopValue = xmlPullParser.getAttributeValue(null, "per-hop");
        if (perHopValue != null) {
            ampExtension.setPerHop(Boolean.parseBoolean(perHopValue));
        }
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == 2) {
                if (parser.getName().equals(Rule.ELEMENT)) {
                    String actionString = xmlPullParser.getAttributeValue(str, "action");
                    Condition condition = createCondition(xmlPullParser.getAttributeValue(str, Condition.ATTRIBUTE_NAME), xmlPullParser.getAttributeValue(str, "value"));
                    Action action = null;
                    if (actionString != null) {
                        try {
                            action = Action.valueOf(actionString);
                        } catch (IllegalArgumentException e2) {
                            IllegalArgumentException ex = e2;
                            Logger logger2 = LOGGER;
                            IllegalArgumentException illegalArgumentException2 = ex;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("Found invalid rule action value ");
                            sb2.append(actionString);
                            logger2.severe(sb2.toString());
                        }
                    }
                    if (action == null || condition == null) {
                        LOGGER.severe("Rule is skipped because either it's action or it's condition is invalid");
                    } else {
                        ampExtension.addRule(new Rule(action, condition));
                    }
                }
            } else if (eventType == 3 && parser.getName().equals(AMPExtension.ELEMENT)) {
                done = true;
            }
            xmlPullParser = parser;
            str = null;
        }
        return ampExtension;
    }

    private static Condition createCondition(String name, String value) {
        if (name == null || value == null) {
            LOGGER.severe("Can't create rule condition from null name and/or value");
            return null;
        } else if (AMPDeliverCondition.NAME.equals(name)) {
            try {
                return new AMPDeliverCondition(Value.valueOf(value));
            } catch (IllegalArgumentException e) {
                Logger logger = LOGGER;
                StringBuilder sb = new StringBuilder();
                sb.append("Found invalid rule delivery condition value ");
                sb.append(value);
                logger.severe(sb.toString());
                return null;
            }
        } else if (AMPExpireAtCondition.NAME.equals(name)) {
            return new AMPExpireAtCondition(value);
        } else {
            if (AMPMatchResourceCondition.NAME.equals(name)) {
                try {
                    return new AMPMatchResourceCondition(AMPMatchResourceCondition.Value.valueOf(value));
                } catch (IllegalArgumentException e2) {
                    Logger logger2 = LOGGER;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Found invalid rule match-resource condition value ");
                    sb2.append(value);
                    logger2.severe(sb2.toString());
                    return null;
                }
            } else {
                Logger logger3 = LOGGER;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Found unknown rule condition name ");
                sb3.append(name);
                logger3.severe(sb3.toString());
                return null;
            }
        }
    }
}
