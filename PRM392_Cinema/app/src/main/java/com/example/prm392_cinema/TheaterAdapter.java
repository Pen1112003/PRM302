package com.example.prm392_cinema;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.model.TheaterShowtime;

import java.util.List;

public class TheaterAdapter extends RecyclerView.Adapter<TheaterAdapter.TheaterViewHolder> {

    private Context context;
    private List<TheaterShowtime> theaterShowtimes;
    private ShowtimeAdapter.OnShowtimeClickListener onShowtimeClickListener;

    public TheaterAdapter(Context context, List<TheaterShowtime> theaterShowtimes, ShowtimeAdapter.OnShowtimeClickListener onShowtimeClickListener) {
        this.context = context;
        this.theaterShowtimes = theaterShowtimes;
        this.onShowtimeClickListener = onShowtimeClickListener;
    }

    @NonNull
    @Override
    public TheaterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_theater, parent, false);
        return new TheaterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TheaterViewHolder holder, int position) {
        TheaterShowtime theaterShowtime = theaterShowtimes.get(position);
        holder.theaterName.setText(theaterShowtime.getTheaterName());

        ShowtimeAdapter showtimeAdapter = new ShowtimeAdapter(context, theaterShowtime.getShowtimes(), onShowtimeClickListener);
        holder.showtimesRecyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        holder.showtimesRecyclerView.setAdapter(showtimeAdapter);
    }

    @Override
    public int getItemCount() {
        return theaterShowtimes.size();
    }

    public static class TheaterViewHolder extends RecyclerView.ViewHolder {
        TextView theaterName;
        RecyclerView showtimesRecyclerView;

        public TheaterViewHolder(@NonNull View itemView) {
            super(itemView);
            theaterName = itemView.findViewById(R.id.theaterName);
            showtimesRecyclerView = itemView.findViewById(R.id.showtimesRecyclerView);
        }
    }
}
