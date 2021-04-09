package org.minidns;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.minidns.dnsname.DnsName;
import org.minidns.record.Data;
import org.minidns.record.Record;
import org.minidns.record.Record.CLASS;
import org.minidns.record.Record.TYPE;

public class RrSet {
    public final CLASS clazz;
    public final DnsName name;
    public final Set<Record<? extends Data>> records;
    public final TYPE type;

    public static class Builder {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private CLASS clazz;
        private DnsName name;
        Set<Record<? extends Data>> records;
        private TYPE type;

        static {
            Class<RrSet> cls = RrSet.class;
        }

        private Builder() {
            this.records = new LinkedHashSet(8);
        }

        public Builder addRecord(Record<? extends Data> record) {
            if (this.name == null) {
                this.name = record.name;
                this.type = record.type;
                this.clazz = record.clazz;
            } else if (!couldContain(record)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Can not add ");
                sb.append(record);
                sb.append(" to RRSet ");
                sb.append(this.name);
                sb.append(' ');
                sb.append(this.type);
                sb.append(' ');
                sb.append(this.clazz);
                throw new IllegalArgumentException(sb.toString());
            }
            boolean add = this.records.add(record);
            return this;
        }

        public boolean couldContain(Record<? extends Data> record) {
            DnsName dnsName = this.name;
            boolean z = true;
            if (dnsName == null) {
                return true;
            }
            if (!(dnsName.equals(record.name) && this.type == record.type && this.clazz == record.clazz)) {
                z = false;
            }
            return z;
        }

        public boolean addIfPossible(Record<? extends Data> record) {
            if (!couldContain(record)) {
                return false;
            }
            addRecord(record);
            return true;
        }

        public RrSet build() {
            DnsName dnsName = this.name;
            if (dnsName != null) {
                RrSet rrSet = new RrSet(dnsName, this.type, this.clazz, this.records);
                return rrSet;
            }
            throw new IllegalStateException();
        }
    }

    private RrSet(DnsName name2, TYPE type2, CLASS clazz2, Set<Record<? extends Data>> records2) {
        this.name = name2;
        this.type = type2;
        this.clazz = clazz2;
        this.records = Collections.unmodifiableSet(records2);
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name);
        sb.append(9);
        sb.append(this.clazz);
        sb.append(9);
        sb.append(this.type);
        sb.append(10);
        for (Record<?> record : this.records) {
            sb.append(record);
            sb.append(10);
        }
        return sb.toString();
    }

    public static Builder builder() {
        return new Builder();
    }
}
