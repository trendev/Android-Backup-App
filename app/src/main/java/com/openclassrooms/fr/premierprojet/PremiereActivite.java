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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

    /**
     * The value used to define the origin of the request towards the second activity
     */
    private final static int requestSecondActivity = 1;

    // Can be used to catch response from the BackupService.
    // private final static int requestBackupActivity = 2;

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
         * BroadcastReceiver br = new MediaEventsReceiver();
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
        initPreferences();
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
        initPreferences();
        Intent deuxiemeActivite = new Intent(this, DeuxiemeActivite.class);
        startActivityForResult(deuxiemeActivite, requestSecondActivity);
        //send a sms
        /*Uri sms = Uri.parse("smsto:+33xxxxxxxx?body=" + sb.toString());
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

    /**
     * Will catch the response from activites/services
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestSecondActivity)
            if (resultCode == RESULT_OK)
                Toast.makeText(this, data.getStringExtra(EXTRA_TOTAL_FILES) + " " + getResources().getString(R.string.files), Toast.LENGTH_SHORT).show();

        //Useless : service is not running through a PendingIntent...
        /*if (requestCode == requestBackupActivity) {
            if (data != null) {
                if (resultCode != RESULT_OK)
                    Log.w(EXTRA_BACKUP_SERVICE, "Something goes wrong...");
                else
                    Log.i(EXTRA_BACKUP_SERVICE, "** Backup Service finished with success **");
            }
        }*/
    }

    /**
     * <pre>
     * Will backup the local media (sdcard) to a remote cifs/samba shared folder.
     * BackupService is activated through an Intent started from the Backup Button.
     * A Property is used to link the button activation with the service activation.
     * When the service is running, the button is not enabled.
     * The ChangeListener is defined here in an anonymous class and will activate/deactivate the Backup
     * button calling the UI Thread.
     * </pre>
     *
     * @param v the Button associated to the action
     */
    public void backup(final View v) {

        //TODO: implement a ProgressBar

        initPreferences();

        //avoid to be garbage-collected...
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //button status is the opposite of the service status
                        v.setEnabled((Boolean) propertyChangeEvent.getOldValue());
                    }
                });
            }
        };

        BackupService.activationProperty.addActivatedPropertyChangeListener(listener);
        final Intent intent = new Intent(this, BackupService.class);
        startService(intent);
    }

    /**
     * Initialize the samba/cifs connections with the application's preferences
     * or use anonymous parameters instead.
     * By default, will use jsie authentification activated ylalsrv01
     */
    void initPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        path = sharedPreferences.getString(getResources().getString(R.string.server_path), "smb://ylalsrv01/jsie-home/");

        boolean anonymous = sharedPreferences.getBoolean(getResources().getString(R.string.checkBoxPref), false);

        if (anonymous) {
            auth = new NtlmPasswordAuthentication(null, null, null);
            userpwd = null;
        } else {
            userpwd = sharedPreferences.getString(getResources().getString(R.string.userpwd_auth), "jsie:qsec0fr");
            auth = new NtlmPasswordAuthentication(userpwd);
        }
    }
}
