package org.minidns.hla;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.minidns.AbstractDnsClient.IpVersionSetting;
import org.minidns.MiniDnsException.NullResultException;
import org.minidns.dnsname.DnsName;
import org.minidns.hla.srv.SrvServiceProto;
import org.minidns.record.A;
import org.minidns.record.AAAA;
import org.minidns.record.InternetAddressRR;
import org.minidns.record.SRV;
import org.minidns.util.SrvUtil;

public class SrvResolverResult extends ResolverResult<SRV> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final IpVersionSetting ipVersion;
    private final ResolverApi resolver;
    private List<ResolvedSrvRecord> sortedSrvResolvedAddresses;
    private final SrvServiceProto srvServiceProto;

    /* renamed from: org.minidns.hla.SrvResolverResult$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting = new int[IpVersionSetting.values().length];

        static {
            try {
                $SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[IpVersionSetting.v4only.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[IpVersionSetting.v6only.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[IpVersionSetting.v4v6.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[IpVersionSetting.v6v4.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public static class ResolvedSrvRecord {
        public final ResolverResult<A> aRecordsResult;
        public final ResolverResult<AAAA> aaaaRecordsResult;
        public final List<InternetAddressRR> addresses;
        public final DnsName name;
        public final int port;
        public final SRV srv;
        public final SrvServiceProto srvServiceProto;

        /* synthetic */ ResolvedSrvRecord(DnsName x0, SrvServiceProto x1, SRV x2, List x3, ResolverResult x4, ResolverResult x5, AnonymousClass1 x6) {
            this(x0, x1, x2, x3, x4, x5);
        }

        private ResolvedSrvRecord(DnsName name2, SrvServiceProto srvServiceProto2, SRV srv2, List<InternetAddressRR> addresses2, ResolverResult<A> aRecordsResult2, ResolverResult<AAAA> aaaaRecordsResult2) {
            this.name = name2;
            this.srvServiceProto = srvServiceProto2;
            this.srv = srv2;
            this.addresses = Collections.unmodifiableList(addresses2);
            this.port = srv2.port;
            this.aRecordsResult = aRecordsResult2;
            this.aaaaRecordsResult = aaaaRecordsResult2;
        }
    }

    SrvResolverResult(ResolverResult<SRV> srvResult, SrvServiceProto srvServiceProto2, ResolverApi resolver2) throws NullResultException {
        super(srvResult.question, srvResult.result, srvResult.unverifiedReasons);
        this.resolver = resolver2;
        this.ipVersion = resolver2.getClient().getPreferedIpVersion();
        this.srvServiceProto = srvServiceProto2;
    }

    public List<ResolvedSrvRecord> getSortedSrvResolvedAddresses() throws IOException {
        Set<A> aRecords;
        ResolverResult resolverResult;
        ResolverResult resolverResult2;
        Set<AAAA> aaaaRecords;
        List<ResolvedSrvRecord> list = this.sortedSrvResolvedAddresses;
        if (list != null) {
            return list;
        }
        throwIseIfErrorResponse();
        if (isServiceDecidedlyNotAvailableAtThisDomain()) {
            return null;
        }
        List<SRV> srvRecords = SrvUtil.sortSrvRecords(getAnswers());
        List<ResolvedSrvRecord> res = new ArrayList<>(srvRecords.size());
        for (SRV srvRecord : srvRecords) {
            Set<A> aRecords2 = Collections.emptySet();
            if (this.ipVersion.v4) {
                ResolverResult<A> aRecordsResult = this.resolver.resolve(srvRecord.target, A.class);
                if (!aRecordsResult.wasSuccessful() || aRecordsResult.hasUnverifiedReasons()) {
                    resolverResult = aRecordsResult;
                    aRecords = aRecords2;
                } else {
                    resolverResult = aRecordsResult;
                    aRecords = aRecordsResult.getAnswers();
                }
            } else {
                resolverResult = null;
                aRecords = aRecords2;
            }
            Set<AAAA> aaaaRecords2 = Collections.emptySet();
            if (this.ipVersion.v6) {
                ResolverResult<AAAA> aaaaRecordsResult = this.resolver.resolve(srvRecord.target, AAAA.class);
                if (!aaaaRecordsResult.wasSuccessful() || aaaaRecordsResult.hasUnverifiedReasons()) {
                    aaaaRecords = aaaaRecords2;
                    resolverResult2 = aaaaRecordsResult;
                } else {
                    aaaaRecords = aaaaRecordsResult.getAnswers();
                    resolverResult2 = aaaaRecordsResult;
                }
            } else {
                aaaaRecords = aaaaRecords2;
                resolverResult2 = null;
            }
            if (!aRecords.isEmpty() || !aaaaRecords.isEmpty()) {
                ArrayList arrayList = new ArrayList(aRecords.size() + aaaaRecords.size());
                int i = AnonymousClass1.$SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[this.ipVersion.ordinal()];
                if (i == 1) {
                    arrayList.addAll(aRecords);
                } else if (i == 2) {
                    arrayList.addAll(aaaaRecords);
                } else if (i == 3) {
                    arrayList.addAll(aRecords);
                    arrayList.addAll(aaaaRecords);
                } else if (i == 4) {
                    arrayList.addAll(aaaaRecords);
                    arrayList.addAll(aRecords);
                }
                ArrayList arrayList2 = arrayList;
                ResolvedSrvRecord resolvedSrvAddresses = new ResolvedSrvRecord(this.question.name, this.srvServiceProto, srvRecord, arrayList, resolverResult, resolverResult2, null);
                res.add(resolvedSrvAddresses);
            }
        }
        this.sortedSrvResolvedAddresses = res;
        return res;
    }

    public boolean isServiceDecidedlyNotAvailableAtThisDomain() {
        Set<SRV> answers = getAnswers();
        if (answers.size() != 1) {
            return false;
        }
        return true ^ ((SRV) answers.iterator().next()).isServiceAvailable();
    }

    @SafeVarargs
    public static List<ResolvedSrvRecord> sortMultiple(Collection<ResolvedSrvRecord>... resolvedSrvRecordCollections) {
        int srvRecordsCount = 0;
        for (Collection<ResolvedSrvRecord> resolvedSrvRecords : resolvedSrvRecordCollections) {
            if (resolvedSrvRecords != null) {
                srvRecordsCount += resolvedSrvRecords.size();
            }
        }
        List<SRV> srvToSort = new ArrayList<>(srvRecordsCount);
        Map<SRV, ResolvedSrvRecord> identityMap = new IdentityHashMap<>(srvRecordsCount);
        for (Collection<ResolvedSrvRecord> resolvedSrvRecords2 : resolvedSrvRecordCollections) {
            if (resolvedSrvRecords2 != null) {
                for (ResolvedSrvRecord resolvedSrvRecord : resolvedSrvRecords2) {
                    srvToSort.add(resolvedSrvRecord.srv);
                    identityMap.put(resolvedSrvRecord.srv, resolvedSrvRecord);
                }
            }
        }
        List<SRV> sortedSrvs = SrvUtil.sortSrvRecords(srvToSort);
        List<ResolvedSrvRecord> res = new ArrayList<>(srvRecordsCount);
        for (SRV sortedSrv : sortedSrvs) {
            res.add((ResolvedSrvRecord) identityMap.get(sortedSrv));
        }
        return res;
    }
}
