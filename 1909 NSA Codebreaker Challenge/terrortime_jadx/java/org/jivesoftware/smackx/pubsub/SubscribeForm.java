package org.jivesoftware.smackx.pubsub;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UnknownFormatConversionException;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.xdata.packet.DataForm.Type;
import org.jxmpp.util.XmppDateTime;

public class SubscribeForm extends Form {
    public SubscribeForm(DataForm configDataForm) {
        super(configDataForm);
    }

    public SubscribeForm(Form subscribeOptionsForm) {
        super(subscribeOptionsForm.getDataFormToSend());
    }

    public SubscribeForm(Type formType) {
        super(formType);
    }

    public boolean isDeliverOn() {
        return parseBoolean(getFieldValue(SubscribeOptionFields.deliver));
    }

    public void setDeliverOn(boolean deliverNotifications) {
        addField(SubscribeOptionFields.deliver, FormField.Type.bool);
        setAnswer(SubscribeOptionFields.deliver.getFieldName(), deliverNotifications);
    }

    public boolean isDigestOn() {
        return parseBoolean(getFieldValue(SubscribeOptionFields.digest));
    }

    public void setDigestOn(boolean digestOn) {
        addField(SubscribeOptionFields.deliver, FormField.Type.bool);
        setAnswer(SubscribeOptionFields.deliver.getFieldName(), digestOn);
    }

    public int getDigestFrequency() {
        return Integer.parseInt(getFieldValue(SubscribeOptionFields.digest_frequency));
    }

    public void setDigestFrequency(int frequency) {
        addField(SubscribeOptionFields.digest_frequency, FormField.Type.text_single);
        setAnswer(SubscribeOptionFields.digest_frequency.getFieldName(), frequency);
    }

    public Date getExpiry() {
        String dateTime = getFieldValue(SubscribeOptionFields.expire);
        try {
            return XmppDateTime.parseDate(dateTime);
        } catch (ParseException e) {
            UnknownFormatConversionException exc = new UnknownFormatConversionException(dateTime);
            exc.initCause(e);
            throw exc;
        }
    }

    public void setExpiry(Date expire) {
        addField(SubscribeOptionFields.expire, FormField.Type.text_single);
        setAnswer(SubscribeOptionFields.expire.getFieldName(), XmppDateTime.formatXEP0082Date(expire));
    }

    public boolean isIncludeBody() {
        return parseBoolean(getFieldValue(SubscribeOptionFields.include_body));
    }

    public void setIncludeBody(boolean include) {
        addField(SubscribeOptionFields.include_body, FormField.Type.bool);
        setAnswer(SubscribeOptionFields.include_body.getFieldName(), include);
    }

    public List<PresenceState> getShowValues() {
        ArrayList<PresenceState> result = new ArrayList<>(5);
        for (String state : getFieldValues(SubscribeOptionFields.show_values)) {
            result.add(PresenceState.valueOf(state));
        }
        return result;
    }

    public void setShowValues(Collection<PresenceState> stateValues) {
        ArrayList<String> values = new ArrayList<>(stateValues.size());
        for (PresenceState state : stateValues) {
            values.add(state.toString());
        }
        addField(SubscribeOptionFields.show_values, FormField.Type.list_multi);
        setAnswer(SubscribeOptionFields.show_values.getFieldName(), (List<? extends CharSequence>) values);
    }

    private static boolean parseBoolean(String fieldValue) {
        return "1".equals(fieldValue) || "true".equals(fieldValue);
    }

    private String getFieldValue(SubscribeOptionFields field) {
        return getField(field.getFieldName()).getFirstValue();
    }

    private List<String> getFieldValues(SubscribeOptionFields field) {
        return getField(field.getFieldName()).getValuesAsString();
    }

    private void addField(SubscribeOptionFields nodeField, FormField.Type type) {
        String fieldName = nodeField.getFieldName();
        if (getField(fieldName) == null) {
            FormField field = new FormField(fieldName);
            field.setType(type);
            addField(field);
        }
    }
}
