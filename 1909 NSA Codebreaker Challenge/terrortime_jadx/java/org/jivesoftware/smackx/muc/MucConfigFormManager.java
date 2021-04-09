package org.jivesoftware.smackx.muc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChatException.MucConfigurationNotSupportedException;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.FormField.Type;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.util.JidUtil;

public class MucConfigFormManager {
    public static final String MUC_ROOMCONFIG_MEMBERSONLY = "muc#roomconfig_membersonly";
    public static final String MUC_ROOMCONFIG_PASSWORDPROTECTEDROOM = "muc#roomconfig_passwordprotectedroom";
    public static final String MUC_ROOMCONFIG_ROOMOWNERS = "muc#roomconfig_roomowners";
    public static final String MUC_ROOMCONFIG_ROOMSECRET = "muc#roomconfig_roomsecret";
    private final Form answerForm;
    private final MultiUserChat multiUserChat;
    private final List<Jid> owners;

    MucConfigFormManager(MultiUserChat multiUserChat2) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        this.multiUserChat = multiUserChat2;
        Form configForm = multiUserChat2.getConfigurationForm();
        this.answerForm = configForm.createAnswerForm();
        for (FormField field : configForm.getFields()) {
            if (field.getType() != Type.hidden && !StringUtils.isNullOrEmpty((CharSequence) field.getVariable())) {
                this.answerForm.setDefaultAnswer(field.getVariable());
            }
        }
        Form form = this.answerForm;
        String str = MUC_ROOMCONFIG_ROOMOWNERS;
        if (form.hasField(str)) {
            List<CharSequence> ownerStrings = this.answerForm.getField(str).getValues();
            this.owners = new ArrayList(ownerStrings.size());
            JidUtil.jidsFrom(ownerStrings, this.owners, null);
            return;
        }
        this.owners = null;
    }

    public boolean supportsRoomOwners() {
        return this.owners != null;
    }

    public MucConfigFormManager setRoomOwners(Collection<? extends Jid> newOwners) throws MucConfigurationNotSupportedException {
        if (supportsRoomOwners()) {
            this.owners.clear();
            this.owners.addAll(newOwners);
            return this;
        }
        throw new MucConfigurationNotSupportedException(MUC_ROOMCONFIG_ROOMOWNERS);
    }

    public boolean supportsMembersOnly() {
        return this.answerForm.hasField(MUC_ROOMCONFIG_MEMBERSONLY);
    }

    public MucConfigFormManager makeMembersOnly() throws MucConfigurationNotSupportedException {
        return setMembersOnly(true);
    }

    public MucConfigFormManager setMembersOnly(boolean isMembersOnly) throws MucConfigurationNotSupportedException {
        boolean supportsMembersOnly = supportsMembersOnly();
        String str = MUC_ROOMCONFIG_MEMBERSONLY;
        if (supportsMembersOnly) {
            this.answerForm.setAnswer(str, isMembersOnly);
            return this;
        }
        throw new MucConfigurationNotSupportedException(str);
    }

    public boolean supportsPasswordProtected() {
        return this.answerForm.hasField(MUC_ROOMCONFIG_PASSWORDPROTECTEDROOM);
    }

    public MucConfigFormManager setAndEnablePassword(String password) throws MucConfigurationNotSupportedException {
        return setIsPasswordProtected(true).setRoomSecret(password);
    }

    public MucConfigFormManager makePasswordProtected() throws MucConfigurationNotSupportedException {
        return setIsPasswordProtected(true);
    }

    public MucConfigFormManager setIsPasswordProtected(boolean isPasswordProtected) throws MucConfigurationNotSupportedException {
        boolean supportsMembersOnly = supportsMembersOnly();
        String str = MUC_ROOMCONFIG_PASSWORDPROTECTEDROOM;
        if (supportsMembersOnly) {
            this.answerForm.setAnswer(str, isPasswordProtected);
            return this;
        }
        throw new MucConfigurationNotSupportedException(str);
    }

    public MucConfigFormManager setRoomSecret(String secret) throws MucConfigurationNotSupportedException {
        Form form = this.answerForm;
        String str = MUC_ROOMCONFIG_ROOMSECRET;
        if (form.hasField(str)) {
            this.answerForm.setAnswer(str, secret);
            return this;
        }
        throw new MucConfigurationNotSupportedException(str);
    }

    public void submitConfigurationForm() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        List<Jid> list = this.owners;
        if (list != null) {
            this.answerForm.setAnswer(MUC_ROOMCONFIG_ROOMOWNERS, JidUtil.toStringList(list));
        }
        this.multiUserChat.sendConfigurationForm(this.answerForm);
    }
}
