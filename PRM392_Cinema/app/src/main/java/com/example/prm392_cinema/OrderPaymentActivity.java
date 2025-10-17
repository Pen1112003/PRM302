package com.example.prm392_cinema;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.Adapters.FabShowAdapter;
import com.example.prm392_cinema.Adapters.SeatShowAdapter;
import com.example.prm392_cinema.Models.Movie;
import com.example.prm392_cinema.Payment.Api.CreateOrder;
import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.BookingService;
import com.example.prm392_cinema.Services.MovieService;
import com.example.prm392_cinema.Stores.AuthStore;
import com.example.prm392_cinema.Stores.HallScreenStore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class OrderPaymentActivity extends AppCompatActivity {
    Button btnPay;
    TextView userName, hallName, movieName, showDate, bookingDate, status, totalPrice, nonFab;

    private RecyclerView recyclerFab, recyclerSeat;
    private FabShowAdapter fabDetailAdapter;
    private SeatShowAdapter seatShowAdapter;
    private List<BookingService.FabDetail> fabDetailList;
    private List<BookingService.SeatDetail> seatDetailList;
    MutableLiveData<BookingService.BookingDetailDTO> orderLiveData;
    String orderId;
    Number total;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment);
        userName = findViewById(R.id.txtUserName);
        hallName = findViewById(R.id.txtHallName);
        movieName = findViewById(R.id.txtMovieName);
        showDate = findViewById(R.id.txtShowDate);
        bookingDate = findViewById(R.id.txtBookingDate);
        status = findViewById(R.id.status);
        totalPrice = findViewById(R.id.totalPrice);
        recyclerFab = findViewById(R.id.fabRecyclerViewOrder);
        recyclerSeat = findViewById(R.id.seatRecyclerViewOrder);
        nonFab = findViewById(R.id.nonFAB);
        nonFab.setVisibility(View.GONE);

        recyclerFab.setLayoutManager(new LinearLayoutManager(this));
        recyclerSeat.setLayoutManager(new LinearLayoutManager(this));

        fabDetailList = new ArrayList<>();
        fabDetailAdapter = new FabShowAdapter(fabDetailList);
        recyclerFab.setAdapter(fabDetailAdapter);

        seatDetailList = new ArrayList<>();
        seatShowAdapter = new SeatShowAdapter(seatDetailList);
        recyclerSeat.setAdapter(seatShowAdapter);
        btnPay = findViewById(R.id.buttonThanhToan);

        if (getIntent() == null) return;
        orderId = getIntent().getStringExtra("orderId");
