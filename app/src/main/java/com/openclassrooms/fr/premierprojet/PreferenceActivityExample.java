package com.openclassrooms.fr.premierprojet;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PreferenceActivityExample extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
