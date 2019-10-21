package com.yordan.finance.view.callback;

public interface ChangeExpenseDialogListener {
    void onReturnedValue(int subcategory, long date, String expenseName);
}
