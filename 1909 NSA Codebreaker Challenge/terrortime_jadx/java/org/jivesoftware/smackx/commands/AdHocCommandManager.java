package org.jivesoftware.smackx.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.packet.StanzaError.Builder;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.commands.AdHocCommand.Action;
import org.jivesoftware.smackx.commands.AdHocCommand.SpecificErrorCondition;
import org.jivesoftware.smackx.commands.AdHocCommand.Status;
import org.jivesoftware.smackx.commands.packet.AdHocCommandData;
import org.jivesoftware.smackx.commands.packet.AdHocCommandData.SpecificError;
import org.jivesoftware.smackx.disco.AbstractNodeInformationProvider;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.disco.packet.DiscoverItems.Item;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.Jid;

public final class AdHocCommandManager extends Manager {
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(AdHocCommandManager.class.getName());
    public static final String NAMESPACE = "http://jabber.org/protocol/commands";
    private static final int SESSION_TIMEOUT = 120;
    private static final Map<XMPPConnection, AdHocCommandManager> instances = new WeakHashMap();
    private final Map<String, AdHocCommandInfo> commands = new ConcurrentHashMap();
    /* access modifiers changed from: private */
    public final Map<String, LocalCommand> executingCommands = new ConcurrentHashMap();
    private final ServiceDiscoveryManager serviceDiscoveryManager;
    private Thread sessionsSweeper;

    private static final class AdHocCommandInfo {
        private LocalCommandFactory factory;
        private String name;
        private String node;
        private final Jid ownerJID;

        private AdHocCommandInfo(String node2, String name2, Jid ownerJID2, LocalCommandFactory factory2) {
            this.node = node2;
            this.name = name2;
            this.ownerJID = ownerJID2;
            this.factory = factory2;
        }

        public LocalCommand getCommandInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
            return this.factory.getInstance();
        }

        public String getName() {
            return this.name;
        }

        public String getNode() {
            return this.node;
        }

