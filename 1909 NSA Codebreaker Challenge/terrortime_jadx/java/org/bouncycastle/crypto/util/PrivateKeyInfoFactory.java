package org.bouncycastle.crypto.util;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECGOST3410Parameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;

public class PrivateKeyInfoFactory {
    private static Set cryptoProOids = new HashSet(5);

    static {
        cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_A);
        cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_B);
        cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_C);
        cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchA);
        cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchB);
    }

    private PrivateKeyInfoFactory() {
    }

    public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter asymmetricKeyParameter) throws IOException {
        return createPrivateKeyInfo(asymmetricKeyParameter, null);
    }

    public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter asymmetricKeyParameter, ASN1Set aSN1Set) throws IOException {
        int i;
        ASN1Encodable aSN1Encodable;
        BigInteger n;
        ASN1ObjectIdentifier aSN1ObjectIdentifier;
        if (asymmetricKeyParameter instanceof RSAKeyParameters) {
            RSAPrivateCrtKeyParameters rSAPrivateCrtKeyParameters = (RSAPrivateCrtKeyParameters) asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
            RSAPrivateKey rSAPrivateKey = new RSAPrivateKey(rSAPrivateCrtKeyParameters.getModulus(), rSAPrivateCrtKeyParameters.getPublicExponent(), rSAPrivateCrtKeyParameters.getExponent(), rSAPrivateCrtKeyParameters.getP(), rSAPrivateCrtKeyParameters.getQ(), rSAPrivateCrtKeyParameters.getDP(), rSAPrivateCrtKeyParameters.getDQ(), rSAPrivateCrtKeyParameters.getQInv());
            return new PrivateKeyInfo(algorithmIdentifier, rSAPrivateKey, aSN1Set);
        } else if (asymmetricKeyParameter instanceof DSAPrivateKeyParameters) {
            DSAPrivateKeyParameters dSAPrivateKeyParameters = (DSAPrivateKeyParameters) asymmetricKeyParameter;
            DSAParameters parameters = dSAPrivateKeyParameters.getParameters();
            return new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(parameters.getP(), parameters.getQ(), parameters.getG())), new ASN1Integer(dSAPrivateKeyParameters.getX()), aSN1Set);
        } else if (asymmetricKeyParameter instanceof ECPrivateKeyParameters) {
            ECPrivateKeyParameters eCPrivateKeyParameters = (ECPrivateKeyParameters) asymmetricKeyParameter;
            ECDomainParameters parameters2 = eCPrivateKeyParameters.getParameters();
            if (parameters2 == null) {
                aSN1Encodable = new X962Parameters((ASN1Null) DERNull.INSTANCE);
                n = eCPrivateKeyParameters.getD();
            } else if (parameters2 instanceof ECGOST3410Parameters) {
                ECGOST3410Parameters eCGOST3410Parameters = (ECGOST3410Parameters) parameters2;
                GOST3410PublicKeyAlgParameters gOST3410PublicKeyAlgParameters = new GOST3410PublicKeyAlgParameters(eCGOST3410Parameters.getPublicKeyParamSet(), eCGOST3410Parameters.getDigestParamSet(), eCGOST3410Parameters.getEncryptionParamSet());
                int i2 = 32;
                if (cryptoProOids.contains(gOST3410PublicKeyAlgParameters.getPublicKeyParamSet())) {
                    aSN1ObjectIdentifier = CryptoProObjectIdentifiers.gostR3410_2001;
                } else {
                    boolean z = eCPrivateKeyParameters.getD().bitLength() > 256;
                    ASN1ObjectIdentifier aSN1ObjectIdentifier2 = z ? RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512 : RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256;
                    if (z) {
                        i2 = 64;
                    }
                    aSN1ObjectIdentifier = aSN1ObjectIdentifier2;
                }
                byte[] bArr = new byte[i2];
                extractBytes(bArr, i2, 0, eCPrivateKeyParameters.getD());
                return new PrivateKeyInfo(new AlgorithmIdentifier(aSN1ObjectIdentifier, gOST3410PublicKeyAlgParameters), new DEROctetString(bArr));
            } else if (parameters2 instanceof ECNamedDomainParameters) {
                aSN1Encodable = new X962Parameters(((ECNamedDomainParameters) parameters2).getName());
                n = parameters2.getN();
            } else {
                X9ECParameters x9ECParameters = new X9ECParameters(parameters2.getCurve(), parameters2.getG(), parameters2.getN(), parameters2.getH(), parameters2.getSeed());
                ASN1Encodable x962Parameters = new X962Parameters(x9ECParameters);
                i = parameters2.getN().bitLength();
                aSN1Encodable = x962Parameters;
                return new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, aSN1Encodable), new ECPrivateKey(i, eCPrivateKeyParameters.getD(), new DERBitString(parameters2.getG().multiply(eCPrivateKeyParameters.getD()).getEncoded(false)), aSN1Encodable), aSN1Set);
            }
            i = n.bitLength();
            return new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, aSN1Encodable), new ECPrivateKey(i, eCPrivateKeyParameters.getD(), new DERBitString(parameters2.getG().multiply(eCPrivateKeyParameters.getD()).getEncoded(false)), aSN1Encodable), aSN1Set);
        } else if (asymmetricKeyParameter instanceof X448PrivateKeyParameters) {
            X448PrivateKeyParameters x448PrivateKeyParameters = (X448PrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_X448), new DEROctetString(x448PrivateKeyParameters.getEncoded()), aSN1Set, x448PrivateKeyParameters.generatePublicKey().getEncoded());
        } else if (asymmetricKeyParameter instanceof X25519PrivateKeyParameters) {
            X25519PrivateKeyParameters x25519PrivateKeyParameters = (X25519PrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_X25519), new DEROctetString(x25519PrivateKeyParameters.getEncoded()), aSN1Set, x25519PrivateKeyParameters.generatePublicKey().getEncoded());
        } else if (asymmetricKeyParameter instanceof Ed448PrivateKeyParameters) {
            Ed448PrivateKeyParameters ed448PrivateKeyParameters = (Ed448PrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed448), new DEROctetString(ed448PrivateKeyParameters.getEncoded()), aSN1Set, ed448PrivateKeyParameters.generatePublicKey().getEncoded());
        } else if (asymmetricKeyParameter instanceof Ed25519PrivateKeyParameters) {
            Ed25519PrivateKeyParameters ed25519PrivateKeyParameters = (Ed25519PrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519), new DEROctetString(ed25519PrivateKeyParameters.getEncoded()), aSN1Set, ed25519PrivateKeyParameters.generatePublicKey().getEncoded());
        } else {
            throw new IOException("key parameters not recognized");
        }
    }

    private static void extractBytes(byte[] bArr, int i, int i2, BigInteger bigInteger) {
        byte[] byteArray = bigInteger.toByteArray();
        if (byteArray.length < i) {
            byte[] bArr2 = new byte[i];
            System.arraycopy(byteArray, 0, bArr2, bArr2.length - byteArray.length, byteArray.length);
            byteArray = bArr2;
        }
        for (int i3 = 0; i3 != i; i3++) {
            bArr[i2 + i3] = byteArray[(byteArray.length - 1) - i3];
        }
    }
}
