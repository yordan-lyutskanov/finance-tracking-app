package com.yordan.finance.view.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.yordan.finance.R;
import com.yordan.finance.workersAndTasks.RegisterAccountAsyncTask;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;

    private Button btCreateAccount;

    private TextView tvInformProgress;
    private ProgressBar pbRegistering;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tilUsername = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        btCreateAccount = findViewById(R.id.bt_create_account);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        tvInformProgress = findViewById(R.id.tv_inform_progress);
        pbRegistering = findViewById(R.id.pb_registering);

        pbRegistering.setVisibility(View.GONE);
        tvInformProgress.setVisibility(View.GONE);

        btCreateAccount.setOnClickListener((v) -> showErrorIfAny());


    }

    private void showErrorIfAny(){
        String username = null;
        String password = null;
        String confirmedPassword = null;

        if(etUsername.getText() != null){
            username = etUsername.getText().toString();
        }

        if(etPassword.getText() != null){
            password = etPassword.getText().toString();
        }

        if(etConfirmPassword.getText() != null){
            confirmedPassword = etConfirmPassword.getText().toString();
        }



        if(username == null || username.isEmpty()){
            tilUsername.setErrorEnabled(true);
            tilUsername.setError("Username can't be empty");
        }else{
            tilUsername.setErrorEnabled(false);
        }

        if(password == null || password.isEmpty()){
            tilPassword.setErrorEnabled(true);
            tilPassword.setError("You must provide a password.");
        }else{
            tilPassword.setErrorEnabled(false);
        }

        if(confirmedPassword == null || confirmedPassword.isEmpty()){
            tilConfirmPassword.setErrorEnabled(true);
            tilConfirmPassword.setError("You must confirm the password.");
        }else{
            tilConfirmPassword.setErrorEnabled(false);
        }

        if(!(password == null || password.isEmpty()) &&
                !(confirmedPassword == null || confirmedPassword.isEmpty())){
            if(password.equals(confirmedPassword)){
                tilConfirmPassword.setErrorEnabled(false);
                tilPassword.setErrorEnabled(false);
                if(username != null && !username.isEmpty()){
                    createAccount(username, password);
                }
            }else{
                tilConfirmPassword.setErrorEnabled(true);
                tilPassword.setErrorEnabled(true);

                tilPassword.setError("Passwords don't match");
                tilConfirmPassword.setError("Passwords don't match");
            }
        }


    }

    private void createAccount(String username, String password) {
        new RegisterAccountAsyncTask(tvInformProgress, pbRegistering).execute(username, password);
        Toast.makeText(this, "Crating account", Toast.LENGTH_SHORT).show();
    }
}
