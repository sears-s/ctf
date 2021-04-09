package org.jivesoftware.smack.proxy;

public class ProxyInfo {
    private String proxyAddress;
    private String proxyPassword;
    private int proxyPort;
    private final ProxySocketConnection proxySocketConnection;
    private ProxyType proxyType;
    private String proxyUsername;

    /* renamed from: org.jivesoftware.smack.proxy.ProxyInfo$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$proxy$ProxyInfo$ProxyType = new int[ProxyType.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$proxy$ProxyInfo$ProxyType[ProxyType.HTTP.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$proxy$ProxyInfo$ProxyType[ProxyType.SOCKS4.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$proxy$ProxyInfo$ProxyType[ProxyType.SOCKS5.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public enum ProxyType {
        HTTP,
        SOCKS4,
        SOCKS5
    }

    public ProxyInfo(ProxyType pType, String pHost, int pPort, String pUser, String pPass) {
        this.proxyType = pType;
        this.proxyAddress = pHost;
        this.proxyPort = pPort;
        this.proxyUsername = pUser;
        this.proxyPassword = pPass;
        int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$proxy$ProxyInfo$ProxyType[this.proxyType.ordinal()];
        if (i == 1) {
            this.proxySocketConnection = new HTTPProxySocketConnection(this);
        } else if (i == 2) {
            this.proxySocketConnection = new Socks4ProxySocketConnection(this);
        } else if (i == 3) {
            this.proxySocketConnection = new Socks5ProxySocketConnection(this);
        } else {
            throw new IllegalStateException();
        }
    }

    public static ProxyInfo forHttpProxy(String pHost, int pPort, String pUser, String pPass) {
        ProxyInfo proxyInfo = new ProxyInfo(ProxyType.HTTP, pHost, pPort, pUser, pPass);
        return proxyInfo;
    }

    public static ProxyInfo forSocks4Proxy(String pHost, int pPort, String pUser, String pPass) {
        ProxyInfo proxyInfo = new ProxyInfo(ProxyType.SOCKS4, pHost, pPort, pUser, pPass);
        return proxyInfo;
    }

    public static ProxyInfo forSocks5Proxy(String pHost, int pPort, String pUser, String pPass) {
        ProxyInfo proxyInfo = new ProxyInfo(ProxyType.SOCKS5, pHost, pPort, pUser, pPass);
        return proxyInfo;
    }

    public ProxyType getProxyType() {
        return this.proxyType;
    }

    public String getProxyAddress() {
        return this.proxyAddress;
    }

    public int getProxyPort() {
        return this.proxyPort;
    }

    public String getProxyUsername() {
        return this.proxyUsername;
    }

    public String getProxyPassword() {
        return this.proxyPassword;
    }

    public ProxySocketConnection getProxySocketConnection() {
        return this.proxySocketConnection;
    }
}
