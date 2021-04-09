package org.minidns.source;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.minidns.MiniDnsException.IdMismatch;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsqueryresult.DnsQueryResult.QueryMethod;
import org.minidns.dnsqueryresult.StandardDnsQueryResult;
import org.minidns.source.AbstractDnsDataSource.QueryMode;
import org.minidns.util.MultipleIoException;

public class NetworkDataSource extends AbstractDnsDataSource {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    protected static final Logger LOGGER = Logger.getLogger(NetworkDataSource.class.getName());

    /* renamed from: org.minidns.source.NetworkDataSource$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$minidns$source$AbstractDnsDataSource$QueryMode = new int[QueryMode.values().length];

        static {
            try {
                $SwitchMap$org$minidns$source$AbstractDnsDataSource$QueryMode[QueryMode.dontCare.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$minidns$source$AbstractDnsDataSource$QueryMode[QueryMode.udpTcp.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$minidns$source$AbstractDnsDataSource$QueryMode[QueryMode.tcp.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public StandardDnsQueryResult query(DnsMessage message, InetAddress address, int port) throws IOException {
        boolean doUdpFirst;
        Object obj;
        QueryMode queryMode = getQueryMode();
        int i = AnonymousClass1.$SwitchMap$org$minidns$source$AbstractDnsDataSource$QueryMode[queryMode.ordinal()];
        if (i == 1 || i == 2) {
            doUdpFirst = true;
        } else if (i == 3) {
            doUdpFirst = false;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Unsupported query mode: ");
            sb.append(queryMode);
            throw new IllegalStateException(sb.toString());
        }
        List<IOException> ioExceptions = new ArrayList<>(2);
        DnsMessage dnsMessage = null;
        if (doUdpFirst) {
            try {
                dnsMessage = queryUdp(message, address, port);
            } catch (IOException e) {
                ioExceptions.add(e);
            }
            if (dnsMessage == null || dnsMessage.truncated) {
                Logger logger = LOGGER;
                Level level = Level.FINE;
                Object[] objArr = new Object[1];
                if (dnsMessage != null) {
                    obj = "response is truncated";
                } else {
                    obj = (Serializable) ioExceptions.get(0);
                }
                objArr[0] = obj;
                logger.log(level, "Fallback to TCP because {0}", objArr);
            } else {
                StandardDnsQueryResult standardDnsQueryResult = new StandardDnsQueryResult(address, port, QueryMethod.udp, message, dnsMessage);
                return standardDnsQueryResult;
            }
        }
        try {
            dnsMessage = queryTcp(message, address, port);
        } catch (IOException e2) {
            ioExceptions.add(e2);
            MultipleIoException.throwIfRequired(ioExceptions);
        }
        StandardDnsQueryResult standardDnsQueryResult2 = new StandardDnsQueryResult(address, port, QueryMethod.tcp, message, dnsMessage);
        return standardDnsQueryResult2;
    }

    /* access modifiers changed from: protected */
    public DnsMessage queryUdp(DnsMessage message, InetAddress address, int port) throws IOException {
        DatagramSocket socket = null;
        DatagramPacket packet = message.asDatagram(address, port);
        byte[] buffer = new byte[this.udpPayloadSize];
        try {
            socket = createDatagramSocket();
            socket.setSoTimeout(this.timeout);
            socket.send(packet);
            DatagramPacket packet2 = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet2);
            DnsMessage dnsMessage = new DnsMessage(packet2.getData());
            if (dnsMessage.id == message.id) {
                return dnsMessage;
            }
            throw new IdMismatch(message, dnsMessage);
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    /* access modifiers changed from: protected */
    public DnsMessage queryTcp(DnsMessage message, InetAddress address, int port) throws IOException {
        Socket socket = null;
        try {
            socket = createSocket();
            socket.connect(new InetSocketAddress(address, port), this.timeout);
            socket.setSoTimeout(this.timeout);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            message.writeTo(dos);
            dos.flush();
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            int length = dis.readUnsignedShort();
            byte[] data = new byte[length];
            for (int read = 0; read < length; read += dis.read(data, read, length - read)) {
            }
            DnsMessage dnsMessage = new DnsMessage(data);
            if (dnsMessage.id == message.id) {
                return dnsMessage;
            }
            throw new IdMismatch(message, dnsMessage);
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    /* access modifiers changed from: protected */
    public Socket createSocket() {
        return new Socket();
    }

    /* access modifiers changed from: protected */
    public DatagramSocket createDatagramSocket() throws SocketException {
        return new DatagramSocket();
    }
}
