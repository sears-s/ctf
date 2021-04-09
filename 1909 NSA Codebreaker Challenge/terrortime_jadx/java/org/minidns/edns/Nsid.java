package org.minidns.edns;

import org.minidns.edns.Edns.OptionCode;
import org.minidns.util.Hex;

public class Nsid extends EdnsOption {
    public static final Nsid REQUEST = new Nsid();

    private Nsid() {
        this(new byte[0]);
    }

    public Nsid(byte[] payload) {
        super(payload);
    }

    public OptionCode getOptionCode() {
        return OptionCode.NSID;
    }

    /* access modifiers changed from: protected */
    public CharSequence toStringInternal() {
        StringBuilder sb = new StringBuilder();
        sb.append(OptionCode.NSID);
        sb.append(": ");
        String res = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(res);
        sb2.append(new String(this.optionData));
        return sb2.toString();
    }

    /* access modifiers changed from: protected */
    public CharSequence asTerminalOutputInternal() {
        return Hex.from(this.optionData);
    }
}
