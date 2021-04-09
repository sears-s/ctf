package org.bouncycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.util.Integers;

class DTLSReliableHandshake {
    private static final int MAX_RECEIVE_AHEAD = 16;
    private static final int MESSAGE_HEADER_LENGTH = 12;
    private Hashtable currentInboundFlight = new Hashtable();
    private TlsHandshakeHash handshakeHash;
    private int message_seq = 0;
    private int next_receive_seq = 0;
    private Vector outboundFlight = new Vector();
    private Hashtable previousInboundFlight = null;
    private DTLSRecordLayer recordLayer;
    private boolean sending = true;

    static class Message {
        private final byte[] body;
        private final int message_seq;
        private final short msg_type;

        private Message(int i, short s, byte[] bArr) {
            this.message_seq = i;
            this.msg_type = s;
            this.body = bArr;
        }

        public byte[] getBody() {
            return this.body;
        }

        public int getSeq() {
            return this.message_seq;
        }

        public short getType() {
            return this.msg_type;
        }
    }

    static class RecordLayerBuffer extends ByteArrayOutputStream {
        RecordLayerBuffer(int i) {
            super(i);
        }

        /* access modifiers changed from: 0000 */
        public void sendToRecordLayer(DTLSRecordLayer dTLSRecordLayer) throws IOException {
            dTLSRecordLayer.send(this.buf, 0, this.count);
            this.buf = null;
        }
    }

    DTLSReliableHandshake(TlsContext tlsContext, DTLSRecordLayer dTLSRecordLayer) {
        this.recordLayer = dTLSRecordLayer;
        this.handshakeHash = new DeferredHash();
        this.handshakeHash.init(tlsContext);
    }

    private int backOff(int i) {
        return Math.min(i * 2, 60000);
    }

    private static boolean checkAll(Hashtable hashtable) {
        Enumeration elements = hashtable.elements();
        while (elements.hasMoreElements()) {
            if (((DTLSReassembler) elements.nextElement()).getBodyIfComplete() == null) {
                return false;
            }
        }
        return true;
    }

    private void checkInboundFlight() {
        Enumeration keys = this.currentInboundFlight.keys();
        while (keys.hasMoreElements()) {
            ((Integer) keys.nextElement()).intValue();
            int i = this.next_receive_seq;
        }
    }

    private Message getPendingMessage() throws IOException {
        DTLSReassembler dTLSReassembler = (DTLSReassembler) this.currentInboundFlight.get(Integers.valueOf(this.next_receive_seq));
        if (dTLSReassembler != null) {
            byte[] bodyIfComplete = dTLSReassembler.getBodyIfComplete();
            if (bodyIfComplete != null) {
                this.previousInboundFlight = null;
                int i = this.next_receive_seq;
                this.next_receive_seq = i + 1;
                return updateHandshakeMessagesDigest(new Message(i, dTLSReassembler.getMsgType(), bodyIfComplete));
            }
        }
        return null;
    }

    private void prepareInboundFlight(Hashtable hashtable) {
        resetAll(this.currentInboundFlight);
        this.previousInboundFlight = this.currentInboundFlight;
        this.currentInboundFlight = hashtable;
    }

    /* access modifiers changed from: private */
    public boolean processRecord(int i, int i2, byte[] bArr, int i3, int i4) throws IOException {
        boolean z;
        byte[] bArr2 = bArr;
        int i5 = i3;
        int i6 = i4;
        boolean z2 = false;
        while (true) {
            z = true;
            if (i6 < 12) {
                break;
            }
            int readUint24 = TlsUtils.readUint24(bArr2, i5 + 9);
            int i7 = readUint24 + 12;
            if (i6 < i7) {
                break;
            }
            int readUint242 = TlsUtils.readUint24(bArr2, i5 + 1);
            int readUint243 = TlsUtils.readUint24(bArr2, i5 + 6);
            if (readUint243 + readUint24 > readUint242) {
                break;
            }
            short readUint8 = TlsUtils.readUint8(bArr2, i5 + 0);
            if (i2 != (readUint8 == 20 ? 1 : 0)) {
                break;
            }
            int readUint16 = TlsUtils.readUint16(bArr2, i5 + 4);
            int i8 = this.next_receive_seq;
            if (readUint16 < i8 + i) {
                if (readUint16 >= i8) {
                    DTLSReassembler dTLSReassembler = (DTLSReassembler) this.currentInboundFlight.get(Integers.valueOf(readUint16));
                    if (dTLSReassembler == null) {
                        dTLSReassembler = new DTLSReassembler(readUint8, readUint242);
                        this.currentInboundFlight.put(Integers.valueOf(readUint16), dTLSReassembler);
                    }
                    dTLSReassembler.contributeFragment(readUint8, readUint242, bArr, i5 + 12, readUint243, readUint24);
                } else {
                    Hashtable hashtable = this.previousInboundFlight;
                    if (hashtable != null) {
                        DTLSReassembler dTLSReassembler2 = (DTLSReassembler) hashtable.get(Integers.valueOf(readUint16));
                        if (dTLSReassembler2 != null) {
                            dTLSReassembler2.contributeFragment(readUint8, readUint242, bArr, i5 + 12, readUint243, readUint24);
                            z2 = true;
                        }
                    }
                }
            }
            i5 += i7;
            i6 -= i7;
        }
        if (!z2 || !checkAll(this.previousInboundFlight)) {
            z = false;
        }
        if (z) {
            resendOutboundFlight();
            resetAll(this.previousInboundFlight);
        }
        return z;
    }

