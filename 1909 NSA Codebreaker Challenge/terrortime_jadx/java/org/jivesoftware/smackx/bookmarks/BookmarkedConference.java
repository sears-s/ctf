package org.jivesoftware.smackx.bookmarks;

import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.parts.Resourcepart;

public class BookmarkedConference implements SharedBookmark {
    private boolean autoJoin;
    private boolean isShared;
    private final EntityBareJid jid;
    private String name;
    private Resourcepart nickname;
    private String password;

    protected BookmarkedConference(EntityBareJid jid2) {
        this.jid = jid2;
    }

    protected BookmarkedConference(String name2, EntityBareJid jid2, boolean autoJoin2, Resourcepart nickname2, String password2) {
        this.name = name2;
        this.jid = jid2;
        this.autoJoin = autoJoin2;
        this.nickname = nickname2;
        this.password = password2;
    }

    public String getName() {
        return this.name;
    }

    /* access modifiers changed from: protected */
    public void setName(String name2) {
        this.name = name2;
    }

    public boolean isAutoJoin() {
        return this.autoJoin;
    }

    /* access modifiers changed from: protected */
    public void setAutoJoin(boolean autoJoin2) {
        this.autoJoin = autoJoin2;
    }

    public EntityBareJid getJid() {
        return this.jid;
    }

    public Resourcepart getNickname() {
        return this.nickname;
    }

    /* access modifiers changed from: protected */
    public void setNickname(Resourcepart nickname2) {
        this.nickname = nickname2;
    }

    public String getPassword() {
        return this.password;
    }

    /* access modifiers changed from: protected */
    public void setPassword(String password2) {
        this.password = password2;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof BookmarkedConference)) {
            return false;
        }
        return ((BookmarkedConference) obj).getJid().equals((CharSequence) this.jid);
    }

    public int hashCode() {
        return getJid().hashCode();
    }

    /* access modifiers changed from: protected */
    public void setShared(boolean isShared2) {
        this.isShared = isShared2;
    }

    public boolean isShared() {
        return this.isShared;
    }
}
