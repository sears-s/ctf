package org.jivesoftware.smack;

import com.badguy.terrortime.BuildConfig;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.util.dns.HostAddress;
import org.jxmpp.jid.Jid;

public class SmackException extends Exception {
    private static final long serialVersionUID = 1844674365368214457L;

    public static class AlreadyConnectedException extends SmackException {
        private static final long serialVersionUID = 5011416918049135231L;

        public AlreadyConnectedException() {
            super("Client is already connected");
        }
    }

    public static class AlreadyLoggedInException extends SmackException {
        private static final long serialVersionUID = 5011416918049935231L;

        public AlreadyLoggedInException() {
            super("Client is already logged in");
        }
    }

    public static class ConnectionException extends SmackException {
        private static final long serialVersionUID = 1686944201672697996L;
        private final List<HostAddress> failedAddresses;

        public ConnectionException(Throwable wrappedThrowable) {
            super(wrappedThrowable);
            this.failedAddresses = new ArrayList(0);
        }

        private ConnectionException(String message, List<HostAddress> failedAddresses2) {
            super(message);
            this.failedAddresses = failedAddresses2;
        }

        public static ConnectionException from(List<HostAddress> failedAddresses2) {
            String str = ", ";
            StringBuilder sb = new StringBuilder("The following addresses failed: ");
            Iterator it = failedAddresses2.iterator();
            while (true) {
                String str2 = ", ";
                if (it.hasNext()) {
                    sb.append(((HostAddress) it.next()).getErrorMessage());
                    sb.append(str2);
                } else {
                    sb.setLength(sb.length() - str2.length());
                    return new ConnectionException(sb.toString(), failedAddresses2);
                }
            }
        }

        public List<HostAddress> getFailedAddresses() {
            return this.failedAddresses;
        }
    }

    public static class FeatureNotSupportedException extends SmackException {
        private static final long serialVersionUID = 4713404802621452016L;
        private final String feature;
        private final Jid jid;

        public FeatureNotSupportedException(String feature2) {
            this(feature2, null);
        }

        public FeatureNotSupportedException(String feature2, Jid jid2) {
            String str;
            StringBuilder sb = new StringBuilder();
            sb.append(feature2);
            sb.append(" not supported");
            if (jid2 == null) {
                str = BuildConfig.FLAVOR;
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(" by '");
                sb2.append(jid2);
                sb2.append("'");
                str = sb2.toString();
            }
            sb.append(str);
            super(sb.toString());
            this.jid = jid2;
            this.feature = feature2;
        }

        public String getFeature() {
            return this.feature;
        }

        public Jid getJid() {
            return this.jid;
        }
    }

    public static class IllegalStateChangeException extends SmackException {
        private static final long serialVersionUID = -1766023961577168927L;
    }

    public static final class NoResponseException extends SmackException {
        private static final long serialVersionUID = -6523363748984543636L;
        private final StanzaFilter filter;

        private NoResponseException(String message) {
            this(message, null);
        }

        private NoResponseException(String message, StanzaFilter filter2) {
            super(message);
            this.filter = filter2;
        }

        public StanzaFilter getFilter() {
            return this.filter;
        }

        public static NoResponseException newWith(XMPPConnection connection, String waitingFor) {
            StringBuilder sb = getWaitingFor(connection);
            sb.append(" While waiting for ");
            sb.append(waitingFor);
            return new NoResponseException(sb.toString());
        }

        @Deprecated
        public static NoResponseException newWith(XMPPConnection connection, StanzaCollector collector) {
            return newWith(connection, collector.getStanzaFilter());
        }

        public static NoResponseException newWith(long timeout, StanzaCollector collector) {
            return newWith(timeout, collector.getStanzaFilter());
        }

        public static NoResponseException newWith(XMPPConnection connection, StanzaFilter filter2) {
            return newWith(connection.getReplyTimeout(), filter2);
        }

        public static NoResponseException newWith(long timeout, StanzaFilter filter2) {
            StringBuilder sb = getWaitingFor(timeout);
            sb.append(" Waited for response using: ");
            if (filter2 != null) {
                sb.append(filter2.toString());
            } else {
                sb.append("No filter used or filter was 'null'");
            }
            sb.append('.');
            return new NoResponseException(sb.toString(), filter2);
        }

        private static StringBuilder getWaitingFor(XMPPConnection connection) {
            return getWaitingFor(connection.getReplyTimeout());
        }

        private static StringBuilder getWaitingFor(long replyTimeout) {
            StringBuilder sb = new StringBuilder(256);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("No response received within reply timeout. Timeout was ");
            sb2.append(replyTimeout);
            sb2.append("ms (~");
            sb2.append(replyTimeout / 1000);
            sb2.append("s).");
            sb.append(sb2.toString());
            return sb;
        }
    }

    public static class NotConnectedException extends SmackException {
        private static final long serialVersionUID = 9197980400776001173L;

        public NotConnectedException() {
            this(null);
        }

        public NotConnectedException(String optionalHint) {
            String str;
            StringBuilder sb = new StringBuilder();
            sb.append("Client is not, or no longer, connected.");
            if (optionalHint != null) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(' ');
                sb2.append(optionalHint);
                str = sb2.toString();
            } else {
                str = BuildConfig.FLAVOR;
            }
            sb.append(str);
            super(sb.toString());
        }

        public NotConnectedException(XMPPConnection connection, String details) {
            StringBuilder sb = new StringBuilder();
            sb.append("The connection ");
            sb.append(connection.toString());
            sb.append(" is no longer connected. ");
            sb.append(details);
            super(sb.toString());
        }

        public NotConnectedException(XMPPConnection connection, StanzaFilter stanzaFilter) {
            StringBuilder sb = new StringBuilder();
            sb.append("The connection ");
            sb.append(connection);
            sb.append(" is no longer connected while waiting for response with ");
            sb.append(stanzaFilter);
            super(sb.toString());
        }
    }

    public static class NotLoggedInException extends SmackException {
        private static final long serialVersionUID = 3216216839100019278L;

        public NotLoggedInException() {
            super("Client is not logged in");
        }
    }

    public static class ResourceBindingNotOfferedException extends SmackException {
        private static final long serialVersionUID = 2346934138253437571L;

        public ResourceBindingNotOfferedException() {
            super("Resource binding was not offered by server");
        }
    }

    public static class SecurityNotPossibleException extends SmackException {
        private static final long serialVersionUID = -6836090872690331336L;

        public SecurityNotPossibleException(String message) {
            super(message);
        }
    }

    public static class SecurityRequiredByClientException extends SecurityRequiredException {
        private static final long serialVersionUID = 2395325821201543159L;

        public SecurityRequiredByClientException() {
            super("SSL/TLS required by client but not supported by server");
        }
    }

    public static class SecurityRequiredByServerException extends SecurityRequiredException {
        private static final long serialVersionUID = 8268148813117631819L;

        public SecurityRequiredByServerException() {
            super("SSL/TLS required by server but disabled in client");
        }
    }

    public static abstract class SecurityRequiredException extends SmackException {
        private static final long serialVersionUID = 384291845029773545L;

        public SecurityRequiredException(String message) {
            super(message);
        }
    }

    public static class SmackWrappedException extends SmackException {
        private static final long serialVersionUID = 1;

        public SmackWrappedException(Exception exception) {
            super((Throwable) exception);
        }
    }

    public SmackException(Throwable wrappedThrowable) {
        super(wrappedThrowable);
    }

    public SmackException(String message) {
        super(message);
    }

    public SmackException(String message, Throwable wrappedThrowable) {
        super(message, wrappedThrowable);
    }

    protected SmackException() {
    }
}
