package org.jivesoftware.smack.packet;

import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;

public abstract class SimpleIQ extends IQ {
    protected SimpleIQ(String childElementName, String childElementNamespace) {
        super(childElementName, childElementNamespace);
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.setEmptyElement();
        return xml;
    }
}
