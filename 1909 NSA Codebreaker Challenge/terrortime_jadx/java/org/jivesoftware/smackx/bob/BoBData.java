package org.jivesoftware.smackx.bob;

import org.jivesoftware.smack.util.stringencoder.Base64;

public class BoBData {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private byte[] contentBinary;
    private String contentString;
    private final int maxAge;
    private final String type;

    public BoBData(String type2, byte[] content) {
        this(type2, content, -1);
    }

    public BoBData(String type2, byte[] content, int maxAge2) {
        this.type = type2;
        this.contentBinary = content;
        this.maxAge = maxAge2;
    }

    public BoBData(String type2, String content) {
        this(type2, content, -1);
    }

    public BoBData(String type2, String content, int maxAge2) {
        this.type = type2;
        this.contentString = content;
        this.maxAge = maxAge2;
    }

    public int getMaxAge() {
        return this.maxAge;
    }

    public String getType() {
        return this.type;
    }

    private void setContentBinaryIfRequired() {
        if (this.contentBinary == null) {
            this.contentBinary = Base64.decode(this.contentString);
        }
    }

    public byte[] getContent() {
        setContentBinaryIfRequired();
        return (byte[]) this.contentBinary.clone();
    }

    public String getContentBase64Encoded() {
        if (this.contentString == null) {
            this.contentString = Base64.encodeToString(getContent());
        }
        return this.contentString;
    }

    public boolean isOfReasonableSize() {
        setContentBinaryIfRequired();
        return this.contentBinary.length <= 8192;
    }
}
