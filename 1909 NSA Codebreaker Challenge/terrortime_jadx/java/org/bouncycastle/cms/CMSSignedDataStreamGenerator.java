package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CMSSignedDataStreamGenerator extends CMSSignedGenerator {
    private int _bufferSize;

    private class CmsSignedDataOutputStream extends OutputStream {
        private ASN1ObjectIdentifier _contentOID;
        private BERSequenceGenerator _eiGen;
        private OutputStream _out;
        private BERSequenceGenerator _sGen;
        private BERSequenceGenerator _sigGen;

        public CmsSignedDataOutputStream(OutputStream outputStream, ASN1ObjectIdentifier aSN1ObjectIdentifier, BERSequenceGenerator bERSequenceGenerator, BERSequenceGenerator bERSequenceGenerator2, BERSequenceGenerator bERSequenceGenerator3) {
            this._out = outputStream;
            this._contentOID = aSN1ObjectIdentifier;
            this._sGen = bERSequenceGenerator;
            this._sigGen = bERSequenceGenerator2;
            this._eiGen = bERSequenceGenerator3;
        }

        public void close() throws IOException {
            this._out.close();
            this._eiGen.close();
            CMSSignedDataStreamGenerator.this.digests.clear();
            if (CMSSignedDataStreamGenerator.this.certs.size() != 0) {
                this._sigGen.getRawOutputStream().write(new BERTaggedObject(false, 0, CMSUtils.createBerSetFromList(CMSSignedDataStreamGenerator.this.certs)).getEncoded());
            }
            if (CMSSignedDataStreamGenerator.this.crls.size() != 0) {
                this._sigGen.getRawOutputStream().write(new BERTaggedObject(false, 1, CMSUtils.createBerSetFromList(CMSSignedDataStreamGenerator.this.crls)).getEncoded());
            }
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            for (SignerInfoGenerator signerInfoGenerator : CMSSignedDataStreamGenerator.this.signerGens) {
                try {
                    aSN1EncodableVector.add(signerInfoGenerator.generate(this._contentOID));
                    CMSSignedDataStreamGenerator.this.digests.put(signerInfoGenerator.getDigestAlgorithm().getAlgorithm().getId(), signerInfoGenerator.getCalculatedDigest());
                } catch (CMSException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("exception generating signers: ");
                    sb.append(e.getMessage());
                    throw new CMSStreamException(sb.toString(), e);
                }
            }
            for (SignerInformation aSN1Structure : CMSSignedDataStreamGenerator.this._signers) {
                aSN1EncodableVector.add(aSN1Structure.toASN1Structure());
            }
            this._sigGen.getRawOutputStream().write(new DERSet(aSN1EncodableVector).getEncoded());
            this._sigGen.close();
            this._sGen.close();
        }

        public void write(int i) throws IOException {
            this._out.write(i);
        }

        public void write(byte[] bArr) throws IOException {
            this._out.write(bArr);
        }

        public void write(byte[] bArr, int i, int i2) throws IOException {
            this._out.write(bArr, i, i2);
        }
    }

    private ASN1Integer calculateVersion(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4 = false;
        if (this.certs != null) {
            z3 = false;
            z2 = false;
            z = false;
            for (Object next : this.certs) {
                if (next instanceof ASN1TaggedObject) {
                    ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) next;
                    if (aSN1TaggedObject.getTagNo() == 1) {
                        z2 = true;
                    } else if (aSN1TaggedObject.getTagNo() == 2) {
                        z = true;
                    } else if (aSN1TaggedObject.getTagNo() == 3) {
                        z3 = true;
                    }
                }
            }
        } else {
            z3 = false;
            z2 = false;
            z = false;
        }
        if (z3) {
            return new ASN1Integer(5);
        }
        if (this.crls != null) {
            for (Object obj : this.crls) {
                if (obj instanceof ASN1TaggedObject) {
                    z4 = true;
                }
            }
        }
        return z4 ? new ASN1Integer(5) : z ? new ASN1Integer(4) : z2 ? new ASN1Integer(3) : checkForVersion3(this._signers, this.signerGens) ? new ASN1Integer(3) : !CMSObjectIdentifiers.data.equals(aSN1ObjectIdentifier) ? new ASN1Integer(3) : new ASN1Integer(1);
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.List, code=java.util.List<org.bouncycastle.cms.SignerInfoGenerator>, for r5v0, types: [java.util.List, java.util.List<org.bouncycastle.cms.SignerInfoGenerator>] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.List, code=java.util.List<org.bouncycastle.cms.SignerInformation>, for r4v0, types: [java.util.List, java.util.List<org.bouncycastle.cms.SignerInformation>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkForVersion3(java.util.List<org.bouncycastle.cms.SignerInformation> r4, java.util.List<org.bouncycastle.cms.SignerInfoGenerator> r5) {
        /*
            r3 = this;
            java.util.Iterator r4 = r4.iterator()
        L_0x0004:
            boolean r0 = r4.hasNext()
            r1 = 1
            r2 = 3
            if (r0 == 0) goto L_0x0029
            java.lang.Object r0 = r4.next()
            org.bouncycastle.cms.SignerInformation r0 = (org.bouncycastle.cms.SignerInformation) r0
            org.bouncycastle.asn1.cms.SignerInfo r0 = r0.toASN1Structure()
            org.bouncycastle.asn1.cms.SignerInfo r0 = org.bouncycastle.asn1.cms.SignerInfo.getInstance(r0)
            org.bouncycastle.asn1.ASN1Integer r0 = r0.getVersion()
            java.math.BigInteger r0 = r0.getValue()
            int r0 = r0.intValue()
            if (r0 != r2) goto L_0x0004
            return r1
        L_0x0029:
            java.util.Iterator r4 = r5.iterator()
        L_0x002d:
            boolean r5 = r4.hasNext()
            if (r5 == 0) goto L_0x0040
            java.lang.Object r5 = r4.next()
            org.bouncycastle.cms.SignerInfoGenerator r5 = (org.bouncycastle.cms.SignerInfoGenerator) r5
            int r5 = r5.getGeneratedVersion()
            if (r5 != r2) goto L_0x002d
            return r1
        L_0x0040:
            r4 = 0
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.cms.CMSSignedDataStreamGenerator.checkForVersion3(java.util.List, java.util.List):boolean");
    }

    public List<AlgorithmIdentifier> getDigestAlgorithms() {
        ArrayList arrayList = new ArrayList();
        for (SignerInformation digestAlgorithmID : this._signers) {
            arrayList.add(CMSSignedHelper.INSTANCE.fixAlgID(digestAlgorithmID.getDigestAlgorithmID()));
        }
        for (SignerInfoGenerator digestAlgorithm : this.signerGens) {
            arrayList.add(digestAlgorithm.getDigestAlgorithm());
        }
        return arrayList;
    }

    public OutputStream open(OutputStream outputStream) throws IOException {
        return open(outputStream, false);
    }

    public OutputStream open(OutputStream outputStream, boolean z) throws IOException {
        return open(CMSObjectIdentifiers.data, outputStream, z);
    }

    public OutputStream open(OutputStream outputStream, boolean z, OutputStream outputStream2) throws IOException {
        return open(CMSObjectIdentifiers.data, outputStream, z, outputStream2);
    }

    public OutputStream open(ASN1ObjectIdentifier aSN1ObjectIdentifier, OutputStream outputStream, boolean z) throws IOException {
        return open(aSN1ObjectIdentifier, outputStream, z, null);
    }

    public OutputStream open(ASN1ObjectIdentifier aSN1ObjectIdentifier, OutputStream outputStream, boolean z, OutputStream outputStream2) throws IOException {
        BERSequenceGenerator bERSequenceGenerator = new BERSequenceGenerator(outputStream);
        bERSequenceGenerator.addObject(CMSObjectIdentifiers.signedData);
        BERSequenceGenerator bERSequenceGenerator2 = new BERSequenceGenerator(bERSequenceGenerator.getRawOutputStream(), 0, true);
        bERSequenceGenerator2.addObject(calculateVersion(aSN1ObjectIdentifier));
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (SignerInformation digestAlgorithmID : this._signers) {
            aSN1EncodableVector.add(CMSSignedHelper.INSTANCE.fixAlgID(digestAlgorithmID.getDigestAlgorithmID()));
        }
        for (SignerInfoGenerator digestAlgorithm : this.signerGens) {
            aSN1EncodableVector.add(digestAlgorithm.getDigestAlgorithm());
        }
        bERSequenceGenerator2.getRawOutputStream().write(new DERSet(aSN1EncodableVector).getEncoded());
        BERSequenceGenerator bERSequenceGenerator3 = new BERSequenceGenerator(bERSequenceGenerator2.getRawOutputStream());
        bERSequenceGenerator3.addObject(aSN1ObjectIdentifier);
        CmsSignedDataOutputStream cmsSignedDataOutputStream = new CmsSignedDataOutputStream(CMSUtils.attachSignersToOutputStream(this.signerGens, CMSUtils.getSafeTeeOutputStream(outputStream2, z ? CMSUtils.createBEROctetOutputStream(bERSequenceGenerator3.getRawOutputStream(), 0, true, this._bufferSize) : null)), aSN1ObjectIdentifier, bERSequenceGenerator, bERSequenceGenerator2, bERSequenceGenerator3);
        return cmsSignedDataOutputStream;
    }

    public void setBufferSize(int i) {
        this._bufferSize = i;
    }
}
