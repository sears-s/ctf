package org.jivesoftware.smack.debugger;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;

public final class ReflectionDebuggerFactory implements SmackDebuggerFactory {
    private static final String DEBUGGER_CLASS_PROPERTY_NAME = "smack.debuggerClass";
    private static final String[] DEFAULT_DEBUGGERS = {"org.jivesoftware.smackx.debugger.EnhancedDebugger", "org.jivesoftware.smackx.debugger.android.AndroidDebugger", "org.jivesoftware.smack.debugger.ConsoleDebugger", "org.jivesoftware.smack.debugger.LiteDebugger", "org.jivesoftware.smack.debugger.JulDebugger"};
    public static final ReflectionDebuggerFactory INSTANCE = new ReflectionDebuggerFactory();
    private static final Logger LOGGER = Logger.getLogger(ReflectionDebuggerFactory.class.getName());

    private ReflectionDebuggerFactory() {
    }

    public static void setDebuggerClass(Class<? extends SmackDebugger> debuggerClass) {
        String str = DEBUGGER_CLASS_PROPERTY_NAME;
        if (debuggerClass == null) {
            System.clearProperty(str);
        } else {
            System.setProperty(str, debuggerClass.getCanonicalName());
        }
    }

    public static Class<SmackDebugger> getDebuggerClass() {
        String customDebuggerClassName = getCustomDebuggerClassName();
        if (customDebuggerClassName == null) {
            return getOneOfDefaultDebuggerClasses();
        }
        try {
            return Class.forName(customDebuggerClassName);
        } catch (Exception e) {
            Logger logger = LOGGER;
            Level level = Level.WARNING;
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to instantiate debugger class ");
            sb.append(customDebuggerClassName);
            logger.log(level, sb.toString(), e);
            return null;
        }
    }

    public SmackDebugger create(XMPPConnection connection) throws IllegalArgumentException {
        Class<SmackDebugger> debuggerClass = getDebuggerClass();
        if (debuggerClass == null) {
            return null;
        }
        try {
            return (SmackDebugger) debuggerClass.getConstructor(new Class[]{XMPPConnection.class}).newInstance(new Object[]{connection});
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't initialize the configured debugger!", e);
        }
    }

    private static String getCustomDebuggerClassName() {
        try {
            return System.getProperty(DEBUGGER_CLASS_PROPERTY_NAME);
        } catch (Throwable th) {
            return null;
        }
    }

    private static Class<SmackDebugger> getOneOfDefaultDebuggerClasses() {
        String[] strArr;
        for (String debugger : DEFAULT_DEBUGGERS) {
            if (!SmackConfiguration.isDisabledSmackClass(debugger)) {
                try {
                    return Class.forName(debugger);
                } catch (ClassNotFoundException e) {
                    Logger logger = LOGGER;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Did not find debugger class '");
                    sb.append(debugger);
                    sb.append("'");
                    logger.fine(sb.toString());
                } catch (ClassCastException e2) {
                    LOGGER.warning("Found debugger class that does not appears to implement SmackDebugger interface");
                } catch (Exception e3) {
                    LOGGER.warning("Unable to instantiate either Smack debugger class");
                }
            }
        }
        return null;
    }
}
