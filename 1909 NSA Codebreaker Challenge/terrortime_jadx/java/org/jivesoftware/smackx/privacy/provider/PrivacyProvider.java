package org.jivesoftware.smackx.privacy.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.util.ArrayList;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.csi.packet.ClientStateIndication.Active;
import org.jivesoftware.smackx.message_markup.element.ListElement;
import org.jivesoftware.smackx.privacy.packet.Privacy;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class PrivacyProvider extends IQProvider<Privacy> {
    public Privacy parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        Privacy privacy = new Privacy();
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == 2) {
                boolean equals = parser.getName().equals(Active.ELEMENT);
                String str = "name";
                String str2 = BuildConfig.FLAVOR;
                if (equals) {
                    String activeName = parser.getAttributeValue(str2, str);
                    if (activeName == null) {
                        privacy.setDeclineActiveList(true);
                    } else {
                        privacy.setActiveName(activeName);
                    }
                } else if (parser.getName().equals("default")) {
                    String defaultName = parser.getAttributeValue(str2, str);
                    if (defaultName == null) {
                        privacy.setDeclineDefaultList(true);
                    } else {
                        privacy.setDefaultName(defaultName);
                    }
                } else if (parser.getName().equals(ListElement.ELEMENT)) {
                    parseList(parser, privacy);
                }
            } else if (eventType == 3 && parser.getName().equals("query")) {
                done = true;
            }
        }
        return privacy;
    }

    private static void parseList(XmlPullParser parser, Privacy privacy) throws XmlPullParserException, IOException, SmackException {
        boolean done = false;
        String listName = parser.getAttributeValue(BuildConfig.FLAVOR, "name");
        ArrayList<PrivacyItem> items = new ArrayList<>();
        while (!done) {
            int eventType = parser.next();
            if (eventType == 2) {
                if (parser.getName().equals("item")) {
                    items.add(parseItem(parser));
                }
            } else if (eventType == 3 && parser.getName().equals(ListElement.ELEMENT)) {
                done = true;
            }
        }
        privacy.setPrivacyList(listName, items);
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x003f  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0064  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0079  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static org.jivesoftware.smackx.privacy.packet.PrivacyItem parseItem(org.xmlpull.v1.XmlPullParser r13) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException, org.jivesoftware.smack.SmackException {
        /*
            java.lang.String r0 = ""
            java.lang.String r1 = "action"
            java.lang.String r1 = r13.getAttributeValue(r0, r1)
            java.lang.String r2 = "order"
            java.lang.Long r2 = org.jivesoftware.smack.util.ParserUtils.getLongAttribute(r13, r2)
            long r9 = r2.longValue()
            java.lang.String r2 = "type"
            java.lang.String r2 = r13.getAttributeValue(r0, r2)
            int r3 = r1.hashCode()
            r4 = 3079692(0x2efe0c, float:4.315568E-39)
            r5 = 1
            if (r3 == r4) goto L_0x0032
            r4 = 92906313(0x589a349, float:1.29434E-35)
            if (r3 == r4) goto L_0x0028
        L_0x0027:
            goto L_0x003c
        L_0x0028:
            java.lang.String r3 = "allow"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0027
            r3 = 0
            goto L_0x003d
        L_0x0032:
            java.lang.String r3 = "deny"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0027
            r3 = r5
            goto L_0x003d
        L_0x003c:
            r3 = -1
        L_0x003d:
            if (r3 == 0) goto L_0x0060
            if (r3 != r5) goto L_0x0044
            r3 = 0
            r11 = r3
            goto L_0x0062
        L_0x0044:
            org.jivesoftware.smack.SmackException r0 = new org.jivesoftware.smack.SmackException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Unknown action value '"
            r3.append(r4)
            r3.append(r1)
            java.lang.String r4 = "'"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r0.<init>(r3)
            throw r0
        L_0x0060:
            r3 = 1
            r11 = r3
        L_0x0062:
            if (r2 == 0) goto L_0x0079
            java.lang.String r3 = "value"
            java.lang.String r0 = r13.getAttributeValue(r0, r3)
            org.jivesoftware.smackx.privacy.packet.PrivacyItem r12 = new org.jivesoftware.smackx.privacy.packet.PrivacyItem
            org.jivesoftware.smackx.privacy.packet.PrivacyItem$Type r4 = org.jivesoftware.smackx.privacy.packet.PrivacyItem.Type.valueOf(r2)
            r3 = r12
            r5 = r0
            r6 = r11
            r7 = r9
            r3.<init>(r4, r5, r6, r7)
            r0 = r12
            goto L_0x007e
        L_0x0079:
            org.jivesoftware.smackx.privacy.packet.PrivacyItem r0 = new org.jivesoftware.smackx.privacy.packet.PrivacyItem
            r0.<init>(r11, r9)
        L_0x007e:
            parseItemChildElements(r13, r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.privacy.provider.PrivacyProvider.parseItem(org.xmlpull.v1.XmlPullParser):org.jivesoftware.smackx.privacy.packet.PrivacyItem");
    }

    private static void parseItemChildElements(XmlPullParser parser, PrivacyItem privacyItem) throws XmlPullParserException, IOException {
        int initialDepth = parser.getDepth();
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                char c = 65535;
                int hashCode = name.hashCode();
                if (hashCode != -1240091849) {
                    if (hashCode != 3368) {
                        if (hashCode != 211864444) {
                            if (hashCode == 954925063 && name.equals(Message.ELEMENT)) {
                                c = 1;
                            }
                        } else if (name.equals("presence-out")) {
                            c = 3;
                        }
                    } else if (name.equals(IQ.IQ_ELEMENT)) {
                        c = 0;
                    }
                } else if (name.equals("presence-in")) {
                    c = 2;
                }
                if (c == 0) {
                    privacyItem.setFilterIQ(true);
                } else if (c == 1) {
                    privacyItem.setFilterMessage(true);
                } else if (c == 2) {
                    privacyItem.setFilterPresenceIn(true);
                } else if (c == 3) {
                    privacyItem.setFilterPresenceOut(true);
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return;
            }
        }
    }
}
