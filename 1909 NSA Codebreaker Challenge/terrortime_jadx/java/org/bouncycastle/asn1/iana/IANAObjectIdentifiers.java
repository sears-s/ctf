package org.bouncycastle.asn1.iana;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface IANAObjectIdentifiers {
    public static final ASN1ObjectIdentifier SNMPv2;
    public static final ASN1ObjectIdentifier _private;
    public static final ASN1ObjectIdentifier directory;
    public static final ASN1ObjectIdentifier experimental;
    public static final ASN1ObjectIdentifier hmacMD5;
    public static final ASN1ObjectIdentifier hmacRIPEMD160;
    public static final ASN1ObjectIdentifier hmacSHA1;
    public static final ASN1ObjectIdentifier hmacTIGER;
    public static final ASN1ObjectIdentifier internet = new ASN1ObjectIdentifier("1.3.6.1");
    public static final ASN1ObjectIdentifier ipsec = security_mechanisms.branch("8");
    public static final ASN1ObjectIdentifier isakmpOakley;
    public static final ASN1ObjectIdentifier mail = internet.branch("7");
    public static final ASN1ObjectIdentifier mgmt;
    public static final ASN1ObjectIdentifier pkix;
    public static final ASN1ObjectIdentifier security;
    public static final ASN1ObjectIdentifier security_mechanisms;
    public static final ASN1ObjectIdentifier security_nametypes;

    static {
        String str = "1";
        directory = internet.branch(str);
        String str2 = "2";
        mgmt = internet.branch(str2);
        String str3 = "3";
        experimental = internet.branch(str3);
        String str4 = "4";
        _private = internet.branch(str4);
        String str5 = "5";
        security = internet.branch(str5);
        String str6 = "6";
        SNMPv2 = internet.branch(str6);
        security_mechanisms = security.branch(str5);
        security_nametypes = security.branch(str6);
        pkix = security_mechanisms.branch(str6);
        isakmpOakley = ipsec.branch(str);
        hmacMD5 = isakmpOakley.branch(str);
        hmacSHA1 = isakmpOakley.branch(str2);
        hmacTIGER = isakmpOakley.branch(str3);
        hmacRIPEMD160 = isakmpOakley.branch(str4);
    }
}
