package org.jivesoftware.smackx.jingle_filetransfer.provider;

import org.jivesoftware.smackx.jingle.provider.JingleContentDescriptionProvider;
import org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransfer;

public class JingleFileTransferProvider extends JingleContentDescriptionProvider<JingleFileTransfer> {
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x004a, code lost:
        if (r8.equals("name") != false) goto L_0x006c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransfer parse(org.xmlpull.v1.XmlPullParser r17, int r18) throws java.lang.Exception {
        /*
            r16 = this;
            r0 = r17
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r2 = 0
            org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransferChild$Builder r3 = org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransferChild.getBuilder()
            r4 = 0
            r5 = 0
            r6 = -1
        L_0x000f:
            int r7 = r17.nextTag()
            java.lang.String r8 = r17.getName()
            java.lang.String r9 = "range"
            r10 = 3
            r11 = 0
            r12 = 1
            r13 = -1
            r14 = 2
            if (r7 != r14) goto L_0x00ed
            int r15 = r8.hashCode()
            switch(r15) {
                case 3076014: goto L_0x0061;
                case 3079825: goto L_0x0057;
                case 3195150: goto L_0x004d;
                case 3373707: goto L_0x0044;
                case 3530753: goto L_0x003a;
                case 108280125: goto L_0x0032;
                case 1893699459: goto L_0x0028;
                default: goto L_0x0027;
            }
        L_0x0027:
            goto L_0x006b
        L_0x0028:
            java.lang.String r9 = "media-type"
            boolean r9 = r8.equals(r9)
            if (r9 == 0) goto L_0x0027
            r10 = r14
            goto L_0x006c
        L_0x0032:
            boolean r9 = r8.equals(r9)
            if (r9 == 0) goto L_0x0027
            r10 = 5
            goto L_0x006c
        L_0x003a:
            java.lang.String r9 = "size"
            boolean r9 = r8.equals(r9)
            if (r9 == 0) goto L_0x0027
            r10 = 4
            goto L_0x006c
        L_0x0044:
            java.lang.String r9 = "name"
            boolean r9 = r8.equals(r9)
            if (r9 == 0) goto L_0x0027
            goto L_0x006c
        L_0x004d:
            java.lang.String r9 = "hash"
            boolean r9 = r8.equals(r9)
            if (r9 == 0) goto L_0x0027
            r10 = 6
            goto L_0x006c
        L_0x0057:
            java.lang.String r9 = "desc"
            boolean r9 = r8.equals(r9)
            if (r9 == 0) goto L_0x0027
            r10 = r12
            goto L_0x006c
        L_0x0061:
            java.lang.String r9 = "date"
            boolean r9 = r8.equals(r9)
            if (r9 == 0) goto L_0x0027
            r10 = r11
            goto L_0x006c
        L_0x006b:
            r10 = r13
        L_0x006c:
            switch(r10) {
                case 0: goto L_0x00e0;
                case 1: goto L_0x00d8;
                case 2: goto L_0x00d0;
                case 3: goto L_0x00c8;
                case 4: goto L_0x00bc;
                case 5: goto L_0x0090;
                case 6: goto L_0x0071;
                default: goto L_0x006f;
            }
        L_0x006f:
            goto L_0x00ec
        L_0x0071:
            if (r2 == 0) goto L_0x0081
            org.jivesoftware.smackx.hashes.provider.HashElementProvider r9 = new org.jivesoftware.smackx.hashes.provider.HashElementProvider
            r9.<init>()
            org.jivesoftware.smack.packet.Element r9 = r9.parse(r0)
            r4 = r9
            org.jivesoftware.smackx.hashes.element.HashElement r4 = (org.jivesoftware.smackx.hashes.element.HashElement) r4
            goto L_0x00ec
        L_0x0081:
            org.jivesoftware.smackx.hashes.provider.HashElementProvider r9 = new org.jivesoftware.smackx.hashes.provider.HashElementProvider
            r9.<init>()
            org.jivesoftware.smack.packet.Element r9 = r9.parse(r0)
            org.jivesoftware.smackx.hashes.element.HashElement r9 = (org.jivesoftware.smackx.hashes.element.HashElement) r9
            r3.setHash(r9)
            goto L_0x00ec
        L_0x0090:
            r2 = 1
            r9 = 0
            java.lang.String r10 = "offset"
            java.lang.String r10 = r0.getAttributeValue(r9, r10)
            java.lang.String r12 = "length"
            java.lang.String r9 = r0.getAttributeValue(r9, r12)
            if (r10 == 0) goto L_0x00a4
            int r11 = java.lang.Integer.parseInt(r10)
        L_0x00a4:
            r5 = r11
            if (r9 == 0) goto L_0x00ab
            int r13 = java.lang.Integer.parseInt(r9)
        L_0x00ab:
            r6 = r13
            boolean r11 = r17.isEmptyElementTag()
            if (r11 == 0) goto L_0x00ec
            r2 = 0
            org.jivesoftware.smackx.jingle_filetransfer.element.Range r11 = new org.jivesoftware.smackx.jingle_filetransfer.element.Range
            r11.<init>(r5, r6)
            r3.setRange(r11)
            goto L_0x00ec
        L_0x00bc:
            java.lang.String r9 = r17.nextText()
            int r9 = java.lang.Integer.parseInt(r9)
            r3.setSize(r9)
            goto L_0x00ec
        L_0x00c8:
            java.lang.String r9 = r17.nextText()
            r3.setName(r9)
            goto L_0x00ec
        L_0x00d0:
            java.lang.String r9 = r17.nextText()
            r3.setMediaType(r9)
            goto L_0x00ec
        L_0x00d8:
            java.lang.String r9 = r17.nextText()
            r3.setDescription(r9)
            goto L_0x00ec
        L_0x00e0:
            java.lang.String r9 = r17.nextText()
            java.util.Date r9 = org.jxmpp.util.XmppDateTime.parseXEP0082Date(r9)
            r3.setDate(r9)
        L_0x00ec:
            goto L_0x0141
        L_0x00ed:
            if (r7 != r10) goto L_0x0141
            int r10 = r8.hashCode()
            r15 = -1724546052(0xffffffff993583fc, float:-9.384135E-24)
            if (r10 == r15) goto L_0x0115
            r15 = 3143036(0x2ff57c, float:4.404332E-39)
            if (r10 == r15) goto L_0x010b
            r15 = 108280125(0x674393d, float:4.5933352E-35)
            if (r10 == r15) goto L_0x0103
        L_0x0102:
            goto L_0x011e
        L_0x0103:
            boolean r9 = r8.equals(r9)
            if (r9 == 0) goto L_0x0102
            r13 = r11
            goto L_0x011e
        L_0x010b:
            java.lang.String r9 = "file"
            boolean r9 = r8.equals(r9)
            if (r9 == 0) goto L_0x0102
            r13 = r12
            goto L_0x011e
        L_0x0115:
            java.lang.String r9 = "description"
            boolean r9 = r8.equals(r9)
            if (r9 == 0) goto L_0x0102
            r13 = r14
        L_0x011e:
            if (r13 == 0) goto L_0x0137
            if (r13 == r12) goto L_0x012b
            if (r13 == r14) goto L_0x0125
            goto L_0x0141
        L_0x0125:
            org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransfer r9 = new org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransfer
            r9.<init>(r1)
            return r9
        L_0x012b:
            org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransferChild r9 = r3.build()
            r1.add(r9)
            org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransferChild$Builder r3 = org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransferChild.getBuilder()
            goto L_0x0141
        L_0x0137:
            r2 = 0
            org.jivesoftware.smackx.jingle_filetransfer.element.Range r9 = new org.jivesoftware.smackx.jingle_filetransfer.element.Range
            r9.<init>(r5, r6, r4)
            r3.setRange(r9)
            r4 = 0
        L_0x0141:
            goto L_0x000f
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.jingle_filetransfer.provider.JingleFileTransferProvider.parse(org.xmlpull.v1.XmlPullParser, int):org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransfer");
    }
}
