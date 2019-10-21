package com.yordan.finance.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class FinanceSyncService extends Service {

    private final String TAG = getClass().getSimpleName();
    public static final Object lock = new Object();
    private SyncAdapter syncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate called");
        synchronized (lock){
            if(syncAdapter == null){
                syncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
