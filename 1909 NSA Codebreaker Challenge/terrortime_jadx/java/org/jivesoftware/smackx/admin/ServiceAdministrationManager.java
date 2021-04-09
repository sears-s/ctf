package org.jivesoftware.smackx.admin;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.commands.AdHocCommandManager;
import org.jivesoftware.smackx.commands.RemoteCommand;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.util.JidUtil;

public class ServiceAdministrationManager extends Manager {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final String COMMAND_NODE = "http://jabber.org/protocol/admin";
    private static final String COMMAND_NODE_HASHSIGN = "http://jabber.org/protocol/admin#";
    private static final Map<XMPPConnection, ServiceAdministrationManager> INSTANCES = new WeakHashMap();
    private final AdHocCommandManager adHocCommandManager;

    public static synchronized ServiceAdministrationManager getInstanceFor(XMPPConnection connection) {
        ServiceAdministrationManager serviceAdministrationManager;
        synchronized (ServiceAdministrationManager.class) {
            serviceAdministrationManager = (ServiceAdministrationManager) INSTANCES.get(connection);
            if (serviceAdministrationManager == null) {
                serviceAdministrationManager = new ServiceAdministrationManager(connection);
                INSTANCES.put(connection, serviceAdministrationManager);
            }
        }
        return serviceAdministrationManager;
    }

    public ServiceAdministrationManager(XMPPConnection connection) {
        super(connection);
        this.adHocCommandManager = AdHocCommandManager.getAddHocCommandsManager(connection);
    }

    public RemoteCommand addUser() {
        return addUser(connection().getXMPPServiceDomain());
    }

    public RemoteCommand addUser(Jid service) {
        return this.adHocCommandManager.getRemoteCommand(service, "http://jabber.org/protocol/admin#add-user");
    }

    public void addUser(EntityBareJid userJid, String password) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        RemoteCommand command = addUser();
        command.execute();
        Form answerForm = command.getForm().createAnswerForm();
        answerForm.getField("accountjid").addValue((CharSequence) userJid.toString());
        answerForm.getField("password").addValue((CharSequence) password);
        answerForm.getField("password-verify").addValue((CharSequence) password);
        command.execute(answerForm);
    }

    public RemoteCommand deleteUser() {
        return deleteUser((Jid) connection().getXMPPServiceDomain());
    }

    public RemoteCommand deleteUser(Jid service) {
        return this.adHocCommandManager.getRemoteCommand(service, "http://jabber.org/protocol/admin#delete-user");
    }

    public void deleteUser(EntityBareJid userJidToDelete) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        deleteUser(Collections.singleton(userJidToDelete));
    }

    public void deleteUser(Set<EntityBareJid> jidsToDelete) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        RemoteCommand command = deleteUser();
        command.execute();
        Form answerForm = command.getForm().createAnswerForm();
        answerForm.getField("accountjids").addValues(JidUtil.toStringList(jidsToDelete));
        command.execute(answerForm);
    }
}
