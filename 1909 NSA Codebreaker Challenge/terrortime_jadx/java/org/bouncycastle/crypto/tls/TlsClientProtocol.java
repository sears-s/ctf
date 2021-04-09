package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.util.Arrays;

public class TlsClientProtocol extends TlsProtocol {
    protected TlsAuthentication authentication = null;
    protected CertificateRequest certificateRequest = null;
    protected CertificateStatus certificateStatus = null;
    protected TlsKeyExchange keyExchange = null;
    protected byte[] selectedSessionID = null;
    protected TlsClient tlsClient = null;
    TlsClientContextImpl tlsClientContext = null;

    public TlsClientProtocol(InputStream inputStream, OutputStream outputStream, SecureRandom secureRandom) {
        super(inputStream, outputStream, secureRandom);
    }

    public TlsClientProtocol(SecureRandom secureRandom) {
        super(secureRandom);
    }

    /* access modifiers changed from: protected */
    public void cleanupHandshake() {
        super.cleanupHandshake();
        this.selectedSessionID = null;
        this.keyExchange = null;
        this.authentication = null;
        this.certificateStatus = null;
        this.certificateRequest = null;
    }

    public void connect(TlsClient tlsClient2) throws IOException {
        if (tlsClient2 == null) {
            throw new IllegalArgumentException("'tlsClient' cannot be null");
        } else if (this.tlsClient == null) {
            this.tlsClient = tlsClient2;
            this.securityParameters = new SecurityParameters();
            this.securityParameters.entity = 1;
            this.tlsClientContext = new TlsClientContextImpl(this.secureRandom, this.securityParameters);
            this.securityParameters.clientRandom = createRandomBlock(tlsClient2.shouldUseGMTUnixTime(), this.tlsClientContext.getNonceRandomGenerator());
            this.tlsClient.init(this.tlsClientContext);
            this.recordStream.init(this.tlsClientContext);
            TlsSession sessionToResume = tlsClient2.getSessionToResume();
            if (sessionToResume != null && sessionToResume.isResumable()) {
                SessionParameters exportSessionParameters = sessionToResume.exportSessionParameters();
                if (exportSessionParameters != null && exportSessionParameters.isExtendedMasterSecret()) {
                    this.tlsSession = sessionToResume;
                    this.sessionParameters = exportSessionParameters;
                }
            }
            sendClientHelloMessage();
            this.connection_state = 1;
            blockForHandshake();
        } else {
            throw new IllegalStateException("'connect' can only be called once");
        }
    }

    /* access modifiers changed from: protected */
    public TlsContext getContext() {
        return this.tlsClientContext;
    }

    /* access modifiers changed from: 0000 */
    public AbstractTlsContext getContextAdmin() {
        return this.tlsClientContext;
    }

