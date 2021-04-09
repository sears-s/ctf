package org.bouncycastle.voms;

import com.badguy.terrortime.BuildConfig;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.IetfAttrSyntax;
import org.bouncycastle.cert.X509AttributeCertificateHolder;

public class VOMSAttribute {
    public static final String VOMS_ATTR_OID = "1.3.6.1.4.1.8005.100.100.4";
    private X509AttributeCertificateHolder myAC;
    private List myFQANs = new ArrayList();
    private String myHostPort;
    private List myStringList = new ArrayList();
    private String myVo;

    public class FQAN {
        String capability;
        String fqan;
        String group;
        String role;

        public FQAN(String str) {
            this.fqan = str;
        }

        public FQAN(String str, String str2, String str3) {
            this.group = str;
            this.role = str2;
            this.capability = str3;
        }

        public String getCapability() {
            if (this.group == null && this.fqan != null) {
                split();
            }
            return this.capability;
        }

        public String getFQAN() {
            String str = this.fqan;
            if (str != null) {
                return str;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(this.group);
            sb.append("/Role=");
            String str2 = this.role;
            String str3 = BuildConfig.FLAVOR;
            if (str2 == null) {
                str2 = str3;
            }
            sb.append(str2);
            if (this.capability != null) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("/Capability=");
                sb2.append(this.capability);
                str3 = sb2.toString();
            }
            sb.append(str3);
            this.fqan = sb.toString();
            return this.fqan;
        }

        public String getGroup() {
            if (this.group == null && this.fqan != null) {
                split();
            }
            return this.group;
        }

        public String getRole() {
            if (this.group == null && this.fqan != null) {
                split();
            }
            return this.role;
        }

        /* access modifiers changed from: protected */
        public void split() {
            this.fqan.length();
            int indexOf = this.fqan.indexOf("/Role=");
            if (indexOf >= 0) {
                this.group = this.fqan.substring(0, indexOf);
                int i = indexOf + 6;
                int indexOf2 = this.fqan.indexOf("/Capability=", i);
                String str = this.fqan;
                String substring = indexOf2 < 0 ? str.substring(i) : str.substring(i, indexOf2);
                if (substring.length() == 0) {
                    substring = null;
                }
                this.role = substring;
                String substring2 = indexOf2 < 0 ? null : this.fqan.substring(indexOf2 + 12);
                if (substring2 == null || substring2.length() == 0) {
                    substring2 = null;
                }
                this.capability = substring2;
            }
        }

        public String toString() {
            return getFQAN();
        }
    }

    public VOMSAttribute(X509AttributeCertificateHolder x509AttributeCertificateHolder) {
        String str = "/";
        if (x509AttributeCertificateHolder != null) {
            this.myAC = x509AttributeCertificateHolder;
            Attribute[] attributes = x509AttributeCertificateHolder.getAttributes(new ASN1ObjectIdentifier(VOMS_ATTR_OID));
            if (attributes != null) {
                int i = 0;
                while (i != attributes.length) {
                    try {
                        IetfAttrSyntax instance = IetfAttrSyntax.getInstance(attributes[i].getAttributeValues()[0]);
                        String string = ((DERIA5String) instance.getPolicyAuthority().getNames()[0].getName()).getString();
                        int indexOf = string.indexOf("://");
                        if (indexOf < 0 || indexOf == string.length() - 1) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Bad encoding of VOMS policyAuthority : [");
                            sb.append(string);
                            sb.append("]");
                            throw new IllegalArgumentException(sb.toString());
                        }
                        this.myVo = string.substring(0, indexOf);
                        this.myHostPort = string.substring(indexOf + 3);
                        if (instance.getValueType() == 1) {
                            ASN1OctetString[] aSN1OctetStringArr = (ASN1OctetString[]) instance.getValues();
                            for (int i2 = 0; i2 != aSN1OctetStringArr.length; i2++) {
                                String str2 = new String(aSN1OctetStringArr[i2].getOctets());
                                FQAN fqan = new FQAN(str2);
                                if (!this.myStringList.contains(str2)) {
                                    StringBuilder sb2 = new StringBuilder();
                                    sb2.append(str);
                                    sb2.append(this.myVo);
                                    sb2.append(str);
                                    if (str2.startsWith(sb2.toString())) {
                                        this.myStringList.add(str2);
                                        this.myFQANs.add(fqan);
                                    }
                                }
                            }
                            i++;
                        } else {
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append("VOMS attribute values are not encoded as octet strings, policyAuthority = ");
                            sb3.append(string);
                            throw new IllegalArgumentException(sb3.toString());
                        }
                    } catch (IllegalArgumentException e) {
                        throw e;
                    } catch (Exception e2) {
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("Badly encoded VOMS extension in AC issued by ");
                        sb4.append(x509AttributeCertificateHolder.getIssuer());
                        throw new IllegalArgumentException(sb4.toString());
                    }
                }
                return;
            }
            return;
        }
        throw new IllegalArgumentException("VOMSAttribute: AttributeCertificate is NULL");
    }

    public X509AttributeCertificateHolder getAC() {
        return this.myAC;
    }

    public List getFullyQualifiedAttributes() {
        return this.myStringList;
    }

    public String getHostPort() {
        return this.myHostPort;
    }

    public List getListOfFQAN() {
        return this.myFQANs;
    }

    public String getVO() {
        return this.myVo;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VO      :");
        sb.append(this.myVo);
        sb.append("\nHostPort:");
        sb.append(this.myHostPort);
        sb.append("\nFQANs   :");
        sb.append(this.myFQANs);
        return sb.toString();
    }
}
