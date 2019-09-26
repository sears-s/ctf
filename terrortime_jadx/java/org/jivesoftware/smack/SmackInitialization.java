package org.jivesoftware.smack;

import android.support.v4.os.EnvironmentCompat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.compression.Java7ZlibInputOutputStream;
import org.jivesoftware.smack.initializer.SmackInitializer;
import org.jivesoftware.smack.packet.Bind;
import org.jivesoftware.smack.provider.BindIQProvider;
import org.jivesoftware.smack.provider.BodyElementProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.sasl.core.SASLAnonymous;
import org.jivesoftware.smack.sasl.core.SASLXOauth2Mechanism;
import org.jivesoftware.smack.sasl.core.SCRAMSHA1Mechanism;
import org.jivesoftware.smack.sasl.core.ScramSha1PlusMechanism;
import org.jivesoftware.smack.util.FileUtils;
import org.jivesoftware.smack.util.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public final class SmackInitialization {
    private static final String DEFAULT_CONFIG_FILE = "org.jivesoftware.smack/smack-config.xml";
    private static final Logger LOGGER = Logger.getLogger(SmackInitialization.class.getName());
    static final String SMACK_VERSION;

    static {
        String smackVersion;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(FileUtils.getStreamForClasspathFile("org.jivesoftware.smack/version", null), StringUtils.UTF8));
            smackVersion = reader.readLine();
            try {
                reader.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "IOException closing stream", e);
            }
        } catch (Exception e2) {
            LOGGER.log(Level.SEVERE, "Could not determine Smack version", e2);
            smackVersion = EnvironmentCompat.MEDIA_UNKNOWN;
        }
        SMACK_VERSION = smackVersion;
        String disabledClasses = System.getProperty("smack.disabledClasses");
        if (disabledClasses != null) {
            for (String s : disabledClasses.split(",")) {
                SmackConfiguration.disabledSmackClasses.add(s);
            }
        }
        try {
            try {
                processConfigFile(FileUtils.getStreamForClasspathFile(DEFAULT_CONFIG_FILE, null), null);
                SmackConfiguration.compressionHandlers.add(new Java7ZlibInputOutputStream());
                try {
                    if (Boolean.getBoolean("smack.debugEnabled")) {
                        SmackConfiguration.DEBUG = true;
                    }
                } catch (Exception e3) {
                    LOGGER.log(Level.FINE, "Could not handle debugEnable property on Smack initialization", e3);
                }
                SASLAuthentication.registerSASLMechanism(new SCRAMSHA1Mechanism());
                SASLAuthentication.registerSASLMechanism(new ScramSha1PlusMechanism());
                SASLAuthentication.registerSASLMechanism(new SASLXOauth2Mechanism());
                SASLAuthentication.registerSASLMechanism(new SASLAnonymous());
                ProviderManager.addIQProvider(Bind.ELEMENT, Bind.NAMESPACE, new BindIQProvider());
                ProviderManager.addExtensionProvider("body", "jabber:client", new BodyElementProvider());
                SmackConfiguration.smackInitialized = true;
            } catch (Exception e4) {
                throw new IllegalStateException("Could not parse Smack configuration file", e4);
            }
        } catch (Exception e5) {
            throw new IllegalStateException("Could not load Smack configuration file", e5);
        }
    }

    public static void processConfigFile(InputStream cfgFileStream, Collection<Exception> exceptions) throws Exception {
        processConfigFile(cfgFileStream, exceptions, SmackInitialization.class.getClassLoader());
    }

    public static void processConfigFile(InputStream cfgFileStream, Collection<Exception> exceptions, ClassLoader classLoader) throws Exception {
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
        parser.setInput(cfgFileStream, StringUtils.UTF8);
        int eventType = parser.getEventType();
        do {
            if (eventType == 2) {
                if (parser.getName().equals("startupClasses")) {
                    parseClassesToLoad(parser, false, exceptions, classLoader);
                } else if (parser.getName().equals("optionalStartupClasses")) {
                    parseClassesToLoad(parser, true, exceptions, classLoader);
                }
            }
            eventType = parser.next();
        } while (eventType != 1);
        try {
            cfgFileStream.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while closing config file input stream", e);
        }
    }

    private static void parseClassesToLoad(XmlPullParser parser, boolean optional, Collection<Exception> exceptions, ClassLoader classLoader) throws Exception {
        String startName = parser.getName();
        while (true) {
            int eventType = parser.next();
            String name = parser.getName();
            if (eventType == 2 && "className".equals(name)) {
                String classToLoad = parser.nextText();
                if (!SmackConfiguration.isDisabledSmackClass(classToLoad)) {
                    try {
                        loadSmackClass(classToLoad, optional, classLoader);
                    } catch (Exception e) {
                        if (exceptions != null) {
                            exceptions.add(e);
                        } else {
                            throw e;
                        }
                    }
                }
            }
            if (eventType == 3 && startName.equals(name)) {
                return;
            }
        }
    }

    private static void loadSmackClass(String className, boolean optional, ClassLoader classLoader) throws Exception {
        Level logLevel;
        try {
            Class<?> initClass = Class.forName(className, true, classLoader);
            if (SmackInitializer.class.isAssignableFrom(initClass)) {
                List<Exception> exceptions = ((SmackInitializer) initClass.getConstructor(new Class[0]).newInstance(new Object[0])).initialize();
                if (exceptions == null || exceptions.size() == 0) {
                    Logger logger = LOGGER;
                    Level level = Level.FINE;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Loaded SmackInitializer ");
                    sb.append(className);
                    logger.log(level, sb.toString());
                } else {
                    for (Exception e : exceptions) {
                        LOGGER.log(Level.SEVERE, "Exception in loadSmackClass", e);
                    }
                }
            } else {
                Logger logger2 = LOGGER;
                Level level2 = Level.FINE;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Loaded ");
                sb2.append(className);
                logger2.log(level2, sb2.toString());
            }
        } catch (ClassNotFoundException cnfe) {
            if (optional) {
                logLevel = Level.FINE;
            } else {
                logLevel = Level.WARNING;
            }
            Logger logger3 = LOGGER;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("A startup class '");
            sb3.append(className);
            sb3.append("' could not be loaded.");
            logger3.log(logLevel, sb3.toString());
            if (!optional) {
                throw cnfe;
            }
        }
    }
}
