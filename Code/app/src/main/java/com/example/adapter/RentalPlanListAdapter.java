package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itemmodels.ItemTransaction;
import com.example.streamingapp.R;

import java.util.ArrayList;

public class RentalPlanListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemTransaction> dataList;


    public RentalPlanListAdapter(ArrayList<ItemTransaction> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rental_transaction, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemTransaction singleItem = dataList.get(position);

        holder.txtPlanAmount.setText(singleItem.getTransction_payment_amount());
        holder.paymentId.setText(singleItem.getTransction_payment_id());
        holder.txtDate.setText(singleItem.getTransction_date());
        holder.txtPromocode.setText(singleItem.getMovie_name());
        holder.txtExpiryDate.setText(singleItem.getExpirty_date());

        holder.txtMovieName.setText("Movie Name");
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }


    static class ItemRowHolder extends RecyclerView.ViewHolder {

        TextView  txtPlanAmount, paymentId, txtDate, txtPromocode, txtMovieName,txtExpiryDate;


        ItemRowHolder(View itemView) {
            super(itemView);

            txtPlanAmount = itemView.findViewById(R.id.txtPlanAmount);
            paymentId = itemView.findViewById(R.id.paymentId);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtPromocode = itemView.findViewById(R.id.txtPromocode);
            txtMovieName = itemView.findViewById(R.id.txtMovieName);
            txtExpiryDate = itemView.findViewById(R.id.txtExpiryDate);
        }
    }
}
