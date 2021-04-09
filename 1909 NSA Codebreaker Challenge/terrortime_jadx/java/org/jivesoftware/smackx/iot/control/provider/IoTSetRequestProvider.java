package org.jivesoftware.smackx.iot.control.provider;

import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.iot.control.element.IoTSetRequest;
import org.jivesoftware.smackx.iot.control.element.SetBoolData;
import org.jivesoftware.smackx.iot.control.element.SetData;
import org.jivesoftware.smackx.iot.control.element.SetDoubleData;
import org.jivesoftware.smackx.iot.control.element.SetIntData;
import org.jivesoftware.smackx.iot.control.element.SetLongData;
import org.xmlpull.v1.XmlPullParser;

public class IoTSetRequestProvider extends IQProvider<IoTSetRequest> {
    public IoTSetRequest parse(XmlPullParser parser, int initialDepth) throws Exception {
        List<SetData> data = new ArrayList<>(4);
        while (true) {
            int eventType = parser.next();
            String name = parser.getName();
            if (eventType == 2) {
                char c = 65535;
                switch (name.hashCode()) {
                    case -1325958191:
                        if (name.equals("double")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 104431:
                        if (name.equals("int")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 3029738:
                        if (name.equals("bool")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 3327612:
                        if (name.equals("long")) {
                            c = 3;
                            break;
                        }
                        break;
                }
                String str = "value";
                String str2 = "name";
                if (c == 0) {
                    data.add(new SetBoolData(parser.getAttributeValue(null, str2), Boolean.parseBoolean(parser.getAttributeValue(null, str))));
                } else if (c == 1) {
                    data.add(new SetDoubleData(parser.getAttributeValue(null, str2), Double.parseDouble(parser.getAttributeValue(null, str))));
                } else if (c == 2) {
                    data.add(new SetIntData(parser.getAttributeValue(null, str2), Integer.parseInt(parser.getAttributeValue(null, str))));
                } else if (c == 3) {
                    data.add(new SetLongData(parser.getAttributeValue(null, str2), Long.parseLong(parser.getAttributeValue(null, str))));
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return new IoTSetRequest(data);
            }
        }
    }
}
