package org.minidns.dnslabel;

public final class NonReservedLdhLabel extends LdhLabel {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    protected NonReservedLdhLabel(String label) {
        super(label);
    }

    public static boolean isNonReservedLdhLabel(String label) {
        if (!isLdhLabel(label)) {
            return false;
        }
        return isNonReservedLdhLabelInternal(label);
    }

    static boolean isNonReservedLdhLabelInternal(String label) {
        return !ReservedLdhLabel.isReservedLdhLabelInternal(label);
    }
}
