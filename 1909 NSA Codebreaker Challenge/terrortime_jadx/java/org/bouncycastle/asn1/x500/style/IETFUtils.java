package org.bouncycastle.asn1.x500.style;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DERUniversalString;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class IETFUtils {
    public static void appendRDN(StringBuffer stringBuffer, RDN rdn, Hashtable hashtable) {
        if (rdn.isMultiValued()) {
            AttributeTypeAndValue[] typesAndValues = rdn.getTypesAndValues();
            boolean z = true;
            for (int i = 0; i != typesAndValues.length; i++) {
                if (z) {
                    z = false;
                } else {
                    stringBuffer.append('+');
                }
                appendTypeAndValue(stringBuffer, typesAndValues[i], hashtable);
            }
        } else if (rdn.getFirst() != null) {
            appendTypeAndValue(stringBuffer, rdn.getFirst(), hashtable);
        }
    }

    public static void appendTypeAndValue(StringBuffer stringBuffer, AttributeTypeAndValue attributeTypeAndValue, Hashtable hashtable) {
        String str = (String) hashtable.get(attributeTypeAndValue.getType());
        if (str == null) {
            str = attributeTypeAndValue.getType().getId();
        }
        stringBuffer.append(str);
        stringBuffer.append('=');
        stringBuffer.append(valueToString(attributeTypeAndValue.getValue()));
    }

    private static boolean atvAreEqual(AttributeTypeAndValue attributeTypeAndValue, AttributeTypeAndValue attributeTypeAndValue2) {
        if (attributeTypeAndValue == attributeTypeAndValue2) {
            return true;
        }
        return attributeTypeAndValue != null && attributeTypeAndValue2 != null && attributeTypeAndValue.getType().equals(attributeTypeAndValue2.getType()) && canonicalString(attributeTypeAndValue.getValue()).equals(canonicalString(attributeTypeAndValue2.getValue()));
    }

    public static String canonicalString(ASN1Encodable aSN1Encodable) {
        return canonicalize(valueToString(aSN1Encodable));
    }

    public static String canonicalize(String str) {
        String lowerCase = Strings.toLowerCase(str);
        int i = 0;
        if (lowerCase.length() > 0 && lowerCase.charAt(0) == '#') {
            ASN1Primitive decodeObject = decodeObject(lowerCase);
            if (decodeObject instanceof ASN1String) {
                lowerCase = Strings.toLowerCase(((ASN1String) decodeObject).getString());
            }
        }
        if (lowerCase.length() > 1) {
            while (true) {
                int i2 = i + 1;
                if (i2 < lowerCase.length() && lowerCase.charAt(i) == '\\' && lowerCase.charAt(i2) == ' ') {
                    i += 2;
                } else {
                    int length = lowerCase.length() - 1;
                }
            }
            int length2 = lowerCase.length() - 1;
            while (true) {
                int i3 = length2 - 1;
                if (i3 > 0 && lowerCase.charAt(i3) == '\\' && lowerCase.charAt(length2) == ' ') {
                    length2 -= 2;
                } else if (i > 0 || length2 < lowerCase.length() - 1) {
                    lowerCase = lowerCase.substring(i, length2 + 1);
                }
            }
            lowerCase = lowerCase.substring(i, length2 + 1);
        }
        return stripInternalSpaces(lowerCase);
    }

    private static int convertHex(char c) {
        if ('0' <= c && c <= '9') {
            return c - '0';
        }
        return (('a' > c || c > 'f') ? c - 'A' : c - 'a') + 10;
    }

    public static ASN1ObjectIdentifier decodeAttrName(String str, Hashtable hashtable) {
        if (Strings.toUpperCase(str).startsWith("OID.")) {
            return new ASN1ObjectIdentifier(str.substring(4));
        }
        if (str.charAt(0) >= '0' && str.charAt(0) <= '9') {
            return new ASN1ObjectIdentifier(str);
        }
        ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier) hashtable.get(Strings.toLowerCase(str));
        if (aSN1ObjectIdentifier != null) {
            return aSN1ObjectIdentifier;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown object id - ");
        sb.append(str);
        sb.append(" - passed to distinguished name");
        throw new IllegalArgumentException(sb.toString());
    }

    private static ASN1Primitive decodeObject(String str) {
        try {
            return ASN1Primitive.fromByteArray(Hex.decode(str.substring(1)));
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("unknown encoding in name: ");
            sb.append(e);
            throw new IllegalStateException(sb.toString());
        }
    }

    public static String[] findAttrNamesForOID(ASN1ObjectIdentifier aSN1ObjectIdentifier, Hashtable hashtable) {
        Enumeration elements = hashtable.elements();
        int i = 0;
        int i2 = 0;
        while (elements.hasMoreElements()) {
            if (aSN1ObjectIdentifier.equals(elements.nextElement())) {
                i2++;
            }
        }
        String[] strArr = new String[i2];
        Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            String str = (String) keys.nextElement();
            if (aSN1ObjectIdentifier.equals(hashtable.get(str))) {
                int i3 = i + 1;
                strArr[i] = str;
                i = i3;
            }
        }
        return strArr;
    }

    private static boolean isHexDigit(char c) {
        return ('0' <= c && c <= '9') || ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F');
    }

    public static boolean rDNAreEqual(RDN rdn, RDN rdn2) {
        boolean isMultiValued = rdn.isMultiValued();
        if (rdn2.isMultiValued() != isMultiValued) {
            return false;
        }
        if (!isMultiValued) {
            return atvAreEqual(rdn.getFirst(), rdn2.getFirst());
        }
        if (rdn.size() != rdn2.size()) {
            return false;
        }
        AttributeTypeAndValue[] typesAndValues = rdn.getTypesAndValues();
        AttributeTypeAndValue[] typesAndValues2 = rdn2.getTypesAndValues();
        if (typesAndValues.length != typesAndValues2.length) {
            return false;
        }
        for (int i = 0; i != typesAndValues.length; i++) {
            if (!atvAreEqual(typesAndValues[i], typesAndValues2[i])) {
                return false;
            }
        }
        return true;
    }

    public static RDN[] rDNsFromString(String str, X500NameStyle x500NameStyle) {
        X500NameTokenizer x500NameTokenizer = new X500NameTokenizer(str);
        X500NameBuilder x500NameBuilder = new X500NameBuilder(x500NameStyle);
        while (x500NameTokenizer.hasMoreTokens()) {
            String nextToken = x500NameTokenizer.nextToken();
            String str2 = "badly formatted directory string";
            if (nextToken.indexOf(43) > 0) {
                X500NameTokenizer x500NameTokenizer2 = new X500NameTokenizer(nextToken, '+');
                X500NameTokenizer x500NameTokenizer3 = new X500NameTokenizer(x500NameTokenizer2.nextToken(), '=');
                String nextToken2 = x500NameTokenizer3.nextToken();
                if (x500NameTokenizer3.hasMoreTokens()) {
                    String nextToken3 = x500NameTokenizer3.nextToken();
                    ASN1ObjectIdentifier attrNameToOID = x500NameStyle.attrNameToOID(nextToken2.trim());
                    if (x500NameTokenizer2.hasMoreTokens()) {
                        Vector vector = new Vector();
                        Vector vector2 = new Vector();
                        while (true) {
                            vector.addElement(attrNameToOID);
                            vector2.addElement(unescape(nextToken3));
                            if (!x500NameTokenizer2.hasMoreTokens()) {
                                x500NameBuilder.addMultiValuedRDN(toOIDArray(vector), toValueArray(vector2));
                                break;
                            }
                            X500NameTokenizer x500NameTokenizer4 = new X500NameTokenizer(x500NameTokenizer2.nextToken(), '=');
                            String nextToken4 = x500NameTokenizer4.nextToken();
                            if (x500NameTokenizer4.hasMoreTokens()) {
                                nextToken3 = x500NameTokenizer4.nextToken();
                                attrNameToOID = x500NameStyle.attrNameToOID(nextToken4.trim());
                            } else {
                                throw new IllegalArgumentException(str2);
                            }
                        }
                    } else {
                        x500NameBuilder.addRDN(attrNameToOID, unescape(nextToken3));
                    }
                } else {
                    throw new IllegalArgumentException(str2);
                }
            } else {
                X500NameTokenizer x500NameTokenizer5 = new X500NameTokenizer(nextToken, '=');
                String nextToken5 = x500NameTokenizer5.nextToken();
                if (x500NameTokenizer5.hasMoreTokens()) {
                    x500NameBuilder.addRDN(x500NameStyle.attrNameToOID(nextToken5.trim()), unescape(x500NameTokenizer5.nextToken()));
                } else {
                    throw new IllegalArgumentException(str2);
                }
            }
        }
        return x500NameBuilder.build().getRDNs();
    }

    public static String stripInternalSpaces(String str) {
        if (str.indexOf("  ") < 0) {
            return str;
        }
        StringBuffer stringBuffer = new StringBuffer();
        char charAt = str.charAt(0);
        stringBuffer.append(charAt);
        for (int i = 1; i < str.length(); i++) {
            char charAt2 = str.charAt(i);
            if (charAt != ' ' || charAt2 != ' ') {
                stringBuffer.append(charAt2);
                charAt = charAt2;
            }
        }
        return stringBuffer.toString();
    }

    private static ASN1ObjectIdentifier[] toOIDArray(Vector vector) {
        ASN1ObjectIdentifier[] aSN1ObjectIdentifierArr = new ASN1ObjectIdentifier[vector.size()];
        for (int i = 0; i != aSN1ObjectIdentifierArr.length; i++) {
            aSN1ObjectIdentifierArr[i] = (ASN1ObjectIdentifier) vector.elementAt(i);
        }
        return aSN1ObjectIdentifierArr;
    }

    private static String[] toValueArray(Vector vector) {
        String[] strArr = new String[vector.size()];
        for (int i = 0; i != strArr.length; i++) {
            strArr[i] = (String) vector.elementAt(i);
        }
        return strArr;
    }

    private static String unescape(String str) {
        int i;
        if (str.length() == 0 || (str.indexOf(92) < 0 && str.indexOf(34) < 0)) {
            return str.trim();
        }
        char[] charArray = str.toCharArray();
        StringBuffer stringBuffer = new StringBuffer(str.length());
        if (charArray[0] == '\\' && charArray[1] == '#') {
            i = 2;
            stringBuffer.append("\\#");
        } else {
            i = 0;
        }
        boolean z = false;
        int i2 = 0;
        boolean z2 = false;
        boolean z3 = false;
        char c = 0;
        while (i != charArray.length) {
            char c2 = charArray[i];
            if (c2 != ' ') {
                z3 = true;
            }
            if (c2 == '\"') {
                if (!z) {
                    z2 = !z2;
                }
                stringBuffer.append(c2);
            } else if (c2 == '\\' && !z && !z2) {
                i2 = stringBuffer.length();
                z = true;
                i++;
            } else if (c2 != ' ' || z || z3) {
                if (z && isHexDigit(c2)) {
                    if (c != 0) {
                        stringBuffer.append((char) ((convertHex(c) * 16) + convertHex(c2)));
                        z = false;
                        c = 0;
                    } else {
                        c = c2;
                    }
                    i++;
                }
                stringBuffer.append(c2);
            } else {
                i++;
            }
            z = false;
            i++;
        }
        if (stringBuffer.length() > 0) {
            while (stringBuffer.charAt(stringBuffer.length() - 1) == ' ' && i2 != stringBuffer.length() - 1) {
                stringBuffer.setLength(stringBuffer.length() - 1);
            }
        }
        return stringBuffer.toString();
    }

    public static ASN1Encodable valueFromHexString(String str, int i) throws IOException {
        byte[] bArr = new byte[((str.length() - i) / 2)];
        for (int i2 = 0; i2 != bArr.length; i2++) {
            int i3 = (i2 * 2) + i;
            char charAt = str.charAt(i3);
            int convertHex = convertHex(charAt) << 4;
            bArr[i2] = (byte) (convertHex(str.charAt(i3 + 1)) | convertHex);
        }
        return ASN1Primitive.fromByteArray(bArr);
    }

    public static String valueToString(ASN1Encodable aSN1Encodable) {
        int i;
        StringBuffer stringBuffer = new StringBuffer();
        int i2 = 0;
        if (!(aSN1Encodable instanceof ASN1String) || (aSN1Encodable instanceof DERUniversalString)) {
            try {
                stringBuffer.append('#');
                stringBuffer.append(Hex.toHexString(aSN1Encodable.toASN1Primitive().getEncoded(ASN1Encoding.DER)));
            } catch (IOException e) {
                throw new IllegalArgumentException("Other value has no encoded form");
            }
        } else {
            String string = ((ASN1String) aSN1Encodable).getString();
            if (string.length() > 0 && string.charAt(0) == '#') {
                stringBuffer.append('\\');
            }
            stringBuffer.append(string);
        }
        int length = stringBuffer.length();
        int i3 = 2;
        if (!(stringBuffer.length() >= 2 && stringBuffer.charAt(0) == '\\' && stringBuffer.charAt(1) == '#')) {
            i3 = 0;
        }
        while (true) {
            String str = "\\";
            if (i != length) {
                char charAt = stringBuffer.charAt(i);
                if (!(charAt == '\"' || charAt == '\\' || charAt == '+' || charAt == ',')) {
                    switch (charAt) {
                        case ';':
                        case '<':
                        case '=':
                        case '>':
                            break;
                        default:
                            i++;
                            continue;
                    }
                }
                stringBuffer.insert(i, str);
                i += 2;
                length++;
            } else {
                if (stringBuffer.length() > 0) {
                    while (stringBuffer.length() > i2 && stringBuffer.charAt(i2) == ' ') {
                        stringBuffer.insert(i2, str);
                        i2 += 2;
                    }
                }
                int length2 = stringBuffer.length() - 1;
                while (length2 >= 0 && stringBuffer.charAt(length2) == ' ') {
                    stringBuffer.insert(length2, '\\');
                    length2--;
                }
                return stringBuffer.toString();
            }
        }
    }
}
