package org.jxmpp.stringprep;

import org.jxmpp.stringprep.simple.SimpleXmppStringprep;
import org.jxmpp.util.cache.Cache;
import org.jxmpp.util.cache.LruCache;

public class XmppStringPrepUtil {
    private static final Cache<String, String> DOMAINPREP_CACHE = new LruCache(100);
    private static final Cache<String, String> NODEPREP_CACHE = new LruCache(100);
    private static final Cache<String, String> RESOURCEPREP_CACHE = new LruCache(100);
    private static XmppStringprep xmppStringprep;

    static {
        SimpleXmppStringprep.setup();
    }

    public static void setXmppStringprep(XmppStringprep xmppStringprep2) {
        xmppStringprep = xmppStringprep2;
    }

    public static String localprep(String string) throws XmppStringprepException {
        if (xmppStringprep == null) {
            return string;
        }
        throwIfEmptyString(string);
        String res = (String) NODEPREP_CACHE.lookup(string);
        if (res != null) {
            return res;
        }
        String res2 = xmppStringprep.localprep(string);
        NODEPREP_CACHE.put(string, res2);
        return res2;
    }

    public static String domainprep(String string) throws XmppStringprepException {
        if (xmppStringprep == null) {
            return string;
        }
        throwIfEmptyString(string);
        String res = (String) DOMAINPREP_CACHE.lookup(string);
        if (res != null) {
            return res;
        }
        String res2 = xmppStringprep.domainprep(string);
        DOMAINPREP_CACHE.put(string, res2);
        return res2;
    }

    public static String resourceprep(String string) throws XmppStringprepException {
        if (xmppStringprep == null) {
            return string;
        }
        throwIfEmptyString(string);
        String res = (String) RESOURCEPREP_CACHE.lookup(string);
        if (res != null) {
            return res;
        }
        String res2 = xmppStringprep.resourceprep(string);
        RESOURCEPREP_CACHE.put(string, res2);
        return res2;
    }

    public static void setMaxCacheSizes(int size) {
        NODEPREP_CACHE.setMaxCacheSize(size);
        DOMAINPREP_CACHE.setMaxCacheSize(size);
        RESOURCEPREP_CACHE.setMaxCacheSize(size);
    }

    private static void throwIfEmptyString(String string) throws XmppStringprepException {
        if (string.length() == 0) {
            throw new XmppStringprepException(string, "Argument can't be the empty string");
        }
    }
}
