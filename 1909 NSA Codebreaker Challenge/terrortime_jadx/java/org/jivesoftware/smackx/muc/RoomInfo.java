package org.jivesoftware.smackx.muc;

import com.badguy.terrortime.BuildConfig;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.util.JidUtil;

public class RoomInfo {
    private static final Logger LOGGER = Logger.getLogger(RoomInfo.class.getName());
    private final List<EntityBareJid> contactJid;
    private final String description;
    private final Form form;
    private final String lang;
    private final String ldapgroup;
    private final URL logs;
    private final int maxhistoryfetch;
    private final boolean membersOnly;
    private final boolean moderated;
    private final String name;
    private final boolean nonanonymous;
    private final int occupantsCount;
    private final boolean passwordProtected;
    private final boolean persistent;
    private final String pubsub;
    private final EntityBareJid room;
    private final String subject;
    private final Boolean subjectmod;

    RoomInfo(DiscoverInfo info) {
        DiscoverInfo discoverInfo = info;
        Jid from = info.getFrom();
        if (from != null) {
            this.room = info.getFrom().asEntityBareJidIfPossible();
        } else {
            this.room = null;
        }
        this.membersOnly = discoverInfo.containsFeature("muc_membersonly");
        this.moderated = discoverInfo.containsFeature("muc_moderated");
        this.nonanonymous = discoverInfo.containsFeature("muc_nonanonymous");
        this.passwordProtected = discoverInfo.containsFeature("muc_passwordprotected");
        this.persistent = discoverInfo.containsFeature("muc_persistent");
        List<Identity> identities = info.getIdentities();
        if (!identities.isEmpty()) {
            this.name = ((Identity) identities.get(0)).getName();
        } else {
            Logger logger = LOGGER;
            StringBuilder sb = new StringBuilder();
            sb.append("DiscoverInfo does not contain any Identity: ");
            sb.append(discoverInfo.toXML((String) null));
            logger.warning(sb.toString());
            this.name = BuildConfig.FLAVOR;
        }
        String subject2 = BuildConfig.FLAVOR;
        int occupantsCount2 = -1;
        String description2 = BuildConfig.FLAVOR;
        int maxhistoryfetch2 = -1;
        List<EntityBareJid> contactJid2 = null;
        String lang2 = null;
        String ldapgroup2 = null;
        Boolean subjectmod2 = null;
        URL logs2 = null;
        String pubsub2 = null;
        this.form = Form.getFormFrom(info);
        Form form2 = this.form;
        if (form2 != null) {
            FormField descField = form2.getField("muc#roominfo_description");
            if (descField != null && !descField.getValues().isEmpty()) {
                description2 = descField.getFirstValue();
            }
            String subject3 = subject2;
            FormField subjField = this.form.getField("muc#roominfo_subject");
            if (subjField != null && !subjField.getValues().isEmpty()) {
                subject3 = subjField.getFirstValue();
            }
            FormField occCountField = this.form.getField("muc#roominfo_occupants");
            if (occCountField != null && !occCountField.getValues().isEmpty()) {
                occupantsCount2 = Integer.parseInt(occCountField.getFirstValue());
            }
            FormField formField = occCountField;
            FormField maxhistoryfetchField = this.form.getField("muc#maxhistoryfetch");
            if (maxhistoryfetchField != null && !maxhistoryfetchField.getValues().isEmpty()) {
                maxhistoryfetch2 = Integer.parseInt(maxhistoryfetchField.getFirstValue());
            }
            FormField formField2 = maxhistoryfetchField;
            FormField contactJidField = this.form.getField("muc#roominfo_contactjid");
            if (contactJidField != null && !contactJidField.getValues().isEmpty()) {
                contactJid2 = JidUtil.filterEntityBareJidList(JidUtil.jidSetFrom((Collection<? extends CharSequence>) contactJidField.getValues()));
            }
            FormField formField3 = contactJidField;
            FormField langField = this.form.getField("muc#roominfo_lang");
            if (langField != null && !langField.getValues().isEmpty()) {
                lang2 = langField.getFirstValue();
            }
            FormField formField4 = langField;
            FormField ldapgroupField = this.form.getField("muc#roominfo_ldapgroup");
            if (ldapgroupField != null && !ldapgroupField.getValues().isEmpty()) {
                ldapgroup2 = ldapgroupField.getFirstValue();
            }
            FormField formField5 = ldapgroupField;
            FormField subjectmodField = this.form.getField("muc#roominfo_subjectmod");
            if (subjectmodField == null || subjectmodField.getValues().isEmpty()) {
            } else {
                String firstValue = subjectmodField.getFirstValue();
                FormField formField6 = subjectmodField;
                subjectmod2 = Boolean.valueOf("true".equals(firstValue) || "1".equals(firstValue));
            }
            FormField urlField = this.form.getField("muc#roominfo_logs");
            if (urlField == null || urlField.getValues().isEmpty()) {
                Jid jid = from;
                List list = identities;
            } else {
                Jid jid2 = from;
                String urlString = urlField.getFirstValue();
                try {
                    logs2 = new URL(urlString);
                    FormField formField7 = urlField;
                    List list2 = identities;
                } catch (MalformedURLException e) {
                    FormField formField8 = urlField;
                    String str = urlString;
                    List list3 = identities;
                    LOGGER.log(Level.SEVERE, "Could not parse URL", e);
                }
            }
            FormField pubsubField = this.form.getField("muc#roominfo_pubsub");
            if (pubsubField == null || pubsubField.getValues().isEmpty()) {
                subject2 = subject3;
            } else {
                pubsub2 = pubsubField.getFirstValue();
                subject2 = subject3;
            }
        } else {
            Jid jid3 = from;
            List list4 = identities;
        }
        this.description = description2;
        this.subject = subject2;
        this.occupantsCount = occupantsCount2;
        this.maxhistoryfetch = maxhistoryfetch2;
        this.contactJid = contactJid2;
        this.lang = lang2;
        this.ldapgroup = ldapgroup2;
        this.subjectmod = subjectmod2;
        this.logs = logs2;
        this.pubsub = pubsub2;
    }

    public EntityBareJid getRoom() {
        return this.room;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getSubject() {
        return this.subject;
    }

    public int getOccupantsCount() {
        return this.occupantsCount;
    }

    public boolean isMembersOnly() {
        return this.membersOnly;
    }

    public boolean isModerated() {
        return this.moderated;
    }

    public boolean isNonanonymous() {
        return this.nonanonymous;
    }

    public boolean isPasswordProtected() {
        return this.passwordProtected;
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    public int getMaxHistoryFetch() {
        return this.maxhistoryfetch;
    }

    public List<EntityBareJid> getContactJids() {
        return Collections.unmodifiableList(this.contactJid);
    }

    public String getLang() {
        return this.lang;
    }

    public String getLdapGroup() {
        return this.ldapgroup;
    }

    public Boolean isSubjectModifiable() {
        return this.subjectmod;
    }

    public String getPubSub() {
        return this.pubsub;
    }

    public URL getLogsUrl() {
        return this.logs;
    }

    public Form getForm() {
        return this.form;
    }
}
