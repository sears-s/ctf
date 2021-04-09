package org.jivesoftware.smackx.bytestreams;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jxmpp.jid.Jid;

public interface BytestreamRequest {
    BytestreamSession accept() throws InterruptedException, XMPPErrorException, SmackException;

    Jid getFrom();

    String getSessionID();

    void reject() throws NotConnectedException, InterruptedException;
}
