package com.yordan.finance.utils;
import android.app.Application;
import android.util.Log;

import com.yordan.finance.App;
import com.yordan.finance.R;
import com.yordan.finance.data.repository.Repository;
import com.yordan.finance.model.Category;
import com.yordan.finance.model.Subcategory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CategoriesUtils {

    private static List<Category> rootCategories = new ArrayList<>();
    private static HashMap<Category, List<Subcategory>> subCategories = new HashMap<>();
    private final String TAG = this.getClass().getSimpleName();

    private static String[] recommendedCategoriesNames = {
            (App.getContext().getString(R.string.housing_root)),
            (App.getContext().getString(R.string.utilities_root)),
            (App.getContext().getString(R.string.groceries_root)),
            (App.getContext().getString(R.string.transportation_root)),
            (App.getContext().getString(R.string.personal_root)),
            (App.getContext().getString(R.string.entertainment_root)),
            (App.getContext().getString(R.string.health_root)),
            (App.getContext().getString(R.string.savings_root)),
            (App.getContext().getString(R.string.others_root))
    };

    private static String[][] recommendedSubcategoriesNames = {
            App.getContext().getResources().getStringArray(R.array.housing_subcat),
            App.getContext().getResources().getStringArray(R.array.utilities_subcategories),
            App.getContext().getResources().getStringArray(R.array.food_subcategories),
            App.getContext().getResources().getStringArray(R.array.transportation_subcategories),
            App.getContext().getResources().getStringArray(R.array.personal_subcategories),
            App.getContext().getResources().getStringArray(R.array.entertainment_subcategories),
            App.getContext().getResources().getStringArray(R.array.health_subcategories),
            App.getContext().getResources().getStringArray(R.array.savings_subcategories),
            App.getContext().getResources().getStringArray(R.array.others_subcategories)
    };

    public static List<Category> getCategories() {
        return rootCategories;
    }

    public static List<String> getCategoriesNames(){
        List<String> categoriesNames = new ArrayList<>();
        for(Category c : rootCategories){
            categoriesNames.add(c.getName());
        }
        return categoriesNames;
    }

    public static HashMap<Category, List<Subcategory>> getSubcategories() {
        return subCategories;
    }

    public static void initCategories(Application app){
        Repository repository = Repository.getInstance(app);
        rootCategories = repository.getCategories();
        List<Subcategory> subcategories = repository.getSubcategories();

        for(Category c : rootCategories){
            subCategories.put(c, new ArrayList<>());
            for(Subcategory s : subcategories){
                if(s.getCategoryId() == c.getId() - 1){
                    subCategories.get(c).add(s);
                }
            }
        }
    }

    public static String getCategoryNameFromId(int id){
        Category category = rootCategories.get(id / 10 - 1);
        return category.getName();
    }

    public static String getSubcategoryNameFromId(int id){
        Category category = rootCategories.get(id / 10 - 1);

        int subcategoryId = ((id % 10) - 1);
        Subcategory subcategory = subCategories.get(category).get(subcategoryId);

        return subcategory.getName();
    }

    public static Set<Category> getRecommendedCategoriesSet(){
        Set<Category> recommendedCategories = new LinkedHashSet<>();

        for(int i = 0; i < recommendedCategoriesNames.length; i++){
            recommendedCategories.add(new Category(i + 1, recommendedCategoriesNames[i]));
        }

        return recommendedCategories;
    }

    public static List<Subcategory> getRecommendedSubcategories(){
        Set<Category> recommendedCategories = CategoriesUtils.getRecommendedCategoriesSet();
        List<Subcategory> recommendedSubcategories = new ArrayList<>();

        for(int i = 0; i < recommendedCategories.size(); i++) {
            for(String name : recommendedSubcategoriesNames[i]){
                recommendedSubcategories.add(new Subcategory(i, name));
            }
        }
        return recommendedSubcategories;
    }
}

