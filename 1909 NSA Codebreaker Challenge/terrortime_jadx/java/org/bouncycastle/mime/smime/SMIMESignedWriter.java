package org.bouncycastle.mime.smime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedDataStreamGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeWriter;
import org.bouncycastle.mime.encoding.Base64OutputStream;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.Strings;
import org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp.Base64;

public class SMIMESignedWriter extends MimeWriter {
    public static final Map RFC3851_MICALGS;
    public static final Map RFC5751_MICALGS;
    public static final Map STANDARD_MICALGS = RFC5751_MICALGS;
    /* access modifiers changed from: private */
    public final String boundary;
    private final String contentTransferEncoding;
    private final OutputStream mimeOut;
    private final CMSSignedDataStreamGenerator sigGen;

    public static class Builder {
        private static final String[] detHeaders;
        private static final String[] detValues = {"multipart/signed; protocol=\"application/pkcs7-signature\""};
        private static final String[] encHeaders;
        private static final String[] encValues = {"application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data", "attachment; filename=\"smime.p7m\"", Base64.ELEMENT, "S/MIME Signed Message"};
        String contentTransferEncoding;
        private final boolean encapsulated;
        private final Map<String, String> extraHeaders;
        private final Map micAlgs;
        /* access modifiers changed from: private */
        public final CMSSignedDataStreamGenerator sigGen;

        static {
            String str = "Content-Type";
            detHeaders = new String[]{str};
            encHeaders = new String[]{str, "Content-Disposition", "Content-Transfer-Encoding", "Content-Description"};
        }

        public Builder() {
            this(false);
        }

        public Builder(boolean z) {
            this.sigGen = new CMSSignedDataStreamGenerator();
            this.extraHeaders = new LinkedHashMap();
            this.micAlgs = SMIMESignedWriter.STANDARD_MICALGS;
            this.contentTransferEncoding = Base64.ELEMENT;
            this.encapsulated = z;
        }

        private void addBoundary(StringBuffer stringBuffer, String str) {
            stringBuffer.append(";\r\n\tboundary=\"");
            stringBuffer.append(str);
            stringBuffer.append("\"");
        }

