package org.minidns.source;

import java.io.IOException;
import java.net.InetAddress;
import org.minidns.DnsCache;
import org.minidns.MiniDnsFuture;
import org.minidns.MiniDnsFuture.InternalMiniDnsFuture;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsqueryresult.DnsQueryResult;
import org.minidns.source.DnsDataSource.OnResponseCallback;

public abstract class AbstractDnsDataSource implements DnsDataSource {
    private DnsCache cache;
    private QueryMode queryMode = QueryMode.dontCare;
    protected int timeout = 5000;
    protected int udpPayloadSize = 1024;

    public enum QueryMode {
        dontCare,
        udpTcp,
        tcp
    }

    public abstract DnsQueryResult query(DnsMessage dnsMessage, InetAddress inetAddress, int i) throws IOException;

    public MiniDnsFuture<DnsQueryResult, IOException> queryAsync(DnsMessage message, InetAddress address, int port, OnResponseCallback onResponseCallback) {
        InternalMiniDnsFuture<DnsQueryResult, IOException> future = new InternalMiniDnsFuture<>();
        try {
            future.setResult(query(message, address, port));
            return future;
        } catch (IOException e) {
            future.setException(e);
            return future;
        }
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout2) {
        if (timeout2 > 0) {
            this.timeout = timeout2;
            return;
        }
        throw new IllegalArgumentException("Timeout must be greater than zero");
    }

    public int getUdpPayloadSize() {
        return this.udpPayloadSize;
    }

    public void setUdpPayloadSize(int udpPayloadSize2) {
        if (udpPayloadSize2 > 0) {
            this.udpPayloadSize = udpPayloadSize2;
            return;
        }
        throw new IllegalArgumentException("UDP payload size must be greater than zero");
    }

    /* access modifiers changed from: protected */
    public final void cacheResult(DnsMessage request, DnsQueryResult response) {
        DnsCache activeCache = this.cache;
        if (activeCache != null) {
            activeCache.put(request, response);
        }
    }

    public void setQueryMode(QueryMode queryMode2) {
        if (queryMode2 != null) {
            this.queryMode = queryMode2;
            return;
        }
        throw new IllegalArgumentException();
    }

    public QueryMode getQueryMode() {
        return this.queryMode;
    }
}
