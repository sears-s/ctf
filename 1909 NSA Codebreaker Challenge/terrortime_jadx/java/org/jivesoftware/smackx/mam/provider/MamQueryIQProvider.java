package org.jivesoftware.smackx.mam.provider;

import com.badguy.terrortime.BuildConfig;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.mam.element.MamQueryIQ;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;
import org.xmlpull.v1.XmlPullParser;

public class MamQueryIQProvider extends IQProvider<MamQueryIQ> {
    public MamQueryIQ parse(XmlPullParser parser, int initialDepth) throws Exception {
        DataForm dataForm = null;
        String str = BuildConfig.FLAVOR;
        String queryId = parser.getAttributeValue(str, "queryid");
        String node = parser.getAttributeValue(str, NodeElement.ELEMENT);
        while (true) {
            int eventType = parser.next();
            String name = parser.getName();
            if (eventType == 2) {
                char c = 65535;
                if (name.hashCode() == 120 && name.equals("x")) {
                    c = 0;
                }
                if (c == 0) {
                    dataForm = (DataForm) DataFormProvider.INSTANCE.parse(parser);
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return new MamQueryIQ(queryId, node, dataForm);
            }
        }
    }
}