    /* access modifiers changed from: protected */
    public TlsPeer getPeer() {
        return this.tlsClient;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0058, code lost:
        r8.keyExchange.skipServerCredentials();
        r8.authentication = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x005f, code lost:
        r8.keyExchange.skipServerKeyExchange();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0064, code lost:
        assertEmpty(r10);
        r8.connection_state = 8;
        r8.recordStream.getHandshakeHash().sealHashAlgorithms();
        r9 = r8.tlsClient.getClientSupplementalData();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x007a, code lost:
        if (r9 == null) goto L_0x007f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x007c, code lost:
        sendSupplementalDataMessage(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x007f, code lost:
        r8.connection_state = 9;
        r9 = r8.certificateRequest;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0085, code lost:
        if (r9 != null) goto L_0x008e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0087, code lost:
        r8.keyExchange.skipClientCredentials();
        r9 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x008e, code lost:
        r9 = r8.authentication.getClientCredentials(r9);
        r10 = r8.keyExchange;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0096, code lost:
        if (r9 != null) goto L_0x009e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0098, code lost:
        r10.skipClientCredentials();
        r10 = org.bouncycastle.crypto.tls.Certificate.EMPTY_CHAIN;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x009e, code lost:
        r10.processClientCredentials(r9);
        r10 = r9.getCertificate();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00a5, code lost:
        sendCertificateMessage(r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00a8, code lost:
        r8.connection_state = 10;
        sendClientKeyExchangeMessage();
        r8.connection_state = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00b9, code lost:
        if (org.bouncycastle.crypto.tls.TlsUtils.isSSL(getContext()) == false) goto L_0x00c4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00bb, code lost:
        establishMasterSecret(getContext(), r8.keyExchange);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00c4, code lost:
        r10 = r8.recordStream.prepareToFinish();
        r8.securityParameters.sessionHash = getCurrentPRFHash(getContext(), r10, null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00de, code lost:
        if (org.bouncycastle.crypto.tls.TlsUtils.isSSL(getContext()) != false) goto L_0x00e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00e0, code lost:
        establishMasterSecret(getContext(), r8.keyExchange);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00e9, code lost:
        r8.recordStream.setPendingConnectionState(getPeer().getCompression(), getPeer().getCipher());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00fe, code lost:
        if (r9 == null) goto L_0x012f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0102, code lost:
        if ((r9 instanceof org.bouncycastle.crypto.tls.TlsSignerCredentials) == false) goto L_0x012f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0104, code lost:
        r9 = (org.bouncycastle.crypto.tls.TlsSignerCredentials) r9;
        r0 = org.bouncycastle.crypto.tls.TlsUtils.getSignatureAndHashAlgorithm(getContext(), r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x010e, code lost:
        if (r0 != null) goto L_0x0117;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0110, code lost:
        r10 = r8.securityParameters.getSessionHash();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0117, code lost:
        r10 = r10.getFinalHash(r0.getHash());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x011f, code lost:
        sendCertificateVerifyMessage(new org.bouncycastle.crypto.tls.DigitallySigned(r0, r9.generateCertificateSignature(r10)));
        r8.connection_state = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x012f, code lost:
        sendChangeCipherSpecMessage();
        sendFinishedMessage();
        r8.connection_state = 13;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleHandshakeMessage(short r9, java.io.ByteArrayInputStream r10) throws java.io.IOException {
        /*
            r8 = this;
            boolean r0 = r8.resumedSession
            r1 = 15
            r2 = 20
            r3 = 13
            r4 = 2
            r5 = 10
            if (r0 == 0) goto L_0x002a
            if (r9 != r2) goto L_0x0024
            short r9 = r8.connection_state
            if (r9 != r4) goto L_0x0024
            r8.processFinishedMessage(r10)
            r8.connection_state = r1
            r8.sendChangeCipherSpecMessage()
            r8.sendFinishedMessage()
            r8.connection_state = r3
            r8.completeHandshake()
            return
        L_0x0024:
            org.bouncycastle.crypto.tls.TlsFatalAlert r9 = new org.bouncycastle.crypto.tls.TlsFatalAlert
            r9.<init>(r5)
            throw r9
        L_0x002a:
            if (r9 == 0) goto L_0x02ac
            r0 = 0
            if (r9 == r4) goto L_0x025c
            r6 = 14
            r7 = 4
            if (r9 == r7) goto L_0x023f
            if (r9 == r2) goto L_0x021e
            r1 = 22
            r2 = 5
            if (r9 == r1) goto L_0x01fd
            r1 = 23
            if (r9 == r1) goto L_0x01ea
            r1 = 6
            r6 = 3
            switch(r9) {
                case 11: goto L_0x01a8;
                case 12: goto L_0x0181;
                case 13: goto L_0x0139;
                case 14: goto L_0x004a;
                default: goto L_0x0044;
            }
        L_0x0044:
            org.bouncycastle.crypto.tls.TlsFatalAlert r9 = new org.bouncycastle.crypto.tls.TlsFatalAlert
            r9.<init>(r5)
            throw r9
        L_0x004a:
            short r9 = r8.connection_state
            switch(r9) {
                case 2: goto L_0x0055;
                case 3: goto L_0x0058;
                case 4: goto L_0x005f;
                case 5: goto L_0x005f;
                case 6: goto L_0x0064;
                case 7: goto L_0x0064;
                default: goto L_0x004f;
            }
        L_0x004f:
            org.bouncycastle.crypto.tls.TlsFatalAlert r9 = new org.bouncycastle.crypto.tls.TlsFatalAlert
            r9.<init>(r5)
            throw r9
        L_0x0055:
            r8.handleSupplementalData(r0)
        L_0x0058:
            org.bouncycastle.crypto.tls.TlsKeyExchange r9 = r8.keyExchange
            r9.skipServerCredentials()
            r8.authentication = r0
        L_0x005f:
            org.bouncycastle.crypto.tls.TlsKeyExchange r9 = r8.keyExchange
            r9.skipServerKeyExchange()
        L_0x0064:
            assertEmpty(r10)
            r9 = 8
            r8.connection_state = r9
            org.bouncycastle.crypto.tls.RecordStream r9 = r8.recordStream
            org.bouncycastle.crypto.tls.TlsHandshakeHash r9 = r9.getHandshakeHash()
            r9.sealHashAlgorithms()
            org.bouncycastle.crypto.tls.TlsClient r9 = r8.tlsClient
            java.util.Vector r9 = r9.getClientSupplementalData()
            if (r9 == 0) goto L_0x007f
            r8.sendSupplementalDataMessage(r9)
        L_0x007f:
            r9 = 9
            r8.connection_state = r9
            org.bouncycastle.crypto.tls.CertificateRequest r9 = r8.certificateRequest
            if (r9 != 0) goto L_0x008e
            org.bouncycastle.crypto.tls.TlsKeyExchange r9 = r8.keyExchange
            r9.skipClientCredentials()
            r9 = r0
            goto L_0x00a8
        L_0x008e:
            org.bouncycastle.crypto.tls.TlsAuthentication r10 = r8.authentication
            org.bouncycastle.crypto.tls.TlsCredentials r9 = r10.getClientCredentials(r9)
            org.bouncycastle.crypto.tls.TlsKeyExchange r10 = r8.keyExchange
            if (r9 != 0) goto L_0x009e
            r10.skipClientCredentials()
            org.bouncycastle.crypto.tls.Certificate r10 = org.bouncycastle.crypto.tls.Certificate.EMPTY_CHAIN
            goto L_0x00a5
        L_0x009e:
            r10.processClientCredentials(r9)
            org.bouncycastle.crypto.tls.Certificate r10 = r9.getCertificate()
        L_0x00a5:
            r8.sendCertificateMessage(r10)
        L_0x00a8:
            r8.connection_state = r5
            r8.sendClientKeyExchangeMessage()
            r10 = 11
            r8.connection_state = r10
            org.bouncycastle.crypto.tls.TlsContext r10 = r8.getContext()
            boolean r10 = org.bouncycastle.crypto.tls.TlsUtils.isSSL(r10)
            if (r10 == 0) goto L_0x00c4
            org.bouncycastle.crypto.tls.TlsContext r10 = r8.getContext()
            org.bouncycastle.crypto.tls.TlsKeyExchange r1 = r8.keyExchange
            establishMasterSecret(r10, r1)
        L_0x00c4:
            org.bouncycastle.crypto.tls.RecordStream r10 = r8.recordStream
            org.bouncycastle.crypto.tls.TlsHandshakeHash r10 = r10.prepareToFinish()
            org.bouncycastle.crypto.tls.SecurityParameters r1 = r8.securityParameters
            org.bouncycastle.crypto.tls.TlsContext r2 = r8.getContext()
            byte[] r0 = getCurrentPRFHash(r2, r10, r0)
            r1.sessionHash = r0
            org.bouncycastle.crypto.tls.TlsContext r0 = r8.getContext()
            boolean r0 = org.bouncycastle.crypto.tls.TlsUtils.isSSL(r0)
            if (r0 != 0) goto L_0x00e9
            org.bouncycastle.crypto.tls.TlsContext r0 = r8.getContext()
            org.bouncycastle.crypto.tls.TlsKeyExchange r1 = r8.keyExchange
            establishMasterSecret(r0, r1)
        L_0x00e9:
            org.bouncycastle.crypto.tls.RecordStream r0 = r8.recordStream
            org.bouncycastle.crypto.tls.TlsPeer r1 = r8.getPeer()
            org.bouncycastle.crypto.tls.TlsCompression r1 = r1.getCompression()
            org.bouncycastle.crypto.tls.TlsPeer r2 = r8.getPeer()
            org.bouncycastle.crypto.tls.TlsCipher r2 = r2.getCipher()
            r0.setPendingConnectionState(r1, r2)
            if (r9 == 0) goto L_0x012f
            boolean r0 = r9 instanceof org.bouncycastle.crypto.tls.TlsSignerCredentials
            if (r0 == 0) goto L_0x012f
            org.bouncycastle.crypto.tls.TlsSignerCredentials r9 = (org.bouncycastle.crypto.tls.TlsSignerCredentials) r9
            org.bouncycastle.crypto.tls.TlsContext r0 = r8.getContext()
            org.bouncycastle.crypto.tls.SignatureAndHashAlgorithm r0 = org.bouncycastle.crypto.tls.TlsUtils.getSignatureAndHashAlgorithm(r0, r9)
            if (r0 != 0) goto L_0x0117
            org.bouncycastle.crypto.tls.SecurityParameters r10 = r8.securityParameters
            byte[] r10 = r10.getSessionHash()
            goto L_0x011f
        L_0x0117:
            short r1 = r0.getHash()
            byte[] r10 = r10.getFinalHash(r1)
        L_0x011f:
            byte[] r9 = r9.generateCertificateSignature(r10)
            org.bouncycastle.crypto.tls.DigitallySigned r10 = new org.bouncycastle.crypto.tls.DigitallySigned
            r10.<init>(r0, r9)
            r8.sendCertificateVerifyMessage(r10)
            r9 = 12
            r8.connection_state = r9
        L_0x012f:
            r8.sendChangeCipherSpecMessage()
            r8.sendFinishedMessage()
            r8.connection_state = r3
            goto L_0x02b8
        L_0x0139:
            short r9 = r8.connection_state
            if (r9 == r7) goto L_0x0148
            if (r9 == r2) goto L_0x0148
            if (r9 != r1) goto L_0x0142
            goto L_0x014d
        L_0x0142:
            org.bouncycastle.crypto.tls.TlsFatalAlert r9 = new org.bouncycastle.crypto.tls.TlsFatalAlert
            r9.<init>(r5)
            throw r9
        L_0x0148:
            org.bouncycastle.crypto.tls.TlsKeyExchange r9 = r8.keyExchange
            r9.skipServerKeyExchange()
        L_0x014d:
            org.bouncycastle.crypto.tls.TlsAuthentication r9 = r8.authentication
            if (r9 == 0) goto L_0x0179
            org.bouncycastle.crypto.tls.TlsContext r9 = r8.getContext()
            org.bouncycastle.crypto.tls.CertificateRequest r9 = org.bouncycastle.crypto.tls.CertificateRequest.parse(r9, r10)
            r8.certificateRequest = r9
            assertEmpty(r10)
            org.bouncycastle.crypto.tls.TlsKeyExchange r9 = r8.keyExchange
            org.bouncycastle.crypto.tls.CertificateRequest r10 = r8.certificateRequest
            r9.validateCertificateRequest(r10)
            org.bouncycastle.crypto.tls.RecordStream r9 = r8.recordStream
            org.bouncycastle.crypto.tls.TlsHandshakeHash r9 = r9.getHandshakeHash()
            org.bouncycastle.crypto.tls.CertificateRequest r10 = r8.certificateRequest
            java.util.Vector r10 = r10.getSupportedSignatureAlgorithms()
            org.bouncycastle.crypto.tls.TlsUtils.trackHashAlgorithms(r9, r10)
            r9 = 7
            r8.connection_state = r9
            goto L_0x02b8
        L_0x0179:
            org.bouncycastle.crypto.tls.TlsFatalAlert r9 = new org.bouncycastle.crypto.tls.TlsFatalAlert
            r10 = 40
            r9.<init>(r10)
            throw r9
        L_0x0181:
            short r9 = r8.connection_state
            if (r9 == r4) goto L_0x0192
            if (r9 == r6) goto L_0x0195
            if (r9 == r7) goto L_0x019c
            if (r9 != r2) goto L_0x018c
            goto L_0x019c
        L_0x018c:
            org.bouncycastle.crypto.tls.TlsFatalAlert r9 = new org.bouncycastle.crypto.tls.TlsFatalAlert
            r9.<init>(r5)
            throw r9
        L_0x0192:
            r8.handleSupplementalData(r0)
        L_0x0195:
            org.bouncycastle.crypto.tls.TlsKeyExchange r9 = r8.keyExchange
            r9.skipServerCredentials()
            r8.authentication = r0
        L_0x019c:
            org.bouncycastle.crypto.tls.TlsKeyExchange r9 = r8.keyExchange
            r9.processServerKeyExchange(r10)
            assertEmpty(r10)
            r8.connection_state = r1
            goto L_0x02b8
        L_0x01a8:
            short r9 = r8.connection_state
            if (r9 == r4) goto L_0x01b5
            if (r9 != r6) goto L_0x01af
            goto L_0x01b8
        L_0x01af:
            org.bouncycastle.crypto.tls.TlsFatalAlert r9 = new org.bouncycastle.crypto.tls.TlsFatalAlert
            r9.<init>(r5)
            throw r9
        L_0x01b5:
            r8.handleSupplementalData(r0)
        L_0x01b8:
            org.bouncycastle.crypto.tls.Certificate r9 = org.bouncycastle.crypto.tls.Certificate.parse(r10)
            r8.peerCertificate = r9
            assertEmpty(r10)
            org.bouncycastle.crypto.tls.Certificate r9 = r8.peerCertificate
            if (r9 == 0) goto L_0x01cd
            org.bouncycastle.crypto.tls.Certificate r9 = r8.peerCertificate
            boolean r9 = r9.isEmpty()
            if (r9 == 0) goto L_0x01d0
        L_0x01cd:
            r9 = 0
            r8.allowCertificateStatus = r9
        L_0x01d0:
            org.bouncycastle.crypto.tls.TlsKeyExchange r9 = r8.keyExchange
            org.bouncycastle.crypto.tls.Certificate r10 = r8.peerCertificate
            r9.processServerCertificate(r10)
            org.bouncycastle.crypto.tls.TlsClient r9 = r8.tlsClient
            org.bouncycastle.crypto.tls.TlsAuthentication r9 = r9.getAuthentication()
            r8.authentication = r9
            org.bouncycastle.crypto.tls.TlsAuthentication r9 = r8.authentication
            org.bouncycastle.crypto.tls.Certificate r10 = r8.peerCertificate
            r9.notifyServerCertificate(r10)
            r8.connection_state = r7
            goto L_0x02b8
        L_0x01ea:
            short r9 = r8.connection_state
            if (r9 != r4) goto L_0x01f7
            java.util.Vector r9 = readSupplementalDataMessage(r10)
            r8.handleSupplementalData(r9)
            goto L_0x02b8
        L_0x01f7:
            org.bouncycastle.crypto.tls.TlsFatalAlert r9 = new org.bouncycastle.crypto.tls.TlsFatalAlert
            r9.<init>(r5)
            throw r9
        L_0x01fd:
            short r9 = r8.connection_state
            if (r9 != r7) goto L_0x0218
            boolean r9 = r8.allowCertificateStatus
            if (r9 == 0) goto L_0x0212
            org.bouncycastle.crypto.tls.CertificateStatus r9 = org.bouncycastle.crypto.tls.CertificateStatus.parse(r10)
            r8.certificateStatus = r9
            assertEmpty(r10)
            r8.connection_state = r2
            goto L_0x02b8
        L_0x0212:
            org.bouncycastle.crypto.tls.TlsFatalAlert r9 = new org.bouncycastle.crypto.tls.TlsFatalAlert
            r9.<init>(r5)
            throw r9
        L_0x0218:
            org.bouncycastle.crypto.tls.TlsFatalAlert r9 = new org.bouncycastle.crypto.tls.TlsFatalAlert
            r9.<init>(r5)
            throw r9
        L_0x021e:
            short r9 = r8.connection_state
            if (r9 == r3) goto L_0x022b
            if (r9 != r6) goto L_0x0225
            goto L_0x022f
        L_0x0225:
            org.bouncycastle.crypto.tls.TlsFatalAlert r9 = new org.bouncycastle.crypto.tls.TlsFatalAlert
            r9.<init>(r5)
            throw r9
        L_0x022b:
            boolean r9 = r8.expectSessionTicket
            if (r9 != 0) goto L_0x0239
        L_0x022f:
            r8.processFinishedMessage(r10)
            r8.connection_state = r1
            r8.completeHandshake()
            goto L_0x02b8
        L_0x0239:
            org.bouncycastle.crypto.tls.TlsFatalAlert r9 = new org.bouncycastle.crypto.tls.TlsFatalAlert
            r9.<init>(r5)
            throw r9
        L_0x023f:
            short r9 = r8.connection_state
            if (r9 != r3) goto L_0x0256
            boolean r9 = r8.expectSessionTicket
            if (r9 == 0) goto L_0x0250
            r8.invalidateSession()
            r8.receiveNewSessionTicketMessage(r10)
            r8.connection_state = r6
            goto L_0x02b8
        L_0x0250:
            org.bouncycastle.crypto.tls.TlsFatalAlert r9 = new org.bouncycastle.crypto.tls.TlsFatalAlert
            r9.<init>(r5)
            throw r9
        L_0x0256:
            org.bouncycastle.crypto.tls.TlsFatalAlert r9 = new org.bouncycastle.crypto.tls.TlsFatalAlert
            r9.<init>(r5)
            throw r9
        L_0x025c:
            short r9 = r8.connection_state
            r1 = 1
            if (r9 != r1) goto L_0x02a6
            r8.receiveServerHelloMessage(r10)
            r8.connection_state = r4
            org.bouncycastle.crypto.tls.RecordStream r9 = r8.recordStream
            r9.notifyHelloComplete()
            r8.applyMaxFragmentLengthExtension()
            boolean r9 = r8.resumedSession
            if (r9 == 0) goto L_0x0296
            org.bouncycastle.crypto.tls.SecurityParameters r9 = r8.securityParameters
            org.bouncycastle.crypto.tls.SessionParameters r10 = r8.sessionParameters
            byte[] r10 = r10.getMasterSecret()
            byte[] r10 = org.bouncycastle.util.Arrays.clone(r10)
            r9.masterSecret = r10
            org.bouncycastle.crypto.tls.RecordStream r9 = r8.recordStream
            org.bouncycastle.crypto.tls.TlsPeer r10 = r8.getPeer()
            org.bouncycastle.crypto.tls.TlsCompression r10 = r10.getCompression()
            org.bouncycastle.crypto.tls.TlsPeer r0 = r8.getPeer()
            org.bouncycastle.crypto.tls.TlsCipher r0 = r0.getCipher()
            r9.setPendingConnectionState(r10, r0)
            goto L_0x02b8
        L_0x0296:
            r8.invalidateSession()
            byte[] r9 = r8.selectedSessionID
            int r10 = r9.length
            if (r10 <= 0) goto L_0x02b8
            org.bouncycastle.crypto.tls.TlsSessionImpl r10 = new org.bouncycastle.crypto.tls.TlsSessionImpl
            r10.<init>(r9, r0)
            r8.tlsSession = r10
            goto L_0x02b8
        L_0x02a6:
            org.bouncycastle.crypto.tls.TlsFatalAlert r9 = new org.bouncycastle.crypto.tls.TlsFatalAlert
            r9.<init>(r5)
            throw r9
        L_0x02ac:
            assertEmpty(r10)
            short r9 = r8.connection_state
            r10 = 16
            if (r9 != r10) goto L_0x02b8
            r8.refuseRenegotiation()
        L_0x02b8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.crypto.tls.TlsClientProtocol.handleHandshakeMessage(short, java.io.ByteArrayInputStream):void");
    }

    /* access modifiers changed from: protected */
    public void handleSupplementalData(Vector vector) throws IOException {
        this.tlsClient.processServerSupplementalData(vector);
        this.connection_state = 3;
        this.keyExchange = this.tlsClient.getKeyExchange();
        this.keyExchange.init(getContext());
    }

    /* access modifiers changed from: protected */
    public void receiveNewSessionTicketMessage(ByteArrayInputStream byteArrayInputStream) throws IOException {
        NewSessionTicket parse = NewSessionTicket.parse(byteArrayInputStream);
        assertEmpty(byteArrayInputStream);
        this.tlsClient.notifyNewSessionTicket(parse);
    }

    /* access modifiers changed from: protected */
    public void receiveServerHelloMessage(ByteArrayInputStream byteArrayInputStream) throws IOException {
        ProtocolVersion readVersion = TlsUtils.readVersion(byteArrayInputStream);
        if (readVersion.isDTLS()) {
            throw new TlsFatalAlert(47);
        } else if (!readVersion.equals(this.recordStream.getReadVersion())) {
            throw new TlsFatalAlert(47);
        } else if (readVersion.isEqualOrEarlierVersionOf(getContext().getClientVersion())) {
            this.recordStream.setWriteVersion(readVersion);
            getContextAdmin().setServerVersion(readVersion);
            this.tlsClient.notifyServerVersion(readVersion);
            this.securityParameters.serverRandom = TlsUtils.readFully(32, (InputStream) byteArrayInputStream);
            this.selectedSessionID = TlsUtils.readOpaque8(byteArrayInputStream);
            byte[] bArr = this.selectedSessionID;
            if (bArr.length <= 32) {
                this.tlsClient.notifySessionID(bArr);
                boolean z = false;
                this.resumedSession = this.selectedSessionID.length > 0 && this.tlsSession != null && Arrays.areEqual(this.selectedSessionID, this.tlsSession.getSessionID());
                int readUint16 = TlsUtils.readUint16(byteArrayInputStream);
                if (!Arrays.contains(this.offeredCipherSuites, readUint16) || readUint16 == 0 || CipherSuite.isSCSV(readUint16) || !TlsUtils.isValidCipherSuiteForVersion(readUint16, getContext().getServerVersion())) {
                    throw new TlsFatalAlert(47);
                }
                this.tlsClient.notifySelectedCipherSuite(readUint16);
                short readUint8 = TlsUtils.readUint8(byteArrayInputStream);
                if (Arrays.contains(this.offeredCompressionMethods, readUint8)) {
                    this.tlsClient.notifySelectedCompressionMethod(readUint8);
                    this.serverExtensions = readExtensions(byteArrayInputStream);
                    this.securityParameters.extendedMasterSecret = !TlsUtils.isSSL(this.tlsClientContext) && TlsExtensionsUtils.hasExtendedMasterSecretExtension(this.serverExtensions);
                    if (this.securityParameters.isExtendedMasterSecret() || (!this.resumedSession && !this.tlsClient.requiresExtendedMasterSecret())) {
                        if (this.serverExtensions != null) {
                            Enumeration keys = this.serverExtensions.keys();
                            while (keys.hasMoreElements()) {
                                Integer num = (Integer) keys.nextElement();
                                if (!num.equals(EXT_RenegotiationInfo)) {
                                    if (TlsUtils.getExtensionData(this.clientExtensions, num) != null) {
                                        boolean z2 = this.resumedSession;
                                    } else {
                                        throw new TlsFatalAlert(AlertDescription.unsupported_extension);
                                    }
                                }
                            }
                        }
                        byte[] extensionData = TlsUtils.getExtensionData(this.serverExtensions, EXT_RenegotiationInfo);
                        if (extensionData != null) {
                            this.secure_renegotiation = true;
                            if (!Arrays.constantTimeAreEqual(extensionData, createRenegotiationInfo(TlsUtils.EMPTY_BYTES))) {
                                throw new TlsFatalAlert(40);
                            }
                        }
                        this.tlsClient.notifySecureRenegotiation(this.secure_renegotiation);
                        Hashtable hashtable = this.clientExtensions;
                        Hashtable hashtable2 = this.serverExtensions;
                        if (this.resumedSession) {
                            if (readUint16 == this.sessionParameters.getCipherSuite() && readUint8 == this.sessionParameters.getCompressionAlgorithm()) {
                                hashtable = null;
                                hashtable2 = this.sessionParameters.readServerExtensions();
                            } else {
                                throw new TlsFatalAlert(47);
                            }
                        }
                        this.securityParameters.cipherSuite = readUint16;
                        this.securityParameters.compressionAlgorithm = readUint8;
                        if (hashtable2 != null && !hashtable2.isEmpty()) {
                            boolean hasEncryptThenMACExtension = TlsExtensionsUtils.hasEncryptThenMACExtension(hashtable2);
                            if (!hasEncryptThenMACExtension || TlsUtils.isBlockCipherSuite(readUint16)) {
                                this.securityParameters.encryptThenMAC = hasEncryptThenMACExtension;
                                this.securityParameters.maxFragmentLength = processMaxFragmentLengthExtension(hashtable, hashtable2, 47);
                                this.securityParameters.truncatedHMac = TlsExtensionsUtils.hasTruncatedHMacExtension(hashtable2);
                                this.allowCertificateStatus = !this.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(hashtable2, TlsExtensionsUtils.EXT_status_request, 47);
                                if (!this.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(hashtable2, TlsProtocol.EXT_SessionTicket, 47)) {
                                    z = true;
                                }
                                this.expectSessionTicket = z;
                            } else {
                                throw new TlsFatalAlert(47);
                            }
                        }
                        if (hashtable != null) {
                            this.tlsClient.processServerExtensions(hashtable2);
                        }
                        this.securityParameters.prfAlgorithm = getPRFAlgorithm(getContext(), this.securityParameters.getCipherSuite());
                        this.securityParameters.verifyDataLength = 12;
                        return;
                    }
                    throw new TlsFatalAlert(40);
                }
                throw new TlsFatalAlert(47);
            }
            throw new TlsFatalAlert(47);
        } else {
            throw new TlsFatalAlert(47);
        }
    }

    /* access modifiers changed from: protected */
    public void sendCertificateVerifyMessage(DigitallySigned digitallySigned) throws IOException {
        HandshakeMessage handshakeMessage = new HandshakeMessage(this, 15);
        digitallySigned.encode(handshakeMessage);
        handshakeMessage.writeToRecordStream();
    }

    /* access modifiers changed from: protected */
    public void sendClientHelloMessage() throws IOException {
        this.recordStream.setWriteVersion(this.tlsClient.getClientHelloRecordLayerVersion());
        ProtocolVersion clientVersion = this.tlsClient.getClientVersion();
        if (!clientVersion.isDTLS()) {
            getContextAdmin().setClientVersion(clientVersion);
            byte[] bArr = TlsUtils.EMPTY_BYTES;
            if (this.tlsSession != null) {
                bArr = this.tlsSession.getSessionID();
                if (bArr == null || bArr.length > 32) {
                    bArr = TlsUtils.EMPTY_BYTES;
                }
            }
            boolean isFallback = this.tlsClient.isFallback();
            this.offeredCipherSuites = this.tlsClient.getCipherSuites();
            this.offeredCompressionMethods = this.tlsClient.getCompressionMethods();
            if (bArr.length > 0 && this.sessionParameters != null && (!this.sessionParameters.isExtendedMasterSecret() || !Arrays.contains(this.offeredCipherSuites, this.sessionParameters.getCipherSuite()) || !Arrays.contains(this.offeredCompressionMethods, this.sessionParameters.getCompressionAlgorithm()))) {
                bArr = TlsUtils.EMPTY_BYTES;
            }
            this.clientExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(this.tlsClient.getClientExtensions());
            if (!clientVersion.isSSL()) {
                TlsExtensionsUtils.addExtendedMasterSecretExtension(this.clientExtensions);
            }
            HandshakeMessage handshakeMessage = new HandshakeMessage(this, 1);
            TlsUtils.writeVersion(clientVersion, handshakeMessage);
            handshakeMessage.write(this.securityParameters.getClientRandom());
            TlsUtils.writeOpaque8(bArr, handshakeMessage);
            boolean z = !Arrays.contains(this.offeredCipherSuites, 255);
            if ((TlsUtils.getExtensionData(this.clientExtensions, EXT_RenegotiationInfo) == null) && z) {
                this.offeredCipherSuites = Arrays.append(this.offeredCipherSuites, 255);
            }
            if (isFallback && !Arrays.contains(this.offeredCipherSuites, (int) CipherSuite.TLS_FALLBACK_SCSV)) {
                this.offeredCipherSuites = Arrays.append(this.offeredCipherSuites, (int) CipherSuite.TLS_FALLBACK_SCSV);
            }
            TlsUtils.writeUint16ArrayWithUint16Length(this.offeredCipherSuites, handshakeMessage);
            TlsUtils.writeUint8ArrayWithUint8Length(this.offeredCompressionMethods, handshakeMessage);
            writeExtensions(handshakeMessage, this.clientExtensions);
            handshakeMessage.writeToRecordStream();
            return;
        }
        throw new TlsFatalAlert(80);
    }

    /* access modifiers changed from: protected */
    public void sendClientKeyExchangeMessage() throws IOException {
        HandshakeMessage handshakeMessage = new HandshakeMessage(this, 16);
        this.keyExchange.generateClientKeyExchange(handshakeMessage);
        handshakeMessage.writeToRecordStream();
    }
}
