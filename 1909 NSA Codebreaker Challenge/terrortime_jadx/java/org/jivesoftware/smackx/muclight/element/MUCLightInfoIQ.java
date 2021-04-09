package org.jivesoftware.smackx.muclight.element;

import java.util.HashMap;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smackx.muclight.MUCLightAffiliation;
import org.jivesoftware.smackx.muclight.MUCLightRoomConfiguration;
import org.jivesoftware.smackx.muclight.element.MUCLightElements.ConfigurationElement;
import org.jivesoftware.smackx.muclight.element.MUCLightElements.OccupantsElement;
import org.jxmpp.jid.Jid;

public class MUCLightInfoIQ extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "urn:xmpp:muclight:0#info";
    private final MUCLightRoomConfiguration configuration;
    private final HashMap<Jid, MUCLightAffiliation> occupants;
    private final String version;

    public MUCLightInfoIQ(String version2, MUCLightRoomConfiguration configuration2, HashMap<Jid, MUCLightAffiliation> occupants2) {
        super("query", "urn:xmpp:muclight:0#info");
        this.version = version2;
        this.configuration = configuration2;
        this.occupants = occupants2;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.optElement("version", this.version);
        xml.element(new ConfigurationElement(this.configuration));
        xml.element(new OccupantsElement(this.occupants));
        return xml;
    }

    public String getVersion() {
        return this.version;
    }

    public MUCLightRoomConfiguration getConfiguration() {
        return this.configuration;
    }

    public HashMap<Jid, MUCLightAffiliation> getOccupants() {
        return this.occupants;
    }
}
