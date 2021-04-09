package org.jivesoftware.smackx.privacy.filter;

import org.jivesoftware.smack.filter.FlexibleStanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.privacy.packet.Privacy;

public final class SetDefaultListFilter extends FlexibleStanzaTypeFilter<Privacy> {
    public static final SetDefaultListFilter INSTANCE = new SetDefaultListFilter();

    private SetDefaultListFilter() {
    }

    /* access modifiers changed from: protected */
    public boolean acceptSpecific(Privacy privacy) {
        boolean z = false;
        if (privacy.getType() != Type.set) {
            return false;
        }
        if (privacy.getDefaultName() != null || privacy.isDeclineDefaultList()) {
            z = true;
        }
        return z;
    }
}
