package org.jivesoftware.smackx.offline.packet;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class OfflineMessageRequest extends IQ {
    public static final String ELEMENT = "offline";
    public static final String NAMESPACE = "http://jabber.org/protocol/offline";
    private boolean fetch = false;
    private final List<Item> items = new ArrayList();
    private boolean purge = false;

    public static class Item {
        private String action;
        private String jid;
        private String node;

        public Item(String node2) {
            this.node = node2;
        }

        public String getNode() {
            return this.node;
        }

        public String getAction() {
            return this.action;
        }

        public void setAction(String action2) {
            this.action = action2;
        }

        public String getJid() {
            return this.jid;
        }

        public void setJid(String jid2) {
            this.jid = jid2;
        }

        public String toXML() {
            StringBuilder buf = new StringBuilder();
            buf.append("<item");
            if (getAction() != null) {
                buf.append(" action=\"");
                buf.append(getAction());
                buf.append('\"');
            }
            if (getJid() != null) {
                buf.append(" jid=\"");
                buf.append(getJid());
                buf.append('\"');
            }
            if (getNode() != null) {
                buf.append(" node=\"");
                buf.append(getNode());
                buf.append('\"');
            }
            buf.append("/>");
            return buf.toString();
        }
    }

    public static class Provider extends IQProvider<OfflineMessageRequest> {
        public OfflineMessageRequest parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
            OfflineMessageRequest request = new OfflineMessageRequest();
            boolean done = false;
            while (!done) {
                int eventType = parser.next();
                if (eventType == 2) {
                    if (parser.getName().equals("item")) {
                        request.addItem(parseItem(parser));
                    } else if (parser.getName().equals("purge")) {
                        request.setPurge(true);
                    } else if (parser.getName().equals("fetch")) {
                        request.setFetch(true);
                    }
                } else if (eventType == 3 && parser.getName().equals(OfflineMessageRequest.ELEMENT)) {
                    done = true;
                }
            }
            return request;
        }

        private static Item parseItem(XmlPullParser parser) throws XmlPullParserException, IOException {
            boolean done = false;
            String str = BuildConfig.FLAVOR;
            Item item = new Item(parser.getAttributeValue(str, NodeElement.ELEMENT));
            item.setAction(parser.getAttributeValue(str, "action"));
            item.setJid(parser.getAttributeValue(str, "jid"));
            while (!done) {
                if (parser.next() == 3 && parser.getName().equals("item")) {
                    done = true;
                }
            }
            return item;
        }
    }

    public OfflineMessageRequest() {
        super(ELEMENT, NAMESPACE);
    }

    public List<Item> getItems() {
        List<Item> unmodifiableList;
        synchronized (this.items) {
            unmodifiableList = Collections.unmodifiableList(new ArrayList(this.items));
        }
        return unmodifiableList;
    }

    public void addItem(Item item) {
        synchronized (this.items) {
            this.items.add(item);
        }
    }

    public boolean isPurge() {
        return this.purge;
    }

    public void setPurge(boolean purge2) {
        this.purge = purge2;
    }

    public boolean isFetch() {
        return this.fetch;
    }

    public void setFetch(boolean fetch2) {
        this.fetch = fetch2;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder buf) {
        buf.rightAngleBracket();
        synchronized (this.items) {
            for (Item item : this.items) {
                buf.append((CharSequence) item.toXML());
            }
        }
        if (this.purge) {
            buf.append((CharSequence) "<purge/>");
        }
        if (this.fetch) {
            buf.append((CharSequence) "<fetch/>");
        }
        return buf;
    }
}
