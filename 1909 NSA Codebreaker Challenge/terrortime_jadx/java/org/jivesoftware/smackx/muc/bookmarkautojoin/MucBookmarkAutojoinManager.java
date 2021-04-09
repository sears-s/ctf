package org.jivesoftware.smackx.muc.bookmarkautojoin;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;
import org.jivesoftware.smackx.muc.MultiUserChat.MucCreateConfigFormHandle;
import org.jivesoftware.smackx.muc.MultiUserChatException.NotAMucServiceException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.parts.Resourcepart;

public final class MucBookmarkAutojoinManager extends Manager {
    private static final Map<XMPPConnection, MucBookmarkAutojoinManager> INSTANCES = new WeakHashMap();
    private static final Logger LOGGER = Logger.getLogger(MucBookmarkAutojoinManager.class.getName());
    private static boolean autojoinEnabledDefault = false;
    /* access modifiers changed from: private */
    public boolean autojoinEnabled = autojoinEnabledDefault;
    private final BookmarkManager bookmarkManager;
    private final MultiUserChatManager multiUserChatManager;

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                MucBookmarkAutojoinManager.getInstanceFor(connection);
            }
        });
    }

    public static void setAutojoinPerDefault(boolean autojoin) {
        autojoinEnabledDefault = autojoin;
    }

    public static synchronized MucBookmarkAutojoinManager getInstanceFor(XMPPConnection connection) {
        MucBookmarkAutojoinManager mbam;
        synchronized (MucBookmarkAutojoinManager.class) {
            mbam = (MucBookmarkAutojoinManager) INSTANCES.get(connection);
            if (mbam == null) {
                mbam = new MucBookmarkAutojoinManager(connection);
                INSTANCES.put(connection, mbam);
            }
        }
        return mbam;
    }

    private MucBookmarkAutojoinManager(XMPPConnection connection) {
        super(connection);
        this.multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        this.bookmarkManager = BookmarkManager.getBookmarkManager(connection);
        connection.addConnectionListener(new AbstractConnectionListener() {
            public void authenticated(XMPPConnection connection, boolean resumed) {
                if (MucBookmarkAutojoinManager.this.autojoinEnabled) {
                    MucBookmarkAutojoinManager.this.autojoinBookmarkedConferences();
                }
            }
        });
    }

    public void setAutojoinEnabled(boolean autojoin) {
        this.autojoinEnabled = autojoin;
    }

    public void autojoinBookmarkedConferences() {
        String str = "Could not autojoin bookmarked MUC";
        String str2 = "Could not get MUC bookmarks";
        try {
            List<BookmarkedConference> bookmarkedConferences = this.bookmarkManager.getBookmarkedConferences();
            Resourcepart defaultNick = connection().getUser().getResourcepart();
            for (BookmarkedConference bookmarkedConference : bookmarkedConferences) {
                if (bookmarkedConference.isAutoJoin()) {
                    Resourcepart nick = bookmarkedConference.getNickname();
                    if (nick == null) {
                        nick = defaultNick;
                    }
                    try {
                        MucCreateConfigFormHandle handle = this.multiUserChatManager.getMultiUserChat(bookmarkedConference.getJid()).createOrJoinIfNecessary(nick, bookmarkedConference.getPassword());
                        if (handle != null) {
                            handle.makeInstant();
                        }
                    } catch (InterruptedException | NotConnectedException e) {
                        LOGGER.log(Level.FINER, str, e);
                    } catch (NoResponseException | XMPPErrorException | NotAMucServiceException e2) {
                        LOGGER.log(Level.WARNING, str, e2);
                    }
                }
            }
        } catch (InterruptedException | NotConnectedException e3) {
            LOGGER.log(Level.FINER, str2, e3);
        } catch (NoResponseException | XMPPErrorException e4) {
            LOGGER.log(Level.WARNING, str2, e4);
        }
    }
}
