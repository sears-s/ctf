package org.jivesoftware.smackx.iqversion.packet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.StringUtils;
import org.jxmpp.jid.Jid;

public class Version extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "jabber:iq:version";
    private final String name;
    private String os;
    private final String version;

    public Version() {
        super("query", NAMESPACE);
        this.name = null;
        this.version = null;
        setType(Type.get);
    }

    public Version(Jid to) {
        this();
        setTo(to);
    }

    public Version(String name2, String version2) {
        this(name2, version2, null);
    }

    public Version(String name2, String version2, String os2) {
        super("query", NAMESPACE);
        setType(Type.result);
        this.name = (String) StringUtils.requireNotNullOrEmpty(name2, "name must not be null");
        this.version = (String) StringUtils.requireNotNullOrEmpty(version2, "version must not be null");
        this.os = os2;
    }

    public Version(Version original) {
        this(original.name, original.version, original.os);
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getOs() {
        return this.os;
    }

    public void setOs(String os2) {
        this.os = os2;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.optElement("name", this.name);
        xml.optElement("version", this.version);
        xml.optElement("os", this.os);
        return xml;
    }

    public static Version createResultFor(Stanza request, Version version2) {
        Version result = new Version(version2);
        result.setStanzaId(request.getStanzaId());
        result.setTo(request.getFrom());
        return result;
    }
}
