package com.example.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.item.ItemSlider;
import com.example.videostreamingapp.MovieDetailsActivity2;
import com.example.videostreamingapp.R;
import com.example.videostreamingapp.ShowDetailsActivity;
import com.example.videostreamingapp.SportDetailsActivity;
import com.example.videostreamingapp.TVDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SliderListAdapter extends PagerAdapter {

    private final LayoutInflater inflater;
    private final Activity context;
    private final ArrayList<ItemSlider> mList;


    public SliderListAdapter(Activity context, ArrayList<ItemSlider> itemChannels) {
        this.context = context;
        this.mList = itemChannels;
        inflater = context.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View imageLayout = inflater.inflate(R.layout.row_slider_item, container, false);
        assert imageLayout != null;
        ImageView imageView = imageLayout.findViewById(R.id.image);
        TextView textTitle = imageLayout.findViewById(R.id.text);
        CardView rootLayout = imageLayout.findViewById(R.id.rootLayout);


        textTitle.setSelected(true);
        final ItemSlider itemChannel = mList.get(position);
        Picasso.with(context).load(itemChannel.getSliderImage()).into(imageView);
        textTitle.setText(itemChannel.getSliderTitle());

        rootLayout.setOnClickListener(v -> {
            Class<?> aClass;
            String recentId = itemChannel.getId();
            String recentType = itemChannel.getSliderType();
            switch (recentType) {
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
            Intent intent = new Intent(context, aClass);
            intent.putExtra("Id", recentId);
            context.startActivity(intent);
        });

        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }
}
