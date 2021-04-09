package org.minidns.dnssec.algorithms;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.minidns.constants.DnssecConstants.DigestAlgorithm;
import org.minidns.constants.DnssecConstants.SignatureAlgorithm;
import org.minidns.dnssec.DigestCalculator;
import org.minidns.dnssec.DnssecValidatorInitializationException;
import org.minidns.dnssec.SignatureVerifier;
import org.minidns.dnssec.algorithms.EcdsaSignatureVerifier.P256SHA256;
import org.minidns.dnssec.algorithms.EcdsaSignatureVerifier.P384SHA284;
import org.minidns.record.NSEC3.HashAlgorithm;

public class AlgorithmMap {
    public static final AlgorithmMap INSTANCE = new AlgorithmMap();
    private Logger LOGGER = Logger.getLogger(AlgorithmMap.class.getName());
    private final Map<DigestAlgorithm, DigestCalculator> dsDigestMap = new HashMap();
    private final Map<HashAlgorithm, DigestCalculator> nsecDigestMap = new HashMap();
    private final Map<SignatureAlgorithm, SignatureVerifier> signatureMap = new HashMap();

    private AlgorithmMap() {
        String str = "SHA-1";
        try {
            this.dsDigestMap.put(DigestAlgorithm.SHA1, new JavaSecDigestCalculator(str));
            this.nsecDigestMap.put(HashAlgorithm.SHA1, new JavaSecDigestCalculator(str));
            try {
                this.dsDigestMap.put(DigestAlgorithm.SHA256, new JavaSecDigestCalculator("SHA-256"));
                try {
                    this.signatureMap.put(SignatureAlgorithm.RSAMD5, new RsaSignatureVerifier("MD5withRSA"));
                } catch (NoSuchAlgorithmException e) {
                    this.LOGGER.log(Level.FINER, "Platform does not support RSA/MD5", e);
                }
                try {
                    DsaSignatureVerifier sha1withDSA = new DsaSignatureVerifier("SHA1withDSA");
                    this.signatureMap.put(SignatureAlgorithm.DSA, sha1withDSA);
                    this.signatureMap.put(SignatureAlgorithm.DSA_NSEC3_SHA1, sha1withDSA);
                } catch (NoSuchAlgorithmException e2) {
                    this.LOGGER.log(Level.FINE, "Platform does not support DSA/SHA-1", e2);
                }
                try {
                    RsaSignatureVerifier sha1withRSA = new RsaSignatureVerifier("SHA1withRSA");
                    this.signatureMap.put(SignatureAlgorithm.RSASHA1, sha1withRSA);
                    this.signatureMap.put(SignatureAlgorithm.RSASHA1_NSEC3_SHA1, sha1withRSA);
                    try {
                        this.signatureMap.put(SignatureAlgorithm.RSASHA256, new RsaSignatureVerifier("SHA256withRSA"));
                    } catch (NoSuchAlgorithmException e3) {
                        this.LOGGER.log(Level.INFO, "Platform does not support RSA/SHA-256", e3);
                    }
                    try {
                        this.signatureMap.put(SignatureAlgorithm.RSASHA512, new RsaSignatureVerifier("SHA512withRSA"));
                    } catch (NoSuchAlgorithmException e4) {
                        this.LOGGER.log(Level.INFO, "Platform does not support RSA/SHA-512", e4);
                    }
                    try {
                        this.signatureMap.put(SignatureAlgorithm.ECC_GOST, new EcgostSignatureVerifier());
                    } catch (NoSuchAlgorithmException e5) {
                        this.LOGGER.log(Level.FINE, "Platform does not support GOST R 34.10-2001", e5);
                    }
                    try {
                        this.signatureMap.put(SignatureAlgorithm.ECDSAP256SHA256, new P256SHA256());
                    } catch (NoSuchAlgorithmException e6) {
                        this.LOGGER.log(Level.INFO, "Platform does not support ECDSA/SHA-256", e6);
                    }
                    try {
                        this.signatureMap.put(SignatureAlgorithm.ECDSAP384SHA384, new P384SHA284());
                    } catch (NoSuchAlgorithmException e7) {
                        this.LOGGER.log(Level.INFO, "Platform does not support ECDSA/SHA-384", e7);
                    }
                } catch (NoSuchAlgorithmException e8) {
                    throw new DnssecValidatorInitializationException("Platform does not support RSA/SHA-1", e8);
                }
            } catch (NoSuchAlgorithmException e9) {
                throw new DnssecValidatorInitializationException("SHA-256 is mandatory", e9);
            }
        } catch (NoSuchAlgorithmException e10) {
            throw new DnssecValidatorInitializationException("SHA-1 is mandatory", e10);
        }
    }

    public DigestCalculator getDsDigestCalculator(DigestAlgorithm algorithm) {
        return (DigestCalculator) this.dsDigestMap.get(algorithm);
    }

    public SignatureVerifier getSignatureVerifier(SignatureAlgorithm algorithm) {
        return (SignatureVerifier) this.signatureMap.get(algorithm);
    }

    public DigestCalculator getNsecDigestCalculator(HashAlgorithm algorithm) {
        return (DigestCalculator) this.nsecDigestMap.get(algorithm);
    }
}
