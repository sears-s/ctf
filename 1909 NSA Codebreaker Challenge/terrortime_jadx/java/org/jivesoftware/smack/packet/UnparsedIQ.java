package org.jivesoftware.smack.packet;

import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;

public class UnparsedIQ extends IQ {
    private final CharSequence content;

    public UnparsedIQ(String element, String namespace, CharSequence content2) {
        super(element, namespace);
        this.content = content2;
    }

    public CharSequence getContent() {
        return this.content;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.escape(this.content);
        return xml;
    }
}
