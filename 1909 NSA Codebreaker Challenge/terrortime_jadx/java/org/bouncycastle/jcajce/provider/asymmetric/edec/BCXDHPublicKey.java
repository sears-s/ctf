package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.jcajce.interfaces.XDHKey;
import org.bouncycastle.jcajce.spec.XDHParameterSpec;
import org.bouncycastle.util.Arrays;

public class BCXDHPublicKey implements XDHKey, PublicKey {
    static final long serialVersionUID = 1;
    private transient AsymmetricKeyParameter xdhPublicKey;

    BCXDHPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        populateFromPubKeyInfo(subjectPublicKeyInfo);
    }

    BCXDHPublicKey(AsymmetricKeyParameter asymmetricKeyParameter) {
        this.xdhPublicKey = asymmetricKeyParameter;
    }

    BCXDHPublicKey(byte[] bArr, byte[] bArr2) throws InvalidKeySpecException {
        AsymmetricKeyParameter x25519PublicKeyParameters;
        int length = bArr.length;
        String str = "raw key data not recognised";
        if (Utils.isValidPrefix(bArr, bArr2)) {
            if (bArr2.length - length == 56) {
                x25519PublicKeyParameters = new X448PublicKeyParameters(bArr2, length);
            } else if (bArr2.length - length == 32) {
                x25519PublicKeyParameters = new X25519PublicKeyParameters(bArr2, length);
            } else {
                throw new InvalidKeySpecException(str);
            }
            this.xdhPublicKey = x25519PublicKeyParameters;
            return;
        }
        throw new InvalidKeySpecException(str);
    }

    private void populateFromPubKeyInfo(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this.xdhPublicKey = EdECObjectIdentifiers.id_X448.equals(subjectPublicKeyInfo.getAlgorithm().getAlgorithm()) ? new X448PublicKeyParameters(subjectPublicKeyInfo.getPublicKeyData().getOctets(), 0) : new X25519PublicKeyParameters(subjectPublicKeyInfo.getPublicKeyData().getOctets(), 0);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance((byte[]) objectInputStream.readObject()));
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(getEncoded());
    }

    /* access modifiers changed from: 0000 */
    public AsymmetricKeyParameter engineGetKeyParameters() {
        return this.xdhPublicKey;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BCXDHPublicKey)) {
            return false;
        }
        return Arrays.areEqual(((BCXDHPublicKey) obj).getEncoded(), getEncoded());
    }

    public String getAlgorithm() {
        return this.xdhPublicKey instanceof X448PublicKeyParameters ? XDHParameterSpec.X448 : XDHParameterSpec.X25519;
    }

    public byte[] getEncoded() {
        if (this.xdhPublicKey instanceof X448PublicKeyParameters) {
            byte[] bArr = new byte[(KeyFactorySpi.x448Prefix.length + 56)];
            System.arraycopy(KeyFactorySpi.x448Prefix, 0, bArr, 0, KeyFactorySpi.x448Prefix.length);
            ((X448PublicKeyParameters) this.xdhPublicKey).encode(bArr, KeyFactorySpi.x448Prefix.length);
            return bArr;
        }
        byte[] bArr2 = new byte[(KeyFactorySpi.x25519Prefix.length + 32)];
        System.arraycopy(KeyFactorySpi.x25519Prefix, 0, bArr2, 0, KeyFactorySpi.x25519Prefix.length);
        ((X25519PublicKeyParameters) this.xdhPublicKey).encode(bArr2, KeyFactorySpi.x25519Prefix.length);
        return bArr2;
    }

    public String getFormat() {
        return "X.509";
    }

    public int hashCode() {
        return Arrays.hashCode(getEncoded());
    }

    public String toString() {
        return Utils.keyToString("Public Key", getAlgorithm(), this.xdhPublicKey);
    }
}
