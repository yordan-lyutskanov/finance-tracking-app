package com.yordan.finance.view.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;
import com.yordan.finance.view.callback.QrScanConfirmationListener;
import com.yordan.finance.view.ui.dialogs.ConfirmQrScanDialog;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class ActivityQRScan extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int CONFIRM_CODE_OK = 1;
    public static final int REQUEST_CAMERA = 1;

    public static final String EXTRA_DATESTRING = "EX_DATESTRING";
    public static final String EXTRA_AMOUNT = "EX_AMOUNT";
    public static final String EXTRA_EXPENSE_NAME = "EX_NAME";
    private ZXingScannerView scannerView;
    private int mSubcategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().hasExtra("subcategory")){
            mSubcategory = getIntent().getIntExtra("subcategory", -1);
        }

        scannerView = new ZXingScannerView(this);


        setContentView(scannerView);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                Toast.makeText(this, "Permission is granted", Toast.LENGTH_SHORT).show();
            }else{
                requestPermission();
            }
        }
    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume(){
        super.onResume();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                if(scannerView == null){
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }else {

            }
        }
    }

    @Override
    public void onDestroy(){
       super.onDestroy();
       scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        String scanResult = result.getText();

        /*
        * The following info is obtained after splitting the QR String result:
        * 1. 8 digit fiscal memory number
        * 2. 6 digit purchase number
        * 3. date
        * 4. time
        * 5. amount
        * */

        String[] expenseInfo = scanResult.split("\\*");

        String amountString = expenseInfo[4];
        String dateString = expenseInfo[2];

        ConfirmQrScanDialog dialog = new ConfirmQrScanDialog(new QrScanConfirmationListener() {
            @Override
            public void onDialogDismissed(int confirmationCode) {
                if(confirmationCode == CONFIRM_CODE_OK){
                    //CREATE THE NEW ITEM AND ADD IT TO THE DB, then go back to choosing category
                    //on stby to create a new expense.

                    Intent intent = getIntent();
                    String expenseName = intent.getStringExtra(EXTRA_EXPENSE_NAME);
                    Toast.makeText(getApplicationContext(), expenseName, Toast.LENGTH_SHORT).show();

                    intent.putExtra(EXTRA_EXPENSE_NAME, expenseName);
                    intent.putExtra(EXTRA_DATESTRING, dateString);
                    intent.putExtra(EXTRA_AMOUNT, amountString);
                    setResult(RESULT_OK, intent);
                    finish();

                }else{
                    finish();
                    //Go back to previous activity
                }
            }
        }, mSubcategory, dateString, amountString);

        dialog.show(getSupportFragmentManager(), "TAGG");
    }


}
