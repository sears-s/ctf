package org.bouncycastle.crypto.util;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.sec.SecP256R1Curve;
import org.bouncycastle.util.Strings;

public class OpenSSHPublicKeyUtil {
    private static final String DSS = "ssh-dss";
    private static final String ECDSA = "ecdsa";
    private static final String ED_25519 = "ssh-ed25519";
    private static final String RSA = "ssh-rsa";

    private OpenSSHPublicKeyUtil() {
    }

    public static byte[] encodePublicKey(AsymmetricKeyParameter asymmetricKeyParameter) throws IOException {
        if (asymmetricKeyParameter == null) {
            throw new IllegalArgumentException("cipherParameters was null.");
        } else if (asymmetricKeyParameter instanceof RSAKeyParameters) {
            if (!asymmetricKeyParameter.isPrivate()) {
                RSAKeyParameters rSAKeyParameters = (RSAKeyParameters) asymmetricKeyParameter;
                BigInteger exponent = rSAKeyParameters.getExponent();
                BigInteger modulus = rSAKeyParameters.getModulus();
                SSHBuilder sSHBuilder = new SSHBuilder();
                sSHBuilder.writeString(RSA);
                sSHBuilder.rawArray(exponent.toByteArray());
                sSHBuilder.rawArray(modulus.toByteArray());
                return sSHBuilder.getBytes();
            }
            throw new IllegalArgumentException("RSAKeyParamaters was for encryption");
        } else if (asymmetricKeyParameter instanceof ECPublicKeyParameters) {
            SSHBuilder sSHBuilder2 = new SSHBuilder();
            ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters) asymmetricKeyParameter;
            if (eCPublicKeyParameters.getParameters().getCurve() instanceof SecP256R1Curve) {
                String str = "nistp256";
                StringBuilder sb = new StringBuilder();
                sb.append("ecdsa-sha2-");
                sb.append(str);
                sSHBuilder2.writeString(sb.toString());
                sSHBuilder2.writeString(str);
                sSHBuilder2.rawArray(eCPublicKeyParameters.getQ().getEncoded(false));
                return sSHBuilder2.getBytes();
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("unable to derive ssh curve name for ");
            sb2.append(eCPublicKeyParameters.getParameters().getCurve().getClass().getName());
            throw new IllegalArgumentException(sb2.toString());
        } else if (asymmetricKeyParameter instanceof DSAPublicKeyParameters) {
            SSHBuilder sSHBuilder3 = new SSHBuilder();
            sSHBuilder3.writeString(DSS);
            DSAPublicKeyParameters dSAPublicKeyParameters = (DSAPublicKeyParameters) asymmetricKeyParameter;
            sSHBuilder3.rawArray(dSAPublicKeyParameters.getParameters().getP().toByteArray());
            sSHBuilder3.rawArray(dSAPublicKeyParameters.getParameters().getQ().toByteArray());
            sSHBuilder3.rawArray(dSAPublicKeyParameters.getParameters().getG().toByteArray());
            sSHBuilder3.rawArray(dSAPublicKeyParameters.getY().toByteArray());
            return sSHBuilder3.getBytes();
        } else if (asymmetricKeyParameter instanceof Ed25519PublicKeyParameters) {
            SSHBuilder sSHBuilder4 = new SSHBuilder();
            sSHBuilder4.writeString(ED_25519);
            sSHBuilder4.rawArray(((Ed25519PublicKeyParameters) asymmetricKeyParameter).getEncoded());
            return sSHBuilder4.getBytes();
        } else {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("unable to convert ");
            sb3.append(asymmetricKeyParameter.getClass().getName());
            sb3.append(" to private key");
            throw new IllegalArgumentException(sb3.toString());
        }
    }

    public static AsymmetricKeyParameter parsePublicKey(SSHBuffer sSHBuffer) {
        AsymmetricKeyParameter asymmetricKeyParameter;
        String fromByteArray = Strings.fromByteArray(sSHBuffer.readString());
        if (RSA.equals(fromByteArray)) {
            asymmetricKeyParameter = new RSAKeyParameters(false, sSHBuffer.positiveBigNum(), sSHBuffer.positiveBigNum());
        } else if (DSS.equals(fromByteArray)) {
            asymmetricKeyParameter = new DSAPublicKeyParameters(sSHBuffer.positiveBigNum(), new DSAParameters(sSHBuffer.positiveBigNum(), sSHBuffer.positiveBigNum(), sSHBuffer.positiveBigNum()));
        } else if (fromByteArray.startsWith(ECDSA)) {
            String fromByteArray2 = Strings.fromByteArray(sSHBuffer.readString());
            if (fromByteArray2.startsWith("nist")) {
                String substring = fromByteArray2.substring(4);
                StringBuilder sb = new StringBuilder();
                sb.append(substring.substring(0, 1));
                sb.append("-");
                sb.append(substring.substring(1));
                fromByteArray2 = sb.toString();
            }
            X9ECParameters byName = ECNamedCurveTable.getByName(fromByteArray2);
            if (byName != null) {
                ECCurve curve = byName.getCurve();
                ECPoint decodePoint = curve.decodePoint(sSHBuffer.readString());
                ECDomainParameters eCDomainParameters = new ECDomainParameters(curve, byName.getG(), byName.getN(), byName.getH(), byName.getSeed());
                asymmetricKeyParameter = new ECPublicKeyParameters(decodePoint, eCDomainParameters);
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("unable to find curve for ");
                sb2.append(fromByteArray);
                sb2.append(" using curve name ");
                sb2.append(fromByteArray2);
                throw new IllegalStateException(sb2.toString());
            }
        } else {
            asymmetricKeyParameter = fromByteArray.startsWith(ED_25519) ? new Ed25519PublicKeyParameters(sSHBuffer.readString(), 0) : null;
        }
        if (asymmetricKeyParameter == null) {
            throw new IllegalArgumentException("unable to parse key");
        } else if (!sSHBuffer.hasRemaining()) {
            return asymmetricKeyParameter;
        } else {
            throw new IllegalArgumentException("uncoded key has trailing data");
        }
    }

    public static AsymmetricKeyParameter parsePublicKey(byte[] bArr) {
        return parsePublicKey(new SSHBuffer(bArr));
    }
}
