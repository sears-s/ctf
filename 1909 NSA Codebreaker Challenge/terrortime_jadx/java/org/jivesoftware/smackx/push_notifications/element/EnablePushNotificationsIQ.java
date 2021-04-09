package org.jivesoftware.smackx.push_notifications.element;

import java.util.HashMap;
import java.util.Map.Entry;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.Jid;

public class EnablePushNotificationsIQ extends IQ {
    public static final String ELEMENT = "enable";
    public static final String NAMESPACE = "urn:xmpp:push:0";
    private final Jid jid;
    private final String node;
    private final HashMap<String, String> publishOptions;

    public EnablePushNotificationsIQ(Jid jid2, String node2, HashMap<String, String> publishOptions2) {
        super("enable", "urn:xmpp:push:0");
        this.jid = jid2;
        this.node = node2;
        this.publishOptions = publishOptions2;
        setType(Type.set);
    }

    public EnablePushNotificationsIQ(Jid jid2, String node2) {
        this(jid2, node2, null);
    }

    public Jid getJid() {
        return this.jid;
    }

    public String getNode() {
        return this.node;
    }

    public HashMap<String, String> getPublishOptions() {
        return this.publishOptions;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("jid", (CharSequence) this.jid);
        xml.attribute(NodeElement.ELEMENT, this.node);
        xml.rightAngleBracket();
        if (this.publishOptions != null) {
            DataForm dataForm = new DataForm(DataForm.Type.submit);
            FormField formTypeField = new FormField(FormField.FORM_TYPE);
            formTypeField.addValue((CharSequence) "http://jabber.org/protocol/pubsub#publish-options");
            dataForm.addField(formTypeField);
            for (Entry<String, String> pairVariableValue : this.publishOptions.entrySet()) {
                FormField field = new FormField((String) pairVariableValue.getKey());
                field.addValue((CharSequence) pairVariableValue.getValue());
                dataForm.addField(field);
            }
            xml.element(dataForm);
        }
        return xml;
    }
}
