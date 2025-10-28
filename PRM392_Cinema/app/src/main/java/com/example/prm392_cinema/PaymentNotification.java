package com.example.prm392_cinema;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_cinema.Models.UpdateStatusRequest;
import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.PaymentService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentNotification extends AppCompatActivity {

    private static final String TAG = "PaymentNotification";
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_notification);

        btnBack = findViewById(R.id.back_to_home_button);

        String transactionId = getIntent().getStringExtra("transactionId");

        if (transactionId != null && !transactionId.isEmpty()) {
            updateOrderStatus(transactionId);
        } else {
            Log.e(TAG, "Transaction ID is missing, cannot update order status.");
            Toast.makeText(this, "Lỗi: Không tìm thấy mã giao dịch.", Toast.LENGTH_SHORT).show();
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentNotification.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateOrderStatus(String transactionId) {
        PaymentService paymentService = ApiClient.getRetrofitInstance().create(PaymentService.class);
        UpdateStatusRequest request = new UpdateStatusRequest(transactionId);

        paymentService.updateOrderStatus(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Successfully updated order status for transaction: " + transactionId);
                } else {
                    Log.e(TAG, "Failed to update order status. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error calling update order status API", t);
            }
        });
    }
}
