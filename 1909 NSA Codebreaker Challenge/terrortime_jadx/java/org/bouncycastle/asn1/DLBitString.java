package org.bouncycastle.asn1;

import java.io.IOException;

public class DLBitString extends ASN1BitString {
    protected DLBitString(byte b, int i) {
        this(toByteArray(b), i);
    }

    public DLBitString(int i) {
        super(getBytes(i), getPadBits(i));
    }

    public DLBitString(ASN1Encodable aSN1Encodable) throws IOException {
        super(aSN1Encodable.toASN1Primitive().getEncoded(ASN1Encoding.DER), 0);
    }

    public DLBitString(byte[] bArr) {
        this(bArr, 0);
    }

    public DLBitString(byte[] bArr, int i) {
        super(bArr, i);
    }

    static DLBitString fromOctetString(byte[] bArr) {
        if (bArr.length >= 1) {
            byte b = bArr[0];
            byte[] bArr2 = new byte[(bArr.length - 1)];
            if (bArr2.length != 0) {
                System.arraycopy(bArr, 1, bArr2, 0, bArr.length - 1);
            }
            return new DLBitString(bArr2, (int) b);
        }
        throw new IllegalArgumentException("truncated BIT STRING detected");
    }

    public static ASN1BitString getInstance(Object obj) {
        if (obj == null || (obj instanceof DLBitString)) {
            return (DLBitString) obj;
        }
        if (obj instanceof DERBitString) {
            return (DERBitString) obj;
        }
        if (obj instanceof byte[]) {
            try {
                return (ASN1BitString) fromByteArray((byte[]) obj);
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("encoding error in getInstance: ");
                sb.append(e.toString());
                throw new IllegalArgumentException(sb.toString());
            }
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("illegal object in getInstance: ");
            sb2.append(obj.getClass().getName());
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    public static ASN1BitString getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        ASN1Primitive object = aSN1TaggedObject.getObject();
        return (z || (object instanceof DLBitString)) ? getInstance(object) : fromOctetString(((ASN1OctetString) object).getOctets());
    }

    private static byte[] toByteArray(byte b) {
        return new byte[]{b};
    }

    /* access modifiers changed from: 0000 */
    public void encode(ASN1OutputStream aSN1OutputStream) throws IOException {
        byte[] bArr = this.data;
        byte[] bArr2 = new byte[(bArr.length + 1)];
        bArr2[0] = (byte) getPadBits();
        System.arraycopy(bArr, 0, bArr2, 1, bArr2.length - 1);
        aSN1OutputStream.writeEncoded(3, bArr2);
    }

    /* access modifiers changed from: 0000 */
    public int encodedLength() {
        return StreamUtil.calculateBodyLength(this.data.length + 1) + 1 + this.data.length + 1;
    }

    /* access modifiers changed from: 0000 */
    public boolean isConstructed() {
        return false;
    }
}
