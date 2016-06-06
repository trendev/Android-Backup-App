package com.openclassrooms.fr.premierprojet.beans;

/**
 * Created by jsie on 06/06/16.
 */
public class LongProperty extends AbstractProperty {
    private long value;

    public LongProperty() {
        this(0l);
    }

    public LongProperty(long value) {
        super();
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        pcs.firePropertyChange("value", this.value, value);
        this.value = value;
    }


}
