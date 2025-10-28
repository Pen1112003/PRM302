package com.example.prm392_cinema;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.Adapters.OrderAdapter;
import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.BookingService;
import com.example.prm392_cinema.Stores.AuthStore;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HistoryOrder extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private List<BookingService.BookingDetailAllDTO> orderList;
    private ProgressBar progressBar; // Loading indicator
    String currentOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_order);

        recyclerViewOrders = findViewById(R.id.orderRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, this);
        recyclerViewOrders.setAdapter(orderAdapter);

        loadUserIdAndFetchData();

        findViewById(R.id.btnSignOut).setOnClickListener(v -> handleSignOut());

        findViewById(R.id.backIcon).setOnClickListener(v -> handleBack());
        
        // Hide the history button on the history screen
        findViewById(R.id.btnHistory).setVisibility(View.GONE);
    }

    private void loadUserIdAndFetchData() {
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        String userId = sharedPreferences.getString(LoginActivity.USER_ID, null); // Read as String

        if (userId != null && !userId.isEmpty()) {
            Log.d("HistoryOrder", "UserId found in SharedPreferences: " + userId);
            loadData(userId);
        } else {
            Log.e("HistoryOrder", "User ID not found in SharedPreferences. Cannot load data.");
            Toast.makeText(this, "Bạn cần đăng nhập để xem lịch sử.", Toast.LENGTH_LONG).show();
        }
    }

    private void handleSignOut() {
        // Clear SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Also clear the static store
        AuthStore.userId = null;
        AuthStore.jwtToken = null;

        Intent intent = new Intent(HistoryOrder.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void handleBack() {
        finish(); // Đóng Activity hiện tại và quay lại Activity trước đó
    }

    private void loadData(String userId) { // Accept String userId
        BookingService apiService = ApiClient.getRetrofitInstance().create(BookingService.class);

        // Show the loading indicator
        progressBar.setVisibility(View.VISIBLE);

        Call<BookingService.ResAllDTO> call = apiService.searchTickets(userId, 1);
        call.enqueue(new Callback<BookingService.ResAllDTO>() {
            @Override
            public void onResponse(Call<BookingService.ResAllDTO> call, Response<BookingService.ResAllDTO> response) {
                progressBar.setVisibility(View.GONE); // Hide loading indicator
                if (response.isSuccessful() && response.body() != null && response.body().result != null) {
                    Log.d("HistoryOrder", "API call successful. Found " + response.body().result.size() + " orders.");
                    orderList.clear();
                    orderList.addAll(response.body().result);
                    orderAdapter.notifyDataSetChanged();
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) { /* ignore */ }
                    Log.e("HistoryOrder", "API Response unsuccessful or body is null. Code: " + response.code() + " Body: " + errorBody);
                    Toast.makeText(HistoryOrder.this, "Không có lịch sử đặt vé.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BookingService.ResAllDTO> call, Throwable t) {
                progressBar.setVisibility(View.GONE); // Hide loading indicator
                Log.e("HistoryOrder", "API call failed: " + t.getMessage(), t);
                Toast.makeText(HistoryOrder.this, "Lỗi khi tải dữ liệu. Vui lòng kiểm tra kết nối.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
