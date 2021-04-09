package org.bouncycastle.openssl;

import java.io.IOException;
import org.bouncycastle.operator.OperatorCreationException;

public class PEMEncryptedKeyPair {
    private final String dekAlgName;
    private final byte[] iv;
    private final byte[] keyBytes;
    private final PEMKeyPairParser parser;

    PEMEncryptedKeyPair(String str, byte[] bArr, byte[] bArr2, PEMKeyPairParser pEMKeyPairParser) {
        this.dekAlgName = str;
        this.iv = bArr;
        this.keyBytes = bArr2;
        this.parser = pEMKeyPairParser;
    }

    public PEMKeyPair decryptKeyPair(PEMDecryptorProvider pEMDecryptorProvider) throws IOException {
        try {
            return this.parser.parse(pEMDecryptorProvider.get(this.dekAlgName).decrypt(this.keyBytes, this.iv));
        } catch (IOException e) {
            throw e;
        } catch (OperatorCreationException e2) {
            StringBuilder sb = new StringBuilder();
            sb.append("cannot create extraction operator: ");
            sb.append(e2.getMessage());
            throw new PEMException(sb.toString(), e2);
        } catch (Exception e3) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("exception processing key pair: ");
            sb2.append(e3.getMessage());
            throw new PEMException(sb2.toString(), e3);
        }
    }
}
