package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.item.ItemPlan;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.PlanActivity;
import com.example.videostreamingapp.R;
import com.example.videostreamingapp.SelectPlanActivity;

import java.util.ArrayList;
import java.util.Random;

public class PlanListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemPlan> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;
    private int row_index = -1;
    private ArrayList<String> strColor;
    private Random random;

    public PlanListAdapter(Context context, ArrayList<ItemPlan> dataList, ArrayList<String> strColor) {
        this.dataList = dataList;
        this.mContext = context;
        this.strColor = strColor;
        random = new Random();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_select_plan, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemPlan singleItem = dataList.get(position);
        holder.textPlanName.setText(singleItem.getPlanName());
        holder.textPlanPrice.setText(singleItem.getPlanPrice());
        holder.textPlanCurrency.setText(singleItem.getPlanCurrencyCode());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvDesc.setText(Html.fromHtml(singleItem.getPlanDesc(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvDesc.setText(Html.fromHtml(singleItem.getPlanDesc()));
        }

        holder.textPlanDuration.setText(mContext.getString(R.string.plan_day_for, singleItem.getPlanDuration()));
        holder.lytPlan.setOnClickListener(v -> clickListener.onItemClick(position));

        holder.radioButton.setOnClickListener(view -> clickListener.onItemClick(position));

        holder.imgSelect.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, SelectPlanActivity.class);
            intent.putExtra("planId", singleItem.getPlanId());
            intent.putExtra("planName", singleItem.getPlanName());
            intent.putExtra("promoCode", "");
            intent.putExtra("planPrice", singleItem.getPlanPrice());
            intent.putExtra("planDuration", singleItem.getPlanDuration());
            mContext.startActivity(intent);
            ((PlanActivity) mContext).finish();
        });

        if (row_index > -1) {
            if (row_index == position) {
                holder.llDesc.setVisibility(View.VISIBLE);
                holder.radioButton.setChecked(true);
                if (position == dataList.size() - 1)
                    ((PlanActivity) mContext).showPromoCode(false);
                else
                    ((PlanActivity) mContext).showPromoCode(false);

            } else {
                holder.llDesc.setVisibility(View.VISIBLE);
                holder.radioButton.setChecked(false);
            }

        }

    }

    public void select(int position) {
        if (row_index == position) {
            row_index = -1;
        } else {
            row_index = position;
        }
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
        TextView textPlanName;
        TextView textPlanPrice;
        TextView textPlanDuration;
        TextView textPlanCurrency, tvDesc;
        RadioButton radioButton;
        LinearLayout llDesc, rootLayout;
        RelativeLayout lytPlan;
        CardView cardPlan;
        ImageView imgSelect;

        ItemRowHolder(View itemView) {
            super(itemView);
            textPlanName = itemView.findViewById(R.id.textPackName);
            llDesc = itemView.findViewById(R.id.ll_plan_desc);
            textPlanPrice = itemView.findViewById(R.id.textPrice);
            textPlanDuration = itemView.findViewById(R.id.textDay);
            textPlanCurrency = itemView.findViewById(R.id.textCurrency);
            tvDesc = itemView.findViewById(R.id.tv_plan_desc);
            radioButton = itemView.findViewById(R.id.radioButton);
            lytPlan = itemView.findViewById(R.id.lytPlan);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            cardPlan = itemView.findViewById(R.id.cardPlan);
            imgSelect = itemView.findViewById(R.id.imgSelect);
        }
    }

}
