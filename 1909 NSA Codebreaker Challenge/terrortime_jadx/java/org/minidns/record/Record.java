package org.minidns.record;

import android.support.v4.view.InputDeviceCompat;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.minidns.dnsmessage.DnsMessage.Builder;
import org.minidns.dnsmessage.Question;
import org.minidns.dnsname.DnsName;
import org.minidns.record.Data;

public final class Record<D extends Data> {
    private transient byte[] bytes;
    public final CLASS clazz;
    public final int clazzValue;
    private transient Integer hashCodeCache;
    public final DnsName name;
    public final D payloadData;
    public final long ttl;
    public final TYPE type;
    public final boolean unicastQuery;

    public enum CLASS {
        IN(1),
        CH(3),
        HS(4),
        NONE(254),
        ANY(255);
        
        private static final HashMap<Integer, CLASS> INVERSE_LUT = null;
        private final int value;

        static {
            int i;
            CLASS[] values;
            INVERSE_LUT = new HashMap<>();
            for (CLASS c : values()) {
                INVERSE_LUT.put(Integer.valueOf(c.getValue()), c);
            }
        }

        private CLASS(int value2) {
            this.value = value2;
        }

        public int getValue() {
            return this.value;
        }

        public static CLASS getClass(int value2) {
            return (CLASS) INVERSE_LUT.get(Integer.valueOf(value2));
        }
    }

    public enum TYPE {
        UNKNOWN(-1),
        A(1, A.class),
        NS(2, NS.class),
        MD(3),
        MF(4),
        CNAME(5, CNAME.class),
        SOA(6, SOA.class),
        MB(7),
        MG(8),
        MR(9),
        NULL(10),
        WKS(11),
        PTR(12, PTR.class),
        HINFO(13),
        MINFO(14),
        MX(15, MX.class),
        TXT(16, TXT.class),
        RP(17),
        AFSDB(18),
        X25(19),
        ISDN(20),
        RT(21),
        NSAP(22),
        NSAP_PTR(23),
        SIG(24),
        KEY(25),
        PX(26),
        GPOS(27),
        AAAA(28, AAAA.class),
        LOC(29),
        NXT(30),
        EID(31),
        NIMLOC(32),
        SRV(33, SRV.class),
        ATMA(34),
        NAPTR(35),
        KX(36),
        CERT(37),
        A6(38),
        DNAME(39, DNAME.class),
        SINK(40),
        OPT(41, OPT.class),
        APL(42),
        DS(43, DS.class),
        SSHFP(44),
        IPSECKEY(45),
        RRSIG(46, RRSIG.class),
        NSEC(47, NSEC.class),
        DNSKEY(48, DNSKEY.class),
        DHCID(49),
        NSEC3(50, NSEC3.class),
        NSEC3PARAM(51, NSEC3PARAM.class),
        TLSA(52, TLSA.class),
        HIP(55),
        NINFO(56),
        RKEY(57),
        TALINK(58),
        CDS(59),
        CDNSKEY(60),
        OPENPGPKEY(61, OPENPGPKEY.class),
        CSYNC(62),
        SPF(99),
        UINFO(100),
        UID(101),
        GID(102),
        UNSPEC(103),
        NID(104),
        L32(105),
        L64(106),
        LP(107),
        EUI48(108),
        EUI64(109),
        TKEY(249),
        TSIG(Callback.DEFAULT_SWIPE_ANIMATION_DURATION),
        IXFR(251),
        AXFR(252),
        MAILB(253),
        MAILA(254),
        ANY(255),
        URI(256),
        CAA(InputDeviceCompat.SOURCE_KEYBOARD),
        TA(32768),
        DLV(32769, DLV.class);
        
        private static final Map<Class<?>, TYPE> DATA_LUT = null;
        private static final Map<Integer, TYPE> INVERSE_LUT = null;
        /* access modifiers changed from: private */
        public final Class<?> dataClass;
        private final int value;

        static {
            int i;
            TYPE[] values;
            INVERSE_LUT = new HashMap();
            DATA_LUT = new HashMap();
            for (TYPE t : values()) {
                INVERSE_LUT.put(Integer.valueOf(t.getValue()), t);
                Class<?> cls = t.dataClass;
                if (cls != null) {
                    DATA_LUT.put(cls, t);
                }
            }
        }

        private TYPE(int value2) {
            this(r2, r3, value2, null);
        }

