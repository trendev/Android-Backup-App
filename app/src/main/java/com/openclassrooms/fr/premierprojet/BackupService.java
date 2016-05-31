package com.openclassrooms.fr.premierprojet;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class BackupService extends IntentService {

    private final static String TAG = "BackupService";

    public BackupService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int value = intent.getIntExtra(PremiereActivite.EXTRA_BACKUP_SERVICE, -2);
        Log.i(PremiereActivite.EXTRA_BACKUP_SERVICE, "In BackupService - " + System.currentTimeMillis() + ", value = " + value);
        value *= 2;
        PremiereActivite.sharedValue += 100;
        intent.putExtra(PremiereActivite.EXTRA_BACKUP_SERVICE, value);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(PremiereActivite.EXTRA_BACKUP_SERVICE, "BackupService dying...");
    }
}
