package com.yordan.finance.view.callback;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.yordan.finance.model.Expense;

import java.util.List;

public class myDiffutilCallback extends DiffUtil.Callback {

    private List<Expense> oldExpenses;
    private List<Expense> newExpenses;

    public myDiffutilCallback(List<Expense> oldExpenses, List<Expense> newExpenses) {
        this.oldExpenses = oldExpenses;
        this.newExpenses = newExpenses;
    }

    @Override
    public int getOldListSize() {
        return oldExpenses.size();
    }

    @Override
    public int getNewListSize() {
        return newExpenses.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        System.out.println("Items are the same...");
        return oldExpenses.get(oldItemPosition).getDate() == newExpenses.get(newItemPosition).getDate();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        System.out.println("Contents are the same...");
        return oldExpenses.get(oldItemPosition).equals(newExpenses.get(newItemPosition));
    }

    @Nullable
    @Override

    public Object getChangePayload(int oldItemPosition, int newItemPosition) {

        //you can return particular field for changed item.

        return super.getChangePayload(oldItemPosition, newItemPosition);

    }
}
