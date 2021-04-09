package org.jivesoftware.smack.packet;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;

public class StreamOpen implements Nonza {
    public static final String CLIENT_NAMESPACE = "jabber:client";
    public static final String ELEMENT = "stream:stream";
    public static final String SERVER_NAMESPACE = "jabber:server";
    public static final String VERSION = "1.0";
    private final String contentNamespace;
    private final String from;
    private final String id;
    private final String lang;
    private final String to;

    /* renamed from: org.jivesoftware.smack.packet.StreamOpen$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$packet$StreamOpen$StreamContentNamespace = new int[StreamContentNamespace.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$packet$StreamOpen$StreamContentNamespace[StreamContentNamespace.client.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$StreamOpen$StreamContentNamespace[StreamContentNamespace.server.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public enum StreamContentNamespace {
        client,
        server
    }

    public StreamOpen(CharSequence to2) {
        this(to2, null, null, null, StreamContentNamespace.client);
    }

    public StreamOpen(CharSequence to2, CharSequence from2, String id2) {
        CharSequence charSequence = to2;
        CharSequence charSequence2 = from2;
        String str = id2;
        this(charSequence, charSequence2, str, "en", StreamContentNamespace.client);
    }

    public StreamOpen(CharSequence to2, CharSequence from2, String id2, String lang2, StreamContentNamespace ns) {
        this.to = StringUtils.maybeToString(to2);
        this.from = StringUtils.maybeToString(from2);
        this.id = id2;
        this.lang = lang2;
        int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$packet$StreamOpen$StreamContentNamespace[ns.ordinal()];
        if (i == 1) {
            this.contentNamespace = "jabber:client";
        } else if (i == 2) {
            this.contentNamespace = SERVER_NAMESPACE;
        } else {
            throw new IllegalStateException();
        }
    }

    public String getNamespace() {
        return this.contentNamespace;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder();
        xml.halfOpenElement(getElementName());
        xml.attribute("xmlns", enclosingNamespace);
        xml.attribute(PrivacyItem.SUBSCRIPTION_TO, this.to);
        xml.attribute("xmlns:stream", "http://etherx.jabber.org/streams");
        xml.attribute("version", "1.0");
        xml.optAttribute(PrivacyItem.SUBSCRIPTION_FROM, this.from);
        xml.optAttribute("id", this.id);
        xml.xmllangAttribute(this.lang);
        xml.rightAngleBracket();
        return xml;
    }
}
