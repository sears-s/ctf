package org.jxmpp.stringprep.simple;

import java.util.Locale;
import org.jxmpp.stringprep.XmppStringPrepUtil;
import org.jxmpp.stringprep.XmppStringprep;
import org.jxmpp.stringprep.XmppStringprepException;

public final class SimpleXmppStringprep implements XmppStringprep {
    private static final char[] LOCALPART_FURTHER_EXCLUDED_CHARACTERS = {'\"', '&', '\'', '/', ':', '<', '>', '@', ' '};
    private static SimpleXmppStringprep instance;

    public static void setup() {
        XmppStringPrepUtil.setXmppStringprep(getInstance());
    }

    public static SimpleXmppStringprep getInstance() {
        if (instance == null) {
            instance = new SimpleXmppStringprep();
        }
        return instance;
    }

    private SimpleXmppStringprep() {
    }

    public String localprep(String string) throws XmppStringprepException {
        char[] charArray;
        String string2 = simpleStringprep(string);
        for (char charFromString : string2.toCharArray()) {
            char[] cArr = LOCALPART_FURTHER_EXCLUDED_CHARACTERS;
            int length = cArr.length;
            int i = 0;
            while (i < length) {
                char forbiddenChar = cArr[i];
                if (charFromString != forbiddenChar) {
                    i++;
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Localpart must not contain '");
                    sb.append(forbiddenChar);
                    sb.append("'");
                    throw new XmppStringprepException(string2, sb.toString());
                }
            }
        }
        return string2;
    }

    public String domainprep(String string) throws XmppStringprepException {
        return simpleStringprep(string);
    }

    public String resourceprep(String string) throws XmppStringprepException {
        return string;
    }

    private static String simpleStringprep(String string) {
        return string.toLowerCase(Locale.US);
    }
}
