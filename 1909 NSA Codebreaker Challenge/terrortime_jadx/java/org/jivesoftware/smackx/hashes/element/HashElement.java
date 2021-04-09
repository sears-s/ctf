package org.jivesoftware.smackx.hashes.element;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jivesoftware.smackx.hashes.HashManager.ALGORITHM;
import org.jivesoftware.smackx.hashes.HashManager.NAMESPACE;

public class HashElement implements ExtensionElement {
    public static final String ATTR_ALGO = "algo";
    public static final String ELEMENT = "hash";
    private final ALGORITHM algorithm;
    private final byte[] hash;
    private final String hashB64;

    public HashElement(ALGORITHM algorithm2, byte[] hash2) {
        this.algorithm = (ALGORITHM) Objects.requireNonNull(algorithm2);
        this.hash = (byte[]) Objects.requireNonNull(hash2);
        this.hashB64 = Base64.encodeToString(hash2);
    }

    public HashElement(ALGORITHM algorithm2, String hashB642) {
        this.algorithm = algorithm2;
        this.hash = Base64.decode(hashB642);
        this.hashB64 = hashB642;
    }

    public ALGORITHM getAlgorithm() {
        return this.algorithm;
    }

    public byte[] getHash() {
        return this.hash;
    }

    public String getHashB64() {
        return this.hashB64;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public CharSequence toXML(String enclosingNamespace) {
        XmlStringBuilder sb = new XmlStringBuilder((ExtensionElement) this);
        sb.attribute(ATTR_ALGO, this.algorithm.toString());
        sb.rightAngleBracket();
        sb.append((CharSequence) this.hashB64);
        sb.closeElement((NamedElement) this);
        return sb;
    }

    public String getNamespace() {
        return NAMESPACE.V2.toString();
    }

    public boolean equals(Object other) {
        boolean z = false;
        if (other == null || !(other instanceof HashElement)) {
            return false;
        }
        if (hashCode() == other.hashCode()) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return toXML(null).toString().hashCode();
    }
}
