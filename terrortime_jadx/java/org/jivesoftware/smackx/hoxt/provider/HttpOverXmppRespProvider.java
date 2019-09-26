package org.jivesoftware.smackx.hoxt.provider;

import com.badguy.terrortime.BuildConfig;
import org.jivesoftware.smackx.hoxt.packet.HttpOverXmppResp;
import org.jivesoftware.smackx.hoxt.packet.HttpOverXmppResp.Builder;
import org.jivesoftware.smackx.shim.packet.HeadersExtension;
import org.xmlpull.v1.XmlPullParser;

public class HttpOverXmppRespProvider extends AbstractHttpOverXmppProvider<HttpOverXmppResp> {
    private static final String ATTRIBUTE_STATUS_CODE = "statusCode";
    private static final String ATTRIBUTE_STATUS_MESSAGE = "statusMessage";

    public HttpOverXmppResp parse(XmlPullParser parser, int initialDepth) throws Exception {
        String str = BuildConfig.FLAVOR;
        String version = parser.getAttributeValue(str, "version");
        String statusMessage = parser.getAttributeValue(str, ATTRIBUTE_STATUS_MESSAGE);
        int statusCode = Integer.parseInt(parser.getAttributeValue(str, ATTRIBUTE_STATUS_CODE));
        HeadersExtension headers = parseHeaders(parser);
        return ((Builder) ((Builder) ((Builder) HttpOverXmppResp.builder().setHeaders(headers)).setData(parseData(parser))).setStatusCode(statusCode).setStatusMessage(statusMessage).setVersion(version)).build();
    }
}
