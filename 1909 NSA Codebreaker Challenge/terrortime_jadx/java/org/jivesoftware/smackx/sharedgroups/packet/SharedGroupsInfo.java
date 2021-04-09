package org.jivesoftware.smackx.sharedgroups.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.roster.packet.RosterPacket.Item;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SharedGroupsInfo extends IQ {
    public static final String ELEMENT = "sharedgroup";
    public static final String NAMESPACE = "http://www.jivesoftware.org/protocol/sharedgroup";
    private final List<String> groups = new ArrayList();

    public static class Provider extends IQProvider<SharedGroupsInfo> {
        public SharedGroupsInfo parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
            SharedGroupsInfo groupsInfo = new SharedGroupsInfo();
            boolean done = false;
            while (!done) {
                int eventType = parser.next();
                if (eventType == 2 && parser.getName().equals(Item.GROUP)) {
                    groupsInfo.getGroups().add(parser.nextText());
                } else if (eventType == 3 && parser.getName().equals(SharedGroupsInfo.ELEMENT)) {
                    done = true;
                }
            }
            return groupsInfo;
        }
    }

    public SharedGroupsInfo() {
        super(ELEMENT, NAMESPACE);
    }

    public List<String> getGroups() {
        return this.groups;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder buf) {
        buf.rightAngleBracket();
        for (String group : this.groups) {
            buf.element(Item.GROUP, group);
        }
        return buf;
    }
}
