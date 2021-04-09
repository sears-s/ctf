package org.jivesoftware.smackx.jingle.element;

import org.jivesoftware.smack.packet.NamedElement;

public abstract class JingleContentTransportCandidate implements NamedElement {
    public static final String ELEMENT = "candidate";

    public String getElementName() {
        return ELEMENT;
    }
}
