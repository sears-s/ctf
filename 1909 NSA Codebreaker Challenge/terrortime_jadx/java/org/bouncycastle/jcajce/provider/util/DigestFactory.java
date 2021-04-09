package org.bouncycastle.jcajce.provider.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;
import org.bouncycastle.util.Strings;
import org.jivesoftware.smack.util.StringUtils;

public class DigestFactory {
    private static Set md5 = new HashSet();
    private static Map oids = new HashMap();
    private static Set sha1 = new HashSet();
    private static Set sha224 = new HashSet();
    private static Set sha256 = new HashSet();
    private static Set sha384 = new HashSet();
    private static Set sha3_224 = new HashSet();
    private static Set sha3_256 = new HashSet();
    private static Set sha3_384 = new HashSet();
    private static Set sha3_512 = new HashSet();
    private static Set sha512 = new HashSet();
    private static Set sha512_224 = new HashSet();
    private static Set sha512_256 = new HashSet();

    static {
        Set set = md5;
        String str = StringUtils.MD5;
        set.add(str);
        md5.add(PKCSObjectIdentifiers.md5.getId());
        String str2 = "SHA1";
        sha1.add(str2);
        String str3 = "SHA-1";
        sha1.add(str3);
        sha1.add(OIWObjectIdentifiers.idSHA1.getId());
        String str4 = "SHA224";
        sha224.add(str4);
        Set set2 = sha224;
        String str5 = McElieceCCA2KeyGenParameterSpec.SHA224;
        set2.add(str5);
        sha224.add(NISTObjectIdentifiers.id_sha224.getId());
        String str6 = "SHA256";
        sha256.add(str6);
        String str7 = "SHA-256";
        sha256.add(str7);
        sha256.add(NISTObjectIdentifiers.id_sha256.getId());
        String str8 = "SHA384";
        sha384.add(str8);
        Set set3 = sha384;
        String str9 = McElieceCCA2KeyGenParameterSpec.SHA384;
        set3.add(str9);
        sha384.add(NISTObjectIdentifiers.id_sha384.getId());
        String str10 = "SHA512";
        sha512.add(str10);
        String str11 = "SHA-512";
        sha512.add(str11);
        sha512.add(NISTObjectIdentifiers.id_sha512.getId());
        String str12 = "SHA512(224)";
        sha512_224.add(str12);
        String str13 = "SHA-512(224)";
        sha512_224.add(str13);
        sha512_224.add(NISTObjectIdentifiers.id_sha512_224.getId());
        String str14 = "SHA512(256)";
        sha512_256.add(str14);
        String str15 = "SHA-512(256)";
        sha512_256.add(str15);
        String str16 = str15;
        sha512_256.add(NISTObjectIdentifiers.id_sha512_256.getId());
        sha3_224.add("SHA3-224");
        sha3_224.add(NISTObjectIdentifiers.id_sha3_224.getId());
        sha3_256.add("SHA3-256");
        sha3_256.add(NISTObjectIdentifiers.id_sha3_256.getId());
        sha3_384.add("SHA3-384");
        sha3_384.add(NISTObjectIdentifiers.id_sha3_384.getId());
        sha3_512.add("SHA3-512");
        sha3_512.add(NISTObjectIdentifiers.id_sha3_512.getId());
        oids.put(str, PKCSObjectIdentifiers.md5);
        oids.put(PKCSObjectIdentifiers.md5.getId(), PKCSObjectIdentifiers.md5);
        oids.put(str2, OIWObjectIdentifiers.idSHA1);
        oids.put(str3, OIWObjectIdentifiers.idSHA1);
        oids.put(OIWObjectIdentifiers.idSHA1.getId(), OIWObjectIdentifiers.idSHA1);
        oids.put(str4, NISTObjectIdentifiers.id_sha224);
        oids.put(str5, NISTObjectIdentifiers.id_sha224);
        oids.put(NISTObjectIdentifiers.id_sha224.getId(), NISTObjectIdentifiers.id_sha224);
        oids.put(str6, NISTObjectIdentifiers.id_sha256);
        oids.put(str7, NISTObjectIdentifiers.id_sha256);
        oids.put(NISTObjectIdentifiers.id_sha256.getId(), NISTObjectIdentifiers.id_sha256);
        oids.put(str8, NISTObjectIdentifiers.id_sha384);
        oids.put(str9, NISTObjectIdentifiers.id_sha384);
        oids.put(NISTObjectIdentifiers.id_sha384.getId(), NISTObjectIdentifiers.id_sha384);
        oids.put(str10, NISTObjectIdentifiers.id_sha512);
        oids.put(str11, NISTObjectIdentifiers.id_sha512);
        oids.put(NISTObjectIdentifiers.id_sha512.getId(), NISTObjectIdentifiers.id_sha512);
        oids.put(str12, NISTObjectIdentifiers.id_sha512_224);
        oids.put(str13, NISTObjectIdentifiers.id_sha512_224);
        oids.put(NISTObjectIdentifiers.id_sha512_224.getId(), NISTObjectIdentifiers.id_sha512_224);
        oids.put(str14, NISTObjectIdentifiers.id_sha512_256);
        oids.put(str16, NISTObjectIdentifiers.id_sha512_256);
        oids.put(NISTObjectIdentifiers.id_sha512_256.getId(), NISTObjectIdentifiers.id_sha512_256);
        oids.put("SHA3-224", NISTObjectIdentifiers.id_sha3_224);
        oids.put(NISTObjectIdentifiers.id_sha3_224.getId(), NISTObjectIdentifiers.id_sha3_224);
        oids.put("SHA3-256", NISTObjectIdentifiers.id_sha3_256);
        oids.put(NISTObjectIdentifiers.id_sha3_256.getId(), NISTObjectIdentifiers.id_sha3_256);
        oids.put("SHA3-384", NISTObjectIdentifiers.id_sha3_384);
        oids.put(NISTObjectIdentifiers.id_sha3_384.getId(), NISTObjectIdentifiers.id_sha3_384);
        oids.put("SHA3-512", NISTObjectIdentifiers.id_sha3_512);
        oids.put(NISTObjectIdentifiers.id_sha3_512.getId(), NISTObjectIdentifiers.id_sha3_512);
    }

