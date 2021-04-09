package org.jivesoftware.smackx.commands;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.commands.AdHocCommand.Action;
import org.jivesoftware.smackx.commands.packet.AdHocCommandData;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.Jid;

public class RemoteCommand extends AdHocCommand {
    private final XMPPConnection connection;
    private final Jid jid;
    private String sessionID;

    protected RemoteCommand(XMPPConnection connection2, String node, Jid jid2) {
        this.connection = connection2;
        this.jid = jid2;
        setNode(node);
    }

    public void cancel() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        executeAction(Action.cancel);
    }

    public void complete(Form form) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        executeAction(Action.complete, form);
    }

    public void execute() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        executeAction(Action.execute);
    }

    public void execute(Form form) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        executeAction(Action.execute, form);
    }

    public void next(Form form) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        executeAction(Action.next, form);
    }

    public void prev() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        executeAction(Action.prev);
    }

    private void executeAction(Action action) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        executeAction(action, null);
    }

    private void executeAction(Action action, Form form) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        AdHocCommandData data = new AdHocCommandData();
        data.setType(Type.set);
        data.setTo(getOwnerJID());
        data.setNode(getNode());
        data.setSessionID(this.sessionID);
        data.setAction(action);
        if (form != null) {
            data.setForm(form.getDataFormToSend());
        }
        AdHocCommandData responseData = null;
        try {
            responseData = (AdHocCommandData) this.connection.createStanzaCollectorAndSend(data).nextResultOrThrow();
        } finally {
            if (responseData != null) {
                this.sessionID = responseData.getSessionID();
                super.setData(responseData);
            }
        }
    }

    public Jid getOwnerJID() {
        return this.jid;
    }
}
