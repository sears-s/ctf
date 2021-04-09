package org.jivesoftware.smackx.xdatavalidation.packet;

import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.NumberUtil;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.FormField.Type;
import org.jivesoftware.smackx.xdatavalidation.ValidationConsistencyException;

public abstract class ValidateElement implements ExtensionElement {
    public static final String DATATYPE_XS_STRING = "xs:string";
    public static final String ELEMENT = "validate";
    public static final String NAMESPACE = "http://jabber.org/protocol/xdata-validate";
    private final String datatype;
    private ListRange listRange;

    /* renamed from: org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[Type.hidden.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[Type.jid_multi.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[Type.jid_single.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[Type.list_multi.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[Type.text_multi.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public static class BasicValidateElement extends ValidateElement {
        public static final String METHOD = "basic";

        public /* bridge */ /* synthetic */ CharSequence toXML(String str) {
            return ValidateElement.super.toXML(str);
        }

        public BasicValidateElement(String dataType) {
            super(dataType, null);
        }

        /* access modifiers changed from: protected */
        public void appendXML(XmlStringBuilder buf) {
            buf.emptyElement(METHOD);
        }

        public void checkConsistency(FormField formField) {
            checkListRangeConsistency(formField);
            if (formField.getType() != null) {
                int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[formField.getType().ordinal()];
                if (i == 1 || i == 2 || i == 3) {
                    throw new ValidationConsistencyException(String.format("Field type '%1$s' is not consistent with validation method '%2$s'.", new Object[]{formField.getType(), METHOD}));
                }
            }
        }
    }

    public static class ListRange implements NamedElement {
        public static final String ELEMENT = "list-range";
        private final Long max;
        private final Long min;

        public ListRange(Long min2, Long max2) {
            if (min2 != null) {
                NumberUtil.checkIfInUInt32Range(min2.longValue());
            }
            if (max2 != null) {
                NumberUtil.checkIfInUInt32Range(max2.longValue());
            }
            if (max2 == null && min2 == null) {
                throw new IllegalArgumentException("Either min or max must be given");
            }
            this.min = min2;
            this.max = max2;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder buf = new XmlStringBuilder((NamedElement) this);
            buf.optLongAttribute("min", getMin());
            buf.optLongAttribute("max", getMax());
            buf.closeEmptyElement();
            return buf;
        }

        public String getElementName() {
            return ELEMENT;
        }

        public Long getMin() {
            return this.min;
        }

        public Long getMax() {
            return this.max;
        }
    }

    public static class OpenValidateElement extends ValidateElement {
        public static final String METHOD = "open";

        public /* bridge */ /* synthetic */ CharSequence toXML(String str) {
            return ValidateElement.super.toXML(str);
        }

        public OpenValidateElement(String dataType) {
            super(dataType, null);
        }

        /* access modifiers changed from: protected */
        public void appendXML(XmlStringBuilder buf) {
            buf.emptyElement("open");
        }

        public void checkConsistency(FormField formField) {
            checkListRangeConsistency(formField);
            if (formField.getType() != null && AnonymousClass1.$SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[formField.getType().ordinal()] == 1) {
                throw new ValidationConsistencyException(String.format("Field type '%1$s' is not consistent with validation method '%2$s'.", new Object[]{formField.getType(), "open"}));
            }
        }
    }

    public static class RangeValidateElement extends ValidateElement {
        public static final String METHOD = "range";
        private final String max;
        private final String min;

        public /* bridge */ /* synthetic */ CharSequence toXML(String str) {
            return ValidateElement.super.toXML(str);
        }

        public RangeValidateElement(String dataType, String min2, String max2) {
            super(dataType, null);
            this.min = min2;
            this.max = max2;
        }

        /* access modifiers changed from: protected */
        public void appendXML(XmlStringBuilder buf) {
            buf.halfOpenElement("range");
            buf.optAttribute("min", getMin());
            buf.optAttribute("max", getMax());
            buf.closeEmptyElement();
        }

        public String getMin() {
            return this.min;
        }

        public String getMax() {
            return this.max;
        }

        public void checkConsistency(FormField formField) {
            String str = "range";
            checkNonMultiConsistency(formField, str);
            if (getDatatype().equals(ValidateElement.DATATYPE_XS_STRING)) {
                throw new ValidationConsistencyException(String.format("Field data type '%1$s' is not consistent with validation method '%2$s'.", new Object[]{getDatatype(), str}));
            }
        }
    }

    public static class RegexValidateElement extends ValidateElement {
        public static final String METHOD = "regex";
        private final String regex;

        public /* bridge */ /* synthetic */ CharSequence toXML(String str) {
            return ValidateElement.super.toXML(str);
        }

        public RegexValidateElement(String dataType, String regex2) {
            super(dataType, null);
            this.regex = regex2;
        }

        public String getRegex() {
            return this.regex;
        }

        /* access modifiers changed from: protected */
        public void appendXML(XmlStringBuilder buf) {
            buf.element(METHOD, getRegex());
        }

        public void checkConsistency(FormField formField) {
            checkNonMultiConsistency(formField, METHOD);
        }
    }

    /* access modifiers changed from: protected */
    public abstract void appendXML(XmlStringBuilder xmlStringBuilder);

    public abstract void checkConsistency(FormField formField);

    /* synthetic */ ValidateElement(String x0, AnonymousClass1 x1) {
        this(x0);
    }

    private ValidateElement(String datatype2) {
        this.datatype = StringUtils.isNotEmpty((CharSequence) datatype2) ? datatype2 : null;
    }

    public String getDatatype() {
        String str = this.datatype;
        return str != null ? str : DATATYPE_XS_STRING;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder buf = new XmlStringBuilder((ExtensionElement) this);
        buf.optAttribute("datatype", this.datatype);
        buf.rightAngleBracket();
        appendXML(buf);
        buf.optAppend((Element) getListRange());
        buf.closeElement((NamedElement) this);
        return buf;
    }

    public void setListRange(ListRange listRange2) {
        this.listRange = listRange2;
    }

    public ListRange getListRange() {
        return this.listRange;
    }

    /* access modifiers changed from: protected */
    public void checkListRangeConsistency(FormField formField) {
        ListRange listRange2 = getListRange();
        if (listRange2 != null) {
            Long max = listRange2.getMax();
            Long min = listRange2.getMin();
            if ((max != null || min != null) && formField.getType() != Type.list_multi) {
                throw new ValidationConsistencyException("Field type is not of type 'list-multi' while a 'list-range' is defined.");
            }
        }
    }

    /* access modifiers changed from: protected */
    public void checkNonMultiConsistency(FormField formField, String method) {
        checkListRangeConsistency(formField);
        if (formField.getType() != null) {
            int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smackx$xdata$FormField$Type[formField.getType().ordinal()];
            if (i == 1 || i == 2 || i == 4 || i == 5) {
                throw new ValidationConsistencyException(String.format("Field type '%1$s' is not consistent with validation method '%2$s'.", new Object[]{formField.getType(), method}));
            }
        }
    }
}
