package org.jivesoftware.smackx.mam.element;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smackx.rsm.packet.RSMSet;

public class MamFinIQ extends IQ {
    public static final String ELEMENT = "fin";
    public static final String NAMESPACE = "urn:xmpp:mam:1";
    private final boolean complete;
    private final String queryId;
    private final RSMSet rsmSet;
    private final boolean stable;

    public MamFinIQ(String queryId2, RSMSet rsmSet2, boolean complete2, boolean stable2) {
        super(ELEMENT, "urn:xmpp:mam:1");
        if (rsmSet2 != null) {
            this.rsmSet = rsmSet2;
            this.complete = complete2;
            this.stable = stable2;
            this.queryId = queryId2;
            return;
        }
        throw new IllegalArgumentException("rsmSet must not be null");
    }

    public RSMSet getRSMSet() {
        return this.rsmSet;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public boolean isStable() {
        return this.stable;
    }

    public final String getQueryId() {
        return this.queryId;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.optAttribute("queryid", this.queryId);
        xml.optBooleanAttribute("complete", this.complete);
        xml.optBooleanAttribute("stable", this.stable);
        if (this.rsmSet == null) {
            xml.setEmptyElement();
        } else {
            xml.rightAngleBracket();
            xml.element(this.rsmSet);
        }
        return xml;
    }
}
