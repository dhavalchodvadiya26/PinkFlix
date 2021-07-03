package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itemmodels.ItemMovie;
import com.example.util.NetworkUtils;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.example.streamingapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeMovieVideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemMovie> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;
    private int columnWidth;
    private boolean isRTL;
    private boolean isHomeMore;

    public HomeMovieVideoListAdapter(Context context, ArrayList<ItemMovie> dataList, boolean isHomeMore) {
        this.dataList = dataList;
        this.mContext = context;
        columnWidth = NetworkUtils.getScreenWidth(mContext);
        isRTL = Boolean.parseBoolean(mContext.getString(R.string.isRTL));
        this.isHomeMore = isHomeMore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_row_movie_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemMovie singleItem = dataList.get(position);
        holder.text.setText(singleItem.getMovieName());
        holder.textDuration.setText(singleItem.getMovieDuration());
        holder.image.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth / 3 - 20, columnWidth / 3 + 100));

        if (!isHomeMore) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(columnWidth / 3 - 20, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, (int) mContext.getResources().getDimension(R.dimen.item_space_new), (int) mContext.getResources().getDimension(R.dimen.item_space_new));
            holder.rootLayout.setLayoutParams(layoutParams);
        }

        Picasso.with(mContext).load(singleItem.getMovieImage()).into(holder.image);
        holder.cardView.setOnClickListener(v -> PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener));

        if (singleItem.isPremium()) {
            if (singleItem.getMovieAccess().equalsIgnoreCase("Premium")) {
                holder.imgPremium.setVisibility(View.GONE);
                holder.imgPremium.setImageResource(R.drawable.ic_primium);
            } else if (singleItem.getMovieAccess().equalsIgnoreCase("Rental")) {
                holder.imgPremium.setVisibility(View.GONE);
                holder.imgPremium.setImageResource(R.drawable.ic_rental);
            }
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
        ImageView image,imgPremium;
        TextView text, textDuration, textPremium;
        CardView cardView;
        RelativeLayout rootLayout;

        ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            textDuration = itemView.findViewById(R.id.textTime);
            textPremium = itemView.findViewById(R.id.textLang);
            cardView = itemView.findViewById(R.id.cardView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            imgPremium = itemView.findViewById(R.id.imgPremium);
        }
    }
}
