package com.example.prm392_cinema;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.AuthService;
import com.example.prm392_cinema.Stores.AuthStore;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Button loginBtn = findViewById(R.id.btnLogin);
        loginBtn.setOnClickListener(v -> handleSignIn());

        Button signUpBtn = findViewById(R.id.btnSignUp);
        signUpBtn.setOnClickListener(v -> navigateToSignUpScreen());
    }

    private void navigateToSignUpScreen() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    private void handleSignIn() {
        TextView emailField = findViewById(R.id.emailInput);
        TextView passwordField = findViewById(R.id.passwordInput);

        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ email và mật khẩu.", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthService apiService = ApiClient.getRetrofitInstance().create(AuthService.class);
        Call<String> call = apiService.login(new AuthService.LoginDto(email, password));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String token = response.body();
                    AuthStore.jwtToken = token;

                    // Decode the JWT to get userId
                    try {
                        JWT jwt = new JWT(token);
                        // The claim name for user ID in ASP.NET Core Identity is typically this long URI
                        Claim userIdClaim = jwt.getClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier");
                        String userIdString = userIdClaim.asString();
                        if (userIdString != null) {
                            AuthStore.userId = Integer.parseInt(userIdString);
                            Log.d("LoginActivity", "Login successful. Token saved. UserId: " + AuthStore.userId);
                        } else {
                            Log.e("LoginActivity", "UserId claim is null in JWT.");
                        }
                    } catch (Exception e){
                        Log.e("LoginActivity", "JWT Decode Error: " + e.getMessage());
                    }

                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Email hoặc mật khẩu không đúng.";
                    try {
                        if (response.errorBody() != null) {
                            Log.e("LoginActivity", "API Error Body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        Log.e("LoginActivity", "Error reading error body", e);
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("LoginActivity", "API call failed", t);
                Toast.makeText(LoginActivity.this, "Lỗi kết nối. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
