package org.minidns.util;

public interface ExceptionCallback<E> {
    void processException(E e);
}
