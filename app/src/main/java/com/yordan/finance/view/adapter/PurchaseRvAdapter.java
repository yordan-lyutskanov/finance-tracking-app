package com.yordan.finance.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.yordan.finance.R;
import com.yordan.finance.utils.CategoriesUtils;
import com.yordan.finance.model.Item;
import com.yordan.finance.utils.DateUtils;
import com.yordan.finance.model.Expense;
import com.yordan.finance.utils.CategoryPersonalization;
import com.yordan.finance.utils.PriceUtils;
import com.yordan.finance.view.callback.myDiffutilCallback;

import java.util.ArrayList;
import java.util.List;

public class PurchaseRvAdapter extends RecyclerView.Adapter<PurchaseRvAdapter.ExpenseViewHolder> {

    private final String TAG = this.getClass().getSimpleName();
    private List<Expense> mExpenses = new ArrayList<>();
    private List<Item> mItmes = new ArrayList<>();
    private int position;
    private Activity callingActivity;

    public PurchaseRvAdapter(Activity callingActivity) {
        this.callingActivity = callingActivity;
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ConstraintLayout clItems;
        TextView tvItemNames;
        TextView tvItemPrices;
        TextView tvAmount;
        TextView tvExpenseName;
        TextView tvDescription;
        TextView tvDate;
        ImageView ivCategoryColor;
        ImageView ivCategoryColor2;
        ImageView ivCategoryColor3;


        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvExpenseName = itemView.findViewById(R.id.tv_expense_name);
            tvItemNames = itemView.findViewById(R.id.tv_extra_names);
            tvItemPrices = itemView.findViewById(R.id.tv_extra_prices);
            ivCategoryColor = itemView.findViewById(R.id.iv_category_color);
            ivCategoryColor2 = itemView.findViewById(R.id.iv_category_color2);
            ivCategoryColor3 = itemView.findViewById(R.id.iv_category_color3);
            clItems = itemView.findViewById(R.id.cl_extra);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(Menu.NONE, R.id.popup_item_edit, Menu.NONE, "Edit");
            contextMenu.add(Menu.NONE, R.id.popup_item_remove, Menu.NONE, "Remove");
        }
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.vh_purchases, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = mExpenses.get(position);
        int categoryColor = CategoryPersonalization.getColorForCategory(expense.getCategory());

        StringBuilder nameBuilder = new StringBuilder();
        StringBuilder priceBuilder = new StringBuilder();
        for(Item i : mItmes){
            if(i.getPurchaseId() == expense.getId()){
                nameBuilder.append("\n" + i.getName());
                priceBuilder.append("\n" + PriceUtils.formatPrice(i.getPrice()));
            }
        }

        holder.tvItemNames.setText(nameBuilder);
        holder.tvItemPrices.setText(priceBuilder);
        holder.tvDescription.setText(CategoriesUtils.getCategoryNameFromId(expense.getCategory()));
        holder.tvAmount.setText(PriceUtils.formatPrice(expense.getAmount()));
        holder.tvExpenseName.setText(expense.getName());
        holder.tvDate.setText(DateUtils.intDateToString(expense.getDate()).toString());
        holder.ivCategoryColor.setImageResource(categoryColor);
        holder.ivCategoryColor2.setImageResource(categoryColor);
        holder.ivCategoryColor3.setImageResource(categoryColor);


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(holder.clItems.getVisibility()){
                    case View.GONE:
                        holder.clItems.setVisibility(View.VISIBLE);
                        notifyItemChanged(position);
                        break;
                    case View.VISIBLE:
                        holder.clItems.setVisibility(View.GONE);
                        notifyItemChanged(position);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mExpenses.size();
    }

    public void setExpenses(List<Expense> expenses){
        this.mExpenses = expenses;
        notifyDataSetChanged();
    }

    public void setItems(List<Item> items){
        this.mItmes = items;
        notifyDataSetChanged();
    }

    public Expense getExpenseAt(int position){
        return mExpenses.get(position);
    }

    public List<Expense> getExpenses(){
        return mExpenses;
    }

    public void updateList(List<Expense> newList){
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new myDiffutilCallback(mExpenses, newList));
        diffResult.dispatchUpdatesTo(this);
    }

    private void setPosition(int position){
        this.position = position;
    }

    public int getPosition(){
        return position;
    }

    @Override
    public void onViewRecycled(@NonNull ExpenseViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }
}
