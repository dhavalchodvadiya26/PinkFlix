package com.example.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.videostreamingapp.EmbeddedPlayerActivity;
import com.example.videostreamingapp.R;
import com.squareup.picasso.Picasso;

public class EmbeddedImagesFragment extends Fragment {


    public static EmbeddedImagesFragment newInstance(String streamUrl, String imageCover, boolean isPlayVisible) {
        EmbeddedImagesFragment f = new EmbeddedImagesFragment();
        Bundle args = new Bundle();
        args.putString("streamUrl", streamUrl);
        args.putString("imageCover", imageCover);
        args.putBoolean("isPlayVisible", isPlayVisible);
        f.setArguments(args);
        return f;
    }

    private ImageView imageCover, imagePlay;
    private String streamUrl, imageUrl;
    private boolean isPlayVisible;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_embedded_image, container, false);

        if (getArguments() != null) {
            streamUrl = getArguments().getString("streamUrl");
            imageUrl = getArguments().getString("imageCover");
            isPlayVisible = getArguments().getBoolean("isPlayVisible");
        }

        imageCover = rootView.findViewById(R.id.imageCover);
        imagePlay = rootView.findViewById(R.id.imagePlay);

        if (isPlayVisible) {
            imagePlay.setVisibility(View.VISIBLE);
        } else {
            imagePlay.setVisibility(View.GONE);
        }

        Picasso.with(getActivity()).load(imageUrl).into(imageCover);

        imagePlay.setOnClickListener(v -> {
            if (!streamUrl.isEmpty()) {
                Intent intent = new Intent(getActivity(), EmbeddedPlayerActivity.class);
                intent.putExtra("streamUrl", streamUrl);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), getString(R.string.stream_not_found), Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
