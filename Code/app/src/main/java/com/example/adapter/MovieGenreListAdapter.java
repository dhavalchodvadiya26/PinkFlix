package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itemmodels.ItemGenre;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.example.streamingapp.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class MovieGenreListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemGenre> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;
    private int row_index = -1;

    public MovieGenreListAdapter(Context context, ArrayList<ItemGenre> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_language_genre_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemGenre singleItem = dataList.get(position);
        holder.text.setText(singleItem.getGenreName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener);
            }
        });

        if (row_index > -1) {
            if (row_index == position) {
                holder.view.setBackgroundResource(R.drawable.image_genre_select_bg);
            } else {
                holder.view.setBackgroundResource(R.drawable.image_genre_bg);
            }
        }
    }

    public void select(int position) {
        row_index = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView text;
        CardView cardView;
        RoundedImageView image;
        View view;

        ItemRowHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            image = itemView.findViewById(R.id.image);
            cardView = itemView.findViewById(R.id.cardView);
            view = itemView.findViewById(R.id.view_movie_adapter);
        }
    }

}
