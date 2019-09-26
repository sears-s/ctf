package org.jivesoftware.smack;

import java.lang.ref.WeakReference;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.util.Objects;

public abstract class Manager {
    final WeakReference<XMPPConnection> weakConnection;

    public Manager(XMPPConnection connection) {
        Objects.requireNonNull(connection, "XMPPConnection must not be null");
        this.weakConnection = new WeakReference<>(connection);
    }

    /* access modifiers changed from: protected */
    public final XMPPConnection connection() {
        return (XMPPConnection) this.weakConnection.get();
    }

    /* access modifiers changed from: protected */
    public final XMPPConnection getAuthenticatedConnectionOrThrow() throws NotLoggedInException {
        XMPPConnection connection = connection();
        if (connection.isAuthenticated()) {
            return connection;
        }
        throw new NotLoggedInException();
    }

    protected static final ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit unit) {
        return AbstractXMPPConnection.SCHEDULED_EXECUTOR_SERVICE.schedule(runnable, delay, unit);
    }
}
