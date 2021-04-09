package org.minidns;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.minidns.AbstractDnsClient.IpVersionSetting;
import org.minidns.MiniDnsException.ErrorResponseException;
import org.minidns.MiniDnsException.NoQueryPossibleException;
import org.minidns.MiniDnsFuture.InternalMiniDnsFuture;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsmessage.DnsMessage.Builder;
import org.minidns.dnsmessage.DnsMessage.RESPONSE_CODE;
import org.minidns.dnsqueryresult.DnsQueryResult;
import org.minidns.dnsserverlookup.AndroidUsingExec;
import org.minidns.dnsserverlookup.AndroidUsingReflection;
import org.minidns.dnsserverlookup.DnsServerLookupMechanism;
import org.minidns.dnsserverlookup.UnixUsingEtcResolvConf;
import org.minidns.util.CollectionsUtil;
import org.minidns.util.ExceptionCallback;
import org.minidns.util.InetAddressUtil;
import org.minidns.util.MultipleIoException;
import org.minidns.util.SuccessCallback;

public class DnsClient extends AbstractDnsClient {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    static final List<DnsServerLookupMechanism> LOOKUP_MECHANISMS = new CopyOnWriteArrayList();
    static final Set<Inet4Address> STATIC_IPV4_DNS_SERVERS = new CopyOnWriteArraySet();
    static final Set<Inet6Address> STATIC_IPV6_DNS_SERVERS = new CopyOnWriteArraySet();
    private static final Set<String> blacklistedDnsServers = Collections.newSetFromMap(new ConcurrentHashMap(4));
    private boolean askForDnssec = false;
    private boolean disableResultFilter = false;
    private final Set<InetAddress> nonRaServers = Collections.newSetFromMap(new ConcurrentHashMap(4));
    private boolean useHardcodedDnsServers = true;

    /* renamed from: org.minidns.DnsClient$3 reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting = new int[IpVersionSetting.values().length];
        static final /* synthetic */ int[] $SwitchMap$org$minidns$dnsmessage$DnsMessage$RESPONSE_CODE = new int[RESPONSE_CODE.values().length];

