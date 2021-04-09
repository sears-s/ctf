package org.minidns.dnsmessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.minidns.edns.Edns;
import org.minidns.record.Data;
import org.minidns.record.OPT;
import org.minidns.record.Record;
import org.minidns.record.Record.TYPE;

public class DnsMessage {
    private static final Logger LOGGER = Logger.getLogger(DnsMessage.class.getName());
    public final List<Record<? extends Data>> additionalSection;
    public final List<Record<? extends Data>> answerSection;
    private long answersMinTtlCache;
    public final boolean authenticData;
    public final boolean authoritativeAnswer;
    public final List<Record<? extends Data>> authoritySection;
    private byte[] byteCache;
    public final boolean checkingDisabled;
    private Edns edns;
    private transient Integer hashCodeCache;
    public final int id;
    private DnsMessage normalizedVersionCache;
    public final OPCODE opcode;
    public final int optRrPosition;
    public final boolean qr;
    public final List<Question> questions;
    public final long receiveTimestamp;
    public final boolean recursionAvailable;
    public final boolean recursionDesired;
    public final RESPONSE_CODE responseCode;
    private String terminalOutputCache;
    private String toStringCache;
    public final boolean truncated;

    /* renamed from: org.minidns.dnsmessage.DnsMessage$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$minidns$dnsmessage$DnsMessage$SectionName = new int[SectionName.values().length];

        static {
            try {
                $SwitchMap$org$minidns$dnsmessage$DnsMessage$SectionName[SectionName.answer.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$minidns$dnsmessage$DnsMessage$SectionName[SectionName.authority.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$minidns$dnsmessage$DnsMessage$SectionName[SectionName.additional.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public static class Builder {
        /* access modifiers changed from: private */
        public List<Record<? extends Data>> additionalSection;
        /* access modifiers changed from: private */
        public List<Record<? extends Data>> answerSection;
        /* access modifiers changed from: private */
        public boolean authenticData;
        /* access modifiers changed from: private */
        public boolean authoritativeAnswer;
        /* access modifiers changed from: private */
        public List<Record<? extends Data>> authoritySection;
        /* access modifiers changed from: private */
        public boolean checkingDisabled;
        /* access modifiers changed from: private */
        public org.minidns.edns.Edns.Builder ednsBuilder;
        /* access modifiers changed from: private */
        public int id;
        /* access modifiers changed from: private */
        public OPCODE opcode;
        /* access modifiers changed from: private */
        public boolean query;
        /* access modifiers changed from: private */
        public List<Question> questions;
        /* access modifiers changed from: private */
        public long receiveTimestamp;
        /* access modifiers changed from: private */
        public boolean recursionAvailable;
        /* access modifiers changed from: private */
        public boolean recursionDesired;
        /* access modifiers changed from: private */
        public RESPONSE_CODE responseCode;
        /* access modifiers changed from: private */
        public boolean truncated;

        /* synthetic */ Builder(AnonymousClass1 x0) {
            this();
        }

        /* synthetic */ Builder(DnsMessage x0, AnonymousClass1 x1) {
            this(x0);
        }

        private Builder() {
            this.opcode = OPCODE.QUERY;
            this.responseCode = RESPONSE_CODE.NO_ERROR;
            this.receiveTimestamp = -1;
        }

        private Builder(DnsMessage message) {
            this.opcode = OPCODE.QUERY;
            this.responseCode = RESPONSE_CODE.NO_ERROR;
            this.receiveTimestamp = -1;
            this.id = message.id;
            this.opcode = message.opcode;
            this.responseCode = message.responseCode;
            this.query = message.qr;
            this.authoritativeAnswer = message.authoritativeAnswer;
            this.truncated = message.truncated;
            this.recursionDesired = message.recursionDesired;
            this.recursionAvailable = message.recursionAvailable;
            this.authenticData = message.authenticData;
            this.checkingDisabled = message.checkingDisabled;
            this.receiveTimestamp = message.receiveTimestamp;
            this.questions = new ArrayList(message.questions.size());
            this.questions.addAll(message.questions);
            this.answerSection = new ArrayList(message.answerSection.size());
            this.answerSection.addAll(message.answerSection);
            this.authoritySection = new ArrayList(message.authoritySection.size());
            this.authoritySection.addAll(message.authoritySection);
            this.additionalSection = new ArrayList(message.additionalSection.size());
            this.additionalSection.addAll(message.additionalSection);
        }

