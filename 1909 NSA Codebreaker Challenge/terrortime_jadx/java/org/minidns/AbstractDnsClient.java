package org.minidns;

import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.minidns.MiniDnsFuture.InternalMiniDnsFuture;
import org.minidns.cache.LruCache;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsmessage.DnsMessage.Builder;
import org.minidns.dnsmessage.Question;
import org.minidns.dnsname.DnsName;
import org.minidns.dnsqueryresult.DnsQueryResult;
import org.minidns.record.A;
import org.minidns.record.AAAA;
import org.minidns.record.Data;
import org.minidns.record.NS;
import org.minidns.record.Record;
import org.minidns.record.Record.CLASS;
import org.minidns.record.Record.TYPE;
import org.minidns.source.DnsDataSource;
import org.minidns.source.DnsDataSource.OnResponseCallback;
import org.minidns.source.NetworkDataSource;

public abstract class AbstractDnsClient {
    protected static final LruCache DEFAULT_CACHE = new LruCache();
    protected static IpVersionSetting DEFAULT_IP_VERSION_SETTING = IpVersionSetting.v4v6;
    protected static final Logger LOGGER = Logger.getLogger(AbstractDnsClient.class.getName());
    protected final DnsCache cache;
    protected DnsDataSource dataSource;
    protected final Random insecureRandom;
    protected IpVersionSetting ipVersionSetting;
    private final OnResponseCallback onResponseCallback;
    protected final Random random;

    /* renamed from: org.minidns.AbstractDnsClient$2 reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
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
        }
    }

    public enum IpVersionSetting {
        v4only(true, false),
        v6only(false, true),
        v4v6(true, true),
        v6v4(true, true);
        
        public final boolean v4;
        public final boolean v6;

        private IpVersionSetting(boolean v42, boolean v62) {
            this.v4 = v42;
            this.v6 = v62;
        }
    }

    /* access modifiers changed from: protected */
    public abstract Builder newQuestion(Builder builder);

    /* access modifiers changed from: protected */
    public abstract DnsQueryResult query(Builder builder) throws IOException;

    public static void setDefaultIpVersion(IpVersionSetting preferedIpVersion) {
        if (preferedIpVersion != null) {
            DEFAULT_IP_VERSION_SETTING = preferedIpVersion;
            return;
        }
        throw new IllegalArgumentException();
    }

    public void setPreferedIpVersion(IpVersionSetting preferedIpVersion) {
        if (preferedIpVersion != null) {
            this.ipVersionSetting = preferedIpVersion;
            return;
        }
        throw new IllegalArgumentException();
    }

    public IpVersionSetting getPreferedIpVersion() {
        return this.ipVersionSetting;
    }

    protected AbstractDnsClient(DnsCache cache2) {
        Random random2;
        this.onResponseCallback = new OnResponseCallback() {
            public void onResponse(DnsMessage requestMessage, DnsQueryResult responseMessage) {
                Question q = requestMessage.getQuestion();
                if (AbstractDnsClient.this.cache != null && AbstractDnsClient.this.isResponseCacheable(q, responseMessage)) {
                    AbstractDnsClient.this.cache.put(requestMessage.asNormalizedVersion(), responseMessage);
                }
            }
        };
        this.insecureRandom = new Random();
        this.dataSource = new NetworkDataSource();
        this.ipVersionSetting = DEFAULT_IP_VERSION_SETTING;
        try {
            random2 = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            random2 = new SecureRandom();
        }
        this.random = random2;
        this.cache = cache2;
    }

    protected AbstractDnsClient() {
        this(DEFAULT_CACHE);
    }

    public final DnsQueryResult query(String name, TYPE type, CLASS clazz) throws IOException {
        return query(new Question((CharSequence) name, type, clazz));
    }

    public final DnsQueryResult query(DnsName name, TYPE type) throws IOException {
        return query(new Question(name, type, CLASS.IN));
    }

    public final DnsQueryResult query(CharSequence name, TYPE type) throws IOException {
        return query(new Question(name, type, CLASS.IN));
    }

    public DnsQueryResult query(Question q) throws IOException {
        return query(buildMessage(q));
    }

    public final MiniDnsFuture<DnsQueryResult, IOException> queryAsync(CharSequence name, TYPE type) {
        return queryAsync(new Question(name, type, CLASS.IN));
    }

    public final MiniDnsFuture<DnsQueryResult, IOException> queryAsync(Question q) {
        return queryAsync(buildMessage(q));
    }

    /* access modifiers changed from: protected */
    public MiniDnsFuture<DnsQueryResult, IOException> queryAsync(Builder query) {
        InternalMiniDnsFuture<DnsQueryResult, IOException> future = new InternalMiniDnsFuture<>();
        try {
            future.setResult(query(query));
            return future;
        } catch (IOException e) {
            future.setException(e);
            return future;
        }
    }

    public final DnsQueryResult query(Question q, InetAddress server, int port) throws IOException {
        return query(getQueryFor(q), server, port);
    }

