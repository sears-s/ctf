package org.bouncycastle.mime.smime;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.OriginatorInformation;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeIOException;
import org.bouncycastle.mime.MimeWriter;
import org.bouncycastle.mime.encoding.Base64OutputStream;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.Strings;
import org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp.Base64;

public class SMIMEEnvelopedWriter extends MimeWriter {
    private final String contentTransferEncoding;
    private final CMSEnvelopedDataStreamGenerator envGen;
    private final OutputStream mimeOut;
    private final OutputEncryptor outEnc;

    public static class Builder {
        private static final String[] stdHeaders = {"Content-Type", "Content-Disposition", "Content-Transfer-Encoding", "Content-Description"};
        private static final String[] stdValues = {"application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data", "attachment; filename=\"smime.p7m\"", Base64.ELEMENT, "S/MIME Encrypted Message"};
        String contentTransferEncoding = Base64.ELEMENT;
        /* access modifiers changed from: private */
        public final CMSEnvelopedDataStreamGenerator envGen = new CMSEnvelopedDataStreamGenerator();
        /* access modifiers changed from: private */
        public final Map<String, String> headers = new LinkedHashMap();

        public Builder() {
            int i = 0;
            while (true) {
                String[] strArr = stdHeaders;
                if (i != strArr.length) {
                    this.headers.put(strArr[i], stdValues[i]);
                    i++;
                } else {
                    return;
                }
            }
        }

        public Builder addRecipientInfoGenerator(RecipientInfoGenerator recipientInfoGenerator) {
            this.envGen.addRecipientInfoGenerator(recipientInfoGenerator);
            return this;
        }

        public SMIMEEnvelopedWriter build(OutputStream outputStream, OutputEncryptor outputEncryptor) {
            return new SMIMEEnvelopedWriter(this, outputEncryptor, outputStream);
        }

        public Builder setBufferSize(int i) {
            this.envGen.setBufferSize(i);
            return this;
        }

        public Builder setOriginatorInfo(OriginatorInformation originatorInformation) {
            this.envGen.setOriginatorInfo(originatorInformation);
            return this;
        }

        public Builder setUnprotectedAttributeGenerator(CMSAttributeTableGenerator cMSAttributeTableGenerator) {
            this.envGen.setUnprotectedAttributeGenerator(cMSAttributeTableGenerator);
            return this;
        }

        public Builder withHeader(String str, String str2) {
            this.headers.put(str, str2);
            return this;
        }
    }

    private class ContentOutputStream extends OutputStream {
        private final OutputStream backing;
        private final OutputStream main;

        ContentOutputStream(OutputStream outputStream, OutputStream outputStream2) {
            this.main = outputStream;
            this.backing = outputStream2;
        }

        public void close() throws IOException {
            this.main.close();
            OutputStream outputStream = this.backing;
            if (outputStream != null) {
                outputStream.close();
            }
        }

        public void write(int i) throws IOException {
            this.main.write(i);
        }
    }

    private SMIMEEnvelopedWriter(Builder builder, OutputEncryptor outputEncryptor, OutputStream outputStream) {
        super(new Headers(mapToLines(builder.headers), builder.contentTransferEncoding));
        this.envGen = builder.envGen;
        this.contentTransferEncoding = builder.contentTransferEncoding;
        this.outEnc = outputEncryptor;
        this.mimeOut = outputStream;
    }

    public OutputStream getContentStream() throws IOException {
        this.headers.dumpHeaders(this.mimeOut);
        this.mimeOut.write(Strings.toByteArray("\r\n"));
        try {
            if (!Base64.ELEMENT.equals(this.contentTransferEncoding)) {
                return new ContentOutputStream(this.envGen.open(SMimeUtils.createUnclosable(this.mimeOut), this.outEnc), null);
            }
            Base64OutputStream base64OutputStream = new Base64OutputStream(this.mimeOut);
            return new ContentOutputStream(this.envGen.open(SMimeUtils.createUnclosable(base64OutputStream), this.outEnc), base64OutputStream);
        } catch (CMSException e) {
            throw new MimeIOException(e.getMessage(), e);
        }
    }
}