        public Builder setId(int id2) {
            this.id = 65535 & id2;
            return this;
        }

        public Builder setOpcode(OPCODE opcode2) {
            this.opcode = opcode2;
            return this;
        }

        public Builder setResponseCode(RESPONSE_CODE responseCode2) {
            this.responseCode = responseCode2;
            return this;
        }

        public Builder setQrFlag(boolean query2) {
            this.query = query2;
            return this;
        }

        public Builder setAuthoritativeAnswer(boolean authoritativeAnswer2) {
            this.authoritativeAnswer = authoritativeAnswer2;
            return this;
        }

        public Builder setTruncated(boolean truncated2) {
            this.truncated = truncated2;
            return this;
        }

        public Builder setRecursionDesired(boolean recursionDesired2) {
            this.recursionDesired = recursionDesired2;
            return this;
        }

        public Builder setRecursionAvailable(boolean recursionAvailable2) {
            this.recursionAvailable = recursionAvailable2;
            return this;
        }

        public Builder setAuthenticData(boolean authenticData2) {
            this.authenticData = authenticData2;
            return this;
        }

        @Deprecated
        public Builder setCheckDisabled(boolean checkingDisabled2) {
            this.checkingDisabled = checkingDisabled2;
            return this;
        }

        public Builder setCheckingDisabled(boolean checkingDisabled2) {
            this.checkingDisabled = checkingDisabled2;
            return this;
        }

        public void copyFlagsFrom(DnsMessage dnsMessage) {
            this.query = dnsMessage.qr;
            this.authoritativeAnswer = dnsMessage.authenticData;
            this.truncated = dnsMessage.truncated;
            this.recursionDesired = dnsMessage.recursionDesired;
            this.recursionAvailable = dnsMessage.recursionAvailable;
            this.authenticData = dnsMessage.authenticData;
            this.checkingDisabled = dnsMessage.checkingDisabled;
        }

        public Builder setReceiveTimestamp(long receiveTimestamp2) {
            this.receiveTimestamp = receiveTimestamp2;
            return this;
        }

        public Builder addQuestion(Question question) {
            if (this.questions == null) {
                this.questions = new ArrayList(1);
            }
            this.questions.add(question);
            return this;
        }

        public Builder setQuestions(List<Question> questions2) {
            this.questions = questions2;
            return this;
        }

        public Builder setQuestion(Question question) {
            this.questions = new ArrayList(1);
            this.questions.add(question);
            return this;
        }

        public Builder addAnswer(Record<? extends Data> answer) {
            if (this.answerSection == null) {
                this.answerSection = new ArrayList(1);
            }
            this.answerSection.add(answer);
            return this;
        }

        public Builder addAnswers(Collection<Record<? extends Data>> records) {
            if (this.answerSection == null) {
                this.answerSection = new ArrayList(records.size());
            }
            this.answerSection.addAll(records);
            return this;
        }

        public Builder setAnswers(Collection<Record<? extends Data>> records) {
            this.answerSection = new ArrayList(records.size());
            this.answerSection.addAll(records);
            return this;
        }

        public List<Record<? extends Data>> getAnswers() {
            List<Record<? extends Data>> list = this.answerSection;
            if (list == null) {
                return Collections.emptyList();
            }
            return list;
        }

        public Builder addNameserverRecords(Record<? extends Data> record) {
            if (this.authoritySection == null) {
                this.authoritySection = new ArrayList(8);
            }
            this.authoritySection.add(record);
            return this;
        }

