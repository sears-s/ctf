package org.minidns.dnsmessage;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.minidns.dnsmessage.DnsMessage.Builder;
import org.minidns.dnsname.DnsName;
import org.minidns.record.Record.CLASS;
import org.minidns.record.Record.TYPE;

public class Question {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private byte[] byteArray;
    public final CLASS clazz;
    public final DnsName name;
    public final TYPE type;
    private final boolean unicastQuery;

    public Question(CharSequence name2, TYPE type2, CLASS clazz2, boolean unicastQuery2) {
        this(DnsName.from(name2), type2, clazz2, unicastQuery2);
    }

    public Question(DnsName name2, TYPE type2, CLASS clazz2, boolean unicastQuery2) {
        this.name = name2;
        this.type = type2;
        this.clazz = clazz2;
        this.unicastQuery = unicastQuery2;
    }

    public Question(DnsName name2, TYPE type2, CLASS clazz2) {
        this(name2, type2, clazz2, false);
    }

    public Question(DnsName name2, TYPE type2) {
        this(name2, type2, CLASS.IN);
    }

    public Question(CharSequence name2, TYPE type2, CLASS clazz2) {
        this(DnsName.from(name2), type2, clazz2);
    }

    public Question(CharSequence name2, TYPE type2) {
        this(DnsName.from(name2), type2);
    }

    public Question(DataInputStream dis, byte[] data) throws IOException {
        this.name = DnsName.parse(dis, data);
        this.type = TYPE.getType(dis.readUnsignedShort());
        this.clazz = CLASS.getClass(dis.readUnsignedShort());
        this.unicastQuery = false;
    }

    public byte[] toByteArray() {
        if (this.byteArray == null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
            DataOutputStream dos = new DataOutputStream(baos);
            try {
                this.name.writeToStream(dos);
                dos.writeShort(this.type.getValue());
                dos.writeShort(this.clazz.getValue() | (this.unicastQuery ? 32768 : 0));
                dos.flush();
                this.byteArray = baos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return this.byteArray;
    }

    public int hashCode() {
        return Arrays.hashCode(toByteArray());
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Question)) {
            return false;
        }
        return Arrays.equals(toByteArray(), ((Question) other).toByteArray());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name.getRawAce());
        sb.append(".\t");
        sb.append(this.clazz);
        sb.append(9);
        sb.append(this.type);
        return sb.toString();
    }

    public Builder asMessageBuilder() {
        Builder builder = DnsMessage.builder();
        builder.setQuestion(this);
        return builder;
    }

    public DnsMessage asQueryMessage() {
        return asMessageBuilder().build();
    }
}
