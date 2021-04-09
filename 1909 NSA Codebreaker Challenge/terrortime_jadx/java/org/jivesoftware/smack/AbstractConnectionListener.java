package org.jivesoftware.smack;

public class AbstractConnectionListener implements ConnectionListener {
    public void connected(XMPPConnection connection) {
    }

    public void authenticated(XMPPConnection connection, boolean resumed) {
    }

    public void connectionClosed() {
    }

    public void connectionClosedOnError(Exception e) {
    }
}
