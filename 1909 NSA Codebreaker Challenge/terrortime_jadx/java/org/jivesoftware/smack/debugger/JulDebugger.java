package org.jivesoftware.smack.debugger;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.XMPPConnection;

public class JulDebugger extends AbstractDebugger {
    private static final Logger LOGGER = Logger.getLogger(JulDebugger.class.getName());

    public JulDebugger(XMPPConnection connection) {
        super(connection);
    }

    /* access modifiers changed from: protected */
    public void log(String logMessage) {
        LOGGER.fine(logMessage);
    }

    /* access modifiers changed from: protected */
    public void log(String logMessage, Throwable throwable) {
        LOGGER.log(Level.FINE, logMessage, throwable);
    }
}
