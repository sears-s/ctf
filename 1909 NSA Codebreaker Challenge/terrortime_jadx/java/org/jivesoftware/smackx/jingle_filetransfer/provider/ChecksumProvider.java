package org.jivesoftware.smackx.jingle_filetransfer.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.jingle_filetransfer.element.Checksum;

public class ChecksumProvider extends ExtensionElementProvider<Checksum> {
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00ba A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smackx.jingle_filetransfer.element.Checksum parse(org.xmlpull.v1.XmlPullParser r17, int r18) throws java.lang.Exception {
        /*
            r16 = this;
            r0 = r17
            r1 = 0
            r2 = 0
            java.lang.String r3 = "creator"
            java.lang.String r3 = r0.getAttributeValue(r2, r3)
            if (r3 == 0) goto L_0x0010
            org.jivesoftware.smackx.jingle.element.JingleContent$Creator r1 = org.jivesoftware.smackx.jingle.element.JingleContent.Creator.valueOf(r3)
        L_0x0010:
            java.lang.String r4 = "name"
            java.lang.String r4 = r0.getAttributeValue(r2, r4)
            org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransferChild$Builder r5 = org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransferChild.getBuilder()
            r6 = 0
            r7 = 0
            r8 = 1
        L_0x001d:
            if (r8 == 0) goto L_0x00cf
            int r9 = r17.nextTag()
            java.lang.String r10 = r17.getText()
            r11 = 2
            java.lang.String r12 = "range"
            r13 = 108280125(0x674393d, float:4.5933352E-35)
            r14 = 1
            if (r9 != r11) goto L_0x0087
            int r11 = r10.hashCode()
            r15 = 3195150(0x30c10e, float:4.477359E-39)
            if (r11 == r15) goto L_0x0044
            if (r11 == r13) goto L_0x003c
        L_0x003b:
            goto L_0x004e
        L_0x003c:
            boolean r11 = r10.equals(r12)
            if (r11 == 0) goto L_0x003b
            r11 = r14
            goto L_0x004f
        L_0x0044:
            java.lang.String r11 = "hash"
            boolean r11 = r10.equals(r11)
            if (r11 == 0) goto L_0x003b
            r11 = 0
            goto L_0x004f
        L_0x004e:
            r11 = -1
        L_0x004f:
            if (r11 == 0) goto L_0x0079
            if (r11 == r14) goto L_0x0054
            goto L_0x0086
        L_0x0054:
            java.lang.String r11 = "offset"
            java.lang.String r11 = r0.getAttributeValue(r2, r11)
            java.lang.String r12 = "length"
            java.lang.String r12 = r0.getAttributeValue(r2, r12)
            if (r11 != 0) goto L_0x0064
            r14 = 0
            goto L_0x0068
        L_0x0064:
            int r14 = java.lang.Integer.parseInt(r11)
        L_0x0068:
            r13 = r14
            if (r12 != 0) goto L_0x006d
            r15 = -1
            goto L_0x0071
        L_0x006d:
            int r15 = java.lang.Integer.parseInt(r12)
        L_0x0071:
            r14 = r15
            org.jivesoftware.smackx.jingle_filetransfer.element.Range r15 = new org.jivesoftware.smackx.jingle_filetransfer.element.Range
            r15.<init>(r13, r14)
            r7 = r15
            goto L_0x0086
        L_0x0079:
            org.jivesoftware.smackx.hashes.provider.HashElementProvider r11 = new org.jivesoftware.smackx.hashes.provider.HashElementProvider
            r11.<init>()
            org.jivesoftware.smack.packet.Element r11 = r11.parse(r0)
            r6 = r11
            org.jivesoftware.smackx.hashes.element.HashElement r6 = (org.jivesoftware.smackx.hashes.element.HashElement) r6
        L_0x0086:
            goto L_0x00cd
        L_0x0087:
            r11 = 3
            if (r9 != r11) goto L_0x00cd
            int r11 = r10.hashCode()
            r15 = 3143036(0x2ff57c, float:4.404332E-39)
            if (r11 == r15) goto L_0x009e
            if (r11 == r13) goto L_0x0096
        L_0x0095:
            goto L_0x00a8
        L_0x0096:
            boolean r11 = r10.equals(r12)
            if (r11 == 0) goto L_0x0095
            r11 = 0
            goto L_0x00a9
        L_0x009e:
            java.lang.String r11 = "file"
            boolean r11 = r10.equals(r11)
            if (r11 == 0) goto L_0x0095
            r11 = r14
            goto L_0x00a9
        L_0x00a8:
            r11 = -1
        L_0x00a9:
            if (r11 == 0) goto L_0x00ba
            if (r11 == r14) goto L_0x00ae
            goto L_0x00cd
        L_0x00ae:
            if (r6 == 0) goto L_0x00b3
            r5.setHash(r6)
        L_0x00b3:
            if (r7 == 0) goto L_0x00b8
            r5.setRange(r7)
        L_0x00b8:
            r8 = 0
            goto L_0x00cd
        L_0x00ba:
            if (r6 == 0) goto L_0x00cd
            if (r7 == 0) goto L_0x00cd
            org.jivesoftware.smackx.jingle_filetransfer.element.Range r11 = new org.jivesoftware.smackx.jingle_filetransfer.element.Range
            int r12 = r7.getOffset()
            int r13 = r7.getLength()
            r11.<init>(r12, r13, r6)
            r7 = r11
            r6 = 0
        L_0x00cd:
            goto L_0x001d
        L_0x00cf:
            org.jivesoftware.smackx.jingle_filetransfer.element.Checksum r2 = new org.jivesoftware.smackx.jingle_filetransfer.element.Checksum
            org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransferChild r9 = r5.build()
            r2.<init>(r1, r4, r9)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.jingle_filetransfer.provider.ChecksumProvider.parse(org.xmlpull.v1.XmlPullParser, int):org.jivesoftware.smackx.jingle_filetransfer.element.Checksum");
    }
}
