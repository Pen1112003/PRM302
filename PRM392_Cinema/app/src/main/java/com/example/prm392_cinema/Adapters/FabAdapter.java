package com.example.prm392_cinema.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.Models.Fab;
import com.example.prm392_cinema.R;
import com.example.prm392_cinema.Services.FabService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FabAdapter extends RecyclerView.Adapter<FabAdapter.FabViewHolder> {
    private List<Fab> fabList;
    public Map<Integer, Integer> fabIdToAmount;

    public FabAdapter(List<Fab> fabList) {
        this.fabList = fabList;
        this.fabIdToAmount = new HashMap<>();
    }

    public List<Fab> getFabList() {
        return fabList;
    }

    @NonNull
    @Override
    public FabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fab_item, parent, false);
        return new FabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FabViewHolder holder, int position) {
        Fab comboItem = fabList.get(position);
        holder.fabTitle.setText(comboItem.getName());
        holder.fabDescription.setText(comboItem.getDescription());
        holder.fabPrice.setText(comboItem.getPrice() + " VNĐ");
        holder.fabImage.setImageResource(comboItem.getName().contains("Bắp") ? R.drawable.corn : R.drawable.coca);

        // Update quantity
        holder.quantityText.setText(String.valueOf(comboItem.getQuantity()));

        // Decrease quantity
        holder.buttonDecrease.setOnClickListener(v -> {
            int quantity = comboItem.getQuantity();
            if (quantity > 0) {
                comboItem.setQuantity(--quantity);
                holder.quantityText.setText(String.valueOf(quantity));
            }

            fabIdToAmount.put(comboItem.getFoodId(), comboItem.getQuantity());
        });

        // Increase quantity
        holder.buttonIncrease.setOnClickListener(v -> {
            int quantity = comboItem.getQuantity();
            comboItem.setQuantity(++quantity);
            holder.quantityText.setText(String.valueOf(quantity));

            fabIdToAmount.put(comboItem.getFoodId(), comboItem.getQuantity());
        });
    }

    public List<FabService.FabOrderDto> getOrderFabDto() {
        List<FabService.FabOrderDto> list = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : fabIdToAmount.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            if (value <= 0) continue;

            list.add(new FabService.FabOrderDto(key, value));
        }
        return list;
    }

    @Override
    public int getItemCount() {
        return fabList.size();
    }

    static class FabViewHolder extends RecyclerView.ViewHolder {
        ImageView fabImage;
        TextView fabTitle, fabDescription, fabPrice, quantityText;
        Button buttonDecrease, buttonIncrease;

        FabViewHolder(View itemView) {
            super(itemView);
            fabImage = itemView.findViewById(R.id.comboImage);
            fabTitle = itemView.findViewById(R.id.comboTitle);
            fabDescription = itemView.findViewById(R.id.comboDescription);
            fabPrice = itemView.findViewById(R.id.comboPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
        }
    }
}
