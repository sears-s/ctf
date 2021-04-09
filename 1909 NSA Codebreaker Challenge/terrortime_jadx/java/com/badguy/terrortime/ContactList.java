package com.badguy.terrortime;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Localpart;

public class ContactList {
    /* access modifiers changed from: private */
    public HashMap<String, Boolean> mAvailabilityMap = new HashMap<>();
    /* access modifiers changed from: private */
    public Client mClient;
    private HashMap<Integer, Jid> mContactMap = new HashMap<>();
    private List<String> mContacts = new ArrayList();
    private HashMap<String, RosterEntry> mJidMap = new HashMap<>();
    private Roster mRoster;

    private class ContactListener implements RosterListener {
        private ContactListener() {
        }

        public void entriesAdded(Collection<Jid> addresses) {
            ContactList.this.addContacts(addresses);
            ContactList.this.sendBroadcast("XMPP_CONTACTS_CHANGED");
        }

        public void entriesUpdated(Collection<Jid> collection) {
            ContactList.this.refreshContactList();
            ContactList.this.sendBroadcast("XMPP_CONTACTS_CHANGED");
        }

        public void entriesDeleted(Collection<Jid> collection) {
            ContactList.this.refreshContactList();
            ContactList.this.sendBroadcast("XMPP_CONTACTS_CHANGED");
        }

        public void presenceChanged(Presence presence) {
            Jid jid = presence.getFrom();
            boolean available = presence.getType() == Type.available;
            Localpart local = presence.getFrom().getLocalpartOrNull();
            if (local == null) {
                ContactList.this.mAvailabilityMap.put(jid.toString(), Boolean.valueOf(available));
            } else {
                ContactList.this.mAvailabilityMap.put(local.toString(), Boolean.valueOf(available));
            }
            if (available) {
                Contact contact = (Contact) ContactList.this.mClient.getContact(jid.asBareJid().toString()).orElse(null);
                if (contact != null) {
                    contact.toggleRefreshOn();
                }
            }
            ContactList.this.sendBroadcast("XMPP_CONTACTS_CHANGED");
        }
    }

    public ContactList(Roster roster, Client client) {
        this.mClient = client;
        this.mRoster = roster;
        this.mRoster.addRosterListener(new ContactListener());
    }

    public void refreshContactList() {
        this.mJidMap.clear();
        this.mContactMap.clear();
        this.mContacts.clear();
        Roster roster = this.mRoster;
        if (roster != null) {
            addContacts((Collection) roster.getEntries().stream().map($$Lambda$ContactList$09gMaIPFwd9UDclAHTGVNxlQOs.INSTANCE).collect(Collectors.toList()));
        }
    }

    /* access modifiers changed from: private */
    public void addContacts(Collection<Jid> contacts) {
        contacts.forEach(new Consumer() {
            public final void accept(Object obj) {
                ContactList.this.lambda$addContacts$0$ContactList((Jid) obj);
            }
        });
    }

    public /* synthetic */ void lambda$addContacts$0$ContactList(Jid jid) {
        this.mJidMap.put(jid.toString(), this.mRoster.getEntry(jid.asBareJid()));
        this.mContactMap.put(Integer.valueOf(this.mContacts.size()), jid);
        this.mClient.addContact(jid.toString());
        Localpart local = jid.getLocalpartOrNull();
        if (local == null) {
            this.mContacts.add(jid.toString());
            this.mAvailabilityMap.putIfAbsent(jid.toString(), Boolean.FALSE);
            return;
        }
        this.mContacts.add(local.toString());
        this.mAvailabilityMap.putIfAbsent(local.toString(), Boolean.FALSE);
    }

    public Optional<RosterEntry> getRosterEntry(String jid) {
        if (jid == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.mJidMap.get(jid));
    }

    public Optional<Jid> getJidFromString(String jid) {
        return ((Optional) Optional.ofNullable(getRosterEntry(jid)).orElseGet($$Lambda$ContactList$Vi20EXpwvwXvQG5DZy6UxPK9iM.INSTANCE)).map($$Lambda$ContactList$7IaMDzmRIGb710fxba30RRcHo.INSTANCE);
    }

    public Optional<String> getLocalPart(String jid) {
        return ((Optional) Optional.ofNullable(getRosterEntry(jid)).orElseGet($$Lambda$ContactList$Vi20EXpwvwXvQG5DZy6UxPK9iM.INSTANCE)).map($$Lambda$ContactList$lug0BpjG90OFpXLjcQRVfOcwvaI.INSTANCE).map($$Lambda$ContactList$VP9Vi6dnBqtMmiP_oVVUGWH3Gxo.INSTANCE);
    }

    public Optional<Roster> getRoster() {
        return Optional.ofNullable(this.mRoster);
    }

    public Set<Jid> getSetOfJids() {
        Roster roster = this.mRoster;
        if (roster == null) {
            return Collections.emptySet();
        }
        return (Set) roster.getEntries().stream().map($$Lambda$ContactList$09gMaIPFwd9UDclAHTGVNxlQOs.INSTANCE).collect(Collectors.toSet());
    }

    public List<String> getContactNames() {
        return this.mContacts;
    }

    public List<Jid> getContactJids() {
        return (List) this.mContactMap.values().stream().collect(Collectors.toList());
    }

    public Optional<Jid> getJidAtIndex(int index) {
        return Optional.ofNullable(this.mContactMap.get(Integer.valueOf(index)));
    }

    static /* synthetic */ Exception lambda$getUserJid$2() {
        return new Exception("No connection object");
    }

    public Optional<Jid> getUserJid() {
        try {
            return Optional.of(((AbstractXMPPConnection) TerrorTimeApplication.getInstance().getXMPPTCPConnection().orElseThrow($$Lambda$ContactList$yM6Y_bc5OGVMb5HpOjHLyarW_4.INSTANCE)).getUser());
        } catch (Throwable e) {
            Log.e("EXCEPTION", "Failed to get user jid", e);
            return Optional.empty();
        }
    }

    public HashMap<String, Boolean> getAvailabilityMap() {
        return this.mAvailabilityMap;
    }

    /* access modifiers changed from: private */
    public void sendBroadcast(String intentAction) {
        Intent intent = new Intent();
        intent.setAction(intentAction);
        LocalBroadcastManager.getInstance(TerrorTimeApplication.getAppContext()).sendBroadcast(intent);
    }
}
