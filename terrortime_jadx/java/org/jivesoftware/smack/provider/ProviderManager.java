package org.jivesoftware.smack.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.StringUtils;
import org.jxmpp.util.XmppStringUtils;

public final class ProviderManager {
    private static final Map<String, ExtensionElementProvider<ExtensionElement>> extensionProviders = new ConcurrentHashMap();
    private static final Map<String, IQProvider<IQ>> iqProviders = new ConcurrentHashMap();
    private static final Map<String, ExtensionElementProvider<ExtensionElement>> streamFeatureProviders = new ConcurrentHashMap();

    static {
        SmackConfiguration.getVersion();
    }

    public static void addLoader(ProviderLoader loader) {
        if (loader.getIQProviderInfo() != null) {
            for (IQProviderInfo info : loader.getIQProviderInfo()) {
                addIQProvider(info.getElementName(), info.getNamespace(), info.getProvider());
            }
        }
        if (loader.getExtensionProviderInfo() != null) {
            for (ExtensionProviderInfo info2 : loader.getExtensionProviderInfo()) {
                addExtensionProvider(info2.getElementName(), info2.getNamespace(), info2.getProvider());
            }
        }
        if (loader.getStreamFeatureProviderInfo() != null) {
            for (StreamFeatureProviderInfo info3 : loader.getStreamFeatureProviderInfo()) {
                addStreamFeatureProvider(info3.getElementName(), info3.getNamespace(), (ExtensionElementProvider) info3.getProvider());
            }
        }
    }

    public static IQProvider<IQ> getIQProvider(String elementName, String namespace) {
        return (IQProvider) iqProviders.get(getKey(elementName, namespace));
    }

    public static List<IQProvider<IQ>> getIQProviders() {
        List<IQProvider<IQ>> providers = new ArrayList<>(iqProviders.size());
        providers.addAll(iqProviders.values());
        return providers;
    }

    public static void addIQProvider(String elementName, String namespace, Object provider) {
        validate(elementName, namespace);
        String key = removeIQProvider(elementName, namespace);
        if (provider instanceof IQProvider) {
            iqProviders.put(key, (IQProvider) provider);
            return;
        }
        throw new IllegalArgumentException("Provider must be an IQProvider");
    }

    public static String removeIQProvider(String elementName, String namespace) {
        String key = getKey(elementName, namespace);
        iqProviders.remove(key);
        return key;
    }

    public static ExtensionElementProvider<ExtensionElement> getExtensionProvider(String elementName, String namespace) {
        return (ExtensionElementProvider) extensionProviders.get(getKey(elementName, namespace));
    }

    public static void addExtensionProvider(String elementName, String namespace, Object provider) {
        validate(elementName, namespace);
        String key = removeExtensionProvider(elementName, namespace);
        if (provider instanceof ExtensionElementProvider) {
            extensionProviders.put(key, (ExtensionElementProvider) provider);
            return;
        }
        throw new IllegalArgumentException("Provider must be a PacketExtensionProvider");
    }

    public static String removeExtensionProvider(String elementName, String namespace) {
        String key = getKey(elementName, namespace);
        extensionProviders.remove(key);
        return key;
    }

    public static List<ExtensionElementProvider<ExtensionElement>> getExtensionProviders() {
        List<ExtensionElementProvider<ExtensionElement>> providers = new ArrayList<>(extensionProviders.size());
        providers.addAll(extensionProviders.values());
        return providers;
    }

    public static ExtensionElementProvider<ExtensionElement> getStreamFeatureProvider(String elementName, String namespace) {
        return (ExtensionElementProvider) streamFeatureProviders.get(getKey(elementName, namespace));
    }

    public static void addStreamFeatureProvider(String elementName, String namespace, ExtensionElementProvider<ExtensionElement> provider) {
        validate(elementName, namespace);
        streamFeatureProviders.put(getKey(elementName, namespace), provider);
    }

    public static void removeStreamFeatureProvider(String elementName, String namespace) {
        streamFeatureProviders.remove(getKey(elementName, namespace));
    }

    private static String getKey(String elementName, String namespace) {
        return XmppStringUtils.generateKey(elementName, namespace);
    }

    private static void validate(String elementName, String namespace) {
        if (StringUtils.isNullOrEmpty((CharSequence) elementName)) {
            throw new IllegalArgumentException("elementName must not be null or empty");
        } else if (StringUtils.isNullOrEmpty((CharSequence) namespace)) {
            throw new IllegalArgumentException("namespace must not be null or empty");
        }
    }
}
