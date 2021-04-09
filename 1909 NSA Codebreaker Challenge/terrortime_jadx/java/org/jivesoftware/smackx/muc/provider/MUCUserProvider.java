package org.jivesoftware.smackx.muc.provider;

import java.io.IOException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.jingle.element.JingleReason;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.smackx.muc.packet.MUCUser.Decline;
import org.jivesoftware.smackx.muc.packet.MUCUser.Invite;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityJid;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class MUCUserProvider extends ExtensionElementProvider<MUCUser> {
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smackx.muc.packet.MUCUser parse(org.xmlpull.v1.XmlPullParser r10, int r11) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r9 = this;
            org.jivesoftware.smackx.muc.packet.MUCUser r0 = new org.jivesoftware.smackx.muc.packet.MUCUser
            r0.<init>()
        L_0x0005:
            int r1 = r10.next()
            r2 = 3
            r3 = 2
            if (r1 == r3) goto L_0x0019
            if (r1 == r2) goto L_0x0011
            goto L_0x00ac
        L_0x0011:
            int r1 = r10.getDepth()
            if (r1 != r11) goto L_0x00ac
            return r0
        L_0x0019:
            java.lang.String r1 = r10.getName()
            r4 = -1
            int r5 = r1.hashCode()
            r6 = 5
            r7 = 4
            r8 = 1
            switch(r5) {
                case -1183699191: goto L_0x005b;
                case -892481550: goto L_0x0051;
                case 3242771: goto L_0x0047;
                case 1216985755: goto L_0x003d;
                case 1542349558: goto L_0x0033;
                case 1557372922: goto L_0x0029;
                default: goto L_0x0028;
            }
        L_0x0028:
            goto L_0x0065
        L_0x0029:
            java.lang.String r5 = "destroy"
            boolean r1 = r1.equals(r5)
            if (r1 == 0) goto L_0x0028
            r1 = r6
            goto L_0x0066
        L_0x0033:
            java.lang.String r5 = "decline"
            boolean r1 = r1.equals(r5)
            if (r1 == 0) goto L_0x0028
            r1 = r7
            goto L_0x0066
        L_0x003d:
            java.lang.String r5 = "password"
            boolean r1 = r1.equals(r5)
            if (r1 == 0) goto L_0x0028
            r1 = r3
            goto L_0x0066
        L_0x0047:
            java.lang.String r5 = "item"
            boolean r1 = r1.equals(r5)
            if (r1 == 0) goto L_0x0028
            r1 = r8
            goto L_0x0066
        L_0x0051:
            java.lang.String r5 = "status"
            boolean r1 = r1.equals(r5)
            if (r1 == 0) goto L_0x0028
            r1 = r2
            goto L_0x0066
        L_0x005b:
            java.lang.String r5 = "invite"
            boolean r1 = r1.equals(r5)
            if (r1 == 0) goto L_0x0028
            r1 = 0
            goto L_0x0066
        L_0x0065:
            r1 = r4
        L_0x0066:
            if (r1 == 0) goto L_0x00a3
            if (r1 == r8) goto L_0x009b
            if (r1 == r3) goto L_0x0093
            if (r1 == r2) goto L_0x0083
            if (r1 == r7) goto L_0x007b
            if (r1 == r6) goto L_0x0073
            goto L_0x00ab
        L_0x0073:
            org.jivesoftware.smackx.muc.packet.Destroy r1 = org.jivesoftware.smackx.muc.provider.MUCParserUtils.parseDestroy(r10)
            r0.setDestroy(r1)
            goto L_0x00ab
        L_0x007b:
            org.jivesoftware.smackx.muc.packet.MUCUser$Decline r1 = parseDecline(r10)
            r0.setDecline(r1)
            goto L_0x00ab
        L_0x0083:
            java.lang.String r1 = ""
            java.lang.String r2 = "code"
            java.lang.String r1 = r10.getAttributeValue(r1, r2)
            org.jivesoftware.smackx.muc.packet.MUCUser$Status r2 = org.jivesoftware.smackx.muc.packet.MUCUser.Status.create(r1)
            r0.addStatusCode(r2)
            goto L_0x00ab
        L_0x0093:
            java.lang.String r1 = r10.nextText()
            r0.setPassword(r1)
            goto L_0x00ab
        L_0x009b:
            org.jivesoftware.smackx.muc.packet.MUCItem r1 = org.jivesoftware.smackx.muc.provider.MUCParserUtils.parseItem(r10)
            r0.setItem(r1)
            goto L_0x00ab
        L_0x00a3:
            org.jivesoftware.smackx.muc.packet.MUCUser$Invite r1 = parseInvite(r10)
            r0.setInvite(r1)
        L_0x00ab:
        L_0x00ac:
            goto L_0x0005
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.muc.provider.MUCUserProvider.parse(org.xmlpull.v1.XmlPullParser, int):org.jivesoftware.smackx.muc.packet.MUCUser");
    }

    private static Invite parseInvite(XmlPullParser parser) throws XmlPullParserException, IOException {
        String reason = null;
        EntityBareJid to = ParserUtils.getBareJidAttribute(parser, PrivacyItem.SUBSCRIPTION_TO);
        EntityJid from = ParserUtils.getEntityJidAttribute(parser, PrivacyItem.SUBSCRIPTION_FROM);
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                if (parser.getName().equals(JingleReason.ELEMENT)) {
                    reason = parser.nextText();
                }
            } else if (eventType == 3 && parser.getName().equals(Invite.ELEMENT)) {
                return new Invite(reason, from, to);
            }
        }
    }

    private static Decline parseDecline(XmlPullParser parser) throws XmlPullParserException, IOException {
        String reason = null;
        EntityBareJid to = ParserUtils.getBareJidAttribute(parser, PrivacyItem.SUBSCRIPTION_TO);
        EntityBareJid from = ParserUtils.getBareJidAttribute(parser, PrivacyItem.SUBSCRIPTION_FROM);
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                if (parser.getName().equals(JingleReason.ELEMENT)) {
                    reason = parser.nextText();
                }
            } else if (eventType == 3 && parser.getName().equals(Decline.ELEMENT)) {
                return new Decline(reason, from, to);
            }
        }
    }
}
