package org.jivesoftware.smackx.muclight;

import java.util.HashMap;
import org.jxmpp.jid.Jid;

public class MUCLightRoomInfo {
    private final MUCLightRoomConfiguration configuration;
    private final HashMap<Jid, MUCLightAffiliation> occupants;
    private final Jid room;
    private final String version;

    public MUCLightRoomInfo(String version2, Jid roomJid, MUCLightRoomConfiguration configuration2, HashMap<Jid, MUCLightAffiliation> occupants2) {
        this.version = version2;
        this.room = roomJid;
        this.configuration = configuration2;
        this.occupants = occupants2;
    }

    public String getVersion() {
        return this.version;
    }

    public Jid getRoom() {
        return this.room;
    }

    public MUCLightRoomConfiguration getConfiguration() {
        return this.configuration;
    }

    public HashMap<Jid, MUCLightAffiliation> getOccupants() {
        return this.occupants;
    }
}
