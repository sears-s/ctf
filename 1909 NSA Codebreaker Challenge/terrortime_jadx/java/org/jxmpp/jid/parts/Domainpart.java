package org.jxmpp.jid.parts;

import org.jxmpp.stringprep.XmppStringPrepUtil;
import org.jxmpp.stringprep.XmppStringprepException;

public class Domainpart extends Part {
    private static final long serialVersionUID = 1;

    private Domainpart(String domain) {
        super(domain);
    }

    public static Domainpart fromOrNull(CharSequence cs) {
        try {
            return from(cs.toString());
        } catch (XmppStringprepException e) {
            return null;
        }
    }

    public static Domainpart fromOrThrowUnchecked(CharSequence cs) {
        try {
            return from(cs.toString());
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Domainpart from(String domain) throws XmppStringprepException {
        if (domain != null) {
            if (domain.length() > 0 && domain.charAt(domain.length() - 1) == '.') {
                domain = domain.substring(0, domain.length() - 1);
            }
            String domain2 = XmppStringPrepUtil.domainprep(domain);
            assertNotLongerThan1023BytesOrEmpty(domain2);
            return new Domainpart(domain2);
        }
        throw new XmppStringprepException(domain, "Input 'domain' must not be null");
    }
}
