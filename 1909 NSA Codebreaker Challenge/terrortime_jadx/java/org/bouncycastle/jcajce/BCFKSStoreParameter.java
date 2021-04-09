package org.bouncycastle.jcajce;

import java.io.OutputStream;
import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;
import org.bouncycastle.crypto.util.PBKDFConfig;

public class BCFKSStoreParameter implements LoadStoreParameter {
    private OutputStream out;
    private final ProtectionParameter protectionParameter;
    private final PBKDFConfig storeConfig;

    public BCFKSStoreParameter(OutputStream outputStream, PBKDFConfig pBKDFConfig, ProtectionParameter protectionParameter2) {
        this.out = outputStream;
        this.storeConfig = pBKDFConfig;
        this.protectionParameter = protectionParameter2;
    }

    public BCFKSStoreParameter(OutputStream outputStream, PBKDFConfig pBKDFConfig, char[] cArr) {
        this(outputStream, pBKDFConfig, (ProtectionParameter) new PasswordProtection(cArr));
    }

    public OutputStream getOutputStream() {
        return this.out;
    }

    public ProtectionParameter getProtectionParameter() {
        return this.protectionParameter;
    }

    public PBKDFConfig getStorePBKDFConfig() {
        return this.storeConfig;
    }
}
