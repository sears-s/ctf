package org.jivesoftware.smackx.filetransfer;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransferException.NoAcceptableTransferMechanisms;
import org.jivesoftware.smackx.filetransfer.FileTransferException.NoStreamMethodsOfferedException;
import org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp.Ibb;
import org.jivesoftware.smackx.si.packet.StreamInitiation;
import org.jivesoftware.smackx.si.packet.StreamInitiation.File;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.FormField.Option;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.Jid;

public final class FileTransferNegotiator extends Manager {
    public static boolean IBB_ONLY = (System.getProperty(Ibb.ELEMENT) != null);
    private static final Map<XMPPConnection, FileTransferNegotiator> INSTANCES = new WeakHashMap();
    private static final String[] NAMESPACE = {"http://jabber.org/protocol/si", SI_PROFILE_FILE_TRANSFER_NAMESPACE};
    public static final String SI_NAMESPACE = "http://jabber.org/protocol/si";
    public static final String SI_PROFILE_FILE_TRANSFER_NAMESPACE = "http://jabber.org/protocol/si/profile/file-transfer";
    protected static final String STREAM_DATA_FIELD_NAME = "stream-method";
    private static final String STREAM_INIT_PREFIX = "jsi_";
    private static final Random randomGenerator = new Random();
    private final StreamNegotiator byteStreamTransferManager;
    private final StreamNegotiator inbandTransferManager;

    public static synchronized FileTransferNegotiator getInstanceFor(XMPPConnection connection) {
        FileTransferNegotiator fileTransferNegotiator;
        synchronized (FileTransferNegotiator.class) {
            fileTransferNegotiator = (FileTransferNegotiator) INSTANCES.get(connection);
            if (fileTransferNegotiator == null) {
                fileTransferNegotiator = new FileTransferNegotiator(connection);
                INSTANCES.put(connection, fileTransferNegotiator);
            }
        }
        return fileTransferNegotiator;
    }

    private static void setServiceEnabled(XMPPConnection connection, boolean isEnabled) {
        ServiceDiscoveryManager manager = ServiceDiscoveryManager.getInstanceFor(connection);
        List<String> namespaces = new ArrayList<>();
        namespaces.addAll(Arrays.asList(NAMESPACE));
        namespaces.add("http://jabber.org/protocol/ibb");
        if (!IBB_ONLY) {
            namespaces.add(Bytestream.NAMESPACE);
        }
        for (String namespace : namespaces) {
            if (isEnabled) {
                manager.addFeature(namespace);
            } else {
                manager.removeFeature(namespace);
            }
        }
    }

    public static boolean isServiceEnabled(XMPPConnection connection) {
        ServiceDiscoveryManager manager = ServiceDiscoveryManager.getInstanceFor(connection);
        List<String> namespaces = new ArrayList<>();
        namespaces.addAll(Arrays.asList(NAMESPACE));
        namespaces.add("http://jabber.org/protocol/ibb");
        if (!IBB_ONLY) {
            namespaces.add(Bytestream.NAMESPACE);
        }
        for (String namespace : namespaces) {
            if (!manager.includesFeature(namespace)) {
                return false;
            }
        }
        return true;
    }

    public static Collection<String> getSupportedProtocols() {
        List<String> protocols = new ArrayList<>();
        protocols.add("http://jabber.org/protocol/ibb");
        if (!IBB_ONLY) {
            protocols.add(Bytestream.NAMESPACE);
        }
        return Collections.unmodifiableList(protocols);
    }

    private FileTransferNegotiator(XMPPConnection connection) {
        super(connection);
        this.byteStreamTransferManager = new Socks5TransferNegotiator(connection);
        this.inbandTransferManager = new IBBTransferNegotiator(connection);
        setServiceEnabled(connection, true);
    }

