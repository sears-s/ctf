package org.jivesoftware.smackx.hoxt.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.hoxt.packet.Base64BinaryChunk;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class Base64BinaryChunkProvider extends ExtensionElementProvider<Base64BinaryChunk> {
    public Base64BinaryChunk parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
        String str = BuildConfig.FLAVOR;
        String streamId = parser.getAttributeValue(str, Base64BinaryChunk.ATTRIBUTE_STREAM_ID);
        String nrString = parser.getAttributeValue(str, Base64BinaryChunk.ATTRIBUTE_NR);
        String lastString = parser.getAttributeValue(str, Base64BinaryChunk.ATTRIBUTE_LAST);
        boolean last = false;
        int nr = Integer.parseInt(nrString);
        if (lastString != null) {
            last = Boolean.parseBoolean(lastString);
        }
        String text = null;
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == 3) {
                if (parser.getName().equals(Base64BinaryChunk.ELEMENT_CHUNK)) {
                    done = true;
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("unexpected end tag of: ");
                    sb.append(parser.getName());
                    throw new IllegalArgumentException(sb.toString());
                }
            } else if (eventType == 4) {
                text = parser.getText();
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("unexpected eventType: ");
                sb2.append(eventType);
                throw new IllegalArgumentException(sb2.toString());
            }
        }
        return new Base64BinaryChunk(text, streamId, nr, last);
    }
}
