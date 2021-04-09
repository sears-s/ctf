package org.minidns.hla;

import java.io.IOException;
import org.minidns.DnsCache;
import org.minidns.MiniDnsException.NullResultException;
import org.minidns.cache.LruCache;
import org.minidns.cache.MiniDnsCacheFactory;
import org.minidns.dnsmessage.Question;
import org.minidns.dnsname.DnsName;
import org.minidns.dnssec.DnssecClient;
import org.minidns.dnssec.DnssecQueryResult;
import org.minidns.iterative.ReliableDnsClient.Mode;
import org.minidns.record.Data;
import org.minidns.record.Record.TYPE;

public class DnssecResolverApi extends ResolverApi {
    public static final DnssecResolverApi INSTANCE = new DnssecResolverApi();
    private final DnssecClient dnssecClient;
    private final DnssecClient iterativeOnlyDnssecClient;
    private final DnssecClient recursiveOnlyDnssecClient;

    public DnssecResolverApi() {
        this(new MiniDnsCacheFactory() {
            public DnsCache newCache() {
                return new LruCache();
            }
        });
    }

    public DnssecResolverApi(MiniDnsCacheFactory cacheFactory) {
        this(new DnssecClient(cacheFactory.newCache()), cacheFactory);
    }

    private DnssecResolverApi(DnssecClient dnssecClient2, MiniDnsCacheFactory cacheFactory) {
        super(dnssecClient2);
        this.dnssecClient = dnssecClient2;
        this.iterativeOnlyDnssecClient = new DnssecClient(cacheFactory.newCache());
        this.iterativeOnlyDnssecClient.setMode(Mode.iterativeOnly);
        this.recursiveOnlyDnssecClient = new DnssecClient(cacheFactory.newCache());
        this.recursiveOnlyDnssecClient.setMode(Mode.recursiveOnly);
    }

    public <D extends Data> ResolverResult<D> resolve(Question question) throws IOException {
        return toResolverResult(question, this.dnssecClient.queryDnssec(question));
    }

    public <D extends Data> ResolverResult<D> resolveDnssecReliable(String name, Class<D> type) throws IOException {
        return resolveDnssecReliable(DnsName.from(name), type);
    }

    public <D extends Data> ResolverResult<D> resolveDnssecReliable(DnsName name, Class<D> type) throws IOException {
        return resolveDnssecReliable(new Question(name, TYPE.getType(type)));
    }

    public <D extends Data> ResolverResult<D> resolveDnssecReliable(Question question) throws IOException {
        DnssecQueryResult dnssecMessage = this.recursiveOnlyDnssecClient.queryDnssec(question);
        if (dnssecMessage == null || !dnssecMessage.isAuthenticData()) {
            dnssecMessage = this.iterativeOnlyDnssecClient.queryDnssec(question);
        }
        return toResolverResult(question, dnssecMessage);
    }

    public DnssecClient getDnssecClient() {
        return this.dnssecClient;
    }

    private static <D extends Data> ResolverResult<D> toResolverResult(Question question, DnssecQueryResult dnssecMessage) throws NullResultException {
        return new ResolverResult<>(question, dnssecMessage.dnsQueryResult, dnssecMessage.getUnverifiedReasons());
    }
}
