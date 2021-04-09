package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface X509AttributeIdentifiers {
    public static final ASN1ObjectIdentifier RoleSyntax;
    public static final ASN1ObjectIdentifier id_aca;
    public static final ASN1ObjectIdentifier id_aca_accessIdentity = id_aca.branch("2");
    public static final ASN1ObjectIdentifier id_aca_authenticationInfo = id_aca.branch("1");
    public static final ASN1ObjectIdentifier id_aca_chargingIdentity = id_aca.branch("3");
    public static final ASN1ObjectIdentifier id_aca_encAttrs;
    public static final ASN1ObjectIdentifier id_aca_group;
    public static final ASN1ObjectIdentifier id_at_clearance = new ASN1ObjectIdentifier("2.5.1.5.55");
    public static final ASN1ObjectIdentifier id_at_role;
    public static final ASN1ObjectIdentifier id_ce_targetInformation = X509ObjectIdentifiers.id_ce.branch("55");
    public static final ASN1ObjectIdentifier id_pe_aaControls;
    public static final ASN1ObjectIdentifier id_pe_ac_auditIdentity;
    public static final ASN1ObjectIdentifier id_pe_ac_proxying;

    static {
        String str = "2.5.4.72";
        RoleSyntax = new ASN1ObjectIdentifier(str);
        String str2 = "4";
        id_pe_ac_auditIdentity = X509ObjectIdentifiers.id_pe.branch(str2);
        String str3 = "6";
        id_pe_aaControls = X509ObjectIdentifiers.id_pe.branch(str3);
        String str4 = "10";
        id_pe_ac_proxying = X509ObjectIdentifiers.id_pe.branch(str4);
        id_aca = X509ObjectIdentifiers.id_pkix.branch(str4);
        id_aca_group = id_aca.branch(str2);
        id_aca_encAttrs = id_aca.branch(str3);
        id_at_role = new ASN1ObjectIdentifier(str);
    }
}
