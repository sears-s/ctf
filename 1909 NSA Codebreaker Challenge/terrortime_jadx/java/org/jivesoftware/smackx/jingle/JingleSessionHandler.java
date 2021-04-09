package org.jivesoftware.smackx.jingle;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.jingle.element.Jingle;

public interface JingleSessionHandler {
    IQ handleJingleSessionRequest(Jingle jingle);
}
