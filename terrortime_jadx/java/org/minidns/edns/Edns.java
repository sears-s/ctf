package org.minidns.edns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.minidns.dnsname.DnsName;
import org.minidns.record.Data;
import org.minidns.record.OPT;
import org.minidns.record.Record;
import org.minidns.record.Record.TYPE;

public class Edns {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final int FLAG_DNSSEC_OK = 32768;
    public final boolean dnssecOk;
    public final int extendedRcode;
    public final int flags;
    private Record<OPT> optRecord;
    private String terminalOutputCache;
    public final int udpPayloadSize;
    public final List<EdnsOption> variablePart;
    public final int version;

    public static class Builder {
        /* access modifiers changed from: private */
        public boolean dnssecOk;
        /* access modifiers changed from: private */
        public int extendedRcode;
        /* access modifiers changed from: private */
        public int udpPayloadSize;
        /* access modifiers changed from: private */
        public List<EdnsOption> variablePart;
        /* access modifiers changed from: private */
        public int version;

        private Builder() {
        }

        public Builder setUdpPayloadSize(int udpPayloadSize2) {
            if (udpPayloadSize2 <= 65535) {
                this.udpPayloadSize = udpPayloadSize2;
                return this;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("UDP payload size must not be greater than 65536, was ");
            sb.append(udpPayloadSize2);
            throw new IllegalArgumentException(sb.toString());
        }

        public Builder setDnssecOk(boolean dnssecOk2) {
            this.dnssecOk = dnssecOk2;
            return this;
        }

        public Builder setDnssecOk() {
            this.dnssecOk = true;
            return this;
        }

        public Builder addEdnsOption(EdnsOption ednsOption) {
            if (this.variablePart == null) {
                this.variablePart = new ArrayList(4);
            }
            this.variablePart.add(ednsOption);
            return this;
        }

        public Edns build() {
            return new Edns(this);
        }
    }

    public enum OptionCode {
        UNKNOWN(-1, UnknownEdnsOption.class),
        NSID(3, Nsid.class);
        
        private static Map<Integer, OptionCode> INVERSE_LUT;
        public final int asInt;
        public final Class<? extends EdnsOption> clazz;

        static {
            int i;
            OptionCode[] values;
            INVERSE_LUT = new HashMap(values().length);
            for (OptionCode optionCode : values()) {
                INVERSE_LUT.put(Integer.valueOf(optionCode.asInt), optionCode);
            }
        }

        private OptionCode(int optionCode, Class<? extends EdnsOption> clazz2) {
            this.asInt = optionCode;
            this.clazz = clazz2;
        }

        public static OptionCode from(int optionCode) {
            OptionCode res = (OptionCode) INVERSE_LUT.get(Integer.valueOf(optionCode));
            if (res == null) {
                return UNKNOWN;
            }
            return res;
        }
    }

    public Edns(Record<OPT> optRecord2) {
        this.udpPayloadSize = optRecord2.clazzValue;
        this.extendedRcode = (int) ((optRecord2.ttl >> 8) & 255);
        this.version = (int) ((optRecord2.ttl >> 16) & 255);
        this.flags = ((int) optRecord2.ttl) & 65535;
        this.dnssecOk = (optRecord2.ttl & 32768) > 0;
        this.variablePart = ((OPT) optRecord2.payloadData).variablePart;
        this.optRecord = optRecord2;
    }

    public Edns(Builder builder) {
        this.udpPayloadSize = builder.udpPayloadSize;
        this.extendedRcode = builder.extendedRcode;
        this.version = builder.version;
        int flags2 = 0;
        if (builder.dnssecOk) {
            flags2 = 0 | 32768;
        }
        this.dnssecOk = builder.dnssecOk;
        this.flags = flags2;
        if (builder.variablePart != null) {
            this.variablePart = builder.variablePart;
        } else {
            this.variablePart = Collections.emptyList();
        }
    }

    public <O extends EdnsOption> O getEdnsOption(OptionCode optionCode) {
        for (EdnsOption o : this.variablePart) {
            if (o.getOptionCode().equals(optionCode)) {
                return o;
            }
        }
        return null;
    }

    public Record<OPT> asRecord() {
        if (this.optRecord == null) {
            Record record = new Record(DnsName.ROOT, TYPE.OPT, this.udpPayloadSize, ((long) this.flags) | ((long) (this.extendedRcode << 8)) | ((long) (this.version << 16)), new OPT(this.variablePart));
            this.optRecord = record;
        }
        return this.optRecord;
    }

    public String asTerminalOutput() {
        if (this.terminalOutputCache == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("EDNS: version: ");
            sb.append(this.version);
            sb.append(", flags:");
            if (this.dnssecOk) {
                sb.append(" do");
            }
            sb.append("; udp: ");
            sb.append(this.udpPayloadSize);
            if (!this.variablePart.isEmpty()) {
                sb.append(10);
                Iterator<EdnsOption> it = this.variablePart.iterator();
                while (it.hasNext()) {
                    EdnsOption edns = (EdnsOption) it.next();
                    sb.append(edns.getOptionCode());
                    sb.append(": ");
                    sb.append(edns.asTerminalOutput());
                    if (it.hasNext()) {
                        sb.append(10);
                    }
                }
            }
            this.terminalOutputCache = sb.toString();
        }
        return this.terminalOutputCache;
    }

    public String toString() {
        return asTerminalOutput();
    }

    public static Edns fromRecord(Record<? extends Data> record) {
        if (record.type != TYPE.OPT) {
            return null;
        }
        return new Edns(record);
    }

    public static Builder builder() {
        return new Builder();
    }
}
