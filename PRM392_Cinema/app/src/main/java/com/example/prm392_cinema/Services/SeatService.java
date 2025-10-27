package com.example.prm392_cinema.Services;

import com.example.prm392_cinema.Models.SeatType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SeatService {
    @GET("/api/seats")
    Call<List<SeatResponseDto>> getSeats(@Query("showTimeId") int showTimeId);

    @GET("/api/SeatType")
    Call<List<SeatType>> getSeatTypes();

    @GET("/api/SeatConfiguration/room/{roomId}")
    Call<List<SeatResponseDto>> getSeatConfiguration(@Path("roomId") int roomId);

    // Đã sửa lại DTO để khớp với cấu trúc JSON phẳng từ API
    public class SeatResponseDto {
        public int seatId;
        public String seatNumber;
        public boolean isSelected;
        public String seatTypeName;
        public double seatPrice;
        public int roomId;
        public String roomName;
        public int theaterId;
        public int seatTypeId;
    }
}
