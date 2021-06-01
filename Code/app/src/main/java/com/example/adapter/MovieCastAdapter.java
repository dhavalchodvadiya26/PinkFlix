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

import com.example.item.CastModel;
import com.example.item.ItemMovie;
import com.example.util.NetworkUtils;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieCastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<CastModel> castList;
    private Context mContext;
    private RvOnClickListener clickListener;
    private int columnWidth;
    private boolean isRTL;
    private boolean isHomeMore;

    public MovieCastAdapter(Context context, ArrayList<CastModel> dataList, boolean isHomeMore) {
        this.castList = dataList;
        this.mContext = context;
        columnWidth = NetworkUtils.getScreenWidth(mContext);
        isRTL = Boolean.parseBoolean(mContext.getString(R.string.isRTL));
        this.isHomeMore = isHomeMore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cast, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final CastModel singleItem = castList.get(position);
        holder.txtCastName.setText(singleItem.getCastName());
        Picasso.with(mContext).load(singleItem.getCastImage()).into(holder.imgCast);
    }

    @Override
    public int getItemCount() {
        return (null != castList ? castList.size() : 0);
    }


    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    static class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView imgCast;
        TextView txtCastName;

        ItemRowHolder(View itemView) {
            super(itemView);
            imgCast = itemView.findViewById(R.id.imgCast);
            txtCastName = itemView.findViewById(R.id.txtCastName);
        }
    }
}
