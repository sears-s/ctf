package org.minidns.record;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class InternetAddressRR extends Data {
    private transient InetAddress inetAddress;
    protected final byte[] ip;

    protected InternetAddressRR(byte[] ip2) {
        this.ip = ip2;
    }

    public final void serialize(DataOutputStream dos) throws IOException {
        dos.write(this.ip);
    }

    public final byte[] getIp() {
        return (byte[]) this.ip.clone();
    }

    public final InetAddress getInetAddress() {
        InetAddress i = this.inetAddress;
        if (i != null) {
            return i;
        }
        try {
            InetAddress i2 = InetAddress.getByAddress(this.ip);
            this.inetAddress = i2;
            return i2;
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        }
    }
}
