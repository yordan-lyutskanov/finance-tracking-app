package com.yordan.finance.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


import com.yordan.finance.data.daos.CategoryDAO;
import com.yordan.finance.data.daos.ExpenseDAO;
import com.yordan.finance.data.daos.ItemDAO;
import com.yordan.finance.data.daos.SubcategoryDAO;
import com.yordan.finance.utils.CategoriesUtils;
import com.yordan.finance.model.Category;
import com.yordan.finance.model.Subcategory;
import com.yordan.finance.model.Item;
import com.yordan.finance.model.Expense;
import java.util.List;
import java.util.Set;

@Database(entities = {Item.class, Expense.class, Category.class, Subcategory.class},
        version = 3, exportSchema = false)
public abstract class FinanceRoomDatabase extends RoomDatabase {

    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_SUBCATEGORIES = "subcategories";

    public abstract ItemDAO itemDAO();
    public abstract ExpenseDAO expenseDAO();
    public abstract CategoryDAO categoryDAO();
    public abstract SubcategoryDAO subcategoryDAO();


    private static FinanceRoomDatabase INSTANCE;

    public static  synchronized FinanceRoomDatabase getDatabase(Context context){
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), FinanceRoomDatabase.class,
                    "finance_database")
                    .addCallback(roomCallback)
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .build();
        }
        return INSTANCE;
    }

    /*
     Adding callback to populate the DB only the first time when it is created.
     I am currently using this to add default categories to the DB
     */

    private static FinanceRoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask <Void, Void, Void> {
        private CategoryDAO categoriesDAO;
        private SubcategoryDAO subcategoryDAO;

        private PopulateDbAsyncTask(FinanceRoomDatabase db){
            this.categoriesDAO = db.categoryDAO();
            this.subcategoryDAO = db.subcategoryDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Set<Category> recommendedCategories = CategoriesUtils.getRecommendedCategoriesSet();
            for(Category category : recommendedCategories){
                categoriesDAO.insertCategory(category);
            }

            List<Subcategory> recommendedSubcategories = CategoriesUtils.getRecommendedSubcategories();
            for(Subcategory subcategory : recommendedSubcategories){
                subcategoryDAO.insertSubcategory(subcategory);
            }

            return null;
        }
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2){
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE expenses ADD COLUMN timestamp INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE items ADD COLUMN timestamp INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("CREATE TABLE categories (id INTEGER NOT NULL PRIMARY KEY, name TEXT)");
            database.execSQL("CREATE TABLE subcategories (id INTEGER NOT NULL PRIMARY KEY, categoryId INTEGER NOT NULL, name TEXT)");

            Set<Category> recomendedCategories = CategoriesUtils.getRecommendedCategoriesSet();

            for (Category category : recomendedCategories) {
                ContentValues categoryInsert = new ContentValues();
                categoryInsert.put("id", category.getId());
                categoryInsert.put("name", category.getName());
                database.insert(TABLE_CATEGORIES, SQLiteDatabase.CONFLICT_REPLACE, categoryInsert);
            }

            for(Subcategory subcategory: CategoriesUtils.getRecommendedSubcategories()){
                ContentValues subcategoryInsert = new ContentValues();
                subcategoryInsert.put("id", subcategory.getId());
                subcategoryInsert.put("categoryId", subcategory.getCategoryId());
                subcategoryInsert.put("name", subcategory.getName());
                database.insert(TABLE_SUBCATEGORIES, SQLiteDatabase.CONFLICT_REPLACE, subcategoryInsert);
            }
        }
    };


}
