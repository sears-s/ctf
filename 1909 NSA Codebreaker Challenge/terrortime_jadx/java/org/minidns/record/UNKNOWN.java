package org.minidns.record;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.minidns.record.Record.TYPE;

public class UNKNOWN extends Data {
    private final byte[] data;
    private final TYPE type;

    private UNKNOWN(DataInputStream dis, int payloadLength, TYPE type2) throws IOException {
        this.type = type2;
        this.data = new byte[payloadLength];
        dis.readFully(this.data);
    }

    public TYPE getType() {
        return this.type;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.write(this.data);
    }

    public static UNKNOWN parse(DataInputStream dis, int payloadLength, TYPE type2) throws IOException {
        return new UNKNOWN(dis, payloadLength, type2);
    }
}
