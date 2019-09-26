package org.bouncycastle.openssl.jcajce;

import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.jcajce.JcaX509CRLHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.openssl.MiscPEMGenerator;
import org.bouncycastle.openssl.PEMEncryptor;

public class JcaMiscPEMGenerator extends MiscPEMGenerator {
    private String algorithm;
    private Object obj;
    private char[] password;
    private Provider provider;
    private SecureRandom random;

    public JcaMiscPEMGenerator(Object obj2) throws IOException {
        super(convertObject(obj2));
    }

    public JcaMiscPEMGenerator(Object obj2, PEMEncryptor pEMEncryptor) throws IOException {
        super(convertObject(obj2), pEMEncryptor);
    }

    private static Object convertObject(Object obj2) throws IOException {
        String str = "Cannot encode object: ";
        if (obj2 instanceof X509Certificate) {
            try {
                return new JcaX509CertificateHolder((X509Certificate) obj2);
            } catch (CertificateEncodingException e) {
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append(e.toString());
                throw new IllegalArgumentException(sb.toString());
            }
        } else if (obj2 instanceof X509CRL) {
            try {
                return new JcaX509CRLHolder((X509CRL) obj2);
            } catch (CRLException e2) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str);
                sb2.append(e2.toString());
                throw new IllegalArgumentException(sb2.toString());
            }
        } else if (obj2 instanceof KeyPair) {
            return convertObject(((KeyPair) obj2).getPrivate());
        } else {
            if (obj2 instanceof PrivateKey) {
                return PrivateKeyInfo.getInstance(((Key) obj2).getEncoded());
            }
            if (obj2 instanceof PublicKey) {
                obj2 = SubjectPublicKeyInfo.getInstance(((PublicKey) obj2).getEncoded());
            }
            return obj2;
        }
    }
}
