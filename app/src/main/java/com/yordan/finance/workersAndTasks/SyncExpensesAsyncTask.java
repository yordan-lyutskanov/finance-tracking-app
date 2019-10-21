package com.yordan.finance.workersAndTasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.yordan.finance.model.Expense;
import com.yordan.finance.model.Item;
import com.yordan.finance.data.repository.Repository;
import com.yordan.finance.sync.FinanceCredentials;
import com.yordan.finance.utils.ServerUtils;

import java.net.SocketTimeoutException;
import java.util.List;

public class SyncExpensesAsyncTask extends AsyncTask<FinanceCredentials, Void, Void> {
    private static final String NUM_DELETED_EXPENSES = "NumberOfDeletedExpenses" ;
    private static final String NUM_ITEMS_DELETED = "NumberOfItemsDeleted";
    private final String TAG = getClass().getSimpleName();

    private Repository repository;



    public SyncExpensesAsyncTask(Repository repository) {
        this.repository = repository;
    }

    @Override
    protected Void doInBackground(FinanceCredentials... financeCredentials) {
        try{

            List<Expense> expensesOnDevice = repository.getExpensesList();
            List<Expense> expensesOnServer = ServerUtils.getExpenses(financeCredentials[0]);
            List<Item> itemsOnDevice = repository.getAllItems();

            if(expensesOnDevice != null && expensesOnDevice.isEmpty()){
                Log.d(TAG, "doInBackground: No data found in the phone. Restoring from server.");

                if(expensesOnServer != null && !expensesOnServer.isEmpty()){
                    Log.d(TAG, "doInBackground: Restoring data from Server.");
                    for(Expense e : expensesOnServer){
                        repository.insertExpense(e);
                    }
                }else{
                    Log.d(TAG, "doInBackground: No data found on the server.");
                }

                return null;
            }else{
                Log.d(TAG, "doInBackground: Syncing with the server.");
                ServerUtils.syncExpenses(financeCredentials[0], expensesOnDevice, itemsOnDevice);
                return null;
            }
        }catch (SocketTimeoutException e){
            Log.d(TAG, "doInBackground: No connection to the server.");
            return null;
        }


    }
}
