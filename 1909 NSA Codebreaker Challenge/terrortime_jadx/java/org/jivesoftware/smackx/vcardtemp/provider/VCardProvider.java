package org.jivesoftware.smackx.vcardtemp.provider;

import java.io.IOException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class VCardProvider extends IQProvider<VCard> {
    private static final String[] ADR = {"POSTAL", "PARCEL", "DOM", "INTL", "PREF", "POBOX", "EXTADR", "STREET", "LOCALITY", "REGION", "PCODE", "CTRY", "FF"};
    private static final String[] TEL = {"VOICE", "FAX", "PAGER", "MSG", "CELL", "VIDEO", "BBS", "MODEM", "ISDN", "PCS", "PREF"};

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0055, code lost:
        if (r1.equals("EMAIL") != false) goto L_0x008b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smackx.vcardtemp.packet.VCard parse(org.xmlpull.v1.XmlPullParser r9, int r10) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException, org.jivesoftware.smack.SmackException {
        /*
            r8 = this;
            org.jivesoftware.smackx.vcardtemp.packet.VCard r0 = new org.jivesoftware.smackx.vcardtemp.packet.VCard
            r0.<init>()
            r1 = 0
        L_0x0006:
            int r2 = r9.next()
            r3 = 4
            r4 = 3
            r5 = 2
            if (r2 == r5) goto L_0x002e
            if (r2 == r4) goto L_0x0026
            if (r2 == r3) goto L_0x0015
            goto L_0x00b8
        L_0x0015:
            int r3 = r10 + 1
            int r4 = r9.getDepth()
            if (r3 != r4) goto L_0x00b8
            java.lang.String r3 = r9.getText()
            r0.setField(r1, r3)
            goto L_0x00b8
        L_0x0026:
            int r3 = r9.getDepth()
            if (r3 != r10) goto L_0x00b8
            return r0
        L_0x002e:
            java.lang.String r1 = r9.getName()
            r6 = -1
            int r7 = r1.hashCode()
            switch(r7) {
                case -370243905: goto L_0x0080;
                case 78: goto L_0x0076;
                case 64655: goto L_0x006c;
                case 78532: goto L_0x0062;
                case 82939: goto L_0x0058;
                case 66081660: goto L_0x004f;
                case 76105234: goto L_0x0045;
                case 853317742: goto L_0x003b;
                default: goto L_0x003a;
            }
        L_0x003a:
            goto L_0x008a
        L_0x003b:
            java.lang.String r3 = "NICKNAME"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x003a
            r3 = 5
            goto L_0x008b
        L_0x0045:
            java.lang.String r3 = "PHOTO"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x003a
            r3 = 7
            goto L_0x008b
        L_0x004f:
            java.lang.String r4 = "EMAIL"
            boolean r4 = r1.equals(r4)
            if (r4 == 0) goto L_0x003a
            goto L_0x008b
        L_0x0058:
            java.lang.String r3 = "TEL"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x003a
            r3 = r5
            goto L_0x008b
        L_0x0062:
            java.lang.String r3 = "ORG"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x003a
            r3 = 1
            goto L_0x008b
        L_0x006c:
            java.lang.String r3 = "ADR"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x003a
            r3 = r4
            goto L_0x008b
        L_0x0076:
            java.lang.String r3 = "N"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x003a
            r3 = 0
            goto L_0x008b
        L_0x0080:
            java.lang.String r3 = "JABBERID"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x003a
            r3 = 6
            goto L_0x008b
        L_0x008a:
            r3 = r6
        L_0x008b:
            switch(r3) {
                case 0: goto L_0x00b3;
                case 1: goto L_0x00af;
                case 2: goto L_0x00ab;
                case 3: goto L_0x00a7;
                case 4: goto L_0x00a3;
                case 5: goto L_0x009b;
                case 6: goto L_0x0093;
                case 7: goto L_0x008f;
                default: goto L_0x008e;
            }
        L_0x008e:
            goto L_0x00b7
        L_0x008f:
            parsePhoto(r9, r0)
            goto L_0x00b7
        L_0x0093:
            java.lang.String r3 = r9.nextText()
            r0.setJabberId(r3)
            goto L_0x00b7
        L_0x009b:
            java.lang.String r3 = r9.nextText()
            r0.setNickName(r3)
            goto L_0x00b7
        L_0x00a3:
            parseEmail(r9, r0)
            goto L_0x00b7
        L_0x00a7:
            parseAddress(r9, r0)
            goto L_0x00b7
        L_0x00ab:
            parseTel(r9, r0)
            goto L_0x00b7
        L_0x00af:
            parseOrg(r9, r0)
            goto L_0x00b7
        L_0x00b3:
            parseName(r9, r0)
        L_0x00b7:
        L_0x00b8:
            goto L_0x0006
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.vcardtemp.provider.VCardProvider.parse(org.xmlpull.v1.XmlPullParser, int):org.jivesoftware.smackx.vcardtemp.packet.VCard");
    }

    private static void parseAddress(XmlPullParser parser, VCard vCard) throws XmlPullParserException, IOException {
        int initialDepth = parser.getDepth();
        boolean isWork = true;
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                if ("HOME".equals(name)) {
                    isWork = false;
                } else {
                    for (String adr : ADR) {
                        if (adr.equals(name)) {
                            if (isWork) {
                                vCard.setAddressFieldWork(name, parser.nextText());
                            } else {
                                vCard.setAddressFieldHome(name, parser.nextText());
                            }
                        }
                    }
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return;
            }
        }
    }

    private static void parseTel(XmlPullParser parser, VCard vCard) throws XmlPullParserException, IOException {
        int initialDepth = parser.getDepth();
        boolean isWork = true;
        String telLabel = null;
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                if ("HOME".equals(name)) {
                    isWork = false;
                } else if ("NUMBER".equals(name)) {
                    if (StringUtils.isNullOrEmpty((CharSequence) telLabel)) {
                        telLabel = "VOICE";
                    }
                    if (isWork) {
                        vCard.setPhoneWork(telLabel, parser.nextText());
                    } else {
                        vCard.setPhoneHome(telLabel, parser.nextText());
                    }
                } else {
                    for (String tel : TEL) {
                        if (tel.equals(name)) {
                            telLabel = name;
                        }
                    }
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return;
            }
        }
    }

    private static void parseOrg(XmlPullParser parser, VCard vCard) throws XmlPullParserException, IOException {
        int initialDepth = parser.getDepth();
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                char c = 65535;
                int hashCode = name.hashCode();
                if (hashCode != -486104241) {
                    if (hashCode == -485883320 && name.equals("ORGUNIT")) {
                        c = 1;
                    }
                } else if (name.equals("ORGNAME")) {
                    c = 0;
                }
                if (c == 0) {
                    vCard.setOrganization(parser.nextText());
                } else if (c == 1) {
                    vCard.setOrganizationUnit(parser.nextText());
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return;
            }
        }
    }

    private static void parseEmail(XmlPullParser parser, VCard vCard) throws XmlPullParserException, IOException {
        int initialDepth = parser.getDepth();
        boolean isWork = false;
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                char c = 65535;
                int hashCode = name.hashCode();
                if (hashCode != -1782700506) {
                    if (hashCode == 2670353 && name.equals("WORK")) {
                        c = 0;
                    }
                } else if (name.equals("USERID")) {
                    c = 1;
                }
                if (c == 0) {
                    isWork = true;
                } else if (c == 1) {
                    if (isWork) {
                        vCard.setEmailWork(parser.nextText());
                    } else {
                        vCard.setEmailHome(parser.nextText());
                    }
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return;
            }
        }
    }

    private static void parseName(XmlPullParser parser, VCard vCard) throws XmlPullParserException, IOException {
        int initialDepth = parser.getDepth();
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                char c = 65535;
                switch (name.hashCode()) {
                    case -2021012075:
                        if (name.equals("MIDDLE")) {
                            c = 2;
                            break;
                        }
                        break;
                    case -1926781294:
                        if (name.equals("PREFIX")) {
                            c = 3;
                            break;
                        }
                        break;
                    case -1838093487:
                        if (name.equals("SUFFIX")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 67829597:
                        if (name.equals("GIVEN")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 2066435940:
                        if (name.equals("FAMILY")) {
                            c = 0;
                            break;
                        }
                        break;
                }
                if (c == 0) {
                    vCard.setLastName(parser.nextText());
                } else if (c == 1) {
                    vCard.setFirstName(parser.nextText());
                } else if (c == 2) {
                    vCard.setMiddleName(parser.nextText());
                } else if (c == 3) {
                    vCard.setPrefix(parser.nextText());
                } else if (c == 4) {
                    vCard.setSuffix(parser.nextText());
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return;
            }
        }
    }

    private static void parsePhoto(XmlPullParser parser, VCard vCard) throws XmlPullParserException, IOException {
        int initialDepth = parser.getDepth();
        String binval = null;
        String mimetype = null;
        while (true) {
            int eventType = parser.next();
            if (eventType != 2) {
                if (eventType == 3 && parser.getDepth() == initialDepth) {
                    break;
                }
            } else {
                String name = parser.getName();
                char c = 65535;
                int hashCode = name.hashCode();
                if (hashCode != 2590522) {
                    if (hashCode == 1959349434 && name.equals("BINVAL")) {
                        c = 0;
                    }
                } else if (name.equals("TYPE")) {
                    c = 1;
                }
                if (c == 0) {
                    binval = parser.nextText();
                } else if (c == 1) {
                    mimetype = parser.nextText();
                }
            }
        }
        if (binval != null && mimetype != null) {
            vCard.setAvatar(binval, mimetype);
        }
    }
}
