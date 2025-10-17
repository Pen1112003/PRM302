package com.example.prm392_cinema;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.Adapters.FabAdapter;
import com.example.prm392_cinema.Adapters.SeatAdapter;
import com.example.prm392_cinema.Models.Fab;
import com.example.prm392_cinema.Models.Seat;
import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.BookingService;
import com.example.prm392_cinema.Services.FabService;
import com.example.prm392_cinema.Services.SeatService;
import com.example.prm392_cinema.Stores.AuthStore;
import com.example.prm392_cinema.Stores.HallScreenStore;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HallActivity extends AppCompatActivity {
    private SeatAdapter seatAdapter;
    private FabAdapter fabAdapter;
    private RecyclerView seatRecyclerView, fabRecyclerView;
    private int movieId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hall);

        ((TextView) findViewById(R.id.hallTitle)).setText("CHỌN GHẾ - " + HallScreenStore.hallName);
        ((TextView) findViewById(R.id.showTime)).setText(HallScreenStore.showTime);

        GradientDrawable backgroundNormalExplain = (GradientDrawable) findViewById(R.id.normalExplain).getBackground();
        backgroundNormalExplain.setColor(Color.rgb(255, 255, 255));
        backgroundNormalExplain.setStroke(6, Color.GREEN);

        GradientDrawable backgroundVipExplain = (GradientDrawable) findViewById(R.id.vipExplain).getBackground();
        backgroundVipExplain.setColor(Color.rgb(255, 255, 255));
        backgroundVipExplain.setStroke(6, Color.RED);

        GradientDrawable backgroundSelectedExplain = (GradientDrawable) findViewById(R.id.selectedExplain).getBackground();
        backgroundSelectedExplain.setColor(Color.rgb(243, 234, 40));
        backgroundSelectedExplain.setStroke(6, Color.rgb(243, 234, 40));

        GradientDrawable backgroundBookedExplain = (GradientDrawable) findViewById(R.id.bookedExplain).getBackground();
        backgroundBookedExplain.setColor(Color.rgb(187, 187, 187));
        backgroundBookedExplain.setStroke(6, Color.rgb(187, 187, 187));

        seatRecyclerView = findViewById(R.id.seatRecyclerView);

        getSeats(this);


        fabRecyclerView = findViewById(R.id.fabRecyclerView);
        fabRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getFabs();

        findViewById(R.id.orderButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBooking();
            }
        });
        findViewById(R.id.btnSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignOut();
            }
        });

        findViewById(R.id.backIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBack();
            }
        });
        (findViewById(R.id.btnHistory)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateHistory();
            }
        });
    }

    private void navigateHistory()
    {
        Intent intent = new Intent(HallActivity.this, HistoryOrder.class);
        startActivity(intent);
    }

    private void handleSignOut() {
        AuthStore.userId = 0;
        Intent intent = new Intent(HallActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void handleBack() {
        Intent intent = new Intent(HallActivity.this, MovieDetailActivity.class);
        intent.putExtra("movieId", HallScreenStore.movieId);
        setResult(RESULT_OK, intent);
        finish(); // Đóng Activity hiện tại và quay lại Activity trước đó
    }

    private void navigateToPaymentScreen(int bookingId) {
        Intent intent = new Intent(HallActivity.this, OrderPaymentActivity.class);
        intent.putExtra("orderId", bookingId + "");
        startActivity(intent);
    }

    private void createBooking() {
        Context context = this;
        List<Integer> selectedSeats = seatAdapter.getSelectedSeatId();

        if (selectedSeats.isEmpty()) {
            Toast.makeText(HallActivity.this, "Cần chọn ít nhất 1 ghế.", Toast.LENGTH_SHORT).show();
            return;
        }
        HallScreenStore.listSeatId = selectedSeats;

        BookingService bookingService = ApiClient.getRetrofitInstance().create(BookingService.class);
        Call<BookingService.CreateBookingResponseDto> call = bookingService.createBooking(new BookingService.CreateBookingDto(1, HallScreenStore.showTimeId, selectedSeats, seatAdapter.getTotalPrice()));
        call.enqueue(new Callback<BookingService.CreateBookingResponseDto>() {
            @Override
            public void onResponse(Call<BookingService.CreateBookingResponseDto> call, Response<BookingService.CreateBookingResponseDto> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                int bookingId = response.body().result.data.bookingId;

                FabService fabService = ApiClient.getRetrofitInstance().create(FabService.class);
                Call<FabService.OrderFabsResponse> call2 = fabService.orderFabs(bookingId, new FabService.OrderFabDto(fabAdapter.getOrderFabDto()));
                Log.d("Fail booking", "Fail booking");
                call2.enqueue(new Callback<FabService.OrderFabsResponse>() {
                    @Override
                    public void onResponse(Call<FabService.OrderFabsResponse> call, Response<FabService.OrderFabsResponse> response) {
                        navigateToPaymentScreen(bookingId);
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Log.d("Fail booking", "Fail booking");
                    }
                });
            }

            @Override
            public void onFailure(Call<BookingService.CreateBookingResponseDto> call, Throwable t) {
                Log.d("Fail booking", "Fail booking");
            }
        });
    }

    private void getSeats(Context context) {
        List<Seat> seats = new ArrayList<>();

        SeatService apiService = ApiClient.getRetrofitInstance().create(SeatService.class);

        // Call API with a query parameter
        Call<List<SeatService.SeatResponseDto>> call = apiService.getSeats(HallScreenStore.showTimeId);  // Pass `id` as 1
        call.enqueue(new Callback<List<SeatService.SeatResponseDto>>() {
            @Override
            public void onResponse(Call<List<SeatService.SeatResponseDto>> call, Response<List<SeatService.SeatResponseDto>> response) {
                Log.d("callAPI", "Done");
                Log.d("callAPI", "Done");
                Log.d("callAPI", "Done");
                Log.d("callAPI", "Done");
                int colSpan = 0;

                if (response.isSuccessful()) {
                    List<SeatService.SeatResponseDto> rows = response.body();

                    for (SeatService.SeatResponseDto row : rows) {
                        if (colSpan == 0) {
                            colSpan = row.rowSeats.size();
                        }
                        for (SeatService.SeatResponseDto.RowSeat seat : row.rowSeats) {
                            seats.add(new Seat(seat.seatId, seat.seatType, seat.price, seat.isSeat, seat.name, seat.isSold, seat.colIndex, seat.seatIndex));
                        }
                    }
                }

                // Setup GridLayoutManager for horizontal and vertical scrolling
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, colSpan); // 16 columns
                seatRecyclerView.setLayoutManager(gridLayoutManager);

                // Adapter setup
                seatAdapter = new SeatAdapter(seats);
                seatRecyclerView.setAdapter(seatAdapter);
            }

            @Override
            public void onFailure(Call<List<SeatService.SeatResponseDto>> call, Throwable t) {
                // Handle the error
                Log.d("callAPI", t.getMessage());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        HallScreenStore.orderFabDto = null;
        HallScreenStore.listSeatId = new ArrayList<>();
    }

    private void getFabs() {
        List<Fab> fabs = new ArrayList<>();

        FabService apiService = ApiClient.getRetrofitInstance().create(FabService.class);

        // Call API with a query parameter
        Call<FabService.GetFabsResponseDto> call = apiService.getFabs();  // Pass `id` as 1
        call.enqueue(new Callback<FabService.GetFabsResponseDto>() {
            @Override
            public void onResponse(Call<FabService.GetFabsResponseDto> call, Response<FabService.GetFabsResponseDto> response) {
                Log.d("callAPI", "Done");
                Log.d("callAPI", "Done");
                Log.d("callAPI", "Done");
                Log.d("callAPI", "Done");
                if (response.isSuccessful()) {
                    FabService.GetFabsResponseDto res = response.body();

                    for (FabService.FabDto fab : res.result.fABList) {
                        fabs.add(new Fab(fab.foodId, fab.name, fab.description, fab.price));
                    }
                }
                Log.d("callAPI", "Done");
                // Adapter setup
                fabAdapter = new FabAdapter(fabs);
                fabRecyclerView.setAdapter(fabAdapter);
            }

            @Override
            public void onFailure(Call<FabService.GetFabsResponseDto> call, Throwable t) {
                // Handle the error
                Log.d("callAPI", t.getMessage());
            }
        });
    }
}