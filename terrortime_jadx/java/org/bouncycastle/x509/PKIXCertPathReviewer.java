package org.bouncycastle.x509;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXParameters;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.asn1.x509.NameConstraints;
import org.bouncycastle.asn1.x509.qualified.MonetaryValue;
import org.bouncycastle.asn1.x509.qualified.QCStatement;
import org.bouncycastle.i18n.ErrorBundle;
import org.bouncycastle.i18n.filter.TrustedInput;
import org.bouncycastle.i18n.filter.UntrustedInput;
import org.bouncycastle.jce.provider.AnnotatedException;
import org.bouncycastle.jce.provider.PKIXNameConstraintValidator;
import org.bouncycastle.jce.provider.PKIXNameConstraintValidatorException;
import org.bouncycastle.util.Integers;

public class PKIXCertPathReviewer extends CertPathValidatorUtilities {
    private static final String AUTH_INFO_ACCESS = Extension.authorityInfoAccess.getId();
    private static final String CRL_DIST_POINTS = Extension.cRLDistributionPoints.getId();
    private static final String QC_STATEMENT = Extension.qCStatements.getId();
    private static final String RESOURCE_NAME = "org.bouncycastle.x509.CertPathReviewerMessages";
    protected CertPath certPath;
    protected List certs;
    protected List[] errors;
    private boolean initialized;
    protected int n;
    protected List[] notifications;
    protected PKIXParameters pkixParams;
    protected PolicyNode policyTree;
    protected PublicKey subjectPublicKey;
    protected TrustAnchor trustAnchor;
    protected Date validDate;

    public PKIXCertPathReviewer() {
    }

    public PKIXCertPathReviewer(CertPath certPath2, PKIXParameters pKIXParameters) throws CertPathReviewerException {
        init(certPath2, pKIXParameters);
    }

