package org.jivesoftware.smack;

public interface ReconnectionListener {
    void reconnectingIn(int i);

    void reconnectionFailed(Exception exc);
}
