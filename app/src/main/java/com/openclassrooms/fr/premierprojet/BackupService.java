package com.openclassrooms.fr.premierprojet;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class BackupService extends IntentService {

    private final static String TAG = "BackupService";

    public BackupService() {
        super(TAG);
    }

    //TODO : Backup will be performed there in a dedicated Service
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(PremiereActivite.EXTRA_BACKUP_SERVICE, "In BackupService - " + System.currentTimeMillis());
        for (int i = 0; i < Integer.MAX_VALUE; i++) ;
        Log.i(PremiereActivite.EXTRA_BACKUP_SERVICE, "Loop is over - " + System.currentTimeMillis());
    }
}
