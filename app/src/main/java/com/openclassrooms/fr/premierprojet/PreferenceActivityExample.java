package com.openclassrooms.fr.premierprojet;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * The preference activity is used to store elements defined in res/xml/preferences.
 *
 * @author jsie
 */
public class PreferenceActivityExample extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //use the deprecated method, should be interesting to find another way to do
        addPreferencesFromResource(R.xml.preferences);
    }
}