        public Builder setNameserverRecords(Collection<Record<? extends Data>> records) {
            this.authoritySection = new ArrayList(records.size());
            this.authoritySection.addAll(records);
            return this;
        }

        public Builder setAdditionalResourceRecords(Collection<Record<? extends Data>> records) {
            this.additionalSection = new ArrayList(records.size());
            this.additionalSection.addAll(records);
            return this;
        }

        public Builder addAdditionalResourceRecord(Record<? extends Data> record) {
            if (this.additionalSection == null) {
                this.additionalSection = new ArrayList();
            }
            this.additionalSection.add(record);
            return this;
        }

        public Builder addAdditionalResourceRecords(List<Record<? extends Data>> records) {
            if (this.additionalSection == null) {
                this.additionalSection = new ArrayList(records.size());
            }
            this.additionalSection.addAll(records);
            return this;
        }

        public List<Record<? extends Data>> getAdditionalResourceRecords() {
            List<Record<? extends Data>> list = this.additionalSection;
            if (list == null) {
                return Collections.emptyList();
            }
            return list;
        }

        public org.minidns.edns.Edns.Builder getEdnsBuilder() {
            if (this.ednsBuilder == null) {
                this.ednsBuilder = Edns.builder();
            }
            return this.ednsBuilder;
        }

        public DnsMessage build() {
            return new DnsMessage(this);
        }

        /* access modifiers changed from: private */
        public void writeToStringBuilder(StringBuilder sb) {
            sb.append('(');
            sb.append(this.id);
            sb.append(' ');
            sb.append(this.opcode);
            sb.append(' ');
            sb.append(this.responseCode);
            sb.append(' ');
            if (this.query) {
                sb.append("resp[qr=1]");
            } else {
                sb.append("query[qr=0]");
            }
            if (this.authoritativeAnswer) {
                sb.append(" aa");
            }
            if (this.truncated) {
                sb.append(" tr");
            }
            if (this.recursionDesired) {
                sb.append(" rd");
            }
            if (this.recursionAvailable) {
                sb.append(" ra");
            }
            if (this.authenticData) {
                sb.append(" ad");
            }
            if (this.checkingDisabled) {
                sb.append(" cd");
            }
            sb.append(")\n");
            List<Question> list = this.questions;
            String str = "]\n";
            if (list != null) {
                for (Question question : list) {
                    sb.append("[Q: ");
                    sb.append(question);
                    sb.append(str);
                }
            }
            List<Record<? extends Data>> list2 = this.answerSection;
            if (list2 != null) {
                for (Record<? extends Data> record : list2) {
                    sb.append("[A: ");
                    sb.append(record);
                    sb.append(str);
                }
            }
            List<Record<? extends Data>> list3 = this.authoritySection;
            if (list3 != null) {
                for (Record<? extends Data> record2 : list3) {
                    sb.append("[N: ");
                    sb.append(record2);
                    sb.append(str);
                }
            }
            List<Record<? extends Data>> list4 = this.additionalSection;
            if (list4 != null) {
                for (Record<? extends Data> record3 : list4) {
                    sb.append("[X: ");
                    Edns edns = Edns.fromRecord(record3);
                    if (edns != null) {
                        sb.append(edns.toString());
                    } else {
                        sb.append(record3);
                    }
                    sb.append(str);
                }
            }
            if (sb.charAt(sb.length() - 1) == 10) {
                sb.setLength(sb.length() - 1);
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("Builder of DnsMessage");
            writeToStringBuilder(sb);
            return sb.toString();
        }
    }

    public enum OPCODE {
        QUERY,
        INVERSE_QUERY,
        STATUS,
        UNASSIGNED3,
        NOTIFY,
        UPDATE;
        
        private static final OPCODE[] INVERSE_LUT = null;
        private final byte value;

        static {
            int i;
            INVERSE_LUT = new OPCODE[values().length];
            OPCODE[] values = values();
            int length = values.length;
            while (i < length) {
                OPCODE opcode = values[i];
                if (INVERSE_LUT[opcode.getValue()] == null) {
                    INVERSE_LUT[opcode.getValue()] = opcode;
                    i++;
                } else {
                    throw new IllegalStateException();
                }
            }
        }

