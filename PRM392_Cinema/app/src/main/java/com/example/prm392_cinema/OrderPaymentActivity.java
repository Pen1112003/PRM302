package com.example.prm392_cinema;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

    private final ActivityResultLauncher<Intent> paymentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d("OrderPaymentActivity", "Received result from payment activity. Result code: " + result.getResultCode());

                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri resultUri = data.getData();
                        String responseCode = resultUri.getQueryParameter("vnp_ResponseCode");
                        Log.d("OrderPaymentActivity", "Payment response code: " + responseCode);

                        if ("00".equals(responseCode)) {
                            // Payment successful, navigate to PaymentNotification
                            Intent notificationIntent = new Intent(OrderPaymentActivity.this, PaymentNotification.class);
                            notificationIntent.putExtra("transactionId", transactionId);
                            startActivity(notificationIntent);
                            finish(); // Finish this activity
                        } else {
                            // Payment failed on the portal
                            Toast.makeText(this, "Thanh toán thất bại.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Should not happen if RESULT_OK is received
                        Toast.makeText(this, "Không nhận được kết quả thanh toán.", Toast.LENGTH_LONG).show();
                    }
                } else if (result.getResultCode() == RESULT_CANCELED) {
                    // Payment was cancelled by the user (back press) or failed on the portal
                    Toast.makeText(this, "Thanh toán đã bị hủy hoặc thất bại.", Toast.LENGTH_LONG).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment);

        transactionId = getIntent().getStringExtra("orderId");

        initializeViews();
        fetchTransactionDetails();

        confirmPaymentButton.setOnClickListener(v -> handlePayment());
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
                    paymentLauncher.launch(intent);
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
