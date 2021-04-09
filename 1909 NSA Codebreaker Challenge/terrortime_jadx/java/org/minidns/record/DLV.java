package org.minidns.record;

import java.io.DataInputStream;
import java.io.IOException;
import org.minidns.constants.DnssecConstants.DigestAlgorithm;
import org.minidns.constants.DnssecConstants.SignatureAlgorithm;
import org.minidns.record.Record.TYPE;

public class DLV extends DelegatingDnssecRR {
    public static DLV parse(DataInputStream dis, int length) throws IOException {
        SharedData parsedData = DelegatingDnssecRR.parseSharedData(dis, length);
        return new DLV(parsedData.keyTag, parsedData.algorithm, parsedData.digestType, parsedData.digest);
    }

    public DLV(int keyTag, byte algorithm, byte digestType, byte[] digest) {
        super(keyTag, algorithm, digestType, digest);
    }

    public DLV(int keyTag, SignatureAlgorithm algorithm, DigestAlgorithm digestType, byte[] digest) {
        super(keyTag, algorithm, digestType, digest);
    }

    public TYPE getType() {
        return TYPE.DLV;
    }
}
