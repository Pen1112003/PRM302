package com.example.prm392_cinema.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.HistoryOrder;
import com.example.prm392_cinema.OrderPaymentActivity;
import com.example.prm392_cinema.R;
import com.example.prm392_cinema.Services.BookingService;
import com.example.prm392_cinema.Utils;

import java.util.List;
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<BookingService.BookingDetailAllDTO> orderList;
    private Context context;

    public OrderAdapter(List<BookingService.BookingDetailAllDTO> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_show, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        BookingService.BookingDetailAllDTO order = orderList.get(position);
        Log.d("BOOKING", order.showtime.movie.getTitle());
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieName, tvShowTime, tvRoomName, tvDuration, tvStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            tvShowTime = itemView.findViewById(R.id.tvShowTime);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(final BookingService.BookingDetailAllDTO order) {
            // Đặt các thông tin cho TextView
            String statusValue;
            if (order.status.equals("Paid")) {
                Log.d("PAYMENT", "");
                statusValue = "Đã thanh toán";

            } else if (order.status.equals("Processing")) {
                statusValue = "Đang tiến hành";
            } else {
                statusValue = "Đã hủy";
            }
            tvMovieName.setText("Phim: " + order.showtime.movie.getTitle());
            tvShowTime.setText("Giờ chiếu: " + Utils.formatDateTime(order.showtime.showDate));
            tvRoomName.setText("Phòng: " + order.showtime.hall.hallName);
            tvDuration.setText("Thời lượng: " + order.showtime.movie.getDuration() + " phút");
            tvStatus.setText("Tình trạng: " + statusValue);

            // Thiết lập sự kiện click và truyền orderId qua Intent
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, OrderPaymentActivity.class);
                intent.putExtra("orderId", order.bookingId +""); // Truyền orderId của item hiện tại
                context.startActivity(intent);
            });
        }
    }
}
