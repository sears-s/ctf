package org.jivesoftware.smackx.muc;

import org.jxmpp.jid.EntityFullJid;

public interface SubjectUpdatedListener {
    void subjectUpdated(String str, EntityFullJid entityFullJid);
}
