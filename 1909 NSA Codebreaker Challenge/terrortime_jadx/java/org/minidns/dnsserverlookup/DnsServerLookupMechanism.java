package org.minidns.dnsserverlookup;

import java.util.List;

public interface DnsServerLookupMechanism extends Comparable<DnsServerLookupMechanism> {
    List<String> getDnsServerAddresses();

    String getName();

    int getPriority();

    boolean isAvailable();
}
