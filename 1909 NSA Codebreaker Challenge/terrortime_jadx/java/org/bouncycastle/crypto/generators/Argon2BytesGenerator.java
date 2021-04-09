package org.bouncycastle.crypto.generators;

import org.bouncycastle.asn1.cmc.BodyPartID;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.encoders.Hex;

public class Argon2BytesGenerator {
    private static final int ARGON2_ADDRESSES_IN_BLOCK = 128;
    private static final int ARGON2_BLOCK_SIZE = 1024;
    private static final int ARGON2_PREHASH_DIGEST_LENGTH = 64;
    private static final int ARGON2_PREHASH_SEED_LENGTH = 72;
    private static final int ARGON2_QWORDS_IN_BLOCK = 128;
    private static final int ARGON2_SYNC_POINTS = 4;
    private static final int MAX_PARALLELISM = 16777216;
    private static final int MIN_ITERATIONS = 1;
    private static final int MIN_OUTLEN = 4;
    private static final int MIN_PARALLELISM = 1;
    private int laneLength;
    private Block[] memory;
    private Argon2Parameters parameters;
    private byte[] result;
    private int segmentLength;

    private static class Block {
        /* access modifiers changed from: private */
        public long[] v;

        private Block() {
            this.v = new long[128];
        }

        /* access modifiers changed from: private */
        public void copyBlock(Block block) {
            long[] jArr = block.v;
            long[] jArr2 = this.v;
            System.arraycopy(jArr, 0, jArr2, 0, jArr2.length);
        }

        /* access modifiers changed from: private */
        public void xor(Block block, Block block2) {
            int i = 0;
            while (true) {
                long[] jArr = this.v;
                if (i < jArr.length) {
                    jArr[i] = block.v[i] ^ block2.v[i];
                    i++;
                } else {
                    return;
                }
            }
        }

        /* access modifiers changed from: private */
        public void xorWith(Block block) {
            int i = 0;
            while (true) {
                long[] jArr = this.v;
                if (i < jArr.length) {
                    jArr[i] = jArr[i] ^ block.v[i];
                    i++;
                } else {
                    return;
                }
            }
        }

        public void clear() {
            Arrays.fill(this.v, 0);
        }

        /* access modifiers changed from: 0000 */
        public void fromBytes(byte[] bArr) {
            if (bArr.length == 1024) {
                int i = 0;
                while (true) {
                    long[] jArr = this.v;
                    if (i < jArr.length) {
                        jArr[i] = Pack.littleEndianToLong(bArr, i * 8);
                        i++;
                    } else {
                        return;
                    }
                }
            } else {
                throw new IllegalArgumentException("input shorter than blocksize");
            }
        }

        /* access modifiers changed from: 0000 */
        public byte[] toBytes() {
            byte[] bArr = new byte[1024];
            int i = 0;
            while (true) {
                long[] jArr = this.v;
                if (i >= jArr.length) {
                    return bArr;
                }
                Pack.longToLittleEndian(jArr[i], bArr, i * 8);
                i++;
            }
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            int i = 0;
            while (true) {
                long[] jArr = this.v;
                if (i >= jArr.length) {
                    return stringBuffer.toString();
                }
                stringBuffer.append(Hex.toHexString(Pack.longToLittleEndian(jArr[i])));
                i++;
            }
        }

