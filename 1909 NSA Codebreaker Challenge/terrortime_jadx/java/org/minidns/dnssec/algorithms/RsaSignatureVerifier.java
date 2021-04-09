package org.minidns.dnssec.algorithms;

import java.io.DataInput;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import org.minidns.dnssec.DnssecValidationFailedException.DataMalformedException;
import org.minidns.dnssec.DnssecValidationFailedException.DnssecInvalidKeySpecException;
import org.minidns.record.DNSKEY;
import org.minidns.record.RRSIG;

class RsaSignatureVerifier extends JavaSecSignatureVerifier {
    public RsaSignatureVerifier(String algorithm) throws NoSuchAlgorithmException {
        super("RSA", algorithm);
    }

    /* access modifiers changed from: protected */
    public PublicKey getPublicKey(DNSKEY key) throws DataMalformedException, DnssecInvalidKeySpecException {
        DataInput dis = key.getKeyAsDataInputStream();
        try {
            int exponentLength = dis.readUnsignedByte();
            int bytesRead = 1;
            if (exponentLength == 0) {
                bytesRead = 1 + 2;
                exponentLength = dis.readUnsignedShort();
            }
            byte[] exponentBytes = new byte[exponentLength];
            dis.readFully(exponentBytes);
            int bytesRead2 = bytesRead + exponentLength;
            BigInteger exponent = new BigInteger(1, exponentBytes);
            byte[] modulusBytes = new byte[(key.getKeyLength() - bytesRead2)];
            dis.readFully(modulusBytes);
            try {
                return getKeyFactory().generatePublic(new RSAPublicKeySpec(new BigInteger(1, modulusBytes), exponent));
            } catch (InvalidKeySpecException e) {
                throw new DnssecInvalidKeySpecException(e);
            }
        } catch (IOException e2) {
            throw new DataMalformedException(e2, key.getKey());
        }
    }

    /* access modifiers changed from: protected */
    public byte[] getSignature(RRSIG rrsig) {
        return rrsig.getSignature();
    }
}
