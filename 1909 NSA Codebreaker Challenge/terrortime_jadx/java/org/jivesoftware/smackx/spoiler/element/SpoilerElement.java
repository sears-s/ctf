package org.jivesoftware.smackx.spoiler.element;

import com.badguy.terrortime.BuildConfig;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class SpoilerElement implements ExtensionElement {
    public static final String ELEMENT = "spoiler";
    public static final SpoilerElement EMPTY = new SpoilerElement(null, null);
    public static final String NAMESPACE = "urn:xmpp:spoiler:0";
    private final String hint;
    private final String language;

    public SpoilerElement(String language2, String hint2) {
        if (!StringUtils.isNotEmpty((CharSequence) language2) || !StringUtils.isNullOrEmpty((CharSequence) hint2)) {
            this.language = language2;
            this.hint = hint2;
            return;
        }
        throw new IllegalArgumentException("Hint cannot be null or empty if language is not empty.");
    }

    public String getHint() {
        return this.hint;
    }

    public static void addSpoiler(Message message) {
        message.addExtension(EMPTY);
    }

    public static void addSpoiler(Message message, String hint2) {
        message.addExtension(new SpoilerElement(null, hint2));
    }

    public static void addSpoiler(Message message, String lang, String hint2) {
        message.addExtension(new SpoilerElement(lang, hint2));
    }

    public static boolean containsSpoiler(Message message) {
        return message.hasExtension(ELEMENT, "urn:xmpp:spoiler:0");
    }

    public static Map<String, String> getSpoilers(Message message) {
        if (!containsSpoiler(message)) {
            return Collections.emptyMap();
        }
        List<ExtensionElement> spoilers = message.getExtensions(ELEMENT, "urn:xmpp:spoiler:0");
        Map<String, String> map = new HashMap<>();
        for (ExtensionElement e : spoilers) {
            SpoilerElement s = (SpoilerElement) e;
            String language2 = s.getLanguage();
            String str = BuildConfig.FLAVOR;
            if (language2 == null || s.getLanguage().equals(str)) {
                map.put(str, s.getHint());
            } else {
                map.put(s.getLanguage(), s.getHint());
            }
        }
        return map;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getNamespace() {
        return "urn:xmpp:spoiler:0";
    }

    public String getElementName() {
        return ELEMENT;
    }

    public CharSequence toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.optXmlLangAttribute(getLanguage());
        if (getHint() == null) {
            xml.closeEmptyElement();
        } else {
            xml.rightAngleBracket();
            xml.append((CharSequence) getHint());
            xml.closeElement((NamedElement) this);
        }
        return xml;
    }
}
