package com.example.prm392_cinema.Services;

import com.example.prm392_cinema.Stores.AuthStore;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {

    // For Android Emulator, use 10.0.2.2 to refer to the host machine's localhost.
    private static final String BASE_URL = "http://10.0.2.2:5290/"; 
    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Create an OkHttpClient to add an interceptor for authentication
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();

                    // Get token from AuthStore
                    String token = AuthStore.jwtToken;

                    // If token exists, add it to the Authorization header
                    if (token != null && !token.isEmpty()) {
                        requestBuilder.header("Authorization", "Bearer " + token);
                    }

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });

            OkHttpClient client = httpClient.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    // Attach the OkHttpClient with the interceptor
                    .client(client)
                    // Add ScalarsConverterFactory first to handle primitive types like String
                    .addConverterFactory(ScalarsConverterFactory.create()) 
                    // Add GsonConverterFactory for JSON objects
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
