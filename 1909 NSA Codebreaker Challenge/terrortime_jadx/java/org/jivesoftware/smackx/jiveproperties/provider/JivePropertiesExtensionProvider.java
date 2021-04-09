package org.jivesoftware.smackx.jiveproperties.provider;

import java.util.logging.Logger;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension;

public class JivePropertiesExtensionProvider extends ExtensionElementProvider<JivePropertiesExtension> {
    private static final Logger LOGGER = Logger.getLogger(JivePropertiesExtensionProvider.class.getName());

    /* JADX WARNING: type inference failed for: r8v0 */
    /* JADX WARNING: type inference failed for: r9v0 */
    /* JADX WARNING: type inference failed for: r9v1 */
    /* JADX WARNING: type inference failed for: r8v1, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r9v2 */
    /* JADX WARNING: type inference failed for: r8v2 */
    /* JADX WARNING: type inference failed for: r9v3, types: [java.lang.Object] */
    /* JADX WARNING: type inference failed for: r12v2, types: [java.lang.Object] */
    /* JADX WARNING: type inference failed for: r0v25 */
    /* JADX WARNING: type inference failed for: r9v4 */
    /* JADX WARNING: type inference failed for: r0v26 */
    /* JADX WARNING: type inference failed for: r9v5 */
    /* JADX WARNING: type inference failed for: r0v27, types: [java.lang.Boolean] */
    /* JADX WARNING: type inference failed for: r9v6 */
    /* JADX WARNING: type inference failed for: r0v28, types: [java.lang.Double] */
    /* JADX WARNING: type inference failed for: r9v7 */
    /* JADX WARNING: type inference failed for: r0v29, types: [java.lang.Float] */
    /* JADX WARNING: type inference failed for: r9v8 */
    /* JADX WARNING: type inference failed for: r0v30, types: [java.lang.Long] */
    /* JADX WARNING: type inference failed for: r9v9 */
    /* JADX WARNING: type inference failed for: r0v31, types: [java.lang.Integer] */
    /* JADX WARNING: type inference failed for: r9v10 */
    /* JADX WARNING: type inference failed for: r8v3 */
    /* JADX WARNING: type inference failed for: r8v4, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r8v5 */
    /* JADX WARNING: type inference failed for: r9v11 */
    /* JADX WARNING: type inference failed for: r9v12 */
    /* JADX WARNING: type inference failed for: r8v6 */
    /* JADX WARNING: type inference failed for: r8v7 */
    /* JADX WARNING: type inference failed for: r8v8 */
    /* JADX WARNING: type inference failed for: r8v9 */
    /* JADX WARNING: type inference failed for: r9v13 */
    /* JADX WARNING: type inference failed for: r8v10 */
    /* JADX WARNING: type inference failed for: r9v14 */
    /* JADX WARNING: type inference failed for: r8v11 */
    /* JADX WARNING: type inference failed for: r8v12 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r9v2
  assigns: []
  uses: []
  mth insns count: 114
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 16 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension parse(org.xmlpull.v1.XmlPullParser r16, int r17) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r15 = this;
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r1 = r0
        L_0x0006:
            int r0 = r16.next()
            r2 = 3
            r3 = 2
            if (r0 != r3) goto L_0x00fc
            java.lang.String r4 = r16.getName()
            java.lang.String r5 = "property"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x00fc
            r4 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
        L_0x001f:
            if (r4 != 0) goto L_0x00f9
            int r10 = r16.next()
            if (r10 != r3) goto L_0x0055
            java.lang.String r0 = r16.getName()
            java.lang.String r11 = "name"
            boolean r11 = r0.equals(r11)
            if (r11 == 0) goto L_0x003a
            java.lang.String r6 = r16.nextText()
            r13 = r16
            goto L_0x0053
        L_0x003a:
            java.lang.String r11 = "value"
            boolean r11 = r0.equals(r11)
            if (r11 == 0) goto L_0x0051
            java.lang.String r11 = ""
            java.lang.String r12 = "type"
            r13 = r16
            java.lang.String r7 = r13.getAttributeValue(r11, r12)
            java.lang.String r8 = r16.nextText()
            goto L_0x0053
        L_0x0051:
            r13 = r16
        L_0x0053:
            r0 = r10
            goto L_0x001f
        L_0x0055:
            r13 = r16
            if (r10 != r2) goto L_0x00f6
            java.lang.String r0 = r16.getName()
            boolean r0 = r0.equals(r5)
            if (r0 == 0) goto L_0x00f6
            java.lang.String r0 = "integer"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0072
            java.lang.Integer r0 = java.lang.Integer.valueOf(r8)
            r9 = r0
            goto L_0x00eb
        L_0x0072:
            java.lang.String r0 = "long"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0081
            java.lang.Long r0 = java.lang.Long.valueOf(r8)
            r9 = r0
            goto L_0x00eb
        L_0x0081:
            java.lang.String r0 = "float"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x008f
            java.lang.Float r0 = java.lang.Float.valueOf(r8)
            r9 = r0
            goto L_0x00eb
        L_0x008f:
            java.lang.String r0 = "double"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x009d
            java.lang.Double r0 = java.lang.Double.valueOf(r8)
            r9 = r0
            goto L_0x00eb
        L_0x009d:
            java.lang.String r0 = "boolean"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x00ab
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r8)
            r9 = r0
            goto L_0x00eb
        L_0x00ab:
            java.lang.String r0 = "string"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x00b6
            r0 = r8
            r9 = r0
            goto L_0x00eb
        L_0x00b6:
            java.lang.String r0 = "java-object"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x00eb
            boolean r0 = org.jivesoftware.smackx.jiveproperties.JivePropertiesManager.isJavaObjectEnabled()
            if (r0 == 0) goto L_0x00e4
            byte[] r0 = org.jivesoftware.smack.util.stringencoder.Base64.decode(r8)     // Catch:{ Exception -> 0x00d9 }
            java.io.ObjectInputStream r11 = new java.io.ObjectInputStream     // Catch:{ Exception -> 0x00d9 }
            java.io.ByteArrayInputStream r12 = new java.io.ByteArrayInputStream     // Catch:{ Exception -> 0x00d9 }
            r12.<init>(r0)     // Catch:{ Exception -> 0x00d9 }
            r11.<init>(r12)     // Catch:{ Exception -> 0x00d9 }
            java.lang.Object r12 = r11.readObject()     // Catch:{ Exception -> 0x00d9 }
            r0 = r12
            r9 = r0
            goto L_0x00eb
        L_0x00d9:
            r0 = move-exception
            java.util.logging.Logger r11 = LOGGER
            java.util.logging.Level r12 = java.util.logging.Level.SEVERE
            java.lang.String r14 = "Error parsing java object"
            r11.log(r12, r14, r0)
            goto L_0x00eb
        L_0x00e4:
            java.util.logging.Logger r0 = LOGGER
            java.lang.String r11 = "JavaObject is not enabled. Enable with JivePropertiesManager.setJavaObjectEnabled(true)"
            r0.severe(r11)
        L_0x00eb:
            if (r6 == 0) goto L_0x00f2
            if (r9 == 0) goto L_0x00f2
            r1.put(r6, r9)
        L_0x00f2:
            r4 = 1
            r0 = r10
            goto L_0x001f
        L_0x00f6:
            r0 = r10
            goto L_0x001f
        L_0x00f9:
            r13 = r16
            goto L_0x0113
        L_0x00fc:
            r13 = r16
            if (r0 != r2) goto L_0x0113
            java.lang.String r2 = r16.getName()
            java.lang.String r3 = "properties"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x0113
            org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension r0 = new org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension
            r0.<init>(r1)
            return r0
        L_0x0113:
            goto L_0x0006
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.jiveproperties.provider.JivePropertiesExtensionProvider.parse(org.xmlpull.v1.XmlPullParser, int):org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension");
    }
}
