package com.example.prm392_cinema.Adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private Map<Integer, Boolean> seatSelectionMap;
    private int totalPrice = 0;
    private OnSeatSelectionChangedListener listener;

    // Interface để lắng nghe sự kiện thay đổi lựa chọn ghế
    public interface OnSeatSelectionChangedListener {
        void onSeatSelectionChanged();
    }

    public void setOnSeatSelectionChangedListener(OnSeatSelectionChangedListener listener) {
        this.listener = listener;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public SeatAdapter(List<Seat> seatList) {
        this.seatList = seatList;
        this.seatSelectionMap = new HashMap<>();
        for (Seat seat : seatList) {
            if (seat.isSeat() && !seat.isBooked()) {
                seatSelectionMap.put(seat.getSeatId(), false);
            }
        }
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
            if (!seat.isSeat() || seat.isBooked()) {
                return;
            }

            boolean isSelected = seatSelectionMap.get(seat.getSeatId());
            seatSelectionMap.put(seat.getSeatId(), !isSelected);

            if (!isSelected) {
                totalPrice += seat.getPrice();
            } else {
                totalPrice -= seat.getPrice();
            }

            notifyItemChanged(position);

            // Thông báo cho listener về sự thay đổi
            if (listener != null) {
                listener.onSeatSelectionChanged();
            }
        });
    }

    private void updateSeatView(SeatViewHolder holder, Seat seat) {
        if (!seat.isSeat()) {
            holder.itemView.setVisibility(View.INVISIBLE);
            return;
        }
        holder.itemView.setVisibility(View.VISIBLE);
        holder.seatName.setText(seat.getName());
        GradientDrawable background = (GradientDrawable) holder.itemView.getBackground();

        if (seat.isBooked()) {
            background.setColor(Color.rgb(187, 187, 187));
            background.setStroke(BORDER_WIDTH, Color.rgb(187, 187, 187));
        } else if (seatSelectionMap.containsKey(seat.getSeatId()) && seatSelectionMap.get(seat.getSeatId())) {
            background.setColor(Color.rgb(243, 234, 40));
            background.setStroke(BORDER_WIDTH, Color.rgb(243, 234, 40));
        } else {
            background.setColor(Color.WHITE);
            if (seat.getSeatTypeId() == 2) {
                background.setStroke(BORDER_WIDTH, Color.RED);
            } else {
                background.setStroke(BORDER_WIDTH, Color.GREEN);
            }
        }
    }

    public List<Integer> getSelectedSeatId() {
        List<Integer> selectedSeats = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> entry : seatSelectionMap.entrySet()) {
            if (entry.getValue()) {
                selectedSeats.add(entry.getKey());
            }
        }
        return selectedSeats;
    }

    public List<String> getSelectedSeatNames() {
        List<String> selectedSeatNames = new ArrayList<>();
        for (Seat seat : seatList) {
            if (seatSelectionMap.containsKey(seat.getSeatId()) && seatSelectionMap.get(seat.getSeatId())) {
                selectedSeatNames.add(seat.getName());
            }
        }
        return selectedSeatNames;
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    static class SeatViewHolder extends RecyclerView.ViewHolder {
        TextView seatName;

        SeatViewHolder(View itemView) {
            super(itemView);
            seatName = itemView.findViewById(R.id.itemText);
        }
    }
}
