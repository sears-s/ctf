package org.jivesoftware.smackx.privacy.filter;

import org.jivesoftware.smack.filter.FlexibleStanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.privacy.packet.Privacy;

public final class SetActiveListFilter extends FlexibleStanzaTypeFilter<Privacy> {
    public static final SetActiveListFilter INSTANCE = new SetActiveListFilter();

    private SetActiveListFilter() {
    }

    /* access modifiers changed from: protected */
    public boolean acceptSpecific(Privacy privacy) {
        boolean z = false;
        if (privacy.getType() != Type.set) {
            return false;
        }
        if (privacy.getActiveName() != null || privacy.isDeclineActiveList()) {
            z = true;
        }
        return z;
    }
}
