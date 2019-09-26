package org.bouncycastle.asn1;

import java.io.IOException;

public class DERExternalParser implements ASN1Encodable, InMemoryRepresentable {
    private ASN1StreamParser _parser;

    public DERExternalParser(ASN1StreamParser aSN1StreamParser) {
        this._parser = aSN1StreamParser;
    }

    public ASN1Primitive getLoadedObject() throws IOException {
        try {
            return new DLExternal(this._parser.readVector());
        } catch (IllegalArgumentException e) {
            throw new ASN1Exception(e.getMessage(), e);
        }
    }

    public ASN1Encodable readObject() throws IOException {
        return this._parser.readObject();
    }

    public ASN1Primitive toASN1Primitive() {
        String str = "unable to get DER object";
        try {
            return getLoadedObject();
        } catch (IOException e) {
            throw new ASN1ParsingException(str, e);
        } catch (IllegalArgumentException e2) {
            throw new ASN1ParsingException(str, e2);
        }
    }
}
