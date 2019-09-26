package org.jivesoftware.smackx.hoxt.packet;

import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.util.Objects;

public final class HttpOverXmppResp extends AbstractHttpOverXmpp {
    public static final String ELEMENT = "resp";
    private final int statusCode;
    private final String statusMessage;

    public static final class Builder extends org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp.Builder<Builder, HttpOverXmppResp> {
        /* access modifiers changed from: private */
        public int statusCode;
        /* access modifiers changed from: private */
        public String statusMessage;

        private Builder() {
            this.statusCode = 200;
            this.statusMessage = null;
        }

        public Builder setStatusCode(int statusCode2) {
            this.statusCode = statusCode2;
            return this;
        }

        public Builder setStatusMessage(String statusMessage2) {
            this.statusMessage = statusMessage2;
            return this;
        }

        public HttpOverXmppResp build() {
            return new HttpOverXmppResp(this);
        }

        /* access modifiers changed from: protected */
        public Builder getThis() {
            return this;
        }
    }

    private HttpOverXmppResp(Builder builder) {
        super(ELEMENT, builder);
        this.statusCode = ((Integer) Objects.requireNonNull(Integer.valueOf(builder.statusCode), "statusCode must not be null")).intValue();
        this.statusMessage = builder.statusMessage;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQHoxtChildElementBuilder(IQChildElementXmlStringBuilder builder) {
        builder.attribute("version", getVersion());
        builder.attribute("statusCode", this.statusCode);
        builder.optAttribute("statusMessage", this.statusMessage);
        builder.rightAngleBracket();
        return builder;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public static Builder builder() {
        return new Builder();
    }
}
