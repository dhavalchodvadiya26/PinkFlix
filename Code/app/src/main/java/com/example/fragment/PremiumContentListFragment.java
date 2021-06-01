package com.example.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.videostreamingapp.MyApplication;
import com.example.videostreamingapp.PlanActivity;
import com.example.videostreamingapp.R;
import com.example.videostreamingapp.SignInActivity;

public class PremiumContentListFragment extends Fragment {
    int LAUNCH_SECOND_ACTIVITY = 101;

    public static PremiumContentListFragment newInstance(String postId, String postType, String movieAccess) {
        PremiumContentListFragment f = new PremiumContentListFragment();
        Bundle args = new Bundle();
        args.putString("postId", postId);
        args.putString("postType", postType);
        args.putString("movie_access", movieAccess);
        f.setArguments(args);
        return f;
    }

    private String postId, postType, movieAccess;
    private MyApplication myApplication;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_premium_content, container, false);
        if (getArguments() != null) {
            postId = getArguments().getString("postId");
            postType = getArguments().getString("postType");
            movieAccess = getArguments().getString("movie_access");
        }
        myApplication = MyApplication.getInstance();
        Button btnSubscribe = rootView.findViewById(R.id.btn_subscribe_now);
        ImageView imgRental = rootView.findViewById(R.id.imgRental);
        TextView text = rootView.findViewById(R.id.text);
        if (movieAccess.equalsIgnoreCase("rental")) {
            text.setText("Continue Watching this movie by Paying Rent.");
            btnSubscribe.setText("Rent Now");
            btnSubscribe.setVisibility(View.GONE);
            imgRental.setVisibility(View.VISIBLE);
        } else {

        }
        imgRental.setOnClickListener(v -> {
            if (myApplication.getIsLogin()) {
                Intent intentPlan;
                if (movieAccess.equalsIgnoreCase("rental"))
                    intentPlan = new Intent(getActivity(), PlanActivity.class).putExtra("rental", "true");
                else
                    intentPlan = new Intent(getActivity(), PlanActivity.class);

                intentPlan.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intentPlan, LAUNCH_SECOND_ACTIVITY);
            } else {
                Toast.makeText(getActivity(), getString(R.string.login_first), Toast.LENGTH_SHORT).show();

                Intent intentLogin = new Intent(getActivity(), SignInActivity.class);
                intentLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentLogin.putExtra("isOtherScreen", true);
                intentLogin.putExtra("postId", postId);
                intentLogin.putExtra("postType", postType);
                startActivity(intentLogin);
            }
        });
        btnSubscribe.setOnClickListener(view -> {
            if (myApplication.getIsLogin()) {
                Intent intentPlan;
                if (movieAccess.equalsIgnoreCase("rental"))
                    intentPlan = new Intent(getActivity(), PlanActivity.class).putExtra("rental", "true");
                else
                    intentPlan = new Intent(getActivity(), PlanActivity.class);

                intentPlan.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intentPlan, LAUNCH_SECOND_ACTIVITY);
            } else {
                Toast.makeText(getActivity(), getString(R.string.login_first), Toast.LENGTH_SHORT).show();

                Intent intentLogin = new Intent(getActivity(), SignInActivity.class);
                intentLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentLogin.putExtra("isOtherScreen", true);
                intentLogin.putExtra("postId", postId);
                intentLogin.putExtra("postType", postType);
                startActivity(intentLogin);
            }
        });

        return rootView;
    }
}
