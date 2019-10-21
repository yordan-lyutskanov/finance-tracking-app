package com.yordan.finance.model;

import android.accounts.Account;
import com.yordan.finance.sync.FinanceCredentials;

import org.json.JSONException;
import org.json.JSONObject;

public class FinanceAccount extends Account implements FinanceCredentials {
    public static String ACCOUNT_TYPE = "com.yordan.finance";
    private static final String ROLE_USER = "ROLE_USER";

    private int id;
    private String password;
    private boolean active;
    private String roles;


    public FinanceAccount(String name, String type, String password) {
        super(name, type);
        this.id = 0;
        this.password = password;
        this.active = true;
        this.roles = ROLE_USER;
    }

    public int getId() {
        return this.id;
    }

    public boolean isActive() {
        return active;
    }

    public String getRoles() {
        return roles;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String toJsonString() {
        JSONObject userJson = new JSONObject();
        try{
            userJson.put("id", getId());
            userJson.put("username", name);
            userJson.put("password", getPassword());
            userJson.put("active", isActive());
            userJson.put("roles", getRoles());
        }catch (JSONException e){
            e.printStackTrace();
        }

        return userJson.toString();
    }

}
