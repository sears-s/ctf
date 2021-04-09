package org.jivesoftware.smackx.iot;

import java.util.logging.Logger;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.iot.provisioning.IoTProvisioningManager;
import org.jxmpp.jid.Jid;

public abstract class IoTManager extends Manager {
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(IoTManager.class.getName());
    private static boolean autoEnable;
    private boolean allowNonFriends;
    private final IoTProvisioningManager ioTProvisioningManager;

    protected abstract class IoTIqRequestHandler extends AbstractIqRequestHandler {
        /* access modifiers changed from: protected */
        public abstract IQ handleIoTIqRequest(IQ iq);

        protected IoTIqRequestHandler(String element, String namespace, Type type, Mode mode) {
            super(element, namespace, type, mode);
        }

        public final IQ handleIQRequest(IQ iqRequest) {
            if (IoTManager.this.isAllowed(iqRequest.getFrom())) {
                return handleIoTIqRequest(iqRequest);
            }
            Logger access$000 = IoTManager.LOGGER;
            StringBuilder sb = new StringBuilder();
            sb.append("Ignoring IQ request ");
            sb.append(iqRequest);
            access$000.warning(sb.toString());
            return null;
        }
    }

    public static void setAutoEnableIoTManagers(boolean autoEnable2) {
        autoEnable = autoEnable2;
    }

    public static boolean isAutoEnableActive() {
        return autoEnable;
    }

    protected IoTManager(XMPPConnection connection) {
        super(connection);
        this.ioTProvisioningManager = IoTProvisioningManager.getInstanceFor(connection);
    }

    public void setAllowNonFriends(boolean allowNonFriends2) {
        this.allowNonFriends = allowNonFriends2;
    }

    /* access modifiers changed from: protected */
    public boolean isAllowed(Jid jid) {
        if (this.allowNonFriends) {
            return true;
        }
        return this.ioTProvisioningManager.isMyFriend(jid);
    }
}
