package org.jivesoftware.smackx.muclight;

import java.util.HashMap;

public class MUCLightRoomConfiguration {
    private final HashMap<String, String> customConfigs;
    private final String roomName;
    private final String subject;

    public MUCLightRoomConfiguration(String roomName2, String subject2, HashMap<String, String> customConfigs2) {
        this.roomName = roomName2;
        this.subject = subject2;
        this.customConfigs = customConfigs2;
    }

    public String getRoomName() {
        return this.roomName;
    }

    public String getSubject() {
        return this.subject;
    }

    public HashMap<String, String> getCustomConfigs() {
        return this.customConfigs;
    }
}
