package org.jivesoftware.smack.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.jivesoftware.smack.compression.XMPPInputOutputStream.FlushMethod;

public class Java7ZlibInputOutputStream extends XMPPInputOutputStream {
    private static final int FULL_FLUSH_INT = 3;
    private static final int SYNC_FLUSH_INT = 2;
    private static final int compressionLevel = -1;
    /* access modifiers changed from: private */
    public static final Method method;
    /* access modifiers changed from: private */
    public static final boolean supported;

    static {
        Method m = null;
        boolean z = true;
        try {
            m = Deflater.class.getMethod("deflate", new Class[]{byte[].class, Integer.TYPE, Integer.TYPE, Integer.TYPE});
        } catch (NoSuchMethodException | SecurityException e) {
        }
        method = m;
        if (method == null) {
            z = false;
        }
        supported = z;
    }

    public Java7ZlibInputOutputStream() {
        super("zlib");
    }

    public boolean isSupported() {
        return supported;
    }

    public InputStream getInputStream(InputStream inputStream) {
        return new InflaterInputStream(inputStream, new Inflater(), 512) {
            public int available() throws IOException {
                if (this.inf.needsInput()) {
                    return 0;
                }
                return super.available();
            }
        };
    }

    public OutputStream getOutputStream(OutputStream outputStream) {
        final int flushMethodInt;
        if (flushMethod == FlushMethod.SYNC_FLUSH) {
            flushMethodInt = 2;
        } else {
            flushMethodInt = 3;
        }
        return new DeflaterOutputStream(outputStream, new Deflater(-1)) {
            public void flush() throws IOException {
                String str = "Can't flush";
                if (!Java7ZlibInputOutputStream.supported) {
                    super.flush();
                    return;
                }
                while (true) {
                    try {
                        int intValue = ((Integer) Java7ZlibInputOutputStream.method.invoke(this.def, new Object[]{this.buf, Integer.valueOf(0), Integer.valueOf(this.buf.length), Integer.valueOf(flushMethodInt)})).intValue();
                        int count = intValue;
                        if (intValue != 0) {
                            this.out.write(this.buf, 0, count);
                        } else {
                            super.flush();
                            return;
                        }
                    } catch (IllegalArgumentException e) {
                        throw new IOException(str);
                    } catch (IllegalAccessException e2) {
                        throw new IOException(str);
                    } catch (InvocationTargetException e3) {
                        throw new IOException(str);
                    }
                }
            }
        };
    }
}
