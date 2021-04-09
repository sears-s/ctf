package org.jivesoftware.smackx.commands;

import org.jivesoftware.smackx.commands.packet.AdHocCommandData;
import org.jxmpp.jid.Jid;

public abstract class LocalCommand extends AdHocCommand {
    private final long creationDate = System.currentTimeMillis();
    private int currentStage = -1;
    private Jid ownerJID;
    private String sessionID;

    public abstract boolean hasPermission(Jid jid);

    public abstract boolean isLastStage();

    public void setSessionID(String sessionID2) {
        this.sessionID = sessionID2;
        getData().setSessionID(sessionID2);
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public void setOwnerJID(Jid ownerJID2) {
        this.ownerJID = ownerJID2;
    }

    public Jid getOwnerJID() {
        return this.ownerJID;
    }

    public long getCreationDate() {
        return this.creationDate;
    }

    public int getCurrentStage() {
        return this.currentStage;
    }

    /* access modifiers changed from: 0000 */
    public void setData(AdHocCommandData data) {
        data.setSessionID(this.sessionID);
        super.setData(data);
    }

    /* access modifiers changed from: 0000 */
    public void incrementStage() {
        this.currentStage++;
    }

    /* access modifiers changed from: 0000 */
    public void decrementStage() {
        this.currentStage--;
    }
}
