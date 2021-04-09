package org.jivesoftware.smackx.jingle.transports.jingle_ibb.element;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.jingle.element.JingleContentTransport;

public class JingleIBBTransport extends JingleContentTransport {
    public static final String ATTR_BLOCK_SIZE = "block-size";
    public static final String ATTR_SID = "sid";
    public static final short DEFAULT_BLOCK_SIZE = 4096;
    public static final String NAMESPACE_V1 = "urn:xmpp:jingle:transports:ibb:1";
    private final short blockSize;
    private final String sid;

    public JingleIBBTransport() {
        this((short) DEFAULT_BLOCK_SIZE);
    }

    public JingleIBBTransport(String sid2) {
        this(DEFAULT_BLOCK_SIZE, sid2);
    }

    public JingleIBBTransport(short blockSize2) {
        this(blockSize2, StringUtils.randomString(24));
    }

    public JingleIBBTransport(short blockSize2, String sid2) {
        super(null);
        if (blockSize2 > 0) {
            this.blockSize = blockSize2;
        } else {
            this.blockSize = DEFAULT_BLOCK_SIZE;
        }
        this.sid = sid2;
    }

    public String getSessionId() {
        return this.sid;
    }

    public short getBlockSize() {
        return this.blockSize;
    }

    /* access modifiers changed from: protected */
    public void addExtraAttributes(XmlStringBuilder xml) {
        xml.attribute(ATTR_BLOCK_SIZE, (int) this.blockSize);
        xml.attribute("sid", this.sid);
    }

    public String getNamespace() {
        return NAMESPACE_V1;
    }

    public boolean equals(Object other) {
        boolean z = false;
        if (other == null || !(other instanceof JingleIBBTransport)) {
            return false;
        }
        if (this == other || hashCode() == other.hashCode()) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return toXML((String) null).toString().hashCode();
    }
}
