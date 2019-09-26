package org.bouncycastle.est;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.Strings;
import org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp.Base64;

public class ESTResponse {
    private static final Long ZERO = Long.valueOf(0);
    private String HttpVersion;
    private Long absoluteReadLimit;
    /* access modifiers changed from: private */
    public Long contentLength;
    private final Headers headers;
    private InputStream inputStream;
    private final byte[] lineBuffer;
    private final ESTRequest originalRequest;
    /* access modifiers changed from: private */
    public long read = 0;
    private final Source source;
    private int statusCode;
    private String statusMessage;

    private class PrintingInputStream extends InputStream {
        private final InputStream src;

        private PrintingInputStream(InputStream inputStream) {
            this.src = inputStream;
        }

        public int available() throws IOException {
            return this.src.available();
        }

        public void close() throws IOException {
            this.src.close();
        }

        public int read() throws IOException {
            int read = this.src.read();
            System.out.print(String.valueOf((char) read));
            return read;
        }
    }

    public ESTResponse(ESTRequest eSTRequest, Source source2) throws IOException {
        this.originalRequest = eSTRequest;
        this.source = source2;
        if (source2 instanceof LimitedSource) {
            this.absoluteReadLimit = ((LimitedSource) source2).getAbsoluteReadLimit();
        }
        Set asKeySet = Properties.asKeySet("org.bouncycastle.debug.est");
        this.inputStream = (asKeySet.contains("input") || asKeySet.contains("all")) ? new PrintingInputStream(source2.getInputStream()) : source2.getInputStream();
        this.headers = new Headers();
        this.lineBuffer = new byte[1024];
        process();
    }

