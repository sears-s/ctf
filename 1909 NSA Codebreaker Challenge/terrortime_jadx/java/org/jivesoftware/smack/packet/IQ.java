package org.jivesoftware.smack.packet;

import com.badguy.terrortime.BuildConfig;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.jivesoftware.smack.packet.StanzaError.Builder;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.XmlStringBuilder;

public abstract class IQ extends Stanza {
    public static final String IQ_ELEMENT = "iq";
    public static final String QUERY_ELEMENT = "query";
    private final String childElementName;
    private final String childElementNamespace;
    private Type type;

    /* renamed from: org.jivesoftware.smack.packet.IQ$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$packet$IQ$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$packet$IQ$Type[Type.get.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$IQ$Type[Type.set.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public static class IQChildElementXmlStringBuilder extends XmlStringBuilder {
        /* access modifiers changed from: private */
        public final String element;
        /* access modifiers changed from: private */
        public boolean isEmptyElement;

        /* synthetic */ IQChildElementXmlStringBuilder(IQ x0, AnonymousClass1 x1) {
            this(x0);
        }

        private IQChildElementXmlStringBuilder(IQ iq) {
            this(iq.getChildElementName(), iq.getChildElementNamespace());
        }

        public IQChildElementXmlStringBuilder(ExtensionElement pe) {
            this(pe.getElementName(), pe.getNamespace());
        }

        private IQChildElementXmlStringBuilder(String element2, String namespace) {
            super(BuildConfig.FLAVOR);
            prelude(element2, namespace);
            this.element = element2;
        }

        public void setEmptyElement() {
            this.isEmptyElement = true;
        }
    }

    public enum Type {
        get,
        set,
        result,
        error;

        public static Type fromString(String string) {
            return valueOf(string.toLowerCase(Locale.US));
        }
    }

    /* access modifiers changed from: protected */
    public abstract IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder iQChildElementXmlStringBuilder);

    public IQ(IQ iq) {
        super((Stanza) iq);
        this.type = Type.get;
        this.type = iq.getType();
        this.childElementName = iq.childElementName;
        this.childElementNamespace = iq.childElementNamespace;
    }

    protected IQ(String childElementName2) {
        this(childElementName2, null);
    }

    protected IQ(String childElementName2, String childElementNamespace2) {
        this.type = Type.get;
        this.childElementName = childElementName2;
        this.childElementNamespace = childElementNamespace2;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type2) {
        this.type = (Type) Objects.requireNonNull(type2, "type must not be null");
    }

    public boolean isRequestIQ() {
        int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$packet$IQ$Type[this.type.ordinal()];
        if (i == 1 || i == 2) {
            return true;
        }
        return false;
    }

    public final String getChildElementName() {
        return this.childElementName;
    }

    public final String getChildElementNamespace() {
        return this.childElementNamespace;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IQ Stanza (");
        sb.append(getChildElementName());
        sb.append(' ');
        sb.append(getChildElementNamespace());
        sb.append(") [");
        logCommonAttributes(sb);
        sb.append("type=");
        sb.append(this.type);
        sb.append(',');
        sb.append(']');
        return sb.toString();
    }

    public final XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder buf = new XmlStringBuilder(enclosingNamespace);
        String str = IQ_ELEMENT;
        buf.halfOpenElement(str);
        addCommonAttributes(buf, enclosingNamespace);
        Type type2 = this.type;
        String str2 = "type";
        if (type2 == null) {
            buf.attribute(str2, "get");
        } else {
            buf.attribute(str2, type2.toString());
        }
        buf.rightAngleBracket();
        buf.append(getChildElementXML(enclosingNamespace));
        buf.closeElement(str);
        return buf;
    }

    public final XmlStringBuilder getChildElementXML() {
        return getChildElementXML(null);
    }

    public final XmlStringBuilder getChildElementXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder();
        if (this.type == Type.error) {
            appendErrorIfExists(xml, enclosingNamespace);
        } else if (this.childElementName != null) {
            IQChildElementXmlStringBuilder iqChildElement = getIQChildElementBuilder(new IQChildElementXmlStringBuilder(this, (AnonymousClass1) null));
            if (iqChildElement != null) {
                xml.append((XmlStringBuilder) iqChildElement);
                List<ExtensionElement> extensionsXml = getExtensions();
                if (iqChildElement.isEmptyElement) {
                    if (extensionsXml.isEmpty()) {
                        xml.closeEmptyElement();
                        return xml;
                    }
                    xml.rightAngleBracket();
                }
                xml.append((Collection<? extends Element>) extensionsXml);
                xml.closeElement(iqChildElement.element);
            }
        }
        return xml;
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public final void initialzeAsResultFor(IQ request) {
        initializeAsResultFor(request);
    }

    /* access modifiers changed from: protected */
    public final void initializeAsResultFor(IQ request) {
        if (request.getType() == Type.get || request.getType() == Type.set) {
            setStanzaId(request.getStanzaId());
            setFrom(request.getTo());
            setTo(request.getFrom());
            setType(Type.result);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("IQ must be of type 'set' or 'get'. Original IQ: ");
        sb.append(request.toXML((String) null));
        throw new IllegalArgumentException(sb.toString());
    }

    public static IQ createResultIQ(IQ request) {
        return new EmptyResultIQ(request);
    }

    public static ErrorIQ createErrorResponse(IQ request, Builder error) {
        if (request.getType() == Type.get || request.getType() == Type.set) {
            ErrorIQ result = new ErrorIQ(error);
            result.setStanzaId(request.getStanzaId());
            result.setFrom(request.getTo());
            result.setTo(request.getFrom());
            error.setStanza(result);
            return result;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("IQ must be of type 'set' or 'get'. Original IQ: ");
        sb.append(request.toXML((String) null));
        throw new IllegalArgumentException(sb.toString());
    }

    public static ErrorIQ createErrorResponse(IQ request, Condition condition) {
        return createErrorResponse(request, StanzaError.getBuilder(condition));
    }

    public static ErrorIQ createErrorResponse(IQ request, StanzaError error) {
        return createErrorResponse(request, StanzaError.getBuilder(error));
    }
}
