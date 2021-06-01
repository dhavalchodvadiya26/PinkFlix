package com.example.videostreamingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.example.item.ItemPaymentSetting;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SelectPlanActivity extends AppCompatActivity{

    String planId, planName, planPrice, planDuration;
    TextView textPlanName, textPlanPrice, textPlanDuration, textChoosePlanName, textEmail, textChangePlan, textLogout, textPlanCurrency, textNoPaymentGateway;
    CardView lytProceed;
    RadioButton radioPayPal, radioStripe, radioRazorPay;
    MyApplication myApplication;
    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    RelativeLayout lytDetails;
    ItemPaymentSetting paymentSetting;
    View viewPaypal, viewStripe;
    String promoCode;
    RadioGroup radioGroup;
    LinearLayout layoutUserInfo;



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_plan);
        IsRTL.ifSupported(this);

        myApplication = MyApplication.getInstance();
        paymentSetting = new ItemPaymentSetting();

        final Intent intent = getIntent();
        planId = intent.getStringExtra("planId");
        promoCode = intent.getStringExtra("promoCode");
        planName = intent.getStringExtra("planName");
        planPrice = intent.getStringExtra("planPrice");
        planDuration = intent.getStringExtra("planDuration");

        layoutUserInfo = findViewById(R.id.layoutUserInfo);
        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        lytDetails = findViewById(R.id.lytDetails);
        textPlanName = findViewById(R.id.textPackName);
        textPlanPrice = findViewById(R.id.textPrice);
        textPlanCurrency = findViewById(R.id.textCurrency);
        textPlanDuration = findViewById(R.id.textDay);
        textChoosePlanName = findViewById(R.id.choosePlanName);
        textEmail = findViewById(R.id.planEmail);
        textLogout = findViewById(R.id.textLogout);
        textChangePlan = findViewById(R.id.changePlan);
        lytProceed = findViewById(R.id.lytProceed);
        radioPayPal = findViewById(R.id.rdPaypal);
        radioStripe = findViewById(R.id.rdStripe);
        radioRazorPay = findViewById(R.id.rdRazorPay);
        viewPaypal = findViewById(R.id.viewPaypal);
        viewStripe = findViewById(R.id.viewStripe);
        textNoPaymentGateway = findViewById(R.id.textNoPaymentGateway);
        radioGroup = findViewById(R.id.radioGrp);

        textPlanName.setText(planName);
        textPlanPrice.setText(planPrice);
        textPlanDuration.setText(getString(R.string.plan_day_for, planDuration));
        textChoosePlanName.setText(planName);
        textEmail.setText(myApplication.getUserEmail());

        textChangePlan.setOnClickListener(view -> finish());

        lytProceed.setOnClickListener(view -> {
            startRazorPayPayment();
        });

        if (NetworkUtils.isConnected(SelectPlanActivity.this)) {
            getPaymentSetting();
        } else {
            Toast.makeText(SelectPlanActivity.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }

    }

    private void startRazorPayPayment() {
        Intent intentRazor = new Intent(SelectPlanActivity.this, RazorPayActivity.class);
        intentRazor.putExtra("planId", planId);
        intentRazor.putExtra("planName", planName);
        intentRazor.putExtra("planPrice", planPrice);
        intentRazor.putExtra("promoCode", promoCode);
        intentRazor.putExtra("planCurrency", paymentSetting.getCurrencyCode());
        intentRazor.putExtra("planGateway", "Razorpay");
        intentRazor.putExtra("planGatewayText", getString(R.string.razor_pay));
        intentRazor.putExtra("razorPayKey", paymentSetting.getRazorPayKey());
        startActivity(intentRazor);
        finish();
    }

//    private boolean hasSubscription() {
//        if (purchaseTransactionDetails != null) {
//            return purchaseTransactionDetails.purchaseInfo != null;
//        }
//        return false;
//    }

    private void getPaymentSetting() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        System.out.println("Select Plan Acttivity ==> Data ==> "+API.toBase64(jsObj.toString()));
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.PAYMENT_SETTING_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                lytDetails.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                lytDetails.setVisibility(View.VISIBLE);


                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    if (jsonArray.length() > 0) {
                        objJson = jsonArray.getJSONObject(0);
                        paymentSetting.setCurrencyCode(objJson.getString(Constant.CURRENCY_CODE));
                        paymentSetting.setPayPal(objJson.getBoolean(Constant.PAY_PAL_ON));
                        paymentSetting.setPayPalSandbox(objJson.getString(Constant.PAY_PAL_SANDBOX).equals("sandbox"));
                        paymentSetting.setPayPalClientId(objJson.getString(Constant.PAY_PAL_CLIENT));
                        paymentSetting.setStripe(objJson.getBoolean(Constant.STRIPE_ON));
                        paymentSetting.setStripePublisherKey(objJson.getString(Constant.STRIPE_PUBLISHER));
                        paymentSetting.setRazorPay(true);
                        paymentSetting.setRazorPayKey(getString(R.string.rpKey));
                        displayData();
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        lytDetails.setVisibility(View.GONE);
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                mProgressBar.setVisibility(View.GONE);
                lytDetails.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void displayData() {
        textPlanCurrency.setText(paymentSetting.getCurrencyCode());
        viewPaypal.setVisibility(View.GONE);
        radioPayPal.setVisibility(View.GONE);
        viewStripe.setVisibility(View.GONE);
        radioStripe.setVisibility(View.GONE);
        viewPaypal.setVisibility(View.GONE);
    }

}