    private void process() throws IOException {
        this.HttpVersion = readStringIncluding(' ');
        this.statusCode = Integer.parseInt(readStringIncluding(' '));
        this.statusMessage = readStringIncluding(10);
        while (true) {
            String readStringIncluding = readStringIncluding(10);
            if (readStringIncluding.length() <= 0) {
                break;
            }
            int indexOf = readStringIncluding.indexOf(58);
            if (indexOf > -1) {
                this.headers.add(Strings.toLowerCase(readStringIncluding.substring(0, indexOf).trim()), readStringIncluding.substring(indexOf + 1).trim());
            }
        }
        this.contentLength = getContentLength();
        int i = this.statusCode;
        if (i == 204 || i == 202) {
            Long l = this.contentLength;
            if (l == null) {
                this.contentLength = Long.valueOf(0);
            } else if (this.statusCode == 204 && l.longValue() > 0) {
                throw new IOException("Got HTTP status 204 but Content-length > 0.");
            }
        }
        Long l2 = this.contentLength;
        if (l2 != null) {
            if (l2.equals(ZERO)) {
                this.inputStream = new InputStream() {
                    public int read() throws IOException {
                        return -1;
                    }
                };
            }
            Long l3 = this.contentLength;
            if (l3 != null) {
                if (l3.longValue() < 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Server returned negative content length: ");
                    sb.append(this.absoluteReadLimit);
                    throw new IOException(sb.toString());
                } else if (this.absoluteReadLimit != null && this.contentLength.longValue() >= this.absoluteReadLimit.longValue()) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Content length longer than absolute read limit: ");
                    sb2.append(this.absoluteReadLimit);
                    sb2.append(" Content-Length: ");
                    sb2.append(this.contentLength);
                    throw new IOException(sb2.toString());
                }
            }
            this.inputStream = wrapWithCounter(this.inputStream, this.absoluteReadLimit);
            if (Base64.ELEMENT.equalsIgnoreCase(getHeader("content-transfer-encoding"))) {
                this.inputStream = new CTEBase64InputStream(this.inputStream, getContentLength());
                return;
            }
            return;
        }
        throw new IOException("No Content-length header.");
    }

    public void close() throws IOException {
        InputStream inputStream2 = this.inputStream;
        if (inputStream2 != null) {
            inputStream2.close();
        }
        this.source.close();
    }

    public Long getContentLength() {
        String firstValue = this.headers.getFirstValue("Content-Length");
        if (firstValue == null) {
            return null;
        }
        try {
            return Long.valueOf(Long.parseLong(firstValue));
        } catch (RuntimeException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Content Length: '");
            sb.append(firstValue);
            sb.append("' invalid. ");
            sb.append(e.getMessage());
            throw new RuntimeException(sb.toString());
        }
    }

    public String getHeader(String str) {
        return this.headers.getFirstValue(str);
    }

    public Headers getHeaders() {
        return this.headers;
    }

    public String getHttpVersion() {
        return this.HttpVersion;
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    public ESTRequest getOriginalRequest() {
        return this.originalRequest;
    }

    public Source getSource() {
        return this.source;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x0028  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x001c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String readStringIncluding(char r7) throws java.io.IOException {
        /*
            r6 = this;
            r0 = 0
            r1 = r0
        L_0x0002:
            java.io.InputStream r2 = r6.inputStream
            int r2 = r2.read()
            byte[] r3 = r6.lineBuffer
            int r4 = r1 + 1
            byte r5 = (byte) r2
            r3[r1] = r5
            int r1 = r3.length
            if (r4 >= r1) goto L_0x002e
            r1 = -1
            if (r2 == r7) goto L_0x001a
            if (r2 > r1) goto L_0x0018
            goto L_0x001a
        L_0x0018:
            r1 = r4
            goto L_0x0002
        L_0x001a:
            if (r2 == r1) goto L_0x0028
            java.lang.String r7 = new java.lang.String
            byte[] r1 = r6.lineBuffer
            r7.<init>(r1, r0, r4)
            java.lang.String r7 = r7.trim()
            return r7
        L_0x0028:
            java.io.EOFException r7 = new java.io.EOFException
            r7.<init>()
            throw r7
        L_0x002e:
            java.io.IOException r7 = new java.io.IOException
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Server sent line > "
            r0.append(r1)
            byte[] r1 = r6.lineBuffer
            int r1 = r1.length
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r7.<init>(r0)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.est.ESTResponse.readStringIncluding(char):java.lang.String");
    }

    /* access modifiers changed from: protected */
    public InputStream wrapWithCounter(final InputStream inputStream2, final Long l) {
        return new InputStream() {
            public void close() throws IOException {
                if (ESTResponse.this.contentLength != null && ESTResponse.this.contentLength.longValue() - 1 > ESTResponse.this.read) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Stream closed before limit fully read, Read: ");
                    sb.append(ESTResponse.this.read);
                    sb.append(" ContentLength: ");
                    sb.append(ESTResponse.this.contentLength);
                    throw new IOException(sb.toString());
                } else if (inputStream2.available() <= 0) {
                    inputStream2.close();
                } else {
                    throw new IOException("Stream closed with extra content in pipe that exceeds content length.");
                }
            }

            public int read() throws IOException {
                int read = inputStream2.read();
                if (read > -1) {
                    
                    /*  JADX ERROR: Method code generation error
                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000b: INVOKE  (wrap: org.bouncycastle.est.ESTResponse
                          0x0009: IGET  (r1v1 org.bouncycastle.est.ESTResponse) = (r5v0 'this' org.bouncycastle.est.ESTResponse$2 A[THIS]) org.bouncycastle.est.ESTResponse.2.this$0 org.bouncycastle.est.ESTResponse) org.bouncycastle.est.ESTResponse.access$108(org.bouncycastle.est.ESTResponse):long type: STATIC in method: org.bouncycastle.est.ESTResponse.2.read():int, dex: classes.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:245)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:213)
                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:138)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:210)
                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:203)
                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:316)
                        	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:262)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:225)
                        	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:661)
                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:595)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:353)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:223)
                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:105)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:303)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:239)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:213)
                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:210)
                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:203)
                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:316)
                        	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:262)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:225)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
                        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:76)
                        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:32)
                        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:20)
                        	at jadx.core.ProcessClass.process(ProcessClass.java:36)
                        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
                        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
                        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
                        Caused by: org.objenesis.ObjenesisException: java.lang.ClassNotFoundException: sun.reflect.ReflectionFactory
                        	at org.objenesis.instantiator.sun.SunReflectionFactoryHelper.getReflectionFactoryClass(SunReflectionFactoryHelper.java:57)
                        	at org.objenesis.instantiator.sun.SunReflectionFactoryHelper.newConstructorForSerialization(SunReflectionFactoryHelper.java:37)
                        	at org.objenesis.instantiator.sun.SunReflectionFactoryInstantiator.<init>(SunReflectionFactoryInstantiator.java:41)
                        	at org.objenesis.strategy.StdInstantiatorStrategy.newInstantiatorOf(StdInstantiatorStrategy.java:68)
                        	at org.objenesis.ObjenesisBase.getInstantiatorOf(ObjenesisBase.java:94)
                        	at org.objenesis.ObjenesisBase.newInstance(ObjenesisBase.java:73)
                        	at com.rits.cloning.ObjenesisInstantiationStrategy.newInstance(ObjenesisInstantiationStrategy.java:17)
                        	at com.rits.cloning.Cloner.newInstance(Cloner.java:300)
                        	at com.rits.cloning.Cloner.cloneObject(Cloner.java:461)
                        	at com.rits.cloning.Cloner.cloneInternal(Cloner.java:456)
                        	at com.rits.cloning.Cloner.deepClone(Cloner.java:326)
                        	at jadx.core.dex.nodes.InsnNode.copy(InsnNode.java:352)
                        	at jadx.core.dex.nodes.InsnNode.copyCommonParams(InsnNode.java:333)
                        	at jadx.core.dex.instructions.IndexInsnNode.copy(IndexInsnNode.java:24)
                        	at jadx.core.dex.instructions.IndexInsnNode.copy(IndexInsnNode.java:9)
                        	at jadx.core.codegen.InsnGen.inlineMethod(InsnGen.java:880)
                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:669)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:357)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:239)
                        	... 41 more
                        Caused by: java.lang.ClassNotFoundException: sun.reflect.ReflectionFactory
                        	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(Unknown Source)
                        	at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(Unknown Source)
                        	at java.base/java.lang.ClassLoader.loadClass(Unknown Source)
                        	at java.base/java.lang.Class.forName0(Native Method)
                        	at java.base/java.lang.Class.forName(Unknown Source)
                        	at org.objenesis.instantiator.sun.SunReflectionFactoryHelper.getReflectionFactoryClass(SunReflectionFactoryHelper.java:54)
                        	... 59 more
                        */
                    /*
                        this = this;
                        java.io.InputStream r0 = r2
                        int r0 = r0.read()
                        r1 = -1
                        if (r0 <= r1) goto L_0x003c
                        org.bouncycastle.est.ESTResponse r1 = org.bouncycastle.est.ESTResponse.this
                        
                        // error: 0x000b: INVOKE  (r1 I:org.bouncycastle.est.ESTResponse) org.bouncycastle.est.ESTResponse.access$108(org.bouncycastle.est.ESTResponse):long type: STATIC
                        java.lang.Long r1 = r3
                        if (r1 == 0) goto L_0x003c
                        org.bouncycastle.est.ESTResponse r1 = org.bouncycastle.est.ESTResponse.this
                        long r1 = r1.read
                        java.lang.Long r3 = r3
                        long r3 = r3.longValue()
                        int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
                        if (r1 >= 0) goto L_0x0023
                        goto L_0x003c
                    L_0x0023:
                        java.io.IOException r0 = new java.io.IOException
                        java.lang.StringBuilder r1 = new java.lang.StringBuilder
                        r1.<init>()
                        java.lang.String r2 = "Absolute Read Limit exceeded: "
                        r1.append(r2)
                        java.lang.Long r2 = r3
                        r1.append(r2)
                        java.lang.String r1 = r1.toString()
                        r0.<init>(r1)
                        throw r0
                    L_0x003c:
                        return r0
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.est.ESTResponse.AnonymousClass2.read():int");
                }
            };
        }
    }
