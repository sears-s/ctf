package org.minidns.edns;

import java.io.DataOutputStream;
import java.io.IOException;
import org.minidns.edns.Edns.OptionCode;

public abstract class EdnsOption {
    public final int optionCode;
    protected final byte[] optionData;
    public final int optionLength;
    private String terminalOutputCache;
    private String toStringCache;

    /* renamed from: org.minidns.edns.EdnsOption$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$minidns$edns$Edns$OptionCode = new int[OptionCode.values().length];

        static {
            try {
                $SwitchMap$org$minidns$edns$Edns$OptionCode[OptionCode.NSID.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    /* access modifiers changed from: protected */
    public abstract CharSequence asTerminalOutputInternal();

    public abstract OptionCode getOptionCode();

    /* access modifiers changed from: protected */
    public abstract CharSequence toStringInternal();

    protected EdnsOption(int optionCode2, byte[] optionData2) {
        this.optionCode = optionCode2;
        this.optionLength = optionData2.length;
        this.optionData = optionData2;
    }

    protected EdnsOption(byte[] optionData2) {
        this.optionCode = getOptionCode().asInt;
        this.optionLength = optionData2.length;
        this.optionData = optionData2;
    }

    public final void writeToDos(DataOutputStream dos) throws IOException {
        dos.writeShort(this.optionCode);
        dos.writeShort(this.optionLength);
        dos.write(this.optionData);
    }

    public final String toString() {
        if (this.toStringCache == null) {
            this.toStringCache = toStringInternal().toString();
        }
        return this.toStringCache;
    }

    public final String asTerminalOutput() {
        if (this.terminalOutputCache == null) {
            this.terminalOutputCache = asTerminalOutputInternal().toString();
        }
        return this.terminalOutputCache;
    }

    public static EdnsOption parse(int intOptionCode, byte[] optionData2) {
        if (AnonymousClass1.$SwitchMap$org$minidns$edns$Edns$OptionCode[OptionCode.from(intOptionCode).ordinal()] != 1) {
            return new UnknownEdnsOption(intOptionCode, optionData2);
        }
        return new Nsid(optionData2);
    }
}
