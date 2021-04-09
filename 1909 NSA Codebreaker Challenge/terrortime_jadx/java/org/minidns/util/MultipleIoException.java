package org.minidns.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MultipleIoException extends IOException {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final long serialVersionUID = -5932211337552319515L;
    private final List<IOException> ioExceptions;

    private MultipleIoException(List<? extends IOException> ioExceptions2) {
        super(getMessage(ioExceptions2));
        this.ioExceptions = Collections.unmodifiableList(ioExceptions2);
    }

    public List<IOException> getExceptions() {
        return this.ioExceptions;
    }

    private static String getMessage(Collection<? extends Exception> exceptions) {
        StringBuilder sb = new StringBuilder();
        Iterator<? extends Exception> it = exceptions.iterator();
        while (it.hasNext()) {
            sb.append(((Exception) it.next()).getMessage());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static void throwIfRequired(List<? extends IOException> ioExceptions2) throws IOException {
        if (ioExceptions2 != null && !ioExceptions2.isEmpty()) {
            if (ioExceptions2.size() == 1) {
                throw ((IOException) ioExceptions2.get(0));
            }
            throw new MultipleIoException(ioExceptions2);
        }
    }

    public static IOException toIOException(List<? extends IOException> ioExceptions2) {
        int size = ioExceptions2.size();
        if (size == 1) {
            return (IOException) ioExceptions2.get(0);
        }
        if (size > 1) {
            return new MultipleIoException(ioExceptions2);
        }
        return null;
    }
}
