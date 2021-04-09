package org.bouncycastle.cert.crmf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.KeyWrapper;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfoBuilder;
import org.bouncycastle.util.Strings;

public class EncryptedValueBuilder {
    private OutputEncryptor encryptor;
    private EncryptedValuePadder padder;
    private KeyWrapper wrapper;

    public EncryptedValueBuilder(KeyWrapper keyWrapper, OutputEncryptor outputEncryptor) {
        this(keyWrapper, outputEncryptor, null);
    }

    public EncryptedValueBuilder(KeyWrapper keyWrapper, OutputEncryptor outputEncryptor, EncryptedValuePadder encryptedValuePadder) {
        this.wrapper = keyWrapper;
        this.encryptor = outputEncryptor;
        this.padder = encryptedValuePadder;
    }

    private EncryptedValue encryptData(byte[] bArr) throws CRMFException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStream outputStream = this.encryptor.getOutputStream(byteArrayOutputStream);
        try {
            outputStream.write(bArr);
            outputStream.close();
            AlgorithmIdentifier algorithmIdentifier = this.encryptor.getAlgorithmIdentifier();
            try {
                this.wrapper.generateWrappedKey(this.encryptor.getKey());
                EncryptedValue encryptedValue = new EncryptedValue(null, algorithmIdentifier, new DERBitString(this.wrapper.generateWrappedKey(this.encryptor.getKey())), this.wrapper.getAlgorithmIdentifier(), null, new DERBitString(byteArrayOutputStream.toByteArray()));
                return encryptedValue;
            } catch (OperatorException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("cannot wrap key: ");
                sb.append(e.getMessage());
                throw new CRMFException(sb.toString(), e);
            }
        } catch (IOException e2) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("cannot process data: ");
            sb2.append(e2.getMessage());
            throw new CRMFException(sb2.toString(), e2);
        }
    }

    private byte[] padData(byte[] bArr) {
        EncryptedValuePadder encryptedValuePadder = this.padder;
        return encryptedValuePadder != null ? encryptedValuePadder.getPaddedData(bArr) : bArr;
    }

    public EncryptedValue build(PrivateKeyInfo privateKeyInfo) throws CRMFException {
        PKCS8EncryptedPrivateKeyInfoBuilder pKCS8EncryptedPrivateKeyInfoBuilder = new PKCS8EncryptedPrivateKeyInfoBuilder(privateKeyInfo);
        AlgorithmIdentifier privateKeyAlgorithm = privateKeyInfo.getPrivateKeyAlgorithm();
        AlgorithmIdentifier algorithmIdentifier = this.encryptor.getAlgorithmIdentifier();
        try {
            EncryptedValue encryptedValue = new EncryptedValue(privateKeyAlgorithm, algorithmIdentifier, new DERBitString(this.wrapper.generateWrappedKey(this.encryptor.getKey())), this.wrapper.getAlgorithmIdentifier(), null, new DERBitString(pKCS8EncryptedPrivateKeyInfoBuilder.build(this.encryptor).getEncryptedData()));
            return encryptedValue;
        } catch (IllegalStateException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("cannot encode key: ");
            sb.append(e.getMessage());
            throw new CRMFException(sb.toString(), e);
        } catch (OperatorException e2) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("cannot wrap key: ");
            sb2.append(e2.getMessage());
            throw new CRMFException(sb2.toString(), e2);
        }
    }

    public EncryptedValue build(X509CertificateHolder x509CertificateHolder) throws CRMFException {
        try {
            return encryptData(padData(x509CertificateHolder.getEncoded()));
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("cannot encode certificate: ");
            sb.append(e.getMessage());
            throw new CRMFException(sb.toString(), e);
        }
    }

    public EncryptedValue build(char[] cArr) throws CRMFException {
        return encryptData(padData(Strings.toUTF8ByteArray(cArr)));
    }
}
