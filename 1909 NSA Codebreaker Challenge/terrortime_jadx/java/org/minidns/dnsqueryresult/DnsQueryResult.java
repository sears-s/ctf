package org.minidns.dnsqueryresult;

import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsmessage.DnsMessage.RESPONSE_CODE;

public abstract class DnsQueryResult {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public final DnsMessage query;
    public final QueryMethod queryMethod;
    public final DnsMessage response;

    public enum QueryMethod {
        udp,
        tcp,
        asyncUdp,
        asyncTcp,
        cachedDirect,
        cachedSynthesized,
        testWorld
    }

    protected DnsQueryResult(QueryMethod queryMethod2, DnsMessage query2, DnsMessage response2) {
        this.queryMethod = queryMethod2;
        this.query = query2;
        this.response = response2;
    }

    public String toString() {
        return this.response.toString();
    }

    public boolean wasSuccessful() {
        return this.response.responseCode == RESPONSE_CODE.NO_ERROR;
    }
}
