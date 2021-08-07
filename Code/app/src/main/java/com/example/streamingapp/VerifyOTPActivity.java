package com.example.streamingapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.util.API;
import com.example.util.Constant;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;

public class VerifyOTPActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtTimer;
    private TextView txtResend;
    private TextView txtNumber;
    private Button btnVerify;
    private Dialog dialog;
    private static final String FORMAT = "%02d:%02d";
    private ArrayList<String> strReasonList;
    private ProgressDialog pDialog;
    private TextView txtOne, txtTwo, txtThree, txtFour, txtFive, txtSix;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    String isFromForgotPassword;
    private String mVerificationId, user_id;
    private String strMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);
        pDialog = new ProgressDialog(this);
        txtTimer = findViewById(R.id.txtTimer);
        txtResend = findViewById(R.id.txtResend);
        btnVerify = findViewById(R.id.btnVerify);
        txtNumber = findViewById(R.id.txtNumber);
        txtNumber.setText("to " + getIntent().getStringExtra("phone"));
        txtResend.setOnClickListener(this);
        btnVerify.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        txtOne = findViewById(R.id.txtOne);
        txtTwo = findViewById(R.id.txtTwo);
        txtThree = findViewById(R.id.txtThree);
        txtFour = findViewById(R.id.txtFour);
        txtFive = findViewById(R.id.txtFive);
        txtSix = findViewById(R.id.txtSix);
        setTimer();
        txtTimer.setVisibility(View.VISIBLE);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Verify OTP");
        }

        setFromForgotPassword(getIntent().getStringExtra("isFromForgotPassword"));

