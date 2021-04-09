package org.jivesoftware.smack.packet;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.jivesoftware.smack.packet.StanzaError.Builder;
import org.jivesoftware.smack.packet.id.StanzaIdUtil;
import org.jivesoftware.smack.util.MultiMap;
import org.jivesoftware.smack.util.PacketUtil;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppStringUtils;

public abstract class Stanza implements TopLevelStreamElement {
    protected static final String DEFAULT_LANGUAGE = Locale.getDefault().getLanguage().toLowerCase(Locale.US);
    public static final String ITEM = "item";
    public static final String TEXT = "text";
    private StanzaError error;
    private Jid from;
    private String id;
    protected String language;
    private final MultiMap<String, ExtensionElement> packetExtensions;
    private Jid to;

    public abstract String toString();

    protected Stanza() {
        this(StanzaIdUtil.newStanzaId());
    }

    protected Stanza(String stanzaId) {
        this.packetExtensions = new MultiMap<>();
        this.id = null;
        this.error = null;
        setStanzaId(stanzaId);
    }

    protected Stanza(Stanza p) {
        this.packetExtensions = new MultiMap<>();
        this.id = null;
        this.error = null;
        this.id = p.getStanzaId();
        this.to = p.getTo();
        this.from = p.getFrom();
        this.error = p.error;
        for (ExtensionElement pe : p.getExtensions()) {
            addExtension(pe);
        }
    }

    public String getStanzaId() {
        return this.id;
    }

    @Deprecated
    public String getPacketID() {
        return getStanzaId();
    }

    public void setStanzaId(String id2) {
        if (id2 != null) {
            StringUtils.requireNotNullOrEmpty(id2, "id must either be null or not the empty String");
        }
        this.id = id2;
    }

    @Deprecated
    public void setPacketID(String packetID) {
        setStanzaId(packetID);
    }

    public boolean hasStanzaIdSet() {
        return this.id != null;
    }

    public String setStanzaId() {
        if (!hasStanzaIdSet()) {
            setStanzaId(StanzaIdUtil.newStanzaId());
        }
        return getStanzaId();
    }

    public Jid getTo() {
        return this.to;
    }

