package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.Digest;

public final class XMSSParameters {
    private final int height;
    private final int k;
    private final XMSSOid oid;
    private final WOTSPlus wotsPlus;

    public XMSSParameters(int i, Digest digest) {
        if (i < 2) {
            throw new IllegalArgumentException("height must be >= 2");
        } else if (digest != null) {
            this.wotsPlus = new WOTSPlus(new WOTSPlusParameters(digest));
            this.height = i;
            this.k = determineMinK();
            this.oid = DefaultXMSSOid.lookup(getDigest().getAlgorithmName(), getDigestSize(), getWinternitzParameter(), this.wotsPlus.getParams().getLen(), i);
        } else {
            throw new NullPointerException("digest == null");
        }
    }

    private int determineMinK() {
        int i = 2;
        while (true) {
            int i2 = this.height;
            if (i > i2) {
                throw new IllegalStateException("should never happen...");
            } else if ((i2 - i) % 2 == 0) {
                return i;
            } else {
                i++;
            }
        }
    }

    /* access modifiers changed from: protected */
    public Digest getDigest() {
        return this.wotsPlus.getParams().getDigest();
    }

    public int getDigestSize() {
        return this.wotsPlus.getParams().getDigestSize();
    }

    public int getHeight() {
        return this.height;
    }

    /* access modifiers changed from: 0000 */
    public int getK() {
        return this.k;
    }

    /* access modifiers changed from: 0000 */
    public XMSSOid getOid() {
        return this.oid;
    }

    /* access modifiers changed from: 0000 */
    public WOTSPlus getWOTSPlus() {
        return this.wotsPlus;
    }

    public int getWinternitzParameter() {
        return this.wotsPlus.getParams().getWinternitzParameter();
    }
}
