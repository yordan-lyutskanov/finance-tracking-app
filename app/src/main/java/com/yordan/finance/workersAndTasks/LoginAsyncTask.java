package com.yordan.finance.workersAndTasks;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yordan.finance.model.FinanceAccount;
import com.yordan.finance.utils.ServerUtils;

import java.lang.ref.WeakReference;

public class LoginAsyncTask extends AsyncTask<String, Void, String[]> {

    private final String TAG = getClass().getSimpleName();

    private WeakReference<TextView> tvLoginResults;
    private WeakReference<ProgressBar> pbLoginProgress;
    private WeakReference<Activity> activityWeakReference;

    public LoginAsyncTask(TextView tvLoginResults, ProgressBar pbLoginProgress, Activity context) {
        this.tvLoginResults = new WeakReference<>(tvLoginResults);
        this.pbLoginProgress = new WeakReference<>(pbLoginProgress);
        this.activityWeakReference = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        tvLoginResults.get().setVisibility(View.GONE);
        pbLoginProgress.get().setVisibility(View.VISIBLE);
    }

    @Override
    protected String[] doInBackground(String... strings) {
        String username = strings[0];
        String password = strings[1];
        String accountType = strings[2];

        FinanceAccount account = new FinanceAccount(username, accountType, password);
        int resposne = ServerUtils.login(account);

        String[] userAndResponse = new String[4];
        System.arraycopy(strings, 0 , userAndResponse, 0, strings.length);
        userAndResponse[3] = String.valueOf(resposne);

        return userAndResponse;
    }

    @Override
    protected void onPostExecute(String[] userAndResponse) {
        String username = userAndResponse[0];
        String password = userAndResponse[1];
        String accountType = userAndResponse[2];
        FinanceAccount account = new FinanceAccount(username, accountType, password);

        int response = Integer.parseInt(userAndResponse[3]);

        tvLoginResults.get().setVisibility(View.VISIBLE);
        pbLoginProgress.get().setVisibility(View.GONE);

        setLoginResult(account, response);
    }

    private void setLoginResult(FinanceAccount account, int response) {
        AccountManager accountManager = AccountManager.get(activityWeakReference.get());
        String accountType = account.type;

        Log.d(TAG, "setLoginResult: RESPONSE CODE WAS: " + response);

        switch (response){
            case 404:
                tvLoginResults.get().setText("Couldn't connect to the server");
                break;
            case 401:
                tvLoginResults.get().setText("Wrong username or password. Please try again.");
                break;
            default:
                Account[] accounts = accountManager.getAccountsByType(accountType);

                boolean exists = false;
                for(Account a : accounts){
                    if(a.name.equals(account.name)){
                        tvLoginResults.get().setText("Account already logged in.");
                        exists = true;
                    }
                }

                if(!exists){
                    if(accounts.length > 0){
                        tvLoginResults.get().setText("You can have only one account logged in.");
                        break;
                    }
                    accountManager.addAccountExplicitly(account, account.getPassword(), null);
                    tvLoginResults.get().setText("Login successful");
                }
            break;
        }
    }
}
