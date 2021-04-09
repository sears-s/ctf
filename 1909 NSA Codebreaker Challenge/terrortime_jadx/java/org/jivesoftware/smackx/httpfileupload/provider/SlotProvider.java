package org.jivesoftware.smackx.httpfileupload.provider;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.httpfileupload.HttpFileUploadManager;
import org.jivesoftware.smackx.httpfileupload.UploadService.Version;
import org.jivesoftware.smackx.httpfileupload.element.Slot;
import org.jivesoftware.smackx.httpfileupload.element.Slot_V0_2;
import org.jivesoftware.smackx.shim.packet.Header;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SlotProvider extends IQProvider<Slot> {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    /* renamed from: org.jivesoftware.smackx.httpfileupload.provider.SlotProvider$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smackx$httpfileupload$UploadService$Version = new int[Version.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smackx$httpfileupload$UploadService$Version[Version.v0_2.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$httpfileupload$UploadService$Version[Version.v0_3.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public static final class PutElement_V0_4_Content {
        /* access modifiers changed from: private */
        public final Map<String, String> headers;
        /* access modifiers changed from: private */
        public final URL putUrl;

        /* synthetic */ PutElement_V0_4_Content(URL x0, Map x1, AnonymousClass1 x2) {
            this(x0, x1);
        }

        private PutElement_V0_4_Content(URL putUrl2, Map<String, String> headers2) {
            this.putUrl = putUrl2;
            this.headers = headers2;
        }

        public URL getPutUrl() {
            return this.putUrl;
        }

        public Map<String, String> getHeaders() {
            return this.headers;
        }
    }

    public Slot parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        String getUrlString;
        Version version = HttpFileUploadManager.namespaceToVersion(parser.getNamespace());
        URL putUrl = null;
        URL getUrl = null;
        PutElement_V0_4_Content putElementV04Content = null;
        while (true) {
            int event = parser.next();
            if (event == 2) {
                String name = parser.getName();
                char c = 65535;
                int hashCode = name.hashCode();
                if (hashCode != 102230) {
                    if (hashCode == 111375 && name.equals("put")) {
                        c = 0;
                    }
                } else if (name.equals("get")) {
                    c = 1;
                }
                if (c == 0) {
                    int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smackx$httpfileupload$UploadService$Version[version.ordinal()];
                    if (i == 1) {
                        putUrl = new URL(parser.nextText());
                    } else if (i == 2) {
                        putElementV04Content = parsePutElement_V0_4(parser);
                    } else {
                        throw new AssertionError();
                    }
                } else if (c != 1) {
                    continue;
                } else {
                    int i2 = AnonymousClass1.$SwitchMap$org$jivesoftware$smackx$httpfileupload$UploadService$Version[version.ordinal()];
                    if (i2 == 1) {
                        getUrlString = parser.nextText();
                    } else if (i2 == 2) {
                        getUrlString = parser.getAttributeValue(null, "url");
                    } else {
                        throw new AssertionError();
                    }
                    getUrl = new URL(getUrlString);
                }
            } else if (event == 3 && parser.getDepth() == initialDepth) {
                int i3 = AnonymousClass1.$SwitchMap$org$jivesoftware$smackx$httpfileupload$UploadService$Version[version.ordinal()];
                if (i3 == 1) {
                    return new Slot_V0_2(putUrl, getUrl);
                }
                if (i3 == 2) {
                    return new Slot(putElementV04Content.putUrl, getUrl, putElementV04Content.headers);
                }
                throw new AssertionError();
            }
        }
    }

    public static PutElement_V0_4_Content parsePutElement_V0_4(XmlPullParser parser) throws XmlPullParserException, IOException {
        int initialDepth = parser.getDepth();
        URL putUrl = new URL(parser.getAttributeValue(null, "url"));
        Map<String, String> headers = null;
        while (true) {
            int next = parser.next();
            if (next == 2) {
                String name = parser.getName();
                char c = 65535;
                if (name.hashCode() == -1221270899 && name.equals(Header.ELEMENT)) {
                    c = 0;
                }
                if (c == 0) {
                    String headerName = ParserUtils.getRequiredAttribute(parser, "name");
                    String headerValue = ParserUtils.getRequiredNextText(parser);
                    if (headers == null) {
                        headers = new HashMap<>();
                    }
                    headers.put(headerName, headerValue);
                }
            } else if (next == 3 && parser.getDepth() == initialDepth) {
                return new PutElement_V0_4_Content(putUrl, headers, null);
            }
        }
    }
}
