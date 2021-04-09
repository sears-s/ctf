package org.jivesoftware.smackx.httpfileupload.element;

import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransferChild;
import org.jxmpp.jid.DomainBareJid;

public class SlotRequest_V0_2 extends SlotRequest {
    public static final String NAMESPACE = "urn:xmpp:http:upload";

    public SlotRequest_V0_2(DomainBareJid uploadServiceAddress, String filename, long size) {
        this(uploadServiceAddress, filename, size, null);
    }

    public SlotRequest_V0_2(DomainBareJid uploadServiceAddress, String filename, long size, String contentType) {
        super(uploadServiceAddress, filename, size, contentType, "urn:xmpp:http:upload");
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.element("filename", this.filename);
        xml.element(JingleFileTransferChild.ELEM_SIZE, String.valueOf(this.size));
        xml.optElement("content-type", this.contentType);
        return xml;
    }
}
