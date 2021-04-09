package org.minidns.dnssec.algorithms;

import java.io.DataInput;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import org.minidns.dnssec.DnssecValidationFailedException.DataMalformedException;
import org.minidns.dnssec.DnssecValidationFailedException.DnssecInvalidKeySpecException;
import org.minidns.record.DNSKEY;
import org.minidns.record.RRSIG;

class EcgostSignatureVerifier extends JavaSecSignatureVerifier {
    private static final int LENGTH = 32;
    private static final ECParameterSpec SPEC = new ECParameterSpec(new EllipticCurve(new ECFieldFp(new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFD97", 16)), new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFD94", 16), new BigInteger("A6", 16)), new ECPoint(BigInteger.ONE, new BigInteger("8D91E471E0989CDA27DF505A453F2B7635294F2DDF23E3B122ACC99C9E9F1E14", 16)), new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF6C611070995AD10045841B09B761B893", 16), 1);

    public EcgostSignatureVerifier() throws NoSuchAlgorithmException {
        super("ECGOST3410", "GOST3411withECGOST3410");
    }

    /* access modifiers changed from: protected */
    public byte[] getSignature(RRSIG rrsig) {
        return rrsig.getSignature();
    }

    /* access modifiers changed from: protected */
    public PublicKey getPublicKey(DNSKEY key) throws DataMalformedException, DnssecInvalidKeySpecException {
        DataInput dis = key.getKeyAsDataInputStream();
        try {
            byte[] xBytes = new byte[32];
            dis.readFully(xBytes);
            reverse(xBytes);
            BigInteger x = new BigInteger(1, xBytes);
            byte[] yBytes = new byte[32];
            dis.readFully(yBytes);
            reverse(yBytes);
            try {
                return getKeyFactory().generatePublic(new ECPublicKeySpec(new ECPoint(x, new BigInteger(1, yBytes)), SPEC));
            } catch (InvalidKeySpecException e) {
                throw new DnssecInvalidKeySpecException(e);
            }
        } catch (IOException e2) {
            throw new DataMalformedException(e2, key.getKey());
        }
    }

    private static void reverse(byte[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            int j = (array.length - i) - 1;
            byte tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }
}
