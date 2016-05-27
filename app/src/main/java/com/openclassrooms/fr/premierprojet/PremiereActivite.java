package com.openclassrooms.fr.premierprojet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class PremiereActivite extends AppCompatActivity {

    public final static String TOTAL_PROCESS = "TOTAL_PROCESS";
    private final static int requestSecondActivity = 1;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premiere_activite);

        textView = (TextView) findViewById(R.id.textView);
        /**
         * restore the data saved in onRetainNonConfigurationInstance()
         * getLastCustomNonConfigurationInstance();
         */

    }

    public void sayHello(View v) {

        textView.setTextSize(32f);

        textView.setMovementMethod(new ScrollingMovementMethod());

        SharedPreferences preferences = getPreferences(R.xml.preferences);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        /**
         * Display the preferences Map
         * Map<String, ?> map = sharedPreferences.getAll();
         * for (Map.Entry<String, ?> e : map.entrySet())
         * System.out.println(e.getKey() + " //// " + e.getValue());
         */

        final String firstname = sharedPreferences.getString(getResources().getString(R.string.firstname_key), "");

        String message = getResources().getString(R.string.helloworld, firstname);

        Calendar calendar = Calendar.getInstance();

        String text = message + "\n" +
                calendar.get(Calendar.DAY_OF_MONTH)
                + "/" + calendar.get(Calendar.MONTH) + 1
                + "/" + calendar.get(Calendar.YEAR)
                + " - " + calendar.get(Calendar.HOUR_OF_DAY)
                + ":" + calendar.get(Calendar.MINUTE);

        textView.setText(text);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void displayProcess(View v) {
        Intent deuxiemeActivite = new Intent(this, DeuxiemeActivite.class);
        startActivityForResult(deuxiemeActivite, requestSecondActivity);
        /*Uri sms = Uri.parse("smsto:+33787428425?body=" + sb.toString());
        Intent sendListProcess = new Intent(Intent.ACTION_SENDTO, sms);
        startActivity(sendListProcess);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestSecondActivity)
            if (resultCode == RESULT_OK)
                Toast.makeText(this, data.getStringExtra(TOTAL_PROCESS) + " " + getResources().getString(R.string.process), Toast.LENGTH_SHORT).show();
    }

    public void openPreferences(View v) {
        Intent prefIntent = new Intent(this, PreferenceActivityExample.class);
        startActivity(prefIntent);
    }

    public void clean(View v) {
        textView.setText("");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
