package com.yordan.finance.view.ui.dialogs;



import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.yordan.finance.R;
import com.yordan.finance.model.Expense;
import com.yordan.finance.utils.DateUtils;
import com.yordan.finance.view.adapter.EditItemSpinnerAdapter;
import com.yordan.finance.view.callback.ChangeExpenseDialogListener;
import com.yordan.finance.view.ui.activities.ActivityAddExpense;
import com.yordan.finance.viewmodel.AppViewModel;

import java.util.Date;


public class ChangeExpenseDialog extends AppCompatDialogFragment {

    private long selectedDate = System.currentTimeMillis();
    private int mSubcategory;
    private String expenseName;
    private Expense expense;
    private AppViewModel viewModel;
    private boolean dialogJustLoaded = true;
    private Spinner categorySpinner;
    private Spinner subcategorySpinner;
    private DatePicker datePicker;
    private EditText etNewPrice;
    private TextView tvCategory;
    private TextView tvSubcategory;
    private EditText etChangeName;
    private Button btSubmit;
    private Button btCancel;

    public ChangeExpenseDialog(int subcategory, String expenseName) {
        this.expense = null;
        this.mSubcategory = subcategory;
        this.expenseName = expenseName;
    }

    public ChangeExpenseDialog(Expense expense, AppViewModel viewModel) {
        this.expenseName = expense.getName();
        this.expense = expense;
        this.viewModel = viewModel;
        this.mSubcategory = expense.getCategory();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_item, null);

        categorySpinner = view.findViewById(R.id.sp_category);
        subcategorySpinner = view.findViewById(R.id.sp_subcategory);
        datePicker = view.findViewById(R.id.calendar_view);
        etNewPrice = view.findViewById(R.id.et_new_price);
        tvCategory = view.findViewById(R.id.tv_category);
        tvSubcategory = view.findViewById(R.id.tv_subcategory);
        etChangeName = view.findViewById(R.id.et_change_name);
        btCancel = view.findViewById(R.id.bt_cancel);
        btSubmit = view.findViewById(R.id.bt_submit);


        Activity activity = getActivity();

        //Modifying the Dialog according to the invoking Activity.
        if(activity instanceof ActivityAddExpense){
            hideViews();
        }else{
            //Activity is instance of ActivityExpenseList
            etNewPrice.setText(expense.getAmount() + "");
            int[] yearMonthDay = DateUtils.parseDate(expense.getDate());
            datePicker.updateDate(yearMonthDay[0], yearMonthDay[1], yearMonthDay[2]);
        }

        etChangeName.setText(expenseName);
        etNewPrice.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
                "fonts/oswald_regular.ttf"));

        datePicker.setMaxDate(System.currentTimeMillis());

        String[] categories = getResources().getStringArray(R.array.categories_root);
        ArrayAdapter<String> categrySpinnerAdapter = new EditItemSpinnerAdapter
                (this.getContext(), R.layout.support_simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(categrySpinnerAdapter);
        categrySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setSelection(mSubcategory / 10 - 1);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<String> subcatAdapter = null;
                switch (i){
                    case 0:
                        String[] subcategories = getResources().getStringArray(R.array.housing_subcat);
                        subcatAdapter = new EditItemSpinnerAdapter(getContext(),
                                android.R.layout.simple_spinner_item, subcategories);
                        break;
                    case 1:
                        String[] subcategories1 = getResources().getStringArray(R.array.utilities_subcategories);
                        subcatAdapter = new EditItemSpinnerAdapter(getContext(),
                                android.R.layout.simple_spinner_item, subcategories1);
                        break;
                    case 2:
                        String[] subcategories2 = getResources().getStringArray(R.array.food_subcategories);
                        subcatAdapter = new EditItemSpinnerAdapter(getContext(),
                                android.R.layout.simple_spinner_item, subcategories2);
                        break;
                    case 3:
                        String[] subcategories3 = getResources().getStringArray(R.array.transportation_subcategories);
                        subcatAdapter = new EditItemSpinnerAdapter(getContext(),
                                android.R.layout.simple_spinner_item, subcategories3);
                        break;
                    case 4:
                        String[] subcategories4 = getResources().getStringArray(R.array.personal_subcategories);
                        subcatAdapter = new EditItemSpinnerAdapter(getContext(),
                                android.R.layout.simple_spinner_item, subcategories4);
                        break;
                    case 5:
                        String[] subcategories5 = getResources().getStringArray(R.array.entertainment_subcategories);
                        subcatAdapter = new EditItemSpinnerAdapter(getContext(),
                                android.R.layout.simple_spinner_item, subcategories5);
                        break;
                    case 6:
                        String[] subcategories6 = getResources().getStringArray(R.array.health_subcategories);
                        subcatAdapter = new EditItemSpinnerAdapter(getContext(),
                                android.R.layout.simple_spinner_item, subcategories6);
                        break;
                    case 7:
                        String[] subcategories7 = getResources().getStringArray(R.array.savings_subcategories);
                        subcatAdapter = new EditItemSpinnerAdapter(getContext(),
                                android.R.layout.simple_spinner_item, subcategories7);
                        break;
                    case 8:
                        String[] subcategories8 = getResources().getStringArray(R.array.others_subcategories);
                        subcatAdapter = new EditItemSpinnerAdapter(getContext(),
                                android.R.layout.simple_spinner_item, subcategories8);
                        break;
                }

                subcategorySpinner.setAdapter(subcatAdapter);
                subcatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                if(dialogJustLoaded){
                    subcategorySpinner.setSelection(mSubcategory % 10 - 1);
                    dialogJustLoaded = false;
                }else{
                    subcategorySpinner.setSelection(0);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] selectedYearMonthDay = new int[]{
                        datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth()
                };
                Date date = DateUtils.parseIntArray(selectedYearMonthDay);
                selectedDate = date.getTime();
                int category = categorySpinner.getSelectedItemPosition() + 1;
                int subcategory = subcategorySpinner.getSelectedItemPosition() + 1;
                int fullCategory = category * 10 + subcategory;
                String expenseName = etChangeName.getText().toString();

                if(getActivity() instanceof ActivityAddExpense){
                    ChangeExpenseDialogListener activity = (ChangeExpenseDialogListener) getActivity();
                    activity.onReturnedValue(mSubcategory, selectedDate, expenseName);
                }else{
                    if(!etNewPrice.getText().toString().trim().isEmpty()){
                        expense.setName(expenseName);
                        expense.setCategory(fullCategory);
                        expense.setAmount(Double.parseDouble(etNewPrice.getText().toString().trim()));
                        expense.setDate((int) (selectedDate / 1000L));
                        expense.setTimestamp(DateUtils.currentDate());
                        viewModel.updateExpense(expense);
                    }else{
                        Snackbar.make(datePicker, "Price cannot be 0", Snackbar.LENGTH_SHORT).show();
                    }

                }
                dismiss();
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return builder.setView(view).create();
    }

    private void hideViews(){
        categorySpinner.setVisibility(View.GONE);
        subcategorySpinner.setVisibility(View.GONE);
        etNewPrice.setVisibility(View.GONE);
        tvCategory.setVisibility(View.GONE);
        tvSubcategory.setVisibility(View.GONE);
    }



}
