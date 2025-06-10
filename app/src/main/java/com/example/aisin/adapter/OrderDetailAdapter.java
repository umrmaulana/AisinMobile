package com.example.aisin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aisin.R;
import com.example.aisin.model.OrderDetailModel;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {
    private List<OrderDetailModel> detailList;
    private Context context;

    public OrderDetailAdapter(Context context, List<OrderDetailModel> detailList) {
        this.context = context;
        this.detailList = detailList;
    }

    public void updateData(List<OrderDetailModel> newDetailList) {
        this.detailList = newDetailList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetailModel detail = detailList.get(position);

        holder.tvPartNumber.setText(detail.getNo_part());
        holder.tvQuantity.setText(detail.getQty() + " items");
        holder.tvItemSequence.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return detailList != null ? detailList.size() : 0;
    }

    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        TextView tvPartNumber, tvQuantity, tvItemSequence;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPartNumber = itemView.findViewById(R.id.tvPartNumber);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvItemSequence = itemView.findViewById(R.id.tvItemSequence);
        }
    }
}
