package org.bouncycastle.pqc.crypto.xmss;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DefaultXMSSOid implements XMSSOid {
    private static final Map<String, DefaultXMSSOid> oidLookupTable;
    private final int oid;
    private final String stringRepresentation;

    static {
        HashMap hashMap = new HashMap();
        String str = "SHA-256";
        hashMap.put(createKey(str, 32, 16, 67, 10), new DefaultXMSSOid(1, "XMSS_SHA2_10_256"));
        hashMap.put(createKey(str, 32, 16, 67, 16), new DefaultXMSSOid(2, "XMSS_SHA2_16_256"));
        hashMap.put(createKey(str, 32, 16, 67, 20), new DefaultXMSSOid(3, "XMSS_SHA2_20_256"));
        String str2 = "SHA-512";
        hashMap.put(createKey(str2, 64, 16, 131, 10), new DefaultXMSSOid(4, "XMSS_SHA2_10_512"));
        hashMap.put(createKey(str2, 64, 16, 131, 16), new DefaultXMSSOid(5, "XMSS_SHA2_16_512"));
        hashMap.put(createKey(str2, 64, 16, 131, 20), new DefaultXMSSOid(6, "XMSS_SHA2_20_512"));
        String str3 = "SHAKE128";
        hashMap.put(createKey(str3, 32, 16, 67, 10), new DefaultXMSSOid(7, "XMSS_SHAKE_10_256"));
        hashMap.put(createKey(str3, 32, 16, 67, 16), new DefaultXMSSOid(8, "XMSS_SHAKE_16_256"));
        hashMap.put(createKey(str3, 32, 16, 67, 20), new DefaultXMSSOid(9, "XMSS_SHAKE_20_256"));
        String str4 = "SHAKE256";
        hashMap.put(createKey(str4, 64, 16, 131, 10), new DefaultXMSSOid(10, "XMSS_SHAKE_10_512"));
        hashMap.put(createKey(str4, 64, 16, 131, 16), new DefaultXMSSOid(11, "XMSS_SHAKE_16_512"));
        hashMap.put(createKey(str4, 64, 16, 131, 20), new DefaultXMSSOid(12, "XMSS_SHAKE_20_512"));
        oidLookupTable = Collections.unmodifiableMap(hashMap);
    }

    private DefaultXMSSOid(int i, String str) {
        this.oid = i;
        this.stringRepresentation = str;
    }

    private static String createKey(String str, int i, int i2, int i3, int i4) {
        if (str != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            String str2 = "-";
            sb.append(str2);
            sb.append(i);
            sb.append(str2);
            sb.append(i2);
            sb.append(str2);
            sb.append(i3);
            sb.append(str2);
            sb.append(i4);
            return sb.toString();
        }
        throw new NullPointerException("algorithmName == null");
    }

    public static DefaultXMSSOid lookup(String str, int i, int i2, int i3, int i4) {
        if (str != null) {
            return (DefaultXMSSOid) oidLookupTable.get(createKey(str, i, i2, i3, i4));
        }
        throw new NullPointerException("algorithmName == null");
    }

    public int getOid() {
        return this.oid;
    }

    public String toString() {
        return this.stringRepresentation;
    }
}
