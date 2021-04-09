package org.minidns.idna;

public interface IdnaTransformator {
    String toASCII(String str);

    String toUnicode(String str);
}
