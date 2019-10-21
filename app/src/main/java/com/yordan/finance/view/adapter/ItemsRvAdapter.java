package com.yordan.finance.view.adapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yordan.finance.R;
import com.yordan.finance.model.Item;
import com.yordan.finance.utils.PriceUtils;

import java.util.List;

public class ItemsRvAdapter extends RecyclerView.Adapter<ItemsRvAdapter.ItemsViewHolder> {

    private List<Item> items;
    private int position;


    private static final String TAG = ItemsRvAdapter.class.getSimpleName();

    public class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView itemName;
        TextView itemPrice;

        public ItemsViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.tv_item_name);
            itemPrice = itemView.findViewById(R.id.tv_price);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(0, R.id.popup_item_remove, 0, "Remove");
        }
    }



    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.vh_items, parent, false);
        return new ItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        Item item = items.get(position);

        holder.itemView.setOnLongClickListener((v) -> {
            setPosition(holder.getAdapterPosition());
            return false;
        });

        holder.itemPrice.setText(PriceUtils.formatPrice(item.getPrice()));
        holder.itemName.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }


    public void setData(List<Item> items){
        this.items = items;
        notifyDataSetChanged();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Item getItemAtPosition(int position){
        return items.get(position);
    }

    @Override
    public void onViewRecycled(@NonNull ItemsViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }
}
