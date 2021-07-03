package com.example.streamingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.RentalPlanListAdapter;
import com.example.adapter.SubscriptionTransactionListAdapter;
import com.example.base.BaseActivity;
import com.example.itemmodels.ItemSubscription;
import com.example.itemmodels.ItemTransaction;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.GradientTextView;
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

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TransactionListActivity extends BaseActivity {
    private RecyclerView rvRecently;
    private RecyclerView rv_rental;
    private RecyclerView rv_donation;
    private MyApplication myApplication;
    private ArrayList<ItemTransaction> recentList;
    private ArrayList<ItemSubscription> recentTransactionList;
    private SubscriptionTransactionListAdapter latestMovieAdapter;
    private RentalPlanListAdapter rentalAdapter;
    private GradientTextView txtDonation;
    private GradientTextView txtRental;
    private GradientTextView txtSubscription;
    LinearLayout lyt_not_found;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);
        IsRTL.ifSupported(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Transaction");
        }
        myApplication = MyApplication.getInstance();
        txtSubscription = findViewById(R.id.txtSubscription);
        lyt_not_found=findViewById(R.id.lyt_not_found);
        txtRental = findViewById(R.id.txtRental);
        txtDonation = findViewById(R.id.txtDonation);
        rvRecently = findViewById(R.id.rv_recently_watched);
        rv_rental = findViewById(R.id.rv_rental);
        rv_donation = findViewById(R.id.rv_donation);
        recyclerViewProperty(rvRecently);
        recyclerViewProperty(rv_rental);
        recyclerViewProperty(rv_donation);
        recentList = new ArrayList<>();
        recentTransactionList = new ArrayList<>();
        if (NetworkUtils.isConnected(TransactionListActivity.this)) {
            getToWatchList();
        } else {
            showToast(getString(R.string.conne_msg1));
        }

    }

    public void showToast(String msg) {
        Toast.makeText(TransactionListActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void recyclerViewProperty(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(TransactionListActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void getToWatchList() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.GET_TRANSACTION_LIST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    if (mainJson.optString("status_code").equalsIgnoreCase("200")) {
                        JSONObject video_Json = mainJson.optJSONObject("VIDEO_STREAMING_APP");

                        assert video_Json != null;
                        JSONArray subscription_array = video_Json.optJSONArray("subscription_plan");
                        JSONArray rental_array = video_Json.optJSONArray("rental_plan");
                        JSONArray donation_array = video_Json.optJSONArray("donate_plan");

                        assert subscription_array != null;
                        for (int i = 0; i < subscription_array.length(); i++) {
                            JSONObject jsonObject = subscription_array.getJSONObject(i);
                            ItemSubscription itemSubscription = new ItemSubscription();
                            itemSubscription.setTransactionId(jsonObject.getString("transction_plan_name"));
                            itemSubscription.setTransction_user_id(jsonObject.getString("transction_user_id"));
                            itemSubscription.setTransction_email(jsonObject.getString("transction_email"));
                            itemSubscription.setTransction_plan_id(jsonObject.getString("transction_plan_id"));
                            itemSubscription.setTransction_gateway(jsonObject.getString("transction_gateway"));
                            itemSubscription.setTransction_payment_amount(jsonObject.getString("transction_payment_amount"));
                            itemSubscription.setTransction_payment_id(jsonObject.getString("transction_payment_id"));
                            itemSubscription.setTransction_promocode(jsonObject.getString("transction_promocode"));
                            itemSubscription.setTransction_donate_flag(jsonObject.getString("transction_donate_flag"));
                            itemSubscription.setTransction_date(jsonObject.getString("transction_date"));
                            itemSubscription.setTransction_expiry_date(jsonObject.getString("transction_expiry_date"));

                            recentTransactionList.add(itemSubscription);
                            latestMovieAdapter = new SubscriptionTransactionListAdapter(recentTransactionList);
                            rvRecently.setAdapter(latestMovieAdapter);
                        }
                        if (subscription_array.length() > 0)
                            txtSubscription.setVisibility(View.VISIBLE);
                        recentList.clear();
                        assert rental_array != null;
                        for (int i = 0; i < rental_array.length(); i++) {
                            JSONObject jsonObject = rental_array.getJSONObject(i);
                            ItemTransaction itemTransaction = new ItemTransaction();
                            itemTransaction.setTransactionId(jsonObject.getString("transction_plan_name"));
                            itemTransaction.setTransction_user_id(jsonObject.getString("transction_user_id"));
                            itemTransaction.setTransction_email(jsonObject.getString("transction_email"));
                            itemTransaction.setTransction_plan_id(jsonObject.getString("transction_plan_id"));
                            itemTransaction.setTransction_gateway(jsonObject.getString("transction_gateway"));
                            itemTransaction.setTransction_payment_amount(jsonObject.getString("transction_payment_amount"));
                            itemTransaction.setTransction_payment_id(jsonObject.getString("transction_payment_id"));
                            itemTransaction.setTransction_promocode(jsonObject.getString("transction_promocode"));
                            itemTransaction.setTransction_donate_flag(jsonObject.getString("transction_donate_flag"));
                            itemTransaction.setTransction_date(jsonObject.getString("transction_date"));
                            itemTransaction.setMovie_name(jsonObject.getString("movie_name"));
                            itemTransaction.setExpirty_date(jsonObject.getString("expirty_date"));

                            recentList.add(itemTransaction);

                        }

                        if (rental_array.length() > 0) {
                            txtRental.setVisibility(View.VISIBLE);
                            rentalAdapter = new RentalPlanListAdapter(recentList);
                            rv_rental.setAdapter(rentalAdapter);
                        }
                    } else {
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("Transaction", error.getMessage());
            }
        });
    }

}
