package com.yordan.finance.viewmodel;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yordan.finance.model.Expense;
import com.yordan.finance.model.Item;
import com.yordan.finance.data.repository.Repository;
import com.yordan.finance.utils.FilterSortUtils;

import java.util.List;


public class AppViewModel extends AndroidViewModel {

    private static final String TAG = AppViewModel.class.getSimpleName();

    private Repository repository;
    private LiveData<List<Item>> items;
    private LiveData<List<Expense>> expenses;

    private static Expense tempExpense;


    public AppViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
        items = repository.getAllItemsObservable();
        expenses = repository.getAllExpensesObservable();
        tempExpense = null;
    }

    public LiveData<List<Item>> getItems(){ return items; }

    public LiveData<List<Expense>> getExpenses() { return expenses; }

    public LiveData<List<Expense>> getSortedExpenses(){
        return repository.getExpensesFilteredSorted
                (FilterSortUtils.getActiveFilter(), FilterSortUtils.getActiveSort());
    }

    public LiveData<List<Expense>> getExpensesApplyFilterSort(String filter, String sort){
        return repository.getExpensesFilteredSorted(filter, sort);
    }

    public void addItemToTemp(String expenseName, String itemName, String price, int expenseId, int category, long date){
        if(tempExpense == null){
            tempExpense = new Expense.Builder(expenseId, category).name(expenseName).build();
        }

        Item item = new Item(itemName, Double.parseDouble(price));
        tempExpense.addItem(item);
    }

    public void removeItemFromTemp(Item item) {
        if(tempExpense != null){
            tempExpense.removeItem(item);
        }
    }

    public void saveTempToStorage(){
        repository.insertExpense(tempExpense);

        for(Item item : tempExpense.getItems()){
            repository.insertItem(item);
        }

        tempExpense = null;
    }

    public void deleteExpense(Expense expense){
        repository.deleteExpense(expense);
    }

    public void addExpense(Expense expense){
        repository.insertExpense(expense);
    }

    public List<Item> getTempItems(){
        return tempExpense.getItems();
    }

    public Expense getTempExpense(){
        return tempExpense;
    }

    public void updateExpense(Expense expense){
        repository.updateExpense(expense);
    }

    public Repository getRepository(){
        return repository;
    }

}
