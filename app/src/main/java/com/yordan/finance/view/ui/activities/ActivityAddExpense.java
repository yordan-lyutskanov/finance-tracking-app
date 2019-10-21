package com.yordan.finance.view.ui.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.yordan.finance.R;
import com.yordan.finance.utils.CategoriesUtils;
import com.yordan.finance.model.Item;
import com.yordan.finance.utils.DateUtils;
import com.yordan.finance.utils.CategoryPersonalization;
import com.yordan.finance.utils.PriceUtils;
import com.yordan.finance.view.adapter.ItemsRvAdapter;
import com.yordan.finance.view.callback.ChangeExpenseDialogListener;
import com.yordan.finance.view.ui.dialogs.ChangeExpenseDialog;
import com.yordan.finance.viewmodel.AppViewModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityAddExpense extends AppCompatActivity implements ChangeExpenseDialogListener {


    public static final String DETAILED_VIEW_BOOLEAN = "A123aavV";
    private static final int CREATE_QR_ITEM_REQUEST_CODE = 251;
    private static final String SINGLE_ITEM_STRING = "Single item purchase";
    private static final String UNNAMED_EXPENSE = "Unnamed Expense";
    private static final String SUBCATEGORY_INDEX = "subcategory";
    private static final String KEY_EXPENSE_NAME = "key_NamE";


    TextView tvExpenseName;
    TextView tvCategorySubcategory;
    ImageView ivCategoryIcon;
    ImageView ivCategoryColor;
    Button btAddItem;
    Button btSubmit;
    EditText etItemName;
    EditText etItemPrice;
    RecyclerView rvListItems;
    ItemsRvAdapter mAdapter;
    ImageView ivSwitchConstraints;
    TextView tvSwitchConstraints;
    ImageView ivChangeExpense;
    ImageView ivScan;
    ConstraintLayout mConstraint;
    ConstraintSet constraintSetSimple = new ConstraintSet();
    ConstraintSet constraintSetDetailed = new ConstraintSet();
    TextView tvPrice;
    TextView tvDate;
    TextView tvLastItem;
    TextInputLayout tilName;
    TextInputLayout tilPrice;
    LinearLayout llProgressInfo;

    private AppViewModel mViewModel;
    private RecyclerView.LayoutManager mLayoutManager;
    private String expenseName;
    private int mCategory;
    private int mExpenseId;
    private long mDate;
    private boolean detailedViewShowing;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            detailedViewShowing = savedInstanceState.getBoolean(DETAILED_VIEW_BOOLEAN);
        }

        if(detailedViewShowing){
            setContentView(R.layout.activity_add_purchase1);
        }else{
            setContentView(R.layout.activity_add_purchase_simple1);
        }


        assignAllViews();
        setTextViewsFromIntentOrInstanceState(savedInstanceState);
        initRecyclerView();
        initViewModel();
        personalizeViewForCategory(mCategory);

        etItemName.setOnFocusChangeListener((View v, boolean hasFocus) -> {if(!hasFocus)hideKeyboard(v);});
        etItemPrice.setOnFocusChangeListener((View v, boolean hasFocus) -> {if(!hasFocus)hideKeyboard(v);});

        mConstraint.setOnClickListener(View::requestFocus);
        etItemPrice.setOnClickListener(View::requestFocus);

        ivSwitchConstraints.setOnClickListener((v) -> swapConstraints());
        tvSwitchConstraints.setOnClickListener((v) -> swapConstraints());

        ivChangeExpense.setOnClickListener((v) -> openChangeDialog());

        ivScan.setOnClickListener((v) -> startScannerActivity());
        tvDate.setOnClickListener((v) -> openChangeDialog());
        tvExpenseName.setOnClickListener((v -> openChangeDialog()));

        btAddItem.setOnClickListener(view -> {
                if(addItem()){
                    mAdapter.setData(mViewModel.getTempItems());
                    tvPrice.setText(PriceUtils.formatPrice(mViewModel.getTempExpense().getAmount()));
                }
        });

        btSubmit.setOnClickListener((v) -> {
            if(!detailedViewShowing){
                if(addSimpleExpense(etItemPrice.getText().toString().trim())){
                    goBackToMain();
                }
            }else{
                if(addDetailedExpense()){
                    goBackToMain();
                }
            }
        });

        tvPrice.setText("");
    }



    private void assignAllViews(){
        tilPrice = findViewById(R.id.til_price);
        tilName = findViewById(R.id.til_name);
        tvSwitchConstraints = findViewById(R.id.tv_rearange);
        ivSwitchConstraints = findViewById(R.id.iv_switch_constraints);
        ivCategoryColor = findViewById(R.id.iv_category_color);
        ivCategoryIcon = findViewById(R.id.iv_category_icon);
        ivChangeExpense = findViewById(R.id.iv_change_expense);
        ivScan = findViewById(R.id.iv_scan);

        llProgressInfo = findViewById(R.id.ll_progress_bar);

        mConstraint = findViewById(R.id.add_expense_root_element);

        tvExpenseName = findViewById(R.id.tv_category);
        tvCategorySubcategory = findViewById(R.id.tv_subcategory);
        tvPrice = findViewById(R.id.tvPrice);
        tvDate = findViewById(R.id.tv_date);
        tvLastItem = findViewById(R.id.tv_last_item);

        btAddItem = findViewById(R.id.btAddItem);
        btSubmit = findViewById(R.id.bt_submit);

        etItemName = findViewById(R.id.etItemName);
        etItemPrice = findViewById(R.id.etItemPrice);

        rvListItems = findViewById(R.id.rvListItems);

        constraintSetDetailed.clone(this, R.layout.activity_add_purchase1);
        constraintSetSimple.clone(mConstraint);

        registerForContextMenu(rvListItems);
    }

    private void personalizeViewForCategory(int mSubcategory) {
        ivCategoryIcon.setImageResource(CategoryPersonalization.getIconForCategory(mSubcategory));

        int categoryColor = CategoryPersonalization.getColorForCategory(mSubcategory);

        ivCategoryColor.setImageResource(categoryColor);
        btSubmit.setBackgroundResource(categoryColor);
        btAddItem.setBackgroundResource(categoryColor);

        etItemName.getBackground().
                setColorFilter(getResources().getColor(categoryColor), PorterDuff.Mode.SRC_ATOP);

        etItemPrice.getBackground().
                setColorFilter(getResources().getColor(categoryColor), PorterDuff.Mode.SRC_ATOP);

    }

    private boolean addItem(){
        String itemName = etItemName.getText().toString();
        String itemPrice = etItemPrice.getText().toString();

        if(isNameValid() && isPriceValid(itemPrice)){
            mViewModel.addItemToTemp(expenseName, itemName, itemPrice, mExpenseId, mCategory, mDate);
            tvLastItem.setText("Item " + itemName + " added to the list." );
            return true;
        }else{
            System.out.println("Name is  NOOOT valid.");
            return false;
        }
    }

    private void removeItem(Item item){
        String itemName = item.getName();
        mViewModel.removeItemFromTemp(item);
        tvLastItem.setText("Item " + itemName + " removed from the list." );
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = mAdapter.getPosition();

        switch (item.getItemId()){
            case R.id.popup_item_remove: // Remove
                removeItem(mAdapter.getItemAtPosition(position));
                mAdapter.notifyDataSetChanged();
                tvPrice.setText(PriceUtils.formatPrice(mViewModel.getTempExpense().getAmount()));
                break;
        }

        return super.onContextItemSelected(item);
    }

    private boolean addSimpleExpense(String itemPrice){
        if(isPriceValid(itemPrice)){
            mViewModel.addItemToTemp(expenseName, "Single item purchase.", itemPrice, mExpenseId, mCategory, mDate);
            mViewModel.saveTempToStorage();
            return true;
        }
        return false;
    }

    private boolean addSimpleExpense(String expensePrice, long mDate, String expenseName){
        if(isPriceValid(expensePrice)){
            if(expenseName.isEmpty()){
                expenseName = UNNAMED_EXPENSE;
            }
            mViewModel.addItemToTemp(expenseName, SINGLE_ITEM_STRING, expensePrice, mExpenseId, mCategory, mDate);
            mViewModel.saveTempToStorage();
            return true;
        }
        return false;
    }

    private boolean addDetailedExpense(){
        if(mViewModel.getTempExpense() != null && !mViewModel.getTempItems().isEmpty()){
            mViewModel.saveTempToStorage();
            return true;
        }else{
            Toast.makeText(this, "Please add at least one item or change to simple Expense.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void setTextViewsFromIntentOrInstanceState(Bundle instanceState){
        Intent currentIntent = getIntent();
        String categoryName = null;


        if(instanceState != null){
            mCategory = instanceState.getInt("SUBCATEGORY_INDEX");
            mDate = instanceState.getLong("DATE");
            expenseName = instanceState.getString(KEY_EXPENSE_NAME);

            categoryName = CategoriesUtils.getCategoryNameFromId(mCategory)
                + " - " + CategoriesUtils.getSubcategoryNameFromId(mCategory);
        }else{
            if(currentIntent.hasExtra("SUBCATEGORY")){
                mCategory = currentIntent.getIntExtra("SUBCATEGORY", -1);
                categoryName = CategoriesUtils.getCategoryNameFromId(mCategory) +
                    " - " + CategoriesUtils.getSubcategoryNameFromId(mCategory);
            }

            expenseName = UNNAMED_EXPENSE;
            mDate = System.currentTimeMillis();
        }

        tvExpenseName.setText(expenseName);
        tvCategorySubcategory.setText(categoryName);
        tvDate.setText(DateUtils.longDateToString(mDate));
    }


    private void initRecyclerView(){
        mAdapter = new ItemsRvAdapter();
        mLayoutManager = new LinearLayoutManager(this);
        rvListItems.setLayoutManager(mLayoutManager);
        rvListItems.setAdapter(mAdapter);
    }

    private void initViewModel(){
        mViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        mViewModel.getExpenses().observe(this, expenses -> {
            if(expenses.size() != 0){
                mExpenseId = expenses.get(expenses.size() - 1).getId() + 1;
            }else{
                mExpenseId = 0;
            }
        });

        if(mViewModel != null && mViewModel.getTempExpense() != null){
            mAdapter.setData(mViewModel.getTempItems());
        }
    }


    private void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void swapConstraints(){
        TransitionManager.beginDelayedTransition(mConstraint);

        if(detailedViewShowing){
            constraintSetSimple.applyTo(mConstraint);
            tvSwitchConstraints.setText(R.string.detailed_expense_label);
            detailedViewShowing = false;
        }else{
            constraintSetDetailed.applyTo(mConstraint);
            tvSwitchConstraints.setText(R.string.simple_expense_label);
            detailedViewShowing = true;
        }
    }

    private void openChangeDialog(){
        ChangeExpenseDialog dialog = null;
        if(expenseName == null) dialog = new ChangeExpenseDialog(mCategory, "Unnamed Expense");
        else dialog = new ChangeExpenseDialog(mCategory, expenseName);
        dialog.show(getSupportFragmentManager(), "TAG");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("SUBCATEGORY_INDEX", mCategory);
        outState.putLong("DATE", mDate);
        outState.putString(KEY_EXPENSE_NAME, this.expenseName);
        outState.putBoolean(DETAILED_VIEW_BOOLEAN, detailedViewShowing);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onReturnedValue(int categorySubcategory, long date, String expenseName) {
        this.mDate = date;
        tvDate.setText(DateUtils.longDateToString(mDate));

        this.expenseName = expenseName;
        tvExpenseName.setText(expenseName);

        mCategory = categorySubcategory;
        tvCategorySubcategory.setText(CategoriesUtils.getSubcategoryNameFromId(mCategory));
        personalizeViewForCategory(categorySubcategory);
    }

    private boolean isNameValid(){
        if(etItemName.getText().toString().trim().isEmpty()){
            tilName.setErrorEnabled(true);
            tilName.setError("You need to enter a valid Name.");
            return false;
        }
        return true;
    }

    private boolean isPriceValid(String price){
        if(price.trim().isEmpty()){
            tilPrice.setErrorEnabled(true);
            tilPrice.setError("You need to enter a valid Price.");
            return false;
        }
        return true;
    }

    private void goBackToMain(){
        Intent intent = new Intent(this, ActivityExpenseList.class);
        startActivity(intent);
        finish();
    }

    private void startScannerActivity(){
        Intent startScanning = new Intent(ActivityAddExpense.this, ActivityQRScan.class);
        startScanning.putExtra(SUBCATEGORY_INDEX, mCategory);
        startActivityForResult(startScanning, CREATE_QR_ITEM_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == CREATE_QR_ITEM_REQUEST_CODE){
                String date = data.getStringExtra(ActivityQRScan.EXTRA_DATESTRING);
                String amount = data.getStringExtra(ActivityQRScan.EXTRA_AMOUNT);
                String expenseName = data.getStringExtra(ActivityQRScan.EXTRA_EXPENSE_NAME);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                try{
                    Date dateFormatted = format.parse(date);
                    long dateMillis = dateFormatted.getTime();
                    addSimpleExpense(amount, dateMillis, expenseName);
                    Toast.makeText(this, "New expense added successfully.", Toast.LENGTH_SHORT).show();
                    goBackToMain();
                }catch (ParseException e){
                    Toast.makeText(this, "Couldn't read the Date", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
