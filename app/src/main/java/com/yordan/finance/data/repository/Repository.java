package com.yordan.finance.data.repository;
import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.yordan.finance.data.daos.CategoryDAO;
import com.yordan.finance.data.daos.ExpenseDAO;
import com.yordan.finance.data.daos.ItemDAO;
import com.yordan.finance.data.daos.SubcategoryDAO;
import com.yordan.finance.data.database.FinanceRoomDatabase;
import com.yordan.finance.model.Category;
import com.yordan.finance.model.Expense;
import com.yordan.finance.model.Item;
import com.yordan.finance.model.Subcategory;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Repository {
    //Repository class should be used to mediate between different types of data sources.
    private static String TAG = Repository.class.getSimpleName();
    private static Repository INSTANCE;

    private ExpenseDAO expenseDAO;
    private ItemDAO itemDAO;
    private CategoryDAO categoryDAO;
    private SubcategoryDAO subcategoryDAO;

    private LiveData<List<Expense>> expenses;
    private LiveData<List<Item>> items;

    private Repository(Application application) {
        FinanceRoomDatabase database = FinanceRoomDatabase.getDatabase(application);

        this.expenseDAO = database.expenseDAO();
        this.itemDAO = database.itemDAO();
        this.categoryDAO = database.categoryDAO();
        this.subcategoryDAO = database.subcategoryDAO();

        expenses = expenseDAO.getObservableExpenses();
        items = itemDAO.getAllItemsObservable();
    }

    public static Repository getInstance(Application application){
        if(INSTANCE == null){
            INSTANCE = new Repository(application);
        }

        return INSTANCE;
    }

    /*
    Public APIs to access the DB from the ViewModel.
    Only the ViewModel should hold a reference to the Repository!
    The ViewModel should access the DB only trough these APIs
     */

    public void insertExpense(Expense expense){
        new InsertExpenseAsyncTask(expenseDAO).execute(expense);
    }

    public void updateExpense(Expense expense){
        new UpdateExpenceAsyncTask(expenseDAO).execute(expense);
    }

    public void deleteExpense(Expense expense){
        new DeleteItemsForPurchaseIdAsyncTask(itemDAO).execute(expense.getId());
        new DeleteExpenseAsyncTask(expenseDAO).execute(expense);
    }

    public LiveData<List<Expense>> getExpensesFilteredSorted(String filter, String sort){
        String query = "SELECT * FROM expenses " + filter + " ORDER BY " + sort;
        return expenseDAO.queryFilterSort(new SimpleSQLiteQuery(query));
    }

    public LiveData<List<Expense>> getAllExpensesObservable(){
        return expenses;
    }

    public List<Expense> getExpensesList(){ return expenseDAO.getAllExpenses(); }

    //Items

    public void insertItem(Item item){
        new InsertItemAsyncTask(itemDAO).execute(item);
    }

    public LiveData<List<Item>> getAllItemsObservable(){
        return items;
    }

    public List<Item> getAllItems() { return itemDAO.getAllItems(); }

    //Categories

    public List<Category> getCategories() {
        try {
            return new GetCategoriesAsyncTask(categoryDAO).execute().get();
        }catch (ExecutionException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        return null;
    }

    public List<Subcategory> getSubcategories() {
        try {
            return new GetSubcategoriesAsyncTask(subcategoryDAO).execute().get();
        }catch (ExecutionException e){
             e.printStackTrace();
         } catch (InterruptedException e){
             e.printStackTrace();
        }

        return null;
    }

    /*
    Static AsyncTaskClasses to be added here. Their purpose is to access the DB on
    a background thread and execute the above mentioned queries. Please make sure to add
    them in the same order as the methods above are added.
     */


    /*
    AsyncTasks for the Expense Class:
     */

    private static class InsertExpenseAsyncTask extends AsyncTask <Expense, Void, Void> {
        private ExpenseDAO expenseDAO;

        InsertExpenseAsyncTask(ExpenseDAO expenseDAO) {
            this.expenseDAO = expenseDAO;
        }

        @Override
        protected Void doInBackground(Expense... expenses) {
            expenseDAO.insertExpense(expenses[0]);
            return null;
        }
    }

    private static class UpdateExpenceAsyncTask extends AsyncTask <Expense, Void, Void> {
        private ExpenseDAO expenseDAO;

        UpdateExpenceAsyncTask(ExpenseDAO expenseDAO) {
            this.expenseDAO = expenseDAO;
        }

        @Override
        protected Void doInBackground(Expense... expenses) {
            expenseDAO.updateExpense(expenses[0]);
            return null;
        }
    }

    private static class DeleteExpenseAsyncTask extends AsyncTask <Expense, Void, Void> {
        private ExpenseDAO expenseDAO;

        DeleteExpenseAsyncTask(ExpenseDAO expenseDAO) {
            this.expenseDAO = expenseDAO;
        }

        @Override
        protected Void doInBackground(Expense... expenses) {
            expenseDAO.deleteExpense(expenses[0]);
            return null;
        }
    }


    /*
    AsyncTasks for the Item class:
     */

    private static class InsertItemAsyncTask extends AsyncTask <Item, Void, Void> {
        private ItemDAO itemDAO;

        InsertItemAsyncTask(ItemDAO itemDAO) {
            this.itemDAO = itemDAO;
        }

        @Override
        protected Void doInBackground(Item... items) {
            itemDAO.insertItem(items[0]);
            return null;
        }
    }

    private static class DeleteItemsForPurchaseIdAsyncTask extends AsyncTask<Integer, Void, Void>{
        private ItemDAO itemDAO;

        DeleteItemsForPurchaseIdAsyncTask(ItemDAO itemDAO) {
            this.itemDAO = itemDAO;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            itemDAO.removeItemsForPurchaseId(integers[0]);
            return null;
        }
    }

    //Categories

    private static class GetCategoriesAsyncTask extends AsyncTask<Void, Void, List<Category>>{
        private CategoryDAO categoryDAO;

        GetCategoriesAsyncTask(CategoryDAO categoryDAO) {
            this.categoryDAO = categoryDAO;
        }

        @Override
        protected List<Category> doInBackground(Void... voids) {
            return categoryDAO.getCategories();
        }
    }

    private static class GetSubcategoriesAsyncTask extends AsyncTask<Void, Void, List<Subcategory>>{
        private SubcategoryDAO subcategoryDAO;

        GetSubcategoriesAsyncTask(SubcategoryDAO subcategoryDAO) {
            this.subcategoryDAO = subcategoryDAO;
        }

        @Override
        protected List<Subcategory> doInBackground(Void... voids) {
            return subcategoryDAO.getSubcategories();
        }
    }















}