    private void resendOutboundFlight() throws IOException {
        this.recordLayer.resetWriteEpoch();
        for (int i = 0; i < this.outboundFlight.size(); i++) {
            writeMessage((Message) this.outboundFlight.elementAt(i));
        }
    }

    private static void resetAll(Hashtable hashtable) {
        Enumeration elements = hashtable.elements();
        while (elements.hasMoreElements()) {
            ((DTLSReassembler) elements.nextElement()).reset();
        }
    }

    private Message updateHandshakeMessagesDigest(Message message) throws IOException {
        if (message.getType() != 0) {
            byte[] body = message.getBody();
            byte[] bArr = new byte[12];
            TlsUtils.writeUint8(message.getType(), bArr, 0);
            TlsUtils.writeUint24(body.length, bArr, 1);
            TlsUtils.writeUint16(message.getSeq(), bArr, 4);
            TlsUtils.writeUint24(0, bArr, 6);
            TlsUtils.writeUint24(body.length, bArr, 9);
            this.handshakeHash.update(bArr, 0, bArr.length);
            this.handshakeHash.update(body, 0, body.length);
        }
        return message;
    }

    private void writeHandshakeFragment(Message message, int i, int i2) throws IOException {
        RecordLayerBuffer recordLayerBuffer = new RecordLayerBuffer(i2 + 12);
        TlsUtils.writeUint8(message.getType(), (OutputStream) recordLayerBuffer);
        TlsUtils.writeUint24(message.getBody().length, recordLayerBuffer);
        TlsUtils.writeUint16(message.getSeq(), recordLayerBuffer);
        TlsUtils.writeUint24(i, recordLayerBuffer);
        TlsUtils.writeUint24(i2, recordLayerBuffer);
        recordLayerBuffer.write(message.getBody(), i, i2);
        recordLayerBuffer.sendToRecordLayer(this.recordLayer);
    }

    private void writeMessage(Message message) throws IOException {
        int sendLimit = this.recordLayer.getSendLimit() - 12;
        if (sendLimit >= 1) {
            int length = message.getBody().length;
            int i = 0;
            do {
                int min = Math.min(length - i, sendLimit);
                writeHandshakeFragment(message, i, min);
                i += min;
            } while (i < length);
            return;
        }
        throw new TlsFatalAlert(80);
    }

    /* access modifiers changed from: 0000 */
    public void finish() {
        AnonymousClass1 r1 = null;
        if (!this.sending) {
            checkInboundFlight();
        } else {
            prepareInboundFlight(null);
            if (this.previousInboundFlight != null) {
                r1 = new DTLSHandshakeRetransmit() {
                    public void receivedHandshakeRecord(int i, byte[] bArr, int i2, int i3) throws IOException {
                        DTLSReliableHandshake.this.processRecord(0, i, bArr, i2, i3);
                    }
                };
            }
        }
        this.recordLayer.handshakeSuccessful(r1);
    }

    /* access modifiers changed from: 0000 */
    public TlsHandshakeHash getHandshakeHash() {
        return this.handshakeHash;
    }

    /* access modifiers changed from: 0000 */
    public void notifyHelloComplete() {
        this.handshakeHash = this.handshakeHash.notifyPRFDetermined();
    }

    /* access modifiers changed from: 0000 */
    public TlsHandshakeHash prepareToFinish() {
        TlsHandshakeHash tlsHandshakeHash = this.handshakeHash;
        this.handshakeHash = tlsHandshakeHash.stopTracking();
        return tlsHandshakeHash;
    }

    /* access modifiers changed from: 0000 */
    public Message receiveMessage() throws IOException {
        if (this.sending) {
            this.sending = false;
            prepareInboundFlight(new Hashtable());
        }
        byte[] bArr = null;
        int i = 1000;
        while (true) {
            try {
                Message pendingMessage = getPendingMessage();
                if (pendingMessage != null) {
                    return pendingMessage;
                }
                int receiveLimit = this.recordLayer.getReceiveLimit();
                if (bArr == null || bArr.length < receiveLimit) {
                    bArr = new byte[receiveLimit];
                }
                int receive = this.recordLayer.receive(bArr, 0, receiveLimit, i);
                if (receive < 0) {
                    resendOutboundFlight();
                    i = backOff(i);
                } else {
                    if (processRecord(16, this.recordLayer.getReadEpoch(), bArr, 0, receive)) {
                        i = backOff(i);
                    }
                }
            } catch (IOException e) {
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public byte[] receiveMessageBody(short s) throws IOException {
        Message receiveMessage = receiveMessage();
        if (receiveMessage.getType() == s) {
            return receiveMessage.getBody();
        }
        throw new TlsFatalAlert(10);
    }

    /* access modifiers changed from: 0000 */
    public void resetHandshakeMessagesDigest() {
        this.handshakeHash.reset();
    }

    /* access modifiers changed from: 0000 */
    public void sendMessage(short s, byte[] bArr) throws IOException {
        TlsUtils.checkUint24(bArr.length);
        if (!this.sending) {
            checkInboundFlight();
            this.sending = true;
            this.outboundFlight.removeAllElements();
        }
        int i = this.message_seq;
        this.message_seq = i + 1;
        Message message = new Message(i, s, bArr);
        this.outboundFlight.addElement(message);
        writeMessage(message);
        updateHandshakeMessagesDigest(message);
    }
}