        /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.List, code=java.util.List<org.bouncycastle.asn1.x509.AlgorithmIdentifier>, for r7v0, types: [java.util.List, java.util.List<org.bouncycastle.asn1.x509.AlgorithmIdentifier>] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void addHashHeader(java.lang.StringBuffer r6, java.util.List<org.bouncycastle.asn1.x509.AlgorithmIdentifier> r7) {
            /*
                r5 = this;
                java.util.Iterator r7 = r7.iterator()
                java.util.TreeSet r0 = new java.util.TreeSet
                r0.<init>()
            L_0x0009:
                boolean r1 = r7.hasNext()
                if (r1 == 0) goto L_0x0029
                java.lang.Object r1 = r7.next()
                org.bouncycastle.asn1.x509.AlgorithmIdentifier r1 = (org.bouncycastle.asn1.x509.AlgorithmIdentifier) r1
                java.util.Map r2 = r5.micAlgs
                org.bouncycastle.asn1.ASN1ObjectIdentifier r1 = r1.getAlgorithm()
                java.lang.Object r1 = r2.get(r1)
                java.lang.String r1 = (java.lang.String) r1
                if (r1 != 0) goto L_0x0025
                java.lang.String r1 = "unknown"
            L_0x0025:
                r0.add(r1)
                goto L_0x0009
            L_0x0029:
                java.util.Iterator r7 = r0.iterator()
                r1 = 0
            L_0x002e:
                boolean r2 = r7.hasNext()
                r3 = 1
                if (r2 == 0) goto L_0x0057
                java.lang.Object r2 = r7.next()
                java.lang.String r2 = (java.lang.String) r2
                if (r1 != 0) goto L_0x004c
                int r4 = r0.size()
                if (r4 == r3) goto L_0x0046
                java.lang.String r3 = "; micalg=\""
                goto L_0x0048
            L_0x0046:
                java.lang.String r3 = "; micalg="
            L_0x0048:
                r6.append(r3)
                goto L_0x0051
            L_0x004c:
                r3 = 44
                r6.append(r3)
            L_0x0051:
                r6.append(r2)
                int r1 = r1 + 1
                goto L_0x002e
            L_0x0057:
                if (r1 == 0) goto L_0x0064
                int r7 = r0.size()
                if (r7 == r3) goto L_0x0064
                r7 = 34
                r6.append(r7)
            L_0x0064:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.mime.smime.SMIMESignedWriter.Builder.addHashHeader(java.lang.StringBuffer, java.util.List):void");
        }

        private String generateBoundary() {
            SecureRandom secureRandom = new SecureRandom();
            StringBuilder sb = new StringBuilder();
            sb.append("==");
            sb.append(new BigInteger(180, secureRandom).setBit(CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA384).toString(16));
            sb.append("=");
            return sb.toString();
        }

        public Builder addCertificate(X509CertificateHolder x509CertificateHolder) throws CMSException {
            this.sigGen.addCertificate(x509CertificateHolder);
            return this;
        }

        public Builder addCertificates(Store store) throws CMSException {
            this.sigGen.addCertificates(store);
            return this;
        }

        public Builder addSignerInfoGenerator(SignerInfoGenerator signerInfoGenerator) {
            this.sigGen.addSignerInfoGenerator(signerInfoGenerator);
            return this;
        }

        public SMIMESignedWriter build(OutputStream outputStream) {
            String str;
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            int i = 0;
            if (!this.encapsulated) {
                str = generateBoundary();
                StringBuffer stringBuffer = new StringBuffer(detValues[0]);
                addHashHeader(stringBuffer, this.sigGen.getDigestAlgorithms());
                addBoundary(stringBuffer, str);
                linkedHashMap.put(detHeaders[0], stringBuffer.toString());
                int i2 = 1;
                while (true) {
                    String[] strArr = detHeaders;
                    if (i2 >= strArr.length) {
                        break;
                    }
                    linkedHashMap.put(strArr[i2], detValues[i2]);
                    i2++;
                }
            } else {
                str = null;
                while (true) {
                    String[] strArr2 = encHeaders;
                    if (i == strArr2.length) {
                        break;
                    }
                    linkedHashMap.put(strArr2[i], encValues[i]);
                    i++;
                }
            }
            String str2 = str;
            for (Entry entry : this.extraHeaders.entrySet()) {
                linkedHashMap.put((String) entry.getKey(), (String) entry.getValue());
            }
            SMIMESignedWriter sMIMESignedWriter = new SMIMESignedWriter(this, linkedHashMap, str2, outputStream);
            return sMIMESignedWriter;
        }

        public Builder withHeader(String str, String str2) {
            this.extraHeaders.put(str, str2);
            return this;
        }
    }

    private class ContentOutputStream extends OutputStream {
        private final OutputStream backing;
        private final OutputStream main;
        private final OutputStream sigBase;
        private final ByteArrayOutputStream sigStream;

        ContentOutputStream(OutputStream outputStream, OutputStream outputStream2, ByteArrayOutputStream byteArrayOutputStream, OutputStream outputStream3) {
            this.main = outputStream;
            this.backing = outputStream2;
            this.sigStream = byteArrayOutputStream;
            this.sigBase = outputStream3;
        }

        public void close() throws IOException {
            if (SMIMESignedWriter.this.boundary != null) {
                this.main.close();
                String str = "\r\n--";
                this.backing.write(Strings.toByteArray(str));
                this.backing.write(Strings.toByteArray(SMIMESignedWriter.this.boundary));
                String str2 = "\r\n";
                this.backing.write(Strings.toByteArray(str2));
                this.backing.write(Strings.toByteArray("Content-Type: application/pkcs7-signature; name=\"smime.p7s\"\r\n"));
                this.backing.write(Strings.toByteArray("Content-Transfer-Encoding: base64\r\n"));
                this.backing.write(Strings.toByteArray("Content-Disposition: attachment; filename=\"smime.p7s\"\r\n"));
                this.backing.write(Strings.toByteArray(str2));
                OutputStream outputStream = this.sigBase;
                if (outputStream != null) {
                    outputStream.close();
                }
                this.backing.write(this.sigStream.toByteArray());
                this.backing.write(Strings.toByteArray(str));
                this.backing.write(Strings.toByteArray(SMIMESignedWriter.this.boundary));
                this.backing.write(Strings.toByteArray("--\r\n"));
            }
            OutputStream outputStream2 = this.backing;
            if (outputStream2 != null) {
                outputStream2.close();
            }
        }

        public void write(int i) throws IOException {
            this.main.write(i);
        }
    }

    static {
        HashMap hashMap = new HashMap();
        String str = "md5";
        hashMap.put(CMSAlgorithm.MD5, str);
        hashMap.put(CMSAlgorithm.SHA1, "sha-1");
        hashMap.put(CMSAlgorithm.SHA224, "sha-224");
        hashMap.put(CMSAlgorithm.SHA256, "sha-256");
        hashMap.put(CMSAlgorithm.SHA384, "sha-384");
        hashMap.put(CMSAlgorithm.SHA512, "sha-512");
        String str2 = "gostr3411-94";
        hashMap.put(CMSAlgorithm.GOST3411, str2);
        String str3 = "gostr3411-2012-256";
        hashMap.put(CMSAlgorithm.GOST3411_2012_256, str3);
        String str4 = "gostr3411-2012-512";
        hashMap.put(CMSAlgorithm.GOST3411_2012_512, str4);
        RFC5751_MICALGS = Collections.unmodifiableMap(hashMap);
        HashMap hashMap2 = new HashMap();
        hashMap2.put(CMSAlgorithm.MD5, str);
        hashMap2.put(CMSAlgorithm.SHA1, "sha1");
        hashMap2.put(CMSAlgorithm.SHA224, "sha224");
        hashMap2.put(CMSAlgorithm.SHA256, "sha256");
        hashMap2.put(CMSAlgorithm.SHA384, "sha384");
        hashMap2.put(CMSAlgorithm.SHA512, "sha512");
        hashMap2.put(CMSAlgorithm.GOST3411, str2);
        hashMap2.put(CMSAlgorithm.GOST3411_2012_256, str3);
        hashMap2.put(CMSAlgorithm.GOST3411_2012_512, str4);
        RFC3851_MICALGS = Collections.unmodifiableMap(hashMap2);
    }

    private SMIMESignedWriter(Builder builder, Map<String, String> map, String str, OutputStream outputStream) {
        super(new Headers(mapToLines(map), builder.contentTransferEncoding));
        this.sigGen = builder.sigGen;
        this.contentTransferEncoding = builder.contentTransferEncoding;
        this.boundary = str;
        this.mimeOut = outputStream;
    }

    public OutputStream getContentStream() throws IOException {
        this.headers.dumpHeaders(this.mimeOut);
        String str = "\r\n";
        this.mimeOut.write(Strings.toByteArray(str));
        if (this.boundary == null) {
            return null;
        }
        this.mimeOut.write(Strings.toByteArray("This is an S/MIME signed message\r\n"));
        this.mimeOut.write(Strings.toByteArray("\r\n--"));
        this.mimeOut.write(Strings.toByteArray(this.boundary));
        this.mimeOut.write(Strings.toByteArray(str));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Base64OutputStream base64OutputStream = new Base64OutputStream(byteArrayOutputStream);
        ContentOutputStream contentOutputStream = new ContentOutputStream(this.sigGen.open((OutputStream) base64OutputStream, false, SMimeUtils.createUnclosable(this.mimeOut)), this.mimeOut, byteArrayOutputStream, base64OutputStream);
        return contentOutputStream;
    }
}
