package org.jivesoftware.smackx.search;

import android.support.v4.app.NotificationCompat;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.SimpleIQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.hoxt.packet.Base64BinaryChunk;
import org.jivesoftware.smackx.nick.packet.Nick;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.Jid;
import org.xmlpull.v1.XmlPullParser;

public class UserSearch extends SimpleIQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "jabber:iq:search";

    public static class Provider extends IQProvider<IQ> {
        public IQ parse(XmlPullParser parser, int initialDepth) throws Exception {
            UserSearch search = null;
            SimpleUserSearch simpleUserSearch = new SimpleUserSearch();
            boolean done = false;
            while (!done) {
                int eventType = parser.next();
                if (eventType == 2 && parser.getName().equals("instructions")) {
                    UserSearch.buildDataForm(simpleUserSearch, parser.nextText(), parser);
                    return simpleUserSearch;
                } else if (eventType == 2 && parser.getName().equals("item")) {
                    simpleUserSearch.parseItems(parser);
                    return simpleUserSearch;
                } else if (eventType == 2 && parser.getNamespace().equals("jabber:x:data")) {
                    search = new UserSearch();
                    PacketParserUtils.addExtensionElement((Stanza) search, parser);
                } else if (eventType == 3 && parser.getName().equals("query")) {
                    done = true;
                }
            }
            if (search != null) {
                return search;
            }
            return simpleUserSearch;
        }
    }

    public UserSearch() {
        super("query", "jabber:iq:search");
    }

    public Form getSearchForm(XMPPConnection con, DomainBareJid searchService) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        UserSearch search = new UserSearch();
        search.setType(Type.get);
        search.setTo((Jid) searchService);
        return Form.getFormFrom((IQ) con.createStanzaCollectorAndSend(search).nextResultOrThrow());
    }

    public ReportedData sendSearchForm(XMPPConnection con, Form searchForm, DomainBareJid searchService) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        UserSearch search = new UserSearch();
        search.setType(Type.set);
        search.setTo((Jid) searchService);
        search.addExtension(searchForm.getDataFormToSend());
        return ReportedData.getReportedDataFrom((IQ) con.createStanzaCollectorAndSend(search).nextResultOrThrow());
    }

    public ReportedData sendSimpleSearchForm(XMPPConnection con, Form searchForm, DomainBareJid searchService) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        SimpleUserSearch search = new SimpleUserSearch();
        search.setForm(searchForm);
        search.setType(Type.set);
        search.setTo((Jid) searchService);
        return ((SimpleUserSearch) con.createStanzaCollectorAndSend(search).nextResultOrThrow()).getReportedData();
    }

    /* access modifiers changed from: private */
    public static void buildDataForm(SimpleUserSearch search, String instructions, XmlPullParser parser) throws Exception {
        String str;
        DataForm dataForm = new DataForm(DataForm.Type.form);
        boolean done = false;
        dataForm.setTitle("User Search");
        dataForm.addInstruction(instructions);
        while (true) {
            str = "jabber:x:data";
            if (done) {
                break;
            }
            int eventType = parser.next();
            if (eventType == 2 && !parser.getNamespace().equals(str)) {
                String name = parser.getName();
                FormField field = new FormField(name);
                if (name.equals("first")) {
                    field.setLabel("First Name");
                } else if (name.equals(Base64BinaryChunk.ATTRIBUTE_LAST)) {
                    field.setLabel("Last Name");
                } else if (name.equals(NotificationCompat.CATEGORY_EMAIL)) {
                    field.setLabel("Email Address");
                } else if (name.equals(Nick.ELEMENT_NAME)) {
                    field.setLabel("Nickname");
                }
                field.setType(FormField.Type.text_single);
                dataForm.addField(field);
            } else if (eventType == 3) {
                if (parser.getName().equals("query")) {
                    done = true;
                }
            } else if (eventType == 2 && parser.getNamespace().equals(str)) {
                PacketParserUtils.addExtensionElement((Stanza) search, parser);
                done = true;
            }
        }
        if (search.getExtension("x", str) == null) {
            search.addExtension(dataForm);
        }
    }
}
