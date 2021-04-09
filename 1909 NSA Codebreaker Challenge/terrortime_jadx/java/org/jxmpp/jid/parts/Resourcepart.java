package org.jxmpp.jid.parts;

import com.badguy.terrortime.BuildConfig;
import org.jxmpp.stringprep.XmppStringPrepUtil;
import org.jxmpp.stringprep.XmppStringprepException;

public class Resourcepart extends Part {
    public static final Resourcepart EMPTY = new Resourcepart(BuildConfig.FLAVOR);
    private static final long serialVersionUID = 1;

    private Resourcepart(String resource) {
        super(resource);
    }

    public static Resourcepart fromOrNull(CharSequence cs) {
        try {
            return from(cs.toString());
        } catch (XmppStringprepException e) {
            return null;
        }
    }

    public static Resourcepart fromOrThrowUnchecked(CharSequence cs) {
        try {
            return from(cs.toString());
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Resourcepart from(String resource) throws XmppStringprepException {
        String resource2 = XmppStringPrepUtil.resourceprep(resource);
        assertNotLongerThan1023BytesOrEmpty(resource2);
        return new Resourcepart(resource2);
    }
}
