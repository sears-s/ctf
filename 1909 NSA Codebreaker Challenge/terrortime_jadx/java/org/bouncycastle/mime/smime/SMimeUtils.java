package org.bouncycastle.mime.smime;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.util.Strings;

class SMimeUtils {
    private static final Map RFC3851_MICALGS;
    private static final Map RFC5751_MICALGS;
    private static final Map STANDARD_MICALGS = RFC5751_MICALGS;
    private static final Map forMic;
    private static final byte[] nl = new byte[2];

    static {
        byte[] bArr = nl;
        bArr[0] = 13;
        bArr[1] = 10;
        HashMap hashMap = new HashMap();
        String str = "md5";
        hashMap.put(CMSAlgorithm.MD5, str);
        hashMap.put(CMSAlgorithm.SHA1, "sha-1");
        hashMap.put(CMSAlgorithm.SHA224, "sha-224");
        hashMap.put(CMSAlgorithm.SHA256, "sha-256");
        hashMap.put(CMSAlgorithm.SHA384, "sha-384");
        hashMap.put(CMSAlgorithm.SHA512, "sha-512");
        String str2 = "gostr3411-94";
        hashMap.put(CMSAlgorithm.GOST3411, str2);
        String str3 = "gostr3411-2012-256";
        hashMap.put(CMSAlgorithm.GOST3411_2012_256, str3);
        String str4 = "gostr3411-2012-512";
        hashMap.put(CMSAlgorithm.GOST3411_2012_512, str4);
        RFC5751_MICALGS = Collections.unmodifiableMap(hashMap);
        HashMap hashMap2 = new HashMap();
        hashMap2.put(CMSAlgorithm.MD5, str);
        hashMap2.put(CMSAlgorithm.SHA1, "sha1");
        hashMap2.put(CMSAlgorithm.SHA224, "sha224");
        hashMap2.put(CMSAlgorithm.SHA256, "sha256");
        hashMap2.put(CMSAlgorithm.SHA384, "sha384");
        hashMap2.put(CMSAlgorithm.SHA512, "sha512");
        hashMap2.put(CMSAlgorithm.GOST3411, str2);
        hashMap2.put(CMSAlgorithm.GOST3411_2012_256, str3);
        hashMap2.put(CMSAlgorithm.GOST3411_2012_512, str4);
        RFC3851_MICALGS = Collections.unmodifiableMap(hashMap2);
        TreeMap treeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        for (Object next : STANDARD_MICALGS.keySet()) {
            treeMap.put(STANDARD_MICALGS.get(next).toString(), (ASN1ObjectIdentifier) next);
        }
        for (Object next2 : RFC3851_MICALGS.keySet()) {
            treeMap.put(RFC3851_MICALGS.get(next2).toString(), (ASN1ObjectIdentifier) next2);
        }
        forMic = Collections.unmodifiableMap(treeMap);
    }

    SMimeUtils() {
    }

    static OutputStream createUnclosable(OutputStream outputStream) {
        return new FilterOutputStream(outputStream) {
            public void close() throws IOException {
            }
        };
    }

    static ASN1ObjectIdentifier getDigestOID(String str) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier) forMic.get(Strings.toLowerCase(str));
        if (aSN1ObjectIdentifier != null) {
            return aSN1ObjectIdentifier;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("unknown micalg passed: ");
        sb.append(str);
        throw new IllegalArgumentException(sb.toString());
    }

    static String getParameter(String str, List<String> list) {
        for (String str2 : list) {
            if (str2.startsWith(str)) {
                return str2;
            }
        }
        return null;
    }

    static String lessQuotes(String str) {
        return (str == null || str.length() <= 1 || str.charAt(0) != '\"' || str.charAt(str.length() - 1) != '\"') ? str : str.substring(1, str.length() - 1);
    }
}