        private <D extends Data> TYPE(int value2, Class<D> dataClass2) {
            this.value = value2;
            this.dataClass = dataClass2;
        }

        public int getValue() {
            return this.value;
        }

        public <D extends Data> Class<D> getDataClass() {
            return this.dataClass;
        }

        public static TYPE getType(int value2) {
            TYPE type = (TYPE) INVERSE_LUT.get(Integer.valueOf(value2));
            if (type == null) {
                return UNKNOWN;
            }
            return type;
        }

        public static <D extends Data> TYPE getType(Class<D> dataClass2) {
            return (TYPE) DATA_LUT.get(dataClass2);
        }
    }

    public static Record<Data> parse(DataInputStream dis, byte[] data) throws IOException {
        Data payloadData2;
        DataInputStream dataInputStream = dis;
        byte[] bArr = data;
        DnsName name2 = DnsName.parse(dis, data);
        TYPE type2 = TYPE.getType(dis.readUnsignedShort());
        int clazzValue2 = dis.readUnsignedShort();
        CLASS clazz2 = CLASS.getClass(clazzValue2 & 32767);
        boolean unicastQuery2 = (32768 & clazzValue2) > 0;
        long ttl2 = (((long) dis.readUnsignedShort()) << 16) + ((long) dis.readUnsignedShort());
        int payloadLength = dis.readUnsignedShort();
        switch (type2) {
            case SOA:
                payloadData2 = SOA.parse(dis, data);
                break;
            case SRV:
                payloadData2 = SRV.parse(dis, data);
                break;
            case MX:
                payloadData2 = MX.parse(dis, data);
                break;
            case AAAA:
                payloadData2 = AAAA.parse(dis);
                break;
            case A:
                payloadData2 = A.parse(dis);
                break;
            case NS:
                payloadData2 = NS.parse(dis, data);
                break;
            case CNAME:
                payloadData2 = CNAME.parse(dis, data);
                break;
            case DNAME:
                payloadData2 = DNAME.parse(dis, data);
                break;
            case PTR:
                payloadData2 = PTR.parse(dis, data);
                break;
            case TXT:
                payloadData2 = TXT.parse(dataInputStream, payloadLength);
                break;
            case OPT:
                payloadData2 = OPT.parse(dataInputStream, payloadLength);
                break;
            case DNSKEY:
                payloadData2 = DNSKEY.parse(dataInputStream, payloadLength);
                break;
            case RRSIG:
                payloadData2 = RRSIG.parse(dataInputStream, bArr, payloadLength);
                break;
            case DS:
                payloadData2 = DS.parse(dataInputStream, payloadLength);
                break;
            case NSEC:
                payloadData2 = NSEC.parse(dataInputStream, bArr, payloadLength);
                break;
            case NSEC3:
                payloadData2 = NSEC3.parse(dataInputStream, payloadLength);
                break;
            case NSEC3PARAM:
                payloadData2 = NSEC3PARAM.parse(dis);
                break;
            case TLSA:
                payloadData2 = TLSA.parse(dataInputStream, payloadLength);
                break;
            case OPENPGPKEY:
                payloadData2 = OPENPGPKEY.parse(dataInputStream, payloadLength);
                break;
            case DLV:
                payloadData2 = DLV.parse(dataInputStream, payloadLength);
                break;
            default:
                payloadData2 = UNKNOWN.parse(dataInputStream, payloadLength, type2);
                break;
        }
        int i = payloadLength;
        Record record = new Record(name2, type2, clazz2, clazzValue2, ttl2, payloadData2, unicastQuery2);
        return record;
    }

    public Record(DnsName name2, TYPE type2, CLASS clazz2, long ttl2, D payloadData2, boolean unicastQuery2) {
        this(name2, type2, clazz2, clazz2.getValue() + (unicastQuery2 ? 32768 : 0), ttl2, payloadData2, unicastQuery2);
    }

    public Record(String name2, TYPE type2, CLASS clazz2, long ttl2, D payloadData2, boolean unicastQuery2) {
        this(DnsName.from(name2), type2, clazz2, ttl2, payloadData2, unicastQuery2);
    }

    public Record(String name2, TYPE type2, int clazzValue2, long ttl2, D payloadData2) {
        this(DnsName.from(name2), type2, CLASS.NONE, clazzValue2, ttl2, payloadData2, false);
    }

    public Record(DnsName name2, TYPE type2, int clazzValue2, long ttl2, D payloadData2) {
        this(name2, type2, CLASS.NONE, clazzValue2, ttl2, payloadData2, false);
    }

