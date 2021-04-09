package org.jivesoftware.smack.initializer;

import java.io.InputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.SmackInitialization;
import org.jivesoftware.smack.provider.ProviderFileLoader;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.FileUtils;

public abstract class UrlInitializer implements SmackInitializer {
    private static final Logger LOGGER = Logger.getLogger(UrlInitializer.class.getName());

    public List<Exception> initialize() {
        ClassLoader classLoader = getClass().getClassLoader();
        List<Exception> exceptions = new LinkedList<>();
        String providerUriString = getProvidersUri();
        if (providerUriString != null) {
            try {
                URI providerUri = URI.create(providerUriString);
                InputStream is = FileUtils.getStreamForUri(providerUri, classLoader);
                Logger logger = LOGGER;
                Level level = Level.FINE;
                StringBuilder sb = new StringBuilder();
                sb.append("Loading providers for providerUri [");
                sb.append(providerUri);
                sb.append("]");
                logger.log(level, sb.toString());
                ProviderFileLoader pfl = new ProviderFileLoader(is, classLoader);
                ProviderManager.addLoader(pfl);
                exceptions.addAll(pfl.getLoadingExceptions());
            } catch (Exception e) {
                Logger logger2 = LOGGER;
                Level level2 = Level.SEVERE;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Error trying to load provider file ");
                sb2.append(providerUriString);
                logger2.log(level2, sb2.toString(), e);
                exceptions.add(e);
            }
        }
        String configUriString = getConfigUri();
        if (configUriString != null) {
            try {
                SmackInitialization.processConfigFile(FileUtils.getStreamForUri(URI.create(configUriString), classLoader), exceptions, classLoader);
            } catch (Exception e2) {
                exceptions.add(e2);
            }
        }
        return exceptions;
    }

    /* access modifiers changed from: protected */
    public String getProvidersUri() {
        return null;
    }

    /* access modifiers changed from: protected */
    public String getConfigUri() {
        return null;
    }
}
