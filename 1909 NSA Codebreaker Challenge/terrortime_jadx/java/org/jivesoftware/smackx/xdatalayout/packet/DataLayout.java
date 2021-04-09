package org.jivesoftware.smackx.xdatalayout.packet;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class DataLayout implements ExtensionElement {
    public static final String ELEMENT = "page";
    public static final String NAMESPACE = "http://jabber.org/protocol/xdata-layout";
    private final String label;
    private final List<DataFormLayoutElement> pageLayout = new ArrayList();

    public interface DataFormLayoutElement extends NamedElement {
    }

    public static class Fieldref implements DataFormLayoutElement {
        public static final String ELEMENT = "fieldref";
        private final String var;

        public Fieldref(String var2) {
            this.var = var2;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder buf = new XmlStringBuilder((NamedElement) this);
            buf.attribute("var", getVar());
            buf.closeEmptyElement();
            return buf;
        }

        public String getVar() {
            return this.var;
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static class Reportedref implements DataFormLayoutElement {
        public static final String ELEMENT = "reportedref";

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder buf = new XmlStringBuilder((NamedElement) this);
            buf.closeEmptyElement();
            return buf;
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static class Section implements DataFormLayoutElement {
        public static final String ELEMENT = "section";
        private final String label;
        private final List<DataFormLayoutElement> sectionLayout = new ArrayList();

        public Section(String label2) {
            this.label = label2;
        }

        public List<DataFormLayoutElement> getSectionLayout() {
            return this.sectionLayout;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder buf = new XmlStringBuilder((NamedElement) this);
            buf.optAttribute(AnnotatedPrivateKey.LABEL, getLabel());
            buf.rightAngleBracket();
            DataLayout.walkList(buf, getSectionLayout());
            buf.closeElement(ELEMENT);
            return buf;
        }

        public String getLabel() {
            return this.label;
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static class Text implements DataFormLayoutElement {
        public static final String ELEMENT = "text";
        private final String text;

        public Text(String text2) {
            this.text = text2;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder buf = new XmlStringBuilder();
            buf.element("text", getText());
            return buf;
        }

        public String getText() {
            return this.text;
        }

        public String getElementName() {
            return "text";
        }
    }

    public DataLayout(String label2) {
        this.label = label2;
    }

    public List<DataFormLayoutElement> getPageLayout() {
        return this.pageLayout;
    }

    public String getLabel() {
        return this.label;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder buf = new XmlStringBuilder((ExtensionElement) this);
        buf.optAttribute(AnnotatedPrivateKey.LABEL, getLabel());
        buf.rightAngleBracket();
        walkList(buf, getPageLayout());
        buf.closeElement((NamedElement) this);
        return buf;
    }

    /* access modifiers changed from: private */
    public static void walkList(XmlStringBuilder buf, List<DataFormLayoutElement> pageLayout2) {
        for (DataFormLayoutElement object : pageLayout2) {
            buf.append(object.toXML(null));
        }
    }
}
