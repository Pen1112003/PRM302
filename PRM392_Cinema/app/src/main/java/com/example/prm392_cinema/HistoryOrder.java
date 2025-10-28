package com.example.prm392_cinema;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_cinema.Adapters.OrderAdapter;
import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.BookingService;
import com.example.prm392_cinema.Stores.AuthStore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HistoryOrder extends AppCompatActivity implements OrderAdapter.OnItemClickListener {

    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private List<BookingService.BookingDetailAllDTO> orderList; 
    private ProgressBar progressBar; 
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_order);

        recyclerViewOrders = findViewById(R.id.orderRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, this, this); 
        recyclerViewOrders.setAdapter(orderAdapter);

        findViewById(R.id.btnSignOut).setOnClickListener(v -> handleSignOut());

        findViewById(R.id.backIcon).setOnClickListener(v -> handleBack());
        
        findViewById(R.id.btnHistory).setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserIdAndFetchData();
    }

    private void loadUserIdAndFetchData() {
        String userId = AuthStore.userId;

        Log.d("HistoryOrder", "Attempting to load data for userId: " + userId);

        if (userId != null && !userId.isEmpty()) {
            Log.d("HistoryOrder", "UserId found in AuthStore: " + userId);
            loadData(userId);
        } else {
            Log.e("HistoryOrder", "User ID not found in AuthStore. Cannot load data.");
            Toast.makeText(this, "Bạn cần đăng nhập để xem lịch sử.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(HistoryOrder.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void handleSignOut() {
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        AuthStore.userId = null;
        AuthStore.jwtToken = null;

        Intent intent = new Intent(HistoryOrder.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void handleBack() {
        finish(); 
    }

    private void loadData(String userId) { 
        BookingService apiService = ApiClient.getRetrofitInstance().create(BookingService.class);

        progressBar.setVisibility(View.VISIBLE);
        Log.d("HistoryOrder", "Calling searchTickets API for userId: " + userId);

        Call<List<BookingService.BookingDetailAllDTO>> call = apiService.searchTickets(userId); 
        call.enqueue(new Callback<List<BookingService.BookingDetailAllDTO>>() {
            @Override
            public void onResponse(Call<List<BookingService.BookingDetailAllDTO>> call, Response<List<BookingService.BookingDetailAllDTO>> response) {
                progressBar.setVisibility(View.GONE); 
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("HistoryOrder", "API search call successful. Raw response body size: " + response.body().size());
                    orderList.clear();
                    Date currentTime = new Date();
                    Log.d("HistoryOrder", "Current time for filtering: " + dateTimeFormat.format(currentTime));

                    for (BookingService.BookingDetailAllDTO order : response.body()) { 
                        Log.d("HistoryOrder", "Processing Order ID: " + order.getOrderId() + ", Status: " + order.getStatus() + ", Showtime: " + order.getShowtime());
                        if ("Pending".equals(order.getStatus())) {
                            String showtimeString = order.getShowtime(); 
                            
                            if (showtimeString != null) {
                                try {
                                    Date showtimeDate = dateTimeFormat.parse(showtimeString);
                                    Log.d("HistoryOrder", "Parsed Showtime Date for Order ID " + order.getOrderId() + ": " + dateTimeFormat.format(showtimeDate));
                                    if (showtimeDate != null && !showtimeDate.before(currentTime)) {
                                        orderList.add(order);
                                        Log.d("HistoryOrder", "Added Pending Order ID: " + order.getOrderId() + " (Showtime in future or present)");
                                    } else {
                                        Log.d("HistoryOrder", "Filtered out Pending Order ID: " + order.getOrderId() + " (Showtime in past)");
                                    }
                                } catch (ParseException e) {
                                    Log.e("HistoryOrder", "Error parsing showtime date for Order ID " + order.getOrderId() + ": " + e.getMessage());
                                    orderList.add(order);
                                    Log.d("HistoryOrder", "Added Order ID: " + order.getOrderId() + " (ParseException, added by default)");
                                }
                            } else {
                                Log.d("HistoryOrder", "Showtime string is null for Order ID: " + order.getOrderId() + ". Adding to list anyway.");
                                orderList.add(order); 
                            }
                        } else { 
                            orderList.add(order);
                            Log.d("HistoryOrder", "Added Order ID: " + order.getOrderId() + " (Status is not Pending)");
                        }
                    }
                    Log.d("HistoryOrder", "Final orderList size after filtering: " + orderList.size());
                    if (orderList.isEmpty()) {
                        Toast.makeText(HistoryOrder.this, "Không có lịch sử đặt vé.", Toast.LENGTH_SHORT).show();
                    }
                    orderAdapter.notifyDataSetChanged();
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) { /* ignore */ }
                    Log.e("HistoryOrder", "API search Response unsuccessful or body is null. Code: " + response.code() + " Body: " + errorBody);
                    Toast.makeText(HistoryOrder.this, "Không có lịch sử đặt vé.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BookingService.BookingDetailAllDTO>> call, Throwable t) {
                progressBar.setVisibility(View.GONE); 
                Log.e("HistoryOrder", "API search call failed: " + t.getMessage(), t);
                Toast.makeText(HistoryOrder.this, "Lỗi khi tải dữ liệu. Vui lòng kiểm tra kết nối.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int orderId) {
        Log.d("HistoryOrder", "Item clicked with Order ID: " + orderId);
        fetchOrderDetail(orderId);
    }

    private void fetchOrderDetail(int orderId) { 
        BookingService apiService = ApiClient.getRetrofitInstance().create(BookingService.class);
        progressBar.setVisibility(View.VISIBLE);

        Call<BookingService.BookingDetailItemDTO> call = apiService.getBookingDetailById(orderId);
        call.enqueue(new Callback<BookingService.BookingDetailItemDTO>() {
            @Override
            public void onResponse(Call<BookingService.BookingDetailItemDTO> call, Response<BookingService.BookingDetailItemDTO> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BookingService.BookingDetailItemDTO detail = response.body();
                    Log.d("HistoryOrder", "Detail API call successful for Order ID: " + orderId);
                    showOrderDetailPopup(detail);
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) { /* ignore */ }
                    Log.e("HistoryOrder", "Detail API Response unsuccessful or body is null. Code: " + response.code() + " Body: " + errorBody);
                    Toast.makeText(HistoryOrder.this, "Không thể tải chi tiết đơn hàng.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BookingService.BookingDetailItemDTO> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("HistoryOrder", "Detail API call failed for Order ID: " + orderId + ": " + t.getMessage(), t);
                Toast.makeText(HistoryOrder.this, "Lỗi khi tải chi tiết. Vui lòng kiểm tra kết nối.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOrderDetailPopup(BookingService.BookingDetailItemDTO detail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_order_detail, null);
        builder.setView(dialogView);

        ImageView ivPopupMoviePoster = dialogView.findViewById(R.id.iv_popup_movie_poster);
        ImageView ivQrCode = dialogView.findViewById(R.id.iv_qr_code);
        TextView tvMovieTitle = dialogView.findViewById(R.id.tv_movie_title);
        TextView tvShowtime = dialogView.findViewById(R.id.tv_showtime); 
        TextView tvRoomName = dialogView.findViewById(R.id.tv_room_name); 
        TextView tvSeats = dialogView.findViewById(R.id.tv_seats); 
        TextView tvTotalAmount = dialogView.findViewById(R.id.tv_total_amount);
        TextView tvPaymentMethod = dialogView.findViewById(R.id.tv_payment_method); 
        TextView tvPaymentStatus = dialogView.findViewById(R.id.tv_payment_status); 
        TextView tvOrderId = dialogView.findViewById(R.id.tv_order_id); 

        // Load poster
        Glide.with(this)
            .load(detail.getPoster())
            .placeholder(R.drawable.ic_launcher_background)
            .into(ivPopupMoviePoster);

        tvMovieTitle.setText(detail.getMovieTitle());

        if (detail.getShowtime() != null && detail.getShowtime().showDate != null && detail.getShowtime().showTime != null) {
            tvShowtime.setText(detail.getShowtime().showDate + " lúc " + detail.getShowtime().showTime);
            tvRoomName.setText("Phòng: " + detail.getShowtime().roomName);
        } else {
             tvShowtime.setText("Suất chiếu: N/A");
             tvRoomName.setText("Phòng: N/A");
        }

        tvSeats.setText("Ghế: " + (detail.getSeats() != null ? String.join(", ", detail.getSeats()) : "N/A"));
        tvTotalAmount.setText(String.format("%,.0f VND", detail.getTotalAmount()));
        tvPaymentMethod.setText("Thanh toán qua " + detail.getPaymentMethod());
        tvOrderId.setText("Mã đơn hàng: #" + detail.getOrderId());

        // Set payment status color and handle QR code visibility
        if ("Completed".equals(detail.getStatus())) {
            tvPaymentStatus.setText("Đã thanh toán");
            tvPaymentStatus.setTextColor(Color.parseColor("#4CAF50"));
            // If QR code is available, load it
            if (detail.getQrCode() != null && !detail.getQrCode().isEmpty()) {
                Glide.with(this).load(detail.getQrCode()).into(ivQrCode);
                ivQrCode.setVisibility(View.VISIBLE);
            }
        } else if ("Pending".equals(detail.getStatus())) {
            tvPaymentStatus.setText("Chưa thanh toán");
            tvPaymentStatus.setTextColor(Color.parseColor("#F44336"));
            ivQrCode.setVisibility(View.GONE); // Hide QR code for pending orders
        } else {
            if (detail.getPayment() != null) {
                tvPaymentStatus.setText(detail.getPayment().paymentStatus);
            } else {
                tvPaymentStatus.setText("N/A");
            }
            tvPaymentStatus.setTextColor(Color.WHITE);
            ivQrCode.setVisibility(View.GONE);
        }

        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
