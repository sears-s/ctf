package org.jivesoftware.smackx.xdata;

import com.badguy.terrortime.BuildConfig;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransferChild;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement;
import org.jxmpp.util.XmppDateTime;

public class FormField implements NamedElement {
    public static final String ELEMENT = "field";
    public static final String FORM_TYPE = "FORM_TYPE";
    private String description;
    private String label;
    private final List<Option> options;
    private boolean required;
    private Type type;
    private ValidateElement validateElement;
    private final List<CharSequence> values;
    private final String variable;

    /* renamed from: org.jivesoftware.smackx.xdata.FormField$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[Type.bool.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    public static class Option implements NamedElement {
        public static final String ELEMENT = "option";
        private String label;
        private final String value;

        public Option(String value2) {
            this.value = value2;
        }

        public Option(String label2, String value2) {
            this.label = label2;
            this.value = value2;
        }

        public String getLabel() {
            return this.label;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return getLabel();
        }

        public String getElementName() {
            return ELEMENT;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
            xml.optAttribute(AnnotatedPrivateKey.LABEL, getLabel());
            xml.rightAngleBracket();
            xml.element("value", getValue());
            xml.closeElement((NamedElement) this);
            return xml;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            Option other = (Option) obj;
            if (!this.value.equals(other.value)) {
                return false;
            }
            String thisLabel = this.label;
            String otherLabel = BuildConfig.FLAVOR;
            if (thisLabel == null) {
                thisLabel = otherLabel;
            }
            String str = other.label;
            if (str != null) {
                otherLabel = str;
            }
            if (!thisLabel.equals(otherLabel)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            int result = ((1 * 37) + this.value.hashCode()) * 37;
            String str = this.label;
            return result + (str == null ? 0 : str.hashCode());
        }
    }

    public enum Type {
        bool,
        fixed,
        hidden,
        jid_multi,
        jid_single,
        list_multi,
        list_single,
        text_multi,
        text_private,
        text_single;

        public String toString() {
            if (AnonymousClass1.$SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[ordinal()] != 1) {
                return name().replace('_', '-');
            }
            return "boolean";
        }

        public static Type fromString(String string) {
            if (string == null) {
                return null;
            }
            char c = 65535;
            if (string.hashCode() == 64711720 && string.equals("boolean")) {
                c = 0;
            }
            if (c != 0) {
                return valueOf(string.replace('-', '_'));
            }
            return bool;
        }
    }

    public FormField(String variable2) {
        this.required = false;
        this.options = new ArrayList();
        this.values = new ArrayList();
        this.variable = (String) StringUtils.requireNotNullOrEmpty(variable2, "Variable must not be null or empty");
    }

    public FormField() {
        this.required = false;
        this.options = new ArrayList();
        this.values = new ArrayList();
        this.variable = null;
        this.type = Type.fixed;
    }

    public String getDescription() {
        return this.description;
    }

    public String getLabel() {
        return this.label;
    }

    public List<Option> getOptions() {
        List<Option> unmodifiableList;
        synchronized (this.options) {
            unmodifiableList = Collections.unmodifiableList(new ArrayList(this.options));
        }
        return unmodifiableList;
    }

    public boolean isRequired() {
        return this.required;
    }

    public Type getType() {
        return this.type;
    }

    public List<CharSequence> getValues() {
        List<CharSequence> unmodifiableList;
        synchronized (this.values) {
            unmodifiableList = Collections.unmodifiableList(new ArrayList(this.values));
        }
        return unmodifiableList;
    }

    public List<String> getValuesAsString() {
        List<CharSequence> valuesAsCharSequence = getValues();
        List<String> res = new ArrayList<>(valuesAsCharSequence.size());
        for (CharSequence value : valuesAsCharSequence) {
            res.add(value.toString());
        }
        return res;
    }

    public String getFirstValue() {
        synchronized (this.values) {
            if (this.values.isEmpty()) {
                return null;
            }
            CharSequence firstValue = (CharSequence) this.values.get(0);
            return firstValue.toString();
        }
    }

    public Date getFirstValueAsDate() throws ParseException {
        String valueString = getFirstValue();
        if (valueString == null) {
            return null;
        }
        return XmppDateTime.parseXEP0082Date(valueString);
    }

    public String getVariable() {
        return this.variable;
    }

    public ValidateElement getValidateElement() {
        return this.validateElement;
    }

    public void setDescription(String description2) {
        this.description = description2;
    }

    public void setLabel(String label2) {
        this.label = label2;
    }

    public void setRequired(boolean required2) {
        this.required = required2;
    }

    public void setValidateElement(ValidateElement validateElement2) {
        validateElement2.checkConsistency(this);
        this.validateElement = validateElement2;
    }

    public void setType(Type type2) {
        if (type2 != Type.fixed) {
            this.type = type2;
            return;
        }
        throw new IllegalArgumentException("Can not set type to fixed, use FormField constructor without arguments instead.");
    }

    public void addValue(CharSequence value) {
        synchronized (this.values) {
            this.values.add(value);
        }
    }

    public void addValue(Date date) {
        addValue((CharSequence) XmppDateTime.formatXEP0082Date(date));
    }

    public void addValues(List<? extends CharSequence> newValues) {
        synchronized (this.values) {
            this.values.addAll(newValues);
        }
    }

    /* access modifiers changed from: protected */
    public void resetValues() {
        synchronized (this.values) {
            this.values.clear();
        }
    }

    public void addOption(Option option) {
        synchronized (this.options) {
            this.options.add(option);
        }
    }

    public String getElementName() {
        return ELEMENT;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder buf = new XmlStringBuilder((NamedElement) this);
        buf.optAttribute(AnnotatedPrivateKey.LABEL, getLabel());
        buf.optAttribute("var", getVariable());
        buf.optAttribute("type", (Enum<?>) getType());
        buf.rightAngleBracket();
        buf.optElement(JingleFileTransferChild.ELEM_DESC, getDescription());
        buf.condEmptyElement(isRequired(), "required");
        for (CharSequence value : getValues()) {
            buf.element("value", value);
        }
        for (Option option : getOptions()) {
            buf.append(option.toXML((String) null));
        }
        buf.optElement(this.validateElement);
        buf.closeElement((NamedElement) this);
        return buf;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FormField)) {
            return false;
        }
        return toXML((String) null).equals(((FormField) obj).toXML((String) null));
    }

    public int hashCode() {
        return toXML((String) null).hashCode();
    }
}
