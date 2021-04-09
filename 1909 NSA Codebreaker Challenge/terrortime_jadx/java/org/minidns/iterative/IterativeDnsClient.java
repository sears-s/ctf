package org.minidns.iterative;

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
import java.util.Random;
import java.util.logging.Level;
import org.minidns.AbstractDnsClient;
import org.minidns.AbstractDnsClient.IpVersionSetting;
import org.minidns.DnsCache;
import org.minidns.constants.DnsRootServer;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsmessage.DnsMessage.Builder;
import org.minidns.dnsmessage.Question;
import org.minidns.dnsname.DnsName;
import org.minidns.dnsqueryresult.DnsQueryResult;
import org.minidns.iterative.IterativeClientException.LoopDetected;
import org.minidns.iterative.IterativeClientException.NotAuthoritativeNorGlueRrFound;
import org.minidns.record.A;
import org.minidns.record.AAAA;
import org.minidns.record.Data;
import org.minidns.record.NS;
import org.minidns.record.RRWithTarget;
import org.minidns.record.Record;
import org.minidns.record.Record.TYPE;
import org.minidns.util.MultipleIoException;

public class IterativeDnsClient extends AbstractDnsClient {
    int maxSteps = 128;

    /* renamed from: org.minidns.iterative.IterativeDnsClient$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting = new int[IpVersionSetting.values().length];
        static final /* synthetic */ int[] $SwitchMap$org$minidns$record$Record$TYPE = new int[TYPE.values().length];

