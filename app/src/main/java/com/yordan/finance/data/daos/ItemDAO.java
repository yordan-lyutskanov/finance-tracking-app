package com.yordan.finance.data.daos;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.yordan.finance.model.Item;

import java.util.List;

@Dao
public interface ItemDAO {
    @Insert
    void insertItem(Item item);

    @Delete
    void deleteItem(Item item);

    @Update
    void updateItem(Item item);

    @Query("DELETE FROM items")
    void deleteAllItems();

    @Query("SELECT * FROM items")
    LiveData<List<Item>> getAllItemsObservable();

    @Query("SELECT * FROM items")
    List<Item> getAllItems();

    @Query("SELECT * FROM items WHERE purchaseId LIKE :purchaseId")
    LiveData<List<Item>> getItemsForPurchase(int purchaseId);

    @Query("DELETE FROM items WHERE purchaseId LIKE :purchaseId")
    void removeItemsForPurchaseId(int purchaseId);

    @Query("DELETE FROM items WHERE purchaseId LIKE :itemId")
    void removeItemById(int itemId);
}
