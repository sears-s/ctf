package org.jivesoftware.smackx.time.packet;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jxmpp.util.XmppDateTime;

public class Time extends IQ {
    public static final String ELEMENT = "time";
    private static final Logger LOGGER = Logger.getLogger(Time.class.getName());
    public static final String NAMESPACE = "urn:xmpp:time";
    private String tzo;
    private String utc;

    public Time() {
        super(ELEMENT, NAMESPACE);
        setType(Type.get);
    }

    public Time(Calendar cal) {
        super(ELEMENT, NAMESPACE);
        this.tzo = XmppDateTime.asString(cal.getTimeZone());
        this.utc = XmppDateTime.formatXEP0082Date(cal.getTime());
    }

    public Date getTime() {
        String str = this.utc;
        if (str == null) {
            return null;
        }
        Date date = null;
        try {
            date = XmppDateTime.parseDate(str);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting local time", e);
        }
        return date;
    }

    public void setTime(Date time) {
    }

    public String getUtc() {
        return this.utc;
    }

    public void setUtc(String utc2) {
        this.utc = utc2;
    }

    public String getTzo() {
        return this.tzo;
    }

    public void setTzo(String tzo2) {
        this.tzo = tzo2;
    }

    public static Time createResponse(IQ request) {
        Time time = new Time(Calendar.getInstance());
        time.setType(Type.result);
        time.setTo(request.getFrom());
        return time;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder buf) {
        if (this.utc != null) {
            buf.rightAngleBracket();
            buf.append((CharSequence) "<utc>").append((CharSequence) this.utc).append((CharSequence) "</utc>");
            buf.append((CharSequence) "<tzo>").append((CharSequence) this.tzo).append((CharSequence) "</tzo>");
        } else {
            buf.setEmptyElement();
        }
        return buf;
    }
}
