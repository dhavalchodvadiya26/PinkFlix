package com.example.streamingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dialog.DatePickerDialogFragment;
import com.example.util.API;
import com.example.util.IsRTL;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.RequestParams;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.ybs.countrypicker.CountryPicker;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class SignUpScreenActivity extends AppCompatActivity implements View.OnClickListener {
    @NotEmpty
    EditText edtFullName;
    @Email
    EditText edtEmail;
    @Password
    EditText edtPassword;
    @ConfirmPassword
    EditText edtPasswordConfirm;
    @NotEmpty
    EditText edtDob;
    Button btnSignUp;
    String strName, strEmail, strPassword, strMessage;
    TextView txtLogin;
    ProgressDialog pDialog;
    private ImageView btnFacebookRegister;
    DatePickerDialogFragment datePickerDialogFragment;
    private String selectedBirthDay;
    private EditText edtCountry;
    private String mVerificationId;
    private String code = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        IsRTL.ifSupported(this);
        pDialog = new ProgressDialog(this);
        edtFullName = findViewById(R.id.editText_name_register);
        edtDob = findViewById(R.id.editText_dob);
        edtEmail = findViewById(R.id.editText_email_register);
        edtPassword = findViewById(R.id.editText_password_register);
        edtPasswordConfirm = findViewById(R.id.editText_confirm);
        btnFacebookRegister = findViewById(R.id.button_fb_login);
        btnSignUp = findViewById(R.id.button_submit);
        txtLogin = findViewById(R.id.textView_login_register);
        edtCountry = findViewById(R.id.edtCountry);
        edtCountry.setFocusable(false);
        edtCountry.setClickable(true);
        edtCountry.setOnClickListener(this);
        btnSignUp.setOnClickListener(v -> putSignUp());
        btnFacebookRegister.setVisibility(View.GONE);

        edtDob.setOnClickListener(v -> {
            datePickerDialogFragment = new DatePickerDialogFragment(date -> {
                selectedBirthDay = date;
                edtDob.setText(selectedBirthDay);
                edtDob.setError(null);
            });
            datePickerDialogFragment.show(getSupportFragmentManager(), "DatePickerDialog");
        });

        txtLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignInScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    public void putSignUp() {
        if (!TextUtils.isEmpty(edtFullName.getText().toString().trim()) && !TextUtils.isEmpty(edtEmail.getText().toString().trim())
                && !TextUtils.isEmpty(edtPassword.getText().toString().trim()) && !TextUtils.isEmpty(edtPasswordConfirm.getText().toString().trim())
                && edtPassword.getText().toString().trim().equalsIgnoreCase(edtPasswordConfirm.getText().toString().trim())
        ) {
            strName = edtFullName.getText().toString();
            strEmail = edtEmail.getText().toString();
            strPassword = edtPassword.getText().toString();


            RequestParams params = new RequestParams();

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
            jsObj.addProperty("name", strName);
            jsObj.addProperty("email", strEmail);
            jsObj.addProperty("password", strPassword);
            jsObj.addProperty("code", edtCountry.getText().toString().trim());
            params.put("data", API.toBase64(jsObj.toString()));
            System.out.println("data ==> " + API.toBase64(jsObj.toString()));
            startActivity(new Intent(SignUpScreenActivity.this, VerifyOTPActivity.class).putExtra("phone", edtEmail.getText().toString().trim())
                    .putExtra("code", edtCountry.getText().toString().trim()).putExtra("data", API.toBase64(jsObj.toString())));

        } else {
            if (TextUtils.isEmpty(edtFullName.getText().toString().trim()))
                showToast("Enter name");
            else if (TextUtils.isEmpty(edtEmail.getText().toString().trim()))
                showToast("Enter mobile number");
            else if (TextUtils.isEmpty(edtPassword.getText().toString().trim()))
                showToast("Enter password");
            else if (TextUtils.isEmpty(edtPasswordConfirm.getText().toString().trim()))
                showToast("Enter confirm password");
            else if (!edtPassword.getText().toString().trim().equalsIgnoreCase(edtPasswordConfirm.getText().toString().trim()))
                showToast("Password & Confirm Password does not Match");

        }
    }

    public void showToast(String msg) {
        Toast.makeText(SignUpScreenActivity.this, msg, Toast.LENGTH_SHORT).show();
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
    public void onClick(View v) {
        if (v == edtCountry) {
            CountryPicker picker = CountryPicker.newInstance("Select Country");
            picker.setListener((name, code, dialCode, i) -> {
                edtCountry.setText(dialCode);
                picker.dismiss();
            });

            picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            code = phoneAuthCredential.getSmsCode();

        }


        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(SignUpScreenActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
            startActivity(new Intent(SignUpScreenActivity.this, VerifyOTPActivity.class).putExtra("phone", edtEmail.getText().toString().trim())
                    .putExtra("code", code).putExtra("id", mVerificationId));
        }
    };
}
