package org.jivesoftware.smackx.bob;

import org.jivesoftware.smack.util.StringUtils;

public class BoBHash {
    private final String cid;
    private final String hash;
    private final String hashType;

    public BoBHash(String hash2, String hashType2) {
        this.hash = (String) StringUtils.requireNotNullOrEmpty(hash2, "hash must not be null or empty");
        this.hashType = (String) StringUtils.requireNotNullOrEmpty(hashType2, "hashType must not be null or empty");
        StringBuilder sb = new StringBuilder();
        sb.append(this.hashType);
        sb.append('+');
        sb.append(this.hash);
        sb.append("@bob.xmpp.org");
        this.cid = sb.toString();
    }

    public String getHash() {
        return this.hash;
    }

    public String getHashType() {
        return this.hashType;
    }

    public String toSrc() {
        StringBuilder sb = new StringBuilder();
        sb.append("cid:");
        sb.append(getCid());
        return sb.toString();
    }

    public String getCid() {
        return this.cid;
    }

    public boolean equals(Object other) {
        if (!(other instanceof BoBHash)) {
            return false;
        }
        return this.cid.equals(((BoBHash) other).cid);
    }

    public int hashCode() {
        return this.cid.hashCode();
    }

    public static BoBHash fromSrc(String src) {
        String str = "+";
        return new BoBHash(src.substring(src.indexOf(str) + 1, src.indexOf("@bob.xmpp.org")), src.substring(src.lastIndexOf("cid:") + 4, src.indexOf(str)));
    }

    public static BoBHash fromCid(String cid2) {
        String str = "+";
        return new BoBHash(cid2.substring(cid2.indexOf(str) + 1, cid2.indexOf("@bob.xmpp.org")), cid2.substring(0, cid2.indexOf(str)));
    }
}
