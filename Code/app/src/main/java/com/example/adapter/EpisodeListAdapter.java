package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.item.ItemEpisode;
import com.example.util.GradientTextView;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EpisodeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemEpisode> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;
    private int row_index = -1;
    private boolean isPurchased;

    public EpisodeListAdapter(Context context, ArrayList<ItemEpisode> dataList, boolean isPurchased) {
        this.dataList = dataList;
        this.mContext = context;
        this.isPurchased = isPurchased;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_episode_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemEpisode singleItem = dataList.get(position);
        holder.text.setText(singleItem.getEpisodeName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.textDescription.setText(Html.fromHtml(singleItem.getDescription(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.textDescription.setText(Html.fromHtml(singleItem.getDescription()));

        }

        holder.rootLayout.setOnClickListener(v -> PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener));
        Picasso.with(mContext).load(singleItem.getEpisodeImage()).into(holder.imagePlay);
        if (row_index > -1) {
            if (row_index == position) {
                holder.text.setTypeface(holder.text.getTypeface(), Typeface.BOLD);
            } else {
                holder.text.setTypeface(holder.text.getTypeface(), Typeface.BOLD);
            }
        }

        if (singleItem.isDownload()) {
            if (singleItem.isPremium()) {
                if (isPurchased) {
                    holder.imageDownload.setVisibility(View.VISIBLE);
                } else {
                    holder.imageDownload.setVisibility(View.VISIBLE);
                }
            } else {
                holder.imageDownload.setVisibility(View.VISIBLE);
            }
        } else {
            holder.imageDownload.setVisibility(View.VISIBLE);
        }

        holder.imageDownload.setOnClickListener(view -> {
            if (singleItem.getDownloadUrl().isEmpty()) {
                Toast.makeText(mContext, mContext.getString(R.string.download_not_found), Toast.LENGTH_SHORT).show();
            } else {
                try {
                    mContext.startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(singleItem.getDownloadUrl())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    Toast.makeText(mContext, mContext.getString(R.string.invalid_download), Toast.LENGTH_SHORT).show();
                }
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

    public void select(int position) {
        row_index = position;
        notifyDataSetChanged();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView textDescription;
        GradientTextView text;
        ImageView imagePlay, imageDownload;
        RelativeLayout rootLayout;

        ItemRowHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textEpisodes);
            imagePlay = itemView.findViewById(R.id.imageEpPlay);
            imageDownload = itemView.findViewById(R.id.imageEpDownload);
            textDescription = itemView.findViewById(R.id.textDescription);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }

}
