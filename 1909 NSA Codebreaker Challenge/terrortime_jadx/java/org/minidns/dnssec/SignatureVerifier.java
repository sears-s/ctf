package org.minidns.dnssec;

import org.minidns.record.DNSKEY;
import org.minidns.record.RRSIG;

public interface SignatureVerifier {
    boolean verify(byte[] bArr, RRSIG rrsig, DNSKEY dnskey) throws DnssecValidationFailedException;
}
