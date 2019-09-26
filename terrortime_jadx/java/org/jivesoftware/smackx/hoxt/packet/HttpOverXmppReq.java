package org.jivesoftware.smackx.hoxt.packet;

import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp.Ibb;
import org.jivesoftware.smackx.jingle.element.Jingle;

public final class HttpOverXmppReq extends AbstractHttpOverXmpp {
    public static final String ELEMENT = "req";
    private final boolean ibb;
    private final boolean jingle;
    private final int maxChunkSize;
    private final HttpMethod method;
    private final String resource;
    private final boolean sipub;

    public static final class Builder extends org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp.Builder<Builder, HttpOverXmppReq> {
        /* access modifiers changed from: private */
        public boolean ibb;
        /* access modifiers changed from: private */
        public boolean jingle;
        /* access modifiers changed from: private */
        public int maxChunkSize;
        /* access modifiers changed from: private */
        public HttpMethod method;
        /* access modifiers changed from: private */
        public String resource;
        /* access modifiers changed from: private */
        public boolean sipub;

        private Builder() {
            this.maxChunkSize = -1;
            this.sipub = true;
            this.ibb = true;
            this.jingle = true;
        }

        public Builder setMethod(HttpMethod method2) {
            this.method = method2;
            return this;
        }

        public Builder setResource(String resource2) {
            this.resource = resource2;
            return this;
        }

        public Builder setJingle(boolean jingle2) {
            this.jingle = jingle2;
            return this;
        }

        public Builder setIbb(boolean ibb2) {
            this.ibb = ibb2;
            return this;
        }

        public Builder setSipub(boolean sipub2) {
            this.sipub = sipub2;
            return this;
        }

        public Builder setMaxChunkSize(int maxChunkSize2) {
            if (maxChunkSize2 < 256 || maxChunkSize2 > 65536) {
                throw new IllegalArgumentException("maxChunkSize must be within [256, 65536]");
            }
            this.maxChunkSize = maxChunkSize2;
            return this;
        }

        public HttpOverXmppReq build() {
            if (this.method == null) {
                throw new IllegalArgumentException("Method cannot be null");
            } else if (this.resource != null) {
                return new HttpOverXmppReq(this);
            } else {
                throw new IllegalArgumentException("Resource cannot be null");
            }
        }

        /* access modifiers changed from: protected */
        public Builder getThis() {
            return this;
        }
    }

    private HttpOverXmppReq(Builder builder) {
        super("req", builder);
        this.method = builder.method;
        this.resource = builder.resource;
        this.maxChunkSize = builder.maxChunkSize;
        this.ibb = builder.ibb;
        this.jingle = builder.jingle;
        this.sipub = builder.sipub;
        setType(Type.set);
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQHoxtChildElementBuilder(IQChildElementXmlStringBuilder builder) {
        builder.attribute("method", (Enum<?>) this.method);
        builder.attribute("resource", this.resource);
        builder.attribute("version", getVersion());
        builder.optIntAttribute("maxChunkSize", this.maxChunkSize);
        builder.optBooleanAttributeDefaultTrue("sipub", this.sipub);
        builder.optBooleanAttributeDefaultTrue(Ibb.ELEMENT, this.ibb);
        builder.optBooleanAttributeDefaultTrue(Jingle.ELEMENT, this.jingle);
        builder.rightAngleBracket();
        return builder;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public String getResource() {
        return this.resource;
    }

    public int getMaxChunkSize() {
        return this.maxChunkSize;
    }

    public boolean isSipub() {
        return this.sipub;
    }

    public boolean isIbb() {
        return this.ibb;
    }

    public boolean isJingle() {
        return this.jingle;
    }

    public static Builder builder() {
        return new Builder();
    }
}
