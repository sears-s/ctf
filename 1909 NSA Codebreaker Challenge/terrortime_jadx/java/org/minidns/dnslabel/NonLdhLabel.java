package org.minidns.dnslabel;

public abstract class NonLdhLabel extends DnsLabel {
    protected NonLdhLabel(String label) {
        super(label);
    }

    protected static DnsLabel fromInternal(String label) {
        if (UnderscoreLabel.isUnderscoreLabelInternal(label)) {
            return new UnderscoreLabel(label);
        }
        if (LeadingOrTrailingHyphenLabel.isLeadingOrTrailingHypenLabelInternal(label)) {
            return new LeadingOrTrailingHyphenLabel(label);
        }
        return new OtherNonLdhLabel(label);
    }
}
