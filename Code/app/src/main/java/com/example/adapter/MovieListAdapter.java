package com.example.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.item.ItemMovie;
import com.example.util.NetworkUtils;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemMovie> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;
    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private int columnWidth;
    private boolean isRTL;

    public MovieListAdapter(Context context, ArrayList<ItemMovie> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        columnWidth = NetworkUtils.getScreenWidth(mContext);
        isRTL = Boolean.parseBoolean(mContext.getString(R.string.isRTL));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_movie_item, parent, false);
            return new ItemRowHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == VIEW_TYPE_ITEM) {
            final ItemRowHolder holder = (ItemRowHolder) viewHolder;
            final ItemMovie singleItem = dataList.get(position);
            holder.textDuration.setText(singleItem.getMovieDuration());
            holder.image.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 3 + 80));
            Picasso.with(mContext).load(singleItem.getMovieImage()).into(holder.image);

            holder.cardView.setOnClickListener(v -> {
                PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener);
            });

            if (singleItem.isPremium()) {
                if (singleItem.getMovieAccess().equalsIgnoreCase("Premium")) {
                    holder.imgPremium.setVisibility(View.VISIBLE);
                    holder.imgPremium.setImageResource(R.drawable.ic_primium);
                } else if (singleItem.getMovieAccess().equalsIgnoreCase("Rental")) {
                    holder.imgPremium.setVisibility(View.VISIBLE);
                    holder.imgPremium.setImageResource(R.drawable.ic_rental);
                }
            } else {
                holder.textPremium.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() + 1 : 0);
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image, imgPremium;
        TextView text, textDuration, textPremium;
        CardView cardView;


        ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            textDuration = itemView.findViewById(R.id.textTime);
            textPremium = itemView.findViewById(R.id.textLang);
            cardView = itemView.findViewById(R.id.cardView);
            imgPremium = itemView.findViewById(R.id.imgPremium);
        }
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        static ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }
}
