package com.yordan.finance.view.ui.activities;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.yordan.finance.R;
import com.yordan.finance.workersAndTasks.LoginAsyncTask;

public class LoginActivity extends AccountAuthenticatorActivity {
    private AccountManager accountManager;
    private final String TAG = getClass().getSimpleName();

    private final int REQ_REGISTER = 11;

    EditText etUsername;
    EditText etPassword;
    TextInputLayout tilUsername;
    TextInputLayout tilPassword;
    Button btLogin;
    Button btRegister;

    TextView tvLoginStatus;
    ProgressBar pbLoginProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username_login);
        etPassword = findViewById(R.id.et_password_login);
        tilUsername = findViewById(R.id.til_username_login);
        tilPassword = findViewById(R.id.til_password_login);

        btLogin = findViewById(R.id.bt_login);
        btRegister = findViewById(R.id.bt_register);

        tvLoginStatus = findViewById(R.id.tv_login_info);
        pbLoginProgress = findViewById(R.id.pb_login_progress);
        pbLoginProgress.setVisibility(View.GONE);

        btLogin.setOnClickListener((v) -> checkInput());
        btRegister.setOnClickListener((v) -> createAccount());


        accountManager = AccountManager.get(getBaseContext());
    }

    public void createAccount(){
        Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
        intent.putExtras(getIntent().getExtras());
        startActivityForResult(intent, REQ_REGISTER);
    }

    private void checkInput(){
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if(username.isEmpty() || password.isEmpty()){
            if(username.isEmpty()){
                tilUsername.setErrorEnabled(true);
                tilUsername.setError("You must enter your username.");
            }

            if(password.isEmpty()){
                tilPassword.setErrorEnabled(true);
                tilPassword.setError("You must enter your password.");
            }
        }else{
            tilPassword.setErrorEnabled(false);
            tilUsername.setErrorEnabled(false);
            login(username, password);
        }
    }

    public void login(String username, String password){
        String accountType = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        new LoginAsyncTask(tvLoginStatus, pbLoginProgress, this).execute(username, password, accountType);
    }




}
