package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.Reference;
import java.net.ConnectException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.CertificatePinner;
import okhttp3.Connection;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.Interceptor.Chain;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.Version;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http1.Http1Codec;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Http2Codec;
import okhttp3.internal.http2.Http2Connection;
import okhttp3.internal.http2.Http2Connection.Builder;
import okhttp3.internal.http2.Http2Connection.Listener;
import okhttp3.internal.http2.Http2Stream;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.tls.OkHostnameVerifier;
import okhttp3.internal.ws.RealWebSocket.Streams;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Close;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;

public final class RealConnection extends Listener implements Connection {
    private static final int MAX_TUNNEL_ATTEMPTS = 21;
    private static final String NPE_THROW_WITH_NULL = "throw with null exception";
    public int allocationLimit = 1;
    public final List<Reference<StreamAllocation>> allocations = new ArrayList();
    private final ConnectionPool connectionPool;
    private Handshake handshake;
    private Http2Connection http2Connection;
    public long idleAtNanos = Long.MAX_VALUE;
    public boolean noNewStreams;
    private Protocol protocol;
    private Socket rawSocket;
    private final Route route;
    private BufferedSink sink;
    private Socket socket;
    private BufferedSource source;
    public int successCount;

    public RealConnection(ConnectionPool connectionPool2, Route route2) {
        this.connectionPool = connectionPool2;
        this.route = route2;
    }

