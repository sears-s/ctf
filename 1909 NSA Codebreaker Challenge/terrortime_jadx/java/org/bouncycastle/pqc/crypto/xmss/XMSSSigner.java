package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.StateAwareMessageSigner;
import org.bouncycastle.pqc.crypto.xmss.XMSSSignature.Builder;
import org.bouncycastle.util.Arrays;

public class XMSSSigner implements StateAwareMessageSigner {
    private boolean hasGenerated;
    private boolean initSign;
    private KeyedHashFunctions khf;
    private XMSSPrivateKeyParameters nextKeyGenerator;
    private XMSSParameters params;
    private XMSSPrivateKeyParameters privateKey;
    private XMSSPublicKeyParameters publicKey;

    private WOTSPlusSignature wotsSign(byte[] bArr, OTSHashAddress oTSHashAddress) {
        if (bArr.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        } else if (oTSHashAddress != null) {
            this.params.getWOTSPlus().importKeys(this.params.getWOTSPlus().getWOTSPlusSecretKey(this.privateKey.getSecretKeySeed(), oTSHashAddress), this.privateKey.getPublicSeed());
            return this.params.getWOTSPlus().sign(bArr, oTSHashAddress);
        } else {
            throw new NullPointerException("otsHashAddress == null");
        }
    }

    public byte[] generateSignature(byte[] bArr) {
        if (bArr == null) {
            throw new NullPointerException("message == null");
        } else if (this.initSign) {
            XMSSPrivateKeyParameters xMSSPrivateKeyParameters = this.privateKey;
            if (xMSSPrivateKeyParameters == null) {
                throw new IllegalStateException("signing key no longer usable");
            } else if (!xMSSPrivateKeyParameters.getBDSState().getAuthenticationPath().isEmpty()) {
                int index = this.privateKey.getIndex();
                long j = (long) index;
                if (XMSSUtil.isIndexValid(this.params.getHeight(), j)) {
                    byte[] PRF = this.khf.PRF(this.privateKey.getSecretKeyPRF(), XMSSUtil.toBytesBigEndian(j, 32));
                    XMSSSignature xMSSSignature = (XMSSSignature) new Builder(this.params).withIndex(index).withRandom(PRF).withWOTSPlusSignature(wotsSign(this.khf.HMsg(Arrays.concatenate(PRF, this.privateKey.getRoot(), XMSSUtil.toBytesBigEndian(j, this.params.getDigestSize())), bArr), (OTSHashAddress) new Builder().withOTSAddress(index).build())).withAuthPath(this.privateKey.getBDSState().getAuthenticationPath()).build();
                    this.hasGenerated = true;
                    XMSSPrivateKeyParameters xMSSPrivateKeyParameters2 = this.nextKeyGenerator;
                    if (xMSSPrivateKeyParameters2 != null) {
                        this.privateKey = xMSSPrivateKeyParameters2.getNextKey();
                        this.nextKeyGenerator = this.privateKey;
                    } else {
                        this.privateKey = null;
                    }
                    return xMSSSignature.toByteArray();
                }
                throw new IllegalStateException("index out of bounds");
            } else {
                throw new IllegalStateException("not initialized");
            }
        } else {
            throw new IllegalStateException("signer not initialized for signature generation");
        }
    }

    public AsymmetricKeyParameter getUpdatedPrivateKey() {
        XMSSPrivateKeyParameters nextKey;
        if (this.hasGenerated) {
            nextKey = this.privateKey;
            this.privateKey = null;
        } else {
            nextKey = this.nextKeyGenerator.getNextKey();
        }
        this.nextKeyGenerator = null;
        return nextKey;
    }

    public long getUsagesRemaining() {
        return this.privateKey.getUsagesRemaining();
    }

    public void init(boolean z, CipherParameters cipherParameters) {
        XMSSParameters xMSSParameters;
        if (z) {
            this.initSign = true;
            this.hasGenerated = false;
            this.privateKey = (XMSSPrivateKeyParameters) cipherParameters;
            XMSSPrivateKeyParameters xMSSPrivateKeyParameters = this.privateKey;
            this.nextKeyGenerator = xMSSPrivateKeyParameters;
            xMSSParameters = xMSSPrivateKeyParameters.getParameters();
        } else {
            this.initSign = false;
            this.publicKey = (XMSSPublicKeyParameters) cipherParameters;
            xMSSParameters = this.publicKey.getParameters();
        }
        this.params = xMSSParameters;
        this.khf = this.params.getWOTSPlus().getKhf();
    }

    public boolean verifySignature(byte[] bArr, byte[] bArr2) {
        XMSSSignature build = new Builder(this.params).withSignature(bArr2).build();
        int index = build.getIndex();
        this.params.getWOTSPlus().importKeys(new byte[this.params.getDigestSize()], this.publicKey.getPublicSeed());
        long j = (long) index;
        byte[] HMsg = this.khf.HMsg(Arrays.concatenate(build.getRandom(), this.publicKey.getRoot(), XMSSUtil.toBytesBigEndian(j, this.params.getDigestSize())), bArr);
        int height = this.params.getHeight();
        int leafIndex = XMSSUtil.getLeafIndex(j, height);
        return Arrays.constantTimeAreEqual(XMSSVerifierUtil.getRootNodeFromSignature(this.params.getWOTSPlus(), height, HMsg, build, (OTSHashAddress) new Builder().withOTSAddress(index).build(), leafIndex).getValue(), this.publicKey.getRoot());
    }
}
