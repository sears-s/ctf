package org.jivesoftware.smackx.muclight.element;

import java.util.HashMap;
import java.util.Map.Entry;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smackx.muclight.element.MUCLightElements.BlockingElement;
import org.jxmpp.jid.Jid;

public class MUCLightBlockingIQ extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "urn:xmpp:muclight:0#blocking";
    private final HashMap<Jid, Boolean> rooms;
    private final HashMap<Jid, Boolean> users;

    public MUCLightBlockingIQ(HashMap<Jid, Boolean> rooms2, HashMap<Jid, Boolean> users2) {
        super("query", NAMESPACE);
        this.rooms = rooms2;
        this.users = users2;
    }

    public HashMap<Jid, Boolean> getRooms() {
        return this.rooms;
    }

    public HashMap<Jid, Boolean> getUsers() {
        return this.users;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        HashMap<Jid, Boolean> hashMap = this.rooms;
        if (hashMap != null) {
            parseBlocking(xml, hashMap, true);
        }
        HashMap<Jid, Boolean> hashMap2 = this.users;
        if (hashMap2 != null) {
            parseBlocking(xml, hashMap2, false);
        }
        return xml;
    }

    private void parseBlocking(IQChildElementXmlStringBuilder xml, HashMap<Jid, Boolean> map, boolean isRoom) {
        for (Entry<Jid, Boolean> pair : map.entrySet()) {
            xml.element(new BlockingElement((Jid) pair.getKey(), (Boolean) pair.getValue(), Boolean.valueOf(isRoom)));
        }
    }
}
