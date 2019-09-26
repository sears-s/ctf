package org.minidns.edns;

import org.minidns.edns.Edns.OptionCode;
import org.minidns.util.Hex;

public class UnknownEdnsOption extends EdnsOption {
    protected UnknownEdnsOption(int optionCode, byte[] optionData) {
        super(optionCode, optionData);
    }

    public OptionCode getOptionCode() {
        return OptionCode.UNKNOWN;
    }

    /* access modifiers changed from: protected */
    public CharSequence asTerminalOutputInternal() {
        return Hex.from(this.optionData);
    }

    /* access modifiers changed from: protected */
    public CharSequence toStringInternal() {
        return asTerminalOutputInternal();
    }
}
