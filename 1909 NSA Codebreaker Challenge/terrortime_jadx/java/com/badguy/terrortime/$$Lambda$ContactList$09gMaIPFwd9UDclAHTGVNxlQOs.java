package com.badguy.terrortime;

import java.util.function.Function;
import org.jivesoftware.smack.roster.RosterEntry;

/* renamed from: com.badguy.terrortime.-$$Lambda$ContactList$09gMaIPFwd-9UDclAHTGVNxlQOs reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ContactList$09gMaIPFwd9UDclAHTGVNxlQOs implements Function {
    public static final /* synthetic */ $$Lambda$ContactList$09gMaIPFwd9UDclAHTGVNxlQOs INSTANCE = new $$Lambda$ContactList$09gMaIPFwd9UDclAHTGVNxlQOs();

    private /* synthetic */ $$Lambda$ContactList$09gMaIPFwd9UDclAHTGVNxlQOs() {
    }

    public final Object apply(Object obj) {
        return ((RosterEntry) obj).getJid();
    }
}
