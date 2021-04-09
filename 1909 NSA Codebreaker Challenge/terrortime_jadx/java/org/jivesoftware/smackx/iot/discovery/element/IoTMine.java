package org.jivesoftware.smackx.iot.discovery.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;

public class IoTMine extends IQ {
    public static final String ELEMENT = "mine";
    public static final String NAMESPACE = "urn:xmpp:iot:discovery";
    private final List<Tag> metaTags;
    private final boolean publicThing;

    public IoTMine(Collection<Tag> metaTags2, boolean publicThing2) {
        this((List<Tag>) new ArrayList<Tag>(metaTags2), publicThing2);
    }

    public IoTMine(List<Tag> metaTags2, boolean publicThing2) {
        super(ELEMENT, "urn:xmpp:iot:discovery");
        this.metaTags = metaTags2;
        this.publicThing = publicThing2;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.optBooleanAttributeDefaultTrue("public", this.publicThing);
        xml.rightAngleBracket();
        xml.append((Collection<? extends Element>) this.metaTags);
        return xml;
    }
}
