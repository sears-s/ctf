package org.jivesoftware.smackx.jingle.transports.jingle_s5b.provider;

import org.jivesoftware.smackx.jingle.provider.JingleContentTransportProvider;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransport;

public class JingleS5BTransportProvider extends JingleContentTransportProvider<JingleS5BTransport> {
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0093, code lost:
        if (r7.equals(org.jivesoftware.smackx.jingle.element.JingleContentTransportCandidate.ELEMENT) != false) goto L_0x00a1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransport parse(org.xmlpull.v1.XmlPullParser r16, int r17) throws java.lang.Exception {
        /*
            r15 = this;
            r0 = r16
            org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransport$Builder r1 = org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransport.getBuilder()
            r2 = 0
            java.lang.String r3 = "sid"
            java.lang.String r3 = r0.getAttributeValue(r2, r3)
            r1.setStreamId(r3)
            java.lang.String r4 = "dstaddr"
            java.lang.String r4 = r0.getAttributeValue(r2, r4)
            r1.setDestinationAddress(r4)
            java.lang.String r5 = "mode"
            java.lang.String r5 = r0.getAttributeValue(r2, r5)
            if (r5 == 0) goto L_0x0035
            org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream$Mode r6 = org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.Mode.udp
            java.lang.String r6 = r6.toString()
            boolean r6 = r5.equals(r6)
            if (r6 == 0) goto L_0x0030
            org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream$Mode r6 = org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.Mode.udp
            goto L_0x0032
        L_0x0030:
            org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream$Mode r6 = org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.Mode.tcp
        L_0x0032:
            r1.setMode(r6)
        L_0x0035:
            int r6 = r16.nextTag()
            java.lang.String r7 = r16.getName()
            r8 = 0
            r9 = -1
            r10 = 3
            r11 = 2
            if (r6 == r11) goto L_0x0065
            if (r6 == r10) goto L_0x0047
            goto L_0x0126
        L_0x0047:
            int r10 = r7.hashCode()
            r11 = 1052964649(0x3ec2f729, float:0.38079193)
            if (r10 == r11) goto L_0x0051
        L_0x0050:
            goto L_0x005a
        L_0x0051:
            java.lang.String r10 = "transport"
            boolean r10 = r7.equals(r10)
            if (r10 == 0) goto L_0x0050
            goto L_0x005b
        L_0x005a:
            r8 = r9
        L_0x005b:
            if (r8 == 0) goto L_0x005f
            goto L_0x0126
        L_0x005f:
            org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransport r2 = r1.build()
            return r2
        L_0x0065:
            int r12 = r7.hashCode()
            r13 = 4
            r14 = 1
            switch(r12) {
                case -1033040578: goto L_0x0096;
                case 508663171: goto L_0x008d;
                case 995927529: goto L_0x0083;
                case 1352626631: goto L_0x0079;
                case 2000321031: goto L_0x006f;
                default: goto L_0x006e;
            }
        L_0x006e:
            goto L_0x00a0
        L_0x006f:
            java.lang.String r8 = "candidate-activated"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x006e
            r8 = r14
            goto L_0x00a1
        L_0x0079:
            java.lang.String r8 = "candidate-used"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x006e
            r8 = r11
            goto L_0x00a1
        L_0x0083:
            java.lang.String r8 = "proxy-error"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x006e
            r8 = r13
            goto L_0x00a1
        L_0x008d:
            java.lang.String r12 = "candidate"
            boolean r12 = r7.equals(r12)
            if (r12 == 0) goto L_0x006e
            goto L_0x00a1
        L_0x0096:
            java.lang.String r8 = "candidate-error"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x006e
            r8 = r10
            goto L_0x00a1
        L_0x00a0:
            r8 = r9
        L_0x00a1:
            java.lang.String r9 = "cid"
            if (r8 == 0) goto L_0x00d5
            if (r8 == r14) goto L_0x00c8
            if (r8 == r11) goto L_0x00bb
            if (r8 == r10) goto L_0x00b5
            if (r8 == r13) goto L_0x00af
            goto L_0x0125
        L_0x00af:
            org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo$ProxyError r8 = org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo.ProxyError.INSTANCE
            r1.setTransportInfo(r8)
            goto L_0x0125
        L_0x00b5:
            org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo$CandidateError r8 = org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo.CandidateError.INSTANCE
            r1.setTransportInfo(r8)
            goto L_0x0125
        L_0x00bb:
            org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo$CandidateUsed r8 = new org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo$CandidateUsed
            java.lang.String r9 = r0.getAttributeValue(r2, r9)
            r8.<init>(r9)
            r1.setTransportInfo(r8)
            goto L_0x0125
        L_0x00c8:
            org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo$CandidateActivated r8 = new org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo$CandidateActivated
            java.lang.String r9 = r0.getAttributeValue(r2, r9)
            r8.<init>(r9)
            r1.setTransportInfo(r8)
            goto L_0x0125
        L_0x00d5:
            org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportCandidate$Builder r8 = org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportCandidate.getBuilder()
            java.lang.String r9 = r0.getAttributeValue(r2, r9)
            r8.setCandidateId(r9)
            java.lang.String r9 = "host"
            java.lang.String r9 = r0.getAttributeValue(r2, r9)
            r8.setHost(r9)
            java.lang.String r9 = "jid"
            java.lang.String r9 = r0.getAttributeValue(r2, r9)
            r8.setJid(r9)
            java.lang.String r9 = "priority"
            java.lang.String r9 = r0.getAttributeValue(r2, r9)
            int r9 = java.lang.Integer.parseInt(r9)
            r8.setPriority(r9)
            java.lang.String r9 = "port"
            java.lang.String r9 = r0.getAttributeValue(r2, r9)
            if (r9 == 0) goto L_0x010e
            int r10 = java.lang.Integer.parseInt(r9)
            r8.setPort(r10)
        L_0x010e:
            java.lang.String r10 = "type"
            java.lang.String r10 = r0.getAttributeValue(r2, r10)
            if (r10 == 0) goto L_0x011d
            org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportCandidate$Type r11 = org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportCandidate.Type.fromString(r10)
            r8.setType(r11)
        L_0x011d:
            org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportCandidate r11 = r8.build()
            r1.addTransportCandidate(r11)
        L_0x0125:
        L_0x0126:
            goto L_0x0035
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.jingle.transports.jingle_s5b.provider.JingleS5BTransportProvider.parse(org.xmlpull.v1.XmlPullParser, int):org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransport");
    }
}
