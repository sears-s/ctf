package org.jivesoftware.smackx.privacy;

import java.util.List;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;

public class PrivacyList {
    private final boolean isActiveList;
    private final boolean isDefaultList;
    private final List<PrivacyItem> items;
    private final String listName;

    protected PrivacyList(boolean isActiveList2, boolean isDefaultList2, String listName2, List<PrivacyItem> privacyItems) {
        this.isActiveList = isActiveList2;
        this.isDefaultList = isDefaultList2;
        this.listName = listName2;
        this.items = privacyItems;
    }

    public String getName() {
        return this.listName;
    }

    public boolean isActiveList() {
        return this.isActiveList;
    }

    public boolean isDefaultList() {
        return this.isDefaultList;
    }

    public List<PrivacyItem> getItems() {
        return this.items;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Privacy List: ");
        sb.append(this.listName);
        sb.append("(active:");
        sb.append(this.isActiveList);
        sb.append(", default:");
        sb.append(this.isDefaultList);
        sb.append(")");
        return sb.toString();
    }
}