        public byte getValue() {
            return this.value;
        }

        public static OPCODE getOpcode(int value2) throws IllegalArgumentException {
            if (value2 < 0 || value2 > 15) {
                throw new IllegalArgumentException();
            }
            OPCODE[] opcodeArr = INVERSE_LUT;
            if (value2 >= opcodeArr.length) {
                return null;
            }
            return opcodeArr[value2];
        }
    }

    public enum RESPONSE_CODE {
        NO_ERROR(0),
        FORMAT_ERR(1),
        SERVER_FAIL(2),
        NX_DOMAIN(3),
        NO_IMP(4),
        REFUSED(5),
        YXDOMAIN(6),
        YXRRSET(7),
        NXRRSET(8),
        NOT_AUTH(9),
        NOT_ZONE(10),
        BADVERS_BADSIG(16),
        BADKEY(17),
        BADTIME(18),
        BADMODE(19),
        BADNAME(20),
        BADALG(21),
        BADTRUNC(22),
        BADCOOKIE(23);
        
        private static final Map<Integer, RESPONSE_CODE> INVERSE_LUT = null;
        private final byte value;

        static {
            int i;
            RESPONSE_CODE[] values;
            INVERSE_LUT = new HashMap(values().length);
            for (RESPONSE_CODE responseCode : values()) {
                INVERSE_LUT.put(Integer.valueOf(responseCode.value), responseCode);
            }
        }

        private RESPONSE_CODE(int value2) {
            this.value = (byte) value2;
        }

        public byte getValue() {
            return this.value;
        }

        public static RESPONSE_CODE getResponseCode(int value2) throws IllegalArgumentException {
            if (value2 >= 0 && value2 <= 65535) {
                return (RESPONSE_CODE) INVERSE_LUT.get(Integer.valueOf(value2));
            }
            throw new IllegalArgumentException();
        }
    }

    private enum SectionName {
        answer,
        authority,
        additional
    }

    protected DnsMessage(Builder builder) {
        this.answersMinTtlCache = -1;
        this.id = builder.id;
        this.opcode = builder.opcode;
        this.responseCode = builder.responseCode;
        this.receiveTimestamp = builder.receiveTimestamp;
        this.qr = builder.query;
        this.authoritativeAnswer = builder.authoritativeAnswer;
        this.truncated = builder.truncated;
        this.recursionDesired = builder.recursionDesired;
        this.recursionAvailable = builder.recursionAvailable;
        this.authenticData = builder.authenticData;
        this.checkingDisabled = builder.checkingDisabled;
        if (builder.questions == null) {
            this.questions = Collections.emptyList();
        } else {
            List<Question> q = new ArrayList<>(builder.questions.size());
            q.addAll(builder.questions);
            this.questions = Collections.unmodifiableList(q);
        }
        if (builder.answerSection == null) {
            this.answerSection = Collections.emptyList();
        } else {
            List<Record<? extends Data>> a = new ArrayList<>(builder.answerSection.size());
            a.addAll(builder.answerSection);
            this.answerSection = Collections.unmodifiableList(a);
        }
        if (builder.authoritySection == null) {
            this.authoritySection = Collections.emptyList();
        } else {
            List<Record<? extends Data>> n = new ArrayList<>(builder.authoritySection.size());
            n.addAll(builder.authoritySection);
            this.authoritySection = Collections.unmodifiableList(n);
        }
        if (builder.additionalSection == null && builder.ednsBuilder == null) {
            this.additionalSection = Collections.emptyList();
        } else {
            int size = 0;
            if (builder.additionalSection != null) {
                size = 0 + builder.additionalSection.size();
            }
            if (builder.ednsBuilder != null) {
                size++;
            }
            List<Record<? extends Data>> a2 = new ArrayList<>(size);
            if (builder.additionalSection != null) {
                a2.addAll(builder.additionalSection);
            }
            if (builder.ednsBuilder != null) {
                Edns edns2 = builder.ednsBuilder.build();
                this.edns = edns2;
                a2.add(edns2.asRecord());
            }
            this.additionalSection = Collections.unmodifiableList(a2);
        }
        this.optRrPosition = getOptRrPosition(this.additionalSection);
        int i = this.optRrPosition;
        if (i != -1) {
            while (true) {
                i++;
                if (i >= this.additionalSection.size()) {
                    return;
                }
                if (((Record) this.additionalSection.get(i)).type == TYPE.OPT) {
                    throw new IllegalArgumentException("There must be only one OPT pseudo RR in the additional section");
                }
            }
        }
    }

