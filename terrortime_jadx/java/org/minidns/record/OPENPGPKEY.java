package org.minidns.record;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.minidns.record.Record.TYPE;
import org.minidns.util.Base64;

public class OPENPGPKEY extends Data {
    private final byte[] publicKeyPacket;
    private transient String publicKeyPacketBase64Cache;

    public static OPENPGPKEY parse(DataInputStream dis, int length) throws IOException {
        byte[] publicKeyPacket2 = new byte[length];
        dis.readFully(publicKeyPacket2);
        return new OPENPGPKEY(publicKeyPacket2);
    }

    OPENPGPKEY(byte[] publicKeyPacket2) {
        this.publicKeyPacket = publicKeyPacket2;
    }

    public TYPE getType() {
        return TYPE.OPENPGPKEY;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.write(this.publicKeyPacket);
    }

    public String toString() {
        return getPublicKeyPacketBase64();
    }

    public String getPublicKeyPacketBase64() {
        if (this.publicKeyPacketBase64Cache == null) {
            this.publicKeyPacketBase64Cache = Base64.encodeToString(this.publicKeyPacket);
        }
        return this.publicKeyPacketBase64Cache;
    }

    public byte[] getPublicKeyPacket() {
        return (byte[]) this.publicKeyPacket.clone();
    }
}
