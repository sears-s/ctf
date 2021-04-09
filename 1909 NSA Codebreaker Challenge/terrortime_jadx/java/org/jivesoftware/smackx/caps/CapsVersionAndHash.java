package org.jivesoftware.smackx.caps;

public class CapsVersionAndHash {
    public final String hash;
    public final String version;

    public CapsVersionAndHash(String version2, String hash2) {
        this.version = version2;
        this.hash = hash2;
    }
}
