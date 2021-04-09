package org.bouncycastle.pqc.crypto.xmss;

import java.io.IOException;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Pack;

public final class XMSSMTPublicKeyParameters extends XMSSMTKeyParameters implements XMSSStoreableObjectInterface, Encodable {
    private final int oid;
    private final XMSSMTParameters params;
    private final byte[] publicSeed;
    private final byte[] root;

    public static class Builder {
        /* access modifiers changed from: private */
        public final XMSSMTParameters params;
        /* access modifiers changed from: private */
        public byte[] publicKey = null;
        /* access modifiers changed from: private */
        public byte[] publicSeed = null;
        /* access modifiers changed from: private */
        public byte[] root = null;

        public Builder(XMSSMTParameters xMSSMTParameters) {
            this.params = xMSSMTParameters;
        }

        public XMSSMTPublicKeyParameters build() {
            return new XMSSMTPublicKeyParameters(this);
        }

        public Builder withPublicKey(byte[] bArr) {
            this.publicKey = XMSSUtil.cloneArray(bArr);
            return this;
        }

        public Builder withPublicSeed(byte[] bArr) {
            this.publicSeed = XMSSUtil.cloneArray(bArr);
            return this;
        }

        public Builder withRoot(byte[] bArr) {
            this.root = XMSSUtil.cloneArray(bArr);
            return this;
        }
    }

    private XMSSMTPublicKeyParameters(Builder builder) {
        int i = 0;
        super(false, builder.params.getDigest().getAlgorithmName());
        this.params = builder.params;
        XMSSMTParameters xMSSMTParameters = this.params;
        if (xMSSMTParameters != null) {
            int digestSize = xMSSMTParameters.getDigestSize();
            byte[] access$100 = builder.publicKey;
            if (access$100 == null) {
                if (this.params.getOid() != null) {
                    i = this.params.getOid().getOid();
                }
                this.oid = i;
                byte[] access$200 = builder.root;
                if (access$200 == null) {
                    this.root = new byte[digestSize];
                } else if (access$200.length == digestSize) {
                    this.root = access$200;
                } else {
                    throw new IllegalArgumentException("length of root must be equal to length of digest");
                }
                byte[] access$300 = builder.publicSeed;
                if (access$300 == null) {
                    this.publicSeed = new byte[digestSize];
                } else if (access$300.length == digestSize) {
                    this.publicSeed = access$300;
                } else {
                    throw new IllegalArgumentException("length of publicSeed must be equal to length of digest");
                }
            } else if (access$100.length == digestSize + digestSize) {
                this.oid = 0;
                this.root = XMSSUtil.extractBytesAtOffset(access$100, 0, digestSize);
                this.publicSeed = XMSSUtil.extractBytesAtOffset(access$100, digestSize + 0, digestSize);
            } else if (access$100.length == digestSize + 4 + digestSize) {
                this.oid = Pack.bigEndianToInt(access$100, 0);
                this.root = XMSSUtil.extractBytesAtOffset(access$100, 4, digestSize);
                this.publicSeed = XMSSUtil.extractBytesAtOffset(access$100, 4 + digestSize, digestSize);
            } else {
                throw new IllegalArgumentException("public key has wrong size");
            }
        } else {
            throw new NullPointerException("params == null");
        }
    }

    public byte[] getEncoded() throws IOException {
        return toByteArray();
    }

    public XMSSMTParameters getParameters() {
        return this.params;
    }

    public byte[] getPublicSeed() {
        return XMSSUtil.cloneArray(this.publicSeed);
    }

    public byte[] getRoot() {
        return XMSSUtil.cloneArray(this.root);
    }

    public byte[] toByteArray() {
        byte[] bArr;
        int digestSize = this.params.getDigestSize();
        int i = this.oid;
        int i2 = 0;
        if (i != 0) {
            bArr = new byte[(digestSize + 4 + digestSize)];
            Pack.intToBigEndian(i, bArr, 0);
            i2 = 4;
        } else {
            bArr = new byte[(digestSize + digestSize)];
        }
        XMSSUtil.copyBytesAtOffset(bArr, this.root, i2);
        XMSSUtil.copyBytesAtOffset(bArr, this.publicSeed, i2 + digestSize);
        return bArr;
    }
}
