package com.example.aisin.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aisin.R;
import com.example.aisin.model.ReceivingModel;
import com.example.aisin.receiving.ReceivingDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReceivingAdapter extends RecyclerView.Adapter<ReceivingAdapter.ReceivingViewHolder> {
    
    private List<ReceivingModel> receivingList;
    private Context context;
    
    public ReceivingAdapter(Context context, List<ReceivingModel> receivingList) {
        this.context = context;
        this.receivingList = receivingList;
    }
    
    public void updateData(List<ReceivingModel> newReceivingList) {
        this.receivingList = newReceivingList;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ReceivingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receiving, parent, false);
        return new ReceivingViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ReceivingViewHolder holder, int position) {
        ReceivingModel receiving = receivingList.get(position);
        
        // Set PKB number
        holder.tvReceivingNumber.setText(receiving.getNo_pkb());
        
        // Set position number
        holder.tvSequenceNumber.setText(String.valueOf(position + 1));
        
        // Format and set the date
        try {
            // Parse the received_at date
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            Date date = inputFormat.parse(receiving.getReceived_at());
            holder.tvDate.setText(outputFormat.format(date));
        } catch (ParseException e) {
            holder.tvDate.setText(receiving.getReceived_at());
        }
        
        // Hide supplier section since it's not in the API data
        if (holder.supplierContainer != null) {
            holder.supplierContainer.setVisibility(View.GONE);
        }
        
        // Hide quantity section since it's not in the API data
        if (holder.quantityContainer != null) {
            holder.quantityContainer.setVisibility(View.GONE);
        }
        
        // Set status as "Received" since all items are received
        if (holder.tvStatus != null) {
            holder.tvStatus.setText("Received");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
        }

        // Set click listener to open detail page
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReceivingDetailActivity.class);
            intent.putExtra("pkb_number", receiving.getNo_pkb());
            context.startActivity(intent);
        });
    }
    
    @Override
    public int getItemCount() {
        return receivingList != null ? receivingList.size() : 0;
    }
    
    public static class ReceivingViewHolder extends RecyclerView.ViewHolder {
        TextView tvReceivingNumber, tvSequenceNumber, tvDate, tvStatus;
        LinearLayout supplierContainer, quantityContainer, dateContainer;
        
        public ReceivingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReceivingNumber = itemView.findViewById(R.id.tvReceivingNumber);
            tvSequenceNumber = itemView.findViewById(R.id.tvSequenceNumber);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            
            supplierContainer = itemView.findViewById(R.id.supplierContainer);
            dateContainer = itemView.findViewById(R.id.dateContainer);
            quantityContainer = itemView.findViewById(R.id.quantityContainer);
        }
    }
}
