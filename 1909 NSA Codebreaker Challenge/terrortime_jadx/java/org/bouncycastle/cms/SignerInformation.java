package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAlgorithmProtection;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.RawContentVerifier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.TeeOutputStream;

public class SignerInformation {
    private final CMSProcessable content;
    private final ASN1ObjectIdentifier contentType;
    protected final AlgorithmIdentifier digestAlgorithm;
    protected final AlgorithmIdentifier encryptionAlgorithm;
    protected final SignerInfo info;
    private final boolean isCounterSignature;
    private byte[] resultDigest;
    private final SignerId sid;
    private final byte[] signature;
    protected final ASN1Set signedAttributeSet;
    private AttributeTable signedAttributeValues;
    protected final ASN1Set unsignedAttributeSet;
    private AttributeTable unsignedAttributeValues;

    SignerInformation(SignerInfo signerInfo, ASN1ObjectIdentifier aSN1ObjectIdentifier, CMSProcessable cMSProcessable, byte[] bArr) {
        SignerId signerId;
        this.info = signerInfo;
        this.contentType = aSN1ObjectIdentifier;
        this.isCounterSignature = aSN1ObjectIdentifier == null;
        SignerIdentifier sid2 = signerInfo.getSID();
        boolean isTagged = sid2.isTagged();
        ASN1Encodable id = sid2.getId();
        if (isTagged) {
            signerId = new SignerId(ASN1OctetString.getInstance(id).getOctets());
        } else {
            IssuerAndSerialNumber instance = IssuerAndSerialNumber.getInstance(id);
            signerId = new SignerId(instance.getName(), instance.getSerialNumber().getValue());
        }
        this.sid = signerId;
        this.digestAlgorithm = signerInfo.getDigestAlgorithm();
        this.signedAttributeSet = signerInfo.getAuthenticatedAttributes();
        this.unsignedAttributeSet = signerInfo.getUnauthenticatedAttributes();
        this.encryptionAlgorithm = signerInfo.getDigestEncryptionAlgorithm();
        this.signature = signerInfo.getEncryptedDigest().getOctets();
        this.content = cMSProcessable;
        this.resultDigest = bArr;
    }

    protected SignerInformation(SignerInformation signerInformation) {
        this.info = signerInformation.info;
        this.contentType = signerInformation.contentType;
        this.isCounterSignature = signerInformation.isCounterSignature();
        this.sid = signerInformation.getSID();
        this.digestAlgorithm = this.info.getDigestAlgorithm();
        this.signedAttributeSet = this.info.getAuthenticatedAttributes();
        this.unsignedAttributeSet = this.info.getUnauthenticatedAttributes();
        this.encryptionAlgorithm = this.info.getDigestEncryptionAlgorithm();
        this.signature = this.info.getEncryptedDigest().getOctets();
        this.content = signerInformation.content;
        this.resultDigest = signerInformation.resultDigest;
        this.signedAttributeValues = signerInformation.signedAttributeValues;
        this.unsignedAttributeValues = signerInformation.unsignedAttributeValues;
    }

