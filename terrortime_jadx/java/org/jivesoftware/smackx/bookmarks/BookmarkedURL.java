package org.jivesoftware.smackx.bookmarks;

public class BookmarkedURL implements SharedBookmark {
    private final String URL;
    private boolean isRss;
    private boolean isShared;
    private String name;

    protected BookmarkedURL(String URL2) {
        this.URL = URL2;
    }

    protected BookmarkedURL(String URL2, String name2, boolean isRss2) {
        this.URL = URL2;
        this.name = name2;
        this.isRss = isRss2;
    }

    public String getName() {
        return this.name;
    }

    /* access modifiers changed from: protected */
    public void setName(String name2) {
        this.name = name2;
    }

    public String getURL() {
        return this.URL;
    }

    /* access modifiers changed from: protected */
    public void setRss(boolean isRss2) {
        this.isRss = isRss2;
    }

    public boolean isRss() {
        return this.isRss;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BookmarkedURL)) {
            return false;
        }
        return ((BookmarkedURL) obj).getURL().equalsIgnoreCase(this.URL);
    }

    public int hashCode() {
        return getURL().hashCode();
    }

    /* access modifiers changed from: protected */
    public void setShared(boolean shared) {
        this.isShared = shared;
    }

    public boolean isShared() {
        return this.isShared;
    }
}
