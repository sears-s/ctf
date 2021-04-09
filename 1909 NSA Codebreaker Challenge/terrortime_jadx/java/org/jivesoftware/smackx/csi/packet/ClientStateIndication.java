package org.jivesoftware.smackx.csi.packet;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Nonza;

public class ClientStateIndication {
    public static final String NAMESPACE = "urn:xmpp:csi:0";

    public static final class Active implements Nonza {
        public static final String ELEMENT = "active";
        public static final Active INSTANCE = new Active();

        private Active() {
        }

        public String getNamespace() {
            return ClientStateIndication.NAMESPACE;
        }

        public String getElementName() {
            return ELEMENT;
        }

        public String toXML(String enclosingNamespace) {
            return "<active xmlns='urn:xmpp:csi:0'/>";
        }
    }

    public static final class Feature implements ExtensionElement {
        public static final String ELEMENT = "csi";
        public static final Feature INSTANCE = new Feature();

        private Feature() {
        }

        public String getElementName() {
            return ELEMENT;
        }

        public String toXML(String enclosingNamespace) {
            return "<csi xmlns='urn:xmpp:csi:0'/>";
        }

        public String getNamespace() {
            return ClientStateIndication.NAMESPACE;
        }
    }

    public static final class Inactive implements Nonza {
        public static final String ELEMENT = "inactive";
        public static final Inactive INSTANCE = new Inactive();

        private Inactive() {
        }

        public String getNamespace() {
            return ClientStateIndication.NAMESPACE;
        }

        public String getElementName() {
            return ELEMENT;
        }

        public String toXML(String enclosingNamespace) {
            return "<inactive xmlns='urn:xmpp:csi:0'/>";
        }
    }
}
