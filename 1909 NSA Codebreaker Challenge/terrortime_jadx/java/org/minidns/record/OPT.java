package org.minidns.record;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.minidns.edns.EdnsOption;
import org.minidns.record.Record.TYPE;

public class OPT extends Data {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public final List<EdnsOption> variablePart;

    public OPT() {
        this(Collections.emptyList());
    }

    public OPT(List<EdnsOption> variablePart2) {
        this.variablePart = Collections.unmodifiableList(variablePart2);
    }

    public static OPT parse(DataInputStream dis, int payloadLength) throws IOException {
        List list;
        if (payloadLength == 0) {
            list = Collections.emptyList();
        } else {
            int payloadLeft = payloadLength;
            List<EdnsOption> variablePart2 = new ArrayList<>(4);
            while (payloadLeft > 0) {
                int optionCode = dis.readUnsignedShort();
                int optionLength = dis.readUnsignedShort();
                byte[] optionData = new byte[optionLength];
                dis.read(optionData);
                variablePart2.add(EdnsOption.parse(optionCode, optionData));
                payloadLeft -= optionLength + 4;
            }
            list = variablePart2;
        }
        return new OPT(list);
    }

    public TYPE getType() {
        return TYPE.OPT;
    }

    /* access modifiers changed from: protected */
    public void serialize(DataOutputStream dos) throws IOException {
        for (EdnsOption endsOption : this.variablePart) {
            endsOption.writeToDos(dos);
        }
    }
}
