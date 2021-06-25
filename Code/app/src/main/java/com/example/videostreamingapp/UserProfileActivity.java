package com.example.videostreamingapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.util.API;
import com.example.util.Constant;
import com.example.util.GradientTextView;
import com.example.util.IsRTL;
import com.example.util.PrettyDialog;
import com.example.util.Remember;
import com.example.util.fullscreen.FullScreenDialogController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class UserProfileActivity extends AppCompatActivity {
    private static final String EXTRA_NAME = "EXTRA_NAME";
    private static final String RESULT_FULL_NAME = "RESULT_FULL_NAME";
    private FragmentManager fragmentManager;
    Toolbar toolbar;
    LinearLayout header;
    String strMessage;
    private FullScreenDialogController dialogController;
    private MyApplication myApplication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);

        IsRTL.ifSupported(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("My Account");
        }
        myApplication = MyApplication.getInstance();
        GradientTextView nav_name = findViewById(R.id.nav_name);
        GradientTextView nav_email = findViewById(R.id.nav_email);
        GradientTextView txtMyAccount = findViewById(R.id.txtMyAccount);
        GradientTextView txtWatchList = findViewById(R.id.txtWatchList);
        GradientTextView txtTransaction = findViewById(R.id.txtTransaction);
        GradientTextView txtMemberShip = findViewById(R.id.txtMemberShip);
        GradientTextView txtSetting = findViewById(R.id.txtSetting);
        GradientTextView txtLogout = findViewById(R.id.txtLogout);
        GradientTextView txtHelp = findViewById(R.id.txtHelp);
        header = findViewById(R.id.header);
        fragmentManager = getSupportFragmentManager();

        if (myApplication.getIsLogin())
            txtLogout.setText("Logout");
        else
            txtLogout.setText("Login");

        if (myApplication.getIsLogin()) {
            nav_name.setText(myApplication.getUserName());
            if (!myApplication.getUserEmail().equals("")) {
                nav_email.setText(myApplication.getUserEmail());
            }else{
                nav_email.setText(myApplication.getMobile());
            }
        }

        txtMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myApplication.getIsLogin()) {
                    Intent intenttransaction = new Intent(UserProfileActivity.this, DashboardActivity.class);
                    intenttransaction.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intenttransaction);
                } else {
                    showAlert();
                }
            }
        });

        txtWatchList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myApplication.getIsLogin()) {
                    Intent intentProfile = new Intent(UserProfileActivity.this, WatchListActivity.class);
                    intentProfile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentProfile);
                } else {
                    showAlert();
                }
            }
        });

        txtTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myApplication.getIsLogin()) {
                    Intent intenttransaction = new Intent(UserProfileActivity.this, TransactionListActivity.class);
                    startActivity(intenttransaction);
                } else {
                    showAlert();
                }
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myApplication.getIsLogin()) {
                    logOut();
                } else {
                    showAlert();
                }
            }
        });

        txtMemberShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myApplication.getIsLogin()) {
                    startActivity(new Intent(UserProfileActivity.this, PlanActivity.class));
                } else
                    showAlert();
            }
        });

        txtHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myApplication.getIsLogin()) {
                    startActivity(new Intent(UserProfileActivity.this, TechnicalIssueActivity.class));
                } else
                    showAlert();
            }
        });


    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.Container, f1, name);
        ft.commit();
        setToolbarTitle(name);
    }

    public void setToolbarTitle(String Title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Title);
        }
    }

    private void showAlert() {
        PrettyDialog pDialog = new PrettyDialog(this);
        pDialog
                .setTitle(getString(R.string.menu_login))
                .setMessage("Please Login to Enjoy Endless Streaming !!")
                .setIcon(R.drawable.login)
                .addButton(
                        getString(R.string.menu_login),
                        R.color.pdlg_color_white2,
                        R.color.pdlg_color_green,
                        () -> {
                            pDialog.dismiss();
                            login();
                        }).addButton(
                getString(android.R.string.no),
                R.color.pdlg_color_white2,
                R.color.pdlg_color_red,
                pDialog::dismiss)
                .show();
    }

    private void login() {
        startActivity(new Intent(UserProfileActivity.this, SignInActivity.class));
        finish();
    }

    private void logOut() {
        PrettyDialog pDialog = new PrettyDialog(this);
        pDialog
                .setTitle(getString(R.string.menu_logout))
                .setMessage(getString(R.string.logout_msg))
                .setIcon(R.drawable.logout)
                .addButton(getString(R.string.menu_logout),
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_green,
                        () -> {
                            AsyncHttpClient client = new AsyncHttpClient();
                            RequestParams params = new RequestParams();

                            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
                            params.put("user_id", myApplication.getIsLogin()?myApplication.getUserId():" ");
                            params.put("imei_number", Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));

                            client.post(Constant.LOGOUT_URL, params, new AsyncHttpResponseHandler() {

                                @Override
                                public void onStart() {
                                    super.onStart();
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                                    dismissProgressDialog();
                                    String result = new String(responseBody);
                                    try {
                                        JSONObject mainJson = new JSONObject(result);
                                        JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                                        System.out.println("LogoutActivity ==> mainJson ==> "+jsonArray);
                                        JSONObject objJson;
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            objJson = jsonArray.getJSONObject(i);
                                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                                            if (Constant.GET_SUCCESS_MSG == 0) {
                                                strMessage = objJson.getString(Constant.MSG);
                                            } else {
                                                pDialog.dismiss();
                                                myApplication.saveIsLogin(false);
                                                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                Remember.clear();
                                                finish();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                                    dismissProgressDialog();
                                    pDialog.dismiss();
                                }

                            });

                        }).addButton(
                getString(android.R.string.no),
                R.color.pdlg_color_white,
                R.color.pdlg_color_red,
                pDialog::dismiss)
                .show();
    }

    public void profile(View v) {
        startActivity(new Intent(UserProfileActivity.this, EditProfileActivity.class));
    }

    public void gotoSetting(View v) {
        startActivity(new Intent(UserProfileActivity.this, SettingsActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
