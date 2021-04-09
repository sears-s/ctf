package org.minidns.record;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.bouncycastle.asn1.cmc.BodyPartID;
import org.minidns.constants.DnssecConstants.SignatureAlgorithm;
import org.minidns.dnsname.DnsName;
import org.minidns.record.Record.TYPE;
import org.minidns.util.Base64;

public class RRSIG extends Data {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public final SignatureAlgorithm algorithm;
    public final byte algorithmByte;
    private transient String base64SignatureCache;
    public final int keyTag;
    public final byte labels;
    public final long originalTtl;
    private final byte[] signature;
    public final Date signatureExpiration;
    public final Date signatureInception;
    public final DnsName signerName;
    public final TYPE typeCovered;

    public static RRSIG parse(DataInputStream dis, byte[] data, int length) throws IOException {
        TYPE typeCovered2 = TYPE.getType(dis.readUnsignedShort());
        byte algorithm2 = dis.readByte();
        byte labels2 = dis.readByte();
        long originalTtl2 = ((long) dis.readInt()) & BodyPartID.bodyIdMax;
        Date signatureExpiration2 = new Date((((long) dis.readInt()) & BodyPartID.bodyIdMax) * 1000);
        Date signatureInception2 = new Date((((long) dis.readInt()) & BodyPartID.bodyIdMax) * 1000);
        int keyTag2 = dis.readUnsignedShort();
        DnsName signerName2 = DnsName.parse(dis, data);
        int sigSize = (length - signerName2.size()) - 18;
        byte[] signature2 = new byte[sigSize];
        if (dis.read(signature2) == signature2.length) {
            int i = sigSize;
            RRSIG rrsig = new RRSIG(typeCovered2, null, algorithm2, labels2, originalTtl2, signatureExpiration2, signatureInception2, keyTag2, signerName2, signature2);
            return rrsig;
        }
        throw new IOException();
    }

    private RRSIG(TYPE typeCovered2, SignatureAlgorithm algorithm2, byte algorithmByte2, byte labels2, long originalTtl2, Date signatureExpiration2, Date signatureInception2, int keyTag2, DnsName signerName2, byte[] signature2) {
        this.typeCovered = typeCovered2;
        this.algorithmByte = algorithmByte2;
        this.algorithm = algorithm2 != null ? algorithm2 : SignatureAlgorithm.forByte(algorithmByte2);
        this.labels = labels2;
        this.originalTtl = originalTtl2;
        this.signatureExpiration = signatureExpiration2;
        this.signatureInception = signatureInception2;
        this.keyTag = keyTag2;
        this.signerName = signerName2;
        this.signature = signature2;
    }

    public RRSIG(TYPE typeCovered2, int algorithm2, byte labels2, long originalTtl2, Date signatureExpiration2, Date signatureInception2, int keyTag2, DnsName signerName2, byte[] signature2) {
        this(typeCovered2, null, (byte) algorithm2, labels2, originalTtl2, signatureExpiration2, signatureInception2, keyTag2, signerName2, signature2);
    }

    public RRSIG(TYPE typeCovered2, int algorithm2, byte labels2, long originalTtl2, Date signatureExpiration2, Date signatureInception2, int keyTag2, String signerName2, byte[] signature2) {
        this(typeCovered2, null, (byte) algorithm2, labels2, originalTtl2, signatureExpiration2, signatureInception2, keyTag2, DnsName.from(signerName2), signature2);
    }

    public RRSIG(TYPE typeCovered2, SignatureAlgorithm algorithm2, byte labels2, long originalTtl2, Date signatureExpiration2, Date signatureInception2, int keyTag2, DnsName signerName2, byte[] signature2) {
        this(typeCovered2, (int) algorithm2.number, labels2, originalTtl2, signatureExpiration2, signatureInception2, keyTag2, signerName2, signature2);
    }

    public RRSIG(TYPE typeCovered2, SignatureAlgorithm algorithm2, byte labels2, long originalTtl2, Date signatureExpiration2, Date signatureInception2, int keyTag2, String signerName2, byte[] signature2) {
        this(typeCovered2, (int) algorithm2.number, labels2, originalTtl2, signatureExpiration2, signatureInception2, keyTag2, DnsName.from(signerName2), signature2);
    }

    public byte[] getSignature() {
        return (byte[]) this.signature.clone();
    }

    public DataInputStream getSignatureAsDataInputStream() {
        return new DataInputStream(new ByteArrayInputStream(this.signature));
    }

    public int getSignatureLength() {
        return this.signature.length;
    }

    public String getSignatureBase64() {
        if (this.base64SignatureCache == null) {
            this.base64SignatureCache = Base64.encodeToString(this.signature);
        }
        return this.base64SignatureCache;
    }

    public TYPE getType() {
        return TYPE.RRSIG;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        writePartialSignature(dos);
        dos.write(this.signature);
    }

    public void writePartialSignature(DataOutputStream dos) throws IOException {
        dos.writeShort(this.typeCovered.getValue());
        dos.writeByte(this.algorithmByte);
        dos.writeByte(this.labels);
        dos.writeInt((int) this.originalTtl);
        dos.writeInt((int) (this.signatureExpiration.getTime() / 1000));
        dos.writeInt((int) (this.signatureInception.getTime() / 1000));
        dos.writeShort(this.keyTag);
        this.signerName.writeToStream(dos);
    }

    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        StringBuilder sb = new StringBuilder();
        sb.append(this.typeCovered);
        sb.append(' ');
        sb.append(this.algorithm);
        sb.append(' ');
        sb.append(this.labels);
        sb.append(' ');
        sb.append(this.originalTtl);
        sb.append(' ');
        sb.append(dateFormat.format(this.signatureExpiration));
        sb.append(' ');
        sb.append(dateFormat.format(this.signatureInception));
        sb.append(' ');
        sb.append(this.keyTag);
        sb.append(' ');
        sb.append(this.signerName);
        sb.append(". ");
        return sb.append(getSignatureBase64()).toString();
    }
}
