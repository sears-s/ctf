package org.jxmpp.jid.parts;

import java.io.Serializable;
import org.jxmpp.stringprep.XmppStringprepException;

public abstract class Part implements CharSequence, Serializable {
    private static final long serialVersionUID = 1;
    private transient String internalizedCache;
    private final String part;

    protected Part(String part2) {
        this.part = part2;
    }

    public final int length() {
        return this.part.length();
    }

    public final char charAt(int index) {
        return this.part.charAt(index);
    }

    public final CharSequence subSequence(int start, int end) {
        return this.part.subSequence(start, end);
    }

    public final String toString() {
        return this.part;
    }

    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        return this.part.equals(other.toString());
    }

    public final int hashCode() {
        return this.part.hashCode();
    }

    protected static void assertNotLongerThan1023BytesOrEmpty(String string) throws XmppStringprepException {
        char[] bytes = string.toCharArray();
        if (bytes.length > 1023) {
            throw new XmppStringprepException(string, "Given string is longer then 1023 bytes");
        } else if (bytes.length == 0) {
            throw new XmppStringprepException(string, "Argument can't be the empty string");
        }
    }

    public final String intern() {
        if (this.internalizedCache == null) {
            this.internalizedCache = toString().intern();
        }
        return this.internalizedCache;
    }
}
