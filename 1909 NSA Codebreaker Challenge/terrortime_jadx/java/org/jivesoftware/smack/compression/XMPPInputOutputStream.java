package org.jivesoftware.smack.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class XMPPInputOutputStream {
    protected static FlushMethod flushMethod;
    protected final String compressionMethod;

    public enum FlushMethod {
        FULL_FLUSH,
        SYNC_FLUSH
    }

    public abstract InputStream getInputStream(InputStream inputStream) throws IOException;

    public abstract OutputStream getOutputStream(OutputStream outputStream) throws IOException;

    public abstract boolean isSupported();

    public static void setFlushMethod(FlushMethod flushMethod2) {
        flushMethod = flushMethod2;
    }

    protected XMPPInputOutputStream(String compressionMethod2) {
        this.compressionMethod = compressionMethod2;
    }

    public String getCompressionMethod() {
        return this.compressionMethod;
    }
}
