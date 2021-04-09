package org.jivesoftware.smackx.xdata.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransferChild;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.FormField.Option;
import org.jivesoftware.smackx.xdata.FormField.Type;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.xdata.packet.DataForm.Item;
import org.jivesoftware.smackx.xdata.packet.DataForm.ReportedData;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement;
import org.jivesoftware.smackx.xdatavalidation.provider.DataValidationProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class DataFormProvider extends ExtensionElementProvider<DataForm> {
    public static final DataFormProvider INSTANCE = new DataFormProvider();

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x006e, code lost:
        if (r5.equals("item") != false) goto L_0x007c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smackx.xdata.packet.DataForm parse(org.xmlpull.v1.XmlPullParser r10, int r11) throws java.lang.Exception {
        /*
            r9 = this;
            java.lang.String r0 = ""
            java.lang.String r1 = "type"
            java.lang.String r0 = r10.getAttributeValue(r0, r1)
            org.jivesoftware.smackx.xdata.packet.DataForm$Type r0 = org.jivesoftware.smackx.xdata.packet.DataForm.Type.fromString(r0)
            org.jivesoftware.smackx.xdata.packet.DataForm r1 = new org.jivesoftware.smackx.xdata.packet.DataForm
            r1.<init>(r0)
        L_0x0011:
            int r2 = r10.next()
            r3 = 3
            r4 = 2
            if (r2 == r4) goto L_0x0025
            if (r2 == r3) goto L_0x001d
            goto L_0x00cb
        L_0x001d:
            int r3 = r10.getDepth()
            if (r3 != r11) goto L_0x00cb
            return r1
        L_0x0025:
            java.lang.String r5 = r10.getName()
            java.lang.String r6 = r10.getNamespace()
            r7 = -1
            int r8 = r5.hashCode()
            switch(r8) {
                case -427039533: goto L_0x0071;
                case 3242771: goto L_0x0068;
                case 3433103: goto L_0x005e;
                case 97427706: goto L_0x0054;
                case 107944136: goto L_0x004a;
                case 110371416: goto L_0x0040;
                case 757376421: goto L_0x0036;
                default: goto L_0x0035;
            }
        L_0x0035:
            goto L_0x007b
        L_0x0036:
            java.lang.String r3 = "instructions"
            boolean r3 = r5.equals(r3)
            if (r3 == 0) goto L_0x0035
            r3 = 0
            goto L_0x007c
        L_0x0040:
            java.lang.String r3 = "title"
            boolean r3 = r5.equals(r3)
            if (r3 == 0) goto L_0x0035
            r3 = 1
            goto L_0x007c
        L_0x004a:
            java.lang.String r3 = "query"
            boolean r3 = r5.equals(r3)
            if (r3 == 0) goto L_0x0035
            r3 = 5
            goto L_0x007c
        L_0x0054:
            java.lang.String r3 = "field"
            boolean r3 = r5.equals(r3)
            if (r3 == 0) goto L_0x0035
            r3 = r4
            goto L_0x007c
        L_0x005e:
            java.lang.String r3 = "page"
            boolean r3 = r5.equals(r3)
            if (r3 == 0) goto L_0x0035
            r3 = 6
            goto L_0x007c
        L_0x0068:
            java.lang.String r4 = "item"
            boolean r4 = r5.equals(r4)
            if (r4 == 0) goto L_0x0035
            goto L_0x007c
        L_0x0071:
            java.lang.String r3 = "reported"
            boolean r3 = r5.equals(r3)
            if (r3 == 0) goto L_0x0035
            r3 = 4
            goto L_0x007c
        L_0x007b:
            r3 = r7
        L_0x007c:
            switch(r3) {
                case 0: goto L_0x00c2;
                case 1: goto L_0x00ba;
                case 2: goto L_0x00b2;
                case 3: goto L_0x00aa;
                case 4: goto L_0x00a2;
                case 5: goto L_0x0090;
                case 6: goto L_0x0080;
                default: goto L_0x007f;
            }
        L_0x007f:
            goto L_0x00ca
        L_0x0080:
            java.lang.String r3 = "http://jabber.org/protocol/xdata-layout"
            boolean r3 = r6.equals(r3)
            if (r3 == 0) goto L_0x00ca
            org.jivesoftware.smackx.xdatalayout.packet.DataLayout r3 = org.jivesoftware.smackx.xdatalayout.provider.DataLayoutProvider.parse(r10)
            r1.addExtensionElement(r3)
            goto L_0x00ca
        L_0x0090:
            java.lang.String r3 = "jabber:iq:roster"
            boolean r3 = r6.equals(r3)
            if (r3 == 0) goto L_0x00ca
            org.jivesoftware.smack.roster.provider.RosterPacketProvider r3 = org.jivesoftware.smack.roster.provider.RosterPacketProvider.INSTANCE
            org.jivesoftware.smack.packet.Element r3 = r3.parse(r10)
            r1.addExtensionElement(r3)
            goto L_0x00ca
        L_0x00a2:
            org.jivesoftware.smackx.xdata.packet.DataForm$ReportedData r3 = parseReported(r10)
            r1.setReportedData(r3)
            goto L_0x00ca
        L_0x00aa:
            org.jivesoftware.smackx.xdata.packet.DataForm$Item r3 = parseItem(r10)
            r1.addItem(r3)
            goto L_0x00ca
        L_0x00b2:
            org.jivesoftware.smackx.xdata.FormField r3 = parseField(r10)
            r1.addField(r3)
            goto L_0x00ca
        L_0x00ba:
            java.lang.String r3 = r10.nextText()
            r1.setTitle(r3)
            goto L_0x00ca
        L_0x00c2:
            java.lang.String r3 = r10.nextText()
            r1.addInstruction(r3)
        L_0x00ca:
        L_0x00cb:
            goto L_0x0011
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.xdata.provider.DataFormProvider.parse(org.xmlpull.v1.XmlPullParser, int):org.jivesoftware.smackx.xdata.packet.DataForm");
    }

    private static FormField parseField(XmlPullParser parser) throws XmlPullParserException, IOException {
        FormField formField;
        int initialDepth = parser.getDepth();
        String str = BuildConfig.FLAVOR;
        String var = parser.getAttributeValue(str, "var");
        Type type = Type.fromString(parser.getAttributeValue(str, "type"));
        if (type == Type.fixed) {
            formField = new FormField();
        } else {
            formField = new FormField(var);
            formField.setType(type);
        }
        formField.setLabel(parser.getAttributeValue(str, AnnotatedPrivateKey.LABEL));
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                String namespace = parser.getNamespace();
                char c = 65535;
                switch (name.hashCode()) {
                    case -1421272810:
                        if (name.equals(ValidateElement.ELEMENT)) {
                            c = 4;
                            break;
                        }
                        break;
                    case -1010136971:
                        if (name.equals(Option.ELEMENT)) {
                            c = 3;
                            break;
                        }
                        break;
                    case -393139297:
                        if (name.equals("required")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 3079825:
                        if (name.equals(JingleFileTransferChild.ELEM_DESC)) {
                            c = 0;
                            break;
                        }
                        break;
                    case 111972721:
                        if (name.equals("value")) {
                            c = 1;
                            break;
                        }
                        break;
                }
                if (c == 0) {
                    formField.setDescription(parser.nextText());
                } else if (c == 1) {
                    formField.addValue((CharSequence) parser.nextText());
                } else if (c == 2) {
                    formField.setRequired(true);
                } else if (c == 3) {
                    formField.addOption(parseOption(parser));
                } else if (c == 4 && namespace.equals(ValidateElement.NAMESPACE)) {
                    formField.setValidateElement(DataValidationProvider.parse(parser));
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return formField;
            }
        }
    }

    private static Item parseItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        int initialDepth = parser.getDepth();
        List<FormField> fields = new ArrayList<>();
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                char c = 65535;
                if (name.hashCode() == 97427706 && name.equals(FormField.ELEMENT)) {
                    c = 0;
                }
                if (c == 0) {
                    fields.add(parseField(parser));
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return new Item(fields);
            }
        }
    }

    private static ReportedData parseReported(XmlPullParser parser) throws XmlPullParserException, IOException {
        int initialDepth = parser.getDepth();
        List<FormField> fields = new ArrayList<>();
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                char c = 65535;
                if (name.hashCode() == 97427706 && name.equals(FormField.ELEMENT)) {
                    c = 0;
                }
                if (c == 0) {
                    fields.add(parseField(parser));
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return new ReportedData(fields);
            }
        }
    }

    private static Option parseOption(XmlPullParser parser) throws XmlPullParserException, IOException {
        int initialDepth = parser.getDepth();
        Option option = null;
        String label = parser.getAttributeValue(BuildConfig.FLAVOR, AnnotatedPrivateKey.LABEL);
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                char c = 65535;
                if (name.hashCode() == 111972721 && name.equals("value")) {
                    c = 0;
                }
                if (c == 0) {
                    option = new Option(label, parser.nextText());
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return option;
            }
        }
    }
}