    public static Digest getDigest(String str) {
        String upperCase = Strings.toUpperCase(str);
        if (sha1.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA1();
        }
        if (md5.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createMD5();
        }
        if (sha224.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA224();
        }
        if (sha256.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA256();
        }
        if (sha384.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA384();
        }
        if (sha512.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA512();
        }
        if (sha512_224.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA512_224();
        }
        if (sha512_256.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA512_256();
        }
        if (sha3_224.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA3_224();
        }
        if (sha3_256.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA3_256();
        }
        if (sha3_384.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA3_384();
        }
        if (sha3_512.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA3_512();
        }
        return null;
    }

    public static ASN1ObjectIdentifier getOID(String str) {
        return (ASN1ObjectIdentifier) oids.get(str);
    }

    public static boolean isSameDigest(String str, String str2) {
        return (sha1.contains(str) && sha1.contains(str2)) || (sha224.contains(str) && sha224.contains(str2)) || ((sha256.contains(str) && sha256.contains(str2)) || ((sha384.contains(str) && sha384.contains(str2)) || ((sha512.contains(str) && sha512.contains(str2)) || ((sha512_224.contains(str) && sha512_224.contains(str2)) || ((sha512_256.contains(str) && sha512_256.contains(str2)) || ((sha3_224.contains(str) && sha3_224.contains(str2)) || ((sha3_256.contains(str) && sha3_256.contains(str2)) || ((sha3_384.contains(str) && sha3_384.contains(str2)) || ((sha3_512.contains(str) && sha3_512.contains(str2)) || (md5.contains(str) && md5.contains(str2)))))))))));
    }
}
