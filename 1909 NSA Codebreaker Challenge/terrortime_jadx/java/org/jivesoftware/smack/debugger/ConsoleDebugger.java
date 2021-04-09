package org.jivesoftware.smack.debugger;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jivesoftware.smack.XMPPConnection;

public class ConsoleDebugger extends AbstractDebugger {
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");

    public static final class Factory implements SmackDebuggerFactory {
        public static final SmackDebuggerFactory INSTANCE = new Factory();

        private Factory() {
        }

        public SmackDebugger create(XMPPConnection connection) throws IllegalArgumentException {
            return new ConsoleDebugger(connection);
        }
    }

    public ConsoleDebugger(XMPPConnection connection) {
        super(connection);
    }

    /* access modifiers changed from: protected */
    public void log(String logMessage) {
        String formatedDate;
        synchronized (this.dateFormatter) {
            formatedDate = this.dateFormatter.format(new Date());
        }
        PrintStream printStream = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append(formatedDate);
        sb.append(' ');
        sb.append(logMessage);
        printStream.println(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void log(String logMessage, Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        StringBuilder sb = new StringBuilder();
        sb.append(logMessage);
        sb.append(sw);
        log(sb.toString());
    }
}
