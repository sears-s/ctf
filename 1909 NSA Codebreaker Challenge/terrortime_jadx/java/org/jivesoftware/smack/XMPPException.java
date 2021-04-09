package org.jivesoftware.smack;

import org.jivesoftware.smack.packet.Nonza;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.packet.StanzaError.Builder;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smack.packet.StreamError;
import org.jxmpp.jid.Jid;

public abstract class XMPPException extends Exception {
    private static final long serialVersionUID = 6881651633890968625L;

    public static class FailedNonzaException extends XMPPException {
        private static final long serialVersionUID = 1;
        private final Condition condition;
        private final Nonza nonza;

        public FailedNonzaException(Nonza nonza2, Condition condition2) {
            this.condition = condition2;
            this.nonza = nonza2;
        }

        public Condition getCondition() {
            return this.condition;
        }

        public Nonza getNonza() {
            return this.nonza;
        }
    }

    public static class StreamErrorException extends XMPPException {
        private static final long serialVersionUID = 3400556867134848886L;
        private final StreamError streamError;

        public StreamErrorException(StreamError streamError2) {
            StringBuilder sb = new StringBuilder();
            sb.append(streamError2.getCondition().toString());
            sb.append(" You can read more about the meaning of this stream error at http://xmpp.org/rfcs/rfc6120.html#streams-error-conditions\n");
            sb.append(streamError2.toString());
            super(sb.toString());
            this.streamError = streamError2;
        }

        public StreamError getStreamError() {
            return this.streamError;
        }
    }

    public static class XMPPErrorException extends XMPPException {
        private static final long serialVersionUID = 212790389529249604L;
        private final StanzaError error;
        private final Stanza request;
        private final Stanza stanza;

        @Deprecated
        public XMPPErrorException(Builder xmppErrorBuilder) {
            this(null, xmppErrorBuilder.build());
        }

        public XMPPErrorException(Stanza stanza2, StanzaError error2) {
            this(stanza2, error2, null);
        }

        public XMPPErrorException(Stanza stanza2, StanzaError error2, Stanza request2) {
            this.error = error2;
            this.stanza = stanza2;
            this.request = request2;
        }

        @Deprecated
        public StanzaError getXMPPError() {
            return this.error;
        }

        public StanzaError getStanzaError() {
            return this.error;
        }

        public Stanza getRequest() {
            return this.request;
        }

        public String getMessage() {
            StringBuilder sb = new StringBuilder();
            Stanza stanza2 = this.stanza;
            if (stanza2 != null) {
                Jid from = stanza2.getFrom();
                if (from != null) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("XMPP error reply received from ");
                    sb2.append(from);
                    sb2.append(": ");
                    sb.append(sb2.toString());
                }
            }
            sb.append(this.error);
            if (this.request != null) {
                sb.append(" as result of the following request: ");
                sb.append(this.request);
            }
            return sb.toString();
        }

        public static void ifHasErrorThenThrow(Stanza packet) throws XMPPErrorException {
            ifHasErrorThenThrow(packet, null);
        }

        public static void ifHasErrorThenThrow(Stanza packet, Stanza request2) throws XMPPErrorException {
            StanzaError xmppError = packet.getError();
            if (xmppError != null) {
                throw new XMPPErrorException(packet, xmppError, request2);
            }
        }
    }

    protected XMPPException() {
    }

    protected XMPPException(String message) {
        super(message);
    }

    protected XMPPException(String message, Throwable wrappedThrowable) {
        super(message, wrappedThrowable);
    }
}
