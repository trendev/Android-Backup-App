package com.openclassrooms.fr.premierprojet.beans;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created by jsie on 03/06/16.
 */
public class ActivationProperty {
    private boolean activated;
    private Object bean;
    private PropertyChangeSupport pcs;

    public ActivationProperty() {
        this(false);
    }

    public ActivationProperty(boolean activated) {
        bean = new Object();
        pcs = new PropertyChangeSupport(bean);
        this.activated = activated;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        pcs.firePropertyChange("activated", this.activated, activated);
        this.activated = activated;
    }

    public void addActivatedPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}
