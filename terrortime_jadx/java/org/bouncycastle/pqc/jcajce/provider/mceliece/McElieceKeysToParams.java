package org.bouncycastle.pqc.jcajce.provider.mceliece;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;

public class McElieceKeysToParams {
    public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof BCMcEliecePrivateKey) {
            BCMcEliecePrivateKey bCMcEliecePrivateKey = (BCMcEliecePrivateKey) privateKey;
            McEliecePrivateKeyParameters mcEliecePrivateKeyParameters = new McEliecePrivateKeyParameters(bCMcEliecePrivateKey.getN(), bCMcEliecePrivateKey.getK(), bCMcEliecePrivateKey.getField(), bCMcEliecePrivateKey.getGoppaPoly(), bCMcEliecePrivateKey.getP1(), bCMcEliecePrivateKey.getP2(), bCMcEliecePrivateKey.getSInv());
            return mcEliecePrivateKeyParameters;
        }
        throw new InvalidKeyException("can't identify McEliece private key.");
    }

    public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof BCMcEliecePublicKey) {
            return ((BCMcEliecePublicKey) publicKey).getKeyParams();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("can't identify McEliece public key: ");
        sb.append(publicKey.getClass().getName());
        throw new InvalidKeyException(sb.toString());
    }
}
