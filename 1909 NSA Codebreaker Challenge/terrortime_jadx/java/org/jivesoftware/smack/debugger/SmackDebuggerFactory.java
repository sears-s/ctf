package org.jivesoftware.smack.debugger;

import org.jivesoftware.smack.XMPPConnection;

public interface SmackDebuggerFactory {
    SmackDebugger create(XMPPConnection xMPPConnection) throws IllegalArgumentException;
}
