package org.jivesoftware.smackx.commands;

import java.util.List;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smackx.commands.packet.AdHocCommandData;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.Jid;

public abstract class AdHocCommand {
    private AdHocCommandData data = new AdHocCommandData();

    public enum Action {
        execute,
        cancel,
        prev,
        next,
        complete,
        unknown
    }

    public enum SpecificErrorCondition {
        badAction("bad-action"),
        malformedAction("malformed-action"),
        badLocale("bad-locale"),
        badPayload("bad-payload"),
        badSessionid("bad-sessionid"),
        sessionExpired("session-expired");
        
        private final String value;

        private SpecificErrorCondition(String value2) {
            this.value = value2;
        }

        public String toString() {
            return this.value;
        }
    }

    public enum Status {
        executing,
        completed,
        canceled
    }

    public abstract void cancel() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException;

    public abstract void complete(Form form) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException;

    public abstract void execute() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException;

    public abstract Jid getOwnerJID();

    public abstract void next(Form form) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException;

    public abstract void prev() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException;

    public static SpecificErrorCondition getSpecificErrorCondition(StanzaError error) {
        SpecificErrorCondition[] values;
        for (SpecificErrorCondition condition : SpecificErrorCondition.values()) {
            if (error.getExtension(condition.toString(), "http://jabber.org/protocol/commands") != null) {
                return condition;
            }
        }
        return null;
    }

    public void setName(String name) {
        this.data.setName(name);
    }

    public String getName() {
        return this.data.getName();
    }

    public void setNode(String node) {
        this.data.setNode(node);
    }

    public String getNode() {
        return this.data.getNode();
    }

    public List<AdHocCommandNote> getNotes() {
        return this.data.getNotes();
    }

    /* access modifiers changed from: protected */
    public void addNote(AdHocCommandNote note) {
        this.data.addNote(note);
    }

    public String getRaw() {
        return this.data.getChildElementXML().toString();
    }

    public Form getForm() {
        if (this.data.getForm() == null) {
            return null;
        }
        return new Form(this.data.getForm());
    }

    /* access modifiers changed from: protected */
    public void setForm(Form form) {
        this.data.setForm(form.getDataFormToSend());
    }

    /* access modifiers changed from: protected */
    public List<Action> getActions() {
        return this.data.getActions();
    }

    /* access modifiers changed from: protected */
    public void addActionAvailable(Action action) {
        this.data.addAction(action);
    }

    /* access modifiers changed from: protected */
    public Action getExecuteAction() {
        return this.data.getExecuteAction();
    }

    /* access modifiers changed from: protected */
    public void setExecuteAction(Action action) {
        this.data.setExecuteAction(action);
    }

    public Status getStatus() {
        return this.data.getStatus();
    }

    public boolean isCompleted() {
        return getStatus() == Status.completed;
    }

    /* access modifiers changed from: 0000 */
    public void setData(AdHocCommandData data2) {
        this.data = data2;
    }

    /* access modifiers changed from: 0000 */
    public AdHocCommandData getData() {
        return this.data;
    }

    /* access modifiers changed from: protected */
    public boolean isValidAction(Action action) {
        return getActions().contains(action) || Action.cancel.equals(action);
    }
}
