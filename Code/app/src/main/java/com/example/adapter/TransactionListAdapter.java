package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.item.ItemSubscription;
import com.example.videostreamingapp.R;

import java.util.ArrayList;

public class TransactionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemSubscription> dataList;


    public TransactionListAdapter(ArrayList<ItemSubscription> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_transaction, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemSubscription singleItem = dataList.get(position);
        holder.txtPlanDetail.setText(singleItem.getTransactionId());
        holder.txtPlanAmount.setText(singleItem.getTransction_payment_amount());
        holder.paymentId.setText(singleItem.getTransction_payment_id());
        holder.txtDate.setText(singleItem.getTransction_date());
        holder.txtExpired.setText(singleItem.getTransction_expiry_date());
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }


    static class ItemRowHolder extends RecyclerView.ViewHolder {

        TextView txtPlanDetail, txtPlanAmount, paymentId, txtDate,txtExpired;


        ItemRowHolder(View itemView) {
            super(itemView);
            txtPlanDetail = itemView.findViewById(R.id.txtPlanDetail);
            txtPlanAmount = itemView.findViewById(R.id.txtPlanAmount);
            paymentId = itemView.findViewById(R.id.paymentId);
            txtDate = itemView.findViewById(R.id.txtDate);


            txtExpired = itemView.findViewById(R.id.txtExpired);
        }
    }
}
