package org.jivesoftware.smackx.iot.control.element;

import org.jivesoftware.smackx.iot.control.element.SetData.Type;

public class SetLongData extends SetData {
    private Long longCache;

    public SetLongData(String name, long value) {
        this(name, Long.toString(value));
        this.longCache = Long.valueOf(value);
    }

    protected SetLongData(String name, String value) {
        super(name, Type.LONG, value);
    }

    public Long getLongValue() {
        if (this.longCache != null) {
            this.longCache = Long.valueOf(getValue());
        }
        return this.longCache;
    }
}
