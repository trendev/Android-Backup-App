package com.openclassrooms.fr.premierprojet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Receiver declared in the manifest xml file
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {

            case Intent.ACTION_MEDIA_MOUNTED:
                Toast.makeText(context, "MOUNTED", Toast.LENGTH_LONG).show();
                break;
            case Intent.ACTION_MEDIA_BAD_REMOVAL:
                Toast.makeText(context, "BAD REMOVAL", Toast.LENGTH_LONG).show();
                break;
            case Intent.ACTION_MEDIA_REMOVED:
                Toast.makeText(context, "REMOVED", Toast.LENGTH_LONG).show();
                break;
        }
    }

}

