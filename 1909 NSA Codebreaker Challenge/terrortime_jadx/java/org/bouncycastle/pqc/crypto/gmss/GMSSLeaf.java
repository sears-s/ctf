package org.bouncycastle.pqc.crypto.gmss;

import com.badguy.terrortime.BuildConfig;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class GMSSLeaf {
    private byte[] concHashs;
    private GMSSRandom gmssRandom;
    private int i;
    private int j;
    private int keysize;
    private byte[] leaf;
    private int mdsize;
    private Digest messDigestOTS;
    byte[] privateKeyOTS;
    private byte[] seed;
    private int steps;
    private int two_power_w;
    private int w;

    GMSSLeaf(Digest digest, int i2, int i3) {
        this.w = i2;
        this.messDigestOTS = digest;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.mdsize = this.messDigestOTS.getDigestSize();
        double d = (double) i2;
        int ceil = (int) Math.ceil(((double) (this.mdsize << 3)) / d);
        this.keysize = ceil + ((int) Math.ceil(((double) getLog((ceil << i2) + 1)) / d));
        int i4 = 1 << i2;
        this.two_power_w = i4;
        int i5 = i4 - 1;
        int i6 = this.keysize;
        this.steps = (int) Math.ceil(((double) (((i5 * i6) + 1) + i6)) / ((double) i3));
        int i7 = this.mdsize;
        this.seed = new byte[i7];
        this.leaf = new byte[i7];
        this.privateKeyOTS = new byte[i7];
        this.concHashs = new byte[(i7 * this.keysize)];
    }

    public GMSSLeaf(Digest digest, int i2, int i3, byte[] bArr) {
        this.w = i2;
        this.messDigestOTS = digest;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.mdsize = this.messDigestOTS.getDigestSize();
        double d = (double) i2;
        int ceil = (int) Math.ceil(((double) (this.mdsize << 3)) / d);
        this.keysize = ceil + ((int) Math.ceil(((double) getLog((ceil << i2) + 1)) / d));
        int i4 = 1 << i2;
        this.two_power_w = i4;
        int i5 = i4 - 1;
        int i6 = this.keysize;
        this.steps = (int) Math.ceil(((double) (((i5 * i6) + 1) + i6)) / ((double) i3));
        int i7 = this.mdsize;
        this.seed = new byte[i7];
        this.leaf = new byte[i7];
        this.privateKeyOTS = new byte[i7];
        this.concHashs = new byte[(i7 * this.keysize)];
        initLeafCalc(bArr);
    }

    public GMSSLeaf(Digest digest, byte[][] bArr, int[] iArr) {
        this.i = iArr[0];
        this.j = iArr[1];
        this.steps = iArr[2];
        this.w = iArr[3];
        this.messDigestOTS = digest;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.mdsize = this.messDigestOTS.getDigestSize();
        int ceil = (int) Math.ceil(((double) (this.mdsize << 3)) / ((double) this.w));
        this.keysize = ceil + ((int) Math.ceil(((double) getLog((ceil << this.w) + 1)) / ((double) this.w)));
        this.two_power_w = 1 << this.w;
        this.privateKeyOTS = bArr[0];
        this.seed = bArr[1];
        this.concHashs = bArr[2];
        this.leaf = bArr[3];
    }

    private GMSSLeaf(GMSSLeaf gMSSLeaf) {
        this.messDigestOTS = gMSSLeaf.messDigestOTS;
        this.mdsize = gMSSLeaf.mdsize;
        this.keysize = gMSSLeaf.keysize;
        this.gmssRandom = gMSSLeaf.gmssRandom;
        this.leaf = Arrays.clone(gMSSLeaf.leaf);
        this.concHashs = Arrays.clone(gMSSLeaf.concHashs);
        this.i = gMSSLeaf.i;
        this.j = gMSSLeaf.j;
        this.two_power_w = gMSSLeaf.two_power_w;
        this.w = gMSSLeaf.w;
        this.steps = gMSSLeaf.steps;
        this.seed = Arrays.clone(gMSSLeaf.seed);
        this.privateKeyOTS = Arrays.clone(gMSSLeaf.privateKeyOTS);
    }

    private int getLog(int i2) {
        int i3 = 1;
        int i4 = 2;
        while (i4 < i2) {
            i4 <<= 1;
            i3++;
        }
        return i3;
    }

    private void updateLeafCalc() {
        byte[] bArr = new byte[this.messDigestOTS.getDigestSize()];
        for (int i2 = 0; i2 < this.steps + 10000; i2++) {
            if (this.i == this.keysize && this.j == this.two_power_w - 1) {
                Digest digest = this.messDigestOTS;
                byte[] bArr2 = this.concHashs;
                digest.update(bArr2, 0, bArr2.length);
                this.leaf = new byte[this.messDigestOTS.getDigestSize()];
                this.messDigestOTS.doFinal(this.leaf, 0);
                return;
            }
            if (this.i == 0 || this.j == this.two_power_w - 1) {
                this.i++;
                this.j = 0;
                this.privateKeyOTS = this.gmssRandom.nextSeed(this.seed);
            } else {
                Digest digest2 = this.messDigestOTS;
                byte[] bArr3 = this.privateKeyOTS;
                digest2.update(bArr3, 0, bArr3.length);
                this.privateKeyOTS = bArr;
                this.messDigestOTS.doFinal(this.privateKeyOTS, 0);
                this.j++;
                if (this.j == this.two_power_w - 1) {
                    byte[] bArr4 = this.privateKeyOTS;
                    byte[] bArr5 = this.concHashs;
                    int i3 = this.mdsize;
                    System.arraycopy(bArr4, 0, bArr5, (this.i - 1) * i3, i3);
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("unable to updateLeaf in steps: ");
        sb.append(this.steps);
        String str = " ";
        sb.append(str);
        sb.append(this.i);
        sb.append(str);
        sb.append(this.j);
        throw new IllegalStateException(sb.toString());
    }

    public byte[] getLeaf() {
        return Arrays.clone(this.leaf);
    }

    public byte[][] getStatByte() {
        return new byte[][]{this.privateKeyOTS, this.seed, this.concHashs, this.leaf};
    }

    public int[] getStatInt() {
        return new int[]{this.i, this.j, this.steps, this.w};
    }

    /* access modifiers changed from: 0000 */
    public void initLeafCalc(byte[] bArr) {
        this.i = 0;
        this.j = 0;
        byte[] bArr2 = new byte[this.mdsize];
        System.arraycopy(bArr, 0, bArr2, 0, this.seed.length);
        this.seed = this.gmssRandom.nextSeed(bArr2);
    }

    /* access modifiers changed from: 0000 */
    public GMSSLeaf nextLeaf() {
        GMSSLeaf gMSSLeaf = new GMSSLeaf(this);
        gMSSLeaf.updateLeafCalc();
        return gMSSLeaf;
    }

    public String toString() {
        String str;
        StringBuilder sb;
        String str2 = BuildConfig.FLAVOR;
        int i2 = 0;
        while (true) {
            str = " ";
            if (i2 >= 4) {
                break;
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str2);
            sb2.append(getStatInt()[i2]);
            sb2.append(str);
            str2 = sb2.toString();
            i2++;
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str2);
        sb3.append(str);
        sb3.append(this.mdsize);
        sb3.append(str);
        sb3.append(this.keysize);
        sb3.append(str);
        sb3.append(this.two_power_w);
        sb3.append(str);
        String sb4 = sb3.toString();
        byte[][] statByte = getStatByte();
        for (int i3 = 0; i3 < 4; i3++) {
            if (statByte[i3] != null) {
                sb = new StringBuilder();
                sb.append(sb4);
                sb.append(new String(Hex.encode(statByte[i3])));
                sb.append(str);
            } else {
                sb = new StringBuilder();
                sb.append(sb4);
                sb.append("null ");
            }
            sb4 = sb.toString();
        }
        return sb4;
    }
}
