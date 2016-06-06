package com.openclassrooms.fr.premierprojet.beans;

import android.content.Intent;
import android.view.View;

/**
 * This Property is a Java Bean used between the main activity (PremiereActivite) and the BackupService.
 *
 * @author jsie
 * @see com.openclassrooms.fr.premierprojet.PremiereActivite#backup(View)
 * @see com.openclassrooms.fr.premierprojet.BackupService#activationProperty
 * @see com.openclassrooms.fr.premierprojet.BackupService#onHandleIntent(Intent)
 * Created on 03/06/16.
 */
public class BooleanProperty extends AbstractProperty {
    private boolean value;

    public BooleanProperty() {
        this(false);
    }

    public BooleanProperty(boolean value) {
        super();
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        pcs.firePropertyChange("value", this.value, value);
        this.value = value;
    }
}
