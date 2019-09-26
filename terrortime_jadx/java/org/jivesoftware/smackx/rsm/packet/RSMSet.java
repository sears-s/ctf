package org.jivesoftware.smackx.rsm.packet;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.hoxt.packet.Base64BinaryChunk;

public class RSMSet implements ExtensionElement {
    public static final String ELEMENT = "set";
    public static final String NAMESPACE = "http://jabber.org/protocol/rsm";
    private final String after;
    private final String before;
    private final int count;
    private final int firstIndex;
    private final String firstString;
    private final int index;
    private final String last;
    private final int max;

    /* renamed from: org.jivesoftware.smackx.rsm.packet.RSMSet$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smackx$rsm$packet$RSMSet$PageDirection = new int[PageDirection.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smackx$rsm$packet$RSMSet$PageDirection[PageDirection.before.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$rsm$packet$RSMSet$PageDirection[PageDirection.after.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public enum PageDirection {
        before,
        after
    }

    public RSMSet(int max2) {
        this(max2, -1);
    }

    public RSMSet(int max2, int index2) {
        this(null, null, -1, index2, null, max2, null, -1);
    }

    public RSMSet(String item, PageDirection pageDirection) {
        this(-1, item, pageDirection);
    }

    public RSMSet(int max2, String item, PageDirection pageDirection) {
        int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smackx$rsm$packet$RSMSet$PageDirection[pageDirection.ordinal()];
        if (i == 1) {
            this.before = item;
            this.after = null;
        } else if (i == 2) {
            this.before = null;
            this.after = item;
        } else {
            throw new AssertionError();
        }
        this.count = -1;
        this.index = -1;
        this.last = null;
        this.max = max2;
        this.firstString = null;
        this.firstIndex = -1;
    }

    public RSMSet(String after2, String before2, int count2, int index2, String last2, int max2, String firstString2, int firstIndex2) {
        this.after = after2;
        this.before = before2;
        this.count = count2;
        this.index = index2;
        this.last = last2;
        this.max = max2;
        this.firstString = firstString2;
        this.firstIndex = firstIndex2;
    }

    public String getAfter() {
        return this.after;
    }

    public String getBefore() {
        return this.before;
    }

    public int getCount() {
        return this.count;
    }

    public int getIndex() {
        return this.index;
    }

    public String getLast() {
        return this.last;
    }

    public int getMax() {
        return this.max;
    }

    public String getFirst() {
        return this.firstString;
    }

    public int getFirstIndex() {
        return this.firstIndex;
    }

    public String getElementName() {
        return "set";
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.rightAngleBracket();
        xml.optElement("after", this.after);
        xml.optElement("before", this.before);
        xml.optIntElement("count", this.count);
        String str = "index";
        if (this.firstString != null) {
            String str2 = "first";
            xml.halfOpenElement(str2);
            xml.optIntAttribute(str, this.firstIndex);
            xml.rightAngleBracket();
            xml.append((CharSequence) this.firstString);
            xml.closeElement(str2);
        }
        xml.optIntElement(str, this.index);
        xml.optElement(Base64BinaryChunk.ATTRIBUTE_LAST, this.last);
        xml.optIntElement("max", this.max);
        xml.closeElement((NamedElement) this);
        return xml;
    }

    public static RSMSet from(Stanza packet) {
        return (RSMSet) packet.getExtension("set", NAMESPACE);
    }

    public static RSMSet newAfter(String after2) {
        return new RSMSet(after2, PageDirection.after);
    }

    public static RSMSet newBefore(String before2) {
        return new RSMSet(before2, PageDirection.before);
    }
}
