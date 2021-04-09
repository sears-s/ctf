package org.jivesoftware.smackx.privacy.packet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;

public class Privacy extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "jabber:iq:privacy";
    private String activeName;
    private boolean declineActiveList = false;
    private boolean declineDefaultList = false;
    private String defaultName;
    private final Map<String, List<PrivacyItem>> itemLists = new HashMap();

    public Privacy() {
        super("query", "jabber:iq:privacy");
    }

    public List<PrivacyItem> setPrivacyList(String listName, List<PrivacyItem> listItem) {
        getItemLists().put(listName, listItem);
        return listItem;
    }

    public List<PrivacyItem> setActivePrivacyList() {
        setActiveName(getDefaultName());
        return (List) getItemLists().get(getActiveName());
    }

    public void deletePrivacyList(String listName) {
        getItemLists().remove(listName);
        if (getDefaultName() != null && listName.equals(getDefaultName())) {
            setDefaultName(null);
        }
    }

    public List<PrivacyItem> getActivePrivacyList() {
        if (getActiveName() == null) {
            return null;
        }
        return (List) getItemLists().get(getActiveName());
    }

    public List<PrivacyItem> getDefaultPrivacyList() {
        if (getDefaultName() == null) {
            return null;
        }
        return (List) getItemLists().get(getDefaultName());
    }

    public List<PrivacyItem> getPrivacyList(String listName) {
        return (List) getItemLists().get(listName);
    }

    public PrivacyItem getItem(String listName, int order) {
        Iterator<PrivacyItem> values = getPrivacyList(listName).iterator();
        PrivacyItem itemFound = null;
        while (itemFound == null && values.hasNext()) {
            PrivacyItem element = (PrivacyItem) values.next();
            if (element.getOrder() == ((long) order)) {
                itemFound = element;
            }
        }
        return itemFound;
    }

    public boolean changeDefaultList(String newDefault) {
        if (!getItemLists().containsKey(newDefault)) {
            return false;
        }
        setDefaultName(newDefault);
        return true;
    }

    public void deleteList(String listName) {
        getItemLists().remove(listName);
    }

    public String getActiveName() {
        return this.activeName;
    }

    public void setActiveName(String activeName2) {
        this.activeName = activeName2;
    }

    public String getDefaultName() {
        return this.defaultName;
    }

    public void setDefaultName(String defaultName2) {
        this.defaultName = defaultName2;
    }

    public Map<String, List<PrivacyItem>> getItemLists() {
        return this.itemLists;
    }

    public boolean isDeclineActiveList() {
        return this.declineActiveList;
    }

    public void setDeclineActiveList(boolean declineActiveList2) {
        this.declineActiveList = declineActiveList2;
    }

    public boolean isDeclineDefaultList() {
        return this.declineDefaultList;
    }

    public void setDeclineDefaultList(boolean declineDefaultList2) {
        this.declineDefaultList = declineDefaultList2;
    }

    public Set<String> getPrivacyListNames() {
        return this.itemLists.keySet();
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder buf) {
        buf.rightAngleBracket();
        String str = "\"/>";
        if (isDeclineActiveList()) {
            buf.append((CharSequence) "<active/>");
        } else if (getActiveName() != null) {
            buf.append((CharSequence) "<active name=\"").escape(getActiveName()).append((CharSequence) str);
        }
        if (isDeclineDefaultList()) {
            buf.append((CharSequence) "<default/>");
        } else if (getDefaultName() != null) {
            buf.append((CharSequence) "<default name=\"").escape(getDefaultName()).append((CharSequence) str);
        }
        for (Entry<String, List<PrivacyItem>> entry : getItemLists().entrySet()) {
            String listName = (String) entry.getKey();
            List<PrivacyItem> items = (List) entry.getValue();
            String str2 = "<list name=\"";
            if (items.isEmpty()) {
                buf.append((CharSequence) str2).escape(listName).append((CharSequence) str);
            } else {
                buf.append((CharSequence) str2).escape(listName).append((CharSequence) "\">");
            }
            for (PrivacyItem item : items) {
                buf.append((CharSequence) item.toXML());
            }
            if (!items.isEmpty()) {
                buf.append((CharSequence) "</list>");
            }
        }
        return buf;
    }
}
