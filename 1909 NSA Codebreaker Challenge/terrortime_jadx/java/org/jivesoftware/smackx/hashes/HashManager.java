package org.jivesoftware.smackx.hashes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.hashes.element.HashElement;

public final class HashManager extends Manager {
    private static final WeakHashMap<XMPPConnection, HashManager> INSTANCES = new WeakHashMap<>();
    public static final String PREFIX_NS_ALGO = "urn:xmpp:hash-function-text-names:";
    public static final String PROVIDER = "BC";
    public static final List<ALGORITHM> RECOMMENDED = Collections.unmodifiableList(Arrays.asList(new ALGORITHM[]{ALGORITHM.SHA_256, ALGORITHM.SHA_384, ALGORITHM.SHA_512, ALGORITHM.SHA3_256, ALGORITHM.SHA3_384, ALGORITHM.SHA3_512, ALGORITHM.BLAKE2B256, ALGORITHM.BLAKE2B384, ALGORITHM.BLAKE2B512}));

    public enum ALGORITHM {
        MD5("md5", AlgorithmRecommendation.must_not),
        SHA_1("sha-1", AlgorithmRecommendation.should_not),
        SHA_224("sha-224", AlgorithmRecommendation.unknown),
        SHA_256("sha-256", AlgorithmRecommendation.must),
        SHA_384("sha-384", AlgorithmRecommendation.unknown),
        SHA_512("sha-512", AlgorithmRecommendation.should),
        SHA3_224("sha3-224", AlgorithmRecommendation.unknown),
        SHA3_256("sha3-256", AlgorithmRecommendation.must),
        SHA3_384("sha3-384", AlgorithmRecommendation.unknown),
        SHA3_512("sha3-512", AlgorithmRecommendation.should),
        BLAKE2B160("id-blake2b160", AlgorithmRecommendation.unknown),
        BLAKE2B256("id-blake2b256", AlgorithmRecommendation.must),
        BLAKE2B384("id-blake2b384", AlgorithmRecommendation.unknown),
        BLAKE2B512("id-blake2b512", AlgorithmRecommendation.should);
        
        private final String name;
        private final AlgorithmRecommendation recommendation;

        private ALGORITHM(String name2, AlgorithmRecommendation recommendation2) {
            this.name = name2;
            this.recommendation = recommendation2;
        }

        public String toString() {
            return this.name;
        }

        public AlgorithmRecommendation getRecommendation() {
            return this.recommendation;
        }

        public static ALGORITHM valueOfName(String s) {
            ALGORITHM[] values;
            for (ALGORITHM a : values()) {
                if (a.toString().equals(s)) {
                    return a;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("No ALGORITHM enum with this name (");
            sb.append(s);
            sb.append(") found.");
            throw new IllegalArgumentException(sb.toString());
        }
    }

    enum AlgorithmRecommendation {
        unknown,
        must_not,
        should_not,
        should,
        must
    }

    public enum NAMESPACE {
        V1("urn:xmpp:hashes:1"),
        V2("urn:xmpp:hashes:2");
        
        final String name;

        private NAMESPACE(String name2) {
            this.name = name2;
        }

        public String toString() {
            return this.name;
        }
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private HashManager(XMPPConnection connection) {
        super(connection);
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature(NAMESPACE.V2.toString());
        addAlgorithmsToFeatures(RECOMMENDED);
    }

    public static HashElement calculateHashElement(ALGORITHM algorithm, byte[] data) {
        return new HashElement(algorithm, hash(algorithm, data));
    }

    public static HashElement assembleHashElement(ALGORITHM algorithm, byte[] hash) {
        return new HashElement(algorithm, hash);
    }

    public void addAlgorithmsToFeatures(List<ALGORITHM> algorithms) {
        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection());
        for (ALGORITHM algo : algorithms) {
            sdm.addFeature(asFeature(algo));
        }
    }

    public static synchronized HashManager getInstanceFor(XMPPConnection connection) {
        HashManager hashManager;
        synchronized (HashManager.class) {
            hashManager = (HashManager) INSTANCES.get(connection);
            if (hashManager == null) {
                hashManager = new HashManager(connection);
                INSTANCES.put(connection, hashManager);
            }
        }
        return hashManager;
    }

    public static String asFeature(ALGORITHM algorithm) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NS_ALGO);
        sb.append(algorithm.toString());
        return sb.toString();
    }

