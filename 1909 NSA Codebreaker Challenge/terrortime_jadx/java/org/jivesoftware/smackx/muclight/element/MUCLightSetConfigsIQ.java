package org.jivesoftware.smackx.muclight.element;

import java.util.HashMap;
import java.util.Map.Entry;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Message.Subject;
import org.jxmpp.jid.Jid;

public class MUCLightSetConfigsIQ extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "urn:xmpp:muclight:0#configuration";
    private HashMap<String, String> customConfigs;
    private String roomName;
    private String subject;

    public MUCLightSetConfigsIQ(Jid roomJid, String roomName2, String subject2, HashMap<String, String> customConfigs2) {
        super("query", "urn:xmpp:muclight:0#configuration");
        this.roomName = roomName2;
        this.subject = subject2;
        this.customConfigs = customConfigs2;
        setType(Type.set);
        setTo(roomJid);
    }

    public MUCLightSetConfigsIQ(Jid roomJid, String roomName2, HashMap<String, String> customConfigs2) {
        this(roomJid, roomName2, null, customConfigs2);
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.optElement("roomname", this.roomName);
        xml.optElement(Subject.ELEMENT, this.subject);
        HashMap<String, String> hashMap = this.customConfigs;
        if (hashMap != null) {
            for (Entry<String, String> pair : hashMap.entrySet()) {
                xml.element((String) pair.getKey(), (String) pair.getValue());
            }
        }
        return xml;
    }
}
