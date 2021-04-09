package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EncryptedData;
import org.bouncycastle.operator.InputDecryptorProvider;

public class CMSEncryptedData {
    private ContentInfo contentInfo;
    private EncryptedData encryptedData;

    public CMSEncryptedData(ContentInfo contentInfo2) {
        this.contentInfo = contentInfo2;
        this.encryptedData = EncryptedData.getInstance(contentInfo2.getContent());
    }

    public byte[] getContent(InputDecryptorProvider inputDecryptorProvider) throws CMSException {
        try {
            return CMSUtils.streamToByteArray(getContentStream(inputDecryptorProvider).getContentStream());
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("unable to parse internal stream: ");
            sb.append(e.getMessage());
            throw new CMSException(sb.toString(), e);
        }
    }

    public CMSTypedStream getContentStream(InputDecryptorProvider inputDecryptorProvider) throws CMSException {
        try {
            EncryptedContentInfo encryptedContentInfo = this.encryptedData.getEncryptedContentInfo();
            return new CMSTypedStream(encryptedContentInfo.getContentType(), inputDecryptorProvider.get(encryptedContentInfo.getContentEncryptionAlgorithm()).getInputStream(new ByteArrayInputStream(encryptedContentInfo.getEncryptedContent().getOctets())));
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("unable to create stream: ");
            sb.append(e.getMessage());
            throw new CMSException(sb.toString(), e);
        }
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }
}
