package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.DHKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource.PSpecified;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.ISO9796d1Encoding;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.ElGamalEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;
import org.bouncycastle.jcajce.provider.util.BadBlockException;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import org.bouncycastle.jce.interfaces.ElGamalKey;
import org.bouncycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;
import org.bouncycastle.util.Strings;
import org.jivesoftware.smack.util.StringUtils;

public class CipherSpi extends BaseCipherSpi {
    private ErasableOutputStream bOut = new ErasableOutputStream();
    private AsymmetricBlockCipher cipher;
    private AlgorithmParameters engineParams;
    private AlgorithmParameterSpec paramSpec;

    public static class NoPadding extends CipherSpi {
        public NoPadding() {
            super(new ElGamalEngine());
        }
    }

    public static class PKCS1v1_5Padding extends CipherSpi {
        public PKCS1v1_5Padding() {
            super(new PKCS1Encoding(new ElGamalEngine()));
        }
    }

    public CipherSpi(AsymmetricBlockCipher asymmetricBlockCipher) {
        this.cipher = asymmetricBlockCipher;
    }

    private byte[] getOutput() throws BadPaddingException {
        String str = "unable to decrypt block";
        try {
            byte[] processBlock = this.cipher.processBlock(this.bOut.getBuf(), 0, this.bOut.size());
            this.bOut.erase();
            return processBlock;
        } catch (InvalidCipherTextException e) {
            throw new BadBlockException(str, e);
        } catch (ArrayIndexOutOfBoundsException e2) {
            throw new BadBlockException(str, e2);
        } catch (Throwable th) {
            this.bOut.erase();
            throw th;
        }
    }

