package org.jivesoftware.smackx.jingle;

import org.jxmpp.jid.FullJid;

public class FullJidAndSessionId {
    private final FullJid fullJid;
    private final String sessionId;

    public FullJidAndSessionId(FullJid fullJid2, String sessionId2) {
        this.fullJid = fullJid2;
        this.sessionId = sessionId2;
    }

    public FullJid getFullJid() {
        return this.fullJid;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public int hashCode() {
        return (this.fullJid.hashCode() * 31 * 31) + this.sessionId.hashCode();
    }

    public boolean equals(Object other) {
        boolean z = false;
        if (!(other instanceof FullJidAndSessionId)) {
            return false;
        }
        FullJidAndSessionId otherFullJidAndSessionId = (FullJidAndSessionId) other;
        if (this.fullJid.equals((CharSequence) otherFullJidAndSessionId.fullJid) && this.sessionId.equals(otherFullJidAndSessionId.sessionId)) {
            z = true;
        }
        return z;
    }
}
