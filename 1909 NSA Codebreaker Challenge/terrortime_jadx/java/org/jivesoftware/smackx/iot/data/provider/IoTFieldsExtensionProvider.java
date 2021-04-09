package org.jivesoftware.smackx.iot.data.provider;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.iot.data.element.IoTDataField;
import org.jivesoftware.smackx.iot.data.element.IoTDataField.BooleanField;
import org.jivesoftware.smackx.iot.data.element.IoTDataField.IntField;
import org.jivesoftware.smackx.iot.data.element.IoTFieldsExtension;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.iot.data.element.TimestampElement;
import org.jivesoftware.smackx.iot.element.NodeInfo;
import org.jivesoftware.smackx.iot.parser.NodeInfoParser;
import org.jxmpp.util.XmppDateTime;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class IoTFieldsExtensionProvider extends ExtensionElementProvider<IoTFieldsExtension> {
    private static final Logger LOGGER = Logger.getLogger(IoTFieldsExtensionProvider.class.getName());

    public IoTFieldsExtension parse(XmlPullParser parser, int initialDepth) throws Exception {
        int seqNr = ParserUtils.getIntegerAttributeOrThrow(parser, "seqnr", "IoT data request <accepted/> without sequence number");
        boolean done = ParserUtils.getBooleanAttribute(parser, "done", false);
        List<NodeElement> nodes = new ArrayList<>();
        while (true) {
            int eventType = parser.next();
            String name = parser.getName();
            if (eventType == 2) {
                char c = 65535;
                if (name.hashCode() == 3386882 && name.equals(NodeElement.ELEMENT)) {
                    c = 0;
                }
                if (c == 0) {
                    nodes.add(parseNode(parser));
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return new IoTFieldsExtension(seqNr, done, nodes);
            }
        }
    }

    public NodeElement parseNode(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        int initialDepth = parser.getDepth();
        NodeInfo nodeInfo = NodeInfoParser.parse(parser);
        List<TimestampElement> timestampElements = new ArrayList<>();
        while (true) {
            int eventType = parser.next();
            String name = parser.getName();
            if (eventType == 2) {
                char c = 65535;
                if (name.hashCode() == 55126294 && name.equals(TimestampElement.ELEMENT)) {
                    c = 0;
                }
                if (c == 0) {
                    timestampElements.add(parseTimestampElement(parser));
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return new NodeElement(nodeInfo, timestampElements);
            }
        }
    }

    public TimestampElement parseTimestampElement(XmlPullParser parser) throws ParseException, XmlPullParserException, IOException {
        XmlPullParser xmlPullParser = parser;
        int initialDepth = parser.getDepth();
        String str = "value";
        Date date = XmppDateTime.parseDate(xmlPullParser.getAttributeValue(null, str));
        List<IoTDataField> fields = new ArrayList<>();
        while (true) {
            int eventType = parser.next();
            String name = parser.getName();
            if (eventType == 2) {
                IoTDataField field = null;
                String fieldName = xmlPullParser.getAttributeValue(null, "name");
                String fieldValue = xmlPullParser.getAttributeValue(null, str);
                char c = 65535;
                int hashCode = name.hashCode();
                if (hashCode != 104431) {
                    if (hashCode == 64711720 && name.equals("boolean")) {
                        c = 1;
                    }
                } else if (name.equals("int")) {
                    c = 0;
                }
                if (c == 0) {
                    field = new IntField(fieldName, Integer.parseInt(fieldValue));
                } else if (c != 1) {
                    Logger logger = LOGGER;
                    StringBuilder sb = new StringBuilder();
                    sb.append("IoT Data field type '");
                    sb.append(name);
                    sb.append("' not implement yet. Ignoring.");
                    logger.warning(sb.toString());
                } else {
                    field = new BooleanField(fieldName, Boolean.parseBoolean(fieldValue));
                }
                if (field != null) {
                    fields.add(field);
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return new TimestampElement(date, fields);
            }
        }
    }
}
