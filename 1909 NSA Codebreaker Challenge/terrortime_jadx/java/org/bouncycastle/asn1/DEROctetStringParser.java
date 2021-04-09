package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;

public class DEROctetStringParser implements ASN1OctetStringParser {
    private DefiniteLengthInputStream stream;

    DEROctetStringParser(DefiniteLengthInputStream definiteLengthInputStream) {
        this.stream = definiteLengthInputStream;
    }

    public ASN1Primitive getLoadedObject() throws IOException {
        return new DEROctetString(this.stream.toByteArray());
    }

    public InputStream getOctetStream() {
        return this.stream;
    }

    public ASN1Primitive toASN1Primitive() {
        try {
            return getLoadedObject();
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("IOException converting stream to byte array: ");
            sb.append(e.getMessage());
            throw new ASN1ParsingException(sb.toString(), e);
        }
    }
}
