package org.jivesoftware.smackx.xdatavalidation.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.util.logging.Logger;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement.BasicValidateElement;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement.ListRange;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement.OpenValidateElement;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement.RangeValidateElement;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement.RegexValidateElement;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class DataValidationProvider {
    private static final Logger LOGGER = Logger.getLogger(DataValidationProvider.class.getName());

    public static ValidateElement parse(XmlPullParser parser) throws XmlPullParserException, IOException {
        int initialDepth = parser.getDepth();
        String str = BuildConfig.FLAVOR;
        String dataType = parser.getAttributeValue(str, "datatype");
        ValidateElement dataValidation = null;
        ListRange listRange = null;
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                char c = 65535;
                switch (name.hashCode()) {
                    case -725250226:
                        if (name.equals(ListRange.ELEMENT)) {
                            c = 4;
                            break;
                        }
                        break;
                    case 3417674:
                        if (name.equals("open")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 93508654:
                        if (name.equals(BasicValidateElement.METHOD)) {
                            c = 1;
                            break;
                        }
                        break;
                    case 108280125:
                        if (name.equals("range")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 108392519:
                        if (name.equals(RegexValidateElement.METHOD)) {
                            c = 3;
                            break;
                        }
                        break;
                }
                if (c == 0) {
                    dataValidation = new OpenValidateElement(dataType);
                } else if (c != 1) {
                    String str2 = "max";
                    String str3 = "min";
                    if (c == 2) {
                        dataValidation = new RangeValidateElement(dataType, parser.getAttributeValue(str, str3), parser.getAttributeValue(str, str2));
                    } else if (c == 3) {
                        dataValidation = new RegexValidateElement(dataType, parser.nextText());
                    } else if (c == 4) {
                        Long min = ParserUtils.getLongAttribute(parser, str3);
                        Long max = ParserUtils.getLongAttribute(parser, str2);
                        if (min == null && max == null) {
                            LOGGER.fine("Ignoring list-range element without min or max attribute");
                        } else {
                            listRange = new ListRange(min, max);
                        }
                    }
                } else {
                    dataValidation = new BasicValidateElement(dataType);
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                if (dataValidation == null) {
                    dataValidation = new BasicValidateElement(dataType);
                }
                dataValidation.setListRange(listRange);
                return dataValidation;
            }
        }
    }
}
