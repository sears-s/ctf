package org.jivesoftware.smackx.httpfileupload.element;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransferChild;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.Jid;

public class SlotRequest extends IQ {
    public static final String ELEMENT = "request";
    public static final String NAMESPACE = "urn:xmpp:http:upload:0";
    protected final String contentType;
    protected final String filename;
    protected final long size;

    public SlotRequest(DomainBareJid uploadServiceAddress, String filename2, long size2) {
        this(uploadServiceAddress, filename2, size2, null);
    }

    public SlotRequest(DomainBareJid uploadServiceAddress, String filename2, long size2, String contentType2) {
        this(uploadServiceAddress, filename2, size2, contentType2, "urn:xmpp:http:upload:0");
    }

    protected SlotRequest(DomainBareJid uploadServiceAddress, String filename2, long size2, String contentType2, String namespace) {
        super("request", namespace);
        if (size2 > 0) {
            this.filename = filename2;
            this.size = size2;
            this.contentType = contentType2;
            setType(Type.get);
            setTo((Jid) uploadServiceAddress);
            return;
        }
        throw new IllegalArgumentException("File fileSize must be greater than zero.");
    }

    public String getFilename() {
        return this.filename;
    }

    public long getSize() {
        return this.size;
    }

    public String getContentType() {
        return this.contentType;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("filename", this.filename);
        xml.attribute(JingleFileTransferChild.ELEM_SIZE, String.valueOf(this.size));
        xml.optAttribute("content-type", this.contentType);
        xml.setEmptyElement();
        return xml;
    }
}
