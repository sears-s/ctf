package org.jivesoftware.smackx.jingle.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JingleContentProviderManager {
    private static final Map<String, JingleContentDescriptionProvider<?>> jingleContentDescriptionProviders = new ConcurrentHashMap();
    private static final Map<String, JingleContentTransportProvider<?>> jingleContentTransportProviders = new ConcurrentHashMap();

    public static JingleContentDescriptionProvider<?> addJingleContentDescriptionProvider(String namespace, JingleContentDescriptionProvider<?> provider) {
        return (JingleContentDescriptionProvider) jingleContentDescriptionProviders.put(namespace, provider);
    }

    public static JingleContentDescriptionProvider<?> getJingleContentDescriptionProvider(String namespace) {
        return (JingleContentDescriptionProvider) jingleContentDescriptionProviders.get(namespace);
    }

    public static JingleContentTransportProvider<?> addJingleContentTransportProvider(String namespace, JingleContentTransportProvider<?> provider) {
        return (JingleContentTransportProvider) jingleContentTransportProviders.put(namespace, provider);
    }

    public static JingleContentTransportProvider<?> getJingleContentTransportProvider(String namespace) {
        return (JingleContentTransportProvider) jingleContentTransportProviders.get(namespace);
    }
}
