package com.openclassrooms.fr.premierprojet;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.util.List;

/**
 * This activity is used to display the list of the different process running on the device
 *
 * @author jsie
 */
public class DeuxiemeActivite extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deuxieme_activite);

        final TextView textViewProcess = (TextView) findViewById(R.id.textViewProcess);
        assert textViewProcess != null;
        //textViewProcess.setTextSize(10f);

        textViewProcess.setMovementMethod(new ScrollingMovementMethod());

        StringBuilder sb = new StringBuilder();
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> pids = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo process : pids)
            sb.append(process.pid).append(" -- ").append(process.processName).append("\n");

        textViewProcess.setText(sb.toString());
        //provide a result to the main activity
        Intent result = new Intent();

        result.putExtra(PremiereActivite.TOTAL_PROCESS, Integer.toString(pids.size()));
        setResult(RESULT_OK, result);
    }

}
