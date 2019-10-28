package com.yordan.finance.view.adapter;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Placeholder;
import androidx.transition.TransitionManager;

import com.yordan.finance.R;
import com.yordan.finance.model.Category;
import com.yordan.finance.model.Subcategory;
import com.yordan.finance.utils.CategoriesUtils;
import com.yordan.finance.utils.CategoryPersonalization;

import java.util.HashMap;
import java.util.List;

public class CategoriesAdapter extends BaseExpandableListAdapter {

    private String TAG = this.getClass().getSimpleName();

    private Context context;
    private List<Category> rootCategories;
    private HashMap<Category, List<Subcategory>> subcategories;



    public CategoriesAdapter(Context context) {
        this.context = context;
        rootCategories = CategoriesUtils.getCategories();
        subcategories = CategoriesUtils.getSubcategories();

        if(rootCategories.isEmpty() || subcategories.isEmpty()){
            CategoriesUtils.initCategories((Application) context.getApplicationContext());
            rootCategories = CategoriesUtils.getCategories();
            subcategories = CategoriesUtils.getSubcategories();
        }
    }

    @Override
    public int getGroupCount() {
        Log.d(TAG, "getGroupCount: " + rootCategories.size());
        return rootCategories.size();
    }

    @Override
    public int getChildrenCount(int i) {

        return subcategories.get(rootCategories.get(i)).size();
    }

    @Override
    public Object getGroup(int i) { return rootCategories.get(i); }

    @Override
    public Object getChild(int i, int i1) {
        return subcategories.get(rootCategories.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String category = rootCategories.get(i).getName();

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.vh_categories, null);
        }

        TextView tvCategoryName = view.findViewById(R.id.tv_categories);
        ImageView ivCategoryIcon = view.findViewById(R.id.iv_category_icon);
        Placeholder phNameAfterCollapse = view.findViewById(R.id.ph_name_after_collapse);
        ConstraintLayout constraintLayout = view.findViewById(R.id.cl_categories_list);

        ivCategoryIcon.setImageResource(CategoryPersonalization.categoryIcons[i]);

        tvCategoryName.setText(category);


        if(b){
            TransitionManager.beginDelayedTransition(constraintLayout);
            phNameAfterCollapse.setContentId(R.id.tv_categories);

        }else{
            phNameAfterCollapse.setContentId(0);
        }

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String subCategory = subcategories.get(rootCategories.get(i)).get(i1).getName();

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.vh_subcategories, viewGroup, false);
        }

        TextView textView = view.findViewById(R.id.tv_subcategory);
        ImageView subcatColor = view.findViewById(R.id.iv_subcategory_color);

        textView.setText(subCategory);
        subcatColor.setImageResource(CategoryPersonalization.categoriesColors[i]);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