    public DnsMessage(byte[] data) throws IOException {
        this.answersMinTtlCache = -1;
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        this.id = dis.readUnsignedShort();
        int header = dis.readUnsignedShort();
        boolean z = true;
        this.qr = ((header >> 15) & 1) == 1;
        this.opcode = OPCODE.getOpcode((header >> 11) & 15);
        this.authoritativeAnswer = ((header >> 10) & 1) == 1;
        this.truncated = ((header >> 9) & 1) == 1;
        this.recursionDesired = ((header >> 8) & 1) == 1;
        this.recursionAvailable = ((header >> 7) & 1) == 1;
        this.authenticData = ((header >> 5) & 1) == 1;
        if (((header >> 4) & 1) != 1) {
            z = false;
        }
        this.checkingDisabled = z;
        this.responseCode = RESPONSE_CODE.getResponseCode(header & 15);
        this.receiveTimestamp = System.currentTimeMillis();
        int questionCount = dis.readUnsignedShort();
        int answerCount = dis.readUnsignedShort();
        int nameserverCount = dis.readUnsignedShort();
        int additionalResourceRecordCount = dis.readUnsignedShort();
        this.questions = new ArrayList(questionCount);
        for (int i = 0; i < questionCount; i++) {
            this.questions.add(new Question(dis, data));
        }
        this.answerSection = new ArrayList(answerCount);
        for (int i2 = 0; i2 < answerCount; i2++) {
            this.answerSection.add(Record.parse(dis, data));
        }
        this.authoritySection = new ArrayList(nameserverCount);
        for (int i3 = 0; i3 < nameserverCount; i3++) {
            this.authoritySection.add(Record.parse(dis, data));
        }
        this.additionalSection = new ArrayList(additionalResourceRecordCount);
        for (int i4 = 0; i4 < additionalResourceRecordCount; i4++) {
            this.additionalSection.add(Record.parse(dis, data));
        }
        this.optRrPosition = getOptRrPosition(this.additionalSection);
    }

    private DnsMessage(DnsMessage message) {
        this.answersMinTtlCache = -1;
        this.id = 0;
        this.qr = message.qr;
        this.opcode = message.opcode;
        this.authoritativeAnswer = message.authoritativeAnswer;
        this.truncated = message.truncated;
        this.recursionDesired = message.recursionDesired;
        this.recursionAvailable = message.recursionAvailable;
        this.authenticData = message.authenticData;
        this.checkingDisabled = message.checkingDisabled;
        this.responseCode = message.responseCode;
        this.receiveTimestamp = message.receiveTimestamp;
        this.questions = message.questions;
        this.answerSection = message.answerSection;
        this.authoritySection = message.authoritySection;
        this.additionalSection = message.additionalSection;
        this.optRrPosition = message.optRrPosition;
    }

    private static int getOptRrPosition(List<Record<? extends Data>> additionalSection2) {
        for (int i = 0; i < additionalSection2.size(); i++) {
            if (((Record) additionalSection2.get(i)).type == TYPE.OPT) {
                return i;
            }
        }
        return -1;
    }

    public byte[] toArray() {
        return (byte[]) serialize().clone();
    }

    public DatagramPacket asDatagram(InetAddress address, int port) {
        byte[] bytes = serialize();
        return new DatagramPacket(bytes, bytes.length, address, port);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        writeTo(outputStream, true);
    }

