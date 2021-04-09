package org.minidns.dnsserverlookup;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractDnsServerLookupMechanism implements DnsServerLookupMechanism {
    protected static final Logger LOGGER = Logger.getLogger(AbstractDnsServerLookupMechanism.class.getName());
    private final String name;
    private final int priority;

    public abstract List<String> getDnsServerAddresses();

    protected AbstractDnsServerLookupMechanism(String name2, int priority2) {
        this.name = name2;
        this.priority = priority2;
    }

    public final String getName() {
        return this.name;
    }

    public final int getPriority() {
        return this.priority;
    }

    public final int compareTo(DnsServerLookupMechanism other) {
        return getPriority() - other.getPriority();
    }

    protected static List<String> toListOfStrings(Collection<? extends InetAddress> inetAddresses) {
        List<String> result = new ArrayList<>(inetAddresses.size());
        for (InetAddress inetAddress : inetAddresses) {
            result.add(inetAddress.getHostAddress());
        }
        return result;
    }
}
