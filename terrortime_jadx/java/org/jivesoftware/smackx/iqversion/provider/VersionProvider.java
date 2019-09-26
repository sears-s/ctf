package org.jivesoftware.smackx.iqversion.provider;

import java.io.IOException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.iqversion.packet.Version;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class VersionProvider extends IQProvider<Version> {
    public Version parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
        String name = null;
        String version = null;
        String os = null;
        while (true) {
            int eventType = parser.next();
            if (eventType != 2) {
                if (eventType == 3 && parser.getDepth() == initialDepth && parser.getName().equals("query")) {
                    break;
                }
            } else {
                String tagName = parser.getName();
                char c = 65535;
                int hashCode = tagName.hashCode();
                if (hashCode != 3556) {
                    if (hashCode != 3373707) {
                        if (hashCode == 351608024 && tagName.equals("version")) {
                            c = 1;
                        }
                    } else if (tagName.equals("name")) {
                        c = 0;
                    }
                } else if (tagName.equals("os")) {
                    c = 2;
                }
                if (c == 0) {
                    name = parser.nextText();
                } else if (c == 1) {
                    version = parser.nextText();
                } else if (c == 2) {
                    os = parser.nextText();
                }
            }
        }
        if (name == null && version == null && os == null) {
            return new Version();
        }
        return new Version(name, version, os);
    }
}
