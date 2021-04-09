package org.jivesoftware.smackx.bytestreams.ibb.provider;

import com.badguy.terrortime.BuildConfig;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Close;
import org.xmlpull.v1.XmlPullParser;

public class CloseIQProvider extends IQProvider<Close> {
    public Close parse(XmlPullParser parser, int initialDepth) {
        return new Close(parser.getAttributeValue(BuildConfig.FLAVOR, "sid"));
    }
}
