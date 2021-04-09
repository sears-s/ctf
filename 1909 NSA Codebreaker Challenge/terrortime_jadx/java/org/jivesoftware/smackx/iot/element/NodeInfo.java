package org.jivesoftware.smackx.iot.element;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;

public final class NodeInfo {
    public static final NodeInfo EMPTY = new NodeInfo();
    private final String cacheType;
    private final String nodeId;
    private final String sourceId;

    private NodeInfo() {
        this.nodeId = null;
        this.sourceId = null;
        this.cacheType = null;
    }

    public NodeInfo(String nodeId2, String sourceId2, String cacheType2) {
        this.nodeId = (String) StringUtils.requireNotNullOrEmpty(nodeId2, "Node ID must not be null or empty");
        this.sourceId = sourceId2;
        this.cacheType = cacheType2;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public String getSourceId() {
        return this.sourceId;
    }

    public String getCacheType() {
        return this.cacheType;
    }

    public void appendTo(XmlStringBuilder xml) {
        String str = this.nodeId;
        if (str != null) {
            String str2 = "cacheType";
            xml.attribute("nodeId", str).optAttribute("sourceId", this.sourceId).optAttribute(str2, this.cacheType);
        }
    }

    public int hashCode() {
        int i = 0;
        if (this == EMPTY) {
            return 0;
        }
        int result = ((1 * 31) + this.nodeId.hashCode()) * 31;
        String str = this.sourceId;
        int result2 = (result + (str == null ? 0 : str.hashCode())) * 31;
        String str2 = this.cacheType;
        if (str2 != null) {
            i = str2.hashCode();
        }
        return result2 + i;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof NodeInfo)) {
            return false;
        }
        NodeInfo otherNodeInfo = (NodeInfo) other;
        if (this.nodeId.equals(otherNodeInfo.nodeId) && StringUtils.nullSafeCharSequenceEquals(this.sourceId, otherNodeInfo.sourceId) && StringUtils.nullSafeCharSequenceEquals(this.cacheType, otherNodeInfo.cacheType)) {
            return true;
        }
        return false;
    }
}
