package com.example.prm392_cinema;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {
    private Context context;
    private List<Calendar> dates;
    private OnDateClickListener listener;
    private int selectedPosition = 0; 

    public interface OnDateClickListener {
        void onDateClick(Calendar date);
    }

    public DateAdapter(Context context, List<Calendar> dates, OnDateClickListener listener) {
        this.context = context;
        this.dates = dates;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_picker_item, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        Calendar date = dates.get(position);
        holder.bind(date, position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDateClick(date);
                int previousPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        private TextView dayOfWeekTextView;
        private TextView dayOfMonthTextView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfWeekTextView = itemView.findViewById(R.id.dayOfWeekTextView);
            dayOfMonthTextView = itemView.findViewById(R.id.dayOfMonthTextView);
        }

        public void bind(Calendar date, boolean isSelected) {
            SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE", Locale.getDefault());
            SimpleDateFormat dayOfMonthFormat = new SimpleDateFormat("d", Locale.getDefault());

            dayOfWeekTextView.setText(dayOfWeekFormat.format(date.getTime()));
            dayOfMonthTextView.setText(dayOfMonthFormat.format(date.getTime()));

            if (isSelected) {
                itemView.setBackgroundColor(Color.parseColor("#f3ea28"));
                dayOfWeekTextView.setTextColor(Color.BLACK);
                dayOfMonthTextView.setTextColor(Color.BLACK);
            } else {
                itemView.setBackgroundColor(Color.WHITE);
                dayOfWeekTextView.setTextColor(Color.BLACK);
                dayOfMonthTextView.setTextColor(Color.BLACK);
            }
        }
    }
}
