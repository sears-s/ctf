package org.jivesoftware.smack.packet;

import com.badguy.terrortime.BuildConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.TypedCloneable;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

public final class Message extends Stanza implements TypedCloneable<Message> {
    public static final String BODY = "body";
    public static final String ELEMENT = "message";
    private final Set<Subject> subjects;
    private String thread;
    private Type type;

    public static final class Body implements ExtensionElement {
        public static final String ELEMENT = "body";
        public static final String NAMESPACE = "jabber:client";
        /* access modifiers changed from: private */
        public final String language;
        /* access modifiers changed from: private */
        public final String message;
        private final BodyElementNamespace namespace;

        enum BodyElementNamespace {
            client("jabber:client"),
            server(StreamOpen.SERVER_NAMESPACE);
            
            /* access modifiers changed from: private */
            public final String xmlNamespace;

            private BodyElementNamespace(String xmlNamespace2) {
                this.xmlNamespace = xmlNamespace2;
            }

            public String getNamespace() {
                return this.xmlNamespace;
            }
        }

        public Body(String language2, String message2) {
            this(language2, message2, BodyElementNamespace.client);
        }

        public Body(String language2, String message2, BodyElementNamespace namespace2) {
            if (message2 != null) {
                this.language = language2;
                this.message = message2;
                this.namespace = (BodyElementNamespace) Objects.requireNonNull(namespace2);
                return;
            }
            throw new NullPointerException("Message cannot be null.");
        }

        public String getLanguage() {
            return this.language;
        }

        public String getMessage() {
            return this.message;
        }

        public int hashCode() {
            int result = 1;
            String str = this.language;
            if (str != null) {
                result = (1 * 31) + str.hashCode();
            }
            return (result * 31) + this.message.hashCode();
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Body other = (Body) obj;
            if (!Objects.equals(this.language, other.language) || !this.message.equals(other.message)) {
                z = false;
            }
            return z;
        }

        public String getElementName() {
            return "body";
        }

        public String getNamespace() {
            return this.namespace.xmlNamespace;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder(this, enclosingNamespace);
            xml.optXmlLangAttribute(getLanguage()).rightAngleBracket();
            xml.escape(this.message);
            xml.closeElement(getElementName());
            return xml;
        }
    }

    public static final class Subject implements ExtensionElement {
        public static final String ELEMENT = "subject";
        public static final String NAMESPACE = "jabber:client";
        /* access modifiers changed from: private */
        public final String language;
        /* access modifiers changed from: private */
        public final String subject;

        private Subject(String language2, String subject2) {
            if (subject2 != null) {
                this.language = language2;
                this.subject = subject2;
                return;
            }
            throw new NullPointerException("Subject cannot be null.");
        }

        public String getLanguage() {
            return this.language;
        }

        public String getSubject() {
            return this.subject;
        }

        public int hashCode() {
            int result = 1;
            String str = this.language;
            if (str != null) {
                result = (1 * 31) + str.hashCode();
            }
            return (result * 31) + this.subject.hashCode();
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Subject other = (Subject) obj;
            if (!this.language.equals(other.language) || !this.subject.equals(other.subject)) {
                z = false;
            }
            return z;
        }

        public String getElementName() {
            return ELEMENT;
        }

        public String getNamespace() {
            return "jabber:client";
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement(getElementName()).optXmlLangAttribute(getLanguage()).rightAngleBracket();
            xml.escape(this.subject);
            xml.closeElement(getElementName());
            return xml;
        }
    }

    public enum Type {
        normal,
        chat,
        groupchat,
        headline,
        error;

        public static Type fromString(String string) {
            return valueOf(string.toLowerCase(Locale.US));
        }
    }

    public Message() {
        this.thread = null;
        this.subjects = new HashSet();
    }

    public Message(Jid to) {
        this.thread = null;
        this.subjects = new HashSet();
        setTo(to);
    }

    public Message(Jid to, Type type2) {
        this(to);
        setType(type2);
    }

    public Message(Jid to, String body) {
        this(to);
        setBody(body);
    }

    public Message(String to, String body) throws XmppStringprepException {
        this(JidCreate.from(to), body);
    }

    public Message(Jid to, ExtensionElement extensionElement) {
        this(to);
        addExtension(extensionElement);
    }

    public Message(Message other) {
        super((Stanza) other);
        this.thread = null;
        this.subjects = new HashSet();
        this.type = other.type;
        this.thread = other.thread;
        this.subjects.addAll(other.subjects);
    }

    public Type getType() {
        Type type2 = this.type;
        if (type2 == null) {
            return Type.normal;
        }
        return type2;
    }

    public void setType(Type type2) {
        this.type = type2;
    }

    public String getSubject() {
        return getSubject(null);
    }

    public String getSubject(String language) {
        Subject subject = getMessageSubject(language);
        if (subject == null) {
            return null;
        }
        return subject.subject;
    }

