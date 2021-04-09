package org.bouncycastle.pkix.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPathBuilder;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;

class PKIXDefaultJcaJceHelper extends DefaultJcaJceHelper implements PKIXJcaJceHelper {
    public CertPathBuilder createCertPathBuilder(String str) throws NoSuchAlgorithmException {
        return CertPathBuilder.getInstance(str);
    }
}
