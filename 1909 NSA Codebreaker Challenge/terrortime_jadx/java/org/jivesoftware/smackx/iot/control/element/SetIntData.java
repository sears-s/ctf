package org.jivesoftware.smackx.iot.control.element;

import org.jivesoftware.smackx.iot.control.element.SetData.Type;

public class SetIntData extends SetData {
    private Integer integerCache;

    public SetIntData(String name, int value) {
        this(name, Integer.toString(value));
        this.integerCache = Integer.valueOf(value);
    }

    protected SetIntData(String name, String value) {
        super(name, Type.INT, value);
    }

    public Integer getIntegerValue() {
        if (this.integerCache != null) {
            this.integerCache = Integer.valueOf(getValue());
        }
        return this.integerCache;
    }
}
