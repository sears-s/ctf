package org.jivesoftware.smackx.bytestreams.socks5;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.util.StringUtils;

public final class Socks5Proxy {
    private static final Logger LOGGER = Logger.getLogger(Socks5Proxy.class.getName());
    private static boolean localSocks5ProxyEnabled = true;
    private static int localSocks5ProxyPort = -7777;
    private static Socks5Proxy socks5Server;
    /* access modifiers changed from: private */
    public final List<String> allowedConnections = Collections.synchronizedList(new LinkedList());
    /* access modifiers changed from: private */
    public final Map<String, Socket> connectionMap = new ConcurrentHashMap();
    private final Set<String> localAddresses = new LinkedHashSet(4);
    private Socks5ServerProcess serverProcess = new Socks5ServerProcess();
    /* access modifiers changed from: private */
    public ServerSocket serverSocket;
    private Thread serverThread;

    private class Socks5ServerProcess implements Runnable {
        private Socks5ServerProcess() {
        }

        public void run() {
            while (true) {
                Socket socket = null;
                try {
                    if (Socks5Proxy.this.serverSocket != null && !Socks5Proxy.this.serverSocket.isClosed()) {
                        if (!Thread.currentThread().isInterrupted()) {
                            establishConnection(Socks5Proxy.this.serverSocket.accept());
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                } catch (SocketException e) {
                } catch (Exception e2) {
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e3) {
                        }
                    }
                }
            }
        }

        private void establishConnection(Socket socket) throws SmackException, IOException {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            if (in.read() == 5) {
                byte[] auth = new byte[in.read()];
                in.readFully(auth);
                byte[] authMethodSelectionResponse = new byte[2];
                authMethodSelectionResponse[0] = 5;
                boolean noAuthMethodFound = false;
                int i = 0;
                while (true) {
                    if (i >= auth.length) {
                        break;
                    } else if (auth[i] == 0) {
                        noAuthMethodFound = true;
                        break;
                    } else {
                        i++;
                    }
                }
                if (noAuthMethodFound) {
                    authMethodSelectionResponse[1] = 0;
                    out.write(authMethodSelectionResponse);
                    out.flush();
                    byte[] connectionRequest = Socks5Utils.receiveSocks5Message(in);
                    String responseDigest = new String(connectionRequest, 5, connectionRequest[4], StringUtils.UTF8);
                    if (Socks5Proxy.this.allowedConnections.contains(responseDigest)) {
                        connectionRequest[1] = 0;
                        out.write(connectionRequest);
                        out.flush();
                        Socks5Proxy.this.connectionMap.put(responseDigest, socket);
                        return;
                    }
                    connectionRequest[1] = 5;
                    out.write(connectionRequest);
                    out.flush();
                    throw new SmackException("Connection is not allowed");
                }
                authMethodSelectionResponse[1] = -1;
                out.write(authMethodSelectionResponse);
                out.flush();
                throw new SmackException("Authentication method not supported");
            }
            throw new SmackException("Only SOCKS5 supported");
        }
    }

    private Socks5Proxy() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            Set<String> localHostAddresses = new HashSet<>();
            Iterator it = Collections.list(networkInterfaces).iterator();
            while (it.hasNext()) {
                Iterator it2 = Collections.list(((NetworkInterface) it.next()).getInetAddresses()).iterator();
                while (it2.hasNext()) {
                    localHostAddresses.add(((InetAddress) it2.next()).getHostAddress());
                }
            }
            if (!localHostAddresses.isEmpty()) {
                replaceLocalAddresses(localHostAddresses);
                return;
            }
            throw new IllegalStateException("Could not determine any local host address");
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean isLocalSocks5ProxyEnabled() {
        return localSocks5ProxyEnabled;
    }

    public static void setLocalSocks5ProxyEnabled(boolean localSocks5ProxyEnabled2) {
        localSocks5ProxyEnabled = localSocks5ProxyEnabled2;
    }