    private Subject getMessageSubject(String language) {
        String language2 = determineLanguage(language);
        for (Subject subject : this.subjects) {
            if (Objects.equals(language2, subject.language)) {
                return subject;
            }
        }
        return null;
    }

    public Set<Subject> getSubjects() {
        return Collections.unmodifiableSet(this.subjects);
    }

    public void setSubject(String subject) {
        if (subject == null) {
            removeSubject(BuildConfig.FLAVOR);
        } else {
            addSubject(null, subject);
        }
    }

    public Subject addSubject(String language, String subject) {
        Subject messageSubject = new Subject(determineLanguage(language), subject);
        this.subjects.add(messageSubject);
        return messageSubject;
    }

    public boolean removeSubject(String language) {
        String language2 = determineLanguage(language);
        for (Subject subject : this.subjects) {
            if (language2.equals(subject.language)) {
                return this.subjects.remove(subject);
            }
        }
        return false;
    }

    public boolean removeSubject(Subject subject) {
        return this.subjects.remove(subject);
    }

    public List<String> getSubjectLanguages() {
        Subject defaultSubject = getMessageSubject(null);
        List<String> languages = new ArrayList<>();
        for (Subject subject : this.subjects) {
            if (!subject.equals(defaultSubject)) {
                languages.add(subject.language);
            }
        }
        return Collections.unmodifiableList(languages);
    }

    public String getBody() {
        return getBody(this.language);
    }

    public String getBody(String language) {
        Body body = getMessageBody(language);
        if (body == null) {
            return null;
        }
        return body.message;
    }

    private Body getMessageBody(String language) {
        String language2 = determineLanguage(language);
        for (Body body : getBodies()) {
            if (Objects.equals(language2, body.language) || (language2 != null && language2.equals(this.language) && body.language == null)) {
                return body;
            }
        }
        return null;
    }

    public Set<Body> getBodies() {
        List<ExtensionElement> bodiesList = getExtensions("body", "jabber:client");
        Set<Body> resultSet = new HashSet<>(bodiesList.size());
        for (ExtensionElement extensionElement : bodiesList) {
            resultSet.add((Body) extensionElement);
        }
        return resultSet;
    }

    public void setBody(CharSequence body) {
        String bodyString;
        if (body != null) {
            bodyString = body.toString();
        } else {
            bodyString = null;
        }
        setBody(bodyString);
    }

    public void setBody(String body) {
        if (body == null) {
            removeBody(BuildConfig.FLAVOR);
        } else {
            addBody(null, body);
        }
    }

    public Body addBody(String language, String body) {
        String language2 = determineLanguage(language);
        removeBody(language2);
        Body messageBody = new Body(language2, body);
        addExtension(messageBody);
        return messageBody;
    }

    public boolean removeBody(String language) {
        String language2 = determineLanguage(language);
        for (Body body : getBodies()) {
            if (Objects.equals(body.getLanguage(), language2)) {
                removeExtension(body);
                return true;
            }
        }
        return false;
    }

    public boolean removeBody(Body body) {
        return removeExtension(body) != null;
    }

    public List<String> getBodyLanguages() {
        Body defaultBody = getMessageBody(null);
        List<String> languages = new ArrayList<>();
        for (Body body : getBodies()) {
            if (!body.equals(defaultBody)) {
                languages.add(body.language);
            }
        }
        return Collections.unmodifiableList(languages);
    }

    public String getThread() {
        return this.thread;
    }

    public void setThread(String thread2) {
        this.thread = thread2;
    }

    private String determineLanguage(String language) {
        String language2 = BuildConfig.FLAVOR.equals(language) ? null : language;
        if (language2 != null || this.language == null) {
            return language2;
        }
        return this.language;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Message Stanza [");
        logCommonAttributes(sb);
        if (this.type != null) {
            sb.append("type=");
            sb.append(this.type);
            sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder buf = new XmlStringBuilder(enclosingNamespace);
        String str = ELEMENT;
        buf.halfOpenElement(str);
        String enclosingNamespace2 = addCommonAttributes(buf, enclosingNamespace);
        buf.optAttribute("type", (Enum<?>) this.type);
        buf.rightAngleBracket();
        Subject defaultSubject = getMessageSubject(null);
        if (defaultSubject != null) {
            buf.element(Subject.ELEMENT, defaultSubject.subject);
        }
        for (Subject subject : getSubjects()) {
            if (!subject.equals(defaultSubject)) {
                buf.append(subject.toXML((String) null));
            }
        }
        buf.optElement("thread", this.thread);
        if (this.type == Type.error) {
            appendErrorIfExists(buf, enclosingNamespace2);
        }
        buf.append(getExtensions(), enclosingNamespace2);
        buf.closeElement(str);
        return buf;
    }

    public Message clone() {
        return new Message(this);
    }
}
