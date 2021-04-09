package org.bouncycastle.tsp.cms;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.TimeStampAndCRL;
import org.bouncycastle.asn1.cms.TimeStampedData;
import org.bouncycastle.asn1.cms.TimeStampedDataParser;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.Arrays;

class TimeStampDataUtil {
    private final MetaDataUtil metaDataUtil;
    private final TimeStampAndCRL[] timeStamps;

    TimeStampDataUtil(TimeStampedData timeStampedData) {
        this.metaDataUtil = new MetaDataUtil(timeStampedData.getMetaData());
        this.timeStamps = timeStampedData.getTemporalEvidence().getTstEvidence().toTimeStampAndCRLArray();
    }

    TimeStampDataUtil(TimeStampedDataParser timeStampedDataParser) throws IOException {
        this.metaDataUtil = new MetaDataUtil(timeStampedDataParser.getMetaData());
        this.timeStamps = timeStampedDataParser.getTemporalEvidence().getTstEvidence().toTimeStampAndCRLArray();
    }

    private void compareDigest(TimeStampToken timeStampToken, byte[] bArr) throws ImprintDigestInvalidException {
        if (!Arrays.areEqual(bArr, timeStampToken.getTimeStampInfo().getMessageImprintDigest())) {
            throw new ImprintDigestInvalidException("hash calculated is different from MessageImprintDigest found in TimeStampToken", timeStampToken);
        }
    }

    /* access modifiers changed from: 0000 */
    public byte[] calculateNextHash(DigestCalculator digestCalculator) throws CMSException {
        TimeStampAndCRL[] timeStampAndCRLArr = this.timeStamps;
        TimeStampAndCRL timeStampAndCRL = timeStampAndCRLArr[timeStampAndCRLArr.length - 1];
        OutputStream outputStream = digestCalculator.getOutputStream();
        try {
            outputStream.write(timeStampAndCRL.getEncoded(ASN1Encoding.DER));
            outputStream.close();
            return digestCalculator.getDigest();
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("exception calculating hash: ");
            sb.append(e.getMessage());
            throw new CMSException(sb.toString(), e);
        }
    }

    /* access modifiers changed from: 0000 */
    public String getFileName() {
        return this.metaDataUtil.getFileName();
    }

    /* access modifiers changed from: 0000 */
    public String getMediaType() {
        return this.metaDataUtil.getMediaType();
    }

    /* access modifiers changed from: 0000 */
    public DigestCalculator getMessageImprintDigestCalculator(DigestCalculatorProvider digestCalculatorProvider) throws OperatorCreationException {
        try {
            DigestCalculator digestCalculator = digestCalculatorProvider.get(new AlgorithmIdentifier(getTimeStampToken(this.timeStamps[0]).getTimeStampInfo().getMessageImprintAlgOID()));
            initialiseMessageImprintDigestCalculator(digestCalculator);
            return digestCalculator;
        } catch (CMSException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("unable to extract algorithm ID: ");
            sb.append(e.getMessage());
            throw new OperatorCreationException(sb.toString(), e);
        }
    }

    /* access modifiers changed from: 0000 */
    public AttributeTable getOtherMetaData() {
        return new AttributeTable(this.metaDataUtil.getOtherMetaData());
    }

    /* access modifiers changed from: 0000 */
    public TimeStampToken getTimeStampToken(TimeStampAndCRL timeStampAndCRL) throws CMSException {
        String str = "token data invalid: ";
        try {
            return new TimeStampToken(timeStampAndCRL.getTimeStampToken());
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("unable to parse token data: ");
            sb.append(e.getMessage());
            throw new CMSException(sb.toString(), e);
        } catch (TSPException e2) {
            if (e2.getCause() instanceof CMSException) {
                throw ((CMSException) e2.getCause());
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(e2.getMessage());
            throw new CMSException(sb2.toString(), e2);
        } catch (IllegalArgumentException e3) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            sb3.append(e3.getMessage());
            throw new CMSException(sb3.toString(), e3);
        }
    }

