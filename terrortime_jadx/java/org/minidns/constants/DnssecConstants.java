package org.minidns.constants;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;

public final class DnssecConstants {
    /* access modifiers changed from: private */
    public static final Map<Byte, DigestAlgorithm> DELEGATION_DIGEST_LUT = new HashMap();
    /* access modifiers changed from: private */
    public static final Map<Byte, SignatureAlgorithm> SIGNATURE_ALGORITHM_LUT = new HashMap();

    public enum DigestAlgorithm {
        SHA1(1, "SHA-1"),
        SHA256(2, "SHA-256"),
        GOST(3, "GOST R 34.11-94"),
        SHA384(4, McElieceCCA2KeyGenParameterSpec.SHA384);
        
        public final String description;
        public final byte value;

        private DigestAlgorithm(int value2, String description2) {
            if (value2 < 0 || value2 > 255) {
                throw new IllegalArgumentException();
            }
            this.value = (byte) value2;
            this.description = description2;
            DnssecConstants.DELEGATION_DIGEST_LUT.put(Byte.valueOf(this.value), this);
        }

        public static DigestAlgorithm forByte(byte b) {
            return (DigestAlgorithm) DnssecConstants.DELEGATION_DIGEST_LUT.get(Byte.valueOf(b));
        }
    }

    public enum SignatureAlgorithm {
        RSAMD5(1, "RSA/MD5"),
        DH(2, "Diffie-Hellman"),
        DSA(3, "DSA/SHA1"),
        RSASHA1(5, "RSA/SHA-1"),
        DSA_NSEC3_SHA1(6, "DSA_NSEC3-SHA1"),
        RSASHA1_NSEC3_SHA1(7, "RSASHA1-NSEC3-SHA1"),
        RSASHA256(8, "RSA/SHA-256"),
        RSASHA512(10, "RSA/SHA-512"),
        ECC_GOST(12, "GOST R 34.10-2001"),
        ECDSAP256SHA256(13, "ECDSA Curve P-256 with SHA-256"),
        ECDSAP384SHA384(14, "ECDSA Curve P-384 with SHA-384"),
        INDIRECT(252, "Reserved for Indirect Keys"),
        PRIVATEDNS(253, "private algorithm"),
        PRIVATEOID(254, "private algorithm oid");
        
        public final String description;
        public final byte number;

        private SignatureAlgorithm(int number2, String description2) {
            if (number2 < 0 || number2 > 255) {
                throw new IllegalArgumentException();
            }
            this.number = (byte) number2;
            this.description = description2;
            DnssecConstants.SIGNATURE_ALGORITHM_LUT.put(Byte.valueOf(this.number), this);
        }

        public static SignatureAlgorithm forByte(byte b) {
            return (SignatureAlgorithm) DnssecConstants.SIGNATURE_ALGORITHM_LUT.get(Byte.valueOf(b));
        }
    }

    private DnssecConstants() {
    }
}
