package org.bouncycastle.cert.dane;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class DANEEntrySelectorFactory {
    private final DigestCalculator digestCalculator;

    public DANEEntrySelectorFactory(DigestCalculator digestCalculator2) {
        this.digestCalculator = digestCalculator2;
    }

    public DANEEntrySelector createSelector(String str) throws DANEException {
        byte[] uTF8ByteArray = Strings.toUTF8ByteArray(str.substring(0, str.indexOf(64)));
        try {
            OutputStream outputStream = this.digestCalculator.getOutputStream();
            outputStream.write(uTF8ByteArray);
            outputStream.close();
            byte[] digest = this.digestCalculator.getDigest();
            StringBuilder sb = new StringBuilder();
            sb.append(Strings.fromByteArray(Hex.encode(digest)));
            sb.append("._smimecert.");
            sb.append(str.substring(str.indexOf(64) + 1));
            return new DANEEntrySelector(sb.toString());
        } catch (IOException e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Unable to calculate digest string: ");
            sb2.append(e.getMessage());
            throw new DANEException(sb2.toString(), e);
        }
    }
}
