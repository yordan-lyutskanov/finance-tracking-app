package com.yordan.finance.view.ui.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.yordan.finance.R;
import com.yordan.finance.data.repository.Repository;
import com.yordan.finance.model.Expense;
import com.yordan.finance.model.FinanceAccount;
import com.yordan.finance.model.FinanceAccountCredentials;
import com.yordan.finance.sync.FinanceCredentials;
import com.yordan.finance.utils.CsvUtils;
import com.yordan.finance.utils.FilterSortUtils;
import com.yordan.finance.view.ui.activities.ActivitySettings;
import com.yordan.finance.viewmodel.AppViewModel;
import com.yordan.finance.workersAndTasks.NotifyWorker;
import com.yordan.finance.workersAndTasks.SyncExpensesAsyncTask;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.yordan.finance.App.REMINDER_NOTIFICATION_WORK_TAG;
import static com.yordan.finance.view.ui.activities.ActivitySettings.KEY_NOTIFICATION_ENABLED;
import static com.yordan.finance.view.ui.activities.ActivitySettings.KEY_NOTIFICATION_INTERVAL;

public class SettingsFragmentRoot extends PreferenceFragmentCompat {
    private static final String KEY_EXPORT_CSV = "export_csv";
    private static final String KEY_BACKUP_MANUAL = "manual_backup";
    private AppViewModel viewModel;
    private List<Expense> allexpenses;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_settings, rootKey);
        viewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        viewModel.getExpenses().observe(this, new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenses) {
                allexpenses = expenses;
            }
        });



        PreferenceManager.getDefaultSharedPreferences(this.getContext()).registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if(s.equals(KEY_NOTIFICATION_ENABLED) || s.equals(KEY_NOTIFICATION_INTERVAL)){
                    loadUserNotificationSettings(sharedPreferences, getContext());
                    Toast.makeText(getContext(), "Notification settings changed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findPreference(KEY_EXPORT_CSV).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AppViewModel viewModel = ViewModelProviders.of(SettingsFragmentRoot.this).get(AppViewModel.class);
                viewModel.getExpensesApplyFilterSort(FilterSortUtils.FILTER_DATE_ALL, FilterSortUtils.ORDER_BY_DATE_DESC)
                        .observe(SettingsFragmentRoot.this, new Observer<List<Expense>>() {
                            @Override
                            public void onChanged(List<Expense> expenses) {
                                File csvFile = CsvUtils.generateCSVOnInternal(getContext(), expenses);
                                Intent shareCsvIntent = new Intent(Intent.ACTION_SEND);

                                if(csvFile.exists()){
                                    shareCsvIntent.setType("text/comma-separated-values");
                                    Uri uri = FileProvider.getUriForFile(getContext(), "com.yordan.finance", csvFile);
                                    shareCsvIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    shareCsvIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing .csv file.");
                                    shareCsvIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    shareCsvIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    startActivity(Intent.createChooser(shareCsvIntent, "Download .csv data."));

                                }
                            }
                        });
                return true;
            }
        });

        findPreference(KEY_BACKUP_MANUAL).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AccountManager accountManager = AccountManager.get(getContext());
                if(accountManager.getAccountsByType("com.yordan.finance").length > 0){
                    Account account = accountManager.getAccounts()[0];
                    String pass = accountManager.getPassword(account);
                    FinanceAccountCredentials credentials = new FinanceAccountCredentials(account.name, pass);
                    new SyncExpensesAsyncTask(viewModel.getRepository()).execute(credentials);

                    ContentResolver.setSyncAutomatically(account, "com.yordan.finance.provider", true);
                }else{
                    Toast.makeText(getContext(), "You need to login first.", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    private static void loadUserNotificationSettings(SharedPreferences sharedPreferences, Context context){
        boolean hasNotificationEnabled;
        int interval;

        hasNotificationEnabled = sharedPreferences.getBoolean(ActivitySettings.KEY_NOTIFICATION_ENABLED, false);
        interval = Integer.parseInt(sharedPreferences.getString(ActivitySettings.KEY_NOTIFICATION_INTERVAL, ""));

        if(hasNotificationEnabled){
            PeriodicWorkRequest notificationRequest  = new PeriodicWorkRequest.Builder(NotifyWorker.class, interval, TimeUnit.HOURS).build();
            WorkManager.getInstance(context.getApplicationContext()).enqueueUniquePeriodicWork(REMINDER_NOTIFICATION_WORK_TAG, ExistingPeriodicWorkPolicy.KEEP , notificationRequest);
        }else{
            WorkManager.getInstance(context.getApplicationContext()).cancelUniqueWork(REMINDER_NOTIFICATION_WORK_TAG);
        }
    }

}
