package com.example.videostreamingapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.util.API;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.ybs.countrypicker.CountryPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import cn.refactor.library.SmoothCheckBox;
import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    @NotEmpty
    @Email
    EditText edtEmail;
    @NotEmpty
    @Password
    EditText edtPassword;
    String strEmail, strPassword, strMessage, strName, strUserId;
    Button btnLogin;
    private FirebaseAuth mAuth;
    TextView btnForgotPass, btnRegister;
    MyApplication myApplication;
    ProgressDialog pDialog;
    SmoothCheckBox checkBox,checkBox2;
    boolean isFromOtherScreen = false;
    String postId, postType;
    ImageView btnFacebook, btnGoogle;
    private EditText edtCountry;
    private CallbackManager callbackManager;
    private ImageView imgShowPassword;
    private boolean showPassword;
    private GoogleSignInOptions gso;
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        IsRTL.ifSupported(this);
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        if (intent.hasExtra("isOtherScreen")) {
            isFromOtherScreen = true;
            postId = intent.getStringExtra("postId");
            postType = intent.getStringExtra("postType");
        }

        pDialog = new ProgressDialog(this);
        myApplication = MyApplication.getInstance();
        edtCountry = findViewById(R.id.edtCountry);
        edtCountry.setFocusable(false);
        edtCountry.setClickable(true);
        edtCountry.setOnClickListener(this);
        edtEmail = findViewById(R.id.editText_email_login_activity);
        edtPassword = findViewById(R.id.editText_password_login_activity);
        btnLogin = findViewById(R.id.button_login_activity);

        btnForgotPass = findViewById(R.id.textView_forget_password_login);
        btnRegister = findViewById(R.id.textView_signup_login);
        checkBox = findViewById(R.id.checkbox_login_activity);
        checkBox2 = findViewById(R.id.checkbox_login_age);
        btnFacebook = findViewById(R.id.button_fb_login);
        btnGoogle = findViewById(R.id.button_google_login);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        btnFacebook.setOnClickListener(v -> doFacebookLogin());
        btnRegister.setOnClickListener(v -> {
            Intent intent1 = new Intent(SignInActivity.this, SignUpActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent1);
        });

        btnForgotPass.setOnClickListener(v -> {
            Intent intent13 = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
            intent13.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent13);
        });

        btnLogin.setOnClickListener(v ->
        {
            if (!TextUtils.isEmpty(edtEmail.getText().toString().trim())) {
                if (!TextUtils.isEmpty(edtPassword.getText().toString().trim())) {
                    if (checkBox2.isChecked()) {
                        putSignIn();
                        showProgressDialog();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Verify Your Age", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Please Enter Mobile Number", Toast.LENGTH_LONG).show();
            }
        });
        imgShowPassword = findViewById(R.id.imgShowPassword);
        imgShowPassword.setOnClickListener(v -> {
            if (showPassword) {
                imgShowPassword.setImageResource(R.drawable.ic_hide);
                edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                showPassword = false;
            } else {
                imgShowPassword.setImageResource(R.drawable.ic_eye);
                edtPassword.setTransformationMethod(null);
                edtPassword.setSelection(edtPassword.getText().toString().length());
                showPassword = true;
            }
        });
        if (myApplication.getIsRemember()) {
            checkBox.setChecked(true);
            edtEmail.setText(myApplication.getRememberEmail());
            edtPassword.setText(myApplication.getRememberPassword());
            imgShowPassword.setVisibility(View.GONE);
        }
        myApplication.saveIsIntroduction(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void doFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, (object, response) -> parseFacebookResponse(response, accessToken));
                Bundle bundle = new Bundle();
                bundle.putString("fields", "id,name,picture.type(large)");
                graphRequest.setParameters(bundle);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignInActivity.this, "Something problem in facebook login. try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignInActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseFacebookResponse(GraphResponse response, AccessToken accessToken) {
        try {
            JSONObject jsonObject = response.getJSONObject();
            String strName = jsonObject.optString(Constant.FB_NAME);
            String phone = jsonObject.optString("phone");
            String fbUserID = accessToken.getUserId();
            if (strName.equalsIgnoreCase("")) {
                Toast.makeText(this, "We could not get name from your facebook account.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Welcome!, You have successfully login with facebook", Toast.LENGTH_SHORT).show();
            callFacebookApi(strName, phone, fbUserID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callFacebookApi(String strName, String phone, String fbUserID) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        System.out.println("callFacebookApi ==> fbUserID ==> " + fbUserID);

        params.put("name", strName);
        params.put("is_facebook", fbUserID);
        params.put("s_type", "facebook");
        params.put("phone", phone);
        Log.d("DETAILS", params.toString());

        client.post(Constant.LOGIN_WITH_FACEBOOK_URL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissProgressDialog();
                String result = new String(responseBody);
                System.out.println("Result_Facebook ==> " + result);
                Log.d("RESPONSE", result);
                Log.d("Result_Facebook", result);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        if (Constant.GET_SUCCESS_MSG == 0) {
                            strMessage = objJson.getString(Constant.MSG);
                        } else {
                            try {
                                JSONArray arrUserList = objJson.getJSONArray("user_list");
                                if (arrUserList != null &&
                                        arrUserList.length() > 0) {
                                    JSONObject objUser = arrUserList.getJSONObject(i);
                                    SignInActivity.this.strName = objUser.getString(Constant.USER_NAME);
                                    strUserId = objUser.getString(Constant.FB_ID);
                                }
                            } catch (Exception e) {
                                strMessage = "Error while login with facebook";
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();

                Intent i = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
                Toast.makeText(SignInActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            callGoogleAPI(account.getDisplayName(), account.getEmail(), account.getId());
            updateUserInterface(account);
        } catch (ApiException e) {
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
            updateUserInterface(null);
        }
    }

    private void updateUI(@Nullable FirebaseUser user) {
        if (user != null) {
        }
    }

    private void updateUserInterface(@Nullable GoogleSignInAccount account) {
        if (account != null) {
        }
    }


    private void callGoogleAPI(String strName, String email, String googleId) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        System.out.println("callGoogleAPI => googleID => " + googleId);
        params.put("name", strName);
        params.put("email", email);
        params.put("is_facebook", googleId);
        params.put("s_type", "google");

        Log.d("DETAILS", params.toString());

        client.post(Constant.LOGIN_WITH_FACEBOOK_URL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissProgressDialog();
                String result = new String(responseBody);
                System.out.println("Result_Google ==> " + result);
                Log.d("RESPONSE", result);
                Log.d("Result_Google", result);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        if (Constant.GET_SUCCESS_MSG == 0) {
                            strMessage = objJson.getString(Constant.MSG);
                        } else {
                            try {
                                JSONArray arrUserList = objJson.getJSONArray("user_list");
                                if (arrUserList != null &&
                                        arrUserList.length() > 0) {
                                    JSONObject objUser = arrUserList.getJSONObject(i);
                                    SignInActivity.this.strName = objUser.getString(Constant.USER_NAME);
                                    strUserId = objUser.getString(Constant.FB_ID);
                                }
                            } catch (Exception e) {
                                strMessage = "Error while login with Google";
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
                Intent i = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
                Toast.makeText(SignInActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    @SuppressLint("HardwareIds")
    public void putSignIn() {
        strEmail = edtEmail.getText().toString();
        strPassword = edtPassword.getText().toString();

        if (checkBox.isChecked()) {
            myApplication.saveIsRemember(true);
            myApplication.saveRemember(strEmail, strPassword);
        } else {
            myApplication.saveIsRemember(false);
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("email", strEmail);
        jsObj.addProperty("password", strPassword);
        jsObj.addProperty("imei_number", Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.LOGIN_URL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissProgressDialog();
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    System.out.println("LoginActivity ==> mainJson ==> "+jsonArray);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        if (Constant.GET_SUCCESS_MSG == 0) {
                            strMessage = objJson.getString(Constant.MSG);
                        } else {
                            strName = objJson.getString(Constant.USER_NAME);
                            strUserId = objJson.getString(Constant.USER_ID);
                        }
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

//    public void putSignIn() {
//        strEmail = edtEmail.getText().toString();
//        strPassword = edtPassword.getText().toString();
//
//        if (checkBox.isChecked()) {
//            myApplication.saveIsRemember(true);
//            myApplication.saveRemember(strEmail, strPassword);
//        } else {
//            myApplication.saveIsRemember(false);
//        }
//
//        AsyncHttpClient client = new AsyncHttpClient();
//        RequestParams params = new RequestParams();
//
//        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
//        jsObj.addProperty("email", strEmail);
//        jsObj.addProperty("password", strPassword);
//        params.put("data", API.toBase64(jsObj.toString()));
//
//        client.post(Constant.LOGIN_URL, params, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onStart() {
//                super.onStart();
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                dismissProgressDialog();
//                String result = new String(responseBody);
//                try {
//                    JSONObject mainJson = new JSONObject(result);
//                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
//                    JSONObject objJson;
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        objJson = jsonArray.getJSONObject(i);
//                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
//                        if (Constant.GET_SUCCESS_MSG == 0) {
//                            strMessage = objJson.getString(Constant.MSG);
//                        } else {
//                            strName = objJson.getString(Constant.USER_NAME);
//                            strUserId = objJson.getString(Constant.USER_ID);
//                        }
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                setResult();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                dismissProgressDialog();
//            }
//
//        });
//    }

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
            builder.setMessage(strMessage)
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, id) -> {
                        dialog.dismiss();
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            myApplication.saveIsLogin(true);
            myApplication.saveLogin(strUserId, strName, strEmail);
            if (isFromOtherScreen) {
                Class<?> aClass;
                switch (postType) {
                    case "Movies":
                        aClass = MovieDetailsActivity2.class;
                        break;
                    case "Shows":
                        aClass = ShowDetailsActivity.class;
                        break;
                    case "LiveTV":
                        aClass = TVDetailsActivity.class;
                        break;
                    default:
                        aClass = SportDetailsActivity.class;
                        break;
                }
                Intent intent = new Intent(SignInActivity.this, aClass);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Id", postId);
                startActivity(intent);
            } else {
                Intent i = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(SignInActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        if (v == edtCountry) {
            CountryPicker picker = CountryPicker.newInstance("Select Country");
            picker.setListener((name, code, dialCode, i) -> {
                edtCountry.setText(dialCode);
                if (dialCode.equalsIgnoreCase("+91"))
                    edtEmail.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                else
                    edtEmail.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

                picker.dismiss();
            });
            picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUserInterface(account);
    }
}
