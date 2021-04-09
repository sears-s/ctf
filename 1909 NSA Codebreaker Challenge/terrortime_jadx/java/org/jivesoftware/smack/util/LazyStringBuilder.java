package org.jivesoftware.smack.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LazyStringBuilder implements Appendable, CharSequence {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final Logger LOGGER = Logger.getLogger(LazyStringBuilder.class.getName());
    private String cache;
    private final List<CharSequence> list = new ArrayList(20);

    private void invalidateCache() {
        this.cache = null;
    }

    public LazyStringBuilder append(LazyStringBuilder lsb) {
        this.list.addAll(lsb.list);
        invalidateCache();
        return this;
    }

    public LazyStringBuilder append(CharSequence csq) {
        this.list.add(csq);
        invalidateCache();
        return this;
    }

    public LazyStringBuilder append(CharSequence csq, int start, int end) {
        this.list.add(csq.subSequence(start, end));
        invalidateCache();
        return this;
    }

    public LazyStringBuilder append(char c) {
        this.list.add(Character.toString(c));
        invalidateCache();
        return this;
    }

    public int length() {
        String str = this.cache;
        if (str != null) {
            return str.length();
        }
        int length = 0;
        try {
            for (CharSequence csq : this.list) {
                length += csq.length();
            }
            return length;
        } catch (NullPointerException npe) {
            StringBuilder sb = safeToStringBuilder();
            Logger logger = LOGGER;
            Level level = Level.SEVERE;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("The following LazyStringBuilder threw a NullPointerException:  ");
            sb2.append(sb);
            logger.log(level, sb2.toString(), npe);
            throw npe;
        }
    }

    public char charAt(int index) {
        String str = this.cache;
        if (str != null) {
            return str.charAt(index);
        }
        for (CharSequence csq : this.list) {
            if (index < csq.length()) {
                return csq.charAt(index);
            }
            index -= csq.length();
        }
        throw new IndexOutOfBoundsException();
    }

    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    public String toString() {
        if (this.cache == null) {
            StringBuilder sb = new StringBuilder(length());
            for (CharSequence csq : this.list) {
                sb.append(csq);
            }
            this.cache = sb.toString();
        }
        return this.cache;
    }

    public StringBuilder safeToStringBuilder() {
        StringBuilder sb = new StringBuilder();
        for (CharSequence csq : this.list) {
            sb.append(csq);
        }
        return sb;
    }

    public List<CharSequence> getAsList() {
        String str = this.cache;
        if (str != null) {
            return Collections.singletonList(str);
        }
        return Collections.unmodifiableList(this.list);
    }
}
