package org.minidns.record;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.minidns.dnsname.DnsName;
import org.minidns.record.Record.TYPE;

public class NSEC extends Data {
    private static final Logger LOGGER = Logger.getLogger(NSEC.class.getName());
    public final DnsName next;
    private final byte[] typeBitmap;
    public final List<TYPE> types;

    public static NSEC parse(DataInputStream dis, byte[] data, int length) throws IOException {
        DnsName next2 = DnsName.parse(dis, data);
        byte[] typeBitmap2 = new byte[(length - next2.size())];
        if (dis.read(typeBitmap2) == typeBitmap2.length) {
            return new NSEC(next2, readTypeBitMap(typeBitmap2));
        }
        throw new IOException();
    }

    public NSEC(String next2, List<TYPE> types2) {
        this(DnsName.from(next2), types2);
    }

    public NSEC(String next2, TYPE... types2) {
        this(DnsName.from(next2), Arrays.asList(types2));
    }

    public NSEC(DnsName next2, List<TYPE> types2) {
        this.next = next2;
        this.types = Collections.unmodifiableList(types2);
        this.typeBitmap = createTypeBitMap(types2);
    }

    public TYPE getType() {
        return TYPE.NSEC;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        this.next.writeToStream(dos);
        dos.write(this.typeBitmap);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.next);
        StringBuilder sb2 = sb.append('.');
        for (TYPE type : this.types) {
            sb2.append(' ');
            sb2.append(type);
        }
        return sb2.toString();
    }

    static byte[] createTypeBitMap(List<TYPE> types2) {
        List<Integer> typeList = new ArrayList<>(types2.size());
        for (TYPE type : types2) {
            typeList.add(Integer.valueOf(type.getValue()));
        }
        Collections.sort(typeList);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        int windowBlock = -1;
        byte[] bitmap = null;
        try {
            for (Integer type2 : typeList) {
                if (windowBlock == -1 || (type2.intValue() >> 8) != windowBlock) {
                    if (windowBlock != -1) {
                        writeOutBlock(bitmap, dos);
                    }
                    windowBlock = type2.intValue() >> 8;
                    dos.writeByte(windowBlock);
                    bitmap = new byte[32];
                }
                int a = (type2.intValue() >> 3) % 32;
                bitmap[a] = (byte) (bitmap[a] | (128 >> (type2.intValue() % 8)));
            }
            if (windowBlock != -1) {
                writeOutBlock(bitmap, dos);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeOutBlock(byte[] values, DataOutputStream dos) throws IOException {
        int n = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] != 0) {
                n = i + 1;
            }
        }
        dos.writeByte(n);
        for (int i2 = 0; i2 < n; i2++) {
            dos.writeByte(values[i2]);
        }
    }

    static List<TYPE> readTypeBitMap(byte[] typeBitmap2) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(typeBitmap2));
        int read = 0;
        ArrayList<TYPE> typeList = new ArrayList<>();
        while (typeBitmap2.length > read) {
            int windowBlock = dis.readUnsignedByte();
            int bitmapLength = dis.readUnsignedByte();
            for (int i = 0; i < bitmapLength; i++) {
                int b = dis.readUnsignedByte();
                for (int j = 0; j < 8; j++) {
                    if (((b >> j) & 1) > 0) {
                        int typeInt = (windowBlock << 8) + (i * 8) + (7 - j);
                        TYPE type = TYPE.getType(typeInt);
                        if (type == TYPE.UNKNOWN) {
                            Logger logger = LOGGER;
                            StringBuilder sb = new StringBuilder();
                            sb.append("Skipping unknown type in type bitmap: ");
                            sb.append(typeInt);
                            logger.warning(sb.toString());
                        } else {
                            typeList.add(type);
                        }
                    }
                }
            }
            read += bitmapLength + 2;
        }
        return typeList;
    }
}
