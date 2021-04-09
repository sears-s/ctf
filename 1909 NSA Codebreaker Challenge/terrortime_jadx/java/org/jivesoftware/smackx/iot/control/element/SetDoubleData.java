package org.jivesoftware.smackx.iot.control.element;

import org.jivesoftware.smackx.iot.control.element.SetData.Type;

public class SetDoubleData extends SetData {
    private Double doubleCache;

    public SetDoubleData(String name, double value) {
        this(name, Double.toString(value));
        this.doubleCache = Double.valueOf(value);
    }

    protected SetDoubleData(String name, String value) {
        super(name, Type.DOUBLE, value);
    }

    public Double getDoubleValue() {
        if (this.doubleCache != null) {
            this.doubleCache = Double.valueOf(getValue());
        }
        return this.doubleCache;
    }
}
