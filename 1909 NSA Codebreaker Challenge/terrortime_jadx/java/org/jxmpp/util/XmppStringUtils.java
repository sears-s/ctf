package org.jxmpp.util;

import com.badguy.terrortime.BuildConfig;
import org.jxmpp.util.cache.LruCache;

public class XmppStringUtils {
    private static final LruCache<String, String> LOCALPART_ESACPE_CACHE = new LruCache<>(100);
    private static final LruCache<String, String> LOCALPART_UNESCAPE_CACHE = new LruCache<>(100);

    public static String parseLocalpart(String jid) {
        if (jid == null) {
            return null;
        }
        int atIndex = jid.indexOf(64);
        String str = BuildConfig.FLAVOR;
        if (atIndex <= 0) {
            return str;
        }
        int slashIndex = jid.indexOf(47);
        if (slashIndex < 0 || slashIndex >= atIndex) {
            return jid.substring(0, atIndex);
        }
        return str;
    }

    public static String parseDomain(String jid) {
        if (jid == null) {
            return null;
        }
        int atIndex = jid.indexOf(64);
        int slashIndex = jid.indexOf(47);
        if (slashIndex <= 0) {
            return jid.substring(atIndex + 1);
        }
        if (slashIndex > atIndex) {
            return jid.substring(atIndex + 1, slashIndex);
        }
        return jid.substring(0, slashIndex);
    }

    public static String parseResource(String jid) {
        if (jid == null) {
            return null;
        }
        int slashIndex = jid.indexOf(47);
        if (slashIndex + 1 > jid.length() || slashIndex < 0) {
            return BuildConfig.FLAVOR;
        }
        return jid.substring(slashIndex + 1);
    }

    @Deprecated
    public static String parseBareAddress(String jid) {
        return parseBareJid(jid);
    }

    public static String parseBareJid(String jid) {
        int slashIndex = jid.indexOf(47);
        if (slashIndex < 0) {
            return jid;
        }
        if (slashIndex == 0) {
            return BuildConfig.FLAVOR;
        }
        return jid.substring(0, slashIndex);
    }

    public static boolean isFullJID(String jid) {
        if (parseLocalpart(jid).length() <= 0 || parseDomain(jid).length() <= 0 || parseResource(jid).length() <= 0) {
            return false;
        }
        return true;
    }

    public static boolean isBareJid(String jid) {
        return parseLocalpart(jid).length() > 0 && parseDomain(jid).length() > 0 && parseResource(jid).length() == 0;
    }