//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                getIntent().getStringExtra("code") + getIntent().getStringExtra("phone"),        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void setTimer() {
        new CountDownTimer(85000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                txtTimer.setText("" + String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                txtResend.setVisibility(View.VISIBLE);
                txtTimer.setVisibility(View.GONE);
            }
        }.start();
    }


    @Override
    public void onClick(View v) {
        if (v == txtResend) {
            RequestParams params = new RequestParams();
            params.put("data", getIntent().getStringExtra("data"));
            resendOTP(params);
        } else if (v == btnVerify) {
            if (!TextUtils.isEmpty(txtOne.getText().toString().trim()) && !TextUtils.isEmpty(txtTwo.getText().toString().trim())
                    && !TextUtils.isEmpty(txtThree.getText().toString().trim()) && !TextUtils.isEmpty(txtFour.getText().toString().trim())
                    && !TextUtils.isEmpty(txtFive.getText().toString().trim()) && !TextUtils.isEmpty(txtSix.getText().toString().trim())
            ) {
                if (isFromForgotPassword().equals("ForgotPassword")) {
                    forgetVerifyOTP(txtOne.getText().toString().trim() + txtTwo.getText().toString().trim() + txtThree.getText().toString().trim()
                            + txtFour.getText().toString().trim() + txtFive.getText().toString().trim() + txtSix.getText().toString().trim());
                } else {
                    verifyOTP(txtOne.getText().toString().trim() + txtTwo.getText().toString().trim() + txtThree.getText().toString().trim()
                            + txtFour.getText().toString().trim() + txtFive.getText().toString().trim() + txtSix.getText().toString().trim());
                }

            } else
                Toast.makeText(this, "Please enter otp", Toast.LENGTH_SHORT).show();
        }
    }

    public String isFromForgotPassword() {
        return isFromForgotPassword;
    }

    public void setFromForgotPassword(String fromForgotPassword) {
        isFromForgotPassword = fromForgotPassword;
    }

    public void one(View v) {
        textDisplay("1");
    }

    public void two(View v) {
        textDisplay("2");
    }

    public void three(View v) {
        textDisplay("3");
    }

    public void four(View v) {
        textDisplay("4");
    }

    public void five(View v) {
        textDisplay("5");
    }

    public void six(View v) {
        textDisplay("6");
    }

    public void seven(View v) {
        textDisplay("7");
    }

    public void eight(View v) {
        textDisplay("8");
    }

    public void nine(View v) {
        textDisplay("9");
    }

    public void zero(View v) {
        textDisplay("0");
    }

    public void textDisplay(String str) {
        if (TextUtils.isEmpty(txtOne.getText().toString().trim()))
            txtOne.setText(str);
        else if (TextUtils.isEmpty(txtTwo.getText().toString().trim()))
            txtTwo.setText(str);
        else if (TextUtils.isEmpty(txtThree.getText().toString().trim()))
            txtThree.setText(str);
        else if (TextUtils.isEmpty(txtFour.getText().toString().trim()))
            txtFour.setText(str);
        else if (TextUtils.isEmpty(txtFive.getText().toString().trim()))
            txtFive.setText(str);
        else if (TextUtils.isEmpty(txtSix.getText().toString().trim())) {
            txtSix.setText(str);
            if (isFromForgotPassword().equals("ForgotPassword")) {
                forgetVerifyOTP(txtOne.getText().toString().trim() + txtTwo.getText().toString().trim() + txtThree.getText().toString().trim()
                        + txtFour.getText().toString().trim() + txtFive.getText().toString().trim() + txtSix.getText().toString().trim());
            } else {
                verifyOTP(txtOne.getText().toString().trim() + txtTwo.getText().toString().trim() + txtThree.getText().toString().trim()
                        + txtFour.getText().toString().trim() + txtFive.getText().toString().trim() + txtSix.getText().toString().trim());
            }
        }

    }

    public void removeText(View v) {
        if (!TextUtils.isEmpty(txtSix.getText().toString().trim()))
            txtSix.setText("");
        else if (!TextUtils.isEmpty(txtFive.getText().toString().trim()))
            txtFive.setText("");
        else if (!TextUtils.isEmpty(txtFour.getText().toString().trim()))
            txtFour.setText("");
        else if (!TextUtils.isEmpty(txtThree.getText().toString().trim()))
            txtThree.setText("");
        else if (!TextUtils.isEmpty(txtTwo.getText().toString().trim()))
            txtTwo.setText("");
        else if (!TextUtils.isEmpty(txtOne.getText().toString().trim()))
            txtOne.setText("");
    }

    public void verifyOTP(String code) {
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
//        signInWithPhoneAuthCredential(credential);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("phone", getIntent().getStringExtra("phone"));
        jsObj.addProperty("otp", code);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.VERIFY_OTP_URL, params, new AsyncHttpResponseHandler() {

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
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        strMessage = objJson.getString(Constant.MSG);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        Constant.VERIFYOTP = objJson.optString(Constant.VERIFY);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(VerifyOTPActivity.this);
                builder.setMessage(strMessage)
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, id) -> {
                                Intent intent = new Intent(getApplicationContext(), SignInScreenActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
                dismissProgressDialog();
            }
        });
    }

    public void forgetVerifyOTP(String code) {
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
//        signInWithPhoneAuthCredential(credential);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("phone", getIntent().getStringExtra("phone"));
        jsObj.addProperty("otp", code);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.FORGET_VERIFY_OTP_URL, params, new AsyncHttpResponseHandler() {

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
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        strMessage = objJson.getString(Constant.MSG);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        Constant.VERIFYOTP = objJson.optString(Constant.VERIFY);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(VerifyOTPActivity.this);
                builder.setMessage(strMessage)
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, id) -> {
                                Intent intent = new Intent(getApplicationContext(), UpdatePasswordActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("isFromForgotPassword", isFromForgotPassword);
                                startActivity(intent);
                                finish();
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
                dismissProgressDialog();
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void showToast(String msg) {
        Toast.makeText(VerifyOTPActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    private void resendOTP(RequestParams params) {
        txtResend.setVisibility(View.GONE);
        txtTimer.setVisibility(View.VISIBLE);
        callSignUpApi();
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                getIntent().getStringExtra("code") + getIntent().getStringExtra("phone"),        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(VerifyOTPActivity.this, task -> {
//                    if (task.isSuccessful()) {
//                        //verification successful we will start the profile activity
//                        if (isFromForgotPassword().equals("ForgotPassword")){
//                            Intent intent = new Intent(getApplicationContext(), UpdatePasswordActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.putExtra("isFromForgotPassword",isFromForgotPassword);
//                            startActivity(intent);
//                            finish();
//                        }else{
//                            callSignUpApi();
//                        }
//                    }
//                });
//    }

//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//        @Override
//        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//
//            //Getting the code sent by SMS
//            String code = phoneAuthCredential.getSmsCode();
//
//            if (code != null) {
//                verifyOTP(code);
//            }
//        }
//
//
//        @Override
//        public void onVerificationFailed(FirebaseException e) {
//            Toast.makeText(VerifyOTPActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//
//
//        @Override
//        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
//
//            //storing the verification id that is sent to the user
//            mVerificationId = s;
//
//        }
//    };

    private void callSignUpApi() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("data", getIntent().getStringExtra("data"));
        client.post(Constant.REGISTER_URL, params, new AsyncHttpResponseHandler() {

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
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        strMessage = objJson.getString(Constant.MSG);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(VerifyOTPActivity.this);
                builder.setMessage(strMessage)
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, id) -> {
                            Intent intent = new Intent(getApplicationContext(), SignInScreenActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
                dismissProgressDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
