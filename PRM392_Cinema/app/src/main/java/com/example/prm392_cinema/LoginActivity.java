package com.example.prm392_cinema;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_ID = "userId";
    public static final String JWT_TOKEN = "jwtToken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body();
                    AuthStore.jwtToken = token;

                    try {
                        JWT jwt = new JWT(token);
                        Claim userIdClaim = jwt.getClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier");
                        String userId = userIdClaim.asString();

                        if (userId != null && !userId.isEmpty()) {
                            AuthStore.userId = userId;

                            saveAuthData(userId, token);

                            Log.d("LoginActivity", "Login successful. UserId: " + userId);

                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            Log.e("LoginActivity", "UserId claim is null or empty in JWT.");
                            Toast.makeText(LoginActivity.this, "Lỗi: Không tìm thấy User ID trong token.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e){
                        Log.e("LoginActivity", "JWT Decode Error: " + e.getMessage());
                        Toast.makeText(LoginActivity.this, "Lỗi giải mã token.", Toast.LENGTH_SHORT).show();
                    }
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

    private void saveAuthData(String userId, String token) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID, userId);
        editor.putString(JWT_TOKEN, token);
        editor.apply();
    }
}
