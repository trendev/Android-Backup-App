package com.openclassrooms.fr.premierprojet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jcifs.smb.NtlmPasswordAuthentication;

/**
 * Main activity.
 *
 * @author jsie
 */
public class PremiereActivite extends AppCompatActivity {

    /**
     * This string is the key used in the second activity where the number of running process
     * is specified
     */
    public final static String EXTRA_TOTAL_FILES = "EXTRA_TOTAL_FILES";

    public final static String EXTRA_BACKUP_SERVICE = "BACKUP_SERVICE";

    /**
     * The value used to define the origin of the request towards the second activity
     */
    private final static int requestSecondActivity = 1;

    private final static int requestBackupActivity = 2;

    static String path;
    static String userpwd;
    static NtlmPasswordAuthentication auth;

    /**
     * The central text zone where message are displayed
     */
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premiere_activite);

        textView = (TextView) findViewById(R.id.textView);

        initPreferences();

        /*Log.i("HOST = ", Build.HOST);
        Log.i("DEVICE = ",Build.DEVICE);
        Log.i("HARDWARE = ",Build.HARDWARE);
        Log.i("MODEL = ", Build.MODEL);
        Log.i("PRODUCT = ",Build.PRODUCT);
        Log.i("USER = ",Build.USER);*/

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

    /**
     * Will leave the application if Key Back is pressed activated the main activity.
     * Threads will be interrupted because they are daemons.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestSecondActivity)
            if (resultCode == RESULT_OK)
                Toast.makeText(this, data.getStringExtra(EXTRA_TOTAL_FILES) + " " + getResources().getString(R.string.files), Toast.LENGTH_SHORT).show();

        if (requestCode == requestBackupActivity) {
            if (data != null) {
                if (resultCode != RESULT_OK)
                    Log.w(EXTRA_BACKUP_SERVICE, "Something goes wrong...");
                else
                    Log.i(EXTRA_BACKUP_SERVICE, "** Backup Service finished with success **");
            }
        }
    }

    /**
     * Will backup the different device's medias activated samba/cifs sharing.
     * Not yet implemented.
     *
     * @param v the Button associated to the action
     */
    public void backup(final View v) {
        final int time = 5000;
        if (BackupService.activated == false) {
            BackupService.activated = true;
            v.setEnabled(false);
            final Intent intent = new Intent(this, BackupService.class);

            startService(intent);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (BackupService.activated) {
                        try {
                            Thread.sleep(time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.e(EXTRA_BACKUP_SERVICE, e.getMessage());
                            stopService(intent);
                        }
                    }
                    BackupService.activated = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            v.setEnabled(true);
                        }
                    });
                }
            }).start();
        }
    }

    /**
     * Initialize the samba/cifs connections with the application's preferences
     * or use anonymous parameters instead.
     * By default, will use jsie authentification activated ylalsrv01
     */
    void initPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //TODO : Get a value from preferences and lock the preferences if anonymous is not selected
        boolean anonymous = false;

        if (anonymous) {
            PremiereActivite.auth = null;
            PremiereActivite.path = null;
            PremiereActivite.userpwd = null;
        } else {
            PremiereActivite.path = sharedPreferences.getString(getResources().getString(R.string.server_path), "smb://ylalsrv01/jsie-home/");
            PremiereActivite.userpwd = sharedPreferences.getString(getResources().getString(R.string.userpwd_auth), "jsie:qsec0fr");
            PremiereActivite.auth = new NtlmPasswordAuthentication(PremiereActivite.userpwd);
        }
    }
}
