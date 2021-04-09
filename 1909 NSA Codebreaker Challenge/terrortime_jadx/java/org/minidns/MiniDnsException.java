package org.minidns;

import java.io.IOException;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsqueryresult.DnsQueryResult;

public abstract class MiniDnsException extends IOException {
    private static final long serialVersionUID = 1;

    public static class ErrorResponseException extends MiniDnsException {
        private static final long serialVersionUID = 1;
        private final DnsMessage request;
        private final DnsQueryResult result;

        public ErrorResponseException(DnsMessage request2, DnsQueryResult result2) {
            StringBuilder sb = new StringBuilder();
            sb.append("Received ");
            sb.append(result2.response.responseCode);
            sb.append(" error response\n");
            sb.append(result2);
            super(sb.toString());
            this.request = request2;
            this.result = result2;
        }

        public DnsMessage getRequest() {
            return this.request;
        }

        public DnsQueryResult getResult() {
            return this.result;
        }
    }

    public static class IdMismatch extends MiniDnsException {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private static final long serialVersionUID = 1;
        private final DnsMessage request;
        private final DnsMessage response;

        static {
            Class<MiniDnsException> cls = MiniDnsException.class;
        }

        public IdMismatch(DnsMessage request2, DnsMessage response2) {
            super(getString(request2, response2));
            this.request = request2;
            this.response = response2;
        }

        public DnsMessage getRequest() {
            return this.request;
        }

        public DnsMessage getResponse() {
            return this.response;
        }

        private static String getString(DnsMessage request2, DnsMessage response2) {
            StringBuilder sb = new StringBuilder();
            sb.append("The response's ID doesn't matches the request ID. Request: ");
            sb.append(request2.id);
            sb.append(". Response: ");
            sb.append(response2.id);
            return sb.toString();
        }
    }

    public static class NoQueryPossibleException extends MiniDnsException {
        private static final long serialVersionUID = 1;
        private final DnsMessage request;

        public NoQueryPossibleException(DnsMessage request2) {
            super("No DNS server could be queried");
            this.request = request2;
        }

        public DnsMessage getRequest() {
            return this.request;
        }
    }

    public static class NullResultException extends MiniDnsException {
        private static final long serialVersionUID = 1;
        private final DnsMessage request;

        public NullResultException(DnsMessage request2) {
            super("The request yielded a 'null' result while resolving.");
            this.request = request2;
        }

        public DnsMessage getRequest() {
            return this.request;
        }
    }

    protected MiniDnsException(String message) {
        super(message);
    }
}
