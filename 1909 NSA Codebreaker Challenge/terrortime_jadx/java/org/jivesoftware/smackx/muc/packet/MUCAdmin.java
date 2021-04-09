package org.jivesoftware.smackx.muc.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;

public class MUCAdmin extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "http://jabber.org/protocol/muc#admin";
    private final List<MUCItem> items = new ArrayList();

    public MUCAdmin() {
        super("query", NAMESPACE);
    }

    public List<MUCItem> getItems() {
        List<MUCItem> unmodifiableList;
        synchronized (this.items) {
            unmodifiableList = Collections.unmodifiableList(new ArrayList(this.items));
        }
        return unmodifiableList;
    }

    public void addItem(MUCItem item) {
        synchronized (this.items) {
            this.items.add(item);
        }
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        synchronized (this.items) {
            for (MUCItem item : this.items) {
                xml.append(item.toXML((String) null));
            }
        }
        return xml;
    }
}
