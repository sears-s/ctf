package org.minidns.dnslabel;

public final class LeadingOrTrailingHyphenLabel extends NonLdhLabel {
    protected LeadingOrTrailingHyphenLabel(String label) {
        super(label);
    }

    protected static boolean isLeadingOrTrailingHypenLabelInternal(String label) {
        if (label.isEmpty()) {
            return false;
        }
        if (label.charAt(0) == '-' || label.charAt(label.length() - 1) == '-') {
            return true;
        }
        return false;
    }
}
