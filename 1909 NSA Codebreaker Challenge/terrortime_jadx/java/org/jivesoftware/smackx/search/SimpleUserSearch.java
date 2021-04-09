package org.jivesoftware.smackx.search;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smackx.search.ReportedData.Column;
import org.jivesoftware.smackx.search.ReportedData.Field;
import org.jivesoftware.smackx.search.ReportedData.Row;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.FormField.Type;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class SimpleUserSearch extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "jabber:iq:search";
    private ReportedData data;
    private Form form;

    SimpleUserSearch() {
        super("query", "jabber:iq:search");
    }

    public void setForm(Form form2) {
        this.form = form2;
    }

    public ReportedData getReportedData() {
        return this.data;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder buf) {
        buf.rightAngleBracket();
        buf.append((CharSequence) getItemsToSearch());
        return buf;
    }

    private String getItemsToSearch() {
        StringBuilder buf = new StringBuilder();
        if (this.form == null) {
            this.form = Form.getFormFrom(this);
        }
        Form form2 = this.form;
        if (form2 == null) {
            return BuildConfig.FLAVOR;
        }
        for (FormField field : form2.getFields()) {
            String name = field.getVariable();
            String value = getSingleValue(field);
            if (value.trim().length() > 0) {
                buf.append('<');
                buf.append(name);
                buf.append('>');
                buf.append(value);
                buf.append("</");
                buf.append(name);
                buf.append('>');
            }
        }
        return buf.toString();
    }

    private static String getSingleValue(FormField formField) {
        List<String> values = formField.getValuesAsString();
        if (values.isEmpty()) {
            return BuildConfig.FLAVOR;
        }
        return (String) values.get(0);
    }

    /* access modifiers changed from: protected */
    public void parseItems(XmlPullParser parser) throws XmlPullParserException, IOException {
        ReportedData data2 = new ReportedData();
        String str = "jid";
        data2.addColumn(new Column("JID", str, Type.text_single));
        boolean done = false;
        List<Field> fields = new ArrayList<>();
        while (!done) {
            if (parser.getAttributeCount() > 0) {
                String jid = parser.getAttributeValue(BuildConfig.FLAVOR, str);
                List<String> valueList = new ArrayList<>();
                valueList.add(jid);
                fields.add(new Field(str, valueList));
            }
            int eventType = parser.next();
            String str2 = "item";
            if (eventType == 2 && parser.getName().equals(str2)) {
                fields = new ArrayList<>();
            } else if (eventType == 3 && parser.getName().equals(str2)) {
                data2.addRow(new Row(fields));
            } else if (eventType == 2) {
                String name = parser.getName();
                String value = parser.nextText();
                List<String> valueList2 = new ArrayList<>();
                valueList2.add(value);
                fields.add(new Field(name, valueList2));
                boolean exists = false;
                Iterator it = data2.getColumns().iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (((Column) it.next()).getVariable().equals(name)) {
                            exists = true;
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (!exists) {
                    data2.addColumn(new Column(name, name, Type.text_single));
                }
            } else if (eventType == 3 && parser.getName().equals("query")) {
                done = true;
            }
        }
        this.data = data2;
    }
}