    public static int getLocalSocks5ProxyPort() {
        return localSocks5ProxyPort;
    }

    public static void setLocalSocks5ProxyPort(int localSocks5ProxyPort2) {
        if (Math.abs(localSocks5ProxyPort2) <= 65535) {
            localSocks5ProxyPort = localSocks5ProxyPort2;
            return;
        }
        throw new IllegalArgumentException("localSocks5ProxyPort must be within (-65535,65535)");
    }

    public static synchronized Socks5Proxy getSocks5Proxy() {
        Socks5Proxy socks5Proxy;
        synchronized (Socks5Proxy.class) {
            if (socks5Server == null) {
                socks5Server = new Socks5Proxy();
            }
            if (isLocalSocks5ProxyEnabled()) {
                socks5Server.start();
            }
            socks5Proxy = socks5Server;
        }
        return socks5Proxy;
    }

    public synchronized void start() {
        if (!isRunning()) {
            try {
                if (getLocalSocks5ProxyPort() < 0) {
                    int port = Math.abs(getLocalSocks5ProxyPort());
                    int i = 0;
                    while (i < 65535 - port) {
                        try {
                            this.serverSocket = new ServerSocket(port + i);
                            break;
                        } catch (IOException e) {
                            i++;
                        }
                    }
                } else {
                    this.serverSocket = new ServerSocket(getLocalSocks5ProxyPort());
                }
                if (this.serverSocket != null) {
                    this.serverThread = new Thread(this.serverProcess);
                    this.serverThread.setName("Smack Local SOCKS5 Proxy");
                    this.serverThread.setDaemon(true);
                    this.serverThread.start();
                }
            } catch (IOException e2) {
                Logger logger = LOGGER;
                Level level = Level.SEVERE;
                StringBuilder sb = new StringBuilder();
                sb.append("couldn't setup local SOCKS5 proxy on port ");
                sb.append(getLocalSocks5ProxyPort());
                logger.log(level, sb.toString(), e2);
            }
        } else {
            return;
        }
        return;
    }

    public synchronized void stop() {
        if (isRunning()) {
            try {
                this.serverSocket.close();
            } catch (IOException e) {
            }
            if (this.serverThread != null && this.serverThread.isAlive()) {
                try {
                    this.serverThread.interrupt();
                    this.serverThread.join();
                } catch (InterruptedException e2) {
                }
            }
            this.serverThread = null;
            this.serverSocket = null;
        }
    }

    public void addLocalAddress(String address) {
        if (address != null) {
            synchronized (this.localAddresses) {
                this.localAddresses.add(address);
            }
        }
    }

    public boolean removeLocalAddress(String address) {
        boolean remove;
        synchronized (this.localAddresses) {
            remove = this.localAddresses.remove(address);
        }
        return remove;
    }

    public List<String> getLocalAddresses() {
        LinkedList linkedList;
        synchronized (this.localAddresses) {
            linkedList = new LinkedList(this.localAddresses);
        }
        return linkedList;
    }

    public void replaceLocalAddresses(Collection<String> addresses) {
        if (addresses != null) {
            synchronized (this.localAddresses) {
                this.localAddresses.clear();
                this.localAddresses.addAll(addresses);
            }
            return;
        }
        throw new IllegalArgumentException("list must not be null");
    }

    public int getPort() {
        if (!isRunning()) {
            return -1;
        }
        return this.serverSocket.getLocalPort();
    }

    /* access modifiers changed from: protected */
    public Socket getSocket(String digest) {
        return (Socket) this.connectionMap.get(digest);
    }

    public void addTransfer(String digest) {
        this.allowedConnections.add(digest);
    }

    /* access modifiers changed from: protected */
    public void removeTransfer(String digest) {
        this.allowedConnections.remove(digest);
        this.connectionMap.remove(digest);
    }

    public boolean isRunning() {
        return this.serverSocket != null;
    }
}
