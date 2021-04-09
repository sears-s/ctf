package org.bouncycastle.pqc.crypto.qtesla;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public final class QTESLAKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private SecureRandom secureRandom;
    private int securityCategory;

    private byte[] allocatePrivate(int i) {
        return new byte[QTESLASecurityCategory.getPrivateSize(i)];
    }

    private byte[] allocatePublic(int i) {
        return new byte[QTESLASecurityCategory.getPublicSize(i)];
    }

    public AsymmetricCipherKeyPair generateKeyPair() {
        byte[] allocatePrivate = allocatePrivate(this.securityCategory);
        byte[] allocatePublic = allocatePublic(this.securityCategory);
        int i = this.securityCategory;
        if (i == 0) {
            QTESLA.generateKeyPairI(allocatePublic, allocatePrivate, this.secureRandom);
        } else if (i == 1) {
            QTESLA.generateKeyPairIIISize(allocatePublic, allocatePrivate, this.secureRandom);
        } else if (i == 2) {
            QTESLA.generateKeyPairIIISpeed(allocatePublic, allocatePrivate, this.secureRandom);
        } else if (i == 3) {
            QTESLA.generateKeyPairIP(allocatePublic, allocatePrivate, this.secureRandom);
        } else if (i == 4) {
            QTESLA.generateKeyPairIIIP(allocatePublic, allocatePrivate, this.secureRandom);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("unknown security category: ");
            sb.append(this.securityCategory);
            throw new IllegalArgumentException(sb.toString());
        }
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new QTESLAPublicKeyParameters(this.securityCategory, allocatePublic), (AsymmetricKeyParameter) new QTESLAPrivateKeyParameters(this.securityCategory, allocatePrivate));
    }

    public void init(KeyGenerationParameters keyGenerationParameters) {
        QTESLAKeyGenerationParameters qTESLAKeyGenerationParameters = (QTESLAKeyGenerationParameters) keyGenerationParameters;
        this.secureRandom = qTESLAKeyGenerationParameters.getRandom();
        this.securityCategory = qTESLAKeyGenerationParameters.getSecurityCategory();
    }
}
