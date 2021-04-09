package org.jivesoftware.smack.util;

public interface ExceptionCallback<E> {
    void processException(E e);
}
