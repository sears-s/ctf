package org.minidns.record;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import org.minidns.constants.DnssecConstants.DigestAlgorithm;
import org.minidns.constants.DnssecConstants.SignatureAlgorithm;

public abstract class DelegatingDnssecRR extends Data {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public final SignatureAlgorithm algorithm;
    public final byte algorithmByte;
    protected final byte[] digest;
    private transient BigInteger digestBigIntCache;
    private transient String digestHexCache;
    public final DigestAlgorithm digestType;
    public final byte digestTypeByte;
    public final int keyTag;

    protected static class SharedData {
        protected final byte algorithm;
        protected final byte[] digest;
        protected final byte digestType;
        protected final int keyTag;

        private SharedData(int keyTag2, byte algorithm2, byte digestType2, byte[] digest2) {
            this.keyTag = keyTag2;
            this.algorithm = algorithm2;
            this.digestType = digestType2;
            this.digest = digest2;
        }
    }

    protected static SharedData parseSharedData(DataInputStream dis, int length) throws IOException {
        int keyTag2 = dis.readUnsignedShort();
        byte algorithm2 = dis.readByte();
        byte digestType2 = dis.readByte();
        byte[] digest2 = new byte[(length - 4)];
        if (dis.read(digest2) == digest2.length) {
            SharedData sharedData = new SharedData(keyTag2, algorithm2, digestType2, digest2);
            return sharedData;
        }
        throw new IOException();
    }

    protected DelegatingDnssecRR(int keyTag2, SignatureAlgorithm algorithm2, byte algorithmByte2, DigestAlgorithm digestType2, byte digestTypeByte2, byte[] digest2) {
        this.keyTag = keyTag2;
        this.algorithmByte = algorithmByte2;
        this.algorithm = algorithm2 != null ? algorithm2 : SignatureAlgorithm.forByte(algorithmByte2);
        this.digestTypeByte = digestTypeByte2;
        this.digestType = digestType2 != null ? digestType2 : DigestAlgorithm.forByte(digestTypeByte2);
        this.digest = digest2;
    }

    protected DelegatingDnssecRR(int keyTag2, byte algorithm2, byte digestType2, byte[] digest2) {
        this(keyTag2, null, algorithm2, null, digestType2, digest2);
    }

    protected DelegatingDnssecRR(int keyTag2, SignatureAlgorithm algorithm2, DigestAlgorithm digestType2, byte[] digest2) {
        this(keyTag2, algorithm2, algorithm2.number, digestType2, digestType2.value, digest2);
    }

    protected DelegatingDnssecRR(int keyTag2, SignatureAlgorithm algorithm2, byte digestType2, byte[] digest2) {
        this(keyTag2, algorithm2, algorithm2.number, null, digestType2, digest2);
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeShort(this.keyTag);
        dos.writeByte(this.algorithmByte);
        dos.writeByte(this.digestTypeByte);
        dos.write(this.digest);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.keyTag);
        sb.append(' ');
        sb.append(this.algorithm);
        sb.append(' ');
        sb.append(this.digestType);
        sb.append(' ');
        return sb.append(new BigInteger(1, this.digest).toString(16).toUpperCase()).toString();
    }

    public BigInteger getDigestBigInteger() {
        if (this.digestBigIntCache == null) {
            this.digestBigIntCache = new BigInteger(1, this.digest);
        }
        return this.digestBigIntCache;
    }

    public String getDigestHex() {
        if (this.digestHexCache == null) {
            this.digestHexCache = getDigestBigInteger().toString(16).toUpperCase();
        }
        return this.digestHexCache;
    }

    public boolean digestEquals(byte[] otherDigest) {
        return Arrays.equals(this.digest, otherDigest);
    }
}
