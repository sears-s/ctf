package org.bouncycastle.asn1.eac;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1ApplicationSpecific;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERApplicationSpecific;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public class CVCertificate extends ASN1Object {
    private static int bodyValid = 1;
    private static int signValid = 2;
    private CertificateBody certificateBody;
    private byte[] signature;
    private int valid;

    private CVCertificate(ASN1ApplicationSpecific aSN1ApplicationSpecific) throws IOException {
        setPrivateData(aSN1ApplicationSpecific);
    }

    public CVCertificate(ASN1InputStream aSN1InputStream) throws IOException {
        initFrom(aSN1InputStream);
    }

    public CVCertificate(CertificateBody certificateBody2, byte[] bArr) throws IOException {
        this.certificateBody = certificateBody2;
        this.signature = Arrays.clone(bArr);
        this.valid |= bodyValid;
        this.valid |= signValid;
    }

    public static CVCertificate getInstance(Object obj) {
        if (obj instanceof CVCertificate) {
            return (CVCertificate) obj;
        }
        if (obj == null) {
            return null;
        }
        try {
            return new CVCertificate(ASN1ApplicationSpecific.getInstance(obj));
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("unable to parse data: ");
            sb.append(e.getMessage());
            throw new ASN1ParsingException(sb.toString(), e);
        }
    }

    private void initFrom(ASN1InputStream aSN1InputStream) throws IOException {
        while (true) {
            ASN1Primitive readObject = aSN1InputStream.readObject();
            if (readObject == null) {
                return;
            }
            if (readObject instanceof ASN1ApplicationSpecific) {
                setPrivateData((ASN1ApplicationSpecific) readObject);
            } else {
                throw new IOException("Invalid Input Stream for creating an Iso7816CertificateStructure");
            }
        }
    }

    private void setPrivateData(ASN1ApplicationSpecific aSN1ApplicationSpecific) throws IOException {
        int i;
        int i2;
        this.valid = 0;
        if (aSN1ApplicationSpecific.getApplicationTag() == 33) {
            ASN1InputStream aSN1InputStream = new ASN1InputStream(aSN1ApplicationSpecific.getContents());
            while (true) {
                ASN1Primitive readObject = aSN1InputStream.readObject();
                if (readObject == null) {
                    aSN1InputStream.close();
                    if (this.valid != (signValid | bodyValid)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("invalid CARDHOLDER_CERTIFICATE :");
                        sb.append(aSN1ApplicationSpecific.getApplicationTag());
                        throw new IOException(sb.toString());
                    }
                    return;
                } else if (readObject instanceof ASN1ApplicationSpecific) {
                    ASN1ApplicationSpecific aSN1ApplicationSpecific2 = (ASN1ApplicationSpecific) readObject;
                    int applicationTag = aSN1ApplicationSpecific2.getApplicationTag();
                    if (applicationTag == 55) {
                        this.signature = aSN1ApplicationSpecific2.getContents();
                        i2 = this.valid;
                        i = signValid;
                    } else if (applicationTag == 78) {
                        this.certificateBody = CertificateBody.getInstance(aSN1ApplicationSpecific2);
                        i2 = this.valid;
                        i = bodyValid;
                    } else {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Invalid tag, not an Iso7816CertificateStructure :");
                        sb2.append(aSN1ApplicationSpecific2.getApplicationTag());
                        throw new IOException(sb2.toString());
                    }
                    this.valid = i2 | i;
                } else {
                    throw new IOException("Invalid Object, not an Iso7816CertificateStructure");
                }
            }
        } else {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("not a CARDHOLDER_CERTIFICATE :");
            sb3.append(aSN1ApplicationSpecific.getApplicationTag());
            throw new IOException(sb3.toString());
        }
    }

    public CertificationAuthorityReference getAuthorityReference() throws IOException {
        return this.certificateBody.getCertificationAuthorityReference();
    }

    public CertificateBody getBody() {
        return this.certificateBody;
    }

    public int getCertificateType() {
        return this.certificateBody.getCertificateType();
    }

    public PackedDate getEffectiveDate() throws IOException {
        return this.certificateBody.getCertificateEffectiveDate();
    }

    public PackedDate getExpirationDate() throws IOException {
        return this.certificateBody.getCertificateExpirationDate();
    }

    public ASN1ObjectIdentifier getHolderAuthorization() throws IOException {
        return this.certificateBody.getCertificateHolderAuthorization().getOid();
    }

    public Flags getHolderAuthorizationRights() throws IOException {
        return new Flags(this.certificateBody.getCertificateHolderAuthorization().getAccessRights() & 31);
    }

    public int getHolderAuthorizationRole() throws IOException {
        return this.certificateBody.getCertificateHolderAuthorization().getAccessRights() & 192;
    }

    public CertificateHolderReference getHolderReference() throws IOException {
        return this.certificateBody.getCertificateHolderReference();
    }

    public int getRole() throws IOException {
        return this.certificateBody.getCertificateHolderAuthorization().getAccessRights();
    }

    public byte[] getSignature() {
        return Arrays.clone(this.signature);
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(this.certificateBody);
        try {
            aSN1EncodableVector.add(new DERApplicationSpecific(false, 55, (ASN1Encodable) new DEROctetString(this.signature)));
            return new DERApplicationSpecific(33, aSN1EncodableVector);
        } catch (IOException e) {
            throw new IllegalStateException("unable to convert signature!");
        }
    }
}
