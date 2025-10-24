package com.example.prm392_cinema.Services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService {
    @POST("/api/Auth/login")
    Call<String> login(@Body LoginDto dto); // Changed to Call<String>

    public class LoginDto {
        public String email;
        public String password;

        public LoginDto(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    // This class is no longer used by the login call but kept for reference
    public class LoginResponseDto {
        public boolean success;
        public LoginUserDto result;
    }

    public class LoginUserDto {
        public int userId;
    }

    @POST("/api/Auth/register")
    Call<String> signUp(@Query("role") String role, @Body SignUpDto dto);

    public class SignUpDto {
        public String username;
        public String email;
        public String password;
        public String fullName;
        public String phoneNumber;
        public String address;
        public String dateOfBirth;
        public String identityId;

        public SignUpDto(String username, String email, String password, String fullName, String phoneNumber, String address, String dateOfBirth, String identityId) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.fullName = fullName;
            this.phoneNumber = phoneNumber;
            this.address = address;
            this.dateOfBirth = dateOfBirth;
            this.identityId = identityId;
        }
    }

    public class SignUpResponseDto {
        public boolean success;
        public LoginUserDto result;
    }
}
