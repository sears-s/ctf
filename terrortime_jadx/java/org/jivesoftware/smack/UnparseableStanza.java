package org.jivesoftware.smack;

public class UnparseableStanza {
    private final CharSequence content;
    private final Exception e;

    UnparseableStanza(CharSequence content2, Exception e2) {
        this.content = content2;
        this.e = e2;
    }

    public Exception getParsingException() {
        return this.e;
    }

    public CharSequence getContent() {
        return this.content;
    }
}
