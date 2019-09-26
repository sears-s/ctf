package org.jivesoftware.smack.util.dns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.ConnectionConfiguration.DnssecMode;
import org.minidns.dnsname.DnsName;

public abstract class DNSResolver {
    protected static final Logger LOGGER = Logger.getLogger(DNSResolver.class.getName());
    private final boolean supportsDnssec;

    /* access modifiers changed from: protected */
    public abstract List<SRVRecord> lookupSRVRecords0(DnsName dnsName, List<HostAddress> list, DnssecMode dnssecMode);

    protected DNSResolver(boolean supportsDnssec2) {
        this.supportsDnssec = supportsDnssec2;
    }

    public final List<SRVRecord> lookupSRVRecords(DnsName name, List<HostAddress> failedAddresses, DnssecMode dnssecMode) {
        checkIfDnssecRequestedAndSupported(dnssecMode);
        return lookupSRVRecords0(name, failedAddresses, dnssecMode);
    }

    public final HostAddress lookupHostAddress(DnsName name, int port, List<HostAddress> failedAddresses, DnssecMode dnssecMode) {
        checkIfDnssecRequestedAndSupported(dnssecMode);
        List<InetAddress> inetAddresses = lookupHostAddress0(name, failedAddresses, dnssecMode);
        if (inetAddresses == null || inetAddresses.isEmpty()) {
            return null;
        }
        return new HostAddress(name, port, inetAddresses);
    }

    /* access modifiers changed from: protected */
    public List<InetAddress> lookupHostAddress0(DnsName name, List<HostAddress> failedAddresses, DnssecMode dnssecMode) {
        if (dnssecMode == DnssecMode.disabled) {
            try {
                return Arrays.asList(InetAddress.getAllByName(name.toString()));
            } catch (UnknownHostException e) {
                failedAddresses.add(new HostAddress(name, (Exception) e));
                return null;
            }
        } else {
            throw new UnsupportedOperationException("This resolver does not support DNSSEC");
        }
    }

    /* access modifiers changed from: protected */
    public final boolean shouldContinue(CharSequence name, CharSequence hostname, List<InetAddress> hostAddresses) {
        if (hostAddresses == null) {
            return true;
        }
        if (!hostAddresses.isEmpty()) {
            return false;
        }
        Logger logger = LOGGER;
        Level level = Level.INFO;
        StringBuilder sb = new StringBuilder();
        sb.append("The DNS name ");
        sb.append(name);
        sb.append(", points to a hostname (");
        sb.append(hostname);
        sb.append(") which has neither A or AAAA resource records. This is an indication of a broken DNS setup.");
        logger.log(level, sb.toString());
        return true;
    }

    private void checkIfDnssecRequestedAndSupported(DnssecMode dnssecMode) {
        if (dnssecMode != DnssecMode.disabled && !this.supportsDnssec) {
            throw new UnsupportedOperationException("This resolver does not support DNSSEC");
        }
    }
}
