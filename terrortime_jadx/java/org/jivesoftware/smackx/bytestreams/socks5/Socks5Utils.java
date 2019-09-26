package org.jivesoftware.smackx.bytestreams.socks5;

import java.io.DataInputStream;
import java.io.IOException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.util.SHA1;
import org.jxmpp.jid.Jid;

public class Socks5Utils {
    public static String createDigest(String sessionID, Jid initiatorJID, Jid targetJID) {
        StringBuilder b = new StringBuilder();
        b.append(sessionID);
        b.append(initiatorJID);
        b.append(targetJID);
        return SHA1.hex(b.toString());
    }

    public static byte[] receiveSocks5Message(DataInputStream in) throws IOException, SmackException {
        byte[] header = new byte[5];
        in.readFully(header, 0, 5);
        if (header[3] == 3) {
            byte addressLength = header[4];
            byte[] response = new byte[(addressLength + 7)];
            System.arraycopy(header, 0, response, 0, header.length);
            in.readFully(response, header.length, addressLength + 2);
            return response;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unsupported SOCKS5 address type: ");
        sb.append(header[3]);
        sb.append(" (expected: 0x03)");
        throw new SmackException(sb.toString());
    }
}
