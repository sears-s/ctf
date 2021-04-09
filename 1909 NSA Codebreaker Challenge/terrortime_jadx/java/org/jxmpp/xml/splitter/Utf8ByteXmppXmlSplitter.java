package org.jxmpp.xml.splitter;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.eac.CertificateBody;

public class Utf8ByteXmppXmlSplitter extends OutputStream {
    private final byte[] buffer = new byte[6];
    private byte count;
    private byte expectedLength;
    private final char[] writeBuffer = new char[2];
    private final XmppXmlSplitter xmppXmlSplitter;

    public Utf8ByteXmppXmlSplitter(XmppElementCallback xmppElementCallback) {
        this.xmppXmlSplitter = new XmppXmlSplitter(xmppElementCallback);
    }

    public void write(byte b) throws IOException {
        int codepoint;
        int len;
        byte[] bArr = this.buffer;
        byte b2 = this.count;
        bArr[b2] = b;
        if (b2 == 0) {
            int firstByte = bArr[0] & 255;
            if (firstByte < 128) {
                this.expectedLength = 1;
            } else if (firstByte < 224) {
                this.expectedLength = 2;
            } else if (firstByte < 240) {
                this.expectedLength = 3;
            } else if (firstByte < 248) {
                this.expectedLength = 4;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Invalid first UTF-8 byte: ");
                sb.append(firstByte);
                throw new IOException(sb.toString());
            }
        }
        byte b3 = (byte) (this.count + 1);
        this.count = b3;
        byte b4 = this.expectedLength;
        if (b3 == b4) {
            if (b4 != 1) {
                if (b4 == 2) {
                    codepoint = (this.buffer[0] & 31) << 6;
                } else if (b4 == 3) {
                    codepoint = (this.buffer[0] & 15) << 12;
                } else if (b4 == 4) {
                    codepoint = (this.buffer[0] & 6) << 18;
                } else {
                    throw new IllegalStateException();
                }
                int i = 1;
                while (true) {
                    byte b5 = this.expectedLength;
                    if (i >= b5) {
                        break;
                    }
                    codepoint |= (this.buffer[i] & 63) << (((b5 - 1) - i) * 6);
                    i++;
                }
            } else {
                codepoint = this.buffer[0] & CertificateBody.profileType;
            }
            if (codepoint < 65536) {
                len = 1;
                this.writeBuffer[0] = (char) codepoint;
            } else {
                len = 2;
                char[] cArr = this.writeBuffer;
                cArr[0] = (char) ((-6291456 & codepoint) + 55296);
                cArr[1] = (char) ((codepoint & 1023) + 56320);
            }
            this.xmppXmlSplitter.write(this.writeBuffer, 0, len);
            this.count = 0;
        }
    }

    public void write(int b) throws IOException {
        write((byte) (b & 255));
    }
}
