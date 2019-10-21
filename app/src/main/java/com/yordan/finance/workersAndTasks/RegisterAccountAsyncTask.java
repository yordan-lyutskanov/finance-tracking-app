package com.yordan.finance.workersAndTasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yordan.finance.model.FinanceAccount;
import com.yordan.finance.utils.ServerUtils;

import static com.yordan.finance.model.FinanceAccount.ACCOUNT_TYPE;

public class RegisterAccountAsyncTask extends AsyncTask<String, Void, Integer> {
    private TextView tvInformStatus;
    private ProgressBar pbRegistering;

    public RegisterAccountAsyncTask(TextView tvInformStatus, ProgressBar pbRegistering) {
        this.tvInformStatus = tvInformStatus;
        this.pbRegistering = pbRegistering;
    }

    @Override
    protected void onPreExecute() {
        pbRegistering.setVisibility(View.VISIBLE);
        tvInformStatus.setVisibility(View.GONE);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        String username = strings[0];
        String password = strings[1];



        FinanceAccount account = new FinanceAccount(username, ACCOUNT_TYPE, password);
        int responseCode = ServerUtils.register(account);

        return responseCode;
    }

    @Override
    protected void onPostExecute(Integer responseCode) {
        pbRegistering.setVisibility(View.GONE);
        tvInformStatus.setVisibility(View.VISIBLE);
        switch (responseCode) {
            case 200:
                tvInformStatus.setText("Account created successfully.");
            break;
            case 500:
                tvInformStatus.setText("Account with this username already exists.");
                break;
            case 404:
                tvInformStatus.setText("Couldn't connect to the server.");
                break;
            default:
                tvInformStatus.setText("Something went wrong. Try again later.");
                break;
        }
    }
}
