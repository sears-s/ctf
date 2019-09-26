package org.jivesoftware.smack.util;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ObservableReader extends Reader {
    final List<ReaderListener> listeners = new ArrayList();
    Reader wrappedReader = null;

    public ObservableReader(Reader wrappedReader2) {
        this.wrappedReader = wrappedReader2;
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        ReaderListener[] readerListeners;
        int count = this.wrappedReader.read(cbuf, off, len);
        if (count > 0) {
            String str = new String(cbuf, off, count);
            synchronized (this.listeners) {
                readerListeners = new ReaderListener[this.listeners.size()];
                this.listeners.toArray(readerListeners);
            }
            for (ReaderListener read : readerListeners) {
                read.read(str);
            }
        }
        return count;
    }

    public void close() throws IOException {
        this.wrappedReader.close();
    }

    public int read() throws IOException {
        return this.wrappedReader.read();
    }

    public int read(char[] cbuf) throws IOException {
        return this.wrappedReader.read(cbuf);
    }

    public long skip(long n) throws IOException {
        return this.wrappedReader.skip(n);
    }

    public boolean ready() throws IOException {
        return this.wrappedReader.ready();
    }

    public boolean markSupported() {
        return this.wrappedReader.markSupported();
    }

    public void mark(int readAheadLimit) throws IOException {
        this.wrappedReader.mark(readAheadLimit);
    }

    public void reset() throws IOException {
        this.wrappedReader.reset();
    }

    public void addReaderListener(ReaderListener readerListener) {
        if (readerListener != null) {
            synchronized (this.listeners) {
                if (!this.listeners.contains(readerListener)) {
                    this.listeners.add(readerListener);
                }
            }
        }
    }

    public void removeReaderListener(ReaderListener readerListener) {
        synchronized (this.listeners) {
            this.listeners.remove(readerListener);
        }
    }
}
