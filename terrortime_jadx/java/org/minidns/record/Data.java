package org.minidns.record;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.minidns.record.Record.TYPE;

public abstract class Data {
    private byte[] bytes;
    private transient Integer hashCodeCache;

    public abstract TYPE getType();

    /* access modifiers changed from: protected */
    public abstract void serialize(DataOutputStream dataOutputStream) throws IOException;

    private final void setBytes() {
        if (this.bytes == null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                serialize(new DataOutputStream(baos));
                this.bytes = baos.toByteArray();
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }
    }

    public final int length() {
        setBytes();
        return this.bytes.length;
    }

    public final void toOutputStream(OutputStream outputStream) throws IOException {
        toOutputStream(new DataOutputStream(outputStream));
    }

    public final void toOutputStream(DataOutputStream dos) throws IOException {
        setBytes();
        dos.write(this.bytes);
    }

    public final byte[] toByteArray() {
        setBytes();
        return (byte[]) this.bytes.clone();
    }

    public final int hashCode() {
        if (this.hashCodeCache == null) {
            setBytes();
            this.hashCodeCache = Integer.valueOf(this.bytes.hashCode());
        }
        return this.hashCodeCache.intValue();
    }

    public final boolean equals(Object other) {
        if (!(other instanceof Data)) {
            return false;
        }
        if (other == this) {
            return true;
        }
        Data otherData = (Data) other;
        otherData.setBytes();
        setBytes();
        return Arrays.equals(this.bytes, otherData.bytes);
    }
}
