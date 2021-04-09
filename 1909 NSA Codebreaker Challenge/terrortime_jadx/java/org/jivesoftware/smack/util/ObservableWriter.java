package org.jivesoftware.smack.util;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class ObservableWriter extends Writer {
    private static final int MAX_STRING_BUILDER_SIZE = 4096;
    final List<WriterListener> listeners = new ArrayList();
    private final StringBuilder stringBuilder = new StringBuilder(4096);
    Writer wrappedWriter = null;

    public ObservableWriter(Writer wrappedWriter2) {
        this.wrappedWriter = wrappedWriter2;
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        this.wrappedWriter.write(cbuf, off, len);
        maybeNotifyListeners(new String(cbuf, off, len));
    }

    public void flush() throws IOException {
        notifyListeners();
        this.wrappedWriter.flush();
    }

    public void close() throws IOException {
        this.wrappedWriter.close();
    }

    public void write(int c) throws IOException {
        this.wrappedWriter.write(c);
    }

    public void write(char[] cbuf) throws IOException {
        this.wrappedWriter.write(cbuf);
        maybeNotifyListeners(new String(cbuf));
    }

    public void write(String str) throws IOException {
        this.wrappedWriter.write(str);
        maybeNotifyListeners(str);
    }

    public void write(String str, int off, int len) throws IOException {
        this.wrappedWriter.write(str, off, len);
        maybeNotifyListeners(str.substring(off, off + len));
    }

    private void maybeNotifyListeners(String s) {
        this.stringBuilder.append(s);
        if (this.stringBuilder.length() > 4096) {
            notifyListeners();
        }
    }

    private void notifyListeners() {
        WriterListener[] writerListeners;
        synchronized (this.listeners) {
            writerListeners = new WriterListener[this.listeners.size()];
            this.listeners.toArray(writerListeners);
        }
        String str = this.stringBuilder.toString();
        this.stringBuilder.setLength(0);
        for (WriterListener writerListener : writerListeners) {
            writerListener.write(str);
        }
    }

    public void addWriterListener(WriterListener writerListener) {
        if (writerListener != null) {
            synchronized (this.listeners) {
                if (!this.listeners.contains(writerListener)) {
                    this.listeners.add(writerListener);
                }
            }
        }
    }

    public void removeWriterListener(WriterListener writerListener) {
        synchronized (this.listeners) {
            this.listeners.remove(writerListener);
        }
    }
}
