package com.yordan.finance.view.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EditItemSpinnerAdapter extends ArrayAdapter<String> {

    Typeface fontOswald = Typeface.createFromAsset(getContext().getAssets(), "fonts/oswald_regular.ttf");

    public EditItemSpinnerAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public EditItemSpinnerAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(fontOswald);
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView dropDownView = (TextView) super.getDropDownView(position, convertView, parent);
        dropDownView.setTypeface(fontOswald);
        return dropDownView;
    }
}
