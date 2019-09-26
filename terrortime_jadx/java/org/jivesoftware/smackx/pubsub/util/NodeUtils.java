package org.jivesoftware.smackx.pubsub.util;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormNode;
import org.jivesoftware.smackx.pubsub.PubSubElementType;

public class NodeUtils {
    public static ConfigureForm getFormFromPacket(Stanza packet, PubSubElementType elem) {
        return new ConfigureForm(((FormNode) packet.getExtension(elem.getElementName(), elem.getNamespace().getXmlns())).getForm());
    }
}
