package com.example.prm392_cinema;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.prm392_cinema.Models.TicketInfo;
import com.example.prm392_cinema.Models.TransactionDetails;
import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.BookingService;
import com.example.prm392_cinema.Services.TransactionService;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderPaymentActivity extends AppCompatActivity {
    private String transactionId;

    private ImageView moviePoster;
    private TextView movieTitle, showtimeInfo, roomInfo, seatInfo, customerName, phoneNumber, paymentMethod, totalAmount;
    private Button confirmPaymentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment);

        transactionId = getIntent().getStringExtra("orderId");

        initializeViews();
        fetchTransactionDetails();

        confirmPaymentButton.setOnClickListener(v -> handlePayment());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Uri data = intent.getData();
        if (data != null && "demozpdk".equals(data.getScheme()) && "app".equals(data.getHost())) {
            Log.d("OrderPaymentActivity", "Returned from payment via deep link.");
            Intent notificationIntent = new Intent(this, PaymentNotification.class);
            notificationIntent.putExtra("transactionId", transactionId); 
            startActivity(notificationIntent);
            finish();
        }
    }

    private void initializeViews() {
        moviePoster = findViewById(R.id.movie_poster);
        movieTitle = findViewById(R.id.movie_title);
        showtimeInfo = findViewById(R.id.showtime_info);
        roomInfo = findViewById(R.id.room_info);
        seatInfo = findViewById(R.id.seat_info);
        customerName = findViewById(R.id.customer_name);
        phoneNumber = findViewById(R.id.phone_number);
        paymentMethod = findViewById(R.id.payment_method);
        totalAmount = findViewById(R.id.total_amount);
        confirmPaymentButton = findViewById(R.id.confirm_payment_button);
    }

    private void fetchTransactionDetails() {
        if (transactionId == null || transactionId.isEmpty()) {
            Toast.makeText(this, "Transaction ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        TransactionService transactionService = ApiClient.getRetrofitInstance().create(TransactionService.class);
        transactionService.getTransactionDetails(transactionId).enqueue(new Callback<TransactionDetails>() {
            @Override
            public void onResponse(Call<TransactionDetails> call, Response<TransactionDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateUi(response.body());
                } else {
                    Toast.makeText(OrderPaymentActivity.this, "Failed to load transaction details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransactionDetails> call, Throwable t) {
                Toast.makeText(OrderPaymentActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUi(TransactionDetails details) {
        if (!details.getTickets().isEmpty()) {
            TicketInfo firstTicket = details.getTickets().get(0);
            movieTitle.setText(firstTicket.getMovieTitle());
            showtimeInfo.setText(String.format("Suất chiếu: %s - %s", firstTicket.getShowtimeDate(), firstTicket.getShowtimeTime()));
            roomInfo.setText("Phòng chiếu: " + firstTicket.getRoomName());
            seatInfo.setText("Ghế: " + String.join(", ", firstTicket.getSeatNumbers()));
            Glide.with(this).load(firstTicket.getPoster()).into(moviePoster);
        }

        customerName.setText("Khách hàng: " + details.getCustomerName());
        phoneNumber.setText("SĐT: " + details.getPhoneNumber());
        paymentMethod.setText("Thanh toán: " + details.getPaymentMethod());
        totalAmount.setText(String.format(Locale.US, "Tổng cộng: %,.0f VNĐ", details.getTotalOrderAmount()));
    }

    private void handlePayment() {
        BookingService bookingService = ApiClient.getRetrofitInstance().create(BookingService.class);
        bookingService.getPaymentUrl(transactionId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String paymentUrl = response.body();
                    Intent intent = new Intent(OrderPaymentActivity.this, PaymentWebViewActivity.class);
                    intent.putExtra("payment_url", paymentUrl);
                    intent.putExtra("transactionId", transactionId);
                    startActivity(intent);
                } else {
                    Toast.makeText(OrderPaymentActivity.this, "Failed to get payment URL.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(OrderPaymentActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
