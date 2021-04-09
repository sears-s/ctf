package org.minidns.dnslabel;

public class ReservedLdhLabel extends LdhLabel {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    protected ReservedLdhLabel(String label) {
        super(label);
    }

    public static boolean isReservedLdhLabel(String label) {
        if (!isLdhLabel(label)) {
            return false;
        }
        return isReservedLdhLabelInternal(label);
    }

    static boolean isReservedLdhLabelInternal(String label) {
        return label.length() >= 4 && label.charAt(2) == '-' && label.charAt(3) == '-';
    }
}