    public static SignerInformation addCounterSigners(SignerInformation signerInformation, SignerInformationStore signerInformationStore) {
        SignerInfo signerInfo = signerInformation.info;
        AttributeTable unsignedAttributes = signerInformation.getUnsignedAttributes();
        ASN1EncodableVector aSN1EncodableVector = unsignedAttributes != null ? unsignedAttributes.toASN1EncodableVector() : new ASN1EncodableVector();
        ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
        for (SignerInformation aSN1Structure : signerInformationStore.getSigners()) {
            aSN1EncodableVector2.add(aSN1Structure.toASN1Structure());
        }
        aSN1EncodableVector.add(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector2)));
        SignerInfo signerInfo2 = new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), (ASN1Set) new DERSet(aSN1EncodableVector));
        return new SignerInformation(signerInfo2, signerInformation.contentType, signerInformation.content, null);
    }

    private boolean doVerify(SignerInformationVerifier signerInformationVerifier) throws CMSException {
        String str = "can't process mime object to create signature.";
        String encryptionAlgName = CMSSignedHelper.INSTANCE.getEncryptionAlgName(getEncryptionAlgOID());
        try {
            ContentVerifier contentVerifier = signerInformationVerifier.getContentVerifier(this.encryptionAlgorithm, this.info.getDigestAlgorithm());
            try {
                OutputStream outputStream = contentVerifier.getOutputStream();
                if (this.resultDigest == null) {
                    DigestCalculator digestCalculator = signerInformationVerifier.getDigestCalculator(getDigestAlgorithmID());
                    if (this.content != null) {
                        OutputStream outputStream2 = digestCalculator.getOutputStream();
                        if (this.signedAttributeSet != null) {
                            this.content.write(outputStream2);
                            outputStream.write(getEncodedSignedAttributes());
                        } else if (contentVerifier instanceof RawContentVerifier) {
                            this.content.write(outputStream2);
                        } else {
                            TeeOutputStream teeOutputStream = new TeeOutputStream(outputStream2, outputStream);
                            this.content.write(teeOutputStream);
                            teeOutputStream.close();
                        }
                        outputStream2.close();
                    } else if (this.signedAttributeSet != null) {
                        outputStream.write(getEncodedSignedAttributes());
                    } else {
                        throw new CMSException("data not encapsulated in signature - use detached constructor.");
                    }
                    this.resultDigest = digestCalculator.getDigest();
                } else if (this.signedAttributeSet != null) {
                    outputStream.write(getEncodedSignedAttributes());
                } else if (this.content != null) {
                    this.content.write(outputStream);
                }
                outputStream.close();
                ASN1Primitive singleValuedSignedAttribute = getSingleValuedSignedAttribute(CMSAttributes.contentType, "content-type");
                if (singleValuedSignedAttribute == null) {
                    if (!this.isCounterSignature && this.signedAttributeSet != null) {
                        throw new CMSException("The content-type attribute type MUST be present whenever signed attributes are present in signed-data");
                    }
                } else if (this.isCounterSignature) {
                    throw new CMSException("[For counter signatures,] the signedAttributes field MUST NOT contain a content-type attribute");
                } else if (!(singleValuedSignedAttribute instanceof ASN1ObjectIdentifier)) {
                    throw new CMSException("content-type attribute value not of ASN.1 type 'OBJECT IDENTIFIER'");
                } else if (!((ASN1ObjectIdentifier) singleValuedSignedAttribute).equals(this.contentType)) {
                    throw new CMSException("content-type attribute value does not match eContentType");
                }
                AttributeTable signedAttributes = getSignedAttributes();
                AttributeTable unsignedAttributes = getUnsignedAttributes();
                if (unsignedAttributes == null || unsignedAttributes.getAll(CMSAttributes.cmsAlgorithmProtect).size() <= 0) {
                    int i = 0;
                    if (signedAttributes != null) {
                        ASN1EncodableVector all = signedAttributes.getAll(CMSAttributes.cmsAlgorithmProtect);
                        if (all.size() > 1) {
                            throw new CMSException("Only one instance of a cmsAlgorithmProtect attribute can be present");
                        } else if (all.size() > 0) {
                            Attribute instance = Attribute.getInstance(all.get(0));
                            if (instance.getAttrValues().size() == 1) {
                                CMSAlgorithmProtection instance2 = CMSAlgorithmProtection.getInstance(instance.getAttributeValues()[0]);
                                if (!CMSUtils.isEquivalent(instance2.getDigestAlgorithm(), this.info.getDigestAlgorithm())) {
                                    throw new CMSException("CMS Algorithm Identifier Protection check failed for digestAlgorithm");
                                } else if (!CMSUtils.isEquivalent(instance2.getSignatureAlgorithm(), this.info.getDigestEncryptionAlgorithm())) {
                                    throw new CMSException("CMS Algorithm Identifier Protection check failed for signatureAlgorithm");
                                }
                            } else {
                                throw new CMSException("A cmsAlgorithmProtect attribute MUST contain exactly one value");
                            }
                        }
                    }
                    ASN1Primitive singleValuedSignedAttribute2 = getSingleValuedSignedAttribute(CMSAttributes.messageDigest, "message-digest");
                    if (singleValuedSignedAttribute2 == null) {
                        if (this.signedAttributeSet != null) {
                            throw new CMSException("the message-digest signed attribute type MUST be present when there are any signed attributes present");
                        }
                    } else if (singleValuedSignedAttribute2 instanceof ASN1OctetString) {
                        if (!Arrays.constantTimeAreEqual(this.resultDigest, ((ASN1OctetString) singleValuedSignedAttribute2).getOctets())) {
                            throw new CMSSignerDigestMismatchException("message-digest attribute value does not match calculated value");
                        }
                    } else {
                        throw new CMSException("message-digest attribute value not of ASN.1 type 'OCTET STRING'");
                    }
                    if (signedAttributes == null || signedAttributes.getAll(CMSAttributes.counterSignature).size() <= 0) {
                        AttributeTable unsignedAttributes2 = getUnsignedAttributes();
                        if (unsignedAttributes2 != null) {
                            ASN1EncodableVector all2 = unsignedAttributes2.getAll(CMSAttributes.counterSignature);
                            while (i < all2.size()) {
                                if (Attribute.getInstance(all2.get(i)).getAttrValues().size() >= 1) {
                                    i++;
                                } else {
                                    throw new CMSException("A countersignature attribute MUST contain at least one AttributeValue");
                                }
                            }
                        }
                        try {
                            if (this.signedAttributeSet != null || this.resultDigest == null || !(contentVerifier instanceof RawContentVerifier)) {
                                return contentVerifier.verify(getSignature());
                            }
                            RawContentVerifier rawContentVerifier = (RawContentVerifier) contentVerifier;
                            return encryptionAlgName.equals("RSA") ? rawContentVerifier.verify(new DigestInfo(new AlgorithmIdentifier(this.digestAlgorithm.getAlgorithm(), DERNull.INSTANCE), this.resultDigest).getEncoded(ASN1Encoding.DER), getSignature()) : rawContentVerifier.verify(this.resultDigest, getSignature());
                        } catch (IOException e) {
                            throw new CMSException(str, e);
                        }
                    } else {
                        throw new CMSException("A countersignature attribute MUST NOT be a signed attribute");
                    }
                } else {
                    throw new CMSException("A cmsAlgorithmProtect attribute MUST be a signed attribute");
                }
            } catch (IOException e2) {
                throw new CMSException(str, e2);
            } catch (OperatorCreationException e3) {
                StringBuilder sb = new StringBuilder();
                sb.append("can't create digest calculator: ");
                sb.append(e3.getMessage());
                throw new CMSException(sb.toString(), e3);
            }
        } catch (OperatorCreationException e4) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("can't create content verifier: ");
            sb2.append(e4.getMessage());
            throw new CMSException(sb2.toString(), e4);
        }
    }

    private byte[] encodeObj(ASN1Encodable aSN1Encodable) throws IOException {
        if (aSN1Encodable != null) {
            return aSN1Encodable.toASN1Primitive().getEncoded();
        }
        return null;
    }

    private Time getSigningTime() throws CMSException {
        ASN1Primitive singleValuedSignedAttribute = getSingleValuedSignedAttribute(CMSAttributes.signingTime, "signing-time");
        if (singleValuedSignedAttribute == null) {
            return null;
        }
        try {
            return Time.getInstance(singleValuedSignedAttribute);
        } catch (IllegalArgumentException e) {
            throw new CMSException("signing-time attribute value not a valid 'Time' structure");
        }
    }

    private ASN1Primitive getSingleValuedSignedAttribute(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str) throws CMSException {
        AttributeTable unsignedAttributes = getUnsignedAttributes();
        if (unsignedAttributes == null || unsignedAttributes.getAll(aSN1ObjectIdentifier).size() <= 0) {
            AttributeTable signedAttributes = getSignedAttributes();
            if (signedAttributes == null) {
                return null;
            }
            ASN1EncodableVector all = signedAttributes.getAll(aSN1ObjectIdentifier);
            int size = all.size();
            if (size == 0) {
                return null;
            }
            if (size == 1) {
                ASN1Set attrValues = ((Attribute) all.get(0)).getAttrValues();
                if (attrValues.size() == 1) {
                    return attrValues.getObjectAt(0).toASN1Primitive();
                }
                StringBuilder sb = new StringBuilder();
                sb.append("A ");
                sb.append(str);
                sb.append(" attribute MUST have a single attribute value");
                throw new CMSException(sb.toString());
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("The SignedAttributes in a signerInfo MUST NOT include multiple instances of the ");
            sb2.append(str);
            sb2.append(" attribute");
            throw new CMSException(sb2.toString());
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append("The ");
        sb3.append(str);
        sb3.append(" attribute MUST NOT be an unsigned attribute");
        throw new CMSException(sb3.toString());
    }

    public static SignerInformation replaceUnsignedAttributes(SignerInformation signerInformation, AttributeTable attributeTable) {
        SignerInfo signerInfo = signerInformation.info;
        SignerInfo signerInfo2 = new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), attributeTable != null ? new DERSet(attributeTable.toASN1EncodableVector()) : null);
        return new SignerInformation(signerInfo2, signerInformation.contentType, signerInformation.content, null);
    }

    public byte[] getContentDigest() {
        byte[] bArr = this.resultDigest;
        if (bArr != null) {
            return Arrays.clone(bArr);
        }
        throw new IllegalStateException("method can only be called after verify.");
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.contentType;
    }

    public SignerInformationStore getCounterSignatures() {
        AttributeTable unsignedAttributes = getUnsignedAttributes();
        if (unsignedAttributes == null) {
            return new SignerInformationStore((Collection<SignerInformation>) new ArrayList<SignerInformation>(0));
        }
        ArrayList arrayList = new ArrayList();
        ASN1EncodableVector all = unsignedAttributes.getAll(CMSAttributes.counterSignature);
        for (int i = 0; i < all.size(); i++) {
            ASN1Set attrValues = ((Attribute) all.get(i)).getAttrValues();
            attrValues.size();
            Enumeration objects = attrValues.getObjects();
            while (objects.hasMoreElements()) {
                arrayList.add(new SignerInformation(SignerInfo.getInstance(objects.nextElement()), null, new CMSProcessableByteArray(getSignature()), null));
            }
        }
        return new SignerInformationStore((Collection<SignerInformation>) arrayList);
    }

    public String getDigestAlgOID() {
        return this.digestAlgorithm.getAlgorithm().getId();
    }

    public byte[] getDigestAlgParams() {
        try {
            return encodeObj(this.digestAlgorithm.getParameters());
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("exception getting digest parameters ");
            sb.append(e);
            throw new RuntimeException(sb.toString());
        }
    }

    public AlgorithmIdentifier getDigestAlgorithmID() {
        return this.digestAlgorithm;
    }

    public byte[] getEncodedSignedAttributes() throws IOException {
        ASN1Set aSN1Set = this.signedAttributeSet;
        if (aSN1Set != null) {
            return aSN1Set.getEncoded(ASN1Encoding.DER);
        }
        return null;
    }

    public String getEncryptionAlgOID() {
        return this.encryptionAlgorithm.getAlgorithm().getId();
    }

    public byte[] getEncryptionAlgParams() {
        try {
            return encodeObj(this.encryptionAlgorithm.getParameters());
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("exception getting encryption parameters ");
            sb.append(e);
            throw new RuntimeException(sb.toString());
        }
    }

    public SignerId getSID() {
        return this.sid;
    }

    public byte[] getSignature() {
        return Arrays.clone(this.signature);
    }

    public AttributeTable getSignedAttributes() {
        ASN1Set aSN1Set = this.signedAttributeSet;
        if (aSN1Set != null && this.signedAttributeValues == null) {
            this.signedAttributeValues = new AttributeTable(aSN1Set);
        }
        return this.signedAttributeValues;
    }

    public AttributeTable getUnsignedAttributes() {
        ASN1Set aSN1Set = this.unsignedAttributeSet;
        if (aSN1Set != null && this.unsignedAttributeValues == null) {
            this.unsignedAttributeValues = new AttributeTable(aSN1Set);
        }
        return this.unsignedAttributeValues;
    }

    public int getVersion() {
        return this.info.getVersion().getValue().intValue();
    }

    public boolean isCounterSignature() {
        return this.isCounterSignature;
    }

    public SignerInfo toASN1Structure() {
        return this.info;
    }

    public boolean verify(SignerInformationVerifier signerInformationVerifier) throws CMSException {
        Time signingTime = getSigningTime();
        if (!signerInformationVerifier.hasAssociatedCertificate() || signingTime == null || signerInformationVerifier.getAssociatedCertificate().isValidOn(signingTime.getDate())) {
            return doVerify(signerInformationVerifier);
        }
        throw new CMSVerifierCertificateNotValidException("verifier not valid at signingTime");
    }
}
