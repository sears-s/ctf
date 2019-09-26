package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.KeyTransRecipientInfoGenerator;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyWrapper;
import org.bouncycastle.operator.jcajce.JceKTSKeyWrapper;
import org.bouncycastle.util.encoders.Hex;

public class JceKTSKeyTransRecipientInfoGenerator extends KeyTransRecipientInfoGenerator {
    private static final byte[] ANONYMOUS_SENDER = Hex.decode("0c14416e6f6e796d6f75732053656e64657220202020");

    public JceKTSKeyTransRecipientInfoGenerator(X509Certificate x509Certificate, String str, int i) throws CertificateEncodingException {
        this(x509Certificate, new IssuerAndSerialNumber(new JcaX509CertificateHolder(x509Certificate).toASN1Structure()), str, i);
    }

    private JceKTSKeyTransRecipientInfoGenerator(X509Certificate x509Certificate, IssuerAndSerialNumber issuerAndSerialNumber, String str, int i) throws CertificateEncodingException {
        JceKTSKeyWrapper jceKTSKeyWrapper = new JceKTSKeyWrapper(x509Certificate, str, i, ANONYMOUS_SENDER, getEncodedRecipID(issuerAndSerialNumber));
        super(issuerAndSerialNumber, (AsymmetricKeyWrapper) jceKTSKeyWrapper);
    }

    public JceKTSKeyTransRecipientInfoGenerator(X509Certificate x509Certificate, AlgorithmIdentifier algorithmIdentifier) throws CertificateEncodingException {
        super(new IssuerAndSerialNumber(new JcaX509CertificateHolder(x509Certificate).toASN1Structure()), (AsymmetricKeyWrapper) new JceAsymmetricKeyWrapper(algorithmIdentifier, x509Certificate.getPublicKey()));
    }

    public JceKTSKeyTransRecipientInfoGenerator(byte[] bArr, PublicKey publicKey, String str, int i) {
        JceKTSKeyWrapper jceKTSKeyWrapper = new JceKTSKeyWrapper(publicKey, str, i, ANONYMOUS_SENDER, getEncodedSubKeyId(bArr));
        super(bArr, (AsymmetricKeyWrapper) jceKTSKeyWrapper);
    }

    public JceKTSKeyTransRecipientInfoGenerator(byte[] bArr, AlgorithmIdentifier algorithmIdentifier, PublicKey publicKey) {
        super(bArr, (AsymmetricKeyWrapper) new JceAsymmetricKeyWrapper(algorithmIdentifier, publicKey));
    }

    private static byte[] getEncodedRecipID(IssuerAndSerialNumber issuerAndSerialNumber) throws CertificateEncodingException {
        try {
            return issuerAndSerialNumber.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot process extracted IssuerAndSerialNumber: ");
            sb.append(e.getMessage());
            throw new CertificateEncodingException(sb.toString()) {
                public Throwable getCause() {
                    return e;
                }
            };
        }
    }

    private static byte[] getEncodedSubKeyId(byte[] bArr) {
        try {
            return new DEROctetString(bArr).getEncoded();
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot process subject key identifier: ");
            sb.append(e.getMessage());
            throw new IllegalArgumentException(sb.toString()) {
                public Throwable getCause() {
                    return e;
                }
            };
        }
    }

    public JceKTSKeyTransRecipientInfoGenerator setProvider(String str) {
        ((JceKTSKeyWrapper) this.wrapper).setProvider(str);
        return this;
    }

    public JceKTSKeyTransRecipientInfoGenerator setProvider(Provider provider) {
        ((JceKTSKeyWrapper) this.wrapper).setProvider(provider);
        return this;
    }
}
