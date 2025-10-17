package com.example.prm392_cinema.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.R;
import com.example.prm392_cinema.Services.BookingService;

import java.util.List;

public class FabShowAdapter extends RecyclerView.Adapter<FabShowAdapter.FabDetailViewHolder> {
    private List<BookingService.FabDetail> fabDetails;

    public FabShowAdapter(List<BookingService.FabDetail> fabDetails) {
        this.fabDetails = fabDetails;
    }

    @NonNull
    @Override
    public FabDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fab_item_show, parent, false);
        return new FabDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FabDetailViewHolder holder, int position) {
        BookingService.FabDetail fabDetail = fabDetails.get(position);
        holder.txtFoodName.setText(fabDetail.foodName);
        holder.txtAmount.setText("Số lượng: " + fabDetail.amount);
        holder.txtPrice.setText(fabDetail.price + " VNĐ");
    }

    @Override
    public int getItemCount() {
        return fabDetails.size();
    }

    public static class FabDetailViewHolder extends RecyclerView.ViewHolder {
        TextView txtFoodName;
        TextView txtAmount;
        TextView txtPrice;

        public FabDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }
}