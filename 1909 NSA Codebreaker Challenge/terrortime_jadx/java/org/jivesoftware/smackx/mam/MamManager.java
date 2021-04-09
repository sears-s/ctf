package org.jivesoftware.smackx.mam;

import com.badguy.terrortime.BuildConfig;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.StanzaCollector;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.IQReplyFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.forward.packet.Forwarded;
import org.jivesoftware.smackx.mam.element.MamElements.MamResultExtension;
import org.jivesoftware.smackx.mam.element.MamFinIQ;
import org.jivesoftware.smackx.mam.element.MamPrefsIQ;
import org.jivesoftware.smackx.mam.element.MamPrefsIQ.DefaultBehavior;
import org.jivesoftware.smackx.mam.element.MamQueryIQ;
import org.jivesoftware.smackx.mam.filter.MamResultFilter;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.rsm.packet.RSMSet;
import org.jivesoftware.smackx.rsm.packet.RSMSet.PageDirection;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;

public final class MamManager extends Manager {
    private static final String FORM_FIELD_END = "end";
    private static final String FORM_FIELD_START = "start";
    private static final String FORM_FIELD_WITH = "with";
    private static final Map<XMPPConnection, Map<Jid, MamManager>> INSTANCES = new WeakHashMap();
    /* access modifiers changed from: private */
    public final Jid archiveAddress;
    private final ServiceDiscoveryManager serviceDiscoveryManager;

    public static final class MamPrefs {
        private final List<Jid> alwaysJids;
        private DefaultBehavior defaultBehavior;
        private final List<Jid> neverJids;

        private MamPrefs(MamPrefsResult mamPrefsResult) {
            MamPrefsIQ mamPrefsIq = mamPrefsResult.mamPrefs;
            this.alwaysJids = new ArrayList(mamPrefsIq.getAlwaysJids());
            this.neverJids = new ArrayList(mamPrefsIq.getNeverJids());
            this.defaultBehavior = mamPrefsIq.getDefault();
        }

        public void setDefaultBehavior(DefaultBehavior defaultBehavior2) {
            this.defaultBehavior = (DefaultBehavior) Objects.requireNonNull(defaultBehavior2, "defaultBehavior must not be null");
        }

        public DefaultBehavior getDefaultBehavior() {
            return this.defaultBehavior;
        }

        public List<Jid> getAlwaysJids() {
            return this.alwaysJids;
        }

        public List<Jid> getNeverJids() {
            return this.neverJids;
        }

        /* access modifiers changed from: private */
        public MamPrefsIQ constructMamPrefsIq() {
            return new MamPrefsIQ(this.alwaysJids, this.neverJids, this.defaultBehavior);
        }
    }

    public static final class MamPrefsResult {
        public final DataForm form;
        public final MamPrefsIQ mamPrefs;

        private MamPrefsResult(MamPrefsIQ mamPrefs2, DataForm form2) {
            this.mamPrefs = mamPrefs2;
            this.form = form2;
        }

        public MamPrefs asMamPrefs() {
            return new MamPrefs(this);
        }
    }

    public final class MamQuery {
        /* access modifiers changed from: private */
        public final DataForm form;
        /* access modifiers changed from: private */
        public MamQueryPage mamQueryPage;
        /* access modifiers changed from: private */
        public final String node;

        private MamQuery(MamQueryPage mamQueryPage2, String node2, DataForm form2) {
            this.node = node2;
            this.form = form2;
            this.mamQueryPage = mamQueryPage2;
        }

        public boolean isComplete() {
            return this.mamQueryPage.getMamFinIq().isComplete();
        }

        public List<Message> getMessages() {
            return this.mamQueryPage.messages;
        }

        public List<MamResultExtension> getMamResultExtensions() {
            return this.mamQueryPage.mamResultExtensions;
        }

        private List<Message> page(RSMSet requestRsmSet) throws NoResponseException, XMPPErrorException, NotConnectedException, NotLoggedInException, InterruptedException {
            MamQueryIQ mamQueryIQ = new MamQueryIQ(UUID.randomUUID().toString(), this.node, this.form);
            mamQueryIQ.setType(Type.set);
            mamQueryIQ.setTo(MamManager.this.archiveAddress);
            mamQueryIQ.addExtension(requestRsmSet);
            this.mamQueryPage = MamManager.this.queryArchivePage(mamQueryIQ);
            return this.mamQueryPage.messages;
        }

