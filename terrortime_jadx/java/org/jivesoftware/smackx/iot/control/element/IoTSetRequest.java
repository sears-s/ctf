package org.jivesoftware.smackx.iot.control.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;

public class IoTSetRequest extends IQ {
    public static final String ELEMENT = "set";
    public static final String NAMESPACE = "urn:xmpp:iot:control";
    private final Collection<SetData> setData;

    public IoTSetRequest(Collection<? extends SetData> setData2) {
        super("set", "urn:xmpp:iot:control");
        setType(Type.set);
        Collection<SetData> tmp = new ArrayList<>(setData2.size());
        for (SetData data : setData2) {
            tmp.add(data);
        }
        this.setData = Collections.unmodifiableCollection(tmp);
    }

    public Collection<SetData> getSetData() {
        return this.setData;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.append(this.setData);
        return xml;
    }
}
