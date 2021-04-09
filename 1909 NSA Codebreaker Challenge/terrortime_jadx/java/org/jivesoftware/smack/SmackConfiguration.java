package org.jivesoftware.smack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import org.jivesoftware.smack.compression.XMPPInputOutputStream;
import org.jivesoftware.smack.debugger.ReflectionDebuggerFactory;
import org.jivesoftware.smack.debugger.SmackDebuggerFactory;
import org.jivesoftware.smack.parsing.ExceptionThrowingCallbackWithHint;
import org.jivesoftware.smack.parsing.ParsingExceptionCallback;
import org.jivesoftware.smack.util.Objects;

public final class SmackConfiguration {
    public static boolean DEBUG = false;
    private static SmackDebuggerFactory DEFAULT_DEBUGGER_FACTORY = ReflectionDebuggerFactory.INSTANCE;
    static final List<XMPPInputOutputStream> compressionHandlers = new ArrayList(2);
    private static ParsingExceptionCallback defaultCallback = new ExceptionThrowingCallbackWithHint();
    private static final int defaultConcurrencyLevelLimit;
    private static HostnameVerifier defaultHostnameVerififer;
    private static List<String> defaultMechs = new ArrayList();
    private static int defaultPacketReplyTimeout = 5000;
    static Set<String> disabledSmackClasses = new HashSet();
    private static int packetCollectorSize = 5000;
    static boolean smackInitialized = false;
    private static UnknownIqRequestReplyMode unknownIqRequestReplyMode = UnknownIqRequestReplyMode.replyFeatureNotImplemented;

    public enum UnknownIqRequestReplyMode {
        doNotReply,
        replyFeatureNotImplemented,
        replyServiceUnavailable
    }

    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        if (availableProcessors < 8) {
            defaultConcurrencyLevelLimit = 8;
        } else {
            defaultConcurrencyLevelLimit = (int) (((double) availableProcessors) * 1.1d);
        }
    }

    public static String getVersion() {
        return SmackInitialization.SMACK_VERSION;
    }

    @Deprecated
    public static int getDefaultPacketReplyTimeout() {
        return getDefaultReplyTimeout();
    }

    @Deprecated
    public static void setDefaultPacketReplyTimeout(int timeout) {
        setDefaultReplyTimeout(timeout);
    }

    public static int getDefaultReplyTimeout() {
        if (defaultPacketReplyTimeout <= 0) {
            defaultPacketReplyTimeout = 5000;
        }
        return defaultPacketReplyTimeout;
    }

    public static void setDefaultReplyTimeout(int timeout) {
        if (timeout > 0) {
            defaultPacketReplyTimeout = timeout;
            return;
        }
        throw new IllegalArgumentException();
    }

    public static void setDefaultSmackDebuggerFactory(SmackDebuggerFactory debuggerFactory) {
        DEFAULT_DEBUGGER_FACTORY = (SmackDebuggerFactory) Objects.requireNonNull(debuggerFactory, "Debugger factory must not be null");
    }

    public static SmackDebuggerFactory getDefaultSmackDebuggerFactory() {
        return DEFAULT_DEBUGGER_FACTORY;
    }

    public static int getStanzaCollectorSize() {
        return packetCollectorSize;
    }

    public static void setStanzaCollectorSize(int collectorSize) {
        packetCollectorSize = collectorSize;
    }

    public static void addSaslMech(String mech) {
        if (!defaultMechs.contains(mech)) {
            defaultMechs.add(mech);
        }
    }

    public static void addSaslMechs(Collection<String> mechs) {
        for (String mech : mechs) {
            addSaslMech(mech);
        }
    }

    public static void removeSaslMech(String mech) {
        defaultMechs.remove(mech);
    }

    public static void removeSaslMechs(Collection<String> mechs) {
        defaultMechs.removeAll(mechs);
    }

    public static List<String> getSaslMechs() {
        return Collections.unmodifiableList(defaultMechs);
    }

    public static void setDefaultParsingExceptionCallback(ParsingExceptionCallback callback) {
        defaultCallback = callback;
    }

    public static ParsingExceptionCallback getDefaultParsingExceptionCallback() {
        return defaultCallback;
    }

    public static void addCompressionHandler(XMPPInputOutputStream xmppInputOutputStream) {
        compressionHandlers.add(xmppInputOutputStream);
    }

    @Deprecated
    public static List<XMPPInputOutputStream> getCompresionHandlers() {
        return getCompressionHandlers();
    }

    public static List<XMPPInputOutputStream> getCompressionHandlers() {
        List<XMPPInputOutputStream> res = new ArrayList<>(compressionHandlers.size());
        for (XMPPInputOutputStream ios : compressionHandlers) {
            if (ios.isSupported()) {
                res.add(ios);
            }
        }
        return res;
    }

    public static void setDefaultHostnameVerifier(HostnameVerifier verifier) {
        defaultHostnameVerififer = verifier;
    }

    public static void addDisabledSmackClass(Class<?> clz) {
        addDisabledSmackClass(clz.getName());
    }

    public static void addDisabledSmackClass(String className) {
        disabledSmackClasses.add(className);
    }

    public static void addDisabledSmackClasses(String... classNames) {
        for (String className : classNames) {
            addDisabledSmackClass(className);
        }
    }

    public static boolean isDisabledSmackClass(String className) {
        for (String disabledClassOrPackage : disabledSmackClasses) {
            if (disabledClassOrPackage.equals(className)) {
                return true;
            }
            int lastDotIndex = disabledClassOrPackage.lastIndexOf(46);
            if (disabledClassOrPackage.length() > lastDotIndex && !Character.isUpperCase(disabledClassOrPackage.charAt(lastDotIndex + 1)) && className.startsWith(disabledClassOrPackage)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSmackInitialized() {
        return smackInitialized;
    }

    static HostnameVerifier getDefaultHostnameVerifier() {
        return defaultHostnameVerififer;
    }

    public static UnknownIqRequestReplyMode getUnknownIqRequestReplyMode() {
        return unknownIqRequestReplyMode;
    }

    public static void setUnknownIqRequestReplyMode(UnknownIqRequestReplyMode unknownIqRequestReplyMode2) {
        unknownIqRequestReplyMode = (UnknownIqRequestReplyMode) Objects.requireNonNull(unknownIqRequestReplyMode2, "Must set mode");
    }

    public static int getDefaultConcurrencyLevelLimit() {
        return defaultConcurrencyLevelLimit;
    }
}
