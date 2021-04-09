package org.jxmpp.jid.parts;

import org.jxmpp.stringprep.XmppStringPrepUtil;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppStringUtils;

public class Localpart extends Part {
    private static final long serialVersionUID = 1;
    private transient String unescapedCache;

    private Localpart(String localpart) {
        super(localpart);
    }

    public String asUnescapedString() {
        String str = this.unescapedCache;
        if (str != null) {
            return str;
        }
        this.unescapedCache = XmppStringUtils.unescapeLocalpart(toString());
        return this.unescapedCache;
    }

    public static Localpart fromOrThrowUnchecked(CharSequence cs) {
        try {
            return from(cs.toString());
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Localpart fromUnescapedOrThrowUnchecked(CharSequence cs) {
        try {
            return fromUnescaped(cs.toString());
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Localpart formUnescapedOrNull(CharSequence cs) {
        try {
            return fromUnescaped(cs);
        } catch (XmppStringprepException e) {
            return null;
        }
    }

    public static Localpart fromUnescaped(String unescapedLocalpart) throws XmppStringprepException {
        return from(XmppStringUtils.escapeLocalpart(unescapedLocalpart));
    }

    public static Localpart fromUnescaped(CharSequence unescapedLocalpart) throws XmppStringprepException {
        return fromUnescaped(unescapedLocalpart.toString());
    }

    public static Localpart fromOrNull(CharSequence cs) {
        try {
            return from(cs.toString());
        } catch (XmppStringprepException e) {
            return null;
        }
    }

    public static Localpart from(String localpart) throws XmppStringprepException {
        String localpart2 = XmppStringPrepUtil.localprep(localpart);
        assertNotLongerThan1023BytesOrEmpty(localpart2);
        return new Localpart(localpart2);
    }
}
