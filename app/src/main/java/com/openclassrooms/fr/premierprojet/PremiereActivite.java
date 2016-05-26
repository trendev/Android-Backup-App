package com.openclassrooms.fr.premierprojet;

import android.app.ActivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class PremiereActivite extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premiere_activite);

        /**
         * restore the data saved in onRetainNonConfigurationInstance()
         * getLastCustomNonConfigurationInstance();
         */

    }

    public void sayHello(View v) {
        final TextView textView = (TextView) findViewById(R.id.textView);
        assert textView != null;
        textView.setTextSize(32f);

        textView.setMovementMethod(new ScrollingMovementMethod());
        final EditText editText = (EditText) findViewById(R.id.firstname);
        assert editText != null;
        final String firstname = editText.getText().toString();
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
        final TextView textView = (TextView) findViewById(R.id.textView);
        assert textView != null;
        textView.setTextSize(10f);

        textView.setMovementMethod(new ScrollingMovementMethod());

        StringBuilder sb = new StringBuilder();
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> pids = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo process : pids)
            sb.append(process.pid).append(" -- ").append(process.processName).append("\n");

        textView.setText(sb.toString());
    }
}