        private RSMSet getPreviousRsmSet() {
            return this.mamQueryPage.getMamFinIq().getRSMSet();
        }

        public List<Message> pageNext(int count) throws NoResponseException, XMPPErrorException, NotConnectedException, NotLoggedInException, InterruptedException {
            return page(new RSMSet(count, getPreviousRsmSet().getLast(), PageDirection.after));
        }

        public List<Message> pagePrevious(int count) throws NoResponseException, XMPPErrorException, NotConnectedException, NotLoggedInException, InterruptedException {
            return page(new RSMSet(count, getPreviousRsmSet().getFirst(), PageDirection.before));
        }

        public int getMessageCount() {
            return getMessages().size();
        }

        public MamQueryPage getPage() {
            return this.mamQueryPage;
        }
    }

    public static final class MamQueryArgs {
        private final String afterUid;
        private final String beforeUid;
        private DataForm dataForm;
        private final Map<String, FormField> formFields;
        private final Integer maxResults;
        /* access modifiers changed from: private */
        public final String node;

        public static final class Builder {
            /* access modifiers changed from: private */
            public String afterUid;
            /* access modifiers changed from: private */
            public String beforeUid;
            /* access modifiers changed from: private */
            public final Map<String, FormField> formFields = new HashMap(8);
            /* access modifiers changed from: private */
            public int maxResults = -1;
            /* access modifiers changed from: private */
            public String node;

            public Builder queryNode(String node2) {
                if (node2 == null) {
                    return this;
                }
                this.node = node2;
                return this;
            }

            public Builder limitResultsToJid(Jid withJid) {
                if (withJid == null) {
                    return this;
                }
                FormField formField = MamManager.getWithFormField(withJid);
                this.formFields.put(formField.getVariable(), formField);
                return this;
            }

            public Builder limitResultsSince(Date start) {
                if (start == null) {
                    return this;
                }
                FormField formField = new FormField("start");
                formField.addValue(start);
                this.formFields.put(formField.getVariable(), formField);
                FormField endFormField = (FormField) this.formFields.get("end");
                if (endFormField != null) {
                    try {
                        Date end = endFormField.getFirstValueAsDate();
                        if (end.getTime() <= start.getTime()) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Given start date (");
                            sb.append(start);
                            sb.append(") is after the existing end date (");
                            sb.append(end);
                            sb.append(')');
                            throw new IllegalArgumentException(sb.toString());
                        }
                    } catch (ParseException e) {
                        throw new IllegalStateException(e);
                    }
                }
                return this;
            }

            public Builder limitResultsBefore(Date end) {
                if (end == null) {
                    return this;
                }
                FormField formField = new FormField("end");
                formField.addValue(end);
                this.formFields.put(formField.getVariable(), formField);
                FormField startFormField = (FormField) this.formFields.get("start");
                if (startFormField != null) {
                    try {
                        Date start = startFormField.getFirstValueAsDate();
                        if (end.getTime() <= start.getTime()) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Given end date (");
                            sb.append(end);
                            sb.append(") is before the existing start date (");
                            sb.append(start);
                            sb.append(')');
                            throw new IllegalArgumentException(sb.toString());
                        }
                    } catch (ParseException e) {
                        throw new IllegalStateException(e);
                    }
                }
                return this;
            }

            public Builder setResultPageSize(Integer max) {
                if (max != null) {
                    return setResultPageSizeTo(max.intValue());
                }
                this.maxResults = -1;
                return this;
            }

            public Builder setResultPageSizeTo(int max) {
                if (max >= 0) {
                    this.maxResults = max;
                    return this;
                }
                throw new IllegalArgumentException();
            }

            public Builder onlyReturnMessageCount() {
                return setResultPageSizeTo(0);
            }

            public Builder withAdditionalFormField(FormField formField) {
                this.formFields.put(formField.getVariable(), formField);
                return this;
            }

            public Builder withAdditionalFormFields(List<FormField> additionalFields) {
                for (FormField formField : additionalFields) {
                    withAdditionalFormField(formField);
                }
                return this;
            }

            public Builder afterUid(String afterUid2) {
                this.afterUid = (String) StringUtils.requireNullOrNotEmpty(afterUid2, "afterUid must not be empty");
                return this;
            }

            public Builder beforeUid(String beforeUid2) {
                this.beforeUid = beforeUid2;
                return this;
            }

