package org.bouncycastle.asn1.util;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ApplicationSpecific;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1External;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.BERApplicationSpecific;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DERApplicationSpecific;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERGraphicString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERT61String;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.DERVideotexString;
import org.bouncycastle.asn1.DERVisibleString;
import org.bouncycastle.asn1.DLApplicationSpecific;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class ASN1Dump {
    private static final int SAMPLE_SIZE = 32;
    private static final String TAB = "    ";

    static void _dumpAsString(String str, boolean z, ASN1Primitive aSN1Primitive, StringBuffer stringBuffer) {
        String str2;
        StringBuilder sb;
        StringBuilder sb2;
        BigInteger value;
        String str3;
        String time;
        StringBuilder sb3;
        int i;
        String lineSeparator = Strings.lineSeparator();
        boolean z2 = aSN1Primitive instanceof ASN1Sequence;
        String str4 = "NULL";
        String str5 = TAB;
        if (z2) {
            Enumeration objects = ((ASN1Sequence) aSN1Primitive).getObjects();
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str);
            sb4.append(str5);
            String sb5 = sb4.toString();
            stringBuffer.append(str);
            String str6 = aSN1Primitive instanceof BERSequence ? "BER Sequence" : aSN1Primitive instanceof DERSequence ? "DER Sequence" : "Sequence";
            stringBuffer.append(str6);
            while (true) {
                stringBuffer.append(lineSeparator);
                while (objects.hasMoreElements()) {
                    Object nextElement = objects.nextElement();
                    if (nextElement == null || nextElement.equals(DERNull.INSTANCE)) {
                        stringBuffer.append(sb5);
                        stringBuffer.append(str4);
                    } else {
                        _dumpAsString(sb5, z, nextElement instanceof ASN1Primitive ? (ASN1Primitive) nextElement : ((ASN1Encodable) nextElement).toASN1Primitive(), stringBuffer);
                    }
                }
                return;
            }
        }
        if (aSN1Primitive instanceof ASN1TaggedObject) {
            StringBuilder sb6 = new StringBuilder();
            sb6.append(str);
            sb6.append(str5);
            String sb7 = sb6.toString();
            stringBuffer.append(str);
            stringBuffer.append(aSN1Primitive instanceof BERTaggedObject ? "BER Tagged [" : "Tagged [");
            ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) aSN1Primitive;
            stringBuffer.append(Integer.toString(aSN1TaggedObject.getTagNo()));
            stringBuffer.append(']');
            if (!aSN1TaggedObject.isExplicit()) {
                stringBuffer.append(" IMPLICIT ");
            }
            stringBuffer.append(lineSeparator);
            if (aSN1TaggedObject.isEmpty()) {
                stringBuffer.append(sb7);
                stringBuffer.append("EMPTY");
            } else {
                _dumpAsString(sb7, z, aSN1TaggedObject.getObject(), stringBuffer);
                return;
            }
        } else if (aSN1Primitive instanceof ASN1Set) {
            Enumeration objects2 = ((ASN1Set) aSN1Primitive).getObjects();
            StringBuilder sb8 = new StringBuilder();
            sb8.append(str);
            sb8.append(str5);
            String sb9 = sb8.toString();
            stringBuffer.append(str);
            String str7 = aSN1Primitive instanceof BERSet ? "BER Set" : aSN1Primitive instanceof DERSet ? "DER Set" : "Set";
            stringBuffer.append(str7);
            while (true) {
                stringBuffer.append(lineSeparator);
                while (objects2.hasMoreElements()) {
                    Object nextElement2 = objects2.nextElement();
                    if (nextElement2 == null) {
                        stringBuffer.append(sb9);
                        stringBuffer.append(str4);
                    } else {
                        _dumpAsString(sb9, z, nextElement2 instanceof ASN1Primitive ? (ASN1Primitive) nextElement2 : ((ASN1Encodable) nextElement2).toASN1Primitive(), stringBuffer);
                    }
                }
                return;
            }
        } else {
            String str8 = "] ";
            if (aSN1Primitive instanceof ASN1OctetString) {
                ASN1OctetString aSN1OctetString = (ASN1OctetString) aSN1Primitive;
                if (aSN1Primitive instanceof BEROctetString) {
                    sb3 = new StringBuilder();
                    sb3.append(str);
                    sb3.append("BER Constructed Octet String[");
                    i = aSN1OctetString.getOctets().length;
                } else {
                    sb3 = new StringBuilder();
                    sb3.append(str);
                    sb3.append("DER Octet String[");
                    i = aSN1OctetString.getOctets().length;
                }
                sb3.append(i);
                sb3.append(str8);
                stringBuffer.append(sb3.toString());
                if (z) {
                    str2 = dumpBinaryDataAsString(str, aSN1OctetString.getOctets());
                }
            } else {
                String str9 = ")";
                if (aSN1Primitive instanceof ASN1ObjectIdentifier) {
                    sb2 = new StringBuilder();
                    sb2.append(str);
                    sb2.append("ObjectIdentifier(");
                    sb2.append(((ASN1ObjectIdentifier) aSN1Primitive).getId());
                } else if (aSN1Primitive instanceof ASN1Boolean) {
                    sb2 = new StringBuilder();
                    sb2.append(str);
                    sb2.append("Boolean(");
                    sb2.append(((ASN1Boolean) aSN1Primitive).isTrue());
                } else {
                    if (aSN1Primitive instanceof ASN1Integer) {
                        sb2 = new StringBuilder();
                        sb2.append(str);
                        sb2.append("Integer(");
                        value = ((ASN1Integer) aSN1Primitive).getValue();
                    } else if (aSN1Primitive instanceof DERBitString) {
                        DERBitString dERBitString = (DERBitString) aSN1Primitive;
                        StringBuilder sb10 = new StringBuilder();
                        sb10.append(str);
                        sb10.append("DER Bit String[");
                        sb10.append(dERBitString.getBytes().length);
                        sb10.append(", ");
                        sb10.append(dERBitString.getPadBits());
                        sb10.append(str8);
                        stringBuffer.append(sb10.toString());
                        if (z) {
                            str2 = dumpBinaryDataAsString(str, dERBitString.getBytes());
                        }
                    } else {
                        String str10 = ") ";
                        if (aSN1Primitive instanceof DERIA5String) {
                            sb = new StringBuilder();
                            sb.append(str);
                            sb.append("IA5String(");
                            time = ((DERIA5String) aSN1Primitive).getString();
                        } else if (aSN1Primitive instanceof DERUTF8String) {
                            sb = new StringBuilder();
                            sb.append(str);
                            sb.append("UTF8String(");
                            time = ((DERUTF8String) aSN1Primitive).getString();
                        } else if (aSN1Primitive instanceof DERPrintableString) {
                            sb = new StringBuilder();
                            sb.append(str);
                            sb.append("PrintableString(");
                            time = ((DERPrintableString) aSN1Primitive).getString();
                        } else if (aSN1Primitive instanceof DERVisibleString) {
                            sb = new StringBuilder();
                            sb.append(str);
                            sb.append("VisibleString(");
                            time = ((DERVisibleString) aSN1Primitive).getString();
                        } else if (aSN1Primitive instanceof DERBMPString) {
                            sb = new StringBuilder();
                            sb.append(str);
                            sb.append("BMPString(");
                            time = ((DERBMPString) aSN1Primitive).getString();
                        } else if (aSN1Primitive instanceof DERT61String) {
                            sb = new StringBuilder();
                            sb.append(str);
                            sb.append("T61String(");
                            time = ((DERT61String) aSN1Primitive).getString();
                        } else if (aSN1Primitive instanceof DERGraphicString) {
                            sb = new StringBuilder();
                            sb.append(str);
                            sb.append("GraphicString(");
                            time = ((DERGraphicString) aSN1Primitive).getString();
                        } else if (aSN1Primitive instanceof DERVideotexString) {
                            sb = new StringBuilder();
                            sb.append(str);
                            sb.append("VideotexString(");
                            time = ((DERVideotexString) aSN1Primitive).getString();
                        } else if (aSN1Primitive instanceof ASN1UTCTime) {
                            sb = new StringBuilder();
                            sb.append(str);
                            sb.append("UTCTime(");
                            time = ((ASN1UTCTime) aSN1Primitive).getTime();
                        } else if (aSN1Primitive instanceof ASN1GeneralizedTime) {
                            sb = new StringBuilder();
                            sb.append(str);
                            sb.append("GeneralizedTime(");
                            time = ((ASN1GeneralizedTime) aSN1Primitive).getTime();
                        } else {
                            if (aSN1Primitive instanceof BERApplicationSpecific) {
                                str3 = ASN1Encoding.BER;
                            } else if (aSN1Primitive instanceof DERApplicationSpecific) {
                                str3 = ASN1Encoding.DER;
                            } else if (aSN1Primitive instanceof DLApplicationSpecific) {
                                str3 = BuildConfig.FLAVOR;
                            } else if (aSN1Primitive instanceof ASN1Enumerated) {
                                ASN1Enumerated aSN1Enumerated = (ASN1Enumerated) aSN1Primitive;
                                sb2 = new StringBuilder();
                                sb2.append(str);
                                sb2.append("DER Enumerated(");
                                value = aSN1Enumerated.getValue();
                            } else if (aSN1Primitive instanceof ASN1External) {
                                ASN1External aSN1External = (ASN1External) aSN1Primitive;
                                StringBuilder sb11 = new StringBuilder();
                                sb11.append(str);
                                sb11.append("External ");
                                sb11.append(lineSeparator);
                                stringBuffer.append(sb11.toString());
                                StringBuilder sb12 = new StringBuilder();
                                sb12.append(str);
                                sb12.append(str5);
                                String sb13 = sb12.toString();
                                if (aSN1External.getDirectReference() != null) {
                                    StringBuilder sb14 = new StringBuilder();
                                    sb14.append(sb13);
                                    sb14.append("Direct Reference: ");
                                    sb14.append(aSN1External.getDirectReference().getId());
                                    sb14.append(lineSeparator);
                                    stringBuffer.append(sb14.toString());
                                }
                                if (aSN1External.getIndirectReference() != null) {
                                    StringBuilder sb15 = new StringBuilder();
                                    sb15.append(sb13);
                                    sb15.append("Indirect Reference: ");
                                    sb15.append(aSN1External.getIndirectReference().toString());
                                    sb15.append(lineSeparator);
                                    stringBuffer.append(sb15.toString());
                                }
                                if (aSN1External.getDataValueDescriptor() != null) {
                                    _dumpAsString(sb13, z, aSN1External.getDataValueDescriptor(), stringBuffer);
                                }
                                StringBuilder sb16 = new StringBuilder();
                                sb16.append(sb13);
                                sb16.append("Encoding: ");
                                sb16.append(aSN1External.getEncoding());
                                sb16.append(lineSeparator);
                                stringBuffer.append(sb16.toString());
                                _dumpAsString(sb13, z, aSN1External.getExternalContent(), stringBuffer);
                                return;
                            } else {
                                sb = new StringBuilder();
                                sb.append(str);
                                sb.append(aSN1Primitive.toString());
                                sb.append(lineSeparator);
                                str2 = sb.toString();
                            }
                            str2 = outputApplicationSpecific(str3, str, z, aSN1Primitive, lineSeparator);
                        }
                        sb.append(time);
                        sb.append(str10);
                        sb.append(lineSeparator);
                        str2 = sb.toString();
                    }
                    sb2.append(value);
                }
                sb.append(str9);
                sb.append(lineSeparator);
                str2 = sb.toString();
            }
            stringBuffer.append(str2);
            return;
        }
        stringBuffer.append(lineSeparator);
    }

    private static String calculateAscString(byte[] bArr, int i, int i2) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i3 = i; i3 != i + i2; i3++) {
            if (bArr[i3] >= 32 && bArr[i3] <= 126) {
                stringBuffer.append((char) bArr[i3]);
            }
        }
        return stringBuffer.toString();
    }

    public static String dumpAsString(Object obj) {
        return dumpAsString(obj, false);
    }

    public static String dumpAsString(Object obj, boolean z) {
        ASN1Primitive aSN1Primitive;
        StringBuffer stringBuffer = new StringBuffer();
        boolean z2 = obj instanceof ASN1Primitive;
        String str = BuildConfig.FLAVOR;
        if (z2) {
            aSN1Primitive = (ASN1Primitive) obj;
        } else if (obj instanceof ASN1Encodable) {
            aSN1Primitive = ((ASN1Encodable) obj).toASN1Primitive();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("unknown object type ");
            sb.append(obj.toString());
            return sb.toString();
        }
        _dumpAsString(str, z, aSN1Primitive, stringBuffer);
        return stringBuffer.toString();
    }

    private static String dumpBinaryDataAsString(String str, byte[] bArr) {
        String calculateAscString;
        String lineSeparator = Strings.lineSeparator();
        StringBuffer stringBuffer = new StringBuffer();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        String str2 = TAB;
        sb.append(str2);
        String sb2 = sb.toString();
        stringBuffer.append(lineSeparator);
        for (int i = 0; i < bArr.length; i += 32) {
            int length = bArr.length - i;
            stringBuffer.append(sb2);
            if (length > 32) {
                stringBuffer.append(Strings.fromByteArray(Hex.encode(bArr, i, 32)));
                stringBuffer.append(str2);
                calculateAscString = calculateAscString(bArr, i, 32);
            } else {
                stringBuffer.append(Strings.fromByteArray(Hex.encode(bArr, i, bArr.length - i)));
                for (int length2 = bArr.length - i; length2 != 32; length2++) {
                    stringBuffer.append("  ");
                }
                stringBuffer.append(str2);
                calculateAscString = calculateAscString(bArr, i, bArr.length - i);
            }
            stringBuffer.append(calculateAscString);
            stringBuffer.append(lineSeparator);
        }
        return stringBuffer.toString();
    }

    private static String outputApplicationSpecific(String str, String str2, boolean z, ASN1Primitive aSN1Primitive, String str3) {
        ASN1ApplicationSpecific instance = ASN1ApplicationSpecific.getInstance(aSN1Primitive);
        StringBuffer stringBuffer = new StringBuffer();
        String str4 = " ApplicationSpecific[";
        if (instance.isConstructed()) {
            try {
                ASN1Sequence instance2 = ASN1Sequence.getInstance(instance.getObject(16));
                StringBuilder sb = new StringBuilder();
                sb.append(str2);
                sb.append(str);
                sb.append(str4);
                sb.append(instance.getApplicationTag());
                sb.append("]");
                sb.append(str3);
                stringBuffer.append(sb.toString());
                Enumeration objects = instance2.getObjects();
                while (objects.hasMoreElements()) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str2);
                    sb2.append(TAB);
                    _dumpAsString(sb2.toString(), z, (ASN1Primitive) objects.nextElement(), stringBuffer);
                }
            } catch (IOException e) {
                stringBuffer.append(e);
            }
            return stringBuffer.toString();
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str2);
        sb3.append(str);
        sb3.append(str4);
        sb3.append(instance.getApplicationTag());
        sb3.append("] (");
        sb3.append(Strings.fromByteArray(Hex.encode(instance.getContents())));
        sb3.append(")");
        sb3.append(str3);
        return sb3.toString();
    }
}
