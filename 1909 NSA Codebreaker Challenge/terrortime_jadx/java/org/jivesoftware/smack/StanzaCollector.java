package org.jivesoftware.smack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;

public class StanzaCollector {
    private volatile boolean cancelled = false;
    private List<Stanza> collectedCache;
    private final StanzaCollector collectorToReset;
    private final XMPPConnection connection;
    private final StanzaFilter packetFilter;
    private final Stanza request;
    private final ArrayBlockingQueue<Stanza> resultQueue;
    private volatile long waitStart;

    public static final class Configuration {
        /* access modifiers changed from: private */
        public StanzaCollector collectorToReset;
        /* access modifiers changed from: private */
        public StanzaFilter packetFilter;
        /* access modifiers changed from: private */
        public Stanza request;
        /* access modifiers changed from: private */
        public int size;

        private Configuration() {
            this.size = SmackConfiguration.getStanzaCollectorSize();
        }

        @Deprecated
        public Configuration setPacketFilter(StanzaFilter packetFilter2) {
            return setStanzaFilter(packetFilter2);
        }

        public Configuration setStanzaFilter(StanzaFilter stanzaFilter) {
            this.packetFilter = stanzaFilter;
            return this;
        }

        public Configuration setSize(int size2) {
            this.size = size2;
            return this;
        }

        public Configuration setCollectorToReset(StanzaCollector collector) {
            this.collectorToReset = collector;
            return this;
        }

        public Configuration setRequest(Stanza request2) {
            this.request = request2;
            return this;
        }
    }

    protected StanzaCollector(XMPPConnection connection2, Configuration configuration) {
        this.connection = connection2;
        this.packetFilter = configuration.packetFilter;
        this.resultQueue = new ArrayBlockingQueue<>(configuration.size);
        this.collectorToReset = configuration.collectorToReset;
        this.request = configuration.request;
    }

    public void cancel() {
        if (!this.cancelled) {
            this.cancelled = true;
            this.connection.removeStanzaCollector(this);
        }
    }

    @Deprecated
    public StanzaFilter getPacketFilter() {
        return getStanzaFilter();
    }

    public StanzaFilter getStanzaFilter() {
        return this.packetFilter;
    }

    public <P extends Stanza> P pollResult() {
        return (Stanza) this.resultQueue.poll();
    }

    public <P extends Stanza> P pollResultOrThrow() throws XMPPErrorException {
        P result = pollResult();
        if (result != null) {
            XMPPErrorException.ifHasErrorThenThrow(result);
        }
        return result;
    }

    public <P extends Stanza> P nextResultBlockForever() throws InterruptedException {
        throwIfCancelled();
        P res = null;
        while (res == null) {
            res = (Stanza) this.resultQueue.take();
        }
        return res;
    }

    public <P extends Stanza> P nextResult() throws InterruptedException {
        return nextResult(this.connection.getReplyTimeout());
    }

    public <P extends Stanza> P nextResult(long timeout) throws InterruptedException {
        throwIfCancelled();
        long remainingWait = timeout;
        this.waitStart = System.currentTimeMillis();
        do {
            P res = (Stanza) this.resultQueue.poll(remainingWait, TimeUnit.MILLISECONDS);
            if (res != null) {
                return res;
            }
            remainingWait = timeout - (System.currentTimeMillis() - this.waitStart);
        } while (remainingWait > 0);
        return null;
    }

    public <P extends Stanza> P nextResultOrThrow() throws NoResponseException, XMPPErrorException, InterruptedException, NotConnectedException {
        return nextResultOrThrow(this.connection.getReplyTimeout());
    }

    public <P extends Stanza> P nextResultOrThrow(long timeout) throws NoResponseException, XMPPErrorException, InterruptedException, NotConnectedException {
        P result = nextResult(timeout);
        cancel();
        if (result != null) {
            XMPPErrorException.ifHasErrorThenThrow(result);
            return result;
        } else if (!this.connection.isConnected()) {
            throw new NotConnectedException(this.connection, this.packetFilter);
        } else {
            throw NoResponseException.newWith(timeout, this);
        }
    }

    public List<Stanza> getCollectedStanzasAfterCancelled() {
        if (this.cancelled) {
            if (this.collectedCache == null) {
                this.collectedCache = new ArrayList(getCollectedCount());
                this.resultQueue.drainTo(this.collectedCache);
            }
            return this.collectedCache;
        }
        throw new IllegalStateException("Stanza collector was not yet cancelled");
    }

    public int getCollectedCount() {
        return this.resultQueue.size();
    }

    /* access modifiers changed from: protected */
    public void processStanza(Stanza packet) {
        StanzaFilter stanzaFilter = this.packetFilter;
        if (stanzaFilter == null || stanzaFilter.accept(packet)) {
            while (!this.resultQueue.offer(packet)) {
                this.resultQueue.poll();
            }
            StanzaCollector stanzaCollector = this.collectorToReset;
            if (stanzaCollector != null) {
                stanzaCollector.waitStart = System.currentTimeMillis();
            }
        }
    }

    private void throwIfCancelled() {
        if (this.cancelled) {
            throw new IllegalStateException("Stanza collector already cancelled");
        }
    }

    public static Configuration newConfiguration() {
        return new Configuration();
    }
}
