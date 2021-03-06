package com.example.streamingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.onesignal.OneSignal;

public class SettingActivity extends AppCompatActivity {
    private MyApplication myApplication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        myApplication = MyApplication.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }
        LinearLayout lytPrivacy = findViewById(R.id.lytPrivacy);
        LinearLayout lytAbout = findViewById(R.id.lytAbout);
        LinearLayout lytTechnicalIssue = findViewById(R.id.lytTechnicalIssue);
        LinearLayout lytProduction = findViewById(R.id.lytProduction);
        LinearLayout lytArtist = findViewById(R.id.lytArtist);
        LinearLayout  lytTerms = findViewById(R.id.lytTerms);
        LinearLayout  lytRefund = findViewById(R.id.lytRefund);
        Switch notificationSwitch = findViewById(R.id.switch_notification);

        notificationSwitch.setChecked(myApplication.getNotification());

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            myApplication.saveIsNotification(isChecked);
            OneSignal.setSubscription(isChecked);
        });

        lytAbout.setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, AboutUsActivity.class));
        });

        lytTechnicalIssue.setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, TechnicalHelpActivity.class));
        });

        lytProduction.setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, ProductionCollaborationActivity.class));
        });

        lytArtist.setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, ArtistListActivity.class));
        });

        lytPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(SettingActivity.this,PrivacyPolicyActivity.class));
            }
        });

        lytTerms.setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, TandCActivity.class));
        });

        lytRefund.setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, RefundPolicyActivity.class));
        });


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