        public void xor(Block block, Block block2, Block block3) {
            int i = 0;
            while (true) {
                long[] jArr = this.v;
                if (i < jArr.length) {
                    jArr[i] = (block.v[i] ^ block2.v[i]) ^ block3.v[i];
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    private static class Position {
        int index;
        int lane;
        int pass;
        int slice;

        Position(int i, int i2, int i3, int i4) {
            this.pass = i;
            this.lane = i2;
            this.slice = i3;
            this.index = i4;
        }
    }

    private void F(Block block, int i, int i2, int i3, int i4) {
        fBlaMka(block, i, i2);
        Block block2 = block;
        rotr64(block2, i4, i, 32);
        fBlaMka(block, i3, i4);
        rotr64(block2, i2, i3, 24);
        fBlaMka(block, i, i2);
        rotr64(block2, i4, i, 16);
        fBlaMka(block, i3, i4);
        rotr64(block2, i2, i3, 63);
    }

    private static void addByteString(Digest digest, byte[] bArr) {
        if (bArr != null) {
            addIntToLittleEndian(digest, bArr.length);
            digest.update(bArr, 0, bArr.length);
            return;
        }
        addIntToLittleEndian(digest, 0);
    }

    private static void addIntToLittleEndian(Digest digest, int i) {
        digest.update((byte) i);
        digest.update((byte) (i >>> 8));
        digest.update((byte) (i >>> 16));
        digest.update((byte) (i >>> 24));
    }

    private void digest(int i) {
        Block block = this.memory[this.laneLength - 1];
        for (int i2 = 1; i2 < this.parameters.getLanes(); i2++) {
            int i3 = this.laneLength;
            block.xorWith(this.memory[(i2 * i3) + (i3 - 1)]);
        }
        this.result = hash(block.toBytes(), i);
    }

    private void doInit(Argon2Parameters argon2Parameters) {
        int memory2 = argon2Parameters.getMemory();
        if (memory2 < argon2Parameters.getLanes() * 8) {
            memory2 = argon2Parameters.getLanes() * 8;
        }
        this.segmentLength = memory2 / (argon2Parameters.getLanes() * 4);
        int i = this.segmentLength;
        this.laneLength = i * 4;
        initMemory(i * argon2Parameters.getLanes() * 4);
    }

    private void fBlaMka(Block block, int i, int i2) {
        block.v[i] = block.v[i] + block.v[i2] + ((block.v[i] & BodyPartID.bodyIdMax) * (block.v[i2] & BodyPartID.bodyIdMax) * 2);
    }

    private void fillBlock(Block block, Block block2, Block block3, boolean z) {
        int i;
        Block block4 = block3;
        Block block5 = new Block();
        Block block6 = new Block();
        block5.xor(block, block2);
        block6.copyBlock(block5);
        int i2 = 0;
        int i3 = 0;
        while (true) {
            i = 8;
            if (i3 >= 8) {
                break;
            }
            int i4 = i3 * 16;
            int i5 = i3;
            Block block7 = block6;
            roundFunction(block6, i4, i4 + 1, i4 + 2, i4 + 3, i4 + 4, i4 + 5, i4 + 6, i4 + 7, i4 + 8, i4 + 9, i4 + 10, i4 + 11, i4 + 12, i4 + 13, i4 + 14, i4 + 15);
            i3 = i5 + 1;
        }
        Block block8 = block6;
        while (i2 < i) {
            int i6 = i2 * 2;
            int i7 = i;
            roundFunction(block8, i6, i6 + 1, i6 + 16, i6 + 17, i6 + 32, i6 + 33, i6 + 48, i6 + 49, i6 + 64, i6 + 65, i6 + 80, i6 + 81, i6 + 96, i6 + 97, i6 + 112, i6 + 113);
            i2++;
            i = i7;
        }
        Block block9 = block8;
        if (z) {
            block4.xor(block5, block9, block4);
        } else {
            block4.xor(block5, block9);
        }
    }

    private void fillFirstBlocks(byte[] bArr) {
        byte[] bArr2 = {1, 0, 0, 0};
        byte[] initialHashLong = getInitialHashLong(bArr, new byte[]{0, 0, 0, 0});
        byte[] initialHashLong2 = getInitialHashLong(bArr, bArr2);
        for (int i = 0; i < this.parameters.getLanes(); i++) {
            Pack.intToLittleEndian(i, initialHashLong, 68);
            Pack.intToLittleEndian(i, initialHashLong2, 68);
            this.memory[(this.laneLength * i) + 0].fromBytes(hash(initialHashLong, 1024));
            this.memory[(this.laneLength * i) + 1].fromBytes(hash(initialHashLong2, 1024));
        }
    }

    private void fillMemoryBlocks() {
        for (int i = 0; i < this.parameters.getIterations(); i++) {
            for (int i2 = 0; i2 < 4; i2++) {
                for (int i3 = 0; i3 < this.parameters.getLanes(); i3++) {
                    fillSegment(new Position(i, i3, i2, 0));
                }
            }
        }
    }

    private void fillSegment(Position position) {
        Block block;
        Block block2;
        Block block3;
        boolean isDataIndependentAddressing = isDataIndependentAddressing(position);
        int startingIndex = getStartingIndex(position);
        int i = (position.lane * this.laneLength) + (position.slice * this.segmentLength) + startingIndex;
        int prevOffset = getPrevOffset(i);
        if (isDataIndependentAddressing) {
            Block block4 = new Block();
            Block block5 = new Block();
            Block block6 = new Block();
            initAddressBlocks(position, block5, block6, block4);
            block3 = block4;
            block = block5;
            block2 = block6;
        } else {
            block3 = null;
            block2 = null;
            block = null;
        }
        position.index = startingIndex;
        int i2 = i;
        while (position.index < this.segmentLength) {
            int rotatePrevOffset = rotatePrevOffset(i2, prevOffset);
            long pseudoRandom = getPseudoRandom(position, block3, block2, block, rotatePrevOffset, isDataIndependentAddressing);
            int refLane = getRefLane(position, pseudoRandom);
            int refColumn = getRefColumn(position, pseudoRandom, refLane == position.lane);
            Block[] blockArr = this.memory;
            fillBlock(blockArr[rotatePrevOffset], blockArr[(this.laneLength * refLane) + refColumn], blockArr[i2], isWithXor(position));
            position.index++;
            i2++;
            prevOffset = rotatePrevOffset + 1;
        }
    }

    private byte[] getInitialHashLong(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[72];
        System.arraycopy(bArr, 0, bArr3, 0, 64);
        System.arraycopy(bArr2, 0, bArr3, 64, 4);
        return bArr3;
    }

    private int getPrevOffset(int i) {
        int i2 = this.laneLength;
        return i % i2 == 0 ? (i + i2) - 1 : i - 1;
    }

    private long getPseudoRandom(Position position, Block block, Block block2, Block block3, int i, boolean z) {
        if (!z) {
            return this.memory[i].v[0];
        }
        if (position.index % 128 == 0) {
            nextAddresses(block3, block2, block);
        }
        return block.v[position.index % 128];
    }

    private int getRefColumn(Position position, long j, boolean z) {
        int i;
        int i2;
        int i3 = -1;
        if (position.pass == 0) {
            if (z) {
                i = ((position.slice * this.segmentLength) + position.index) - 1;
            } else {
                int i4 = position.slice * this.segmentLength;
                if (position.index != 0) {
                    i3 = 0;
                }
                i = i4 + i3;
            }
            i2 = 0;
        } else {
            int i5 = position.slice + 1;
            int i6 = this.segmentLength;
            int i7 = i5 * i6;
            int i8 = this.laneLength;
            i2 = i7 % i8;
            int i9 = i8 - i6;
            int i10 = position.index;
            if (z) {
                i = (i9 + i10) - 1;
            } else {
                if (i10 != 0) {
                    i3 = 0;
                }
                i = i9 + i3;
            }
        }
        long j2 = j & BodyPartID.bodyIdMax;
        return ((int) (((long) i2) + (((long) (i - 1)) - ((((long) i) * ((j2 * j2) >>> 32)) >>> 32)))) % this.laneLength;
    }

    private int getRefLane(Position position, long j) {
        int lanes = (int) ((j >>> 32) % ((long) this.parameters.getLanes()));
        return (position.pass == 0 && position.slice == 0) ? position.lane : lanes;
    }

    private static int getStartingIndex(Position position) {
        return (position.pass == 0 && position.slice == 0) ? 2 : 0;
    }

    private byte[] hash(byte[] bArr, int i) {
        byte[] bArr2 = new byte[i];
        byte[] intToLittleEndian = Pack.intToLittleEndian(i);
        if (i <= 64) {
            Blake2bDigest blake2bDigest = new Blake2bDigest(i * 8);
            blake2bDigest.update(intToLittleEndian, 0, intToLittleEndian.length);
            blake2bDigest.update(bArr, 0, bArr.length);
            blake2bDigest.doFinal(bArr2, 0);
        } else {
            Blake2bDigest blake2bDigest2 = new Blake2bDigest(512);
            byte[] bArr3 = new byte[64];
            blake2bDigest2.update(intToLittleEndian, 0, intToLittleEndian.length);
            blake2bDigest2.update(bArr, 0, bArr.length);
            blake2bDigest2.doFinal(bArr3, 0);
            System.arraycopy(bArr3, 0, bArr2, 0, 32);
            int i2 = 2;
            int i3 = ((i + 31) / 32) - 2;
            int i4 = 32;
            while (i2 <= i3) {
                blake2bDigest2.update(bArr3, 0, bArr3.length);
                blake2bDigest2.doFinal(bArr3, 0);
                System.arraycopy(bArr3, 0, bArr2, i4, 32);
                i2++;
                i4 += 32;
            }
            Blake2bDigest blake2bDigest3 = new Blake2bDigest((i - (i3 * 32)) * 8);
            blake2bDigest3.update(bArr3, 0, bArr3.length);
            blake2bDigest3.doFinal(bArr2, i4);
        }
        return bArr2;
    }

    private void initAddressBlocks(Position position, Block block, Block block2, Block block3) {
        block2.v[0] = intToLong(position.pass);
        block2.v[1] = intToLong(position.lane);
        block2.v[2] = intToLong(position.slice);
        block2.v[3] = intToLong(this.memory.length);
        block2.v[4] = intToLong(this.parameters.getIterations());
        block2.v[5] = intToLong(this.parameters.getType());
        if (position.pass == 0 && position.slice == 0) {
            nextAddresses(block, block2, block3);
        }
    }

    private void initMemory(int i) {
        this.memory = new Block[i];
        int i2 = 0;
        while (true) {
            Block[] blockArr = this.memory;
            if (i2 < blockArr.length) {
                blockArr[i2] = new Block();
                i2++;
            } else {
                return;
            }
        }
    }

    private byte[] initialHash(Argon2Parameters argon2Parameters, int i, byte[] bArr) {
        Blake2bDigest blake2bDigest = new Blake2bDigest(512);
        addIntToLittleEndian(blake2bDigest, argon2Parameters.getLanes());
        addIntToLittleEndian(blake2bDigest, i);
        addIntToLittleEndian(blake2bDigest, argon2Parameters.getMemory());
        addIntToLittleEndian(blake2bDigest, argon2Parameters.getIterations());
        addIntToLittleEndian(blake2bDigest, argon2Parameters.getVersion());
        addIntToLittleEndian(blake2bDigest, argon2Parameters.getType());
        addByteString(blake2bDigest, bArr);
        addByteString(blake2bDigest, argon2Parameters.getSalt());
        addByteString(blake2bDigest, argon2Parameters.getSecret());
        addByteString(blake2bDigest, argon2Parameters.getAdditional());
        byte[] bArr2 = new byte[blake2bDigest.getDigestSize()];
        blake2bDigest.doFinal(bArr2, 0);
        return bArr2;
    }

    private void initialize(byte[] bArr, int i) {
        fillFirstBlocks(initialHash(this.parameters, i, bArr));
    }

    private long intToLong(int i) {
        return ((long) i) & BodyPartID.bodyIdMax;
    }

    private boolean isDataIndependentAddressing(Position position) {
        if (this.parameters.getType() != 1) {
            return this.parameters.getType() == 2 && position.pass == 0 && position.slice < 2;
        }
        return true;
    }

    private boolean isWithXor(Position position) {
        return (position.pass == 0 || this.parameters.getVersion() == 16) ? false : true;
    }

    private void nextAddresses(Block block, Block block2, Block block3) {
        long[] access$300 = block2.v;
        access$300[6] = access$300[6] + 1;
        fillBlock(block, block2, block3, false);
        fillBlock(block, block3, block3, false);
    }

    private void reset() {
        int i = 0;
        while (true) {
            Block[] blockArr = this.memory;
            if (i < blockArr.length) {
                blockArr[i].clear();
                i++;
            } else {
                this.memory = null;
                Arrays.fill(this.result, 0);
                doInit(this.parameters);
                return;
            }
        }
    }

    private int rotatePrevOffset(int i, int i2) {
        return i % this.laneLength == 1 ? i - 1 : i2;
    }

    private void rotr64(Block block, int i, int i2, long j) {
        long j2 = block.v[i] ^ block.v[i2];
        block.v[i] = (j2 << ((int) (64 - j))) | (j2 >>> ((int) j));
    }

    private void roundFunction(Block block, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16) {
        Block block2 = block;
        F(block2, i, i5, i9, i13);
        F(block2, i2, i6, i10, i14);
        F(block2, i3, i7, i11, i15);
        int i17 = i16;
        F(block2, i4, i8, i12, i17);
        F(block2, i, i6, i11, i17);
        F(block2, i2, i7, i12, i13);
        F(block2, i3, i8, i9, i14);
        F(block2, i4, i5, i10, i15);
    }

    public int generateBytes(byte[] bArr, byte[] bArr2) {
        return generateBytes(bArr, bArr2, 0, bArr2.length);
    }

    public int generateBytes(byte[] bArr, byte[] bArr2, int i, int i2) {
        if (i2 >= 4) {
            initialize(bArr, i2);
            fillMemoryBlocks();
            digest(i2);
            System.arraycopy(this.result, 0, bArr2, i, i2);
            reset();
            return i2;
        }
        throw new IllegalStateException("output length less than 4");
    }

    public int generateBytes(char[] cArr, byte[] bArr) {
        return generateBytes(this.parameters.getCharToByteConverter().convert(cArr), bArr);
    }

    public int generateBytes(char[] cArr, byte[] bArr, int i, int i2) {
        return generateBytes(this.parameters.getCharToByteConverter().convert(cArr), bArr, i, i2);
    }

    public void init(Argon2Parameters argon2Parameters) {
        this.parameters = argon2Parameters;
        if (argon2Parameters.getLanes() < 1) {
            throw new IllegalStateException("lanes must be greater than 1");
        } else if (argon2Parameters.getLanes() > 16777216) {
            throw new IllegalStateException("lanes must be less than 16777216");
        } else if (argon2Parameters.getMemory() < argon2Parameters.getLanes() * 2) {
            StringBuilder sb = new StringBuilder();
            sb.append("memory is less than: ");
            sb.append(argon2Parameters.getLanes() * 2);
            sb.append(" expected ");
            sb.append(argon2Parameters.getLanes() * 2);
            throw new IllegalStateException(sb.toString());
        } else if (argon2Parameters.getIterations() >= 1) {
            doInit(argon2Parameters);
        } else {
            throw new IllegalStateException("iterations is less than: 1");
        }
    }
}
