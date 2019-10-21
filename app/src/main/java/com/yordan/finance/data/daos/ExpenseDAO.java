package com.yordan.finance.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.yordan.finance.model.Expense;

import java.util.List;

@Dao
public interface ExpenseDAO {
    @Insert
    void insertExpense(Expense expense);

    @Delete
    void deleteExpense(Expense expense);

    @Update
    void updateExpense(Expense expense);

    @Query("DELETE FROM expenses")
    void deleteAll();

    @Query("SELECT * FROM expenses")
    LiveData<List<Expense>> getObservableExpenses();

    @Query("SELECT * FROM expenses")
    List<Expense> getAllExpenses();

    @Query("SELECT * FROM expenses WHERE _id LIKE :id")
    Expense queryExpenseById(int id);

    @Query("SELECT * FROM expenses WHERE date > :date")
    LiveData<List<Expense>> queryExpenseByDate(int date);

    @RawQuery(observedEntities = Expense.class)
    LiveData<List<Expense>> queryFilterSort(SupportSQLiteQuery query);

}