    public final DnsQueryResult query(DnsMessage requestMessage, InetAddress address, int port) throws IOException {
        DnsCache dnsCache = this.cache;
        DnsQueryResult responseMessage = dnsCache == null ? null : dnsCache.get(requestMessage);
        if (responseMessage != null) {
            return responseMessage;
        }
        Question q = requestMessage.getQuestion();
        Level TRACE_LOG_LEVEL = Level.FINE;
        LOGGER.log(TRACE_LOG_LEVEL, "Asking {0} on {1} for {2} with:\n{3}", new Object[]{address, Integer.valueOf(port), q, requestMessage});
        try {
            DnsQueryResult responseMessage2 = this.dataSource.query(requestMessage, address, port);
            LOGGER.log(TRACE_LOG_LEVEL, "Response from {0} on {1} for {2}:\n{3}", new Object[]{address, Integer.valueOf(port), q, responseMessage2});
            this.onResponseCallback.onResponse(requestMessage, responseMessage2);
            return responseMessage2;
        } catch (IOException e) {
            LOGGER.log(TRACE_LOG_LEVEL, "IOException {0} on {1} while resolving {2}: {3}", new Object[]{address, Integer.valueOf(port), q, e});
            throw e;
        }
    }

    public final MiniDnsFuture<DnsQueryResult, IOException> queryAsync(DnsMessage requestMessage, InetAddress address, int port) {
        DnsCache dnsCache = this.cache;
        DnsQueryResult responseMessage = dnsCache == null ? null : dnsCache.get(requestMessage);
        if (responseMessage != null) {
            return MiniDnsFuture.from(responseMessage);
        }
        Question q = requestMessage.getQuestion();
        LOGGER.log(Level.FINE, "Asynchronusly asking {0} on {1} for {2} with:\n{3}", new Object[]{address, Integer.valueOf(port), q, requestMessage});
        return this.dataSource.queryAsync(requestMessage, address, port, this.onResponseCallback);
    }

    /* access modifiers changed from: protected */
    public boolean isResponseCacheable(Question q, DnsQueryResult result) {
        for (Record<? extends Data> record : result.response.answerSection) {
            if (record.isAnswer(q)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: 0000 */
    public final Builder buildMessage(Question question) {
        Builder message = DnsMessage.builder();
        message.setQuestion(question);
        message.setId(this.random.nextInt());
        return newQuestion(message);
    }

    public DnsQueryResult query(String name, TYPE type, CLASS clazz, InetAddress address, int port) throws IOException {
        return query(new Question((CharSequence) name, type, clazz), address, port);
    }

    public DnsQueryResult query(String name, TYPE type, CLASS clazz, InetAddress address) throws IOException {
        return query(new Question((CharSequence) name, type, clazz), address);
    }

    public DnsQueryResult query(String name, TYPE type, InetAddress address) throws IOException {
        return query(new Question((CharSequence) name, type, CLASS.IN), address);
    }

    public final DnsQueryResult query(DnsMessage query, InetAddress host) throws IOException {
        return query(query, host, 53);
    }

    public DnsQueryResult query(Question q, InetAddress address) throws IOException {
        return query(q, address, 53);
    }

    public final MiniDnsFuture<DnsQueryResult, IOException> queryAsync(DnsMessage query, InetAddress dnsServer) {
        return queryAsync(query, dnsServer, 53);
    }

    public DnsDataSource getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(DnsDataSource dataSource2) {
        if (dataSource2 != null) {
            this.dataSource = dataSource2;
            return;
        }
        throw new IllegalArgumentException();
    }

    public DnsCache getCache() {
        return this.cache;
    }

    /* access modifiers changed from: protected */
    public DnsMessage getQueryFor(Question q) {
        return buildMessage(q).build();
    }

    private <D extends Data> Set<D> getCachedRecordsFor(DnsName dnsName, TYPE type) {
        Question dnsNameNs = new Question(dnsName, type);
        DnsQueryResult cachedResult = this.cache.get(getQueryFor(dnsNameNs));
        if (cachedResult == null) {
            return Collections.emptySet();
        }
        return cachedResult.response.getAnswersFor(dnsNameNs);
    }

    public Set<NS> getCachedNameserverRecordsFor(DnsName dnsName) {
        return getCachedRecordsFor(dnsName, TYPE.NS);
    }

    public Set<A> getCachedIPv4AddressesFor(DnsName dnsName) {
        return getCachedRecordsFor(dnsName, TYPE.A);
    }

    public Set<AAAA> getCachedIPv6AddressesFor(DnsName dnsName) {
        return getCachedRecordsFor(dnsName, TYPE.AAAA);
    }

    private <D extends Data> Set<D> getCachedIPNameserverAddressesFor(DnsName dnsName, TYPE type) {
        Set<D> addresses;
        Set<NS> nsSet = getCachedNameserverRecordsFor(dnsName);
        if (nsSet.isEmpty()) {
            return Collections.emptySet();
        }
        Set<D> res = new HashSet<>(nsSet.size() * 3);
        for (NS ns : nsSet) {
            int i = AnonymousClass2.$SwitchMap$org$minidns$record$Record$TYPE[type.ordinal()];
            if (i == 1) {
                addresses = getCachedIPv4AddressesFor(ns.target);
            } else if (i == 2) {
                addresses = getCachedIPv6AddressesFor(ns.target);
            } else {
                throw new AssertionError();
            }
            res.addAll(addresses);
        }
        return res;
    }

    public Set<A> getCachedIPv4NameserverAddressesFor(DnsName dnsName) {
        return getCachedIPNameserverAddressesFor(dnsName, TYPE.A);
    }

    public Set<AAAA> getCachedIPv6NameserverAddressesFor(DnsName dnsName) {
        return getCachedIPNameserverAddressesFor(dnsName, TYPE.AAAA);
    }
}
