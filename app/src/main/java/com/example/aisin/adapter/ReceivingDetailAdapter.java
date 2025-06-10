package com.example.aisin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aisin.R;
import com.example.aisin.model.ReceivingDetailModel;

import java.util.List;

public class ReceivingDetailAdapter extends RecyclerView.Adapter<ReceivingDetailAdapter.ReceivingDetailViewHolder> {
    private List<ReceivingDetailModel> detailList;
    private Context context;

    public ReceivingDetailAdapter(Context context, List<ReceivingDetailModel> detailList) {
        this.context = context;
        this.detailList = detailList;
    }

    public void updateData(List<ReceivingDetailModel> newDetailList) {
        this.detailList = newDetailList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReceivingDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receiving_detail, parent, false);
        return new ReceivingDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceivingDetailViewHolder holder, int position) {
        ReceivingDetailModel detail = detailList.get(position);

        holder.tvPartNumber.setText(detail.getNo_part());
        holder.tvQuantity.setText(detail.getQty() + " items");
        holder.tvItemSequence.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return detailList != null ? detailList.size() : 0;
    }

    public static class ReceivingDetailViewHolder extends RecyclerView.ViewHolder {
        TextView tvPartNumber, tvQuantity, tvItemSequence;

        public ReceivingDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPartNumber = itemView.findViewById(R.id.tvPartNumber);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvItemSequence = itemView.findViewById(R.id.tvItemSequence);
        }
    }
}
