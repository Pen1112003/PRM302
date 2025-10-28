package com.example.prm392_cinema;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.Adapters.ProductAdapter;
import com.example.prm392_cinema.Adapters.SeatAdapter;
import com.example.prm392_cinema.Models.Product;
import com.example.prm392_cinema.Models.Seat;
import com.example.prm392_cinema.Models.SeatType;
import com.example.prm392_cinema.model.ShowtimeDetail;
import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.BookingService;
import com.example.prm392_cinema.Services.MovieService;
import com.example.prm392_cinema.Services.ProductService;
import com.example.prm392_cinema.Services.SeatService;
import com.example.prm392_cinema.Stores.AuthStore;
import com.example.prm392_cinema.R;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HallActivity extends AppCompatActivity {
    private int showtimeId;
    private int movieId;
    private String userId;
    private SeatAdapter seatAdapter;
    private RecyclerView seatRecyclerView;
    private LinearLayout seatTypeContainer;
    private LinearLayout orderSummaryLayout;
    private TextView selectedSeatsTextView;
    private TextView selectedFoodTextView;
    private TextView totalPriceTextView;

    private List<Product> selectedProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageView userIcon = findViewById(R.id.user_icon);
        userIcon.setOnClickListener(v -> showUserDialog());

        userId = AuthStore.userId;

        showtimeId = getIntent().getIntExtra("showtimeId", -1);
        if (showtimeId == -1) {
            Toast.makeText(this, "Showtime ID is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        fetchShowtimeDetails(showtimeId);
        getSeatTypes();
    }

    private void initializeViews() {
        seatRecyclerView = findViewById(R.id.seatRecyclerView);
        seatTypeContainer = findViewById(R.id.seatTypeContainer);
        orderSummaryLayout = findViewById(R.id.orderSummaryLayout);
        selectedSeatsTextView = findViewById(R.id.selectedSeatsTextView);
        selectedFoodTextView = findViewById(R.id.selectedFoodTextView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        findViewById(R.id.orderButton).setOnClickListener(v -> createBooking());
        findViewById(R.id.foodButton).setOnClickListener(v -> showProductDialog());
    }

    private void showUserDialog() {
        if (userId == null || userId.isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Tài khoản")
                    .setItems(new CharSequence[]{"Lịch sử đặt vé", "Đăng xuất"}, (dialog, which) -> {
                        switch (which) {
                            case 0:
                                navigateHistory();
                                break;
                            case 1:
                                handleSignOut();
                                break;
                        }
                    })
                    .show();
        }
    }

    private void updateOrderSummary() {
        List<String> selectedSeatNames = seatAdapter != null ? seatAdapter.getSelectedSeatNames() : new ArrayList<>();
        double ticketPrice = seatAdapter != null ? seatAdapter.getTotalPrice() : 0;
        double productPrice = selectedProducts.stream().mapToDouble(p -> p.getPrice() * p.getSelectedQuantity()).sum();
        double totalPrice = ticketPrice + productPrice;

        if (!selectedSeatNames.isEmpty() || !selectedProducts.isEmpty()) {
            orderSummaryLayout.setVisibility(View.VISIBLE);

            if (selectedSeatNames.isEmpty()) {
                selectedSeatsTextView.setVisibility(View.GONE);
            } else {
                selectedSeatsTextView.setVisibility(View.VISIBLE);
                selectedSeatsTextView.setText("Ghế: " + String.join(", ", selectedSeatNames));
            }

            if (selectedProducts.isEmpty()) {
                selectedFoodTextView.setVisibility(View.GONE);
            } else {
                selectedFoodTextView.setVisibility(View.VISIBLE);
                StringBuilder foodSummary = new StringBuilder("Bắp nước:\n");
                for (Product p : selectedProducts) {
                    foodSummary.append("- ").append(p.getSelectedQuantity()).append("x ").append(p.getProductName()).append("\n");
                }
                selectedFoodTextView.setText(foodSummary.toString().trim());
            }

            totalPriceTextView.setText(String.format(Locale.US, "Tổng: %,.0f VNĐ", totalPrice));

        } else {
            orderSummaryLayout.setVisibility(View.GONE);
        }
    }

    private void showProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_products, null);
        builder.setView(dialogView);

        RecyclerView productsRecyclerView = dialogView.findViewById(R.id.productsRecyclerView);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        AlertDialog dialog = builder.create();

        ProductService productService = ApiClient.getRetrofitInstance().create(ProductService.class);
        productService.getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductAdapter productAdapter = new ProductAdapter(HallActivity.this, response.body());
                    productsRecyclerView.setAdapter(productAdapter);

                    dialogView.findViewById(R.id.btnDone).setOnClickListener(v -> {
                        selectedProducts = productAdapter.getProductList().stream()
                                .filter(p -> p.getSelectedQuantity() > 0)
                                .collect(Collectors.toList());
                        updateOrderSummary();
                        dialog.dismiss();
                    });

                    dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

                    dialog.show();
                } else {
                    Toast.makeText(HallActivity.this, "Không thể tải danh sách sản phẩm.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(HallActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchShowtimeDetails(int showtimeId) {
        MovieService movieService = ApiClient.getRetrofitInstance().create(MovieService.class);
        Call<ShowtimeDetail> call = movieService.getShowtimeDetail(showtimeId);

        call.enqueue(new Callback<ShowtimeDetail>() {
            @Override
            public void onResponse(Call<ShowtimeDetail> call, Response<ShowtimeDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ShowtimeDetail showtimeDetail = response.body();
                    if (showtimeDetail.getRoom() == null) {
                        Toast.makeText(HallActivity.this, "Lỗi: Dữ liệu phòng chiếu không đầy đủ.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    movieId = showtimeDetail.getMovieId();
                    ((TextView) findViewById(R.id.hallTitle)).setText("CHỌN GHẾ - " + showtimeDetail.getRoom().getRoomName());
                    ((TextView) findViewById(R.id.showTime)).setText("Ngày chiếu: " + showtimeDetail.getShowDate() + ", " + showtimeDetail.getShowTime());
                    fetchSeatConfiguration(showtimeDetail.getRoomId());
                } else {
                    Toast.makeText(HallActivity.this, "Không thể tải thông tin suất chiếu.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShowtimeDetail> call, Throwable t) {
                Toast.makeText(HallActivity.this, "Lỗi mạng khi tải thông tin suất chiếu.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSeatConfiguration(int roomId) {
        SeatService seatService = ApiClient.getRetrofitInstance().create(SeatService.class);
        Call<List<SeatService.SeatResponseDto>> call = seatService.getSeatConfiguration(roomId);

        call.enqueue(new Callback<List<SeatService.SeatResponseDto>>() {
            @Override
            public void onResponse(Call<List<SeatService.SeatResponseDto>> call, Response<List<SeatService.SeatResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Seat> seats = new ArrayList<>();
                    final int colSpan = 4;

                    for (int i = 0; i < response.body().size(); i++) {
                        SeatService.SeatResponseDto seatDto = response.body().get(i);
                        int inferredRow = i / colSpan;
                        int inferredCol = i % colSpan;
                        String seatName = (char)('A' + inferredRow) + String.valueOf(inferredCol + 1);
                        seats.add(new Seat(seatDto.seatId, seatDto.seatTypeName, (int) seatDto.seatPrice, true, seatName, seatDto.isSelected, inferredCol, inferredRow, seatDto.seatTypeId));
                    }

                    seatRecyclerView.setLayoutManager(new GridLayoutManager(HallActivity.this, colSpan));
                    seatAdapter = new SeatAdapter(seats);

                    seatAdapter.setOnSeatSelectionChangedListener(() -> updateOrderSummary());

                    seatRecyclerView.setAdapter(seatAdapter);
                    findViewById(R.id.orderButton).setEnabled(true);
                } else {
                    Toast.makeText(HallActivity.this, "Không thể tải sơ đồ ghế.", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.orderButton).setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<List<SeatService.SeatResponseDto>> call, Throwable t) {
                Toast.makeText(HallActivity.this, "Lỗi mạng khi tải sơ đồ ghế.", Toast.LENGTH_SHORT).show();
                findViewById(R.id.orderButton).setEnabled(false);
            }
        });
    }

    private void getSeatTypes() {
        SeatService seatService = ApiClient.getRetrofitInstance().create(SeatService.class);
        seatService.getSeatTypes().enqueue(new Callback<List<SeatType>>() {
            @Override
            public void onResponse(Call<List<SeatType>> call, Response<List<SeatType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateSeatTypeLegends(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<SeatType>> call, Throwable t) { }
        });
    }

    private void populateSeatTypeLegends(List<SeatType> seatTypes) {
        seatTypeContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        Map<String, SeatType> distinctSeatTypes = new LinkedHashMap<>();
        for (SeatType seatType : seatTypes) {
            distinctSeatTypes.putIfAbsent(seatType.getName(), seatType);
        }

        for (SeatType seatType : distinctSeatTypes.values()) {
            View legendView = inflater.inflate(R.layout.item_seat_legend, seatTypeContainer, false);
            
            View seatColorView = legendView.findViewById(R.id.seatColorView);
            TextView seatTypeName = legendView.findViewById(R.id.seatTypeName);
            TextView seatTypePrice = legendView.findViewById(R.id.seatTypePrice);

            Drawable backgroundDrawable = seatColorView.getBackground();
            if (backgroundDrawable instanceof GradientDrawable) {
                GradientDrawable background = (GradientDrawable) backgroundDrawable;
                if ("VIP".equalsIgnoreCase(seatType.getName())) {
                    background.setStroke(6, Color.RED);
                } else {
                    background.setStroke(6, Color.GREEN);
                }
            }

            seatTypeName.setText(seatType.getName());
            seatTypePrice.setText(String.format(Locale.US, "%,.0f VNĐ", seatType.getPrice()));

            seatTypeContainer.addView(legendView);
        }
    }

    private void createBooking() {
        if (userId == null){
            Toast.makeText(this, "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (seatAdapter == null) {
            Toast.makeText(this, "Sơ đồ ghế chưa được tải.", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Integer> selectedSeatIds = seatAdapter.getSelectedSeatId();
        if (selectedSeatIds.isEmpty() && selectedProducts.isEmpty()) {
            Toast.makeText(this, "Cần chọn ít nhất 1 ghế hoặc 1 sản phẩm.", Toast.LENGTH_SHORT).show();
            return;
        }

        double ticketPrice = (seatAdapter != null) ? seatAdapter.getTotalPrice() : 0;
        double productPrice = selectedProducts.stream().mapToDouble(p -> p.getPrice() * p.getSelectedQuantity()).sum();
        double totalPrice = ticketPrice + productPrice;

        List<Integer> productIds = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        for (Product product : selectedProducts) {
            productIds.add(product.getProductId());
            quantities.add(product.getSelectedQuantity());
        }

        BookingService.CreateBookingDto bookingDto = new BookingService.CreateBookingDto(
                showtimeId,
                userId,
                selectedSeatIds,
                productIds,
                quantities,
                totalPrice,
                ticketPrice,
                0, 
                "VNPay"
        );

        BookingService bookingService = ApiClient.getRetrofitInstance().create(BookingService.class);
        bookingService.createBooking(bookingDto).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String transactionId = response.body();
                    navigateToPaymentScreen(transactionId);
                } else {
                    String errorBodyString = "";
                    if (response.errorBody() != null) {
                        try {
                            errorBodyString = response.errorBody().string();
                        } catch (IOException e) {
                            errorBodyString = "Error reading error body.";
                        }
                    }
                    Log.e("CreateBooking", "Booking failed. Code: " + response.code() + " - Body: " + errorBodyString);
                    Toast.makeText(HallActivity.this, "Đặt vé thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("CreateBooking", "API call failed: " + t.getMessage(), t);
                Toast.makeText(HallActivity.this, "Lỗi mạng khi đặt vé.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToPaymentScreen(String transactionId) {
        Intent intent = new Intent(this, OrderPaymentActivity.class);
        intent.putExtra("orderId", transactionId);
        startActivity(intent);
    }

    private void handleSignOut() {
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        AuthStore.userId = null;
        AuthStore.jwtToken = null;

        Intent intent = new Intent(HallActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
     }
    private void handleBack() { finish(); }
    private void navigateHistory() {
        Intent intent = new Intent(this, HistoryOrder.class);
        startActivity(intent);
     }
}
