package org.minidns.iterative;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.minidns.AbstractDnsClient;
import org.minidns.DnsCache;
import org.minidns.DnsClient;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsmessage.DnsMessage.Builder;
import org.minidns.dnsmessage.Question;
import org.minidns.dnsqueryresult.DnsQueryResult;
import org.minidns.source.DnsDataSource;
import org.minidns.util.MultipleIoException;

public class ReliableDnsClient extends AbstractDnsClient {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final DnsClient dnsClient;
    private Mode mode;
    private final IterativeDnsClient recursiveDnsClient;

    public enum Mode {
        recursiveWithIterativeFallback,
        recursiveOnly,
        iterativeOnly
    }

    public ReliableDnsClient(DnsCache dnsCache) {
        super(dnsCache);
        this.mode = Mode.recursiveWithIterativeFallback;
        this.recursiveDnsClient = new IterativeDnsClient(dnsCache) {
            /* access modifiers changed from: protected */
            public Builder newQuestion(Builder questionMessage) {
                return ReliableDnsClient.this.newQuestion(super.newQuestion(questionMessage));
            }

            /* access modifiers changed from: protected */
            public boolean isResponseCacheable(Question q, DnsQueryResult dnsMessage) {
                return ReliableDnsClient.this.isResponseCacheable(q, dnsMessage) && super.isResponseCacheable(q, dnsMessage);
            }
        };
        this.dnsClient = new DnsClient(dnsCache) {
            /* access modifiers changed from: protected */
            public Builder newQuestion(Builder questionMessage) {
                return ReliableDnsClient.this.newQuestion(super.newQuestion(questionMessage));
            }

            /* access modifiers changed from: protected */
            public boolean isResponseCacheable(Question q, DnsQueryResult dnsMessage) {
                return ReliableDnsClient.this.isResponseCacheable(q, dnsMessage) && super.isResponseCacheable(q, dnsMessage);
            }
        };
    }

    public ReliableDnsClient() {
        this(DEFAULT_CACHE);
    }

    /* access modifiers changed from: protected */
    public DnsQueryResult query(Builder q) throws IOException {
        String logString;
        DnsQueryResult dnsMessage = null;
        String unacceptableReason = null;
        List<IOException> ioExceptions = new LinkedList<>();
        if (this.mode != Mode.iterativeOnly) {
            try {
                dnsMessage = this.dnsClient.query(q);
                if (dnsMessage != null) {
                    unacceptableReason = isResponseAcceptable(dnsMessage.response);
                    if (unacceptableReason == null) {
                        return dnsMessage;
                    }
                }
            } catch (IOException ioException) {
                ioExceptions.add(ioException);
            }
        }
        if (this.mode == Mode.recursiveOnly) {
            return dnsMessage;
        }
        Level FALLBACK_LOG_LEVEL = Level.FINE;
        if (LOGGER.isLoggable(FALLBACK_LOG_LEVEL) && this.mode != Mode.iterativeOnly) {
            String logString2 = "Resolution fall back to iterative mode because: ";
            if (!ioExceptions.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append(logString2);
                sb.append(ioExceptions.get(0));
                logString = sb.toString();
            } else if (dnsMessage == null) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(logString2);
                sb2.append(" DnsClient did not return a response");
                logString = sb2.toString();
            } else if (unacceptableReason != null) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(logString2);
                sb3.append(unacceptableReason);
                sb3.append(". Response:\n");
                sb3.append(dnsMessage);
                logString = sb3.toString();
            } else {
                throw new AssertionError("This should never been reached");
            }
            LOGGER.log(FALLBACK_LOG_LEVEL, logString);
        }
        try {
            dnsMessage = this.recursiveDnsClient.query(q);
        } catch (IOException ioException2) {
            ioExceptions.add(ioException2);
        }
        if (dnsMessage == null) {
            MultipleIoException.throwIfRequired(ioExceptions);
        }
        return dnsMessage;
    }

    /* access modifiers changed from: protected */
    public Builder newQuestion(Builder questionMessage) {
        return questionMessage;
    }

    /* access modifiers changed from: protected */
    public boolean isResponseCacheable(Question q, DnsQueryResult result) {
        return isResponseAcceptable(result.response) == null;
    }

    /* access modifiers changed from: protected */
    public String isResponseAcceptable(DnsMessage response) {
        return null;
    }

    public void setDataSource(DnsDataSource dataSource) {
        super.setDataSource(dataSource);
        this.recursiveDnsClient.setDataSource(dataSource);
        this.dnsClient.setDataSource(dataSource);
    }

    public void setMode(Mode mode2) {
        if (mode2 != null) {
            this.mode = mode2;
            return;
        }
        throw new IllegalArgumentException("Mode must not be null.");
    }

    public void setUseHardcodedDnsServers(boolean useHardcodedDnsServers) {
        this.dnsClient.setUseHardcodedDnsServers(useHardcodedDnsServers);
    }
}
