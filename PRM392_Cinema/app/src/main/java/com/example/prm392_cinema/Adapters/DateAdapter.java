package com.example.prm392_cinema.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.R;
import com.example.prm392_cinema.DateUtils.DateGroup;

import java.util.List;

// DateAdapter.java
public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {
    private Context context;
    private List<DateGroup> dateGroups;

    public DateAdapter(Context context, List<DateGroup> dateGroups) {
        this.context = context;
        this.dateGroups = dateGroups;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DateGroup dateGroup = dateGroups.get(position);
        holder.bind(context, dateGroup);
    }

    @Override
    public int getItemCount() {
        return dateGroups.size();
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;
        private TextView dateTextView;

        public DateViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.showtimesRecyclerView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }

        public void bind(Context context, DateGroup dateGroup) {
            dateTextView.setText(dateGroup.getFormatDate());
            LinearLayoutManager layout = new LinearLayoutManager(itemView.getContext());
            layout.setOrientation(RecyclerView.HORIZONTAL);
            recyclerView.setLayoutManager(layout);
            recyclerView.setAdapter(new ShowtimeAdapter(context,dateGroup.getFormatDate(), dateGroup.getShowtimes()));
        }
    }
}

