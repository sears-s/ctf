package org.jivesoftware.smackx.si.packet;

import java.util.Date;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransferChild;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.util.XmppDateTime;

public class StreamInitiation extends IQ {
    public static final String ELEMENT = "si";
    public static final String NAMESPACE = "http://jabber.org/protocol/si";
    private Feature featureNegotiation;
    private File file;
    private String id;
    private String mimeType;

    /* renamed from: org.jivesoftware.smackx.si.packet.StreamInitiation$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$packet$IQ$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$packet$IQ$Type[Type.set.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$IQ$Type[Type.result.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public static class Feature implements ExtensionElement {
        private final DataForm data;

        public Feature(DataForm data2) {
            this.data = data2;
        }

        public DataForm getData() {
            return this.data;
        }

        public String getNamespace() {
            return "http://jabber.org/protocol/feature-neg";
        }

        public String getElementName() {
            return "feature";
        }

        public String toXML(String enclosingNamespace) {
            StringBuilder buf = new StringBuilder();
            buf.append("<feature xmlns=\"http://jabber.org/protocol/feature-neg\">");
            buf.append(this.data.toXML((String) null));
            buf.append("</feature>");
            return buf.toString();
        }
    }

    public static class File implements ExtensionElement {
        private Date date;
        private String desc;
        private String hash;
        private boolean isRanged;
        private final String name;
        private final long size;

        public File(String name2, long size2) {
            if (name2 != null) {
                this.name = name2;
                this.size = size2;
                return;
            }
            throw new NullPointerException("name cannot be null");
        }

        public String getName() {
            return this.name;
        }

        public long getSize() {
            return this.size;
        }

        public void setHash(String hash2) {
            this.hash = hash2;
        }

        public String getHash() {
            return this.hash;
        }

        public void setDate(Date date2) {
            this.date = date2;
        }

        public Date getDate() {
            return this.date;
        }

        public void setDesc(String desc2) {
            this.desc = desc2;
        }

        public String getDesc() {
            return this.desc;
        }

        public void setRanged(boolean isRanged2) {
            this.isRanged = isRanged2;
        }

        public boolean isRanged() {
            return this.isRanged;
        }

        public String getElementName() {
            return JingleFileTransferChild.ELEMENT;
        }

        public String getNamespace() {
            return FileTransferNegotiator.SI_PROFILE_FILE_TRANSFER_NAMESPACE;
        }

        public String toXML(String enclosingNamespace) {
            StringBuilder buffer = new StringBuilder();
            buffer.append('<');
            buffer.append(getElementName());
            buffer.append(" xmlns=\"");
            buffer.append(getNamespace());
            String str = "\" ";
            buffer.append(str);
            if (getName() != null) {
                buffer.append("name=\"");
                buffer.append(StringUtils.escapeForXmlAttribute(getName()));
                buffer.append(str);
            }
            if (getSize() > 0) {
                buffer.append("size=\"");
                buffer.append(getSize());
                buffer.append(str);
            }
            if (getDate() != null) {
                buffer.append("date=\"");
                buffer.append(XmppDateTime.formatXEP0082Date(this.date));
                buffer.append(str);
            }
            if (getHash() != null) {
                buffer.append("hash=\"");
                buffer.append(getHash());
                buffer.append(str);
            }
            String str2 = this.desc;
            if ((str2 == null || str2.length() <= 0) && !this.isRanged) {
                buffer.append("/>");
            } else {
                buffer.append('>');
                if (getDesc() != null && this.desc.length() > 0) {
                    buffer.append("<desc>");
                    buffer.append(StringUtils.escapeForXmlText(getDesc()));
                    buffer.append("</desc>");
                }
                if (isRanged()) {
                    buffer.append("<range/>");
                }
                buffer.append("</");
                buffer.append(getElementName());
                buffer.append('>');
            }
            return buffer.toString();
        }
    }

    public StreamInitiation() {
        super(ELEMENT, "http://jabber.org/protocol/si");
    }

    public void setSessionID(String id2) {
        this.id = id2;
    }

    public String getSessionID() {
        return this.id;
    }

    public void setMimeType(String mimeType2) {
        this.mimeType = mimeType2;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setFile(File file2) {
        this.file = file2;
    }

    public File getFile() {
        return this.file;
    }

    public void setFeatureNegotiationForm(DataForm form) {
        this.featureNegotiation = new Feature(form);
    }

    public DataForm getFeatureNegotiationForm() {
        return this.featureNegotiation.getData();
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder buf) {
        int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$packet$IQ$Type[getType().ordinal()];
        if (i == 1) {
            buf.optAttribute("id", getSessionID());
            buf.optAttribute("mime-type", getMimeType());
            buf.attribute("profile", FileTransferNegotiator.SI_PROFILE_FILE_TRANSFER_NAMESPACE);
            buf.rightAngleBracket();
            buf.optAppend((CharSequence) this.file.toXML((String) null));
        } else if (i == 2) {
            buf.rightAngleBracket();
        } else {
            throw new IllegalArgumentException("IQ Type not understood");
        }
        Feature feature = this.featureNegotiation;
        if (feature != null) {
            buf.append((CharSequence) feature.toXML((String) null));
        }
        return buf;
    }
}
