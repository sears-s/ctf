package org.minidns.record;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import org.minidns.constants.DnssecConstants.SignatureAlgorithm;
import org.minidns.record.Record.TYPE;
import org.minidns.util.Base64;

public class DNSKEY extends Data {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final short FLAG_REVOKE = 128;
    public static final short FLAG_SECURE_ENTRY_POINT = 1;
    public static final short FLAG_ZONE = 256;
    public static final byte PROTOCOL_RFC4034 = 3;
    public final SignatureAlgorithm algorithm;
    public final byte algorithmByte;
    public final short flags;
    private final byte[] key;
    private transient String keyBase64Cache;
    private transient BigInteger keyBigIntegerCache;
    private transient Integer keyTag;
    public final byte protocol;

    public static DNSKEY parse(DataInputStream dis, int length) throws IOException {
        short flags2 = dis.readShort();
        byte protocol2 = dis.readByte();
        byte algorithm2 = dis.readByte();
        byte[] key2 = new byte[(length - 4)];
        dis.readFully(key2);
        return new DNSKEY(flags2, protocol2, algorithm2, key2);
    }

    private DNSKEY(short flags2, byte protocol2, SignatureAlgorithm algorithm2, byte algorithmByte2, byte[] key2) {
        this.flags = flags2;
        this.protocol = protocol2;
        this.algorithmByte = algorithmByte2;
        this.algorithm = algorithm2 != null ? algorithm2 : SignatureAlgorithm.forByte(algorithmByte2);
        this.key = key2;
    }

    public DNSKEY(short flags2, byte protocol2, byte algorithm2, byte[] key2) {
        this(flags2, protocol2, SignatureAlgorithm.forByte(algorithm2), key2);
    }

    public DNSKEY(short flags2, byte protocol2, SignatureAlgorithm algorithm2, byte[] key2) {
        this(flags2, protocol2, algorithm2, algorithm2.number, key2);
    }

    public TYPE getType() {
        return TYPE.DNSKEY;
    }

    public int getKeyTag() {
        if (this.keyTag == null) {
            byte[] recordBytes = toByteArray();
            long ac = 0;
            for (int i = 0; i < recordBytes.length; i++) {
                ac += (i & 1) > 0 ? ((long) recordBytes[i]) & 255 : (((long) recordBytes[i]) & 255) << 8;
            }
            this.keyTag = Integer.valueOf((int) ((ac + ((ac >> 16) & 65535)) & 65535));
        }
        return this.keyTag.intValue();
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeShort(this.flags);
        dos.writeByte(this.protocol);
        dos.writeByte(this.algorithm.number);
        dos.write(this.key);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.flags);
        sb.append(' ');
        sb.append(this.protocol);
        sb.append(' ');
        sb.append(this.algorithm);
        sb.append(' ');
        return sb.append(Base64.encodeToString(this.key)).toString();
    }

    public int getKeyLength() {
        return this.key.length;
    }

    public byte[] getKey() {
        return (byte[]) this.key.clone();
    }

    public DataInputStream getKeyAsDataInputStream() {
        return new DataInputStream(new ByteArrayInputStream(this.key));
    }

    public String getKeyBase64() {
        if (this.keyBase64Cache == null) {
            this.keyBase64Cache = Base64.encodeToString(this.key);
        }
        return this.keyBase64Cache;
    }

    public BigInteger getKeyBigInteger() {
        if (this.keyBigIntegerCache == null) {
            this.keyBigIntegerCache = new BigInteger(this.key);
        }
        return this.keyBigIntegerCache;
    }

    public boolean keyEquals(byte[] otherKey) {
        return Arrays.equals(this.key, otherKey);
    }

    public boolean isSecureEntryPoint() {
        return (this.flags & 1) == 1;
    }
}