        static {
            try {
                $SwitchMap$org$minidns$record$Record$TYPE[TYPE.A.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$minidns$record$Record$TYPE[TYPE.AAAA.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[IpVersionSetting.v4only.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[IpVersionSetting.v6only.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[IpVersionSetting.v4v6.ordinal()] = 3;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[IpVersionSetting.v6v4.ordinal()] = 4;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    private static class IpResultSet {
        final List<InetAddress> addresses;

        private static class Builder {
            /* access modifiers changed from: private */
            public final List<InetAddress> ipv4Addresses;
            /* access modifiers changed from: private */
            public final List<InetAddress> ipv6Addresses;
            private final Random random;

            /* synthetic */ Builder(Random x0, AnonymousClass1 x1) {
                this(x0);
            }

            private Builder(Random random2) {
                this.ipv4Addresses = new ArrayList(8);
                this.ipv6Addresses = new ArrayList(8);
                this.random = random2;
            }

            public IpResultSet build() {
                return new IpResultSet(this.ipv4Addresses, this.ipv6Addresses, this.random, null);
            }
        }

        /* synthetic */ IpResultSet(List x0, List x1, Random x2, AnonymousClass1 x3) {
            this(x0, x1, x2);
        }

        private IpResultSet(List<InetAddress> ipv4Addresses, List<InetAddress> ipv6Addresses, Random random) {
            int size;
            int i = AnonymousClass1.$SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[IterativeDnsClient.DEFAULT_IP_VERSION_SETTING.ordinal()];
            if (i == 1) {
                size = ipv4Addresses.size();
            } else if (i != 2) {
                size = ipv4Addresses.size() + ipv6Addresses.size();
            } else {
                size = ipv6Addresses.size();
            }
            if (size == 0) {
                this.addresses = Collections.emptyList();
                return;
            }
            if (IterativeDnsClient.DEFAULT_IP_VERSION_SETTING.v4) {
                Collections.shuffle(ipv4Addresses, random);
            }
            if (IterativeDnsClient.DEFAULT_IP_VERSION_SETTING.v6) {
                Collections.shuffle(ipv6Addresses, random);
            }
            List<InetAddress> addresses2 = new ArrayList<>(size);
            int i2 = AnonymousClass1.$SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[IterativeDnsClient.DEFAULT_IP_VERSION_SETTING.ordinal()];
            if (i2 == 1) {
                addresses2.addAll(ipv4Addresses);
            } else if (i2 == 2) {
                addresses2.addAll(ipv6Addresses);
            } else if (i2 == 3) {
                addresses2.addAll(ipv4Addresses);
                addresses2.addAll(ipv6Addresses);
            } else if (i2 == 4) {
                addresses2.addAll(ipv6Addresses);
                addresses2.addAll(ipv4Addresses);
            }
            this.addresses = Collections.unmodifiableList(addresses2);
        }
    }

    public IterativeDnsClient() {
    }

    public IterativeDnsClient(DnsCache cache) {
        super(cache);
    }

    /* access modifiers changed from: protected */
    public DnsQueryResult query(Builder queryBuilder) throws IOException {
        return queryRecursive(new ResolutionState(this), queryBuilder.build());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0028, code lost:
        if (r0[1] != null) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x002a, code lost:
        r0[1] = r2.getInetAddress();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.net.InetAddress[] getTargets(java.util.Collection<? extends org.minidns.record.InternetAddressRR> r6, java.util.Collection<? extends org.minidns.record.InternetAddressRR> r7) {
        /*
            r0 = 2
            java.net.InetAddress[] r0 = new java.net.InetAddress[r0]
            java.util.Iterator r1 = r6.iterator()
        L_0x0007:
            boolean r2 = r1.hasNext()
            r3 = 1
            r4 = 0
            if (r2 == 0) goto L_0x0030
            java.lang.Object r2 = r1.next()
            org.minidns.record.InternetAddressRR r2 = (org.minidns.record.InternetAddressRR) r2
            r5 = r0[r4]
            if (r5 != 0) goto L_0x0026
            java.net.InetAddress r5 = r2.getInetAddress()
            r0[r4] = r5
            boolean r5 = r7.isEmpty()
            if (r5 == 0) goto L_0x0026
            goto L_0x0007
        L_0x0026:
            r1 = r0[r3]
            if (r1 != 0) goto L_0x0030
            java.net.InetAddress r1 = r2.getInetAddress()
            r0[r3] = r1
        L_0x0030:
            java.util.Iterator r1 = r7.iterator()
        L_0x0034:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0055
            java.lang.Object r2 = r1.next()
            org.minidns.record.InternetAddressRR r2 = (org.minidns.record.InternetAddressRR) r2
            r5 = r0[r4]
            if (r5 != 0) goto L_0x004b
            java.net.InetAddress r5 = r2.getInetAddress()
            r0[r4] = r5
            goto L_0x0034
        L_0x004b:
            r1 = r0[r3]
            if (r1 != 0) goto L_0x0055
            java.net.InetAddress r1 = r2.getInetAddress()
            r0[r3] = r1
        L_0x0055:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.minidns.iterative.IterativeDnsClient.getTargets(java.util.Collection, java.util.Collection):java.net.InetAddress[]");
    }

    private DnsQueryResult queryRecursive(ResolutionState resolutionState, DnsMessage q) throws IOException {
        InetAddress primaryTarget = null;
        InetAddress secondaryTarget = null;
        DnsName parent = q.getQuestion().name.getParent();
        int i = AnonymousClass1.$SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[this.ipVersionSetting.ordinal()];
        if (i != 1) {
            if (i == 2) {
                Iterator it = getCachedIPv6NameserverAddressesFor(parent).iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    AAAA aaaa = (AAAA) it.next();
                    if (primaryTarget != null) {
                        secondaryTarget = aaaa.getInetAddress();
                        break;
                    }
                    primaryTarget = aaaa.getInetAddress();
                }
            } else if (i == 3) {
                InetAddress[] v4v6targets = getTargets(getCachedIPv4NameserverAddressesFor(parent), getCachedIPv6NameserverAddressesFor(parent));
                primaryTarget = v4v6targets[0];
                secondaryTarget = v4v6targets[1];
            } else if (i == 4) {
                InetAddress[] v6v4targets = getTargets(getCachedIPv6NameserverAddressesFor(parent), getCachedIPv4NameserverAddressesFor(parent));
                primaryTarget = v6v4targets[0];
                secondaryTarget = v6v4targets[1];
            } else {
                throw new AssertionError();
            }
        } else {
            Iterator it2 = getCachedIPv4NameserverAddressesFor(parent).iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                A a = (A) it2.next();
                if (primaryTarget != null) {
                    secondaryTarget = a.getInetAddress();
                    break;
                }
                primaryTarget = a.getInetAddress();
            }
        }
        DnsName authoritativeZone = parent;
        if (primaryTarget == null) {
            authoritativeZone = DnsName.ROOT;
            int i2 = AnonymousClass1.$SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[this.ipVersionSetting.ordinal()];
            if (i2 == 1) {
                primaryTarget = DnsRootServer.getRandomIpv4RootServer(this.insecureRandom);
            } else if (i2 == 2) {
                primaryTarget = DnsRootServer.getRandomIpv6RootServer(this.insecureRandom);
            } else if (i2 == 3) {
                primaryTarget = DnsRootServer.getRandomIpv4RootServer(this.insecureRandom);
                secondaryTarget = DnsRootServer.getRandomIpv6RootServer(this.insecureRandom);
            } else if (i2 == 4) {
                primaryTarget = DnsRootServer.getRandomIpv6RootServer(this.insecureRandom);
                secondaryTarget = DnsRootServer.getRandomIpv4RootServer(this.insecureRandom);
            }
        }
        List<IOException> ioExceptions = new LinkedList<>();
        try {
            return queryRecursive(resolutionState, q, primaryTarget, authoritativeZone);
        } catch (IOException ioException) {
            abortIfFatal(ioException);
            ioExceptions.add(ioException);
            if (secondaryTarget != null) {
                try {
                    return queryRecursive(resolutionState, q, secondaryTarget, authoritativeZone);
                } catch (IOException ioException2) {
                    ioExceptions.add(ioException2);
                    MultipleIoException.throwIfRequired(ioExceptions);
                    return null;
                }
            }
            MultipleIoException.throwIfRequired(ioExceptions);
            return null;
        }
    }

    private DnsQueryResult queryRecursive(ResolutionState resolutionState, DnsMessage q, InetAddress address, DnsName authoritativeZone) throws IOException {
        ResolutionState resolutionState2 = resolutionState;
        DnsMessage dnsMessage = q;
        InetAddress inetAddress = address;
        DnsName dnsName = authoritativeZone;
        resolutionState2.recurse(inetAddress, dnsMessage);
        DnsQueryResult dnsQueryResult = query(dnsMessage, inetAddress);
        DnsMessage resMessage = dnsQueryResult.response;
        if (resMessage.authoritativeAnswer) {
            return dnsQueryResult;
        }
        if (this.cache != null) {
            this.cache.offer(dnsMessage, dnsQueryResult, dnsName);
        }
        List<Record<? extends Data>> authorities = resMessage.copyAuthority();
        List<IOException> ioExceptions = new LinkedList<>();
        Iterator<Record<? extends Data>> iterator = authorities.iterator();
        while (iterator.hasNext()) {
            Record ifPossibleAs = ((Record) iterator.next()).ifPossibleAs(NS.class);
            if (ifPossibleAs == null) {
                iterator.remove();
            } else {
                Iterator<InetAddress> addressIterator = searchAdditional(resMessage, ((NS) ifPossibleAs.payloadData).target).addresses.iterator();
                while (addressIterator.hasNext()) {
                    try {
                        return queryRecursive(resolutionState2, dnsMessage, (InetAddress) addressIterator.next(), ifPossibleAs.name);
                    } catch (IOException e) {
                        abortIfFatal(e);
                        DnsMessage resMessage2 = resMessage;
                        Record record = ifPossibleAs;
                        LOGGER.log(Level.FINER, "Exception while recursing", e);
                        resolutionState.decrementSteps();
                        ioExceptions.add(e);
                        if (!addressIterator.hasNext()) {
                            iterator.remove();
                        }
                        InetAddress inetAddress2 = address;
                        resMessage = resMessage2;
                        ifPossibleAs = record;
                    }
                }
                Record record2 = ifPossibleAs;
                InetAddress inetAddress3 = address;
            }
        }
        for (Record<? extends Data> record3 : authorities) {
            Question question = q.getQuestion();
            DnsName name = ((NS) record3.payloadData).target;
            if (!question.name.equals(name) || !(question.type == TYPE.A || question.type == TYPE.AAAA)) {
                IpResultSet res = null;
                try {
                    res = resolveIpRecursive(resolutionState2, name);
                } catch (IOException e2) {
                    IOException e3 = e2;
                    resolutionState.decrementSteps();
                    ioExceptions.add(e3);
                }
                if (res == null) {
                    continue;
                } else {
                    for (InetAddress target : res.addresses) {
                        try {
                            return queryRecursive(resolutionState2, dnsMessage, target, record3.name);
                        } catch (IOException e4) {
                            resolutionState.decrementSteps();
                            ioExceptions.add(e4);
                        }
                    }
                    continue;
                }
            }
        }
        MultipleIoException.throwIfRequired(ioExceptions);
        throw new NotAuthoritativeNorGlueRrFound(dnsMessage, dnsQueryResult, dnsName);
    }

    private IpResultSet resolveIpRecursive(ResolutionState resolutionState, DnsName name) throws IOException {
        Builder res = newIpResultSetBuilder();
        DnsMessage aMessage = null;
        if (this.ipVersionSetting.v4) {
            Question question = new Question(name, TYPE.A);
            DnsQueryResult aDnsQueryResult = queryRecursive(resolutionState, getQueryFor(question));
            DnsMessage aMessage2 = aDnsQueryResult != null ? aDnsQueryResult.response : null;
            if (aMessage2 != null) {
                for (Record<? extends Data> answer : aMessage2.answerSection) {
                    if (answer.isAnswer(question)) {
                        res.ipv4Addresses.add(inetAddressFromRecord(name.ace, (A) answer.payloadData));
                    } else if (answer.type == TYPE.CNAME && answer.name.equals(name)) {
                        return resolveIpRecursive(resolutionState, ((RRWithTarget) answer.payloadData).target);
                    }
                }
            }
        }
        if (this.ipVersionSetting.v6) {
            Question question2 = new Question(name, TYPE.AAAA);
            DnsQueryResult aDnsQueryResult2 = queryRecursive(resolutionState, getQueryFor(question2));
            if (aDnsQueryResult2 != null) {
                aMessage = aDnsQueryResult2.response;
            }
            if (aMessage != null) {
                for (Record<? extends Data> answer2 : aMessage.answerSection) {
                    if (answer2.isAnswer(question2)) {
                        res.ipv6Addresses.add(inetAddressFromRecord(name.ace, (AAAA) answer2.payloadData));
                    } else if (answer2.type == TYPE.CNAME && answer2.name.equals(name)) {
                        return resolveIpRecursive(resolutionState, ((RRWithTarget) answer2.payloadData).target);
                    }
                }
            }
        }
        return res.build();
    }

    private IpResultSet searchAdditional(DnsMessage message, DnsName name) {
        Builder res = newIpResultSetBuilder();
        for (Record<? extends Data> record : message.additionalSection) {
            if (record.name.equals(name)) {
                int i = AnonymousClass1.$SwitchMap$org$minidns$record$Record$TYPE[record.type.ordinal()];
                if (i == 1) {
                    res.ipv4Addresses.add(inetAddressFromRecord(name.ace, (A) record.payloadData));
                } else if (i == 2) {
                    res.ipv6Addresses.add(inetAddressFromRecord(name.ace, (AAAA) record.payloadData));
                }
            }
        }
        return res.build();
    }

    private static InetAddress inetAddressFromRecord(String name, A recordPayload) {
        try {
            return InetAddress.getByAddress(name, recordPayload.getIp());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static InetAddress inetAddressFromRecord(String name, AAAA recordPayload) {
        try {
            return InetAddress.getByAddress(name, recordPayload.getIp());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<InetAddress> getRootServer(char rootServerId) {
        return getRootServer(rootServerId, DEFAULT_IP_VERSION_SETTING);
    }

    public static List<InetAddress> getRootServer(char rootServerId, IpVersionSetting setting) {
        Inet4Address ipv4Root = DnsRootServer.getIpv4RootServerById(rootServerId);
        Inet6Address ipv6Root = DnsRootServer.getIpv6RootServerById(rootServerId);
        List<InetAddress> res = new ArrayList<>(2);
        int i = AnonymousClass1.$SwitchMap$org$minidns$AbstractDnsClient$IpVersionSetting[setting.ordinal()];
        if (i != 1) {
            if (i != 2) {
                if (i == 3) {
                    if (ipv4Root != null) {
                        res.add(ipv4Root);
                    }
                    if (ipv6Root != null) {
                        res.add(ipv6Root);
                    }
                } else if (i == 4) {
                    if (ipv6Root != null) {
                        res.add(ipv6Root);
                    }
                    if (ipv4Root != null) {
                        res.add(ipv4Root);
                    }
                }
            } else if (ipv6Root != null) {
                res.add(ipv6Root);
            }
        } else if (ipv4Root != null) {
            res.add(ipv4Root);
        }
        return res;
    }

    /* access modifiers changed from: protected */
    public boolean isResponseCacheable(Question q, DnsQueryResult result) {
        return result.response.authoritativeAnswer;
    }

    /* access modifiers changed from: protected */
    public Builder newQuestion(Builder message) {
        message.setRecursionDesired(false);
        message.getEdnsBuilder().setUdpPayloadSize(this.dataSource.getUdpPayloadSize());
        return message;
    }

    private Builder newIpResultSetBuilder() {
        return new Builder(this.insecureRandom, null);
    }

    protected static void abortIfFatal(IOException ioException) throws IOException {
        if (ioException instanceof LoopDetected) {
            throw ioException;
        }
    }
}
