package org.minidns.record;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.util.StringUtils;
import org.minidns.record.Record.TYPE;

public class TXT extends Data {
    private final byte[] blob;
    private transient List<String> characterStringsCache;
    private transient String textCache;

    public static TXT parse(DataInputStream dis, int length) throws IOException {
        byte[] blob2 = new byte[length];
        dis.readFully(blob2);
        return new TXT(blob2);
    }

    public TXT(byte[] blob2) {
        this.blob = blob2;
    }

    public byte[] getBlob() {
        return (byte[]) this.blob.clone();
    }

    public String getText() {
        if (this.textCache == null) {
            StringBuilder sb = new StringBuilder();
            Iterator<String> it = getCharacterStrings().iterator();
            while (it.hasNext()) {
                sb.append((String) it.next());
                if (it.hasNext()) {
                    sb.append(" / ");
                }
            }
            this.textCache = sb.toString();
        }
        return this.textCache;
    }

    public List<String> getCharacterStrings() {
        if (this.characterStringsCache == null) {
            List<byte[]> extents = getExtents();
            List<String> characterStrings = new ArrayList<>(extents.size());
            for (byte[] extent : extents) {
                try {
                    characterStrings.add(new String(extent, StringUtils.UTF8));
                } catch (UnsupportedEncodingException e) {
                    throw new AssertionError(e);
                }
            }
            this.characterStringsCache = Collections.unmodifiableList(characterStrings);
        }
        return this.characterStringsCache;
    }

    public List<byte[]> getExtents() {
        ArrayList<byte[]> extents = new ArrayList<>();
        int used = 0;
        while (true) {
            byte[] bArr = this.blob;
            if (used >= bArr.length) {
                return extents;
            }
            int segLength = bArr[used] & 255;
            int used2 = used + 1;
            extents.add(Arrays.copyOfRange(bArr, used2, used2 + segLength));
            used = used2 + segLength;
        }
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.write(this.blob);
    }

    public TYPE getType() {
        return TYPE.TXT;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        String str = "\"";
        sb.append(str);
        sb.append(getText());
        sb.append(str);
        return sb.toString();
    }
}
