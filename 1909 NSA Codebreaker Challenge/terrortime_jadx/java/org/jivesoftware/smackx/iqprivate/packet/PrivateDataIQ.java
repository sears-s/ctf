package org.jivesoftware.smackx.iqprivate.packet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;

public class PrivateDataIQ extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "jabber:iq:private";
    private final String getElement;
    private final String getNamespace;
    private final PrivateData privateData;

    public PrivateDataIQ(PrivateData privateData2) {
        this(privateData2, null, null);
        setType(Type.set);
    }

    public PrivateDataIQ(String element, String namespace) {
        this(null, element, namespace);
        setType(Type.get);
    }

    private PrivateDataIQ(PrivateData privateData2, String getElement2, String getNamespace2) {
        super("query", NAMESPACE);
        this.privateData = privateData2;
        this.getElement = getElement2;
        this.getNamespace = getNamespace2;
    }

    public PrivateData getPrivateData() {
        return this.privateData;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        PrivateData privateData2 = this.privateData;
        if (privateData2 != null) {
            xml.append(privateData2.toXML());
        } else {
            xml.halfOpenElement(this.getElement).xmlnsAttribute(this.getNamespace).closeEmptyElement();
        }
        return xml;
    }
}
