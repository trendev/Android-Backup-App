package com.openclassrooms.fr.premierprojet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PremiereActivite extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_premiere_activite);
        String hello = getResources().getString(R.string.helloworld);
        System.out.println(hello.concat(" I'm happy to meet you;) "));//display on terminal output
        TextView textView = (TextView) findViewById(R.id.textView);
        if (textView != null)
            textView.setText(textView.getText().toString().concat("\nIt's a pleasure."));
    }
}
