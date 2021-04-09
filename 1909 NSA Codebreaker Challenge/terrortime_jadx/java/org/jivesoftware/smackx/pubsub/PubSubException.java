package org.jivesoftware.smackx.pubsub;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jxmpp.jid.BareJid;

public abstract class PubSubException extends SmackException {
    private static final long serialVersionUID = 1;
    private final String nodeId;

    public static class NotALeafNodeException extends PubSubException {
        private static final long serialVersionUID = 1;
        private final BareJid pubSubService;

        NotALeafNodeException(String nodeId, BareJid pubSubService2) {
            super(nodeId);
            this.pubSubService = pubSubService2;
        }

        public BareJid getPubSubService() {
            return this.pubSubService;
        }
    }

    public static class NotAPubSubNodeException extends PubSubException {
        private static final long serialVersionUID = 1;
        private final DiscoverInfo discoverInfo;

        NotAPubSubNodeException(String nodeId, DiscoverInfo discoverInfo2) {
            super(nodeId);
            this.discoverInfo = discoverInfo2;
        }

        public DiscoverInfo getDiscoverInfo() {
            return this.discoverInfo;
        }
    }

    protected PubSubException(String nodeId2) {
        this.nodeId = nodeId2;
    }

    public String getNodeId() {
        return this.nodeId;
    }
}
