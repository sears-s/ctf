package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

abstract class DigestAlgorithmProvider extends AlgorithmProvider {
    DigestAlgorithmProvider() {
    }

    /* access modifiers changed from: protected */
    public void addHMACAlgorithm(ConfigurableProvider configurableProvider, String str, String str2, String str3) {
        StringBuilder sb = new StringBuilder();
        sb.append("HMAC");
        sb.append(str);
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Mac.");
        sb3.append(sb2);
        configurableProvider.addAlgorithm(sb3.toString(), str2);
        StringBuilder sb4 = new StringBuilder();
        sb4.append("Alg.Alias.Mac.HMAC-");
        sb4.append(str);
        configurableProvider.addAlgorithm(sb4.toString(), sb2);
        StringBuilder sb5 = new StringBuilder();
        sb5.append("Alg.Alias.Mac.HMAC/");
        sb5.append(str);
        configurableProvider.addAlgorithm(sb5.toString(), sb2);
        StringBuilder sb6 = new StringBuilder();
        sb6.append("KeyGenerator.");
        sb6.append(sb2);
        configurableProvider.addAlgorithm(sb6.toString(), str3);
        StringBuilder sb7 = new StringBuilder();
        sb7.append("Alg.Alias.KeyGenerator.HMAC-");
        sb7.append(str);
        configurableProvider.addAlgorithm(sb7.toString(), sb2);
        StringBuilder sb8 = new StringBuilder();
        sb8.append("Alg.Alias.KeyGenerator.HMAC/");
        sb8.append(str);
        configurableProvider.addAlgorithm(sb8.toString(), sb2);
    }

    /* access modifiers changed from: protected */
    public void addHMACAlias(ConfigurableProvider configurableProvider, String str, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        StringBuilder sb = new StringBuilder();
        sb.append("HMAC");
        sb.append(str);
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Alg.Alias.Mac.");
        sb3.append(aSN1ObjectIdentifier);
        configurableProvider.addAlgorithm(sb3.toString(), sb2);
        StringBuilder sb4 = new StringBuilder();
        sb4.append("Alg.Alias.KeyGenerator.");
        sb4.append(aSN1ObjectIdentifier);
        configurableProvider.addAlgorithm(sb4.toString(), sb2);
    }
}
