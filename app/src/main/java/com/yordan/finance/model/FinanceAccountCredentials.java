package com.yordan.finance.model;

import com.yordan.finance.sync.FinanceCredentials;

public class FinanceAccountCredentials implements FinanceCredentials {
    private String name;
    private String password;

    public FinanceAccountCredentials(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
