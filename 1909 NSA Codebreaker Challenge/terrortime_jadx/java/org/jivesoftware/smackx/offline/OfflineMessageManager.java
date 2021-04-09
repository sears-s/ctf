package org.jivesoftware.smackx.offline;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaCollector;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverItems.Item;
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.xdata.Form;

public class OfflineMessageManager {
    private static final Logger LOGGER = Logger.getLogger(OfflineMessageManager.class.getName());
    private static final StanzaFilter PACKET_FILTER = new AndFilter(new StanzaExtensionFilter((ExtensionElement) new OfflineMessageInfo()), StanzaTypeFilter.MESSAGE);
    private static final String namespace = "http://jabber.org/protocol/offline";
    private final XMPPConnection connection;

    public OfflineMessageManager(XMPPConnection connection2) {
        this.connection = connection2;
    }

    public boolean supportsFlexibleRetrieval() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(this.connection).serverSupportsFeature("http://jabber.org/protocol/offline");
    }

    public int getMessageCount() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Form extendedInfo = Form.getFormFrom(ServiceDiscoveryManager.getInstanceFor(this.connection).discoverInfo(null, "http://jabber.org/protocol/offline"));
        if (extendedInfo != null) {
            return Integer.parseInt(extendedInfo.getField("number_of_messages").getFirstValue());
        }
        return 0;
    }

    public List<OfflineMessageHeader> getHeaders() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        List<OfflineMessageHeader> answer = new ArrayList<>();
        for (Item item : ServiceDiscoveryManager.getInstanceFor(this.connection).discoverItems(null, "http://jabber.org/protocol/offline").getItems()) {
            answer.add(new OfflineMessageHeader(item));
        }
        return answer;
    }

    public List<Message> getMessages(final List<String> nodes) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        List<Message> messages = new ArrayList<>(nodes.size());
        OfflineMessageRequest request = new OfflineMessageRequest();
        for (String node : nodes) {
            OfflineMessageRequest.Item item = new OfflineMessageRequest.Item(node);
            item.setAction("view");
            request.addItem(item);
        }
        StanzaFilter messageFilter = new AndFilter(PACKET_FILTER, new StanzaFilter() {
            public boolean accept(Stanza packet) {
                return nodes.contains(((OfflineMessageInfo) packet.getExtension(OfflineMessageRequest.ELEMENT, "http://jabber.org/protocol/offline")).getNode());
            }
        });
        int pendingNodes = nodes.size();
        StanzaCollector messageCollector = this.connection.createStanzaCollector(messageFilter);
        try {
            this.connection.createStanzaCollectorAndSend(request).nextResultOrThrow();
            do {
                Message message = (Message) messageCollector.nextResult();
                if (message != null) {
                    messages.add(message);
                    pendingNodes--;
                } else if (message == null && pendingNodes > 0) {
                    Logger logger = LOGGER;
                    Level level = Level.WARNING;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Did not receive all expected offline messages. ");
                    sb.append(pendingNodes);
                    sb.append(" are missing.");
                    logger.log(level, sb.toString());
                }
                if (message == null) {
                    break;
                }
            } while (pendingNodes > 0);
            return messages;
        } finally {
            messageCollector.cancel();
        }
    }

    public List<Message> getMessages() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        OfflineMessageRequest request = new OfflineMessageRequest();
        request.setFetch(true);
        StanzaCollector resultCollector = this.connection.createStanzaCollectorAndSend(request);
        StanzaCollector messageCollector = this.connection.createStanzaCollector(StanzaCollector.newConfiguration().setStanzaFilter(PACKET_FILTER).setCollectorToReset(resultCollector));
        try {
            resultCollector.nextResultOrThrow();
            messageCollector.cancel();
            List<Message> messages = new ArrayList<>(messageCollector.getCollectedCount());
            while (true) {
                Message message = (Message) messageCollector.pollResult();
                Message message2 = message;
                if (message == null) {
                    return messages;
                }
                messages.add(message2);
            }
        } finally {
            messageCollector.cancel();
        }
    }

    public void deleteMessages(List<String> nodes) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        OfflineMessageRequest request = new OfflineMessageRequest();
        request.setType(Type.set);
        for (String node : nodes) {
            OfflineMessageRequest.Item item = new OfflineMessageRequest.Item(node);
            item.setAction("remove");
            request.addItem(item);
        }
        this.connection.createStanzaCollectorAndSend(request).nextResultOrThrow();
    }

    public void deleteMessages() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        OfflineMessageRequest request = new OfflineMessageRequest();
        request.setType(Type.set);
        request.setPurge(true);
        this.connection.createStanzaCollectorAndSend(request).nextResultOrThrow();
    }
}
