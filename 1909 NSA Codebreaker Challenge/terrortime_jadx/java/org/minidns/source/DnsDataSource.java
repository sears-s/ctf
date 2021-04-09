package org.minidns.source;

import java.io.IOException;
import java.net.InetAddress;
import org.minidns.MiniDnsFuture;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsqueryresult.DnsQueryResult;

public interface DnsDataSource {

    public interface OnResponseCallback {
        void onResponse(DnsMessage dnsMessage, DnsQueryResult dnsQueryResult);
    }

    int getTimeout();

    int getUdpPayloadSize();

    DnsQueryResult query(DnsMessage dnsMessage, InetAddress inetAddress, int i) throws IOException;

    MiniDnsFuture<DnsQueryResult, IOException> queryAsync(DnsMessage dnsMessage, InetAddress inetAddress, int i, OnResponseCallback onResponseCallback);

    void setTimeout(int i);
}
