package com.yordan.finance.view.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.yordan.finance.R;
import com.yordan.finance.utils.CategoriesUtils;
import com.yordan.finance.utils.CategoryPersonalization;
import com.yordan.finance.view.callback.QrScanConfirmationListener;
import com.yordan.finance.view.ui.activities.ActivityQRScan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ConfirmQrScanDialog extends AppCompatDialogFragment{

    private static int CONFIRM_CODE_OK = 1;
    private static int CONFIRM_CODE_CANCEL = -1;

    private int id;
    private String date;
    private String amount;
    private QrScanConfirmationListener listener;

    public ConfirmQrScanDialog(QrScanConfirmationListener listener, int category, String date, String amount) {
        this.id = category;
        this.date = date;
        this.amount = amount;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.confirm_qr_info, null);

        TextView tvCategory = view.findViewById(R.id.tv_category);
        TextView tvSubcategory = view.findViewById(R.id.tv_subcategory);
        TextView tvDate = view.findViewById(R.id.tv_date);
        TextView tvAmount = view.findViewById(R.id.tv_amount);

        ImageView ivCategoryIcon = view.findViewById(R.id.iv_category_icon);
        Button btConfirm = view.findViewById(R.id.bt_confirm);
        Button btCancel = view.findViewById(R.id.bt_cancel);

        EditText etExpenseName = view.findViewById(R.id.et_expense_name);



        tvCategory.setText(CategoriesUtils.getCategoryNameFromId(id));
        tvSubcategory.setText(CategoriesUtils.getSubcategoryNameFromId(id));

        this.date = reformatDateString(date);
        tvDate.setText(date);
        tvAmount.setText(amount + " лв.");

        ivCategoryIcon.setImageResource(CategoryPersonalization.getIconForCategory(id));

        btCancel.setOnClickListener((v)-> {
            listener.onDialogDismissed(CONFIRM_CODE_CANCEL);
            dismiss();
        });

        btConfirm.setOnClickListener((v) -> {
            Intent invokingIntent = getActivity().getIntent();
            String expenseName = etExpenseName.getText().toString();
            invokingIntent.putExtra(ActivityQRScan.EXTRA_EXPENSE_NAME, expenseName);
            listener.onDialogDismissed(CONFIRM_CODE_OK);
            dismiss();
        });

        return builder.setView(view).create();
    }

    private String reformatDateString(String initialDateString){
        SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatOutput = new SimpleDateFormat("dd-MM-yyyy");

        try{
            Date date = formatInput.parse(initialDateString);
            String reformatedDate = formatOutput.format(date);
            return reformatedDate;
        }catch (ParseException e){
            e.printStackTrace();
            return null;
        }

    }


}
