package org.jivesoftware.smackx.iot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.jivesoftware.smackx.iot.control.ThingControlRequest;
import org.jivesoftware.smackx.iot.data.ThingMomentaryReadOutRequest;
import org.jivesoftware.smackx.iot.discovery.element.Tag;
import org.jivesoftware.smackx.iot.discovery.element.Tag.Type;
import org.jivesoftware.smackx.iot.element.NodeInfo;

public final class Thing {
    private final ThingControlRequest controlRequestHandler;
    private final HashMap<String, Tag> metaTags;
    private final ThingMomentaryReadOutRequest momentaryReadOutRequestHandler;
    private final NodeInfo nodeInfo;
    private final boolean selfOwned;
    private String toStringCache;

    public static class Builder {
        /* access modifiers changed from: private */
        public ThingControlRequest controlRequest;
        /* access modifiers changed from: private */
        public HashMap<String, Tag> metaTags = new HashMap<>();
        /* access modifiers changed from: private */
        public ThingMomentaryReadOutRequest momentaryReadOutRequest;
        /* access modifiers changed from: private */
        public NodeInfo nodeInfo = NodeInfo.EMPTY;
        /* access modifiers changed from: private */
        public boolean selfOwned;

        public Builder setSerialNumber(String sn) {
            String str = "SN";
            String str2 = "SN";
            this.metaTags.put(str2, new Tag(str2, Type.str, sn));
            return this;
        }

        public Builder setKey(String key) {
            String str = "KEY";
            String str2 = "KEY";
            this.metaTags.put(str2, new Tag(str2, Type.str, key));
            return this;
        }

        public Builder setManufacturer(String manufacturer) {
            String str = "MAN";
            String str2 = "MAN";
            this.metaTags.put(str2, new Tag(str2, Type.str, manufacturer));
            return this;
        }

        public Builder setModel(String model) {
            String str = "MODEL";
            String str2 = "MODEL";
            this.metaTags.put(str2, new Tag(str2, Type.str, model));
            return this;
        }

        public Builder setVersion(String version) {
            String str = "V";
            String str2 = "V";
            this.metaTags.put(str2, new Tag(str2, Type.num, version));
            return this;
        }

        public Builder setMomentaryReadOutRequestHandler(ThingMomentaryReadOutRequest momentaryReadOutRequestHandler) {
            this.momentaryReadOutRequest = momentaryReadOutRequestHandler;
            return this;
        }

        public Builder setControlRequestHandler(ThingControlRequest controlRequest2) {
            this.controlRequest = controlRequest2;
            return this;
        }

        public Thing build() {
            return new Thing(this);
        }
    }

    private Thing(Builder builder) {
        this.metaTags = builder.metaTags;
        this.selfOwned = builder.selfOwned;
        this.nodeInfo = builder.nodeInfo;
        this.momentaryReadOutRequestHandler = builder.momentaryReadOutRequest;
        this.controlRequestHandler = builder.controlRequest;
    }

    public Collection<Tag> getMetaTags() {
        return this.metaTags.values();
    }

    public boolean isSelfOwened() {
        return this.selfOwned;
    }

    public NodeInfo getNodeInfo() {
        return this.nodeInfo;
    }

    public String getNodeId() {
        return this.nodeInfo.getNodeId();
    }

    public String getSourceId() {
        return this.nodeInfo.getSourceId();
    }

    public String getCacheType() {
        return this.nodeInfo.getCacheType();
    }

    public ThingMomentaryReadOutRequest getMomentaryReadOutRequestHandler() {
        return this.momentaryReadOutRequestHandler;
    }

    public ThingControlRequest getControlRequestHandler() {
        return this.controlRequestHandler;
    }

    public String toString() {
        if (this.toStringCache == null) {
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Thing ");
            sb2.append(this.nodeInfo);
            sb2.append(" [");
            sb.append(sb2.toString());
            Iterator<Tag> it = this.metaTags.values().iterator();
            while (it.hasNext()) {
                sb.append((Tag) it.next());
                if (it.hasNext()) {
                    sb.append(' ');
                }
            }
            sb.append(']');
            this.toStringCache = sb.toString();
        }
        return this.toStringCache;
    }

    public static Builder builder() {
        return new Builder();
    }
}
