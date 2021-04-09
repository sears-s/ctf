package org.jivesoftware.smackx.ping.android;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.util.Async;
import org.jivesoftware.smackx.ping.PingManager;

public final class ServerPingWithAlarmManager extends Manager {
    private static final BroadcastReceiver ALARM_BROADCAST_RECEIVER = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Set<Entry<XMPPConnection, ServerPingWithAlarmManager>> managers;
            ServerPingWithAlarmManager.LOGGER.fine("Ping Alarm broadcast received");
            synchronized (ServerPingWithAlarmManager.class) {
                managers = new HashSet<>(ServerPingWithAlarmManager.INSTANCES.entrySet());
            }
            for (Entry<XMPPConnection, ServerPingWithAlarmManager> entry : managers) {
                XMPPConnection connection = (XMPPConnection) entry.getKey();
                if (((ServerPingWithAlarmManager) entry.getValue()).isEnabled()) {
                    Logger access$000 = ServerPingWithAlarmManager.LOGGER;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Calling pingServerIfNecessary for connection ");
                    sb.append(connection);
                    access$000.fine(sb.toString());
                    final PingManager pingManager = PingManager.getInstanceFor(connection);
                    AnonymousClass1 r5 = new Runnable() {
                        public void run() {
                            pingManager.pingServerIfNecessary();
                        }
                    };
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("PingServerIfNecessary (");
                    sb2.append(connection.getConnectionCounter());
                    sb2.append(')');
                    Async.go(r5, sb2.toString());
                } else {
                    Logger access$0002 = ServerPingWithAlarmManager.LOGGER;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("NOT calling pingServerIfNecessary (disabled) on connection ");
                    sb3.append(connection.getConnectionCounter());
                    access$0002.fine(sb3.toString());
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public static final Map<XMPPConnection, ServerPingWithAlarmManager> INSTANCES = new WeakHashMap();
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(ServerPingWithAlarmManager.class.getName());
    private static final String PING_ALARM_ACTION = "org.igniterealtime.smackx.ping.ACTION";
    private static AlarmManager sAlarmManager;
    private static Context sContext;
    private static PendingIntent sPendingIntent;
    private boolean mEnabled = true;

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                ServerPingWithAlarmManager.getInstanceFor(connection);
            }
        });
    }

    public static synchronized ServerPingWithAlarmManager getInstanceFor(XMPPConnection connection) {
        ServerPingWithAlarmManager serverPingWithAlarmManager;
        synchronized (ServerPingWithAlarmManager.class) {
            serverPingWithAlarmManager = (ServerPingWithAlarmManager) INSTANCES.get(connection);
            if (serverPingWithAlarmManager == null) {
                serverPingWithAlarmManager = new ServerPingWithAlarmManager(connection);
                INSTANCES.put(connection, serverPingWithAlarmManager);
            }
        }
        return serverPingWithAlarmManager;
    }

    private ServerPingWithAlarmManager(XMPPConnection connection) {
        super(connection);
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public static void onCreate(Context context) {
        sContext = context;
        BroadcastReceiver broadcastReceiver = ALARM_BROADCAST_RECEIVER;
        String str = PING_ALARM_ACTION;
        context.registerReceiver(broadcastReceiver, new IntentFilter(str));
        sAlarmManager = (AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM);
        sPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(str), 0);
        sAlarmManager.setInexactRepeating(2, SystemClock.elapsedRealtime() + 1800000, 1800000, sPendingIntent);
    }

    public static void onDestroy() {
        sContext.unregisterReceiver(ALARM_BROADCAST_RECEIVER);
        sAlarmManager.cancel(sPendingIntent);
    }
}