        static {
            try {
                $SwitchMap$org$minidns$dnsmessage$DnsMessage$RESPONSE_CODE[RESPONSE_CODE.NO_ERROR.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$minidns$dnsmessage$DnsMessage$RESPONSE_CODE[RESPONSE_CODE.NX_DOMAIN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[IpVersionSetting.v4v6.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[IpVersionSetting.v6v4.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[IpVersionSetting.v4only.ordinal()] = 3;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[IpVersionSetting.v6only.ordinal()] = 4;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    static {
        addDnsServerLookupMechanism(AndroidUsingExec.INSTANCE);
        addDnsServerLookupMechanism(AndroidUsingReflection.INSTANCE);
        addDnsServerLookupMechanism(UnixUsingEtcResolvConf.INSTANCE);
        try {
            STATIC_IPV4_DNS_SERVERS.add(InetAddressUtil.ipv4From("8.8.8.8"));
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Could not add static IPv4 DNS Server", e);
        }
        try {
            STATIC_IPV6_DNS_SERVERS.add(InetAddressUtil.ipv6From("[2001:4860:4860::8888]"));
        } catch (IllegalArgumentException e2) {
            LOGGER.log(Level.WARNING, "Could not add static IPv6 DNS Server", e2);
        }
    }

    public DnsClient() {
    }

    public DnsClient(DnsCache dnsCache) {
        super(dnsCache);
    }

    /* access modifiers changed from: protected */
    public Builder newQuestion(Builder message) {
        message.setRecursionDesired(true);
        message.getEdnsBuilder().setUdpPayloadSize(this.dataSource.getUdpPayloadSize()).setDnssecOk(this.askForDnssec);
        return message;
    }

    private List<InetAddress> getServerAddresses() {
        List<InetAddress> dnsServerAddresses = findDnsAddresses();
        InetAddress[] selectedHardcodedDnsServerAddresses = new InetAddress[2];
        if (this.useHardcodedDnsServers) {
            InetAddress primaryHardcodedDnsServer = null;
            InetAddress secondaryHardcodedDnsServer = null;
            int i = AnonymousClass3.$SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[this.ipVersionSetting.ordinal()];
            if (i == 1) {
                primaryHardcodedDnsServer = getRandomHardcodedIpv4DnsServer();
                secondaryHardcodedDnsServer = getRandomHarcodedIpv6DnsServer();
            } else if (i == 2) {
                primaryHardcodedDnsServer = getRandomHarcodedIpv6DnsServer();
                secondaryHardcodedDnsServer = getRandomHardcodedIpv4DnsServer();
            } else if (i == 3) {
                primaryHardcodedDnsServer = getRandomHardcodedIpv4DnsServer();
            } else if (i == 4) {
                primaryHardcodedDnsServer = getRandomHarcodedIpv6DnsServer();
            }
            selectedHardcodedDnsServerAddresses[0] = primaryHardcodedDnsServer;
            selectedHardcodedDnsServerAddresses[1] = secondaryHardcodedDnsServer;
        }
        for (InetAddress selectedHardcodedDnsServerAddress : selectedHardcodedDnsServerAddresses) {
            if (selectedHardcodedDnsServerAddress != null) {
                dnsServerAddresses.add(selectedHardcodedDnsServerAddress);
            }
        }
        return dnsServerAddresses;
    }

    public DnsQueryResult query(Builder queryBuilder) throws IOException {
        DnsMessage q = newQuestion(queryBuilder).build();
        DnsQueryResult dnsQueryResult = this.cache == null ? null : this.cache.get(q);
        if (dnsQueryResult != null) {
            return dnsQueryResult;
        }
        List<InetAddress> dnsServerAddresses = getServerAddresses();
        List<IOException> ioExceptions = new ArrayList<>(dnsServerAddresses.size());
        for (InetAddress dns : dnsServerAddresses) {
            if (this.nonRaServers.contains(dns)) {
                Logger logger = LOGGER;
                StringBuilder sb = new StringBuilder();
                sb.append("Skipping ");
                sb.append(dns);
                sb.append(" because it was marked as \"recursion not available\"");
                logger.finer(sb.toString());
            } else {
                try {
                    DnsQueryResult dnsQueryResult2 = query(q, dns);
                    DnsMessage responseMessage = dnsQueryResult2.response;
                    if (!responseMessage.recursionAvailable) {
                        if (this.nonRaServers.add(dns)) {
                            Logger logger2 = LOGGER;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("The DNS server ");
                            sb2.append(dns);
                            sb2.append(" returned a response without the \"recursion available\" (RA) flag set. This likely indicates a misconfiguration because the server is not suitable for DNS resolution");
                            logger2.warning(sb2.toString());
                        }
                    } else if (this.disableResultFilter) {
                        return dnsQueryResult2;
                    } else {
                        int i = AnonymousClass3.$SwitchMap$org$minidns$dnsmessage$DnsMessage$RESPONSE_CODE[responseMessage.responseCode.ordinal()];
                        if (i == 1 || i == 2) {
                            return dnsQueryResult2;
                        }
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("Response from ");
                        sb3.append(dns);
                        sb3.append(" asked for ");
                        sb3.append(q.getQuestion());
                        sb3.append(" with error code: ");
                        sb3.append(responseMessage.responseCode);
                        sb3.append('.');
                        String warning = sb3.toString();
                        if (!LOGGER.isLoggable(Level.FINE)) {
                            StringBuilder sb4 = new StringBuilder();
                            sb4.append(warning);
                            sb4.append("\n");
                            sb4.append(responseMessage);
                            warning = sb4.toString();
                        }
                        LOGGER.warning(warning);
                        ioExceptions.add(new ErrorResponseException(q, dnsQueryResult2));
                    }
                } catch (IOException ioe) {
                    ioExceptions.add(ioe);
                }
            }
        }
        MultipleIoException.throwIfRequired(ioExceptions);
        throw new NoQueryPossibleException(q);
    }

    /* access modifiers changed from: protected */
    public MiniDnsFuture<DnsQueryResult, IOException> queryAsync(Builder queryBuilder) {
        DnsMessage q = newQuestion(queryBuilder).build();
        DnsQueryResult responseMessage = this.cache == null ? null : this.cache.get(q);
        if (responseMessage != null) {
            return MiniDnsFuture.from(responseMessage);
        }
        final List<InetAddress> dnsServerAddresses = getServerAddresses();
        final InternalMiniDnsFuture<DnsQueryResult, IOException> future = new InternalMiniDnsFuture<>();
        final List<IOException> exceptions = Collections.synchronizedList(new ArrayList(dnsServerAddresses.size()));
        Iterator<InetAddress> it = dnsServerAddresses.iterator();
        while (it.hasNext()) {
            InetAddress dns = (InetAddress) it.next();
            if (this.nonRaServers.contains(dns)) {
                it.remove();
                Logger logger = LOGGER;
                StringBuilder sb = new StringBuilder();
                sb.append("Skipping ");
                sb.append(dns);
                sb.append(" because it was marked as \"recursion not available\"");
                logger.finer(sb.toString());
            }
        }
        List<MiniDnsFuture<DnsQueryResult, IOException>> futures = new ArrayList<>(dnsServerAddresses.size());
        Iterator it2 = dnsServerAddresses.iterator();
        while (true) {
            if (!it2.hasNext()) {
                break;
            }
            InetAddress dns2 = (InetAddress) it2.next();
            if (future.isDone()) {
                for (MiniDnsFuture<DnsQueryResult, IOException> futureToCancel : futures) {
                    futureToCancel.cancel(true);
                }
            } else {
                MiniDnsFuture<DnsQueryResult, IOException> f = queryAsync(q, dns2);
                f.onSuccess(new SuccessCallback<DnsQueryResult>() {
                    public void onSuccess(DnsQueryResult result) {
                        future.setResult(result);
                    }
                });
                f.onError(new ExceptionCallback<IOException>() {
                    public void processException(IOException exception) {
                        exceptions.add(exception);
                        if (exceptions.size() == dnsServerAddresses.size()) {
                            future.setException(MultipleIoException.toIOException(exceptions));
                        }
                    }
                });
                futures.add(f);
            }
        }
        return future;
    }

    public static List<String> findDNS() {
        String str;
        List<String> res = null;
        for (DnsServerLookupMechanism mechanism : LOOKUP_MECHANISMS) {
            res = mechanism.getDnsServerAddresses();
            if (res != null) {
                Iterator<String> it = res.iterator();
                while (true) {
                    str = "The DNS server lookup mechanism '";
                    if (!it.hasNext()) {
                        break;
                    }
                    String potentialDnsServer = (String) it.next();
                    String str2 = "'";
                    if (!InetAddressUtil.isIpAddress(potentialDnsServer)) {
                        Logger logger = LOGGER;
                        StringBuilder sb = new StringBuilder();
                        sb.append(str);
                        sb.append(mechanism.getName());
                        sb.append("' returned an invalid non-IP address result: '");
                        sb.append(potentialDnsServer);
                        sb.append(str2);
                        logger.warning(sb.toString());
                        it.remove();
                    } else if (blacklistedDnsServers.contains(potentialDnsServer)) {
                        Logger logger2 = LOGGER;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(str);
                        sb2.append(mechanism.getName());
                        sb2.append("' returned a blacklisted result: '");
                        sb2.append(potentialDnsServer);
                        sb2.append(str2);
                        logger2.fine(sb2.toString());
                        it.remove();
                    }
                }
                if (!res.isEmpty()) {
                    break;
                }
                Logger logger3 = LOGGER;
                StringBuilder sb3 = new StringBuilder();
                sb3.append(str);
                sb3.append(mechanism.getName());
                sb3.append("' returned not a single valid IP address after sanitazion");
                logger3.warning(sb3.toString());
            }
        }
        return res;
    }

    public static List<InetAddress> findDnsAddresses() {
        List<String> res = findDNS();
        if (res == null) {
            return new ArrayList();
        }
        IpVersionSetting setting = DEFAULT_IP_VERSION_SETTING;
        List<Inet4Address> ipv4DnsServer = null;
        List<Inet6Address> ipv6DnsServer = null;
        if (setting.v4) {
            ipv4DnsServer = new ArrayList<>(res.size());
        }
        if (setting.v6) {
            ipv6DnsServer = new ArrayList<>(res.size());
        }
        for (String dnsServerString : res) {
            try {
                InetAddress dnsServerAddress = InetAddress.getByName(dnsServerString);
                if (dnsServerAddress instanceof Inet4Address) {
                    if (setting.v4) {
                        ipv4DnsServer.add((Inet4Address) dnsServerAddress);
                    }
                } else if (!(dnsServerAddress instanceof Inet6Address)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("The address '");
                    sb.append(dnsServerAddress);
                    sb.append("' is neither of type Inet(4|6)Address");
                    throw new AssertionError(sb.toString());
                } else if (setting.v6) {
                    ipv6DnsServer.add((Inet6Address) dnsServerAddress);
                }
            } catch (UnknownHostException e) {
                Logger logger = LOGGER;
                Level level = Level.SEVERE;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Could not transform '");
                sb2.append(dnsServerString);
                sb2.append("' to InetAddress");
                logger.log(level, sb2.toString(), e);
            }
        }
        List<InetAddress> dnsServers = new LinkedList<>();
        int i = AnonymousClass3.$SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[setting.ordinal()];
        if (i == 1) {
            dnsServers.addAll(ipv4DnsServer);
            dnsServers.addAll(ipv6DnsServer);
        } else if (i == 2) {
            dnsServers.addAll(ipv6DnsServer);
            dnsServers.addAll(ipv4DnsServer);
        } else if (i == 3) {
            dnsServers.addAll(ipv4DnsServer);
        } else if (i == 4) {
            dnsServers.addAll(ipv6DnsServer);
        }
        return dnsServers;
    }

    public static void addDnsServerLookupMechanism(DnsServerLookupMechanism dnsServerLookup) {
        if (!dnsServerLookup.isAvailable()) {
            Logger logger = LOGGER;
            StringBuilder sb = new StringBuilder();
            sb.append("Not adding ");
            sb.append(dnsServerLookup.getName());
            sb.append(" as it is not available.");
            logger.fine(sb.toString());
            return;
        }
        synchronized (LOOKUP_MECHANISMS) {
            ArrayList<DnsServerLookupMechanism> tempList = new ArrayList<>(LOOKUP_MECHANISMS.size() + 1);
            tempList.addAll(LOOKUP_MECHANISMS);
            tempList.add(dnsServerLookup);
            Collections.sort(tempList);
            LOOKUP_MECHANISMS.clear();
            LOOKUP_MECHANISMS.addAll(tempList);
        }
    }

    public static boolean removeDNSServerLookupMechanism(DnsServerLookupMechanism dnsServerLookup) {
        boolean remove;
        synchronized (LOOKUP_MECHANISMS) {
            remove = LOOKUP_MECHANISMS.remove(dnsServerLookup);
        }
        return remove;
    }

    public static boolean addBlacklistedDnsServer(String dnsServer) {
        return blacklistedDnsServers.add(dnsServer);
    }

    public static boolean removeBlacklistedDnsServer(String dnsServer) {
        return blacklistedDnsServers.remove(dnsServer);
    }

    public boolean isAskForDnssec() {
        return this.askForDnssec;
    }

    public void setAskForDnssec(boolean askForDnssec2) {
        this.askForDnssec = askForDnssec2;
    }

    public boolean isDisableResultFilter() {
        return this.disableResultFilter;
    }

    public void setDisableResultFilter(boolean disableResultFilter2) {
        this.disableResultFilter = disableResultFilter2;
    }

    public boolean isUseHardcodedDnsServersEnabled() {
        return this.useHardcodedDnsServers;
    }

    public void setUseHardcodedDnsServers(boolean useHardcodedDnsServers2) {
        this.useHardcodedDnsServers = useHardcodedDnsServers2;
    }

    public InetAddress getRandomHardcodedIpv4DnsServer() {
        return (InetAddress) CollectionsUtil.getRandomFrom(STATIC_IPV4_DNS_SERVERS, this.insecureRandom);
    }

    public InetAddress getRandomHarcodedIpv6DnsServer() {
        return (InetAddress) CollectionsUtil.getRandomFrom(STATIC_IPV6_DNS_SERVERS, this.insecureRandom);
    }
}
