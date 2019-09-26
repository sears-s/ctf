package org.jivesoftware.smackx.address;

import java.util.List;
import org.jivesoftware.smackx.address.packet.MultipleAddresses;
import org.jivesoftware.smackx.address.packet.MultipleAddresses.Address;
import org.jivesoftware.smackx.address.packet.MultipleAddresses.Type;
import org.jxmpp.jid.Jid;

public class MultipleRecipientInfo {
    MultipleAddresses extension;

    MultipleRecipientInfo(MultipleAddresses extension2) {
        this.extension = extension2;
    }

    public List<Address> getTOAddresses() {
        return this.extension.getAddressesOfType(Type.to);
    }

    public List<Address> getCCAddresses() {
        return this.extension.getAddressesOfType(Type.cc);
    }

    public Jid getReplyRoom() {
        List<Address> replyRoom = this.extension.getAddressesOfType(Type.replyroom);
        if (replyRoom.isEmpty()) {
            return null;
        }
        return ((Address) replyRoom.get(0)).getJid();
    }

    public boolean shouldNotReply() {
        return !this.extension.getAddressesOfType(Type.noreply).isEmpty();
    }

    public Address getReplyAddress() {
        List<Address> replyTo = this.extension.getAddressesOfType(Type.replyto);
        if (replyTo.isEmpty()) {
            return null;
        }
        return (Address) replyTo.get(0);
    }
}
