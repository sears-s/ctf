package org.jivesoftware.smackx.httpfileupload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.httpfileupload.UploadService.Version;
import org.jivesoftware.smackx.httpfileupload.element.Slot;
import org.jivesoftware.smackx.httpfileupload.element.SlotRequest;
import org.jivesoftware.smackx.httpfileupload.element.SlotRequest_V0_2;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.DomainBareJid;

public final class HttpFileUploadManager extends Manager {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final Map<XMPPConnection, HttpFileUploadManager> INSTANCES = new WeakHashMap();
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(HttpFileUploadManager.class.getName());
    public static final String NAMESPACE = "urn:xmpp:http:upload:0";
    public static final String NAMESPACE_0_2 = "urn:xmpp:http:upload";
    private UploadService defaultUploadService;
    private SSLSocketFactory tlsSocketFactory;

    /* renamed from: org.jivesoftware.smackx.httpfileupload.HttpFileUploadManager$3 reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smackx$httpfileupload$UploadService$Version = new int[Version.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smackx$httpfileupload$UploadService$Version[Version.v0_3.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$httpfileupload$UploadService$Version[Version.v0_2.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                HttpFileUploadManager.getInstanceFor(connection);
            }
        });
    }

    public static synchronized HttpFileUploadManager getInstanceFor(XMPPConnection connection) {
        HttpFileUploadManager httpFileUploadManager;
        synchronized (HttpFileUploadManager.class) {
            httpFileUploadManager = (HttpFileUploadManager) INSTANCES.get(connection);
            if (httpFileUploadManager == null) {
                httpFileUploadManager = new HttpFileUploadManager(connection);
                INSTANCES.put(connection, httpFileUploadManager);
            }
        }
        return httpFileUploadManager;
    }

    private HttpFileUploadManager(XMPPConnection connection) {
        super(connection);
        connection.addConnectionListener(new AbstractConnectionListener() {
            public void authenticated(XMPPConnection connection, boolean resumed) {
                if (!resumed) {
                    try {
                        HttpFileUploadManager.this.discoverUploadService();
                    } catch (InterruptedException | NoResponseException | NotConnectedException | XMPPErrorException e) {
                        HttpFileUploadManager.LOGGER.log(Level.WARNING, "Error during discovering HTTP File Upload service", e);
                    }
                }
            }
        });
    }

    private static UploadService uploadServiceFrom(DiscoverInfo discoverInfo) {
        Version version;
        if (discoverInfo.containsFeature("urn:xmpp:http:upload:0")) {
            version = Version.v0_3;
        } else if (discoverInfo.containsFeature("urn:xmpp:http:upload")) {
            version = Version.v0_2;
        } else {
            throw new AssertionError();
        }
        DomainBareJid address = discoverInfo.getFrom().asDomainBareJid();
        DataForm dataForm = DataForm.from(discoverInfo);
        if (dataForm == null) {
            return new UploadService(address, version);
        }
        FormField field = dataForm.getField("max-file-size");
        if (field == null) {
            return new UploadService(address, version);
        }
        String maxFileSizeValue = field.getFirstValue();
        if (maxFileSizeValue == null) {
            return new UploadService(address, version);
        }
        return new UploadService(address, version, Long.valueOf(maxFileSizeValue));
    }

    public boolean discoverUploadService() throws XMPPErrorException, NotConnectedException, InterruptedException, NoResponseException {
        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection());
        List<DiscoverInfo> servicesDiscoverInfo = sdm.findServicesDiscoverInfo("urn:xmpp:http:upload:0", true, true);
        if (servicesDiscoverInfo.isEmpty()) {
            servicesDiscoverInfo = sdm.findServicesDiscoverInfo("urn:xmpp:http:upload", true, true);
            if (servicesDiscoverInfo.isEmpty()) {
                return false;
            }
        }
        this.defaultUploadService = uploadServiceFrom((DiscoverInfo) servicesDiscoverInfo.get(0));
        return true;
    }

    public boolean isUploadServiceDiscovered() {
        return this.defaultUploadService != null;
    }

    public UploadService getDefaultUploadService() {
        return this.defaultUploadService;
    }

    public URL uploadFile(File file) throws InterruptedException, XMPPErrorException, SmackException, IOException {
        return uploadFile(file, null);
    }

    public URL uploadFile(File file, UploadProgressListener listener) throws InterruptedException, XMPPErrorException, SmackException, IOException {
        if (file.isFile()) {
            Slot slot = requestSlot(file.getName(), file.length(), "application/octet-stream");
            uploadFile(file, slot, listener);
            return slot.getGetUrl();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("The path ");
        sb.append(file.getAbsolutePath());
        sb.append(" is not a file");
        throw new FileNotFoundException(sb.toString());
    }

    public Slot requestSlot(String filename, long fileSize) throws InterruptedException, XMPPErrorException, SmackException {
        return requestSlot(filename, fileSize, null, null);
    }

    public Slot requestSlot(String filename, long fileSize, String contentType) throws SmackException, InterruptedException, XMPPErrorException {
        return requestSlot(filename, fileSize, contentType, null);
    }

    public Slot requestSlot(String filename, long fileSize, String contentType, DomainBareJid uploadServiceAddress) throws SmackException, InterruptedException, XMPPErrorException {
        UploadService uploadService;
        IQ iq;
        long j = fileSize;
        DomainBareJid domainBareJid = uploadServiceAddress;
        XMPPConnection connection = connection();
        UploadService defaultUploadService2 = this.defaultUploadService;
        if (domainBareJid == null) {
            uploadService = defaultUploadService2;
        } else if (defaultUploadService2 == null || !defaultUploadService2.getAddress().equals((CharSequence) domainBareJid)) {
            DiscoverInfo discoverInfo = ServiceDiscoveryManager.getInstanceFor(connection).discoverInfo(domainBareJid);
            if (containsHttpFileUploadNamespace(discoverInfo)) {
                uploadService = uploadServiceFrom(discoverInfo);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("There is no HTTP upload service running at the given address '");
                sb.append(domainBareJid);
                sb.append('\'');
                throw new IllegalArgumentException(sb.toString());
            }
        } else {
            uploadService = defaultUploadService2;
        }
        if (uploadService == null) {
            throw new SmackException("No upload service specified and also none discovered.");
        } else if (uploadService.acceptsFileOfSize(j)) {
            int i = AnonymousClass3.$SwitchMap$org$jivesoftware$smackx$httpfileupload$UploadService$Version[uploadService.getVersion().ordinal()];
            if (i == 1) {
                SlotRequest slotRequest = new SlotRequest(uploadService.getAddress(), filename, fileSize, contentType);
                iq = slotRequest;
            } else if (i == 2) {
                SlotRequest_V0_2 slotRequest_V0_2 = new SlotRequest_V0_2(uploadService.getAddress(), filename, fileSize, contentType);
                iq = slotRequest_V0_2;
            } else {
                throw new AssertionError();
            }
            return (Slot) connection.createStanzaCollectorAndSend(iq).nextResultOrThrow();
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Requested file size ");
            sb2.append(j);
            sb2.append(" is greater than max allowed size ");
            sb2.append(uploadService.getMaxFileSize());
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    public void setTlsContext(SSLContext tlsContext) {
        if (tlsContext != null) {
            this.tlsSocketFactory = tlsContext.getSocketFactory();
        }
    }

    public void useTlsSettingsFrom(ConnectionConfiguration connectionConfiguration) {
        setTlsContext(connectionConfiguration.getCustomSSLContext());
    }

    private void uploadFile(File file, Slot slot, UploadProgressListener listener) throws IOException {
        BufferedInputStream inputStream;
        byte[] buffer;
        Throwable th;
        int bytesRead;
        UploadProgressListener uploadProgressListener = listener;
        String str = "Exception while closing output stream";
        String str2 = "Exception while closing input stream";
        long fileSize = file.length();
        if (fileSize < 2147483647L) {
            int fileSizeInt = (int) fileSize;
            FileInputStream fis = new FileInputStream(file);
            URL putUrl = slot.getPutUrl();
            HttpURLConnection urlConnection = (HttpURLConnection) putUrl.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            urlConnection.setFixedLengthStreamingMode(fileSizeInt);
            urlConnection.setRequestProperty("Content-Type", "application/octet-stream;");
            for (Entry<String, String> header : slot.getHeaders().entrySet()) {
                urlConnection.setRequestProperty((String) header.getKey(), (String) header.getValue());
            }
            SSLSocketFactory tlsSocketFactory2 = this.tlsSocketFactory;
            if (tlsSocketFactory2 != null && (urlConnection instanceof HttpsURLConnection)) {
                ((HttpsURLConnection) urlConnection).setSSLSocketFactory(tlsSocketFactory2);
            }
            try {
                OutputStream outputStream = urlConnection.getOutputStream();
                long bytesSend = 0;
                if (uploadProgressListener != null) {
                    int i = fileSizeInt;
                    try {
                        uploadProgressListener.onUploadProgress(0, fileSize);
                    } catch (Throwable th2) {
                        th = th2;
                        FileInputStream fileInputStream = fis;
                    }
                }
                try {
                    inputStream = new BufferedInputStream(fis);
                    buffer = new byte[4096];
                    while (true) {
                        try {
                            int read = inputStream.read(buffer);
                            bytesRead = read;
                            FileInputStream fis2 = fis;
                            if (read == -1) {
                                break;
                            }
                            int bytesRead2 = bytesRead;
                            try {
                                outputStream.write(buffer, 0, bytesRead2);
                                bytesSend += (long) bytesRead2;
                                UploadProgressListener uploadProgressListener2 = listener;
                                if (uploadProgressListener2 != null) {
                                    uploadProgressListener2.onUploadProgress(bytesSend, fileSize);
                                }
                                fis = fis2;
                            } catch (Throwable th3) {
                                th = th3;
                                byte[] bArr = buffer;
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    LOGGER.log(Level.WARNING, str2, e);
                                }
                                try {
                                    outputStream.close();
                                } catch (IOException e2) {
                                    LOGGER.log(Level.WARNING, str, e2);
                                }
                                throw th;
                            }
                        } catch (Throwable th4) {
                            byte[] bArr2 = buffer;
                            FileInputStream fileInputStream2 = fis;
                            th = th4;
                            inputStream.close();
                            outputStream.close();
                            throw th;
                        }
                    }
                    int i2 = bytesRead;
                    try {
                        outputStream.close();
                    } catch (IOException e3) {
                        LOGGER.log(Level.WARNING, str, e3);
                    }
                    int status = urlConnection.getResponseCode();
                    if (!(status == 200 || status == 201)) {
                        if (status != 204) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Error response ");
                            sb.append(status);
                            sb.append(" from server during file upload: ");
                            sb.append(urlConnection.getResponseMessage());
                            sb.append(", file size: ");
                            sb.append(fileSize);
                            sb.append(", put URL: ");
                            sb.append(putUrl);
                            throw new IOException(sb.toString());
                        }
                    }
                    urlConnection.disconnect();
                    return;
                } catch (Throwable th5) {
                    th = th5;
                    FileInputStream fileInputStream3 = fis;
                    urlConnection.disconnect();
                    throw th;
                }
                try {
                    inputStream.close();
                    byte[] bArr3 = buffer;
                } catch (IOException e4) {
                    byte[] bArr4 = buffer;
                    LOGGER.log(Level.WARNING, str2, e4);
                } catch (Throwable th6) {
                    th = th6;
                    urlConnection.disconnect();
                    throw th;
                }
            } catch (Throwable th7) {
                th = th7;
                int i3 = fileSizeInt;
                FileInputStream fileInputStream4 = fis;
                urlConnection.disconnect();
                throw th;
            }
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("File size ");
            sb2.append(fileSize);
            sb2.append(" must be less than ");
            sb2.append(ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0027  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x002e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.jivesoftware.smackx.httpfileupload.UploadService.Version namespaceToVersion(java.lang.String r3) {
        /*
            int r0 = r3.hashCode()
            r1 = -1906675379(0xffffffff8e5a714d, float:-2.6925127E-30)
            r2 = 1
            if (r0 == r1) goto L_0x001a
            r1 = -1320418345(0xffffffffb14c03d7, float:-2.968809E-9)
            if (r0 == r1) goto L_0x0010
        L_0x000f:
            goto L_0x0024
        L_0x0010:
            java.lang.String r0 = "urn:xmpp:http:upload"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000f
            r0 = r2
            goto L_0x0025
        L_0x001a:
            java.lang.String r0 = "urn:xmpp:http:upload:0"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x000f
            r0 = 0
            goto L_0x0025
        L_0x0024:
            r0 = -1
        L_0x0025:
            if (r0 == 0) goto L_0x002e
            if (r0 == r2) goto L_0x002b
            r0 = 0
            goto L_0x0031
        L_0x002b:
            org.jivesoftware.smackx.httpfileupload.UploadService$Version r0 = org.jivesoftware.smackx.httpfileupload.UploadService.Version.v0_2
            goto L_0x0031
        L_0x002e:
            org.jivesoftware.smackx.httpfileupload.UploadService$Version r0 = org.jivesoftware.smackx.httpfileupload.UploadService.Version.v0_3
        L_0x0031:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.httpfileupload.HttpFileUploadManager.namespaceToVersion(java.lang.String):org.jivesoftware.smackx.httpfileupload.UploadService$Version");
    }

    private static boolean containsHttpFileUploadNamespace(DiscoverInfo discoverInfo) {
        return discoverInfo.containsFeature("urn:xmpp:http:upload:0") || discoverInfo.containsFeature("urn:xmpp:http:upload");
    }
}
