package org.minidns.source;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import org.minidns.AbstractDnsClient;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsqueryresult.StandardDnsQueryResult;

public class NetworkDataSourceWithAccounting extends NetworkDataSource {
    /* access modifiers changed from: private */
    public final AtomicInteger failedQueries = new AtomicInteger();
    /* access modifiers changed from: private */
    public final AtomicInteger failedTcpQueries = new AtomicInteger();
    /* access modifiers changed from: private */
    public final AtomicInteger failedUdpQueries = new AtomicInteger();
    /* access modifiers changed from: private */
    public final AtomicInteger responseSize = new AtomicInteger();
    /* access modifiers changed from: private */
    public final AtomicInteger successfulQueries = new AtomicInteger();
    /* access modifiers changed from: private */
    public final AtomicInteger successfulTcpQueries = new AtomicInteger();
    /* access modifiers changed from: private */
    public final AtomicInteger successfulUdpQueries = new AtomicInteger();
    /* access modifiers changed from: private */
    public final AtomicInteger tcpResponseSize = new AtomicInteger();
    /* access modifiers changed from: private */
    public final AtomicInteger udpResponseSize = new AtomicInteger();

    public static class Stats {
        public final int averageResponseSize;
        public final int averageTcpResponseSize;
        public final int averageUdpResponseSize;
        public final int failedQueries;
        public final int failedTcpQueries;
        public final int failedUdpQueries;
        public final int responseSize;
        private String stringCache;
        public final int successfulQueries;
        public final int successfulTcpQueries;
        public final int successfulUdpQueries;
        public final int tcpResponseSize;
        public final int udpResponseSize;

        private Stats(NetworkDataSourceWithAccounting ndswa) {
            this.successfulQueries = ndswa.successfulQueries.get();
            this.responseSize = ndswa.responseSize.get();
            this.failedQueries = ndswa.failedQueries.get();
            this.successfulUdpQueries = ndswa.successfulUdpQueries.get();
            this.udpResponseSize = ndswa.udpResponseSize.get();
            this.failedUdpQueries = ndswa.failedUdpQueries.get();
            this.successfulTcpQueries = ndswa.successfulTcpQueries.get();
            this.tcpResponseSize = ndswa.tcpResponseSize.get();
            this.failedTcpQueries = ndswa.failedTcpQueries.get();
            int i = this.successfulQueries;
            int i2 = 0;
            this.averageResponseSize = i > 0 ? this.responseSize / i : 0;
            int i3 = this.successfulUdpQueries;
            this.averageUdpResponseSize = i3 > 0 ? this.udpResponseSize / i3 : 0;
            int i4 = this.successfulTcpQueries;
            if (i4 > 0) {
                i2 = this.tcpResponseSize / i4;
            }
            this.averageTcpResponseSize = i2;
        }

        public String toString() {
            String str = this.stringCache;
            if (str != null) {
                return str;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Stats\t");
            sb.append("# Successful");
            sb.append(9);
            sb.append("# Failed");
            sb.append(9);
            sb.append("Resp. Size");
            sb.append(9);
            sb.append("Avg. Resp. Size");
            sb.append(10);
            sb.append("Total\t");
            sb.append(toString(this.successfulQueries));
            sb.append(9);
            sb.append(toString(this.failedQueries));
            sb.append(9);
            sb.append(toString(this.responseSize));
            sb.append(9);
            sb.append(toString(this.averageResponseSize));
            sb.append(10);
            sb.append("UDP\t");
            sb.append(toString(this.successfulUdpQueries));
            sb.append(9);
            sb.append(toString(this.failedUdpQueries));
            sb.append(9);
            sb.append(toString(this.udpResponseSize));
            sb.append(9);
            sb.append(toString(this.averageUdpResponseSize));
            sb.append(10);
            sb.append("TCP\t");
            sb.append(toString(this.successfulTcpQueries));
            sb.append(9);
            sb.append(toString(this.failedTcpQueries));
            sb.append(9);
            sb.append(toString(this.tcpResponseSize));
            sb.append(9);
            sb.append(toString(this.averageTcpResponseSize));
            sb.append(10);
            this.stringCache = sb.toString();
            return this.stringCache;
        }

        private static String toString(int i) {
            return String.format(Locale.US, "%,09d", new Object[]{Integer.valueOf(i)});
        }
    }

    public StandardDnsQueryResult query(DnsMessage message, InetAddress address, int port) throws IOException {
        try {
            StandardDnsQueryResult response = super.query(message, address, port);
            this.successfulQueries.incrementAndGet();
            this.responseSize.addAndGet(response.response.toArray().length);
            return response;
        } catch (IOException e) {
            this.failedQueries.incrementAndGet();
            throw e;
        }
    }

    /* access modifiers changed from: protected */
    public DnsMessage queryUdp(DnsMessage message, InetAddress address, int port) throws IOException {
        try {
            DnsMessage response = super.queryUdp(message, address, port);
            this.successfulUdpQueries.incrementAndGet();
            this.udpResponseSize.addAndGet(response.toArray().length);
            return response;
        } catch (IOException e) {
            this.failedUdpQueries.incrementAndGet();
            throw e;
        }
    }

    /* access modifiers changed from: protected */
    public DnsMessage queryTcp(DnsMessage message, InetAddress address, int port) throws IOException {
        try {
            DnsMessage response = super.queryTcp(message, address, port);
            this.successfulTcpQueries.incrementAndGet();
            this.tcpResponseSize.addAndGet(response.toArray().length);
            return response;
        } catch (IOException e) {
            this.failedTcpQueries.incrementAndGet();
            throw e;
        }
    }

    public Stats getStats() {
        return new Stats();
    }

    public static NetworkDataSourceWithAccounting from(AbstractDnsClient client) {
        DnsDataSource ds = client.getDataSource();
        if (ds instanceof NetworkDataSourceWithAccounting) {
            return (NetworkDataSourceWithAccounting) ds;
        }
        return null;
    }
}
