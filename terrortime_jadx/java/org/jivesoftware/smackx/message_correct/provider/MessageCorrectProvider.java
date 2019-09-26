package org.jivesoftware.smackx.message_correct.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.message_correct.element.MessageCorrectExtension;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class MessageCorrectProvider extends ExtensionElementProvider<MessageCorrectExtension> {
    public MessageCorrectExtension parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        return new MessageCorrectExtension(parser.getAttributeValue(BuildConfig.FLAVOR, "id"));
    }
}
