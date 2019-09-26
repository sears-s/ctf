package org.jivesoftware.smackx.search;

import java.util.List;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.DomainBareJid;

public class UserSearchManager {
    private final XMPPConnection con;
    private final UserSearch userSearch = new UserSearch();

    public UserSearchManager(XMPPConnection con2) {
        this.con = con2;
    }

    public Form getSearchForm(DomainBareJid searchService) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return this.userSearch.getSearchForm(this.con, searchService);
    }

    public ReportedData getSearchResults(Form searchForm, DomainBareJid searchService) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return this.userSearch.sendSearchForm(this.con, searchForm, searchService);
    }

    public List<DomainBareJid> getSearchServices() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(this.con).findServices("jabber:iq:search", false, false);
    }
}