    public StreamNegotiator selectStreamNegotiator(FileTransferRequest request) throws NotConnectedException, NoStreamMethodsOfferedException, NoAcceptableTransferMechanisms, InterruptedException {
        StreamInitiation si = request.getStreamInitiation();
        FormField streamMethodField = getStreamMethodField(si.getFeatureNegotiationForm());
        if (streamMethodField != null) {
            try {
                return getNegotiator(streamMethodField);
            } catch (NoAcceptableTransferMechanisms e) {
                connection().sendStanza(IQ.createErrorResponse((IQ) si, StanzaError.from(Condition.bad_request, "No acceptable transfer mechanism")));
                throw e;
            }
        } else {
            connection().sendStanza(IQ.createErrorResponse((IQ) si, StanzaError.from(Condition.bad_request, "No stream methods contained in stanza.")));
            throw new NoStreamMethodsOfferedException();
        }
    }

    private static FormField getStreamMethodField(DataForm form) {
        return form.getField(STREAM_DATA_FIELD_NAME);
    }

    private StreamNegotiator getNegotiator(FormField field) throws NoAcceptableTransferMechanisms {
        boolean isByteStream = false;
        boolean isIBB = false;
        for (Option option : field.getOptions()) {
            String variable = option.getValue();
            if (variable.equals(Bytestream.NAMESPACE) && !IBB_ONLY) {
                isByteStream = true;
            } else if (variable.equals("http://jabber.org/protocol/ibb")) {
                isIBB = true;
            }
        }
        if (!isByteStream && !isIBB) {
            throw new NoAcceptableTransferMechanisms();
        } else if (isByteStream && isIBB) {
            return new FaultTolerantNegotiator(connection(), this.byteStreamTransferManager, this.inbandTransferManager);
        } else {
            if (isByteStream) {
                return this.byteStreamTransferManager;
            }
            return this.inbandTransferManager;
        }
    }

    public static String getNextStreamID() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(STREAM_INIT_PREFIX);
        buffer.append(Math.abs(randomGenerator.nextLong()));
        return buffer.toString();
    }

    public StreamNegotiator negotiateOutgoingTransfer(Jid userID, String streamID, String fileName, long size, String desc, int responseTimeout) throws XMPPErrorException, NotConnectedException, NoResponseException, NoAcceptableTransferMechanisms, InterruptedException {
        StreamInitiation si = new StreamInitiation();
        si.setSessionID(streamID);
        si.setMimeType(URLConnection.guessContentTypeFromName(fileName));
        File siFile = new File(fileName, size);
        siFile.setDesc(desc);
        si.setFile(siFile);
        si.setFeatureNegotiationForm(createDefaultInitiationForm());
        si.setFrom((Jid) connection().getUser());
        si.setTo(userID);
        si.setType(Type.set);
        Stanza siResponse = connection().createStanzaCollectorAndSend(si).nextResultOrThrow((long) responseTimeout);
        if (!(siResponse instanceof IQ)) {
            return null;
        }
        IQ iqResponse = (IQ) siResponse;
        if (iqResponse.getType().equals(Type.result)) {
            return getOutgoingNegotiator(getStreamMethodField(((StreamInitiation) siResponse).getFeatureNegotiationForm()));
        }
        throw new XMPPErrorException(iqResponse, iqResponse.getError());
    }

    private StreamNegotiator getOutgoingNegotiator(FormField field) throws NoAcceptableTransferMechanisms {
        boolean isByteStream = false;
        boolean isIBB = false;
        for (CharSequence variable : field.getValues()) {
            if (variable.equals(Bytestream.NAMESPACE) && !IBB_ONLY) {
                isByteStream = true;
            } else if (variable.equals("http://jabber.org/protocol/ibb")) {
                isIBB = true;
            }
        }
        if (!isByteStream && !isIBB) {
            throw new NoAcceptableTransferMechanisms();
        } else if (isByteStream && isIBB) {
            return new FaultTolerantNegotiator(connection(), this.byteStreamTransferManager, this.inbandTransferManager);
        } else {
            if (isByteStream) {
                return this.byteStreamTransferManager;
            }
            return this.inbandTransferManager;
        }
    }

    private static DataForm createDefaultInitiationForm() {
        DataForm form = new DataForm(DataForm.Type.form);
        FormField field = new FormField(STREAM_DATA_FIELD_NAME);
        field.setType(FormField.Type.list_single);
        if (!IBB_ONLY) {
            field.addOption(new Option(Bytestream.NAMESPACE));
        }
        field.addOption(new Option("http://jabber.org/protocol/ibb"));
        form.addField(field);
        return form;
    }
}
