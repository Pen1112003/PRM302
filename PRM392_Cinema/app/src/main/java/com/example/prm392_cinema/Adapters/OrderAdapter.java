package com.example.prm392_cinema.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_cinema.R;
import com.example.prm392_cinema.Services.BookingService;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<BookingService.BookingDetailAllDTO> orderList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int orderId);
    }

    public OrderAdapter(List<BookingService.BookingDetailAllDTO> orderList, Context context, OnItemClickListener listener) {
        this.orderList = orderList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        BookingService.BookingDetailAllDTO order = orderList.get(position);

        // Load movie poster using Glide
        Glide.with(context)
            .load(order.getPoster()) 
            .placeholder(R.drawable.ic_launcher_background) // Placeholder image while loading
            .error(R.drawable.ic_launcher_background) // Image to show if loading fails
            .into(holder.ivMoviePoster);

        holder.movieTitle.setText(order.getMovieTitle());
        holder.showtime.setText(order.getShowtime()); 
        holder.totalAmount.setText(String.format("%,.0f VND", order.getTotalAmount()));
        holder.status.setText("Đơn hàng: " + order.getStatus());

        // Get the default text color from the theme to handle view recycling properly
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        int defaultColor = ContextCompat.getColor(context, typedValue.resourceId);

        // Display custom payment status with colors based on order status
        if ("Completed".equals(order.getStatus())) {
            holder.paymentStatus.setText("Đã thanh toán");
            holder.paymentStatus.setTextColor(Color.parseColor("#4CAF50")); // Green color
        } else if ("Pending".equals(order.getStatus())) {
            holder.paymentStatus.setText("Chưa thanh toán");
            holder.paymentStatus.setTextColor(Color.parseColor("#F44336")); // Red color
        } else {
            // Fallback for other statuses, using the default text color
            holder.paymentStatus.setText(order.getPaymentStatus()); 
            holder.paymentStatus.setTextColor(defaultColor); // Set to default theme color
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(order.getOrderId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMoviePoster;
        TextView movieTitle, showtime, totalAmount, status, paymentStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMoviePoster = itemView.findViewById(R.id.ivMoviePoster);
            movieTitle = itemView.findViewById(R.id.movieTitle);
            showtime = itemView.findViewById(R.id.showtime);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            status = itemView.findViewById(R.id.status);
            paymentStatus = itemView.findViewById(R.id.paymentStatus);
        }
    }
}
