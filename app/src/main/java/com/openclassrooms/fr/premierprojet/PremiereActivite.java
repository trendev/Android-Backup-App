package com.openclassrooms.fr.premierprojet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Main activity.
 * TODO: Integrate the local medias backup in a Service
 *
 * @author jsie
 */
public class PremiereActivite extends AppCompatActivity {

    /**
     * This string is the key used in the second activity where the number of running process
     * is specified
     */
    public final static String TOTAL_FILES = "TOTAL_FILES";

    /**
     * The value used to define the origin of the request towards the second activity
     */
    private final static int requestSecondActivity = 1;


    /**
     * The central text zone where message are displayed
     */
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

        /** Dynamic Broadcast
         * BroadcastReceiver br = new MyReceiver();
         * IntentFilter filter = new IntentFilter();
         * filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
         * filter.addAction(Intent.ACTION_MEDIA_REMOVED);
         * filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
         * filter.addDataScheme("file"); //mandatory or ACTION are not catched/understood
         * registerReceiver(br,filter);
         * */
    }

    /**
     * Method called when the hello button is pressed.
     * Will display hello with a first name specified in preferences (an empty string instead).
     *
     * @param v The button associated to the action
     */
    public void sayHello(View v) {

        textView.setMovementMethod(new ScrollingMovementMethod());

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

        //Java 7 : no use of new Date/Time classes of Java 8...
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - kk:mm");

        String text = message + "\n" + sdf.format(calendar.getTime());
        //System.out.println(text);

        textView.setText(text);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Will create a second activity and display the list of the running process
     *
     * @param v the Button associated to the action
     */
    public void displaySharing(View v) {
        Intent deuxiemeActivite = new Intent(this, DeuxiemeActivite.class);
        startActivityForResult(deuxiemeActivite, requestSecondActivity);
        //send a sms
        /*Uri sms = Uri.parse("smsto:+33787428425?body=" + sb.toString());
        Intent sendListProcess = new Intent(Intent.ACTION_SENDTO, sms);
        startActivity(sendListProcess);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestSecondActivity)
            if (resultCode == RESULT_OK)
                Toast.makeText(this, data.getStringExtra(TOTAL_FILES) + " " + getResources().getString(R.string.files), Toast.LENGTH_SHORT).show();
    }

    /**
     * Will open a preferences form where the user will be able to define and store its first name.
     *
     * @param v the Button associated to the action
     */
    public void openPreferences(View v) {
        Intent prefIntent = new Intent(this, PreferenceActivityExample.class);
        startActivity(prefIntent);
    }

    /**
     * Will clean the displayed text and remove the stored preferences.
     *
     * @param v the Button associated to the action
     */
    public void clean(View v) {
        textView.setText("");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            System.exit(0);
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Will backup the different device's medias on samba/cifs sharing.
     * Not yet implemented.
     *
     * @param v the Button associated to the action
     */
    public void backup(View v) {

    }
}
