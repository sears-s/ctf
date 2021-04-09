package org.bouncycastle.util.encoders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.Strings;

public class Hex {
    private static final Encoder encoder = new HexEncoder();

    public static int decode(String str, OutputStream outputStream) throws IOException {
        return encoder.decode(str, outputStream);
    }

    public static byte[] decode(String str) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            encoder.decode(str, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("exception decoding Hex string: ");
            sb.append(e.getMessage());
            throw new DecoderException(sb.toString(), e);
        }
    }

    public static byte[] decode(byte[] bArr) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            encoder.decode(bArr, 0, bArr.length, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("exception decoding Hex data: ");
            sb.append(e.getMessage());
            throw new DecoderException(sb.toString(), e);
        }
    }

    public static int encode(byte[] bArr, int i, int i2, OutputStream outputStream) throws IOException {
        return encoder.encode(bArr, i, i2, outputStream);
    }

    public static int encode(byte[] bArr, OutputStream outputStream) throws IOException {
        return encoder.encode(bArr, 0, bArr.length, outputStream);
    }

    public static byte[] encode(byte[] bArr) {
        return encode(bArr, 0, bArr.length);
    }

    public static byte[] encode(byte[] bArr, int i, int i2) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            encoder.encode(bArr, i, i2, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("exception encoding Hex string: ");
            sb.append(e.getMessage());
            throw new EncoderException(sb.toString(), e);
        }
    }

    public static String toHexString(byte[] bArr) {
        return toHexString(bArr, 0, bArr.length);
    }

    public static String toHexString(byte[] bArr, int i, int i2) {
        return Strings.fromByteArray(encode(bArr, i, i2));
    }
}
