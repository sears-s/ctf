package org.jivesoftware.smack.roster.provider;

import java.io.IOException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.roster.packet.RosterVer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class RosterVerStreamFeatureProvider extends ExtensionElementProvider<RosterVer> {
    public RosterVer parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        return RosterVer.INSTANCE;
    }
}
