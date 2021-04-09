package org.jivesoftware.smack.tcp;

import org.jivesoftware.smack.ConnectionConfiguration;

public final class XMPPTCPConnectionConfiguration extends ConnectionConfiguration {
    public static int DEFAULT_CONNECT_TIMEOUT = 30000;
    private final boolean compressionEnabled;
    private final int connectTimeout;

    public static final class Builder extends org.jivesoftware.smack.ConnectionConfiguration.Builder<Builder, XMPPTCPConnectionConfiguration> {
        /* access modifiers changed from: private */
        public boolean compressionEnabled;
        /* access modifiers changed from: private */
        public int connectTimeout;

        private Builder() {
            this.compressionEnabled = false;
            this.connectTimeout = XMPPTCPConnectionConfiguration.DEFAULT_CONNECT_TIMEOUT;
        }

        public Builder setCompressionEnabled(boolean compressionEnabled2) {
            this.compressionEnabled = compressionEnabled2;
            return this;
        }

        public Builder setConnectTimeout(int connectTimeout2) {
            this.connectTimeout = connectTimeout2;
            return this;
        }

        /* access modifiers changed from: protected */
        public Builder getThis() {
            return this;
        }

        public XMPPTCPConnectionConfiguration build() {
            return new XMPPTCPConnectionConfiguration(this);
        }
    }

    private XMPPTCPConnectionConfiguration(Builder builder) {
        super(builder);
        this.compressionEnabled = builder.compressionEnabled;
        this.connectTimeout = builder.connectTimeout;
    }

    public boolean isCompressionEnabled() {
        return this.compressionEnabled;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public static Builder builder() {
        return new Builder();
    }
}
