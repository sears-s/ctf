package org.jivesoftware.smack.packet;

import com.badguy.terrortime.BuildConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.PacketUtil;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class AbstractError {
    protected final Map<String, String> descriptiveTexts;
    protected final List<ExtensionElement> extensions;
    protected final String textNamespace;

    public static abstract class Builder<B extends Builder<B>> {
        protected Map<String, String> descriptiveTexts;
        protected List<ExtensionElement> extensions;
        protected String textNamespace;

        /* access modifiers changed from: protected */
        public abstract B getThis();

        public B setDescriptiveTexts(Map<String, String> descriptiveTexts2) {
            if (descriptiveTexts2 == null) {
                this.descriptiveTexts = null;
                return getThis();
            }
            for (String key : descriptiveTexts2.keySet()) {
                if (key == null) {
                    throw new IllegalArgumentException("descriptiveTexts cannot contain null key");
                }
            }
            Map<String, String> map = this.descriptiveTexts;
            if (map == null) {
                this.descriptiveTexts = descriptiveTexts2;
            } else {
                map.putAll(descriptiveTexts2);
            }
            return getThis();
        }

        public B setDescriptiveEnText(String descriptiveEnText) {
            if (this.descriptiveTexts == null) {
                this.descriptiveTexts = new HashMap();
            }
            this.descriptiveTexts.put("en", descriptiveEnText);
            return getThis();
        }

        public B setTextNamespace(String textNamespace2) {
            this.textNamespace = textNamespace2;
            return getThis();
        }

        public B setExtensions(List<ExtensionElement> extensions2) {
            List<ExtensionElement> list = this.extensions;
            if (list == null) {
                this.extensions = extensions2;
            } else {
                list.addAll(extensions2);
            }
            return getThis();
        }

        public B addExtension(ExtensionElement extension) {
            if (this.extensions == null) {
                this.extensions = new ArrayList();
            }
            this.extensions.add(extension);
            return getThis();
        }
    }

    protected AbstractError(Map<String, String> descriptiveTexts2) {
        this(descriptiveTexts2, null);
    }

    protected AbstractError(Map<String, String> descriptiveTexts2, List<ExtensionElement> extensions2) {
        this(descriptiveTexts2, null, extensions2);
    }

    protected AbstractError(Map<String, String> descriptiveTexts2, String textNamespace2, List<ExtensionElement> extensions2) {
        if (descriptiveTexts2 != null) {
            this.descriptiveTexts = descriptiveTexts2;
        } else {
            this.descriptiveTexts = Collections.emptyMap();
        }
        this.textNamespace = textNamespace2;
        if (extensions2 != null) {
            this.extensions = extensions2;
        } else {
            this.extensions = Collections.emptyList();
        }
    }

    public String getDescriptiveText() {
        String descriptiveText = getDescriptiveText(Locale.getDefault().getLanguage());
        if (descriptiveText != null) {
            return descriptiveText;
        }
        String descriptiveText2 = getDescriptiveText("en");
        if (descriptiveText2 == null) {
            return getDescriptiveText(BuildConfig.FLAVOR);
        }
        return descriptiveText2;
    }

    public String getDescriptiveText(String xmllang) {
        Objects.requireNonNull(xmllang, "xmllang must not be null");
        return (String) this.descriptiveTexts.get(xmllang);
    }

    public <PE extends ExtensionElement> PE getExtension(String elementName, String namespace) {
        return PacketUtil.extensionElementFrom(this.extensions, elementName, namespace);
    }

    /* access modifiers changed from: protected */
    public void addDescriptiveTextsAndExtensions(XmlStringBuilder xml) {
        for (Entry<String, String> entry : this.descriptiveTexts.entrySet()) {
            String text = (String) entry.getValue();
            String str = "text";
            xml.halfOpenElement(str).xmlnsAttribute(this.textNamespace).optXmlLangAttribute((String) entry.getKey()).rightAngleBracket();
            xml.escape(text);
            xml.closeElement(str);
        }
        for (ExtensionElement packetExtension : this.extensions) {
            xml.append(packetExtension.toXML(null));
        }
    }
}
