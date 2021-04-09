package org.minidns.record;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.minidns.dnslabel.DnsLabel;
import org.minidns.record.Record.TYPE;
import org.minidns.util.Base32;

public class NSEC3 extends Data {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final byte FLAG_OPT_OUT = 1;
    /* access modifiers changed from: private */
    public static final Map<Byte, HashAlgorithm> HASH_ALGORITHM_LUT = new HashMap();
    public final byte flags;
    public final HashAlgorithm hashAlgorithm;
    public final byte hashAlgorithmByte;
    public final int iterations;
    private final byte[] nextHashed;
    private String nextHashedBase32Cache;
    private DnsLabel nextHashedDnsLabelCache;
    private final byte[] salt;
    private final byte[] typeBitmap;
    public final List<TYPE> types;

    public enum HashAlgorithm {
        RESERVED(0, "Reserved"),
        SHA1(1, "SHA-1");
        
        public final String description;
        public final byte value;

        private HashAlgorithm(int value2, String description2) {
            if (value2 < 0 || value2 > 255) {
                throw new IllegalArgumentException();
            }
            this.value = (byte) value2;
            this.description = description2;
            NSEC3.HASH_ALGORITHM_LUT.put(Byte.valueOf(this.value), this);
        }

        public static HashAlgorithm forByte(byte b) {
            return (HashAlgorithm) NSEC3.HASH_ALGORITHM_LUT.get(Byte.valueOf(b));
        }
    }

    public static NSEC3 parse(DataInputStream dis, int length) throws IOException {
        DataInputStream dataInputStream = dis;
        byte hashAlgorithm2 = dis.readByte();
        byte flags2 = dis.readByte();
        int iterations2 = dis.readUnsignedShort();
        int saltLength = dis.readUnsignedByte();
        byte[] salt2 = new byte[saltLength];
        if (dataInputStream.read(salt2) == salt2.length) {
            int hashLength = dis.readUnsignedByte();
            byte[] nextHashed2 = new byte[hashLength];
            if (dataInputStream.read(nextHashed2) == nextHashed2.length) {
                byte[] typeBitmap2 = new byte[(length - ((saltLength + 6) + hashLength))];
                if (dataInputStream.read(typeBitmap2) == typeBitmap2.length) {
                    NSEC3 nsec3 = new NSEC3(hashAlgorithm2, flags2, iterations2, salt2, nextHashed2, NSEC.readTypeBitMap(typeBitmap2));
                    return nsec3;
                }
                throw new IOException();
            }
            throw new IOException();
        }
        throw new IOException();
    }

    private NSEC3(HashAlgorithm hashAlgorithm2, byte hashAlgorithmByte2, byte flags2, int iterations2, byte[] salt2, byte[] nextHashed2, List<TYPE> types2) {
        this.hashAlgorithmByte = hashAlgorithmByte2;
        this.hashAlgorithm = hashAlgorithm2 != null ? hashAlgorithm2 : HashAlgorithm.forByte(hashAlgorithmByte2);
        this.flags = flags2;
        this.iterations = iterations2;
        this.salt = salt2;
        this.nextHashed = nextHashed2;
        this.types = types2;
        this.typeBitmap = NSEC.createTypeBitMap(types2);
    }

    public NSEC3(byte hashAlgorithm2, byte flags2, int iterations2, byte[] salt2, byte[] nextHashed2, List<TYPE> types2) {
        this(null, hashAlgorithm2, flags2, iterations2, salt2, nextHashed2, types2);
    }

    public NSEC3(byte hashAlgorithm2, byte flags2, int iterations2, byte[] salt2, byte[] nextHashed2, TYPE... types2) {
        this(null, hashAlgorithm2, flags2, iterations2, salt2, nextHashed2, Arrays.asList(types2));
    }

    public TYPE getType() {
        return TYPE.NSEC3;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeByte(this.hashAlgorithmByte);
        dos.writeByte(this.flags);
        dos.writeShort(this.iterations);
        dos.writeByte(this.salt.length);
        dos.write(this.salt);
        dos.writeByte(this.nextHashed.length);
        dos.write(this.nextHashed);
        dos.write(this.typeBitmap);
    }

    public String toString() {
        String str;
        StringBuilder sb = new StringBuilder();
        sb.append(this.hashAlgorithm);
        sb.append(' ');
        sb.append(this.flags);
        sb.append(' ');
        sb.append(this.iterations);
        sb.append(' ');
        byte[] bArr = this.salt;
        if (bArr.length == 0) {
            str = "-";
        } else {
            str = new BigInteger(1, bArr).toString(16).toUpperCase();
        }
        sb.append(str);
        sb.append(' ');
        StringBuilder sb2 = sb.append(Base32.encodeToString(this.nextHashed));
        for (TYPE type : this.types) {
            sb2.append(' ');
            sb2.append(type);
        }
        return sb2.toString();
    }

    public byte[] getSalt() {
        return (byte[]) this.salt.clone();
    }

    public int getSaltLength() {
        return this.salt.length;
    }

    public byte[] getNextHashed() {
        return (byte[]) this.nextHashed.clone();
    }

    public String getNextHashedBase32() {
        if (this.nextHashedBase32Cache == null) {
            this.nextHashedBase32Cache = Base32.encodeToString(this.nextHashed);
        }
        return this.nextHashedBase32Cache;
    }

    public DnsLabel getNextHashedDnsLabel() {
        if (this.nextHashedDnsLabelCache == null) {
            this.nextHashedDnsLabelCache = DnsLabel.from(getNextHashedBase32());
        }
        return this.nextHashedDnsLabelCache;
    }

    public void copySaltInto(byte[] dest, int destPos) {
        byte[] bArr = this.salt;
        System.arraycopy(bArr, 0, dest, destPos, bArr.length);
    }
}
