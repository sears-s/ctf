package org.bouncycastle.pkix.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.cert.CertPathBuilder;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;

class PKIXProviderJcaJceHelper extends ProviderJcaJceHelper implements PKIXJcaJceHelper {
    public PKIXProviderJcaJceHelper(Provider provider) {
        super(provider);
    }

    public CertPathBuilder createCertPathBuilder(String str) throws NoSuchAlgorithmException {
        return CertPathBuilder.getInstance(str, this.provider);
    }
}
