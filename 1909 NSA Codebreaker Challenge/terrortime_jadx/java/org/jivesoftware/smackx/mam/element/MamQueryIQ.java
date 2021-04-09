package org.jivesoftware.smackx.mam.element;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;

public class MamQueryIQ extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "urn:xmpp:mam:1";
    private final DataForm dataForm;
    private final String node;
    private final String queryId;

    public MamQueryIQ(String queryId2) {
        this(queryId2, null, null);
        setType(Type.get);
    }

    public MamQueryIQ(DataForm form) {
        this(null, null, form);
    }

    public MamQueryIQ(String queryId2, DataForm form) {
        this(queryId2, null, form);
    }

    public MamQueryIQ(String queryId2, String node2, DataForm dataForm2) {
        String str = "urn:xmpp:mam:1";
        super("query", str);
        this.queryId = queryId2;
        this.node = node2;
        this.dataForm = dataForm2;
        if (dataForm2 != null) {
            FormField field = dataForm2.getHiddenFormTypeField();
            if (field == null) {
                throw new IllegalArgumentException("If a data form is given it must posses a hidden form type field");
            } else if (((CharSequence) field.getValues().get(0)).equals(str)) {
                addExtension(dataForm2);
            } else {
                throw new IllegalArgumentException("Value of the hidden form type field must be 'urn:xmpp:mam:1'");
            }
        }
    }

    public String getQueryId() {
        return this.queryId;
    }

    public String getNode() {
        return this.node;
    }

    public DataForm getDataForm() {
        return this.dataForm;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.optAttribute("queryid", this.queryId);
        xml.optAttribute(NodeElement.ELEMENT, this.node);
        xml.rightAngleBracket();
        return xml;
    }
}
