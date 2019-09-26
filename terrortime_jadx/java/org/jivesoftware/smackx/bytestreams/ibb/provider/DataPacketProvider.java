package org.jivesoftware.smackx.bytestreams.ibb.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Data;
import org.jivesoftware.smackx.bytestreams.ibb.packet.DataPacketExtension;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class DataPacketProvider {

    public static class IQProvider extends org.jivesoftware.smack.provider.IQProvider<Data> {
        private static final PacketExtensionProvider packetExtensionProvider = new PacketExtensionProvider();

        public Data parse(XmlPullParser parser, int initialDepth) throws Exception {
            return new Data((DataPacketExtension) packetExtensionProvider.parse(parser));
        }
    }

    public static class PacketExtensionProvider extends ExtensionElementProvider<DataPacketExtension> {
        public DataPacketExtension parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
            String str = BuildConfig.FLAVOR;
            return new DataPacketExtension(parser.getAttributeValue(str, "sid"), Long.parseLong(parser.getAttributeValue(str, "seq")), parser.nextText());
        }
    }
}
