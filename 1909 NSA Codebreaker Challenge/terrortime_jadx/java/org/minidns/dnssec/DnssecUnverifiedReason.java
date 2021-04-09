package org.minidns.dnssec;

import java.util.Collections;
import java.util.List;
import org.minidns.constants.DnssecConstants.DigestAlgorithm;
import org.minidns.dnsmessage.Question;
import org.minidns.dnsname.DnsName;
import org.minidns.record.DNSKEY;
import org.minidns.record.Data;
import org.minidns.record.RRSIG;
import org.minidns.record.Record;
import org.minidns.record.Record.TYPE;

public abstract class DnssecUnverifiedReason {

    public static class AlgorithmExceptionThrownReason extends DnssecUnverifiedReason {
        private final int algorithmNumber;
        private final String kind;
        private final Exception reason;
        private final Record<? extends Data> record;

        public AlgorithmExceptionThrownReason(DigestAlgorithm algorithm, String kind2, Record<? extends Data> record2, Exception reason2) {
            this.algorithmNumber = algorithm.value;
            this.kind = kind2;
            this.record = record2;
            this.reason = reason2;
        }

        public String getReasonString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.kind);
            sb.append(" algorithm ");
            sb.append(this.algorithmNumber);
            sb.append(" threw exception while verifying ");
            sb.append(this.record.name);
            sb.append(": ");
            sb.append(this.reason);
            return sb.toString();
        }
    }

    public static class AlgorithmNotSupportedReason extends DnssecUnverifiedReason {
        private final String algorithm;
        private final Record<? extends Data> record;
        private final TYPE type;

        public AlgorithmNotSupportedReason(byte algorithm2, TYPE type2, Record<? extends Data> record2) {
            this.algorithm = Integer.toString(algorithm2 & 255);
            this.type = type2;
            this.record = record2;
        }

        public String getReasonString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.type.name());
            sb.append(" algorithm ");
            sb.append(this.algorithm);
            sb.append(" required to verify ");
            sb.append(this.record.name);
            sb.append(" is unknown or not supported by platform");
            return sb.toString();
        }
    }

    public static class ConflictsWithSep extends DnssecUnverifiedReason {
        private final Record<DNSKEY> record;

        public ConflictsWithSep(Record<DNSKEY> record2) {
            this.record = record2;
        }

        public String getReasonString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Zone ");
            sb.append(this.record.name.ace);
            sb.append(" is in list of known SEPs, but DNSKEY from response mismatches!");
            return sb.toString();
        }
    }

    public static class NSECDoesNotMatchReason extends DnssecUnverifiedReason {
        private final Question question;
        private final Record<? extends Data> record;

        public NSECDoesNotMatchReason(Question question2, Record<? extends Data> record2) {
            this.question = question2;
            this.record = record2;
        }

        public String getReasonString() {
            StringBuilder sb = new StringBuilder();
            sb.append("NSEC ");
            sb.append(this.record.name);
            sb.append(" does nat match question for ");
            sb.append(this.question.type);
            sb.append(" at ");
            sb.append(this.question.name);
            return sb.toString();
        }
    }

    public static class NoActiveSignaturesReason extends DnssecUnverifiedReason {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final List<RRSIG> outdatedRrSigs;
        private final Question question;

        static {
            Class<DnssecUnverifiedReason> cls = DnssecUnverifiedReason.class;
        }

        public NoActiveSignaturesReason(Question question2, List<RRSIG> outdatedRrSigs2) {
            this.question = question2;
            this.outdatedRrSigs = Collections.unmodifiableList(outdatedRrSigs2);
        }

        public String getReasonString() {
            StringBuilder sb = new StringBuilder();
            sb.append("No currently active signatures were attached to answer on question for ");
            sb.append(this.question.type);
            sb.append(" at ");
            sb.append(this.question.name);
            return sb.toString();
        }

        public List<RRSIG> getOutdatedRrSigs() {
            return this.outdatedRrSigs;
        }
    }

    public static class NoRootSecureEntryPointReason extends DnssecUnverifiedReason {
        public String getReasonString() {
            return "No secure entry point was found for the root zone (\"Did you forget to configure a root SEP?\")";
        }
    }

    public static class NoSecureEntryPointReason extends DnssecUnverifiedReason {
        private final DnsName zone;

        public NoSecureEntryPointReason(DnsName zone2) {
            this.zone = zone2;
        }

        public String getReasonString() {
            StringBuilder sb = new StringBuilder();
            sb.append("No secure entry point was found for zone ");
            sb.append(this.zone);
            return sb.toString();
        }
    }

    public static class NoSignaturesReason extends DnssecUnverifiedReason {
        private final Question question;

        public NoSignaturesReason(Question question2) {
            this.question = question2;
        }

        public String getReasonString() {
            StringBuilder sb = new StringBuilder();
            sb.append("No signatures were attached to answer on question for ");
            sb.append(this.question.type);
            sb.append(" at ");
            sb.append(this.question.name);
            return sb.toString();
        }
    }

    public static class NoTrustAnchorReason extends DnssecUnverifiedReason {
        private final DnsName zone;

        public NoTrustAnchorReason(DnsName zone2) {
            this.zone = zone2;
        }

        public String getReasonString() {
            StringBuilder sb = new StringBuilder();
            sb.append("No trust anchor was found for zone ");
            sb.append(this.zone);
            sb.append(". Try enabling DLV");
            return sb.toString();
        }
    }

    public abstract String getReasonString();

    public String toString() {
        return getReasonString();
    }

    public int hashCode() {
        return getReasonString().hashCode();
    }

    public boolean equals(Object obj) {
        return (obj instanceof DnssecUnverifiedReason) && ((DnssecUnverifiedReason) obj).getReasonString().equals(getReasonString());
    }
}
