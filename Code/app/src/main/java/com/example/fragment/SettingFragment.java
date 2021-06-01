package com.example.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.videostreamingapp.AboutActivity;
import com.example.videostreamingapp.ArtistActivity;
import com.example.videostreamingapp.MainActivity;
import com.example.videostreamingapp.MyApplication;
import com.example.videostreamingapp.PrivacyPolicyActivity;
import com.example.videostreamingapp.ProductionActivity;
import com.example.videostreamingapp.R;
import com.example.videostreamingapp.RefundActivity;
import com.example.videostreamingapp.SettingsActivity;
import com.example.videostreamingapp.TechnicalIssueActivity;
import com.example.videostreamingapp.TermsandConditionActivity;
import com.onesignal.OneSignal;


public class SettingFragment extends Fragment {

    private MyApplication myApplication;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        myApplication = MyApplication.getInstance();

        LinearLayout lytPrivacy = rootView.findViewById(R.id.lytPrivacy);
        LinearLayout lytAbout = rootView.findViewById(R.id.lytAbout);
        LinearLayout lytTechnicalIssue = rootView.findViewById(R.id.lytTechnicalIssue);
        LinearLayout lytProduction = rootView.findViewById(R.id.lytProduction);
        LinearLayout lytArtist = rootView.findViewById(R.id.lytArtist);
        LinearLayout  lytTerms = rootView.findViewById(R.id.lytTerms);
        LinearLayout  lytRefund = rootView.findViewById(R.id.lytRefund);
        Switch notificationSwitch = rootView.findViewById(R.id.switch_notification);

        notificationSwitch.setChecked(myApplication.getNotification());

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            myApplication.saveIsNotification(isChecked);
            OneSignal.setSubscription(isChecked);
        });

        lytAbout.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(),AboutActivity.class));
        });

        lytTechnicalIssue.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TechnicalIssueActivity.class));
        });

        lytProduction.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProductionActivity.class));
        });

        lytArtist.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ArtistActivity.class));
        });

        lytPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),PrivacyPolicyActivity.class));
            }
        });

        lytTerms.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TermsandConditionActivity.class));
        });

        lytRefund.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RefundActivity.class));
        });

        return rootView;
    }
}
