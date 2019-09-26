package org.bouncycastle.cert.selector;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.X509AttributeCertificateHolder;

public class X509AttributeCertificateHolderSelectorBuilder {
    private X509AttributeCertificateHolder attributeCert;
    private Date attributeCertificateValid;
    private AttributeCertificateHolder holder;
    private AttributeCertificateIssuer issuer;
    private BigInteger serialNumber;
    private Collection targetGroups = new HashSet();
    private Collection targetNames = new HashSet();

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Collection, code=java.util.Collection<java.lang.Object>, for r3v0, types: [java.util.Collection<java.lang.Object>, java.util.Collection] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Set extractGeneralNames(java.util.Collection<java.lang.Object> r3) throws java.io.IOException {
        /*
            r2 = this;
            if (r3 == 0) goto L_0x0025
            boolean r0 = r3.isEmpty()
            if (r0 == 0) goto L_0x0009
            goto L_0x0025
        L_0x0009:
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            java.util.Iterator r3 = r3.iterator()
        L_0x0012:
            boolean r1 = r3.hasNext()
            if (r1 == 0) goto L_0x0024
            java.lang.Object r1 = r3.next()
            org.bouncycastle.asn1.x509.GeneralName r1 = org.bouncycastle.asn1.x509.GeneralName.getInstance(r1)
            r0.add(r1)
            goto L_0x0012
        L_0x0024:
            return r0
        L_0x0025:
            java.util.HashSet r3 = new java.util.HashSet
            r3.<init>()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.cert.selector.X509AttributeCertificateHolderSelectorBuilder.extractGeneralNames(java.util.Collection):java.util.Set");
    }

    public void addTargetGroup(GeneralName generalName) {
        this.targetGroups.add(generalName);
    }

    public void addTargetName(GeneralName generalName) {
        this.targetNames.add(generalName);
    }

    public X509AttributeCertificateHolderSelector build() {
        X509AttributeCertificateHolderSelector x509AttributeCertificateHolderSelector = new X509AttributeCertificateHolderSelector(this.holder, this.issuer, this.serialNumber, this.attributeCertificateValid, this.attributeCert, Collections.unmodifiableCollection(new HashSet(this.targetNames)), Collections.unmodifiableCollection(new HashSet(this.targetGroups)));
        return x509AttributeCertificateHolderSelector;
    }

    public void setAttributeCert(X509AttributeCertificateHolder x509AttributeCertificateHolder) {
        this.attributeCert = x509AttributeCertificateHolder;
    }

    public void setAttributeCertificateValid(Date date) {
        if (date != null) {
            this.attributeCertificateValid = new Date(date.getTime());
        } else {
            this.attributeCertificateValid = null;
        }
    }

    public void setHolder(AttributeCertificateHolder attributeCertificateHolder) {
        this.holder = attributeCertificateHolder;
    }

    public void setIssuer(AttributeCertificateIssuer attributeCertificateIssuer) {
        this.issuer = attributeCertificateIssuer;
    }

    public void setSerialNumber(BigInteger bigInteger) {
        this.serialNumber = bigInteger;
    }

    public void setTargetGroups(Collection collection) throws IOException {
        this.targetGroups = extractGeneralNames(collection);
    }

    public void setTargetNames(Collection collection) throws IOException {
        this.targetNames = extractGeneralNames(collection);
    }
}