    public static byte[] hash(ALGORITHM algorithm, byte[] data) {
        return getMessageDigest(algorithm).digest(data);
    }

    public static byte[] hash(ALGORITHM algorithm, String data) {
        return hash(algorithm, StringUtils.toUtf8Bytes(data));
    }

    public static MessageDigest getMessageDigest(ALGORITHM algorithm) {
        try {
            String str = "BC";
            switch (algorithm) {
                case MD5:
                    return MessageDigest.getInstance(StringUtils.MD5, str);
                case SHA_1:
                    return MessageDigest.getInstance("SHA-1", str);
                case SHA_224:
                    return MessageDigest.getInstance(McElieceCCA2KeyGenParameterSpec.SHA224, str);
                case SHA_256:
                    return MessageDigest.getInstance("SHA-256", str);
                case SHA_384:
                    return MessageDigest.getInstance(McElieceCCA2KeyGenParameterSpec.SHA384, str);
                case SHA_512:
                    return MessageDigest.getInstance("SHA-512", str);
                case SHA3_224:
                    return MessageDigest.getInstance("SHA3-224", str);
                case SHA3_256:
                    return MessageDigest.getInstance("SHA3-256", str);
                case SHA3_384:
                    return MessageDigest.getInstance("SHA3-384", str);
                case SHA3_512:
                    return MessageDigest.getInstance("SHA3-512", str);
                case BLAKE2B160:
                    return MessageDigest.getInstance("BLAKE2b-160", str);
                case BLAKE2B256:
                    return MessageDigest.getInstance("BLAKE2b-256", str);
                case BLAKE2B384:
                    return MessageDigest.getInstance("BLAKE2b-384", str);
                case BLAKE2B512:
                    return MessageDigest.getInstance("BLAKE2b-512", str);
                default:
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invalid enum value: ");
                    sb.append(algorithm);
                    throw new AssertionError(sb.toString());
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new AssertionError(e);
        }
        throw new AssertionError(e);
    }

    public static byte[] md5(byte[] data) {
        return getMessageDigest(ALGORITHM.MD5).digest(data);
    }

    public static byte[] md5(String data) {
        return md5(StringUtils.toUtf8Bytes(data));
    }

    public static String md5HexString(byte[] data) {
        return StringUtils.encodeHex(md5(data));
    }

    public static String md5HexString(String data) {
        return StringUtils.encodeHex(md5(data));
    }

    public static byte[] sha_1(byte[] data) {
        return getMessageDigest(ALGORITHM.SHA_1).digest(data);
    }

    public static byte[] sha_1(String data) {
        return sha_1(StringUtils.toUtf8Bytes(data));
    }

    public static String sha_1HexString(byte[] data) {
        return StringUtils.encodeHex(sha_1(data));
    }

    public static String sha_1HexString(String data) {
        return StringUtils.encodeHex(sha_1(data));
    }

    public static byte[] sha_224(byte[] data) {
        return getMessageDigest(ALGORITHM.SHA_224).digest(data);
    }

    public static byte[] sha_224(String data) {
        return sha_224(StringUtils.toUtf8Bytes(data));
    }

    public static String sha_224HexString(byte[] data) {
        return StringUtils.encodeHex(sha_224(data));
    }

    public static String sha_224HexString(String data) {
        return StringUtils.encodeHex(sha_224(data));
    }

    public static byte[] sha_256(byte[] data) {
        return getMessageDigest(ALGORITHM.SHA_256).digest(data);
    }

    public static byte[] sha_256(String data) {
        return sha_256(StringUtils.toUtf8Bytes(data));
    }

    public static String sha_256HexString(byte[] data) {
        return StringUtils.encodeHex(sha_256(data));
    }

    public static String sha_256HexString(String data) {
        return StringUtils.encodeHex(sha_256(data));
    }

    public static byte[] sha_384(byte[] data) {
        return getMessageDigest(ALGORITHM.SHA_384).digest(data);
    }

    public static byte[] sha_384(String data) {
        return sha_384(StringUtils.toUtf8Bytes(data));
    }

    public static String sha_384HexString(byte[] data) {
        return StringUtils.encodeHex(sha_384(data));
    }

    public static String sha_384HexString(String data) {
        return StringUtils.encodeHex(sha_384(data));
    }

    public static byte[] sha_512(byte[] data) {
        return getMessageDigest(ALGORITHM.SHA_512).digest(data);
    }

    public static byte[] sha_512(String data) {
        return sha_512(StringUtils.toUtf8Bytes(data));
    }

    public static String sha_512HexString(byte[] data) {
        return StringUtils.encodeHex(sha_512(data));
    }

    public static String sha_512HexString(String data) {
        return StringUtils.encodeHex(sha_512(data));
    }

    public static byte[] sha3_224(byte[] data) {
        return getMessageDigest(ALGORITHM.SHA3_224).digest(data);
    }

    public static byte[] sha3_224(String data) {
        return sha3_224(StringUtils.toUtf8Bytes(data));
    }

    public static String sha3_224HexString(byte[] data) {
        return StringUtils.encodeHex(sha3_224(data));
    }

    public static String sha3_224HexString(String data) {
        return StringUtils.encodeHex(sha3_224(data));
    }

    public static byte[] sha3_256(byte[] data) {
        return getMessageDigest(ALGORITHM.SHA3_256).digest(data);
    }

    public static byte[] sha3_256(String data) {
        return sha3_256(StringUtils.toUtf8Bytes(data));
    }

    public static String sha3_256HexString(byte[] data) {
        return StringUtils.encodeHex(sha3_256(data));
    }

    public static String sha3_256HexString(String data) {
        return StringUtils.encodeHex(sha3_256(data));
    }

    public static byte[] sha3_384(byte[] data) {
        return getMessageDigest(ALGORITHM.SHA3_384).digest(data);
    }

    public static byte[] sha3_384(String data) {
        return sha3_384(StringUtils.toUtf8Bytes(data));
    }

    public static String sha3_384HexString(byte[] data) {
        return StringUtils.encodeHex(sha3_384(data));
    }

    public static String sha3_384HexString(String data) {
        return StringUtils.encodeHex(sha3_384(data));
    }

    public static byte[] sha3_512(byte[] data) {
        return getMessageDigest(ALGORITHM.SHA3_512).digest(data);
    }

    public static byte[] sha3_512(String data) {
        return sha3_512(StringUtils.toUtf8Bytes(data));
    }

    public static String sha3_512HexString(byte[] data) {
        return StringUtils.encodeHex(sha3_512(data));
    }

    public static String sha3_512HexString(String data) {
        return StringUtils.encodeHex(sha3_512(data));
    }

    public static byte[] blake2b160(byte[] data) {
        return getMessageDigest(ALGORITHM.BLAKE2B160).digest(data);
    }

    public static byte[] blake2b160(String data) {
        return blake2b160(StringUtils.toUtf8Bytes(data));
    }

    public static String blake2b160HexString(byte[] data) {
        return StringUtils.encodeHex(blake2b160(data));
    }

    public static String blake2b160HexString(String data) {
        return StringUtils.encodeHex(blake2b160(data));
    }

    public static byte[] blake2b256(byte[] data) {
        return getMessageDigest(ALGORITHM.BLAKE2B256).digest(data);
    }

    public static byte[] blake2b256(String data) {
        return blake2b256(StringUtils.toUtf8Bytes(data));
    }

    public static String blake2b256HexString(byte[] data) {
        return StringUtils.encodeHex(blake2b256(data));
    }

    public static String blake2b256HexString(String data) {
        return StringUtils.encodeHex(blake2b256(data));
    }

    public static byte[] blake2b384(byte[] data) {
        return getMessageDigest(ALGORITHM.BLAKE2B384).digest(data);
    }

    public static byte[] blake2b384(String data) {
        return blake2b384(StringUtils.toUtf8Bytes(data));
    }

    public static String blake2b384HexString(byte[] data) {
        return StringUtils.encodeHex(blake2b384(data));
    }

    public static String blake2b384HexString(String data) {
        return StringUtils.encodeHex(blake2b384(data));
    }

    public static byte[] blake2b512(byte[] data) {
        return getMessageDigest(ALGORITHM.BLAKE2B512).digest(data);
    }

    public static byte[] blake2b512(String data) {
        return blake2b512(StringUtils.toUtf8Bytes(data));
    }

    public static String blake2b512HexString(byte[] data) {
        return StringUtils.encodeHex(blake2b512(data));
    }

    public static String blake2b512HexString(String data) {
        return StringUtils.encodeHex(blake2b512(data));
    }
}
