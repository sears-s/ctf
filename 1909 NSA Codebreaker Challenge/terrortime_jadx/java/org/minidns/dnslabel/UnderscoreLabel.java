package org.minidns.dnslabel;

public final class UnderscoreLabel extends NonLdhLabel {
    protected UnderscoreLabel(String label) {
        super(label);
    }

    protected static boolean isUnderscoreLabelInternal(String label) {
        return label.charAt(0) == '_';
    }
}
