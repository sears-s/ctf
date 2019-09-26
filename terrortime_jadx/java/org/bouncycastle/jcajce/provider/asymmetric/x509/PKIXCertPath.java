package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.security.NoSuchProviderException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

public class PKIXCertPath extends CertPath {
    static final List certPathEncodings;
    private List certificates;
    private final JcaJceHelper helper = new BCJcaJceHelper();

    static {
        ArrayList arrayList = new ArrayList();
        arrayList.add("PkiPath");
        arrayList.add("PEM");
        arrayList.add("PKCS7");
        certPathEncodings = Collections.unmodifiableList(arrayList);
    }

    PKIXCertPath(InputStream inputStream, String str) throws CertificateException {
        String str2 = "X.509";
        super(str2);
        try {
            if (!str.equalsIgnoreCase("PkiPath")) {
                if (!str.equalsIgnoreCase("PKCS7")) {
                    if (!str.equalsIgnoreCase("PEM")) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("unsupported encoding: ");
                        sb.append(str);
                        throw new CertificateException(sb.toString());
                    }
                }
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                this.certificates = new ArrayList();
                CertificateFactory createCertificateFactory = this.helper.createCertificateFactory(str2);
                while (true) {
                    Certificate generateCertificate = createCertificateFactory.generateCertificate(bufferedInputStream);
                    if (generateCertificate == null) {
                        break;
                    }
                    this.certificates.add(generateCertificate);
                }
            } else {
                ASN1Primitive readObject = new ASN1InputStream(inputStream).readObject();
                if (readObject instanceof ASN1Sequence) {
                    Enumeration objects = ((ASN1Sequence) readObject).getObjects();
                    this.certificates = new ArrayList();
                    CertificateFactory createCertificateFactory2 = this.helper.createCertificateFactory(str2);
                    while (objects.hasMoreElements()) {
                        this.certificates.add(0, createCertificateFactory2.generateCertificate(new ByteArrayInputStream(((ASN1Encodable) objects.nextElement()).toASN1Primitive().getEncoded(ASN1Encoding.DER))));
                    }
                } else {
                    throw new CertificateException("input stream does not contain a ASN1 SEQUENCE while reading PkiPath encoded data to load CertPath");
                }
            }
            this.certificates = sortCerts(this.certificates);
        } catch (IOException e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("IOException throw while decoding CertPath:\n");
            sb2.append(e.toString());
            throw new CertificateException(sb2.toString());
        } catch (NoSuchProviderException e2) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("BouncyCastle provider not found while trying to get a CertificateFactory:\n");
            sb3.append(e2.toString());
            throw new CertificateException(sb3.toString());
        }
    }

    PKIXCertPath(List list) {
        super("X.509");
        this.certificates = sortCerts(new ArrayList(list));
    }

    private List sortCerts(List list) {
        boolean z;
        boolean z2;
        if (list.size() < 2) {
            return list;
        }
        X500Principal issuerX500Principal = ((X509Certificate) list.get(0)).getIssuerX500Principal();
        int i = 1;
        while (true) {
            if (i != list.size()) {
                if (!issuerX500Principal.equals(((X509Certificate) list.get(i)).getSubjectX500Principal())) {
                    z = false;
                    break;
                }
                issuerX500Principal = ((X509Certificate) list.get(i)).getIssuerX500Principal();
                i++;
            } else {
                z = true;
                break;
            }
        }
        if (z) {
            return list;
        }
        ArrayList arrayList = new ArrayList(list.size());
        ArrayList arrayList2 = new ArrayList(list);
        for (int i2 = 0; i2 < list.size(); i2++) {
            X509Certificate x509Certificate = (X509Certificate) list.get(i2);
            X500Principal subjectX500Principal = x509Certificate.getSubjectX500Principal();
            int i3 = 0;
            while (true) {
                if (i3 == list.size()) {
                    z2 = false;
                    break;
                } else if (((X509Certificate) list.get(i3)).getIssuerX500Principal().equals(subjectX500Principal)) {
                    z2 = true;
                    break;
                } else {
                    i3++;
                }
            }
            if (!z2) {
                arrayList.add(x509Certificate);
                list.remove(i2);
            }
        }
        if (arrayList.size() > 1) {
            return arrayList2;
        }
        for (int i4 = 0; i4 != arrayList.size(); i4++) {
            X500Principal issuerX500Principal2 = ((X509Certificate) arrayList.get(i4)).getIssuerX500Principal();
            int i5 = 0;
            while (true) {
                if (i5 >= list.size()) {
                    break;
                }
                X509Certificate x509Certificate2 = (X509Certificate) list.get(i5);
                if (issuerX500Principal2.equals(x509Certificate2.getSubjectX500Principal())) {
                    arrayList.add(x509Certificate2);
                    list.remove(i5);
                    break;
                }
                i5++;
            }
        }
        return list.size() > 0 ? arrayList2 : arrayList;
    }

    private ASN1Primitive toASN1Object(X509Certificate x509Certificate) throws CertificateEncodingException {
        try {
            return new ASN1InputStream(x509Certificate.getEncoded()).readObject();
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Exception while encoding certificate: ");
            sb.append(e.toString());
            throw new CertificateEncodingException(sb.toString());
        }
    }

    private byte[] toDEREncoded(ASN1Encodable aSN1Encodable) throws CertificateEncodingException {
        try {
            return aSN1Encodable.toASN1Primitive().getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Exception thrown: ");
            sb.append(e);
            throw new CertificateEncodingException(sb.toString());
        }
    }

    public List getCertificates() {
        return Collections.unmodifiableList(new ArrayList(this.certificates));
    }

    public byte[] getEncoded() throws CertificateEncodingException {
        Iterator encodings = getEncodings();
        if (encodings.hasNext()) {
            Object next = encodings.next();
            if (next instanceof String) {
                return getEncoded((String) next);
            }
        }
        return null;
    }

    public byte[] getEncoded(String str) throws CertificateEncodingException {
        if (str.equalsIgnoreCase("PkiPath")) {
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            List list = this.certificates;
            ListIterator listIterator = list.listIterator(list.size());
            while (listIterator.hasPrevious()) {
                aSN1EncodableVector.add(toASN1Object((X509Certificate) listIterator.previous()));
            }
            return toDEREncoded(new DERSequence(aSN1EncodableVector));
        }
        int i = 0;
        if (str.equalsIgnoreCase("PKCS7")) {
            ContentInfo contentInfo = new ContentInfo(PKCSObjectIdentifiers.data, null);
            ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
            while (i != this.certificates.size()) {
                aSN1EncodableVector2.add(toASN1Object((X509Certificate) this.certificates.get(i)));
                i++;
            }
            SignedData signedData = new SignedData(new ASN1Integer(1), new DERSet(), contentInfo, new DERSet(aSN1EncodableVector2), null, new DERSet());
            return toDEREncoded(new ContentInfo(PKCSObjectIdentifiers.signedData, signedData));
        } else if (str.equalsIgnoreCase("PEM")) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PemWriter pemWriter = new PemWriter(new OutputStreamWriter(byteArrayOutputStream));
            while (i != this.certificates.size()) {
                try {
                    pemWriter.writeObject(new PemObject("CERTIFICATE", ((X509Certificate) this.certificates.get(i)).getEncoded()));
                    i++;
                } catch (Exception e) {
                    throw new CertificateEncodingException("can't encode certificate for PEM encoded path");
                }
            }
            pemWriter.close();
            return byteArrayOutputStream.toByteArray();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("unsupported encoding: ");
            sb.append(str);
            throw new CertificateEncodingException(sb.toString());
        }
    }

    public Iterator getEncodings() {
        return certPathEncodings.iterator();
    }
}
