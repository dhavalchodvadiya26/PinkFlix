package com.example.streamingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.util.IsRTL;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class DonateListActivity extends AppCompatActivity implements PaymentResultListener {
    Toolbar toolbar;
    EditText edAmount;
    Button btnPay;
    MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        myApplication = MyApplication.getInstance();
        IsRTL.ifSupported(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        edAmount = findViewById(R.id.ed_amount);
        edAmount.setSelection(edAmount.getText().length());
        btnPay = findViewById(R.id.btn_payment);
        btnPay.setOnClickListener(view -> {
            if (edAmount.getText().toString().trim().length() > 0) {
                startPayment();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void startPayment() {
        final Activity activity = this;
        final Checkout co = new Checkout();
        co.setKeyID(getString(R.string.rpKey));

        try {
            JSONObject options = new JSONObject();
            options.put("name", myApplication.getUserName());
            options.put("description", "Donation");
            options.put("currency", "INR");
            double big = Double.valueOf(edAmount.getText().toString().trim());
            int amount = (int) (big) * 100;
            options.put("amount", amount);

            JSONObject preFill = new JSONObject();
            preFill.put("email", myApplication.getUserEmail());
            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            showMessage("Error in payment: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        showMessage("Payment successfully done", true);
    }

    private void showMessage(String message, boolean isSuccess) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.razor_payment_error_1))
                .setCancelable(false)
                .setMessage(message)
                .setIcon(R.drawable.ic_v)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> handleBackPressed(isSuccess))
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        showMessage(s, false);
    }

    private void handleBackPressed(boolean isPaymentSuccess) {
        if (isPaymentSuccess) {
            goToHome();
        }
    }


    private void goToHome() {
        Intent intent1 = new Intent(this, MainActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}