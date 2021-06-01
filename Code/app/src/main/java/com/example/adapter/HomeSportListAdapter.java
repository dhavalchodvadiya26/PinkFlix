package com.example.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.item.ItemSport;
import com.example.util.NetworkUtils;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeSportListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemSport> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;
    private int columnWidth;
    private boolean isRTL;

    public HomeSportListAdapter(Context context, ArrayList<ItemSport> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        columnWidth = NetworkUtils.getScreenWidth(mContext);
        isRTL = Boolean.parseBoolean(mContext.getString(R.string.isRTL));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_row_sport_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemSport singleItem = dataList.get(position);
        holder.text.setText(singleItem.getSportName());
        holder.image.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth / 2, columnWidth / 4));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(columnWidth / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, (int) mContext.getResources().getDimension(R.dimen.item_space), (int) mContext.getResources().getDimension(R.dimen.item_space));
        holder.rootLayout.setLayoutParams(layoutParams);

        Picasso.with(mContext).load(singleItem.getSportImage()).into(holder.image);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener);
            }
        });

        if (singleItem.isPremium()) {
            holder.textPremium.setVisibility(View.VISIBLE);

            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.RECTANGLE);
            gd.setColor(mContext.getResources().getColor(R.color.tab_select));
            if (isRTL) {
                gd.setCornerRadii(new float[]{40.0f, 40.0f, 0, 0, 0, 0, 40.0f, 40.0f});
            } else {
                gd.setCornerRadii(new float[]{0, 0, 40.0f, 40.0f, 40.0f, 40.0f, 0, 0});
            }
            holder.textPremium.setBackground(gd);
        } else {
            holder.textPremium.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text, textPremium;
        CardView cardView;
        RelativeLayout rootLayout;

        ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            textPremium = itemView.findViewById(R.id.textLang);
            cardView = itemView.findViewById(R.id.cardView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }
}
