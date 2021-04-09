package org.minidns.dnssec;

import java.util.Collections;
import java.util.Set;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsqueryresult.DnsQueryResult;
import org.minidns.record.RRSIG;
import org.minidns.record.Record;

public class DnssecQueryResult {
    public final DnsQueryResult dnsQueryResult;
    private final Set<DnssecUnverifiedReason> dnssecUnverifiedReasons;
    private final Set<Record<RRSIG>> signatures;
    public final DnsMessage synthesizedResponse;

    DnssecQueryResult(DnsMessage synthesizedResponse2, DnsQueryResult dnsQueryResult2, Set<Record<RRSIG>> signatures2, Set<DnssecUnverifiedReason> dnssecUnverifiedReasons2) {
        this.synthesizedResponse = synthesizedResponse2;
        this.dnsQueryResult = dnsQueryResult2;
        this.signatures = Collections.unmodifiableSet(signatures2);
        if (dnssecUnverifiedReasons2 == null) {
            this.dnssecUnverifiedReasons = Collections.emptySet();
        } else {
            this.dnssecUnverifiedReasons = Collections.unmodifiableSet(dnssecUnverifiedReasons2);
        }
    }

    public boolean isAuthenticData() {
        return this.dnssecUnverifiedReasons.isEmpty();
    }

    public Set<Record<RRSIG>> getSignatures() {
        return this.signatures;
    }

    public Set<DnssecUnverifiedReason> getUnverifiedReasons() {
        return this.dnssecUnverifiedReasons;
    }
}
