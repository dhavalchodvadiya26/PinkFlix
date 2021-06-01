package com.example.videostreamingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.util.API;
import com.example.util.Constant;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText edt_new_password, confirm_new_pwd,current_pwd;
    String strMessage;
    String new_pwd,conf_pwd,cur_pwd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_activity);
        Button change = findViewById(R.id.button_confirm);

        confirm_new_pwd = findViewById(R.id.confirm_new_pwd);
        edt_new_password = findViewById(R.id.edt_new_password);
        current_pwd = findViewById(R.id.edt_current_password);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Change Password");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_pwd = edt_new_password.getText().toString();
                conf_pwd = confirm_new_pwd.getText().toString();
                 cur_pwd= current_pwd.getText().toString();

                System.out.println("New Password = "+new_pwd);
                System.out.println("Confirm Password = "+conf_pwd);
                System.out.println("Current Password = "+cur_pwd);

                if (!new_pwd.isEmpty() && !conf_pwd.isEmpty() && !cur_pwd.isEmpty()){
                    if (new_pwd.equals(conf_pwd) && cur_pwd.equals(MyApplication.getInstance().getRememberPassword())){
                        String strMobile = MyApplication.getInstance().getRememberEmail();
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
                        jsObj.addProperty("phone", strMobile);
                        jsObj.addProperty("password", new_pwd);
                        params.put("data", API.toBase64(jsObj.toString()));
                        client.post(Constant.CHANGE_PASSWORD, params, new AsyncHttpResponseHandler() {

                            @Override
                            public void onStart() {
                                super.onStart();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String result = new String(responseBody);
                                try {
                                    JSONObject mainJson = new JSONObject(result);
                                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                                    JSONObject objJson;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        objJson = jsonArray.getJSONObject(i);
                                        strMessage = objJson.getString(Constant.MSG);
                                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                                    }
                                    Toast.makeText(ChangePasswordActivity.this, strMessage, Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            }

                        });
                    }else if(!new_pwd.equals(conf_pwd) && cur_pwd.equals(MyApplication.getInstance().getRememberPassword())){
                        Toast.makeText(getApplicationContext(),"Passwords doesn't match",Toast.LENGTH_LONG).show();
                    }else if (new_pwd.equals(conf_pwd) && !cur_pwd.equals(MyApplication.getInstance().getRememberPassword())){
                        Toast.makeText(getApplicationContext(),"Invalid Current Password",Toast.LENGTH_LONG).show();
                    }
                }else if (!new_pwd.isEmpty() && conf_pwd.isEmpty() && cur_pwd.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "Please Enter Current & Confirm Password", Toast.LENGTH_SHORT).show();
                }else if (new_pwd.isEmpty() && !conf_pwd.isEmpty() && cur_pwd.isEmpty()){
                    Toast.makeText(ChangePasswordActivity.this, "Please Enter Current & New Password", Toast.LENGTH_SHORT).show();
                }else if (new_pwd.isEmpty() && conf_pwd.isEmpty() && !cur_pwd.isEmpty()){
                    Toast.makeText(ChangePasswordActivity.this, "Please Enter New & Confirm Password", Toast.LENGTH_SHORT).show();
                }else if (!new_pwd.isEmpty() && conf_pwd.isEmpty() && !cur_pwd.isEmpty()){
                    Toast.makeText(ChangePasswordActivity.this, "Please Enter Confirm Password", Toast.LENGTH_SHORT).show();
                }else if (new_pwd.isEmpty() && !conf_pwd.isEmpty() && !cur_pwd.isEmpty()){
                    Toast.makeText(ChangePasswordActivity.this, "Please Enter New Password", Toast.LENGTH_SHORT).show();
                }else if (!new_pwd.isEmpty() && !conf_pwd.isEmpty() && cur_pwd.isEmpty()){
                    Toast.makeText(ChangePasswordActivity.this, "Please Enter Current Password", Toast.LENGTH_SHORT).show();
                }else if (new_pwd.isEmpty() && conf_pwd.isEmpty() && cur_pwd.isEmpty()){
                    Toast.makeText(ChangePasswordActivity.this, "Please Fill all Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
