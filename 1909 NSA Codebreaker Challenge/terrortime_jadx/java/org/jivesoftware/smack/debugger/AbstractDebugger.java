package org.jivesoftware.smack.debugger;

import com.badguy.terrortime.BuildConfig;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Logger;
import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.TopLevelStreamElement;
import org.jivesoftware.smack.util.ObservableReader;
import org.jivesoftware.smack.util.ObservableWriter;
import org.jivesoftware.smack.util.ReaderListener;
import org.jivesoftware.smack.util.WriterListener;
import org.jxmpp.jid.EntityFullJid;

public abstract class AbstractDebugger extends SmackDebugger {
    private static final Logger LOGGER = Logger.getLogger(AbstractDebugger.class.getName());
    public static boolean printInterpreted = false;
    private final ConnectionListener connListener;
    private ObservableReader reader = new ObservableReader(this.reader);
    private final ReaderListener readerListener;
    private final ReconnectionListener reconnectionListener;
    private ObservableWriter writer;
    private final WriterListener writerListener;

    /* access modifiers changed from: protected */
    public abstract void log(String str);

    /* access modifiers changed from: protected */
    public abstract void log(String str, Throwable th);

    public AbstractDebugger(final XMPPConnection connection) {
        super(connection);
        this.readerListener = new ReaderListener() {
            public void read(String str) {
                AbstractDebugger abstractDebugger = AbstractDebugger.this;
                StringBuilder sb = new StringBuilder();
                sb.append("RECV (");
                sb.append(connection.getConnectionCounter());
                sb.append("): ");
                sb.append(str);
                abstractDebugger.log(sb.toString());
            }
        };
        this.reader.addReaderListener(this.readerListener);
        this.writer = new ObservableWriter(this.writer);
        this.writerListener = new WriterListener() {
            public void write(String str) {
                AbstractDebugger abstractDebugger = AbstractDebugger.this;
                StringBuilder sb = new StringBuilder();
                sb.append("SENT (");
                sb.append(connection.getConnectionCounter());
                sb.append("): ");
                sb.append(str);
                abstractDebugger.log(sb.toString());
            }
        };
        this.writer.addWriterListener(this.writerListener);
        this.connListener = new AbstractConnectionListener() {
            public void connected(XMPPConnection connection) {
                AbstractDebugger abstractDebugger = AbstractDebugger.this;
                StringBuilder sb = new StringBuilder();
                sb.append("XMPPConnection connected (");
                sb.append(connection);
                sb.append(")");
                abstractDebugger.log(sb.toString());
            }

            public void authenticated(XMPPConnection connection, boolean resumed) {
                StringBuilder sb = new StringBuilder();
                sb.append("XMPPConnection authenticated (");
                sb.append(connection);
                sb.append(")");
                String logString = sb.toString();
                if (resumed) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(logString);
                    sb2.append(" and resumed");
                    logString = sb2.toString();
                }
                AbstractDebugger.this.log(logString);
            }

            public void connectionClosed() {
                AbstractDebugger abstractDebugger = AbstractDebugger.this;
                StringBuilder sb = new StringBuilder();
                sb.append("XMPPConnection closed (");
                sb.append(connection);
                sb.append(")");
                abstractDebugger.log(sb.toString());
            }

            public void connectionClosedOnError(Exception e) {
                AbstractDebugger abstractDebugger = AbstractDebugger.this;
                StringBuilder sb = new StringBuilder();
                sb.append("XMPPConnection closed due to an exception (");
                sb.append(connection);
                sb.append(")");
                abstractDebugger.log(sb.toString(), e);
            }
        };
        this.reconnectionListener = new ReconnectionListener() {
            public void reconnectionFailed(Exception e) {
                AbstractDebugger abstractDebugger = AbstractDebugger.this;
                StringBuilder sb = new StringBuilder();
                sb.append("Reconnection failed due to an exception (");
                sb.append(connection);
                sb.append(")");
                abstractDebugger.log(sb.toString(), e);
            }

            public void reconnectingIn(int seconds) {
                AbstractDebugger abstractDebugger = AbstractDebugger.this;
                StringBuilder sb = new StringBuilder();
                sb.append("XMPPConnection (");
                sb.append(connection);
                sb.append(") will reconnect in ");
                sb.append(seconds);
                abstractDebugger.log(sb.toString());
            }
        };
        if (connection instanceof AbstractXMPPConnection) {
            ReconnectionManager.getInstanceFor((AbstractXMPPConnection) connection).addReconnectionListener(this.reconnectionListener);
            return;
        }
        Logger logger = LOGGER;
        StringBuilder sb = new StringBuilder();
        sb.append("The connection instance ");
        sb.append(connection);
        sb.append(" is not an instance of AbstractXMPPConnection, thus we can not install the ReconnectionListener");
        logger.info(sb.toString());
    }

    public Reader newConnectionReader(Reader newReader) {
        this.reader.removeReaderListener(this.readerListener);
        ObservableReader debugReader = new ObservableReader(newReader);
        debugReader.addReaderListener(this.readerListener);
        this.reader = debugReader;
        return this.reader;
    }

    public Writer newConnectionWriter(Writer newWriter) {
        this.writer.removeWriterListener(this.writerListener);
        ObservableWriter debugWriter = new ObservableWriter(newWriter);
        debugWriter.addWriterListener(this.writerListener);
        this.writer = debugWriter;
        return this.writer;
    }

    public void userHasLogged(EntityFullJid user) {
        String localpart = user.getLocalpart().toString();
        String str = BuildConfig.FLAVOR;
        boolean isAnonymous = str.equals(localpart);
        StringBuilder sb = new StringBuilder();
        sb.append("User logged (");
        sb.append(this.connection.getConnectionCounter());
        sb.append("): ");
        if (!isAnonymous) {
            str = localpart;
        }
        sb.append(str);
        sb.append("@");
        sb.append(this.connection.getXMPPServiceDomain());
        sb.append(":");
        sb.append(this.connection.getPort());
        String title = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(title);
        sb2.append("/");
        sb2.append(user.getResourcepart());
        log(sb2.toString());
        this.connection.addConnectionListener(this.connListener);
    }

    public void onIncomingStreamElement(TopLevelStreamElement streamElement) {
        if (printInterpreted) {
            StringBuilder sb = new StringBuilder();
            sb.append("RCV PKT (");
            sb.append(this.connection.getConnectionCounter());
            sb.append("): ");
            sb.append(streamElement.toXML(null));
            log(sb.toString());
        }
    }

    public void onOutgoingStreamElement(TopLevelStreamElement streamElement) {
    }
}
