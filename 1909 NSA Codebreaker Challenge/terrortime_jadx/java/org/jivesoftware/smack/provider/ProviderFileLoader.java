package org.jivesoftware.smack.provider;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class ProviderFileLoader implements ProviderLoader {
    private static final Logger LOGGER = Logger.getLogger(ProviderFileLoader.class.getName());
    private List<Exception> exceptions;
    private final Collection<ExtensionProviderInfo> extProviders;
    private final Collection<IQProviderInfo> iqProviders;
    private final Collection<StreamFeatureProviderInfo> sfProviders;

    public ProviderFileLoader(InputStream providerStream) {
        this(providerStream, ProviderFileLoader.class.getClassLoader());
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x009c A[Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0110 A[Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ProviderFileLoader(java.io.InputStream r14, java.lang.ClassLoader r15) {
        /*
            r13 = this;
            r13.<init>()
            java.util.LinkedList r0 = new java.util.LinkedList
            r0.<init>()
            r13.iqProviders = r0
            java.util.LinkedList r0 = new java.util.LinkedList
            r0.<init>()
            r13.extProviders = r0
            java.util.LinkedList r0 = new java.util.LinkedList
            r0.<init>()
            r13.sfProviders = r0
            java.util.LinkedList r0 = new java.util.LinkedList
            r0.<init>()
            r13.exceptions = r0
            org.xmlpull.v1.XmlPullParserFactory r0 = org.xmlpull.v1.XmlPullParserFactory.newInstance()     // Catch:{ Exception -> 0x01af }
            org.xmlpull.v1.XmlPullParser r0 = r0.newPullParser()     // Catch:{ Exception -> 0x01af }
            java.lang.String r1 = "http://xmlpull.org/v1/doc/features.html#process-namespaces"
            r2 = 1
            r0.setFeature(r1, r2)     // Catch:{ Exception -> 0x01af }
            java.lang.String r1 = "UTF-8"
            r0.setInput(r14, r1)     // Catch:{ Exception -> 0x01af }
            int r1 = r0.getEventType()     // Catch:{ Exception -> 0x01af }
        L_0x0036:
            r3 = 2
            if (r1 != r3) goto L_0x01a2
            java.lang.String r4 = r0.getName()     // Catch:{ Exception -> 0x01af }
            java.lang.String r5 = "smackProviders"
            boolean r5 = r5.equals(r4)     // Catch:{ IllegalArgumentException -> 0x017f }
            if (r5 != 0) goto L_0x017e
            r0.next()     // Catch:{ IllegalArgumentException -> 0x017f }
            r0.next()     // Catch:{ IllegalArgumentException -> 0x017f }
            java.lang.String r5 = r0.nextText()     // Catch:{ IllegalArgumentException -> 0x017f }
            r0.next()     // Catch:{ IllegalArgumentException -> 0x017f }
            r0.next()     // Catch:{ IllegalArgumentException -> 0x017f }
            java.lang.String r6 = r0.nextText()     // Catch:{ IllegalArgumentException -> 0x017f }
            r0.next()     // Catch:{ IllegalArgumentException -> 0x017f }
            r0.next()     // Catch:{ IllegalArgumentException -> 0x017f }
            java.lang.String r7 = r0.nextText()     // Catch:{ IllegalArgumentException -> 0x017f }
            java.lang.Class r8 = r15.loadClass(r7)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r9 = -1
            int r10 = r4.hashCode()     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r11 = -797518000(0xffffffffd076d750, float:-1.65652234E10)
            r12 = 0
            if (r10 == r11) goto L_0x0091
            r11 = 80611175(0x4ce0767, float:4.8437165E-36)
            if (r10 == r11) goto L_0x0087
            r11 = 1834143545(0x6d52cf39, float:4.077648E27)
            if (r10 == r11) goto L_0x007d
        L_0x007c:
            goto L_0x009a
        L_0x007d:
            java.lang.String r10 = "iqProvider"
            boolean r10 = r4.equals(r10)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            if (r10 == 0) goto L_0x007c
            r9 = r12
            goto L_0x009a
        L_0x0087:
            java.lang.String r10 = "streamFeatureProvider"
            boolean r10 = r4.equals(r10)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            if (r10 == 0) goto L_0x007c
            r9 = r3
            goto L_0x009a
        L_0x0091:
            java.lang.String r10 = "extensionProvider"
            boolean r10 = r4.equals(r10)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            if (r10 == 0) goto L_0x007c
            r9 = r2
        L_0x009a:
            if (r9 == 0) goto L_0x0110
            if (r9 == r2) goto L_0x00d2
            if (r9 == r3) goto L_0x00b8
            java.util.logging.Logger r3 = LOGGER     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r9.<init>()     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.String r10 = "Unknown provider type: "
            r9.append(r10)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r9.append(r4)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.String r9 = r9.toString()     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r3.warning(r9)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            goto L_0x014e
        L_0x00b8:
            java.lang.Class[] r3 = new java.lang.Class[r12]     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.reflect.Constructor r3 = r8.getConstructor(r3)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.Object[] r9 = new java.lang.Object[r12]     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.Object r3 = r3.newInstance(r9)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            org.jivesoftware.smack.provider.ExtensionElementProvider r3 = (org.jivesoftware.smack.provider.ExtensionElementProvider) r3     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.util.Collection<org.jivesoftware.smack.provider.StreamFeatureProviderInfo> r9 = r13.sfProviders     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            org.jivesoftware.smack.provider.StreamFeatureProviderInfo r10 = new org.jivesoftware.smack.provider.StreamFeatureProviderInfo     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r10.<init>(r5, r6, r3)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r9.add(r10)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            goto L_0x014e
        L_0x00d2:
            java.lang.Class<org.jivesoftware.smack.provider.ExtensionElementProvider> r3 = org.jivesoftware.smack.provider.ExtensionElementProvider.class
            boolean r3 = r3.isAssignableFrom(r8)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            if (r3 == 0) goto L_0x00f4
            java.lang.Class[] r3 = new java.lang.Class[r12]     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.reflect.Constructor r3 = r8.getConstructor(r3)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.Object[] r9 = new java.lang.Object[r12]     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.Object r3 = r3.newInstance(r9)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            org.jivesoftware.smack.provider.ExtensionElementProvider r3 = (org.jivesoftware.smack.provider.ExtensionElementProvider) r3     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.util.Collection<org.jivesoftware.smack.provider.ExtensionProviderInfo> r9 = r13.extProviders     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            org.jivesoftware.smack.provider.ExtensionProviderInfo r10 = new org.jivesoftware.smack.provider.ExtensionProviderInfo     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r10.<init>(r5, r6, r3)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r9.add(r10)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            goto L_0x014e
        L_0x00f4:
            java.util.List<java.lang.Exception> r3 = r13.exceptions     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.IllegalArgumentException r9 = new java.lang.IllegalArgumentException     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r10.<init>()     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r10.append(r7)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.String r11 = " is not a PacketExtensionProvider"
            r10.append(r11)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.String r10 = r10.toString()     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r9.<init>(r10)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r3.add(r9)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            goto L_0x014e
        L_0x0110:
            java.lang.Class<org.jivesoftware.smack.provider.IQProvider> r3 = org.jivesoftware.smack.provider.IQProvider.class
            boolean r3 = r3.isAssignableFrom(r8)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            if (r3 == 0) goto L_0x0132
            java.lang.Class[] r3 = new java.lang.Class[r12]     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.reflect.Constructor r3 = r8.getConstructor(r3)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.Object[] r9 = new java.lang.Object[r12]     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.Object r3 = r3.newInstance(r9)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            org.jivesoftware.smack.provider.IQProvider r3 = (org.jivesoftware.smack.provider.IQProvider) r3     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.util.Collection<org.jivesoftware.smack.provider.IQProviderInfo> r9 = r13.iqProviders     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            org.jivesoftware.smack.provider.IQProviderInfo r10 = new org.jivesoftware.smack.provider.IQProviderInfo     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r10.<init>(r5, r6, r3)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r9.add(r10)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            goto L_0x014e
        L_0x0132:
            java.util.List<java.lang.Exception> r3 = r13.exceptions     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.IllegalArgumentException r9 = new java.lang.IllegalArgumentException     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r10.<init>()     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r10.append(r7)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.String r11 = " is not a IQProvider"
            r10.append(r11)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            java.lang.String r10 = r10.toString()     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r9.<init>(r10)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
            r3.add(r9)     // Catch:{ ClassNotFoundException -> 0x016e, InstantiationException -> 0x014f }
        L_0x014e:
            goto L_0x017e
        L_0x014f:
            r3 = move-exception
            java.util.logging.Logger r8 = LOGGER     // Catch:{ IllegalArgumentException -> 0x017f }
            java.util.logging.Level r9 = java.util.logging.Level.SEVERE     // Catch:{ IllegalArgumentException -> 0x017f }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x017f }
            r10.<init>()     // Catch:{ IllegalArgumentException -> 0x017f }
            java.lang.String r11 = "Could not instanciate "
            r10.append(r11)     // Catch:{ IllegalArgumentException -> 0x017f }
            r10.append(r7)     // Catch:{ IllegalArgumentException -> 0x017f }
            java.lang.String r10 = r10.toString()     // Catch:{ IllegalArgumentException -> 0x017f }
            r8.log(r9, r10, r3)     // Catch:{ IllegalArgumentException -> 0x017f }
            java.util.List<java.lang.Exception> r8 = r13.exceptions     // Catch:{ IllegalArgumentException -> 0x017f }
            r8.add(r3)     // Catch:{ IllegalArgumentException -> 0x017f }
            goto L_0x017e
        L_0x016e:
            r3 = move-exception
            java.util.logging.Logger r8 = LOGGER     // Catch:{ IllegalArgumentException -> 0x017f }
            java.util.logging.Level r9 = java.util.logging.Level.SEVERE     // Catch:{ IllegalArgumentException -> 0x017f }
            java.lang.String r10 = "Could not find provider class"
            r8.log(r9, r10, r3)     // Catch:{ IllegalArgumentException -> 0x017f }
            java.util.List<java.lang.Exception> r8 = r13.exceptions     // Catch:{ IllegalArgumentException -> 0x017f }
            r8.add(r3)     // Catch:{ IllegalArgumentException -> 0x017f }
        L_0x017e:
            goto L_0x01a2
        L_0x017f:
            r3 = move-exception
            java.util.logging.Logger r5 = LOGGER     // Catch:{ Exception -> 0x01af }
            java.util.logging.Level r6 = java.util.logging.Level.SEVERE     // Catch:{ Exception -> 0x01af }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01af }
            r7.<init>()     // Catch:{ Exception -> 0x01af }
            java.lang.String r8 = "Invalid provider type found ["
            r7.append(r8)     // Catch:{ Exception -> 0x01af }
            r7.append(r4)     // Catch:{ Exception -> 0x01af }
            java.lang.String r8 = "] when expecting iqProvider or extensionProvider"
            r7.append(r8)     // Catch:{ Exception -> 0x01af }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x01af }
            r5.log(r6, r7, r3)     // Catch:{ Exception -> 0x01af }
            java.util.List<java.lang.Exception> r5 = r13.exceptions     // Catch:{ Exception -> 0x01af }
            r5.add(r3)     // Catch:{ Exception -> 0x01af }
        L_0x01a2:
            int r3 = r0.next()     // Catch:{ Exception -> 0x01af }
            r1 = r3
            if (r1 != r2) goto L_0x0036
            r14.close()     // Catch:{ Exception -> 0x01c2 }
            goto L_0x01c1
        L_0x01ad:
            r0 = move-exception
            goto L_0x01c5
        L_0x01af:
            r0 = move-exception
            java.util.logging.Logger r1 = LOGGER     // Catch:{ all -> 0x01ad }
            java.util.logging.Level r2 = java.util.logging.Level.SEVERE     // Catch:{ all -> 0x01ad }
            java.lang.String r3 = "Unknown error occurred while parsing provider file"
            r1.log(r2, r3, r0)     // Catch:{ all -> 0x01ad }
            java.util.List<java.lang.Exception> r1 = r13.exceptions     // Catch:{ all -> 0x01ad }
            r1.add(r0)     // Catch:{ all -> 0x01ad }
            r14.close()     // Catch:{ Exception -> 0x01c2 }
        L_0x01c1:
            goto L_0x01c4
        L_0x01c2:
            r0 = move-exception
        L_0x01c4:
            return
        L_0x01c5:
            r14.close()     // Catch:{ Exception -> 0x01c9 }
            goto L_0x01ca
        L_0x01c9:
            r1 = move-exception
        L_0x01ca:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.provider.ProviderFileLoader.<init>(java.io.InputStream, java.lang.ClassLoader):void");
    }

    public Collection<IQProviderInfo> getIQProviderInfo() {
        return this.iqProviders;
    }

    public Collection<ExtensionProviderInfo> getExtensionProviderInfo() {
        return this.extProviders;
    }

    public Collection<StreamFeatureProviderInfo> getStreamFeatureProviderInfo() {
        return this.sfProviders;
    }

    public List<Exception> getLoadingExceptions() {
        return Collections.unmodifiableList(this.exceptions);
    }
}
