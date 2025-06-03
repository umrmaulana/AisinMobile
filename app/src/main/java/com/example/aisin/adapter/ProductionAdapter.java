package com.example.aisin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aisin.R;
import com.example.aisin.model.ProductionModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductionAdapter extends RecyclerView.Adapter<ProductionAdapter.ProductionViewHolder> {
    private List<ProductionModel> productionList;
    private Context context;
    
    public ProductionAdapter(Context context, List<ProductionModel> productionList) {
        this.context = context;
        this.productionList = productionList;
    }
    
    public void updateData(List<ProductionModel> newProductionList) {
        this.productionList = newProductionList;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ProductionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_production, parent, false);
        return new ProductionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProductionViewHolder holder, int position) {
        ProductionModel production = productionList.get(position);
        
        holder.tvProductionNumber.setText(production.getNo_fg());
        holder.tvSequenceNumber.setText(String.valueOf(position + 1));
        holder.tvQuantity.setText(String.valueOf(production.getQty()));
        
        // Parse and format the date
        try {
            String createdAt = production.getCreated_at();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            Date date = inputFormat.parse(createdAt);
            holder.tvDate.setText(outputFormat.format(date));
        } catch (ParseException e) {
            holder.tvDate.setText(production.getCreated_at());
        }
        
        // Set status as "Completed" since it's not in the API response
        if (holder.tvStatus != null) {
            holder.tvStatus.setText("Completed");
        }
    }
    
    @Override
    public int getItemCount() {
        return productionList != null ? productionList.size() : 0;
    }
    
    public static class ProductionViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductionNumber, tvSequenceNumber, tvDate, tvQuantity, tvStatus;
        
        public ProductionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductionNumber = itemView.findViewById(R.id.tvProductionNumber);
            tvSequenceNumber = itemView.findViewById(R.id.tvSequenceNumber);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
