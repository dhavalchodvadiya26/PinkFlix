package com.example.streamingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.util.API;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.ybs.countrypicker.CountryPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class ForgotPasswordActivity extends AppCompatActivity {
    @NotEmpty
    @Email
    EditText edtEmail;
    private EditText edtCountry;


    String strEmail, strMessage,strUserId;

    Button btnSubmit;
    ProgressDialog pDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.button_text_forgot_password));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        pDialog = new ProgressDialog(this);
        edtEmail = findViewById(R.id.editText_fp);
        btnSubmit = findViewById(R.id.button_fp);
        edtCountry = findViewById(R.id.edtCountry_fp);
        edtCountry.setFocusable(false);
        edtCountry.setClickable(true);

        edtCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    CountryPicker picker = CountryPicker.newInstance("Select Country");
                    picker.setListener((name, code, dialCode, i) -> {
                        edtCountry.setText(dialCode);
                        picker.dismiss();
                    });

                    picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (NetworkUtils.isConnected(ForgotPasswordActivity.this)) {
                    putForgotPassword();
                } else {
                    showToast(getString(R.string.conne_msg1));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void putForgotPassword() {

//
        strEmail = edtEmail.getText().toString();
        MyApplication.getInstance().saveMobile(strEmail);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("phone", strEmail);
        params.put("data", API.toBase64(jsObj.toString()));
        System.out.println("data ==> "+API.toBase64(jsObj.toString()));

        client.post(Constant.FORGOT_PASSWORD_URL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissProgressDialog();
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        strMessage = objJson.getString(Constant.MSG);
                        strUserId= objJson.getString(Constant.USER_ID);
                        System.out.println("ForgotPassword JSON ==> "+objJson);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
            }

        });
    }

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
            builder.setMessage(strMessage)
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, id) -> {
                        dialog.dismiss();
                    });
            AlertDialog alert = builder.create();
            alert.show();
            edtEmail.setText("");
            edtEmail.requestFocus();
        } else {
//            AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
//            builder.setMessage(strMessage)
//                    .setCancelable(false)
//                    .setPositiveButton("OK", (dialog, id) -> {
//                        dialog.dismiss();
//                        Intent intentco = new Intent(ForgotPasswordActivity.this, SignInScreenActivity.class);
//                        intentco.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intentco);
//                        finish();
//                        dialog.dismiss();
//                    });
//            AlertDialog alert = builder.create();
//            alert.show();
            startActivity(new Intent(ForgotPasswordActivity.this, VerifyOTPActivity.class).putExtra("phone", edtEmail.getText().toString().trim())
                .putExtra("code", edtCountry.getText().toString().trim()).putExtra("isFromForgotPassword", "ForgotPassword"));
        }
    }

    public void showToast(String msg) {
        Toast.makeText(ForgotPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
