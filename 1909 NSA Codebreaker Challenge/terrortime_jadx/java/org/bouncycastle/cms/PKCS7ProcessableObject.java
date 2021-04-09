package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;

public class PKCS7ProcessableObject implements CMSTypedData {
    private final ASN1Encodable structure;
    private final ASN1ObjectIdentifier type;

    public PKCS7ProcessableObject(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Encodable aSN1Encodable) {
        this.type = aSN1ObjectIdentifier;
        this.structure = aSN1Encodable;
    }

    public Object getContent() {
        return this.structure;
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }

    public void write(OutputStream outputStream) throws IOException, CMSException {
        ASN1Encodable aSN1Encodable = this.structure;
        boolean z = aSN1Encodable instanceof ASN1Sequence;
        String str = ASN1Encoding.DER;
        if (z) {
            Iterator it = ASN1Sequence.getInstance(aSN1Encodable).iterator();
            while (it.hasNext()) {
                outputStream.write(((ASN1Encodable) it.next()).toASN1Primitive().getEncoded(str));
            }
            return;
        }
        byte[] encoded = aSN1Encodable.toASN1Primitive().getEncoded(str);
        int i = 1;
        while ((encoded[i] & 255) > Byte.MAX_VALUE) {
            i++;
        }
        int i2 = i + 1;
        outputStream.write(encoded, i2, encoded.length - i2);
    }
}
