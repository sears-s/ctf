package org.jivesoftware.smack.util.dns.minidns;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.jivesoftware.smack.ConnectionConfiguration.DnssecMode;
import org.jivesoftware.smack.initializer.SmackInitializer;
import org.jivesoftware.smack.util.DNSUtil;
import org.jivesoftware.smack.util.dns.DNSResolver;
import org.jivesoftware.smack.util.dns.HostAddress;
import org.jivesoftware.smack.util.dns.SRVRecord;
import org.minidns.dnsname.DnsName;
import org.minidns.dnssec.UnverifiedReason;
import org.minidns.hla.DnssecResolverApi;
import org.minidns.hla.ResolutionUnsuccessfulException;
import org.minidns.hla.ResolverApi;
import org.minidns.hla.ResolverResult;
import org.minidns.hla.SrvResolverResult;
import org.minidns.record.A;
import org.minidns.record.AAAA;
import org.minidns.record.SRV;

public class MiniDnsResolver extends DNSResolver implements SmackInitializer {
    private static final ResolverApi DNSSEC_RESOLVER = DnssecResolverApi.INSTANCE;
    private static final MiniDnsResolver INSTANCE = new MiniDnsResolver();
    private static final ResolverApi NON_DNSSEC_RESOLVER = ResolverApi.INSTANCE;

    /* renamed from: org.jivesoftware.smack.util.dns.minidns.MiniDnsResolver$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$ConnectionConfiguration$DnssecMode = new int[DnssecMode.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$ConnectionConfiguration$DnssecMode[DnssecMode.needsDnssec.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$ConnectionConfiguration$DnssecMode[DnssecMode.needsDnssecAndDane.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$ConnectionConfiguration$DnssecMode[DnssecMode.disabled.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public static DNSResolver getInstance() {
        return INSTANCE;
    }

    public MiniDnsResolver() {
        super(true);
    }

    /* access modifiers changed from: protected */
    public List<SRVRecord> lookupSRVRecords0(DnsName name, List<HostAddress> failedAddresses, DnssecMode dnssecMode) {
        DnsName dnsName = name;
        List<HostAddress> list = failedAddresses;
        DnssecMode dnssecMode2 = dnssecMode;
        try {
            SrvResolverResult result = getResolver(dnssecMode).resolveSrv(dnsName);
            ResolutionUnsuccessfulException resolutionUnsuccessfulException = result.getResolutionUnsuccessfulException();
            if (resolutionUnsuccessfulException != null) {
                list.add(new HostAddress(dnsName, (Exception) resolutionUnsuccessfulException));
                return null;
            } else if (shouldAbortIfNotAuthentic(dnsName, dnssecMode2, result, list)) {
                return null;
            } else {
                List<SRVRecord> res = new LinkedList<>();
                for (SRV srv : result.getAnswers()) {
                    DnsName hostname = srv.target;
                    List<InetAddress> hostAddresses = lookupHostAddress0(hostname, list, dnssecMode2);
                    if (!shouldContinue(dnsName, hostname, hostAddresses)) {
                        int i = srv.port;
                        int i2 = srv.priority;
                        List<InetAddress> hostAddresses2 = hostAddresses;
                        int i3 = srv.weight;
                        DnsName dnsName2 = hostname;
                        SRVRecord srvRecord = new SRVRecord(hostname, i, i2, i3, hostAddresses2);
                        res.add(srvRecord);
                    }
                }
                return res;
            }
        } catch (IOException e) {
            list.add(new HostAddress(dnsName, (Exception) e));
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public List<InetAddress> lookupHostAddress0(DnsName name, List<HostAddress> failedAddresses, DnssecMode dnssecMode) {
        Set<A> aResults;
        Set<AAAA> aaaaResults;
        ResolverApi resolver = getResolver(dnssecMode);
        try {
            ResolverResult<A> aResult = resolver.resolve(name, A.class);
            ResolverResult<AAAA> aaaaResult = resolver.resolve(name, AAAA.class);
            if (!aResult.wasSuccessful() && !aaaaResult.wasSuccessful()) {
                failedAddresses.add(new HostAddress(name, (Exception) getExceptionFrom(aResult)));
                failedAddresses.add(new HostAddress(name, (Exception) getExceptionFrom(aaaaResult)));
                return null;
            } else if (shouldAbortIfNotAuthentic(name, dnssecMode, aResult, failedAddresses) || shouldAbortIfNotAuthentic(name, dnssecMode, aaaaResult, failedAddresses)) {
                return null;
            } else {
                if (aResult.wasSuccessful()) {
                    aResults = aResult.getAnswers();
                } else {
                    aResults = Collections.emptySet();
                }
                if (aaaaResult.wasSuccessful()) {
                    aaaaResults = aaaaResult.getAnswers();
                } else {
                    aaaaResults = Collections.emptySet();
                }
                List<InetAddress> inetAddresses = new ArrayList<>(aResults.size() + aaaaResults.size());
                for (A a : aResults) {
                    try {
                        inetAddresses.add(InetAddress.getByAddress(a.getIp()));
                    } catch (UnknownHostException e) {
                    }
                }
                for (AAAA aaaa : aaaaResults) {
                    try {
                        inetAddresses.add(InetAddress.getByAddress(name.ace, aaaa.getIp()));
                    } catch (UnknownHostException e2) {
                    }
                }
                return inetAddresses;
            }
        } catch (IOException e3) {
            failedAddresses.add(new HostAddress(name, (Exception) e3));
            return null;
        }
    }

    public static void setup() {
        DNSUtil.setDNSResolver(getInstance());
    }

    public List<Exception> initialize() {
        setup();
        MiniDnsDane.setup();
        return null;
    }

    private static ResolverApi getResolver(DnssecMode dnssecMode) {
        if (dnssecMode == DnssecMode.disabled) {
            return NON_DNSSEC_RESOLVER;
        }
        return DNSSEC_RESOLVER;
    }

    private static boolean shouldAbortIfNotAuthentic(DnsName name, DnssecMode dnssecMode, ResolverResult<?> result, List<HostAddress> failedAddresses) {
        int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$ConnectionConfiguration$DnssecMode[dnssecMode.ordinal()];
        if (i == 1 || i == 2) {
            if (!result.isAuthenticData()) {
                StringBuilder sb = new StringBuilder();
                sb.append("DNSSEC verification failed: ");
                sb.append(((UnverifiedReason) result.getUnverifiedReasons().iterator().next()).getReasonString());
                failedAddresses.add(new HostAddress(name, new Exception(sb.toString())));
                return true;
            }
        } else if (i != 3) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Unknown DnssecMode: ");
            sb2.append(dnssecMode);
            throw new IllegalStateException(sb2.toString());
        }
        return false;
    }

    private static ResolutionUnsuccessfulException getExceptionFrom(ResolverResult<?> result) {
        return new ResolutionUnsuccessfulException(result.getQuestion(), result.getResponseCode());
    }
}
