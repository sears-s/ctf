package org.jivesoftware.smackx.hoxt.packet;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class Base64BinaryChunk implements ExtensionElement {
    public static final String ATTRIBUTE_LAST = "last";
    public static final String ATTRIBUTE_NR = "nr";
    public static final String ATTRIBUTE_STREAM_ID = "streamId";
    public static final String ELEMENT_CHUNK = "chunk";
    private final boolean last;
    private final int nr;
    private final String streamId;
    private final String text;

    public Base64BinaryChunk(String text2, String streamId2, int nr2, boolean last2) {
        this.text = (String) Objects.requireNonNull(text2, "text must not be null");
        this.streamId = (String) Objects.requireNonNull(streamId2, "streamId must not be null");
        if (nr2 >= 0) {
            this.nr = nr2;
            this.last = last2;
            return;
        }
        throw new IllegalArgumentException("nr must be a non negative integer");
    }

    public Base64BinaryChunk(String text2, String streamId2, int nr2) {
        this(text2, streamId2, nr2, false);
    }

    public String getStreamId() {
        return this.streamId;
    }

    public boolean isLast() {
        return this.last;
    }

    public String getText() {
        return this.text;
    }

    public int getNr() {
        return this.nr;
    }

    public String getElementName() {
        return ELEMENT_CHUNK;
    }

    public String getNamespace() {
        return "urn:xmpp:http";
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.attribute(ATTRIBUTE_STREAM_ID, this.streamId);
        xml.attribute(ATTRIBUTE_NR, this.nr);
        xml.optBooleanAttribute(ATTRIBUTE_LAST, this.last);
        xml.rightAngleBracket();
        xml.append((CharSequence) this.text);
        xml.closeElement((NamedElement) this);
        return xml;
    }
}
