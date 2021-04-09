package org.jivesoftware.smackx.muc;

import org.jivesoftware.smack.SmackException;
import org.jxmpp.jid.DomainBareJid;

public abstract class MultiUserChatException extends SmackException {
    private static final long serialVersionUID = 1;

    public static class MissingMucCreationAcknowledgeException extends MultiUserChatException {
        private static final long serialVersionUID = 1;
    }

    public static class MucAlreadyJoinedException extends MultiUserChatException {
        private static final long serialVersionUID = 1;
    }

    public static class MucConfigurationNotSupportedException extends MultiUserChatException {
        private static final long serialVersionUID = 1;

        public MucConfigurationNotSupportedException(String configString) {
            StringBuilder sb = new StringBuilder();
            sb.append("The MUC configuration '");
            sb.append(configString);
            sb.append("' is not supported by the MUC service");
            super(sb.toString());
        }
    }

    public static class MucNotJoinedException extends MultiUserChatException {
        private static final long serialVersionUID = 1;

        public MucNotJoinedException(MultiUserChat multiUserChat) {
            StringBuilder sb = new StringBuilder();
            sb.append("Client not currently joined ");
            sb.append(multiUserChat.getRoom());
            super(sb.toString());
        }
    }

    public static class NotAMucServiceException extends MultiUserChatException {
        private static final long serialVersionUID = 1;

        NotAMucServiceException(DomainBareJid service) {
            StringBuilder sb = new StringBuilder();
            sb.append("Can't perform operation because ");
            sb.append(service);
            sb.append(" does not provide a MUC (XEP-45) service.");
            super(sb.toString());
        }

        NotAMucServiceException(MultiUserChat multiUserChat) {
            StringBuilder sb = new StringBuilder();
            sb.append("Can not join '");
            sb.append(multiUserChat.getRoom());
            sb.append("', because '");
            sb.append(multiUserChat.getRoom().asDomainBareJid());
            sb.append("' does not provide a MUC (XEP-45) service.");
            super(sb.toString());
        }
    }

    protected MultiUserChatException() {
    }

    protected MultiUserChatException(String message) {
        super(message);
    }
}
