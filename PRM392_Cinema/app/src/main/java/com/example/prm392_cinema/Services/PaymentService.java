package com.example.prm392_cinema.Services;

import com.example.prm392_cinema.Models.UpdateStatusRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;

public interface PaymentService {
    @PUT("/api/payment/update-order-status")
    Call<Void> updateOrderStatus(@Body UpdateStatusRequest request);
}