    /* access modifiers changed from: 0000 */
    public TimeStampToken[] getTimeStampTokens() throws CMSException {
        TimeStampToken[] timeStampTokenArr = new TimeStampToken[this.timeStamps.length];
        int i = 0;
        while (true) {
            TimeStampAndCRL[] timeStampAndCRLArr = this.timeStamps;
            if (i >= timeStampAndCRLArr.length) {
                return timeStampTokenArr;
            }
            timeStampTokenArr[i] = getTimeStampToken(timeStampAndCRLArr[i]);
            i++;
        }
    }

    /* access modifiers changed from: 0000 */
    public TimeStampAndCRL[] getTimeStamps() {
        return this.timeStamps;
    }

    /* access modifiers changed from: 0000 */
    public void initialiseMessageImprintDigestCalculator(DigestCalculator digestCalculator) throws CMSException {
        this.metaDataUtil.initialiseMessageImprintDigestCalculator(digestCalculator);
    }

    /* access modifiers changed from: 0000 */
    public void validate(DigestCalculatorProvider digestCalculatorProvider, byte[] bArr) throws ImprintDigestInvalidException, CMSException {
        int i = 0;
        while (true) {
            TimeStampAndCRL[] timeStampAndCRLArr = this.timeStamps;
            if (i < timeStampAndCRLArr.length) {
                try {
                    TimeStampToken timeStampToken = getTimeStampToken(timeStampAndCRLArr[i]);
                    if (i > 0) {
                        DigestCalculator digestCalculator = digestCalculatorProvider.get(timeStampToken.getTimeStampInfo().getHashAlgorithm());
                        digestCalculator.getOutputStream().write(this.timeStamps[i - 1].getEncoded(ASN1Encoding.DER));
                        bArr = digestCalculator.getDigest();
                    }
                    compareDigest(timeStampToken, bArr);
                    i++;
                } catch (IOException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("exception calculating hash: ");
                    sb.append(e.getMessage());
                    throw new CMSException(sb.toString(), e);
                } catch (OperatorCreationException e2) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("cannot create digest: ");
                    sb2.append(e2.getMessage());
                    throw new CMSException(sb2.toString(), e2);
                }
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void validate(DigestCalculatorProvider digestCalculatorProvider, byte[] bArr, TimeStampToken timeStampToken) throws ImprintDigestInvalidException, CMSException {
        try {
            byte[] encoded = timeStampToken.getEncoded();
            int i = 0;
            while (true) {
                TimeStampAndCRL[] timeStampAndCRLArr = this.timeStamps;
                if (i < timeStampAndCRLArr.length) {
                    try {
                        TimeStampToken timeStampToken2 = getTimeStampToken(timeStampAndCRLArr[i]);
                        if (i > 0) {
                            DigestCalculator digestCalculator = digestCalculatorProvider.get(timeStampToken2.getTimeStampInfo().getHashAlgorithm());
                            digestCalculator.getOutputStream().write(this.timeStamps[i - 1].getEncoded(ASN1Encoding.DER));
                            bArr = digestCalculator.getDigest();
                        }
                        compareDigest(timeStampToken2, bArr);
                        if (!Arrays.areEqual(timeStampToken2.getEncoded(), encoded)) {
                            i++;
                        } else {
                            return;
                        }
                    } catch (IOException e) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("exception calculating hash: ");
                        sb.append(e.getMessage());
                        throw new CMSException(sb.toString(), e);
                    } catch (OperatorCreationException e2) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("cannot create digest: ");
                        sb2.append(e2.getMessage());
                        throw new CMSException(sb2.toString(), e2);
                    }
                } else {
                    throw new ImprintDigestInvalidException("passed in token not associated with timestamps present", timeStampToken);
                }
            }
        } catch (IOException e3) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("exception encoding timeStampToken: ");
            sb3.append(e3.getMessage());
            throw new CMSException(sb3.toString(), e3);
        }
    }
}
