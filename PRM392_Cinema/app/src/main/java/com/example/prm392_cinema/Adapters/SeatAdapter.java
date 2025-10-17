package com.example.prm392_cinema.Adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.Models.Seat;
import com.example.prm392_cinema.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {
    private final int BORDER_WIDTH = 6;
    private final List<Seat> seatList;
    public Map<Integer, Boolean> seatIndexToSelected;
    private int totalPrice = 0;

    public int getTotalPrice() {
        return totalPrice;
    }

    public SeatAdapter(List<Seat> seatList) {
        this.seatList = seatList;
        this.seatIndexToSelected = new HashMap<>();
    }

    public List<Seat> getSeatList() {
        return seatList;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seat_item, parent, false);
        return new SeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        Seat seat = seatList.get(position);
        updateSeatView(holder, seat);

        holder.itemView.setOnClickListener(v -> {
            if (seat.isAvailable()) {
                // Đổi trạng thái từ "có sẵn" sang "đã chọn"
                this.seatIndexToSelected.put(seat.getSeatId(), true);
                totalPrice += seat.getPrice();
                seat.setStatus(Seat.STATUS_SELECTED);
            } else if (seat.isSelected()) {
                // Đổi trạng thái từ "đã chọn" sang "trống"
                this.seatIndexToSelected.remove(seat.getSeatId());
                totalPrice -= seat.getPrice();
                seat.setStatus(Seat.STATUS_AVAILABLE);
            }
            // Cập nhật lại view khi ghế được chọn
            notifyItemChanged(position);
        });
    }

    private void updateSeatView(SeatViewHolder holder, Seat seat) {
        if (!seat.isSeat()) {
            holder.itemView.setVisibility(View.INVISIBLE);  // Hide seat for empty space
            holder.seatName.setText("");
            return;
        }

        holder.seatName.setText(seat.getName());
        GradientDrawable background = (GradientDrawable) holder.seatStyle.getBackground();

        if (seat.isBooked()) {
            background.setColor(Color.rgb(187, 187, 187));
            background.setStroke(BORDER_WIDTH, Color.rgb(187, 187, 187));
        } else if (seat.isSelected()) {
            background.setColor(Color.rgb(243, 234, 40));
            background.setStroke(BORDER_WIDTH, Color.rgb(243, 234, 40));
        } else if (seat.isNormal()) {
            background.setColor(Color.rgb(255, 255, 255));
            background.setStroke(BORDER_WIDTH, Color.GREEN);
        } else {
            background.setColor(Color.rgb(255, 255, 255));
            background.setStroke(BORDER_WIDTH, Color.RED);
        }
    }

    public List<Integer> getSelectedSeatId() {
        List<Integer> result = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> entry : seatIndexToSelected.entrySet()) {
            Integer key = entry.getKey();
            Boolean value = entry.getValue();
            if (!value) continue;
            result.add(key);
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    static class SeatViewHolder extends RecyclerView.ViewHolder {
        View seatStyle;
        TextView seatName;

        SeatViewHolder(View itemView) {
            super(itemView);
            seatStyle = itemView;
            seatName = itemView.findViewById(R.id.itemText);
        }
    }
}