    public static RealConnection testConnection(ConnectionPool connectionPool2, Route route2, Socket socket2, long idleAtNanos2) {
        RealConnection result = new RealConnection(connectionPool2, route2);
        result.socket = socket2;
        result.idleAtNanos = idleAtNanos2;
        return result;
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x00ec  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0138  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x013f  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0144  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x014c A[EDGE_INSN: B:63:0x014c->B:57:0x014c ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:66:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void connect(int r17, int r18, int r19, int r20, boolean r21, okhttp3.Call r22, okhttp3.EventListener r23) {
        /*
            r16 = this;
            r7 = r16
            r8 = r22
            r9 = r23
            okhttp3.Protocol r0 = r7.protocol
            if (r0 != 0) goto L_0x0160
            r0 = 0
            okhttp3.Route r1 = r7.route
            okhttp3.Address r1 = r1.address()
            java.util.List r10 = r1.connectionSpecs()
            okhttp3.internal.connection.ConnectionSpecSelector r1 = new okhttp3.internal.connection.ConnectionSpecSelector
            r1.<init>(r10)
            r11 = r1
            okhttp3.Route r1 = r7.route
            okhttp3.Address r1 = r1.address()
            javax.net.ssl.SSLSocketFactory r1 = r1.sslSocketFactory()
            if (r1 != 0) goto L_0x0076
            okhttp3.ConnectionSpec r1 = okhttp3.ConnectionSpec.CLEARTEXT
            boolean r1 = r10.contains(r1)
            if (r1 == 0) goto L_0x0069
            okhttp3.Route r1 = r7.route
            okhttp3.Address r1 = r1.address()
            okhttp3.HttpUrl r1 = r1.url()
            java.lang.String r1 = r1.host()
            okhttp3.internal.platform.Platform r2 = okhttp3.internal.platform.Platform.get()
            boolean r2 = r2.isCleartextTrafficPermitted(r1)
            if (r2 == 0) goto L_0x0048
            goto L_0x0088
        L_0x0048:
            okhttp3.internal.connection.RouteException r2 = new okhttp3.internal.connection.RouteException
            java.net.UnknownServiceException r3 = new java.net.UnknownServiceException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "CLEARTEXT communication to "
            r4.append(r5)
            r4.append(r1)
            java.lang.String r5 = " not permitted by network security policy"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            r2.<init>(r3)
            throw r2
        L_0x0069:
            okhttp3.internal.connection.RouteException r1 = new okhttp3.internal.connection.RouteException
            java.net.UnknownServiceException r2 = new java.net.UnknownServiceException
            java.lang.String r3 = "CLEARTEXT communication not enabled for client"
            r2.<init>(r3)
            r1.<init>(r2)
            throw r1
        L_0x0076:
            okhttp3.Route r1 = r7.route
            okhttp3.Address r1 = r1.address()
            java.util.List r1 = r1.protocols()
            okhttp3.Protocol r2 = okhttp3.Protocol.H2_PRIOR_KNOWLEDGE
            boolean r1 = r1.contains(r2)
            if (r1 != 0) goto L_0x014d
        L_0x0088:
            r12 = r0
        L_0x0089:
            okhttp3.Route r0 = r7.route     // Catch:{ IOException -> 0x0101 }
            boolean r0 = r0.requiresTunnel()     // Catch:{ IOException -> 0x0101 }
            if (r0 == 0) goto L_0x00b0
            r1 = r16
            r2 = r17
            r3 = r18
            r4 = r19
            r5 = r22
            r6 = r23
            r1.connectTunnel(r2, r3, r4, r5, r6)     // Catch:{ IOException -> 0x0101 }
            java.net.Socket r0 = r7.rawSocket     // Catch:{ IOException -> 0x0101 }
            if (r0 != 0) goto L_0x00ab
            r13 = r17
            r14 = r18
            r15 = r20
            goto L_0x00ce
        L_0x00ab:
            r13 = r17
            r14 = r18
            goto L_0x00b7
        L_0x00b0:
            r13 = r17
            r14 = r18
            r7.connectSocket(r13, r14, r8, r9)     // Catch:{ IOException -> 0x00ff }
        L_0x00b7:
            r15 = r20
            r7.establishProtocol(r11, r15, r8, r9)     // Catch:{ IOException -> 0x00fd }
            okhttp3.Route r0 = r7.route     // Catch:{ IOException -> 0x00fd }
            java.net.InetSocketAddress r0 = r0.socketAddress()     // Catch:{ IOException -> 0x00fd }
            okhttp3.Route r1 = r7.route     // Catch:{ IOException -> 0x00fd }
            java.net.Proxy r1 = r1.proxy()     // Catch:{ IOException -> 0x00fd }
            okhttp3.Protocol r2 = r7.protocol     // Catch:{ IOException -> 0x00fd }
            r9.connectEnd(r8, r0, r1, r2)     // Catch:{ IOException -> 0x00fd }
        L_0x00ce:
            okhttp3.Route r0 = r7.route
            boolean r0 = r0.requiresTunnel()
            if (r0 == 0) goto L_0x00e8
            java.net.Socket r0 = r7.rawSocket
            if (r0 == 0) goto L_0x00db
            goto L_0x00e8
        L_0x00db:
            java.net.ProtocolException r0 = new java.net.ProtocolException
            java.lang.String r1 = "Too many tunnel connections attempted: 21"
            r0.<init>(r1)
            okhttp3.internal.connection.RouteException r1 = new okhttp3.internal.connection.RouteException
            r1.<init>(r0)
            throw r1
        L_0x00e8:
            okhttp3.internal.http2.Http2Connection r0 = r7.http2Connection
            if (r0 == 0) goto L_0x00fc
            okhttp3.ConnectionPool r1 = r7.connectionPool
            monitor-enter(r1)
            okhttp3.internal.http2.Http2Connection r0 = r7.http2Connection     // Catch:{ all -> 0x00f9 }
            int r0 = r0.maxConcurrentStreams()     // Catch:{ all -> 0x00f9 }
            r7.allocationLimit = r0     // Catch:{ all -> 0x00f9 }
            monitor-exit(r1)     // Catch:{ all -> 0x00f9 }
            goto L_0x00fc
        L_0x00f9:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00f9 }
            throw r0
        L_0x00fc:
            return
        L_0x00fd:
            r0 = move-exception
            goto L_0x0108
        L_0x00ff:
            r0 = move-exception
            goto L_0x0106
        L_0x0101:
            r0 = move-exception
            r13 = r17
            r14 = r18
        L_0x0106:
            r15 = r20
        L_0x0108:
            java.net.Socket r1 = r7.socket
            okhttp3.internal.Util.closeQuietly(r1)
            java.net.Socket r1 = r7.rawSocket
            okhttp3.internal.Util.closeQuietly(r1)
            r1 = 0
            r7.socket = r1
            r7.rawSocket = r1
            r7.source = r1
            r7.sink = r1
            r7.handshake = r1
            r7.protocol = r1
            r7.http2Connection = r1
            okhttp3.Route r1 = r7.route
            java.net.InetSocketAddress r3 = r1.socketAddress()
            okhttp3.Route r1 = r7.route
            java.net.Proxy r4 = r1.proxy()
            r5 = 0
            r1 = r23
            r2 = r22
            r6 = r0
            r1.connectFailed(r2, r3, r4, r5, r6)
            if (r12 != 0) goto L_0x013f
            okhttp3.internal.connection.RouteException r1 = new okhttp3.internal.connection.RouteException
            r1.<init>(r0)
            r12 = r1
            goto L_0x0142
        L_0x013f:
            r12.addConnectException(r0)
        L_0x0142:
            if (r21 == 0) goto L_0x014c
            boolean r1 = r11.connectionFailed(r0)
            if (r1 == 0) goto L_0x014c
            goto L_0x0089
        L_0x014c:
            throw r12
        L_0x014d:
            r13 = r17
            r14 = r18
            r15 = r20
            okhttp3.internal.connection.RouteException r1 = new okhttp3.internal.connection.RouteException
            java.net.UnknownServiceException r2 = new java.net.UnknownServiceException
            java.lang.String r3 = "H2_PRIOR_KNOWLEDGE cannot be used with HTTPS"
            r2.<init>(r3)
            r1.<init>(r2)
            throw r1
        L_0x0160:
            r13 = r17
            r14 = r18
            r15 = r20
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "already connected"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.RealConnection.connect(int, int, int, int, boolean, okhttp3.Call, okhttp3.EventListener):void");
    }

    private void connectTunnel(int connectTimeout, int readTimeout, int writeTimeout, Call call, EventListener eventListener) throws IOException {
        Request tunnelRequest = createTunnelRequest();
        HttpUrl url = tunnelRequest.url();
        int i = 0;
        while (i < 21) {
            connectSocket(connectTimeout, readTimeout, call, eventListener);
            tunnelRequest = createTunnel(readTimeout, writeTimeout, tunnelRequest, url);
            if (tunnelRequest != null) {
                Util.closeQuietly(this.rawSocket);
                this.rawSocket = null;
                this.sink = null;
                this.source = null;
                eventListener.connectEnd(call, this.route.socketAddress(), this.route.proxy(), null);
                i++;
            } else {
                return;
            }
        }
    }

    private void connectSocket(int connectTimeout, int readTimeout, Call call, EventListener eventListener) throws IOException {
        Socket socket2;
        Proxy proxy = this.route.proxy();
        Address address = this.route.address();
        if (proxy.type() == Type.DIRECT || proxy.type() == Type.HTTP) {
            socket2 = address.socketFactory().createSocket();
        } else {
            socket2 = new Socket(proxy);
        }
        this.rawSocket = socket2;
        eventListener.connectStart(call, this.route.socketAddress(), proxy);
        this.rawSocket.setSoTimeout(readTimeout);
        try {
            Platform.get().connectSocket(this.rawSocket, this.route.socketAddress(), connectTimeout);
            try {
                this.source = Okio.buffer(Okio.source(this.rawSocket));
                this.sink = Okio.buffer(Okio.sink(this.rawSocket));
            } catch (NullPointerException npe) {
                if (NPE_THROW_WITH_NULL.equals(npe.getMessage())) {
                    throw new IOException(npe);
                }
            }
        } catch (ConnectException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to connect to ");
            sb.append(this.route.socketAddress());
            ConnectException ce = new ConnectException(sb.toString());
            ce.initCause(e);
            throw ce;
        }
    }

    private void establishProtocol(ConnectionSpecSelector connectionSpecSelector, int pingIntervalMillis, Call call, EventListener eventListener) throws IOException {
        if (this.route.address().sslSocketFactory() != null) {
            eventListener.secureConnectStart(call);
            connectTls(connectionSpecSelector);
            eventListener.secureConnectEnd(call, this.handshake);
            if (this.protocol == Protocol.HTTP_2) {
                startHttp2(pingIntervalMillis);
            }
        } else if (this.route.address().protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE)) {
            this.socket = this.rawSocket;
            this.protocol = Protocol.H2_PRIOR_KNOWLEDGE;
            startHttp2(pingIntervalMillis);
        } else {
            this.socket = this.rawSocket;
            this.protocol = Protocol.HTTP_1_1;
        }
    }

    private void startHttp2(int pingIntervalMillis) throws IOException {
        this.socket.setSoTimeout(0);
        this.http2Connection = new Builder(true).socket(this.socket, this.route.address().url().host(), this.source, this.sink).listener(this).pingIntervalMillis(pingIntervalMillis).build();
        this.http2Connection.start();
    }

    private void connectTls(ConnectionSpecSelector connectionSpecSelector) throws IOException {
        String maybeProtocol;
        Protocol protocol2;
        Address address = this.route.address();
        SSLSocket sslSocket = null;
        try {
            sslSocket = (SSLSocket) address.sslSocketFactory().createSocket(this.rawSocket, address.url().host(), address.url().port(), true);
            ConnectionSpec connectionSpec = connectionSpecSelector.configureSecureSocket(sslSocket);
            if (connectionSpec.supportsTlsExtensions()) {
                Platform.get().configureTlsExtensions(sslSocket, address.url().host(), address.protocols());
            }
            sslSocket.startHandshake();
            SSLSession sslSocketSession = sslSocket.getSession();
            Handshake unverifiedHandshake = Handshake.get(sslSocketSession);
            if (address.hostnameVerifier().verify(address.url().host(), sslSocketSession)) {
                address.certificatePinner().check(address.url().host(), unverifiedHandshake.peerCertificates());
                if (connectionSpec.supportsTlsExtensions()) {
                    maybeProtocol = Platform.get().getSelectedProtocol(sslSocket);
                } else {
                    maybeProtocol = null;
                }
                this.socket = sslSocket;
                this.source = Okio.buffer(Okio.source(this.socket));
                this.sink = Okio.buffer(Okio.sink(this.socket));
                this.handshake = unverifiedHandshake;
                if (maybeProtocol != null) {
                    protocol2 = Protocol.get(maybeProtocol);
                } else {
                    protocol2 = Protocol.HTTP_1_1;
                }
                this.protocol = protocol2;
                if (sslSocket != null) {
                    Platform.get().afterHandshake(sslSocket);
                }
                if (1 == 0) {
                    Util.closeQuietly((Socket) sslSocket);
                    return;
                }
                return;
            }
            X509Certificate cert = (X509Certificate) unverifiedHandshake.peerCertificates().get(0);
            StringBuilder sb = new StringBuilder();
            sb.append("Hostname ");
            sb.append(address.url().host());
            sb.append(" not verified:\n    certificate: ");
            sb.append(CertificatePinner.pin(cert));
            sb.append("\n    DN: ");
            sb.append(cert.getSubjectDN().getName());
            sb.append("\n    subjectAltNames: ");
            sb.append(OkHostnameVerifier.allSubjectAltNames(cert));
            throw new SSLPeerUnverifiedException(sb.toString());
        } catch (AssertionError e) {
            if (Util.isAndroidGetsocknameError(e)) {
                throw new IOException(e);
            }
            throw e;
        } catch (Throwable th) {
            if (sslSocket != null) {
                Platform.get().afterHandshake(sslSocket);
            }
            if (0 == 0) {
                Util.closeQuietly((Socket) sslSocket);
            }
            throw th;
        }
    }

    private Request createTunnel(int readTimeout, int writeTimeout, Request tunnelRequest, HttpUrl url) throws IOException {
        Response response;
        StringBuilder sb = new StringBuilder();
        sb.append("CONNECT ");
        sb.append(Util.hostHeader(url, true));
        sb.append(" HTTP/1.1");
        String requestLine = sb.toString();
        do {
            Http1Codec tunnelConnection = new Http1Codec(null, null, this.source, this.sink);
            this.source.timeout().timeout((long) readTimeout, TimeUnit.MILLISECONDS);
            this.sink.timeout().timeout((long) writeTimeout, TimeUnit.MILLISECONDS);
            tunnelConnection.writeRequest(tunnelRequest.headers(), requestLine);
            tunnelConnection.finishRequest();
            response = tunnelConnection.readResponseHeaders(false).request(tunnelRequest).build();
            long contentLength = HttpHeaders.contentLength(response);
            if (contentLength == -1) {
                contentLength = 0;
            }
            Source body = tunnelConnection.newFixedLengthSource(contentLength);
            Util.skipAll(body, ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, TimeUnit.MILLISECONDS);
            body.close();
            int code = response.code();
            if (code != 200) {
                if (code == 407) {
                    tunnelRequest = this.route.address().proxyAuthenticator().authenticate(this.route, response);
                    if (tunnelRequest != null) {
                    } else {
                        throw new IOException("Failed to authenticate with proxy");
                    }
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Unexpected response code for CONNECT: ");
                    sb2.append(response.code());
                    throw new IOException(sb2.toString());
                }
            } else if (this.source.buffer().exhausted() && this.sink.buffer().exhausted()) {
                return null;
            } else {
                throw new IOException("TLS tunnel buffered too many bytes!");
            }
        } while (!Close.ELEMENT.equalsIgnoreCase(response.header("Connection")));
        return tunnelRequest;
    }

    private Request createTunnelRequest() {
        return new Request.Builder().url(this.route.address().url()).header("Host", Util.hostHeader(this.route.address().url(), true)).header("Proxy-Connection", "Keep-Alive").header("User-Agent", Version.userAgent()).build();
    }

    public boolean isEligible(Address address, @Nullable Route route2) {
        if (this.allocations.size() >= this.allocationLimit || this.noNewStreams || !Internal.instance.equalsNonHost(this.route.address(), address)) {
            return false;
        }
        if (address.url().host().equals(route().address().url().host())) {
            return true;
        }
        if (this.http2Connection == null || route2 == null || route2.proxy().type() != Type.DIRECT || this.route.proxy().type() != Type.DIRECT || !this.route.socketAddress().equals(route2.socketAddress()) || route2.address().hostnameVerifier() != OkHostnameVerifier.INSTANCE || !supportsUrl(address.url())) {
            return false;
        }
        try {
            address.certificatePinner().check(address.url().host(), handshake().peerCertificates());
            return true;
        } catch (SSLPeerUnverifiedException e) {
            return false;
        }
    }

    public boolean supportsUrl(HttpUrl url) {
        if (url.port() != this.route.address().url().port()) {
            return false;
        }
        boolean z = true;
        if (url.host().equals(this.route.address().url().host())) {
            return true;
        }
        if (this.handshake == null || !OkHostnameVerifier.INSTANCE.verify(url.host(), (X509Certificate) this.handshake.peerCertificates().get(0))) {
            z = false;
        }
        return z;
    }

    public HttpCodec newCodec(OkHttpClient client, Chain chain, StreamAllocation streamAllocation) throws SocketException {
        Http2Connection http2Connection2 = this.http2Connection;
        if (http2Connection2 != null) {
            return new Http2Codec(client, chain, streamAllocation, http2Connection2);
        }
        this.socket.setSoTimeout(chain.readTimeoutMillis());
        this.source.timeout().timeout((long) chain.readTimeoutMillis(), TimeUnit.MILLISECONDS);
        this.sink.timeout().timeout((long) chain.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
        return new Http1Codec(client, streamAllocation, this.source, this.sink);
    }

    public Streams newWebSocketStreams(StreamAllocation streamAllocation) {
        final StreamAllocation streamAllocation2 = streamAllocation;
        AnonymousClass1 r0 = new Streams(true, this.source, this.sink) {
            public void close() throws IOException {
                StreamAllocation streamAllocation = streamAllocation2;
                streamAllocation.streamFinished(true, streamAllocation.codec(), -1, null);
            }
        };
        return r0;
    }

    public Route route() {
        return this.route;
    }

    public void cancel() {
        Util.closeQuietly(this.rawSocket);
    }

    public Socket socket() {
        return this.socket;
    }

    public boolean isHealthy(boolean doExtensiveChecks) {
        int readTimeout;
        if (this.socket.isClosed() || this.socket.isInputShutdown() || this.socket.isOutputShutdown()) {
            return false;
        }
        Http2Connection http2Connection2 = this.http2Connection;
        if (http2Connection2 != null) {
            return !http2Connection2.isShutdown();
        }
        if (doExtensiveChecks) {
            try {
                readTimeout = this.socket.getSoTimeout();
                this.socket.setSoTimeout(1);
                if (this.source.exhausted()) {
                    this.socket.setSoTimeout(readTimeout);
                    return false;
                }
                this.socket.setSoTimeout(readTimeout);
                return true;
            } catch (SocketTimeoutException e) {
            } catch (IOException e2) {
                return false;
            } catch (Throwable th) {
                this.socket.setSoTimeout(readTimeout);
                throw th;
            }
        }
        return true;
    }

    public void onStream(Http2Stream stream) throws IOException {
        stream.close(ErrorCode.REFUSED_STREAM);
    }

    public void onSettings(Http2Connection connection) {
        synchronized (this.connectionPool) {
            this.allocationLimit = connection.maxConcurrentStreams();
        }
    }

    public Handshake handshake() {
        return this.handshake;
    }

    public boolean isMultiplexed() {
        return this.http2Connection != null;
    }

    public Protocol protocol() {
        return this.protocol;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Connection{");
        sb.append(this.route.address().url().host());
        sb.append(":");
        sb.append(this.route.address().url().port());
        sb.append(", proxy=");
        sb.append(this.route.proxy());
        sb.append(" hostAddress=");
        sb.append(this.route.socketAddress());
        sb.append(" cipherSuite=");
        Handshake handshake2 = this.handshake;
        sb.append(handshake2 != null ? handshake2.cipherSuite() : PrivacyItem.SUBSCRIPTION_NONE);
        sb.append(" protocol=");
        sb.append(this.protocol);
        sb.append('}');
        return sb.toString();
    }
}
