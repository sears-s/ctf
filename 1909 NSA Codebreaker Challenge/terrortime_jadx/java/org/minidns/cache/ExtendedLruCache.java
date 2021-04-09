package org.minidns.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsmessage.DnsMessage.Builder;
import org.minidns.dnsmessage.Question;
import org.minidns.dnsname.DnsName;
import org.minidns.dnsqueryresult.CachedDnsQueryResult;
import org.minidns.dnsqueryresult.DnsQueryResult;
import org.minidns.dnsqueryresult.SynthesizedCachedDnsQueryResult;
import org.minidns.record.Data;
import org.minidns.record.Record;

public class ExtendedLruCache extends LruCache {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    public ExtendedLruCache() {
        this(512);
    }

    public ExtendedLruCache(int capacity) {
        super(capacity);
    }

    public ExtendedLruCache(int capacity, long maxTTL) {
        super(capacity, maxTTL);
    }

    /* access modifiers changed from: protected */
    public void putNormalized(DnsMessage q, DnsQueryResult result) {
        super.putNormalized(q, result);
        DnsMessage message = result.response;
        Map<DnsMessage, List<Record<? extends Data>>> extraCaches = new HashMap<>(message.additionalSection.size());
        gather(extraCaches, q, message.answerSection, null);
        gather(extraCaches, q, message.authoritySection, null);
        gather(extraCaches, q, message.additionalSection, null);
        putExtraCaches(result, extraCaches);
    }

    public void offer(DnsMessage query, DnsQueryResult result, DnsName authoritativeZone) {
        DnsMessage reply = result.response;
        Map<DnsMessage, List<Record<? extends Data>>> extraCaches = new HashMap<>(reply.additionalSection.size());
        gather(extraCaches, query, reply.authoritySection, authoritativeZone);
        gather(extraCaches, query, reply.additionalSection, authoritativeZone);
        putExtraCaches(result, extraCaches);
    }

    private final void gather(Map<DnsMessage, List<Record<? extends Data>>> extraCaches, DnsMessage q, List<Record<? extends Data>> records, DnsName authoritativeZone) {
        for (Record<? extends Data> extraRecord : records) {
            if (shouldGather(extraRecord, q.getQuestion(), authoritativeZone)) {
                Builder additionalRecordQuestionBuilder = extraRecord.getQuestionMessage();
                if (additionalRecordQuestionBuilder != null) {
                    additionalRecordQuestionBuilder.copyFlagsFrom(q);
                    additionalRecordQuestionBuilder.setAdditionalResourceRecords(q.additionalSection);
                    DnsMessage additionalRecordQuestion = additionalRecordQuestionBuilder.build();
                    if (!additionalRecordQuestion.equals(q)) {
                        List list = (List) extraCaches.get(additionalRecordQuestion);
                        if (list == null) {
                            list = new LinkedList();
                            extraCaches.put(additionalRecordQuestion, list);
                        }
                        list.add(extraRecord);
                    }
                }
            }
        }
    }

    private final void putExtraCaches(DnsQueryResult synthesynthesizationSource, Map<DnsMessage, List<Record<? extends Data>>> extraCaches) {
        DnsMessage reply = synthesynthesizationSource.response;
        for (Entry<DnsMessage, List<Record<? extends Data>>> entry : extraCaches.entrySet()) {
            DnsMessage question = (DnsMessage) entry.getKey();
            CachedDnsQueryResult cachedDnsQueryResult = new SynthesizedCachedDnsQueryResult(question, reply.asBuilder().setQuestion(question.getQuestion()).setAuthoritativeAnswer(true).addAnswers((Collection) entry.getValue()).build(), synthesynthesizationSource);
            synchronized (this) {
                this.backend.put(question, cachedDnsQueryResult);
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldGather(Record<? extends Data> extraRecord, Question question, DnsName authoritativeZone) {
        boolean extraRecordIsChildOfQuestion = extraRecord.name.isChildOf(question.name);
        boolean extraRecordIsChildOfAuthoritativeZone = false;
        if (authoritativeZone != null) {
            extraRecordIsChildOfAuthoritativeZone = extraRecord.name.isChildOf(authoritativeZone);
        }
        return extraRecordIsChildOfQuestion || extraRecordIsChildOfAuthoritativeZone;
    }
}
