package com.yordan.finance.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.yordan.finance.data.repository.Repository;
import com.yordan.finance.model.FinanceAccountCredentials;
import com.yordan.finance.workersAndTasks.SyncExpensesAsyncTask;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    ContentResolver contentResolver;
    Context context;
    private String TAG = getClass().getSimpleName();

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        contentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account a,
                              Bundle bundle,
                              String s,
                              ContentProviderClient contentProviderClient,
                              SyncResult syncResult) {
        Application application = (Application) getContext().getApplicationContext();
        Repository repository = Repository.getInstance(application);
        String password = AccountManager.get(context).getPassword(a);
        FinanceAccountCredentials credentials = new FinanceAccountCredentials(a.name, password);
        new SyncExpensesAsyncTask(repository).execute(credentials);
    }
}
