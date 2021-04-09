package org.jivesoftware.smack.roster.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.roster.packet.RosterPacket.Item;
import org.jivesoftware.smack.roster.packet.RosterPacket.ItemType;
import org.jivesoftware.smack.roster.packet.RosterVer;
import org.jivesoftware.smack.util.ParserUtils;
import org.jxmpp.jid.impl.JidCreate;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class RosterPacketProvider extends IQProvider<RosterPacket> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final RosterPacketProvider INSTANCE = new RosterPacketProvider();

    public RosterPacket parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        RosterPacket roster = new RosterPacket();
        roster.setVersion(parser.getAttributeValue(BuildConfig.FLAVOR, RosterVer.ELEMENT));
        while (true) {
            int eventType = parser.next();
            boolean z = false;
            if (eventType == 2) {
                String startTag = parser.getName();
                if (startTag.hashCode() != 3242771 || !startTag.equals("item")) {
                    z = true;
                }
                if (!z) {
                    roster.addRosterItem(parseItem(parser));
                }
            } else if (eventType != 3) {
                continue;
            } else {
                String endTag = parser.getName();
                if (endTag.hashCode() != 107944136 || !endTag.equals("query")) {
                    z = true;
                }
                if (!z && parser.getDepth() == initialDepth) {
                    return roster;
                }
            }
        }
    }

    public static Item parseItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        ParserUtils.assertAtStartTag(parser, "item");
        int initialDepth = parser.getDepth();
        String str = BuildConfig.FLAVOR;
        String jidString = parser.getAttributeValue(str, "jid");
        Item item = new Item(JidCreate.bareFrom(jidString), parser.getAttributeValue(str, "name"));
        item.setSubscriptionPending("subscribe".equals(parser.getAttributeValue(str, "ask")));
        item.setItemType(ItemType.fromString(parser.getAttributeValue(str, "subscription")));
        item.setApproved(ParserUtils.getBooleanAttribute(parser, "approved", false));
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                char c = 65535;
                if (name.hashCode() == 98629247 && name.equals(Item.GROUP)) {
                    c = 0;
                }
                if (c == 0) {
                    String groupName = parser.nextText();
                    if (groupName != null && groupName.trim().length() > 0) {
                        item.addGroupName(groupName);
                    }
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                ParserUtils.assertAtEndTag(parser);
                return item;
            }
        }
    }
}
