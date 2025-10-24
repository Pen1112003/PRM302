package com.example.prm392_cinema;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.AuthService;
import com.example.prm392_cinema.Stores.AuthStore;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText dateOfBirthField;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        dateOfBirthField = findViewById(R.id.dateOfBirthInput);
        dateOfBirthField.setOnClickListener(v -> showDatePickerDialog());

        Button signUpBtn = findViewById(R.id.btnSignUp);
        signUpBtn.setOnClickListener(v -> handleSignUp());

        Button loginBtn = findViewById(R.id.btnLogin);
        loginBtn.setOnClickListener(v -> navigateToLoginScreen());
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    dateOfBirthField.setText(selectedDate);
                },
                year,
                month,
                day);
        datePickerDialog.show();
    }

    private void navigateToLoginScreen() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleSignUp() {
        TextView usernameField = findViewById(R.id.usernameInput);
        TextView emailField = findViewById(R.id.emailInput);
        TextView passwordField = findViewById(R.id.passwordInput);
        TextView passwordConfirmField = findViewById(R.id.passwordConfirmInput);
        TextView fullNameField = findViewById(R.id.fullNameInput);
        TextView phoneNumberField = findViewById(R.id.phoneNumberInput);
        TextView addressField = findViewById(R.id.addressInput);
        TextView identityIdField = findViewById(R.id.identityIdInput);

        String username = usernameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString();
        String passwordConfirm = passwordConfirmField.getText().toString();
        String fullName = fullNameField.getText().toString().trim();
        String phoneNumber = phoneNumberField.getText().toString().trim();
        String address = addressField.getText().toString().trim();
        String dateOfBirth = dateOfBirthField.getText().toString().trim();
        String identityId = identityIdField.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || fullName.isEmpty() || dateOfBirth.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ các trường bắt buộc.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Vui lòng nhập email hợp lệ.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            Toast.makeText(this, "Mật khẩu phải dài ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Mật khẩu không khớp.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phoneNumber.isEmpty() && !Patterns.PHONE.matcher(phoneNumber).matches()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại hợp lệ.", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthService apiService = ApiClient.getRetrofitInstance().create(AuthService.class);
        AuthService.SignUpDto signUpDto = new AuthService.SignUpDto(
                username,
                email,
                password,
                fullName,
                phoneNumber,
                address,
                dateOfBirth,
                identityId
        );

        Call<String> call = apiService.signUp("User", signUpDto);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String token = response.body(); // The JWT token
                    Log.d("SignupActivity", "Registration successful. Token: " + token);
                    Toast.makeText(SignupActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    navigateToLoginScreen();
                } else {
                    String errorMsg = "Đăng ký thất bại. Email hoặc Username có thể đã tồn tại.";
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyStr = response.errorBody().string();
                            Log.e("SignupActivity", "API Error Body: " + errorBodyStr);
                        }
                    } catch (IOException e) {
                        Log.e("SignupActivity", "Error reading error body", e);
                    }
                    Toast.makeText(SignupActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("SignupActivity", "API call failed: " + t.getMessage(), t);
                Toast.makeText(SignupActivity.this, "Đã xảy ra lỗi kết nối.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
