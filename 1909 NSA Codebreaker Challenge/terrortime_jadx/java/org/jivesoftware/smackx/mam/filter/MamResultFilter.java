package org.jivesoftware.smackx.mam.filter;

import org.jivesoftware.smack.filter.FlexibleStanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.mam.element.MamQueryIQ;

public class MamResultFilter extends FlexibleStanzaTypeFilter<Message> {
    private final String queryId;

    public MamResultFilter(MamQueryIQ mamQueryIQ) {
        super(Message.class);
        this.queryId = mamQueryIQ.getQueryId();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001a, code lost:
        if (r3.equals(r2) != false) goto L_0x001c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean acceptSpecific(org.jivesoftware.smack.packet.Message r5) {
        /*
            r4 = this;
            org.jivesoftware.smackx.mam.element.MamElements$MamResultExtension r0 = org.jivesoftware.smackx.mam.element.MamElements.MamResultExtension.from(r5)
            r1 = 0
            if (r0 != 0) goto L_0x0008
            return r1
        L_0x0008:
            java.lang.String r2 = r0.getQueryId()
            java.lang.String r3 = r4.queryId
            if (r3 != 0) goto L_0x0012
            if (r2 == 0) goto L_0x001c
        L_0x0012:
            java.lang.String r3 = r4.queryId
            if (r3 == 0) goto L_0x001d
            boolean r3 = r3.equals(r2)
            if (r3 == 0) goto L_0x001d
        L_0x001c:
            r1 = 1
        L_0x001d:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.mam.filter.MamResultFilter.acceptSpecific(org.jivesoftware.smack.packet.Message):boolean");
    }
}
