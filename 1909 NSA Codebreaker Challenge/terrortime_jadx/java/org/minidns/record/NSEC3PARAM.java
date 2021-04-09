package org.minidns.record;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.minidns.record.NSEC3.HashAlgorithm;
import org.minidns.record.Record.TYPE;

public class NSEC3PARAM extends Data {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public final byte flags;
    public final HashAlgorithm hashAlgorithm;
    public final byte hashAlgorithmByte;
    public final int iterations;
    private final byte[] salt;

    public static NSEC3PARAM parse(DataInputStream dis) throws IOException {
        byte hashAlgorithm2 = dis.readByte();
        byte flags2 = dis.readByte();
        int iterations2 = dis.readUnsignedShort();
        byte[] salt2 = new byte[dis.readUnsignedByte()];
        if (dis.read(salt2) == salt2.length || salt2.length == 0) {
            return new NSEC3PARAM(hashAlgorithm2, flags2, iterations2, salt2);
        }
        throw new IOException();
    }

    private NSEC3PARAM(HashAlgorithm hashAlgorithm2, byte hashAlgorithmByte2, byte flags2, int iterations2, byte[] salt2) {
        this.hashAlgorithmByte = hashAlgorithmByte2;
        this.hashAlgorithm = hashAlgorithm2 != null ? hashAlgorithm2 : HashAlgorithm.forByte(hashAlgorithmByte2);
        this.flags = flags2;
        this.iterations = iterations2;
        this.salt = salt2;
    }

    NSEC3PARAM(byte hashAlgorithm2, byte flags2, int iterations2, byte[] salt2) {
        this(null, hashAlgorithm2, flags2, iterations2, salt2);
    }

    public TYPE getType() {
        return TYPE.NSEC3PARAM;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeByte(this.hashAlgorithmByte);
        dos.writeByte(this.flags);
        dos.writeShort(this.iterations);
        dos.writeByte(this.salt.length);
        dos.write(this.salt);
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
        return sb.append(str).toString();
    }

    public int getSaltLength() {
        return this.salt.length;
    }
}
