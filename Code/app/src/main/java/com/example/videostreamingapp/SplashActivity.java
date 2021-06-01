package com.example.videostreamingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.InflateException;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.Remember;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cz.msebera.android.httpclient.Header;


public class SplashActivity extends Activity {

    private static final int SPLASH_DURATION = 500;
    MyApplication myApplication;
    boolean isLoginDisable = false;
    private boolean mIsBackButtonPressed;
    String Id = "";
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        IsRTL.ifSupported(this);
        generateKeyHash();
        myApplication = MyApplication.getInstance();
        if (NetworkUtils.isConnected(SplashActivity.this)) {
            checkLicense();
        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.e("Splash", deepLink.toString());

                            String referLink = deepLink.toString();
                            try {
                                referLink = referLink.substring(referLink.lastIndexOf("=") + 1);

                                Id = referLink.substring(referLink.lastIndexOf("=") + 1);
                                System.out.println("SplashSctivity ==> Movie_id ==> " + Id);

                                Remember.putString("Id", Id);

                            } catch (Exception e) {
                                Log.e("Splash", "Error: " + e.toString());
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("SplashScreen", "getDynamicLink:onFailure", e);
                    }
                });
    }


    private void splashScreen() {
        System.out.println("SplashScreen() ==> Movie_ID ==> " + Id);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (!mIsBackButtonPressed) {
                if (myApplication.getIsIntroduction()) {
                    if (isLoginDisable && myApplication.getIsLogin()) {
                        Intent intent;
                        if (Id.equals("")) {
                            intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        } else {
                            intent = new Intent(getApplicationContext(), MovieDetailsActivity2.class);
                            intent.putExtra("Id", Id);
                        }
                        startActivity(intent);
                        finish();
                    } else if (!isLoginDisable && myApplication.getIsLogin()) {
                        myApplication.saveIsLogin(false);
                        Toast.makeText(SplashActivity.this, getString(R.string.user_disable), Toast.LENGTH_SHORT).show();
                        Intent intent;
                        if (Id.equals("")) {
                            intent = new Intent(getApplicationContext(), SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        } else {
                            intent = new Intent(getApplicationContext(), MovieDetailsActivity2.class);
                            intent.putExtra("Id", Id);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent;
                        if (Id.equals("")) {
                            intent = new Intent(getApplicationContext(), SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        } else {
                            intent = new Intent(getApplicationContext(), MovieDetailsActivity2.class);
                            intent.putExtra("Id", Id);
                        }
                        startActivity(intent);
                        finish();
                    }

                } else {
                    System.out.println("Movie_ID ==> else ==> " + Id);
                    Intent intent;
                    if (Id.equals("")) {
                        intent = new Intent(getApplicationContext(), SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    } else {
                        intent = new Intent(getApplicationContext(), MovieDetailsActivity2.class);
                        intent.putExtra("Id", Id);
                    }
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_DURATION);
    }


    @Override
    public void onBackPressed() {
        mIsBackButtonPressed = true;
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_splash);
        IsRTL.ifSupported(this);
        generateKeyHash();
        myApplication = MyApplication.getInstance();
        if (NetworkUtils.isConnected(SplashActivity.this)) {
            checkLicense();
        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.e("Splash", deepLink.toString());

                            String referLink = deepLink.toString();
                            try {
                                referLink = referLink.substring(referLink.lastIndexOf("=") + 1);

                                Id = referLink.substring(referLink.lastIndexOf("=") + 1);
                                System.out.println("SplashSctivity ==> Movie_id ==> " + Id);

                                Remember.putString("Id", Id);

                            } catch (Exception e) {
                                Log.e("Splash", "Error: " + e.toString());
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("SplashScreen", "getDynamicLink:onFailure", e);
                    }
                });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setContentView(R.layout.activity_splash);
        IsRTL.ifSupported(this);
        generateKeyHash();
        myApplication = MyApplication.getInstance();
        if (NetworkUtils.isConnected(SplashActivity.this)) {
            checkLicense();
        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.e("Splash", deepLink.toString());

                            String referLink = deepLink.toString();
                            try {
                                referLink = referLink.substring(referLink.lastIndexOf("=") + 1);

                                Id = referLink.substring(referLink.lastIndexOf("=") + 1);
                                System.out.println("SplashSctivity ==> Movie_id ==> " + Id);

                                Remember.putString("Id", Id);

                            } catch (Exception e) {
                                Log.e("Splash", "Error: " + e.toString());
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("SplashScreen", "getDynamicLink:onFailure", e);
                    }
                });
    }

    private void checkLicense() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        System.out.println("SplashActivity ==> checkLicense() ==> params ==> " + params);
        params.put("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : " ");
        params.put("version", BuildConfig.VERSION_NAME);

        client.post(Constant.APP_UPDATE_CHECK, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                System.out.println("SplashActivity ==> checkLicense() ==> result ==> " + result);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    isLoginDisable = mainJson.getBoolean("user_status");
                    int status_code = mainJson.getInt("status_code");
                    if (status_code==200) {
                        splashScreen();
                    } else {
                        updateDialog();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("ERROR1", e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
                Log.d("ERROR", error.getMessage());
                finish();
            }

        });
    }

    private void updateDialog() {
        final String appPackageName = getPackageName();

        new AlertDialog.Builder(SplashActivity.this)
                .setTitle("CinePrime")
                .setMessage("New Version of This app is Available Now!!")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("UPDATE NOW", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        dialog.dismiss();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("UPDATE LATER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        splashScreen();
                    }
                })
                .setIcon(R.drawable.ic_v)
                .show();
    }

    public void generateKeyHash() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            StringBuilder stringBuffer = new StringBuilder();
            for (Signature signature : signatures) {
                byte[] bytes = signature.toByteArray();
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(bytes);
                String hash = Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT);
                stringBuffer.append(hash);
            }
            Log.d("HASH", stringBuffer.toString());
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
