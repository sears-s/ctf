package org.jivesoftware.smackx.muclight.element;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smackx.muclight.MUCLightRoomConfiguration;
import org.jivesoftware.smackx.muclight.element.MUCLightElements.ConfigurationElement;

public class MUCLightConfigurationIQ extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "urn:xmpp:muclight:0#configuration";
    private final MUCLightRoomConfiguration configuration;
    private final String version;

    public MUCLightConfigurationIQ(String version2, MUCLightRoomConfiguration configuration2) {
        super("query", "urn:xmpp:muclight:0#configuration");
        this.version = version2;
        this.configuration = configuration2;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.optElement("version", this.version);
        xml.element(new ConfigurationElement(this.configuration));
        return xml;
    }

    public String getVersion() {
        return this.version;
    }

    public MUCLightRoomConfiguration getConfiguration() {
        return this.configuration;
    }
}
