package com.badguy.terrortime;

import java.util.function.Function;
import org.jivesoftware.smack.roster.RosterEntry;

/* renamed from: com.badguy.terrortime.-$$Lambda$ContactList$lug0BpjG90OFpXLjcQRVfOcwvaI reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ContactList$lug0BpjG90OFpXLjcQRVfOcwvaI implements Function {
    public static final /* synthetic */ $$Lambda$ContactList$lug0BpjG90OFpXLjcQRVfOcwvaI INSTANCE = new $$Lambda$ContactList$lug0BpjG90OFpXLjcQRVfOcwvaI();

    private /* synthetic */ $$Lambda$ContactList$lug0BpjG90OFpXLjcQRVfOcwvaI() {
    }

    public final Object apply(Object obj) {
        return ((RosterEntry) obj).getJid().getLocalpartOrNull();
    }
}
