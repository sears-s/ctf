package org.jivesoftware.smackx.disco.packet;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jxmpp.jid.Jid;

public class DiscoverItems extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "http://jabber.org/protocol/disco#items";
    private final List<Item> items = new LinkedList();
    private String node;

    public static class Item {
        public static final String REMOVE_ACTION = "remove";
        public static final String UPDATE_ACTION = "update";
        private String action;
        private final Jid entityID;
        private String name;
        private String node;

        public Item(Jid entityID2) {
            this.entityID = entityID2;
        }

        public Jid getEntityID() {
            return this.entityID;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name2) {
            this.name = name2;
        }

        public String getNode() {
            return this.node;
        }

        public void setNode(String node2) {
            this.node = node2;
        }

        public String getAction() {
            return this.action;
        }

        public void setAction(String action2) {
            this.action = action2;
        }

        public XmlStringBuilder toXML() {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement("item");
            xml.attribute("jid", (CharSequence) this.entityID);
            xml.optAttribute("name", this.name);
            xml.optAttribute(NodeElement.ELEMENT, this.node);
            xml.optAttribute("action", this.action);
            xml.closeEmptyElement();
            return xml;
        }
    }

    public DiscoverItems() {
        super("query", NAMESPACE);
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public void addItems(Collection<Item> itemsToAdd) {
        if (itemsToAdd != null) {
            for (Item i : itemsToAdd) {
                addItem(i);
            }
        }
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    public String getNode() {
        return this.node;
    }

    public void setNode(String node2) {
        this.node = node2;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.optAttribute(NodeElement.ELEMENT, getNode());
        xml.rightAngleBracket();
        for (Item item : this.items) {
            xml.append(item.toXML());
        }
        return xml;
    }
}
