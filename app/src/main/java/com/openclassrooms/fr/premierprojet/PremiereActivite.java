package com.openclassrooms.fr.premierprojet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PremiereActivite extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_premiere_activite);
    }

    public void sayHello(View v) {
        final TextView textView = (TextView) findViewById(R.id.textView);
        assert textView != null;
        final EditText editText = (EditText) findViewById(R.id.firstname);
        assert editText != null;
        final String firstname = editText.getText().toString();
        String message = getResources().getString(R.string.helloworld, firstname);
        textView.setText(message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
