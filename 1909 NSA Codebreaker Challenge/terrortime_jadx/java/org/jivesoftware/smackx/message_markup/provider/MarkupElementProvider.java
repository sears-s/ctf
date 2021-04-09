package org.jivesoftware.smackx.message_markup.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.message_markup.element.MarkupElement;

public class MarkupElementProvider extends ExtensionElementProvider<MarkupElement> {
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00e0, code lost:
        if (r9.equals(org.jivesoftware.smackx.message_markup.element.CodeBlockElement.ELEMENT) != false) goto L_0x0112;
     */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x005a  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00b6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smackx.message_markup.element.MarkupElement parse(org.xmlpull.v1.XmlPullParser r18, int r19) throws java.lang.Exception {
        /*
            r17 = this;
            r0 = r18
            org.jivesoftware.smackx.message_markup.element.MarkupElement$Builder r1 = org.jivesoftware.smackx.message_markup.element.MarkupElement.getBuilder()
            r2 = -1
            r3 = -1
            java.util.HashSet r4 = new java.util.HashSet
            r4.<init>()
            r5 = -1
            r6 = -1
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
        L_0x0014:
            int r8 = r18.next()
            java.lang.String r9 = r18.getName()
            java.lang.String r10 = "span"
            java.lang.String r11 = "list"
            r13 = 3
            r15 = 2
            r12 = 1
            if (r8 == r15) goto L_0x00bd
            if (r8 == r13) goto L_0x0029
            goto L_0x0182
        L_0x0029:
            int r13 = r9.hashCode()
            r14 = -1081305560(0xffffffffbf8c9628, float:-1.0983324)
            if (r13 == r14) goto L_0x004d
            r14 = 3322014(0x32b09e, float:4.655133E-39)
            if (r13 == r14) goto L_0x0045
            r11 = 3536714(0x35f74a, float:4.955992E-39)
            if (r13 == r11) goto L_0x003d
        L_0x003c:
            goto L_0x0057
        L_0x003d:
            boolean r10 = r9.equals(r10)
            if (r10 == 0) goto L_0x003c
            r10 = 0
            goto L_0x0058
        L_0x0045:
            boolean r10 = r9.equals(r11)
            if (r10 == 0) goto L_0x003c
            r10 = r12
            goto L_0x0058
        L_0x004d:
            java.lang.String r10 = "markup"
            boolean r10 = r9.equals(r10)
            if (r10 == 0) goto L_0x003c
            r10 = r15
            goto L_0x0058
        L_0x0057:
            r10 = -1
        L_0x0058:
            if (r10 == 0) goto L_0x00b6
            if (r10 == r12) goto L_0x0065
            if (r10 == r15) goto L_0x0060
            goto L_0x0182
        L_0x0060:
            org.jivesoftware.smackx.message_markup.element.MarkupElement r10 = r1.build()
            return r10
        L_0x0065:
            org.jivesoftware.smackx.message_markup.element.MarkupElement$Builder$ListBuilder r10 = r1.beginList()
            int r11 = r7.size()
            if (r11 <= 0) goto L_0x0085
            r14 = 0
            java.lang.Object r11 = r7.get(r14)
            org.jivesoftware.smackx.message_markup.element.ListElement$ListEntryElement r11 = (org.jivesoftware.smackx.message_markup.element.ListElement.ListEntryElement) r11
            int r11 = r11.getStart()
            if (r11 != r5) goto L_0x007d
            goto L_0x0085
        L_0x007d:
            org.jivesoftware.smack.SmackException r11 = new org.jivesoftware.smack.SmackException
            java.lang.String r12 = "Error while parsing incoming MessageMarkup ListElement: 'start' attribute of first 'li' element must equal 'start' attribute of list."
            r11.<init>(r12)
            throw r11
        L_0x0085:
            r11 = 0
        L_0x0086:
            int r13 = r7.size()
            if (r11 >= r13) goto L_0x00b1
            java.lang.Object r13 = r7.get(r11)
            org.jivesoftware.smackx.message_markup.element.ListElement$ListEntryElement r13 = (org.jivesoftware.smackx.message_markup.element.ListElement.ListEntryElement) r13
            int r13 = r13.getStart()
            int r14 = r7.size()
            int r14 = r14 - r12
            if (r11 >= r14) goto L_0x00aa
            int r14 = r11 + 1
            java.lang.Object r14 = r7.get(r14)
            org.jivesoftware.smackx.message_markup.element.ListElement$ListEntryElement r14 = (org.jivesoftware.smackx.message_markup.element.ListElement.ListEntryElement) r14
            int r14 = r14.getStart()
            goto L_0x00ab
        L_0x00aa:
            r14 = r6
        L_0x00ab:
            r10.addEntry(r13, r14)
            int r11 = r11 + 1
            goto L_0x0086
        L_0x00b1:
            r10.endList()
            goto L_0x0182
        L_0x00b6:
            r1.addSpan(r2, r3, r4)
            r2 = -1
            r3 = -1
            goto L_0x0182
        L_0x00bd:
            r14 = 0
            int r16 = r9.hashCode()
            switch(r16) {
                case -1381356710: goto L_0x0107;
                case 3453: goto L_0x00fd;
                case 3059181: goto L_0x00f3;
                case 3322014: goto L_0x00eb;
                case 3536714: goto L_0x00e3;
                case 93564239: goto L_0x00da;
                case 1189352828: goto L_0x00d0;
                case 1550463001: goto L_0x00c6;
                default: goto L_0x00c5;
            }
        L_0x00c5:
            goto L_0x0111
        L_0x00c6:
            java.lang.String r10 = "deleted"
            boolean r10 = r9.equals(r10)
            if (r10 == 0) goto L_0x00c5
            r12 = 5
            goto L_0x0112
        L_0x00d0:
            java.lang.String r10 = "emphasis"
            boolean r10 = r9.equals(r10)
            if (r10 == 0) goto L_0x00c5
            r12 = 4
            goto L_0x0112
        L_0x00da:
            java.lang.String r10 = "bcode"
            boolean r10 = r9.equals(r10)
            if (r10 == 0) goto L_0x00c5
            goto L_0x0112
        L_0x00e3:
            boolean r10 = r9.equals(r10)
            if (r10 == 0) goto L_0x00c5
            r12 = r15
            goto L_0x0112
        L_0x00eb:
            boolean r10 = r9.equals(r11)
            if (r10 == 0) goto L_0x00c5
            r12 = 6
            goto L_0x0112
        L_0x00f3:
            java.lang.String r10 = "code"
            boolean r10 = r9.equals(r10)
            if (r10 == 0) goto L_0x00c5
            r12 = r13
            goto L_0x0112
        L_0x00fd:
            java.lang.String r10 = "li"
            boolean r10 = r9.equals(r10)
            if (r10 == 0) goto L_0x00c5
            r12 = 7
            goto L_0x0112
        L_0x0107:
            java.lang.String r10 = "bquote"
            boolean r10 = r9.equals(r10)
            if (r10 == 0) goto L_0x00c5
            r12 = r14
            goto L_0x0112
        L_0x0111:
            r12 = -1
        L_0x0112:
            java.lang.String r10 = "end"
            java.lang.String r11 = "start"
            switch(r12) {
                case 0: goto L_0x0171;
                case 1: goto L_0x0161;
                case 2: goto L_0x014e;
                case 3: goto L_0x0148;
                case 4: goto L_0x0142;
                case 5: goto L_0x013c;
                case 6: goto L_0x0129;
                case 7: goto L_0x011a;
                default: goto L_0x0119;
            }
        L_0x0119:
            goto L_0x0181
        L_0x011a:
            java.lang.String r10 = "Message Markup ListElement 'li' MUST contain a 'start' attribute."
            int r10 = org.jivesoftware.smack.util.ParserUtils.getIntegerAttributeOrThrow(r0, r11, r10)
            org.jivesoftware.smackx.message_markup.element.ListElement$ListEntryElement r11 = new org.jivesoftware.smackx.message_markup.element.ListElement$ListEntryElement
            r11.<init>(r10)
            r7.add(r11)
            goto L_0x0181
        L_0x0129:
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            r7 = r12
            java.lang.String r12 = "Message Markup ListElement MUST contain a 'start' attribute."
            int r5 = org.jivesoftware.smack.util.ParserUtils.getIntegerAttributeOrThrow(r0, r11, r12)
            java.lang.String r11 = "Message Markup ListElement MUST contain a 'end' attribute."
            int r6 = org.jivesoftware.smack.util.ParserUtils.getIntegerAttributeOrThrow(r0, r10, r11)
            goto L_0x0181
        L_0x013c:
            org.jivesoftware.smackx.message_markup.element.SpanElement$SpanStyle r10 = org.jivesoftware.smackx.message_markup.element.SpanElement.SpanStyle.deleted
            r4.add(r10)
            goto L_0x0181
        L_0x0142:
            org.jivesoftware.smackx.message_markup.element.SpanElement$SpanStyle r10 = org.jivesoftware.smackx.message_markup.element.SpanElement.SpanStyle.emphasis
            r4.add(r10)
            goto L_0x0181
        L_0x0148:
            org.jivesoftware.smackx.message_markup.element.SpanElement$SpanStyle r10 = org.jivesoftware.smackx.message_markup.element.SpanElement.SpanStyle.code
            r4.add(r10)
            goto L_0x0181
        L_0x014e:
            java.util.HashSet r12 = new java.util.HashSet
            r12.<init>()
            r4 = r12
            java.lang.String r12 = "Message Markup SpanElement MUST contain a 'start' attribute."
            int r2 = org.jivesoftware.smack.util.ParserUtils.getIntegerAttributeOrThrow(r0, r11, r12)
            java.lang.String r11 = "Message Markup SpanElement MUST contain a 'end' attribute."
            int r3 = org.jivesoftware.smack.util.ParserUtils.getIntegerAttributeOrThrow(r0, r10, r11)
            goto L_0x0181
        L_0x0161:
            java.lang.String r12 = "Message Markup CodeBlockElement MUST contain a 'start' attribute."
            int r11 = org.jivesoftware.smack.util.ParserUtils.getIntegerAttributeOrThrow(r0, r11, r12)
            java.lang.String r12 = "Message Markup CodeBlockElement MUST contain a 'end' attribute."
            int r10 = org.jivesoftware.smack.util.ParserUtils.getIntegerAttributeOrThrow(r0, r10, r12)
            r1.setCodeBlock(r11, r10)
            goto L_0x0181
        L_0x0171:
            java.lang.String r12 = "Message Markup BlockQuoteElement MUST contain a 'start' attribute."
            int r11 = org.jivesoftware.smack.util.ParserUtils.getIntegerAttributeOrThrow(r0, r11, r12)
            java.lang.String r12 = "Message Markup BlockQuoteElement MUST contain a 'end' attribute."
            int r10 = org.jivesoftware.smack.util.ParserUtils.getIntegerAttributeOrThrow(r0, r10, r12)
            r1.setBlockQuote(r11, r10)
        L_0x0181:
        L_0x0182:
            goto L_0x0014
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.message_markup.provider.MarkupElementProvider.parse(org.xmlpull.v1.XmlPullParser, int):org.jivesoftware.smackx.message_markup.element.MarkupElement");
    }
}
