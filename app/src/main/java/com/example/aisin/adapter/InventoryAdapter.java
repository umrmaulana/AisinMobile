package com.example.aisin.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aisin.R;
import com.example.aisin.model.PartModel;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<PartModel> partList;
    private Context context;

    public InventoryAdapter(Context context, List<PartModel> partList) {
        this.context = context;
        this.partList = partList;
    }

    public void updateData(List<PartModel> newPartList) {
        this.partList = newPartList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        PartModel part = partList.get(position);
        
        holder.tvPartNumber.setText(part.getPart_number());
        holder.tvNumber.setText(String.valueOf(position + 1));
        holder.tvStock.setText(String.valueOf(part.getStock()));
        
        // Set status text
        String status = part.getStatus();
        holder.tvStatus.setText(status);
        
        // Set card background color and status text color based on status
        MaterialCardView cardView = (MaterialCardView) holder.itemView;
        
        switch (status.toLowerCase()) {
            case "urgent":
                // Red background for urgent
                cardView.setCardBackgroundColor(Color.parseColor("#FFEBEE")); // Light red
                holder.tvStatus.setBackgroundResource(R.drawable.status_badge_urgent);
                holder.tvStatus.setTextColor(Color.parseColor("#C62828")); // Dark red
                break;
                
            case "critical":
                // Orange background for critical
                cardView.setCardBackgroundColor(Color.parseColor("#FFF3E0")); // Light orange
                holder.tvStatus.setBackgroundResource(R.drawable.status_badge_critical);
                holder.tvStatus.setTextColor(Color.parseColor("#E65100")); // Dark orange
                break;
                
            case "normal":
                // Blue background for normal
                cardView.setCardBackgroundColor(Color.parseColor("#E3F2FD")); // Light blue
                holder.tvStatus.setBackgroundResource(R.drawable.status_badge_normal);
                holder.tvStatus.setTextColor(Color.parseColor("#1565C0")); // Dark blue
                break;
                
            case "over":
                // Green background for over
                cardView.setCardBackgroundColor(Color.parseColor("#E8F5E9")); // Light green
                holder.tvStatus.setBackgroundResource(R.drawable.status_badge_over);
                holder.tvStatus.setTextColor(Color.parseColor("#2E7D32")); // Dark green
                break;
                
            default:
                // Default background for other statuses
                cardView.setCardBackgroundColor(Color.WHITE);
                holder.tvStatus.setBackgroundResource(R.drawable.status_badge_background);
                holder.tvStatus.setTextColor(Color.parseColor("#757575")); // Gray
                break;
        }
    }

    @Override
    public int getItemCount() {
        return partList != null ? partList.size() : 0;
    }

    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvPartNumber, tvNumber, tvStock, tvStatus;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPartNumber = itemView.findViewById(R.id.tvPartNumber);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
