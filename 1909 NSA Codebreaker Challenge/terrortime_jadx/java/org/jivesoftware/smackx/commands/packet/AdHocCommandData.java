package org.jivesoftware.smackx.commands.packet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.commands.AdHocCommand.Action;
import org.jivesoftware.smackx.commands.AdHocCommand.SpecificErrorCondition;
import org.jivesoftware.smackx.commands.AdHocCommand.Status;
import org.jivesoftware.smackx.commands.AdHocCommandNote;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.Jid;

public class AdHocCommandData extends IQ {
    public static final String ELEMENT = "command";
    public static final String NAMESPACE = "http://jabber.org/protocol/commands";
    private Action action;
    private final ArrayList<Action> actions = new ArrayList<>();
    private Action executeAction;
    private DataForm form;
    private Jid id;
    private String name;
    private String node;
    private final List<AdHocCommandNote> notes = new ArrayList();
    private String sessionID;
    private Status status;

    public static class SpecificError implements ExtensionElement {
        public static final String namespace = "http://jabber.org/protocol/commands";
        public SpecificErrorCondition condition;

        public SpecificError(SpecificErrorCondition condition2) {
            this.condition = condition2;
        }

        public String getElementName() {
            return this.condition.toString();
        }

        public String getNamespace() {
            return "http://jabber.org/protocol/commands";
        }

        public SpecificErrorCondition getCondition() {
            return this.condition;
        }

        public String toXML(String enclosingNamespace) {
            StringBuilder buf = new StringBuilder();
            buf.append('<');
            buf.append(getElementName());
            buf.append(" xmlns=\"");
            buf.append(getNamespace());
            buf.append("\"/>");
            return buf.toString();
        }
    }

    public AdHocCommandData() {
        super(ELEMENT, "http://jabber.org/protocol/commands");
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute(NodeElement.ELEMENT, this.node);
        xml.optAttribute("sessionid", this.sessionID);
        xml.optAttribute("status", (Enum<?>) this.status);
        xml.optAttribute("action", (Enum<?>) this.action);
        xml.rightAngleBracket();
        if (getType() == Type.result) {
            String str = "actions";
            xml.halfOpenElement(str);
            xml.optAttribute("execute", (Enum<?>) this.executeAction);
            if (this.actions.size() == 0) {
                xml.closeEmptyElement();
            } else {
                xml.rightAngleBracket();
                Iterator it = this.actions.iterator();
                while (it.hasNext()) {
                    xml.emptyElement((Enum<?>) (Action) it.next());
                }
                xml.closeElement(str);
            }
        }
        DataForm dataForm = this.form;
        if (dataForm != null) {
            xml.append(dataForm.toXML((String) null));
        }
        for (AdHocCommandNote note : this.notes) {
            String str2 = "note";
            xml.halfOpenElement(str2).attribute("type", note.getType().toString()).rightAngleBracket();
            xml.append((CharSequence) note.getValue());
            xml.closeElement(str2);
        }
        return xml;
    }

    public Jid getId() {
        return this.id;
    }

    public void setId(Jid id2) {
        this.id = id2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getNode() {
        return this.node;
    }

    public void setNode(String node2) {
        this.node = node2;
    }

    public List<AdHocCommandNote> getNotes() {
        return this.notes;
    }

    public void addNote(AdHocCommandNote note) {
        this.notes.add(note);
    }

    public void removeNote(AdHocCommandNote note) {
        this.notes.remove(note);
    }

    public DataForm getForm() {
        return this.form;
    }

    public void setForm(DataForm form2) {
        this.form = form2;
    }

    public Action getAction() {
        return this.action;
    }

    public void setAction(Action action2) {
        this.action = action2;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status2) {
        this.status = status2;
    }

    public List<Action> getActions() {
        return this.actions;
    }

    public void addAction(Action action2) {
        this.actions.add(action2);
    }

    public void setExecuteAction(Action executeAction2) {
        this.executeAction = executeAction2;
    }

    public Action getExecuteAction() {
        return this.executeAction;
    }

    public void setSessionID(String sessionID2) {
        this.sessionID = sessionID2;
    }

    public String getSessionID() {
        return this.sessionID;
    }
}
