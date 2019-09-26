package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

abstract class SymmetricAlgorithmProvider extends AlgorithmProvider {
    SymmetricAlgorithmProvider() {
    }

    /* access modifiers changed from: protected */
    public void addCMacAlgorithm(ConfigurableProvider configurableProvider, String str, String str2, String str3) {
        StringBuilder sb = new StringBuilder();
        sb.append("Mac.");
        sb.append(str);
        String str4 = "-CMAC";
        sb.append(str4);
        configurableProvider.addAlgorithm(sb.toString(), str2);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Alg.Alias.Mac.");
        sb2.append(str);
        String str5 = "CMAC";
        sb2.append(str5);
        String sb3 = sb2.toString();
        StringBuilder sb4 = new StringBuilder();
        sb4.append(str);
        sb4.append(str4);
        configurableProvider.addAlgorithm(sb3, sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("KeyGenerator.");
        sb5.append(str);
        sb5.append(str4);
        configurableProvider.addAlgorithm(sb5.toString(), str3);
        StringBuilder sb6 = new StringBuilder();
        sb6.append("Alg.Alias.KeyGenerator.");
        sb6.append(str);
        sb6.append(str5);
        String sb7 = sb6.toString();
        StringBuilder sb8 = new StringBuilder();
        sb8.append(str);
        sb8.append(str4);
        configurableProvider.addAlgorithm(sb7, sb8.toString());
    }

    /* access modifiers changed from: protected */
    public void addGMacAlgorithm(ConfigurableProvider configurableProvider, String str, String str2, String str3) {
        StringBuilder sb = new StringBuilder();
        sb.append("Mac.");
        sb.append(str);
        String str4 = "-GMAC";
        sb.append(str4);
        configurableProvider.addAlgorithm(sb.toString(), str2);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Alg.Alias.Mac.");
        sb2.append(str);
        String str5 = "GMAC";
        sb2.append(str5);
        String sb3 = sb2.toString();
        StringBuilder sb4 = new StringBuilder();
        sb4.append(str);
        sb4.append(str4);
        configurableProvider.addAlgorithm(sb3, sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("KeyGenerator.");
        sb5.append(str);
        sb5.append(str4);
        configurableProvider.addAlgorithm(sb5.toString(), str3);
        StringBuilder sb6 = new StringBuilder();
        sb6.append("Alg.Alias.KeyGenerator.");
        sb6.append(str);
        sb6.append(str5);
        String sb7 = sb6.toString();
        StringBuilder sb8 = new StringBuilder();
        sb8.append(str);
        sb8.append(str4);
        configurableProvider.addAlgorithm(sb7, sb8.toString());
    }

    /* access modifiers changed from: protected */
    public void addPoly1305Algorithm(ConfigurableProvider configurableProvider, String str, String str2, String str3) {
        StringBuilder sb = new StringBuilder();
        sb.append("Mac.POLY1305-");
        sb.append(str);
        configurableProvider.addAlgorithm(sb.toString(), str2);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Alg.Alias.Mac.POLY1305");
        sb2.append(str);
        String sb3 = sb2.toString();
        StringBuilder sb4 = new StringBuilder();
        String str4 = "POLY1305-";
        sb4.append(str4);
        sb4.append(str);
        configurableProvider.addAlgorithm(sb3, sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("KeyGenerator.POLY1305-");
        sb5.append(str);
        configurableProvider.addAlgorithm(sb5.toString(), str3);
        StringBuilder sb6 = new StringBuilder();
        sb6.append("Alg.Alias.KeyGenerator.POLY1305");
        sb6.append(str);
        String sb7 = sb6.toString();
        StringBuilder sb8 = new StringBuilder();
        sb8.append(str4);
        sb8.append(str);
        configurableProvider.addAlgorithm(sb7, sb8.toString());
    }
}
