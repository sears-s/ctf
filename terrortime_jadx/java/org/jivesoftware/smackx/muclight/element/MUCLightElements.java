package org.jivesoftware.smackx.muclight.element;

import java.util.HashMap;
import java.util.Map.Entry;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Subject;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.muclight.MUCLightAffiliation;
import org.jivesoftware.smackx.muclight.MUCLightRoomConfiguration;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jxmpp.jid.Jid;

public abstract class MUCLightElements {

    public static class AffiliationsChangeExtension implements ExtensionElement {
        public static final String ELEMENT = "x";
        public static final String NAMESPACE = "urn:xmpp:muclight:0#affiliations";
        private final HashMap<Jid, MUCLightAffiliation> affiliations;
        private final String prevVersion;
        private final String version;

        public AffiliationsChangeExtension(HashMap<Jid, MUCLightAffiliation> affiliations2, String prevVersion2, String version2) {
            this.affiliations = affiliations2;
            this.prevVersion = prevVersion2;
            this.version = version2;
        }

        public String getElementName() {
            return "x";
        }

        public String getNamespace() {
            return "urn:xmpp:muclight:0#affiliations";
        }

        public HashMap<Jid, MUCLightAffiliation> getAffiliations() {
            return this.affiliations;
        }

        public String getPrevVersion() {
            return this.prevVersion;
        }

        public String getVersion() {
            return this.version;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
            xml.rightAngleBracket();
            xml.optElement("prev-version", this.prevVersion);
            xml.optElement("version", this.version);
            for (Entry<Jid, MUCLightAffiliation> pair : this.affiliations.entrySet()) {
                xml.element(new UserWithAffiliationElement((Jid) pair.getKey(), (MUCLightAffiliation) pair.getValue()));
            }
            xml.closeElement((NamedElement) this);
            return xml;
        }

        public static AffiliationsChangeExtension from(Message message) {
            return (AffiliationsChangeExtension) message.getExtension("x", "urn:xmpp:muclight:0#affiliations");
        }
    }

    public static class BlockingElement implements Element {
        private Boolean allow;
        private Boolean isRoom;
        private Jid jid;

        public BlockingElement(Jid jid2, Boolean allow2, Boolean isRoom2) {
            this.jid = jid2;
            this.allow = allow2;
            this.isRoom = isRoom2;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder();
            String tag = this.isRoom.booleanValue() ? "room" : "user";
            xml.halfOpenElement(tag);
            xml.attribute("action", this.allow.booleanValue() ? "allow" : "deny");
            xml.rightAngleBracket();
            xml.escape((CharSequence) this.jid);
            xml.closeElement(tag);
            return xml;
        }
    }

    public static class ConfigurationElement implements Element {
        private MUCLightRoomConfiguration configuration;

        public ConfigurationElement(MUCLightRoomConfiguration configuration2) {
            this.configuration = configuration2;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder();
            String str = "configuration";
            xml.openElement(str);
            xml.element("roomname", this.configuration.getRoomName());
            xml.optElement(Subject.ELEMENT, this.configuration.getSubject());
            if (this.configuration.getCustomConfigs() != null) {
                for (Entry<String, String> pair : this.configuration.getCustomConfigs().entrySet()) {
                    xml.element((String) pair.getKey(), (String) pair.getValue());
                }
            }
            xml.closeElement(str);
            return xml;
        }
    }

    public static class ConfigurationsChangeExtension implements ExtensionElement {
        public static final String ELEMENT = "x";
        public static final String NAMESPACE = "urn:xmpp:muclight:0#configuration";
        private final HashMap<String, String> customConfigs;
        private final String prevVersion;
        private final String roomName;
        private final String subject;
        private final String version;

        public ConfigurationsChangeExtension(String prevVersion2, String version2, String roomName2, String subject2, HashMap<String, String> customConfigs2) {
            this.prevVersion = prevVersion2;
            this.version = version2;
            this.roomName = roomName2;
            this.subject = subject2;
            this.customConfigs = customConfigs2;
        }

        public String getElementName() {
            return "x";
        }

        public String getNamespace() {
            return "urn:xmpp:muclight:0#configuration";
        }

        public String getPrevVersion() {
            return this.prevVersion;
        }

        public String getVersion() {
            return this.version;
        }

        public String getRoomName() {
            return this.roomName;
        }

        public String getSubject() {
            return this.subject;
        }

        public HashMap<String, String> getCustomConfigs() {
            return this.customConfigs;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
            xml.rightAngleBracket();
            xml.optElement("prev-version", this.prevVersion);
            xml.optElement("version", this.version);
            xml.optElement("roomname", this.roomName);
            xml.optElement(Subject.ELEMENT, this.subject);
            HashMap<String, String> hashMap = this.customConfigs;
            if (hashMap != null) {
                for (Entry<String, String> pair : hashMap.entrySet()) {
                    xml.element((String) pair.getKey(), (String) pair.getValue());
                }
            }
            xml.closeElement((NamedElement) this);
            return xml;
        }

        public static ConfigurationsChangeExtension from(Message message) {
            return (ConfigurationsChangeExtension) message.getExtension("x", "urn:xmpp:muclight:0#configuration");
        }
    }

    public static class OccupantsElement implements Element {
        private HashMap<Jid, MUCLightAffiliation> occupants;

        public OccupantsElement(HashMap<Jid, MUCLightAffiliation> occupants2) {
            this.occupants = occupants2;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder();
            String str = "occupants";
            xml.openElement(str);
            for (Entry<Jid, MUCLightAffiliation> pair : this.occupants.entrySet()) {
                xml.element(new UserWithAffiliationElement((Jid) pair.getKey(), (MUCLightAffiliation) pair.getValue()));
            }
            xml.closeElement(str);
            return xml;
        }
    }

    public static class UserWithAffiliationElement implements Element {
        private MUCLightAffiliation affiliation;
        private Jid user;

        public UserWithAffiliationElement(Jid user2, MUCLightAffiliation affiliation2) {
            this.user = user2;
            this.affiliation = affiliation2;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder();
            String str = "user";
            xml.halfOpenElement(str);
            xml.attribute(Affiliation.ELEMENT, (Enum<?>) this.affiliation);
            xml.rightAngleBracket();
            xml.escape((CharSequence) this.user);
            xml.closeElement(str);
            return xml;
        }
    }
}
