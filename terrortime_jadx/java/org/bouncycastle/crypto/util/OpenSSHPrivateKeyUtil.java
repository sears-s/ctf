package org.bouncycastle.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;

public class OpenSSHPrivateKeyUtil {
    static final byte[] AUTH_MAGIC = Strings.toByteArray("openssh-key-v1\u0000");

    private OpenSSHPrivateKeyUtil() {
    }

    private static boolean allIntegers(ASN1Sequence aSN1Sequence) {
        for (int i = 0; i < aSN1Sequence.size(); i++) {
            if (!(aSN1Sequence.getObjectAt(i) instanceof ASN1Integer)) {
                return false;
            }
        }
        return true;
    }

    public static byte[] encodePrivateKey(AsymmetricKeyParameter asymmetricKeyParameter) throws IOException {
        if (asymmetricKeyParameter == null) {
            throw new IllegalArgumentException("param is null");
        } else if (asymmetricKeyParameter instanceof RSAPrivateCrtKeyParameters) {
            return PrivateKeyInfoFactory.createPrivateKeyInfo(asymmetricKeyParameter).parsePrivateKey().toASN1Primitive().getEncoded();
        } else {
            if (asymmetricKeyParameter instanceof ECPrivateKeyParameters) {
                return PrivateKeyInfoFactory.createPrivateKeyInfo(asymmetricKeyParameter).parsePrivateKey().toASN1Primitive().getEncoded();
            }
            if (asymmetricKeyParameter instanceof DSAPrivateKeyParameters) {
                ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                aSN1EncodableVector.add(new ASN1Integer(0));
                DSAPrivateKeyParameters dSAPrivateKeyParameters = (DSAPrivateKeyParameters) asymmetricKeyParameter;
                aSN1EncodableVector.add(new ASN1Integer(dSAPrivateKeyParameters.getParameters().getP()));
                aSN1EncodableVector.add(new ASN1Integer(dSAPrivateKeyParameters.getParameters().getQ()));
                aSN1EncodableVector.add(new ASN1Integer(dSAPrivateKeyParameters.getParameters().getG()));
                aSN1EncodableVector.add(new ASN1Integer(dSAPrivateKeyParameters.getParameters().getG().modPow(dSAPrivateKeyParameters.getX(), dSAPrivateKeyParameters.getParameters().getP())));
                aSN1EncodableVector.add(new ASN1Integer(dSAPrivateKeyParameters.getX()));
                try {
                    return new DERSequence(aSN1EncodableVector).getEncoded();
                } catch (Exception e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("unable to encode DSAPrivateKeyParameters ");
                    sb.append(e.getMessage());
                    throw new IllegalStateException(sb.toString());
                }
            } else if (asymmetricKeyParameter instanceof Ed25519PrivateKeyParameters) {
                SSHBuilder sSHBuilder = new SSHBuilder();
                sSHBuilder.write(AUTH_MAGIC);
                String str = PrivacyItem.SUBSCRIPTION_NONE;
                sSHBuilder.writeString(str);
                sSHBuilder.writeString(str);
                sSHBuilder.u32(0);
                sSHBuilder.u32(1);
                Ed25519PrivateKeyParameters ed25519PrivateKeyParameters = (Ed25519PrivateKeyParameters) asymmetricKeyParameter;
                sSHBuilder.rawArray(OpenSSHPublicKeyUtil.encodePublicKey(ed25519PrivateKeyParameters.generatePublicKey()));
                SSHBuilder sSHBuilder2 = new SSHBuilder();
                sSHBuilder2.u32(16711935);
                sSHBuilder2.u32(16711935);
                sSHBuilder2.writeString("ssh-ed25519");
                byte[] encoded = ed25519PrivateKeyParameters.generatePublicKey().getEncoded();
                sSHBuilder2.rawArray(encoded);
                sSHBuilder2.rawArray(Arrays.concatenate(ed25519PrivateKeyParameters.getEncoded(), encoded));
                sSHBuilder2.u32(0);
                sSHBuilder.rawArray(sSHBuilder2.getBytes());
                return sSHBuilder.getBytes();
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("unable to convert ");
                sb2.append(asymmetricKeyParameter.getClass().getName());
                sb2.append(" to openssh private key");
                throw new IllegalArgumentException(sb2.toString());
            }
        }
    }

