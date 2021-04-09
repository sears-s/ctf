package org.bouncycastle.jcajce.provider.util;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;

public abstract class AsymmetricAlgorithmProvider extends AlgorithmProvider {
    /* access modifiers changed from: protected */
    public void addSignatureAlgorithm(ConfigurableProvider configurableProvider, String str, String str2, String str3, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("WITH");
        sb.append(str2);
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        sb3.append("with");
        sb3.append(str2);
        String sb4 = sb3.toString();
        StringBuilder sb5 = new StringBuilder();
        sb5.append(str);
        sb5.append("With");
        sb5.append(str2);
        String sb6 = sb5.toString();
        StringBuilder sb7 = new StringBuilder();
        sb7.append(str);
        sb7.append("/");
        sb7.append(str2);
        String sb8 = sb7.toString();
        StringBuilder sb9 = new StringBuilder();
        sb9.append("Signature.");
        sb9.append(sb2);
        configurableProvider.addAlgorithm(sb9.toString(), str3);
        StringBuilder sb10 = new StringBuilder();
        String str4 = "Alg.Alias.Signature.";
        sb10.append(str4);
        sb10.append(sb4);
        configurableProvider.addAlgorithm(sb10.toString(), sb2);
        StringBuilder sb11 = new StringBuilder();
        sb11.append(str4);
        sb11.append(sb6);
        configurableProvider.addAlgorithm(sb11.toString(), sb2);
        StringBuilder sb12 = new StringBuilder();
        sb12.append(str4);
        sb12.append(sb8);
        configurableProvider.addAlgorithm(sb12.toString(), sb2);
        StringBuilder sb13 = new StringBuilder();
        sb13.append(str4);
        sb13.append(aSN1ObjectIdentifier);
        configurableProvider.addAlgorithm(sb13.toString(), sb2);
        StringBuilder sb14 = new StringBuilder();
        sb14.append("Alg.Alias.Signature.OID.");
        sb14.append(aSN1ObjectIdentifier);
        configurableProvider.addAlgorithm(sb14.toString(), sb2);
    }

    /* access modifiers changed from: protected */
    public void addSignatureAlgorithm(ConfigurableProvider configurableProvider, String str, String str2, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        StringBuilder sb = new StringBuilder();
        sb.append("Signature.");
        sb.append(str);
        configurableProvider.addAlgorithm(sb.toString(), str2);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Alg.Alias.Signature.");
        sb2.append(aSN1ObjectIdentifier);
        configurableProvider.addAlgorithm(sb2.toString(), str);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Alg.Alias.Signature.OID.");
        sb3.append(aSN1ObjectIdentifier);
        configurableProvider.addAlgorithm(sb3.toString(), str);
    }

    /* access modifiers changed from: protected */
    public void registerOid(ConfigurableProvider configurableProvider, ASN1ObjectIdentifier aSN1ObjectIdentifier, String str, AsymmetricKeyInfoConverter asymmetricKeyInfoConverter) {
        StringBuilder sb = new StringBuilder();
        sb.append("Alg.Alias.KeyFactory.");
        sb.append(aSN1ObjectIdentifier);
        configurableProvider.addAlgorithm(sb.toString(), str);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Alg.Alias.KeyPairGenerator.");
        sb2.append(aSN1ObjectIdentifier);
        configurableProvider.addAlgorithm(sb2.toString(), str);
        configurableProvider.addKeyInfoConverter(aSN1ObjectIdentifier, asymmetricKeyInfoConverter);
    }

    /* access modifiers changed from: protected */
    public void registerOidAlgorithmParameterGenerator(ConfigurableProvider configurableProvider, ASN1ObjectIdentifier aSN1ObjectIdentifier, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("Alg.Alias.AlgorithmParameterGenerator.");
        sb.append(aSN1ObjectIdentifier);
        configurableProvider.addAlgorithm(sb.toString(), str);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Alg.Alias.AlgorithmParameters.");
        sb2.append(aSN1ObjectIdentifier);
        configurableProvider.addAlgorithm(sb2.toString(), str);
    }

    /* access modifiers changed from: protected */
    public void registerOidAlgorithmParameters(ConfigurableProvider configurableProvider, ASN1ObjectIdentifier aSN1ObjectIdentifier, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("Alg.Alias.AlgorithmParameters.");
        sb.append(aSN1ObjectIdentifier);
        configurableProvider.addAlgorithm(sb.toString(), str);
    }
}
