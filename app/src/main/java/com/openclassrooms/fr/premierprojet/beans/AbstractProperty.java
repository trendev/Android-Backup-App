package com.openclassrooms.fr.premierprojet.beans;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created by jsie on 06/06/16.
 */
public abstract class AbstractProperty {
    final protected PropertyChangeSupport pcs;
    private Object bean;

    public AbstractProperty() {
        bean = new Object();
        pcs = new PropertyChangeSupport(bean);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

}
