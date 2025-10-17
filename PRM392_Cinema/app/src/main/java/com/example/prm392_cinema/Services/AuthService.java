package com.example.prm392_cinema.Services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthService {
    @POST("/api/user/login")
    Call<LoginResponseDto> login(@Body LoginDto dto);

    public class LoginDto {
        public String email;
        public String password;

        public LoginDto(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public class LoginResponseDto {
        public boolean success;
        public LoginUserDto result;
    }

    public class LoginUserDto {
        public int userId;
    }

    @POST("/api/user/sign-up")
    Call<SignUpResponseDto> signUp(@Body SignUpDto dto);

    public class SignUpDto {
        public String username;
        public String email;
        public String password;

        public SignUpDto(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }
    }

    public class SignUpResponseDto {
        public boolean success;
        public LoginUserDto result;
    }
}
