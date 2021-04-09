package org.minidns.record;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.minidns.record.Record.TYPE;

public class TLSA extends Data {
    /* access modifiers changed from: private */
    public static final Map<Byte, CertUsage> CERT_USAGE_LUT = new HashMap();
    /* access modifiers changed from: private */
    public static final Map<Byte, MatchingType> MATCHING_TYPE_LUT = new HashMap();
    /* access modifiers changed from: private */
    public static final Map<Byte, Selector> SELECTOR_LUT = new HashMap();
    public final CertUsage certUsage;
    public final byte certUsageByte;
    private final byte[] certificateAssociation;
    public final MatchingType matchingType;
    public final byte matchingTypeByte;
    public final Selector selector;
    public final byte selectorByte;

    public enum CertUsage {
        caConstraint(0),
        serviceCertificateConstraint(1),
        trustAnchorAssertion(2),
        domainIssuedCertificate(3);
        
        public final byte byteValue;

        private CertUsage(byte byteValue2) {
            this.byteValue = byteValue2;
            TLSA.CERT_USAGE_LUT.put(Byte.valueOf(byteValue2), this);
        }
    }

    public enum MatchingType {
        noHash(0),
        sha256(1),
        sha512(2);
        
        public final byte byteValue;

        private MatchingType(byte byteValue2) {
            this.byteValue = byteValue2;
            TLSA.MATCHING_TYPE_LUT.put(Byte.valueOf(byteValue2), this);
        }
    }

    public enum Selector {
        fullCertificate(0),
        subjectPublicKeyInfo(1);
        
        public final byte byteValue;

        private Selector(byte byteValue2) {
            this.byteValue = byteValue2;
            TLSA.SELECTOR_LUT.put(Byte.valueOf(byteValue2), this);
        }
    }

    static {
        CertUsage.values();
        Selector.values();
        MatchingType.values();
    }

    public static TLSA parse(DataInputStream dis, int length) throws IOException {
        byte certUsage2 = dis.readByte();
        byte selector2 = dis.readByte();
        byte matchingType2 = dis.readByte();
        byte[] certificateAssociation2 = new byte[(length - 3)];
        if (dis.read(certificateAssociation2) == certificateAssociation2.length) {
            return new TLSA(certUsage2, selector2, matchingType2, certificateAssociation2);
        }
        throw new IOException();
    }

    TLSA(byte certUsageByte2, byte selectorByte2, byte matchingTypeByte2, byte[] certificateAssociation2) {
        this.certUsageByte = certUsageByte2;
        this.certUsage = (CertUsage) CERT_USAGE_LUT.get(Byte.valueOf(certUsageByte2));
        this.selectorByte = selectorByte2;
        this.selector = (Selector) SELECTOR_LUT.get(Byte.valueOf(selectorByte2));
        this.matchingTypeByte = matchingTypeByte2;
        this.matchingType = (MatchingType) MATCHING_TYPE_LUT.get(Byte.valueOf(matchingTypeByte2));
        this.certificateAssociation = certificateAssociation2;
    }

    public TYPE getType() {
        return TYPE.TLSA;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeByte(this.certUsageByte);
        dos.writeByte(this.selectorByte);
        dos.writeByte(this.matchingTypeByte);
        dos.write(this.certificateAssociation);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.certUsageByte);
        sb.append(' ');
        sb.append(this.selectorByte);
        sb.append(' ');
        sb.append(this.matchingTypeByte);
        sb.append(' ');
        sb.append(new BigInteger(1, this.certificateAssociation).toString(16));
        return sb.toString();
    }

    public byte[] getCertificateAssociation() {
        return (byte[]) this.certificateAssociation.clone();
    }

    public boolean certificateAssociationEquals(byte[] otherCertificateAssociation) {
        return Arrays.equals(this.certificateAssociation, otherCertificateAssociation);
    }
}
