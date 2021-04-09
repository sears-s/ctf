package org.minidns.dnsname;

import com.badguy.terrortime.BuildConfig;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import org.minidns.dnslabel.DnsLabel;
import org.minidns.dnslabel.DnsLabel.LabelToLongException;
import org.minidns.dnsname.InvalidDnsNameException.DNSNameTooLongException;
import org.minidns.dnsname.InvalidDnsNameException.LabelTooLongException;
import org.minidns.idna.MiniDnsIdna;

public class DnsName implements CharSequence, Serializable, Comparable<DnsName> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final DnsName IN_ADDR_ARPA = new DnsName("in-addr.arpa");
    public static final DnsName IP6_ARPA = new DnsName("ip6.arpa");
    private static final String LABEL_SEP_REGEX = "[.。．｡]";
    static final int MAX_DNSNAME_LENGTH_IN_OCTETS = 255;
    public static final int MAX_LABELS = 128;
    public static final DnsName ROOT = new DnsName(".");
    public static boolean VALIDATE = true;
    private static final long serialVersionUID = 1;
    public final String ace;
    private transient byte[] bytes;
    private transient String domainpart;
    private transient int hashCode;
    private transient String hostpart;
    private transient String idn;
    private transient DnsLabel[] labels;
    private final String rawAce;
    private transient byte[] rawBytes;
    private transient DnsLabel[] rawLabels;
    private int size;

    private DnsName(String name) {
        this(name, true);
    }

    private DnsName(String name, boolean inAce) {
        this.size = -1;
        if (name.isEmpty()) {
            this.rawAce = ROOT.rawAce;
        } else {
            int nameLength = name.length();
            int nameLastPos = nameLength - 1;
            if (nameLength >= 2 && name.charAt(nameLastPos) == '.') {
                name = name.subSequence(0, nameLastPos).toString();
            }
            if (inAce) {
                this.rawAce = name;
            } else {
                this.rawAce = MiniDnsIdna.toASCII(name);
            }
        }
        this.ace = this.rawAce.toLowerCase(Locale.US);
        if (VALIDATE) {
            validateMaxDnsnameLengthInOctets();
        }
    }

    private DnsName(DnsLabel[] rawLabels2, boolean validateMaxDnsnameLength) {
        this.size = -1;
        this.rawLabels = rawLabels2;
        this.labels = new DnsLabel[rawLabels2.length];
        int size2 = 0;
        for (int i = 0; i < rawLabels2.length; i++) {
            size2 += rawLabels2[i].length() + 1;
            this.labels[i] = rawLabels2[i].asLowercaseVariant();
        }
        this.rawAce = labelsToString(rawLabels2, size2);
        this.ace = labelsToString(this.labels, size2);
        if (validateMaxDnsnameLength && VALIDATE) {
            validateMaxDnsnameLengthInOctets();
        }
    }

    private static String labelsToString(DnsLabel[] labels2, int stringLength) {
        StringBuilder sb = new StringBuilder(stringLength);
        for (int i = labels2.length - 1; i >= 0; i--) {
            sb.append(labels2[i]);
            sb.append('.');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    private void validateMaxDnsnameLengthInOctets() {
        setBytesIfRequired();
        byte[] bArr = this.bytes;
        if (bArr.length > 255) {
            throw new DNSNameTooLongException(this.ace, bArr);
        }
    }

    public void writeToStream(OutputStream os) throws IOException {
        setBytesIfRequired();
        os.write(this.bytes);
    }

    public byte[] getBytes() {
        setBytesIfRequired();
        return (byte[]) this.bytes.clone();
    }

    public byte[] getRawBytes() {
        if (this.rawBytes == null) {
            setLabelsIfRequired();
            this.rawBytes = toBytes(this.rawLabels);
        }
        return (byte[]) this.rawBytes.clone();
    }

    private void setBytesIfRequired() {
        if (this.bytes == null) {
            setLabelsIfRequired();
            this.bytes = toBytes(this.labels);
        }
    }

    private static byte[] toBytes(DnsLabel[] labels2) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(64);
        for (int i = labels2.length - 1; i >= 0; i--) {
            labels2[i].writeToBoas(baos);
        }
        baos.write(0);
        return baos.toByteArray();
    }

    private void setLabelsIfRequired() {
        if (this.labels != null && this.rawLabels != null) {
            return;
        }
        if (isRootLabel()) {
            DnsLabel[] dnsLabelArr = new DnsLabel[0];
            this.labels = dnsLabelArr;
            this.rawLabels = dnsLabelArr;
            return;
        }
        this.labels = getLabels(this.ace);
        this.rawLabels = getLabels(this.rawAce);
    }

    private static DnsLabel[] getLabels(String ace2) {
        String[] labels2 = ace2.split(LABEL_SEP_REGEX, 128);
        for (int i = 0; i < labels2.length / 2; i++) {
            String t = labels2[i];
            int j = (labels2.length - i) - 1;
            labels2[i] = labels2[j];
            labels2[j] = t;
        }
        try {
            return DnsLabel.from(labels2);
        } catch (LabelToLongException e) {
            throw new LabelTooLongException(ace2, e.label);
        }
    }

    public String getRawAce() {
        return this.rawAce;
    }

    public String asIdn() {
        String str = this.idn;
        if (str != null) {
            return str;
        }
        this.idn = MiniDnsIdna.toUnicode(this.ace);
        return this.idn;
    }

    public String getDomainpart() {
        setHostnameAndDomainpartIfRequired();
        return this.domainpart;
    }

    public String getHostpart() {
        setHostnameAndDomainpartIfRequired();
        return this.hostpart;
    }

    public DnsLabel getHostpartLabel() {
        setLabelsIfRequired();
        DnsLabel[] dnsLabelArr = this.labels;
        return dnsLabelArr[dnsLabelArr.length];
    }

    private void setHostnameAndDomainpartIfRequired() {
        if (this.hostpart == null) {
            String[] parts = this.ace.split(LABEL_SEP_REGEX, 2);
            this.hostpart = parts[0];
            if (parts.length > 1) {
                this.domainpart = parts[1];
            } else {
                this.domainpart = BuildConfig.FLAVOR;
            }
        }
    }

    public int size() {
        if (this.size < 0) {
            if (isRootLabel()) {
                this.size = 1;
            } else {
                this.size = this.ace.length() + 2;
            }
        }
        return this.size;
    }

    public int length() {
        return this.ace.length();
    }

    public char charAt(int index) {
        return this.ace.charAt(index);
    }

    public CharSequence subSequence(int start, int end) {
        return this.ace.subSequence(start, end);
    }

    public String toString() {
        return this.ace;
    }

    public static DnsName from(CharSequence name) {
        return from(name.toString());
    }

    public static DnsName from(String name) {
        return new DnsName(name, false);
    }

    public static DnsName from(DnsName child, DnsName parent) {
        child.setLabelsIfRequired();
        parent.setLabelsIfRequired();
        int length = child.rawLabels.length;
        DnsLabel[] dnsLabelArr = parent.rawLabels;
        DnsLabel[] rawLabels2 = new DnsLabel[(length + dnsLabelArr.length)];
        System.arraycopy(dnsLabelArr, 0, rawLabels2, 0, dnsLabelArr.length);
        DnsLabel[] dnsLabelArr2 = child.rawLabels;
        System.arraycopy(dnsLabelArr2, 0, rawLabels2, parent.rawLabels.length, dnsLabelArr2.length);
        return new DnsName(rawLabels2, true);
    }

    public static DnsName from(DnsLabel child, DnsName parent) {
        parent.setLabelsIfRequired();
        DnsLabel[] dnsLabelArr = parent.rawLabels;
        DnsLabel[] rawLabels2 = new DnsLabel[(dnsLabelArr.length + 1)];
        System.arraycopy(dnsLabelArr, 0, rawLabels2, 0, dnsLabelArr.length);
        rawLabels2[rawLabels2.length] = child;
        return new DnsName(rawLabels2, true);
    }

    public static DnsName from(DnsLabel grandchild, DnsLabel child, DnsName parent) {
        parent.setBytesIfRequired();
        DnsLabel[] dnsLabelArr = parent.rawLabels;
        DnsLabel[] rawLabels2 = new DnsLabel[(dnsLabelArr.length + 2)];
        System.arraycopy(dnsLabelArr, 0, rawLabels2, 0, dnsLabelArr.length);
        DnsLabel[] dnsLabelArr2 = parent.rawLabels;
        rawLabels2[dnsLabelArr2.length] = child;
        rawLabels2[dnsLabelArr2.length + 1] = grandchild;
        return new DnsName(rawLabels2, true);
    }

    public static DnsName from(DnsName... nameComponents) {
        int labelCount = 0;
        for (DnsName component : nameComponents) {
            component.setLabelsIfRequired();
            labelCount += component.rawLabels.length;
        }
        DnsLabel[] rawLabels2 = new DnsLabel[labelCount];
        int destLabelPos = 0;
        for (int i = nameComponents.length - 1; i >= 0; i--) {
            DnsName component2 = nameComponents[i];
            DnsLabel[] dnsLabelArr = component2.rawLabels;
            System.arraycopy(dnsLabelArr, 0, rawLabels2, destLabelPos, dnsLabelArr.length);
            destLabelPos += component2.rawLabels.length;
        }
        return new DnsName(rawLabels2, true);
    }

    public static DnsName from(String[] parts) {
        return new DnsName(DnsLabel.from(parts), true);
    }

    public static DnsName parse(DataInputStream dis, byte[] data) throws IOException {
        int c = dis.readUnsignedByte();
        if ((c & 192) == 192) {
            int c2 = ((c & 63) << 8) + dis.readUnsignedByte();
            HashSet<Integer> jumps = new HashSet<>();
            jumps.add(Integer.valueOf(c2));
            return parse(data, c2, jumps);
        } else if (c == 0) {
            return ROOT;
        } else {
            byte[] b = new byte[c];
            dis.readFully(b);
            return from(new DnsName(new String(b)), parse(dis, data));
        }
    }

    private static DnsName parse(byte[] data, int offset, HashSet<Integer> jumps) throws IllegalStateException {
        int c = data[offset] & 255;
        if ((c & 192) == 192) {
            int c2 = ((c & 63) << 8) + (data[offset + 1] & 255);
            if (!jumps.contains(Integer.valueOf(c2))) {
                jumps.add(Integer.valueOf(c2));
                return parse(data, c2, jumps);
            }
            throw new IllegalStateException("Cyclic offsets detected.");
        } else if (c == 0) {
            return ROOT;
        } else {
            return from(new DnsName(new String(data, offset + 1, c)), parse(data, offset + 1 + c, jumps));
        }
    }

    public int compareTo(DnsName other) {
        return this.ace.compareTo(other.ace);
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof DnsName)) {
            return false;
        }
        DnsName otherDnsName = (DnsName) other;
        setBytesIfRequired();
        otherDnsName.setBytesIfRequired();
        return Arrays.equals(this.bytes, otherDnsName.bytes);
    }

    public int hashCode() {
        if (this.hashCode == 0 && !isRootLabel()) {
            setBytesIfRequired();
            this.hashCode = Arrays.hashCode(this.bytes);
        }
        return this.hashCode;
    }

    public boolean isDirectChildOf(DnsName parent) {
        setLabelsIfRequired();
        parent.setLabelsIfRequired();
        if (this.labels.length - 1 != parent.labels.length) {
            return false;
        }
        int i = 0;
        while (true) {
            DnsLabel[] dnsLabelArr = parent.labels;
            if (i >= dnsLabelArr.length) {
                return true;
            }
            if (!this.labels[i].equals(dnsLabelArr[i])) {
                return false;
            }
            i++;
        }
    }

    public boolean isChildOf(DnsName parent) {
        setLabelsIfRequired();
        parent.setLabelsIfRequired();
        if (this.labels.length < parent.labels.length) {
            return false;
        }
        int i = 0;
        while (true) {
            DnsLabel[] dnsLabelArr = parent.labels;
            if (i >= dnsLabelArr.length) {
                return true;
            }
            if (!this.labels[i].equals(dnsLabelArr[i])) {
                return false;
            }
            i++;
        }
    }

    public int getLabelCount() {
        setLabelsIfRequired();
        return this.labels.length;
    }

    public DnsLabel[] getLabels() {
        setLabelsIfRequired();
        return (DnsLabel[]) this.labels.clone();
    }

    public DnsLabel getLabel(int labelNum) {
        setLabelsIfRequired();
        return this.labels[labelNum];
    }

    public DnsLabel[] getRawLabels() {
        setLabelsIfRequired();
        return (DnsLabel[]) this.rawLabels.clone();
    }

    public DnsName stripToLabels(int labelCount) {
        setLabelsIfRequired();
        DnsLabel[] dnsLabelArr = this.labels;
        if (labelCount > dnsLabelArr.length) {
            throw new IllegalArgumentException();
        } else if (labelCount == dnsLabelArr.length) {
            return this;
        } else {
            if (labelCount == 0) {
                return ROOT;
            }
            return new DnsName((DnsLabel[]) Arrays.copyOfRange(this.rawLabels, 0, labelCount), false);
        }
    }

    public DnsName getParent() {
        if (isRootLabel()) {
            return ROOT;
        }
        return stripToLabels(getLabelCount() - 1);
    }

    public boolean isRootLabel() {
        return this.ace.isEmpty() || this.ace.equals(".");
    }
}
