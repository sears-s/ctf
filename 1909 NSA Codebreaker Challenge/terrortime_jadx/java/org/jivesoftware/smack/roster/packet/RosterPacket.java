package org.jivesoftware.smack.roster.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jxmpp.jid.BareJid;

public class RosterPacket extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "jabber:iq:roster";
    private final List<Item> rosterItems = new ArrayList();
    private String rosterVersion;

    public static class Item implements NamedElement {
        public static final String ELEMENT = "item";
        public static final String GROUP = "group";
        private boolean approved;
        private final Set<String> groupNames;
        private ItemType itemType;
        private final BareJid jid;
        private String name;
        private boolean subscriptionPending;

        public Item(BareJid jid2, String name2) {
            this(jid2, name2, false);
        }

        public Item(BareJid jid2, String name2, boolean subscriptionPending2) {
            this.itemType = ItemType.none;
            this.jid = (BareJid) Objects.requireNonNull(jid2);
            this.name = name2;
            this.subscriptionPending = subscriptionPending2;
            this.groupNames = new CopyOnWriteArraySet();
        }

        public String getElementName() {
            return "item";
        }

        @Deprecated
        public String getUser() {
            return this.jid.toString();
        }

        public BareJid getJid() {
            return this.jid;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name2) {
            this.name = name2;
        }

        public ItemType getItemType() {
            return this.itemType;
        }

        public void setItemType(ItemType itemType2) {
            this.itemType = (ItemType) Objects.requireNonNull(itemType2, "itemType must not be null");
        }

        public void setSubscriptionPending(boolean subscriptionPending2) {
            this.subscriptionPending = subscriptionPending2;
        }

        public boolean isSubscriptionPending() {
            return this.subscriptionPending;
        }

        public boolean isApproved() {
            return this.approved;
        }

        public void setApproved(boolean approved2) {
            this.approved = approved2;
        }

        public Set<String> getGroupNames() {
            return Collections.unmodifiableSet(this.groupNames);
        }

        public void addGroupName(String groupName) {
            this.groupNames.add(groupName);
        }

        public void removeGroupName(String groupName) {
            this.groupNames.remove(groupName);
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
            xml.attribute("jid", (CharSequence) this.jid);
            xml.optAttribute("name", this.name);
            xml.optAttribute("subscription", (Enum<?>) this.itemType);
            if (this.subscriptionPending) {
                xml.append((CharSequence) " ask='subscribe'");
            }
            xml.optBooleanAttribute("approved", this.approved);
            xml.rightAngleBracket();
            for (String groupName : this.groupNames) {
                String str = GROUP;
                xml.openElement(str).escape(groupName).closeElement(str);
            }
            xml.closeElement((NamedElement) this);
            return xml;
        }

        public int hashCode() {
            int i = 1 * 31;
            Set<String> set = this.groupNames;
            int i2 = 0;
            int result = (((i + (set == null ? 0 : set.hashCode())) * 31) + (this.subscriptionPending ^ true ? 1 : 0)) * 31;
            ItemType itemType2 = this.itemType;
            int result2 = (result + (itemType2 == null ? 0 : itemType2.hashCode())) * 31;
            String str = this.name;
            int result3 = (result2 + (str == null ? 0 : str.hashCode())) * 31;
            BareJid bareJid = this.jid;
            if (bareJid != null) {
                i2 = bareJid.hashCode();
            }
            return ((result3 + i2) * 31) + (this.approved ? 1 : 0);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Item other = (Item) obj;
            Set<String> set = this.groupNames;
            if (set == null) {
                if (other.groupNames != null) {
                    return false;
                }
            } else if (!set.equals(other.groupNames)) {
                return false;
            }
            if (this.subscriptionPending != other.subscriptionPending || this.itemType != other.itemType) {
                return false;
            }
            String str = this.name;
            if (str == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!str.equals(other.name)) {
                return false;
            }
            BareJid bareJid = this.jid;
            if (bareJid == null) {
                if (other.jid != null) {
                    return false;
                }
            } else if (!bareJid.equals((CharSequence) other.jid)) {
                return false;
            }
            if (this.approved != other.approved) {
                return false;
            }
            return true;
        }
    }

    public enum ItemType {
        none(8869),
        to(8592),
        from(8594),
        both(8596),
        remove(9889);
        
        private static final char ME = '●';
        private final String symbol;

        private ItemType(char secondSymbolChar) {
            StringBuilder sb = new StringBuilder(2);
            sb.append(ME);
            sb.append(secondSymbolChar);
            this.symbol = sb.toString();
        }

        public static ItemType fromString(String string) {
            if (StringUtils.isNullOrEmpty((CharSequence) string)) {
                return none;
            }
            return valueOf(string.toLowerCase(Locale.US));
        }

        public String asSymbol() {
            return this.symbol;
        }
    }

    public RosterPacket() {
        super("query", NAMESPACE);
    }

    public void addRosterItem(Item item) {
        synchronized (this.rosterItems) {
            this.rosterItems.add(item);
        }
    }

    public int getRosterItemCount() {
        int size;
        synchronized (this.rosterItems) {
            size = this.rosterItems.size();
        }
        return size;
    }

    public List<Item> getRosterItems() {
        ArrayList arrayList;
        synchronized (this.rosterItems) {
            arrayList = new ArrayList(this.rosterItems);
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder buf) {
        buf.optAttribute(RosterVer.ELEMENT, this.rosterVersion);
        buf.rightAngleBracket();
        synchronized (this.rosterItems) {
            for (Item entry : this.rosterItems) {
                buf.append(entry.toXML((String) null));
            }
        }
        return buf;
    }

    public String getVersion() {
        return this.rosterVersion;
    }

    public void setVersion(String version) {
        this.rosterVersion = version;
    }
}