    public void writeTo(OutputStream outputStream, boolean writeLength) throws IOException {
        byte[] bytes = serialize();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        if (writeLength) {
            dataOutputStream.writeShort(bytes.length);
        }
        dataOutputStream.write(bytes);
    }

    public ByteBuffer getInByteBuffer() {
        return ByteBuffer.wrap((byte[]) serialize().clone());
    }

    private byte[] serialize() {
        byte[] bArr = this.byteCache;
        if (bArr != null) {
            return bArr;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        DataOutputStream dos = new DataOutputStream(baos);
        int header = calculateHeaderBitmap();
        try {
            dos.writeShort((short) this.id);
            dos.writeShort((short) header);
            if (this.questions == null) {
                dos.writeShort(0);
            } else {
                dos.writeShort((short) this.questions.size());
            }
            if (this.answerSection == null) {
                dos.writeShort(0);
            } else {
                dos.writeShort((short) this.answerSection.size());
            }
            if (this.authoritySection == null) {
                dos.writeShort(0);
            } else {
                dos.writeShort((short) this.authoritySection.size());
            }
            if (this.additionalSection == null) {
                dos.writeShort(0);
            } else {
                dos.writeShort((short) this.additionalSection.size());
            }
            if (this.questions != null) {
                for (Question question : this.questions) {
                    dos.write(question.toByteArray());
                }
            }
            if (this.answerSection != null) {
                for (Record<? extends Data> answer : this.answerSection) {
                    dos.write(answer.toByteArray());
                }
            }
            if (this.authoritySection != null) {
                for (Record<? extends Data> nameserverRecord : this.authoritySection) {
                    dos.write(nameserverRecord.toByteArray());
                }
            }
            if (this.additionalSection != null) {
                for (Record<? extends Data> additionalResourceRecord : this.additionalSection) {
                    dos.write(additionalResourceRecord.toByteArray());
                }
            }
            dos.flush();
            this.byteCache = baos.toByteArray();
            return this.byteCache;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /* access modifiers changed from: 0000 */
    public int calculateHeaderBitmap() {
        int header = 0;
        if (this.qr) {
            header = 0 + 32768;
        }
        OPCODE opcode2 = this.opcode;
        if (opcode2 != null) {
            header += opcode2.getValue() << 11;
        }
        if (this.authoritativeAnswer) {
            header += 1024;
        }
        if (this.truncated) {
            header += 512;
        }
        if (this.recursionDesired) {
            header += 256;
        }
        if (this.recursionAvailable) {
            header += 128;
        }
        if (this.authenticData) {
            header += 32;
        }
        if (this.checkingDisabled) {
            header += 16;
        }
        RESPONSE_CODE response_code = this.responseCode;
        if (response_code != null) {
            return header + response_code.getValue();
        }
        return header;
    }

    public Question getQuestion() {
        return (Question) this.questions.get(0);
    }

    public List<Question> copyQuestions() {
        List<Question> copy = new ArrayList<>(this.questions.size());
        copy.addAll(this.questions);
        return copy;
    }

    public List<Record<? extends Data>> copyAnswers() {
        List<Record<? extends Data>> res = new ArrayList<>(this.answerSection.size());
        res.addAll(this.answerSection);
        return res;
    }

    public List<Record<? extends Data>> copyAuthority() {
        List<Record<? extends Data>> res = new ArrayList<>(this.authoritySection.size());
        res.addAll(this.authoritySection);
        return res;
    }

    public Edns getEdns() {
        Edns edns2 = this.edns;
        if (edns2 != null) {
            return edns2;
        }
        Record<OPT> optRecord = getOptPseudoRecord();
        if (optRecord == null) {
            return null;
        }
        this.edns = new Edns(optRecord);
        return this.edns;
    }

    public Record<OPT> getOptPseudoRecord() {
        int i = this.optRrPosition;
        if (i == -1) {
            return null;
        }
        return (Record) this.additionalSection.get(i);
    }

    public boolean isDnssecOk() {
        Edns edns2 = getEdns();
        if (edns2 == null) {
            return false;
        }
        return edns2.dnssecOk;
    }

    public String toString() {
        String str = this.toStringCache;
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder("DnsMessage");
        asBuilder().writeToStringBuilder(sb);
        this.toStringCache = sb.toString();
        return this.toStringCache;
    }

    public String asTerminalOutput() {
        String str = this.terminalOutputCache;
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder(";; ->>HEADER<<-");
        sb.append(" opcode: ");
        sb.append(this.opcode);
        sb.append(", status: ");
        sb.append(this.responseCode);
        sb.append(", id: ");
        sb.append(this.id);
        sb.append("\n");
        StringBuilder sb2 = sb.append(";; flags:");
        if (!this.qr) {
            sb2.append(" qr");
        }
        if (this.authoritativeAnswer) {
            sb2.append(" aa");
        }
        if (this.truncated) {
            sb2.append(" tr");
        }
        if (this.recursionDesired) {
            sb2.append(" rd");
        }
        if (this.recursionAvailable) {
            sb2.append(" ra");
        }
        if (this.authenticData) {
            sb2.append(" ad");
        }
        if (this.checkingDisabled) {
            sb2.append(" cd");
        }
        sb2.append("; QUERY: ");
        sb2.append(this.questions.size());
        sb2.append(", ANSWER: ");
        sb2.append(this.answerSection.size());
        sb2.append(", AUTHORITY: ");
        sb2.append(this.authoritySection.size());
        sb2.append(", ADDITIONAL: ");
        sb2.append(this.additionalSection.size());
        sb2.append("\n\n");
        Iterator it = this.additionalSection.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Edns edns2 = Edns.fromRecord((Record) it.next());
            if (edns2 != null) {
                sb2.append(";; OPT PSEUDOSECTION:\n; ");
                sb2.append(edns2.asTerminalOutput());
                break;
            }
        }
        if (this.questions.size() != 0) {
            sb2.append(";; QUESTION SECTION:\n");
            for (Question question : this.questions) {
                sb2.append(';');
                sb2.append(question.toString());
                sb2.append(10);
            }
        }
        if (this.authoritySection.size() != 0) {
            sb2.append("\n;; AUTHORITY SECTION:\n");
            for (Record<? extends Data> record : this.authoritySection) {
                sb2.append(record.toString());
                sb2.append(10);
            }
        }
        if (this.answerSection.size() != 0) {
            sb2.append("\n;; ANSWER SECTION:\n");
            for (Record<? extends Data> record2 : this.answerSection) {
                sb2.append(record2.toString());
                sb2.append(10);
            }
        }
        if (this.additionalSection.size() != 0) {
            boolean hasNonOptArr = false;
            for (Record<? extends Data> record3 : this.additionalSection) {
                if (record3.type != TYPE.OPT) {
                    if (!hasNonOptArr) {
                        hasNonOptArr = true;
                        sb2.append("\n;; ADDITIONAL SECTION:\n");
                    }
                    sb2.append(record3.toString());
                    sb2.append(10);
                }
            }
        }
        if (this.receiveTimestamp > 0) {
            sb2.append("\n;; WHEN: ");
            sb2.append(new Date(this.receiveTimestamp).toString());
        }
        this.terminalOutputCache = sb2.toString();
        return this.terminalOutputCache;
    }

    public <D extends Data> Set<D> getAnswersFor(Question q) {
        if (this.responseCode != RESPONSE_CODE.NO_ERROR) {
            return null;
        }
        Set<D> res = new HashSet<>(this.answerSection.size());
        for (Record<? extends Data> record : this.answerSection) {
            if (record.isAnswer(q) && !res.add(record.getPayload())) {
                Logger logger = LOGGER;
                Level level = Level.WARNING;
                StringBuilder sb = new StringBuilder();
                sb.append("DnsMessage contains duplicate answers. Record: ");
                sb.append(record);
                sb.append("; DnsMessage: ");
                sb.append(this);
                logger.log(level, sb.toString());
            }
        }
        return res;
    }

    public long getAnswersMinTtl() {
        long j = this.answersMinTtlCache;
        if (j >= 0) {
            return j;
        }
        this.answersMinTtlCache = Long.MAX_VALUE;
        for (Record<? extends Data> r : this.answerSection) {
            this.answersMinTtlCache = Math.min(this.answersMinTtlCache, r.ttl);
        }
        return this.answersMinTtlCache;
    }

    public Builder asBuilder() {
        return new Builder(this, null);
    }

    public DnsMessage asNormalizedVersion() {
        if (this.normalizedVersionCache == null) {
            this.normalizedVersionCache = new DnsMessage(this);
        }
        return this.normalizedVersionCache;
    }

    public Builder getResponseBuilder(RESPONSE_CODE responseCode2) {
        if (!this.qr) {
            return builder().setQrFlag(true).setResponseCode(responseCode2).setId(this.id).setQuestion(getQuestion());
        }
        throw new IllegalStateException();
    }

    public int hashCode() {
        if (this.hashCodeCache == null) {
            this.hashCodeCache = Integer.valueOf(Arrays.hashCode(serialize()));
        }
        return this.hashCodeCache.intValue();
    }

    private <D extends Data> List<Record<D>> filterSectionByType(boolean stopOnFirst, SectionName sectionName, Class<D> type) {
        List<Record<? extends Data>> list;
        int i = AnonymousClass1.$SwitchMap$org$minidns$dnsmessage$DnsMessage$SectionName[sectionName.ordinal()];
        int i2 = 1;
        if (i == 1) {
            list = this.answerSection;
        } else if (i == 2) {
            list = this.authoritySection;
        } else if (i == 3) {
            list = this.additionalSection;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Unknown section name ");
            sb.append(sectionName);
            throw new AssertionError(sb.toString());
        }
        if (!stopOnFirst) {
            i2 = list.size();
        }
        List<Record<D>> res = new ArrayList<>(i2);
        for (Record<?> record : list) {
            Record<D> target = record.ifPossibleAs(type);
            if (target != null) {
                res.add(target);
                if (stopOnFirst) {
                    return res;
                }
            }
        }
        return res;
    }

    private <D extends Data> List<Record<D>> filterSectionByType(SectionName sectionName, Class<D> type) {
        return filterSectionByType(false, sectionName, type);
    }

    private <D extends Data> Record<D> getFirstOfType(SectionName sectionName, Class<D> type) {
        List<Record<D>> result = filterSectionByType(true, sectionName, type);
        if (result.isEmpty()) {
            return null;
        }
        return (Record) result.get(0);
    }

    public <D extends Data> List<Record<D>> filterAnswerSectionBy(Class<D> type) {
        return filterSectionByType(SectionName.answer, type);
    }

    public <D extends Data> List<Record<D>> filterAuthoritySectionBy(Class<D> type) {
        return filterSectionByType(SectionName.authority, type);
    }

    public <D extends Data> List<Record<D>> filterAdditionalSectionBy(Class<D> type) {
        return filterSectionByType(SectionName.additional, type);
    }

    public <D extends Data> Record<D> getFirstOfTypeFromAnswerSection(Class<D> type) {
        return getFirstOfType(SectionName.answer, type);
    }

    public <D extends Data> Record<D> getFirstOfTypeFromAuthoritySection(Class<D> type) {
        return getFirstOfType(SectionName.authority, type);
    }

    public <D extends Data> Record<D> getFirstOfTypeFromAdditionalSection(Class<D> type) {
        return getFirstOfType(SectionName.additional, type);
    }

    public boolean equals(Object other) {
        if (!(other instanceof DnsMessage)) {
            return false;
        }
        if (other == this) {
            return true;
        }
        return Arrays.equals(serialize(), ((DnsMessage) other).serialize());
    }

    public static Builder builder() {
        return new Builder((AnonymousClass1) null);
    }
}
