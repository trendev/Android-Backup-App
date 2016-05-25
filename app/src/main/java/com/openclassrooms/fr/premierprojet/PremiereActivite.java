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

        final TextView textView = (TextView) findViewById(R.id.textView);

        Button button = (Button) findViewById(R.id.button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (textView != null) {
                        textView.setText(R.string.helloworld);
                    }
                }
            });
        }
    }
}
