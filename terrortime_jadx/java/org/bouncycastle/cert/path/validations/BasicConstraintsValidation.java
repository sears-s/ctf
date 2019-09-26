package org.bouncycastle.cert.path.validations;

import java.math.BigInteger;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.util.Memoable;

public class BasicConstraintsValidation implements CertPathValidation {
    private BasicConstraints bc;
    private boolean isMandatory;
    private BigInteger maxPathLength;
    private int pathLengthRemaining;

    public BasicConstraintsValidation() {
        this(true);
    }

    public BasicConstraintsValidation(boolean z) {
        this.isMandatory = z;
    }

    public Memoable copy() {
        BasicConstraintsValidation basicConstraintsValidation = new BasicConstraintsValidation(this.isMandatory);
        basicConstraintsValidation.bc = this.bc;
        basicConstraintsValidation.pathLengthRemaining = this.pathLengthRemaining;
        return basicConstraintsValidation;
    }

    public void reset(Memoable memoable) {
        BasicConstraintsValidation basicConstraintsValidation = (BasicConstraintsValidation) memoable;
        this.isMandatory = basicConstraintsValidation.isMandatory;
        this.bc = basicConstraintsValidation.bc;
        this.pathLengthRemaining = basicConstraintsValidation.pathLengthRemaining;
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x0062  */
    /* JADX WARNING: Removed duplicated region for block: B:32:? A[ADDED_TO_REGION, ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void validate(org.bouncycastle.cert.path.CertPathValidationContext r2, org.bouncycastle.cert.X509CertificateHolder r3) throws org.bouncycastle.cert.path.CertPathValidationException {
        /*
            r1 = this;
            java.math.BigInteger r0 = r1.maxPathLength
            if (r0 == 0) goto L_0x0011
            int r0 = r1.pathLengthRemaining
            if (r0 < 0) goto L_0x0009
            goto L_0x0011
        L_0x0009:
            org.bouncycastle.cert.path.CertPathValidationException r2 = new org.bouncycastle.cert.path.CertPathValidationException
            java.lang.String r3 = "BasicConstraints path length exceeded"
            r2.<init>(r3)
            throw r2
        L_0x0011:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r0 = org.bouncycastle.asn1.x509.Extension.basicConstraints
            r2.addHandledExtension(r0)
            org.bouncycastle.asn1.x509.Extensions r2 = r3.getExtensions()
            org.bouncycastle.asn1.x509.BasicConstraints r2 = org.bouncycastle.asn1.x509.BasicConstraints.fromExtensions(r2)
            if (r2 == 0) goto L_0x0054
            org.bouncycastle.asn1.x509.BasicConstraints r3 = r1.bc
            if (r3 == 0) goto L_0x003d
            boolean r3 = r2.isCA()
            if (r3 == 0) goto L_0x005e
            java.math.BigInteger r3 = r2.getPathLenConstraint()
            if (r3 == 0) goto L_0x005e
            int r3 = r3.intValue()
            int r0 = r1.pathLengthRemaining
            if (r3 >= r0) goto L_0x005e
            r1.pathLengthRemaining = r3
            r1.bc = r2
            goto L_0x005e
        L_0x003d:
            r1.bc = r2
            boolean r3 = r2.isCA()
            if (r3 == 0) goto L_0x005e
            java.math.BigInteger r2 = r2.getPathLenConstraint()
            r1.maxPathLength = r2
            java.math.BigInteger r2 = r1.maxPathLength
            if (r2 == 0) goto L_0x005e
            int r2 = r2.intValue()
            goto L_0x005c
        L_0x0054:
            org.bouncycastle.asn1.x509.BasicConstraints r2 = r1.bc
            if (r2 == 0) goto L_0x005e
            int r2 = r1.pathLengthRemaining
            int r2 = r2 + -1
        L_0x005c:
            r1.pathLengthRemaining = r2
        L_0x005e:
            boolean r2 = r1.isMandatory
            if (r2 == 0) goto L_0x006f
            org.bouncycastle.asn1.x509.BasicConstraints r2 = r1.bc
            if (r2 == 0) goto L_0x0067
            goto L_0x006f
        L_0x0067:
            org.bouncycastle.cert.path.CertPathValidationException r2 = new org.bouncycastle.cert.path.CertPathValidationException
            java.lang.String r3 = "BasicConstraints not present in path"
            r2.<init>(r3)
            throw r2
        L_0x006f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.cert.path.validations.BasicConstraintsValidation.validate(org.bouncycastle.cert.path.CertPathValidationContext, org.bouncycastle.cert.X509CertificateHolder):void");
    }
}
