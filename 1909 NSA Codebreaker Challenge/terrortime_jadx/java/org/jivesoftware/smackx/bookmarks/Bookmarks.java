package org.jivesoftware.smackx.bookmarks;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.iqprivate.packet.PrivateData;
import org.jivesoftware.smackx.iqprivate.provider.PrivateDataProvider;
import org.jivesoftware.smackx.nick.packet.Nick;
import org.jxmpp.jid.parts.Resourcepart;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class Bookmarks implements PrivateData {
    public static final String ELEMENT = "storage";
    public static final String NAMESPACE = "storage:bookmarks";
    private final List<BookmarkedConference> bookmarkedConferences = new ArrayList();
    private final List<BookmarkedURL> bookmarkedURLS = new ArrayList();

    public static class Provider implements PrivateDataProvider {
        public PrivateData parsePrivateData(XmlPullParser parser) throws XmlPullParserException, IOException {
            Bookmarks storage = new Bookmarks();
            boolean done = false;
            while (!done) {
                int eventType = parser.next();
                if (eventType == 2) {
                    if ("url".equals(parser.getName())) {
                        BookmarkedURL urlStorage = Bookmarks.getURLStorage(parser);
                        if (urlStorage != null) {
                            storage.addBookmarkedURL(urlStorage);
                        }
                    }
                }
                if (eventType == 2) {
                    if ("conference".equals(parser.getName())) {
                        storage.addBookmarkedConference(Bookmarks.getConferenceStorage(parser));
                    }
                }
                if (eventType == 3) {
                    if (Bookmarks.ELEMENT.equals(parser.getName())) {
                        done = true;
                    }
                }
            }
            return storage;
        }
    }

    public void addBookmarkedURL(BookmarkedURL bookmarkedURL) {
        this.bookmarkedURLS.add(bookmarkedURL);
    }

    public void removeBookmarkedURL(BookmarkedURL bookmarkedURL) {
        this.bookmarkedURLS.remove(bookmarkedURL);
    }

    public void clearBookmarkedURLS() {
        this.bookmarkedURLS.clear();
    }

    public void addBookmarkedConference(BookmarkedConference bookmarkedConference) {
        this.bookmarkedConferences.add(bookmarkedConference);
    }

    public void removeBookmarkedConference(BookmarkedConference bookmarkedConference) {
        this.bookmarkedConferences.remove(bookmarkedConference);
    }

    public void clearBookmarkedConferences() {
        this.bookmarkedConferences.clear();
    }

    public List<BookmarkedURL> getBookmarkedURLS() {
        return this.bookmarkedURLS;
    }

    public List<BookmarkedConference> getBookmarkedConferences() {
        return this.bookmarkedConferences;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public XmlStringBuilder toXML() {
        String str;
        XmlStringBuilder buf = new XmlStringBuilder();
        String str2 = ELEMENT;
        buf.halfOpenElement(str2).xmlnsAttribute(NAMESPACE).rightAngleBracket();
        Iterator it = getBookmarkedURLS().iterator();
        while (true) {
            str = "name";
            if (!it.hasNext()) {
                break;
            }
            BookmarkedURL urlStorage = (BookmarkedURL) it.next();
            if (!urlStorage.isShared()) {
                String str3 = "url";
                buf.halfOpenElement(str3).attribute(str, urlStorage.getName()).attribute(str3, urlStorage.getURL());
                buf.condAttribute(urlStorage.isRss(), "rss", "true");
                buf.closeEmptyElement();
            }
        }
        for (BookmarkedConference conference : getBookmarkedConferences()) {
            if (!conference.isShared()) {
                String str4 = "conference";
                buf.halfOpenElement(str4);
                buf.attribute(str, conference.getName());
                buf.attribute("autojoin", Boolean.toString(conference.isAutoJoin()));
                buf.attribute("jid", (CharSequence) conference.getJid());
                buf.rightAngleBracket();
                buf.optElement(Nick.ELEMENT_NAME, (CharSequence) conference.getNickname());
                buf.optElement("password", conference.getPassword());
                buf.closeElement(str4);
            }
        }
        buf.closeElement(str2);
        return buf;
    }

    /* access modifiers changed from: private */
    public static BookmarkedURL getURLStorage(XmlPullParser parser) throws IOException, XmlPullParserException {
        String str = BuildConfig.FLAVOR;
        String name = parser.getAttributeValue(str, "name");
        String str2 = "url";
        String url = parser.getAttributeValue(str, str2);
        String rssString = parser.getAttributeValue(str, "rss");
        BookmarkedURL urlStore = new BookmarkedURL(url, name, rssString != null && "true".equals(rssString));
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == 2) {
                if ("shared_bookmark".equals(parser.getName())) {
                    urlStore.setShared(true);
                }
            }
            if (eventType == 3 && str2.equals(parser.getName())) {
                done = true;
            }
        }
        return urlStore;
    }

    /* access modifiers changed from: private */
    public static BookmarkedConference getConferenceStorage(XmlPullParser parser) throws XmlPullParserException, IOException {
        String name = parser.getAttributeValue(BuildConfig.FLAVOR, "name");
        boolean autojoin = ParserUtils.getBooleanAttribute(parser, "autojoin", false);
        BookmarkedConference conf = new BookmarkedConference(ParserUtils.getBareJidAttribute(parser));
        conf.setName(name);
        conf.setAutoJoin(autojoin);
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == 2) {
                if (Nick.ELEMENT_NAME.equals(parser.getName())) {
                    conf.setNickname(Resourcepart.from(parser.nextText()));
                }
            }
            if (eventType == 2) {
                if ("password".equals(parser.getName())) {
                    conf.setPassword(parser.nextText());
                }
            }
            if (eventType == 2) {
                if ("shared_bookmark".equals(parser.getName())) {
                    conf.setShared(true);
                }
            }
            if (eventType == 3) {
                if ("conference".equals(parser.getName())) {
                    done = true;
                }
            }
        }
        return conf;
    }
}
