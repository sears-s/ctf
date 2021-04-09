package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public class BEROctetString extends ASN1OctetString {
    private static final int DEFAULT_LENGTH = 1000;
    private final int chunkSize;
    /* access modifiers changed from: private */
    public final ASN1OctetString[] octs;

    public BEROctetString(byte[] bArr) {
        this(bArr, 1000);
    }

    public BEROctetString(byte[] bArr, int i) {
        this(bArr, null, i);
    }

    private BEROctetString(byte[] bArr, ASN1OctetString[] aSN1OctetStringArr, int i) {
        super(bArr);
        this.octs = aSN1OctetStringArr;
        this.chunkSize = i;
    }

    public BEROctetString(ASN1OctetString[] aSN1OctetStringArr) {
        this(aSN1OctetStringArr, 1000);
    }

    public BEROctetString(ASN1OctetString[] aSN1OctetStringArr, int i) {
        this(toBytes(aSN1OctetStringArr), aSN1OctetStringArr, i);
    }

    static BEROctetString fromSequence(ASN1Sequence aSN1Sequence) {
        ASN1OctetString[] aSN1OctetStringArr = new ASN1OctetString[aSN1Sequence.size()];
        Enumeration objects = aSN1Sequence.getObjects();
        int i = 0;
        while (objects.hasMoreElements()) {
            int i2 = i + 1;
            aSN1OctetStringArr[i] = (ASN1OctetString) objects.nextElement();
            i = i2;
        }
        return new BEROctetString(aSN1OctetStringArr);
    }

    private Vector generateOcts() {
        Vector vector = new Vector();
        int i = 0;
        while (i < this.string.length) {
            byte[] bArr = new byte[((this.chunkSize + i > this.string.length ? this.string.length : this.chunkSize + i) - i)];
            System.arraycopy(this.string, i, bArr, 0, bArr.length);
            vector.addElement(new DEROctetString(bArr));
            i += this.chunkSize;
        }
        return vector;
    }

    private static byte[] toBytes(ASN1OctetString[] aSN1OctetStringArr) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i = 0;
        while (i != aSN1OctetStringArr.length) {
            try {
                byteArrayOutputStream.write(aSN1OctetStringArr[i].getOctets());
                i++;
            } catch (ClassCastException e) {
                StringBuilder sb = new StringBuilder();
                sb.append(aSN1OctetStringArr[i].getClass().getName());
                sb.append(" found in input should only contain DEROctetString");
                throw new IllegalArgumentException(sb.toString());
            } catch (IOException e2) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("exception converting octets ");
                sb2.append(e2.toString());
                throw new IllegalArgumentException(sb2.toString());
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    public void encode(ASN1OutputStream aSN1OutputStream) throws IOException {
        aSN1OutputStream.write(36);
        aSN1OutputStream.write(128);
        Enumeration objects = getObjects();
        while (objects.hasMoreElements()) {
            aSN1OutputStream.writeObject((ASN1Encodable) objects.nextElement());
        }
        aSN1OutputStream.write(0);
        aSN1OutputStream.write(0);
    }

    /* access modifiers changed from: 0000 */
    public int encodedLength() throws IOException {
        Enumeration objects = getObjects();
        int i = 0;
        while (objects.hasMoreElements()) {
            i += ((ASN1Encodable) objects.nextElement()).toASN1Primitive().encodedLength();
        }
        return i + 2 + 2;
    }

    public Enumeration getObjects() {
        return this.octs == null ? generateOcts().elements() : new Enumeration() {
            int counter = 0;

            public boolean hasMoreElements() {
                return this.counter < BEROctetString.this.octs.length;
            }

            public Object nextElement() {
                ASN1OctetString[] access$000 = BEROctetString.this.octs;
                int i = this.counter;
                this.counter = i + 1;
                return access$000[i];
            }
        };
    }

    public byte[] getOctets() {
        return this.string;
    }

    /* access modifiers changed from: 0000 */
    public boolean isConstructed() {
        return true;
    }
}
