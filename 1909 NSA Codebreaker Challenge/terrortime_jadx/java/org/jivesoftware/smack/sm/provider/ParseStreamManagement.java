package org.jivesoftware.smack.sm.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smack.packet.StanzaErrorTextElement;
import org.jivesoftware.smack.sm.packet.StreamManagement.AckAnswer;
import org.jivesoftware.smack.sm.packet.StreamManagement.AckRequest;
import org.jivesoftware.smack.sm.packet.StreamManagement.Enabled;
import org.jivesoftware.smack.sm.packet.StreamManagement.Failed;
import org.jivesoftware.smack.sm.packet.StreamManagement.Resume;
import org.jivesoftware.smack.sm.packet.StreamManagement.Resumed;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.xhtmlim.XHTMLText;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ParseStreamManagement {
    public static Enabled enabled(XmlPullParser parser) throws XmlPullParserException, IOException {
        ParserUtils.assertAtStartTag(parser);
        boolean resume = ParserUtils.getBooleanAttribute(parser, Resume.ELEMENT, false);
        String str = BuildConfig.FLAVOR;
        String id = parser.getAttributeValue(str, "id");
        String location = parser.getAttributeValue(str, "location");
        int max = ParserUtils.getIntegerAttribute(parser, "max", -1);
        parser.next();
        ParserUtils.assertAtEndTag(parser);
        return new Enabled(id, resume, location, max);
    }

    public static Failed failed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ParserUtils.assertAtStartTag(parser);
        Condition condition = null;
        List<StanzaErrorTextElement> textElements = new ArrayList<>(4);
        while (true) {
            int event = parser.next();
            if (event == 2) {
                String name = parser.getName();
                if ("urn:ietf:params:xml:ns:xmpp-stanzas".equals(parser.getNamespace())) {
                    if (name.equals("text")) {
                        textElements.add(new StanzaErrorTextElement(parser.nextText(), ParserUtils.getXmlLang(parser)));
                    } else {
                        condition = Condition.fromString(name);
                    }
                }
            } else if (event != 3) {
                continue;
            } else {
                if (Failed.ELEMENT.equals(parser.getName())) {
                    ParserUtils.assertAtEndTag(parser);
                    return new Failed(condition, textElements);
                }
            }
        }
    }

    public static Resumed resumed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ParserUtils.assertAtStartTag(parser);
        long h = ParserUtils.getLongAttribute(parser, XHTMLText.H).longValue();
        String previd = parser.getAttributeValue(BuildConfig.FLAVOR, "previd");
        parser.next();
        ParserUtils.assertAtEndTag(parser);
        return new Resumed(h, previd);
    }

    public static AckAnswer ackAnswer(XmlPullParser parser) throws XmlPullParserException, IOException {
        ParserUtils.assertAtStartTag(parser);
        long h = ParserUtils.getLongAttribute(parser, XHTMLText.H).longValue();
        parser.next();
        ParserUtils.assertAtEndTag(parser);
        return new AckAnswer(h);
    }

    public static AckRequest ackRequest(XmlPullParser parser) throws XmlPullParserException, IOException {
        ParserUtils.assertAtStartTag(parser);
        parser.next();
        ParserUtils.assertAtEndTag(parser);
        return AckRequest.INSTANCE;
    }
}
