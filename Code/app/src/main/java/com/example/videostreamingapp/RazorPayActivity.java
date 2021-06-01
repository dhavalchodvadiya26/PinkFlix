package com.example.videostreamingapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.util.DialogUtil;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class RazorPayActivity extends AppCompatActivity implements PaymentResultListener {

    String planId = "", planPrice, planCurrency, planGateway = "", planGateWayText, razorPayKey, planName;
    Button btnEnjoySteaming;
    MyApplication myApplication;
    TextView tvTitle, tvMessage;
    ImageButton ibClose;
    private boolean isPaymentSuccess = false;
    private String promoCode = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        IsRTL.ifSupported(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        myApplication = MyApplication.getInstance();

        Intent intent = getIntent();
        promoCode = intent.getStringExtra("promoCode");
        planId = intent.getStringExtra("planId");
        planName = intent.getStringExtra("planName");
        planPrice = intent.getStringExtra("planPrice");
        planCurrency = intent.getStringExtra("planCurrency");
        planGateway = intent.getStringExtra("planGateway");
        planGateWayText = intent.getStringExtra("planGatewayText");
        razorPayKey = intent.getStringExtra("razorPayKey");

        tvTitle = findViewById(R.id.tv_title);
        ibClose = findViewById(R.id.ib_close);
        tvMessage = findViewById(R.id.tv_message);

        btnEnjoySteaming = findViewById(R.id.btn_success);

        Checkout.preload(getApplicationContext());

        startPayment();

        btnEnjoySteaming.setOnClickListener(view -> {
            if (isPaymentSuccess) {
                goToHome();
            } else {
                startPayment();
            }
        });

        ibClose.setOnClickListener(view -> {
            handleBackPressed();
        });


    }

    private void goToHome() {
        Intent intent1 = new Intent(RazorPayActivity.this, MainActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);
        finish();
    }

    public void startPayment() {
        final Activity activity = this;
        final Checkout co = new Checkout();
        co.setKeyID(razorPayKey);

        try {
            JSONObject options = new JSONObject();
            options.put("name", myApplication.getUserName());
            options.put("description", planName);
            options.put("currency", planCurrency);
            double big = Double.valueOf(planPrice);
            int amount = (int) (big) * 100;
            options.put("amount", amount);

            JSONObject preFill = new JSONObject();
//            preFill.put("email", myApplication.getUserEmail());
            preFill.put("contact", myApplication.getUserEmail());
            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            showError("Error in payment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String Title) {
        new AlertDialog.Builder(RazorPayActivity.this)
                .setTitle(getString(R.string.razor_payment_error_1))
                .setCancelable(false)
                .setMessage(Title)
                .setIcon(R.drawable.ic_v)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        onBackPressed();
                    }
                })
                .show();
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        btnEnjoySteaming.setText(getString(R.string.enjoy_streaming));
        tvTitle.setText(getString(R.string.payment_successful));
        tvMessage.setText(getString(R.string.plan_details_was_sent_to_n_your_email));
        isPaymentSuccess = true;
        try {
            if (NetworkUtils.isConnected(RazorPayActivity.this)) {
                DialogUtil.AlertBox(RazorPayActivity.this, getResources().getString(R.string.app_name), "Payment sucessfully.");
                new Transaction(RazorPayActivity.this)
                        .purchasedItem(planId, razorpayPaymentID, planGateway, promoCode);
            } else {
                showError(getString(R.string.conne_msg1));
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        btnEnjoySteaming.setText(getString(R.string.try_again));
        tvTitle.setText(getString(R.string.payment_failed));
        tvMessage.setText(response);
        isPaymentSuccess = false;
        try {
            showError("Payment failed: " + code + " " + response);
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentError", e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        handleBackPressed();
    }

    private void handleBackPressed() {
        if (isPaymentSuccess) {
            goToHome();
        } else {
            finish();
        }
    }
}
