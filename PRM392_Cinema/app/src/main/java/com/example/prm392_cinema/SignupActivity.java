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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.AuthService;
import com.example.prm392_cinema.Stores.AuthStore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        Button signUpBtn = findViewById(R.id.btnSignUp);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });

        Button loginBtn = findViewById(R.id.btnLogin);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLoginScreen();
            }
        });
    }

    private void navigateToLoginScreen() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void handleSignUp() {
        CharSequence email = ((TextView) findViewById(R.id.emailInput)).getText();
        CharSequence password = ((TextView) findViewById(R.id.passwordInput)).getText();
        CharSequence passwordConfirm = ((TextView) findViewById(R.id.passwordConfirmInput)).getText();

        if (email.length() == 0) {
            Toast.makeText(SignupActivity.this, "Email bắt buộc.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() == 0) {
            Toast.makeText(SignupActivity.this, "Mật khẩu bắt buộc.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordConfirm.length() == 0) {
            Toast.makeText(SignupActivity.this, "Xác nhận mật khẩu bắt buộc.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordConfirm.toString().equals(password.toString())) {
            Toast.makeText(SignupActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthService apiService = ApiClient.getRetrofitInstance().create(AuthService.class);
        Call<AuthService.SignUpResponseDto> call = apiService.signUp(new AuthService.SignUpDto(email.toString().split("@")[0], email.toString(), password.toString()));
        call.enqueue(new Callback<AuthService.SignUpResponseDto>() {
            @Override
            public void onResponse(Call<AuthService.SignUpResponseDto> call, Response<AuthService.SignUpResponseDto> response) {
                Log.d("callAPI", "Done");
                Log.d("callAPI", "Done");
                Log.d("callAPI", "Done");
                Log.d("callAPI", "Done");

                AuthService.SignUpResponseDto res = response.body();

                AuthStore.userId = res.result.userId;
                Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<AuthService.SignUpResponseDto> call, Throwable t) {
                // Handle the error
                Log.d("callAPI", t.getMessage());
            }
        });
    }
}