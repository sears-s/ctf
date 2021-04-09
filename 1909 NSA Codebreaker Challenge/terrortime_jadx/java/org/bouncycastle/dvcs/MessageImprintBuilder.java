package org.bouncycastle.dvcs;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.operator.DigestCalculator;

public class MessageImprintBuilder {
    private final DigestCalculator digestCalculator;

    public MessageImprintBuilder(DigestCalculator digestCalculator2) {
        this.digestCalculator = digestCalculator2;
    }

    public MessageImprint build(byte[] bArr) throws DVCSException {
        try {
            OutputStream outputStream = this.digestCalculator.getOutputStream();
            outputStream.write(bArr);
            outputStream.close();
            return new MessageImprint(new DigestInfo(this.digestCalculator.getAlgorithmIdentifier(), this.digestCalculator.getDigest()));
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("unable to build MessageImprint: ");
            sb.append(e.getMessage());
            throw new DVCSException(sb.toString(), e);
        }
    }
}
