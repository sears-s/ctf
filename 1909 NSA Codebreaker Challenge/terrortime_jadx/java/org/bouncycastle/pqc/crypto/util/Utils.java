package org.bouncycastle.pqc.crypto.util;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSKeyParameters;
import org.bouncycastle.util.Integers;

class Utils {
    static final AlgorithmIdentifier AlgID_qTESLA_I = new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_I);
    static final AlgorithmIdentifier AlgID_qTESLA_III_size = new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_III_size);
    static final AlgorithmIdentifier AlgID_qTESLA_III_speed = new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_III_speed);
    static final AlgorithmIdentifier AlgID_qTESLA_p_I = new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_p_I);
    static final AlgorithmIdentifier AlgID_qTESLA_p_III = new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_p_III);
    static final AlgorithmIdentifier SPHINCS_SHA3_256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha3_256);
    static final AlgorithmIdentifier SPHINCS_SHA512_256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256);
    static final AlgorithmIdentifier XMSS_SHA256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
    static final AlgorithmIdentifier XMSS_SHA512 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512);
    static final AlgorithmIdentifier XMSS_SHAKE128 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_shake128);
    static final AlgorithmIdentifier XMSS_SHAKE256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_shake256);
    static final Map categories = new HashMap();

    static {
        categories.put(PQCObjectIdentifiers.qTESLA_I, Integers.valueOf(0));
        categories.put(PQCObjectIdentifiers.qTESLA_III_size, Integers.valueOf(1));
        categories.put(PQCObjectIdentifiers.qTESLA_III_speed, Integers.valueOf(2));
        categories.put(PQCObjectIdentifiers.qTESLA_p_I, Integers.valueOf(3));
        categories.put(PQCObjectIdentifiers.qTESLA_p_III, Integers.valueOf(4));
    }

    Utils() {
    }

    static Digest getDigest(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha256)) {
            return new SHA256Digest();
        }
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha512)) {
            return new SHA512Digest();
        }
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_shake128)) {
            return new SHAKEDigest(128);
        }
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_shake256)) {
            return new SHAKEDigest(256);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("unrecognized digest OID: ");
        sb.append(aSN1ObjectIdentifier);
        throw new IllegalArgumentException(sb.toString());
    }

    static AlgorithmIdentifier qTeslaLookupAlgID(int i) {
        if (i == 0) {
            return AlgID_qTESLA_I;
        }
        if (i == 1) {
            return AlgID_qTESLA_III_size;
        }
        if (i == 2) {
            return AlgID_qTESLA_III_speed;
        }
        if (i == 3) {
            return AlgID_qTESLA_p_I;
        }
        if (i == 4) {
            return AlgID_qTESLA_p_III;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("unknown security category: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }

    static int qTeslaLookupSecurityCategory(AlgorithmIdentifier algorithmIdentifier) {
        return ((Integer) categories.get(algorithmIdentifier.getAlgorithm())).intValue();
    }

    static AlgorithmIdentifier sphincs256LookupTreeAlgID(String str) {
        if (str.equals("SHA3-256")) {
            return SPHINCS_SHA3_256;
        }
        if (str.equals(SPHINCSKeyParameters.SHA512_256)) {
            return SPHINCS_SHA512_256;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("unknown tree digest: ");
        sb.append(str);
        throw new IllegalArgumentException(sb.toString());
    }

    static String sphincs256LookupTreeAlgName(SPHINCS256KeyParams sPHINCS256KeyParams) {
        AlgorithmIdentifier treeDigest = sPHINCS256KeyParams.getTreeDigest();
        if (treeDigest.getAlgorithm().equals(SPHINCS_SHA3_256.getAlgorithm())) {
            return "SHA3-256";
        }
        if (treeDigest.getAlgorithm().equals(SPHINCS_SHA512_256.getAlgorithm())) {
            return SPHINCSKeyParameters.SHA512_256;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("unknown tree digest: ");
        sb.append(treeDigest.getAlgorithm());
        throw new IllegalArgumentException(sb.toString());
    }

    static AlgorithmIdentifier xmssLookupTreeAlgID(String str) {
        if (str.equals("SHA-256")) {
            return XMSS_SHA256;
        }
        if (str.equals("SHA-512")) {
            return XMSS_SHA512;
        }
        if (str.equals("SHAKE128")) {
            return XMSS_SHAKE128;
        }
        if (str.equals("SHAKE256")) {
            return XMSS_SHAKE256;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("unknown tree digest: ");
        sb.append(str);
        throw new IllegalArgumentException(sb.toString());
    }
}
