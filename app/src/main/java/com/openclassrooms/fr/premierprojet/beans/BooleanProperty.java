package com.openclassrooms.fr.premierprojet.beans;

import android.content.Intent;
import android.view.View;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This Property is a Java Bean used between the main activity (PremiereActivite) and the BackupService.
 * @see com.openclassrooms.fr.premierprojet.PremiereActivite#backup(View)
 * @see com.openclassrooms.fr.premierprojet.BackupService#activationProperty
 * @see com.openclassrooms.fr.premierprojet.BackupService#onHandleIntent(Intent)
 * Created on 03/06/16.
 * @author jsie
 */
public class BooleanProperty {
    private boolean activated;
    private Object bean;
    private PropertyChangeSupport pcs;

    public BooleanProperty() {
        this(false);
    }

    public BooleanProperty(boolean activated) {
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
