package org.jivesoftware.smackx.delay.provider;

import java.text.ParseException;
import java.util.Date;
import org.jxmpp.util.XmppDateTime;

public class DelayInformationProvider extends AbstractDelayInformationProvider {
    public static final DelayInformationProvider INSTANCE = new DelayInformationProvider();

    /* access modifiers changed from: protected */
    public Date parseDate(String string) throws ParseException {
        return XmppDateTime.parseXEP0082Date(string);
    }
}
