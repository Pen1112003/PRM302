package com.example.prm392_cinema.Services;

import com.example.prm392_cinema.Models.TransactionDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TransactionService {
    @GET("api/Transaction/{transactionId}")
    Call<TransactionDetails> getTransactionDetails(@Path("transactionId") String transactionId);
}
