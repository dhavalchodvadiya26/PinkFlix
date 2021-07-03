package com.example.streamingapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TechnicalHelpActivity extends AppCompatActivity {

    RelativeLayout call_us,mail_us;
    TextView mobile,email;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technical_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Request Help");
        }

        mobile=findViewById(R.id.t4);
        email=findViewById(R.id.t6);
        call_us=findViewById(R.id.call_us);
        mail_us=findViewById(R.id.mail_us);

        call_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean installed=appInstalledOrNot("com.whatsapp");
                System.out.println("App Installed ==> "+installed);

                if (installed){
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=+918356967009"));
                    startActivity(intent);
                }else{
                    Uri uri1 = Uri.parse("smsto: +918356967009");
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SENDTO, uri1);
                    startActivity(Intent.createChooser(shareIntent, "Send Message via"));
                    Toast.makeText(TechnicalHelpActivity.this, "Whatsapp Not installed on your Device", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mail_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail(email.getText().toString());
            }
        });

    }

    private boolean appInstalledOrNot(String s) {
        PackageManager packageManager=getPackageManager();
        boolean app_installed;
        try {
            packageManager.getPackageInfo(s,PackageManager.GET_ACTIVITIES);
            app_installed=true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            app_installed=false;
        }
        return app_installed;
    }

    protected void sendEmail(String email) {
        Log.i("Send email", "");
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:contact@sanskrati.in"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL,  "contact@sanskrati.in");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "PinkFlix Customer Query ");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Log.i("Finished sending email.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(TechnicalHelpActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
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