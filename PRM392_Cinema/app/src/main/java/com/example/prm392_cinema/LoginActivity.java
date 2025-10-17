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

import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.AuthService;
import com.example.prm392_cinema.Stores.AuthStore;

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
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignIn();
            }
        });

        Button signUpBtn = findViewById(R.id.btnSignUp);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignUpScreen();
            }
        });
    }

    private void navigateToSignUpScreen()
    {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    private void handleSignIn() {
        CharSequence email = ((TextView) findViewById(R.id.emailInput)).getText();
        CharSequence password = ((TextView) findViewById(R.id.passwordInput)).getText();

        if (email.length() == 0) {
            Toast.makeText(LoginActivity.this, "Email bắt buộc.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() == 0) {
            Toast.makeText(LoginActivity.this, "Mật khẩu bắt buộc.", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthService apiService = ApiClient.getRetrofitInstance().create(AuthService.class);
        Call<AuthService.LoginResponseDto> call = apiService.login(new AuthService.LoginDto(email.toString(), password.toString()));
        call.enqueue(new Callback<AuthService.LoginResponseDto>() {
            @Override
            public void onResponse(Call<AuthService.LoginResponseDto> call, Response<AuthService.LoginResponseDto> response) {
                Log.d("callAPI", "Done");
                Log.d("callAPI", "Done");
                Log.d("callAPI", "Done");
                Log.d("callAPI", "Done");
                if (!response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Email hoặc/và mật khẩu không đúng.", Toast.LENGTH_SHORT).show();
                    return;
                }

                AuthService.LoginResponseDto res = response.body();
                if (!res.success) {
                    Toast.makeText(LoginActivity.this, "Email hoặc/và mật khẩu không đúng.", Toast.LENGTH_SHORT).show();
                    return;
                }

                AuthStore.userId = res.result.userId;
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<AuthService.LoginResponseDto> call, Throwable t) {
                // Handle the error
                Log.d("callAPI", t.getMessage());
            }
        });
    }
}