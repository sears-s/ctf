package org.jivesoftware.smack.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.StringUtils;

public abstract class AbstractListFilter implements StanzaFilter {
    protected final List<StanzaFilter> filters;

    protected AbstractListFilter() {
        this.filters = new ArrayList();
    }

    protected AbstractListFilter(StanzaFilter... filters2) {
        String str = "Parameter must not be null.";
        Objects.requireNonNull(filters2, str);
        for (StanzaFilter filter : filters2) {
            Objects.requireNonNull(filter, str);
        }
        this.filters = new ArrayList(Arrays.asList(filters2));
    }

    public void addFilter(StanzaFilter filter) {
        Objects.requireNonNull(filter, "Parameter must not be null.");
        this.filters.add(filter);
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(": (");
        sb.append(StringUtils.toStringBuilder(this.filters, ", "));
        sb.append(')');
        return sb.toString();
    }
}
