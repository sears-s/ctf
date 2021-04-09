package org.jivesoftware.smackx.muclight.element;

import java.util.HashMap;
import java.util.List;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.muclight.MUCLightAffiliation;
import org.jivesoftware.smackx.muclight.MUCLightRoomConfiguration;
import org.jivesoftware.smackx.muclight.element.MUCLightElements.ConfigurationElement;
import org.jivesoftware.smackx.muclight.element.MUCLightElements.OccupantsElement;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.Jid;

public class MUCLightCreateIQ extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "urn:xmpp:muclight:0#create";
    private MUCLightRoomConfiguration configuration;
    private final HashMap<Jid, MUCLightAffiliation> occupants;

    public MUCLightCreateIQ(EntityJid room, String roomName, String subject, HashMap<String, String> customConfigs, List<Jid> occupants2) {
        super("query", NAMESPACE);
        this.configuration = new MUCLightRoomConfiguration(roomName, subject, customConfigs);
        this.occupants = new HashMap<>();
        for (Jid occupant : occupants2) {
            this.occupants.put(occupant, MUCLightAffiliation.member);
        }
        setType(Type.set);
        setTo((Jid) room);
    }

    public MUCLightCreateIQ(EntityJid room, String roomName, List<Jid> occupants2) {
        this(room, roomName, null, null, occupants2);
    }

    public MUCLightRoomConfiguration getConfiguration() {
        return this.configuration;
    }

    public HashMap<Jid, MUCLightAffiliation> getOccupants() {
        return this.occupants;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.element(new ConfigurationElement(this.configuration));
        if (!this.occupants.isEmpty()) {
            xml.element(new OccupantsElement(this.occupants));
        }
        return xml;
    }
}
