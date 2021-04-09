package org.jivesoftware.smackx.bytestreams.socks5;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.StreamHost;

public class Socks5Client {
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(Socks5Client.class.getName());
    protected String digest;
    protected StreamHost streamHost;

    public Socks5Client(StreamHost streamHost2, String digest2) {
        this.streamHost = streamHost2;
        this.digest = digest2;
    }

    public Socket getSocket(int timeout) throws IOException, InterruptedException, TimeoutException, SmackException, XMPPException {
        FutureTask<Socket> futureTask = new FutureTask<>(new Callable<Socket>() {
            public Socket call() throws IOException, SmackException {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(Socks5Client.this.streamHost.getAddress(), Socks5Client.this.streamHost.getPort()));
                try {
                    Socks5Client.this.establish(socket);
                    return socket;
                } catch (SmackException e) {
                    if (!socket.isClosed()) {
                        try {
                            socket.close();
                        } catch (IOException e2) {
                            Socks5Client.LOGGER.log(Level.WARNING, "Could not close SOCKS5 socket", e2);
                        }
                    }
                    throw e;
                }
            }
        });
        new Thread(futureTask).start();
        try {
            return (Socket) futureTask.get((long) timeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                if (cause instanceof IOException) {
                    throw ((IOException) cause);
                } else if (cause instanceof SmackException) {
                    throw ((SmackException) cause);
                }
            }
            throw new SmackException("Error while connecting to SOCKS5 proxy", e);
        }
    }

    /* access modifiers changed from: protected */
    public void establish(Socket socket) throws SmackException, IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.write(new byte[]{5, 1, 0});
        out.flush();
        byte[] response = new byte[2];
        in.readFully(response);
        if (response[0] == 5 && response[1] == 0) {
            byte[] connectionRequest = createSocks5ConnectRequest();
            out.write(connectionRequest);
            out.flush();
            byte[] connectionResponse = Socks5Utils.receiveSocks5Message(in);
            connectionRequest[1] = 0;
            if (!Arrays.equals(connectionRequest, connectionResponse)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Connection request does not equal connection response. Response: ");
                sb.append(Arrays.toString(connectionResponse));
                sb.append(". Request: ");
                sb.append(Arrays.toString(connectionRequest));
                throw new SmackException(sb.toString());
            }
            return;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Remote SOCKS5 server responded with unexpected version: ");
        sb2.append(response[0]);
        sb2.append(' ');
        sb2.append(response[1]);
        sb2.append(". Should be 0x05 0x00.");
        throw new SmackException(sb2.toString());
    }

    private byte[] createSocks5ConnectRequest() {
        try {
            byte[] addr = this.digest.getBytes(StringUtils.UTF8);
            byte[] data = new byte[(addr.length + 7)];
            data[0] = 5;
            data[1] = 1;
            data[2] = 0;
            data[3] = 3;
            data[4] = (byte) addr.length;
            System.arraycopy(addr, 0, data, 5, addr.length);
            data[data.length - 2] = 0;
            data[data.length - 1] = 0;
            return data;
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }
}