    private Record(DnsName name2, TYPE type2, CLASS clazz2, int clazzValue2, long ttl2, D payloadData2, boolean unicastQuery2) {
        this.name = name2;
        this.type = type2;
        this.clazz = clazz2;
        this.clazzValue = clazzValue2;
        this.ttl = ttl2;
        this.payloadData = payloadData2;
        this.unicastQuery = unicastQuery2;
    }

    public void toOutputStream(OutputStream outputStream) throws IOException {
        if (this.payloadData != null) {
            DataOutputStream dos = new DataOutputStream(outputStream);
            this.name.writeToStream(dos);
            dos.writeShort(this.type.getValue());
            dos.writeShort(this.clazzValue);
            dos.writeInt((int) this.ttl);
            dos.writeShort(this.payloadData.length());
            this.payloadData.toOutputStream(dos);
            return;
        }
        throw new IllegalStateException("Empty Record has no byte representation");
    }

    public byte[] toByteArray() {
        if (this.bytes == null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(this.name.size() + 10 + this.payloadData.length());
            try {
                toOutputStream(new DataOutputStream(baos));
                this.bytes = baos.toByteArray();
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }
        return (byte[]) this.bytes.clone();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name.getRawAce());
        sb.append(".\t");
        sb.append(this.ttl);
        sb.append(9);
        sb.append(this.clazz);
        sb.append(9);
        sb.append(this.type);
        sb.append(9);
        sb.append(this.payloadData);
        return sb.toString();
    }

    public boolean isAnswer(Question q) {
        return (q.type == this.type || q.type == TYPE.ANY) && (q.clazz == this.clazz || q.clazz == CLASS.ANY) && q.name.equals(this.name);
    }

    public boolean isUnicastQuery() {
        return this.unicastQuery;
    }

    public D getPayload() {
        return this.payloadData;
    }

    public long getTtl() {
        return this.ttl;
    }

    public Question getQuestion() {
        int i = AnonymousClass1.$SwitchMap$org$minidns$record$Record$TYPE[this.type.ordinal()];
        if (i == 11) {
            return null;
        }
        if (i != 13) {
            return new Question(this.name, this.type, this.clazz);
        }
        return new Question(this.name, ((RRSIG) this.payloadData).typeCovered, this.clazz);
    }

    public Builder getQuestionMessage() {
        Question question = getQuestion();
        if (question == null) {
            return null;
        }
        return question.asMessageBuilder();
    }

    public int hashCode() {
        if (this.hashCodeCache == null) {
            this.hashCodeCache = Integer.valueOf((((((((1 * 37) + this.name.hashCode()) * 37) + this.type.hashCode()) * 37) + this.clazz.hashCode()) * 37) + this.payloadData.hashCode());
        }
        return this.hashCodeCache.intValue();
    }

    public boolean equals(Object other) {
        if (!(other instanceof Record)) {
            return false;
        }
        if (other == this) {
            return true;
        }
        Record<?> otherRecord = (Record) other;
        if (this.name.equals(otherRecord.name) && this.type == otherRecord.type && this.clazz == otherRecord.clazz && this.payloadData.equals(otherRecord.payloadData)) {
            return true;
        }
        return false;
    }

    public <E extends Data> Record<E> ifPossibleAs(Class<E> dataClass) {
        if (this.type.dataClass == dataClass) {
            return this;
        }
        return null;
    }

    public <E extends Data> Record<E> as(Class<E> dataClass) {
        Record<E> eRecord = ifPossibleAs(dataClass);
        if (eRecord != null) {
            return eRecord;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("The instance ");
        sb.append(this);
        sb.append(" can not be cast to a Record with");
        sb.append(dataClass);
        throw new IllegalArgumentException(sb.toString());
    }

    public static <E extends Data> void filter(Collection<Record<E>> result, Class<E> dataClass, Collection<Record<? extends Data>> input) {
        for (Record<? extends Data> record : input) {
            Record<E> filteredRecord = record.ifPossibleAs(dataClass);
            if (filteredRecord != null) {
                result.add(filteredRecord);
            }
        }
    }

    public static <E extends Data> List<Record<E>> filter(Class<E> dataClass, Collection<Record<? extends Data>> input) {
        List<Record<E>> result = new ArrayList<>(input.size());
        filter(result, dataClass, input);
        return result;
    }
}
