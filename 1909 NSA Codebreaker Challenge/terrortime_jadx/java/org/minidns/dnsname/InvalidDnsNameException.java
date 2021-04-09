package org.minidns.dnsname;

public abstract class InvalidDnsNameException extends IllegalStateException {
    private static final long serialVersionUID = 1;
    protected final String ace;

    public static class DNSNameTooLongException extends InvalidDnsNameException {
        private static final long serialVersionUID = 1;
        private final byte[] bytes;

        public DNSNameTooLongException(String ace, byte[] bytes2) {
            super(ace);
            this.bytes = bytes2;
        }

        public String getMessage() {
            StringBuilder sb = new StringBuilder();
            sb.append("The DNS name '");
            sb.append(this.ace);
            sb.append("' exceeds the maximum name length of ");
            sb.append(255);
            sb.append(" octets by ");
            sb.append(this.bytes.length - 255);
            sb.append(" octets.");
            return sb.toString();
        }
    }

    public static class LabelTooLongException extends InvalidDnsNameException {
        private static final long serialVersionUID = 1;
        private final String label;

        public LabelTooLongException(String ace, String label2) {
            super(ace);
            this.label = label2;
        }

        public String getMessage() {
            StringBuilder sb = new StringBuilder();
            sb.append("The DNS name '");
            sb.append(this.ace);
            sb.append("' contains the label '");
            sb.append(this.label);
            sb.append("' which exceeds the maximum label length of ");
            sb.append(63);
            sb.append(" octets by ");
            sb.append(this.label.length() - 63);
            sb.append(" octets.");
            return sb.toString();
        }
    }

    protected InvalidDnsNameException(String ace2) {
        this.ace = ace2;
    }
}
