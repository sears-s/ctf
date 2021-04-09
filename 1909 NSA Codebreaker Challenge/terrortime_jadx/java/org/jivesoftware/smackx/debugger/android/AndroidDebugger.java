package org.jivesoftware.smackx.debugger.android;

import android.util.Log;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.debugger.AbstractDebugger;

public class AndroidDebugger extends AbstractDebugger {
    public AndroidDebugger(XMPPConnection connection) {
        super(connection);
    }

    /* access modifiers changed from: protected */
    public void log(String logMessage) {
        Log.d("SMACK", logMessage);
    }

    /* access modifiers changed from: protected */
    public void log(String logMessage, Throwable throwable) {
        Log.d("SMACK", logMessage, throwable);
    }
}
