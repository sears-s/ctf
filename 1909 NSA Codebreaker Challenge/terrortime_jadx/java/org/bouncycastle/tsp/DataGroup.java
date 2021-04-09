package org.bouncycastle.tsp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Arrays;

public class DataGroup {
    private List<byte[]> dataObjects;
    private byte[] groupHash;
    private TreeSet<byte[]> hashes;

    private class ByteArrayComparator implements Comparator {
        private ByteArrayComparator() {
        }

        public int compare(Object obj, Object obj2) {
            byte[] bArr = (byte[]) obj;
            byte[] bArr2 = (byte[]) obj2;
            int length = bArr.length < bArr2.length ? bArr.length : bArr2.length;
            for (int i = 0; i != length; i++) {
                byte b = bArr[i] & 255;
                byte b2 = bArr2[i] & 255;
                if (b != b2) {
                    return b - b2;
                }
            }
            return bArr.length - bArr2.length;
        }
    }

    public DataGroup(List<byte[]> list) {
        this.dataObjects = list;
    }

    public DataGroup(byte[] bArr) {
        this.dataObjects = new ArrayList();
        this.dataObjects.add(bArr);
    }

    static byte[] calcDigest(DigestCalculator digestCalculator, byte[] bArr) {
        try {
            OutputStream outputStream = digestCalculator.getOutputStream();
            outputStream.write(bArr);
            outputStream.close();
            return digestCalculator.getDigest();
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("digest calculator failure: ");
            sb.append(e.getMessage());
            throw new IllegalStateException(sb.toString());
        }
    }

    private TreeSet<byte[]> getHashes(DigestCalculator digestCalculator, byte[] bArr) {
        if (this.hashes == null) {
            this.hashes = new TreeSet<>(new ByteArrayComparator());
            for (int i = 0; i != this.dataObjects.size(); i++) {
                byte[] bArr2 = (byte[]) this.dataObjects.get(i);
                TreeSet<byte[]> treeSet = this.hashes;
                byte[] calcDigest = calcDigest(digestCalculator, bArr2);
                if (bArr != null) {
                    treeSet.add(calcDigest(digestCalculator, Arrays.concatenate(calcDigest, bArr)));
                } else {
                    treeSet.add(calcDigest);
                }
            }
        }
        return this.hashes;
    }

    public byte[] getHash(DigestCalculator digestCalculator) {
        if (this.groupHash == null) {
            TreeSet hashes2 = getHashes(digestCalculator);
            if (hashes2.size() > 1) {
                byte[] bArr = new byte[0];
                Iterator it = hashes2.iterator();
                while (it.hasNext()) {
                    bArr = Arrays.concatenate(bArr, (byte[]) it.next());
                }
                this.groupHash = calcDigest(digestCalculator, bArr);
            } else {
                this.groupHash = (byte[]) hashes2.first();
            }
        }
        return this.groupHash;
    }

    public TreeSet<byte[]> getHashes(DigestCalculator digestCalculator) {
        return getHashes(digestCalculator, null);
    }
}
