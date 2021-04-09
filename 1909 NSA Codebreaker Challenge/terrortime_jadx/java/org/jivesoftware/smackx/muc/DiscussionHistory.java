package org.jivesoftware.smackx.muc;

import java.util.Date;
import org.jivesoftware.smackx.muc.packet.MUCInitialPresence.History;

@Deprecated
public class DiscussionHistory {
    private int maxChars = -1;
    private int maxStanzas = -1;
    private int seconds = -1;
    private Date since;

    public int getMaxChars() {
        return this.maxChars;
    }

    public int getMaxStanzas() {
        return this.maxStanzas;
    }

    public int getSeconds() {
        return this.seconds;
    }

    public Date getSince() {
        return this.since;
    }

    public void setMaxChars(int maxChars2) {
        this.maxChars = maxChars2;
    }

    public void setMaxStanzas(int maxStanzas2) {
        this.maxStanzas = maxStanzas2;
    }

    public void setSeconds(int seconds2) {
        this.seconds = seconds2;
    }

    public void setSince(Date since2) {
        this.since = since2;
    }

    private boolean isConfigured() {
        return this.maxChars > -1 || this.maxStanzas > -1 || this.seconds > -1 || this.since != null;
    }

    /* access modifiers changed from: 0000 */
    public History getMUCHistory() {
        if (!isConfigured()) {
            return null;
        }
        return new History(this.maxChars, this.maxStanzas, this.seconds, this.since);
    }
}
