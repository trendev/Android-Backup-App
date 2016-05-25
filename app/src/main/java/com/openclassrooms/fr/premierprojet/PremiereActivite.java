package com.openclassrooms.fr.premierprojet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PremiereActivite extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_premiere_activite);
    }

    public void sayHello(View v) {
        final TextView textView = (TextView) findViewById(R.id.textView);
        assert textView != null;
        textView.setText(R.string.helloworld);
    }
}
