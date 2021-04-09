package org.bouncycastle.pkix.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertPathBuilder;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;

class PKIXNamedJcaJceHelper extends NamedJcaJceHelper implements PKIXJcaJceHelper {
    public PKIXNamedJcaJceHelper(String str) {
        super(str);
    }

    public CertPathBuilder createCertPathBuilder(String str) throws NoSuchAlgorithmException, NoSuchProviderException {
        return CertPathBuilder.getInstance(str, this.providerName);
    }
}
