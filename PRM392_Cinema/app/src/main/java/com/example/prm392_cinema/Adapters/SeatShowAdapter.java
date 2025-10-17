package com.example.prm392_cinema.Adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.R;
import com.example.prm392_cinema.Services.BookingService;

import java.util.List;
import java.util.Objects;

public class SeatShowAdapter extends RecyclerView.Adapter<SeatShowAdapter.ViewHolder> {

    private List<BookingService.SeatDetail> seatList;

    public SeatShowAdapter(List<BookingService.SeatDetail> seatList) {
        this.seatList = seatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seat_show_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingService.SeatDetail seat = seatList.get(position);
        Log.d("CALL", seat.seatNumber);
        holder.txtSeatName.setText(seat.seatNumber);
        holder.typeName.setText(seat.seatType);
        holder.price.setText((seat.seatPrice + (seat.seatType.equals("Ghế VIP") ? 10000 : 0)) + " VND");

        GradientDrawable background = (GradientDrawable) holder.seatStyle.getBackground();

        if (Objects.equals(seat.seatType, "Ghế Thường")) {
            background.setColor(Color.rgb(255, 255, 255));
            background.setStroke(6, Color.GREEN);
        } else {
            background.setColor(Color.rgb(255, 255, 255));
            background.setStroke(6, Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtSeatName, typeName, price;
        View seatStyle;

        public ViewHolder(View itemView) {
            super(itemView);
            txtSeatName = itemView.findViewById(R.id.seatId);
            typeName = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            seatStyle = itemView.findViewById(R.id.seatLayout);
        }
    }
}
