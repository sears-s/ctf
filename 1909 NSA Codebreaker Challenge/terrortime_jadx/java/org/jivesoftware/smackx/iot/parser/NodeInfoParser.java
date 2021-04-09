package org.jivesoftware.smackx.iot.parser;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.iot.element.NodeInfo;
import org.xmlpull.v1.XmlPullParser;

public class NodeInfoParser {
    public static NodeInfo parse(XmlPullParser parser) {
        String nodeId = parser.getAttributeValue(null, "nodeId");
        String sourceId = parser.getAttributeValue(null, "sourceId");
        String cacheType = parser.getAttributeValue(null, "cacheType");
        if (!StringUtils.isNullOrEmpty((CharSequence) nodeId) || !StringUtils.isNullOrEmpty((CharSequence) sourceId) || !StringUtils.isNullOrEmpty((CharSequence) cacheType)) {
            return new NodeInfo(nodeId, sourceId, cacheType);
        }
        return NodeInfo.EMPTY;
    }
}
