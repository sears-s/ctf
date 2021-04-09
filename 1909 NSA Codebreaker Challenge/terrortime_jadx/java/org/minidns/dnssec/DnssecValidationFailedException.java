package org.minidns.dnssec;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsmessage.Question;
import org.minidns.record.Data;
import org.minidns.record.Record;

public class DnssecValidationFailedException extends IOException {
    private static final long serialVersionUID = 5413184667629832742L;

    public static class AuthorityDoesNotContainSoa extends DnssecValidationFailedException {
        private static final long serialVersionUID = 1;
        private final DnsMessage response;

        public AuthorityDoesNotContainSoa(DnsMessage response2) {
            super("Autority does not contain SOA");
            this.response = response2;
        }

        public DnsMessage getResponse() {
            return this.response;
        }
    }

    public static class DataMalformedException extends DnssecValidationFailedException {
        private static final long serialVersionUID = 1;
        private final byte[] data;

        public DataMalformedException(IOException exception, byte[] data2) {
            super("Malformed data", (Throwable) exception);
            this.data = data2;
        }

        public DataMalformedException(String message, IOException exception, byte[] data2) {
            super(message, (Throwable) exception);
            this.data = data2;
        }

        public byte[] getData() {
            return this.data;
        }
    }

    public static class DnssecInvalidKeySpecException extends DnssecValidationFailedException {
        private static final long serialVersionUID = 1;

        public DnssecInvalidKeySpecException(InvalidKeySpecException exception) {
            super("Invalid key spec", (Throwable) exception);
        }

        public DnssecInvalidKeySpecException(String message, InvalidKeySpecException exception, byte[] data) {
            super(message, (Throwable) exception);
        }
    }

    public DnssecValidationFailedException(Question question, String reason) {
        StringBuilder sb = new StringBuilder();
        sb.append("Validation of request to ");
        sb.append(question);
        sb.append(" failed: ");
        sb.append(reason);
        super(sb.toString());
    }

    public DnssecValidationFailedException(String message) {
        super(message);
    }

    public DnssecValidationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DnssecValidationFailedException(Record<? extends Data> record, String reason) {
        StringBuilder sb = new StringBuilder();
        sb.append("Validation of record ");
        sb.append(record);
        sb.append(" failed: ");
        sb.append(reason);
        super(sb.toString());
    }

    public DnssecValidationFailedException(List<Record<? extends Data>> records, String reason) {
        StringBuilder sb = new StringBuilder();
        sb.append("Validation of ");
        sb.append(records.size());
        sb.append(" ");
        sb.append(((Record) records.get(0)).type);
        sb.append(" record");
        sb.append(records.size() > 1 ? "s" : BuildConfig.FLAVOR);
        sb.append(" failed: ");
        sb.append(reason);
        super(sb.toString());
    }
}
