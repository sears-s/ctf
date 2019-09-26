package org.jivesoftware.smackx.xdata.packet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bouncycastle.i18n.MessageBundle;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.xdata.FormField;

public class DataForm implements ExtensionElement {
    public static final String ELEMENT = "x";
    public static final String NAMESPACE = "jabber:x:data";
    private final List<Element> extensionElements = new ArrayList();
    private final Map<String, FormField> fields = new LinkedHashMap();
    private final List<String> instructions = new ArrayList();
    private final List<Item> items = new ArrayList();
    private ReportedData reportedData;
    private String title;
    private Type type;

    public static class Item {
        public static final String ELEMENT = "item";
        private List<FormField> fields = new ArrayList();

        public Item(List<FormField> fields2) {
            this.fields = fields2;
        }

        public List<FormField> getFields() {
            return Collections.unmodifiableList(new ArrayList(this.fields));
        }

        public CharSequence toXML() {
            XmlStringBuilder buf = new XmlStringBuilder();
            String str = "item";
            buf.openElement(str);
            for (FormField field : getFields()) {
                buf.append(field.toXML((String) null));
            }
            buf.closeElement(str);
            return buf;
        }
    }

    public static class ReportedData {
        public static final String ELEMENT = "reported";
        private List<FormField> fields = new ArrayList();

        public ReportedData(List<FormField> fields2) {
            this.fields = fields2;
        }

        public List<FormField> getFields() {
            return Collections.unmodifiableList(new ArrayList(this.fields));
        }

        public CharSequence toXML() {
            XmlStringBuilder buf = new XmlStringBuilder();
            String str = ELEMENT;
            buf.openElement(str);
            for (FormField field : getFields()) {
                buf.append(field.toXML((String) null));
            }
            buf.closeElement(str);
            return buf;
        }
    }

    public enum Type {
        form,
        submit,
        cancel,
        result;

        public static Type fromString(String string) {
            return valueOf(string.toLowerCase(Locale.US));
        }
    }

    public DataForm(Type type2) {
        this.type = type2;
    }

    public Type getType() {
        return this.type;
    }

    public String getTitle() {
        return this.title;
    }

    public List<String> getInstructions() {
        List<String> unmodifiableList;
        synchronized (this.instructions) {
            unmodifiableList = Collections.unmodifiableList(new ArrayList(this.instructions));
        }
        return unmodifiableList;
    }

    public ReportedData getReportedData() {
        return this.reportedData;
    }

    public List<Item> getItems() {
        List<Item> unmodifiableList;
        synchronized (this.items) {
            unmodifiableList = Collections.unmodifiableList(new ArrayList(this.items));
        }
        return unmodifiableList;
    }

    public List<FormField> getFields() {
        ArrayList arrayList;
        synchronized (this.fields) {
            arrayList = new ArrayList(this.fields.values());
        }
        return arrayList;
    }

    public FormField getField(String variableName) {
        FormField formField;
        synchronized (this.fields) {
            formField = (FormField) this.fields.get(variableName);
        }
        return formField;
    }

    public boolean hasField(String variableName) {
        boolean containsKey;
        synchronized (this.fields) {
            containsKey = this.fields.containsKey(variableName);
        }
        return containsKey;
    }

    public String getElementName() {
        return "x";
    }

    public String getNamespace() {
        return "jabber:x:data";
    }

    public void setTitle(String title2) {
        this.title = title2;
    }

    public void setInstructions(List<String> instructions2) {
        synchronized (this.instructions) {
            this.instructions.clear();
            this.instructions.addAll(instructions2);
        }
    }

    public void setReportedData(ReportedData reportedData2) {
        this.reportedData = reportedData2;
    }

    public void addField(FormField field) {
        String fieldVariableName = field.getVariable();
        if (fieldVariableName == null || !hasField(fieldVariableName)) {
            synchronized (this.fields) {
                this.fields.put(fieldVariableName, field);
            }
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("This data form already contains a form field with the variable name '");
        sb.append(fieldVariableName);
        sb.append("'");
        throw new IllegalArgumentException(sb.toString());
    }

    public boolean addFields(Collection<FormField> fieldsToAdd) {
        boolean fieldOverridden = false;
        synchronized (this.fields) {
            for (FormField field : fieldsToAdd) {
                if (((FormField) this.fields.put(field.getVariable(), field)) != null) {
                    fieldOverridden = true;
                }
            }
        }
        return fieldOverridden;
    }

    public void addInstruction(String instruction) {
        synchronized (this.instructions) {
            this.instructions.add(instruction);
        }
    }

    public void addItem(Item item) {
        synchronized (this.items) {
            this.items.add(item);
        }
    }

    public void addExtensionElement(Element element) {
        this.extensionElements.add(element);
    }

    public List<Element> getExtensionElements() {
        return Collections.unmodifiableList(this.extensionElements);
    }

    public FormField getHiddenFormTypeField() {
        FormField field = getField(FormField.FORM_TYPE);
        if (field == null || field.getType() != org.jivesoftware.smackx.xdata.FormField.Type.hidden) {
            return null;
        }
        return field;
    }

    public boolean hasHiddenFormTypeField() {
        return getHiddenFormTypeField() != null;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder buf = new XmlStringBuilder((ExtensionElement) this);
        buf.attribute("type", (Enum<?>) getType());
        buf.rightAngleBracket();
        buf.optElement(MessageBundle.TITLE_ENTRY, getTitle());
        for (String instruction : getInstructions()) {
            buf.element("instructions", instruction);
        }
        if (getReportedData() != null) {
            buf.append(getReportedData().toXML());
        }
        for (Item item : getItems()) {
            buf.append(item.toXML());
        }
        for (FormField field : getFields()) {
            buf.append(field.toXML((String) null));
        }
        for (Element element : this.extensionElements) {
            buf.append(element.toXML(null));
        }
        buf.closeElement((NamedElement) this);
        return buf;
    }

    public static DataForm from(Stanza packet) {
        return (DataForm) packet.getExtension("x", "jabber:x:data");
    }
}
