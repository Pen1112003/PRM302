package com.example.prm392_cinema;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.model.Showtime;

import java.util.List;

public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.ShowtimeViewHolder> {

    private Context context;
    private List<Showtime> showtimes;
    private OnShowtimeClickListener onShowtimeClickListener;

    // Interface for click events
    public interface OnShowtimeClickListener {
        void onShowtimeClick(Showtime showtime);
    }

    public ShowtimeAdapter(Context context, List<Showtime> showtimes, OnShowtimeClickListener onShowtimeClickListener) {
        this.context = context;
        this.showtimes = showtimes;
        this.onShowtimeClickListener = onShowtimeClickListener;
    }

    @NonNull
    @Override
    public ShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_showtime, parent, false);
        return new ShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowtimeViewHolder holder, int position) {
        Showtime showtime = showtimes.get(position);
        holder.showtime.setText(showtime.getShowtime());

        holder.itemView.setOnClickListener(v -> {
            if (onShowtimeClickListener != null) {
                onShowtimeClickListener.onShowtimeClick(showtime);
            }
        });
    }

    @Override
    public int getItemCount() {
        return showtimes.size();
    }

    public static class ShowtimeViewHolder extends RecyclerView.ViewHolder {
        TextView showtime;

        public ShowtimeViewHolder(@NonNull View itemView) {
            super(itemView);
            showtime = itemView.findViewById(R.id.showtime);
        }
    }
}
