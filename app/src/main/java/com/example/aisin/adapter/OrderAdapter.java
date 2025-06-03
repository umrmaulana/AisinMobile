package com.example.aisin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aisin.R;
import com.example.aisin.model.OrderModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<OrderModel> orderList;
    private Context context;
    
    public OrderAdapter(Context context, List<OrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }
    
    public void updateData(List<OrderModel> newOrderList) {
        this.orderList = newOrderList;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orderList.get(position);
        
        holder.tvOrderNumber.setText(order.getNo_po());
        holder.tvSequenceNumber.setText(String.valueOf(position + 1));
        
        // Parse and format the date
        try {
            String createdAt = order.getCreated_at();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            Date date = inputFormat.parse(createdAt);
            holder.tvDate.setText(outputFormat.format(date));
        } catch (ParseException e) {
            holder.tvDate.setText(order.getCreated_at());
        }
        
        // Hide customer and total sections as they're not provided by the API
        if (holder.customerContainer != null) {
            holder.customerContainer.setVisibility(View.GONE);
        }
        
        if (holder.totalContainer != null) {
            holder.totalContainer.setVisibility(View.GONE);
        }
        
        // Hide status as it's not provided by the API
        if (holder.tvStatus != null) {
            holder.tvStatus.setVisibility(View.GONE);
        }
    }
    
    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }
    
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNumber, tvSequenceNumber, tvDate, tvCustomer, tvTotal, tvStatus;
        LinearLayout customerContainer, totalContainer;
        
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvSequenceNumber = itemView.findViewById(R.id.tvSequenceNumber);
            tvDate = itemView.findViewById(R.id.tvDate);
            customerContainer = itemView.findViewById(R.id.customerContainer);
            totalContainer = itemView.findViewById(R.id.totalContainer);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            
            // Find text views within containers if needed
            if (customerContainer != null) {
                tvCustomer = customerContainer.findViewById(R.id.tvCustomer);
            }
            
            if (totalContainer != null) {
                tvTotal = totalContainer.findViewById(R.id.tvTotal);
            }
        }
    }
}
