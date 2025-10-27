package com.example.prm392_cinema;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.Adapters.ProductAdapter;
import com.example.prm392_cinema.Adapters.SeatAdapter;
import com.example.prm392_cinema.Models.Product;
import com.example.prm392_cinema.Models.Seat;
import com.example.prm392_cinema.Models.SeatType;
import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.BookingService;
import com.example.prm392_cinema.Services.MovieService;
import com.example.prm392_cinema.Services.ProductService;
import com.example.prm392_cinema.Services.SeatService;
import com.example.prm392_cinema.model.ShowtimeDetail;

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
    private SeatAdapter seatAdapter;
    private RecyclerView seatRecyclerView;
    private LinearLayout seatTypeContainer, orderSummaryLayout;
    private TextView selectedFoodTextView, totalPriceTextView;
    private int showtimeId;
    private int movieId;
    private List<Product> selectedProducts = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall);

        seatTypeContainer = findViewById(R.id.seatTypeContainer);
        seatRecyclerView = findViewById(R.id.seatRecyclerView);
        orderSummaryLayout = findViewById(R.id.orderSummaryLayout);
        selectedFoodTextView = findViewById(R.id.selectedFoodTextView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra("showtimeId")) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID suất chiếu.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        showtimeId = intent.getIntExtra("showtimeId", 0);
        if (showtimeId == 0) {
            Toast.makeText(this, "Lỗi: ID suất chiếu không hợp lệ.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        fetchShowtimeDetails(showtimeId);
        getSeatTypes();

        findViewById(R.id.orderButton).setOnClickListener(v -> createBooking());
        findViewById(R.id.btnChooseFood).setOnClickListener(v -> showProductDialog());
        findViewById(R.id.btnSignOut).setOnClickListener(v -> handleSignOut());
        findViewById(R.id.backIcon).setOnClickListener(v -> handleBack());
        (findViewById(R.id.btnHistory)).setOnClickListener(v -> navigateHistory());
    }

    private void updateOrderSummary() {
        double seatPrice = (seatAdapter != null) ? seatAdapter.getTotalPrice() : 0;
        double productPrice = selectedProducts.stream()
                .mapToDouble(p -> p.getPrice() * p.getSelectedQuantity())
                .sum();
        double totalPrice = seatPrice + productPrice;

        if (totalPrice > 0) {
            orderSummaryLayout.setVisibility(View.VISIBLE);

            StringBuilder foodSummary = new StringBuilder();
            for (Product p : selectedProducts) {
                foodSummary.append("- ").append(p.getSelectedQuantity()).append("x ").append(p.getProductName()).append("\n");
            }
            selectedFoodTextView.setText(foodSummary.toString().trim());

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
        if (seatAdapter == null) {
            Toast.makeText(this, "Sơ đồ ghế chưa được tải.", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Integer> selectedSeats = seatAdapter.getSelectedSeatId();
        if (selectedSeats.isEmpty() && selectedProducts.isEmpty()) {
            Toast.makeText(this, "Cần chọn ít nhất 1 ghế hoặc 1 sản phẩm.", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalProductPrice = selectedProducts.stream().mapToDouble(p -> p.getPrice() * p.getSelectedQuantity()).sum();
        double totalSeatPrice = (seatAdapter != null) ? seatAdapter.getTotalPrice() : 0;

        BookingService.CreateBookingDto bookingDto = new BookingService.CreateBookingDto(1, showtimeId, selectedSeats, totalSeatPrice + totalProductPrice);

        List<BookingService.ProductOrderDto> productOrders = new ArrayList<>();
        for (Product product : selectedProducts) {
            productOrders.add(new BookingService.ProductOrderDto(product.getProductId(), product.getSelectedQuantity()));
        }
        bookingDto.setProducts(productOrders);

        BookingService bookingService = ApiClient.getRetrofitInstance().create(BookingService.class);
        bookingService.createBooking(bookingDto).enqueue(new Callback<BookingService.CreateBookingResponseDto>() {
            @Override
            public void onResponse(Call<BookingService.CreateBookingResponseDto> call, Response<BookingService.CreateBookingResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    navigateToPaymentScreen(response.body().result.data.bookingId);
                } else {
                    Toast.makeText(HallActivity.this, "Đặt vé thất bại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BookingService.CreateBookingResponseDto> call, Throwable t) {
                Toast.makeText(HallActivity.this, "Lỗi mạng khi đặt vé.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToPaymentScreen(int bookingId) {
        Intent intent = new Intent(this, OrderPaymentActivity.class);
        intent.putExtra("orderId", String.valueOf(bookingId));
        startActivity(intent);
    }

    private void handleSignOut() { 
        Intent intent = new Intent(HallActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
     }
    private void handleBack() { finish(); }
    private void navigateHistory() {
        Intent intent = new Intent(HallActivity.this, HistoryOrder.class);
        startActivity(intent);
     }
}
