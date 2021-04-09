package org.bouncycastle.pqc.crypto.qtesla;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;

public class QTESLASigner implements MessageSigner {
    private QTESLAPrivateKeyParameters privateKey;
    private QTESLAPublicKeyParameters publicKey;
    private SecureRandom secureRandom;

    public byte[] generateSignature(byte[] bArr) {
        byte[] bArr2 = new byte[QTESLASecurityCategory.getSignatureSize(this.privateKey.getSecurityCategory())];
        int securityCategory = this.privateKey.getSecurityCategory();
        if (securityCategory == 0) {
            QTESLA.signingI(bArr2, bArr, 0, bArr.length, this.privateKey.getSecret(), this.secureRandom);
        } else if (securityCategory == 1) {
            QTESLA.signingIIISize(bArr2, bArr, 0, bArr.length, this.privateKey.getSecret(), this.secureRandom);
        } else if (securityCategory == 2) {
            QTESLA.signingIIISpeed(bArr2, bArr, 0, bArr.length, this.privateKey.getSecret(), this.secureRandom);
        } else if (securityCategory == 3) {
            QTESLA.signingIP(bArr2, bArr, 0, bArr.length, this.privateKey.getSecret(), this.secureRandom);
        } else if (securityCategory == 4) {
            QTESLA.signingIIIP(bArr2, bArr, 0, bArr.length, this.privateKey.getSecret(), this.secureRandom);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("unknown security category: ");
            sb.append(this.privateKey.getSecurityCategory());
            throw new IllegalArgumentException(sb.toString());
        }
        return bArr2;
    }

    public void init(boolean z, CipherParameters cipherParameters) {
        int i;
        if (z) {
            if (cipherParameters instanceof ParametersWithRandom) {
                ParametersWithRandom parametersWithRandom = (ParametersWithRandom) cipherParameters;
                this.secureRandom = parametersWithRandom.getRandom();
                this.privateKey = (QTESLAPrivateKeyParameters) parametersWithRandom.getParameters();
            } else {
                this.secureRandom = CryptoServicesRegistrar.getSecureRandom();
                this.privateKey = (QTESLAPrivateKeyParameters) cipherParameters;
            }
            this.publicKey = null;
            i = this.privateKey.getSecurityCategory();
        } else {
            this.privateKey = null;
            this.publicKey = (QTESLAPublicKeyParameters) cipherParameters;
            i = this.publicKey.getSecurityCategory();
        }
        QTESLASecurityCategory.validate(i);
    }

    public boolean verifySignature(byte[] bArr, byte[] bArr2) {
        int i;
        int securityCategory = this.publicKey.getSecurityCategory();
        if (securityCategory == 0) {
            i = QTESLA.verifyingI(bArr, bArr2, 0, bArr2.length, this.publicKey.getPublicData());
        } else if (securityCategory == 1) {
            i = QTESLA.verifyingIIISize(bArr, bArr2, 0, bArr2.length, this.publicKey.getPublicData());
        } else if (securityCategory == 2) {
            i = QTESLA.verifyingIIISpeed(bArr, bArr2, 0, bArr2.length, this.publicKey.getPublicData());
        } else if (securityCategory == 3) {
            i = QTESLA.verifyingPI(bArr, bArr2, 0, bArr2.length, this.publicKey.getPublicData());
        } else if (securityCategory == 4) {
            i = QTESLA.verifyingPIII(bArr, bArr2, 0, bArr2.length, this.publicKey.getPublicData());
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("unknown security category: ");
            sb.append(this.publicKey.getSecurityCategory());
            throw new IllegalArgumentException(sb.toString());
        }
        return i == 0;
    }
}
