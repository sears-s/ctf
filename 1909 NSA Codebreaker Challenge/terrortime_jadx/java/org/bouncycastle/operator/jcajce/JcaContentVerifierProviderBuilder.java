package org.bouncycastle.operator.jcajce;

import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jcajce.io.OutputStreamFactory;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.RawContentVerifier;
import org.bouncycastle.operator.RuntimeOperatorException;

public class JcaContentVerifierProviderBuilder {
    /* access modifiers changed from: private */
    public OperatorHelper helper = new OperatorHelper(new DefaultJcaJceHelper());

    private class RawSigVerifier extends SigVerifier implements RawContentVerifier {
        private Signature rawSignature;

        RawSigVerifier(AlgorithmIdentifier algorithmIdentifier, Signature signature, Signature signature2) {
            super(algorithmIdentifier, signature);
            this.rawSignature = signature2;
        }

        public boolean verify(byte[] bArr) {
            try {
                return super.verify(bArr);
            } finally {
                try {
                    this.rawSignature.verify(bArr);
                } catch (Exception e) {
                }
            }
        }

        public boolean verify(byte[] bArr, byte[] bArr2) {
            try {
                this.rawSignature.update(bArr);
                boolean verify = this.rawSignature.verify(bArr2);
                try {
                    this.rawSignature.verify(bArr2);
                } catch (Exception e) {
                }
                return verify;
            } catch (SignatureException e2) {
                StringBuilder sb = new StringBuilder();
                sb.append("exception obtaining raw signature: ");
                sb.append(e2.getMessage());
                throw new RuntimeOperatorException(sb.toString(), e2);
            } catch (Throwable th) {
                try {
                    this.rawSignature.verify(bArr2);
                } catch (Exception e3) {
                }
                throw th;
            }
        }
    }

    private class SigVerifier implements ContentVerifier {
        private final AlgorithmIdentifier algorithm;
        private final Signature signature;
        protected final OutputStream stream;

        SigVerifier(AlgorithmIdentifier algorithmIdentifier, Signature signature2) {
            this.algorithm = algorithmIdentifier;
            this.signature = signature2;
            this.stream = OutputStreamFactory.createStream(signature2);
        }

        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithm;
        }

        public OutputStream getOutputStream() {
            OutputStream outputStream = this.stream;
            if (outputStream != null) {
                return outputStream;
            }
            throw new IllegalStateException("verifier not initialised");
        }

        public boolean verify(byte[] bArr) {
            try {
                return this.signature.verify(bArr);
            } catch (SignatureException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("exception obtaining signature: ");
                sb.append(e.getMessage());
                throw new RuntimeOperatorException(sb.toString(), e);
            }
        }
    }

    /* access modifiers changed from: private */
    public Signature createRawSig(AlgorithmIdentifier algorithmIdentifier, PublicKey publicKey) {
        try {
            Signature createRawSignature = this.helper.createRawSignature(algorithmIdentifier);
            if (createRawSignature == null) {
                return createRawSignature;
            }
            createRawSignature.initVerify(publicKey);
            return createRawSignature;
        } catch (Exception e) {
            return null;
        }
    }

    /* access modifiers changed from: private */
    public Signature createSignature(AlgorithmIdentifier algorithmIdentifier, PublicKey publicKey) throws OperatorCreationException {
        try {
            Signature createSignature = this.helper.createSignature(algorithmIdentifier);
            createSignature.initVerify(publicKey);
            return createSignature;
        } catch (GeneralSecurityException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("exception on setup: ");
            sb.append(e);
            throw new OperatorCreationException(sb.toString(), e);
        }
    }

    public ContentVerifierProvider build(final PublicKey publicKey) throws OperatorCreationException {
        return new ContentVerifierProvider() {
            public ContentVerifier get(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                Signature access$200 = JcaContentVerifierProviderBuilder.this.createSignature(algorithmIdentifier, publicKey);
                Signature access$100 = JcaContentVerifierProviderBuilder.this.createRawSig(algorithmIdentifier, publicKey);
                return access$100 != null ? new RawSigVerifier(algorithmIdentifier, access$200, access$100) : new SigVerifier(algorithmIdentifier, access$200);
            }

            public X509CertificateHolder getAssociatedCertificate() {
                return null;
            }

            public boolean hasAssociatedCertificate() {
                return false;
            }
        };
    }

    public ContentVerifierProvider build(final X509Certificate x509Certificate) throws OperatorCreationException {
        try {
            final JcaX509CertificateHolder jcaX509CertificateHolder = new JcaX509CertificateHolder(x509Certificate);
            return new ContentVerifierProvider() {
                public ContentVerifier get(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                    try {
                        Signature createSignature = JcaContentVerifierProviderBuilder.this.helper.createSignature(algorithmIdentifier);
                        createSignature.initVerify(x509Certificate.getPublicKey());
                        Signature access$100 = JcaContentVerifierProviderBuilder.this.createRawSig(algorithmIdentifier, x509Certificate.getPublicKey());
                        return access$100 != null ? new RawSigVerifier(algorithmIdentifier, createSignature, access$100) : new SigVerifier(algorithmIdentifier, createSignature);
                    } catch (GeneralSecurityException e) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("exception on setup: ");
                        sb.append(e);
                        throw new OperatorCreationException(sb.toString(), e);
                    }
                }

                public X509CertificateHolder getAssociatedCertificate() {
                    return jcaX509CertificateHolder;
                }

                public boolean hasAssociatedCertificate() {
                    return true;
                }
            };
        } catch (CertificateEncodingException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("cannot process certificate: ");
            sb.append(e.getMessage());
            throw new OperatorCreationException(sb.toString(), e);
        }
    }

    public ContentVerifierProvider build(SubjectPublicKeyInfo subjectPublicKeyInfo) throws OperatorCreationException {
        return build(this.helper.convertPublicKey(subjectPublicKeyInfo));
    }

    public ContentVerifierProvider build(X509CertificateHolder x509CertificateHolder) throws OperatorCreationException, CertificateException {
        return build(this.helper.convertCertificate(x509CertificateHolder));
    }

    public JcaContentVerifierProviderBuilder setProvider(String str) {
        this.helper = new OperatorHelper(new NamedJcaJceHelper(str));
        return this;
    }

    public JcaContentVerifierProviderBuilder setProvider(Provider provider) {
        this.helper = new OperatorHelper(new ProviderJcaJceHelper(provider));
        return this;
    }
}
