package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.item.ItemRecent;
import com.example.util.NetworkUtils;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

public class HomeRecentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemRecent> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;
    private int columnWidth;

    public HomeRecentListAdapter(Context context, ArrayList<ItemRecent> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        columnWidth = NetworkUtils.getScreenWidth(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_row_recent_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemRecent singleItem = dataList.get(position);

        holder.image.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 4));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(columnWidth / 2 - 20, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, (int) mContext.getResources().getDimension(R.dimen.item_space), (int) mContext.getResources().getDimension(R.dimen.item_space));
        holder.rootLayout.setLayoutParams(layoutParams);
        holder.progress.setProgress(new Random().nextInt((100 - 10) + 1) + 10);
        Picasso.with(mContext).load(singleItem.getRecentImage()).into(holder.image);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    static class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image;
        CardView cardView;
        RelativeLayout rootLayout;
        ProgressBar progress;

        ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            cardView = itemView.findViewById(R.id.cardView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            progress = itemView.findViewById(R.id.progress);
        }
    }

}
