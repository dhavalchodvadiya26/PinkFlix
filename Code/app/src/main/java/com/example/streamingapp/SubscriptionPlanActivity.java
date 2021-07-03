package com.example.streamingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.SubcscriptionPlanListAdapter;
import com.example.base.BaseActivity;
import com.example.itemmodels.ItemPlan;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.Remember;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SubscriptionPlanActivity extends BaseActivity {

    private ProgressBar mProgressBar;
    private LinearLayout lyt_not_found;
    private RecyclerView rvPlan;
    private NestedScrollView nestedScrollView;
    private LinearLayout lytProceed;
    private ArrayList<ItemPlan> mListItem;
    private SubcscriptionPlanListAdapter adapter;
    private int selectedPlan = -1;
    private ImageView imageClose;
    private TextView txtPLan;
    private TextView txtDesc;
    private EditText edPromoCode;
    private int promoCodeTrial = 0;
    private TextView txtPlan;
    private MyApplication myApplication;
    private ArrayList<String> strColor;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_plan);
        IsRTL.ifSupported(this);
        strColor = new ArrayList<>();
        strColor.add("#F9E79F");
        strColor.add("#C5E1A5");
        strColor.add("#ffab91");
        myApplication = MyApplication.getInstance();
        mListItem = new ArrayList<>();
        edPromoCode = findViewById(R.id.ed_promo_code);
        edPromoCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6), new InputFilter.AllCaps()});
        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        lytProceed = findViewById(R.id.lytProceed);
        imageClose = findViewById(R.id.imageClose);
        txtPlan = findViewById(R.id.txtPlan);
        txtPLan = findViewById(R.id.txtPLan);
        txtDesc = findViewById(R.id.txtDesc);

        rvPlan = findViewById(R.id.rv_plan);
        rvPlan.setHasFixedSize(true);
        rvPlan.setLayoutManager(new LinearLayoutManager(SubscriptionPlanActivity.this, LinearLayoutManager.VERTICAL, false));
        rvPlan.setFocusable(false);
        rvPlan.setNestedScrollingEnabled(false);

        if (NetworkUtils.isConnected(SubscriptionPlanActivity.this)) {
            getPlan();
        } else {
            Toast.makeText(SubscriptionPlanActivity.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }

        imageClose.setOnClickListener(view -> finish());

    }

    private void getPlan() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        if (TextUtils.isEmpty(Remember.getString(Constant.SHOW_ID, ""))) {
            jsObj.addProperty("movie_id", Remember.getString(Constant.MOVIE_ID, ""));
            jsObj.addProperty("show_id", 0);
        } else {
            jsObj.addProperty("show_id", Remember.getString(Constant.SHOW_ID, ""));
            jsObj.addProperty("movie_id", 0);
        }
        params.put("data", API.toBase64(jsObj.toString()));
        String strPlan;
        if (getIntent().getExtras() == null)
            strPlan = Constant.PLAN_LIST_URL;
        else {
            strPlan = Constant.RENTAL_LIST_URL;
            edPromoCode.setVisibility(View.GONE);
            txtPlan.setText("Rental Plan");
            txtPLan.setText("Select Your Rental Plan");
            txtDesc.setText("Full access to watch this Movie/series content. This will be valid for limited Period of time...");

        }
        client.post(strPlan, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
                lytProceed.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);
                lytProceed.setVisibility(View.GONE);


                String result = new String(responseBody);
                Log.d("RESPONSE", result);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);

                            ItemPlan objItem = new ItemPlan();
                            objItem.setPlanId(objJson.getString(Constant.PLAN_ID));
                            objItem.setPlanName(objJson.getString(Constant.PLAN_NAME));
                            objItem.setPlanPrice(objJson.getString(Constant.PLAN_PRICE));
                            objItem.setPlanDuration(objJson.getString(Constant.PLAN_DURATION));
                            objItem.setPlanDesc(objJson.getString(Constant.PLAN_DESC));
                            objItem.setPlanCurrencyCode(objJson.getString(Constant.CURRENCY_CODE));
                            mListItem.add(objItem);

                        }
                        displayData();
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.GONE);
                        lytProceed.setVisibility(View.GONE);
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
                nestedScrollView.setVisibility(View.GONE);
                lytProceed.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayData() {
        adapter = new SubcscriptionPlanListAdapter(SubscriptionPlanActivity.this, mListItem, strColor);
        rvPlan.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {

            selectedPlan = position;
            adapter.select(position);
        });
        edPromoCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edPromoCode.getText().toString().trim().length() == 6) {
                    if (!checkPromoCodeValidity()) {
                        checkPromoCodeAPI();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lytProceed.setOnClickListener(view -> {
            if (selectedPlan != -1) {
                RequestParams params = new RequestParams();
                JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
                jsObj.addProperty("promocode", edPromoCode.getText().toString());
                params.put("data", API.toBase64(jsObj.toString()));
                ItemPlan itemPlan = mListItem.get(selectedPlan);
                String isFreePlan = itemPlan.getPlanPrice();
                if (isFreePlan.equals("0.00")) {
                    if (NetworkUtils.isConnected(SubscriptionPlanActivity.this)) {
                        new Transaction(SubscriptionPlanActivity.this).purchasedItem(itemPlan.getPlanId(), "-", "N/A", "");
                    } else {
                        Toast.makeText(SubscriptionPlanActivity.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intent = new Intent(SubscriptionPlanActivity.this, SelectSubscriptionPlanActivity.class);
                    intent.putExtra("planId", itemPlan.getPlanId());
                    intent.putExtra("planName", itemPlan.getPlanName());
                    intent.putExtra("promoCode", edPromoCode.getText().toString());
                    intent.putExtra("planPrice", itemPlan.getPlanPrice());
                    intent.putExtra("planDuration", itemPlan.getPlanDuration());
                    startActivity(intent);
                    finish();
                }
            } else {
                Toast.makeText(this, "Please select plan first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkPromoCodeValidity() {
        if (MyApplication.getInstance().preferences.contains("IS_PROMOCODE_DONE")) {
            long timer = MyApplication.getInstance().preferences.getLong("PROMOCODE_TIME", 0);
            if (MyApplication.getInstance().preferences.getBoolean("IS_PROMOCODE_DONE", false) && (System.currentTimeMillis() - timer) >= (24 * 60 * 60 * 1000)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private void checkPromoCodeAPI() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        jsObj.addProperty("promocode", edPromoCode.getText().toString());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.CHECK_PROMOCODE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissProgressDialog();
                String result = new String(responseBody);
                Log.d("RESULT", result);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject jsonArray = mainJson.getJSONObject(Constant.ARRAY_NAME);
                    if (jsonArray.length() > 0) {
                        int isSuccess = jsonArray.getInt("promocode");
                        if (isSuccess == 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SubscriptionPlanActivity.this);
                            builder.setMessage("Promocode applied successfully, You have Enrolled into Premium Subscription")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialog, id) -> {
                                        Intent returnIntent = new Intent();
                                        setResult(Activity.RESULT_OK, returnIntent);
                                        finish();
                                        dialog.dismiss();
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else if (isSuccess == 2) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SubscriptionPlanActivity.this);
                            builder.setMessage("This Promocode is already used.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialog, id) -> {
                                        dialog.dismiss();
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            promoCodeTrial += 1;
                            Toast.makeText(SubscriptionPlanActivity.this, "Promo code is invalid", Toast.LENGTH_SHORT).show();

                            if (promoCodeTrial == 3) {
                                MyApplication.getInstance().preferences.edit().putBoolean("IS_PROMOCODE_DONE", true).apply();
                                MyApplication.getInstance().preferences.edit().putLong("PROMOCODE_TIME", System.currentTimeMillis()).apply();
                            }
                        }

                    }
                } catch (JSONException e) {
                    Log.d("ERROR1", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("ERROR2", error.getMessage());
                dismissProgressDialog();
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showPromoCode(boolean show) {
        if (getIntent().getExtras() == null)
            if (show) {
                edPromoCode.setVisibility(View.VISIBLE);
                edPromoCode.setSelection(0);
            } else
                edPromoCode.setVisibility(View.GONE);
    }
}
