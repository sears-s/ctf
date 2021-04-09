package org.jivesoftware.smack.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.ConnectionConfiguration.DnssecMode;
import org.jivesoftware.smack.util.dns.DNSResolver;
import org.jivesoftware.smack.util.dns.HostAddress;
import org.jivesoftware.smack.util.dns.SRVRecord;
import org.jivesoftware.smack.util.dns.SmackDaneProvider;
import org.minidns.dnsname.DnsName;

public class DNSUtil {
    private static final Logger LOGGER = Logger.getLogger(DNSUtil.class.getName());
    public static final String XMPP_CLIENT_DNS_SRV_PREFIX = "_xmpp-client._tcp";
    public static final String XMPP_SERVER_DNS_SRV_PREFIX = "_xmpp-server._tcp";
    private static SmackDaneProvider daneProvider;
    private static DNSResolver dnsResolver = null;

    /* renamed from: org.jivesoftware.smack.util.DNSUtil$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$util$DNSUtil$DomainType = new int[DomainType.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$util$DNSUtil$DomainType[DomainType.client.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$util$DNSUtil$DomainType[DomainType.server.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    enum DomainType {
        server(DNSUtil.XMPP_SERVER_DNS_SRV_PREFIX),
        client(DNSUtil.XMPP_CLIENT_DNS_SRV_PREFIX);
        
        public final DnsName srvPrefix;

        private DomainType(String srvPrefixString) {
            this.srvPrefix = DnsName.from(srvPrefixString);
        }
    }

    public static void setDNSResolver(DNSResolver resolver) {
        dnsResolver = (DNSResolver) Objects.requireNonNull(resolver);
    }

    public static DNSResolver getDNSResolver() {
        return dnsResolver;
    }

    public static void setDaneProvider(SmackDaneProvider daneProvider2) {
        daneProvider = (SmackDaneProvider) Objects.requireNonNull(daneProvider2);
    }

    public static SmackDaneProvider getDaneProvider() {
        return daneProvider;
    }

    public static List<HostAddress> resolveXMPPServiceDomain(DnsName domain, List<HostAddress> failedAddresses, DnssecMode dnssecMode) {
        return resolveDomain(domain, DomainType.client, failedAddresses, dnssecMode);
    }

    public static List<HostAddress> resolveXMPPServerDomain(DnsName domain, List<HostAddress> failedAddresses, DnssecMode dnssecMode) {
        return resolveDomain(domain, DomainType.server, failedAddresses, dnssecMode);
    }

    private static List<HostAddress> resolveDomain(DnsName domain, DomainType domainType, List<HostAddress> failedAddresses, DnssecMode dnssecMode) {
        if (dnsResolver != null) {
            List<HostAddress> addresses = new ArrayList<>();
            DnsName srvDomain = DnsName.from(domainType.srvPrefix, domain);
            List<SRVRecord> srvRecords = dnsResolver.lookupSRVRecords(srvDomain, failedAddresses, dnssecMode);
            if (srvRecords == null || srvRecords.isEmpty()) {
                Logger logger = LOGGER;
                StringBuilder sb = new StringBuilder();
                sb.append("Could not resolve DNS SRV resource records for ");
                sb.append(srvDomain);
                sb.append(". Consider adding those.");
                logger.info(sb.toString());
            } else {
                if (LOGGER.isLoggable(Level.FINE)) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Resolved SRV RR for ");
                    sb2.append(srvDomain);
                    sb2.append(":");
                    String logMessage = sb2.toString();
                    for (SRVRecord r : srvRecords) {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append(logMessage);
                        sb3.append(" ");
                        sb3.append(r);
                        logMessage = sb3.toString();
                    }
                    LOGGER.fine(logMessage);
                }
                addresses.addAll(sortSRVRecords(srvRecords));
            }
            int defaultPort = -1;
            int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$util$DNSUtil$DomainType[domainType.ordinal()];
            if (i == 1) {
                defaultPort = 5222;
            } else if (i == 2) {
                defaultPort = 5269;
            }
            HostAddress hostAddress = dnsResolver.lookupHostAddress(domain, defaultPort, failedAddresses, dnssecMode);
            if (hostAddress != null) {
                addresses.add(hostAddress);
            }
            return addresses;
        }
        throw new IllegalStateException("No DNS Resolver active in Smack");
    }

    private static List<HostAddress> sortSRVRecords(List<SRVRecord> records) {
        int selectedPos;
        if (records.size() == 1 && ((SRVRecord) records.get(0)).getFQDN().isRootLabel()) {
            return Collections.emptyList();
        }
        Collections.sort(records);
        SortedMap<Integer, List<SRVRecord>> buckets = new TreeMap<>();
        for (SRVRecord r : records) {
            Integer priority = Integer.valueOf(r.getPriority());
            List list = (List) buckets.get(priority);
            if (list == null) {
                list = new LinkedList();
                buckets.put(priority, list);
            }
            list.add(r);
        }
        List<HostAddress> res = new ArrayList<>(records.size());
        for (Integer priority2 : buckets.keySet()) {
            List<SRVRecord> bucket = (List) buckets.get(priority2);
            while (true) {
                int size = bucket.size();
                int bucketSize = size;
                if (size > 0) {
                    int[] totals = new int[bucketSize];
                    int running_total = 0;
                    int count = 0;
                    int zeroWeight = 1;
                    Iterator it = bucket.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            if (((SRVRecord) it.next()).getWeight() > 0) {
                                zeroWeight = 0;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    for (SRVRecord r2 : bucket) {
                        running_total += r2.getWeight() + zeroWeight;
                        totals[count] = running_total;
                        count++;
                    }
                    if (running_total == 0) {
                        selectedPos = (int) (Math.random() * ((double) bucketSize));
                    } else {
                        selectedPos = bisect(totals, Math.random() * ((double) running_total));
                    }
                    res.add((SRVRecord) bucket.remove(selectedPos));
                }
            }
        }
        return res;
    }

    private static int bisect(int[] array, double value) {
        int pos = 0;
        int length = array.length;
        int i = 0;
        while (i < length && value >= ((double) array[i])) {
            pos++;
            i++;
        }
        return pos;
    }
}
