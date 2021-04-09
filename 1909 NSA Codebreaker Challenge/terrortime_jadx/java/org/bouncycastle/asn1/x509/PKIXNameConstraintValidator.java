package org.bouncycastle.asn1.x509;

import com.badguy.terrortime.BuildConfig;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;

public class PKIXNameConstraintValidator implements NameConstraintValidator {
    private Set excludedSubtreesDN = new HashSet();
    private Set excludedSubtreesDNS = new HashSet();
    private Set excludedSubtreesEmail = new HashSet();
    private Set excludedSubtreesIP = new HashSet();
    private Set excludedSubtreesOtherName = new HashSet();
    private Set excludedSubtreesURI = new HashSet();
    private Set permittedSubtreesDN;
    private Set permittedSubtreesDNS;
    private Set permittedSubtreesEmail;
    private Set permittedSubtreesIP;
    private Set permittedSubtreesOtherName;
    private Set permittedSubtreesURI;

    private final void addLine(StringBuilder sb, String str) {
        sb.append(str);
        sb.append(Strings.lineSeparator());
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<org.bouncycastle.asn1.ASN1Sequence>, for r2v0, types: [java.util.Set<org.bouncycastle.asn1.ASN1Sequence>, java.util.Set] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkExcludedDN(java.util.Set<org.bouncycastle.asn1.ASN1Sequence> r2, org.bouncycastle.asn1.ASN1Sequence r3) throws org.bouncycastle.asn1.x509.NameConstraintValidatorException {
        /*
            r1 = this;
            boolean r0 = r2.isEmpty()
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            java.util.Iterator r2 = r2.iterator()
        L_0x000b:
            boolean r0 = r2.hasNext()
            if (r0 == 0) goto L_0x0026
            java.lang.Object r0 = r2.next()
            org.bouncycastle.asn1.ASN1Sequence r0 = (org.bouncycastle.asn1.ASN1Sequence) r0
            boolean r0 = withinDNSubtree(r3, r0)
            if (r0 != 0) goto L_0x001e
            goto L_0x000b
        L_0x001e:
            org.bouncycastle.asn1.x509.NameConstraintValidatorException r2 = new org.bouncycastle.asn1.x509.NameConstraintValidatorException
            java.lang.String r3 = "Subject distinguished name is from an excluded subtree"
            r2.<init>(r3)
            throw r2
        L_0x0026:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.checkExcludedDN(java.util.Set, org.bouncycastle.asn1.ASN1Sequence):void");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.lang.String>, for r3v0, types: [java.util.Set, java.util.Set<java.lang.String>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkExcludedDNS(java.util.Set<java.lang.String> r3, java.lang.String r4) throws org.bouncycastle.asn1.x509.NameConstraintValidatorException {
        /*
            r2 = this;
            boolean r0 = r3.isEmpty()
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            java.util.Iterator r3 = r3.iterator()
        L_0x000b:
            boolean r0 = r3.hasNext()
            if (r0 == 0) goto L_0x002c
            java.lang.Object r0 = r3.next()
            java.lang.String r0 = (java.lang.String) r0
            boolean r1 = r2.withinDomain(r4, r0)
            if (r1 != 0) goto L_0x0024
            boolean r0 = r4.equalsIgnoreCase(r0)
            if (r0 != 0) goto L_0x0024
            goto L_0x000b
        L_0x0024:
            org.bouncycastle.asn1.x509.NameConstraintValidatorException r3 = new org.bouncycastle.asn1.x509.NameConstraintValidatorException
            java.lang.String r4 = "DNS is from an excluded subtree."
            r3.<init>(r4)
            throw r3
        L_0x002c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.checkExcludedDNS(java.util.Set, java.lang.String):void");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.lang.String>, for r2v0, types: [java.util.Set, java.util.Set<java.lang.String>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkExcludedEmail(java.util.Set<java.lang.String> r2, java.lang.String r3) throws org.bouncycastle.asn1.x509.NameConstraintValidatorException {
        /*
            r1 = this;
            boolean r0 = r2.isEmpty()
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            java.util.Iterator r2 = r2.iterator()
        L_0x000b:
            boolean r0 = r2.hasNext()
            if (r0 == 0) goto L_0x0026
            java.lang.Object r0 = r2.next()
            java.lang.String r0 = (java.lang.String) r0
            boolean r0 = r1.emailIsConstrained(r3, r0)
            if (r0 != 0) goto L_0x001e
            goto L_0x000b
        L_0x001e:
            org.bouncycastle.asn1.x509.NameConstraintValidatorException r2 = new org.bouncycastle.asn1.x509.NameConstraintValidatorException
            java.lang.String r3 = "Email address is from an excluded subtree."
            r2.<init>(r3)
            throw r2
        L_0x0026:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.checkExcludedEmail(java.util.Set, java.lang.String):void");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<byte[]>, for r2v0, types: [java.util.Set, java.util.Set<byte[]>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkExcludedIP(java.util.Set<byte[]> r2, byte[] r3) throws org.bouncycastle.asn1.x509.NameConstraintValidatorException {
        /*
            r1 = this;
            boolean r0 = r2.isEmpty()
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            java.util.Iterator r2 = r2.iterator()
        L_0x000b:
            boolean r0 = r2.hasNext()
            if (r0 == 0) goto L_0x0028
            java.lang.Object r0 = r2.next()
            byte[] r0 = (byte[]) r0
            byte[] r0 = (byte[]) r0
            boolean r0 = r1.isIPConstrained(r3, r0)
            if (r0 != 0) goto L_0x0020
            goto L_0x000b
        L_0x0020:
            org.bouncycastle.asn1.x509.NameConstraintValidatorException r2 = new org.bouncycastle.asn1.x509.NameConstraintValidatorException
            java.lang.String r3 = "IP is from an excluded subtree."
            r2.<init>(r3)
            throw r2
        L_0x0028:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.checkExcludedIP(java.util.Set, byte[]):void");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.lang.Object>, for r2v0, types: [java.util.Set<java.lang.Object>, java.util.Set] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkExcludedOtherName(java.util.Set<java.lang.Object> r2, org.bouncycastle.asn1.x509.OtherName r3) throws org.bouncycastle.asn1.x509.NameConstraintValidatorException {
        /*
            r1 = this;
            boolean r0 = r2.isEmpty()
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            java.util.Iterator r2 = r2.iterator()
        L_0x000b:
            boolean r0 = r2.hasNext()
            if (r0 == 0) goto L_0x0028
            java.lang.Object r0 = r2.next()
            org.bouncycastle.asn1.x509.OtherName r0 = org.bouncycastle.asn1.x509.OtherName.getInstance(r0)
            boolean r0 = r1.otherNameIsConstrained(r3, r0)
            if (r0 != 0) goto L_0x0020
            goto L_0x000b
        L_0x0020:
            org.bouncycastle.asn1.x509.NameConstraintValidatorException r2 = new org.bouncycastle.asn1.x509.NameConstraintValidatorException
            java.lang.String r3 = "OtherName is from an excluded subtree."
            r2.<init>(r3)
            throw r2
        L_0x0028:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.checkExcludedOtherName(java.util.Set, org.bouncycastle.asn1.x509.OtherName):void");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.lang.String>, for r2v0, types: [java.util.Set, java.util.Set<java.lang.String>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkExcludedURI(java.util.Set<java.lang.String> r2, java.lang.String r3) throws org.bouncycastle.asn1.x509.NameConstraintValidatorException {
        /*
            r1 = this;
            boolean r0 = r2.isEmpty()
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            java.util.Iterator r2 = r2.iterator()
        L_0x000b:
            boolean r0 = r2.hasNext()
            if (r0 == 0) goto L_0x0026
            java.lang.Object r0 = r2.next()
            java.lang.String r0 = (java.lang.String) r0
            boolean r0 = r1.isUriConstrained(r3, r0)
            if (r0 != 0) goto L_0x001e
            goto L_0x000b
        L_0x001e:
            org.bouncycastle.asn1.x509.NameConstraintValidatorException r2 = new org.bouncycastle.asn1.x509.NameConstraintValidatorException
            java.lang.String r3 = "URI is from an excluded subtree."
            r2.<init>(r3)
            throw r2
        L_0x0026:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.checkExcludedURI(java.util.Set, java.lang.String):void");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<org.bouncycastle.asn1.ASN1Sequence>, for r2v0, types: [java.util.Set<org.bouncycastle.asn1.ASN1Sequence>, java.util.Set] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkPermittedDN(java.util.Set<org.bouncycastle.asn1.ASN1Sequence> r2, org.bouncycastle.asn1.ASN1Sequence r3) throws org.bouncycastle.asn1.x509.NameConstraintValidatorException {
        /*
            r1 = this;
            if (r2 != 0) goto L_0x0003
            return
        L_0x0003:
            boolean r0 = r2.isEmpty()
            if (r0 == 0) goto L_0x0010
            int r0 = r3.size()
            if (r0 != 0) goto L_0x0010
            return
        L_0x0010:
            java.util.Iterator r2 = r2.iterator()
        L_0x0014:
            boolean r0 = r2.hasNext()
            if (r0 == 0) goto L_0x0027
            java.lang.Object r0 = r2.next()
            org.bouncycastle.asn1.ASN1Sequence r0 = (org.bouncycastle.asn1.ASN1Sequence) r0
            boolean r0 = withinDNSubtree(r3, r0)
            if (r0 == 0) goto L_0x0014
            return
        L_0x0027:
            org.bouncycastle.asn1.x509.NameConstraintValidatorException r2 = new org.bouncycastle.asn1.x509.NameConstraintValidatorException
            java.lang.String r3 = "Subject distinguished name is not from a permitted subtree"
            r2.<init>(r3)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.checkPermittedDN(java.util.Set, org.bouncycastle.asn1.ASN1Sequence):void");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.lang.String>, for r4v0, types: [java.util.Set, java.util.Set<java.lang.String>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkPermittedDNS(java.util.Set<java.lang.String> r4, java.lang.String r5) throws org.bouncycastle.asn1.x509.NameConstraintValidatorException {
        /*
            r3 = this;
            if (r4 != 0) goto L_0x0003
            return
        L_0x0003:
            java.util.Iterator r0 = r4.iterator()
        L_0x0007:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0020
            java.lang.Object r1 = r0.next()
            java.lang.String r1 = (java.lang.String) r1
            boolean r2 = r3.withinDomain(r5, r1)
            if (r2 != 0) goto L_0x001f
            boolean r1 = r5.equalsIgnoreCase(r1)
            if (r1 == 0) goto L_0x0007
        L_0x001f:
            return
        L_0x0020:
            int r5 = r5.length()
            if (r5 != 0) goto L_0x002d
            int r4 = r4.size()
            if (r4 != 0) goto L_0x002d
            return
        L_0x002d:
            org.bouncycastle.asn1.x509.NameConstraintValidatorException r4 = new org.bouncycastle.asn1.x509.NameConstraintValidatorException
            java.lang.String r5 = "DNS is not from a permitted subtree."
            r4.<init>(r5)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.checkPermittedDNS(java.util.Set, java.lang.String):void");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.lang.String>, for r3v0, types: [java.util.Set, java.util.Set<java.lang.String>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkPermittedEmail(java.util.Set<java.lang.String> r3, java.lang.String r4) throws org.bouncycastle.asn1.x509.NameConstraintValidatorException {
        /*
            r2 = this;
            if (r3 != 0) goto L_0x0003
            return
        L_0x0003:
            java.util.Iterator r0 = r3.iterator()
        L_0x0007:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x001a
            java.lang.Object r1 = r0.next()
            java.lang.String r1 = (java.lang.String) r1
            boolean r1 = r2.emailIsConstrained(r4, r1)
            if (r1 == 0) goto L_0x0007
            return
        L_0x001a:
            int r4 = r4.length()
            if (r4 != 0) goto L_0x0027
            int r3 = r3.size()
            if (r3 != 0) goto L_0x0027
            return
        L_0x0027:
            org.bouncycastle.asn1.x509.NameConstraintValidatorException r3 = new org.bouncycastle.asn1.x509.NameConstraintValidatorException
            java.lang.String r4 = "Subject email address is not from a permitted subtree."
            r3.<init>(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.checkPermittedEmail(java.util.Set, java.lang.String):void");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<byte[]>, for r3v0, types: [java.util.Set, java.util.Set<byte[]>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkPermittedIP(java.util.Set<byte[]> r3, byte[] r4) throws org.bouncycastle.asn1.x509.NameConstraintValidatorException {
        /*
            r2 = this;
            if (r3 != 0) goto L_0x0003
            return
        L_0x0003:
            java.util.Iterator r0 = r3.iterator()
        L_0x0007:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x001c
            java.lang.Object r1 = r0.next()
            byte[] r1 = (byte[]) r1
            byte[] r1 = (byte[]) r1
            boolean r1 = r2.isIPConstrained(r4, r1)
            if (r1 == 0) goto L_0x0007
            return
        L_0x001c:
            int r4 = r4.length
            if (r4 != 0) goto L_0x0026
            int r3 = r3.size()
            if (r3 != 0) goto L_0x0026
            return
        L_0x0026:
            org.bouncycastle.asn1.x509.NameConstraintValidatorException r3 = new org.bouncycastle.asn1.x509.NameConstraintValidatorException
            java.lang.String r4 = "IP is not from a permitted subtree."
            r3.<init>(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.checkPermittedIP(java.util.Set, byte[]):void");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<org.bouncycastle.asn1.x509.OtherName>, for r2v0, types: [java.util.Set, java.util.Set<org.bouncycastle.asn1.x509.OtherName>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkPermittedOtherName(java.util.Set<org.bouncycastle.asn1.x509.OtherName> r2, org.bouncycastle.asn1.x509.OtherName r3) throws org.bouncycastle.asn1.x509.NameConstraintValidatorException {
        /*
            r1 = this;
            if (r2 != 0) goto L_0x0003
            return
        L_0x0003:
            java.util.Iterator r2 = r2.iterator()
        L_0x0007:
            boolean r0 = r2.hasNext()
            if (r0 == 0) goto L_0x001a
            java.lang.Object r0 = r2.next()
            org.bouncycastle.asn1.x509.OtherName r0 = (org.bouncycastle.asn1.x509.OtherName) r0
            boolean r0 = r1.otherNameIsConstrained(r3, r0)
            if (r0 == 0) goto L_0x0007
            return
        L_0x001a:
            org.bouncycastle.asn1.x509.NameConstraintValidatorException r2 = new org.bouncycastle.asn1.x509.NameConstraintValidatorException
            java.lang.String r3 = "Subject OtherName is not from a permitted subtree."
            r2.<init>(r3)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.checkPermittedOtherName(java.util.Set, org.bouncycastle.asn1.x509.OtherName):void");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.lang.String>, for r3v0, types: [java.util.Set, java.util.Set<java.lang.String>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkPermittedURI(java.util.Set<java.lang.String> r3, java.lang.String r4) throws org.bouncycastle.asn1.x509.NameConstraintValidatorException {
        /*
            r2 = this;
            if (r3 != 0) goto L_0x0003
            return
        L_0x0003:
            java.util.Iterator r0 = r3.iterator()
        L_0x0007:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x001a
            java.lang.Object r1 = r0.next()
            java.lang.String r1 = (java.lang.String) r1
            boolean r1 = r2.isUriConstrained(r4, r1)
            if (r1 == 0) goto L_0x0007
            return
        L_0x001a:
            int r4 = r4.length()
            if (r4 != 0) goto L_0x0027
            int r3 = r3.size()
            if (r3 != 0) goto L_0x0027
            return
        L_0x0027:
            org.bouncycastle.asn1.x509.NameConstraintValidatorException r3 = new org.bouncycastle.asn1.x509.NameConstraintValidatorException
            java.lang.String r4 = "URI is not from a permitted subtree."
            r3.<init>(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.checkPermittedURI(java.util.Set, java.lang.String):void");
    }

    private boolean collectionsAreEqual(Collection collection, Collection collection2) {
        boolean z;
        if (collection == collection2) {
            return true;
        }
        if (collection == null || collection2 == null || collection.size() != collection2.size()) {
            return false;
        }
        for (Object next : collection) {
            Iterator it = collection2.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (equals(next, it.next())) {
                        z = true;
                        continue;
                        break;
                    }
                } else {
                    z = false;
                    continue;
                    break;
                }
            }
            if (!z) {
                return false;
            }
        }
        return true;
    }

    private static int compareTo(byte[] bArr, byte[] bArr2) {
        if (Arrays.areEqual(bArr, bArr2)) {
            return 0;
        }
        return Arrays.areEqual(max(bArr, bArr2), bArr) ? 1 : -1;
    }

    private boolean emailIsConstrained(String str, String str2) {
        String substring = str.substring(str.indexOf(64) + 1);
        if (str2.indexOf(64) != -1) {
            return str.equalsIgnoreCase(str2) || substring.equalsIgnoreCase(str2.substring(1));
        }
        if (str2.charAt(0) != '.') {
            if (substring.equalsIgnoreCase(str2)) {
                return true;
            }
        } else if (withinDomain(substring, str2)) {
            return true;
        }
    }

    private boolean equals(Object obj, Object obj2) {
        if (obj == obj2) {
            return true;
        }
        if (obj == null || obj2 == null) {
            return false;
        }
        return (!(obj instanceof byte[]) || !(obj2 instanceof byte[])) ? obj.equals(obj2) : Arrays.areEqual((byte[]) obj, (byte[]) obj2);
    }

    private static String extractHostFromURL(String str) {
        String substring = str.substring(str.indexOf(58) + 1);
        String str2 = "//";
        if (substring.indexOf(str2) != -1) {
            substring = substring.substring(substring.indexOf(str2) + 2);
        }
        if (substring.lastIndexOf(58) != -1) {
            substring = substring.substring(0, substring.lastIndexOf(58));
        }
        String substring2 = substring.substring(substring.indexOf(58) + 1);
        String substring3 = substring2.substring(substring2.indexOf(64) + 1);
        return substring3.indexOf(47) != -1 ? substring3.substring(0, substring3.indexOf(47)) : substring3;
    }

    private byte[][] extractIPsAndSubnetMasks(byte[] bArr, byte[] bArr2) {
        int length = bArr.length / 2;
        byte[] bArr3 = new byte[length];
        byte[] bArr4 = new byte[length];
        System.arraycopy(bArr, 0, bArr3, 0, length);
        System.arraycopy(bArr, length, bArr4, 0, length);
        byte[] bArr5 = new byte[length];
        byte[] bArr6 = new byte[length];
        System.arraycopy(bArr2, 0, bArr5, 0, length);
        System.arraycopy(bArr2, length, bArr6, 0, length);
        return new byte[][]{bArr3, bArr4, bArr5, bArr6};
    }

    private String extractNameAsString(GeneralName generalName) {
        return DERIA5String.getInstance(generalName.getName()).getString();
    }

    private int hashCollection(Collection collection) {
        int i = 0;
        if (collection == null) {
            return 0;
        }
        for (Object next : collection) {
            i += next instanceof byte[] ? Arrays.hashCode((byte[]) next) : next.hashCode();
        }
        return i;
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<org.bouncycastle.asn1.ASN1Sequence>, for r6v0, types: [java.util.Set<org.bouncycastle.asn1.ASN1Sequence>, java.util.Set] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<org.bouncycastle.asn1.x509.GeneralSubtree>, for r7v0, types: [java.util.Set<org.bouncycastle.asn1.x509.GeneralSubtree>, java.util.Set] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Set intersectDN(java.util.Set<org.bouncycastle.asn1.ASN1Sequence> r6, java.util.Set<org.bouncycastle.asn1.x509.GeneralSubtree> r7) {
        /*
            r5 = this;
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            java.util.Iterator r7 = r7.iterator()
        L_0x0009:
            boolean r1 = r7.hasNext()
            if (r1 == 0) goto L_0x0051
            java.lang.Object r1 = r7.next()
            org.bouncycastle.asn1.x509.GeneralSubtree r1 = (org.bouncycastle.asn1.x509.GeneralSubtree) r1
            org.bouncycastle.asn1.x509.GeneralName r1 = r1.getBase()
            org.bouncycastle.asn1.ASN1Encodable r1 = r1.getName()
            org.bouncycastle.asn1.ASN1Primitive r1 = r1.toASN1Primitive()
            org.bouncycastle.asn1.ASN1Sequence r1 = org.bouncycastle.asn1.ASN1Sequence.getInstance(r1)
            if (r6 != 0) goto L_0x002d
            if (r1 == 0) goto L_0x0009
            r0.add(r1)
            goto L_0x0009
        L_0x002d:
            java.util.Iterator r2 = r6.iterator()
        L_0x0031:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x0009
            java.lang.Object r3 = r2.next()
            org.bouncycastle.asn1.ASN1Sequence r3 = (org.bouncycastle.asn1.ASN1Sequence) r3
            boolean r4 = withinDNSubtree(r1, r3)
            if (r4 == 0) goto L_0x0047
            r0.add(r1)
            goto L_0x0031
        L_0x0047:
            boolean r4 = withinDNSubtree(r3, r1)
            if (r4 == 0) goto L_0x0031
            r0.add(r3)
            goto L_0x0031
        L_0x0051:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.intersectDN(java.util.Set, java.util.Set):java.util.Set");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.lang.String>, for r6v0, types: [java.util.Set, java.util.Set<java.lang.String>] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<org.bouncycastle.asn1.x509.GeneralSubtree>, for r7v0, types: [java.util.Set<org.bouncycastle.asn1.x509.GeneralSubtree>, java.util.Set] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Set intersectDNS(java.util.Set<java.lang.String> r6, java.util.Set<org.bouncycastle.asn1.x509.GeneralSubtree> r7) {
        /*
            r5 = this;
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            java.util.Iterator r7 = r7.iterator()
        L_0x0009:
            boolean r1 = r7.hasNext()
            if (r1 == 0) goto L_0x0049
            java.lang.Object r1 = r7.next()
            org.bouncycastle.asn1.x509.GeneralSubtree r1 = (org.bouncycastle.asn1.x509.GeneralSubtree) r1
            org.bouncycastle.asn1.x509.GeneralName r1 = r1.getBase()
            java.lang.String r1 = r5.extractNameAsString(r1)
            if (r6 != 0) goto L_0x0025
            if (r1 == 0) goto L_0x0009
            r0.add(r1)
            goto L_0x0009
        L_0x0025:
            java.util.Iterator r2 = r6.iterator()
        L_0x0029:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x0009
            java.lang.Object r3 = r2.next()
            java.lang.String r3 = (java.lang.String) r3
            boolean r4 = r5.withinDomain(r3, r1)
            if (r4 == 0) goto L_0x003f
            r0.add(r3)
            goto L_0x0029
        L_0x003f:
            boolean r3 = r5.withinDomain(r1, r3)
            if (r3 == 0) goto L_0x0029
            r0.add(r1)
            goto L_0x0029
        L_0x0049:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.intersectDNS(java.util.Set, java.util.Set):java.util.Set");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.lang.String>, for r5v0, types: [java.util.Set, java.util.Set<java.lang.String>] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<org.bouncycastle.asn1.x509.GeneralSubtree>, for r6v0, types: [java.util.Set<org.bouncycastle.asn1.x509.GeneralSubtree>, java.util.Set] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Set intersectEmail(java.util.Set<java.lang.String> r5, java.util.Set<org.bouncycastle.asn1.x509.GeneralSubtree> r6) {
        /*
            r4 = this;
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            java.util.Iterator r6 = r6.iterator()
        L_0x0009:
            boolean r1 = r6.hasNext()
            if (r1 == 0) goto L_0x0039
            java.lang.Object r1 = r6.next()
            org.bouncycastle.asn1.x509.GeneralSubtree r1 = (org.bouncycastle.asn1.x509.GeneralSubtree) r1
            org.bouncycastle.asn1.x509.GeneralName r1 = r1.getBase()
            java.lang.String r1 = r4.extractNameAsString(r1)
            if (r5 != 0) goto L_0x0025
            if (r1 == 0) goto L_0x0009
            r0.add(r1)
            goto L_0x0009
        L_0x0025:
            java.util.Iterator r2 = r5.iterator()
        L_0x0029:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x0009
            java.lang.Object r3 = r2.next()
            java.lang.String r3 = (java.lang.String) r3
            r4.intersectEmail(r1, r3, r0)
            goto L_0x0029
        L_0x0039:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.intersectEmail(java.util.Set, java.util.Set):java.util.Set");
    }

    private void intersectEmail(String str, String str2, Set set) {
        String str3 = ".";
        if (str.indexOf(64) != -1) {
            String substring = str.substring(str.indexOf(64) + 1);
            if (str2.indexOf(64) != -1) {
                if (!str.equalsIgnoreCase(str2)) {
                    return;
                }
            } else if (str2.startsWith(str3)) {
                if (!withinDomain(substring, str2)) {
                    return;
                }
            } else if (!substring.equalsIgnoreCase(str2)) {
                return;
            }
        } else {
            if (str.startsWith(str3)) {
                if (str2.indexOf(64) != -1) {
                    if (!withinDomain(str2.substring(str.indexOf(64) + 1), str)) {
                        return;
                    }
                } else if (str2.startsWith(str3)) {
                    if (!withinDomain(str, str2) && !str.equalsIgnoreCase(str2)) {
                        if (!withinDomain(str2, str)) {
                            return;
                        }
                    }
                } else if (!withinDomain(str2, str)) {
                    return;
                }
            } else if (str2.indexOf(64) != -1) {
                if (!str2.substring(str2.indexOf(64) + 1).equalsIgnoreCase(str)) {
                    return;
                }
            } else if (str2.startsWith(str3)) {
                if (!withinDomain(str, str2)) {
                    return;
                }
            } else if (!str.equalsIgnoreCase(str2)) {
                return;
            }
            set.add(str2);
            return;
        }
        set.add(str);
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<byte[]>, for r5v0, types: [java.util.Set, java.util.Set<byte[]>] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<org.bouncycastle.asn1.x509.GeneralSubtree>, for r6v0, types: [java.util.Set<org.bouncycastle.asn1.x509.GeneralSubtree>, java.util.Set] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Set intersectIP(java.util.Set<byte[]> r5, java.util.Set<org.bouncycastle.asn1.x509.GeneralSubtree> r6) {
        /*
            r4 = this;
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            java.util.Iterator r6 = r6.iterator()
        L_0x0009:
            boolean r1 = r6.hasNext()
            if (r1 == 0) goto L_0x0047
            java.lang.Object r1 = r6.next()
            org.bouncycastle.asn1.x509.GeneralSubtree r1 = (org.bouncycastle.asn1.x509.GeneralSubtree) r1
            org.bouncycastle.asn1.x509.GeneralName r1 = r1.getBase()
            org.bouncycastle.asn1.ASN1Encodable r1 = r1.getName()
            org.bouncycastle.asn1.ASN1OctetString r1 = org.bouncycastle.asn1.ASN1OctetString.getInstance(r1)
            byte[] r1 = r1.getOctets()
            if (r5 != 0) goto L_0x002d
            if (r1 == 0) goto L_0x0009
            r0.add(r1)
            goto L_0x0009
        L_0x002d:
            java.util.Iterator r2 = r5.iterator()
        L_0x0031:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x0009
            java.lang.Object r3 = r2.next()
            byte[] r3 = (byte[]) r3
            byte[] r3 = (byte[]) r3
            java.util.Set r3 = r4.intersectIPRange(r3, r1)
            r0.addAll(r3)
            goto L_0x0031
        L_0x0047:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.intersectIP(java.util.Set, java.util.Set):java.util.Set");
    }

    private Set intersectIPRange(byte[] bArr, byte[] bArr2) {
        if (bArr.length != bArr2.length) {
            return Collections.EMPTY_SET;
        }
        byte[][] extractIPsAndSubnetMasks = extractIPsAndSubnetMasks(bArr, bArr2);
        byte[] bArr3 = extractIPsAndSubnetMasks[0];
        byte[] bArr4 = extractIPsAndSubnetMasks[1];
        byte[] bArr5 = extractIPsAndSubnetMasks[2];
        byte[] bArr6 = extractIPsAndSubnetMasks[3];
        byte[][] minMaxIPs = minMaxIPs(bArr3, bArr4, bArr5, bArr6);
        return compareTo(max(minMaxIPs[0], minMaxIPs[2]), min(minMaxIPs[1], minMaxIPs[3])) == 1 ? Collections.EMPTY_SET : Collections.singleton(ipWithSubnetMask(or(minMaxIPs[0], minMaxIPs[2]), or(bArr4, bArr6)));
    }

    private Set intersectOtherName(Set set, Set set2) {
        HashSet hashSet = new HashSet(set);
        hashSet.retainAll(set2);
        return hashSet;
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.lang.String>, for r5v0, types: [java.util.Set, java.util.Set<java.lang.String>] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<org.bouncycastle.asn1.x509.GeneralSubtree>, for r6v0, types: [java.util.Set<org.bouncycastle.asn1.x509.GeneralSubtree>, java.util.Set] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Set intersectURI(java.util.Set<java.lang.String> r5, java.util.Set<org.bouncycastle.asn1.x509.GeneralSubtree> r6) {
        /*
            r4 = this;
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            java.util.Iterator r6 = r6.iterator()
        L_0x0009:
            boolean r1 = r6.hasNext()
            if (r1 == 0) goto L_0x0039
            java.lang.Object r1 = r6.next()
            org.bouncycastle.asn1.x509.GeneralSubtree r1 = (org.bouncycastle.asn1.x509.GeneralSubtree) r1
            org.bouncycastle.asn1.x509.GeneralName r1 = r1.getBase()
            java.lang.String r1 = r4.extractNameAsString(r1)
            if (r5 != 0) goto L_0x0025
            if (r1 == 0) goto L_0x0009
            r0.add(r1)
            goto L_0x0009
        L_0x0025:
            java.util.Iterator r2 = r5.iterator()
        L_0x0029:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x0009
            java.lang.Object r3 = r2.next()
            java.lang.String r3 = (java.lang.String) r3
            r4.intersectURI(r3, r1, r0)
            goto L_0x0029
        L_0x0039:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.intersectURI(java.util.Set, java.util.Set):java.util.Set");
    }

    private void intersectURI(String str, String str2, Set set) {
        String str3 = ".";
        if (str.indexOf(64) != -1) {
            String substring = str.substring(str.indexOf(64) + 1);
            if (str2.indexOf(64) != -1) {
                if (!str.equalsIgnoreCase(str2)) {
                    return;
                }
            } else if (str2.startsWith(str3)) {
                if (!withinDomain(substring, str2)) {
                    return;
                }
            } else if (!substring.equalsIgnoreCase(str2)) {
                return;
            }
        } else {
            if (str.startsWith(str3)) {
                if (str2.indexOf(64) != -1) {
                    if (!withinDomain(str2.substring(str.indexOf(64) + 1), str)) {
                        return;
                    }
                } else if (str2.startsWith(str3)) {
                    if (!withinDomain(str, str2) && !str.equalsIgnoreCase(str2)) {
                        if (!withinDomain(str2, str)) {
                            return;
                        }
                    }
                } else if (!withinDomain(str2, str)) {
                    return;
                }
            } else if (str2.indexOf(64) != -1) {
                if (!str2.substring(str2.indexOf(64) + 1).equalsIgnoreCase(str)) {
                    return;
                }
            } else if (str2.startsWith(str3)) {
                if (!withinDomain(str, str2)) {
                    return;
                }
            } else if (!str.equalsIgnoreCase(str2)) {
                return;
            }
            set.add(str2);
            return;
        }
        set.add(str);
    }

    private byte[] ipWithSubnetMask(byte[] bArr, byte[] bArr2) {
        int length = bArr.length;
        byte[] bArr3 = new byte[(length * 2)];
        System.arraycopy(bArr, 0, bArr3, 0, length);
        System.arraycopy(bArr2, 0, bArr3, length, length);
        return bArr3;
    }

    private boolean isIPConstrained(byte[] bArr, byte[] bArr2) {
        int length = bArr.length;
        if (length != bArr2.length / 2) {
            return false;
        }
        byte[] bArr3 = new byte[length];
        System.arraycopy(bArr2, length, bArr3, 0, length);
        byte[] bArr4 = new byte[length];
        byte[] bArr5 = new byte[length];
        for (int i = 0; i < length; i++) {
            bArr4[i] = (byte) (bArr2[i] & bArr3[i]);
            bArr5[i] = (byte) (bArr[i] & bArr3[i]);
        }
        return Arrays.areEqual(bArr4, bArr5);
    }

    private boolean isUriConstrained(String str, String str2) {
        String extractHostFromURL = extractHostFromURL(str);
        if (!str2.startsWith(".")) {
            if (extractHostFromURL.equalsIgnoreCase(str2)) {
                return true;
            }
        } else if (withinDomain(extractHostFromURL, str2)) {
            return true;
        }
        return false;
    }

    private static byte[] max(byte[] bArr, byte[] bArr2) {
        for (int i = 0; i < bArr.length; i++) {
            if ((bArr[i] & 65535) > (65535 & bArr2[i])) {
                return bArr;
            }
        }
        return bArr2;
    }

    private static byte[] min(byte[] bArr, byte[] bArr2) {
        for (int i = 0; i < bArr.length; i++) {
            if ((bArr[i] & 65535) < (65535 & bArr2[i])) {
                return bArr;
            }
        }
        return bArr2;
    }

    private byte[][] minMaxIPs(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        int length = bArr.length;
        byte[] bArr5 = new byte[length];
        byte[] bArr6 = new byte[length];
        byte[] bArr7 = new byte[length];
        byte[] bArr8 = new byte[length];
        for (int i = 0; i < length; i++) {
            bArr5[i] = (byte) (bArr[i] & bArr2[i]);
            bArr6[i] = (byte) ((bArr[i] & bArr2[i]) | (~bArr2[i]));
            bArr7[i] = (byte) (bArr3[i] & bArr4[i]);
            bArr8[i] = (byte) ((bArr3[i] & bArr4[i]) | (~bArr4[i]));
        }
        return new byte[][]{bArr5, bArr6, bArr7, bArr8};
    }

    private static byte[] or(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
            bArr3[i] = (byte) (bArr[i] | bArr2[i]);
        }
        return bArr3;
    }

    private boolean otherNameIsConstrained(OtherName otherName, OtherName otherName2) {
        return otherName2.equals(otherName);
    }

    private String stringifyIP(byte[] bArr) {
        String str;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            str = ".";
            if (i >= bArr.length / 2) {
                break;
            }
            if (sb.length() > 0) {
                sb.append(str);
            }
            sb.append(Integer.toString(bArr[i] & 255));
            i++;
        }
        sb.append("/");
        boolean z = true;
        for (int length = bArr.length / 2; length < bArr.length; length++) {
            if (z) {
                z = false;
            } else {
                sb.append(str);
            }
            sb.append(Integer.toString(bArr[length] & 255));
        }
        return sb.toString();
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<byte[]>, for r4v0, types: [java.util.Set, java.util.Set<byte[]>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String stringifyIPCollection(java.util.Set<byte[]> r4) {
        /*
            r3 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "["
            r0.append(r1)
            java.util.Iterator r4 = r4.iterator()
        L_0x000e:
            boolean r1 = r4.hasNext()
            if (r1 == 0) goto L_0x0030
            int r1 = r0.length()
            r2 = 1
            if (r1 <= r2) goto L_0x0020
            java.lang.String r1 = ","
            r0.append(r1)
        L_0x0020:
            java.lang.Object r1 = r4.next()
            byte[] r1 = (byte[]) r1
            byte[] r1 = (byte[]) r1
            java.lang.String r1 = r3.stringifyIP(r1)
            r0.append(r1)
            goto L_0x000e
        L_0x0030:
            java.lang.String r4 = "]"
            r0.append(r4)
            java.lang.String r4 = r0.toString()
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.stringifyIPCollection(java.util.Set):java.lang.String");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.lang.Object>, for r4v0, types: [java.util.Set<java.lang.Object>, java.util.Set] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String stringifyOtherNameCollection(java.util.Set<java.lang.Object> r4) {
        /*
            r3 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "["
            r0.append(r1)
            java.util.Iterator r4 = r4.iterator()
        L_0x000e:
            boolean r1 = r4.hasNext()
            if (r1 == 0) goto L_0x0055
            int r1 = r0.length()
            r2 = 1
            if (r1 <= r2) goto L_0x0020
            java.lang.String r1 = ","
            r0.append(r1)
        L_0x0020:
            java.lang.Object r1 = r4.next()
            org.bouncycastle.asn1.x509.OtherName r1 = org.bouncycastle.asn1.x509.OtherName.getInstance(r1)
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = r1.getTypeID()
            java.lang.String r2 = r2.getId()
            r0.append(r2)
            java.lang.String r2 = ":"
            r0.append(r2)
            org.bouncycastle.asn1.ASN1Encodable r1 = r1.getValue()     // Catch:{ IOException -> 0x004c }
            org.bouncycastle.asn1.ASN1Primitive r1 = r1.toASN1Primitive()     // Catch:{ IOException -> 0x004c }
            byte[] r1 = r1.getEncoded()     // Catch:{ IOException -> 0x004c }
            java.lang.String r1 = org.bouncycastle.util.encoders.Hex.toHexString(r1)     // Catch:{ IOException -> 0x004c }
            r0.append(r1)     // Catch:{ IOException -> 0x004c }
            goto L_0x000e
        L_0x004c:
            r1 = move-exception
            java.lang.String r1 = r1.toString()
            r0.append(r1)
            goto L_0x000e
        L_0x0055:
            java.lang.String r4 = "]"
            r0.append(r4)
            java.lang.String r4 = r0.toString()
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.stringifyOtherNameCollection(java.util.Set):java.lang.String");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<org.bouncycastle.asn1.ASN1Sequence>, for r4v0, types: [java.util.Set<org.bouncycastle.asn1.ASN1Sequence>, java.util.Set] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Set unionDN(java.util.Set<org.bouncycastle.asn1.ASN1Sequence> r4, org.bouncycastle.asn1.ASN1Sequence r5) {
        /*
            r3 = this;
            boolean r0 = r4.isEmpty()
            if (r0 == 0) goto L_0x000d
            if (r5 != 0) goto L_0x0009
            return r4
        L_0x0009:
            r4.add(r5)
            return r4
        L_0x000d:
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            java.util.Iterator r4 = r4.iterator()
        L_0x0016:
            boolean r1 = r4.hasNext()
            if (r1 == 0) goto L_0x003a
            java.lang.Object r1 = r4.next()
            org.bouncycastle.asn1.ASN1Sequence r1 = (org.bouncycastle.asn1.ASN1Sequence) r1
            boolean r2 = withinDNSubtree(r5, r1)
            if (r2 == 0) goto L_0x002c
            r0.add(r1)
            goto L_0x0016
        L_0x002c:
            boolean r2 = withinDNSubtree(r1, r5)
            if (r2 == 0) goto L_0x0033
            goto L_0x0036
        L_0x0033:
            r0.add(r1)
        L_0x0036:
            r0.add(r5)
            goto L_0x0016
        L_0x003a:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.unionDN(java.util.Set, org.bouncycastle.asn1.ASN1Sequence):java.util.Set");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.lang.String>, for r4v0, types: [java.util.Set, java.util.Set<java.lang.String>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Set unionDNS(java.util.Set<java.lang.String> r4, java.lang.String r5) {
        /*
            r3 = this;
            boolean r0 = r4.isEmpty()
            if (r0 == 0) goto L_0x000d
            if (r5 != 0) goto L_0x0009
            return r4
        L_0x0009:
            r4.add(r5)
            return r4
        L_0x000d:
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            java.util.Iterator r4 = r4.iterator()
        L_0x0016:
            boolean r1 = r4.hasNext()
            if (r1 == 0) goto L_0x0036
            java.lang.Object r1 = r4.next()
            java.lang.String r1 = (java.lang.String) r1
            boolean r2 = r3.withinDomain(r1, r5)
            if (r2 == 0) goto L_0x002c
        L_0x0028:
            r0.add(r5)
            goto L_0x0016
        L_0x002c:
            boolean r2 = r3.withinDomain(r5, r1)
            r0.add(r1)
            if (r2 == 0) goto L_0x0028
            goto L_0x0016
        L_0x0036:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.unionDNS(java.util.Set, java.lang.String):java.util.Set");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.lang.String>, for r3v0, types: [java.util.Set, java.util.Set<java.lang.String>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Set unionEmail(java.util.Set<java.lang.String> r3, java.lang.String r4) {
        /*
            r2 = this;
            boolean r0 = r3.isEmpty()
            if (r0 == 0) goto L_0x000d
            if (r4 != 0) goto L_0x0009
            return r3
        L_0x0009:
            r3.add(r4)
            return r3
        L_0x000d:
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            java.util.Iterator r3 = r3.iterator()
        L_0x0016:
            boolean r1 = r3.hasNext()
            if (r1 == 0) goto L_0x0026
            java.lang.Object r1 = r3.next()
            java.lang.String r1 = (java.lang.String) r1
            r2.unionEmail(r1, r4, r0)
            goto L_0x0016
        L_0x0026:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.unionEmail(java.util.Set, java.lang.String):java.util.Set");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0052, code lost:
        if (withinDomain(r6.substring(r5.indexOf(64) + 1), r5) != false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x006c, code lost:
        if (withinDomain(r6, r5) != false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0073, code lost:
        if (withinDomain(r6, r5) != false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x008a, code lost:
        if (r6.substring(r5.indexOf(64) + 1).equalsIgnoreCase(r5) != false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0097, code lost:
        if (withinDomain(r5, r6) != false) goto L_0x00a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x009e, code lost:
        if (r5.equalsIgnoreCase(r6) != false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x001f, code lost:
        if (r5.equalsIgnoreCase(r6) != false) goto L_0x00a0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void unionEmail(java.lang.String r5, java.lang.String r6, java.util.Set r7) {
        /*
            r4 = this;
            r0 = 64
            int r1 = r5.indexOf(r0)
            java.lang.String r2 = "."
            r3 = -1
            if (r1 == r3) goto L_0x0038
            int r1 = r5.indexOf(r0)
            int r1 = r1 + 1
            java.lang.String r1 = r5.substring(r1)
            int r0 = r6.indexOf(r0)
            if (r0 == r3) goto L_0x0023
            boolean r0 = r5.equalsIgnoreCase(r6)
            if (r0 == 0) goto L_0x00a4
            goto L_0x00a0
        L_0x0023:
            boolean r0 = r6.startsWith(r2)
            if (r0 == 0) goto L_0x0031
            boolean r0 = r4.withinDomain(r1, r6)
            if (r0 == 0) goto L_0x00a4
            goto L_0x0099
        L_0x0031:
            boolean r0 = r1.equalsIgnoreCase(r6)
            if (r0 == 0) goto L_0x00a4
            goto L_0x0099
        L_0x0038:
            boolean r1 = r5.startsWith(r2)
            if (r1 == 0) goto L_0x0076
            int r1 = r6.indexOf(r0)
            if (r1 == r3) goto L_0x0055
            int r0 = r5.indexOf(r0)
            int r0 = r0 + 1
            java.lang.String r0 = r6.substring(r0)
            boolean r0 = r4.withinDomain(r0, r5)
            if (r0 == 0) goto L_0x00a4
            goto L_0x00a0
        L_0x0055:
            boolean r0 = r6.startsWith(r2)
            if (r0 == 0) goto L_0x006f
            boolean r0 = r4.withinDomain(r5, r6)
            if (r0 != 0) goto L_0x00a7
            boolean r0 = r5.equalsIgnoreCase(r6)
            if (r0 == 0) goto L_0x0068
            goto L_0x00a7
        L_0x0068:
            boolean r0 = r4.withinDomain(r6, r5)
            if (r0 == 0) goto L_0x00a4
            goto L_0x00a0
        L_0x006f:
            boolean r0 = r4.withinDomain(r6, r5)
            if (r0 == 0) goto L_0x00a4
            goto L_0x00a0
        L_0x0076:
            int r1 = r6.indexOf(r0)
            if (r1 == r3) goto L_0x008d
            int r0 = r5.indexOf(r0)
            int r0 = r0 + 1
            java.lang.String r0 = r6.substring(r0)
            boolean r0 = r0.equalsIgnoreCase(r5)
            if (r0 == 0) goto L_0x00a4
            goto L_0x00a0
        L_0x008d:
            boolean r0 = r6.startsWith(r2)
            if (r0 == 0) goto L_0x009a
            boolean r0 = r4.withinDomain(r5, r6)
            if (r0 == 0) goto L_0x00a4
        L_0x0099:
            goto L_0x00a7
        L_0x009a:
            boolean r0 = r5.equalsIgnoreCase(r6)
            if (r0 == 0) goto L_0x00a4
        L_0x00a0:
            r7.add(r5)
            goto L_0x00aa
        L_0x00a4:
            r7.add(r5)
        L_0x00a7:
            r7.add(r6)
        L_0x00aa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.unionEmail(java.lang.String, java.lang.String, java.util.Set):void");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<byte[]>, for r3v0, types: [java.util.Set, java.util.Set<byte[]>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Set unionIP(java.util.Set<byte[]> r3, byte[] r4) {
        /*
            r2 = this;
            boolean r0 = r3.isEmpty()
            if (r0 == 0) goto L_0x000d
            if (r4 != 0) goto L_0x0009
            return r3
        L_0x0009:
            r3.add(r4)
            return r3
        L_0x000d:
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            java.util.Iterator r3 = r3.iterator()
        L_0x0016:
            boolean r1 = r3.hasNext()
            if (r1 == 0) goto L_0x002c
            java.lang.Object r1 = r3.next()
            byte[] r1 = (byte[]) r1
            byte[] r1 = (byte[]) r1
            java.util.Set r1 = r2.unionIPRange(r1, r4)
            r0.addAll(r1)
            goto L_0x0016
        L_0x002c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.unionIP(java.util.Set, byte[]):java.util.Set");
    }

    private Set unionIPRange(byte[] bArr, byte[] bArr2) {
        HashSet hashSet = new HashSet();
        boolean areEqual = Arrays.areEqual(bArr, bArr2);
        hashSet.add(bArr);
        if (!areEqual) {
            hashSet.add(bArr2);
        }
        return hashSet;
    }

    private Set unionOtherName(Set set, OtherName otherName) {
        HashSet hashSet = new HashSet(set);
        hashSet.add(otherName);
        return hashSet;
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Set, code=java.util.Set<java.lang.String>, for r3v0, types: [java.util.Set, java.util.Set<java.lang.String>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Set unionURI(java.util.Set<java.lang.String> r3, java.lang.String r4) {
        /*
            r2 = this;
            boolean r0 = r3.isEmpty()
            if (r0 == 0) goto L_0x000d
            if (r4 != 0) goto L_0x0009
            return r3
        L_0x0009:
            r3.add(r4)
            return r3
        L_0x000d:
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            java.util.Iterator r3 = r3.iterator()
        L_0x0016:
            boolean r1 = r3.hasNext()
            if (r1 == 0) goto L_0x0026
            java.lang.Object r1 = r3.next()
            java.lang.String r1 = (java.lang.String) r1
            r2.unionURI(r1, r4, r0)
            goto L_0x0016
        L_0x0026:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.unionURI(java.util.Set, java.lang.String):java.util.Set");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0052, code lost:
        if (withinDomain(r6.substring(r5.indexOf(64) + 1), r5) != false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x006c, code lost:
        if (withinDomain(r6, r5) != false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0073, code lost:
        if (withinDomain(r6, r5) != false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x008a, code lost:
        if (r6.substring(r5.indexOf(64) + 1).equalsIgnoreCase(r5) != false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0097, code lost:
        if (withinDomain(r5, r6) != false) goto L_0x00a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x009e, code lost:
        if (r5.equalsIgnoreCase(r6) != false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x001f, code lost:
        if (r5.equalsIgnoreCase(r6) != false) goto L_0x00a0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void unionURI(java.lang.String r5, java.lang.String r6, java.util.Set r7) {
        /*
            r4 = this;
            r0 = 64
            int r1 = r5.indexOf(r0)
            java.lang.String r2 = "."
            r3 = -1
            if (r1 == r3) goto L_0x0038
            int r1 = r5.indexOf(r0)
            int r1 = r1 + 1
            java.lang.String r1 = r5.substring(r1)
            int r0 = r6.indexOf(r0)
            if (r0 == r3) goto L_0x0023
            boolean r0 = r5.equalsIgnoreCase(r6)
            if (r0 == 0) goto L_0x00a4
            goto L_0x00a0
        L_0x0023:
            boolean r0 = r6.startsWith(r2)
            if (r0 == 0) goto L_0x0031
            boolean r0 = r4.withinDomain(r1, r6)
            if (r0 == 0) goto L_0x00a4
            goto L_0x0099
        L_0x0031:
            boolean r0 = r1.equalsIgnoreCase(r6)
            if (r0 == 0) goto L_0x00a4
            goto L_0x0099
        L_0x0038:
            boolean r1 = r5.startsWith(r2)
            if (r1 == 0) goto L_0x0076
            int r1 = r6.indexOf(r0)
            if (r1 == r3) goto L_0x0055
            int r0 = r5.indexOf(r0)
            int r0 = r0 + 1
            java.lang.String r0 = r6.substring(r0)
            boolean r0 = r4.withinDomain(r0, r5)
            if (r0 == 0) goto L_0x00a4
            goto L_0x00a0
        L_0x0055:
            boolean r0 = r6.startsWith(r2)
            if (r0 == 0) goto L_0x006f
            boolean r0 = r4.withinDomain(r5, r6)
            if (r0 != 0) goto L_0x00a7
            boolean r0 = r5.equalsIgnoreCase(r6)
            if (r0 == 0) goto L_0x0068
            goto L_0x00a7
        L_0x0068:
            boolean r0 = r4.withinDomain(r6, r5)
            if (r0 == 0) goto L_0x00a4
            goto L_0x00a0
        L_0x006f:
            boolean r0 = r4.withinDomain(r6, r5)
            if (r0 == 0) goto L_0x00a4
            goto L_0x00a0
        L_0x0076:
            int r1 = r6.indexOf(r0)
            if (r1 == r3) goto L_0x008d
            int r0 = r5.indexOf(r0)
            int r0 = r0 + 1
            java.lang.String r0 = r6.substring(r0)
            boolean r0 = r0.equalsIgnoreCase(r5)
            if (r0 == 0) goto L_0x00a4
            goto L_0x00a0
        L_0x008d:
            boolean r0 = r6.startsWith(r2)
            if (r0 == 0) goto L_0x009a
            boolean r0 = r4.withinDomain(r5, r6)
            if (r0 == 0) goto L_0x00a4
        L_0x0099:
            goto L_0x00a7
        L_0x009a:
            boolean r0 = r5.equalsIgnoreCase(r6)
            if (r0 == 0) goto L_0x00a4
        L_0x00a0:
            r7.add(r5)
            goto L_0x00aa
        L_0x00a4:
            r7.add(r5)
        L_0x00a7:
            r7.add(r6)
        L_0x00aa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x509.PKIXNameConstraintValidator.unionURI(java.lang.String, java.lang.String, java.util.Set):void");
    }

    private static boolean withinDNSubtree(ASN1Sequence aSN1Sequence, ASN1Sequence aSN1Sequence2) {
        if (aSN1Sequence2.size() < 1 || aSN1Sequence2.size() > aSN1Sequence.size()) {
            return false;
        }
        for (int i = 0; i < aSN1Sequence2.size(); i++) {
            RDN instance = RDN.getInstance(aSN1Sequence2.getObjectAt(i));
            RDN instance2 = RDN.getInstance(aSN1Sequence.getObjectAt(i));
            if (instance.size() != instance2.size() || !instance.getFirst().getType().equals(instance2.getFirst().getType())) {
                return false;
            }
            if (instance.size() != 1 || !instance.getFirst().getType().equals(RFC4519Style.serialNumber)) {
                if (!IETFUtils.rDNAreEqual(instance, instance2)) {
                    return false;
                }
            } else if (!instance2.getFirst().getValue().toString().startsWith(instance.getFirst().getValue().toString())) {
                return false;
            }
        }
        return true;
    }

    private boolean withinDomain(String str, String str2) {
        if (str2.startsWith(".")) {
            str2 = str2.substring(1);
        }
        String[] split = Strings.split(str2, '.');
        String[] split2 = Strings.split(str, '.');
        if (split2.length <= split.length) {
            return false;
        }
        int length = split2.length - split.length;
        for (int i = -1; i < split.length; i++) {
            if (i == -1) {
                if (split2[i + length].equals(BuildConfig.FLAVOR)) {
                    return false;
                }
            } else if (!split[i].equalsIgnoreCase(split2[i + length])) {
                return false;
            }
        }
        return true;
    }

    public void addExcludedSubtree(GeneralSubtree generalSubtree) {
        GeneralName base = generalSubtree.getBase();
        int tagNo = base.getTagNo();
        if (tagNo == 0) {
            this.excludedSubtreesOtherName = unionOtherName(this.excludedSubtreesOtherName, OtherName.getInstance(base.getName()));
        } else if (tagNo == 1) {
            this.excludedSubtreesEmail = unionEmail(this.excludedSubtreesEmail, extractNameAsString(base));
        } else if (tagNo == 2) {
            this.excludedSubtreesDNS = unionDNS(this.excludedSubtreesDNS, extractNameAsString(base));
        } else if (tagNo == 4) {
            this.excludedSubtreesDN = unionDN(this.excludedSubtreesDN, (ASN1Sequence) base.getName().toASN1Primitive());
        } else if (tagNo == 6) {
            this.excludedSubtreesURI = unionURI(this.excludedSubtreesURI, extractNameAsString(base));
        } else if (tagNo == 7) {
            this.excludedSubtreesIP = unionIP(this.excludedSubtreesIP, ASN1OctetString.getInstance(base.getName()).getOctets());
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Unknown tag encountered: ");
            sb.append(base.getTagNo());
            throw new IllegalStateException(sb.toString());
        }
    }

    public void checkExcluded(GeneralName generalName) throws NameConstraintValidatorException {
        int tagNo = generalName.getTagNo();
        if (tagNo == 0) {
            checkExcludedOtherName(this.excludedSubtreesOtherName, OtherName.getInstance(generalName.getName()));
        } else if (tagNo == 1) {
            checkExcludedEmail(this.excludedSubtreesEmail, extractNameAsString(generalName));
        } else if (tagNo == 2) {
            checkExcludedDNS(this.excludedSubtreesDNS, DERIA5String.getInstance(generalName.getName()).getString());
        } else if (tagNo == 4) {
            checkExcludedDN(X500Name.getInstance(generalName.getName()));
        } else if (tagNo == 6) {
            checkExcludedURI(this.excludedSubtreesURI, DERIA5String.getInstance(generalName.getName()).getString());
        } else if (tagNo == 7) {
            checkExcludedIP(this.excludedSubtreesIP, ASN1OctetString.getInstance(generalName.getName()).getOctets());
        }
    }

    public void checkExcludedDN(X500Name x500Name) throws NameConstraintValidatorException {
        checkExcludedDN(this.excludedSubtreesDN, ASN1Sequence.getInstance(x500Name));
    }

    public void checkPermitted(GeneralName generalName) throws NameConstraintValidatorException {
        int tagNo = generalName.getTagNo();
        if (tagNo == 0) {
            checkPermittedOtherName(this.permittedSubtreesOtherName, OtherName.getInstance(generalName.getName()));
        } else if (tagNo == 1) {
            checkPermittedEmail(this.permittedSubtreesEmail, extractNameAsString(generalName));
        } else if (tagNo == 2) {
            checkPermittedDNS(this.permittedSubtreesDNS, DERIA5String.getInstance(generalName.getName()).getString());
        } else if (tagNo == 4) {
            checkPermittedDN(X500Name.getInstance(generalName.getName()));
        } else if (tagNo == 6) {
            checkPermittedURI(this.permittedSubtreesURI, DERIA5String.getInstance(generalName.getName()).getString());
        } else if (tagNo == 7) {
            checkPermittedIP(this.permittedSubtreesIP, ASN1OctetString.getInstance(generalName.getName()).getOctets());
        }
    }

    public void checkPermittedDN(X500Name x500Name) throws NameConstraintValidatorException {
        checkPermittedDN(this.permittedSubtreesDN, ASN1Sequence.getInstance(x500Name.toASN1Primitive()));
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof PKIXNameConstraintValidator)) {
            return false;
        }
        PKIXNameConstraintValidator pKIXNameConstraintValidator = (PKIXNameConstraintValidator) obj;
        if (collectionsAreEqual(pKIXNameConstraintValidator.excludedSubtreesDN, this.excludedSubtreesDN) && collectionsAreEqual(pKIXNameConstraintValidator.excludedSubtreesDNS, this.excludedSubtreesDNS) && collectionsAreEqual(pKIXNameConstraintValidator.excludedSubtreesEmail, this.excludedSubtreesEmail) && collectionsAreEqual(pKIXNameConstraintValidator.excludedSubtreesIP, this.excludedSubtreesIP) && collectionsAreEqual(pKIXNameConstraintValidator.excludedSubtreesURI, this.excludedSubtreesURI) && collectionsAreEqual(pKIXNameConstraintValidator.excludedSubtreesOtherName, this.excludedSubtreesOtherName) && collectionsAreEqual(pKIXNameConstraintValidator.permittedSubtreesDN, this.permittedSubtreesDN) && collectionsAreEqual(pKIXNameConstraintValidator.permittedSubtreesDNS, this.permittedSubtreesDNS) && collectionsAreEqual(pKIXNameConstraintValidator.permittedSubtreesEmail, this.permittedSubtreesEmail) && collectionsAreEqual(pKIXNameConstraintValidator.permittedSubtreesIP, this.permittedSubtreesIP) && collectionsAreEqual(pKIXNameConstraintValidator.permittedSubtreesURI, this.permittedSubtreesURI) && collectionsAreEqual(pKIXNameConstraintValidator.permittedSubtreesOtherName, this.permittedSubtreesOtherName)) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return hashCollection(this.excludedSubtreesDN) + hashCollection(this.excludedSubtreesDNS) + hashCollection(this.excludedSubtreesEmail) + hashCollection(this.excludedSubtreesIP) + hashCollection(this.excludedSubtreesURI) + hashCollection(this.excludedSubtreesOtherName) + hashCollection(this.permittedSubtreesDN) + hashCollection(this.permittedSubtreesDNS) + hashCollection(this.permittedSubtreesEmail) + hashCollection(this.permittedSubtreesIP) + hashCollection(this.permittedSubtreesURI) + hashCollection(this.permittedSubtreesOtherName);
    }

    public void intersectEmptyPermittedSubtree(int i) {
        if (i == 0) {
            this.permittedSubtreesOtherName = new HashSet();
        } else if (i == 1) {
            this.permittedSubtreesEmail = new HashSet();
        } else if (i == 2) {
            this.permittedSubtreesDNS = new HashSet();
        } else if (i == 4) {
            this.permittedSubtreesDN = new HashSet();
        } else if (i == 6) {
            this.permittedSubtreesURI = new HashSet();
        } else if (i == 7) {
            this.permittedSubtreesIP = new HashSet();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Unknown tag encountered: ");
            sb.append(i);
            throw new IllegalStateException(sb.toString());
        }
    }

    public void intersectPermittedSubtree(GeneralSubtree generalSubtree) {
        intersectPermittedSubtree(new GeneralSubtree[]{generalSubtree});
    }

    public void intersectPermittedSubtree(GeneralSubtree[] generalSubtreeArr) {
        HashMap hashMap = new HashMap();
        for (int i = 0; i != generalSubtreeArr.length; i++) {
            GeneralSubtree generalSubtree = generalSubtreeArr[i];
            Integer valueOf = Integers.valueOf(generalSubtree.getBase().getTagNo());
            if (hashMap.get(valueOf) == null) {
                hashMap.put(valueOf, new HashSet());
            }
            ((Set) hashMap.get(valueOf)).add(generalSubtree);
        }
        for (Entry entry : hashMap.entrySet()) {
            int intValue = ((Integer) entry.getKey()).intValue();
            if (intValue == 0) {
                this.permittedSubtreesOtherName = intersectOtherName(this.permittedSubtreesOtherName, (Set) entry.getValue());
            } else if (intValue == 1) {
                this.permittedSubtreesEmail = intersectEmail(this.permittedSubtreesEmail, (Set) entry.getValue());
            } else if (intValue == 2) {
                this.permittedSubtreesDNS = intersectDNS(this.permittedSubtreesDNS, (Set) entry.getValue());
            } else if (intValue == 4) {
                this.permittedSubtreesDN = intersectDN(this.permittedSubtreesDN, (Set) entry.getValue());
            } else if (intValue == 6) {
                this.permittedSubtreesURI = intersectURI(this.permittedSubtreesURI, (Set) entry.getValue());
            } else if (intValue == 7) {
                this.permittedSubtreesIP = intersectIP(this.permittedSubtreesIP, (Set) entry.getValue());
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Unknown tag encountered: ");
                sb.append(intValue);
                throw new IllegalStateException(sb.toString());
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        addLine(sb, "permitted:");
        String str = "DN:";
        if (this.permittedSubtreesDN != null) {
            addLine(sb, str);
            addLine(sb, this.permittedSubtreesDN.toString());
        }
        String str2 = "DNS:";
        if (this.permittedSubtreesDNS != null) {
            addLine(sb, str2);
            addLine(sb, this.permittedSubtreesDNS.toString());
        }
        String str3 = "Email:";
        if (this.permittedSubtreesEmail != null) {
            addLine(sb, str3);
            addLine(sb, this.permittedSubtreesEmail.toString());
        }
        String str4 = "URI:";
        if (this.permittedSubtreesURI != null) {
            addLine(sb, str4);
            addLine(sb, this.permittedSubtreesURI.toString());
        }
        String str5 = "IP:";
        if (this.permittedSubtreesIP != null) {
            addLine(sb, str5);
            addLine(sb, stringifyIPCollection(this.permittedSubtreesIP));
        }
        String str6 = "OtherName:";
        if (this.permittedSubtreesOtherName != null) {
            addLine(sb, str6);
            addLine(sb, stringifyOtherNameCollection(this.permittedSubtreesOtherName));
        }
        addLine(sb, "excluded:");
        if (!this.excludedSubtreesDN.isEmpty()) {
            addLine(sb, str);
            addLine(sb, this.excludedSubtreesDN.toString());
        }
        if (!this.excludedSubtreesDNS.isEmpty()) {
            addLine(sb, str2);
            addLine(sb, this.excludedSubtreesDNS.toString());
        }
        if (!this.excludedSubtreesEmail.isEmpty()) {
            addLine(sb, str3);
            addLine(sb, this.excludedSubtreesEmail.toString());
        }
        if (!this.excludedSubtreesURI.isEmpty()) {
            addLine(sb, str4);
            addLine(sb, this.excludedSubtreesURI.toString());
        }
        if (!this.excludedSubtreesIP.isEmpty()) {
            addLine(sb, str5);
            addLine(sb, stringifyIPCollection(this.excludedSubtreesIP));
        }
        if (!this.excludedSubtreesOtherName.isEmpty()) {
            addLine(sb, str6);
            addLine(sb, stringifyOtherNameCollection(this.excludedSubtreesOtherName));
        }
        return sb.toString();
    }
}
