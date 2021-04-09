package org.bouncycastle.eac;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.eac.CVCertificateRequest;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.eac.operator.EACSignatureVerifier;

public class EACCertificateRequestHolder {
    private CVCertificateRequest request;

    public EACCertificateRequestHolder(CVCertificateRequest cVCertificateRequest) {
        this.request = cVCertificateRequest;
    }

    public EACCertificateRequestHolder(byte[] bArr) throws IOException {
        this(parseBytes(bArr));
    }

    private static CVCertificateRequest parseBytes(byte[] bArr) throws IOException {
        String str = "malformed data: ";
        try {
            return CVCertificateRequest.getInstance(bArr);
        } catch (ClassCastException e) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(e.getMessage());
            throw new EACIOException(sb.toString(), e);
        } catch (IllegalArgumentException e2) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(e2.getMessage());
            throw new EACIOException(sb2.toString(), e2);
        } catch (ASN1ParsingException e3) {
            if (e3.getCause() instanceof IOException) {
                throw ((IOException) e3.getCause());
            }
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            sb3.append(e3.getMessage());
            throw new EACIOException(sb3.toString(), e3);
        }
    }

    public PublicKeyDataObject getPublicKeyDataObject() {
        return this.request.getPublicKey();
    }

    public boolean isInnerSignatureValid(EACSignatureVerifier eACSignatureVerifier) throws EACException {
        try {
            OutputStream outputStream = eACSignatureVerifier.getOutputStream();
            outputStream.write(this.request.getCertificateBody().getEncoded(ASN1Encoding.DER));
            outputStream.close();
            return eACSignatureVerifier.verify(this.request.getInnerSignature());
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("unable to process signature: ");
            sb.append(e.getMessage());
            throw new EACException(sb.toString(), e);
        }
    }

    public CVCertificateRequest toASN1Structure() {
        return this.request;
    }
}
