package org.minidns.dnslabel;

public abstract class LdhLabel extends DnsLabel {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    protected LdhLabel(String label) {
        super(label);
    }

    public static boolean isLdhLabel(String label) {
        if (label.isEmpty() || LeadingOrTrailingHyphenLabel.isLeadingOrTrailingHypenLabelInternal(label)) {
            return false;
        }
        for (int i = 0; i < label.length(); i++) {
            char c = label.charAt(i);
            if ((c < 'a' || c > 'z') && ((c < 'A' || c > 'Z') && ((c < '0' || c > '9') && c != '-'))) {
                return false;
            }
        }
        return true;
    }

    protected static LdhLabel fromInternal(String label) {
        if (!ReservedLdhLabel.isReservedLdhLabel(label)) {
            return new NonReservedLdhLabel(label);
        }
        if (XnLabel.isXnLabelInternal(label)) {
            return XnLabel.fromInternal(label);
        }
        return new ReservedLdhLabel(label);
    }
}
