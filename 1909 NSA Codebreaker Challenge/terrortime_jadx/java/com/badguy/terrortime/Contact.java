package com.badguy.terrortime;

import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;

public class Contact {
    private TextAppField clientId = new TextAppField();
    private TextAppField contactId = new TextAppField();
    private boolean refreshKeys;
    private Set<PublicKey> rsaPublicKeySet;

    public final Set<PublicKey> getPublicKeys() {
        if (this.refreshKeys) {
            this.rsaPublicKeySet = VCardHelper.getPublicKeys(this.contactId.getValue());
            this.refreshKeys = false;
        }
        return this.rsaPublicKeySet;
    }

    public final void setPublicKeys(Set<PublicKey> keySet) {
        this.rsaPublicKeySet = keySet;
    }

    public final boolean addPublicKey(PublicKey key) {
        return this.rsaPublicKeySet.add(key);
    }

    public final boolean addPublicKeys(Set<PublicKey> keySet) {
        return this.rsaPublicKeySet.addAll(keySet);
    }

    public final boolean removePublicKey(PublicKey key) {
        return this.rsaPublicKeySet.remove(key);
    }

    public final void toggleRefreshOn() {
        this.refreshKeys = true;
    }

    public Contact(String clientId2, String contactId2) {
        if (!(contactId2 == null || clientId2 == null)) {
            this.contactId.setValue(contactId2);
            this.clientId.setValue(clientId2);
        }
        this.rsaPublicKeySet = new HashSet();
        this.refreshKeys = true;
    }

    public final String getContactId() {
        return this.contactId.getValue();
    }

    public final void setContactId(String contactId2) {
        if (contactId2 != null) {
            this.contactId.setValue(contactId2);
        }
    }

    public final String getClientId() {
        return this.clientId.getValue();
    }

    public final void setClientId(String clientId2) {
        if (clientId2 != null) {
            this.clientId.setValue(clientId2);
        }
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Contact other = (Contact) Contact.class.cast(o);
        if (!this.contactId.equals(other.contactId) || !this.clientId.equals(other.clientId)) {
            return false;
        }
        Set<PublicKey> set = this.rsaPublicKeySet;
        if (set != null) {
            Set<PublicKey> set2 = other.rsaPublicKeySet;
            if (set2 == null || set.equals(set2)) {
                return true;
            }
            return false;
        }
        return true;
    }
}