    /* JADX WARNING: type inference failed for: r0v1, types: [org.bouncycastle.crypto.params.AsymmetricKeyParameter] */
    /* JADX WARNING: type inference failed for: r1v6, types: [org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters] */
    /* JADX WARNING: type inference failed for: r0v7 */
    /* JADX WARNING: type inference failed for: r0v8 */
    /* JADX WARNING: type inference failed for: r8v0, types: [org.bouncycastle.crypto.params.ECPrivateKeyParameters] */
    /* JADX WARNING: type inference failed for: r0v16 */
    /* JADX WARNING: type inference failed for: r0v26, types: [org.bouncycastle.crypto.params.DSAPrivateKeyParameters] */
    /* JADX WARNING: type inference failed for: r0v27 */
    /* JADX WARNING: type inference failed for: r0v28 */
    /* JADX WARNING: type inference failed for: r0v29 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 5 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.bouncycastle.crypto.params.AsymmetricKeyParameter parsePrivateKeyBlob(byte[] r10) {
        /*
            r0 = 0
            byte r1 = r10[r0]
            r2 = 48
            if (r1 != r2) goto L_0x00fb
            org.bouncycastle.asn1.ASN1Sequence r10 = org.bouncycastle.asn1.ASN1Sequence.getInstance(r10)
            int r1 = r10.size()
            r2 = 6
            r3 = 2
            r4 = 3
            if (r1 != r2) goto L_0x0062
            boolean r1 = allIntegers(r10)
            if (r1 == 0) goto L_0x00f9
            org.bouncycastle.asn1.ASN1Encodable r0 = r10.getObjectAt(r0)
            org.bouncycastle.asn1.ASN1Integer r0 = (org.bouncycastle.asn1.ASN1Integer) r0
            java.math.BigInteger r0 = r0.getPositiveValue()
            java.math.BigInteger r1 = org.bouncycastle.util.BigIntegers.ZERO
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00f9
            org.bouncycastle.crypto.params.DSAPrivateKeyParameters r0 = new org.bouncycastle.crypto.params.DSAPrivateKeyParameters
            r1 = 5
            org.bouncycastle.asn1.ASN1Encodable r1 = r10.getObjectAt(r1)
            org.bouncycastle.asn1.ASN1Integer r1 = (org.bouncycastle.asn1.ASN1Integer) r1
            java.math.BigInteger r1 = r1.getPositiveValue()
            org.bouncycastle.crypto.params.DSAParameters r2 = new org.bouncycastle.crypto.params.DSAParameters
            r5 = 1
            org.bouncycastle.asn1.ASN1Encodable r5 = r10.getObjectAt(r5)
            org.bouncycastle.asn1.ASN1Integer r5 = (org.bouncycastle.asn1.ASN1Integer) r5
            java.math.BigInteger r5 = r5.getPositiveValue()
            org.bouncycastle.asn1.ASN1Encodable r3 = r10.getObjectAt(r3)
            org.bouncycastle.asn1.ASN1Integer r3 = (org.bouncycastle.asn1.ASN1Integer) r3
            java.math.BigInteger r3 = r3.getPositiveValue()
            org.bouncycastle.asn1.ASN1Encodable r10 = r10.getObjectAt(r4)
            org.bouncycastle.asn1.ASN1Integer r10 = (org.bouncycastle.asn1.ASN1Integer) r10
            java.math.BigInteger r10 = r10.getPositiveValue()
            r2.<init>(r5, r3, r10)
            r0.<init>(r1, r2)
            goto L_0x015d
        L_0x0062:
            int r1 = r10.size()
            r2 = 9
            if (r1 != r2) goto L_0x00ae
            boolean r1 = allIntegers(r10)
            if (r1 == 0) goto L_0x00f9
            org.bouncycastle.asn1.ASN1Encodable r0 = r10.getObjectAt(r0)
            org.bouncycastle.asn1.ASN1Integer r0 = (org.bouncycastle.asn1.ASN1Integer) r0
            java.math.BigInteger r0 = r0.getPositiveValue()
            java.math.BigInteger r1 = org.bouncycastle.util.BigIntegers.ZERO
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00f9
            org.bouncycastle.asn1.pkcs.RSAPrivateKey r10 = org.bouncycastle.asn1.pkcs.RSAPrivateKey.getInstance(r10)
            org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters r9 = new org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters
            java.math.BigInteger r1 = r10.getModulus()
            java.math.BigInteger r2 = r10.getPublicExponent()
            java.math.BigInteger r3 = r10.getPrivateExponent()
            java.math.BigInteger r4 = r10.getPrime1()
            java.math.BigInteger r5 = r10.getPrime2()
            java.math.BigInteger r6 = r10.getExponent1()
            java.math.BigInteger r7 = r10.getExponent2()
            java.math.BigInteger r8 = r10.getCoefficient()
            r0 = r9
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8)
            goto L_0x015d
        L_0x00ae:
            int r0 = r10.size()
            r1 = 4
            if (r0 != r1) goto L_0x00f9
            org.bouncycastle.asn1.ASN1Encodable r0 = r10.getObjectAt(r4)
            boolean r0 = r0 instanceof org.bouncycastle.asn1.DERTaggedObject
            if (r0 == 0) goto L_0x00f9
            org.bouncycastle.asn1.ASN1Encodable r0 = r10.getObjectAt(r3)
            boolean r0 = r0 instanceof org.bouncycastle.asn1.DERTaggedObject
            if (r0 == 0) goto L_0x00f9
            org.bouncycastle.asn1.sec.ECPrivateKey r10 = org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(r10)
            org.bouncycastle.asn1.ASN1Primitive r0 = r10.getParameters()
            r2 = r0
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = (org.bouncycastle.asn1.ASN1ObjectIdentifier) r2
            org.bouncycastle.asn1.x9.X9ECParameters r0 = org.bouncycastle.asn1.x9.ECNamedCurveTable.getByOID(r2)
            org.bouncycastle.crypto.params.ECPrivateKeyParameters r8 = new org.bouncycastle.crypto.params.ECPrivateKeyParameters
            java.math.BigInteger r10 = r10.getKey()
            org.bouncycastle.crypto.params.ECNamedDomainParameters r9 = new org.bouncycastle.crypto.params.ECNamedDomainParameters
            org.bouncycastle.math.ec.ECCurve r3 = r0.getCurve()
            org.bouncycastle.math.ec.ECPoint r4 = r0.getG()
            java.math.BigInteger r5 = r0.getN()
            java.math.BigInteger r6 = r0.getH()
            byte[] r7 = r0.getSeed()
            r1 = r9
            r1.<init>(r2, r3, r4, r5, r6, r7)
            r8.<init>(r10, r9)
            r0 = r8
            goto L_0x015d
        L_0x00f9:
            r0 = 0
            goto L_0x015d
        L_0x00fb:
            org.bouncycastle.crypto.util.SSHBuffer r1 = new org.bouncycastle.crypto.util.SSHBuffer
            byte[] r2 = AUTH_MAGIC
            r1.<init>(r2, r10)
            byte[] r10 = r1.readString()
            java.lang.String r10 = org.bouncycastle.util.Strings.fromByteArray(r10)
            java.lang.String r2 = "none"
            boolean r10 = r2.equals(r10)
            if (r10 == 0) goto L_0x0187
            r1.readString()
            r1.readString()
            int r10 = r1.readU32()
            long r2 = (long) r10
            r10 = r0
        L_0x011e:
            long r4 = (long) r10
            int r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x012d
            byte[] r4 = r1.readString()
            org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil.parsePublicKey(r4)
            int r10 = r10 + 1
            goto L_0x011e
        L_0x012d:
            org.bouncycastle.crypto.util.SSHBuffer r10 = new org.bouncycastle.crypto.util.SSHBuffer
            byte[] r1 = r1.readPaddedString()
            r10.<init>(r1)
            int r1 = r10.readU32()
            int r2 = r10.readU32()
            if (r1 != r2) goto L_0x017f
            byte[] r1 = r10.readString()
            java.lang.String r1 = org.bouncycastle.util.Strings.fromByteArray(r1)
            java.lang.String r2 = "ssh-ed25519"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x0168
            r10.readString()
            byte[] r10 = r10.readString()
            org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters r1 = new org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
            r1.<init>(r10, r0)
            r0 = r1
        L_0x015d:
            if (r0 == 0) goto L_0x0160
            return r0
        L_0x0160:
            java.lang.IllegalArgumentException r10 = new java.lang.IllegalArgumentException
            java.lang.String r0 = "unable to parse key"
            r10.<init>(r0)
            throw r10
        L_0x0168:
            java.lang.IllegalStateException r10 = new java.lang.IllegalStateException
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "can not parse private key of type "
            r0.append(r2)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r10.<init>(r0)
            throw r10
        L_0x017f:
            java.lang.IllegalStateException r10 = new java.lang.IllegalStateException
            java.lang.String r0 = "private key check values are not the same"
            r10.<init>(r0)
            throw r10
        L_0x0187:
            java.lang.IllegalStateException r10 = new java.lang.IllegalStateException
            java.lang.String r0 = "encrypted keys not supported"
            r10.<init>(r0)
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.crypto.util.OpenSSHPrivateKeyUtil.parsePrivateKeyBlob(byte[]):org.bouncycastle.crypto.params.AsymmetricKeyParameter");
    }
}
