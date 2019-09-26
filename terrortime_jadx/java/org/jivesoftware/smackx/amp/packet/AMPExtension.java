package org.jivesoftware.smackx.amp.packet;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jivesoftware.smack.packet.ExtensionElement;

public class AMPExtension implements ExtensionElement {
    public static final String ELEMENT = "amp";
    public static final String NAMESPACE = "http://jabber.org/protocol/amp";
    private final String from;
    private boolean perHop;
    private final CopyOnWriteArrayList<Rule> rules;
    private final Status status;
    private final String to;

    public enum Action {
        alert,
        drop,
        error,
        notify;
        
        public static final String ATTRIBUTE_NAME = "action";
    }

    public interface Condition {
        public static final String ATTRIBUTE_NAME = "condition";

        String getName();

        String getValue();
    }

    public static class Rule {
        public static final String ELEMENT = "rule";
        private final Action action;
        private final Condition condition;

        public Action getAction() {
            return this.action;
        }

        public Condition getCondition() {
            return this.condition;
        }

        public Rule(Action action2, Condition condition2) {
            if (action2 == null) {
                throw new NullPointerException("Can't create Rule with null action");
            } else if (condition2 != null) {
                this.action = action2;
                this.condition = condition2;
            } else {
                throw new NullPointerException("Can't create Rule with null condition");
            }
        }

        /* access modifiers changed from: private */
        public String toXML() {
            StringBuilder sb = new StringBuilder();
            sb.append("<rule action=\"");
            sb.append(this.action.toString());
            sb.append("\" ");
            sb.append(Condition.ATTRIBUTE_NAME);
            sb.append("=\"");
            sb.append(this.condition.getName());
            sb.append("\" value=\"");
            sb.append(this.condition.getValue());
            sb.append("\"/>");
            return sb.toString();
        }
    }

    public enum Status {
        alert,
        error,
        notify
    }

    public AMPExtension(String from2, String to2, Status status2) {
        this.rules = new CopyOnWriteArrayList<>();
        this.perHop = false;
        this.from = from2;
        this.to = to2;
        this.status = status2;
    }

    public AMPExtension() {
        this.rules = new CopyOnWriteArrayList<>();
        this.perHop = false;
        this.from = null;
        this.to = null;
        this.status = null;
    }

    public String getFrom() {
        return this.from;
    }

    public String getTo() {
        return this.to;
    }

    public Status getStatus() {
        return this.status;
    }

    public List<Rule> getRules() {
        return Collections.unmodifiableList(this.rules);
    }

    public void addRule(Rule rule) {
        this.rules.add(rule);
    }

    public int getRulesCount() {
        return this.rules.size();
    }

    public synchronized void setPerHop(boolean enabled) {
        this.perHop = enabled;
    }

    public synchronized boolean isPerHop() {
        return this.perHop;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public String toXML(String enclosingNamespace) {
        StringBuilder buf = new StringBuilder();
        buf.append('<');
        buf.append(getElementName());
        buf.append(" xmlns=\"");
        buf.append(getNamespace());
        buf.append('\"');
        if (this.status != null) {
            buf.append(" status=\"");
            buf.append(this.status.toString());
            buf.append('\"');
        }
        if (this.to != null) {
            buf.append(" to=\"");
            buf.append(this.to);
            buf.append('\"');
        }
        if (this.from != null) {
            buf.append(" from=\"");
            buf.append(this.from);
            buf.append('\"');
        }
        if (this.perHop) {
            buf.append(" per-hop=\"true\"");
        }
        buf.append('>');
        for (Rule rule : getRules()) {
            buf.append(rule.toXML());
        }
        buf.append("</");
        buf.append(getElementName());
        buf.append('>');
        return buf.toString();
    }
}
