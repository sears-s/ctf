package org.jivesoftware.smackx.pubsub;

import org.jivesoftware.smackx.xdata.Form;

public class FormNode extends NodeExtension {
    private final Form configForm;

    public FormNode(FormNodeType formType, Form submitForm) {
        super(formType.getNodeElement());
        if (submitForm != null) {
            this.configForm = submitForm;
            return;
        }
        throw new IllegalArgumentException("Submit form cannot be null");
    }

    public FormNode(FormNodeType formType, String nodeId, Form submitForm) {
        super(formType.getNodeElement(), nodeId);
        if (submitForm != null) {
            this.configForm = submitForm;
            return;
        }
        throw new IllegalArgumentException("Submit form cannot be null");
    }

    public Form getForm() {
        return this.configForm;
    }

    public CharSequence toXML(String enclosingNamespace) {
        if (this.configForm == null) {
            return super.toXML(enclosingNamespace);
        }
        StringBuilder builder = new StringBuilder("<");
        builder.append(getElementName());
        if (getNode() != null) {
            builder.append(" node='");
            builder.append(getNode());
            builder.append("'>");
        } else {
            builder.append('>');
        }
        builder.append(this.configForm.getDataFormToSend().toXML((String) null));
        builder.append("</");
        StringBuilder sb = new StringBuilder();
        sb.append(getElementName());
        sb.append('>');
        builder.append(sb.toString());
        return builder.toString();
    }
}
