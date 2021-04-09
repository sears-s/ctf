package org.minidns.dnssec.algorithms;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import org.minidns.dnssec.DnssecValidationFailedException.DataMalformedException;
import org.minidns.dnssec.DnssecValidationFailedException.DnssecInvalidKeySpecException;
import org.minidns.record.DNSKEY;
import org.minidns.record.RRSIG;

class DsaSignatureVerifier extends JavaSecSignatureVerifier {
    private static final int LENGTH = 20;

    public DsaSignatureVerifier(String algorithm) throws NoSuchAlgorithmException {
        super("DSA", algorithm);
    }

    /* access modifiers changed from: protected */
    public byte[] getSignature(RRSIG rrsig) throws DataMalformedException {
        DataInput dis = rrsig.getSignatureAsDataInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            byte readByte = dis.readByte();
            byte[] r = new byte[20];
            dis.readFully(r);
            int slen = 21;
            int rlen = r[0] < 0 ? 21 : 20;
            byte[] s = new byte[20];
            dis.readFully(s);
            if (s[0] >= 0) {
                slen = 20;
            }
            dos.writeByte(48);
            dos.writeByte(rlen + slen + 4);
            dos.writeByte(2);
            dos.writeByte(rlen);
            if (rlen > 20) {
                dos.writeByte(0);
            }
            dos.write(r);
            dos.writeByte(2);
            dos.writeByte(slen);
            if (slen > 20) {
                dos.writeByte(0);
            }
            dos.write(s);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new DataMalformedException(e, rrsig.getSignature());
        }
    }

    /* access modifiers changed from: protected */
    public PublicKey getPublicKey(DNSKEY key) throws DataMalformedException, DnssecInvalidKeySpecException {
        DataInput dis = key.getKeyAsDataInputStream();
        try {
            int t = dis.readUnsignedByte();
            byte[] subPrimeBytes = new byte[20];
            dis.readFully(subPrimeBytes);
            BigInteger subPrime = new BigInteger(1, subPrimeBytes);
            byte[] primeBytes = new byte[((t * 8) + 64)];
            dis.readFully(primeBytes);
            BigInteger prime = new BigInteger(1, primeBytes);
            byte[] baseBytes = new byte[((t * 8) + 64)];
            dis.readFully(baseBytes);
            BigInteger base = new BigInteger(1, baseBytes);
            byte[] pubKeyBytes = new byte[((t * 8) + 64)];
            dis.readFully(pubKeyBytes);
            try {
                return getKeyFactory().generatePublic(new DSAPublicKeySpec(new BigInteger(1, pubKeyBytes), prime, subPrime, base));
            } catch (InvalidKeySpecException e) {
                throw new DnssecInvalidKeySpecException(e);
            }
        } catch (IOException e2) {
            throw new DataMalformedException(e2, key.getKey());
        }
    }
}
