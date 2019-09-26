package org.jivesoftware.smack;

public interface ConnectionListener {
    void authenticated(XMPPConnection xMPPConnection, boolean z);

    void connected(XMPPConnection xMPPConnection);

    void connectionClosed();

    void connectionClosedOnError(Exception exc);
}
