package com.yordan.finance.utils;

import com.yordan.finance.R;

public class CategoryPersonalization {
    public static int[] categoryIcons = {
            R.drawable.ic_category_home, R.drawable.ic_categories_utilities,
            R.drawable.ic_category_groceries, R.drawable.ic_categories_transportation,
            R.drawable.ic_categories_personal, R.drawable.ic_categories_entertainment,
            R.drawable.ic_categories_health, R.drawable.ic_category_savings,
            R.drawable.ic_categories_others
    };

    public static int[] categoriesColors = {
            R.color.colorPie, R.color.colorPie1, R.color.colorPie2,
            R.color.colorPie3, R.color.colorPie4, R.color.colorPie5,
            R.color.colorPie6, R.color.colorPie7, R.color.colorPie8
    };



    public static int getIconForCategory(int category){
        int index = category / 10 - 1;
        return categoryIcons[index];
    }

    public static int getColorForCategory(int category){
        int index = category / 10 - 1;
        return categoriesColors[index];
    }


}
