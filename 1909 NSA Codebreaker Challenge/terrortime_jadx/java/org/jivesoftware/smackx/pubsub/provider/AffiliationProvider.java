package org.jivesoftware.smackx.pubsub.provider;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.Affiliation.AffiliationNamespace;
import org.jivesoftware.smackx.pubsub.Affiliation.Type;
import org.jxmpp.jid.BareJid;
import org.xmlpull.v1.XmlPullParser;

public class AffiliationProvider extends ExtensionElementProvider<Affiliation> {
    public Affiliation parse(XmlPullParser parser, int initialDepth) throws Exception {
        String node = parser.getAttributeValue(null, NodeElement.ELEMENT);
        BareJid jid = ParserUtils.getBareJidAttribute(parser);
        AffiliationNamespace namespace = AffiliationNamespace.fromXmlns(parser.getNamespace());
        String affiliationString = parser.getAttributeValue(null, Affiliation.ELEMENT);
        Type affiliationType = null;
        if (affiliationString != null) {
            affiliationType = Type.valueOf(affiliationString);
        }
        if (node != null && jid == null) {
            return new Affiliation(node, affiliationType, namespace);
        }
        if (node == null && jid != null) {
            return new Affiliation(jid, affiliationType, namespace);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid affililation. Either one of 'node' or 'jid' must be set. Node: ");
        sb.append(node);
        sb.append(". Jid: ");
        sb.append(jid);
        sb.append('.');
        throw new SmackException(sb.toString());
    }
}
