package org.jivesoftware.smackx.iot.provisioning.provider;

import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.iot.provisioning.element.IoTIsFriendResponse;
import org.jivesoftware.smackx.mam.element.MamElements.MamResultExtension;
import org.xmlpull.v1.XmlPullParser;

public class IoTIsFriendResponseProvider extends IQProvider<IoTIsFriendResponse> {
    public IoTIsFriendResponse parse(XmlPullParser parser, int initialDepth) throws Exception {
        return new IoTIsFriendResponse(ParserUtils.getJidAttribute(parser).asBareJid(), ParserUtils.getBooleanAttribute(parser, MamResultExtension.ELEMENT).booleanValue());
    }
}