    @Deprecated
    public void setTo(String to2) {
        try {
            setTo(JidCreate.from(to2));
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setTo(Jid to2) {
        this.to = to2;
    }

    public Jid getFrom() {
        return this.from;
    }

    @Deprecated
    public void setFrom(String from2) {
        try {
            setFrom(JidCreate.from(from2));
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setFrom(Jid from2) {
        this.from = from2;
    }

    public StanzaError getError() {
        return this.error;
    }

    @Deprecated
    public void setError(StanzaError error2) {
        this.error = error2;
    }

    public void setError(Builder xmppErrorBuilder) {
        if (xmppErrorBuilder != null) {
            xmppErrorBuilder.setStanza(this);
            this.error = xmppErrorBuilder.build();
        }
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language2) {
        this.language = language2;
    }

    public List<ExtensionElement> getExtensions() {
        List<ExtensionElement> values;
        synchronized (this.packetExtensions) {
            values = this.packetExtensions.values();
        }
        return values;
    }

    public List<ExtensionElement> getExtensions(String elementName, String namespace) {
        StringUtils.requireNotNullOrEmpty(elementName, "elementName must not be null or empty");
        StringUtils.requireNotNullOrEmpty(namespace, "namespace must not be null or empty");
        return this.packetExtensions.getAll(XmppStringUtils.generateKey(elementName, namespace));
    }

    public ExtensionElement getExtension(String namespace) {
        return PacketUtil.extensionElementFrom(getExtensions(), null, namespace);
    }

    public <PE extends ExtensionElement> PE getExtension(String elementName, String namespace) {
        ExtensionElement packetExtension;
        if (namespace == null) {
            return null;
        }
        String key = XmppStringUtils.generateKey(elementName, namespace);
        synchronized (this.packetExtensions) {
            packetExtension = (ExtensionElement) this.packetExtensions.getFirst(key);
        }
        if (packetExtension == null) {
            return null;
        }
        return packetExtension;
    }

    public void addExtension(ExtensionElement extension) {
        if (extension != null) {
            String key = XmppStringUtils.generateKey(extension.getElementName(), extension.getNamespace());
            synchronized (this.packetExtensions) {
                this.packetExtensions.put(key, extension);
            }
        }
    }

    public ExtensionElement overrideExtension(ExtensionElement extension) {
        ExtensionElement removedExtension;
        if (extension == null) {
            return null;
        }
        synchronized (this.packetExtensions) {
            removedExtension = removeExtension(extension.getElementName(), extension.getNamespace());
            addExtension(extension);
        }
        return removedExtension;
    }

    public void addExtensions(Collection<ExtensionElement> extensions) {
        if (extensions != null) {
            for (ExtensionElement packetExtension : extensions) {
                addExtension(packetExtension);
            }
        }
    }

    public boolean hasExtension(String elementName, String namespace) {
        boolean containsKey;
        if (elementName == null) {
            return hasExtension(namespace);
        }
        String key = XmppStringUtils.generateKey(elementName, namespace);
        synchronized (this.packetExtensions) {
            containsKey = this.packetExtensions.containsKey(key);
        }
        return containsKey;
    }

    public boolean hasExtension(String namespace) {
        synchronized (this.packetExtensions) {
            for (ExtensionElement packetExtension : this.packetExtensions.values()) {
                if (packetExtension.getNamespace().equals(namespace)) {
                    return true;
                }
            }
            return false;
        }
    }

    public ExtensionElement removeExtension(String elementName, String namespace) {
        ExtensionElement extensionElement;
        String key = XmppStringUtils.generateKey(elementName, namespace);
        synchronized (this.packetExtensions) {
            extensionElement = (ExtensionElement) this.packetExtensions.remove(key);
        }
        return extensionElement;
    }

    public ExtensionElement removeExtension(ExtensionElement extension) {
        String key = XmppStringUtils.generateKey(extension.getElementName(), extension.getNamespace());
        synchronized (this.packetExtensions) {
            if (this.packetExtensions.getAll(key).remove(extension)) {
                return extension;
            }
            return null;
        }
    }

    public static String getDefaultLanguage() {
        return DEFAULT_LANGUAGE;
    }

    /* access modifiers changed from: protected */
    public String addCommonAttributes(XmlStringBuilder xml, String enclosingNamespace) {
        String namespace;
        if (enclosingNamespace == null || !enclosingNamespace.equals("jabber:client") || !enclosingNamespace.equals(StreamOpen.SERVER_NAMESPACE)) {
            namespace = "jabber:client";
        } else {
            namespace = enclosingNamespace;
        }
        xml.xmlnsAttribute(namespace);
        xml.optAttribute(PrivacyItem.SUBSCRIPTION_TO, (CharSequence) getTo());
        xml.optAttribute(PrivacyItem.SUBSCRIPTION_FROM, (CharSequence) getFrom());
        xml.optAttribute("id", getStanzaId());
        xml.xmllangAttribute(getLanguage());
        return namespace;
    }

    /* access modifiers changed from: protected */
    public void logCommonAttributes(StringBuilder sb) {
        if (getTo() != null) {
            sb.append("to=");
            sb.append(this.to);
            sb.append(',');
        }
        if (getFrom() != null) {
            sb.append("from=");
            sb.append(this.from);
            sb.append(',');
        }
        if (hasStanzaIdSet()) {
            sb.append("id=");
            sb.append(this.id);
            sb.append(',');
        }
    }

    /* access modifiers changed from: protected */
    public void appendErrorIfExists(XmlStringBuilder xml, String enclosingNamespace) {
        StanzaError error2 = getError();
        if (error2 != null) {
            xml.append(error2.toXML(enclosingNamespace));
        }
    }
}