    private void initFromSpec(OAEPParameterSpec oAEPParameterSpec) throws NoSuchPaddingException {
        MGF1ParameterSpec mGF1ParameterSpec = (MGF1ParameterSpec) oAEPParameterSpec.getMGFParameters();
        Digest digest = DigestFactory.getDigest(mGF1ParameterSpec.getDigestAlgorithm());
        if (digest != null) {
            this.cipher = new OAEPEncoding(new ElGamalEngine(), digest, ((PSpecified) oAEPParameterSpec.getPSource()).getValue());
            this.paramSpec = oAEPParameterSpec;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("no match on OAEP constructor for digest algorithm: ");
        sb.append(mGF1ParameterSpec.getDigestAlgorithm());
        throw new NoSuchPaddingException(sb.toString());
    }

    /* access modifiers changed from: protected */
    public int engineDoFinal(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
        if (engineGetOutputSize(i2) + i3 <= bArr2.length) {
            if (bArr != null) {
                this.bOut.write(bArr, i, i2);
            }
            String str = "too much data for ElGamal block";
            if (this.cipher instanceof ElGamalEngine) {
                if (this.bOut.size() > this.cipher.getInputBlockSize() + 1) {
                    throw new ArrayIndexOutOfBoundsException(str);
                }
            } else if (this.bOut.size() > this.cipher.getInputBlockSize()) {
                throw new ArrayIndexOutOfBoundsException(str);
            }
            byte[] output = getOutput();
            for (int i4 = 0; i4 != output.length; i4++) {
                bArr2[i3 + i4] = output[i4];
            }
            return output.length;
        }
        throw new ShortBufferException("output buffer too short for input.");
    }

    /* access modifiers changed from: protected */
    public byte[] engineDoFinal(byte[] bArr, int i, int i2) throws IllegalBlockSizeException, BadPaddingException {
        if (bArr != null) {
            this.bOut.write(bArr, i, i2);
        }
        String str = "too much data for ElGamal block";
        if (this.cipher instanceof ElGamalEngine) {
            if (this.bOut.size() > this.cipher.getInputBlockSize() + 1) {
                throw new ArrayIndexOutOfBoundsException(str);
            }
        } else if (this.bOut.size() > this.cipher.getInputBlockSize()) {
            throw new ArrayIndexOutOfBoundsException(str);
        }
        return getOutput();
    }

    /* access modifiers changed from: protected */
    public int engineGetBlockSize() {
        return this.cipher.getInputBlockSize();
    }

    /* access modifiers changed from: protected */
    public int engineGetKeySize(Key key) {
        BigInteger p;
        if (key instanceof ElGamalKey) {
            p = ((ElGamalKey) key).getParameters().getP();
        } else if (key instanceof DHKey) {
            p = ((DHKey) key).getParams().getP();
        } else {
            throw new IllegalArgumentException("not an ElGamal key!");
        }
        return p.bitLength();
    }

    /* access modifiers changed from: protected */
    public int engineGetOutputSize(int i) {
        return this.cipher.getOutputBlockSize();
    }

    /* access modifiers changed from: protected */
    public AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.paramSpec != null) {
            try {
                this.engineParams = createParametersInstance("OAEP");
                this.engineParams.init(this.paramSpec);
            } catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
        }
        return this.engineParams;
    }

    /* access modifiers changed from: protected */
    public void engineInit(int i, Key key, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("can't handle parameters in ElGamal");
    }

    /* access modifiers changed from: protected */
    public void engineInit(int i, Key key, SecureRandom secureRandom) throws InvalidKeyException {
        try {
            engineInit(i, key, (AlgorithmParameterSpec) null, secureRandom);
        } catch (InvalidAlgorithmParameterException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Eeeek! ");
            sb.append(e.toString());
            throw new InvalidKeyException(sb.toString(), e);
        }
    }

    /* access modifiers changed from: protected */
    public void engineInit(int i, Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters cipherParameters;
        AsymmetricBlockCipher asymmetricBlockCipher;
        if (key instanceof DHPublicKey) {
            cipherParameters = ElGamalUtil.generatePublicKeyParameter((PublicKey) key);
        } else if (key instanceof DHPrivateKey) {
            cipherParameters = ElGamalUtil.generatePrivateKeyParameter((PrivateKey) key);
        } else {
            throw new InvalidKeyException("unknown key type passed to ElGamal");
        }
        if (algorithmParameterSpec instanceof OAEPParameterSpec) {
            OAEPParameterSpec oAEPParameterSpec = (OAEPParameterSpec) algorithmParameterSpec;
            this.paramSpec = algorithmParameterSpec;
            if (!oAEPParameterSpec.getMGFAlgorithm().equalsIgnoreCase("MGF1") && !oAEPParameterSpec.getMGFAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1.getId())) {
                throw new InvalidAlgorithmParameterException("unknown mask generation function specified");
            } else if (oAEPParameterSpec.getMGFParameters() instanceof MGF1ParameterSpec) {
                Digest digest = DigestFactory.getDigest(oAEPParameterSpec.getDigestAlgorithm());
                if (digest != null) {
                    MGF1ParameterSpec mGF1ParameterSpec = (MGF1ParameterSpec) oAEPParameterSpec.getMGFParameters();
                    Digest digest2 = DigestFactory.getDigest(mGF1ParameterSpec.getDigestAlgorithm());
                    if (digest2 != null) {
                        this.cipher = new OAEPEncoding(new ElGamalEngine(), digest, digest2, ((PSpecified) oAEPParameterSpec.getPSource()).getValue());
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("no match on MGF digest algorithm: ");
                        sb.append(mGF1ParameterSpec.getDigestAlgorithm());
                        throw new InvalidAlgorithmParameterException(sb.toString());
                    }
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("no match on digest algorithm: ");
                    sb2.append(oAEPParameterSpec.getDigestAlgorithm());
                    throw new InvalidAlgorithmParameterException(sb2.toString());
                }
            } else {
                throw new InvalidAlgorithmParameterException("unkown MGF parameters");
            }
        } else if (algorithmParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("unknown parameter type.");
        }
        if (secureRandom != null) {
            cipherParameters = new ParametersWithRandom(cipherParameters, secureRandom);
        }
        boolean z = true;
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("unknown opmode ");
                        sb3.append(i);
                        sb3.append(" passed to ElGamal");
                        throw new InvalidParameterException(sb3.toString());
                    }
                }
            }
            asymmetricBlockCipher = this.cipher;
            z = false;
            asymmetricBlockCipher.init(z, cipherParameters);
        }
        asymmetricBlockCipher = this.cipher;
        asymmetricBlockCipher.init(z, cipherParameters);
    }

    /* access modifiers changed from: protected */
    public void engineSetMode(String str) throws NoSuchAlgorithmException {
        String upperCase = Strings.toUpperCase(str);
        if (!upperCase.equals("NONE") && !upperCase.equals("ECB")) {
            StringBuilder sb = new StringBuilder();
            sb.append("can't support mode ");
            sb.append(str);
            throw new NoSuchAlgorithmException(sb.toString());
        }
    }

    /* access modifiers changed from: protected */
    public void engineSetPadding(String str) throws NoSuchPaddingException {
        OAEPParameterSpec oAEPParameterSpec;
        AsymmetricBlockCipher iSO9796d1Encoding;
        String upperCase = Strings.toUpperCase(str);
        if (upperCase.equals("NOPADDING")) {
            iSO9796d1Encoding = new ElGamalEngine();
        } else if (upperCase.equals("PKCS1PADDING")) {
            iSO9796d1Encoding = new PKCS1Encoding(new ElGamalEngine());
        } else if (upperCase.equals("ISO9796-1PADDING")) {
            iSO9796d1Encoding = new ISO9796d1Encoding(new ElGamalEngine());
        } else {
            if (!upperCase.equals("OAEPPADDING")) {
                String str2 = "MGF1";
                if (upperCase.equals("OAEPWITHMD5ANDMGF1PADDING")) {
                    String str3 = StringUtils.MD5;
                    oAEPParameterSpec = new OAEPParameterSpec(str3, str2, new MGF1ParameterSpec(str3), PSpecified.DEFAULT);
                } else if (!upperCase.equals("OAEPWITHSHA1ANDMGF1PADDING")) {
                    if (upperCase.equals("OAEPWITHSHA224ANDMGF1PADDING")) {
                        String str4 = McElieceCCA2KeyGenParameterSpec.SHA224;
                        oAEPParameterSpec = new OAEPParameterSpec(str4, str2, new MGF1ParameterSpec(str4), PSpecified.DEFAULT);
                    } else if (upperCase.equals("OAEPWITHSHA256ANDMGF1PADDING")) {
                        oAEPParameterSpec = new OAEPParameterSpec("SHA-256", str2, MGF1ParameterSpec.SHA256, PSpecified.DEFAULT);
                    } else if (upperCase.equals("OAEPWITHSHA384ANDMGF1PADDING")) {
                        oAEPParameterSpec = new OAEPParameterSpec(McElieceCCA2KeyGenParameterSpec.SHA384, str2, MGF1ParameterSpec.SHA384, PSpecified.DEFAULT);
                    } else if (upperCase.equals("OAEPWITHSHA512ANDMGF1PADDING")) {
                        oAEPParameterSpec = new OAEPParameterSpec("SHA-512", str2, MGF1ParameterSpec.SHA512, PSpecified.DEFAULT);
                    } else if (upperCase.equals("OAEPWITHSHA3-224ANDMGF1PADDING")) {
                        String str5 = "SHA3-224";
                        oAEPParameterSpec = new OAEPParameterSpec(str5, str2, new MGF1ParameterSpec(str5), PSpecified.DEFAULT);
                    } else if (upperCase.equals("OAEPWITHSHA3-256ANDMGF1PADDING")) {
                        String str6 = "SHA3-256";
                        oAEPParameterSpec = new OAEPParameterSpec(str6, str2, new MGF1ParameterSpec(str6), PSpecified.DEFAULT);
                    } else if (upperCase.equals("OAEPWITHSHA3-384ANDMGF1PADDING")) {
                        String str7 = "SHA3-384";
                        oAEPParameterSpec = new OAEPParameterSpec(str7, str2, new MGF1ParameterSpec(str7), PSpecified.DEFAULT);
                    } else if (upperCase.equals("OAEPWITHSHA3-512ANDMGF1PADDING")) {
                        String str8 = "SHA3-512";
                        oAEPParameterSpec = new OAEPParameterSpec(str8, str2, new MGF1ParameterSpec(str8), PSpecified.DEFAULT);
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append(str);
                        sb.append(" unavailable with ElGamal.");
                        throw new NoSuchPaddingException(sb.toString());
                    }
                }
                initFromSpec(oAEPParameterSpec);
                return;
            }
            oAEPParameterSpec = OAEPParameterSpec.DEFAULT;
            initFromSpec(oAEPParameterSpec);
            return;
        }
        this.cipher = iSO9796d1Encoding;
    }

    /* access modifiers changed from: protected */
    public int engineUpdate(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        this.bOut.write(bArr, i, i2);
        return 0;
    }

    /* access modifiers changed from: protected */
    public byte[] engineUpdate(byte[] bArr, int i, int i2) {
        this.bOut.write(bArr, i, i2);
        return null;
    }
}
