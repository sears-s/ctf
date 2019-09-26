package org.minidns.iterative;

import java.net.InetAddress;
import org.minidns.MiniDnsException;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsmessage.Question;
import org.minidns.dnsname.DnsName;
import org.minidns.dnsqueryresult.DnsQueryResult;

public abstract class IterativeClientException extends MiniDnsException {
    private static final long serialVersionUID = 1;

    public static class LoopDetected extends IterativeClientException {
        private static final long serialVersionUID = 1;
        public final InetAddress address;
        public final Question question;

        public LoopDetected(InetAddress address2, Question question2) {
            StringBuilder sb = new StringBuilder();
            sb.append("Resolution loop detected: We already asked ");
            sb.append(address2);
            sb.append(" about ");
            sb.append(question2);
            super(sb.toString());
            this.address = address2;
            this.question = question2;
        }
    }

    public static class MaxIterativeStepsReached extends IterativeClientException {
        private static final long serialVersionUID = 1;

        public MaxIterativeStepsReached() {
            super("Maxmimum steps reached");
        }
    }

    public static class NotAuthoritativeNorGlueRrFound extends IterativeClientException {
        private static final long serialVersionUID = 1;
        private final DnsName authoritativeZone;
        private final DnsMessage request;
        private final DnsQueryResult result;

        public NotAuthoritativeNorGlueRrFound(DnsMessage request2, DnsQueryResult result2, DnsName authoritativeZone2) {
            super("Did not receive an authoritative answer, nor did the result contain any glue records");
            this.request = request2;
            this.result = result2;
            this.authoritativeZone = authoritativeZone2;
        }

        public DnsMessage getRequest() {
            return this.request;
        }

        public DnsQueryResult getResult() {
            return this.result;
        }

        public DnsName getAuthoritativeZone() {
            return this.authoritativeZone;
        }
    }

    protected IterativeClientException(String message) {
        super(message);
    }
}
