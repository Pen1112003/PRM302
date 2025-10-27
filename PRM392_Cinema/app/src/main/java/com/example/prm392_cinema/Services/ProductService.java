package com.example.prm392_cinema.Services;

import com.example.prm392_cinema.Models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ProductService {
    @GET("/api/Product/GetAllProducts")
    Call<List<Product>> getAllProducts();
}
