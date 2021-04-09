package org.jivesoftware.smackx.eme.element;

import java.util.HashMap;
import java.util.Map;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class ExplicitMessageEncryptionElement implements ExtensionElement {
    public static final String ELEMENT = "encryption";
    public static final String NAMESPACE = "urn:xmpp:eme:0";
    /* access modifiers changed from: private */
    public static final Map<String, ExplicitMessageEncryptionProtocol> PROTOCOL_LUT = new HashMap();
    private final String encryptionNamespace;
    private boolean isUnknownProtocol;
    private final String name;
    private ExplicitMessageEncryptionProtocol protocolCache;

    public enum ExplicitMessageEncryptionProtocol {
        openpgpV0("urn:xmpp:openpgp:0", "OpenPGP for XMPP (XEP-0373)"),
        otrV0("urn:xmpp:otr:0", "Off-the-Record Messaging (XEP-0364)"),
        omemoVAxolotl("eu.siacs.conversations.axolotl", "OMEMO Multi End Message and Object Encryption (XEP-0384)"),
        legacyOpenPGP("jabber:x:encrypted", "Legacy OpenPGP for XMPP [DANGEROUS, DO NOT USE!]");
        
        private final String name;
        private final String namespace;

        private ExplicitMessageEncryptionProtocol(String namespace2, String name2) {
            this.namespace = namespace2;
            this.name = name2;
            ExplicitMessageEncryptionElement.PROTOCOL_LUT.put(namespace2, this);
        }

        public String getNamespace() {
            return this.namespace;
        }

        public String getName() {
            return this.name;
        }

        public static ExplicitMessageEncryptionProtocol from(String namespace2) {
            return (ExplicitMessageEncryptionProtocol) ExplicitMessageEncryptionElement.PROTOCOL_LUT.get(namespace2);
        }
    }

    public ExplicitMessageEncryptionElement(ExplicitMessageEncryptionProtocol protocol) {
        this(protocol.getNamespace(), protocol.getName());
    }

    public ExplicitMessageEncryptionElement(String encryptionNamespace2) {
        this(encryptionNamespace2, null);
    }

    public ExplicitMessageEncryptionElement(String encryptionNamespace2, String name2) {
        this.encryptionNamespace = (String) StringUtils.requireNotNullOrEmpty(encryptionNamespace2, "encryptionNamespace must not be null");
        this.name = name2;
    }

    public ExplicitMessageEncryptionProtocol getProtocol() {
        ExplicitMessageEncryptionProtocol explicitMessageEncryptionProtocol = this.protocolCache;
        if (explicitMessageEncryptionProtocol != null) {
            return explicitMessageEncryptionProtocol;
        }
        if (this.isUnknownProtocol) {
            return null;
        }
        ExplicitMessageEncryptionProtocol protocol = (ExplicitMessageEncryptionProtocol) PROTOCOL_LUT.get(this.encryptionNamespace);
        if (protocol == null) {
            this.isUnknownProtocol = true;
            return null;
        }
        this.protocolCache = protocol;
        return protocol;
    }

    public String getEncryptionNamespace() {
        return this.encryptionNamespace;
    }

    public String getName() {
        return this.name;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return "urn:xmpp:eme:0";
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.attribute("namespace", getEncryptionNamespace());
        xml.optAttribute("name", getName());
        xml.closeEmptyElement();
        return xml;
    }

    public static ExplicitMessageEncryptionElement from(Message message) {
        return (ExplicitMessageEncryptionElement) message.getExtension(ELEMENT, "urn:xmpp:eme:0");
    }
}
