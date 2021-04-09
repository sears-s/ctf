package org.jivesoftware.smackx.delay.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class AbstractDelayInformationProvider extends ExtensionElementProvider<DelayInformation> {
    /* access modifiers changed from: protected */
    public abstract Date parseDate(String str) throws ParseException;

    public final DelayInformation parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        String str = BuildConfig.FLAVOR;
        String stampString = parser.getAttributeValue(str, "stamp");
        String from = parser.getAttributeValue(str, PrivacyItem.SUBSCRIPTION_FROM);
        String reason = null;
        if (!parser.isEmptyElementTag()) {
            int event = parser.next();
            if (event == 3) {
                reason = BuildConfig.FLAVOR;
            } else if (event == 4) {
                reason = parser.getText();
                parser.next();
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Unexpected event: ");
                sb.append(event);
                throw new IllegalStateException(sb.toString());
            }
        } else {
            parser.next();
        }
        try {
            return new DelayInformation(parseDate(stampString), from, reason);
        } catch (ParseException e) {
            throw new SmackException((Throwable) e);
        }
    }
}
