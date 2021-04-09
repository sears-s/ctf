package org.jivesoftware.smackx.jingle.element;

import org.jivesoftware.smack.packet.NamedElement;

public abstract class JingleContentDescriptionChildElement implements NamedElement {
    public static final String ELEMENT = "payload-type";

    public String getElementName() {
        return ELEMENT;
    }
}
