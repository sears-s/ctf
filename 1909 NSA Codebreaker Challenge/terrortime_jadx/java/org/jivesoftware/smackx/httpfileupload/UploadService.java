package org.jivesoftware.smackx.httpfileupload;

import org.jivesoftware.smack.util.Objects;
import org.jxmpp.jid.DomainBareJid;

public class UploadService {
    private final DomainBareJid address;
    private final Long maxFileSize;
    private final Version version;

    public enum Version {
        v0_2,
        v0_3
    }

    UploadService(DomainBareJid address2, Version version2) {
        this(address2, version2, null);
    }

    UploadService(DomainBareJid address2, Version version2, Long maxFileSize2) {
        this.address = (DomainBareJid) Objects.requireNonNull(address2);
        this.version = version2;
        this.maxFileSize = maxFileSize2;
    }

    public DomainBareJid getAddress() {
        return this.address;
    }

    public Version getVersion() {
        return this.version;
    }

    public boolean hasMaxFileSizeLimit() {
        return this.maxFileSize != null;
    }

    public Long getMaxFileSize() {
        return this.maxFileSize;
    }

    public boolean acceptsFileOfSize(long size) {
        boolean z = true;
        if (!hasMaxFileSizeLimit()) {
            return true;
        }
        if (size > this.maxFileSize.longValue()) {
            z = false;
        }
        return z;
    }
}
