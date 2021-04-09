package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import org.bouncycastle.util.Arrays;

public final class SessionParameters {
    private int cipherSuite;
    private short compressionAlgorithm;
    private byte[] encodedServerExtensions;
    private boolean extendedMasterSecret;
    private byte[] masterSecret;
    private Certificate peerCertificate;
    private byte[] pskIdentity;
    private byte[] srpIdentity;

    public static final class Builder {
        private int cipherSuite = -1;
        private short compressionAlgorithm = -1;
        private byte[] encodedServerExtensions = null;
        private boolean extendedMasterSecret = false;
        private byte[] masterSecret = null;
        private Certificate peerCertificate = null;
        private byte[] pskIdentity = null;
        private byte[] srpIdentity = null;

        private void validate(boolean z, String str) {
            if (!z) {
                StringBuilder sb = new StringBuilder();
                sb.append("Required session parameter '");
                sb.append(str);
                sb.append("' not configured");
                throw new IllegalStateException(sb.toString());
            }
        }

        public SessionParameters build() {
            boolean z = true;
            validate(this.cipherSuite >= 0, "cipherSuite");
            validate(this.compressionAlgorithm >= 0, "compressionAlgorithm");
            if (this.masterSecret == null) {
                z = false;
            }
            validate(z, "masterSecret");
            SessionParameters sessionParameters = new SessionParameters(this.cipherSuite, this.compressionAlgorithm, this.masterSecret, this.peerCertificate, this.pskIdentity, this.srpIdentity, this.encodedServerExtensions, this.extendedMasterSecret);
            return sessionParameters;
        }

        public Builder setCipherSuite(int i) {
            this.cipherSuite = i;
            return this;
        }

        public Builder setCompressionAlgorithm(short s) {
            this.compressionAlgorithm = s;
            return this;
        }

        public Builder setExtendedMasterSecret(boolean z) {
            this.extendedMasterSecret = z;
            return this;
        }

        public Builder setMasterSecret(byte[] bArr) {
            this.masterSecret = bArr;
            return this;
        }

        public Builder setPSKIdentity(byte[] bArr) {
            this.pskIdentity = bArr;
            return this;
        }

        public Builder setPeerCertificate(Certificate certificate) {
            this.peerCertificate = certificate;
            return this;
        }

        public Builder setPskIdentity(byte[] bArr) {
            this.pskIdentity = bArr;
            return this;
        }

        public Builder setSRPIdentity(byte[] bArr) {
            this.srpIdentity = bArr;
            return this;
        }

        public Builder setServerExtensions(Hashtable hashtable) throws IOException {
            if (hashtable == null) {
                this.encodedServerExtensions = null;
            } else {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                TlsProtocol.writeExtensions(byteArrayOutputStream, hashtable);
                this.encodedServerExtensions = byteArrayOutputStream.toByteArray();
            }
            return this;
        }
    }

    private SessionParameters(int i, short s, byte[] bArr, Certificate certificate, byte[] bArr2, byte[] bArr3, byte[] bArr4, boolean z) {
        this.pskIdentity = null;
        this.srpIdentity = null;
        this.cipherSuite = i;
        this.compressionAlgorithm = s;
        this.masterSecret = Arrays.clone(bArr);
        this.peerCertificate = certificate;
        this.pskIdentity = Arrays.clone(bArr2);
        this.srpIdentity = Arrays.clone(bArr3);
        this.encodedServerExtensions = bArr4;
        this.extendedMasterSecret = z;
    }

    public void clear() {
        byte[] bArr = this.masterSecret;
        if (bArr != null) {
            Arrays.fill(bArr, 0);
        }
    }

    public SessionParameters copy() {
        SessionParameters sessionParameters = new SessionParameters(this.cipherSuite, this.compressionAlgorithm, this.masterSecret, this.peerCertificate, this.pskIdentity, this.srpIdentity, this.encodedServerExtensions, this.extendedMasterSecret);
        return sessionParameters;
    }

    public int getCipherSuite() {
        return this.cipherSuite;
    }

    public short getCompressionAlgorithm() {
        return this.compressionAlgorithm;
    }

    public byte[] getMasterSecret() {
        return this.masterSecret;
    }

    public byte[] getPSKIdentity() {
        return this.pskIdentity;
    }

    public Certificate getPeerCertificate() {
        return this.peerCertificate;
    }

    public byte[] getPskIdentity() {
        return this.pskIdentity;
    }

    public byte[] getSRPIdentity() {
        return this.srpIdentity;
    }

    public boolean isExtendedMasterSecret() {
        return this.extendedMasterSecret;
    }

    public Hashtable readServerExtensions() throws IOException {
        byte[] bArr = this.encodedServerExtensions;
        if (bArr == null) {
            return null;
        }
        return TlsProtocol.readExtensions(new ByteArrayInputStream(bArr));
    }
}
