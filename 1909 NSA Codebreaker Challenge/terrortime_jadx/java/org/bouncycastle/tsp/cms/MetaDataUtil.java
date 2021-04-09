package org.bouncycastle.tsp.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.cms.Attributes;
import org.bouncycastle.asn1.cms.MetaData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;

class MetaDataUtil {
    private final MetaData metaData;

    MetaDataUtil(MetaData metaData2) {
        this.metaData = metaData2;
    }

    private String convertString(ASN1String aSN1String) {
        if (aSN1String != null) {
            return aSN1String.toString();
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public String getFileName() {
        MetaData metaData2 = this.metaData;
        if (metaData2 != null) {
            return convertString(metaData2.getFileName());
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public String getMediaType() {
        MetaData metaData2 = this.metaData;
        if (metaData2 != null) {
            return convertString(metaData2.getMediaType());
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public Attributes getOtherMetaData() {
        MetaData metaData2 = this.metaData;
        if (metaData2 != null) {
            return metaData2.getOtherMetaData();
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public void initialiseMessageImprintDigestCalculator(DigestCalculator digestCalculator) throws CMSException {
        MetaData metaData2 = this.metaData;
        if (metaData2 != null && metaData2.isHashProtected()) {
            try {
                digestCalculator.getOutputStream().write(this.metaData.getEncoded(ASN1Encoding.DER));
            } catch (IOException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("unable to initialise calculator from metaData: ");
                sb.append(e.getMessage());
                throw new CMSException(sb.toString(), e);
            }
        }
    }
}
