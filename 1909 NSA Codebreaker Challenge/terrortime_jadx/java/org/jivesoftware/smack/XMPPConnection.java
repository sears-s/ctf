package org.jivesoftware.smack;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaCollector.Configuration;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.iqrequest.IQRequestHandler;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Nonza;
import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityFullJid;

public interface XMPPConnection {

    public enum FromMode {
        UNCHANGED,
        OMITTED,
        USER
    }

    void addAsyncStanzaListener(StanzaListener stanzaListener, StanzaFilter stanzaFilter);

    void addConnectionListener(ConnectionListener connectionListener);

    void addOneTimeSyncCallback(StanzaListener stanzaListener, StanzaFilter stanzaFilter);

    @Deprecated
    void addPacketInterceptor(StanzaListener stanzaListener, StanzaFilter stanzaFilter);

    @Deprecated
    void addPacketSendingListener(StanzaListener stanzaListener, StanzaFilter stanzaFilter);

    void addStanzaInterceptor(StanzaListener stanzaListener, StanzaFilter stanzaFilter);

    void addStanzaSendingListener(StanzaListener stanzaListener, StanzaFilter stanzaFilter);

    void addSyncStanzaListener(StanzaListener stanzaListener, StanzaFilter stanzaFilter);

    StanzaCollector createStanzaCollector(Configuration configuration);

    StanzaCollector createStanzaCollector(StanzaFilter stanzaFilter);

    StanzaCollector createStanzaCollectorAndSend(StanzaFilter stanzaFilter, Stanza stanza) throws NotConnectedException, InterruptedException;

    StanzaCollector createStanzaCollectorAndSend(IQ iq) throws NotConnectedException, InterruptedException;

    int getConnectionCounter();

    <F extends ExtensionElement> F getFeature(String str, String str2);

    FromMode getFromMode();

    String getHost();

    long getLastStanzaReceived();

    int getPort();

    long getReplyTimeout();

    String getStreamId();

    EntityFullJid getUser();

    DomainBareJid getXMPPServiceDomain();

    boolean hasFeature(String str, String str2);

    boolean isAnonymous();

    boolean isAuthenticated();

    boolean isConnected();

    boolean isSecureConnection();

    boolean isUsingCompression();

    IQRequestHandler registerIQRequestHandler(IQRequestHandler iQRequestHandler);

    boolean removeAsyncStanzaListener(StanzaListener stanzaListener);

    void removeConnectionListener(ConnectionListener connectionListener);

    @Deprecated
    void removePacketInterceptor(StanzaListener stanzaListener);

    @Deprecated
    void removePacketSendingListener(StanzaListener stanzaListener);

    void removeStanzaCollector(StanzaCollector stanzaCollector);

    void removeStanzaInterceptor(StanzaListener stanzaListener);

    void removeStanzaSendingListener(StanzaListener stanzaListener);

    boolean removeSyncStanzaListener(StanzaListener stanzaListener);

    <S extends Stanza> SmackFuture<S, Exception> sendAsync(S s, StanzaFilter stanzaFilter);

    <S extends Stanza> SmackFuture<S, Exception> sendAsync(S s, StanzaFilter stanzaFilter, long j);

    <I extends IQ> I sendIqRequestAndWaitForResponse(IQ iq) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException;

    SmackFuture<IQ, Exception> sendIqRequestAsync(IQ iq);

    SmackFuture<IQ, Exception> sendIqRequestAsync(IQ iq, long j);

    @Deprecated
    void sendIqWithResponseCallback(IQ iq, StanzaListener stanzaListener) throws NotConnectedException, InterruptedException;

    @Deprecated
    void sendIqWithResponseCallback(IQ iq, StanzaListener stanzaListener, ExceptionCallback exceptionCallback) throws NotConnectedException, InterruptedException;

    @Deprecated
    void sendIqWithResponseCallback(IQ iq, StanzaListener stanzaListener, ExceptionCallback exceptionCallback, long j) throws NotConnectedException, InterruptedException;

    void sendNonza(Nonza nonza) throws NotConnectedException, InterruptedException;

    void sendStanza(Stanza stanza) throws NotConnectedException, InterruptedException;

    @Deprecated
    void sendStanzaWithResponseCallback(Stanza stanza, StanzaFilter stanzaFilter, StanzaListener stanzaListener) throws NotConnectedException, InterruptedException;

    @Deprecated
    void sendStanzaWithResponseCallback(Stanza stanza, StanzaFilter stanzaFilter, StanzaListener stanzaListener, ExceptionCallback exceptionCallback) throws NotConnectedException, InterruptedException;

    @Deprecated
    void sendStanzaWithResponseCallback(Stanza stanza, StanzaFilter stanzaFilter, StanzaListener stanzaListener, ExceptionCallback exceptionCallback, long j) throws NotConnectedException, InterruptedException;

    void setFromMode(FromMode fromMode);

    void setReplyTimeout(long j);

    IQRequestHandler unregisterIQRequestHandler(String str, String str2, Type type);

    IQRequestHandler unregisterIQRequestHandler(IQRequestHandler iQRequestHandler);
}
