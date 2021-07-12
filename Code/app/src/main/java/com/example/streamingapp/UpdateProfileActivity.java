package com.example.streamingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.example.util.API;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.nguyenhoanglam.imagepicker.model.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class UpdateProfileActivity extends AppCompatActivity implements Validator.ValidationListener {

    private ProgressBar mProgressBar;
    private LinearLayout lyt_not_found;
    private NestedScrollView nestedScrollView;

    @NotEmpty
    private EditText edtName;
    private EditText edtPassword;
    @Length(max = 14, min = 6, message = "Enter valid Phone Number")
    private EditText edtPhone;
    @NotEmpty
    private EditText edtAddress;
    private TextView changePwd;
    String s_type;

    private String strName, strEmail, strPassword, strMobi, strMessage, strAddress;

    private Button btnSubmit;
    private CircularImageView imageAvtar;
    private MyApplication myApplication;

    private ProgressDialog pDialog;

    private Validator validator;

    private ArrayList<Image> userImage = new ArrayList<>();
    private boolean isImage = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Edit Profile");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        edtName = findViewById(R.id.edt_name);
        edtPhone = findViewById(R.id.edt_phone);
        edtAddress = findViewById(R.id.edt_address);
        changePwd = findViewById(R.id.changepwd);
        btnSubmit = findViewById(R.id.button_submit);
        myApplication = MyApplication.getInstance();

        pDialog = new ProgressDialog(UpdateProfileActivity.this);
        nestedScrollView.setVisibility(View.GONE);

        if (NetworkUtils.isConnected(UpdateProfileActivity.this)) {
            getUserProfile();
        } else {
            showToast(getString(R.string.conne_msg1));
        }

        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UpdatePasswordActivity.class);
                intent.putExtra("isFromForgotPassword","Profile");
                startActivity(intent);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strName = edtName.getText().toString();
                strMobi = edtPhone.getText().toString();
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
                jsObj.addProperty("name", strName);
                jsObj.addProperty("phone", strMobi);
                jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
                params.put("data", API.toBase64(jsObj.toString()));
                client.post(Constant.EDIT_PROFILE_URL, params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showProgressDialog();
//                                Toast.makeText(UpdatePasswordActivity.this, "JSON Started", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        dismissProgressDialog();
                        String result = new String(responseBody);
                        System.out.println("UpdateProfileActivity ==> User Result ==> "+result);
                        try {
                            JSONObject mainJson = new JSONObject(result);
                            JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                            JSONObject objJson;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                objJson = jsonArray.getJSONObject(i);
                                strMessage = objJson.getString(Constant.MSG);
                                Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                            }
                            myApplication.saveLogin(myApplication.getIsLogin() ? myApplication.getUserId() : "", strName, myApplication.getRememberEmail());
                            myApplication.saveMobile(strMobi);
                            Toast.makeText(UpdateProfileActivity.this, strMessage, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        dismissProgressDialog();
//                                Toast.makeText(UpdatePasswordActivity.this, "JSON Failed", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    public void onValidationSucceeded() {

    }

    public void onValidationFailed(List<ValidationError> errors) {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getUserProfile() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("user_id", myApplication.getUserId());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.PROFILE_URL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                System.out.println("UpdateProfileActivity ==> result ==> "+result);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    if (jsonArray.length() > 0) {
                        JSONObject objJson;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            edtName.setText(objJson.getString(Constant.USER_NAME));
                            edtPhone.setText(objJson.getString(Constant.USER_PHONE));
                            edtAddress.setText(objJson.getString(Constant.USER_ADDRESS));
                            s_type=objJson.getString(Constant.SOCIAL_TYPE);
                        }
                        if (s_type.equals("")){
                            changePwd.setVisibility(View.VISIBLE);
                        }else {
                            changePwd.setVisibility(View.GONE);
                        }
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.GONE);
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(UpdateProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
