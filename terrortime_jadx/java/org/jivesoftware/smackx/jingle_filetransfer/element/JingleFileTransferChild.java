package org.jivesoftware.smackx.jingle_filetransfer.element;

import java.io.File;
import java.util.Date;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.hashes.element.HashElement;
import org.jivesoftware.smackx.jingle.element.JingleContentDescriptionChildElement;

public class JingleFileTransferChild extends JingleContentDescriptionChildElement {
    public static final String ELEMENT = "file";
    public static final String ELEM_DATE = "date";
    public static final String ELEM_DESC = "desc";
    public static final String ELEM_MEDIA_TYPE = "media-type";
    public static final String ELEM_NAME = "name";
    public static final String ELEM_SIZE = "size";
    private final Date date;
    private final String desc;
    private final HashElement hash;
    private final String mediaType;
    private final String name;
    private final Range range;
    private final int size;

    public static final class Builder {
        private Date date;
        private String desc;
        private HashElement hash;
        private String mediaType;
        private String name;
        private Range range;
        private int size;

        private Builder() {
        }

        public Builder setDate(Date date2) {
            this.date = date2;
            return this;
        }

        public Builder setDescription(String desc2) {
            this.desc = desc2;
            return this;
        }

        public Builder setHash(HashElement hash2) {
            this.hash = hash2;
            return this;
        }

        public Builder setMediaType(String mediaType2) {
            this.mediaType = mediaType2;
            return this;
        }

        public Builder setName(String name2) {
            this.name = name2;
            return this;
        }

        public Builder setSize(int size2) {
            this.size = size2;
            return this;
        }

        public Builder setRange(Range range2) {
            this.range = range2;
            return this;
        }

        public JingleFileTransferChild build() {
            JingleFileTransferChild jingleFileTransferChild = new JingleFileTransferChild(this.date, this.desc, this.hash, this.mediaType, this.name, this.size, this.range);
            return jingleFileTransferChild;
        }

        public Builder setFile(File file) {
            return setDate(new Date(file.lastModified())).setName(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1)).setSize((int) file.length());
        }
    }

    public JingleFileTransferChild(Date date2, String desc2, HashElement hash2, String mediaType2, String name2, int size2, Range range2) {
        this.date = date2;
        this.desc = desc2;
        this.hash = hash2;
        this.mediaType = mediaType2;
        this.name = name2;
        this.size = size2;
        this.range = range2;
    }

    public Date getDate() {
        return this.date;
    }

    public String getDescription() {
        return this.desc;
    }

    public HashElement getHash() {
        return this.hash;
    }

    public String getMediaType() {
        return this.mediaType;
    }

    public String getName() {
        return this.name;
    }

    public int getSize() {
        return this.size;
    }

    public Range getRange() {
        return this.range;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public CharSequence toXML(String enclosingNamespace) {
        XmlStringBuilder sb = new XmlStringBuilder((NamedElement) this);
        sb.rightAngleBracket();
        sb.optElement(ELEM_DATE, this.date);
        sb.optElement(ELEM_DESC, this.desc);
        sb.optElement(ELEM_MEDIA_TYPE, this.mediaType);
        sb.optElement("name", this.name);
        sb.optElement(this.range);
        int i = this.size;
        if (i > 0) {
            sb.element(ELEM_SIZE, Integer.toString(i));
        }
        sb.optElement(this.hash);
        sb.closeElement((NamedElement) this);
        return sb;
    }

    public static Builder getBuilder() {
        return new Builder();
    }
}
