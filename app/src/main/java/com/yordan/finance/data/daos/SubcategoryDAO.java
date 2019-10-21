package com.yordan.finance.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.yordan.finance.model.Category;
import com.yordan.finance.model.Subcategory;

import java.util.List;

@Dao
public interface SubcategoryDAO {
    @Insert
    void insertSubcategory(Subcategory subcategory);

    @Delete
    void deleteSubcategory(Subcategory subcategory);

    @Update
    void updateSubcategory(Subcategory subcategory);

    @Query("SELECT * FROM subcategories")
    List<Subcategory> getSubcategories();

    @Query("SELECT * FROM subcategories")
    LiveData<List<Subcategory>> getSubcategoriesObservable();

}