    public static String escapeLocalpart(String localpart) {
        if (localpart == null) {
            return null;
        }
        String res = (String) LOCALPART_ESACPE_CACHE.lookup(localpart);
        if (res != null) {
            return res;
        }
        StringBuilder buf = new StringBuilder(localpart.length() + 8);
        int n = localpart.length();
        for (int i = 0; i < n; i++) {
            char c = localpart.charAt(i);
            if (c == '\"') {
                buf.append("\\22");
            } else if (c == '/') {
                buf.append("\\2f");
            } else if (c == ':') {
                buf.append("\\3a");
            } else if (c == '<') {
                buf.append("\\3c");
            } else if (c == '>') {
                buf.append("\\3e");
            } else if (c == '@') {
                buf.append("\\40");
            } else if (c == '\\') {
                buf.append("\\5c");
            } else if (c == '&') {
                buf.append("\\26");
            } else if (c == '\'') {
                buf.append("\\27");
            } else if (Character.isWhitespace(c)) {
                buf.append("\\20");
            } else {
                buf.append(c);
            }
        }
        String res2 = buf.toString();
        LOCALPART_ESACPE_CACHE.put(localpart, res2);
        return res2;
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String unescapeLocalpart(java.lang.String r10) {
        /*
            if (r10 != 0) goto L_0x0004
            r0 = 0
            return r0
        L_0x0004:
            org.jxmpp.util.cache.LruCache<java.lang.String, java.lang.String> r0 = LOCALPART_UNESCAPE_CACHE
            java.lang.Object r0 = r0.lookup(r10)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x000f
            return r0
        L_0x000f:
            char[] r1 = r10.toCharArray()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            int r3 = r1.length
            r2.<init>(r3)
            r3 = 0
            int r4 = r1.length
        L_0x001b:
            if (r3 >= r4) goto L_0x00b5
            char r5 = r10.charAt(r3)
            r6 = 92
            if (r5 != r6) goto L_0x00ae
            int r6 = r3 + 2
            if (r6 >= r4) goto L_0x00ae
            int r6 = r3 + 1
            char r6 = r1[r6]
            int r7 = r3 + 2
            char r7 = r1[r7]
            r8 = 99
            r9 = 48
            switch(r6) {
                case 50: goto L_0x0073;
                case 51: goto L_0x0050;
                case 52: goto L_0x0045;
                case 53: goto L_0x003a;
                default: goto L_0x0038;
            }
        L_0x0038:
            goto L_0x00ae
        L_0x003a:
            if (r7 != r8) goto L_0x00ae
            java.lang.String r8 = "\\"
            r2.append(r8)
            int r3 = r3 + 2
            goto L_0x00b1
        L_0x0045:
            if (r7 != r9) goto L_0x00ae
            java.lang.String r8 = "@"
            r2.append(r8)
            int r3 = r3 + 2
            goto L_0x00b1
        L_0x0050:
            r9 = 97
            if (r7 == r9) goto L_0x006b
            if (r7 == r8) goto L_0x0063
            r8 = 101(0x65, float:1.42E-43)
            if (r7 == r8) goto L_0x005b
            goto L_0x00ae
        L_0x005b:
            r8 = 62
            r2.append(r8)
            int r3 = r3 + 2
            goto L_0x00b1
        L_0x0063:
            r8 = 60
            r2.append(r8)
            int r3 = r3 + 2
            goto L_0x00b1
        L_0x006b:
            r8 = 58
            r2.append(r8)
            int r3 = r3 + 2
            goto L_0x00b1
        L_0x0073:
            if (r7 == r9) goto L_0x00a6
            r8 = 50
            if (r7 == r8) goto L_0x009e
            r8 = 102(0x66, float:1.43E-43)
            if (r7 == r8) goto L_0x0096
            r8 = 54
            if (r7 == r8) goto L_0x008e
            r8 = 55
            if (r7 == r8) goto L_0x0086
            goto L_0x00ae
        L_0x0086:
            r8 = 39
            r2.append(r8)
            int r3 = r3 + 2
            goto L_0x00b1
        L_0x008e:
            r8 = 38
            r2.append(r8)
            int r3 = r3 + 2
            goto L_0x00b1
        L_0x0096:
            r8 = 47
            r2.append(r8)
            int r3 = r3 + 2
            goto L_0x00b1
        L_0x009e:
            r8 = 34
            r2.append(r8)
            int r3 = r3 + 2
            goto L_0x00b1
        L_0x00a6:
            r8 = 32
            r2.append(r8)
            int r3 = r3 + 2
            goto L_0x00b1
        L_0x00ae:
            r2.append(r5)
        L_0x00b1:
            int r3 = r3 + 1
            goto L_0x001b
        L_0x00b5:
            java.lang.String r0 = r2.toString()
            org.jxmpp.util.cache.LruCache<java.lang.String, java.lang.String> r3 = LOCALPART_UNESCAPE_CACHE
            r3.put(r10, r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jxmpp.util.XmppStringUtils.unescapeLocalpart(java.lang.String):java.lang.String");
    }

    public static String completeJidFrom(CharSequence localpart, CharSequence domainpart) {
        return completeJidFrom(localpart != null ? localpart.toString() : null, domainpart.toString());
    }

    public static String completeJidFrom(String localpart, String domainpart) {
        return completeJidFrom(localpart, domainpart, (String) null);
    }

    public static String completeJidFrom(CharSequence localpart, CharSequence domainpart, CharSequence resource) {
        String str = null;
        String charSequence = localpart != null ? localpart.toString() : null;
        String charSequence2 = domainpart.toString();
        if (resource != null) {
            str = resource.toString();
        }
        return completeJidFrom(charSequence, charSequence2, str);
    }

    public static String completeJidFrom(String localpart, String domainpart, String resource) {
        if (domainpart != null) {
            int resourceLength = 0;
            int localpartLength = localpart != null ? localpart.length() : 0;
            int domainpartLength = domainpart.length();
            if (resource != null) {
                resourceLength = resource.length();
            }
            StringBuilder sb = new StringBuilder(localpartLength + domainpartLength + resourceLength + 2);
            if (localpartLength > 0) {
                sb.append(localpart);
                sb.append('@');
            }
            sb.append(domainpart);
            if (resourceLength > 0) {
                sb.append('/');
                sb.append(resource);
            }
            return sb.toString();
        }
        throw new IllegalArgumentException("domainpart must not be null");
    }

    public static String generateKey(String element, String namespace) {
        StringBuilder sb = new StringBuilder();
        sb.append(element);
        sb.append(9);
        sb.append(namespace);
        return sb.toString();
    }
}
