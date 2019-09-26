package org.jivesoftware.smackx.bytestreams.socks5;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.packet.StanzaError.Builder;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smackx.bytestreams.BytestreamRequest;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.StreamHost;
import org.jxmpp.jid.Jid;
import org.jxmpp.util.cache.Cache;
import org.jxmpp.util.cache.ExpirationCache;
import org.minidns.dnsserverlookup.UnixUsingEtcResolvConf;

public class Socks5BytestreamRequest implements BytestreamRequest {
    private static final Cache<String, Integer> ADDRESS_BLACKLIST = new ExpirationCache(100, BLACKLIST_LIFETIME);
    private static final long BLACKLIST_LIFETIME = 7200000;
    private static final int BLACKLIST_MAX_SIZE = 100;
    private static int CONNECTION_FAILURE_THRESHOLD = 2;
    private Bytestream bytestreamRequest;
    private Socks5BytestreamManager manager;
    private int minimumConnectTimeout = UnixUsingEtcResolvConf.PRIORITY;
    private int totalConnectTimeout = 10000;

    public static int getConnectFailureThreshold() {
        return CONNECTION_FAILURE_THRESHOLD;
    }

    public static void setConnectFailureThreshold(int connectFailureThreshold) {
        CONNECTION_FAILURE_THRESHOLD = connectFailureThreshold;
    }

    protected Socks5BytestreamRequest(Socks5BytestreamManager manager2, Bytestream bytestreamRequest2) {
        this.manager = manager2;
        this.bytestreamRequest = bytestreamRequest2;
    }

    public int getTotalConnectTimeout() {
        int i = this.totalConnectTimeout;
        if (i <= 0) {
            return 10000;
        }
        return i;
    }

    public void setTotalConnectTimeout(int totalConnectTimeout2) {
        this.totalConnectTimeout = totalConnectTimeout2;
    }

    public int getMinimumConnectTimeout() {
        int i = this.minimumConnectTimeout;
        if (i <= 0) {
            return UnixUsingEtcResolvConf.PRIORITY;
        }
        return i;
    }

    public void setMinimumConnectTimeout(int minimumConnectTimeout2) {
        this.minimumConnectTimeout = minimumConnectTimeout2;
    }

    public Jid getFrom() {
        return this.bytestreamRequest.getFrom();
    }

    public String getSessionID() {
        return this.bytestreamRequest.getSessionID();
    }

    public Socks5BytestreamSession accept() throws InterruptedException, XMPPErrorException, SmackException {
        Collection<StreamHost> streamHosts = this.bytestreamRequest.getStreamHosts();
        if (streamHosts.size() == 0) {
            cancelRequest();
        }
        StreamHost selectedHost = null;
        Socket socket = null;
        String digest = Socks5Utils.createDigest(this.bytestreamRequest.getSessionID(), this.bytestreamRequest.getFrom(), this.manager.getConnection().getUser());
        int timeout = Math.max(getTotalConnectTimeout() / streamHosts.size(), getMinimumConnectTimeout());
        Iterator it = streamHosts.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            StreamHost streamHost = (StreamHost) it.next();
            StringBuilder sb = new StringBuilder();
            sb.append(streamHost.getAddress());
            sb.append(":");
            sb.append(streamHost.getPort());
            String address = sb.toString();
            int failures = getConnectionFailures(address);
            int i = CONNECTION_FAILURE_THRESHOLD;
            if (i <= 0 || failures < i) {
                try {
                    socket = new Socks5Client(streamHost, digest).getSocket(timeout);
                    selectedHost = streamHost;
                    break;
                } catch (IOException | TimeoutException | SmackException | XMPPException e) {
                    incrementConnectionFailures(address);
                }
            }
        }
        if (selectedHost == null || socket == null) {
            cancelRequest();
        }
        this.manager.getConnection().sendStanza(createUsedHostResponse(selectedHost));
        return new Socks5BytestreamSession(socket, selectedHost.getJID().equals((CharSequence) this.bytestreamRequest.getFrom()));
    }

    public void reject() throws NotConnectedException, InterruptedException {
        this.manager.replyRejectPacket(this.bytestreamRequest);
    }

    private void cancelRequest() throws XMPPErrorException, NotConnectedException, InterruptedException {
        Builder error = StanzaError.from(Condition.item_not_found, "Could not establish socket with any provided host");
        IQ errorIQ = IQ.createErrorResponse((IQ) this.bytestreamRequest, error);
        this.manager.getConnection().sendStanza(errorIQ);
        throw new XMPPErrorException(errorIQ, error.build());
    }

    private Bytestream createUsedHostResponse(StreamHost selectedHost) {
        Bytestream response = new Bytestream(this.bytestreamRequest.getSessionID());
        response.setTo(this.bytestreamRequest.getFrom());
        response.setType(Type.result);
        response.setStanzaId(this.bytestreamRequest.getStanzaId());
        response.setUsedHost(selectedHost.getJID());
        return response;
    }

    private static void incrementConnectionFailures(String address) {
        Integer count = (Integer) ADDRESS_BLACKLIST.lookup(address);
        Cache<String, Integer> cache = ADDRESS_BLACKLIST;
        int i = 1;
        if (count != null) {
            i = 1 + count.intValue();
        }
        cache.put(address, Integer.valueOf(i));
    }

    private static int getConnectionFailures(String address) {
        Integer count = (Integer) ADDRESS_BLACKLIST.lookup(address);
        if (count != null) {
            return count.intValue();
        }
        return 0;
    }
}
