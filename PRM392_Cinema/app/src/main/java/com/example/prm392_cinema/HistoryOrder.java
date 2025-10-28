package com.example.prm392_cinema;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_cinema.Adapters.OrderAdapter;
import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.BookingService;
import com.example.prm392_cinema.Stores.AuthStore;
import com.example.prm392_cinema.receivers.NotificationReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.US);
    private String currentTransactionId; // To hold the transaction ID being paid

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (!isGranted) {
            Toast.makeText(this, "Bạn đã từ chối quyền gửi thông báo.", Toast.LENGTH_SHORT).show();
        }
    });

    private final ActivityResultLauncher<Intent> paymentLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            Log.d("HistoryOrder", "Payment result received. Result code: " + result.getResultCode());
            if (result.getResultCode() == RESULT_CANCELED) {
                Toast.makeText(this, "Thanh toán đã bị hủy.", Toast.LENGTH_LONG).show();
                loadUserIdAndFetchData(); // Refresh list on cancellation
            } else if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri data = result.getData().getData();
                if (data != null) {
                    String responseCode = data.getQueryParameter("vnp_ResponseCode");
                    if ("00".equals(responseCode)) {
                        Log.d("HistoryOrder", "Payment successful. Updating status for transaction: " + currentTransactionId);
                        if (currentTransactionId != null && !currentTransactionId.isEmpty()) {
                            updateOrderStatusOnServer(currentTransactionId);
                        } else {
                            Toast.makeText(this, "Lỗi: Không tìm thấy mã giao dịch để cập nhật.", Toast.LENGTH_LONG).show();
                            loadUserIdAndFetchData(); // Still refresh
                        }
                    } else {
                        Toast.makeText(this, "Giao dịch thất bại trên cổng thanh toán.", Toast.LENGTH_LONG).show();
                        loadUserIdAndFetchData(); // Refresh on failure
                    }
                }
            }
        }
    );

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

        askNotificationPermission();
        checkExactAlarmPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserIdAndFetchData();
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                new AlertDialog.Builder(this)
                    .setTitle("Yêu cầu quyền đặc biệt")
                    .setMessage("Ứng dụng cần quyền đặt báo thức chính xác để gửi thông báo nhắc nhở.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        startActivity(intent);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            }
        }
    }

    private void loadUserIdAndFetchData() {
        String userId = AuthStore.userId;
        if (userId != null && !userId.isEmpty()) {
            loadData(userId);
        } else {
            Toast.makeText(this, "Bạn cần đăng nhập để xem lịch sử.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(HistoryOrder.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void handleSignOut() {
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
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

        Call<List<BookingService.BookingDetailAllDTO>> call = apiService.searchTickets(userId);
        call.enqueue(new Callback<List<BookingService.BookingDetailAllDTO>>() {
            @Override
            public void onResponse(Call<List<BookingService.BookingDetailAllDTO>> call, Response<List<BookingService.BookingDetailAllDTO>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    Date currentTime = new Date();

                    for (BookingService.BookingDetailAllDTO order : response.body()) {
                        if ("Pending".equals(order.getStatus())) {
                            String showtimeString = order.getShowtime();
                            if (showtimeString != null && !showtimeString.isEmpty()) {
                                try {
                                    Date showtimeDate = dateTimeFormat.parse(showtimeString);
                                    if (!showtimeDate.before(currentTime)) {
                                        orderList.add(order);
                                    }
                                } catch (ParseException e) {
                                    Log.e("HistoryOrder", "Error parsing showtime for pending order: " + order.getOrderId(), e);
                                }
                            }
                        } else {
                            orderList.add(order);
                            if ("Completed".equals(order.getStatus())) {
                                scheduleNotification(order);
                            }
                        }
                    }
                    
                    if (orderList.isEmpty()) {
                        Toast.makeText(HistoryOrder.this, "Không có lịch sử đặt vé.", Toast.LENGTH_SHORT).show();
                    }
                    orderAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(HistoryOrder.this, "Không thể tải lịch sử đặt vé.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BookingService.BookingDetailAllDTO>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HistoryOrder.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scheduleNotification(BookingService.BookingDetailAllDTO order) {
        // ... (schedule logic remains the same)
    }

    @Override
    public void onItemClick(int orderId) {
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
                    showOrderDetailPopup(response.body());
                } else {
                    Toast.makeText(HistoryOrder.this, "Không thể tải chi tiết đơn hàng.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BookingService.BookingDetailItemDTO> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HistoryOrder.this, "Lỗi khi tải chi tiết: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOrderDetailPopup(BookingService.BookingDetailItemDTO detail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_order_detail, null);
        final AlertDialog dialog = builder.setView(dialogView).create();

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
        Button btnPayNow = dialogView.findViewById(R.id.btn_pay_now);

        Glide.with(this).load(detail.getPoster()).into(ivPopupMoviePoster);
        tvMovieTitle.setText(detail.getMovieTitle());

        if (detail.getShowtime() != null) {
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

        if ("Completed".equals(detail.getStatus())) {
            tvPaymentStatus.setText("Đã thanh toán");
            tvPaymentStatus.setTextColor(Color.parseColor("#4CAF50"));
            ivQrCode.setVisibility(detail.getQrCode() != null && !detail.getQrCode().isEmpty() ? View.VISIBLE : View.GONE);
            if (detail.getQrCode() != null && !detail.getQrCode().isEmpty()) {
                Glide.with(this).load(detail.getQrCode()).into(ivQrCode);
            }
            btnPayNow.setVisibility(View.GONE);
        } else if ("Pending".equals(detail.getStatus())) {
            tvPaymentStatus.setText("Chưa thanh toán");
            tvPaymentStatus.setTextColor(Color.parseColor("#F44336"));
            ivQrCode.setVisibility(View.GONE);
            btnPayNow.setVisibility(View.VISIBLE);

            btnPayNow.setOnClickListener(v -> {
                Log.d("HistoryOrder", "Pay Now clicked for Order ID: " + detail.getOrderId());
                dialog.dismiss();

                if (detail.getOrder().payments != null && !detail.getOrder().payments.isEmpty()) {
                    String transactionId = detail.getOrder().payments.get(0).transactionId;
                    if (transactionId != null && !transactionId.isEmpty()) {
                        this.currentTransactionId = transactionId;
                        handlePaymentForTransaction(transactionId);
                    } else {
                        Toast.makeText(this, "Không tìm thấy mã giao dịch.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Không có thông tin giao dịch để thanh toán.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            tvPaymentStatus.setText(detail.getPayment() != null ? detail.getPayment().paymentStatus : "N/A");
            tvPaymentStatus.setTextColor(Color.WHITE);
            ivQrCode.setVisibility(View.GONE);
            btnPayNow.setVisibility(View.GONE);
        }

        dialog.show();
    }

    private void handlePaymentForTransaction(String transactionId) {
        progressBar.setVisibility(View.VISIBLE);
        BookingService bookingService = ApiClient.getRetrofitInstance().create(BookingService.class);
        bookingService.getPaymentUrl(transactionId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    String paymentUrl = response.body();
                    Intent intent = new Intent(HistoryOrder.this, PaymentWebViewActivity.class);
                    intent.putExtra("payment_url", paymentUrl);
                    paymentLauncher.launch(intent);
                } else {
                    Toast.makeText(HistoryOrder.this, "Không thể lấy URL thanh toán.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HistoryOrder.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateOrderStatusOnServer(String transactionId) {
        BookingService apiService = ApiClient.getRetrofitInstance().create(BookingService.class);
        BookingService.UpdateOrderStatusDto dto = new BookingService.UpdateOrderStatusDto(transactionId);

        apiService.updateOrderStatus(dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(HistoryOrder.this, "Thanh toán thành công! Trạng thái đã được cập nhật.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HistoryOrder.this, "Lỗi khi cập nhật trạng thái đơn hàng.", Toast.LENGTH_SHORT).show();
                }
                // Always refresh data after attempting to update
                loadUserIdAndFetchData();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(HistoryOrder.this, "Lỗi kết nối khi cập nhật trạng thái: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Always refresh data after attempting to update
                loadUserIdAndFetchData();
            }
        });
    }
}
