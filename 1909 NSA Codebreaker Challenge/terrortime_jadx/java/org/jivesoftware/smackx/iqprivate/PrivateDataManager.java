package org.jivesoftware.smackx.iqprivate;

import java.util.Hashtable;
import java.util.Map;
import java.util.WeakHashMap;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.iqprivate.packet.PrivateData;
import org.jivesoftware.smackx.iqprivate.packet.PrivateDataIQ;
import org.jivesoftware.smackx.iqprivate.provider.PrivateDataProvider;
import org.jxmpp.util.XmppStringUtils;

public final class PrivateDataManager extends Manager {
    private static final PrivateData DUMMY_PRIVATE_DATA = new PrivateData() {
        public String getElementName() {
            return "smackDummyPrivateData";
        }

        public String getNamespace() {
            return "https://igniterealtime.org/projects/smack/";
        }

        public CharSequence toXML() {
            StringBuilder sb = new StringBuilder();
            sb.append('<');
            sb.append(getElementName());
            sb.append(" xmlns='");
            sb.append(getNamespace());
            sb.append("'/>");
            return sb.toString();
        }
    };
    private static final Map<XMPPConnection, PrivateDataManager> instances = new WeakHashMap();
    private static final Map<String, PrivateDataProvider> privateDataProviders = new Hashtable();

    public static class PrivateDataIQProvider extends IQProvider<PrivateDataIQ> {
        /* JADX WARNING: type inference failed for: r0v0 */
        /* JADX WARNING: type inference failed for: r0v1, types: [org.jivesoftware.smackx.iqprivate.packet.PrivateData] */
        /* JADX WARNING: type inference failed for: r0v2 */
        /* JADX WARNING: type inference failed for: r8v0, types: [org.jivesoftware.smackx.iqprivate.packet.DefaultPrivateData] */
        /* JADX WARNING: type inference failed for: r0v3 */
        /* JADX WARNING: type inference failed for: r0v4, types: [org.jivesoftware.smackx.iqprivate.packet.PrivateData] */
        /* JADX WARNING: type inference failed for: r0v5 */
        /* JADX WARNING: type inference failed for: r0v6 */
        /* JADX WARNING: type inference failed for: r0v7 */
        /* JADX WARNING: type inference failed for: r0v8 */
        /* JADX WARNING: type inference failed for: r0v9 */
        /* JADX WARNING: type inference failed for: r0v10 */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x0002, code lost:
            r0 = r0;
         */
        /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r0v2
  assigns: []
  uses: []
  mth insns count: 44
        	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$0(DepthTraversal.java:13)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:13)
        	at jadx.core.ProcessClass.process(ProcessClass.java:30)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
        	at jadx.core.ProcessClass.process(ProcessClass.java:35)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
         */
        /* JADX WARNING: Unknown variable types count: 5 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.jivesoftware.smackx.iqprivate.packet.PrivateDataIQ parse(org.xmlpull.v1.XmlPullParser r14, int r15) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException, org.jivesoftware.smack.SmackException {
            /*
                r13 = this;
                r0 = 0
                r1 = 0
            L_0x0002:
                if (r1 != 0) goto L_0x006c
                int r2 = r14.next()
                r3 = 3
                r4 = 2
                if (r2 != r4) goto L_0x005c
                java.lang.String r5 = r14.getName()
                java.lang.String r6 = r14.getNamespace()
                org.jivesoftware.smackx.iqprivate.provider.PrivateDataProvider r7 = org.jivesoftware.smackx.iqprivate.PrivateDataManager.getPrivateDataProvider(r5, r6)
                if (r7 == 0) goto L_0x001f
                org.jivesoftware.smackx.iqprivate.packet.PrivateData r0 = r7.parsePrivateData(r14)
                goto L_0x005b
            L_0x001f:
                org.jivesoftware.smackx.iqprivate.packet.DefaultPrivateData r8 = new org.jivesoftware.smackx.iqprivate.packet.DefaultPrivateData
                r8.<init>(r5, r6)
                r9 = 0
            L_0x0025:
                if (r9 != 0) goto L_0x005a
                int r10 = r14.next()
                if (r10 != r4) goto L_0x004c
                java.lang.String r11 = r14.getName()
                boolean r12 = r14.isEmptyElementTag()
                if (r12 == 0) goto L_0x003d
                java.lang.String r12 = ""
                r8.setValue(r11, r12)
                goto L_0x004b
            L_0x003d:
                int r10 = r14.next()
                r12 = 4
                if (r10 != r12) goto L_0x004b
                java.lang.String r12 = r14.getText()
                r8.setValue(r11, r12)
            L_0x004b:
                goto L_0x0059
            L_0x004c:
                if (r10 != r3) goto L_0x0059
                java.lang.String r11 = r14.getName()
                boolean r11 = r11.equals(r5)
                if (r11 == 0) goto L_0x0059
                r9 = 1
            L_0x0059:
                goto L_0x0025
            L_0x005a:
                r0 = r8
            L_0x005b:
                goto L_0x006b
            L_0x005c:
                if (r2 != r3) goto L_0x006b
                java.lang.String r3 = r14.getName()
                java.lang.String r4 = "query"
                boolean r3 = r3.equals(r4)
                if (r3 == 0) goto L_0x006b
                r1 = 1
            L_0x006b:
                goto L_0x0002
            L_0x006c:
                org.jivesoftware.smackx.iqprivate.packet.PrivateDataIQ r2 = new org.jivesoftware.smackx.iqprivate.packet.PrivateDataIQ
                r2.<init>(r0)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.iqprivate.PrivateDataManager.PrivateDataIQProvider.parse(org.xmlpull.v1.XmlPullParser, int):org.jivesoftware.smackx.iqprivate.packet.PrivateDataIQ");
        }
    }

    public static synchronized PrivateDataManager getInstanceFor(XMPPConnection connection) {
        PrivateDataManager privateDataManager;
        synchronized (PrivateDataManager.class) {
            privateDataManager = (PrivateDataManager) instances.get(connection);
            if (privateDataManager == null) {
                privateDataManager = new PrivateDataManager(connection);
            }
        }
        return privateDataManager;
    }

    public static PrivateDataProvider getPrivateDataProvider(String elementName, String namespace) {
        return (PrivateDataProvider) privateDataProviders.get(XmppStringUtils.generateKey(elementName, namespace));
    }

    public static void addPrivateDataProvider(String elementName, String namespace, PrivateDataProvider provider) {
        privateDataProviders.put(XmppStringUtils.generateKey(elementName, namespace), provider);
    }

    public static void removePrivateDataProvider(String elementName, String namespace) {
        privateDataProviders.remove(XmppStringUtils.generateKey(elementName, namespace));
    }

    private PrivateDataManager(XMPPConnection connection) {
        super(connection);
        instances.put(connection, this);
    }

    public PrivateData getPrivateData(String elementName, String namespace) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ((PrivateDataIQ) connection().createStanzaCollectorAndSend(new PrivateDataIQ(elementName, namespace)).nextResultOrThrow()).getPrivateData();
    }

    public void setPrivateData(PrivateData privateData) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        connection().createStanzaCollectorAndSend(new PrivateDataIQ(privateData)).nextResultOrThrow();
    }

    public boolean isSupported() throws NoResponseException, NotConnectedException, InterruptedException, XMPPErrorException {
        try {
            setPrivateData(DUMMY_PRIVATE_DATA);
            return true;
        } catch (XMPPErrorException e) {
            if (e.getStanzaError().getCondition() == Condition.service_unavailable) {
                return false;
            }
            throw e;
        }
    }
}
