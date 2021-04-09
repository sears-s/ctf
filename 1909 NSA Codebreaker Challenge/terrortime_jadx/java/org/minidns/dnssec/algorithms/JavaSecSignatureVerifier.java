package org.minidns.dnssec.algorithms;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import org.minidns.dnssec.DnssecValidationFailedException;
import org.minidns.dnssec.DnssecValidationFailedException.DataMalformedException;
import org.minidns.dnssec.DnssecValidationFailedException.DnssecInvalidKeySpecException;
import org.minidns.dnssec.SignatureVerifier;
import org.minidns.record.DNSKEY;
import org.minidns.record.RRSIG;

public abstract class JavaSecSignatureVerifier implements SignatureVerifier {
    private final KeyFactory keyFactory;
    private final String signatureAlgorithm;

    /* access modifiers changed from: protected */
    public abstract PublicKey getPublicKey(DNSKEY dnskey) throws DataMalformedException, DnssecInvalidKeySpecException;

    /* access modifiers changed from: protected */
    public abstract byte[] getSignature(RRSIG rrsig) throws DataMalformedException;

    public JavaSecSignatureVerifier(String keyAlgorithm, String signatureAlgorithm2) throws NoSuchAlgorithmException {
        this.keyFactory = KeyFactory.getInstance(keyAlgorithm);
        this.signatureAlgorithm = signatureAlgorithm2;
        Signature.getInstance(signatureAlgorithm2);
    }

    public KeyFactory getKeyFactory() {
        return this.keyFactory;
    }

    public boolean verify(byte[] content, RRSIG rrsig, DNSKEY key) throws DnssecValidationFailedException {
        try {
            PublicKey publicKey = getPublicKey(key);
            Signature signature = Signature.getInstance(this.signatureAlgorithm);
            signature.initVerify(publicKey);
            signature.update(content);
            return signature.verify(getSignature(rrsig));
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        } catch (ArithmeticException | InvalidKeyException | SignatureException e2) {
            throw new DnssecValidationFailedException("Validating signature failed", (Throwable) e2);
        }
    }
}
