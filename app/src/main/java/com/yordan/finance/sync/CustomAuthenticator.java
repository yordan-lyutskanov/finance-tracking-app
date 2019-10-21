package com.yordan.finance.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.yordan.finance.view.ui.activities.LoginActivity;

public class CustomAuthenticator extends AbstractAccountAuthenticator {
    private static final String TAG = "CustomAuthenticator";
    private Context context;

    public CustomAuthenticator(Context context) {
        super(context);
        this.context = context;
    }

    //Editing properties is not supported
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }

    //Don't add additional accounts
    @Override
    public Bundle addAccount(
            AccountAuthenticatorResponse accountAuthenticatorResponse,
            String accountType,
            String authTokenType,
            String[] strings,
            Bundle bundle)
            throws NetworkErrorException {

        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }

    //Ignore attempts to confirm credentials
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                     Account account,
                                     Bundle bundle)
            throws NetworkErrorException {
        return null;
    }

    //Getting authentication token is not supported in my implementation. Users are instead verified
    //against their user details. The details are then saved in AccountManager.
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse,
                               Account account,
                               String authTokenType,
                               Bundle response)
            throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    //Not supported
    @Override
    public String getAuthTokenLabel(String s){
        throw new UnsupportedOperationException();
    }

    //Not supported
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                    Account account,
                                    String s,
                                    Bundle bundle)
            throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    //Not supported
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse,
                              Account account,
                              String[] strings)
            throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }
}
