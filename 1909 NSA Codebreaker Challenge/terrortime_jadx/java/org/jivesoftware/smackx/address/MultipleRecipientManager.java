package org.jivesoftware.smackx.address;

import java.util.ArrayList;
import java.util.Collection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.FeatureNotSupportedException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.address.packet.MultipleAddresses;
import org.jivesoftware.smackx.address.packet.MultipleAddresses.Address;
import org.jivesoftware.smackx.address.packet.MultipleAddresses.Type;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;

public class MultipleRecipientManager {

    private static final class PacketCopy extends Stanza {
        private final CharSequence text;

        private PacketCopy(CharSequence text2) {
            this.text = text2;
        }

        public CharSequence toXML(String enclosingNamespace) {
            return this.text;
        }

        public String toString() {
            return toXML(null).toString();
        }
    }

    public static void send(XMPPConnection connection, Stanza packet, Collection<? extends Jid> to, Collection<? extends Jid> cc, Collection<? extends Jid> bcc) throws NoResponseException, XMPPErrorException, FeatureNotSupportedException, NotConnectedException, InterruptedException {
        send(connection, packet, to, cc, bcc, null, null, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0055  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void send(org.jivesoftware.smack.XMPPConnection r11, org.jivesoftware.smack.packet.Stanza r12, java.util.Collection<? extends org.jxmpp.jid.Jid> r13, java.util.Collection<? extends org.jxmpp.jid.Jid> r14, java.util.Collection<? extends org.jxmpp.jid.Jid> r15, org.jxmpp.jid.Jid r16, org.jxmpp.jid.Jid r17, boolean r18) throws org.jivesoftware.smack.SmackException.NoResponseException, org.jivesoftware.smack.XMPPException.XMPPErrorException, org.jivesoftware.smack.SmackException.FeatureNotSupportedException, org.jivesoftware.smack.SmackException.NotConnectedException, java.lang.InterruptedException {
        /*
            if (r13 == 0) goto L_0x003e
            int r0 = r13.size()
            r1 = 1
            if (r0 != r1) goto L_0x003e
            if (r14 == 0) goto L_0x0014
            boolean r0 = r14.isEmpty()
            if (r0 == 0) goto L_0x0012
            goto L_0x0014
        L_0x0012:
            r9 = r12
            goto L_0x003f
        L_0x0014:
            if (r15 == 0) goto L_0x001c
            boolean r0 = r15.isEmpty()
            if (r0 == 0) goto L_0x0012
        L_0x001c:
            if (r18 != 0) goto L_0x003e
            boolean r0 = org.jivesoftware.smack.util.StringUtils.isNullOrEmpty(r16)
            if (r0 == 0) goto L_0x003c
            boolean r0 = org.jivesoftware.smack.util.StringUtils.isNullOrEmpty(r17)
            if (r0 == 0) goto L_0x003c
            java.util.Iterator r0 = r13.iterator()
            java.lang.Object r0 = r0.next()
            org.jxmpp.jid.Jid r0 = (org.jxmpp.jid.Jid) r0
            r9 = r12
            r12.setTo(r0)
            r11.sendStanza(r12)
            return
        L_0x003c:
            r9 = r12
            goto L_0x003f
        L_0x003e:
            r9 = r12
        L_0x003f:
            org.jxmpp.jid.DomainBareJid r10 = getMultipleRecipientServiceAddress(r11)
            if (r10 == 0) goto L_0x0055
            r0 = r11
            r1 = r12
            r2 = r13
            r3 = r14
            r4 = r15
            r5 = r16
            r6 = r17
            r7 = r18
            r8 = r10
            sendThroughService(r0, r1, r2, r3, r4, r5, r6, r7, r8)
            goto L_0x005e
        L_0x0055:
            if (r18 != 0) goto L_0x005f
            if (r16 != 0) goto L_0x005f
            if (r17 != 0) goto L_0x005f
            sendToIndividualRecipients(r11, r12, r13, r14, r15)
        L_0x005e:
            return
        L_0x005f:
            org.jivesoftware.smack.SmackException$FeatureNotSupportedException r0 = new org.jivesoftware.smack.SmackException$FeatureNotSupportedException
            java.lang.String r1 = "Extended Stanza Addressing"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.address.MultipleRecipientManager.send(org.jivesoftware.smack.XMPPConnection, org.jivesoftware.smack.packet.Stanza, java.util.Collection, java.util.Collection, java.util.Collection, org.jxmpp.jid.Jid, org.jxmpp.jid.Jid, boolean):void");
    }

    public static void reply(XMPPConnection connection, Message original, Message reply) throws SmackException, XMPPErrorException, InterruptedException {
        MultipleRecipientInfo info = getMultipleRecipientInfo(original);
        if (info == null) {
            throw new SmackException("Original message does not contain multiple recipient info");
        } else if (info.shouldNotReply()) {
            throw new SmackException("Original message should not be replied");
        } else if (info.getReplyRoom() == null) {
            if (original.getThread() != null) {
                reply.setThread(original.getThread());
            }
            Address replyAddress = info.getReplyAddress();
            if (replyAddress == null || replyAddress.getJid() == null) {
                ArrayList arrayList = new ArrayList(info.getTOAddresses().size());
                ArrayList arrayList2 = new ArrayList(info.getCCAddresses().size());
                for (Address jid : info.getTOAddresses()) {
                    arrayList.add(jid.getJid());
                }
                for (Address jid2 : info.getCCAddresses()) {
                    arrayList2.add(jid2.getJid());
                }
                if (!arrayList.contains(original.getFrom()) && !arrayList2.contains(original.getFrom())) {
                    arrayList.add(original.getFrom());
                }
                EntityFullJid from = connection.getUser();
                if (!arrayList.remove(from) && !arrayList2.remove(from)) {
                    EntityBareJid bareJID = from.asEntityBareJid();
                    arrayList.remove(bareJID);
                    arrayList2.remove(bareJID);
                }
                send(connection, reply, arrayList, arrayList2, null, null, null, false);
                return;
            }
            reply.setTo(replyAddress.getJid());
            connection.sendStanza(reply);
        } else {
            throw new SmackException("Reply should be sent through a room");
        }
    }

    public static MultipleRecipientInfo getMultipleRecipientInfo(Stanza packet) {
        MultipleAddresses extension = (MultipleAddresses) packet.getExtension(MultipleAddresses.ELEMENT, MultipleAddresses.NAMESPACE);
        if (extension == null) {
            return null;
        }
        return new MultipleRecipientInfo(extension);
    }

    private static void sendToIndividualRecipients(XMPPConnection connection, Stanza packet, Collection<? extends Jid> to, Collection<? extends Jid> cc, Collection<? extends Jid> bcc) throws NotConnectedException, InterruptedException {
        if (to != null) {
            for (Jid jid : to) {
                packet.setTo(jid);
                connection.sendStanza(new PacketCopy(packet.toXML(null)));
            }
        }
        if (cc != null) {
            for (Jid jid2 : cc) {
                packet.setTo(jid2);
                connection.sendStanza(new PacketCopy(packet.toXML(null)));
            }
        }
        if (bcc != null) {
            for (Jid jid3 : bcc) {
                packet.setTo(jid3);
                connection.sendStanza(new PacketCopy(packet.toXML(null)));
            }
        }
    }

    private static void sendThroughService(XMPPConnection connection, Stanza packet, Collection<? extends Jid> to, Collection<? extends Jid> cc, Collection<? extends Jid> bcc, Jid replyTo, Jid replyRoom, boolean noReply, DomainBareJid serviceAddress) throws NotConnectedException, InterruptedException {
        Stanza stanza = packet;
        MultipleAddresses multipleAddresses = new MultipleAddresses();
        if (to != null) {
            for (Jid jid : to) {
                MultipleAddresses multipleAddresses2 = multipleAddresses;
                multipleAddresses2.addAddress(Type.to, jid, null, null, false, null);
            }
        }
        if (cc != null) {
            for (Jid jid2 : cc) {
                MultipleAddresses multipleAddresses3 = multipleAddresses;
                multipleAddresses3.addAddress(Type.to, jid2, null, null, false, null);
            }
        }
        if (bcc != null) {
            for (Jid jid3 : bcc) {
                MultipleAddresses multipleAddresses4 = multipleAddresses;
                multipleAddresses4.addAddress(Type.bcc, jid3, null, null, false, null);
            }
        }
        if (noReply) {
            multipleAddresses.setNoReply();
        } else {
            if (replyTo != null) {
                multipleAddresses.addAddress(Type.replyto, replyTo, null, null, false, null);
            }
            if (replyRoom != null) {
                multipleAddresses.addAddress(Type.replyroom, replyRoom, null, null, false, null);
            }
        }
        packet.setTo((Jid) serviceAddress);
        packet.addExtension(multipleAddresses);
        connection.sendStanza(packet);
    }

    private static DomainBareJid getMultipleRecipientServiceAddress(XMPPConnection connection) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection).findService(MultipleAddresses.NAMESPACE, true);
    }
}
