package org.jivesoftware.smackx.bob.provider;

import com.badguy.terrortime.BuildConfig;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.bob.BoBData;
import org.jivesoftware.smackx.bob.BoBHash;
import org.jivesoftware.smackx.bob.element.BoBIQ;
import org.xmlpull.v1.XmlPullParser;

public class BoBIQProvider extends IQProvider<BoBIQ> {
    public BoBIQ parse(XmlPullParser parser, int initialDepth) throws Exception {
        BoBData bobData;
        String str = BuildConfig.FLAVOR;
        BoBHash bobHash = BoBHash.fromCid(parser.getAttributeValue(str, "cid"));
        String dataType = parser.getAttributeValue(str, "type");
        int maxAge = ParserUtils.getIntegerAttribute(parser, "max-age", -1);
        String base64EncodedData = parser.nextText();
        if (dataType != null) {
            bobData = new BoBData(dataType, base64EncodedData, maxAge);
        } else {
            bobData = null;
        }
        return new BoBIQ(bobHash, bobData);
    }
}
