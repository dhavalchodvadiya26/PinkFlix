package com.example.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.streamingapp.AboutUsActivity;
import com.example.streamingapp.ArtistListActivity;
import com.example.streamingapp.MyApplication;
import com.example.streamingapp.PrivacyPolicyActivity;
import com.example.streamingapp.ProductionCollaborationActivity;
import com.example.streamingapp.R;
import com.example.streamingapp.RefundPolicyActivity;
import com.example.streamingapp.TechnicalHelpActivity;
import com.example.streamingapp.TandCActivity;
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
            startActivity(new Intent(getActivity(), AboutUsActivity.class));
        });

        lytTechnicalIssue.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TechnicalHelpActivity.class));
        });

        lytProduction.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProductionCollaborationActivity.class));
        });

        lytArtist.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ArtistListActivity.class));
        });

        lytPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),PrivacyPolicyActivity.class));
            }
        });

        lytTerms.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TandCActivity.class));
        });

        lytRefund.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RefundPolicyActivity.class));
        });

        return rootView;
    }
}