            public Builder queryLastPage() {
                return beforeUid(BuildConfig.FLAVOR);
            }

            public MamQueryArgs build() {
                return new MamQueryArgs(this);
            }
        }

        private MamQueryArgs(Builder builder) {
            this.node = builder.node;
            this.formFields = builder.formFields;
            if (builder.maxResults > 0) {
                this.maxResults = Integer.valueOf(builder.maxResults);
            } else {
                this.maxResults = null;
            }
            this.afterUid = builder.afterUid;
            this.beforeUid = builder.beforeUid;
        }

        /* access modifiers changed from: 0000 */
        public DataForm getDataForm() {
            DataForm dataForm2 = this.dataForm;
            if (dataForm2 != null) {
                return dataForm2;
            }
            this.dataForm = MamManager.getNewMamForm();
            this.dataForm.addFields(this.formFields.values());
            return this.dataForm;
        }

        /* access modifiers changed from: 0000 */
        public void maybeAddRsmSet(MamQueryIQ mamQueryIQ) {
            int max;
            if (this.maxResults != null || this.afterUid != null || this.beforeUid != null) {
                Integer num = this.maxResults;
                if (num != null) {
                    max = num.intValue();
                } else {
                    max = -1;
                }
                RSMSet rsmSet = new RSMSet(this.afterUid, this.beforeUid, -1, -1, null, max, null, -1);
                mamQueryIQ.addExtension(rsmSet);
            }
        }

        public static Builder builder() {
            return new Builder();
        }
    }

    public static final class MamQueryPage {
        /* access modifiers changed from: private */
        public final List<Forwarded> forwardedMessages;
        /* access modifiers changed from: private */
        public final MamFinIQ mamFin;
        private final List<Message> mamResultCarrierMessages;
        /* access modifiers changed from: private */
        public final List<MamResultExtension> mamResultExtensions;
        /* access modifiers changed from: private */
        public final List<Message> messages;

        private MamQueryPage(StanzaCollector stanzaCollector, MamFinIQ mamFin2) {
            this.mamFin = mamFin2;
            List<Stanza> mamResultCarrierStanzas = stanzaCollector.getCollectedStanzasAfterCancelled();
            List<Message> mamResultCarrierMessages2 = new ArrayList<>(mamResultCarrierStanzas.size());
            List<MamResultExtension> mamResultExtensions2 = new ArrayList<>(mamResultCarrierStanzas.size());
            List<Forwarded> forwardedMessages2 = new ArrayList<>(mamResultCarrierStanzas.size());
            for (Stanza mamResultStanza : mamResultCarrierStanzas) {
                Message resultMessage = (Message) mamResultStanza;
                mamResultCarrierMessages2.add(resultMessage);
                MamResultExtension mamResultExtension = MamResultExtension.from(resultMessage);
                mamResultExtensions2.add(mamResultExtension);
                forwardedMessages2.add(mamResultExtension.getForwarded());
            }
            this.mamResultCarrierMessages = Collections.unmodifiableList(mamResultCarrierMessages2);
            this.mamResultExtensions = Collections.unmodifiableList(mamResultExtensions2);
            this.forwardedMessages = Collections.unmodifiableList(forwardedMessages2);
            this.messages = Collections.unmodifiableList(Forwarded.extractMessagesFrom(forwardedMessages2));
        }

        public List<Message> getMessages() {
            return this.messages;
        }

        public List<Forwarded> getForwarded() {
            return this.forwardedMessages;
        }

        public List<MamResultExtension> getMamResultExtensions() {
            return this.mamResultExtensions;
        }

        public List<Message> getMamResultCarrierMessages() {
            return this.mamResultCarrierMessages;
        }

        public MamFinIQ getMamFinIq() {
            return this.mamFin;
        }
    }

    @Deprecated
    public static final class MamQueryResult {
        /* access modifiers changed from: private */
        public final DataForm form;
        public final List<Forwarded> forwardedMessages;
        public final MamFinIQ mamFin;
        /* access modifiers changed from: private */
        public final String node;

        private MamQueryResult(MamQuery mamQuery) {
            this(mamQuery.mamQueryPage.forwardedMessages, mamQuery.mamQueryPage.mamFin, mamQuery.node, mamQuery.form);
        }