        public Jid getOwnerJID() {
            return this.ownerJID;
        }
    }

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                AdHocCommandManager.getAddHocCommandsManager(connection);
            }
        });
    }

    public static synchronized AdHocCommandManager getAddHocCommandsManager(XMPPConnection connection) {
        AdHocCommandManager ahcm;
        synchronized (AdHocCommandManager.class) {
            ahcm = (AdHocCommandManager) instances.get(connection);
            if (ahcm == null) {
                ahcm = new AdHocCommandManager(connection);
                instances.put(connection, ahcm);
            }
        }
        return ahcm;
    }

    private AdHocCommandManager(XMPPConnection connection) {
        super(connection);
        this.serviceDiscoveryManager = ServiceDiscoveryManager.getInstanceFor(connection);
        String str = "http://jabber.org/protocol/commands";
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature(str);
        ServiceDiscoveryManager.getInstanceFor(connection).setNodeInformationProvider(str, new AbstractNodeInformationProvider() {
            public List<Item> getNodeItems() {
                List<Item> answer = new ArrayList<>();
                for (AdHocCommandInfo info : AdHocCommandManager.this.getRegisteredCommands()) {
                    Item item = new Item(info.getOwnerJID());
                    item.setName(info.getName());
                    item.setNode(info.getNode());
                    answer.add(item);
                }
                return answer;
            }
        });
        AnonymousClass3 r3 = new AbstractIqRequestHandler(AdHocCommandData.ELEMENT, "http://jabber.org/protocol/commands", Type.set, Mode.async) {
            public IQ handleIQRequest(IQ iqRequest) {
                try {
                    return AdHocCommandManager.this.processAdHocCommand((AdHocCommandData) iqRequest);
                } catch (InterruptedException | NoResponseException | NotConnectedException e) {
                    AdHocCommandManager.LOGGER.log(Level.INFO, "processAdHocCommand threw exception", e);
                    return null;
                }
            }
        };
        connection.registerIQRequestHandler(r3);
        this.sessionsSweeper = null;
    }

    public void registerCommand(String node, String name, final Class<? extends LocalCommand> clazz) {
        registerCommand(node, name, (LocalCommandFactory) new LocalCommandFactory() {
            public LocalCommand getInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
                return (LocalCommand) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
        });
    }

    public void registerCommand(String node, final String name, LocalCommandFactory factory) {
        AdHocCommandInfo commandInfo = new AdHocCommandInfo(node, name, connection().getUser(), factory);
        this.commands.put(node, commandInfo);
        this.serviceDiscoveryManager.setNodeInformationProvider(node, new AbstractNodeInformationProvider() {
            public List<String> getNodeFeatures() {
                List<String> answer = new ArrayList<>();
                answer.add("http://jabber.org/protocol/commands");
                answer.add("jabber:x:data");
                return answer;
            }

            public List<Identity> getNodeIdentities() {
                List<Identity> answer = new ArrayList<>();
                answer.add(new Identity("automation", name, "command-node"));
                return answer;
            }
        });
    }

    public DiscoverItems discoverCommands(Jid jid) throws XMPPException, SmackException, InterruptedException {
        return this.serviceDiscoveryManager.discoverItems(jid, "http://jabber.org/protocol/commands");
    }

    @Deprecated
    public void publishCommands(Jid jid) throws XMPPException, SmackException, InterruptedException {
        DiscoverItems discoverItems = new DiscoverItems();
        for (AdHocCommandInfo info : getRegisteredCommands()) {
            Item item = new Item(info.getOwnerJID());
            item.setName(info.getName());
            item.setNode(info.getNode());
            discoverItems.addItem(item);
        }
        this.serviceDiscoveryManager.publishItems(jid, "http://jabber.org/protocol/commands", discoverItems);
    }

    public RemoteCommand getRemoteCommand(Jid jid, String node) {
        return new RemoteCommand(connection(), node, jid);
    }

    /* access modifiers changed from: private */
    public IQ processAdHocCommand(AdHocCommandData requestData) throws NoResponseException, NotConnectedException, InterruptedException {
        AdHocCommandData response = new AdHocCommandData();
        response.setTo(requestData.getFrom());
        response.setStanzaId(requestData.getStanzaId());
        response.setNode(requestData.getNode());
        response.setId(requestData.getTo());
        String sessionId = requestData.getSessionID();
        String commandNode = requestData.getNode();
        if (sessionId != null) {
            LocalCommand command = (LocalCommand) this.executingCommands.get(sessionId);
            if (command == null) {
                return respondError(response, Condition.bad_request, SpecificErrorCondition.badSessionid);
            }
            if (System.currentTimeMillis() - command.getCreationDate() > 120000) {
                this.executingCommands.remove(sessionId);
                return respondError(response, Condition.not_allowed, SpecificErrorCondition.sessionExpired);
            }
            synchronized (command) {
                Action action = requestData.getAction();
                if (action == null || !action.equals(Action.unknown)) {
                    if (action == null || Action.execute.equals(action)) {
                        action = command.getExecuteAction();
                    }
                    if (!command.isValidAction(action)) {
                        IQ respondError = respondError(response, Condition.bad_request, SpecificErrorCondition.badAction);
                        return respondError;
                    }
                    try {
                        response.setType(Type.result);
                        command.setData(response);
                        if (Action.next.equals(action)) {
                            command.incrementStage();
                            command.next(new Form(requestData.getForm()));
                            if (command.isLastStage()) {
                                response.setStatus(Status.completed);
                            } else {
                                response.setStatus(Status.executing);
                            }
                        } else if (Action.complete.equals(action)) {
                            command.incrementStage();
                            command.complete(new Form(requestData.getForm()));
                            response.setStatus(Status.completed);
                            this.executingCommands.remove(sessionId);
                        } else if (Action.prev.equals(action)) {
                            command.decrementStage();
                            command.prev();
                        } else if (Action.cancel.equals(action)) {
                            command.cancel();
                            response.setStatus(Status.canceled);
                            this.executingCommands.remove(sessionId);
                        }
                        return response;
                    } catch (XMPPErrorException e) {
                        StanzaError error = e.getStanzaError();
                        if (StanzaError.Type.CANCEL.equals(error.getType())) {
                            response.setStatus(Status.canceled);
                            this.executingCommands.remove(sessionId);
                        }
                        return respondError(response, StanzaError.getBuilder(error));
                    }
                } else {
                    IQ respondError2 = respondError(response, Condition.bad_request, SpecificErrorCondition.malformedAction);
                    return respondError2;
                }
            }
        } else if (!this.commands.containsKey(commandNode)) {
            return respondError(response, Condition.item_not_found);
        } else {
            String sessionId2 = StringUtils.randomString(15);
            try {
                LocalCommand command2 = newInstanceOfCmd(commandNode, sessionId2);
                try {
                    response.setType(Type.result);
                    command2.setData(response);
                    if (!command2.hasPermission(requestData.getFrom())) {
                        return respondError(response, Condition.forbidden);
                    }
                    Action action2 = requestData.getAction();
                    if (action2 != null && action2.equals(Action.unknown)) {
                        return respondError(response, Condition.bad_request, SpecificErrorCondition.malformedAction);
                    }
                    if (action2 != null && !action2.equals(Action.execute)) {
                        return respondError(response, Condition.bad_request, SpecificErrorCondition.badAction);
                    }
                    command2.incrementStage();
                    command2.execute();
                    if (command2.isLastStage()) {
                        response.setStatus(Status.completed);
                    } else {
                        response.setStatus(Status.executing);
                        this.executingCommands.put(sessionId2, command2);
                        if (this.sessionsSweeper == null) {
                            this.sessionsSweeper = new Thread(new Runnable() {
                                public void run() {
                                    while (true) {
                                        for (String sessionId : AdHocCommandManager.this.executingCommands.keySet()) {
                                            LocalCommand command = (LocalCommand) AdHocCommandManager.this.executingCommands.get(sessionId);
                                            if (command != null) {
                                                if (System.currentTimeMillis() - command.getCreationDate() > 240000) {
                                                    AdHocCommandManager.this.executingCommands.remove(sessionId);
                                                }
                                            }
                                        }
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                        }
                                    }
                                }
                            });
                            this.sessionsSweeper.setDaemon(true);
                            this.sessionsSweeper.start();
                        }
                    }
                    return response;
                } catch (XMPPErrorException e2) {
                    StanzaError error2 = e2.getStanzaError();
                    if (StanzaError.Type.CANCEL.equals(error2.getType())) {
                        response.setStatus(Status.canceled);
                        this.executingCommands.remove(sessionId2);
                    }
                    return respondError(response, StanzaError.getBuilder(error2));
                }
            } catch (InstantiationException e3) {
                e = e3;
                return respondError(response, (Builder) StanzaError.getBuilder().setCondition(Condition.internal_server_error).setDescriptiveEnText(e.getMessage()));
            } catch (IllegalAccessException e4) {
                e = e4;
                return respondError(response, (Builder) StanzaError.getBuilder().setCondition(Condition.internal_server_error).setDescriptiveEnText(e.getMessage()));
            } catch (IllegalArgumentException e5) {
                e = e5;
                return respondError(response, (Builder) StanzaError.getBuilder().setCondition(Condition.internal_server_error).setDescriptiveEnText(e.getMessage()));
            } catch (InvocationTargetException e6) {
                e = e6;
                return respondError(response, (Builder) StanzaError.getBuilder().setCondition(Condition.internal_server_error).setDescriptiveEnText(e.getMessage()));
            } catch (NoSuchMethodException e7) {
                e = e7;
                return respondError(response, (Builder) StanzaError.getBuilder().setCondition(Condition.internal_server_error).setDescriptiveEnText(e.getMessage()));
            } catch (SecurityException e8) {
                e = e8;
                return respondError(response, (Builder) StanzaError.getBuilder().setCondition(Condition.internal_server_error).setDescriptiveEnText(e.getMessage()));
            }
        }
    }

    private static IQ respondError(AdHocCommandData response, Condition condition) {
        return respondError(response, StanzaError.getBuilder(condition));
    }

    private static IQ respondError(AdHocCommandData response, Condition condition, SpecificErrorCondition specificCondition) {
        return respondError(response, (Builder) StanzaError.getBuilder(condition).addExtension(new SpecificError(specificCondition)));
    }

    private static IQ respondError(AdHocCommandData response, Builder error) {
        response.setType(Type.error);
        response.setError(error);
        return response;
    }

    private LocalCommand newInstanceOfCmd(String commandNode, String sessionID) throws XMPPErrorException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        AdHocCommandInfo commandInfo = (AdHocCommandInfo) this.commands.get(commandNode);
        LocalCommand command = commandInfo.getCommandInstance();
        command.setSessionID(sessionID);
        command.setName(commandInfo.getName());
        command.setNode(commandInfo.getNode());
        return command;
    }

    /* access modifiers changed from: private */
    public Collection<AdHocCommandInfo> getRegisteredCommands() {
        return this.commands.values();
    }
}
