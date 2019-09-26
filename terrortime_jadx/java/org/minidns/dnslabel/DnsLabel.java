package org.minidns.dnslabel;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public abstract class DnsLabel implements CharSequence, Comparable<DnsLabel> {
    public static final int MAX_LABEL_LENGTH_IN_OCTETS = 63;
    public static boolean VALIDATE = true;
    public static final DnsLabel WILDCARD_LABEL = from("*");
    private transient byte[] byteCache;
    private transient String internationalizedRepresentation;
    public final String label;
    private transient DnsLabel lowercasedVariant;

    public static class LabelToLongException extends IllegalArgumentException {
        private static final long serialVersionUID = 1;
        public final String label;

        LabelToLongException(String label2) {
            this.label = label2;
        }
    }

    protected DnsLabel(String label2) {
        this.label = label2;
        if (VALIDATE) {
            setBytesIfRequired();
            if (this.byteCache.length > 63) {
                throw new LabelToLongException(label2);
            }
        }
    }

    public final String getInternationalizedRepresentation() {
        if (this.internationalizedRepresentation == null) {
            this.internationalizedRepresentation = getInternationalizedRepresentationInternal();
        }
        return this.internationalizedRepresentation;
    }

    /* access modifiers changed from: protected */
    public String getInternationalizedRepresentationInternal() {
        return this.label;
    }

    public final String getLabelType() {
        return getClass().getSimpleName();
    }

    public final int length() {
        return this.label.length();
    }

    public final char charAt(int index) {
        return this.label.charAt(index);
    }

    public final CharSequence subSequence(int start, int end) {
        return this.label.subSequence(start, end);
    }

    public final String toString() {
        return this.label;
    }

    public final boolean equals(Object other) {
        if (!(other instanceof DnsLabel)) {
            return false;
        }
        return this.label.equals(((DnsLabel) other).label);
    }

    public final int hashCode() {
        return this.label.hashCode();
    }

    public final DnsLabel asLowercaseVariant() {
        if (this.lowercasedVariant == null) {
            this.lowercasedVariant = from(this.label.toLowerCase(Locale.US));
        }
        return this.lowercasedVariant;
    }

    private void setBytesIfRequired() {
        if (this.byteCache == null) {
            this.byteCache = this.label.getBytes();
        }
    }

    public final void writeToBoas(ByteArrayOutputStream byteArrayOutputStream) {
        setBytesIfRequired();
        byteArrayOutputStream.write(this.byteCache.length);
        byte[] bArr = this.byteCache;
        byteArrayOutputStream.write(bArr, 0, bArr.length);
    }

    public final int compareTo(DnsLabel other) {
        return asLowercaseVariant().label.compareTo(other.asLowercaseVariant().label);
    }

    public static DnsLabel from(String label2) {
        if (label2 == null || label2.isEmpty()) {
            throw new IllegalArgumentException("Label is null or empty");
        } else if (LdhLabel.isLdhLabel(label2)) {
            return LdhLabel.fromInternal(label2);
        } else {
            return NonLdhLabel.fromInternal(label2);
        }
    }

    public static DnsLabel[] from(String[] labels) {
        DnsLabel[] res = new DnsLabel[labels.length];
        for (int i = 0; i < labels.length; i++) {
            res[i] = from(labels[i]);
        }
        return res;
    }

    public static boolean isIdnAcePrefixed(String string) {
        return string.toLowerCase(Locale.US).startsWith("xn--");
    }
}
