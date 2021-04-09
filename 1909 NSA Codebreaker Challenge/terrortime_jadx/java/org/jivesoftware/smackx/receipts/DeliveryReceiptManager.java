package org.jivesoftware.smackx.receipts;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.MessageWithBodiesFilter;
import org.jivesoftware.smack.filter.NotFilter;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jxmpp.jid.Jid;

public final class DeliveryReceiptManager extends Manager {
    private static final StanzaListener AUTO_ADD_DELIVERY_RECEIPT_REQUESTS_LISTENER = new StanzaListener() {
        public void processStanza(Stanza packet) throws NotConnectedException {
            DeliveryReceiptRequest.addTo((Message) packet);
        }
    };
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(DeliveryReceiptManager.class.getName());
    private static final StanzaFilter MESSAGES_TO_REQUEST_RECEIPTS_FOR;
    private static final StanzaFilter MESSAGES_WITH_DELIVERY_RECEIPT;
    private static final StanzaFilter NON_ERROR_GROUPCHAT_MESSAGES_WITH_DELIVERY_RECEIPT_REQUEST = new AndFilter(StanzaTypeFilter.MESSAGE, new StanzaExtensionFilter((ExtensionElement) new DeliveryReceiptRequest()), new NotFilter(MessageTypeFilter.ERROR));
    private static AutoReceiptMode defaultAutoReceiptMode = AutoReceiptMode.ifIsSubscribed;
    private static final Map<XMPPConnection, DeliveryReceiptManager> instances = new WeakHashMap();
    /* access modifiers changed from: private */
    public AutoReceiptMode autoReceiptMode = defaultAutoReceiptMode;
    /* access modifiers changed from: private */
    public final Set<ReceiptReceivedListener> receiptReceivedListeners = new CopyOnWriteArraySet();

    /* renamed from: org.jivesoftware.smackx.receipts.DeliveryReceiptManager$5 reason: invalid class name */
    static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smackx$receipts$DeliveryReceiptManager$AutoReceiptMode = new int[AutoReceiptMode.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smackx$receipts$DeliveryReceiptManager$AutoReceiptMode[AutoReceiptMode.disabled.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$receipts$DeliveryReceiptManager$AutoReceiptMode[AutoReceiptMode.ifIsSubscribed.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$receipts$DeliveryReceiptManager$AutoReceiptMode[AutoReceiptMode.always.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public enum AutoReceiptMode {
        disabled,
        ifIsSubscribed,
        always
    }

    static {
        String str = DeliveryReceipt.NAMESPACE;
        String str2 = "received";
        MESSAGES_WITH_DELIVERY_RECEIPT = new AndFilter(StanzaTypeFilter.MESSAGE, new StanzaExtensionFilter(str2, str));
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                DeliveryReceiptManager.getInstanceFor(connection);
            }
        });
        MESSAGES_TO_REQUEST_RECEIPTS_FOR = new AndFilter(MessageTypeFilter.NORMAL_OR_CHAT_OR_HEADLINE, new NotFilter(new StanzaExtensionFilter(str2, str)), MessageWithBodiesFilter.INSTANCE);
    }

    public static void setDefaultAutoReceiptMode(AutoReceiptMode autoReceiptMode2) {
        defaultAutoReceiptMode = autoReceiptMode2;
    }

    private DeliveryReceiptManager(XMPPConnection connection) {
        super(connection);
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature(DeliveryReceipt.NAMESPACE);
        connection.addAsyncStanzaListener(new StanzaListener() {
            public void processStanza(Stanza packet) throws NotConnectedException {
                DeliveryReceipt dr = DeliveryReceipt.from((Message) packet);
                for (ReceiptReceivedListener l : DeliveryReceiptManager.this.receiptReceivedListeners) {
                    l.onReceiptReceived(packet.getFrom(), packet.getTo(), dr.getId(), packet);
                }
            }
        }, MESSAGES_WITH_DELIVERY_RECEIPT);
        connection.addAsyncStanzaListener(new StanzaListener() {
            public void processStanza(Stanza packet) throws NotConnectedException, InterruptedException {
                Jid from = packet.getFrom();
                XMPPConnection connection = DeliveryReceiptManager.this.connection();
                int i = AnonymousClass5.$SwitchMap$org$jivesoftware$smackx$receipts$DeliveryReceiptManager$AutoReceiptMode[DeliveryReceiptManager.this.autoReceiptMode.ordinal()];
                if (i == 1) {
                    return;
                }
                if (i != 2 || Roster.getInstanceFor(connection).isSubscribedToMyPresence(from)) {
                    Message messageWithReceiptRequest = (Message) packet;
                    Message ack = DeliveryReceiptManager.receiptMessageFor(messageWithReceiptRequest);
                    if (ack == null) {
                        Logger access$300 = DeliveryReceiptManager.LOGGER;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Received message stanza with receipt request from '");
                        sb.append(from);
                        sb.append("' without a stanza ID set. Message: ");
                        sb.append(messageWithReceiptRequest);
                        access$300.warning(sb.toString());
                        return;
                    }
                    connection.sendStanza(ack);
                }
            }
        }, NON_ERROR_GROUPCHAT_MESSAGES_WITH_DELIVERY_RECEIPT_REQUEST);
    }

    public static synchronized DeliveryReceiptManager getInstanceFor(XMPPConnection connection) {
        DeliveryReceiptManager receiptManager;
        synchronized (DeliveryReceiptManager.class) {
            receiptManager = (DeliveryReceiptManager) instances.get(connection);
            if (receiptManager == null) {
                receiptManager = new DeliveryReceiptManager(connection);
                instances.put(connection, receiptManager);
            }
        }
        return receiptManager;
    }

    public boolean isSupported(Jid jid) throws SmackException, XMPPException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).supportsFeature(jid, DeliveryReceipt.NAMESPACE);
    }

    public void setAutoReceiptMode(AutoReceiptMode autoReceiptMode2) {
        this.autoReceiptMode = autoReceiptMode2;
    }

    public AutoReceiptMode getAutoReceiptMode() {
        return this.autoReceiptMode;
    }

    public void addReceiptReceivedListener(ReceiptReceivedListener listener) {
        this.receiptReceivedListeners.add(listener);
    }

    public void removeReceiptReceivedListener(ReceiptReceivedListener listener) {
        this.receiptReceivedListeners.remove(listener);
    }

    public void autoAddDeliveryReceiptRequests() {
        connection().addStanzaInterceptor(AUTO_ADD_DELIVERY_RECEIPT_REQUESTS_LISTENER, MESSAGES_TO_REQUEST_RECEIPTS_FOR);
    }

    public void dontAutoAddDeliveryReceiptRequests() {
        connection().removeStanzaInterceptor(AUTO_ADD_DELIVERY_RECEIPT_REQUESTS_LISTENER);
    }

    public static boolean hasDeliveryReceiptRequest(Message message) {
        return DeliveryReceiptRequest.from(message) != null;
    }

    @Deprecated
    public static String addDeliveryReceiptRequest(Message m) {
        return DeliveryReceiptRequest.addTo(m);
    }

    public static Message receiptMessageFor(Message messageWithReceiptRequest) {
        String stanzaId = messageWithReceiptRequest.getStanzaId();
        if (StringUtils.isNullOrEmpty((CharSequence) stanzaId)) {
            return null;
        }
        Message message = new Message(messageWithReceiptRequest.getFrom(), messageWithReceiptRequest.getType());
        message.addExtension(new DeliveryReceipt(stanzaId));
        return message;
    }
}
