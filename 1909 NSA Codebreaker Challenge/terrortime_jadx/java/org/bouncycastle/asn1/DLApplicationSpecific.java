package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DLApplicationSpecific extends ASN1ApplicationSpecific {
    public DLApplicationSpecific(int i, ASN1Encodable aSN1Encodable) throws IOException {
        this(true, i, aSN1Encodable);
    }

    public DLApplicationSpecific(int i, ASN1EncodableVector aSN1EncodableVector) {
        super(true, i, getEncodedVector(aSN1EncodableVector));
    }

    public DLApplicationSpecific(int i, byte[] bArr) {
        this(false, i, bArr);
    }

    public DLApplicationSpecific(boolean z, int i, ASN1Encodable aSN1Encodable) throws IOException {
        super(z || aSN1Encodable.toASN1Primitive().isConstructed(), i, getEncoding(z, aSN1Encodable));
    }

    DLApplicationSpecific(boolean z, int i, byte[] bArr) {
        super(z, i, bArr);
    }

    private static byte[] getEncodedVector(ASN1EncodableVector aSN1EncodableVector) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i = 0;
        while (i != aSN1EncodableVector.size()) {
            try {
                byteArrayOutputStream.write(((ASN1Object) aSN1EncodableVector.get(i)).getEncoded(ASN1Encoding.DL));
                i++;
            } catch (IOException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("malformed object: ");
                sb.append(e);
                throw new ASN1ParsingException(sb.toString(), e);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private static byte[] getEncoding(boolean z, ASN1Encodable aSN1Encodable) throws IOException {
        byte[] encoded = aSN1Encodable.toASN1Primitive().getEncoded(ASN1Encoding.DL);
        if (z) {
            return encoded;
        }
        int lengthOfHeader = getLengthOfHeader(encoded);
        byte[] bArr = new byte[(encoded.length - lengthOfHeader)];
        System.arraycopy(encoded, lengthOfHeader, bArr, 0, bArr.length);
        return bArr;
    }

    /* access modifiers changed from: 0000 */
    public void encode(ASN1OutputStream aSN1OutputStream) throws IOException {
        aSN1OutputStream.writeEncoded(this.isConstructed ? 96 : 64, this.tag, this.octets);
    }
}
