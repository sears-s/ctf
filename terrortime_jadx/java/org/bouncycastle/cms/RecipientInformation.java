package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.io.Streams;

public abstract class RecipientInformation {
    private AuthAttributesProvider additionalData;
    protected AlgorithmIdentifier keyEncAlg;
    protected AlgorithmIdentifier messageAlgorithm;
    private RecipientOperator operator;
    private byte[] resultMac;
    protected RecipientId rid;
    protected CMSSecureReadable secureReadable;

    RecipientInformation(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, CMSSecureReadable cMSSecureReadable, AuthAttributesProvider authAttributesProvider) {
        this.keyEncAlg = algorithmIdentifier;
        this.messageAlgorithm = algorithmIdentifier2;
        this.secureReadable = cMSSecureReadable;
        this.additionalData = authAttributesProvider;
    }

    private byte[] encodeObj(ASN1Encodable aSN1Encodable) throws IOException {
        if (aSN1Encodable != null) {
            return aSN1Encodable.toASN1Primitive().getEncoded();
        }
        return null;
    }

    public byte[] getContent(Recipient recipient) throws CMSException {
        try {
            return CMSUtils.streamToByteArray(getContentStream(recipient).getContentStream());
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("unable to parse internal stream: ");
            sb.append(e.getMessage());
            throw new CMSException(sb.toString(), e);
        }
    }

    public byte[] getContentDigest() {
        CMSSecureReadable cMSSecureReadable = this.secureReadable;
        if (cMSSecureReadable instanceof CMSDigestAuthenticatedSecureReadable) {
            return ((CMSDigestAuthenticatedSecureReadable) cMSSecureReadable).getDigest();
        }
        return null;
    }

    public CMSTypedStream getContentStream(Recipient recipient) throws CMSException, IOException {
        this.operator = getRecipientOperator(recipient);
        return this.additionalData != null ? new CMSTypedStream(this.secureReadable.getInputStream()) : new CMSTypedStream(this.operator.getInputStream(this.secureReadable.getInputStream()));
    }

    public String getKeyEncryptionAlgOID() {
        return this.keyEncAlg.getAlgorithm().getId();
    }

    public byte[] getKeyEncryptionAlgParams() {
        try {
            return encodeObj(this.keyEncAlg.getParameters());
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("exception getting encryption parameters ");
            sb.append(e);
            throw new RuntimeException(sb.toString());
        }
    }

    public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
        return this.keyEncAlg;
    }

    public byte[] getMac() {
        if (this.resultMac == null && this.operator.isMacBased()) {
            AuthAttributesProvider authAttributesProvider = this.additionalData;
            if (authAttributesProvider != null) {
                try {
                    Streams.drain(this.operator.getInputStream(new ByteArrayInputStream(authAttributesProvider.getAuthAttributes().getEncoded(ASN1Encoding.DER))));
                } catch (IOException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("unable to drain input: ");
                    sb.append(e.getMessage());
                    throw new IllegalStateException(sb.toString());
                }
            }
            this.resultMac = this.operator.getMac();
        }
        return this.resultMac;
    }

    public RecipientId getRID() {
        return this.rid;
    }

    /* access modifiers changed from: protected */
    public abstract RecipientOperator getRecipientOperator(Recipient recipient) throws CMSException, IOException;
}
