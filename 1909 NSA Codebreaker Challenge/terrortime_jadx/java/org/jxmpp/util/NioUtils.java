package org.jxmpp.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class NioUtils {
    public static void write(ByteBuffer byteBuffer, OutputStream outputStream) throws IOException {
        while (byteBuffer.remaining() > 0) {
            outputStream.write(byteBuffer.get());
        }
    }
}
