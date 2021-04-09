package org.jivesoftware.smack.sm.packet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Nonza;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smack.packet.StanzaErrorTextElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.xhtmlim.XHTMLText;

public class StreamManagement {
    public static final String NAMESPACE = "urn:xmpp:sm:3";

    private static abstract class AbstractEnable implements Nonza {
        protected int max;
        protected boolean resume;

        private AbstractEnable() {
            this.max = -1;
            this.resume = false;
        }

        /* access modifiers changed from: protected */
        public void maybeAddResumeAttributeTo(XmlStringBuilder xml) {
            if (this.resume) {
                xml.attribute(Resume.ELEMENT, "true");
            }
        }

        /* access modifiers changed from: protected */
        public void maybeAddMaxAttributeTo(XmlStringBuilder xml) {
            int i = this.max;
            if (i > 0) {
                xml.attribute("max", Integer.toString(i));
            }
        }

        public boolean isResumeSet() {
            return this.resume;
        }

        public int getMaxResumptionTime() {
            return this.max;
        }

        public final String getNamespace() {
            return StreamManagement.NAMESPACE;
        }
    }

    private static abstract class AbstractResume implements Nonza {
        private final long handledCount;
        private final String previd;

        private AbstractResume(long handledCount2, String previd2) {
            this.handledCount = handledCount2;
            this.previd = previd2;
        }

        public long getHandledCount() {
            return this.handledCount;
        }

        public String getPrevId() {
            return this.previd;
        }

        public final String getNamespace() {
            return StreamManagement.NAMESPACE;
        }

        public final XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
            xml.attribute(XHTMLText.H, Long.toString(this.handledCount));
            xml.attribute("previd", this.previd);
            xml.closeEmptyElement();
            return xml;
        }
    }

    public static class AckAnswer implements Nonza {
        public static final String ELEMENT = "a";
        private final long handledCount;

        public AckAnswer(long handledCount2) {
            this.handledCount = handledCount2;
        }

        public long getHandledCount() {
            return this.handledCount;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
            xml.attribute(XHTMLText.H, Long.toString(this.handledCount));
            xml.closeEmptyElement();
            return xml;
        }

        public String getNamespace() {
            return StreamManagement.NAMESPACE;
        }

        public String getElementName() {
            return "a";
        }
    }

    public static final class AckRequest implements Nonza {
        public static final String ELEMENT = "r";
        public static final AckRequest INSTANCE = new AckRequest();

        private AckRequest() {
        }

        public CharSequence toXML(String enclosingNamespace) {
            return "<r xmlns='urn:xmpp:sm:3'/>";
        }

        public String getNamespace() {
            return StreamManagement.NAMESPACE;
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static class Enable extends AbstractEnable {
        public static final String ELEMENT = "enable";
        public static final Enable INSTANCE = new Enable();

        public /* bridge */ /* synthetic */ int getMaxResumptionTime() {
            return super.getMaxResumptionTime();
        }

        public /* bridge */ /* synthetic */ boolean isResumeSet() {
            return super.isResumeSet();
        }

        private Enable() {
            super();
        }

        public Enable(boolean resume) {
            super();
            this.resume = resume;
        }

        public Enable(boolean resume, int max) {
            this(resume);
            this.max = max;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
            maybeAddResumeAttributeTo(xml);
            maybeAddMaxAttributeTo(xml);
            xml.closeEmptyElement();
            return xml;
        }

        public String getElementName() {
            return "enable";
        }
    }

    public static class Enabled extends AbstractEnable {
        public static final String ELEMENT = "enabled";
        private final String id;
        private final String location;

        public /* bridge */ /* synthetic */ int getMaxResumptionTime() {
            return super.getMaxResumptionTime();
        }

        public /* bridge */ /* synthetic */ boolean isResumeSet() {
            return super.isResumeSet();
        }

        public Enabled(String id2, boolean resume) {
            this(id2, resume, null, -1);
        }

        public Enabled(String id2, boolean resume, String location2, int max) {
            super();
            this.id = id2;
            this.resume = resume;
            this.location = location2;
            this.max = max;
        }

        public String getId() {
            return this.id;
        }

        public String getLocation() {
            return this.location;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
            xml.optAttribute("id", this.id);
            maybeAddResumeAttributeTo(xml);
            xml.optAttribute("location", this.location);
            maybeAddMaxAttributeTo(xml);
            xml.closeEmptyElement();
            return xml;
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static class Failed implements Nonza {
        public static final String ELEMENT = "failed";
        private final Condition condition;
        private final List<StanzaErrorTextElement> textElements;

        public Failed() {
            this(null, null);
        }

        public Failed(Condition condition2, List<StanzaErrorTextElement> textElements2) {
            this.condition = condition2;
            if (textElements2 == null) {
                this.textElements = Collections.emptyList();
            } else {
                this.textElements = Collections.unmodifiableList(textElements2);
            }
        }

        public Condition getStanzaErrorCondition() {
            return this.condition;
        }

        public List<StanzaErrorTextElement> getTextElements() {
            return this.textElements;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
            if (this.condition != null || !this.textElements.isEmpty()) {
                if (this.condition != null) {
                    xml.rightAngleBracket();
                    xml.append((CharSequence) this.condition.toString());
                    xml.xmlnsAttribute("urn:ietf:params:xml:ns:xmpp-stanzas");
                    xml.closeEmptyElement();
                }
                xml.append((Collection<? extends Element>) this.textElements);
                xml.closeElement(ELEMENT);
            } else {
                xml.closeEmptyElement();
            }
            return xml;
        }

        public String getNamespace() {
            return StreamManagement.NAMESPACE;
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static class Resume extends AbstractResume {
        public static final String ELEMENT = "resume";

        public /* bridge */ /* synthetic */ long getHandledCount() {
            return super.getHandledCount();
        }

        public /* bridge */ /* synthetic */ String getPrevId() {
            return super.getPrevId();
        }

        public Resume(long handledCount, String previd) {
            super(handledCount, previd);
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static class Resumed extends AbstractResume {
        public static final String ELEMENT = "resumed";

        public /* bridge */ /* synthetic */ long getHandledCount() {
            return super.getHandledCount();
        }

        public /* bridge */ /* synthetic */ String getPrevId() {
            return super.getPrevId();
        }

        public Resumed(long handledCount, String previd) {
            super(handledCount, previd);
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static final class StreamManagementFeature implements ExtensionElement {
        public static final String ELEMENT = "sm";
        public static final StreamManagementFeature INSTANCE = new StreamManagementFeature();

        private StreamManagementFeature() {
        }

        public String getElementName() {
            return ELEMENT;
        }

        public String getNamespace() {
            return StreamManagement.NAMESPACE;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
            xml.closeEmptyElement();
            return xml;
        }
    }
}
