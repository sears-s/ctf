package org.bouncycastle.cert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.util.Encodable;

public class X509CertificateHolder implements Encodable, Serializable {
    private static final long serialVersionUID = 20170722001L;
    private transient Extensions extensions;
    private transient Certificate x509Certificate;

    public X509CertificateHolder(Certificate certificate) {
        init(certificate);
    }

    public X509CertificateHolder(byte[] bArr) throws IOException {
        this(parseBytes(bArr));
    }

    private void init(Certificate certificate) {
        this.x509Certificate = certificate;
        this.extensions = certificate.getTBSCertificate().getExtensions();
    }

    private static Certificate parseBytes(byte[] bArr) throws IOException {
        String str = "malformed data: ";
        try {
            return Certificate.getInstance(CertUtils.parseNonEmptyASN1(bArr));
        } catch (ClassCastException e) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(e.getMessage());
            throw new CertIOException(sb.toString(), e);
        } catch (IllegalArgumentException e2) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(e2.getMessage());
            throw new CertIOException(sb2.toString(), e2);
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        init(Certificate.getInstance(objectInputStream.readObject()));
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(getEncoded());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof X509CertificateHolder)) {
            return false;
        }
        return this.x509Certificate.equals(((X509CertificateHolder) obj).x509Certificate);
    }

    public Set getCriticalExtensionOIDs() {
        return CertUtils.getCriticalExtensionOIDs(this.extensions);
    }

    public byte[] getEncoded() throws IOException {
        return this.x509Certificate.getEncoded();
    }

    public Extension getExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        Extensions extensions2 = this.extensions;
        if (extensions2 != null) {
            return extensions2.getExtension(aSN1ObjectIdentifier);
        }
        return null;
    }

    public List getExtensionOIDs() {
        return CertUtils.getExtensionOIDs(this.extensions);
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    public X500Name getIssuer() {
        return X500Name.getInstance(this.x509Certificate.getIssuer());
    }

    public Set getNonCriticalExtensionOIDs() {
        return CertUtils.getNonCriticalExtensionOIDs(this.extensions);
    }

    public Date getNotAfter() {
        return this.x509Certificate.getEndDate().getDate();
    }

    public Date getNotBefore() {
        return this.x509Certificate.getStartDate().getDate();
    }

    public BigInteger getSerialNumber() {
        return this.x509Certificate.getSerialNumber().getValue();
    }

    public byte[] getSignature() {
        return this.x509Certificate.getSignature().getOctets();
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.x509Certificate.getSignatureAlgorithm();
    }

    public X500Name getSubject() {
        return X500Name.getInstance(this.x509Certificate.getSubject());
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.x509Certificate.getSubjectPublicKeyInfo();
    }

    public int getVersion() {
        return this.x509Certificate.getVersionNumber();
    }

    public int getVersionNumber() {
        return this.x509Certificate.getVersionNumber();
    }

    public boolean hasExtensions() {
        return this.extensions != null;
    }

    public int hashCode() {
        return this.x509Certificate.hashCode();
    }

    public boolean isSignatureValid(ContentVerifierProvider contentVerifierProvider) throws CertException {
        TBSCertificate tBSCertificate = this.x509Certificate.getTBSCertificate();
        if (CertUtils.isAlgIdEqual(tBSCertificate.getSignature(), this.x509Certificate.getSignatureAlgorithm())) {
            try {
                ContentVerifier contentVerifier = contentVerifierProvider.get(tBSCertificate.getSignature());
                OutputStream outputStream = contentVerifier.getOutputStream();
                new DEROutputStream(outputStream).writeObject(tBSCertificate);
                outputStream.close();
                return contentVerifier.verify(getSignature());
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("unable to process signature: ");
                sb.append(e.getMessage());
                throw new CertException(sb.toString(), e);
            }
        } else {
            throw new CertException("signature invalid - algorithm identifier mismatch");
        }
    }

    public boolean isValidOn(Date date) {
        return !date.before(this.x509Certificate.getStartDate().getDate()) && !date.after(this.x509Certificate.getEndDate().getDate());
    }

    public Certificate toASN1Structure() {
        return this.x509Certificate;
    }
}
