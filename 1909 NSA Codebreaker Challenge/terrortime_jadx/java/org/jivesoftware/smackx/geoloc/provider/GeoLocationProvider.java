package org.jivesoftware.smackx.geoloc.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.geoloc.packet.GeoLocation;

public class GeoLocationProvider extends ExtensionElementProvider<GeoLocation> {
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00b2, code lost:
        if (r4.equals("area") != false) goto L_0x0138;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smackx.geoloc.packet.GeoLocation parse(org.xmlpull.v1.XmlPullParser r8, int r9) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException, java.text.ParseException, java.net.URISyntaxException {
        /*
            r7 = this;
            org.jivesoftware.smackx.geoloc.packet.GeoLocation$Builder r0 = org.jivesoftware.smackx.geoloc.packet.GeoLocation.builder()
        L_0x0004:
            int r1 = r8.next()
            r2 = 3
            r3 = 2
            if (r1 == r3) goto L_0x001c
            if (r1 == r2) goto L_0x0010
            goto L_0x022b
        L_0x0010:
            int r2 = r8.getDepth()
            if (r2 != r9) goto L_0x022b
            org.jivesoftware.smackx.geoloc.packet.GeoLocation r1 = r0.build()
            return r1
        L_0x001c:
            java.lang.String r4 = r8.getName()
            r5 = -1
            int r6 = r4.hashCode()
            switch(r6) {
                case -2131707655: goto L_0x012d;
                case -1724546052: goto L_0x0122;
                case -1476113789: goto L_0x0118;
                case -1430646092: goto L_0x010e;
                case -934795532: goto L_0x0103;
                case -891990013: goto L_0x00f8;
                case -234326098: goto L_0x00ee;
                case 96681: goto L_0x00e4;
                case 106911: goto L_0x00d9;
                case 107339: goto L_0x00ce;
                case 115369: goto L_0x00c2;
                case 116076: goto L_0x00b6;
                case 3002509: goto L_0x00ac;
                case 3506395: goto L_0x00a0;
                case 3556653: goto L_0x0094;
                case 55126294: goto L_0x0088;
                case 95357039: goto L_0x007c;
                case 96784904: goto L_0x0070;
                case 97526796: goto L_0x0064;
                case 109641799: goto L_0x0058;
                case 697727394: goto L_0x004d;
                case 957831062: goto L_0x0042;
                case 1900805475: goto L_0x0036;
                case 2012106040: goto L_0x002a;
                default: goto L_0x0028;
            }
        L_0x0028:
            goto L_0x0137
        L_0x002a:
            java.lang.String r2 = "postalcode"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 15
            goto L_0x0138
        L_0x0036:
            java.lang.String r2 = "locality"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 13
            goto L_0x0138
        L_0x0042:
            java.lang.String r2 = "country"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 6
            goto L_0x0138
        L_0x004d:
            java.lang.String r2 = "altaccuracy"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = r3
            goto L_0x0138
        L_0x0058:
            java.lang.String r2 = "speed"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 18
            goto L_0x0138
        L_0x0064:
            java.lang.String r2 = "floor"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 11
            goto L_0x0138
        L_0x0070:
            java.lang.String r2 = "error"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 10
            goto L_0x0138
        L_0x007c:
            java.lang.String r2 = "datum"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 8
            goto L_0x0138
        L_0x0088:
            java.lang.String r2 = "timestamp"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 21
            goto L_0x0138
        L_0x0094:
            java.lang.String r2 = "text"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 20
            goto L_0x0138
        L_0x00a0:
            java.lang.String r2 = "room"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 17
            goto L_0x0138
        L_0x00ac:
            java.lang.String r3 = "area"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x0028
            goto L_0x0138
        L_0x00b6:
            java.lang.String r2 = "uri"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 23
            goto L_0x0138
        L_0x00c2:
            java.lang.String r2 = "tzo"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 22
            goto L_0x0138
        L_0x00ce:
            java.lang.String r2 = "lon"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 14
            goto L_0x0138
        L_0x00d9:
            java.lang.String r2 = "lat"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 12
            goto L_0x0138
        L_0x00e4:
            java.lang.String r2 = "alt"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 1
            goto L_0x0138
        L_0x00ee:
            java.lang.String r2 = "bearing"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 4
            goto L_0x0138
        L_0x00f8:
            java.lang.String r2 = "street"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 19
            goto L_0x0138
        L_0x0103:
            java.lang.String r2 = "region"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 16
            goto L_0x0138
        L_0x010e:
            java.lang.String r2 = "building"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 5
            goto L_0x0138
        L_0x0118:
            java.lang.String r2 = "countrycode"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 7
            goto L_0x0138
        L_0x0122:
            java.lang.String r2 = "description"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 9
            goto L_0x0138
        L_0x012d:
            java.lang.String r2 = "accuracy"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0028
            r2 = 0
            goto L_0x0138
        L_0x0137:
            r2 = r5
        L_0x0138:
            switch(r2) {
                case 0: goto L_0x021e;
                case 1: goto L_0x0212;
                case 2: goto L_0x0206;
                case 3: goto L_0x01fe;
                case 4: goto L_0x01f2;
                case 5: goto L_0x01ea;
                case 6: goto L_0x01e2;
                case 7: goto L_0x01da;
                case 8: goto L_0x01d2;
                case 9: goto L_0x01ca;
                case 10: goto L_0x01be;
                case 11: goto L_0x01b5;
                case 12: goto L_0x01a8;
                case 13: goto L_0x019f;
                case 14: goto L_0x0192;
                case 15: goto L_0x0189;
                case 16: goto L_0x0180;
                case 17: goto L_0x0177;
                case 18: goto L_0x016a;
                case 19: goto L_0x0161;
                case 20: goto L_0x0158;
                case 21: goto L_0x014f;
                case 22: goto L_0x0146;
                case 23: goto L_0x013d;
                default: goto L_0x013b;
            }
        L_0x013b:
            goto L_0x022a
        L_0x013d:
            java.net.URI r2 = org.jivesoftware.smack.util.ParserUtils.getUriFromNextText(r8)
            r0.setUri(r2)
            goto L_0x022a
        L_0x0146:
            java.lang.String r2 = r8.nextText()
            r0.setTzo(r2)
            goto L_0x022a
        L_0x014f:
            java.util.Date r2 = org.jivesoftware.smack.util.ParserUtils.getDateFromNextText(r8)
            r0.setTimestamp(r2)
            goto L_0x022a
        L_0x0158:
            java.lang.String r2 = r8.nextText()
            r0.setText(r2)
            goto L_0x022a
        L_0x0161:
            java.lang.String r2 = r8.nextText()
            r0.setStreet(r2)
            goto L_0x022a
        L_0x016a:
            double r2 = org.jivesoftware.smack.util.ParserUtils.getDoubleFromNextText(r8)
            java.lang.Double r2 = java.lang.Double.valueOf(r2)
            r0.setSpeed(r2)
            goto L_0x022a
        L_0x0177:
            java.lang.String r2 = r8.nextText()
            r0.setRoom(r2)
            goto L_0x022a
        L_0x0180:
            java.lang.String r2 = r8.nextText()
            r0.setRegion(r2)
            goto L_0x022a
        L_0x0189:
            java.lang.String r2 = r8.nextText()
            r0.setPostalcode(r2)
            goto L_0x022a
        L_0x0192:
            double r2 = org.jivesoftware.smack.util.ParserUtils.getDoubleFromNextText(r8)
            java.lang.Double r2 = java.lang.Double.valueOf(r2)
            r0.setLon(r2)
            goto L_0x022a
        L_0x019f:
            java.lang.String r2 = r8.nextText()
            r0.setLocality(r2)
            goto L_0x022a
        L_0x01a8:
            double r2 = org.jivesoftware.smack.util.ParserUtils.getDoubleFromNextText(r8)
            java.lang.Double r2 = java.lang.Double.valueOf(r2)
            r0.setLat(r2)
            goto L_0x022a
        L_0x01b5:
            java.lang.String r2 = r8.nextText()
            r0.setFloor(r2)
            goto L_0x022a
        L_0x01be:
            double r2 = org.jivesoftware.smack.util.ParserUtils.getDoubleFromNextText(r8)
            java.lang.Double r2 = java.lang.Double.valueOf(r2)
            r0.setError(r2)
            goto L_0x022a
        L_0x01ca:
            java.lang.String r2 = r8.nextText()
            r0.setDescription(r2)
            goto L_0x022a
        L_0x01d2:
            java.lang.String r2 = r8.nextText()
            r0.setDatum(r2)
            goto L_0x022a
        L_0x01da:
            java.lang.String r2 = r8.nextText()
            r0.setCountryCode(r2)
            goto L_0x022a
        L_0x01e2:
            java.lang.String r2 = r8.nextText()
            r0.setCountry(r2)
            goto L_0x022a
        L_0x01ea:
            java.lang.String r2 = r8.nextText()
            r0.setBuilding(r2)
            goto L_0x022a
        L_0x01f2:
            double r2 = org.jivesoftware.smack.util.ParserUtils.getDoubleFromNextText(r8)
            java.lang.Double r2 = java.lang.Double.valueOf(r2)
            r0.setBearing(r2)
            goto L_0x022a
        L_0x01fe:
            java.lang.String r2 = r8.nextText()
            r0.setArea(r2)
            goto L_0x022a
        L_0x0206:
            double r2 = org.jivesoftware.smack.util.ParserUtils.getDoubleFromNextText(r8)
            java.lang.Double r2 = java.lang.Double.valueOf(r2)
            r0.setAltAccuracy(r2)
            goto L_0x022a
        L_0x0212:
            double r2 = org.jivesoftware.smack.util.ParserUtils.getDoubleFromNextText(r8)
            java.lang.Double r2 = java.lang.Double.valueOf(r2)
            r0.setAlt(r2)
            goto L_0x022a
        L_0x021e:
            double r2 = org.jivesoftware.smack.util.ParserUtils.getDoubleFromNextText(r8)
            java.lang.Double r2 = java.lang.Double.valueOf(r2)
            r0.setAccuracy(r2)
        L_0x022a:
        L_0x022b:
            goto L_0x0004
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.geoloc.provider.GeoLocationProvider.parse(org.xmlpull.v1.XmlPullParser, int):org.jivesoftware.smackx.geoloc.packet.GeoLocation");
    }
}
