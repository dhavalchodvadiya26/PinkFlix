package com.example.streamingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.util.API;
import com.example.util.Constant;
import com.example.util.Remember;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Transaction {
    MyApplication myApplication;
    private ProgressDialog pDialog;
    private Activity mContext;

    public Transaction(Activity context) {
        this.mContext = context;
        pDialog = new ProgressDialog(mContext);
        myApplication = MyApplication.getInstance();
    }

    public void purchasedItem(String planId, String paymentId, String paymentGateway, String promoCode) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("user_id", myApplication.getUserId());
        jsObj.addProperty("plan_id", planId);
        jsObj.addProperty("payment_id", paymentId);
        jsObj.addProperty("promocode", "ANDROID");
        jsObj.addProperty("payment_gateway", paymentGateway);
        jsObj.addProperty("donation", "0");

        if (TextUtils.isEmpty(Remember.getString(Constant.SHOW_ID, ""))) {
            jsObj.addProperty("movie_id", Remember.getString(Constant.MOVIE_ID, ""));
            jsObj.addProperty("show_id", 0);
        } else {
            jsObj.addProperty("show_id", Remember.getString(Constant.SHOW_ID, ""));
            jsObj.addProperty("movie_id", 0);
        }

        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.TRANSACTION_URL, params, new AsyncHttpResponseHandler() {
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
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    if (jsonArray.length() > 0) {
                        objJson = jsonArray.getJSONObject(0);
                        Toast.makeText(mContext, objJson.getString(Constant.MSG), Toast.LENGTH_SHORT).show();

                        ActivityCompat.finishAffinity(mContext);

                        Intent intentDashboard = new Intent(mContext, MainActivity.class);
                        intentDashboard.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intentDashboard.putExtra("isPurchased", true);
                        mContext.startActivity(intentDashboard);
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
                Toast.makeText(myApplication, "Please select paid plan.", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void showProgressDialog() {
        pDialog.setMessage(mContext.getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }
}
