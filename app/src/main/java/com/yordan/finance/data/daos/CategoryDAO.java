package com.yordan.finance.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.yordan.finance.model.Category;
import com.yordan.finance.utils.CategoriesUtils;

import java.util.List;

@Dao
public interface CategoryDAO {
    @Insert
    void insertCategory(Category category);

    @Delete
    void deleteCategory(Category category);

    @Update
    void updateCategory(Category category);

    @Query("SELECT * FROM categories")
    List<Category> getCategories();

    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getCategoriesObservable();

    @Query("SELECT * FROM categories WHERE id LIKE :id")
    Category getCategoryFromId(int id);
}
