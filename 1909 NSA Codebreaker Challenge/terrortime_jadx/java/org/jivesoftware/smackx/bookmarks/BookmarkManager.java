package org.jivesoftware.smackx.bookmarks;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.bookmarks.Bookmarks.Provider;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.parts.Resourcepart;

public final class BookmarkManager {
    private static final Map<XMPPConnection, BookmarkManager> bookmarkManagerMap = new WeakHashMap();
    private final Object bookmarkLock = new Object();
    private Bookmarks bookmarks;
    private final PrivateDataManager privateDataManager;

    static {
        PrivateDataManager.addPrivateDataProvider(Bookmarks.ELEMENT, Bookmarks.NAMESPACE, new Provider());
    }

    public static synchronized BookmarkManager getBookmarkManager(XMPPConnection connection) {
        BookmarkManager manager;
        synchronized (BookmarkManager.class) {
            manager = (BookmarkManager) bookmarkManagerMap.get(connection);
            if (manager == null) {
                manager = new BookmarkManager(connection);
                bookmarkManagerMap.put(connection, manager);
            }
        }
        return manager;
    }

    private BookmarkManager(XMPPConnection connection) {
        this.privateDataManager = PrivateDataManager.getInstanceFor(connection);
    }

    public List<BookmarkedConference> getBookmarkedConferences() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        retrieveBookmarks();
        return Collections.unmodifiableList(this.bookmarks.getBookmarkedConferences());
    }

    public void addBookmarkedConference(String name, EntityBareJid jid, boolean isAutoJoin, Resourcepart nickname, String password) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        retrieveBookmarks();
        BookmarkedConference bookmark = new BookmarkedConference(name, jid, isAutoJoin, nickname, password);
        List<BookmarkedConference> conferences = this.bookmarks.getBookmarkedConferences();
        if (conferences.contains(bookmark)) {
            BookmarkedConference oldConference = (BookmarkedConference) conferences.get(conferences.indexOf(bookmark));
            if (!oldConference.isShared()) {
                oldConference.setAutoJoin(isAutoJoin);
                oldConference.setName(name);
                oldConference.setNickname(nickname);
                oldConference.setPassword(password);
            } else {
                throw new IllegalArgumentException("Cannot modify shared bookmark");
            }
        } else {
            this.bookmarks.addBookmarkedConference(bookmark);
        }
        this.privateDataManager.setPrivateData(this.bookmarks);
    }

    public void removeBookmarkedConference(EntityBareJid jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        retrieveBookmarks();
        Iterator<BookmarkedConference> it = this.bookmarks.getBookmarkedConferences().iterator();
        while (it.hasNext()) {
            BookmarkedConference conference = (BookmarkedConference) it.next();
            if (conference.getJid().equals((CharSequence) jid)) {
                if (!conference.isShared()) {
                    it.remove();
                    this.privateDataManager.setPrivateData(this.bookmarks);
                    return;
                }
                throw new IllegalArgumentException("Conference is shared and can't be removed");
            }
        }
    }

    public List<BookmarkedURL> getBookmarkedURLs() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        retrieveBookmarks();
        return Collections.unmodifiableList(this.bookmarks.getBookmarkedURLS());
    }

    public void addBookmarkedURL(String URL, String name, boolean isRSS) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        retrieveBookmarks();
        BookmarkedURL bookmark = new BookmarkedURL(URL, name, isRSS);
        List<BookmarkedURL> urls = this.bookmarks.getBookmarkedURLS();
        if (urls.contains(bookmark)) {
            BookmarkedURL oldURL = (BookmarkedURL) urls.get(urls.indexOf(bookmark));
            if (!oldURL.isShared()) {
                oldURL.setName(name);
                oldURL.setRss(isRSS);
            } else {
                throw new IllegalArgumentException("Cannot modify shared bookmarks");
            }
        } else {
            this.bookmarks.addBookmarkedURL(bookmark);
        }
        this.privateDataManager.setPrivateData(this.bookmarks);
    }

    public void removeBookmarkedURL(String bookmarkURL) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        retrieveBookmarks();
        Iterator<BookmarkedURL> it = this.bookmarks.getBookmarkedURLS().iterator();
        while (it.hasNext()) {
            BookmarkedURL bookmark = (BookmarkedURL) it.next();
            if (bookmark.getURL().equalsIgnoreCase(bookmarkURL)) {
                if (!bookmark.isShared()) {
                    it.remove();
                    this.privateDataManager.setPrivateData(this.bookmarks);
                    return;
                }
                throw new IllegalArgumentException("Cannot delete a shared bookmark.");
            }
        }
    }

    public boolean isSupported() throws NoResponseException, NotConnectedException, XMPPErrorException, InterruptedException {
        return this.privateDataManager.isSupported();
    }

    private Bookmarks retrieveBookmarks() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Bookmarks bookmarks2;
        synchronized (this.bookmarkLock) {
            if (this.bookmarks == null) {
                this.bookmarks = (Bookmarks) this.privateDataManager.getPrivateData(Bookmarks.ELEMENT, Bookmarks.NAMESPACE);
            }
            bookmarks2 = this.bookmarks;
        }
        return bookmarks2;
    }
}