    private String IPtoString(byte[] bArr) {
        try {
            return InetAddress.getByAddress(bArr).getHostAddress();
        } catch (Exception e) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i != bArr.length; i++) {
                stringBuffer.append(Integer.toHexString(bArr[i] & 255));
                stringBuffer.append(' ');
            }
            return stringBuffer.toString();
        }
    }

    private void checkCriticalExtensions() {
        int size;
        String str = RESOURCE_NAME;
        List<PKIXCertPathChecker> certPathCheckers = this.pkixParams.getCertPathCheckers();
        for (PKIXCertPathChecker init : certPathCheckers) {
            try {
                init.init(false);
            } catch (CertPathValidatorException e) {
                throw new CertPathReviewerException(new ErrorBundle(str, "CertPathReviewer.certPathCheckerError", new Object[]{e.getMessage(), e, e.getClass().getName()}), e);
            } catch (CertPathValidatorException e2) {
                throw new CertPathReviewerException(new ErrorBundle(str, "CertPathReviewer.criticalExtensionError", new Object[]{e2.getMessage(), e2, e2.getClass().getName()}), e2.getCause(), this.certPath, size);
            } catch (CertPathReviewerException e3) {
                addError(e3.getErrorMessage(), e3.getIndex());
                return;
            }
        }
        size = this.certs.size() - 1;
        while (size >= 0) {
            X509Certificate x509Certificate = (X509Certificate) this.certs.get(size);
            Set<String> criticalExtensionOIDs = x509Certificate.getCriticalExtensionOIDs();
            if (criticalExtensionOIDs != null) {
                if (!criticalExtensionOIDs.isEmpty()) {
                    criticalExtensionOIDs.remove(KEY_USAGE);
                    criticalExtensionOIDs.remove(CERTIFICATE_POLICIES);
                    criticalExtensionOIDs.remove(POLICY_MAPPINGS);
                    criticalExtensionOIDs.remove(INHIBIT_ANY_POLICY);
                    criticalExtensionOIDs.remove(ISSUING_DISTRIBUTION_POINT);
                    criticalExtensionOIDs.remove(DELTA_CRL_INDICATOR);
                    criticalExtensionOIDs.remove(POLICY_CONSTRAINTS);
                    criticalExtensionOIDs.remove(BASIC_CONSTRAINTS);
                    criticalExtensionOIDs.remove(SUBJECT_ALTERNATIVE_NAME);
                    criticalExtensionOIDs.remove(NAME_CONSTRAINTS);
                    if (criticalExtensionOIDs.contains(QC_STATEMENT) && processQcStatements(x509Certificate, size)) {
                        criticalExtensionOIDs.remove(QC_STATEMENT);
                    }
                    for (PKIXCertPathChecker check : certPathCheckers) {
                        check.check(x509Certificate, criticalExtensionOIDs);
                    }
                    if (!criticalExtensionOIDs.isEmpty()) {
                        for (String aSN1ObjectIdentifier : criticalExtensionOIDs) {
                            addError(new ErrorBundle(str, "CertPathReviewer.unknownCriticalExt", new Object[]{new ASN1ObjectIdentifier(aSN1ObjectIdentifier)}), size);
                        }
                    }
                }
            }
            size--;
        }
    }

    private void checkNameConstraints() {
        GeneralName instance;
        PKIXNameConstraintValidator pKIXNameConstraintValidator = new PKIXNameConstraintValidator();
        for (int size = this.certs.size() - 1; size > 0; size--) {
            int i = this.n;
            X509Certificate x509Certificate = (X509Certificate) this.certs.get(size);
            boolean isSelfIssued = isSelfIssued(x509Certificate);
            String str = RESOURCE_NAME;
            if (!isSelfIssued) {
                X500Principal subjectPrincipal = getSubjectPrincipal(x509Certificate);
                try {
                    ASN1Sequence aSN1Sequence = (ASN1Sequence) new ASN1InputStream((InputStream) new ByteArrayInputStream(subjectPrincipal.getEncoded())).readObject();
                    pKIXNameConstraintValidator.checkPermittedDN(aSN1Sequence);
                    pKIXNameConstraintValidator.checkExcludedDN(aSN1Sequence);
                    ASN1Sequence aSN1Sequence2 = (ASN1Sequence) getExtensionValue(x509Certificate, SUBJECT_ALTERNATIVE_NAME);
                    if (aSN1Sequence2 != null) {
                        for (int i2 = 0; i2 < aSN1Sequence2.size(); i2++) {
                            instance = GeneralName.getInstance(aSN1Sequence2.getObjectAt(i2));
                            pKIXNameConstraintValidator.checkPermitted(instance);
                            pKIXNameConstraintValidator.checkExcluded(instance);
                        }
                    }
                } catch (AnnotatedException e) {
                    throw new CertPathReviewerException(new ErrorBundle(str, "CertPathReviewer.ncExtError"), e, this.certPath, size);
                } catch (IOException e2) {
                    throw new CertPathReviewerException(new ErrorBundle(str, "CertPathReviewer.ncSubjectNameError", new Object[]{new UntrustedInput(subjectPrincipal)}), e2, this.certPath, size);
                } catch (PKIXNameConstraintValidatorException e3) {
                    throw new CertPathReviewerException(new ErrorBundle(str, "CertPathReviewer.notPermittedDN", new Object[]{new UntrustedInput(subjectPrincipal.getName())}), e3, this.certPath, size);
                } catch (PKIXNameConstraintValidatorException e4) {
                    throw new CertPathReviewerException(new ErrorBundle(str, "CertPathReviewer.excludedDN", new Object[]{new UntrustedInput(subjectPrincipal.getName())}), e4, this.certPath, size);
                } catch (AnnotatedException e5) {
                    throw new CertPathReviewerException(new ErrorBundle(str, "CertPathReviewer.subjAltNameExtError"), e5, this.certPath, size);
                } catch (PKIXNameConstraintValidatorException e6) {
                    throw new CertPathReviewerException(new ErrorBundle(str, "CertPathReviewer.notPermittedEmail", new Object[]{new UntrustedInput(instance)}), e6, this.certPath, size);
                } catch (CertPathReviewerException e7) {
                    addError(e7.getErrorMessage(), e7.getIndex());
                    return;
                }
            }
            ASN1Sequence aSN1Sequence3 = (ASN1Sequence) getExtensionValue(x509Certificate, NAME_CONSTRAINTS);
            if (aSN1Sequence3 != null) {
                NameConstraints instance2 = NameConstraints.getInstance(aSN1Sequence3);
                GeneralSubtree[] permittedSubtrees = instance2.getPermittedSubtrees();
                if (permittedSubtrees != null) {
                    pKIXNameConstraintValidator.intersectPermittedSubtree(permittedSubtrees);
                }
                GeneralSubtree[] excludedSubtrees = instance2.getExcludedSubtrees();
                if (excludedSubtrees != null) {
                    for (int i3 = 0; i3 != excludedSubtrees.length; i3++) {
                        pKIXNameConstraintValidator.addExcludedSubtree(excludedSubtrees[i3]);
                    }
                }
            }
        }
    }

    private void checkPathLength() {
        BasicConstraints basicConstraints;
        int size = this.certs.size() - 1;
        int i = this.n;
        int i2 = 0;
        while (true) {
            String str = RESOURCE_NAME;
            if (size > 0) {
                int i3 = this.n;
                X509Certificate x509Certificate = (X509Certificate) this.certs.get(size);
                if (!isSelfIssued(x509Certificate)) {
                    if (i <= 0) {
                        addError(new ErrorBundle(str, "CertPathReviewer.pathLengthExtended"));
                    }
                    i--;
                    i2++;
                }
                try {
                    basicConstraints = BasicConstraints.getInstance(getExtensionValue(x509Certificate, BASIC_CONSTRAINTS));
                } catch (AnnotatedException e) {
                    addError(new ErrorBundle(str, "CertPathReviewer.processLengthConstError"), size);
                    basicConstraints = null;
                }
                if (basicConstraints != null) {
                    BigInteger pathLenConstraint = basicConstraints.getPathLenConstraint();
                    if (pathLenConstraint != null) {
                        int intValue = pathLenConstraint.intValue();
                        if (intValue < i) {
                            i = intValue;
                        }
                    }
                }
                size--;
            } else {
                addNotification(new ErrorBundle(str, "CertPathReviewer.totalPathLength", new Object[]{Integers.valueOf(i2)}));
                return;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:212:0x03e8, code lost:
        if (r2 < r12) goto L_0x03ec;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x0441, code lost:
        r26 = r0;
        r2 = r14;
        r27 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x044a, code lost:
        if (isSelfIssued(r7) != false) goto L_0x0450;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x044c, code lost:
        if (r8 <= 0) goto L_0x0450;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x044e, code lost:
        r8 = r8 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:?, code lost:
        r0 = (org.bouncycastle.asn1.ASN1Sequence) getExtensionValue(r7, POLICY_CONSTRAINTS);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x0458, code lost:
        if (r0 == null) goto L_0x0486;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x045a, code lost:
        r0 = r0.getObjects();
        r5 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x0463, code lost:
        if (r0.hasMoreElements() == false) goto L_0x0484;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x0465, code lost:
        r4 = (org.bouncycastle.asn1.ASN1TaggedObject) r0.nextElement();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x046f, code lost:
        if (r4.getTagNo() == 0) goto L_0x0473;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x0480, code lost:
        if (org.bouncycastle.asn1.ASN1Integer.getInstance(r4, false).getValue().intValue() != 0) goto L_0x045f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x0482, code lost:
        r5 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x0484, code lost:
        r13 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x0486, code lost:
        r13 = 0;
        r5 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x0488, code lost:
        r0 = "CertPathReviewer.explicitPolicy";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x048a, code lost:
        if (r27 != null) goto L_0x04a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x0492, code lost:
        if (r1.pkixParams.isExplicitPolicyRequired() != false) goto L_0x0498;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x0494, code lost:
        r16 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x04a4, code lost:
        throw new org.bouncycastle.x509.CertPathReviewerException(new org.bouncycastle.i18n.ErrorBundle(r11, r0), r1.certPath, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x04a9, code lost:
        if (isAnyPolicy(r26) == false) goto L_0x0547;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x04b1, code lost:
        if (r1.pkixParams.isExplicitPolicyRequired() == false) goto L_0x0543;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x04b7, code lost:
        if (r9.isEmpty() != false) goto L_0x0536;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x04b9, code lost:
        r0 = new java.util.HashSet();
        r2 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x04c0, code lost:
        if (r2 >= r3.length) goto L_0x04f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x04c2, code lost:
        r4 = r3[r2];
        r7 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x04c9, code lost:
        if (r7 >= r4.size()) goto L_0x04f0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x04cb, code lost:
        r8 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r4.get(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x04d9, code lost:
        if (r6.equals(r8.getValidPolicy()) == false) goto L_0x04ed;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x04db, code lost:
        r8 = r8.getChildren();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x04e3, code lost:
        if (r8.hasNext() == false) goto L_0x04ed;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:273:0x04e5, code lost:
        r0.add(r8.next());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x04ed, code lost:
        r7 = r7 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x04f0, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x04f3, code lost:
        r0 = r0.iterator();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x04fb, code lost:
        if (r0.hasNext() == false) goto L_0x050b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x04fd, code lost:
        r9.contains(((org.bouncycastle.jce.provider.PKIXPolicyNode) r0.next()).getValidPolicy());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x050b, code lost:
        if (r27 == null) goto L_0x0543;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x050d, code lost:
        r0 = r1.n - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0511, code lost:
        if (r0 < 0) goto L_0x0543;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x0513, code lost:
        r2 = r3[r0];
        r4 = r13;
        r6 = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x051c, code lost:
        if (r4 >= r2.size()) goto L_0x0531;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x051e, code lost:
        r7 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r2.get(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x0528, code lost:
        if (r7.hasChildren() != false) goto L_0x052e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x052a, code lost:
        r6 = removePolicyNode(r6, r3, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x052e, code lost:
        r4 = r4 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x0531, code lost:
        r0 = r0 - 1;
        r27 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x0542, code lost:
        throw new org.bouncycastle.x509.CertPathReviewerException(new org.bouncycastle.i18n.ErrorBundle(r11, r0), r1.certPath, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x0543, code lost:
        r16 = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x0547, code lost:
        r0 = new java.util.HashSet();
        r2 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x054e, code lost:
        if (r2 >= r3.length) goto L_0x058d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x0550, code lost:
        r4 = r3[r2];
        r7 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x0557, code lost:
        if (r7 >= r4.size()) goto L_0x058a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x0559, code lost:
        r8 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r4.get(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x0567, code lost:
        if (r6.equals(r8.getValidPolicy()) == false) goto L_0x0587;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x0569, code lost:
        r8 = r8.getChildren();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x0571, code lost:
        if (r8.hasNext() == false) goto L_0x0587;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x0573, code lost:
        r9 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r8.next();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x0581, code lost:
        if (r6.equals(r9.getValidPolicy()) != false) goto L_0x056d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x0583, code lost:
        r0.add(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x0587, code lost:
        r7 = r7 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x058a, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x058d, code lost:
        r0 = r0.iterator();
        r2 = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x0597, code lost:
        if (r0.hasNext() == false) goto L_0x05b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x0599, code lost:
        r4 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r0.next();
        r7 = r26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x05a9, code lost:
        if (r7.contains(r4.getValidPolicy()) != false) goto L_0x05af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x05ab, code lost:
        r2 = removePolicyNode(r2, r3, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x05af, code lost:
        r26 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x05b2, code lost:
        if (r2 == null) goto L_0x05db;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x05b4, code lost:
        r0 = r1.n - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x05b8, code lost:
        if (r0 < 0) goto L_0x05db;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x05ba, code lost:
        r4 = r3[r0];
        r6 = r2;
        r2 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x05c2, code lost:
        if (r2 >= r4.size()) goto L_0x05d7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x05c4, code lost:
        r7 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r4.get(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x05ce, code lost:
        if (r7.hasChildren() != false) goto L_0x05d4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x05d0, code lost:
        r6 = removePolicyNode(r6, r3, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x05d4, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x05d7, code lost:
        r0 = r0 - 1;
        r2 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x05db, code lost:
        r16 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x05dd, code lost:
        if (r5 > 0) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x05df, code lost:
        if (r16 == null) goto L_0x05e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:333:0x05ee, code lost:
        throw new org.bouncycastle.x509.CertPathReviewerException(new org.bouncycastle.i18n.ErrorBundle(r11, "CertPathReviewer.invalidPolicy"));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:?, code lost:
        r2 = getQualifierSet(r9.getPolicyQualifiers());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:?, code lost:
        r9 = r3[r7 - 1];
        r29 = r0;
        r15 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0161, code lost:
        if (r15 >= r9.size()) goto L_0x0128;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0163, code lost:
        r0 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r9.get(r15);
        r30 = r0.getExpectedPolicies().iterator();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0175, code lost:
        if (r30.hasNext() == false) goto L_0x01ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x0177, code lost:
        r31 = r9;
        r9 = r30.next();
        r32 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0181, code lost:
        if ((r9 instanceof java.lang.String) == false) goto L_0x0186;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0183, code lost:
        r9 = (java.lang.String) r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x0188, code lost:
        if ((r9 instanceof org.bouncycastle.asn1.ASN1ObjectIdentifier) == false) goto L_0x01e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x018a, code lost:
        r9 = ((org.bouncycastle.asn1.ASN1ObjectIdentifier) r9).getId();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x0190, code lost:
        r12 = r0.getChildren();
        r17 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x019a, code lost:
        if (r12.hasNext() == false) goto L_0x01b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x019c, code lost:
        r19 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x01ac, code lost:
        if (r9.equals(((org.bouncycastle.jce.provider.PKIXPolicyNode) r12.next()).getValidPolicy()) == false) goto L_0x01b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x01ae, code lost:
        r17 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x01b0, code lost:
        r12 = r19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x01b3, code lost:
        if (r17 != false) goto L_0x01e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x01b5, code lost:
        r12 = new java.util.HashSet();
        r12.add(r9);
        r33 = r13;
        r17 = new org.bouncycastle.jce.provider.PKIXPolicyNode(new java.util.ArrayList(), r7, r12, r0, r2, r9, false);
        r0.addChild(r17);
        r3[r7].add(r17);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x01e0, code lost:
        r33 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x01e2, code lost:
        r9 = r31;
        r12 = r32;
        r13 = r33;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x01e9, code lost:
        r9 = r31;
        r12 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x01ee, code lost:
        r31 = r9;
        r32 = r12;
        r33 = r13;
        r15 = r15 + 1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x020a A[Catch:{ AnnotatedException -> 0x05ef, AnnotatedException -> 0x0432, AnnotatedException -> 0x040f, AnnotatedException -> 0x03ff, AnnotatedException -> 0x03ef, AnnotatedException -> 0x0350, CertPathValidatorException -> 0x0341, CertPathValidatorException -> 0x01f8, CertPathValidatorException -> 0x00db, CertPathReviewerException -> 0x05fd }] */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0236 A[Catch:{ AnnotatedException -> 0x05ef, AnnotatedException -> 0x0432, AnnotatedException -> 0x040f, AnnotatedException -> 0x03ff, AnnotatedException -> 0x03ef, AnnotatedException -> 0x0350, CertPathValidatorException -> 0x0341, CertPathValidatorException -> 0x01f8, CertPathValidatorException -> 0x00db, CertPathReviewerException -> 0x05fd }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkPolicy() {
        /*
            r34 = this;
            r1 = r34
            java.lang.String r2 = "CertPathReviewer.policyExtError"
            java.security.cert.PKIXParameters r0 = r1.pkixParams
            java.util.Set r0 = r0.getInitialPolicies()
            int r3 = r1.n
            r4 = 1
            int r3 = r3 + r4
            java.util.ArrayList[] r3 = new java.util.ArrayList[r3]
            r5 = 0
            r6 = r5
        L_0x0012:
            int r7 = r3.length
            if (r6 >= r7) goto L_0x001f
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            r3[r6] = r7
            int r6 = r6 + 1
            goto L_0x0012
        L_0x001f:
            java.util.HashSet r10 = new java.util.HashSet
            r10.<init>()
            java.lang.String r6 = "2.5.29.32.0"
            r10.add(r6)
            org.bouncycastle.jce.provider.PKIXPolicyNode r15 = new org.bouncycastle.jce.provider.PKIXPolicyNode
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            r9 = 0
            r11 = 0
            java.util.HashSet r12 = new java.util.HashSet
            r12.<init>()
            r14 = 0
            java.lang.String r13 = "2.5.29.32.0"
            r7 = r15
            r7.<init>(r8, r9, r10, r11, r12, r13, r14)
            r7 = r3[r5]
            r7.add(r15)
            java.security.cert.PKIXParameters r7 = r1.pkixParams
            boolean r7 = r7.isExplicitPolicyRequired()
            if (r7 == 0) goto L_0x004d
            r7 = r5
            goto L_0x0050
        L_0x004d:
            int r7 = r1.n
            int r7 = r7 + r4
        L_0x0050:
            java.security.cert.PKIXParameters r8 = r1.pkixParams
            boolean r8 = r8.isAnyPolicyInhibited()
            if (r8 == 0) goto L_0x005a
            r8 = r5
            goto L_0x005d
        L_0x005a:
            int r8 = r1.n
            int r8 = r8 + r4
        L_0x005d:
            java.security.cert.PKIXParameters r9 = r1.pkixParams
            boolean r9 = r9.isPolicyMappingInhibited()
            if (r9 == 0) goto L_0x0067
            r9 = r5
            goto L_0x006a
        L_0x0067:
            int r9 = r1.n
            int r9 = r9 + r4
        L_0x006a:
            java.util.List r10 = r1.certs     // Catch:{ CertPathReviewerException -> 0x05fd }
            int r10 = r10.size()     // Catch:{ CertPathReviewerException -> 0x05fd }
            int r10 = r10 - r4
            r12 = r8
            r13 = r9
            r9 = 0
            r8 = r7
            r7 = 0
        L_0x0076:
            java.lang.String r14 = "CertPathReviewer.policyConstExtError"
            java.lang.String r11 = "org.bouncycastle.x509.CertPathReviewerMessages"
            if (r10 < 0) goto L_0x0441
            int r7 = r1.n     // Catch:{ CertPathReviewerException -> 0x05fd }
            int r7 = r7 - r10
            java.util.List r4 = r1.certs     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.Object r4 = r4.get(r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.security.cert.X509Certificate r4 = (java.security.cert.X509Certificate) r4     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r5 = CERTIFICATE_POLICIES     // Catch:{ AnnotatedException -> 0x0432 }
            org.bouncycastle.asn1.ASN1Primitive r5 = getExtensionValue(r4, r5)     // Catch:{ AnnotatedException -> 0x0432 }
            org.bouncycastle.asn1.ASN1Sequence r5 = (org.bouncycastle.asn1.ASN1Sequence) r5     // Catch:{ AnnotatedException -> 0x0432 }
            r25 = r14
            java.lang.String r14 = "CertPathReviewer.policyQualifierError"
            if (r5 == 0) goto L_0x0254
            if (r15 == 0) goto L_0x0254
            java.util.Enumeration r17 = r5.getObjects()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r26 = r0
            java.util.HashSet r0 = new java.util.HashSet     // Catch:{ CertPathReviewerException -> 0x05fd }
            r0.<init>()     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x00a2:
            boolean r18 = r17.hasMoreElements()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r18 == 0) goto L_0x00ee
            java.lang.Object r18 = r17.nextElement()     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.asn1.x509.PolicyInformation r18 = org.bouncycastle.asn1.x509.PolicyInformation.getInstance(r18)     // Catch:{ CertPathReviewerException -> 0x05fd }
            r27 = r15
            org.bouncycastle.asn1.ASN1ObjectIdentifier r15 = r18.getPolicyIdentifier()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r28 = r2
            java.lang.String r2 = r15.getId()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r0.add(r2)     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r2 = r15.getId()     // Catch:{ CertPathReviewerException -> 0x05fd }
            boolean r2 = r6.equals(r2)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r2 != 0) goto L_0x00e9
            org.bouncycastle.asn1.ASN1Sequence r2 = r18.getPolicyQualifiers()     // Catch:{ CertPathValidatorException -> 0x00db }
            java.util.Set r2 = getQualifierSet(r2)     // Catch:{ CertPathValidatorException -> 0x00db }
            boolean r18 = processCertD1i(r7, r3, r15, r2)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r18 != 0) goto L_0x00e9
            processCertD1ii(r7, r3, r15, r2)     // Catch:{ CertPathReviewerException -> 0x05fd }
            goto L_0x00e9
        L_0x00db:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2.<init>(r11, r14)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.x509.CertPathReviewerException r3 = new org.bouncycastle.x509.CertPathReviewerException     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.security.cert.CertPath r4 = r1.certPath     // Catch:{ CertPathReviewerException -> 0x05fd }
            r3.<init>(r2, r0, r4, r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            throw r3     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x00e9:
            r15 = r27
            r2 = r28
            goto L_0x00a2
        L_0x00ee:
            r28 = r2
            r27 = r15
            if (r9 == 0) goto L_0x0119
            boolean r2 = r9.contains(r6)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r2 == 0) goto L_0x00fb
            goto L_0x0119
        L_0x00fb:
            java.util.Iterator r2 = r9.iterator()     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.util.HashSet r9 = new java.util.HashSet     // Catch:{ CertPathReviewerException -> 0x05fd }
            r9.<init>()     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x0104:
            boolean r15 = r2.hasNext()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r15 == 0) goto L_0x0118
            java.lang.Object r15 = r2.next()     // Catch:{ CertPathReviewerException -> 0x05fd }
            boolean r17 = r0.contains(r15)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r17 == 0) goto L_0x0104
            r9.add(r15)     // Catch:{ CertPathReviewerException -> 0x05fd }
            goto L_0x0104
        L_0x0118:
            r0 = r9
        L_0x0119:
            if (r12 > 0) goto L_0x012e
            int r2 = r1.n     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r7 >= r2) goto L_0x0126
            boolean r2 = isSelfIssued(r4)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r2 == 0) goto L_0x0126
            goto L_0x012e
        L_0x0126:
            r29 = r0
        L_0x0128:
            r32 = r12
            r33 = r13
            goto L_0x0206
        L_0x012e:
            java.util.Enumeration r2 = r5.getObjects()     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x0132:
            boolean r9 = r2.hasMoreElements()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r9 == 0) goto L_0x0126
            java.lang.Object r9 = r2.nextElement()     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.asn1.x509.PolicyInformation r9 = org.bouncycastle.asn1.x509.PolicyInformation.getInstance(r9)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r15 = r9.getPolicyIdentifier()     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r15 = r15.getId()     // Catch:{ CertPathReviewerException -> 0x05fd }
            boolean r15 = r6.equals(r15)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r15 == 0) goto L_0x0132
            org.bouncycastle.asn1.ASN1Sequence r2 = r9.getPolicyQualifiers()     // Catch:{ CertPathValidatorException -> 0x01f8 }
            java.util.Set r2 = getQualifierSet(r2)     // Catch:{ CertPathValidatorException -> 0x01f8 }
            int r9 = r7 + -1
            r9 = r3[r9]     // Catch:{ CertPathReviewerException -> 0x05fd }
            r29 = r0
            r15 = 0
        L_0x015d:
            int r0 = r9.size()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r15 >= r0) goto L_0x0128
            java.lang.Object r0 = r9.get(r15)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.jce.provider.PKIXPolicyNode r0 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r0     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.util.Set r17 = r0.getExpectedPolicies()     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.util.Iterator r30 = r17.iterator()     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x0171:
            boolean r17 = r30.hasNext()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r17 == 0) goto L_0x01ee
            r31 = r9
            java.lang.Object r9 = r30.next()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r32 = r12
            boolean r12 = r9 instanceof java.lang.String     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r12 == 0) goto L_0x0186
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ CertPathReviewerException -> 0x05fd }
            goto L_0x0190
        L_0x0186:
            boolean r12 = r9 instanceof org.bouncycastle.asn1.ASN1ObjectIdentifier     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r12 == 0) goto L_0x01e9
            org.bouncycastle.asn1.ASN1ObjectIdentifier r9 = (org.bouncycastle.asn1.ASN1ObjectIdentifier) r9     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r9 = r9.getId()     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x0190:
            java.util.Iterator r12 = r0.getChildren()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r17 = 0
        L_0x0196:
            boolean r18 = r12.hasNext()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r18 == 0) goto L_0x01b3
            java.lang.Object r18 = r12.next()     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.jce.provider.PKIXPolicyNode r18 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r18     // Catch:{ CertPathReviewerException -> 0x05fd }
            r19 = r12
            java.lang.String r12 = r18.getValidPolicy()     // Catch:{ CertPathReviewerException -> 0x05fd }
            boolean r12 = r9.equals(r12)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r12 == 0) goto L_0x01b0
            r17 = 1
        L_0x01b0:
            r12 = r19
            goto L_0x0196
        L_0x01b3:
            if (r17 != 0) goto L_0x01e0
            java.util.HashSet r12 = new java.util.HashSet     // Catch:{ CertPathReviewerException -> 0x05fd }
            r12.<init>()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r12.add(r9)     // Catch:{ CertPathReviewerException -> 0x05fd }
            r33 = r13
            org.bouncycastle.jce.provider.PKIXPolicyNode r13 = new org.bouncycastle.jce.provider.PKIXPolicyNode     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.util.ArrayList r18 = new java.util.ArrayList     // Catch:{ CertPathReviewerException -> 0x05fd }
            r18.<init>()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r24 = 0
            r17 = r13
            r19 = r7
            r20 = r12
            r21 = r0
            r22 = r2
            r23 = r9
            r17.<init>(r18, r19, r20, r21, r22, r23, r24)     // Catch:{ CertPathReviewerException -> 0x05fd }
            r0.addChild(r13)     // Catch:{ CertPathReviewerException -> 0x05fd }
            r9 = r3[r7]     // Catch:{ CertPathReviewerException -> 0x05fd }
            r9.add(r13)     // Catch:{ CertPathReviewerException -> 0x05fd }
            goto L_0x01e2
        L_0x01e0:
            r33 = r13
        L_0x01e2:
            r9 = r31
            r12 = r32
            r13 = r33
            goto L_0x0171
        L_0x01e9:
            r9 = r31
            r12 = r32
            goto L_0x0171
        L_0x01ee:
            r31 = r9
            r32 = r12
            r33 = r13
            int r15 = r15 + 1
            goto L_0x015d
        L_0x01f8:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2.<init>(r11, r14)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.x509.CertPathReviewerException r3 = new org.bouncycastle.x509.CertPathReviewerException     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.security.cert.CertPath r4 = r1.certPath     // Catch:{ CertPathReviewerException -> 0x05fd }
            r3.<init>(r2, r0, r4, r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            throw r3     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x0206:
            int r0 = r7 + -1
        L_0x0208:
            if (r0 < 0) goto L_0x0230
            r2 = r3[r0]     // Catch:{ CertPathReviewerException -> 0x05fd }
            r12 = r27
            r9 = 0
        L_0x020f:
            int r13 = r2.size()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r9 >= r13) goto L_0x022b
            java.lang.Object r13 = r2.get(r9)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.jce.provider.PKIXPolicyNode r13 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r13     // Catch:{ CertPathReviewerException -> 0x05fd }
            boolean r15 = r13.hasChildren()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r15 != 0) goto L_0x0228
            org.bouncycastle.jce.provider.PKIXPolicyNode r12 = removePolicyNode(r12, r3, r13)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r12 != 0) goto L_0x0228
            goto L_0x022b
        L_0x0228:
            int r9 = r9 + 1
            goto L_0x020f
        L_0x022b:
            r27 = r12
            int r0 = r0 + -1
            goto L_0x0208
        L_0x0230:
            java.util.Set r0 = r4.getCriticalExtensionOIDs()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r0 == 0) goto L_0x0251
            java.lang.String r2 = CERTIFICATE_POLICIES     // Catch:{ CertPathReviewerException -> 0x05fd }
            boolean r0 = r0.contains(r2)     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2 = r3[r7]     // Catch:{ CertPathReviewerException -> 0x05fd }
            r9 = 0
        L_0x023f:
            int r12 = r2.size()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r9 >= r12) goto L_0x0251
            java.lang.Object r12 = r2.get(r9)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.jce.provider.PKIXPolicyNode r12 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r12     // Catch:{ CertPathReviewerException -> 0x05fd }
            r12.setCritical(r0)     // Catch:{ CertPathReviewerException -> 0x05fd }
            int r9 = r9 + 1
            goto L_0x023f
        L_0x0251:
            r9 = r29
            goto L_0x025e
        L_0x0254:
            r26 = r0
            r28 = r2
            r32 = r12
            r33 = r13
            r27 = r15
        L_0x025e:
            if (r5 != 0) goto L_0x0262
            r27 = 0
        L_0x0262:
            if (r8 > 0) goto L_0x0274
            if (r27 == 0) goto L_0x0267
            goto L_0x0274
        L_0x0267:
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r2 = "CertPathReviewer.noValidPolicyTree"
            r0.<init>(r11, r2)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.x509.CertPathReviewerException r2 = new org.bouncycastle.x509.CertPathReviewerException     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2.<init>(r0)     // Catch:{ CertPathReviewerException -> 0x05fd }
            throw r2     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x0274:
            int r0 = r1.n     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r7 == r0) goto L_0x041f
            java.lang.String r0 = POLICY_MAPPINGS     // Catch:{ AnnotatedException -> 0x040f }
            org.bouncycastle.asn1.ASN1Primitive r0 = getExtensionValue(r4, r0)     // Catch:{ AnnotatedException -> 0x040f }
            if (r0 == 0) goto L_0x02d1
            r2 = r0
            org.bouncycastle.asn1.ASN1Sequence r2 = (org.bouncycastle.asn1.ASN1Sequence) r2     // Catch:{ CertPathReviewerException -> 0x05fd }
            r5 = 0
        L_0x0284:
            int r12 = r2.size()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r5 >= r12) goto L_0x02d1
            org.bouncycastle.asn1.ASN1Encodable r12 = r2.getObjectAt(r5)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.asn1.ASN1Sequence r12 = (org.bouncycastle.asn1.ASN1Sequence) r12     // Catch:{ CertPathReviewerException -> 0x05fd }
            r13 = 0
            org.bouncycastle.asn1.ASN1Encodable r15 = r12.getObjectAt(r13)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r15 = (org.bouncycastle.asn1.ASN1ObjectIdentifier) r15     // Catch:{ CertPathReviewerException -> 0x05fd }
            r13 = 1
            org.bouncycastle.asn1.ASN1Encodable r12 = r12.getObjectAt(r13)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r12 = (org.bouncycastle.asn1.ASN1ObjectIdentifier) r12     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r13 = r15.getId()     // Catch:{ CertPathReviewerException -> 0x05fd }
            boolean r13 = r6.equals(r13)     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r15 = "CertPathReviewer.invalidPolicyMapping"
            if (r13 != 0) goto L_0x02c4
            java.lang.String r12 = r12.getId()     // Catch:{ CertPathReviewerException -> 0x05fd }
            boolean r12 = r6.equals(r12)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r12 != 0) goto L_0x02b7
            int r5 = r5 + 1
            goto L_0x0284
        L_0x02b7:
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x05fd }
            r0.<init>(r11, r15)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.x509.CertPathReviewerException r2 = new org.bouncycastle.x509.CertPathReviewerException     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.security.cert.CertPath r3 = r1.certPath     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2.<init>(r0, r3, r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            throw r2     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x02c4:
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x05fd }
            r0.<init>(r11, r15)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.x509.CertPathReviewerException r2 = new org.bouncycastle.x509.CertPathReviewerException     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.security.cert.CertPath r3 = r1.certPath     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2.<init>(r0, r3, r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            throw r2     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x02d1:
            if (r0 == 0) goto L_0x036f
            org.bouncycastle.asn1.ASN1Sequence r0 = (org.bouncycastle.asn1.ASN1Sequence) r0     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.util.HashMap r2 = new java.util.HashMap     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2.<init>()     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.util.HashSet r5 = new java.util.HashSet     // Catch:{ CertPathReviewerException -> 0x05fd }
            r5.<init>()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r12 = 0
        L_0x02e0:
            int r13 = r0.size()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r12 >= r13) goto L_0x0327
            org.bouncycastle.asn1.ASN1Encodable r13 = r0.getObjectAt(r12)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.asn1.ASN1Sequence r13 = (org.bouncycastle.asn1.ASN1Sequence) r13     // Catch:{ CertPathReviewerException -> 0x05fd }
            r15 = 0
            org.bouncycastle.asn1.ASN1Encodable r17 = r13.getObjectAt(r15)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r17 = (org.bouncycastle.asn1.ASN1ObjectIdentifier) r17     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r15 = r17.getId()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r17 = r0
            r0 = 1
            org.bouncycastle.asn1.ASN1Encodable r13 = r13.getObjectAt(r0)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r13 = (org.bouncycastle.asn1.ASN1ObjectIdentifier) r13     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r0 = r13.getId()     // Catch:{ CertPathReviewerException -> 0x05fd }
            boolean r13 = r2.containsKey(r15)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r13 != 0) goto L_0x0319
            java.util.HashSet r13 = new java.util.HashSet     // Catch:{ CertPathReviewerException -> 0x05fd }
            r13.<init>()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r13.add(r0)     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2.put(r15, r13)     // Catch:{ CertPathReviewerException -> 0x05fd }
            r5.add(r15)     // Catch:{ CertPathReviewerException -> 0x05fd }
            goto L_0x0322
        L_0x0319:
            java.lang.Object r13 = r2.get(r15)     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.util.Set r13 = (java.util.Set) r13     // Catch:{ CertPathReviewerException -> 0x05fd }
            r13.add(r0)     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x0322:
            int r12 = r12 + 1
            r0 = r17
            goto L_0x02e0
        L_0x0327:
            java.util.Iterator r0 = r5.iterator()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r5 = r27
        L_0x032d:
            boolean r12 = r0.hasNext()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r12 == 0) goto L_0x036c
            java.lang.Object r12 = r0.next()     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r12 = (java.lang.String) r12     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r33 <= 0) goto L_0x0361
            prepareNextCertB1(r7, r3, r12, r2, r4)     // Catch:{ AnnotatedException -> 0x0350, CertPathValidatorException -> 0x0341 }
            r13 = r28
            goto L_0x0369
        L_0x0341:
            r0 = move-exception
            r2 = r0
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x05fd }
            r0.<init>(r11, r14)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.x509.CertPathReviewerException r3 = new org.bouncycastle.x509.CertPathReviewerException     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.security.cert.CertPath r4 = r1.certPath     // Catch:{ CertPathReviewerException -> 0x05fd }
            r3.<init>(r0, r2, r4, r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            throw r3     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x0350:
            r0 = move-exception
            r2 = r0
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x05fd }
            r13 = r28
            r0.<init>(r11, r13)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.x509.CertPathReviewerException r3 = new org.bouncycastle.x509.CertPathReviewerException     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.security.cert.CertPath r4 = r1.certPath     // Catch:{ CertPathReviewerException -> 0x05fd }
            r3.<init>(r0, r2, r4, r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            throw r3     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x0361:
            r13 = r28
            if (r33 > 0) goto L_0x0369
            org.bouncycastle.jce.provider.PKIXPolicyNode r5 = prepareNextCertB2(r7, r3, r12, r5)     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x0369:
            r28 = r13
            goto L_0x032d
        L_0x036c:
            r13 = r28
            goto L_0x0373
        L_0x036f:
            r13 = r28
            r5 = r27
        L_0x0373:
            boolean r0 = isSelfIssued(r4)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r0 != 0) goto L_0x038c
            if (r8 == 0) goto L_0x037d
            int r8 = r8 + -1
        L_0x037d:
            if (r33 == 0) goto L_0x0382
            int r0 = r33 + -1
            goto L_0x0384
        L_0x0382:
            r0 = r33
        L_0x0384:
            if (r32 == 0) goto L_0x0389
            int r12 = r32 + -1
            goto L_0x0390
        L_0x0389:
            r12 = r32
            goto L_0x0390
        L_0x038c:
            r12 = r32
            r0 = r33
        L_0x0390:
            java.lang.String r2 = POLICY_CONSTRAINTS     // Catch:{ AnnotatedException -> 0x03ff }
            org.bouncycastle.asn1.ASN1Primitive r2 = getExtensionValue(r4, r2)     // Catch:{ AnnotatedException -> 0x03ff }
            org.bouncycastle.asn1.ASN1Sequence r2 = (org.bouncycastle.asn1.ASN1Sequence) r2     // Catch:{ AnnotatedException -> 0x03ff }
            if (r2 == 0) goto L_0x03d6
            java.util.Enumeration r2 = r2.getObjects()     // Catch:{ AnnotatedException -> 0x03ff }
        L_0x039e:
            boolean r7 = r2.hasMoreElements()     // Catch:{ AnnotatedException -> 0x03ff }
            if (r7 == 0) goto L_0x03d6
            java.lang.Object r7 = r2.nextElement()     // Catch:{ AnnotatedException -> 0x03ff }
            org.bouncycastle.asn1.ASN1TaggedObject r7 = (org.bouncycastle.asn1.ASN1TaggedObject) r7     // Catch:{ AnnotatedException -> 0x03ff }
            int r14 = r7.getTagNo()     // Catch:{ AnnotatedException -> 0x03ff }
            if (r14 == 0) goto L_0x03c5
            r15 = 1
            if (r14 == r15) goto L_0x03b4
            goto L_0x039e
        L_0x03b4:
            r14 = 0
            org.bouncycastle.asn1.ASN1Integer r7 = org.bouncycastle.asn1.ASN1Integer.getInstance(r7, r14)     // Catch:{ AnnotatedException -> 0x03ff }
            java.math.BigInteger r7 = r7.getValue()     // Catch:{ AnnotatedException -> 0x03ff }
            int r7 = r7.intValue()     // Catch:{ AnnotatedException -> 0x03ff }
            if (r7 >= r0) goto L_0x039e
            r0 = r7
            goto L_0x039e
        L_0x03c5:
            r14 = 0
            org.bouncycastle.asn1.ASN1Integer r7 = org.bouncycastle.asn1.ASN1Integer.getInstance(r7, r14)     // Catch:{ AnnotatedException -> 0x03ff }
            java.math.BigInteger r7 = r7.getValue()     // Catch:{ AnnotatedException -> 0x03ff }
            int r7 = r7.intValue()     // Catch:{ AnnotatedException -> 0x03ff }
            if (r7 >= r8) goto L_0x039e
            r8 = r7
            goto L_0x039e
        L_0x03d6:
            java.lang.String r2 = INHIBIT_ANY_POLICY     // Catch:{ AnnotatedException -> 0x03ef }
            org.bouncycastle.asn1.ASN1Primitive r2 = getExtensionValue(r4, r2)     // Catch:{ AnnotatedException -> 0x03ef }
            org.bouncycastle.asn1.ASN1Integer r2 = (org.bouncycastle.asn1.ASN1Integer) r2     // Catch:{ AnnotatedException -> 0x03ef }
            if (r2 == 0) goto L_0x03eb
            java.math.BigInteger r2 = r2.getValue()     // Catch:{ AnnotatedException -> 0x03ef }
            int r2 = r2.intValue()     // Catch:{ AnnotatedException -> 0x03ef }
            if (r2 >= r12) goto L_0x03eb
            goto L_0x03ec
        L_0x03eb:
            r2 = r12
        L_0x03ec:
            r12 = r2
            r15 = r5
            goto L_0x0427
        L_0x03ef:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r2 = "CertPathReviewer.policyInhibitExtError"
            r0.<init>(r11, r2)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.x509.CertPathReviewerException r2 = new org.bouncycastle.x509.CertPathReviewerException     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.security.cert.CertPath r3 = r1.certPath     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2.<init>(r0, r3, r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            throw r2     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x03ff:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2 = r25
            r0.<init>(r11, r2)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.x509.CertPathReviewerException r2 = new org.bouncycastle.x509.CertPathReviewerException     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.security.cert.CertPath r3 = r1.certPath     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2.<init>(r0, r3, r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            throw r2     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x040f:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r3 = "CertPathReviewer.policyMapExtError"
            r2.<init>(r11, r3)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.x509.CertPathReviewerException r3 = new org.bouncycastle.x509.CertPathReviewerException     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.security.cert.CertPath r4 = r1.certPath     // Catch:{ CertPathReviewerException -> 0x05fd }
            r3.<init>(r2, r0, r4, r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            throw r3     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x041f:
            r13 = r28
            r15 = r27
            r12 = r32
            r0 = r33
        L_0x0427:
            int r10 = r10 + -1
            r7 = r4
            r2 = r13
            r4 = 1
            r5 = 0
            r13 = r0
            r0 = r26
            goto L_0x0076
        L_0x0432:
            r0 = move-exception
            r13 = r2
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2.<init>(r11, r13)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.x509.CertPathReviewerException r3 = new org.bouncycastle.x509.CertPathReviewerException     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.security.cert.CertPath r4 = r1.certPath     // Catch:{ CertPathReviewerException -> 0x05fd }
            r3.<init>(r2, r0, r4, r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            throw r3     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x0441:
            r26 = r0
            r2 = r14
            r27 = r15
            boolean r0 = isSelfIssued(r7)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r0 != 0) goto L_0x0450
            if (r8 <= 0) goto L_0x0450
            int r8 = r8 + -1
        L_0x0450:
            java.lang.String r0 = POLICY_CONSTRAINTS     // Catch:{ AnnotatedException -> 0x05ef }
            org.bouncycastle.asn1.ASN1Primitive r0 = getExtensionValue(r7, r0)     // Catch:{ AnnotatedException -> 0x05ef }
            org.bouncycastle.asn1.ASN1Sequence r0 = (org.bouncycastle.asn1.ASN1Sequence) r0     // Catch:{ AnnotatedException -> 0x05ef }
            if (r0 == 0) goto L_0x0486
            java.util.Enumeration r0 = r0.getObjects()     // Catch:{ AnnotatedException -> 0x05ef }
            r5 = r8
        L_0x045f:
            boolean r4 = r0.hasMoreElements()     // Catch:{ AnnotatedException -> 0x05ef }
            if (r4 == 0) goto L_0x0484
            java.lang.Object r4 = r0.nextElement()     // Catch:{ AnnotatedException -> 0x05ef }
            org.bouncycastle.asn1.ASN1TaggedObject r4 = (org.bouncycastle.asn1.ASN1TaggedObject) r4     // Catch:{ AnnotatedException -> 0x05ef }
            int r7 = r4.getTagNo()     // Catch:{ AnnotatedException -> 0x05ef }
            if (r7 == 0) goto L_0x0473
            r13 = 0
            goto L_0x045f
        L_0x0473:
            r13 = 0
            org.bouncycastle.asn1.ASN1Integer r4 = org.bouncycastle.asn1.ASN1Integer.getInstance(r4, r13)     // Catch:{ AnnotatedException -> 0x05ef }
            java.math.BigInteger r4 = r4.getValue()     // Catch:{ AnnotatedException -> 0x05ef }
            int r4 = r4.intValue()     // Catch:{ AnnotatedException -> 0x05ef }
            if (r4 != 0) goto L_0x045f
            r5 = r13
            goto L_0x045f
        L_0x0484:
            r13 = 0
            goto L_0x0488
        L_0x0486:
            r13 = 0
            r5 = r8
        L_0x0488:
            java.lang.String r0 = "CertPathReviewer.explicitPolicy"
            if (r27 != 0) goto L_0x04a5
            java.security.cert.PKIXParameters r2 = r1.pkixParams     // Catch:{ CertPathReviewerException -> 0x05fd }
            boolean r2 = r2.isExplicitPolicyRequired()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r2 != 0) goto L_0x0498
            r16 = 0
            goto L_0x05dd
        L_0x0498:
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2.<init>(r11, r0)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.x509.CertPathReviewerException r0 = new org.bouncycastle.x509.CertPathReviewerException     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.security.cert.CertPath r3 = r1.certPath     // Catch:{ CertPathReviewerException -> 0x05fd }
            r0.<init>(r2, r3, r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            throw r0     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x04a5:
            boolean r2 = isAnyPolicy(r26)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r2 == 0) goto L_0x0547
            java.security.cert.PKIXParameters r2 = r1.pkixParams     // Catch:{ CertPathReviewerException -> 0x05fd }
            boolean r2 = r2.isExplicitPolicyRequired()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r2 == 0) goto L_0x0543
            boolean r2 = r9.isEmpty()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r2 != 0) goto L_0x0536
            java.util.HashSet r0 = new java.util.HashSet     // Catch:{ CertPathReviewerException -> 0x05fd }
            r0.<init>()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2 = r13
        L_0x04bf:
            int r4 = r3.length     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r2 >= r4) goto L_0x04f3
            r4 = r3[r2]     // Catch:{ CertPathReviewerException -> 0x05fd }
            r7 = r13
        L_0x04c5:
            int r8 = r4.size()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r7 >= r8) goto L_0x04f0
            java.lang.Object r8 = r4.get(r7)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.jce.provider.PKIXPolicyNode r8 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r8     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r10 = r8.getValidPolicy()     // Catch:{ CertPathReviewerException -> 0x05fd }
            boolean r10 = r6.equals(r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r10 == 0) goto L_0x04ed
            java.util.Iterator r8 = r8.getChildren()     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x04df:
            boolean r10 = r8.hasNext()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r10 == 0) goto L_0x04ed
            java.lang.Object r10 = r8.next()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r0.add(r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            goto L_0x04df
        L_0x04ed:
            int r7 = r7 + 1
            goto L_0x04c5
        L_0x04f0:
            int r2 = r2 + 1
            goto L_0x04bf
        L_0x04f3:
            java.util.Iterator r0 = r0.iterator()     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x04f7:
            boolean r2 = r0.hasNext()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r2 == 0) goto L_0x050b
            java.lang.Object r2 = r0.next()     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.jce.provider.PKIXPolicyNode r2 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r2     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r2 = r2.getValidPolicy()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r9.contains(r2)     // Catch:{ CertPathReviewerException -> 0x05fd }
            goto L_0x04f7
        L_0x050b:
            if (r27 == 0) goto L_0x0543
            int r0 = r1.n     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2 = 1
            int r0 = r0 - r2
        L_0x0511:
            if (r0 < 0) goto L_0x0543
            r2 = r3[r0]     // Catch:{ CertPathReviewerException -> 0x05fd }
            r4 = r13
            r6 = r27
        L_0x0518:
            int r7 = r2.size()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r4 >= r7) goto L_0x0531
            java.lang.Object r7 = r2.get(r4)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.jce.provider.PKIXPolicyNode r7 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r7     // Catch:{ CertPathReviewerException -> 0x05fd }
            boolean r8 = r7.hasChildren()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r8 != 0) goto L_0x052e
            org.bouncycastle.jce.provider.PKIXPolicyNode r6 = removePolicyNode(r6, r3, r7)     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x052e:
            int r4 = r4 + 1
            goto L_0x0518
        L_0x0531:
            int r0 = r0 + -1
            r27 = r6
            goto L_0x0511
        L_0x0536:
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2.<init>(r11, r0)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.x509.CertPathReviewerException r0 = new org.bouncycastle.x509.CertPathReviewerException     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.security.cert.CertPath r3 = r1.certPath     // Catch:{ CertPathReviewerException -> 0x05fd }
            r0.<init>(r2, r3, r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            throw r0     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x0543:
            r16 = r27
            goto L_0x05dd
        L_0x0547:
            java.util.HashSet r0 = new java.util.HashSet     // Catch:{ CertPathReviewerException -> 0x05fd }
            r0.<init>()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2 = r13
        L_0x054d:
            int r4 = r3.length     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r2 >= r4) goto L_0x058d
            r4 = r3[r2]     // Catch:{ CertPathReviewerException -> 0x05fd }
            r7 = r13
        L_0x0553:
            int r8 = r4.size()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r7 >= r8) goto L_0x058a
            java.lang.Object r8 = r4.get(r7)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.jce.provider.PKIXPolicyNode r8 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r8     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r9 = r8.getValidPolicy()     // Catch:{ CertPathReviewerException -> 0x05fd }
            boolean r9 = r6.equals(r9)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r9 == 0) goto L_0x0587
            java.util.Iterator r8 = r8.getChildren()     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x056d:
            boolean r9 = r8.hasNext()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r9 == 0) goto L_0x0587
            java.lang.Object r9 = r8.next()     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.jce.provider.PKIXPolicyNode r9 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r9     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r10 = r9.getValidPolicy()     // Catch:{ CertPathReviewerException -> 0x05fd }
            boolean r10 = r6.equals(r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r10 != 0) goto L_0x056d
            r0.add(r9)     // Catch:{ CertPathReviewerException -> 0x05fd }
            goto L_0x056d
        L_0x0587:
            int r7 = r7 + 1
            goto L_0x0553
        L_0x058a:
            int r2 = r2 + 1
            goto L_0x054d
        L_0x058d:
            java.util.Iterator r0 = r0.iterator()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2 = r27
        L_0x0593:
            boolean r4 = r0.hasNext()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r4 == 0) goto L_0x05b2
            java.lang.Object r4 = r0.next()     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.jce.provider.PKIXPolicyNode r4 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r4     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r6 = r4.getValidPolicy()     // Catch:{ CertPathReviewerException -> 0x05fd }
            r7 = r26
            boolean r6 = r7.contains(r6)     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r6 != 0) goto L_0x05af
            org.bouncycastle.jce.provider.PKIXPolicyNode r2 = removePolicyNode(r2, r3, r4)     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x05af:
            r26 = r7
            goto L_0x0593
        L_0x05b2:
            if (r2 == 0) goto L_0x05db
            int r0 = r1.n     // Catch:{ CertPathReviewerException -> 0x05fd }
            r4 = 1
            int r0 = r0 - r4
        L_0x05b8:
            if (r0 < 0) goto L_0x05db
            r4 = r3[r0]     // Catch:{ CertPathReviewerException -> 0x05fd }
            r6 = r2
            r2 = r13
        L_0x05be:
            int r7 = r4.size()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r2 >= r7) goto L_0x05d7
            java.lang.Object r7 = r4.get(r2)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.jce.provider.PKIXPolicyNode r7 = (org.bouncycastle.jce.provider.PKIXPolicyNode) r7     // Catch:{ CertPathReviewerException -> 0x05fd }
            boolean r8 = r7.hasChildren()     // Catch:{ CertPathReviewerException -> 0x05fd }
            if (r8 != 0) goto L_0x05d4
            org.bouncycastle.jce.provider.PKIXPolicyNode r6 = removePolicyNode(r6, r3, r7)     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x05d4:
            int r2 = r2 + 1
            goto L_0x05be
        L_0x05d7:
            int r0 = r0 + -1
            r2 = r6
            goto L_0x05b8
        L_0x05db:
            r16 = r2
        L_0x05dd:
            if (r5 > 0) goto L_0x0609
            if (r16 == 0) goto L_0x05e2
            goto L_0x0609
        L_0x05e2:
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.lang.String r2 = "CertPathReviewer.invalidPolicy"
            r0.<init>(r11, r2)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.x509.CertPathReviewerException r2 = new org.bouncycastle.x509.CertPathReviewerException     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2.<init>(r0)     // Catch:{ CertPathReviewerException -> 0x05fd }
            throw r2     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x05ef:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x05fd }
            r0.<init>(r11, r2)     // Catch:{ CertPathReviewerException -> 0x05fd }
            org.bouncycastle.x509.CertPathReviewerException r2 = new org.bouncycastle.x509.CertPathReviewerException     // Catch:{ CertPathReviewerException -> 0x05fd }
            java.security.cert.CertPath r3 = r1.certPath     // Catch:{ CertPathReviewerException -> 0x05fd }
            r2.<init>(r0, r3, r10)     // Catch:{ CertPathReviewerException -> 0x05fd }
            throw r2     // Catch:{ CertPathReviewerException -> 0x05fd }
        L_0x05fd:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r2 = r0.getErrorMessage()
            int r0 = r0.getIndex()
            r1.addError(r2, r0)
        L_0x0609:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.x509.PKIXCertPathReviewer.checkPolicy():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:106:0x02da A[LOOP:1: B:104:0x02d4->B:106:0x02da, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0303 A[LOOP:2: B:108:0x02fd->B:110:0x0303, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x034b  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x0356  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0386  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x03e1  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0100  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0146  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0149  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0170  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x017f  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0294 A[SYNTHETIC, Splitter:B:89:0x0294] */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x02b7 A[Catch:{ AnnotatedException -> 0x02bc }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkSignatures() {
        /*
            r21 = this;
            r10 = r21
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            r11 = 2
            java.lang.Object[] r1 = new java.lang.Object[r11]
            org.bouncycastle.i18n.filter.TrustedInput r2 = new org.bouncycastle.i18n.filter.TrustedInput
            java.util.Date r3 = r10.validDate
            r2.<init>(r3)
            r12 = 0
            r1[r12] = r2
            org.bouncycastle.i18n.filter.TrustedInput r2 = new org.bouncycastle.i18n.filter.TrustedInput
            java.util.Date r3 = new java.util.Date
            r3.<init>()
            r2.<init>(r3)
            r13 = 1
            r1[r13] = r2
            java.lang.String r14 = "org.bouncycastle.x509.CertPathReviewerMessages"
            java.lang.String r2 = "CertPathReviewer.certPathValidDate"
            r0.<init>(r14, r2, r1)
            r10.addNotification(r0)
            java.util.List r0 = r10.certs     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            java.util.List r1 = r10.certs     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            int r1 = r1.size()     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            int r1 = r1 - r13
            java.lang.Object r0 = r0.get(r1)     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            java.security.cert.X509Certificate r0 = (java.security.cert.X509Certificate) r0     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            java.security.cert.PKIXParameters r1 = r10.pkixParams     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            java.util.Set r1 = r1.getTrustAnchors()     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            java.util.Collection r1 = r10.getTrustAnchors(r0, r1)     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            int r2 = r1.size()     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            if (r2 <= r13) goto L_0x0069
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            java.lang.String r3 = "CertPathReviewer.conflictingTrustAnchors"
            java.lang.Object[] r4 = new java.lang.Object[r11]     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            int r1 = r1.size()     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            java.lang.Integer r1 = org.bouncycastle.util.Integers.valueOf(r1)     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            r4[r12] = r1     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            org.bouncycastle.i18n.filter.UntrustedInput r1 = new org.bouncycastle.i18n.filter.UntrustedInput     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            javax.security.auth.x500.X500Principal r0 = r0.getIssuerX500Principal()     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            r1.<init>(r0)     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            r4[r13] = r1     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            r2.<init>(r14, r3, r4)     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            r10.addError(r2)     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            goto L_0x0096
        L_0x0069:
            boolean r2 = r1.isEmpty()     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            if (r2 == 0) goto L_0x0098
            org.bouncycastle.i18n.ErrorBundle r1 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            java.lang.String r2 = "CertPathReviewer.noTrustAnchorFound"
            java.lang.Object[] r3 = new java.lang.Object[r11]     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            org.bouncycastle.i18n.filter.UntrustedInput r4 = new org.bouncycastle.i18n.filter.UntrustedInput     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            javax.security.auth.x500.X500Principal r0 = r0.getIssuerX500Principal()     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            r4.<init>(r0)     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            r3[r12] = r4     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            java.security.cert.PKIXParameters r0 = r10.pkixParams     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            java.util.Set r0 = r0.getTrustAnchors()     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            int r0 = r0.size()     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            java.lang.Integer r0 = org.bouncycastle.util.Integers.valueOf(r0)     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            r3[r13] = r0     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            r1.<init>(r14, r2, r3)     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            r10.addError(r1)     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
        L_0x0096:
            r1 = 0
            goto L_0x00fb
        L_0x0098:
            java.util.Iterator r1 = r1.iterator()     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            java.lang.Object r1 = r1.next()     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            java.security.cert.TrustAnchor r1 = (java.security.cert.TrustAnchor) r1     // Catch:{ CertPathReviewerException -> 0x00f2, all -> 0x00d1 }
            java.security.cert.X509Certificate r2 = r1.getTrustedCert()     // Catch:{ CertPathReviewerException -> 0x00cf, all -> 0x00cd }
            if (r2 == 0) goto L_0x00b1
            java.security.cert.X509Certificate r2 = r1.getTrustedCert()     // Catch:{ CertPathReviewerException -> 0x00cf, all -> 0x00cd }
            java.security.PublicKey r2 = r2.getPublicKey()     // Catch:{ CertPathReviewerException -> 0x00cf, all -> 0x00cd }
            goto L_0x00b5
        L_0x00b1:
            java.security.PublicKey r2 = r1.getCAPublicKey()     // Catch:{ CertPathReviewerException -> 0x00cf, all -> 0x00cd }
        L_0x00b5:
            java.security.cert.PKIXParameters r3 = r10.pkixParams     // Catch:{ SignatureException -> 0x00c1, Exception -> 0x00bf }
            java.lang.String r3 = r3.getSigProvider()     // Catch:{ SignatureException -> 0x00c1, Exception -> 0x00bf }
            org.bouncycastle.x509.CertPathValidatorUtilities.verifyX509Certificate(r0, r2, r3)     // Catch:{ SignatureException -> 0x00c1, Exception -> 0x00bf }
            goto L_0x00fb
        L_0x00bf:
            r0 = move-exception
            goto L_0x00fb
        L_0x00c1:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x00cf, all -> 0x00cd }
            java.lang.String r2 = "CertPathReviewer.trustButInvalidCert"
            r0.<init>(r14, r2)     // Catch:{ CertPathReviewerException -> 0x00cf, all -> 0x00cd }
            r10.addError(r0)     // Catch:{ CertPathReviewerException -> 0x00cf, all -> 0x00cd }
            goto L_0x00fb
        L_0x00cd:
            r0 = move-exception
            goto L_0x00d3
        L_0x00cf:
            r0 = move-exception
            goto L_0x00f4
        L_0x00d1:
            r0 = move-exception
            r1 = 0
        L_0x00d3:
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.Object[] r3 = new java.lang.Object[r11]
            org.bouncycastle.i18n.filter.UntrustedInput r4 = new org.bouncycastle.i18n.filter.UntrustedInput
            java.lang.String r5 = r0.getMessage()
            r4.<init>(r5)
            r3[r12] = r4
            org.bouncycastle.i18n.filter.UntrustedInput r4 = new org.bouncycastle.i18n.filter.UntrustedInput
            r4.<init>(r0)
            r3[r13] = r4
            java.lang.String r0 = "CertPathReviewer.unknown"
            r2.<init>(r14, r0, r3)
            r10.addError(r2)
            goto L_0x00fb
        L_0x00f2:
            r0 = move-exception
            r1 = 0
        L_0x00f4:
            org.bouncycastle.i18n.ErrorBundle r0 = r0.getErrorMessage()
            r10.addError(r0)
        L_0x00fb:
            r9 = r1
            r16 = 5
            if (r9 == 0) goto L_0x0146
            java.security.cert.X509Certificate r1 = r9.getTrustedCert()
            if (r1 == 0) goto L_0x010b
            javax.security.auth.x500.X500Principal r0 = getSubjectPrincipal(r1)     // Catch:{ IllegalArgumentException -> 0x0115 }
            goto L_0x012e
        L_0x010b:
            javax.security.auth.x500.X500Principal r0 = new javax.security.auth.x500.X500Principal     // Catch:{ IllegalArgumentException -> 0x0115 }
            java.lang.String r2 = r9.getCAName()     // Catch:{ IllegalArgumentException -> 0x0115 }
            r0.<init>(r2)     // Catch:{ IllegalArgumentException -> 0x0115 }
            goto L_0x012e
        L_0x0115:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.Object[] r2 = new java.lang.Object[r13]
            org.bouncycastle.i18n.filter.UntrustedInput r3 = new org.bouncycastle.i18n.filter.UntrustedInput
            java.lang.String r4 = r9.getCAName()
            r3.<init>(r4)
            r2[r12] = r3
            java.lang.String r3 = "CertPathReviewer.trustDNInvalid"
            r0.<init>(r14, r3, r2)
            r10.addError(r0)
            r0 = 0
        L_0x012e:
            if (r1 == 0) goto L_0x0144
            boolean[] r1 = r1.getKeyUsage()
            if (r1 == 0) goto L_0x0144
            boolean r1 = r1[r16]
            if (r1 != 0) goto L_0x0144
            org.bouncycastle.i18n.ErrorBundle r1 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r2 = "CertPathReviewer.trustKeyUsage"
            r1.<init>(r14, r2)
            r10.addNotification(r1)
        L_0x0144:
            r1 = r0
            goto L_0x0147
        L_0x0146:
            r1 = 0
        L_0x0147:
            if (r9 == 0) goto L_0x0170
            java.security.cert.X509Certificate r2 = r9.getTrustedCert()
            if (r2 == 0) goto L_0x0154
            java.security.PublicKey r0 = r2.getPublicKey()
            goto L_0x0158
        L_0x0154:
            java.security.PublicKey r0 = r9.getCAPublicKey()
        L_0x0158:
            r3 = r0
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r0 = getAlgorithmIdentifier(r3)     // Catch:{ CertPathValidatorException -> 0x0164 }
            r0.getAlgorithm()     // Catch:{ CertPathValidatorException -> 0x0164 }
            r0.getParameters()     // Catch:{ CertPathValidatorException -> 0x0164 }
            goto L_0x0172
        L_0x0164:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r4 = "CertPathReviewer.trustPubKeyError"
            r0.<init>(r14, r4)
            r10.addError(r0)
            goto L_0x0172
        L_0x0170:
            r2 = 0
            r3 = 0
        L_0x0172:
            java.util.List r0 = r10.certs
            int r0 = r0.size()
            int r0 = r0 - r13
            r8 = r0
            r6 = r1
            r5 = r2
            r7 = r3
        L_0x017d:
            if (r8 < 0) goto L_0x040f
            int r0 = r10.n
            int r4 = r0 - r8
            java.util.List r0 = r10.certs
            java.lang.Object r0 = r0.get(r8)
            r3 = r0
            java.security.cert.X509Certificate r3 = (java.security.cert.X509Certificate) r3
            java.lang.String r1 = "CertPathReviewer.signatureNotVerified"
            r2 = 3
            if (r7 == 0) goto L_0x01bb
            java.security.cert.PKIXParameters r0 = r10.pkixParams     // Catch:{ GeneralSecurityException -> 0x019c }
            java.lang.String r0 = r0.getSigProvider()     // Catch:{ GeneralSecurityException -> 0x019c }
            org.bouncycastle.x509.CertPathValidatorUtilities.verifyX509Certificate(r3, r7, r0)     // Catch:{ GeneralSecurityException -> 0x019c }
            goto L_0x0256
        L_0x019c:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r15 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r17 = r0.getMessage()
            r2[r12] = r17
            r2[r13] = r0
            java.lang.Class r0 = r0.getClass()
            java.lang.String r0 = r0.getName()
            r2[r11] = r0
            r15.<init>(r14, r1, r2)
        L_0x01b6:
            r10.addError(r15, r8)
            goto L_0x0256
        L_0x01bb:
            boolean r0 = isSelfIssued(r3)
            if (r0 == 0) goto L_0x01f5
            java.security.PublicKey r0 = r3.getPublicKey()     // Catch:{ GeneralSecurityException -> 0x01da }
            java.security.cert.PKIXParameters r15 = r10.pkixParams     // Catch:{ GeneralSecurityException -> 0x01da }
            java.lang.String r15 = r15.getSigProvider()     // Catch:{ GeneralSecurityException -> 0x01da }
            org.bouncycastle.x509.CertPathValidatorUtilities.verifyX509Certificate(r3, r0, r15)     // Catch:{ GeneralSecurityException -> 0x01da }
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ GeneralSecurityException -> 0x01da }
            java.lang.String r15 = "CertPathReviewer.rootKeyIsValidButNotATrustAnchor"
            r0.<init>(r14, r15)     // Catch:{ GeneralSecurityException -> 0x01da }
            r10.addError(r0, r8)     // Catch:{ GeneralSecurityException -> 0x01da }
            goto L_0x0256
        L_0x01da:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r15 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r17 = r0.getMessage()
            r2[r12] = r17
            r2[r13] = r0
            java.lang.Class r0 = r0.getClass()
            java.lang.String r0 = r0.getName()
            r2[r11] = r0
            r15.<init>(r14, r1, r2)
            goto L_0x01b6
        L_0x01f5:
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r1 = "CertPathReviewer.NoIssuerPublicKey"
            r0.<init>(r14, r1)
            org.bouncycastle.asn1.ASN1ObjectIdentifier r1 = org.bouncycastle.asn1.x509.Extension.authorityKeyIdentifier
            java.lang.String r1 = r1.getId()
            byte[] r1 = r3.getExtensionValue(r1)
            if (r1 == 0) goto L_0x0253
            org.bouncycastle.asn1.ASN1OctetString r1 = org.bouncycastle.asn1.DEROctetString.getInstance(r1)
            byte[] r1 = r1.getOctets()
            org.bouncycastle.asn1.x509.AuthorityKeyIdentifier r1 = org.bouncycastle.asn1.x509.AuthorityKeyIdentifier.getInstance(r1)
            org.bouncycastle.asn1.x509.GeneralNames r15 = r1.getAuthorityCertIssuer()
            if (r15 == 0) goto L_0x0253
            org.bouncycastle.asn1.x509.GeneralName[] r15 = r15.getNames()
            r15 = r15[r12]
            java.math.BigInteger r1 = r1.getAuthorityCertSerialNumber()
            if (r1 == 0) goto L_0x0253
            r2 = 7
            java.lang.Object[] r2 = new java.lang.Object[r2]
            org.bouncycastle.i18n.LocaleString r11 = new org.bouncycastle.i18n.LocaleString
            java.lang.String r13 = "missingIssuer"
            r11.<init>(r14, r13)
            r2[r12] = r11
            java.lang.String r11 = " \""
            r13 = 1
            r2[r13] = r11
            r11 = 2
            r2[r11] = r15
            java.lang.String r11 = "\" "
            r13 = 3
            r2[r13] = r11
            r11 = 4
            org.bouncycastle.i18n.LocaleString r13 = new org.bouncycastle.i18n.LocaleString
            java.lang.String r15 = "missingSerial"
            r13.<init>(r14, r15)
            r2[r11] = r13
            java.lang.String r11 = " "
            r2[r16] = r11
            r11 = 6
            r2[r11] = r1
            r0.setExtraArguments(r2)
        L_0x0253:
            r10.addError(r0, r8)
        L_0x0256:
            java.util.Date r0 = r10.validDate     // Catch:{ CertificateNotYetValidException -> 0x0273, CertificateExpiredException -> 0x025c }
            r3.checkValidity(r0)     // Catch:{ CertificateNotYetValidException -> 0x0273, CertificateExpiredException -> 0x025c }
            goto L_0x028c
        L_0x025c:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            r1 = 1
            java.lang.Object[] r2 = new java.lang.Object[r1]
            org.bouncycastle.i18n.filter.TrustedInput r1 = new org.bouncycastle.i18n.filter.TrustedInput
            java.util.Date r11 = r3.getNotAfter()
            r1.<init>(r11)
            r2[r12] = r1
            java.lang.String r1 = "CertPathReviewer.certificateExpired"
            r0.<init>(r14, r1, r2)
            goto L_0x0289
        L_0x0273:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            r1 = 1
            java.lang.Object[] r2 = new java.lang.Object[r1]
            org.bouncycastle.i18n.filter.TrustedInput r1 = new org.bouncycastle.i18n.filter.TrustedInput
            java.util.Date r11 = r3.getNotBefore()
            r1.<init>(r11)
            r2[r12] = r1
            java.lang.String r1 = "CertPathReviewer.certificateNotYetValid"
            r0.<init>(r14, r1, r2)
        L_0x0289:
            r10.addError(r0, r8)
        L_0x028c:
            java.security.cert.PKIXParameters r0 = r10.pkixParams
            boolean r0 = r0.isRevocationEnabled()
            if (r0 == 0) goto L_0x034b
            java.lang.String r0 = CRL_DIST_POINTS     // Catch:{ AnnotatedException -> 0x02a3 }
            org.bouncycastle.asn1.ASN1Primitive r0 = getExtensionValue(r3, r0)     // Catch:{ AnnotatedException -> 0x02a3 }
            if (r0 == 0) goto L_0x02a1
            org.bouncycastle.asn1.x509.CRLDistPoint r15 = org.bouncycastle.asn1.x509.CRLDistPoint.getInstance(r0)     // Catch:{ AnnotatedException -> 0x02a3 }
            goto L_0x02af
        L_0x02a1:
            r15 = 0
            goto L_0x02af
        L_0x02a3:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r1 = "CertPathReviewer.crlDistPtExtError"
            r0.<init>(r14, r1)
            r10.addError(r0, r8)
            goto L_0x02a1
        L_0x02af:
            java.lang.String r0 = AUTH_INFO_ACCESS     // Catch:{ AnnotatedException -> 0x02bc }
            org.bouncycastle.asn1.ASN1Primitive r0 = getExtensionValue(r3, r0)     // Catch:{ AnnotatedException -> 0x02bc }
            if (r0 == 0) goto L_0x02c7
            org.bouncycastle.asn1.x509.AuthorityInformationAccess r0 = org.bouncycastle.asn1.x509.AuthorityInformationAccess.getInstance(r0)     // Catch:{ AnnotatedException -> 0x02bc }
            goto L_0x02c8
        L_0x02bc:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r1 = "CertPathReviewer.crlAuthInfoAccError"
            r0.<init>(r14, r1)
            r10.addError(r0, r8)
        L_0x02c7:
            r0 = 0
        L_0x02c8:
            java.util.Vector r11 = r10.getCRLDistUrls(r15)
            java.util.Vector r0 = r10.getOCSPUrls(r0)
            java.util.Iterator r1 = r11.iterator()
        L_0x02d4:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x02f7
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle
            r13 = 1
            java.lang.Object[] r15 = new java.lang.Object[r13]
            org.bouncycastle.i18n.filter.UntrustedUrlInput r13 = new org.bouncycastle.i18n.filter.UntrustedUrlInput
            r17 = r3
            java.lang.Object r3 = r1.next()
            r13.<init>(r3)
            r15[r12] = r13
            java.lang.String r3 = "CertPathReviewer.crlDistPoint"
            r2.<init>(r14, r3, r15)
            r10.addNotification(r2, r8)
            r3 = r17
            goto L_0x02d4
        L_0x02f7:
            r17 = r3
            java.util.Iterator r1 = r0.iterator()
        L_0x02fd:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x031c
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle
            r3 = 1
            java.lang.Object[] r13 = new java.lang.Object[r3]
            org.bouncycastle.i18n.filter.UntrustedUrlInput r3 = new org.bouncycastle.i18n.filter.UntrustedUrlInput
            java.lang.Object r15 = r1.next()
            r3.<init>(r15)
            r13[r12] = r3
            java.lang.String r3 = "CertPathReviewer.ocspLocation"
            r2.<init>(r14, r3, r13)
            r10.addNotification(r2, r8)
            goto L_0x02fd
        L_0x031c:
            java.security.cert.PKIXParameters r2 = r10.pkixParams     // Catch:{ CertPathReviewerException -> 0x0338 }
            java.util.Date r13 = r10.validDate     // Catch:{ CertPathReviewerException -> 0x0338 }
            r1 = r21
            r15 = r17
            r3 = r15
            r18 = r4
            r4 = r13
            r13 = r6
            r6 = r7
            r19 = r7
            r7 = r11
            r11 = r8
            r8 = r0
            r20 = r9
            r9 = r11
            r1.checkRevocation(r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ CertPathReviewerException -> 0x0336 }
            goto L_0x0354
        L_0x0336:
            r0 = move-exception
            goto L_0x0343
        L_0x0338:
            r0 = move-exception
            r18 = r4
            r13 = r6
            r19 = r7
            r11 = r8
            r20 = r9
            r15 = r17
        L_0x0343:
            org.bouncycastle.i18n.ErrorBundle r0 = r0.getErrorMessage()
            r10.addError(r0, r11)
            goto L_0x0354
        L_0x034b:
            r15 = r3
            r18 = r4
            r13 = r6
            r19 = r7
            r11 = r8
            r20 = r9
        L_0x0354:
            if (r13 == 0) goto L_0x037f
            javax.security.auth.x500.X500Principal r0 = r15.getIssuerX500Principal()
            boolean r0 = r0.equals(r13)
            if (r0 != 0) goto L_0x037f
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            r1 = 2
            java.lang.Object[] r2 = new java.lang.Object[r1]
            java.lang.String r3 = r13.getName()
            r2[r12] = r3
            javax.security.auth.x500.X500Principal r3 = r15.getIssuerX500Principal()
            java.lang.String r3 = r3.getName()
            r4 = 1
            r2[r4] = r3
            java.lang.String r3 = "CertPathReviewer.certWrongIssuer"
            r0.<init>(r14, r3, r2)
            r10.addError(r0, r11)
            goto L_0x0380
        L_0x037f:
            r1 = 2
        L_0x0380:
            int r0 = r10.n
            r2 = r18
            if (r2 == r0) goto L_0x03e1
            java.lang.String r0 = "CertPathReviewer.noCACert"
            if (r15 == 0) goto L_0x039a
            int r2 = r15.getVersion()
            r3 = 1
            if (r2 != r3) goto L_0x039b
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle
            r2.<init>(r14, r0)
            r10.addError(r2, r11)
            goto L_0x039b
        L_0x039a:
            r3 = 1
        L_0x039b:
            java.lang.String r2 = BASIC_CONSTRAINTS     // Catch:{ AnnotatedException -> 0x03c1 }
            org.bouncycastle.asn1.ASN1Primitive r2 = getExtensionValue(r15, r2)     // Catch:{ AnnotatedException -> 0x03c1 }
            org.bouncycastle.asn1.x509.BasicConstraints r2 = org.bouncycastle.asn1.x509.BasicConstraints.getInstance(r2)     // Catch:{ AnnotatedException -> 0x03c1 }
            if (r2 == 0) goto L_0x03b6
            boolean r2 = r2.isCA()     // Catch:{ AnnotatedException -> 0x03c1 }
            if (r2 != 0) goto L_0x03cc
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ AnnotatedException -> 0x03c1 }
            r2.<init>(r14, r0)     // Catch:{ AnnotatedException -> 0x03c1 }
            r10.addError(r2, r11)     // Catch:{ AnnotatedException -> 0x03c1 }
            goto L_0x03cc
        L_0x03b6:
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ AnnotatedException -> 0x03c1 }
            java.lang.String r2 = "CertPathReviewer.noBasicConstraints"
            r0.<init>(r14, r2)     // Catch:{ AnnotatedException -> 0x03c1 }
            r10.addError(r0, r11)     // Catch:{ AnnotatedException -> 0x03c1 }
            goto L_0x03cc
        L_0x03c1:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r2 = "CertPathReviewer.errorProcesingBC"
            r0.<init>(r14, r2)
            r10.addError(r0, r11)
        L_0x03cc:
            boolean[] r0 = r15.getKeyUsage()
            if (r0 == 0) goto L_0x03e2
            boolean r0 = r0[r16]
            if (r0 != 0) goto L_0x03e2
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r2 = "CertPathReviewer.noCertSign"
            r0.<init>(r14, r2)
            r10.addError(r0, r11)
            goto L_0x03e2
        L_0x03e1:
            r3 = 1
        L_0x03e2:
            javax.security.auth.x500.X500Principal r6 = r15.getSubjectX500Principal()
            java.util.List r0 = r10.certs     // Catch:{ CertPathValidatorException -> 0x03f9 }
            java.security.PublicKey r7 = getNextWorkingKey(r0, r11)     // Catch:{ CertPathValidatorException -> 0x03f9 }
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r0 = getAlgorithmIdentifier(r7)     // Catch:{ CertPathValidatorException -> 0x03f7 }
            r0.getAlgorithm()     // Catch:{ CertPathValidatorException -> 0x03f7 }
            r0.getParameters()     // Catch:{ CertPathValidatorException -> 0x03f7 }
            goto L_0x0406
        L_0x03f7:
            r0 = move-exception
            goto L_0x03fc
        L_0x03f9:
            r0 = move-exception
            r7 = r19
        L_0x03fc:
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r2 = "CertPathReviewer.pubKeyError"
            r0.<init>(r14, r2)
            r10.addError(r0, r11)
        L_0x0406:
            int r8 = r11 + -1
            r11 = r1
            r13 = r3
            r5 = r15
            r9 = r20
            goto L_0x017d
        L_0x040f:
            r19 = r7
            r2 = r9
            r10.trustAnchor = r2
            r3 = r19
            r10.subjectPublicKey = r3
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.x509.PKIXCertPathReviewer.checkSignatures():void");
    }

    private X509CRL getCRL(String str) throws CertPathReviewerException {
        try {
            URL url = new URL(str);
            if (!url.getProtocol().equals("http")) {
                if (!url.getProtocol().equals("https")) {
                    return null;
                }
            }
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                return (X509CRL) CertificateFactory.getInstance("X.509", "BC").generateCRL(httpURLConnection.getInputStream());
            }
            throw new Exception(httpURLConnection.getResponseMessage());
        } catch (Exception e) {
            throw new CertPathReviewerException(new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.loadCrlDistPointError", new Object[]{new UntrustedInput(str), e.getMessage(), e, e.getClass().getName()}));
        }
    }

    private boolean processQcStatements(X509Certificate x509Certificate, int i) {
        ErrorBundle errorBundle;
        ErrorBundle errorBundle2;
        int i2 = i;
        String str = RESOURCE_NAME;
        try {
            ASN1Sequence aSN1Sequence = (ASN1Sequence) getExtensionValue(x509Certificate, QC_STATEMENT);
            boolean z = false;
            for (int i3 = 0; i3 < aSN1Sequence.size(); i3++) {
                QCStatement instance = QCStatement.getInstance(aSN1Sequence.getObjectAt(i3));
                if (QCStatement.id_etsi_qcs_QcCompliance.equals(instance.getStatementId())) {
                    errorBundle2 = new ErrorBundle(str, "CertPathReviewer.QcEuCompliance");
                } else {
                    if (!QCStatement.id_qcs_pkixQCSyntax_v1.equals(instance.getStatementId())) {
                        if (QCStatement.id_etsi_qcs_QcSSCD.equals(instance.getStatementId())) {
                            errorBundle2 = new ErrorBundle(str, "CertPathReviewer.QcSSCD");
                        } else if (QCStatement.id_etsi_qcs_LimiteValue.equals(instance.getStatementId())) {
                            MonetaryValue instance2 = MonetaryValue.getInstance(instance.getStatementInfo());
                            instance2.getCurrency();
                            double doubleValue = instance2.getAmount().doubleValue() * Math.pow(10.0d, instance2.getExponent().doubleValue());
                            if (instance2.getCurrency().isAlphabetic()) {
                                errorBundle = new ErrorBundle(str, "CertPathReviewer.QcLimitValueAlpha", new Object[]{instance2.getCurrency().getAlphabetic(), new TrustedInput(new Double(doubleValue)), instance2});
                            } else {
                                errorBundle = new ErrorBundle(str, "CertPathReviewer.QcLimitValueNum", new Object[]{Integers.valueOf(instance2.getCurrency().getNumeric()), new TrustedInput(new Double(doubleValue)), instance2});
                            }
                            addNotification(errorBundle, i2);
                        } else {
                            addNotification(new ErrorBundle(str, "CertPathReviewer.QcUnknownStatement", new Object[]{instance.getStatementId(), new UntrustedInput(instance)}), i2);
                            z = true;
                        }
                    }
                }
                addNotification(errorBundle2, i2);
            }
            return true ^ z;
        } catch (AnnotatedException e) {
            addError(new ErrorBundle(str, "CertPathReviewer.QcStatementExtError"), i2);
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public void addError(ErrorBundle errorBundle) {
        this.errors[0].add(errorBundle);
    }

    /* access modifiers changed from: protected */
    public void addError(ErrorBundle errorBundle, int i) {
        if (i < -1 || i >= this.n) {
            throw new IndexOutOfBoundsException();
        }
        this.errors[i + 1].add(errorBundle);
    }

    /* access modifiers changed from: protected */
    public void addNotification(ErrorBundle errorBundle) {
        this.notifications[0].add(errorBundle);
    }

    /* access modifiers changed from: protected */
    public void addNotification(ErrorBundle errorBundle, int i) {
        if (i < -1 || i >= this.n) {
            throw new IndexOutOfBoundsException();
        }
        this.notifications[i + 1].add(errorBundle);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x02bb  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x02d0  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x02ed  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkCRLs(java.security.cert.PKIXParameters r21, java.security.cert.X509Certificate r22, java.util.Date r23, java.security.cert.X509Certificate r24, java.security.PublicKey r25, java.util.Vector r26, int r27) throws org.bouncycastle.x509.CertPathReviewerException {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            r3 = r22
            r4 = r25
            r5 = r27
            java.lang.String r6 = "CertPathReviewer.distrPtExtError"
            java.lang.String r7 = "CertPathReviewer.crlExtractionError"
            java.lang.String r8 = "CertPathReviewer.crlIssuerException"
            java.lang.String r9 = "org.bouncycastle.x509.CertPathReviewerMessages"
            org.bouncycastle.x509.X509CRLStoreSelector r0 = new org.bouncycastle.x509.X509CRLStoreSelector
            r0.<init>()
            javax.security.auth.x500.X500Principal r10 = getEncodedIssuerPrincipal(r22)     // Catch:{ IOException -> 0x04a5 }
            byte[] r10 = r10.getEncoded()     // Catch:{ IOException -> 0x04a5 }
            r0.addIssuerName(r10)     // Catch:{ IOException -> 0x04a5 }
            r0.setCertificateChecking(r3)
            r10 = 3
            org.bouncycastle.x509.PKIXCRLUtil r14 = CRL_UTIL     // Catch:{ AnnotatedException -> 0x0092 }
            java.util.Set r14 = r14.findCRLs(r0, r2)     // Catch:{ AnnotatedException -> 0x0092 }
            java.util.Iterator r15 = r14.iterator()     // Catch:{ AnnotatedException -> 0x0092 }
            boolean r14 = r14.isEmpty()     // Catch:{ AnnotatedException -> 0x0092 }
            if (r14 == 0) goto L_0x008d
            org.bouncycastle.x509.PKIXCRLUtil r14 = CRL_UTIL     // Catch:{ AnnotatedException -> 0x0092 }
            org.bouncycastle.x509.X509CRLStoreSelector r11 = new org.bouncycastle.x509.X509CRLStoreSelector     // Catch:{ AnnotatedException -> 0x0092 }
            r11.<init>()     // Catch:{ AnnotatedException -> 0x0092 }
            java.util.Set r11 = r14.findCRLs(r11, r2)     // Catch:{ AnnotatedException -> 0x0092 }
            java.util.Iterator r11 = r11.iterator()     // Catch:{ AnnotatedException -> 0x0092 }
            java.util.ArrayList r14 = new java.util.ArrayList     // Catch:{ AnnotatedException -> 0x0092 }
            r14.<init>()     // Catch:{ AnnotatedException -> 0x0092 }
        L_0x004a:
            boolean r16 = r11.hasNext()     // Catch:{ AnnotatedException -> 0x0092 }
            if (r16 == 0) goto L_0x005e
            java.lang.Object r16 = r11.next()     // Catch:{ AnnotatedException -> 0x0092 }
            java.security.cert.X509CRL r16 = (java.security.cert.X509CRL) r16     // Catch:{ AnnotatedException -> 0x0092 }
            javax.security.auth.x500.X500Principal r13 = r16.getIssuerX500Principal()     // Catch:{ AnnotatedException -> 0x0092 }
            r14.add(r13)     // Catch:{ AnnotatedException -> 0x0092 }
            goto L_0x004a
        L_0x005e:
            int r11 = r14.size()     // Catch:{ AnnotatedException -> 0x0092 }
            org.bouncycastle.i18n.ErrorBundle r13 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ AnnotatedException -> 0x0092 }
            java.lang.String r12 = "CertPathReviewer.noCrlInCertstore"
            r17 = r15
            java.lang.Object[] r15 = new java.lang.Object[r10]     // Catch:{ AnnotatedException -> 0x0092 }
            org.bouncycastle.i18n.filter.UntrustedInput r10 = new org.bouncycastle.i18n.filter.UntrustedInput     // Catch:{ AnnotatedException -> 0x0092 }
            java.util.Collection r0 = r0.getIssuerNames()     // Catch:{ AnnotatedException -> 0x0092 }
            r10.<init>(r0)     // Catch:{ AnnotatedException -> 0x0092 }
            r16 = 0
            r15[r16] = r10     // Catch:{ AnnotatedException -> 0x0092 }
            org.bouncycastle.i18n.filter.UntrustedInput r0 = new org.bouncycastle.i18n.filter.UntrustedInput     // Catch:{ AnnotatedException -> 0x0092 }
            r0.<init>(r14)     // Catch:{ AnnotatedException -> 0x0092 }
            r10 = 1
            r15[r10] = r0     // Catch:{ AnnotatedException -> 0x0092 }
            java.lang.Integer r0 = org.bouncycastle.util.Integers.valueOf(r11)     // Catch:{ AnnotatedException -> 0x0092 }
            r10 = 2
            r15[r10] = r0     // Catch:{ AnnotatedException -> 0x0092 }
            r13.<init>(r9, r12, r15)     // Catch:{ AnnotatedException -> 0x0092 }
            r1.addNotification(r13, r5)     // Catch:{ AnnotatedException -> 0x0092 }
            goto L_0x008f
        L_0x008d:
            r17 = r15
        L_0x008f:
            r15 = r17
            goto L_0x00c8
        L_0x0092:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r10 = new org.bouncycastle.i18n.ErrorBundle
            r11 = 3
            java.lang.Object[] r12 = new java.lang.Object[r11]
            java.lang.Throwable r11 = r0.getCause()
            java.lang.String r11 = r11.getMessage()
            r13 = 0
            r12[r13] = r11
            java.lang.Throwable r11 = r0.getCause()
            r13 = 1
            r12[r13] = r11
            java.lang.Throwable r0 = r0.getCause()
            java.lang.Class r0 = r0.getClass()
            java.lang.String r0 = r0.getName()
            r11 = 2
            r12[r11] = r0
            r10.<init>(r9, r7, r12)
            r1.addError(r10, r5)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.Iterator r15 = r0.iterator()
        L_0x00c8:
            r0 = 0
        L_0x00c9:
            boolean r11 = r15.hasNext()
            if (r11 == 0) goto L_0x0138
            java.lang.Object r0 = r15.next()
            java.security.cert.X509CRL r0 = (java.security.cert.X509CRL) r0
            java.util.Date r11 = r0.getNextUpdate()
            if (r11 == 0) goto L_0x0110
            java.util.Date r11 = r21.getDate()
            java.util.Date r12 = r0.getNextUpdate()
            boolean r11 = r11.before(r12)
            if (r11 == 0) goto L_0x00ea
            goto L_0x0110
        L_0x00ea:
            org.bouncycastle.i18n.ErrorBundle r11 = new org.bouncycastle.i18n.ErrorBundle
            r12 = 2
            java.lang.Object[] r13 = new java.lang.Object[r12]
            org.bouncycastle.i18n.filter.TrustedInput r12 = new org.bouncycastle.i18n.filter.TrustedInput
            java.util.Date r14 = r0.getThisUpdate()
            r12.<init>(r14)
            r14 = 0
            r13[r14] = r12
            org.bouncycastle.i18n.filter.TrustedInput r12 = new org.bouncycastle.i18n.filter.TrustedInput
            java.util.Date r14 = r0.getNextUpdate()
            r12.<init>(r14)
            r14 = 1
            r13[r14] = r12
            java.lang.String r12 = "CertPathReviewer.localInvalidCRL"
            r11.<init>(r9, r12, r13)
            r1.addNotification(r11, r5)
            goto L_0x00c9
        L_0x0110:
            org.bouncycastle.i18n.ErrorBundle r11 = new org.bouncycastle.i18n.ErrorBundle
            r12 = 2
            java.lang.Object[] r13 = new java.lang.Object[r12]
            org.bouncycastle.i18n.filter.TrustedInput r12 = new org.bouncycastle.i18n.filter.TrustedInput
            java.util.Date r14 = r0.getThisUpdate()
            r12.<init>(r14)
            r14 = 0
            r13[r14] = r12
            org.bouncycastle.i18n.filter.TrustedInput r12 = new org.bouncycastle.i18n.filter.TrustedInput
            java.util.Date r14 = r0.getNextUpdate()
            r12.<init>(r14)
            r14 = 1
            r13[r14] = r12
            java.lang.String r12 = "CertPathReviewer.localValidCRL"
            r11.<init>(r9, r12, r13)
            r1.addNotification(r11, r5)
            r11 = r0
            r13 = 1
            goto L_0x013a
        L_0x0138:
            r11 = r0
            r13 = 0
        L_0x013a:
            if (r13 != 0) goto L_0x0251
            java.util.Iterator r12 = r26.iterator()
        L_0x0140:
            boolean r0 = r12.hasNext()
            if (r0 == 0) goto L_0x024c
            java.lang.Object r0 = r12.next()     // Catch:{ CertPathReviewerException -> 0x0238 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ CertPathReviewerException -> 0x0238 }
            java.security.cert.X509CRL r14 = r1.getCRL(r0)     // Catch:{ CertPathReviewerException -> 0x0238 }
            if (r14 == 0) goto L_0x022a
            javax.security.auth.x500.X500Principal r15 = r22.getIssuerX500Principal()     // Catch:{ CertPathReviewerException -> 0x0238 }
            javax.security.auth.x500.X500Principal r10 = r14.getIssuerX500Principal()     // Catch:{ CertPathReviewerException -> 0x0238 }
            boolean r10 = r15.equals(r10)     // Catch:{ CertPathReviewerException -> 0x0238 }
            if (r10 != 0) goto L_0x01a7
            org.bouncycastle.i18n.ErrorBundle r10 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x01a0 }
            java.lang.String r15 = "CertPathReviewer.onlineCRLWrongCA"
            r18 = r11
            r26 = r12
            r11 = 3
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ CertPathReviewerException -> 0x019d }
            org.bouncycastle.i18n.filter.UntrustedInput r11 = new org.bouncycastle.i18n.filter.UntrustedInput     // Catch:{ CertPathReviewerException -> 0x019d }
            javax.security.auth.x500.X500Principal r14 = r14.getIssuerX500Principal()     // Catch:{ CertPathReviewerException -> 0x019d }
            java.lang.String r14 = r14.getName()     // Catch:{ CertPathReviewerException -> 0x019d }
            r11.<init>(r14)     // Catch:{ CertPathReviewerException -> 0x019d }
            r14 = 0
            r12[r14] = r11     // Catch:{ CertPathReviewerException -> 0x019d }
            org.bouncycastle.i18n.filter.UntrustedInput r11 = new org.bouncycastle.i18n.filter.UntrustedInput     // Catch:{ CertPathReviewerException -> 0x019d }
            javax.security.auth.x500.X500Principal r14 = r22.getIssuerX500Principal()     // Catch:{ CertPathReviewerException -> 0x019d }
            java.lang.String r14 = r14.getName()     // Catch:{ CertPathReviewerException -> 0x019d }
            r11.<init>(r14)     // Catch:{ CertPathReviewerException -> 0x019d }
            r14 = 1
            r12[r14] = r11     // Catch:{ CertPathReviewerException -> 0x019d }
            org.bouncycastle.i18n.filter.UntrustedUrlInput r11 = new org.bouncycastle.i18n.filter.UntrustedUrlInput     // Catch:{ CertPathReviewerException -> 0x019d }
            r11.<init>(r0)     // Catch:{ CertPathReviewerException -> 0x019d }
            r14 = 2
            r12[r14] = r11     // Catch:{ CertPathReviewerException -> 0x019d }
            r10.<init>(r9, r15, r12)     // Catch:{ CertPathReviewerException -> 0x019d }
            r1.addNotification(r10, r5)     // Catch:{ CertPathReviewerException -> 0x019d }
        L_0x0199:
            r19 = r13
            goto L_0x0230
        L_0x019d:
            r0 = move-exception
            goto L_0x023f
        L_0x01a0:
            r0 = move-exception
            r18 = r11
            r26 = r12
            goto L_0x023f
        L_0x01a7:
            r18 = r11
            r26 = r12
            java.util.Date r10 = r14.getNextUpdate()     // Catch:{ CertPathReviewerException -> 0x0228 }
            if (r10 == 0) goto L_0x01f6
            java.security.cert.PKIXParameters r10 = r1.pkixParams     // Catch:{ CertPathReviewerException -> 0x0228 }
            java.util.Date r10 = r10.getDate()     // Catch:{ CertPathReviewerException -> 0x0228 }
            java.util.Date r11 = r14.getNextUpdate()     // Catch:{ CertPathReviewerException -> 0x0228 }
            boolean r10 = r10.before(r11)     // Catch:{ CertPathReviewerException -> 0x0228 }
            if (r10 == 0) goto L_0x01c2
            goto L_0x01f6
        L_0x01c2:
            org.bouncycastle.i18n.ErrorBundle r10 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x0228 }
            java.lang.String r11 = "CertPathReviewer.onlineInvalidCRL"
            r12 = 3
            java.lang.Object[] r15 = new java.lang.Object[r12]     // Catch:{ CertPathReviewerException -> 0x0228 }
            org.bouncycastle.i18n.filter.TrustedInput r12 = new org.bouncycastle.i18n.filter.TrustedInput     // Catch:{ CertPathReviewerException -> 0x0228 }
            r19 = r13
            java.util.Date r13 = r14.getThisUpdate()     // Catch:{ CertPathReviewerException -> 0x01f2 }
            r12.<init>(r13)     // Catch:{ CertPathReviewerException -> 0x01f2 }
            r13 = 0
            r15[r13] = r12     // Catch:{ CertPathReviewerException -> 0x01f2 }
            org.bouncycastle.i18n.filter.TrustedInput r12 = new org.bouncycastle.i18n.filter.TrustedInput     // Catch:{ CertPathReviewerException -> 0x01f2 }
            java.util.Date r13 = r14.getNextUpdate()     // Catch:{ CertPathReviewerException -> 0x01f2 }
            r12.<init>(r13)     // Catch:{ CertPathReviewerException -> 0x01f2 }
            r13 = 1
            r15[r13] = r12     // Catch:{ CertPathReviewerException -> 0x01f2 }
            org.bouncycastle.i18n.filter.UntrustedUrlInput r12 = new org.bouncycastle.i18n.filter.UntrustedUrlInput     // Catch:{ CertPathReviewerException -> 0x01f2 }
            r12.<init>(r0)     // Catch:{ CertPathReviewerException -> 0x01f2 }
            r13 = 2
            r15[r13] = r12     // Catch:{ CertPathReviewerException -> 0x01f2 }
            r10.<init>(r9, r11, r15)     // Catch:{ CertPathReviewerException -> 0x01f2 }
            r1.addNotification(r10, r5)     // Catch:{ CertPathReviewerException -> 0x01f2 }
            goto L_0x0230
        L_0x01f2:
            r0 = move-exception
            r13 = r19
            goto L_0x023f
        L_0x01f6:
            org.bouncycastle.i18n.ErrorBundle r10 = new org.bouncycastle.i18n.ErrorBundle     // Catch:{ CertPathReviewerException -> 0x0225 }
            java.lang.String r11 = "CertPathReviewer.onlineValidCRL"
            r12 = 3
            java.lang.Object[] r13 = new java.lang.Object[r12]     // Catch:{ CertPathReviewerException -> 0x0225 }
            org.bouncycastle.i18n.filter.TrustedInput r15 = new org.bouncycastle.i18n.filter.TrustedInput     // Catch:{ CertPathReviewerException -> 0x0225 }
            java.util.Date r12 = r14.getThisUpdate()     // Catch:{ CertPathReviewerException -> 0x0225 }
            r15.<init>(r12)     // Catch:{ CertPathReviewerException -> 0x0225 }
            r12 = 0
            r13[r12] = r15     // Catch:{ CertPathReviewerException -> 0x0225 }
            org.bouncycastle.i18n.filter.TrustedInput r12 = new org.bouncycastle.i18n.filter.TrustedInput     // Catch:{ CertPathReviewerException -> 0x0225 }
            java.util.Date r15 = r14.getNextUpdate()     // Catch:{ CertPathReviewerException -> 0x0225 }
            r12.<init>(r15)     // Catch:{ CertPathReviewerException -> 0x0225 }
            r15 = 1
            r13[r15] = r12     // Catch:{ CertPathReviewerException -> 0x0225 }
            org.bouncycastle.i18n.filter.UntrustedUrlInput r12 = new org.bouncycastle.i18n.filter.UntrustedUrlInput     // Catch:{ CertPathReviewerException -> 0x0225 }
            r12.<init>(r0)     // Catch:{ CertPathReviewerException -> 0x0225 }
            r15 = 2
            r13[r15] = r12     // Catch:{ CertPathReviewerException -> 0x0225 }
            r10.<init>(r9, r11, r13)     // Catch:{ CertPathReviewerException -> 0x0225 }
            r1.addNotification(r10, r5)     // Catch:{ CertPathReviewerException -> 0x0225 }
            r13 = 1
            goto L_0x0255
        L_0x0225:
            r0 = move-exception
            r13 = 1
            goto L_0x023f
        L_0x0228:
            r0 = move-exception
            goto L_0x023d
        L_0x022a:
            r18 = r11
            r26 = r12
            goto L_0x0199
        L_0x0230:
            r12 = r26
            r11 = r18
            r13 = r19
            goto L_0x0140
        L_0x0238:
            r0 = move-exception
            r18 = r11
            r26 = r12
        L_0x023d:
            r19 = r13
        L_0x023f:
            org.bouncycastle.i18n.ErrorBundle r0 = r0.getErrorMessage()
            r1.addNotification(r0, r5)
            r12 = r26
            r11 = r18
            goto L_0x0140
        L_0x024c:
            r18 = r11
            r19 = r13
            goto L_0x0253
        L_0x0251:
            r18 = r11
        L_0x0253:
            r14 = r18
        L_0x0255:
            if (r14 == 0) goto L_0x0495
            r0 = 7
            if (r24 == 0) goto L_0x0276
            boolean[] r10 = r24.getKeyUsage()
            if (r10 == 0) goto L_0x0276
            int r11 = r10.length
            if (r11 < r0) goto L_0x0269
            r11 = 6
            boolean r10 = r10[r11]
            if (r10 == 0) goto L_0x0269
            goto L_0x0276
        L_0x0269:
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r2 = "CertPathReviewer.noCrlSigningPermited"
            r0.<init>(r9, r2)
            org.bouncycastle.x509.CertPathReviewerException r2 = new org.bouncycastle.x509.CertPathReviewerException
            r2.<init>(r0)
            throw r2
        L_0x0276:
            if (r4 == 0) goto L_0x0488
            java.lang.String r10 = "BC"
            r14.verify(r4, r10)     // Catch:{ Exception -> 0x047a }
            java.math.BigInteger r4 = r22.getSerialNumber()
            java.security.cert.X509CRLEntry r4 = r14.getRevokedCertificate(r4)
            if (r4 == 0) goto L_0x030c
            boolean r10 = r4.hasExtensions()
            if (r10 == 0) goto L_0x02b8
            org.bouncycastle.asn1.ASN1ObjectIdentifier r10 = org.bouncycastle.asn1.x509.Extension.reasonCode     // Catch:{ AnnotatedException -> 0x02aa }
            java.lang.String r10 = r10.getId()     // Catch:{ AnnotatedException -> 0x02aa }
            org.bouncycastle.asn1.ASN1Primitive r10 = getExtensionValue(r4, r10)     // Catch:{ AnnotatedException -> 0x02aa }
            org.bouncycastle.asn1.ASN1Enumerated r10 = org.bouncycastle.asn1.ASN1Enumerated.getInstance(r10)     // Catch:{ AnnotatedException -> 0x02aa }
            if (r10 == 0) goto L_0x02b8
            java.lang.String[] r11 = crlReasons
            java.math.BigInteger r10 = r10.getValue()
            int r10 = r10.intValue()
            r10 = r11[r10]
            goto L_0x02b9
        L_0x02aa:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r3 = "CertPathReviewer.crlReasonExtError"
            r2.<init>(r9, r3)
            org.bouncycastle.x509.CertPathReviewerException r3 = new org.bouncycastle.x509.CertPathReviewerException
            r3.<init>(r2, r0)
            throw r3
        L_0x02b8:
            r10 = 0
        L_0x02b9:
            if (r10 != 0) goto L_0x02bf
            java.lang.String[] r10 = crlReasons
            r10 = r10[r0]
        L_0x02bf:
            org.bouncycastle.i18n.LocaleString r0 = new org.bouncycastle.i18n.LocaleString
            r0.<init>(r9, r10)
            java.util.Date r10 = r4.getRevocationDate()
            r11 = r23
            boolean r10 = r11.before(r10)
            if (r10 == 0) goto L_0x02ed
            org.bouncycastle.i18n.ErrorBundle r10 = new org.bouncycastle.i18n.ErrorBundle
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]
            org.bouncycastle.i18n.filter.TrustedInput r12 = new org.bouncycastle.i18n.filter.TrustedInput
            java.util.Date r4 = r4.getRevocationDate()
            r12.<init>(r4)
            r15 = 0
            r11[r15] = r12
            r12 = 1
            r11[r12] = r0
            java.lang.String r0 = "CertPathReviewer.revokedAfterValidation"
            r10.<init>(r9, r0, r11)
            r1.addNotification(r10, r5)
            goto L_0x0316
        L_0x02ed:
            r11 = 2
            r12 = 1
            r15 = 0
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.Object[] r3 = new java.lang.Object[r11]
            org.bouncycastle.i18n.filter.TrustedInput r5 = new org.bouncycastle.i18n.filter.TrustedInput
            java.util.Date r4 = r4.getRevocationDate()
            r5.<init>(r4)
            r3[r15] = r5
            r3[r12] = r0
            java.lang.String r0 = "CertPathReviewer.certRevoked"
            r2.<init>(r9, r0, r3)
            org.bouncycastle.x509.CertPathReviewerException r0 = new org.bouncycastle.x509.CertPathReviewerException
            r0.<init>(r2)
            throw r0
        L_0x030c:
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r4 = "CertPathReviewer.notRevoked"
            r0.<init>(r9, r4)
            r1.addNotification(r0, r5)
        L_0x0316:
            java.util.Date r0 = r14.getNextUpdate()
            if (r0 == 0) goto L_0x0347
            java.util.Date r0 = r14.getNextUpdate()
            java.security.cert.PKIXParameters r4 = r1.pkixParams
            java.util.Date r4 = r4.getDate()
            boolean r0 = r0.before(r4)
            if (r0 == 0) goto L_0x0347
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            r4 = 1
            java.lang.Object[] r10 = new java.lang.Object[r4]
            org.bouncycastle.i18n.filter.TrustedInput r11 = new org.bouncycastle.i18n.filter.TrustedInput
            java.util.Date r12 = r14.getNextUpdate()
            r11.<init>(r12)
            r16 = 0
            r10[r16] = r11
            java.lang.String r11 = "CertPathReviewer.crlUpdateAvailable"
            r0.<init>(r9, r11, r10)
            r1.addNotification(r0, r5)
            goto L_0x034a
        L_0x0347:
            r4 = 1
            r16 = 0
        L_0x034a:
            java.lang.String r0 = ISSUING_DISTRIBUTION_POINT     // Catch:{ AnnotatedException -> 0x046e }
            org.bouncycastle.asn1.ASN1Primitive r0 = getExtensionValue(r14, r0)     // Catch:{ AnnotatedException -> 0x046e }
            java.lang.String r5 = DELTA_CRL_INDICATOR     // Catch:{ AnnotatedException -> 0x0460 }
            org.bouncycastle.asn1.ASN1Primitive r5 = getExtensionValue(r14, r5)     // Catch:{ AnnotatedException -> 0x0460 }
            if (r5 == 0) goto L_0x03f6
            org.bouncycastle.x509.X509CRLStoreSelector r10 = new org.bouncycastle.x509.X509CRLStoreSelector
            r10.<init>()
            javax.security.auth.x500.X500Principal r11 = getIssuerPrincipal(r14)     // Catch:{ IOException -> 0x03ea }
            byte[] r11 = r11.getEncoded()     // Catch:{ IOException -> 0x03ea }
            r10.addIssuerName(r11)     // Catch:{ IOException -> 0x03ea }
            org.bouncycastle.asn1.ASN1Integer r5 = (org.bouncycastle.asn1.ASN1Integer) r5
            java.math.BigInteger r5 = r5.getPositiveValue()
            r10.setMinCRLNumber(r5)
            java.lang.String r5 = CRL_NUMBER     // Catch:{ AnnotatedException -> 0x03dc }
            org.bouncycastle.asn1.ASN1Primitive r5 = getExtensionValue(r14, r5)     // Catch:{ AnnotatedException -> 0x03dc }
            org.bouncycastle.asn1.ASN1Integer r5 = (org.bouncycastle.asn1.ASN1Integer) r5     // Catch:{ AnnotatedException -> 0x03dc }
            java.math.BigInteger r5 = r5.getPositiveValue()     // Catch:{ AnnotatedException -> 0x03dc }
            r11 = 1
            java.math.BigInteger r8 = java.math.BigInteger.valueOf(r11)     // Catch:{ AnnotatedException -> 0x03dc }
            java.math.BigInteger r5 = r5.subtract(r8)     // Catch:{ AnnotatedException -> 0x03dc }
            r10.setMaxCRLNumber(r5)     // Catch:{ AnnotatedException -> 0x03dc }
            org.bouncycastle.x509.PKIXCRLUtil r5 = CRL_UTIL     // Catch:{ AnnotatedException -> 0x03d0 }
            java.util.Set r2 = r5.findCRLs(r10, r2)     // Catch:{ AnnotatedException -> 0x03d0 }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ AnnotatedException -> 0x03d0 }
        L_0x0394:
            boolean r5 = r2.hasNext()
            if (r5 == 0) goto L_0x03be
            java.lang.Object r5 = r2.next()
            java.security.cert.X509CRL r5 = (java.security.cert.X509CRL) r5
            java.lang.String r7 = ISSUING_DISTRIBUTION_POINT     // Catch:{ AnnotatedException -> 0x03b2 }
            org.bouncycastle.asn1.ASN1Primitive r5 = getExtensionValue(r5, r7)     // Catch:{ AnnotatedException -> 0x03b2 }
            if (r0 != 0) goto L_0x03ab
            if (r5 != 0) goto L_0x0394
            goto L_0x03c0
        L_0x03ab:
            boolean r5 = r0.equals(r5)
            if (r5 == 0) goto L_0x0394
            goto L_0x03c0
        L_0x03b2:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle
            r2.<init>(r9, r6)
            org.bouncycastle.x509.CertPathReviewerException r3 = new org.bouncycastle.x509.CertPathReviewerException
            r3.<init>(r2, r0)
            throw r3
        L_0x03be:
            r4 = r16
        L_0x03c0:
            if (r4 == 0) goto L_0x03c3
            goto L_0x03f6
        L_0x03c3:
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r2 = "CertPathReviewer.noBaseCRL"
            r0.<init>(r9, r2)
            org.bouncycastle.x509.CertPathReviewerException r2 = new org.bouncycastle.x509.CertPathReviewerException
            r2.<init>(r0)
            throw r2
        L_0x03d0:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle
            r2.<init>(r9, r7)
            org.bouncycastle.x509.CertPathReviewerException r3 = new org.bouncycastle.x509.CertPathReviewerException
            r3.<init>(r2, r0)
            throw r3
        L_0x03dc:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r3 = "CertPathReviewer.crlNbrExtError"
            r2.<init>(r9, r3)
            org.bouncycastle.x509.CertPathReviewerException r3 = new org.bouncycastle.x509.CertPathReviewerException
            r3.<init>(r2, r0)
            throw r3
        L_0x03ea:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle
            r2.<init>(r9, r8)
            org.bouncycastle.x509.CertPathReviewerException r3 = new org.bouncycastle.x509.CertPathReviewerException
            r3.<init>(r2, r0)
            throw r3
        L_0x03f6:
            if (r0 == 0) goto L_0x0495
            org.bouncycastle.asn1.x509.IssuingDistributionPoint r0 = org.bouncycastle.asn1.x509.IssuingDistributionPoint.getInstance(r0)
            java.lang.String r2 = BASIC_CONSTRAINTS     // Catch:{ AnnotatedException -> 0x0452 }
            org.bouncycastle.asn1.ASN1Primitive r2 = getExtensionValue(r3, r2)     // Catch:{ AnnotatedException -> 0x0452 }
            org.bouncycastle.asn1.x509.BasicConstraints r2 = org.bouncycastle.asn1.x509.BasicConstraints.getInstance(r2)     // Catch:{ AnnotatedException -> 0x0452 }
            boolean r3 = r0.onlyContainsUserCerts()
            if (r3 == 0) goto L_0x0422
            if (r2 == 0) goto L_0x0422
            boolean r3 = r2.isCA()
            if (r3 != 0) goto L_0x0415
            goto L_0x0422
        L_0x0415:
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r2 = "CertPathReviewer.crlOnlyUserCert"
            r0.<init>(r9, r2)
            org.bouncycastle.x509.CertPathReviewerException r2 = new org.bouncycastle.x509.CertPathReviewerException
            r2.<init>(r0)
            throw r2
        L_0x0422:
            boolean r3 = r0.onlyContainsCACerts()
            if (r3 == 0) goto L_0x043e
            if (r2 == 0) goto L_0x0431
            boolean r2 = r2.isCA()
            if (r2 == 0) goto L_0x0431
            goto L_0x043e
        L_0x0431:
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r2 = "CertPathReviewer.crlOnlyCaCert"
            r0.<init>(r9, r2)
            org.bouncycastle.x509.CertPathReviewerException r2 = new org.bouncycastle.x509.CertPathReviewerException
            r2.<init>(r0)
            throw r2
        L_0x043e:
            boolean r0 = r0.onlyContainsAttributeCerts()
            if (r0 != 0) goto L_0x0445
            goto L_0x0495
        L_0x0445:
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r2 = "CertPathReviewer.crlOnlyAttrCert"
            r0.<init>(r9, r2)
            org.bouncycastle.x509.CertPathReviewerException r2 = new org.bouncycastle.x509.CertPathReviewerException
            r2.<init>(r0)
            throw r2
        L_0x0452:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r3 = "CertPathReviewer.crlBCExtError"
            r2.<init>(r9, r3)
            org.bouncycastle.x509.CertPathReviewerException r3 = new org.bouncycastle.x509.CertPathReviewerException
            r3.<init>(r2, r0)
            throw r3
        L_0x0460:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r2 = "CertPathReviewer.deltaCrlExtError"
            r0.<init>(r9, r2)
            org.bouncycastle.x509.CertPathReviewerException r2 = new org.bouncycastle.x509.CertPathReviewerException
            r2.<init>(r0)
            throw r2
        L_0x046e:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            r0.<init>(r9, r6)
            org.bouncycastle.x509.CertPathReviewerException r2 = new org.bouncycastle.x509.CertPathReviewerException
            r2.<init>(r0)
            throw r2
        L_0x047a:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r3 = "CertPathReviewer.crlVerifyFailed"
            r2.<init>(r9, r3)
            org.bouncycastle.x509.CertPathReviewerException r3 = new org.bouncycastle.x509.CertPathReviewerException
            r3.<init>(r2, r0)
            throw r3
        L_0x0488:
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r2 = "CertPathReviewer.crlNoIssuerPublicKey"
            r0.<init>(r9, r2)
            org.bouncycastle.x509.CertPathReviewerException r2 = new org.bouncycastle.x509.CertPathReviewerException
            r2.<init>(r0)
            throw r2
        L_0x0495:
            if (r13 == 0) goto L_0x0498
            return
        L_0x0498:
            org.bouncycastle.i18n.ErrorBundle r0 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r2 = "CertPathReviewer.noValidCrlFound"
            r0.<init>(r9, r2)
            org.bouncycastle.x509.CertPathReviewerException r2 = new org.bouncycastle.x509.CertPathReviewerException
            r2.<init>(r0)
            throw r2
        L_0x04a5:
            r0 = move-exception
            org.bouncycastle.i18n.ErrorBundle r2 = new org.bouncycastle.i18n.ErrorBundle
            r2.<init>(r9, r8)
            org.bouncycastle.x509.CertPathReviewerException r3 = new org.bouncycastle.x509.CertPathReviewerException
            r3.<init>(r2, r0)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.x509.PKIXCertPathReviewer.checkCRLs(java.security.cert.PKIXParameters, java.security.cert.X509Certificate, java.util.Date, java.security.cert.X509Certificate, java.security.PublicKey, java.util.Vector, int):void");
    }

    /* access modifiers changed from: protected */
    public void checkRevocation(PKIXParameters pKIXParameters, X509Certificate x509Certificate, Date date, X509Certificate x509Certificate2, PublicKey publicKey, Vector vector, Vector vector2, int i) throws CertPathReviewerException {
        checkCRLs(pKIXParameters, x509Certificate, date, x509Certificate2, publicKey, vector, i);
    }

    /* access modifiers changed from: protected */
    public void doChecks() {
        if (!this.initialized) {
            throw new IllegalStateException("Object not initialized. Call init() first.");
        } else if (this.notifications == null) {
            int i = this.n;
            this.notifications = new List[(i + 1)];
            this.errors = new List[(i + 1)];
            int i2 = 0;
            while (true) {
                List[] listArr = this.notifications;
                if (i2 < listArr.length) {
                    listArr[i2] = new ArrayList();
                    this.errors[i2] = new ArrayList();
                    i2++;
                } else {
                    checkSignatures();
                    checkNameConstraints();
                    checkPathLength();
                    checkPolicy();
                    checkCriticalExtensions();
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public Vector getCRLDistUrls(CRLDistPoint cRLDistPoint) {
        Vector vector = new Vector();
        if (cRLDistPoint != null) {
            DistributionPoint[] distributionPoints = cRLDistPoint.getDistributionPoints();
            for (DistributionPoint distributionPoint : distributionPoints) {
                DistributionPointName distributionPoint2 = distributionPoint.getDistributionPoint();
                if (distributionPoint2.getType() == 0) {
                    GeneralName[] names = GeneralNames.getInstance(distributionPoint2.getName()).getNames();
                    for (int i = 0; i < names.length; i++) {
                        if (names[i].getTagNo() == 6) {
                            vector.add(((DERIA5String) names[i].getName()).getString());
                        }
                    }
                }
            }
        }
        return vector;
    }

    public CertPath getCertPath() {
        return this.certPath;
    }

    public int getCertPathSize() {
        return this.n;
    }

    public List getErrors(int i) {
        doChecks();
        return this.errors[i + 1];
    }

    public List[] getErrors() {
        doChecks();
        return this.errors;
    }

    public List getNotifications(int i) {
        doChecks();
        return this.notifications[i + 1];
    }

    public List[] getNotifications() {
        doChecks();
        return this.notifications;
    }

    /* access modifiers changed from: protected */
    public Vector getOCSPUrls(AuthorityInformationAccess authorityInformationAccess) {
        Vector vector = new Vector();
        if (authorityInformationAccess != null) {
            AccessDescription[] accessDescriptions = authorityInformationAccess.getAccessDescriptions();
            for (int i = 0; i < accessDescriptions.length; i++) {
                if (accessDescriptions[i].getAccessMethod().equals(AccessDescription.id_ad_ocsp)) {
                    GeneralName accessLocation = accessDescriptions[i].getAccessLocation();
                    if (accessLocation.getTagNo() == 6) {
                        vector.add(((DERIA5String) accessLocation.getName()).getString());
                    }
                }
            }
        }
        return vector;
    }

    public PolicyNode getPolicyTree() {
        doChecks();
        return this.policyTree;
    }

    public PublicKey getSubjectPublicKey() {
        doChecks();
        return this.subjectPublicKey;
    }

    public TrustAnchor getTrustAnchor() {
        doChecks();
        return this.trustAnchor;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.security.cert.TrustAnchor>, for r8v0, types: [java.util.Set<java.security.cert.TrustAnchor>, java.util.Set] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.Collection getTrustAnchors(java.security.cert.X509Certificate r7, java.util.Set<java.security.cert.TrustAnchor> r8) throws org.bouncycastle.x509.CertPathReviewerException {
        /*
            r6 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.Iterator r8 = r8.iterator()
            java.security.cert.X509CertSelector r1 = new java.security.cert.X509CertSelector
            r1.<init>()
            javax.security.auth.x500.X500Principal r2 = getEncodedIssuerPrincipal(r7)     // Catch:{ IOException -> 0x0091 }
            byte[] r2 = r2.getEncoded()     // Catch:{ IOException -> 0x0091 }
            r1.setSubject(r2)     // Catch:{ IOException -> 0x0091 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = org.bouncycastle.asn1.x509.Extension.authorityKeyIdentifier     // Catch:{ IOException -> 0x0091 }
            java.lang.String r2 = r2.getId()     // Catch:{ IOException -> 0x0091 }
            byte[] r2 = r7.getExtensionValue(r2)     // Catch:{ IOException -> 0x0091 }
            if (r2 == 0) goto L_0x0050
            org.bouncycastle.asn1.ASN1Primitive r2 = org.bouncycastle.asn1.ASN1Primitive.fromByteArray(r2)     // Catch:{ IOException -> 0x0091 }
            org.bouncycastle.asn1.ASN1OctetString r2 = (org.bouncycastle.asn1.ASN1OctetString) r2     // Catch:{ IOException -> 0x0091 }
            byte[] r2 = r2.getOctets()     // Catch:{ IOException -> 0x0091 }
            org.bouncycastle.asn1.ASN1Primitive r2 = org.bouncycastle.asn1.ASN1Primitive.fromByteArray(r2)     // Catch:{ IOException -> 0x0091 }
            org.bouncycastle.asn1.x509.AuthorityKeyIdentifier r2 = org.bouncycastle.asn1.x509.AuthorityKeyIdentifier.getInstance(r2)     // Catch:{ IOException -> 0x0091 }
            java.math.BigInteger r3 = r2.getAuthorityCertSerialNumber()     // Catch:{ IOException -> 0x0091 }
            r1.setSerialNumber(r3)     // Catch:{ IOException -> 0x0091 }
            byte[] r2 = r2.getKeyIdentifier()     // Catch:{ IOException -> 0x0091 }
            if (r2 == 0) goto L_0x0050
            org.bouncycastle.asn1.DEROctetString r3 = new org.bouncycastle.asn1.DEROctetString     // Catch:{ IOException -> 0x0091 }
            r3.<init>(r2)     // Catch:{ IOException -> 0x0091 }
            byte[] r2 = r3.getEncoded()     // Catch:{ IOException -> 0x0091 }
            r1.setSubjectKeyIdentifier(r2)     // Catch:{ IOException -> 0x0091 }
        L_0x0050:
            boolean r2 = r8.hasNext()
            if (r2 == 0) goto L_0x0090
            java.lang.Object r2 = r8.next()
            java.security.cert.TrustAnchor r2 = (java.security.cert.TrustAnchor) r2
            java.security.cert.X509Certificate r3 = r2.getTrustedCert()
            if (r3 == 0) goto L_0x0070
            java.security.cert.X509Certificate r3 = r2.getTrustedCert()
            boolean r3 = r1.match(r3)
            if (r3 == 0) goto L_0x0050
        L_0x006c:
            r0.add(r2)
            goto L_0x0050
        L_0x0070:
            java.lang.String r3 = r2.getCAName()
            if (r3 == 0) goto L_0x0050
            java.security.PublicKey r3 = r2.getCAPublicKey()
            if (r3 == 0) goto L_0x0050
            javax.security.auth.x500.X500Principal r3 = getEncodedIssuerPrincipal(r7)
            javax.security.auth.x500.X500Principal r4 = new javax.security.auth.x500.X500Principal
            java.lang.String r5 = r2.getCAName()
            r4.<init>(r5)
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x0050
            goto L_0x006c
        L_0x0090:
            return r0
        L_0x0091:
            r7 = move-exception
            org.bouncycastle.i18n.ErrorBundle r7 = new org.bouncycastle.i18n.ErrorBundle
            java.lang.String r8 = "org.bouncycastle.x509.CertPathReviewerMessages"
            java.lang.String r0 = "CertPathReviewer.trustAnchorIssuerError"
            r7.<init>(r8, r0)
            org.bouncycastle.x509.CertPathReviewerException r8 = new org.bouncycastle.x509.CertPathReviewerException
            r8.<init>(r7)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.x509.PKIXCertPathReviewer.getTrustAnchors(java.security.cert.X509Certificate, java.util.Set):java.util.Collection");
    }

    public void init(CertPath certPath2, PKIXParameters pKIXParameters) throws CertPathReviewerException {
        if (!this.initialized) {
            this.initialized = true;
            if (certPath2 != null) {
                this.certPath = certPath2;
                this.certs = certPath2.getCertificates();
                this.n = this.certs.size();
                if (!this.certs.isEmpty()) {
                    this.pkixParams = (PKIXParameters) pKIXParameters.clone();
                    this.validDate = getValidDate(this.pkixParams);
                    this.notifications = null;
                    this.errors = null;
                    this.trustAnchor = null;
                    this.subjectPublicKey = null;
                    this.policyTree = null;
                    return;
                }
                throw new CertPathReviewerException(new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.emptyCertPath"));
            }
            throw new NullPointerException("certPath was null");
        }
        throw new IllegalStateException("object is already initialized!");
    }

    public boolean isValidCertPath() {
        doChecks();
        int i = 0;
        while (true) {
            List[] listArr = this.errors;
            if (i >= listArr.length) {
                return true;
            }
            if (!listArr[i].isEmpty()) {
                return false;
            }
            i++;
        }
    }
}
