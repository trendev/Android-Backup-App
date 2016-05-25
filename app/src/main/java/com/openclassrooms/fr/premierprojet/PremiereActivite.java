package com.openclassrooms.fr.premierprojet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PremiereActivite extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * It's possible to add a simple TextView but it's recommanded to use xml configuration
         * instead.
         */
        setContentView(R.layout.activity_premiere_activite);
    }
}
