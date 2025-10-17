package com.example.prm392_cinema.Services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SeatService {
    @GET("/api/seats")
    Call<List<SeatResponseDto>> getSeats(@Query("showTimeId") int showTimeId);

    public class SeatResponseDto {
        public String rowName;
        public List<RowSeat> rowSeats;

        public class RowSeat {
            public int seatId;
            public String seatType;
            public int price;
            public boolean isSeat;
            public String name;
            public boolean isSold;
            public int colIndex;
            public int seatIndex;
        }
    }
}
