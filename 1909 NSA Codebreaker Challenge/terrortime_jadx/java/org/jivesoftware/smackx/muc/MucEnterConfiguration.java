package org.jivesoftware.smackx.muc;

import java.util.Date;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smackx.muc.packet.MUCInitialPresence;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;

public final class MucEnterConfiguration {
    private final Presence joinPresence;
    private final int maxChars;
    private final int maxStanzas;
    private final Resourcepart nickname;
    private final String password;
    private final int seconds;
    private final Date since;
    private final long timeout;

    public static final class Builder {
        /* access modifiers changed from: private */
        public Presence joinPresence;
        /* access modifiers changed from: private */
        public int maxChars = -1;
        /* access modifiers changed from: private */
        public int maxStanzas = -1;
        /* access modifiers changed from: private */
        public final Resourcepart nickname;
        /* access modifiers changed from: private */
        public String password;
        /* access modifiers changed from: private */
        public int seconds = -1;
        /* access modifiers changed from: private */
        public Date since;
        /* access modifiers changed from: private */
        public long timeout;

        Builder(Resourcepart nickname2, long timeout2) {
            this.nickname = (Resourcepart) Objects.requireNonNull(nickname2, "Nickname must not be null");
            timeoutAfter(timeout2);
        }

        public Builder withPresence(Presence presence) {
            if (presence.getType() == Type.available) {
                this.joinPresence = presence;
                return this;
            }
            throw new IllegalArgumentException("Presence must be of type 'available'");
        }

        public Builder withPassword(String password2) {
            this.password = password2;
            return this;
        }

        public Builder timeoutAfter(long timeout2) {
            if (timeout2 > 0) {
                this.timeout = timeout2;
                return this;
            }
            throw new IllegalArgumentException("timeout must be positive");
        }

        public Builder requestNoHistory() {
            this.maxChars = 0;
            this.maxStanzas = -1;
            this.seconds = -1;
            this.since = null;
            return this;
        }

        public Builder requestMaxCharsHistory(int maxChars2) {
            this.maxChars = maxChars2;
            return this;
        }

        public Builder requestMaxStanzasHistory(int maxStanzas2) {
            this.maxStanzas = maxStanzas2;
            return this;
        }

        public Builder requestHistorySince(int seconds2) {
            this.seconds = seconds2;
            return this;
        }

        public Builder requestHistorySince(Date since2) {
            this.since = since2;
            return this;
        }

        public MucEnterConfiguration build() {
            return new MucEnterConfiguration(this);
        }
    }

    MucEnterConfiguration(Builder builder) {
        this.nickname = builder.nickname;
        this.password = builder.password;
        this.maxChars = builder.maxChars;
        this.maxStanzas = builder.maxStanzas;
        this.seconds = builder.seconds;
        this.since = builder.since;
        this.timeout = builder.timeout;
        if (builder.joinPresence == null) {
            this.joinPresence = new Presence(Type.available);
        } else {
            this.joinPresence = builder.joinPresence.clone();
        }
        Presence presence = this.joinPresence;
        MUCInitialPresence mUCInitialPresence = new MUCInitialPresence(this.password, this.maxChars, this.maxStanzas, this.seconds, this.since);
        presence.addExtension(mUCInitialPresence);
    }

    /* access modifiers changed from: 0000 */
    public Presence getJoinPresence(MultiUserChat multiUserChat) {
        this.joinPresence.setTo((Jid) JidCreate.fullFrom(multiUserChat.getRoom(), this.nickname));
        return this.joinPresence;
    }

    /* access modifiers changed from: 0000 */
    public long getTimeout() {
        return this.timeout;
    }
}
