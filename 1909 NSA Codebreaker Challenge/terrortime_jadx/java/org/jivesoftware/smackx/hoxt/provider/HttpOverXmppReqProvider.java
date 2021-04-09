package org.jivesoftware.smackx.hoxt.provider;

import com.badguy.terrortime.BuildConfig;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp.Ibb;
import org.jivesoftware.smackx.hoxt.packet.HttpMethod;
import org.jivesoftware.smackx.hoxt.packet.HttpOverXmppReq;
import org.jivesoftware.smackx.hoxt.packet.HttpOverXmppReq.Builder;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.xmlpull.v1.XmlPullParser;

public class HttpOverXmppReqProvider extends AbstractHttpOverXmppProvider<HttpOverXmppReq> {
    private static final String ATTRIBUTE_MAX_CHUNK_SIZE = "maxChunkSize";
    private static final String ATTRIBUTE_METHOD = "method";
    private static final String ATTRIBUTE_RESOURCE = "resource";

    public HttpOverXmppReq parse(XmlPullParser parser, int initialDepth) throws Exception {
        Builder builder = HttpOverXmppReq.builder();
        String str = BuildConfig.FLAVOR;
        builder.setResource(parser.getAttributeValue(str, ATTRIBUTE_RESOURCE));
        builder.setVersion(parser.getAttributeValue(str, "version"));
        builder.setMethod(HttpMethod.valueOf(parser.getAttributeValue(str, ATTRIBUTE_METHOD)));
        String sipubStr = parser.getAttributeValue(str, "sipub");
        String ibbStr = parser.getAttributeValue(str, Ibb.ELEMENT);
        String jingleStr = parser.getAttributeValue(str, Jingle.ELEMENT);
        if (sipubStr != null) {
            builder.setSipub(ParserUtils.parseXmlBoolean(sipubStr));
        }
        if (ibbStr != null) {
            builder.setIbb(ParserUtils.parseXmlBoolean(ibbStr));
        }
        if (jingleStr != null) {
            builder.setJingle(ParserUtils.parseXmlBoolean(jingleStr));
        }
        String maxChunkSize = parser.getAttributeValue(str, ATTRIBUTE_MAX_CHUNK_SIZE);
        if (maxChunkSize != null) {
            builder.setMaxChunkSize(Integer.parseInt(maxChunkSize));
        }
        builder.setHeaders(parseHeaders(parser));
        builder.setData(parseData(parser));
        return builder.build();
    }
}