//        orderId="13";

        loadOrderDetails(orderId);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ZaloPaySDK.init(2553, Environment.SANDBOX);


        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateOrder orderApi = new CreateOrder();
                try {
                    JSONObject data = orderApi.createOrder(total + "");

//                    lblZpTransToken.setVisibility(View.VISIBLE);
                    String code = data.getString("return_code");
                    Toast.makeText(getApplicationContext(), "Tiếp tục với ZALO Pay", Toast.LENGTH_LONG).show();

                    if (code.equals("1")) {
                        String token = data.getString("zp_trans_token");
                        ZaloPaySDK.getInstance().payOrder(OrderPaymentActivity.this, token, "demozpdk://app", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        BookingService apiService = ApiClient.getRetrofitInstance().create(BookingService.class);
                                        BookingService.UpdateBookingStatusRequest request = new BookingService.UpdateBookingStatusRequest(orderId, 2);
                                        Call<BookingService.ResDTO> call = apiService.updateBookingStatus(request);
                                        call.enqueue(new Callback<BookingService.ResDTO>() {
                                            @Override
                                            public void onResponse(Call<BookingService.ResDTO> call, Response<BookingService.ResDTO> response) {
                                                Log.d("callAPI", "Done");
                                                if (response.isSuccessful() && response.body() != null) {
                                                    Log.d("callAPI", "Done");
                                                    Intent intentSuccess = new Intent(OrderPaymentActivity.this, PaymentNotification.class);
                                                    intentSuccess.putExtra("result", "Thanh toán thành công");
                                                    startActivity(intentSuccess);
                                                } else {
                                                    Log.e("callAPI", "Lỗi khi cập nhật trạng thái: " + response.message());
                                                    Toast.makeText(OrderPaymentActivity.this, "Lỗi khi cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<BookingService.ResDTO> call, Throwable t) {
                                                // Handle the error
                                                Toast.makeText(OrderPaymentActivity.this, "Lỗi khi cập nhật trạng thái: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                Log.d("callAPI", t.getMessage());
                                            }
                                        });
//                                        new AlertDialog.Builder(OrderPaymentActivity.this).setTitle("Payment Success").setMessage(String.format("TransactionId: %s - TransToken: %s", transactionId, transToken)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                            }
//                                        }).setNegativeButton("Cancel", null).show();
                                    }

                                });
                                IsLoading();


                            }

                            @Override
                            public void onPaymentCanceled(String zpTransToken, String appTransID) {
                                new AlertDialog.Builder(OrderPaymentActivity.this).setTitle("User Cancel Payment").setMessage(String.format("zpTransToken: %s \n", zpTransToken)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).setNegativeButton("Cancel", null).show();
                                Toast.makeText(OrderPaymentActivity.this, "Thanh toán thất bại", Toast.LENGTH_LONG).show();
//                                Intent intentCanceled = new Intent(OrderPaymentActivity.this, PaymentNotification.class);
//                                intentCanceled.putExtra("result", "Thanh toán thất bại");
//                                startActivity(intentCanceled);
                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                                new AlertDialog.Builder(OrderPaymentActivity.this).setTitle("Payment Fail").setMessage(String.format("ZaloPayErrorCode: %s \nTransToken: %s", zaloPayError.toString(), zpTransToken)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).setNegativeButton("Cancel", null).show();
                                Toast.makeText(OrderPaymentActivity.this, "Thanh toán thất bại", Toast.LENGTH_LONG).show();
//                                Intent intentCanceled = new Intent(OrderPaymentActivity.this, PaymentNotification.class);
//                                intentCanceled.putExtra("result", "Thanh toán thất bại");
//                                startActivity(intentCanceled);
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

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
        Intent intent = new Intent(OrderPaymentActivity.this, HistoryOrder.class);
        startActivity(intent);
    }

    private void handleSignOut() {
        AuthStore.userId = 0;
        Intent intent = new Intent(OrderPaymentActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void handleBack() {
        Intent intent = new Intent(OrderPaymentActivity.this, HallActivity.class);
        intent.putExtra("movieId", HallScreenStore.movieId);
        setResult(RESULT_OK, intent);
        finish(); // Đóng Activity hiện tại và quay lại Activity trước đó
    }


    private void loadOrderDetails(String orderId) {
        BookingService apiService = ApiClient.getRetrofitInstance().create(BookingService.class);

        Call<BookingService.ResDTO> call = apiService.getBookingDetail(orderId);
        call.enqueue(new Callback<BookingService.ResDTO>() {
            @Override
            public void onResponse(Call<BookingService.ResDTO> call, Response<BookingService.ResDTO> response) {
                Log.d("callAPI", "Done");
                if (response.isSuccessful() && response.body() != null) {

                    loadingData(response.body().result);
                }
                Log.d("callAPI", "Done");

            }

            @Override
            public void onFailure(Call<BookingService.ResDTO> call, Throwable t) {
                // Handle the error
                Log.d("callAPI", t.getMessage());
            }
        });
    }

    private void IsDone() {
        btnPay.setVisibility(View.VISIBLE);
    }

    private void IsLoading() {
        btnPay.setVisibility(View.INVISIBLE);
    }

    private void loadingData(BookingService.BookingDetailDTO order) {
        String statusValue;
        if (order.status.equals("Paid")) {
            Log.d("PAYMENT", "");
            btnPay.setVisibility(View.GONE);
            statusValue = "Đã thanh toán";

        } else if (order.status.equals("Processing")) {
            statusValue = "Đang tiến hành";
        } else {
            statusValue = "Đã hủy";
        }
        userName.setText("Người đặt vé: " + order.userName);
        hallName.setText("Phòng: " + order.hallName);
        movieName.setText("Phim: " + order.movieName);
        showDate.setText("Ngày chiếu: " + Utils.formatDateTime(order.showDate));
        bookingDate.setText("Ngày đặt: " + Utils.formatDateTime(order.bookingDate));
        status.setText("Tình trạng: " + statusValue);
        totalPrice.setText("Tổng cộng: " + order.totalPrice + " VNĐ");
        total = order.totalPrice;


        fabDetailList.clear();
        fabDetailList.addAll(order.fabDetails);
        if (order.fabDetails.size() > 0) {
            nonFab.setVisibility(View.GONE);
        } else {
            nonFab.setVisibility(View.VISIBLE);
        }
        fabDetailAdapter.notifyDataSetChanged();
//
        seatDetailList.clear();
        seatDetailList.addAll(order.seatNames);
        seatShowAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}
