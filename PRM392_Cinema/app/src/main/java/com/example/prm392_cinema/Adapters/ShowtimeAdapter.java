package com.example.prm392_cinema.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.HallActivity;
import com.example.prm392_cinema.Models.Showtime;
import com.example.prm392_cinema.R;
import com.example.prm392_cinema.Stores.HallScreenStore;

import java.util.List;

// ShowtimeAdapter.java
public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.ShowtimeViewHolder> {
    Context context;
    String showDate;
    private List<Showtime> showtimes;

    public ShowtimeAdapter(Context context, String showDate, List<Showtime> showtimes) {
        this.context = context;
        this.showDate = showDate;
        this.showtimes = showtimes;
    }

    @NonNull
    @Override
    public ShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.showtime_item, parent, false);
        return new ShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowtimeViewHolder holder, int position) {
        Showtime showtime = showtimes.get(position);
        // Bind showtime data to UI elements in item_showtime layout
        holder.bind(showtime);

        holder.itemView.setOnClickListener(v -> {
            HallScreenStore.showTimeId = showtime.getShowtimeId();
            HallScreenStore.hallName = showtime.getHallName();
            String showHour = showtime.getShowDate();
            HallScreenStore.showTime = "Ngày chiếu: " + showDate + ", " + showHour + " giờ";
            Intent intent = new Intent(context, HallActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return showtimes.size();
    }

    static class ShowtimeViewHolder extends RecyclerView.ViewHolder {
        private TextView timeText;

        public ShowtimeViewHolder(View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.timeText);
        }

        public void bind(Showtime showtime) {
            timeText.setText(showtime.getShowDate());
        }
    }
}

