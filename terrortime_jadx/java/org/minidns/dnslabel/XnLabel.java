package org.minidns.dnslabel;

import java.util.Locale;
import org.minidns.idna.MiniDnsIdna;

public abstract class XnLabel extends ReservedLdhLabel {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    protected XnLabel(String label) {
        super(label);
    }

    protected static LdhLabel fromInternal(String label) {
        if (label.equals(MiniDnsIdna.toUnicode(label))) {
            return new FakeALabel(label);
        }
        return new ALabel(label);
    }

    public static boolean isXnLabel(String label) {
        if (!isLdhLabel(label)) {
            return false;
        }
        return isXnLabelInternal(label);
    }

    static boolean isXnLabelInternal(String label) {
        return label.substring(0, 2).toLowerCase(Locale.US).equals("xn");
    }
}
