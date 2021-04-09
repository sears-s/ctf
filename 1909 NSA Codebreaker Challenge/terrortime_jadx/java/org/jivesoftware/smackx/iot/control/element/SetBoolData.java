package org.jivesoftware.smackx.iot.control.element;

import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.iot.control.element.SetData.Type;

public class SetBoolData extends SetData {
    private Boolean booleanCache;

    public SetBoolData(String name, boolean value) {
        this(name, Boolean.toString(value));
        this.booleanCache = Boolean.valueOf(value);
    }

    protected SetBoolData(String name, String value) {
        super(name, Type.BOOL, value);
    }

    public Boolean getBooleanValue() {
        if (this.booleanCache != null) {
            this.booleanCache = Boolean.valueOf(ParserUtils.parseXmlBoolean(getValue()));
        }
        return this.booleanCache;
    }
}
