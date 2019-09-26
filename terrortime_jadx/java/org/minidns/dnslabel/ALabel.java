package org.minidns.dnslabel;

import org.minidns.idna.MiniDnsIdna;

public final class ALabel extends XnLabel {
    protected ALabel(String label) {
        super(label);
    }

    /* access modifiers changed from: protected */
    public String getInternationalizedRepresentationInternal() {
        return MiniDnsIdna.toUnicode(this.label);
    }
}