        private MamQueryResult(List<Forwarded> forwardedMessages2, MamFinIQ mamFin2, String node2, DataForm form2) {
            this.forwardedMessages = forwardedMessages2;
            this.mamFin = mamFin2;
            this.node = node2;
            this.form = form2;
        }
    }

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                MamManager.getInstanceFor(connection);
            }
        });
    }

    public static MamManager getInstanceFor(XMPPConnection connection) {
        return getInstanceFor(connection, null);
    }

    public static MamManager getInstanceFor(MultiUserChat multiUserChat) {
        return getInstanceFor(multiUserChat.getXmppConnection(), multiUserChat.getRoom());
    }

    public static synchronized MamManager getInstanceFor(XMPPConnection connection, Jid archiveAddress2) {
        MamManager mamManager;
        synchronized (MamManager.class) {
            Map map = (Map) INSTANCES.get(connection);
            if (map == null) {
                map = new HashMap();
                INSTANCES.put(connection, map);
            }
            mamManager = (MamManager) map.get(archiveAddress2);
            if (mamManager == null) {
                mamManager = new MamManager(connection, archiveAddress2);
                map.put(archiveAddress2, mamManager);
            }
        }
        return mamManager;
    }

    private MamManager(XMPPConnection connection, Jid archiveAddress2) {
        super(connection);
        this.archiveAddress = archiveAddress2;
        this.serviceDiscoveryManager = ServiceDiscoveryManager.getInstanceFor(connection);
    }

    public Jid getArchiveAddress() {
        Jid jid = this.archiveAddress;
        if (jid != null) {
            return jid;
        }
        EntityFullJid localJid = connection().getUser();
        if (localJid == null) {
            return null;
        }
        return localJid.asBareJid();
    }

    @Deprecated
    public MamQueryResult queryArchive(Integer max) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        return queryArchive(null, max, null, null, null, null);
    }

    @Deprecated
    public MamQueryResult queryArchive(Jid withJid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        return queryArchive(null, null, null, null, withJid, null);
    }

    @Deprecated
    public MamQueryResult queryArchive(Date start, Date end) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        return queryArchive(null, null, start, end, null, null);
    }

    @Deprecated
    public MamQueryResult queryArchive(List<FormField> additionalFields) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        return queryArchive(null, null, null, null, null, additionalFields);
    }

    @Deprecated
    public MamQueryResult queryArchiveWithStartDate(Date start) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        return queryArchive(null, null, start, null, null, null);
    }

    @Deprecated
    public MamQueryResult queryArchiveWithEndDate(Date end) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        return queryArchive(null, null, null, end, null, null);
    }

    @Deprecated
    public MamQueryResult queryArchive(Integer max, Date start, Date end, Jid withJid, List<FormField> additionalFields) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        return queryArchive(null, max, start, end, withJid, additionalFields);
    }

    @Deprecated
    public MamQueryResult queryArchive(String node, Integer max, Date start, Date end, Jid withJid, List<FormField> additionalFields) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        return new MamQueryResult(queryArchive(MamQueryArgs.builder().queryNode(node).setResultPageSize(max).limitResultsSince(start).limitResultsBefore(end).limitResultsToJid(withJid).withAdditionalFormFields(additionalFields).build()));
    }

    public MamQuery queryArchive(MamQueryArgs mamQueryArgs) throws NoResponseException, XMPPErrorException, NotConnectedException, NotLoggedInException, InterruptedException {
        MamQueryIQ mamQueryIQ = new MamQueryIQ(UUID.randomUUID().toString(), mamQueryArgs.node, mamQueryArgs.getDataForm());
        mamQueryIQ.setType(Type.set);
        mamQueryIQ.setTo(this.archiveAddress);
        mamQueryArgs.maybeAddRsmSet(mamQueryIQ);
        return queryArchive(mamQueryIQ);
    }

    /* access modifiers changed from: private */
    public static FormField getWithFormField(Jid withJid) {
        FormField formField = new FormField(FORM_FIELD_WITH);
        formField.addValue((CharSequence) withJid.toString());
        return formField;
    }

    private static void addWithJid(Jid withJid, DataForm dataForm) {
        if (withJid != null) {
            dataForm.addField(getWithFormField(withJid));
        }
    }

    @Deprecated
    public MamQueryResult page(DataForm dataForm, RSMSet rsmSet) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        return page(null, dataForm, rsmSet);
    }

    @Deprecated
    public MamQueryResult page(String node, DataForm dataForm, RSMSet rsmSet) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        MamQueryIQ mamQueryIQ = new MamQueryIQ(UUID.randomUUID().toString(), node, dataForm);
        mamQueryIQ.setType(Type.set);
        mamQueryIQ.setTo(this.archiveAddress);
        mamQueryIQ.addExtension(rsmSet);
        return new MamQueryResult(queryArchive(mamQueryIQ));
    }

    @Deprecated
    public MamQueryResult pageNext(MamQueryResult mamQueryResult, int count) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        return page(mamQueryResult, new RSMSet(count, mamQueryResult.mamFin.getRSMSet().getLast(), PageDirection.after));
    }

    @Deprecated
    public MamQueryResult pagePrevious(MamQueryResult mamQueryResult, int count) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        return page(mamQueryResult, new RSMSet(count, mamQueryResult.mamFin.getRSMSet().getFirst(), PageDirection.before));
    }

    private MamQueryResult page(MamQueryResult mamQueryResult, RSMSet requestRsmSet) throws NoResponseException, XMPPErrorException, NotConnectedException, NotLoggedInException, InterruptedException {
        ensureMamQueryResultMatchesThisManager(mamQueryResult);
        return page(mamQueryResult.node, mamQueryResult.form, requestRsmSet);
    }

    @Deprecated
    public MamQueryResult pageBefore(Jid chatJid, String messageUid, int max) throws XMPPErrorException, NotLoggedInException, NotConnectedException, InterruptedException, NoResponseException {
        RSMSet rsmSet = new RSMSet(null, messageUid, -1, -1, null, max, null, -1);
        DataForm dataForm = getNewMamForm();
        addWithJid(chatJid, dataForm);
        return page(null, dataForm, rsmSet);
    }

    @Deprecated
    public MamQueryResult pageAfter(Jid chatJid, String messageUid, int max) throws XMPPErrorException, NotLoggedInException, NotConnectedException, InterruptedException, NoResponseException {
        RSMSet rsmSet = new RSMSet(messageUid, null, -1, -1, null, max, null, -1);
        DataForm dataForm = getNewMamForm();
        addWithJid(chatJid, dataForm);
        return page(null, dataForm, rsmSet);
    }

    @Deprecated
    public MamQueryResult mostRecentPage(Jid chatJid, int max) throws XMPPErrorException, NotLoggedInException, NotConnectedException, InterruptedException, NoResponseException {
        return pageBefore(chatJid, BuildConfig.FLAVOR, max);
    }

    public MamQuery queryMostRecentPage(Jid jid, int max) throws NoResponseException, XMPPErrorException, NotConnectedException, NotLoggedInException, InterruptedException {
        return queryArchive(MamQueryArgs.builder().queryLastPage().limitResultsToJid(jid).setResultPageSize(Integer.valueOf(max)).build());
    }

    public List<FormField> retrieveFormFields() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        return retrieveFormFields(null);
    }

    public List<FormField> retrieveFormFields(String node) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        MamQueryIQ mamQueryIq = new MamQueryIQ(UUID.randomUUID().toString(), node, null);
        mamQueryIq.setTo(this.archiveAddress);
        return ((MamQueryIQ) connection().createStanzaCollectorAndSend(mamQueryIq).nextResultOrThrow()).getDataForm().getFields();
    }

    private MamQuery queryArchive(MamQueryIQ mamQueryIq) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        MamQuery mamQuery = new MamQuery(queryArchivePage(mamQueryIq), mamQueryIq.getNode(), DataForm.from(mamQueryIq));
        return mamQuery;
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: private */
    public MamQueryPage queryArchivePage(MamQueryIQ mamQueryIq) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        XMPPConnection connection = getAuthenticatedConnectionOrThrow();
        StanzaCollector mamFinIQCollector = connection.createStanzaCollector((StanzaFilter) new IQReplyFilter(mamQueryIq, connection));
        StanzaCollector resultCollector = connection.createStanzaCollector(StanzaCollector.newConfiguration().setStanzaFilter(new MamResultFilter(mamQueryIq)).setCollectorToReset(mamFinIQCollector));
        try {
            connection.sendStanza(mamQueryIq);
            MamFinIQ mamFinIQ = (MamFinIQ) mamFinIQCollector.nextResultOrThrow();
            mamFinIQCollector.cancel();
            resultCollector.cancel();
            return new MamQueryPage(resultCollector, mamFinIQ);
        } catch (Throwable th) {
            mamFinIQCollector.cancel();
            resultCollector.cancel();
            throw th;
        }
    }

    private void ensureMamQueryResultMatchesThisManager(MamQueryResult mamQueryResult) {
        EntityFullJid localAddress = connection().getUser();
        EntityBareJid localBareAddress = null;
        if (localAddress != null) {
            localBareAddress = localAddress.asEntityBareJid();
        }
        Jid jid = this.archiveAddress;
        boolean isLocalUserArchive = jid == null || jid.equals((CharSequence) localBareAddress);
        Jid finIqFrom = mamQueryResult.mamFin.getFrom();
        if (finIqFrom != null) {
            if (!finIqFrom.equals((CharSequence) this.archiveAddress) && (!isLocalUserArchive || !finIqFrom.equals((CharSequence) localBareAddress))) {
                StringBuilder sb = new StringBuilder();
                sb.append("The given MamQueryResult is from the MAM archive '");
                sb.append(finIqFrom);
                sb.append("' whereas this MamManager is responsible for '");
                sb.append(this.archiveAddress);
                sb.append('\'');
                throw new IllegalArgumentException(sb.toString());
            }
        } else if (!isLocalUserArchive) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("The given MamQueryResult is from the local entity (user) MAM archive, whereas this MamManager is responsible for '");
            sb2.append(this.archiveAddress);
            sb2.append('\'');
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    public boolean isSupported() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return this.serviceDiscoveryManager.supportsFeature(getArchiveAddress(), "urn:xmpp:mam:1");
    }

    /* access modifiers changed from: private */
    public static DataForm getNewMamForm() {
        FormField field = new FormField(FormField.FORM_TYPE);
        field.setType(FormField.Type.hidden);
        field.addValue((CharSequence) "urn:xmpp:mam:1");
        DataForm form = new DataForm(DataForm.Type.submit);
        form.addField(field);
        return form;
    }

    public String getMessageUidOfLatestMessage() throws NoResponseException, XMPPErrorException, NotConnectedException, NotLoggedInException, InterruptedException {
        MamQuery mamQuery = queryArchive(MamQueryArgs.builder().setResultPageSize(Integer.valueOf(1)).queryLastPage().build());
        if (mamQuery.getMessages().isEmpty()) {
            return null;
        }
        return ((MamResultExtension) mamQuery.getMamResultExtensions().get(0)).getId();
    }

    public MamPrefsResult retrieveArchivingPreferences() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        return queryMamPrefs(new MamPrefsIQ());
    }

    @Deprecated
    public MamPrefsResult updateArchivingPreferences(List<Jid> alwaysJids, List<Jid> neverJids, DefaultBehavior defaultBehavior) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        Objects.requireNonNull(defaultBehavior, "Default behavior must be set");
        return queryMamPrefs(new MamPrefsIQ(alwaysJids, neverJids, defaultBehavior));
    }

    public MamPrefsResult updateArchivingPreferences(MamPrefs mamPrefs) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        return queryMamPrefs(mamPrefs.constructMamPrefsIq());
    }

    public MamPrefsResult enableMamForAllMessages() throws NoResponseException, XMPPErrorException, NotConnectedException, NotLoggedInException, InterruptedException {
        return setDefaultBehavior(DefaultBehavior.always);
    }

    public MamPrefsResult enableMamForRosterMessages() throws NoResponseException, XMPPErrorException, NotConnectedException, NotLoggedInException, InterruptedException {
        return setDefaultBehavior(DefaultBehavior.roster);
    }

    public MamPrefsResult setDefaultBehavior(DefaultBehavior desiredDefaultBehavior) throws NoResponseException, XMPPErrorException, NotConnectedException, NotLoggedInException, InterruptedException {
        MamPrefsResult mamPrefsResult = retrieveArchivingPreferences();
        if (mamPrefsResult.mamPrefs.getDefault() == desiredDefaultBehavior) {
            return mamPrefsResult;
        }
        MamPrefs mamPrefs = mamPrefsResult.asMamPrefs();
        mamPrefs.setDefaultBehavior(desiredDefaultBehavior);
        return updateArchivingPreferences(mamPrefs);
    }

    private MamPrefsResult queryMamPrefs(MamPrefsIQ mamPrefsIQ) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        return new MamPrefsResult((MamPrefsIQ) getAuthenticatedConnectionOrThrow().createStanzaCollectorAndSend(mamPrefsIQ).nextResultOrThrow(), DataForm.from(mamPrefsIQ));
    }
}
