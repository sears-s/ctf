package org.jivesoftware.smackx.iqlast.packet;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.jxmpp.jid.Jid;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class LastActivity extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "jabber:iq:last";
    public long lastActivity;
    public String message;

    public static class Provider extends IQProvider<LastActivity> {
        public LastActivity parse(XmlPullParser parser, int initialDepth) throws SmackException, XmlPullParserException {
            LastActivity lastActivity = new LastActivity();
            String seconds = parser.getAttributeValue(BuildConfig.FLAVOR, "seconds");
            if (seconds != null) {
                try {
                    lastActivity.setLastActivity(Long.parseLong(seconds));
                } catch (NumberFormatException e) {
                    throw new SmackException("Could not parse last activity number", e);
                }
            }
            try {
                lastActivity.setMessage(parser.nextText());
                return lastActivity;
            } catch (IOException e2) {
                throw new SmackException((Throwable) e2);
            }
        }
    }

    public LastActivity() {
        super("query", NAMESPACE);
        this.lastActivity = -1;
        setType(Type.get);
    }

    public LastActivity(Jid to) {
        this();
        setTo(to);
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.optLongAttribute("seconds", Long.valueOf(this.lastActivity));
        xml.setEmptyElement();
        return xml;
    }

    public void setLastActivity(long lastActivity2) {
        this.lastActivity = lastActivity2;
    }

    /* access modifiers changed from: private */
    public void setMessage(String message2) {
        this.message = message2;
    }

    public long getIdleTime() {
        return this.lastActivity;
    }

    public String getStatusMessage() {
        return this.message;
    }
}
