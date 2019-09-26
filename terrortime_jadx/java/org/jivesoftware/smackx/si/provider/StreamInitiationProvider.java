package org.jivesoftware.smackx.si.provider;

import com.badguy.terrortime.BuildConfig;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.hashes.element.HashElement;
import org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransferChild;
import org.jivesoftware.smackx.si.packet.StreamInitiation;
import org.jivesoftware.smackx.si.packet.StreamInitiation.File;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;
import org.jxmpp.util.XmppDateTime;
import org.xmlpull.v1.XmlPullParser;

public class StreamInitiationProvider extends IQProvider<StreamInitiation> {
    private static final Logger LOGGER = Logger.getLogger(StreamInitiationProvider.class.getName());

    public StreamInitiation parse(XmlPullParser parser, int initialDepth) throws Exception {
        String size;
        String str;
        XmlPullParser xmlPullParser = parser;
        String str2 = BuildConfig.FLAVOR;
        String id = xmlPullParser.getAttributeValue(str2, "id");
        String mimeType = xmlPullParser.getAttributeValue(str2, "mime-type");
        StreamInitiation initiation = new StreamInitiation();
        DataForm form = null;
        DataFormProvider dataFormProvider = new DataFormProvider();
        boolean isRanged = false;
        String desc = null;
        String date = null;
        String hash = null;
        String size2 = null;
        String name = null;
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            boolean done2 = done;
            String elementName = parser.getName();
            DataForm form2 = form;
            String namespace = parser.getNamespace();
            String mimeType2 = mimeType;
            String mimeType3 = JingleFileTransferChild.ELEMENT;
            if (eventType == 2) {
                if (elementName.equals(mimeType3)) {
                    name = xmlPullParser.getAttributeValue(str2, "name");
                    size2 = xmlPullParser.getAttributeValue(str2, JingleFileTransferChild.ELEM_SIZE);
                    hash = xmlPullParser.getAttributeValue(str2, HashElement.ELEMENT);
                    date = xmlPullParser.getAttributeValue(str2, JingleFileTransferChild.ELEM_DATE);
                    done = done2;
                    form = form2;
                    mimeType = mimeType2;
                } else if (elementName.equals(JingleFileTransferChild.ELEM_DESC)) {
                    desc = parser.nextText();
                    done = done2;
                    form = form2;
                    mimeType = mimeType2;
                } else if (elementName.equals("range")) {
                    isRanged = true;
                    done = done2;
                    form = form2;
                    mimeType = mimeType2;
                } else if (!elementName.equals("x")) {
                    str = str2;
                    size = size2;
                } else if (namespace.equals("jabber:x:data")) {
                    form = (DataForm) dataFormProvider.parse(xmlPullParser);
                    done = done2;
                    mimeType = mimeType2;
                } else {
                    str = str2;
                    size = size2;
                }
            } else if (eventType != 3) {
                str = str2;
                String str3 = elementName;
                size = size2;
                String str4 = namespace;
            } else if (elementName.equals(StreamInitiation.ELEMENT)) {
                done = true;
                form = form2;
                mimeType = mimeType2;
            } else if (elementName.equals(mimeType3)) {
                String str5 = str2;
                long fileSize = 0;
                if (size2 == null || size2.trim().length() == 0) {
                    str = str5;
                    String str6 = elementName;
                    size = size2;
                    String str7 = namespace;
                } else {
                    try {
                        fileSize = Long.parseLong(size2);
                        str = str5;
                        String str8 = elementName;
                        size = size2;
                        String str9 = namespace;
                    } catch (NumberFormatException e) {
                        NumberFormatException e2 = e;
                        str = str5;
                        Logger logger = LOGGER;
                        String str10 = elementName;
                        Level level = Level.SEVERE;
                        size = size2;
                        StringBuilder sb = new StringBuilder();
                        String str11 = namespace;
                        sb.append("Failed to parse file size from ");
                        sb.append(0);
                        logger.log(level, sb.toString(), e2);
                    }
                }
                Date fileDate = new Date();
                if (date != null) {
                    try {
                        fileDate = XmppDateTime.parseDate(date);
                    } catch (ParseException e3) {
                    }
                }
                File file = new File(name, fileSize);
                file.setHash(hash);
                file.setDate(fileDate);
                file.setDesc(desc);
                file.setRanged(isRanged);
                initiation.setFile(file);
            } else {
                str = str2;
                String str12 = elementName;
                size = size2;
                String str13 = namespace;
            }
            xmlPullParser = parser;
            done = done2;
            form = form2;
            mimeType = mimeType2;
            str2 = str;
            size2 = size;
        }
        String mimeType4 = mimeType;
        initiation.setSessionID(id);
        initiation.setMimeType(mimeType4);
        initiation.setFeatureNegotiationForm(form);
        return initiation;
    }
}
